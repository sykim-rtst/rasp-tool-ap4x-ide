package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import autosar40.adaptiveplatform.applicationdesign.applicationstructure.AdaptiveApplicationSwComponentType;
import autosar40.adaptiveplatform.applicationdesign.portinterface.ServiceInterface;
import autosar40.adaptiveplatform.deployment.machine.Machine;
import autosar40.commonstructure.basetypes.BaseType;
import kr.co.rtst.autosar.ap4x.core.model.IAPTopElement;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;

public class AdaptiveAutosarModelLabelProvider extends LabelProvider implements ILabelProvider {
	
	@Override
	public String getText(Object element) {
		if(element instanceof IAPTopElement) {
			return ((IAPTopElement) element).getName();
		}else if(element instanceof BaseType) {
			return ((BaseType)element).getShortName();
		}else if(element instanceof AdaptiveApplicationSwComponentType) {
			return ((AdaptiveApplicationSwComponentType)element).getShortName();
		}else if(element instanceof ServiceInterface) {
			return ((ServiceInterface)element).getShortName();
		}else if(element instanceof Machine) {
			return ((Machine)element).getShortName();
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
		if(element instanceof IAPTopElement) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/"+((IAPTopElement) element).getPackageName()+".png");
		}else if(element instanceof BaseType) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/type/type.png");
		}else if(element instanceof AdaptiveApplicationSwComponentType) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/swc/swc.png");
		}else if(element instanceof ServiceInterface) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/service/service.png");
		}else if(element instanceof Machine) {
			return IDEActivator.getDefault().getIdeImageRegistry().getImage("icons/machine/machine.png");
		}
		return null;
	}

}
