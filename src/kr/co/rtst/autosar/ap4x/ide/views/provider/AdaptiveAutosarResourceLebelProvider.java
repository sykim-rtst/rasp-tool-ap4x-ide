package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;

public class AdaptiveAutosarResourceLebelProvider extends LabelProvider implements ILabelProvider {
	
	@Override
	public String getText(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if(IAdaptiveAutosarProject.isAdaptiveAutosarTopFolder(element)) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/"+IAdaptiveAutosarProject.getTopDirImageName(((IFolder)element).getName())+".png");
		}
		return null;
	}
	
}
