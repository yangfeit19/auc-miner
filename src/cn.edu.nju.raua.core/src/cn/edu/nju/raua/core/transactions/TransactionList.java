package cn.edu.nju.raua.core.transactions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TransactionSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionList {
	
	@XmlElement(name="Transaction")
	private List<Transaction> transactions;
	
	public TransactionList() {
		transactions = new ArrayList<>();
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void addTransaction(Transaction transaction) {
		if (transaction != null) {
			transactions.add(transaction);
		}
	}
	
	public void addAllTransactions(List<Transaction> transactions) {
		if (transactions != null) {
			this.transactions.addAll(transactions);
		}
	}
	
}
