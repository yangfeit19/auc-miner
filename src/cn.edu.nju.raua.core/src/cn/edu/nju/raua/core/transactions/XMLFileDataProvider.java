package cn.edu.nju.raua.core.transactions;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLFileDataProvider implements ITransactionProvider {
	
	private List<Transaction> transactions;
	private int index = 0;
	
	public XMLFileDataProvider (String filePath) {
		if (filePath == null) {
			throw new NullPointerException("The 'filePath' argument cannot be null.");
		}
		
		try {
			JAXBContext jc = JAXBContext.newInstance(TransactionList.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			System.gc();
			TransactionList transactionList = (TransactionList)unmarshaller.unmarshal(new File(filePath));
			transactions = transactionList.getTransactions();
			System.gc();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean hasNext() {
		return (transactions == null) ? false : (index < transactions.size());
	}

	@Override
	public void resetDataSource() {
		index = 0;
	}

	@Override
	public Transaction getTransaction() {
		return (transactions == null) ? null : transactions.get(index++);
	}

}
