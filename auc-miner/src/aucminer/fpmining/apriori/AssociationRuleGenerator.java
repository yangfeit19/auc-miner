package aucminer.fpmining.apriori;

import java.util.ArrayList;
import java.util.List;

import aucminer.core.AssociationRule;
import aucminer.core.AssociationRuleList;
import aucminer.core.FrequentItemset;
import aucminer.core.FrequentItemsetList;
import aucminer.core.ITransactionProvider;
import aucminer.core.Item;
import aucminer.core.Transaction;

public class AssociationRuleGenerator {

	public static AssociationRuleList generateAssociationRules(FrequentItemsetList freqItemsetList, ITransactionProvider provider) {
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
					associationRules.addAssociationRule(new AssociationRule(removed, added, itemset.getSupport()));
				}
			}
		}
		calculateConfidence(associationRules, provider);
		return associationRules;
	}
	
	private static void calculateConfidence(AssociationRuleList rules, ITransactionProvider provider) {
		if (provider != null) {
			for (AssociationRule rule : rules.getRules()) {
				double supportAntecedent = 0;
				provider.resetDataSource();
				while (provider.hasNext()) {
					Transaction transaction = provider.getTransaction();
					if (transaction.getItemset().containsAllItems(rule.getAntecedent())) {
						supportAntecedent++;
					}
				}
				
				if (supportAntecedent == 0) {
					System.out.println("error occurs in calculateConfidence function");
					continue;
				}
				rule.setConfidence((rule.getSupport() * 1.0)/supportAntecedent*100);
			}
		}
	}
}
