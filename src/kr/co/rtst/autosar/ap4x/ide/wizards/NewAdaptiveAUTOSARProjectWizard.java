package kr.co.rtst.autosar.ap4x.ide.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;

public class NewAdaptiveAUTOSARProjectWizard extends BasicNewProjectResourceWizard implements INewWizard {
	
	private AdaptiveAutosarNewProjectWizardSecondPage page2;
	
	@Override
	public void addPages() {
		super.addPages();
		page2 = new AdaptiveAutosarNewProjectWizardSecondPage();
		addPage(page2);
	}
	
	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			
			// 최상위 폴더 생성
			IProject newProject = getNewProject();
			if(newProject != null) {
				for (int i = 0; i < AdaptiveAutosarProjectUtil.DEFAULT_TOP_DIR.length; i++) {
					if(page2.getProjectTypeSelection(i)) {
						newProject.getFolder(AdaptiveAutosarProjectUtil.DEFAULT_TOP_DIR[i]).create(true, true, monitor);
					}
				}
			}
			
			AdaptiveAutosarProjectUtil.addRTSTAdaptiveAutosarNature(getNewProject(), monitor);
			
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return result;
	}
	

}
