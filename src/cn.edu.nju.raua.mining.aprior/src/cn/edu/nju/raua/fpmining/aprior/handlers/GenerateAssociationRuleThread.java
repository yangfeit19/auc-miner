package cn.edu.nju.raua.fpmining.aprior.handlers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import cn.edu.nju.raua.core.fpmining.AssociationRule;
import cn.edu.nju.raua.core.fpmining.AssociationRuleList;
import cn.edu.nju.raua.core.fpmining.FrequentItemsetList;
import cn.edu.nju.raua.core.transactions.ITransactionProvider;
import cn.edu.nju.raua.fpmining.aprior.Apriori;
import cn.edu.nju.raua.fpmining.aprior.AssociationRuleGenerator;
import cn.edu.nju.raua.utility.RAUAUtility;

public class GenerateAssociationRuleThread extends Job {

	private ITransactionProvider provider;
	private String fiRilePath;
	private String arFilePath;
	
	public GenerateAssociationRuleThread(ITransactionProvider provider, String fiFilePath, String arFilePath) {
		super("Run Apriori");
		this.provider = provider;
		this.fiRilePath = fiFilePath;
		this.arFilePath = arFilePath;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean miningCloSet = true;
		
		Apriori apriori = new Apriori(100, miningCloSet);
		FrequentItemsetList freqItemsetList = apriori.miningFrequentItemset(provider, 2); // >= 2
		
		freqItemsetList.sortByDescendingSupport();
		RAUAUtility.outputFrequentItemsetList(freqItemsetList, fiRilePath);
		
		AssociationRuleList rules = AssociationRuleGenerator.generateAssociationRules(freqItemsetList, provider);
		
		double min_conf = 100;
		List<AssociationRule> ruleList = rules.getRules();
		Iterator<AssociationRule> iterator = ruleList.iterator();
		while (iterator.hasNext()) {
			AssociationRule assRule = iterator.next();
			if (assRule.getConfidence() < min_conf) {
				iterator.remove();
			}
		}
		
		rules.sortByDescendingSupport();
		RAUAUtility.outputAssociationRuleList(rules, arFilePath);
		
		return Status.OK_STATUS;
	}

}
