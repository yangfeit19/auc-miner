package cn.edu.nju.raua.fpmining.aprior;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.raua.core.fpmining.AssociationRule;
import cn.edu.nju.raua.core.fpmining.AssociationRuleList;
import cn.edu.nju.raua.core.fpmining.FrequentItemset;
import cn.edu.nju.raua.core.fpmining.FrequentItemsetList;
import cn.edu.nju.raua.core.transactions.ITransactionProvider;
import cn.edu.nju.raua.core.transactions.Item;
import cn.edu.nju.raua.core.transactions.Transaction;

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
