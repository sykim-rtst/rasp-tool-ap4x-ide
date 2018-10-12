package kr.co.rtst.autosar.ap4x.ide.wizards;

import org.artop.aal.workspace.ui.wizards.pages.NewAutosarProjectCreationPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import kr.co.rtst.autosar.ap4x.ide.consts.IDEText;
import kr.co.rtst.autosar.common.ui.util.UiUtil;

public class AdaptiveAutosarNewProjectWizardMainPage extends NewAutosarProjectCreationPage{

	public static final String PAGE_NAME = "kr.co.rtst.autosar.ap4x.ide.wizards.mainPage";
	
	private final AdaptiveAutosarProjectCreationInfo projectInfo;
	private Text txtPackageName;
	private Button btnPredefinedArxml;
	
	public AdaptiveAutosarNewProjectWizardMainPage(AdaptiveAutosarProjectCreationInfo projectInfo) {
		super(PAGE_NAME);
		this.projectInfo = projectInfo;
		setTitle(IDEText.WIZARD_MAIN_PAGE_TITLE);
		setDescription(IDEText.WIZARD_MAIN_PAGE_MESSAGE);
	}
	
//    @Override
//	public void createControl(Composite parent) {
//        Composite composite = new Composite(parent, SWT.NULL);
//
//
//        initializeDialogUnits(parent);
//
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
//                IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);
//
//        composite.setLayout(new GridLayout());
//        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//        createProjectNameGroup(composite);
//        locationArea = new ProjectContentsLocationArea(getErrorReporter(), composite);
//        if(initialProjectFieldValue != null) {
//			locationArea.updateProjectName(initialProjectFieldValue);
//		}
//
//		// Scale the button based on the rest of the dialog
//		setButtonLayoutData(locationArea.getBrowseButton());
//
//        setPageComplete(validatePage());
//        // Show description on opening
//        setErrorMessage(null);
//        setMessage(null);
//        setControl(composite);
//        Dialog.applyDialogFont(composite);
//    }
//	
//    /**
//     * Creates the project name specification controls.
//     *
//     * @param parent the parent composite
//     */
//    private final void createProjectNameGroup(Composite parent) {
//        // project specification group
//        Composite projectGroup = new Composite(parent, SWT.NONE);
//        GridLayout layout = new GridLayout();
//        layout.numColumns = 2;
//        projectGroup.setLayout(layout);
//        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//        // new project label
//        Label projectLabel = new Label(projectGroup, SWT.NONE);
//        projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
//        projectLabel.setFont(parent.getFont());
//
//        // new project name entry field
//        projectNameField = new Text(projectGroup, SWT.BORDER);
//        GridData data = new GridData(GridData.FILL_HORIZONTAL);
//        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
//        projectNameField.setLayoutData(data);
//        projectNameField.setFont(parent.getFont());
//
//        // Set the initial value first before listener
//        // to avoid handling an event during the creation.
//        if (initialProjectFieldValue != null) {
//			projectNameField.setText(initialProjectFieldValue);
//		}
//        projectNameField.addListener(SWT.Modify, nameModifyListener);
//        BidiUtils.applyBidiProcessing(projectNameField, BidiUtils.BTD_DEFAULT);
//    }
	
//	public AdaptiveAutosarNewProjectWizardMainPage(IStructuredSelection selection) {
//		super(PAGE_NAME, selection, false);
//		this.projectInfo = projectInfo;
//	}
	
	@Override
	protected void createAdditionalControls(Composite parent) {
		createMetaModelVersionGroup(parent);
		createDefaultPackageGroup(parent);
		
		init();
	}
	
	protected void createDefaultPackageGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(IDEText.WIZARD_ARXML_GROUP_NAME);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(UiUtil.getGridLayoutDefault(2, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setText(IDEText.WIZARD_PACKAGE_LABEL);
		
		txtPackageName = new Text(group, SWT.BORDER);
		txtPackageName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtPackageName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				projectInfo.setTopPackageName(txtPackageName.getText().trim());
				setPageComplete(validatePage());
			}
		});
		
		btnPredefinedArxml = new Button(group, SWT.CHECK);
		btnPredefinedArxml.setText(IDEText.WIZARD_PREDEFINED_ARXML_BUTTON);
		btnPredefinedArxml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectInfo.setPredefinedArxml(btnPredefinedArxml.getSelection());
			}
		});
	}
	
	@Override
	protected boolean validatePage() {
		boolean result = super.validatePage();
		if(result) {
			if(txtPackageName == null || txtPackageName.getText().trim().length()==0) {
				result = false;
				setMessage(IDEText.WIZARD_PACKAGE_ERROR_MESSAGE);
			} 
		}
		return result;
	}
	
	private void init() {
		btnPredefinedArxml.setSelection(true);
		projectInfo.setPredefinedArxml(btnPredefinedArxml.getSelection());
	}
	
}
