package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;

public class AdaptiveAutosarProjectFilter extends ViewerFilter {

	public AdaptiveAutosarProjectFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IProject) {
			return AdaptiveAutosarProjectUtil.isAdaptiveAutosarProject((IProject)element);
		}
		return true;
	}

}
