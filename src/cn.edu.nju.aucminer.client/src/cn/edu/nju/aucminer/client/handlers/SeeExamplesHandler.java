package cn.edu.nju.aucminer.client.handlers;

import java.io.File;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.edu.nju.aucminer.client.adaptionexamples.ApiAdaptionExample;
import cn.edu.nju.aucminer.client.adaptionexamples.FileCompareEditorInput;
import cn.edu.nju.aucminer.client.adaptionexamples.IAdaptionExampleGetter;
import cn.edu.nju.aucminer.client.adaptionexamples.LocalAdaptionExampleGetter;
import cn.edu.nju.aucminer.client.views.ReplacementView;
import cn.edu.nju.aucminer.recommender.InvocationsReplacement;

public class SeeExamplesHandler extends AbstractHandler {
	
	private IAdaptionExampleGetter exampleGetter = new LocalAdaptionExampleGetter();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ReplacementView) {
			ReplacementView replacementView = ((ReplacementView) part);
			IStructuredSelection selection = (IStructuredSelection) replacementView.getReplacementViewer().getSelection();
			if (selection != null) {
				Object data = selection.getFirstElement();
				if (data instanceof InvocationsReplacement) {
					InvocationsReplacement replacement = (InvocationsReplacement)data;
					ApiAdaptionExample example = replacement.getExamplesList().get(0);
					compare(example.getPathFrom(), example.getPathTo());
				}
			}
		}
		return null;
	}
	
	private void compare(String pathFrom, String pathTo) {
		CompareEditorInput input = null;
		
		File[] filePair = exampleGetter.getAdaptionExampleSourceFiles(pathFrom, pathTo);
		if (filePair[0] != null && filePair[1] != null) {
			CompareConfiguration configuration = new CompareConfiguration();
			input = new FileCompareEditorInput(configuration, filePair[0], filePair[1]);
		}
		
		if (input != null) {
			CompareUI.openCompareEditor(input);
		}
	}
}
