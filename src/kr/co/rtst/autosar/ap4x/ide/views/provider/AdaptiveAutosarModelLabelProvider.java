package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import autosar40.adaptiveplatform.applicationdesign.applicationstructure.AdaptiveApplicationSwComponentType;
import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import kr.co.rtst.autosar.ap4x.core.model.IAPTopElement;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;

public class AdaptiveAutosarModelLabelProvider extends LabelProvider implements ILabelProvider {
	
	@Override
	public String getText(Object element) {
		if(element instanceof IAPTopElement) {
			return ((IAPTopElement) element).getName();
		}
		
//		if(element instanceof GARPackage) {
//			return ((GARPackage)element).gGetShortName();
//		}else if(element instanceof AdaptiveApplicationSwComponentType) {
//			((AdaptiveApplicationSwComponentType)element).gGetShortName();
//		}
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
//		if(element instanceof IFile && ((IFile) element).getFileExtension().equals("aaswc")) {
//			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/swc/swc.png");
//		} else if(element instanceof GARPackage) {
//			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/swc/swc.png");
//		} else if(element instanceof AdaptiveApplicationSwComponentType) {
//			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/swc/swc.png");
//		}
		return null;
	}

}
