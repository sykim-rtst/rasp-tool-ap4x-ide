package kr.co.rtst.autosar.ap4x.ide.views.provider;

import java.util.Comparator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;

public class AdaptiveAutosarResourceComparator extends ViewerComparator {

	public AdaptiveAutosarResourceComparator() {
	}

	public AdaptiveAutosarResourceComparator(Comparator<? super String> comparator) {
		super(comparator);
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
//		if(AdaptiveAutosarProjectUtil.isAdaptiveAutosarTopFolder(e1) || AdaptiveAutosarProjectUtil.isAdaptiveAutosarTopFolder(e2)) {
//			if( !(e1 instanceof IFolder) ) {
//				return 1; // 무조건 e2가 우선
//			} else if( !(e2 instanceof IFolder) ) {
//				return -1; // 무조건 e1이 우선
//			} else {
//				return AdaptiveAutosarProjectUtil.compareTopFolder(((IFolder)e1).getName(), ((IFolder)e2).getName());
//			}
//		}
////		if(e1 instanceof IFolder && e2 instanceof IFolder) {
////			if(((IFolder)e1).getParent() instanceof IProject && ((IFolder)e1).getParent() instanceof IProject) {
////				return AdaptiveAutosarProjectUtil.compareTopFolder(((IFolder)e1).getName(), ((IFolder)e2).getName());
////			}
////		}
		if(IAdaptiveAutosarProject.hasAdaptiveAutosarTopDir(e1, e2)) {
			return IAdaptiveAutosarProject.compareTopFolder((IResource)e1, (IResource)e2);
		}
		return super.compare(viewer, e1, e2);
	}

}
