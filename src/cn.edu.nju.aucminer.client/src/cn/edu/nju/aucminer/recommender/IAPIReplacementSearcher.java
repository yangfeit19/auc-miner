package cn.edu.nju.aucminer.recommender;

import java.util.List;

public interface IAPIReplacementSearcher {
	
	public APIReplacementSearchResult findReplacementForAPI(List<MethodInfo> oldMethods);
	
}
