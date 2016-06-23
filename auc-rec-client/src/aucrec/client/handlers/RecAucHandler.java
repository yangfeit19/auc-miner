package aucrec.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class RecAucHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AucRecThread thread = new AucRecThread("Recommend Thread");
		thread.setUser(true);
		thread.schedule();
		return null;
	}

}
