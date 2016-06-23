package aucrec.client.rec;

import java.util.ArrayList;
import java.util.List;

public class AucRecFinder {
	
	private String clientProjectName;
	private IInvocationFilter apiUsageFilter;
	private IAucRecSearcher apiReplacementSearcher;

	public AucRecFinder(String clientProjectName, IInvocationFilter apiUsageFilter, IAucRecSearcher apiReplacementSearcher) {
		this.clientProjectName = clientProjectName;
		this.apiUsageFilter = apiUsageFilter;
		this.apiReplacementSearcher = apiReplacementSearcher;
	}
	
	
	public List<Replacement> findAPIReplacement() {
		List<Replacement> result = new ArrayList<Replacement>();
		
		InvocationsFinder extractor = new InvocationsFinder(clientProjectName, apiUsageFilter);
		List<InvocationInfo> apiUsageList = extractor.extractAPIUsage();
		
		for (InvocationInfo apiUsage : apiUsageList) {
			List<MethodSignature> oldMethods = new ArrayList<MethodSignature>();
			oldMethods.add(apiUsage.getMethodInfo());
			List<AucRule> searchResult = apiReplacementSearcher.searchAucRec(oldMethods);
			if (!searchResult.isEmpty()) {
				List<MethodSignature> newMethods = searchResult.get(0).getConsequent();
				List<InvocationInfo> originalInvocations = new ArrayList<InvocationInfo>();
				originalInvocations.add(apiUsage);
				List<InvocationInfo> replacement = new ArrayList<InvocationInfo>();
				for (MethodSignature methodInfo : newMethods) {
					replacement.add(new InvocationInfo(methodInfo, null, null, -1, -1, -1));
				}
				result.add(new Replacement(originalInvocations, replacement, searchResult.get(0).getExamples()));
			}
		}
		
		return result;
	}
}
