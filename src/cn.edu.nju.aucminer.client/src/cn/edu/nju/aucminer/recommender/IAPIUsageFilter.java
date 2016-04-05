package cn.edu.nju.aucminer.recommender;

public interface IAPIUsageFilter {
	
	public boolean isAPIUsageOfSpecifiedLibrary(InvocationInfo invocation);
	
}
