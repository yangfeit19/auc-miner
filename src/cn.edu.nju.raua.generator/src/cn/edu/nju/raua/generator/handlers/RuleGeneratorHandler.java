package cn.edu.nju.raua.generator.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.edu.nju.raua.core.configuration.Configuration;
import cn.edu.nju.raua.utility.RAUAUtility;

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
