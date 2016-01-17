package cn.edu.nju.raua.fpmining.aprior.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.edu.nju.raua.core.transactions.XMLFileDataProvider;

public class GenerateAssociationRuleHandler extends AbstractHandler {
	
	private static final String OutputFrequentItemsetFileName = "FrequentItemset.xml";
	private static final String OutputAssociationRuleFileName = "AssociationRules.xml";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		String transactionFile = PluginUtil.getTransactionFile(window.getShell());
		
		if (transactionFile != null) {
			int lastSlashIndex = transactionFile.lastIndexOf(File.separator);
			String outputFrequentItemsetFile = transactionFile.substring(0, lastSlashIndex + 1) + OutputFrequentItemsetFileName;
			String outputAssociationRuleFile = transactionFile.substring(0, lastSlashIndex + 1) + OutputAssociationRuleFileName;
			
			GenerateAssociationRuleThread thread = new GenerateAssociationRuleThread(new XMLFileDataProvider(transactionFile), outputFrequentItemsetFile, outputAssociationRuleFile);
			thread.setUser(true);
			thread.schedule();
		}
		
		return null;
	}

}
