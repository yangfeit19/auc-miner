package cn.edu.nju.aucminer.recommender;

public class DefaultAPIUsageFilter implements IAPIUsageFilter {

	@Override
	public boolean isAPIUsageOfSpecifiedLibrary(InvocationInfo invocation) {
		return true;
	}

}
