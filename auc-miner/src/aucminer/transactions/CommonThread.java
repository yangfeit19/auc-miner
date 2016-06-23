package aucminer.transactions;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.progress.IProgressConstants;

import aucminer.AucMinerActivator;
import aucminer.core.Configuration;
import aucminer.core.RAUAUtility;;

public class CommonThread extends Job {
	
	private String fileName;
	private CommonHandler.BuildType type;
	
	private static Map<CommonHandler.BuildType, String> type2NameMap;
	
	static {
		type2NameMap = new HashMap<CommonHandler.BuildType, String>();
		type2NameMap.put(CommonHandler.BuildType.OLD, "Building Old Version Call Dependency Model");
		type2NameMap.put(CommonHandler.BuildType.NEW, "Building New Version Call Dependency Model");
		type2NameMap.put(CommonHandler.BuildType.ALL, "Generating Transactions");
	}
	
	public CommonThread(CommonHandler.BuildType type, String fileName) {
		super(type2NameMap.get(type));
		this.type = type;
		this.fileName = fileName;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		boolean includeField = true;
		boolean storeToFile = true;
		boolean usingSplit = true;
		int splitThreshold = 6;
		
		Configuration config = RAUAUtility.parseConfiguration(fileName);
		if (config != null) {
			switch (type) {
				case OLD:
					TransactionUtility.buildOldCDModel(config.getOldVersionProject(), config.getSaveDir(), includeField, storeToFile);
					break;
				case NEW:
					TransactionUtility.buildNewCDModel(config.getNewVersionProject(), config.getSaveDir(), includeField, storeToFile);
					break;
				case ALL:
					CDModel oldModel = TransactionUtility.buildOldCDModel(config.getOldVersionProject(), config.getSaveDir(), includeField, storeToFile);
					CDModel newModel = TransactionUtility.buildNewCDModel(config.getNewVersionProject(), config.getSaveDir(), includeField, storeToFile);
					
					TransactionUtility.extractTransactions(oldModel, newModel, usingSplit, splitThreshold, storeToFile, config.getSaveDir());
					break;
			}
			
			setProperty(IProgressConstants.ICON_PROPERTY, null);
			setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
			setProperty(IProgressConstants.ACTION_PROPERTY, getReservationCompletedAction());
			
			return Status.OK_STATUS;
		}
		return new Status(IStatus.ERROR, AucMinerActivator.PLUGIN_ID, 1, "error", null);
	}
	
	protected Action getReservationCompletedAction()
	{
		return new Action(type2NameMap.get(type) + " status")
		{
			public void run()
			{				
				MessageDialog.openInformation(null, type2NameMap.get(type) + " Complete", 
						type2NameMap.get(type) + " has been completed");
			}
		};
	}

}
