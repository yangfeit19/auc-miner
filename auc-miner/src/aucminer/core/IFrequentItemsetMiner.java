package aucminer.core;

public interface IFrequentItemsetMiner {

	public FrequentItemsetList miningFrequentItemset(ITransactionProvider provider, int min_sup);
	
}
