package aucminer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FrequentItemsetList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FrequentItemsetList {
	
	@XmlElement(name="FrequentItemset")
	private List<FrequentItemset> freqItemsetList;
	
	public FrequentItemsetList() {
		freqItemsetList = new ArrayList<>();
	}
	
	public FrequentItemsetList(List<FrequentItemset> freqItemsetList) {
		if (freqItemsetList == null) {
			this.freqItemsetList = new ArrayList<>();
		}
		else {
			this.freqItemsetList = new ArrayList<>(freqItemsetList);
		}
	}
	
	public void clear() {
		freqItemsetList.clear();
	}
	
	public List<FrequentItemset> toList() {
		return freqItemsetList;
	}
	
	public int size() {
		return freqItemsetList.size();
	}
	
	public boolean itemsetExistInList(Itemset itemset) {
		
		boolean exist = false;
		for (FrequentItemset i : freqItemsetList) {
			if (i.getItemset().equals(itemset)) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public void addFrequentItemset(Itemset itemset) {
		boolean exist = false;
		for (FrequentItemset i : freqItemsetList) {
			if (i.getItemset().equals(itemset)) {
				exist = true;
				i.increaseSupport();
				break;
			}
		}
		
		if (!exist) {
			FrequentItemset freqItemset = new FrequentItemset(itemset, 1);
			freqItemsetList.add(freqItemset);
		}
	}
	
	public void addFrequentItemset(FrequentItemset itemset) {
		freqItemsetList.add(itemset);
	}
	
	public void addFrequentItemsetList(FrequentItemsetList itemsetList) {
		freqItemsetList.addAll(itemsetList.toList());
	}
	
	public void filterFrequentItemset(int min_support) {
		Iterator<FrequentItemset> iterator = freqItemsetList.iterator();
		while (iterator.hasNext()) {
			FrequentItemset freqItemset = iterator.next();
			if (freqItemset.getSupport() < min_support) {
				iterator.remove();
			}
		}
	}
	
	public void sortByDescendingSupport() {
		Collections.sort(freqItemsetList, new FrequentItemsetComparator());
	}
	
	class FrequentItemsetComparator implements Comparator<FrequentItemset> {

		@Override
		public int compare(FrequentItemset arg0, FrequentItemset arg1) {
			return arg1.getSupport() - arg0.getSupport();
		}

	}

}
