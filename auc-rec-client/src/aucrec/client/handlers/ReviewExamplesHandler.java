package aucrec.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import aucrec.client.rec.Replacement;
import aucrec.client.views.ApiUsageChangeRecView;
import aucrec.client.views.AucExamplesView;

public class ReviewExamplesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ApiUsageChangeRecView) {
			ApiUsageChangeRecView replacementView = ((ApiUsageChangeRecView) part);
			IStructuredSelection selection = (IStructuredSelection) replacementView.getReplacementViewer().getSelection();
			if (selection != null) {
				Object data = selection.getFirstElement();
				if (data instanceof Replacement) {
					Replacement replacement = (Replacement)data;
					
					AucExamplesView aucExamplesView = new AucExamplesView(replacement.getExamplesList());
					aucExamplesView.openAucExamplesView();
				}
			}
		}
		return null;
	}
}
