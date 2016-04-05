package cn.edu.nju.aucminer.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class RecommendHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RecommendThread thread = new RecommendThread("Recommend Thread");
		thread.setUser(true);
		thread.schedule();
		return null;
	}

}
