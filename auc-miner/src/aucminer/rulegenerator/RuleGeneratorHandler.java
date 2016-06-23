package aucminer.rulegenerator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import aucminer.core.Configuration;
import aucminer.core.RAUAUtility;

public class RuleGeneratorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		String configFilePath = RAUAUtility.getConfigureFile(window.getShell());
		Configuration rauaConfig = RAUAUtility.parseConfiguration(configFilePath);
		
		if (rauaConfig != null) {
			RuleGeneratorThread thread = new RuleGeneratorThread(rauaConfig);
			thread.setUser(true);
			thread.schedule();
		}
		
		return null;
	}

}
