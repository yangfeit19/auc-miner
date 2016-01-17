package cn.edu.nju.raua.fpmining.closetplus;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.raua.core.fpmining.FrequentItemset;
import cn.edu.nju.raua.core.fpmining.FrequentItemsetList;
import cn.edu.nju.raua.core.fpmining.IFrequentItemsetMiner;
import cn.edu.nju.raua.core.transactions.ITransactionProvider;
import cn.edu.nju.raua.core.transactions.Itemset;
import cn.edu.nju.raua.core.transactions.Transaction;
import cn.edu.nju.raua.fpmining.closetplus.fptree.FPTree;
import cn.edu.nju.raua.fpmining.closetplus.fptree.FPTreeNode;
import cn.edu.nju.raua.fpmining.closetplus.fptree.HeaderTable;
import cn.edu.nju.raua.fpmining.closetplus.fptree.HeaderTableItem;

public class CLOSETPlus implements IFrequentItemsetMiner {
	
	private FrequentItemsetList freqCloSetList = new FrequentItemsetList();

	@Override
	public FrequentItemsetList miningFrequentItemset(ITransactionProvider provider, int min_sup) {
		
		freqCloSetList.clear();
		
		FPTree fpTree = new FPTree(provider, min_sup);
		recursiveMiningClosedItemset(fpTree, new Itemset(), min_sup);
		
		return freqCloSetList;
	}
	
	private void recursiveMiningClosedItemset(FPTree fpTree, Itemset prefix, int min_sup) {
	
		HeaderTable headerTable = fpTree.getHeaderTable();
		List<HeaderTableItem> tableItemList = headerTable.getReverseTableItemList();
		int index = 0;
		while (index < tableItemList.size()) {
			HeaderTableItem tableItem = tableItemList.get(index);
			
			boolean subItemsetPruning = false;
			for (FrequentItemset fis : freqCloSetList.toList()) {
				if (fis.getItemset().isSubset(prefix, true) && 
					fis.getItemset().containsItem(tableItem.getItem()) &&
					(tableItem.getSupportCount() == fis.getSupport())) {
					subItemsetPruning = true;
					break;
				}
			}
			if (subItemsetPruning) continue;
			
			FPTreeNode sideLinkedNode = tableItem.getSideLinkPointer();
			boolean itemMerging = (sideLinkedNode.getSideLinkNext() == null);
			List<Transaction> projectedTransactionList = new ArrayList<>();
			while (sideLinkedNode != null) {
				Itemset itemset = new Itemset();
				FPTreeNode parentNode = sideLinkedNode.getParent();
				while(parentNode.getParent() != null) {
					itemset.addItem(parentNode.getItem());
					parentNode = parentNode.getParent();
				}
				
				if (itemMerging) {
					Itemset cloSet = new Itemset();
					cloSet.addItems(prefix.getItems());
					cloSet.addItems(itemset.getItems());
					FrequentItemset freqCloSet = new FrequentItemset(cloSet, tableItem.getSupportCount());
					freqCloSetList.addFrequentItemset(freqCloSet);
				}
				
				if (itemset.size() > 0) {
					Transaction transaction = new Transaction("projected", itemset);
					for (int i = 0; i < sideLinkedNode.getCount(); i++) {
						projectedTransactionList.add(transaction);
					}
				}
				sideLinkedNode = sideLinkedNode.getSideLinkNext();
			}
			
			if (itemMerging) continue;
			
			FPTree projectedFPTree = new FPTree(new ConditionalProjectedTransactionsProvider(projectedTransactionList), min_sup);
			int j = index + 1;
			while ( j < tableItemList.size()) {
				if (projectedFPTree.getHeaderTable().getTableItemList().contains(tableItemList.get(j))) {
					tableItemList.remove(j);
					continue;
				}
				j++;
			}
			
			prefix.addItem(tableItem.getItem());
			recursiveMiningClosedItemset(projectedFPTree, prefix, min_sup);
			prefix.removeItem(tableItem.getItem());
			
			index++;
		}
	}
	
	protected class ConditionalProjectedTransactionsProvider implements ITransactionProvider {
		
		private List<Transaction> transactions;
		private int index;
		
		public ConditionalProjectedTransactionsProvider(List<Transaction> transactions) {
			this.transactions = transactions;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < transactions.size();
		}

		@Override
		public void resetDataSource() {
			index = 0;
		}

		@Override
		public Transaction getTransaction() {
			return transactions.get(index++);
		}
	}

}
