package kr.co.rtst.autosar.ap4x.ide.wizards.job;

import java.net.URI;

import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.gautosar.services.DefaultMetaModelServiceProvider;
import org.artop.aal.gautosar.services.factories.IGAutosarFactoryService;
import org.artop.aal.workspace.jobs.CreateNewAutosarProjectJob;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.sphinx.platform.util.StatusUtil;

import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import gautosar.ggenericstructure.ginfrastructure.GAUTOSAR;
import kr.co.rtst.autosar.ap4x.core.artop.AutosarCoreModelRegistry;
import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.core.model.ProjectManager;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;
import kr.co.rtst.autosar.ap4x.ide.wizards.AdaptiveAutosarProjectCreationInfo;

public class CreateNewAdaptiveAutosarProjectJob extends CreateNewAutosarProjectJob {
	
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
		try
        {
            SubMonitor progress = SubMonitor.convert(monitor, getName(), 100);
            if(progress.isCanceled())
                throw new OperationCanceledException();
            createNewProject(progress.newChild(70));
            addNatures(progress.newChild(15));
            
            IAdaptiveAutosarProject apProject = ProjectManager.getInstance().getAdaptiveAutosarProject(newProject);
            if(projectInfo.isPredefinedArxml()) {
            	createPredefinedArxml(apProject, apProject.getProject().getFile(IAdaptiveAutosarProject.PREDEFINED_ARXML_NAME), monitor);
            }
            if(projectInfo.getImportArxmlList() != null && !projectInfo.getImportArxmlList().isEmpty()) {
            	// TODO 병합
            }
            
//            newProject.getFolder(IAdaptiveAutosarProject.)
            
            if(metaModelVersionDescriptor != null && metaModelVersionPreference != null)
                metaModelVersionPreference.setInProject(newProject, metaModelVersionDescriptor);
            progress.worked(15);
            return Status.OK_STATUS;
        }
        catch(OperationCanceledException exception)
        {
            return Status.CANCEL_STATUS;
        }
        catch(Exception ex)
        {
            return StatusUtil.createErrorStatus(IDEActivator.getDefault(), ex);
        }
	}
	
	protected void createPredefinedArxml(IAdaptiveAutosarProject project, IFile file, IProgressMonitor monitor) {
		
		GAUTOSAR autosar = (GAUTOSAR)createInitialModel();
		
        IGAutosarFactoryService autosarFactory = getAutosarFactoryService();
        if(autosarFactory != null)
        {
            GARPackage arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + project.getTypes().getPackageName());
            autosar.gGetArPackages().add(arPackage);
        }
		
    	AutosarCoreModelRegistry.getInstance().putCoreModel(file, autosar);
    	
    	saveInitialModel(autosar, file, monitor);
	}
	
	protected IGAutosarFactoryService getAutosarFactoryService()
    {
        return (IGAutosarFactoryService)(new DefaultMetaModelServiceProvider()).getService(metaModelVersionDescriptor, org.artop.aal.gautosar.services.factories.IGAutosarFactoryService.class);
    }
	
	protected EObject createInitialModel()
    {
        return rootObjectEPackage.getEFactoryInstance().create((EClass)rootObjectEClassifier);
    }

    protected void saveInitialModel(EObject rootObject, IFile newFile, IProgressMonitor monitor)
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
