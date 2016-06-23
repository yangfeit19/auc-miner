package aucrec.client.rec;

public interface IInvocationFilter {
	
	public boolean isAPIUsageOfSpecifiedLibrary(InvocationInfo invocation);
	
}
