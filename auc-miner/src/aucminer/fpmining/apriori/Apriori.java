package aucminer.fpmining.apriori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import aucminer.core.FrequentItemset;
import aucminer.core.FrequentItemsetList;
import aucminer.core.IFrequentItemsetMiner;
import aucminer.core.ITransactionProvider;
import aucminer.core.Item;
import aucminer.core.Itemset;
import aucminer.core.Transaction;

public class Apriori implements IFrequentItemsetMiner {
	
	private int maxTransactionSize;
	private boolean miningCloSet;
	
	public Apriori(int maxTransactionSize, boolean miningCloSet) {
		this.maxTransactionSize = maxTransactionSize;
		this.miningCloSet = miningCloSet;
	}
	
	@Override
	public FrequentItemsetList miningFrequentItemset(ITransactionProvider provider, int min_sup) {
		
		int[] array = new int[maxTransactionSize];
		int[] added = new int[maxTransactionSize];
		int[] removed = new int[maxTransactionSize];
		provider.resetDataSource();
		while (provider.hasNext()) {
			Transaction transaction = provider.getTransaction();
			array[transaction.getItemset().size()]++;
			for (Item item : transaction.getItemset().getItems()) {
				if (item.isAdded()) {
					added[transaction.getItemset().size()]++;
				}
				else {
					removed[transaction.getItemset().size()]++;
				}
			}
		}
		
		for (int i = 0; i < maxTransactionSize; i++) {
			System.out.print("(" + i + ", " + array[i] + ", " + added[i] + ", " + removed[i] + ")");
		}
		System.out.println();
		
		
		FrequentItemsetList preSizeItemsetList = findFrequentOneItemsets(provider, min_sup);
		FrequentItemsetList freqItemsetList = new FrequentItemsetList(preSizeItemsetList.toList());
		
		int iterator = 1;
		while (preSizeItemsetList.size() > 0) {
			System.out.print(iterator + " || " + preSizeItemsetList.size() + " || ");
			Set<Itemset> candidates = generateCandidateItemsetSet(preSizeItemsetList);
			System.out.print(" final candidates:" + candidates.size());
			
			preSizeItemsetList.clear();
			provider.resetDataSource();
			while (provider.hasNext()) {
				
				Transaction transaction = provider.getTransaction();
				Itemset itemset = transaction.getItemset();
				for (Itemset candidate : candidates) {
					
					if (itemset.isSubset(candidate, false)) {
//						System.out.println(iterator + "--------------------------------------------------");
//						for (Item item : itemset.getItems()) {
//							System.out.println("         (" + item.isAdded() + "|" + item.getCallee() + ")");
//						}
//						System.out.println("\n");
//						for (Item item : candidate.getItems()) {
//							System.out.println("         (" + item.isAdded() + "|" + item.getCallee() + ")");
//						}
						preSizeItemsetList.addFrequentItemset(candidate);
					}
				}
			}
			iterator++;
			preSizeItemsetList.filterFrequentItemset(min_sup);
			System.out.println("||   " + preSizeItemsetList.size() + "    ||   ");
			freqItemsetList.addFrequentItemsetList(preSizeItemsetList);
		}
		
		if (miningCloSet) {
			generateClosedSet(freqItemsetList);
		}
		 
		return freqItemsetList;
	}

	private FrequentItemsetList findFrequentOneItemsets(ITransactionProvider provider, int min_sup) {
		
		FrequentItemsetList freqOneItemsetList = new FrequentItemsetList();
		
		provider.resetDataSource();
		while(provider.hasNext()) {
			Transaction transaction = provider.getTransaction();
			for (Item item : transaction.getItemset().getItems()) {
				Itemset oneItemset = new Itemset();
				oneItemset.addItem(item);
				freqOneItemsetList.addFrequentItemset(oneItemset);
			}
		}
		freqOneItemsetList.filterFrequentItemset(min_sup);
		
		return freqOneItemsetList;
	}
	
	private Set<Itemset> generateCandidateItemsetSet(FrequentItemsetList preSizeItemsetList) {
		
		int total = 0;
		Set<Itemset> candidateItemsetSet = new HashSet<>();
		if (preSizeItemsetList != null) {
			List<FrequentItemset> freqItemsetList = preSizeItemsetList.toList();
			for (FrequentItemset freqItemset1 : freqItemsetList) {
				for (FrequentItemset freqItemset2 : freqItemsetList) {
					Itemset itemset1 = freqItemset1.getItemset();
					Itemset itemset2 = freqItemset2.getItemset();
					if (itemset1.size() == itemset2.size()) {
						Itemset candidate = Itemset.union(itemset1, itemset2);
						if (candidate.size() == itemset1.size() + 1) {
							total++;
							if (!hasInfrequentSubset(candidate, preSizeItemsetList)) {
								candidateItemsetSet.add(candidate);
							}
						}
					}
				}
			}
		}
		System.out.print(" total candidates:" + total);
		return candidateItemsetSet;
	}
	
	private boolean hasInfrequentSubset(Itemset itemset, FrequentItemsetList preSizeItemsetList) {
		
		Item[] itemArray = itemset.getItems().toArray(new Item[0]);
		for ( int i = 0; i < itemArray.length; i++) {
			Itemset preSizeSubset = new Itemset();
			for (int j = 0; j < itemArray.length; j++) {
				if (i != j) {
					preSizeSubset.addItem(itemArray[j]);
				}
			}
			if (!preSizeItemsetList.itemsetExistInList(preSizeSubset)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void generateClosedSet(FrequentItemsetList freqItemsetList) {
		List<FrequentItemset> list = freqItemsetList.toList();
		List<FrequentItemset> original = new ArrayList<>(list);
		Iterator<FrequentItemset> iterator = list.iterator();
		while (iterator.hasNext()) {
			FrequentItemset freqItem = iterator.next();
			for (FrequentItemset freqItem2 : original) {
				if ((freqItem2.getSupport() == freqItem.getSupport()) 
						&& freqItem2.getItemset().isSubset(freqItem.getItemset(), true)) {
					iterator.remove();
					break;
				}
			}
		}
	}
	
}
