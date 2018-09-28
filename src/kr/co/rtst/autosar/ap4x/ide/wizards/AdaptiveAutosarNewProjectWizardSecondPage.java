package kr.co.rtst.autosar.ap4x.ide.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;
import kr.co.rtst.autosar.common.ui.util.UiUtil;

public class AdaptiveAutosarNewProjectWizardSecondPage extends WizardPage {

	public static final String PAGE_NAME = "kr.co.rtst.autosar.ap4x.ide.wizards.secondPage";
	
	private boolean[] projectTypeState;
	private Button[] btnProjectType;
	
	protected AdaptiveAutosarNewProjectWizardSecondPage() {
		super(PAGE_NAME, "Project Configuration", null);
		projectTypeState = new boolean[AdaptiveAutosarProjectUtil.DEFAULT_TOP_DIR.length];
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComp.setLayout(UiUtil.getGridLayoutWithMargin(1, true, 0));
		
		Group grpProjectType = new Group(mainComp, SWT.NONE);
		grpProjectType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grpProjectType.setText("Project Type Selection");
		grpProjectType.setLayout(UiUtil.getGridLayoutDefault(1, false));
		
		btnProjectType = new Button[AdaptiveAutosarProjectUtil.DEFAULT_TOP_DIR.length];
		for (int i = 0; i < btnProjectType.length; i++) {
			btnProjectType[i] = new Button(grpProjectType, SWT.CHECK);
			btnProjectType[i].setText(AdaptiveAutosarProjectUtil.DEFAULT_TOP_DIR[i]);
			btnProjectType[i].setData(i);
			btnProjectType[i].addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Integer index = (Integer)((Button)e.getSource()).getData();
					projectTypeState[index] = ((Button)e.getSource()).getSelection();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		
		init();
		
		setControl(mainComp);
	}
	
	private void init() {
		for (int i = 0; i < btnProjectType.length; i++) {
			btnProjectType[i].setSelection(false);
		}
		for (int i = 0; i < projectTypeState.length; i++) {
			projectTypeState[0] = false;
		}
	}
	
	public boolean getProjectTypeSelection(int index) {
		return projectTypeState[index];
	}

}
