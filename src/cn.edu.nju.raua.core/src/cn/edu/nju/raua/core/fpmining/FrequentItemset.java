package cn.edu.nju.raua.core.fpmining;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import cn.edu.nju.raua.core.transactions.Itemset;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FrequentItemset", propOrder={"itemset", "support"})
public class FrequentItemset {
	
	private Itemset itemset;
	private int support;
	
	public FrequentItemset(Itemset itemset, int support) {
		if ((itemset == null) || (support < 0)) {
			throw new IllegalArgumentException();
		}
		
		this.itemset = itemset;
		this.support = support;
	}

	public Itemset getItemset() {
		return itemset;
	}

	public int getSupport() {
		return support;
	}
	
	public void increaseSupport() {
		support++;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof FrequentItemset) {
			return itemset.equals(((FrequentItemset)object).getItemset());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return itemset.hashCode() * 7 + new Integer(support).hashCode() * 13;
	}
}
