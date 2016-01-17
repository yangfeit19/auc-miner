package cn.edu.nju.raua.core.fpmining;

import cn.edu.nju.raua.core.transactions.ITransactionProvider;

public interface IFrequentItemsetMiner {

	public FrequentItemsetList miningFrequentItemset(ITransactionProvider provider, int min_sup);
	
}
