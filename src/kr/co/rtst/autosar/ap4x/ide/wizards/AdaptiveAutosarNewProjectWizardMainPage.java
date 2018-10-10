package kr.co.rtst.autosar.ap4x.ide.wizards;

import org.artop.aal.workspace.ui.wizards.pages.NewAutosarProjectCreationPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
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
	
	public AdaptiveAutosarNewProjectWizardMainPage(AdaptiveAutosarProjectCreationInfo projectInfo) {
		super(PAGE_NAME);
		this.projectInfo = projectInfo;
	}

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
		group.setText(IDEText.WIZARD_PACKAGE_GROUP_NAME);
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
	}
	
	@Override
	protected boolean validatePage() {
		boolean result = super.validatePage();
		if(result) {
			if(txtPackageName == null || txtPackageName.getText().trim().length()==0) {
				result = false;
				setErrorMessage(IDEText.WIZARD_PACKAGE_ERROR_MSG);
			} 
		}
		return result;
	}
	
	private void init() {

	}
	
}
