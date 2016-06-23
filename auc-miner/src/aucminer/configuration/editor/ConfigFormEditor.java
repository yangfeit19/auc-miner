package aucminer.configuration.editor;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import aucminer.AucMinerActivator;
import aucminer.configuration.ConfigUtility;
import aucminer.configuration.Configuration;

public class ConfigFormEditor extends FormEditor {
	
	public static String CONFIG_EDITOR_ID = "aucminer.editors.ConfigFormEditor";
	public static String CONFIG_FORM_ID = "aucminer.editors.ConfigForm";
	
	protected ConfigForm configFormEditor;
	protected int configFormEditorIndex;
	protected StructuredTextEditor configTextEditor;
	protected int configTextEditorIndex;

	/** Keeps track of dirty code from source editor. */
	protected boolean configTextDirty = false;
	protected boolean configModelDirty = false;
	protected Configuration configModel = null;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		
		if (input instanceof IURIEditorInput) {
			URI uri = ((IURIEditorInput)input).getURI();
			configModel = ConfigUtility.parseConfigFromXmlFile(new File(uri));
		}
	}

	@Override
	protected void addPages() {
		configFormEditor = new ConfigForm(this, CONFIG_FORM_ID, "Config Form Editor");
		configTextEditor = new StructuredTextEditor();
		configTextEditor.setEditorPart(this);

		try {
			// add form pages
			configFormEditorIndex = addPage(configFormEditor);

			// add source page
			configTextEditorIndex = addPage(configTextEditor, getEditorInput());
			setPageText(configTextEditorIndex, "configuration.xml");
		} catch (final PartInitException e) {
			e.printStackTrace();
		}

		// add listener for changes of the document source
		getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentAboutToBeChanged(final DocumentEvent event) {
				// nothing to do
			}

			@Override
			public void documentChanged(final DocumentEvent event) {
				configTextDirty = true;
			}
		});
	}
	
	@Override
	public boolean isDirty() {
		return configFormEditor.isDirty() || configTextEditor.isDirty();
	}

	@Override
	public void doSaveAs() {
		// not allowed
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		if (getActivePage() != configTextEditorIndex)
			updateSourceFromModel();
		configTextEditor.doSave(monitor);
		configFormEditor.doSave(monitor);
		
		AucMinerActivator.getDefault().setConfiguration(configModel);
	}

	@Override
	protected void pageChange(final int newPageIndex) {
		// check for update from the source code
		if ((newPageIndex == configFormEditorIndex) && configTextDirty)
			updateModelFromSource();

		// check for updates to be propagated to the source code
		if ((newPageIndex == configTextEditorIndex) && configModelDirty)
			updateSourceFromModel();

		// switch page
		super.pageChange(newPageIndex);

		// update page if needed
		final IFormPage page = getActivePageInstance();
		if (page != null) {
			page.setFocus();
		}
	}

	private void updateModelFromSource() {
		configModel = ConfigUtility.parseConfigFromXmlString(getContent());
		configTextDirty = false;
		configModelDirty = false;
	}

	private void updateSourceFromModel() {
		String xmlString = ConfigUtility.configToXmlStirng(configModel);
		setContent(xmlString);
		configTextDirty = false;
		configModelDirty = false;
	}

	private IDocument getDocument() {
		final IDocumentProvider provider = configTextEditor.getDocumentProvider();
		return provider.getDocument(getEditorInput());
	}
	
	private void setContent(String content) {
		getDocument().set(content);
	}

	private String getContent() {
		return getDocument().get();
	}
	
	public Configuration getConfigModelData() {
		return configModel;
	}
}
