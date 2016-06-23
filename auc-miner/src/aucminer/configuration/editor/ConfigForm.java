package aucminer.configuration.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class ConfigForm extends FormPage {
	
	private boolean isDirty = false;
	
	private ConfigFormEditor configFormEditor;
	
	private Text oldVersionText;
	private Text newVersionText;
	
	private Button fileStorageButton;
	private Button mysqlStorageButton;
	private Text storageDirectoryText;
	private Button selectDirectoryButton;
	private Text driverText;
	private Text hostText;
	private Text portText;
	private Text userText;
	private Text passwdText;
	
	private Text includeFieldText;
	private Text splitMethodText;
	private Text splitThresholdText;
	private Text invocationChainLenText;
	private Text minSupportText;
	private Text minConfidenceText;
	
	public ConfigForm(final String id, final String title) {
		super(id, title);
		
	    configFormEditor = (ConfigFormEditor)getEditor();
	}

	public ConfigForm(final FormEditor editor, final String id, final String title) {
		super(editor, id, title);
		
		configFormEditor = (ConfigFormEditor)getEditor();
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		final FormToolkit formToolkit = managedForm.getToolkit();
		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setText("Configuration");
		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		scrolledForm.getBody().setLayout(layout);

		createTwoReleaseSection(formToolkit, scrolledForm);
		createStorageSection(formToolkit, scrolledForm);
		createParameterSection(formToolkit, scrolledForm);
		
		final Composite body = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		formToolkit.paintBordersFor(body);
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public void doSave(org.eclipse.core.runtime.IProgressMonitor monitor) {
		isDirty = false;
	}
	
	@SuppressWarnings("unused")
	private void createTwoReleaseSection(final FormToolkit formToolkit, final ScrolledForm scrolledForm) {
		Section twoReleaseSection = formToolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR);
		twoReleaseSection.setText("Two Releases");
		TableWrapData twoReleaseSectionTabelWrapData = new TableWrapData(TableWrapData.FILL);
		twoReleaseSectionTabelWrapData.grabHorizontal = true;
		twoReleaseSection.setLayoutData(twoReleaseSectionTabelWrapData);
		
		Composite sectionClient = formToolkit.createComposite(twoReleaseSection);
		TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 2;
		sectionClient.setLayout(sectionLayout);
		// Old Version
		Label oldVersionLabel = formToolkit.createLabel(sectionClient, "Old Version:");
		oldVersionText = formToolkit.createText(sectionClient, configFormEditor.configModel.getOldVersion(), SWT.BORDER);
		TableWrapData oldVersionTextTwd = new TableWrapData(TableWrapData.FILL);
		oldVersionTextTwd.grabHorizontal = true;
		oldVersionText.setLayoutData(oldVersionTextTwd);
		oldVersionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.setOldVersion(oldVersionText.getText());
			}
		});
		// New Version
		Label newVersionLabel = formToolkit.createLabel(sectionClient, "New Version:");
		newVersionText = formToolkit.createText(sectionClient, configFormEditor.configModel.getNewVersion(), SWT.BORDER);
		TableWrapData newVersionTextTwd = new TableWrapData(TableWrapData.FILL);
		newVersionTextTwd.grabHorizontal = true;
		newVersionText.setLayoutData(newVersionTextTwd);
		newVersionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.setNewVersion(newVersionText.getText());
			}
		});
		
		twoReleaseSection.setClient(sectionClient);
	}
	
	@SuppressWarnings("unused")
	private void createStorageSection(final FormToolkit formToolkit, final ScrolledForm scrolledForm) {
		Section storageSection = formToolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR);
		storageSection.setText("Result Storage");
		TableWrapData storageSectionTabelWrapData = new TableWrapData(TableWrapData.FILL);
		storageSectionTabelWrapData.grabHorizontal = true;
		storageSection.setLayoutData(storageSectionTabelWrapData);
		
		Composite sectionClient = formToolkit.createComposite(storageSection);
		TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 3;
		sectionClient.setLayout(sectionLayout);
		// Storage Type
		Label storageTypeLabel = formToolkit.createLabel(sectionClient, "Type:");
		fileStorageButton = formToolkit.createButton(sectionClient, "File", SWT.RADIO);
		mysqlStorageButton = formToolkit.createButton(sectionClient, "MySQL", SWT.RADIO);
		// Storage Directory
		Label storageDirectoryLabel = formToolkit.createLabel(sectionClient, "Directory:");
		storageDirectoryText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getFileStoragePath(), SWT.BORDER);
		selectDirectoryButton = formToolkit.createButton(sectionClient, "Select...", SWT.BUTTON1);
		selectDirectoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(selectDirectoryButton.getShell(), SWT.OPEN);
					        
				directoryDialog.setFilterPath(null);
				directoryDialog.setMessage("Please select a directory and click OK");
					        
				String dir = directoryDialog.open();
				if(dir != null) {
					storageDirectoryText.setText(dir);
					setDirty();
					
					configFormEditor.configModelDirty = true;
					configFormEditor.configModel.getResultStorage().setFileStoragePath(dir);
				}
			}
		});
		//MySQL Configruation
		TableWrapData driverTwd = new TableWrapData(TableWrapData.FILL);
		driverTwd.grabHorizontal = true;
		driverTwd.colspan = 2;
		Label driverLabel = formToolkit.createLabel(sectionClient, "Driver:");
		driverText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getDriver(), SWT.BORDER);
		driverText.setLayoutData(driverTwd);
		driverText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getResultStorage().setDriver(driverText.getText());;
			}
		});
		TableWrapData hostTwd = new TableWrapData(TableWrapData.FILL);
		hostTwd.grabHorizontal = true;
		hostTwd.colspan = 2;
		Label hostLabel = formToolkit.createLabel(sectionClient, "Host:");
		hostText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getHost(), SWT.BORDER);
		hostText.setLayoutData(hostTwd);
		hostText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getResultStorage().setHost(hostText.getText());;
			}
		});
		TableWrapData portTwd = new TableWrapData(TableWrapData.FILL);
		portTwd.grabHorizontal = true;
		portTwd.colspan = 2;
		Label portLabel = formToolkit.createLabel(sectionClient, "Port:");
		portText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getPort(), SWT.BORDER);
		portText.setLayoutData(portTwd);
		portText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getResultStorage().setPort(portText.getText());;
			}
		});
		TableWrapData userTwd = new TableWrapData(TableWrapData.FILL);
		userTwd.grabHorizontal = true;
		userTwd.colspan = 2;
		Label userLabel = formToolkit.createLabel(sectionClient, "User:");
		userText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getUser(), SWT.BORDER);
		userText.setLayoutData(userTwd);
		userText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getResultStorage().setUser(userText.getText());;
			}
		});
		TableWrapData passwdTwd = new TableWrapData(TableWrapData.FILL);
		passwdTwd.grabHorizontal = true;
		passwdTwd.colspan = 2;
		Label passwdLabel = formToolkit.createLabel(sectionClient, "Password:");
		passwdText = formToolkit.createText(sectionClient, configFormEditor.configModel.getResultStorage().getPassword(), SWT.BORDER);
		passwdText.setLayoutData(passwdTwd);
		passwdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getResultStorage().setPassword(passwdText.getText());;
			}
		});
		// Initial State
		if (configFormEditor.configModel.getResultStorage().isStoreToFileOrDB()) {
			fileStorageButton.setSelection(true);
			storageDirectoryText.setEnabled(true);
			selectDirectoryButton.setEnabled(true);
			driverText.setEnabled(false);
			hostText.setEnabled(false);
			portText.setEnabled(false);
			userText.setEnabled(false);
			passwdText.setEnabled(false);
		} 
		else {
			mysqlStorageButton.setSelection(true);
			storageDirectoryText.setEnabled(false);
			selectDirectoryButton.setEnabled(false);
			driverText.setEnabled(true);
			hostText.setEnabled(true);
			portText.setEnabled(true);
			userText.setEnabled(true);
			passwdText.setEnabled(true);
		}
		fileStorageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button)e.widget;
				if (button.getSelection() && !configFormEditor.configModel.getResultStorage().isStoreToFileOrDB()) {
					setDirty();
					storageDirectoryText.setEnabled(true);
					selectDirectoryButton.setEnabled(true);
					driverText.setEnabled(false);
					hostText.setEnabled(false);
					portText.setEnabled(false);
					userText.setEnabled(false);
					passwdText.setEnabled(false);
					
					configFormEditor.configModelDirty = true;
					configFormEditor.configModel.getResultStorage().setStoreToFileOrDB(true);
				}
			}
		});
		mysqlStorageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button)e.widget;
				if (button.getSelection() && configFormEditor.configModel.getResultStorage().isStoreToFileOrDB()) {
					setDirty();
					storageDirectoryText.setEnabled(false);
					selectDirectoryButton.setEnabled(false);
					driverText.setEnabled(true);
					hostText.setEnabled(true);
					portText.setEnabled(true);
					userText.setEnabled(true);
					passwdText.setEnabled(true);
					
					configFormEditor.configModelDirty = true;
					configFormEditor.configModel.getResultStorage().setStoreToFileOrDB(false);
				}
			}
		});
		
		storageSection.setClient(sectionClient);
	}
	
	@SuppressWarnings("unused")
	private void createParameterSection(final FormToolkit formToolkit, final ScrolledForm scrolledForm) {
		Section parameterSection = formToolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR);
		parameterSection.setText("Two Releases");
		TableWrapData pamameterSectionTabelWrapData = new TableWrapData(TableWrapData.FILL);
		pamameterSectionTabelWrapData.grabHorizontal = true;
		parameterSection.setLayoutData(pamameterSectionTabelWrapData);
		
		Composite sectionClient = formToolkit.createComposite(parameterSection);
		TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 2;
		sectionClient.setLayout(sectionLayout);
		// includeField parameter
		Label includeFieldLabel = formToolkit.createLabel(sectionClient, "includeField:");
		includeFieldText = formToolkit.createText(sectionClient, 
				Boolean.toString(configFormEditor.configModel.getParameters().isIncludeField()), 
				SWT.BORDER);
		TableWrapData includeFieldTextTwd = new TableWrapData(TableWrapData.FILL);
		includeFieldTextTwd.grabHorizontal = true;
		includeFieldText.setLayoutData(includeFieldTextTwd);
		includeFieldText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setIncludeField(Boolean.parseBoolean(includeFieldText.getText()));;
			}
		});
		// splitMethod parameter
		Label splitMethodLabel = formToolkit.createLabel(sectionClient, "splitMethod:");
		splitMethodText = formToolkit.createText(sectionClient, 
				Boolean.toString(configFormEditor.configModel.getParameters().isSplitMethod()), 
				SWT.BORDER);
		TableWrapData splitMethodTextTwd = new TableWrapData(TableWrapData.FILL);
		splitMethodTextTwd.grabHorizontal = true;
		splitMethodText.setLayoutData(splitMethodTextTwd);
		splitMethodText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setSplitMethod(Boolean.parseBoolean(splitMethodText.getText()));;
			}
		});
		// splitThreshod parameter
		Label splitThresholdLabel = formToolkit.createLabel(sectionClient, "splitThreshold:");
		splitThresholdText = formToolkit.createText(sectionClient, 
				Integer.toString(configFormEditor.configModel.getParameters().getSplitThreshold()), 
				SWT.BORDER);
		TableWrapData splitThresholdTextTwd = new TableWrapData(TableWrapData.FILL);
		splitThresholdTextTwd.grabHorizontal = true;
		splitThresholdText.setLayoutData(splitThresholdTextTwd);
		splitThresholdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setSplitThreshold(Integer.parseInt(splitThresholdText.getText()));;
			}
		});
		// invocationChainLen parameter
		Label invocationChainLenLabel = formToolkit.createLabel(sectionClient, "invocationChainLen:");
		invocationChainLenText = formToolkit.createText(sectionClient, 
				Integer.toString(configFormEditor.configModel.getParameters().getInvocationChainLen()), 
				SWT.BORDER);
		TableWrapData invocationChainLenTextTwd = new TableWrapData(TableWrapData.FILL);
		invocationChainLenTextTwd.grabHorizontal = true;
		invocationChainLenText.setLayoutData(invocationChainLenTextTwd);
		invocationChainLenText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setInvocationChainLen(Integer.parseInt(invocationChainLenText.getText()));;
			}
		});
		// minSupport parameter
		Label minSupportLabel = formToolkit.createLabel(sectionClient, "minSupport:");
		minSupportText = formToolkit.createText(sectionClient, 
				Integer.toString(configFormEditor.configModel.getParameters().getMinSupport()), 
				SWT.BORDER);
		TableWrapData minSupportTextTwd = new TableWrapData(TableWrapData.FILL);
		minSupportTextTwd.grabHorizontal = true;
		minSupportText.setLayoutData(minSupportTextTwd);
		minSupportText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setMinSupport(Integer.parseInt(minSupportText.getText()));;
			}
		});
		// minConfidence parameter
		Label minConfidenceLabel = formToolkit.createLabel(sectionClient, "minConfidence:");
		minConfidenceText = formToolkit.createText(sectionClient, 
				Float.toString(configFormEditor.configModel.getParameters().getMinConfidence()), 
				SWT.BORDER);
		TableWrapData minConfidenceTextTwd = new TableWrapData(TableWrapData.FILL);
		minConfidenceTextTwd.grabHorizontal = true;
		minConfidenceText.setLayoutData(minConfidenceTextTwd);
		minConfidenceText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty();
				
				configFormEditor.configModelDirty = true;
				configFormEditor.configModel.getParameters().setMinConfidence(Float.parseFloat(minConfidenceText.getText()));;
			}
		});
		
		parameterSection.setClient(sectionClient);
	}
	
	private void setDirty() {
		isDirty = true;
		getEditor().editorDirtyStateChanged();
	}
}
