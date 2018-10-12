package kr.co.rtst.autosar.ap4x.ide.wizards.job;

import java.io.File;
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.scoping.IResourceScopeProvider;
import org.eclipse.sphinx.emf.scoping.ResourceScopeProviderRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.util.StatusUtil;

import autosar40.autosartoplevelstructure.impl.AUTOSARImpl;
import gautosar.ggenericstructure.ginfrastructure.GARPackage;
import gautosar.ggenericstructure.ginfrastructure.GAUTOSAR;
import gautosar.ggenericstructure.ginfrastructure.GReferrable;
import kr.co.rtst.autosar.ap4x.core.model.IAPTopElement;
import kr.co.rtst.autosar.ap4x.core.model.IAdaptiveAutosarProject;
import kr.co.rtst.autosar.ap4x.core.model.ProjectManager;
import kr.co.rtst.autosar.ap4x.ide.IDEActivator;
import kr.co.rtst.autosar.ap4x.ide.wizards.AdaptiveAutosarProjectCreationInfo;

public class CreateNewAdaptiveAutosarProjectJob2 extends CreateNewAutosarProjectJob {
	
	private final AdaptiveAutosarProjectCreationInfo projectInfo;
	
//    protected IMetaModelDescriptor metaModelDescriptor;
    protected EPackage rootObjectEPackage;
    protected EClassifier rootObjectEClassifier;

	public CreateNewAdaptiveAutosarProjectJob2(String name, IProject project, URI location,
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
            
            GAUTOSAR autosar = createProjectArxml(apProject, apProject.getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME), monitor);
            if(projectInfo.getImportArxmlList() != null && !projectInfo.getImportArxmlList().isEmpty()) {
            	// TODO 병합
//            	margeUserDefinedARXMLtoProjectARXML(apProject.getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME));
            	
            	// 임시 폴더 생성
            	final String TEMP_FOLDER = "__ap_temp__";
            	IFolder tempFolder = apProject.getProject().getFolder(TEMP_FOLDER);
            	if(!tempFolder.exists()) {
            		tempFolder.create(true, true, monitor);
            	}
            	try {
	            	
	            	File[] files = projectInfo.getImportArxmlList().toArray(new File[0]);
            		for (int i = 0; i < files.length; i++) {
//            			System.out.println("EXIST B:"+tempFolder.getFile(files[i].getName()).exists());
            			tempFolder.getFile(files[i].getName()).create(Files.newInputStream(files[i].toPath()), true, monitor);
            			System.out.println("EXIST A:"+tempFolder.getFile(files[i].getName()).exists());
            			if(tempFolder.getFile(files[i].getName()).exists()) {
            				System.out.println(tempFolder.getFile("NAME:"+files[i].getName()).getName()+", SIZE:"+tempFolder.getFile(files[i].getName()).getContents().available());
            			}
            			margeUserDefinedARXMLtoProjectARXML(apProject, autosar, tempFolder.getFile(files[i].getName())/*, files[i]*/, monitor);
            			
//            			tempFolder.getFile(files[i].getName()).delete(true, monitor);
					}
            	} finally {
            		if(tempFolder.exists()) {
//            			tempFolder.delete(true, monitor);
//            			apProject.getProject().getFolder(TEMP_FOLDER).delete(true, monitor);
                	}
            	}
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
	
	private List<GReferrable> collectSelection(GAUTOSAR root/*IStructuredSelection selections*/)
    {
//		AUTOSARImpl
//		GAUTOSAR
//        List<GReferrable> elements = new ArrayList(selections.size());
		List<GReferrable> elements = new ArrayList(root.gGetArPackages().size());
        for(Iterator it = root.gGetArPackages().iterator(); it.hasNext();)
        {
            Object object = it.next();
            if(object instanceof GReferrable)
                elements.add((GReferrable)object);
        }

        return elements;
    }
	
	private IMetaModelDescriptor getAutosarRelease(EObject eObject, TransactionalEditingDomain editingDomain)
    {
        Resource eResource = eObject.eResource();
        if(eResource == null)
            return null;
        org.eclipse.emf.common.util.URI eUri = eResource.getURI();
        if(eUri.isPlatformResource())
        {
            String platformString = eUri.toPlatformString(true);
            IFile file = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
            if(file != null)
            {
//            	IFile file = ((IAPTopElement) parentElement).getApProject().getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME);
//    			AutosarReleaseDescriptor releaseDescription = (AutosarReleaseDescriptor)IAutosarWorkspacePreferences.AUTOSAR_RELEASE.get(file.getProject());
//    			org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);
//    			EObject object = EcorePlatformUtil.loadModelRoot(editingDomain, file);
    			
    			IMetaModelDescriptor descriptor = (AutosarReleaseDescriptor)IAutosarWorkspacePreferences.AUTOSAR_RELEASE.get(file.getProject());
    			
//                IMetaModelDescriptor descriptor = AutosarProjects.getMetaModelDescriptor(file.getProject());
                return descriptor;
            }
        }
        return null;
    }
	
	private static void wait(Object jobFamily)
    {
        boolean wasInterrupted = false;
        do
            try
            {
                Job.getJobManager().join(jobFamily, null);
                wasInterrupted = false;
            }
            catch(OperationCanceledException e)
            {
                e.printStackTrace();
            }
            catch(InterruptedException _ex)
            {
                wasInterrupted = true;
            }
        while(wasInterrupted);
    }
	
	protected void margeUserDefinedARXMLtoProjectARXML(IAdaptiveAutosarProject apProject, GAUTOSAR firstElement, IFile file/*, File cFile*/, IProgressMonitor monitor) {
		
		final IFile projectArxmlFile = apProject.getProject().getFile(IAdaptiveAutosarProject.USER_DEFINED_ARXML_NAME);
		AutosarReleaseDescriptor releaseDescription = (AutosarReleaseDescriptor)IAutosarWorkspacePreferences.AUTOSAR_RELEASE.get(file.getProject());
		
		System.out.println("병합대상:"+file.getFullPath()+", 위치:"+projectArxmlFile.getFullPath());
		
		System.out.println("*************TRUE*1:"+file.isAccessible());
		System.out.println("*************FALSE*2:"+ResourceScopeProviderRegistry.INSTANCE.isNotInAnyScope(file));
		IMetaModelDescriptor effectiveMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getEffectiveDescriptor(file);
		System.out.println("*************TRUE*3:"+releaseDescription.getClass().isInstance(effectiveMMDescriptor));
		System.out.println("*************FALSE*4:"+EcorePlatformUtil.isFileLoaded(file));
		IResourceScopeProvider resourceScopeProvider = ResourceScopeProviderRegistry.INSTANCE.getResourceScopeProvider(effectiveMMDescriptor);
		System.out.println("*************TRUE*5:"+(resourceScopeProvider != null));
		if(resourceScopeProvider != null) {
			System.out.println("*************TRUE*5-1:"+resourceScopeProvider);
			IResourceScope resourceScope = resourceScopeProvider.getScope(file);
			System.out.println("*************TRUE*6:"+(resourceScope != null));
			if(resourceScope != null) {
				System.out.println("*************TRUE*6-1:"+resourceScope);
				TransactionalEditingDomain tEditingDomain = WorkspaceEditingDomainUtil.getMappedEditingDomain(file);
				System.out.println("*************TRUE*7:"+(tEditingDomain != null));
				if(tEditingDomain != null) {
					System.out.println("**************7-1:"+tEditingDomain);
//					Collection filesToLoadInEditingDomain = (Collection)filesToLoad.get(editingDomain);
					System.out.println("**************9:"+file.isAccessible());
					System.out.println("**************10:"+file.isAccessible());
					System.out.println("**************11:"+file.isAccessible());
					System.out.println("**************12:"+file.isAccessible());
				}
			}
		}
		
		ModelLoadManager.INSTANCE.loadFile(file, releaseDescription, true, monitor);
		System.out.println("*************TRUE*104:"+EcorePlatformUtil.isFileLoaded(file));
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("*************TRUE*104-1:"+EcorePlatformUtil.isFileLoaded(file));
		
		
		org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);
		EObject margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file);
		System.out.println("TESTXXX::"+margeEObject);
		
		editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(projectArxmlFile.getProject(), releaseDescription);		
		margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, projectArxmlFile);
		System.out.println("TESTYYY::"+margeEObject);
		
		
		
//		GAUTOSAR firstElement = createProjectArxml(apProject, projectArxmlFile, monitor);
		
		
		
		
		editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);		
		margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file);
		System.out.println("TEST222::"+margeEObject);

//		ModelLoadManager.INSTANCE.loadFile(file, releaseDescription, true, monitor);
//		editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);
//		margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file);
//		
//		System.out.println("TEST333::"+margeEObject);
		
		
//		Resource res = getModelResource(file);
//		ModelLoadManager.INSTANCE.loadURI(res.getURI(), releaseDescription, true, monitor);
////		wait(IExtendedPlatformConstants.FAMILY_MODEL_LOADING);
//		editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), releaseDescription);
//		margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file);
//		System.out.println("TEST444::"+margeEObject);
		
		
		
//		org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(file.getProject(), metaModelVersionDescriptor);
//		EObject margeEObject = EcorePlatformUtil.loadModelRoot(editingDomain, file);		
		
		
		List<GReferrable> autosarModels = new ArrayList<>();
		
		System.out.println("TEST::"+margeEObject);
		
		
        if(firstElement == null) {
        	return;
//            break MISSING_BLOCK_LABEL_216;
        }
        EObject destinationEObject = (EObject)firstElement;
        
        for (int i = 0; i < firstElement.gGetArPackages().size(); i++) {
        	autosarModels.add((GReferrable)(firstElement.gGetArPackages().get(i)));
		}
        autosarModels.addAll(firstElement.gGetArPackages());
        autosarModels.addAll(collectSelection((AUTOSARImpl)margeEObject));
//        autosarModels = collectSelection((AUTOSARImpl)margeEObject/*(IStructuredSelection)currentSelection*/);
        System.out.println("복사할 패키지수:"+autosarModels.size());
        for (int i = 0; i < autosarModels.size(); i++) {
			System.out.println("복사패키지["+i+"]:"+autosarModels.get(i).gGetShortName());
		}
//        editingDomain = TransactionUtil.getEditingDomain(destinationEObject);
//        releaseDescription = getAutosarRelease(destinationEObject, editingDomain);
		
        APMergeAndSaveJob mergeAndSaveJob = new APMergeAndSaveJob(autosarModels, EcorePlatformUtil.createURI(projectArxmlFile.getFullPath()), editingDomain, releaseDescription);
        mergeAndSaveJob.setPriority(40);
        mergeAndSaveJob.schedule();
        
//		System.out.println("margeUserDefinedARXMLtoProjectARXML():"+file.getName());
////		EcoreUIUtil.
////		AutosarURIFactory.
////		EcoreResourceUtil.getModelRoot(new )
////		EObject eobj = EcorePlatformUtil.getModelRoot(file);
//		
////		GAUTOSAR g = new 
//		
////		final TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
////		EcoreResourceUtil.loadEObject(editingDomain.getResourceSet(), uri);
//		
//		
//		System.out.println("11:"+file.getFullPath());
//		System.out.println("12:"+file.getLocation());
//		System.out.println("13:"+file.getProjectRelativePath());
//		System.out.println("14:"+file.getRawLocation());
//		System.out.println("WS:"+ResourcesPlugin.getWorkspace().getRoot().getProject(file.getProject().getName()).getFile(file.getName()).getFullPath());
////		System.out.println("11:"+Path.forWindows(fullPath));
////		System.out.println("12:"+file.getLocation());
////		System.out.println("13:"+file.getProjectRelativePath());
////		System.out.println("14:"+file.getRawLocation());
//		
//		org.eclipse.emf.common.util.URI uri1 = org.eclipse.emf.common.util.URI.createFileURI(file.getLocation().toOSString());
//		org.eclipse.emf.common.util.URI uri2 = EcorePlatformUtil.createURI(file.getLocation());
//		EObject eobj1 = EcorePlatformUtil.getEObject(uri1); //getFullPath() getLocation() getProjectRelativePath() getRawLocation()
//		EObject eobj2 = EcorePlatformUtil.getEObject(uri2); //getFullPath() getLocation() getProjectRelativePath() getRawLocation()
//		
//		System.out.println("URI1:"+uri1+", "+uri1.hasFragment());
//		System.out.println("URI2:"+uri2+", "+uri2.hasFragment());
//		
//		System.out.println("TYPE1:"+eobj1);
//		System.out.println("TYPE2:"+eobj2);
//		if(eobj1 != null) {
//			System.out.println("TYPE1-1:"+eobj1.getClass());
//		}
//		if(eobj2 != null) {
//			System.out.println("TYPE2-1:"+eobj2.getClass());
//		}
//		
//		Resource modelResource = getModelResource(file);
////		(EObject)modelResource.getContents().get(0)
//		System.out.println("-------------1>>"+modelResource);
//		if(modelResource != null) {
//			System.out.println("-------------2>>"+modelResource.getContents().get(0));
//		}
//		
//		eobj1 = EcoreResourceUtil.loadEObject(modelResource.getResourceSet(), uri1);
//		eobj2 = EcoreResourceUtil.loadEObject(modelResource.getResourceSet(), uri2);
//		
//		
//		System.out.println("------------------------------------------------------------------------------------------"+eobj1+":::"+eobj2);
//		
//////		URIEditorInput input = EcoreUIUtil.createURIEditorInput(((File)file).getUR);
//////		TransactionalEditingDomain editingDomain1 = WorkspaceEditingDomainUtil.getEditingDomain(uri1);
////		org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain1 = WorkspaceEditingDomainUtil.getEditingDomain(file/*.getProject(), metaModelVersionDescriptor*/);
////		eobj1 = EcoreResourceUtil.loadEObject(editingDomain1.getResourceSet(), uri1);
////		
//////		TransactionalEditingDomain editingDomain2 = WorkspaceEditingDomainUtil.getEditingDomain(uri2);
////		org.eclipse.emf.transaction.TransactionalEditingDomain editingDomain2 = WorkspaceEditingDomainUtil.getEditingDomain(file/*.getProject(), metaModelVersionDescriptor*/);
//////		eobj2 = EcoreResourceUtil.loadEObject(editingDomain2.getResourceSet(), uri2);
////		eobj2 = EcoreResourceUtil.loadEObject(editingDomain2.getResourceSet(), uri2);
////		
////		System.out.println("TYPE1:"+eobj1);
////		System.out.println("TYPE2:"+eobj2);
////		if(eobj1 != null) {
////			System.out.println("TYPE1-1:"+eobj1.getClass());
////		}
////		if(eobj2 != null) {
////			System.out.println("TYPE2-1:"+eobj2.getClass());
////		}
//		
//		System.out.println("==========================================================================================");
	}
	
	private void createPredefinedArxml(IAdaptiveAutosarProject project, IFile file, IProgressMonitor monitor) {
		
		GAUTOSAR autosar = (GAUTOSAR)createInitialModel();
		
        IGAutosarFactoryService autosarFactory = getAutosarFactoryService();
        if(autosarFactory != null)
        {
            GARPackage arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + project.getTypes().getPackageName());
            autosar.gGetArPackages().add(arPackage);
        }
		
    	saveInitialModel(autosar, file, monitor);
	}
	
	private GAUTOSAR createProjectArxml(IAdaptiveAutosarProject project, IFile file, IProgressMonitor monitor) {
		
		GAUTOSAR autosar = (GAUTOSAR)createInitialModel();
		
        IGAutosarFactoryService autosarFactory = getAutosarFactoryService();
        if(autosarFactory != null)
        {
            GARPackage arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + project.getApplications().getPackageName());
            autosar.gGetArPackages().add(arPackage);
            
            arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + project.getServices().getPackageName());
            autosar.gGetArPackages().add(arPackage);
            
            arPackage = autosarFactory.createGARPackage();
            arPackage.gSetShortName(projectInfo.getTopPackageName() + "." + project.getMachines().getPackageName());
            autosar.gGetArPackages().add(arPackage);
        }
		
    	saveInitialModel(autosar, file, monitor);
    	return autosar;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Resource getModelResource(IResource workspaceResource)
    {
        if(workspaceResource instanceof IFile)
        {
            IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel((IFile)workspaceResource);
            if(modelDescriptor != null)
            {
                Resource modelResource = EcorePlatformUtil.getResource((IFile)workspaceResource);
                if(modelResource != null)
                    return modelResource;
//                addTransactionalEditingDomainListeners(modelDescriptor.getEditingDomain());
                ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);
            }
        }
        return null;
    }
	
//	public List getModelContentRoots(Resource modelResource)
//    {
//        if(modelResource != null)
//        {
//            ArrayList modelContentRoots = new ArrayList(3);
//            if(!modelResource.getContents().isEmpty())
//            {
//                Object deprecatedModelContentRoot = getModelContentRoot((EObject)modelResource.getContents().get(0));
//                if(deprecatedModelContentRoot != null)
//                    modelContentRoots.add(deprecatedModelContentRoot);
//            }
//            if(modelContentRoots.isEmpty())
//                modelContentRoots.add(modelResource);
//            return modelContentRoots;
//        } else
//        {
//            return Collections.emptyList();
//        }
//    }
//	
//	protected AdapterFactoryContentProvider getModelContentProvider(Object object)
//    {
//        TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(object);
//        if(editingDomain != null)
//        {
//            AdapterFactoryContentProvider modelContentProvider = (AdapterFactoryContentProvider)modelContentProviders.get(editingDomain);
//            if(modelContentProvider == null)
//            {
//                modelContentProvider = createModelContentProvider(editingDomain);
//                modelContentProviders.put(editingDomain, modelContentProvider);
//                addTransactionalEditingDomainListeners(editingDomain);
//            }
//            return modelContentProvider;
//        } else
//        {
//            return null;
//        }
//    }
//	
//	protected AdapterFactoryContentProvider createModelContentProvider(final TransactionalEditingDomain editingDomain)
//    {
//        Assert.isNotNull(editingDomain);
//        AdapterFactory adapterFactory = getAdapterFactory(editingDomain);
//        return new TransactionalAdapterFactoryContentProvider(adapterFactory, editingDomain) {
//
//            protected Object run(RunnableWithResult run)
//            {
//                try
//                {
//                    return TransactionUtil.runExclusive(editingDomain, run);
//                }
//                catch(Exception e)
//                {
//                    Tracing.catching(org/eclipse/emf/transaction/ui/provider/TransactionalAdapterFactoryLabelProvider, "run", e);
//                }
//                Thread.currentThread().interrupt();
//                return null;
//            }
//
//            final BasicExplorerContentProvider this$0;
//            private final TransactionalEditingDomain val$editingDomain;
//
//            
//        }
//    }
//	
//	protected void addTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain)
//    {
//        Assert.isNotNull(editingDomain);
//        if(resourceChangedListener == null)
//        {
//            resourceChangedListener = createResourceChangedListener();
//            Assert.isNotNull(resourceChangedListener);
//        }
//        editingDomain.addResourceSetListener(resourceChangedListener);
//        if(resourceMovedListener == null)
//        {
//            resourceMovedListener = createResourceMovedListener();
//            Assert.isNotNull(resourceMovedListener);
//        }
//        editingDomain.addResourceSetListener(resourceMovedListener);
//        if(crossReferenceChangedListener == null)
//        {
//            crossReferenceChangedListener = createCrossReferenceChangedListener();
//            Assert.isNotNull(crossReferenceChangedListener);
//        }
//        editingDomain.addResourceSetListener(crossReferenceChangedListener);
//        if(modelContentRootChangedListener == null)
//        {
//            modelContentRootChangedListener = createModelContentRootChangedListener();
//            Assert.isNotNull(modelContentRootChangedListener);
//        }
//        editingDomain.addResourceSetListener(modelContentRootChangedListener);
//    }
	
}
