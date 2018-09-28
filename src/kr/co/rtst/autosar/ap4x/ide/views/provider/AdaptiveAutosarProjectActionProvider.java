package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;

public class AdaptiveAutosarProjectActionProvider extends CommonActionProvider {

	public AdaptiveAutosarProjectActionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		
//		IAction action = new Action("TEST...") {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				super.run();
//			}
//		};
//		
//		menu.add(action);
		
		IAction action2 = new Action("Adaptive Autosar...") {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
			}
		};
		
		menu.appendToGroup("additions", action2);
		
//		IContributionItem[] items = menu.getItems();
//		for (int i = 0; i < items.length; i++) {
//			System.out.println("ITEM["+i+"]:"+items[i].getId()+", TYPE:"+items[i].getClass());
//			if(items[i] instanceof Separator) {
//				System.out.println("Separator 1:"+((Separator)items[i]).getGroupName());
//			} else if(items[i] instanceof GroupMarker) {
//				System.out.println("GroupMarker 1:"+((GroupMarker)items[i]).getGroupName());
//			}
//		}
//		
//		System.out.println("::::::::::::::::1:"+menu.findMenuUsingPath("group.new"));
//		System.out.println("::::::::::::::::2:"+menu.findMenuUsingPath("group.show"));
		
	}
	
//	@Override
//	public void fillActionBars(IActionBars actionBars) {
//		// TODO Auto-generated method stub
//		super.fillActionBars(actionBars);
//		
//		IAction action = new Action("TEST2...") {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				super.run();
//			}
//		};
//		
//		actionBars.getToolBarManager().add(action);
//		
//		IAction action2 = new Action("TEST3...") {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				super.run();
//			}
//		};
//		
//		actionBars.getMenuManager().add(action2);
//		
//	}
	
	@Override
	protected boolean filterAction(IAction action) {
		// TODO Auto-generated method stub
		return super.filterAction(action);
	}
	
	@Override
	public void updateActionBars() {
		System.out.println(":::updateActionBars");
		super.updateActionBars();
	}
	
}
