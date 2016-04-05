package cn.edu.nju.aucminer.client.views;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import cn.edu.nju.aucminer.recommender.InvocationsReplacement;

public class ReplacementView extends ViewPart {
	
	public static String VIEWID = "cn.edu.nju.aucminer.client.views.replacementSuggestionsView";
	
	private TableViewer replacementViewer;
	
	public ReplacementView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		replacementViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = replacementViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn orignalColumn = new TableColumn(table, SWT.LEFT);
		orignalColumn.setText("Orignal Invocations");
		orignalColumn.setWidth(400);
		
		TableColumn replacementColumn = new TableColumn(table, SWT.LEFT);
		replacementColumn.setText("Replacement");
		replacementColumn.setWidth(400);
		
		replacementViewer.setLabelProvider(new ReplacementTableLabelProvider());
		replacementViewer.setContentProvider(new ArrayContentProvider()); 
		
		replacementViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
	            InvocationsReplacement replacement = (InvocationsReplacement) selection.getFirstElement();
	            
	            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {  
	                public void run() {
	                	// Get the active window
	                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();  
	            		if (window == null) return;
	            		
	            		// Get the active page
	            		IWorkbenchPage page = window.getActivePage();
	            		if (page == null) return;
	            		
	            		URI fileUri = new File(replacement.getOldInvocations().get(0).getPath()).toURI();
	            		URI rootUri = ResourcesPlugin.getWorkspace().getRoot().getLocationURI(); 
	            		fileUri = rootUri.relativize(fileUri);
	            		IPath path = new Path(fileUri.getPath());
	            		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	            		
	            		HashMap<String, Object> map = new HashMap<String, Object>();
	            	    map.put(IMarker.LINE_NUMBER, replacement.getOldInvocations().get(0).getStartLineNum());
	            	    IMarker marker = null;
	            	    try {
	            	        marker = file.createMarker(IMarker.TEXT);
	            	        marker.setAttributes(map);
	            	        try {
	            	            IDE.openEditor(page, marker);
	            	        } catch ( PartInitException e ) {
	            	            e.printStackTrace();
	            	        }
	            	    } catch (CoreException e1) {
	            	        e1.printStackTrace();
	            	    } finally {
	            	        try {
	            	            if (marker != null)
	            	                marker.delete();
	            	        } catch (CoreException e) {
	            	            e.printStackTrace();
	            	        }
	            	    }
	                }  
	            });
			}
		});
		
		// Creating context menu
		MenuManager menuManager = new MenuManager();  
		Menu contextMenu = menuManager.createContextMenu(table);  
		table.setMenu(contextMenu);  
		getSite().registerContextMenu(menuManager, replacementViewer);
	}
	
	public void setReplacements(List<InvocationsReplacement> replacementList) {
		replacementViewer.setInput(replacementList);
	}

	@Override
	public void setFocus() {
		replacementViewer.getTable().setFocus();
	}
	
	public TableViewer getReplacementViewer() {
		return replacementViewer;
	}
}
