package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;

public class AdaptiveAutosarProjectFilter extends ViewerFilter {

	public AdaptiveAutosarProjectFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IProject) {
			return AdaptiveAutosarProjectUtil.isAdaptiveAutosarProject((IProject)element);
		}else if(element instanceof IFile) {
			return !(IAdaptiveAutosarProject.PREDEFINED_ARXML_NAME.equals(((IFile) element).getName()) ||
					IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME.equals(((IFile) element).getName()));
		}
		return true;
	}

}
