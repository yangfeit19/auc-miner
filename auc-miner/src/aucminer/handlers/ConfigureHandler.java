package aucminer.handlers;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import aucminer.AucMinerActivator;
import aucminer.configuration.editor.ConfigFormEditor;

public class ConfigureHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {  
            public void run() {
            	// Get the active window
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();  
        		if (window == null) return;
        		
        		// Get the active page
        		IWorkbenchPage page = window.getActivePage();
        		if (page == null) return;
        		
        		URI configUri = AucMinerActivator.getDefault().getConfigFileUri();
        		try {
					IDE.openEditor(page, configUri, ConfigFormEditor.CONFIG_EDITOR_ID, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
            }  
            
        });
		return null;
	}

}
