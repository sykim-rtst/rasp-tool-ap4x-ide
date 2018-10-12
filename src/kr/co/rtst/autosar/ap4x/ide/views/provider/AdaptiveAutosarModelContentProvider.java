package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.workspace.preferences.IAutosarWorkspacePreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;

import autosar40.autosartoplevelstructure.impl.AUTOSARImpl;
import gautosar.ggenericstructure.ginfrastructure.GARPackage;
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
			IFile file = ((IAPTopElement) parentElement).getApProject().getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME);
			AutosarReleaseDescriptor releaseDescription = (AutosarReleaseDescriptor)IAutosarWorkspacePreferences.AUTOSAR_RELEASE.get(file.getProject());
			org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);
			EObject object = EcorePlatformUtil.loadModelRoot(editingDomain, file);
			System.out.println("TEST::"+object);
			
			return ((AUTOSARImpl)object).getArPackages().toArray();
			
		} else if(parentElement instanceof GARPackage) {
			System.out.println("TESTSETEST-------------------");
			return ((GARPackage)parentElement).gGetElements().toArray();
		}  
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IAPTopElement) {
			return ((IAPTopElement) element).getApProject().getProject();
		} else if(element instanceof GARPackage) {
			return null;
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
		} else if(element instanceof GARPackage) {
			return true;
		}
		return false;
	}
	
	
	
	
//	@Override
//	public Object[] getChildren(Object parentElement) {
//		if(parentElement instanceof IFile && ((IFile) parentElement).getFileExtension().equals("aaswc")) {
//			GAUTOSAR autosar = AutosarCoreModelRegistry.getInstance().getCoreModel(((IFile) parentElement));
//			if(autosar != null) {
//				return autosar.gGetArPackages().toArray();
//			}
//		} else if(parentElement instanceof GARPackage) {
//			return ((GARPackage)parentElement).gGetElements().toArray();
//		} 
//		return null;
//	}
//
//	@Override
//	public Object getParent(Object element) {
//		/*if(element instanceof IFile && ((IFile) element).getFileExtension().equals("aaswc")) {
//			
//		} else if(element instanceof GAUTOSAR) {
//			
//		} else*/ if(element instanceof GARPackage) {
////			return ((GARPackage)element).get;
//		} else if(element instanceof GPackageableElement) {
//			return ((GPackageableElement)element).gGetARPackage();
//		}
//		return null;
//	}
//
//	@Override
//	public boolean hasChildren(Object element) {
//		if(element instanceof IFile && ((IFile) element).getFileExtension().equals("aaswc")) {
//			GAUTOSAR autosar = AutosarCoreModelRegistry.getInstance().getCoreModel(((IFile) element));
//			if(autosar != null) {
//				return !autosar.gGetArPackages().isEmpty();
//			}
//		}/* else if(element instanceof GAUTOSAR) {
//			return !((GAUTOSAR)element).gGetArPackages().isEmpty();
//		}*/ else if(element instanceof GARPackage) {
//			return !((GARPackage)element).gGetElements().isEmpty();
//		}
//		return false;
//	}

}
