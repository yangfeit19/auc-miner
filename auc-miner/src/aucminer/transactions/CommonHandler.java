package aucminer.transactions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import aucminer.core.RAUAUtility;

public class CommonHandler extends AbstractHandler {
	
	public enum BuildType {
		OLD,
		NEW,
		ALL
	}
	
	private BuildType buildType;
	
	public CommonHandler(BuildType type) {
		super();
		this.buildType = type;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		String configFile = RAUAUtility.getConfigureFile(window.getShell());
		
		if (configFile != null) {
			CommonThread thread = new CommonThread(buildType, configFile);
			thread.setUser(true);
			thread.schedule();
		}
		
		return null;
	}

}
