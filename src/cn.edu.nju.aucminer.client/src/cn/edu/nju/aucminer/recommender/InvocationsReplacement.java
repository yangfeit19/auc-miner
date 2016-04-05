package cn.edu.nju.aucminer.recommender;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.aucminer.client.adaptionexamples.ApiAdaptionExample;

public class InvocationsReplacement {
	
	private List<InvocationInfo> oldInvocations;
	private List<InvocationInfo> newInvocations;
	private List<ApiAdaptionExample> examplesList;
	
	public InvocationsReplacement(List<InvocationInfo> oldInvocations, 
			List<InvocationInfo> newInvocations, List<ApiAdaptionExample> examplesList) {
		this.oldInvocations = new ArrayList<>(oldInvocations);
		this.newInvocations = new ArrayList<>(newInvocations);
		this.examplesList = new ArrayList<>(examplesList);
	}
	
	public List<InvocationInfo> getOldInvocations() {
		return oldInvocations;
	}

	public List<InvocationInfo> getNewInvocations() {
		return newInvocations;
	}

	public List<ApiAdaptionExample> getExamplesList() {
		return examplesList;
	}

}
