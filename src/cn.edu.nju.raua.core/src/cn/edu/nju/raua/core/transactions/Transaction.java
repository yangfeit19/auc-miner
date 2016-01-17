package cn.edu.nju.raua.core.transactions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Transaction", propOrder={"tid", "items"})
public class Transaction {

	@XmlElement(name="TID")
	private String tid;
	@XmlElement(name="ItemSet")
	private Itemset items;
	
	public Transaction() {
		
	}
	
	public Transaction(String tid) {
		if ((tid == null) || tid.isEmpty()) {
			throw new NullPointerException("parameter 'tid' cannot be null or empty");
		}
		this.tid = tid;
		this.items = new Itemset();
	}
	
	public Transaction(String tid, Itemset itemset) {
		if ((tid == null) || tid.isEmpty()) {
			throw new NullPointerException("parameter 'tid' cannot be null or empty");
		}
		if (itemset == null) {
			throw new NullPointerException("Parameter 'itemset' cannot be null");
		}
		
		this.tid = tid;
		this.items = itemset;
	}

	public String getTid() {
		return tid;
	}

	public Itemset getItemset() {
		return items;
	}

	public void addItem(Item item) {
		if (item != null) {
			items.addItem(item);
		}
	}
	
}
