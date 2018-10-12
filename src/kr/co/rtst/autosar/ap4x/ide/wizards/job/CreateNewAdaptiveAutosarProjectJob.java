package kr.co.rtst.autosar.ap4x.ide.wizards.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.gautosar.services.DefaultMetaModelServiceProvider;
import org.artop.aal.gautosar.services.factories.IGAutosarFactoryService;
import org.artop.aal.workspace.jobs.CreateNewAutosarProjectJob;
import org.artop.aal.workspace.preferences.IAutosarWorkspacePreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.util.StatusUtil;

import autosar40.autosartoplevelstructure.impl.AUTOSARImpl;
import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import gautosar.ggenericstructure.ginfrastructure.GAUTOSAR;
import gautosar.ggenericstructure.ginfrastructure.GReferrable;
import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.core.model.ProjectManager;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;
import kr.co.rtst.autosar.ap4x.ide.wizards.AdaptiveAutosarProjectCreationInfo;

public class CreateNewAdaptiveAutosarProjectJob extends CreateNewAutosarProjectJob {
	
	public static final String TEMP_FOLDER = "__ap_temp__";
	
	private final AdaptiveAutosarProjectCreationInfo projectInfo;
	
//    protected IMetaModelDescriptor metaModelDescriptor;
    protected EPackage rootObjectEPackage;
    protected EClassifier rootObjectEClassifier;

	public CreateNewAdaptiveAutosarProjectJob(String name, IProject project, URI location,
			AutosarReleaseDescriptor autosarRelease, AdaptiveAutosarProjectCreationInfo projectInfo) {
		super(name, project, location, autosarRelease);
		this.rootObjectEPackage = autosarRelease.getRootEPackage();
		this.rootObjectEClassifier = autosarRelease.getRootEPackage().getEClassifier("AUTOSAR");
		this.projectInfo = projectInfo;
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		IAdaptiveAutosarProject apProject = ProjectManager.getInstance().getAdaptiveAutosarProject(newProject);
		if(apProject != null) {
			try
	        {
	            SubMonitor progress = SubMonitor.convert(monitor, getName(), 100);
	            if(progress.isCanceled())
	                throw new OperationCanceledException();
	            createNewProject(progress.newChild(70));
	            addNatures(progress.newChild(15));
	            
	            if(projectInfo.isPredefinedArxml()) {
	            	createPredefinedArxml(apProject, monitor);
	            }
	            
	            IFile[] importFiles = makeFileResource(apProject, monitor);
	            if(importFiles == null || importFiles.length == 0) {
	        		createEmptyProjectArxml(apProject, monitor);
	        	} else {
	        		createImportedProjectArxml(apProject, importFiles, monitor);
	        	}
	            
	            if(metaModelVersionDescriptor != null && metaModelVersionPreference != null)
	                metaModelVersionPreference.setInProject(newProject, metaModelVersionDescriptor);
	            progress.worked(15);
	            return Status.OK_STATUS;
	        } catch(OperationCanceledException exception) {
	            return Status.CANCEL_STATUS;
	        } catch(Exception ex) {
	            return StatusUtil.createErrorStatus(IDEActivator.getDefault(), ex);
	        }finally {
	        	if(apProject.getProject().getFolder(TEMP_FOLDER).exists()) {
//	    			apProject.getProject().getFolder(TEMP_FOLDER).delete(true, monitor);
	        	}
	        }
		}
		return Status.CANCEL_STATUS;
	}
	
	private List<GReferrable> collectSelection(GAUTOSAR root/*IStructuredSelection selections*/)
    {
		List<GReferrable> elements = new ArrayList(root.gGetArPackages().size());
        for(Iterator it = root.gGetArPackages().iterator(); it.hasNext();)
        {
            Object object = it.next();
            if(object instanceof GReferrable)
                elements.add((GReferrable)object);
        }

        return elements;
    }
	
	/**
	 * 임포트하기 위해 선택된 파일들을 이클립스 IFile 형태로 만들어 반환한다.
	 * @param apProject
	 * @param monitor
	 * @return
	 */
	private IFile[] makeFileResource(IAdaptiveAutosarProject apProject, IProgressMonitor monitor) {
		if(projectInfo.getImportArxmlList() != null && !projectInfo.getImportArxmlList().isEmpty()) {
			File[] files = projectInfo.getImportArxmlList().toArray(new File[0]);
			IFile[] result = new IFile[files.length];
			IFolder tempFolder = apProject.getProject().getFolder(TEMP_FOLDER);
			try {
		    	if(!tempFolder.exists()) {
		    		tempFolder.create(true, true, monitor);
		    	}
	    		for (int i = 0; i < files.length; i++) {
	    			result[i] = tempFolder.getFile(files[i].getName());
	    			result[i].create(Files.newInputStream(files[i].toPath()), true, monitor);
				}
	    		return result;
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
			}
		}
		return null;
	}
	
	private void createImportedProjectArxml(IAdaptiveAutosarProject apProject, IFile[] file, IProgressMonitor monitor) {
		
		final IFile projectArxmlFile = apProject.getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME);
		AutosarReleaseDescriptor releaseDescription = (AutosarReleaseDescriptor)IAutosarWorkspacePreferences.AUTOSAR_RELEASE.get(apProject.getProject());
		org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(apProject.getProject(), releaseDescription);
		
		List<GReferrable> autosarModels = new ArrayList<>();
		
		GARPackage[] packages = getInitialPackages(apProject, monitor);
		if(packages != null) {
			for (int i = 0; i < packages.length; i++) {
				autosarModels.add(packages[i]);
			}
		}
		
		EObject margeEObject = null;
		System.out.println("병합될 파일의 개수:"+file.length);
		for (int i = 0; i < file.length; i++) {
			ModelLoadManager.INSTANCE.loadFile(file[i], releaseDescription, true, monitor);
			margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file[i]);
			System.out.println("처리파일["+i+"]:"+file[i].getFullPath());
			if(margeEObject instanceof AUTOSARImpl) {
				System.out.println("EOBJECT["+i+"]:"+margeEObject);
				System.out.println("PACKAGE SIZE:"+((AUTOSARImpl)margeEObject).getArPackages().size()+", ARPACKAGES:"+((AUTOSARImpl)margeEObject).getArPackages());
				autosarModels.addAll(collectSelection((AUTOSARImpl)margeEObject));
			}
		}
		
		if(!autosarModels.isEmpty()) {
			APMergeAndSaveJob mergeAndSaveJob = new APMergeAndSaveJob(autosarModels, EcorePlatformUtil.createURI(projectArxmlFile.getFullPath()), editingDomain, releaseDescription);
	        mergeAndSaveJob.setPriority(40);
	        mergeAndSaveJob.schedule();
		}
	}
	
	private void createPredefinedArxml(IAdaptiveAutosarProject apProject, IProgressMonitor monitor) {
		IFile file = apProject.getProject().getFile(IAdaptiveAutosarProject.PREDEFINED_ARXML_NAME);
		GAUTOSAR autosar = (GAUTOSAR)createInitialModel();
		
        IGAutosarFactoryService autosarFactory = getAutosarFactoryService();
        if(autosarFactory != null)
        {
            GARPackage arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + apProject.getTypes().getPackageName());
            autosar.gGetArPackages().add(arPackage);
        }
		
    	saveInitialModel(autosar, file, monitor);
	}
	
	private GAUTOSAR createEmptyProjectArxml(IAdaptiveAutosarProject apProject, IProgressMonitor monitor) {
		IFile file = apProject.getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME);
		GAUTOSAR autosar = (GAUTOSAR)createInitialModel();
		
		GARPackage[] packages = getInitialPackages(apProject, monitor);
		if(packages != null) {
			for (int i = 0; i < packages.length; i++) {
				autosar.gGetArPackages().add(packages[i]);
			}
		}
		
    	saveInitialModel(autosar, file, monitor);
    	return autosar;
	}
	
	private GARPackage[] getInitialPackages(IAdaptiveAutosarProject apProject, IProgressMonitor monitor) {
		IGAutosarFactoryService autosarFactory = getAutosarFactoryService();
		GARPackage[] packages = null;
    	int index = 0;
    	
        if(autosarFactory != null)
        {
        	packages = new GARPackage[3];
        	
            GARPackage arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + apProject.getApplications().getPackageName());
            packages[index++] = arPackage;
            
            arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + apProject.getServices().getPackageName());
            packages[index++] = arPackage;
            
            arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + apProject.getMachines().getPackageName());
            packages[index++] = arPackage;
        }
        return packages;
	}
	
	private IGAutosarFactoryService getAutosarFactoryService()
    {
        return (IGAutosarFactoryService)(new DefaultMetaModelServiceProvider()).getService(metaModelVersionDescriptor, org.artop.aal.gautosar.services.factories.IGAutosarFactoryService.class);
    }
	
	private EObject createInitialModel()
    {
        return rootObjectEPackage.getEFactoryInstance().create((EClass)rootObjectEClassifier);
    }

	private void saveInitialModel(EObject rootObject, IFile newFile, IProgressMonitor monitor)
    {
        org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(newFile.getProject(), metaModelVersionDescriptor);
        EcorePlatformUtil.saveNewModelResource(editingDomain, newFile.getFullPath(), metaModelVersionDescriptor.getDefaultContentTypeId(), rootObject, false, monitor);
    }
	
	@Override
	protected void addNatures(IProgressMonitor monitor) throws CoreException {
		super.addNatures(monitor);
		IAdaptiveAutosarProject.addRTSTAdaptiveAutosarNature(newProject, monitor);
	}
	
}
