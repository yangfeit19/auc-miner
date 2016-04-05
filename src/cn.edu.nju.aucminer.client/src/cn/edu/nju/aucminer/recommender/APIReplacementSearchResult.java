package cn.edu.nju.aucminer.recommender;

import java.util.List;

import cn.edu.nju.aucminer.client.adaptionexamples.ApiAdaptionExample;

public class APIReplacementSearchResult {
	private List<MethodInfo> newMethods;
	private List<ApiAdaptionExample> examples;
	
	public APIReplacementSearchResult(List<MethodInfo> newMethods, List<ApiAdaptionExample> examples) {
		super();
		this.newMethods = newMethods;
		this.examples = examples;
	}

	public List<MethodInfo> getNewMethods() {
		return newMethods;
	}

	public List<ApiAdaptionExample> getExamples() {
		return examples;
	}
	
}
