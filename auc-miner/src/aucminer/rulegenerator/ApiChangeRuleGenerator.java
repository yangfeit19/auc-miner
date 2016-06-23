package aucminer.rulegenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aucminer.core.AssociationRule;
import aucminer.core.AssociationRuleList;
import aucminer.core.Configuration;
import aucminer.core.FrequentItemset;
import aucminer.core.FrequentItemsetList;
import aucminer.core.ITransactionProvider;
import aucminer.core.Item;
import aucminer.core.RAUAUtility;
import aucminer.core.Transaction;
import aucminer.core.TransactionList;
import aucminer.fpmining.apriori.Apriori;
import aucminer.transactions.CDModel;
import aucminer.transactions.DeprecatedMethodCommentExtractor;
import aucminer.transactions.MethodInfo;
import aucminer.transactions.TransactionUtility;
import aucminer.transactions.TranscationExtractor;

/**
 * 生成API替换规则的类，该类需要一个{@link Configuration}对象作为参数。
 * {@link APIChangeRuleGenerator}包含以下基本步骤：
 * 1）根据配置文件中指定的项目，构建新旧版本的Call Dependency信息{@link CDModel}；
 * 2）根据新旧版本的Call Dependency信息生成事务集合{@link Transaction};
 * 3) 指定最小支持度，利用频繁挖掘算法Apriori{@link Apriori}生成频繁项集{@link FrequentItemset};
 * 3) 根据指定的最小置信度生成关联规则；
 * 4）根据指定项目源码的注释信息过滤规则，并生成Root Method的替换规则。
 */
public class ApiChangeRuleGenerator {
	
	private Configuration rauaConfig;
	
	private CDModel oldModel; 
	private CDModel newModel;
	private TransactionList transactions;

	private CDModel middleModel;
	
	/**
	 * 构造函数
	 * @param rauaConfig {@link Configuration}类的一个实例，包含了RAUA运行的配置信息。
	 */
	public ApiChangeRuleGenerator(Configuration rauaConfig) {
		if (rauaConfig == null) {
			throw new NullPointerException("rauaConfig parameter can not be null.");
		}
		this.rauaConfig = rauaConfig;
	}
	
	/** 
	 * @param includeField 是否包含类的字段信息
	 * @param storeToFile  是否将Call Dependency和Transaction的信息保存到文件
	 * @param splitThreshold 用于划分方法对
	 */
	public AssociationRuleList generateApiChangeRules(boolean includeField, boolean storeToFile, boolean usingSplit,
			int splitThreshold, int minSupport, float minConfidence) {
		//从指定项目新旧版本的源码中提取事务集合, 将信息保存到相应的字段。对应步骤1）和2）。
		extractTransactions(includeField, storeToFile, usingSplit, splitThreshold);
		
		//利用Apriori算法生成频繁项集（或闭频繁项集）。对应步骤3）。
		boolean miningCloSet = true;
		Apriori apriori = new Apriori(100, miningCloSet);
		ListDataProvider dataProvider = new ListDataProvider(transactions.getTransactions());
		FrequentItemsetList freqItemsetList = apriori.miningFrequentItemset(dataProvider, minSupport); // >= 2
		if (storeToFile) {
			freqItemsetList.sortByDescendingSupport();
			String freqItemsetFilePath = this.rauaConfig.getSaveDir() + File.separator + "FrequentItemset.xml";
			RAUAUtility.outputFrequentItemsetList(freqItemsetList, freqItemsetFilePath);
		}
		
		//根据指定的最小置信度从频繁项集中生成关联规则。对应步骤4）。
		AssociationRuleList rulesFromApriori = generateAssociationRules(freqItemsetList, minConfidence);
		if (storeToFile) {
			rulesFromApriori.sortByDescendingSupport();
			String ruleFilePath = this.rauaConfig.getSaveDir() + File.separator + "ApriorAssociationRules.xml";
			RAUAUtility.outputAssociationRuleList(rulesFromApriori, ruleFilePath);
		}
		
		//根据指定项目源码的注释信息过滤规则，并生成Root Method的替换规则。对应步骤5)。
		AssociationRuleList associationRules = new AssociationRuleList();
		AssociationRuleList rulesFromComments = extractRuleForDeprecatedFromDoc(rauaConfig.getNewVersionProject());
		for (AssociationRule r1 : rulesFromApriori.getRules()) {
			boolean validRule = true;
			for (AssociationRule r2 : rulesFromComments.getRules()) {
				if (r1.getAntecedent().containsAll(r2.getAntecedent())) {
					if (!r1.getConsequent().containsAll(r2.getConsequent())) {
						validRule = false;
					}
					else if ((r1.getAntecedent().size() == 1) || (r1.getConsequent().size() == 1)) {
						validRule = false;
					}
				}
				if (!validRule) break;
			}
			
			if (validRule) {
				associationRules.addAssociationRule(r1);
			}
		}
		for (AssociationRule r : rulesFromComments.getRules()) {
			associationRules.addAssociationRule(r);
		}
		
		AssociationRuleList rulesUsingSimilarity = extractRulesUsingSimilarity(associationRules);
		for (AssociationRule r : rulesUsingSimilarity.getRules()) {
			associationRules.addAssociationRule(r);
		}
		
		if (storeToFile) {
			associationRules.sortByDescendingSupport();
			String ruleFilePath = this.rauaConfig.getSaveDir() + File.separator + "FinalAssociationRules.xml";
			RAUAUtility.outputAssociationRuleList(associationRules, ruleFilePath);
		}
		
		return associationRules;
	}
	
	/**
	 * 从指定项目新旧版本的源码中提取事务集合, 将信息保存到相应的字段。
	 * @param includeField 是否分析类的字段。
	 * @param storeToFile 是否将分析结果（包括oldModel, newModel和transactions）保存到XML文件中。
	 * @param splitThreshold 用于对方法对进行划分的阈值。
	 */
	private void extractTransactions(boolean includeField, boolean storeToFile, boolean usingSplit, int splitThreshold) {
		oldModel = TransactionUtility.buildOldCDModel(rauaConfig.getOldVersionProject(), 
				rauaConfig.getSaveDir(), includeField, storeToFile);
		newModel = TransactionUtility.buildNewCDModel(rauaConfig.getNewVersionProject(), 
				rauaConfig.getSaveDir(), includeField, storeToFile);
		TranscationExtractor extractor = new TranscationExtractor(oldModel, newModel, rauaConfig.getSaveDir());
		transactions = extractor.extract(storeToFile, usingSplit, splitThreshold);
		middleModel = extractor.getMiddleModel();
	}
	
	/**
	 * 从频繁项集中生成关联规则。
	 * @param freqItemsetList 频繁项集的集合，{@link FrequentItemsetList}。
	 * @param minConfidence 指定的最小置信度。
	 * @return 生成的关联规则集合,{@link AssociationRuleList}。
	 */
	private AssociationRuleList generateAssociationRules(FrequentItemsetList freqItemsetList, float minConfidence) {
		AssociationRuleList associationRules = new AssociationRuleList();
		if (freqItemsetList != null) {
			for (FrequentItemset itemset : freqItemsetList.toList()) {
				List<Item> removed = new ArrayList<>();
				List<Item> added = new ArrayList<>();
				
				for (Item item : itemset.getItemset().getItems()) {
					if (item.isAdded()) {
						added.add(item);
					}
					else {
						removed.add(item);
					}
				}
				if ((removed.size() > 0) && (added.size() > 0)) {
					AssociationRule rule = new AssociationRule(removed, added, itemset.getSupport());
					calculateConfidence(rule);
					if (rule.getConfidence() >= minConfidence) {
						associationRules.addAssociationRule(rule);
					}
				}
			}
		}
		return associationRules;
	}
	
	/***
	 * 计算关联规则的置信度.
	 *            规则的支持度（AssociationRule.getSupport()）
	 * 置信度 = ----------------------------------------------- * 100
	 *            规则的Antecedent部分在旧版本中被调用的次数
	 * @param rule {@link AssociationRule}对象，需要计算置信度的规则。
	 */
	private void calculateConfidence(AssociationRule rule) {
		double supportAntecedent = 0;
		// FIXME: right?
		Map<MethodInfo, List<MethodInfo>> callerToCalleesMap = middleModel.getCallerToCalleesMap();
		for (MethodInfo  caller : callerToCalleesMap.keySet()) {
			boolean containsAll = true;
			List<MethodInfo> callees = callerToCalleesMap.get(caller);
			for (Item item : rule.getAntecedent()) {
				boolean containItem = false;
				for (MethodInfo callee : callees) {
					if (callee.fullQualifiedName.equals(item.getCallee())) {
						containItem = true;
						break;
					}
				}
				if (!containItem) {
					containsAll = false;
					break;
				}
			}
			if (containsAll) {
				supportAntecedent++;
			}
		}
		
		if (supportAntecedent != 0) {
			rule.setConfidence((rule.getSupport() * 1.0)/supportAntecedent*100);
		}
	}
	
	/**
	 * 从项目过时的API的代码注释中提取替换规则。
	 * @param projectName 项目名称
	 * @return 返回提取的关联规则列表，{@link AssociationRuleList}
	 */
	private AssociationRuleList extractRuleForDeprecatedFromDoc(String projectName) {
		
		AssociationRuleList rulesFromComment = new AssociationRuleList();
		
		DeprecatedMethodCommentExtractor commentExtractor = new DeprecatedMethodCommentExtractor(projectName);
		Map<String,String> methodToCommentMap = commentExtractor.extract();
		for (String key : methodToCommentMap.keySet()) {
			boolean findReplacement = false;
			String comment = methodToCommentMap.get(key);
			
			/* FIXME:这里需要完善，以从更多的文本模式中提取替换方法。
			 * 现在仅可以识别"use {@link xxx} instead"形式的文本。 
			 */
			Pattern replacementPattern = Pattern.compile("use\\s*\\{@link\\s+(.*)\\s*\\}\\s*instead", Pattern.CASE_INSENSITIVE);
			Matcher matcher = replacementPattern.matcher(comment);
			if (matcher.find()) {
				String replacementPartialName = matcher.group(1).split("\\s+")[0].trim();
				String replacement = getPossibleFullQualifiedName(key, replacementPartialName);
				if (replacement != null) {
					findReplacement = true;
					List<Item> antecedent = new ArrayList<Item>();
					antecedent.add(new Item(false, key));
					List<Item> consequent = new ArrayList<Item>();
					consequent.add(new Item(true, replacement));
					rulesFromComment.addAssociationRule(new AssociationRule(antecedent, consequent, 1));
				}
			}
			
			if (!findReplacement) {
				System.out.println("Not Find Replacement!");
				System.out.println(key);
				System.out.println(comment);
				System.out.println("===========================================================\n\n");
			}
		}
		
		return rulesFromComment;
	}
	
	private String getPossibleFullQualifiedName(String originalMethodOrField, String replacementPartialName) {
		String tempNameString = replacementPartialName;
		if (replacementPartialName.indexOf("(") < 0) {
			//使用字段替换的情况
			if (replacementPartialName.startsWith("#")) {
				//需要考虑被替换者是方法还是字段
				int prefixEndIndex = (originalMethodOrField.indexOf("(") > 0) ? originalMethodOrField.indexOf("(") : originalMethodOrField.length();
				String fullNamePrefix = originalMethodOrField.substring(originalMethodOrField.indexOf("#")+1, originalMethodOrField.lastIndexOf(".", prefixEndIndex));
				tempNameString = fullNamePrefix + replacementPartialName;
			}
			tempNameString = tempNameString.replace("#", ".");
			for (String declaredString : newModel.getDeclaredMethodAndFieldSet()) {
				if (declaredString.endsWith(tempNameString)) {
					return declaredString;
				}
			}
		}
		else {
			//使用方法替换的情况
			if (replacementPartialName.startsWith("#")) {
				//需要考虑被替换者是方法还是字段
				int prefixEndIndex = (originalMethodOrField.indexOf("(") > 0) ? originalMethodOrField.indexOf("(") : originalMethodOrField.length();
				String fullNamePrefix = originalMethodOrField.substring(originalMethodOrField.indexOf("#")+1, originalMethodOrField.lastIndexOf(".", prefixEndIndex));
				tempNameString = fullNamePrefix + replacementPartialName; 
			}
			tempNameString = tempNameString.replace("#", ".");
			
			List<String> replacementParams = getParamStringList(tempNameString);
			
			for (String declaredString : newModel.getDeclaredMethodAndFieldSet()) {
				if (declaredString.indexOf("(") > 0) {
					List<String> declarationParams = getParamStringList(declaredString);
					if (replacementParams.size() == declarationParams.size()) {
						boolean equals = true;
						for (int i = 0; i < declarationParams.size(); ++i) {
							if (!declarationParams.get(i).endsWith(replacementParams.get(i))) {
								equals = false;
								break;
							}
						}
						if (equals) {
							return declaredString;
						}
					}
				}
			}
		}
		return null;
	}
	
	private List<String> getParamStringList(String methodString) {
		int  angleBracketIndex = 0;
		List<String> paramStringList = new ArrayList<String>();
		StringBuilder paramBuilder = new StringBuilder();
		for (int i = methodString.indexOf("(") + 1; i < methodString.length() - 1; ++i) {
			if (methodString.charAt(i) == '<') {
				angleBracketIndex++;
			} else if (methodString.charAt(i) == '>') {
				angleBracketIndex--;
			} else {
				if (angleBracketIndex == 0) {
					if (methodString.charAt(i) == ',') {
						paramStringList.add(paramBuilder.toString().trim());
						paramBuilder.setLength(0);
					}
					else {
						paramBuilder.append(methodString.charAt(i));
					}
				}
			}
		}
		paramStringList.add(paramBuilder.toString().trim());
		return paramStringList;
	}
	
	private AssociationRuleList extractRulesUsingSimilarity(AssociationRuleList rules) {
		Set<String> foundReplacementMethod = new HashSet<String>();
		Set<String> isReplacementMethod = new HashSet<String>();
		Map<String, Set<String>> potentialCalssMap = new HashMap<String, Set<String>>();
		for (AssociationRule rule : rules.getRules()) {
			Set<String> oldClasses = getDeclaredCalssNames(rule.getAntecedent());
			Set<String> newClasses = getDeclaredCalssNames(rule.getConsequent());
			if (oldClasses.size() == 1 && newClasses.size() == 1) {
				String oldClassName = (String)oldClasses.toArray()[0];
				String newClassName = (String)newClasses.toArray()[0];
				if (potentialCalssMap.get(oldClassName) == null) {
					potentialCalssMap.put(oldClassName, new HashSet<String>());
				}
				potentialCalssMap.get(oldClassName).add(newClassName);
			}
			
			for (Item item : rule.getAntecedent()) {
				foundReplacementMethod.add(item.getCallee());
			}
			
			for (Item item : rule.getConsequent()) {
				isReplacementMethod.add(item.getCallee());
			}
		}
		
		AssociationRuleList tsRules = new AssociationRuleList();
		for (Entry<String, Set<String>> entry : potentialCalssMap.entrySet()) {
			if (oldModel.getClassToMethodsMap().containsKey(entry.getKey())) {
				Set<String> oldMethods = new HashSet<String>(oldModel.getClassToMethodsMap().get(entry.getKey()));
				Set<String> newMethods = new HashSet<String>();
				for (String className : entry.getValue()) {
					if (newModel.getClassToMethodsMap().containsKey(className)) {
						newMethods.addAll(newModel.getClassToMethodsMap().get(className));
					}
				}
				
				// FIXME: 这里还需要考虑被标记为 Deprecated 的方法，需要改进。
				Set<String> tempOldMethods = new HashSet<String>(oldMethods);
				oldMethods.removeAll(newMethods);
				newMethods.removeAll(tempOldMethods);
				
				oldMethods.removeAll(foundReplacementMethod);
				newMethods.removeAll(isReplacementMethod);
				
				List<MethodSignatureSimilarity> methodSimilarityList = new ArrayList<MethodSignatureSimilarity>();
				for (String removedMethod : oldMethods) {
					for (String addedMethod : newMethods) {
						methodSimilarityList.add(new MethodSignatureSimilarity(removedMethod, addedMethod));
					}
				}
				Collections.sort(methodSimilarityList);
				
				Set<String> handled = new HashSet<String>();
				for (int i = 0; i < methodSimilarityList.size(); ++i) {
//					System.out.println(methodSimilarityList.get(i).oldMethodFullQulifiedName);
//					System.out.println(methodSimilarityList.get(i).newMethodFullQulifiedName);
//					System.out.println(methodSimilarityList.get(i).similarity);
//					System.out.println("=======================================");
					if (!handled.contains(methodSimilarityList.get(i).oldMethodFullQulifiedName) && 
							!handled.contains(methodSimilarityList.get(i).newMethodFullQulifiedName) && 
							methodSimilarityList.get(i).similarity >= 0.6) {
						List<Item> antecedent = new ArrayList<Item>();
						antecedent.add(new Item(false, methodSimilarityList.get(i).oldMethodFullQulifiedName));
						
						List<Item> consequent = new ArrayList<Item>();
						consequent.add(new Item(true, methodSimilarityList.get(i).newMethodFullQulifiedName));
						
						tsRules.addAssociationRule(new AssociationRule(antecedent, consequent, 1));
					}
				}
			}
		}
		
		return tsRules;
	}
	
	private Set<String> getDeclaredCalssNames(List<Item> items) {
		Set<String> classNameSet = new HashSet<String>();
		for (Item item : items) {
			int firstSharpIndex = item.getCallee().indexOf('#');
			int lastDotIndex = item.getCallee().lastIndexOf('.');
			if (firstSharpIndex >= 0 && lastDotIndex >= 0) {
				classNameSet.add(item.getCallee().substring(firstSharpIndex + 1, lastDotIndex));
			}
		}
		return classNameSet;
	}
	
	private String getReturnType(String methodFullQualifiedName) {
		int sharpIndex = methodFullQualifiedName.indexOf('#');
		if (sharpIndex == 0) {
			return "";
		}
		else if (sharpIndex > 0) {
			int methodNameEndIndex = sharpIndex;
			if (methodFullQualifiedName.charAt(sharpIndex - 1) == '>') {
				int rightAngleBracketCount = 1;
				for (int i = sharpIndex - 2; i >= 0; --i) {
					if (methodFullQualifiedName.charAt(i) == '>') {
						rightAngleBracketCount++;
					}
					else if (methodFullQualifiedName.charAt(i) == '<') {
						rightAngleBracketCount--;
					}
					
					if (rightAngleBracketCount == 0) {
						methodNameEndIndex = i;
						break;
					}
				}
			}
			int lastDotBeforeLeftBracket = methodFullQualifiedName.substring(0, methodNameEndIndex).lastIndexOf('.');
			return methodFullQualifiedName.substring(lastDotBeforeLeftBracket + 1, methodNameEndIndex);
		}
		return null;
	}
	
	private String getMethodName(String methodFullQualifiedName) {
		int leftBracketIndex = methodFullQualifiedName.indexOf('(');
		if (leftBracketIndex < 0) {
			int lastDot = methodFullQualifiedName.lastIndexOf('.');
			return methodFullQualifiedName.substring(lastDot + 1);
		}
		else {
			int methodNameEndIndex = leftBracketIndex;
			if (methodFullQualifiedName.charAt(leftBracketIndex - 1) == '>') {
				int rightAngleBracketCount = 1;
				for (int i = leftBracketIndex - 2; i >= 0; --i) {
					if (methodFullQualifiedName.charAt(i) == '>') {
						rightAngleBracketCount++;
					}
					else if (methodFullQualifiedName.charAt(i) == '<') {
						rightAngleBracketCount--;
					}
					
					if (rightAngleBracketCount == 0) {
						methodNameEndIndex = i;
						break;
					}
				}
			}
			int lastDotBeforeLeftBracket = methodFullQualifiedName.substring(0, methodNameEndIndex).lastIndexOf('.');
			return methodFullQualifiedName.substring(lastDotBeforeLeftBracket + 1, methodNameEndIndex);
		}
	}
	
	private List<String> getMethodParameterType(String methodFullQualifiedName) {
		List<String> parameterTyes = new ArrayList<String>();
		int leftBracketIndex = methodFullQualifiedName.indexOf('(');
		if (leftBracketIndex >= 0) {
			int leftAngleBracketCount = 0;
			StringBuilder sb = new StringBuilder();
			String parameterStr = methodFullQualifiedName.substring(leftBracketIndex + 1, methodFullQualifiedName.length() - 1);
			for (int i = 0; i < parameterStr.length(); ++i) {
				if (parameterStr.charAt(i) == '<') {
					leftAngleBracketCount++;
				}
				if (parameterStr.charAt(i) == '>') {
					leftAngleBracketCount--;
				}
				if (leftAngleBracketCount == 0 && parameterStr.charAt(i) != '>')  {
					sb.append(parameterStr.charAt(i));
				}
			}

			String[] parameterArray = sb.toString().split(",");
			for (int i = 0; i < parameterArray.length; ++i) {
				String[] temp = parameterArray[i].split("\\.");
				parameterTyes.add(temp[temp.length - 1]);
			}
		}
		return parameterTyes;
	}
	
	protected class MethodSignatureSimilarity implements Comparable<MethodSignatureSimilarity> {
		public String oldMethodFullQulifiedName;
		public String newMethodFullQulifiedName;
		public double similarity;
		
		public MethodSignatureSimilarity(String oldMethod, String newMethod) {
			oldMethodFullQulifiedName = oldMethod;
			newMethodFullQulifiedName = newMethod;
			similarity = 0.25 * calculateReturnTypeSimilarity() + 0.5 * calculateMethodNameSimilarity() + 0.25 * calculateParametersSimilarity();
		}
		
		public double calculateReturnTypeSimilarity() {
			String oldReturnType = getReturnType(oldMethodFullQulifiedName);
			String newReturnType = getReturnType(newMethodFullQulifiedName);
			return calculateStringSimilarity(oldReturnType, newReturnType);
		}
		
		public double calculateMethodNameSimilarity() {
			String oldMethodName = getMethodName(oldMethodFullQulifiedName);
			String newMethodName = getMethodName(newMethodFullQulifiedName);
			return calculateStringSimilarity(oldMethodName, newMethodName);
		}
		
		public double calculateParametersSimilarity() {
			List<String> oldParameters = getMethodParameterType(oldMethodFullQulifiedName);
			List<String> newParameters = getMethodParameterType(newMethodFullQulifiedName);
			
			String oldParameterString = "";
			String newParameterString = "";
			for (String para : oldParameters) {
				oldParameterString += para;
			}
			for (String para : newParameters) {
				newParameterString += para;
			}
			
			return calculateStringSimilarity(oldParameterString, newParameterString);
		}
		
		public List<String> getTokens(String name) {
			List<String> tokens = new ArrayList<String>();
			if (name != null) {
				int start = 0;
				for (int i = 0; i < name.length(); ++i) {
					if (name.charAt(i) == '_' || name.charAt(i) == ' ' || (name.charAt(i) >= '0' && name.charAt(i) <= '9')) {
						if (start < i) {
							tokens.add(name.substring(start, i));
						}
						start = i + 1;
					}
					
					if ((i > 0 && (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') && (name.charAt(i-1) >= 'a' && name.charAt(i-1) <= 'z'))
							|| (i < name.length() - 1 && (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') && (name.charAt(i+1) >= 'a' && name.charAt(i+1) <= 'z'))) {
						if (start < i) {
							tokens.add(name.substring(start, i));
						}
						start = i;
					}
				}
				
				if (start < name.length()) {
					tokens.add(name.substring(start, name.length()));
				}
			}
			return tokens;
		}
		
		public double calculateStringSimilarity(String strA, String strB) {
			if (strA.length() == 0 && strB.length() == 0) return 1.0;
			
			int lenA = strA.length() + 1;
			int lenB = strB.length() + 1;
		    int[][] c = new int[lenA][lenB];
		 
		    // Record the distance of all begin points of each string
		    //初始化方式与背包问题有点不同
		    for(int i = 0; i < lenA; i++) c[i][0] = i;
		    for(int j = 0; j < lenB; j++) c[0][j] = j;
		    
		    c[0][0] = 0;
		    for(int i = 1; i < lenA; i++) {
		    	for(int j = 1; j < lenB; j++) {
		    		if(strB.charAt(j-1) == strA.charAt(i-1))
		    			c[i][j] = c[i-1][j-1];
		    		else
		    			c[i][j] = Math.min(Math.min(c[i][j-1], c[i-1][j]), c[i-1][j-1]) + 1;
		    	}
		    }
		    return 1.0 - c[lenA-1][lenB-1] * 1.0 / (Math.max(strA.length(), strB.length()) * 1.0);
		}

		@Override
		public int compareTo(MethodSignatureSimilarity arg) {
			if (similarity < arg.similarity) {
				return -1;
			}
			else if (similarity == arg.similarity) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
	
	private class ListDataProvider implements ITransactionProvider {
		
		private int index;
		private List<Transaction> transactionList;
		
		public ListDataProvider(List<Transaction> transactionList) {
			this.index = 0;
			this.transactionList = transactionList;
		}

		@Override
		public boolean hasNext() {
			return (transactionList == null) ? false : (index < transactionList.size());
		}

		@Override
		public void resetDataSource() {
			index = 0;
		}

		@Override
		public Transaction getTransaction() {
			return (transactionList == null) ? null : transactionList.get(index++);
		}
		
	}
}
