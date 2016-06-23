package aucminer.core;

public interface ITransactionProvider {

	public boolean hasNext();
	public void resetDataSource();
	public Transaction getTransaction();
	
}
