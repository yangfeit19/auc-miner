
package cn.edu.nju.raua.core.transactions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemSet")
public class Itemset {
	
	@XmlElement(name="Item")
	private Set<Item> items = new HashSet<>();
	
	public int size() {
		return (items == null) ? 0 : items.size();
	}
	
	public boolean containsItem(Item item) {
		return (items == null) ? false : items.contains(item);
	}
	
	public boolean containsAllItems(List<Item> items) {
		if (items != null) {
			for (Item item : items) {
				if (!this.items.contains(item)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public Set<Item> getItems() {
		if (items == null) {
			return new HashSet<Item>();
		}
		return items;
	}
	
	public void addItem(Item item) {
		if (items == null) {
			items = new HashSet<Item>();
		}
		items.add(item);
	}
	
	public void removeItem(Item item) {
		if (items != null) {
			items.remove(item);
		}
	}
	
	public void addItems(Set<Item> itemset) {
		if (items == null) {
			items = new HashSet<Item>();
		}
		items.addAll(itemset);
	}
	
	public boolean isSubset(Itemset itemset, boolean proper) {
		if ((itemset == null) || (items == null)) {
			return false;
		}
		boolean subset = items.containsAll(itemset.getItems());
		if (proper) {
			subset = subset && (items.size() > itemset.getItems().size());
		}
		return subset;
	}
	
	public static Itemset union(Itemset itemset1, Itemset itemset2) {
		Itemset newItemset = new Itemset();
		newItemset.addItems(itemset1.getItems());
		newItemset.addItems(itemset2.getItems());
		return newItemset;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Itemset) {
			Itemset set2 = (Itemset)object;
			if (items.size() == set2.size()) {
				for (Item item : items) {
					if (!set2.containsItem(item)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashcode = 1;
		for (Item item : items) {
			hashcode ^= item.hashCode();
		}
		return hashcode;
	}
	
}
