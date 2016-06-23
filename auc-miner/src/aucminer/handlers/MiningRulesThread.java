package aucminer.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import aucminer.AucMinerActivator;
import aucminer.core.AssociationRuleList;
import aucminer.core.Configuration;
import aucminer.rulegenerator.ApiChangeRuleGenerator;

public class MiningRulesThread extends Job {

	private Configuration rauaConfig;
	
	public MiningRulesThread() {
		super("Run ALL");
		String workingDirectory = AucMinerActivator.getDefault().getConfiguration().getResultStorage().getFileStoragePath();
		String oldVersionProject = AucMinerActivator.getDefault().getConfiguration().getOldVersion();
		String newVersionProject = AucMinerActivator.getDefault().getConfiguration().getNewVersion();
		this.rauaConfig = new Configuration(workingDirectory, oldVersionProject, newVersionProject);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean includeField = true;
		boolean storeToFile = true;
		boolean usingSplit = false; // Note
		int splitThreshold = 6;
		int minSupport = 2;
		float minConfidence = 100;
		
		long start = System.currentTimeMillis();
		
		ApiChangeRuleGenerator generator = new ApiChangeRuleGenerator(rauaConfig);
		AssociationRuleList apiChangeRules = generator.generateApiChangeRules(includeField, storeToFile, usingSplit, splitThreshold, minSupport, minConfidence);
		System.out.println(String.format("Generate %d API change rules", apiChangeRules.getRules().size()));
		
		long end = System.currentTimeMillis();
		System.out.println("运行时间：" + (end - start) / 1000.0 + "秒");
		
		return Status.OK_STATUS;
	}

}
