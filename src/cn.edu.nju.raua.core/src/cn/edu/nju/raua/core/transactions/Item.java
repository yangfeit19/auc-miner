package cn.edu.nju.raua.core.transactions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Item", propOrder={"added", "callee"})
public class Item {
	
	@XmlElement(name="Added")
	private boolean added;
	@XmlElement(name="Callee")
	private String callee;
	
	public Item() {
		
	}
	
	public Item(boolean added, String callee) {
		this.added = added;
		this.callee = callee;
	}
	
	public boolean isAdded() {
		return added;
	}

	public String getCallee() {
		return callee;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Item) {
			Item other = (Item)object;
			return (added == other.isAdded()) && (callee.equals(other.getCallee()));
		}
		return false;	
	}
	
	@Override
	public int hashCode() {
		String addedType = added ? "added" : "removed";
		return addedType.hashCode()  ^ callee.hashCode();
	}
}
