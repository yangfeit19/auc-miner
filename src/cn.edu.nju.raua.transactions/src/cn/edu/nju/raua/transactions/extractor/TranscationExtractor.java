package cn.edu.nju.raua.transactions.extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.internal.DocLineComparator;
import org.eclipse.compare.internal.MergeViewerContentProvider;
import org.eclipse.compare.internal.Utilities;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;

import cn.edu.nju.raua.core.transactions.Item;
import cn.edu.nju.raua.core.transactions.Transaction;
import cn.edu.nju.raua.core.transactions.TransactionList;
import cn.edu.nju.raua.transactions.cd.CDModel;
import cn.edu.nju.raua.transactions.cd.MethodInfo;

@SuppressWarnings("restriction")
public class TranscationExtractor {

	private CDModel oldModel;
	private CDModel newModel;
	private CDModel middleModel;
	private String saveDir;
	private TransactionList transactions;
	
	public TranscationExtractor(CDModel oldModel, CDModel newModel, String saveDir) {
		if ((oldModel == null) || (newModel == null)) {
			throw new IllegalArgumentException("oldModel and newModel arguments can not be null.");
		}
		
		this.oldModel = oldModel;
		this.newModel = newModel;
		this.middleModel = new CDModel();
		this.saveDir = (saveDir == null) ? "./" : saveDir;
		this.transactions = new TransactionList();
	}
	
	public TransactionList extract(boolean storeToFile, boolean usingSplit, int splitThreshold) {
		Map<MethodInfo, List<MethodInfo>> oldCDs = oldModel.getCallerToCalleesMap();
		Map<MethodInfo, List<MethodInfo>> newCDs = newModel.getCallerToCalleesMap();
		for (MethodInfo oldCaller : oldCDs.keySet()) {
			for (MethodInfo newCaller : newCDs.keySet()) {
				if (oldCaller.equals(newCaller)) {
					List<MethodInfo> oldCallees = oldCDs.get(oldCaller);
					List<MethodInfo> newCallees = newCDs.get(newCaller);
					if (usingSplit) {
						//对匹配的方法对的代码进行基于文本的划分，返回代码片段对信息
						List<CodeSegmentPair> codeSegmentPairs = getSplitPointPairs(oldCaller, newCaller, splitThreshold);
						
						int codeSegPairIndex = 0;
						for (CodeSegmentPair pair : codeSegmentPairs) {
							if (codeSegmentPairs.size() > 1) {
								codeSegPairIndex++;
							}
							
							//获得旧版本代码片段中调用的方法和访问的字段
							Set<MethodInfo> oldSegmentCallees = new HashSet<MethodInfo>();
							for (MethodInfo callee : oldCallees) {
								if (callee.startLine >= pair.leftStart && callee.startLine <= pair.leftEnd) {
									oldSegmentCallees.add(callee);
								}
							}
							
							// FIXME: middleModel?
							MethodInfo middleCaller = new MethodInfo(pair.leftStart, pair.leftEnd, 
									oldCaller.fullQualifiedName + (codeSegPairIndex > 0 ? "_" + codeSegPairIndex : ""), 
									oldCaller.fileFullPath);
							middleModel.addCallRelationship(middleCaller, new ArrayList<MethodInfo>(oldSegmentCallees));
							
							//获得新版本代码片段中调用的方法和访问的字段
							Set<MethodInfo> newSegmentCallees = new HashSet<MethodInfo>();
							for (MethodInfo callee : newCallees) {
								if (callee.startLine >= pair.rightStart && callee.startLine <= pair.rightEnd) {
									newSegmentCallees.add(callee);
								}
							}
							
							generateTransaction(oldCaller, oldSegmentCallees, oldCDs, newSegmentCallees, newCDs, codeSegPairIndex);
						}
					}
					else {
						generateTransaction(oldCaller, 
								new HashSet<MethodInfo>(oldCallees), 
								oldCDs, 
								new HashSet<MethodInfo>(newCallees), 
								newCDs, 
								0);
					}
					break;
				}			
			}
		}
		
		if (storeToFile) {
			outputTransactions(transactions);
		}
		
		return transactions;
	}
	
	public CDModel getMiddleModel() {
		return middleModel;
	}
	
	private void generateTransaction(MethodInfo oldCaller, Set<MethodInfo> oldCallees, Map<MethodInfo, List<MethodInfo>> oldCDs, 
			Set<MethodInfo> newCallees, Map<MethodInfo, List<MethodInfo>> newCDs, int codeSegPairIndex) {
		/**
		 * 处理可疑的方法调用移除、添加。如：
		 * 可疑的方法调用移除（对C的调用） Old Version: A->C  New Version: A->B->C;
		 * 可疑的方法调用添加（对C的调用） Old Version: A->B->C New Version: A->C;
		 */
		Set<MethodInfo> temp1 = new HashSet<MethodInfo>(oldCallees);
		for (MethodInfo method : temp1) {
			if (shouldBeRemoved(method, newCallees, newCDs)) {
				oldCallees.remove(method);
			}
		}
		Set<MethodInfo> temp2 = new HashSet<MethodInfo>(newCallees);
		for (MethodInfo method : temp2) {
			if (shouldBeRemoved(method, temp1, oldCDs)) {
				newCallees.remove(method);
			}
		}
		
		//根据代码片段对的调用关系的变化产生一个或零个事务
		if (!oldCallees.isEmpty() || !newCallees.isEmpty()) {
			String tid = oldCaller.fullQualifiedName;
			if (codeSegPairIndex > 0) {
				tid += ("_" + codeSegPairIndex);
			}
				
			Transaction transaction = new Transaction(tid);
			
			for (MethodInfo callee : oldCallees) {
				transaction.addItem(new Item(false, callee.fullQualifiedName));
			}
			
			for (MethodInfo callee : newCallees) {
				transaction.addItem(new Item(true, callee.fullQualifiedName));
			}
			
			transactions.addTransaction(transaction);
		}
	}
	
	private boolean shouldBeRemoved(MethodInfo method, Set<MethodInfo> segmentCallees, Map<MethodInfo, List<MethodInfo>> cdModel) {
		if (segmentCallees.contains(method)) {
			return true;
		} else {
			for (MethodInfo callee : segmentCallees) {
				if (cdModel.containsKey(callee)) {
					if (cdModel.get(callee).contains(method)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private List<CodeSegmentPair> getSplitPointPairs(MethodInfo oldMethod, MethodInfo newMethod, int splitThreshold) {
		List<CodeSegmentPair> splitPointPairs = new ArrayList<CodeSegmentPair>();
	
		RangeDifference[] differences = compareTextOfMethodBody(oldMethod, newMethod);
		
		int leftPreSegEnd = oldMethod.startLine, rightPreSegEnd = newMethod.startLine;
		for (RangeDifference diff : differences) {
			if (diff.kind() == RangeDifference.NOCHANGE) {
				if (diff.leftLength() >= splitThreshold) {
					int leftSplitPoint = oldMethod.startLine + (diff.leftStart() + diff.leftEnd())/2;
					int rightSplitPoint = newMethod.startLine + (diff.rightStart() + diff.rightEnd())/2;
					
					splitPointPairs.add(new CodeSegmentPair(leftPreSegEnd, leftSplitPoint, rightPreSegEnd, rightSplitPoint));
					
					leftPreSegEnd = leftSplitPoint;
					rightPreSegEnd = rightSplitPoint;
				}
			}
		}
		//处理最后一个代码片段对
		splitPointPairs.add(new CodeSegmentPair(leftPreSegEnd, oldMethod.endLine, rightPreSegEnd, newMethod.endLine));
		
		return splitPointPairs;
	}
	
	private void outputTransactions(TransactionList transactions) {
		try {
			JAXBContext jc = JAXBContext.newInstance(TransactionList.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			System.gc();
			String fileName = saveDir  + "transactions.xml";
			FileOutputStream stream = new FileOutputStream(fileName);
			marshaller.marshal(transactions, stream);
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//比较新旧版本方法的方法体的代码文本
	private RangeDifference[] compareTextOfMethodBody(MethodInfo oldMethod, MethodInfo newMethod) {
		IDocument oldVersion = new Document(readFileToString(oldMethod.fileFullPath));
		IDocument newVersion = new Document(readFileToString(newMethod.fileFullPath));
		
		Region lregion = null, rregion = null;
		try {
			//注意：getLineOffset返回给定行的第一个字符的offset，行号从0开始
			int start = oldVersion.getLineOffset(oldMethod.startLine - 1);
			int end = oldVersion.getLineOffset(oldMethod.endLine) - 1;
			lregion = new Region(start, end - start);
			start = newVersion.getLineOffset(newMethod.startLine - 1);
			end = newVersion.getLineOffset(newMethod.endLine) - 1;
			rregion = new Region(start, end - start);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		DocLineComparator sleft = new DocLineComparator(oldVersion, lregion, true, 
				Utilities.getCompareFilters(new CompareConfiguration()), MergeViewerContentProvider.LEFT_CONTRIBUTOR);
		DocLineComparator sright = new DocLineComparator(newVersion, rregion, true, 
				Utilities.getCompareFilters(new CompareConfiguration()), MergeViewerContentProvider.RIGHT_CONTRIBUTOR);
		DocLineComparator sa = null, sl= sleft, sr= sright;
		RangeDifference[] diffs = RangeDifferencer.findRanges(null, sa, sl, sr);;
	
		return diffs;
	}
	
	private static String readFileToString(String fileName) {
		StringBuffer  buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			String line="";
			while((line= br.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	//代码片段对的信息：记录在各自文件中的起始位置
	private class CodeSegmentPair {
	    int leftStart;
	    int leftEnd;
	    int rightStart;
	    int rightEnd;
	    
	    public CodeSegmentPair(int leftStart, int leftEnd, int rightStart, int rightEnd) {
	    	this.leftStart = leftStart;
	    	this.leftEnd = leftEnd;
	    	this.rightStart = rightStart;
	    	this.rightEnd = rightEnd;
	    }
	    
	    @Override
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("CodeSegmentPair: (letf:{start:");
	    	sb.append(leftStart);
	    	sb.append(", end:");
	    	sb.append(leftEnd);
	    	sb.append("}, right:{start:");
	    	sb.append(rightStart);
	    	sb.append(", end:");
	    	sb.append(rightEnd);
	    	sb.append("})");
	    	return sb.toString();
	    }
	}
}
