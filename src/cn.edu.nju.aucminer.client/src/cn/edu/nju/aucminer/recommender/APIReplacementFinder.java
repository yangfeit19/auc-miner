package cn.edu.nju.aucminer.recommender;

import java.util.ArrayList;
import java.util.List;

public class APIReplacementFinder {
	
	private String clientProjectName;
	private IAPIUsageFilter apiUsageFilter;
	private IAPIReplacementSearcher apiReplacementSearcher;

	public APIReplacementFinder(String clientProjectName, IAPIUsageFilter apiUsageFilter, IAPIReplacementSearcher apiReplacementSearcher) {
		this.clientProjectName = clientProjectName;
		this.apiUsageFilter = apiUsageFilter;
		this.apiReplacementSearcher = apiReplacementSearcher;
	}
	
	
	public List<InvocationsReplacement> findAPIReplacement() {
		List<InvocationsReplacement> result = new ArrayList<InvocationsReplacement>();
		
		APIUsageExtractor extractor = new APIUsageExtractor(clientProjectName, apiUsageFilter);
		List<InvocationInfo> apiUsageList = extractor.extractAPIUsage();
		
		for (InvocationInfo apiUsage : apiUsageList) {
			List<MethodInfo> oldMethods = new ArrayList<MethodInfo>();
			oldMethods.add(apiUsage.getMethodInfo());
			APIReplacementSearchResult searchResult = apiReplacementSearcher.findReplacementForAPI(oldMethods);
			if (searchResult != null) {
				List<MethodInfo> newMethods = searchResult.getNewMethods();
				List<InvocationInfo> originalInvocations = new ArrayList<InvocationInfo>();
				originalInvocations.add(apiUsage);
				List<InvocationInfo> replacement = new ArrayList<InvocationInfo>();
				for (MethodInfo methodInfo : newMethods) {
					replacement.add(new InvocationInfo(methodInfo, null, null, -1, -1, -1));
				}
				result.add(new InvocationsReplacement(originalInvocations, replacement, searchResult.getExamples()));
			}
		}
		
		return result;
	}
}
