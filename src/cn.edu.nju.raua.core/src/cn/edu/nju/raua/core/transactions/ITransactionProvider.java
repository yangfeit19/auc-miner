package cn.edu.nju.raua.core.transactions;

public interface ITransactionProvider {

	public boolean hasNext();
	public void resetDataSource();
	public Transaction getTransaction();
	
}
