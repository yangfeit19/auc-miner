package aucminer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class MiningRulesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MiningRulesThread thread = new MiningRulesThread();
		thread.setUser(true);
		thread.schedule();
		return null;
	}

}
