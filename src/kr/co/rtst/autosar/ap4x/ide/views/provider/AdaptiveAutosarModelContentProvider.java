package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import gautosar.ggenericstructure.ginfrastructure.GPackageableElement;
import kr.co.rtst.autosar.ap4x.core.model.IAPTopElement;
import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.core.model.ProjectManager;
import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;

public class AdaptiveAutosarModelContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IProject && AdaptiveAutosarProjectUtil.isAdaptiveAutosarProject(((IProject) parentElement))) {
			IAdaptiveAutosarProject apProject = ProjectManager.getInstance().getAdaptiveAutosarProject(((IProject) parentElement));
			if(apProject != null) {
				return apProject.getTopElements();
			}
		} else if(parentElement instanceof IAPTopElement) {
			return new String[] {"aaa", "bbb"};
		} 
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IAPTopElement) {
			return ((IAPTopElement) element).getApProject().getProject();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IProject && AdaptiveAutosarProjectUtil.isAdaptiveAutosarProject(((IProject) element))) {
			IAdaptiveAutosarProject apProject = ProjectManager.getInstance().getAdaptiveAutosarProject(((IProject) element));
			if(apProject != null) {
				return true;
			}
		} else if(element instanceof IAPTopElement) {
			return true;
		}
		return false;
	}

}
