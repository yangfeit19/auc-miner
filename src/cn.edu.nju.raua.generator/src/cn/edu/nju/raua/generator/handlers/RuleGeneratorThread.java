package cn.edu.nju.raua.generator.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import cn.edu.nju.raua.core.configuration.Configuration;
import cn.edu.nju.raua.core.fpmining.AssociationRuleList;
import cn.edu.nju.raua.generator.ApiChangeRuleGenerator;

public class RuleGeneratorThread extends Job {

	private Configuration rauaConfig;
	
	public RuleGeneratorThread(Configuration rauaConfig) {
		super("Run ALL");
		this.rauaConfig = rauaConfig;
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
