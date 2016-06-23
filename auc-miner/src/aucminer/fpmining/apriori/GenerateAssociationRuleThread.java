package aucminer.fpmining.apriori;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import aucminer.core.AssociationRule;
import aucminer.core.AssociationRuleList;
import aucminer.core.FrequentItemsetList;
import aucminer.core.ITransactionProvider;
import aucminer.core.RAUAUtility;

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
