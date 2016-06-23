package aucrec.client.rec;

public class InvocationFilter implements IInvocationFilter {

	@Override
	public boolean isAPIUsageOfSpecifiedLibrary(InvocationInfo invocation) {
		return true;
	}

}
