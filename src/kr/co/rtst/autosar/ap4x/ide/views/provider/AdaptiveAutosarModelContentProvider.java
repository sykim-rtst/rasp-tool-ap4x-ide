package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;

import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import gautosar.ggenericstructure.ginfrastructure.GAUTOSAR;
import gautosar.ggenericstructure.ginfrastructure.GPackageableElement;
import kr.co.rtst.autosar.ap4x.core.artop.AutosarCoreModelRegistry;
import kr.co.rtst.autosar.ap4x.core.util.AdaptiveAutosarProjectUtil;

public class AdaptiveAutosarModelContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IFile && ((IFile) parentElement).getFileExtension().equals("aaswc")) {
			GAUTOSAR autosar = AutosarCoreModelRegistry.getInstance().getCoreModel(((IFile) parentElement));
			if(autosar != null) {
				return autosar.gGetArPackages().toArray();
			}
		} else if(parentElement instanceof GARPackage) {
			return ((GARPackage)parentElement).gGetElements().toArray();
		} 
		return null;
	}

	@Override
	public Object getParent(Object element) {
		/*if(element instanceof IFile && ((IFile) element).getFileExtension().equals("aaswc")) {
			
		} else if(element instanceof GAUTOSAR) {
			
		} else*/ if(element instanceof GARPackage) {
//			return ((GARPackage)element).get;
		} else if(element instanceof GPackageableElement) {
			return ((GPackageableElement)element).gGetARPackage();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IFile && ((IFile) element).getFileExtension().equals("aaswc")) {
			GAUTOSAR autosar = AutosarCoreModelRegistry.getInstance().getCoreModel(((IFile) element));
			if(autosar != null) {
				return !autosar.gGetArPackages().isEmpty();
			}
		}/* else if(element instanceof GAUTOSAR) {
			return !((GAUTOSAR)element).gGetArPackages().isEmpty();
		}*/ else if(element instanceof GARPackage) {
			return !((GARPackage)element).gGetElements().isEmpty();
		}
		return false;
	}

}
