package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;

public class AdaptiveAutosarResourceLebelProvider extends LabelProvider implements ILabelProvider {
	
	@Override
	public String getText(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof IFolder && ((IFolder) element).getParent() instanceof IProject) {
			if(AdaptiveAutosarProjectUtil.isSoftwareTopFolder(((IFolder)element).getName())) {
				return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/nav_software.png");
			} else if(AdaptiveAutosarProjectUtil.isServiceTopFolder(((IFolder)element).getName())) {
				return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/nav_service.png");
			} else if(AdaptiveAutosarProjectUtil.isMachineTopFolder(((IFolder)element).getName())) {
				return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/nav_machine.png");
			}
		}
		return null;
	}
	
}
