package cn.edu.nju.aucminer.client.handlers;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import cn.edu.nju.aucminer.client.views.ReplacementView;
import cn.edu.nju.aucminer.recommender.*;

public class RecommendThread extends Job {

	public RecommendThread(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IAPIUsageFilter apiUsageFilter = new DefaultAPIUsageFilter();
		IAPIReplacementSearcher apiReplacementSearcher = new DefaultAPIReplacementSearch();
		APIReplacementFinder finder = new APIReplacementFinder("eoecn", apiUsageFilter, apiReplacementSearcher);
		List<InvocationsReplacement> replacementList = finder.findAPIReplacement();
		
		// 必须在 UI 线程中调用
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {  
            public void run() {
            	// Get the active window
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();  
        		if (window == null) return;
        		
        		// Get the active page
        		IWorkbenchPage page = window.getActivePage();
        		if (page == null) return;
        		
        		// Open and activate the Favorites view
        		try {
        			page.showView(ReplacementView.VIEWID);
        			ReplacementView view = (ReplacementView)page.findView(ReplacementView.VIEWID);
        			view.setReplacements(replacementList);
        		} catch (PartInitException e) {
        			e.printStackTrace();
        		}
            }  
        });
		
		return Status.OK_STATUS;
	}

}
