package aucrec.client.rec;

import java.util.ArrayList;
import java.util.List;

import aucrec.client.examples.UsageChangeExample;

public class Replacement {
	
	private List<InvocationInfo> oldInvocations;
	private List<InvocationInfo> newInvocations;
	private List<UsageChangeExample> examplesList;
	
	public Replacement(List<InvocationInfo> oldInvocations, 
			List<InvocationInfo> newInvocations, List<UsageChangeExample> examplesList) {
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

	public List<UsageChangeExample> getExamplesList() {
		return examplesList;
	}

}
