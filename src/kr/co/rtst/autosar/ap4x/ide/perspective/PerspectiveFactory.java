package kr.co.rtst.autosar.ap4x.ide.perspective;

import org.artop.aal.examples.common.ui.perspectives.IArtopPerspectiveConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import kr.co.rtst.autosar.ap4x.ide.views.AdaptiveConfNavigator;

public class PerspectiveFactory implements IPerspectiveFactory {
	
	public static final String LAYOUT_LEFT_TOP = "kr.co.rtst.autosar.ap4x.ide.perspective.left.top";
	public static final String LAYOUT_RIGHT_TOP = "kr.co.rtst.autosar.ap4x.ide.perspective.right.top";
	public static final String LAYOUT_BOTTOM = "kr.co.rtst.autosar.ap4x.ide.perspective.bottom";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		
		final String editorArea = layout.getEditorArea();

		IFolderLayout leftTop = layout.createFolder(LAYOUT_LEFT_TOP, IPageLayout.LEFT, 0.2f, editorArea);
		leftTop.addView(AdaptiveConfNavigator.ID);
		leftTop.addView(IArtopPerspectiveConstants.ID_AUTOSAR_EXPLORER);
		leftTop.addView(IPageLayout.ID_PROJECT_EXPLORER);

		IFolderLayout bottom = layout.createFolder(LAYOUT_BOTTOM, IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IArtopPerspectiveConstants.ID_VALIDATION_VIEW);
		bottom.addView(IArtopPerspectiveConstants.ID_ERROR_LOG_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
//		bottom.addPlaceholder(IArtopPerspectiveConstants.ID_PROBLEMS_VIEW);
//		bottom.addPlaceholder(IArtopPerspectiveConstants.ID_CONSOLE_VIEW);
		
		IFolderLayout rightTop= layout.createFolder(LAYOUT_RIGHT_TOP, IPageLayout.RIGHT,(float)0.75, editorArea); //$NON-NLS-1$
		rightTop.addView(IPageLayout.ID_OUTLINE);
			
		
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_AUTOSAR_NEW_PROJECT);
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_AUTOSAR_NEW_FILE);
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_NEW_LINKED_FILE);
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_NEW_LINKED_FOLDER);
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_ECLIPSE_NEW_FOLDER);
		layout.addNewWizardShortcut(IArtopPerspectiveConstants.ID_ECLIPSE_NEW_FILE);
		
		
		layout.addShowViewShortcut(AdaptiveConfNavigator.ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IArtopPerspectiveConstants.ID_VALIDATION_VIEW);
		layout.addShowViewShortcut(IArtopPerspectiveConstants.ID_ERROR_LOG_VIEW);
		layout.addShowViewShortcut(IArtopPerspectiveConstants.ID_PROBLEMS_VIEW);
		layout.addShowViewShortcut(IArtopPerspectiveConstants.ID_CONSOLE_VIEW);
		
		
		layout.addPerspectiveShortcut(IArtopPerspectiveConstants.ID_RESOURCE_PERSPECTIVE);
		
		
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		layout.addActionSet(IArtopPerspectiveConstants.ID_LAUNCH_ACTIONSET);
		layout.addActionSet(IArtopPerspectiveConstants.ID_TEAM_ACTIONSET);
		
	}

}
