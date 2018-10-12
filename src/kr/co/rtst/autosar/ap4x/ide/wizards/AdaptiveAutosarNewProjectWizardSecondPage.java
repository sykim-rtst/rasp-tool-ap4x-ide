package kr.co.rtst.autosar.ap4x.ide.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import kr.co.rtst.autosar.ap4x.ide.consts.IDEText;
import kr.co.rtst.autosar.common.ui.util.UiUtil;

public class AdaptiveAutosarNewProjectWizardSecondPage extends WizardPage {

	public static final String PAGE_NAME = "kr.co.rtst.autosar.ap4x.ide.wizards.secondPage";
	
	private final AdaptiveAutosarProjectCreationInfo projectInfo;
	
	private Button btnImportArxml, btnAddArxml, btnRemoveArxml;
	private TableViewer tblArxmlList;
	private Label lblImportArxml;
	private List<ArxmlItem> arxmlList;
	
	protected AdaptiveAutosarNewProjectWizardSecondPage(AdaptiveAutosarProjectCreationInfo projectInfo) {
		super(PAGE_NAME, "Project Configuration", null);
		arxmlList = new ArrayList<>();
		this.projectInfo = projectInfo;
		setTitle(IDEText.WIZARD_SECOND_PAGE_TITLE);
		setDescription(IDEText.WIZARD_SECOND_PAGE_MESSAGE);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComp.setLayout(UiUtil.getGridLayoutWithMargin(1, true, 0));
		
		createArxmlGroup(mainComp);
		
		init();
		
		setControl(mainComp);
	}
	
	protected void createArxmlGroup(Composite parent) {
//		Group group = new Group(parent, SWT.NONE);
//		group.setText(IDEText.WIZARD_ARXML_GROUP_NAME);
//		group.setLayoutData(new GridData(GridData.FILL_BOTH));
//		group.setLayout(UiUtil.getGridLayoutDefault(1, false));

		btnImportArxml = new Button(parent, SWT.CHECK);
		btnImportArxml.setText(IDEText.WIZARD_INPORT_ARXML_BUTTON);
		btnImportArxml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateArxmlImportState();
			}
		});
		
		lblImportArxml = new Label(parent, SWT.NONE);
		lblImportArxml.setText(IDEText.WIZARD_INPORT_ARXML_LIST_LABEL);
		
		Composite compTable = new Composite(parent, SWT.NONE);
		compTable.setLayout(UiUtil.getGridLayoutNoMargin(2, false));
		compTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Table table = UiUtil.createTable(compTable, SWT.BORDER|SWT.FULL_SELECTION|SWT.V_SCROLL|SWT.H_SCROLL|SWT.MULTI,
				IDEText.WIZARD_INPORT_ARXML_TABLE_COLUMN,
				new int[] {150, 300},
				new int[] {SWT.LEFT, SWT.LEFT},
				new boolean[] {false, false},
				null);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		tblArxmlList = new TableViewer(table);
		tblArxmlList.setContentProvider(new ArxmlContentProvider());
		tblArxmlList.setLabelProvider(new ArxmlLabelProvider());
		tblArxmlList.setInput(arxmlList);
		
		Composite compButton = new Composite(compTable, SWT.NONE);
		GridLayout layout = UiUtil.getGridLayoutNoMargin(1, false);
		GridData gData = new GridData(GridData.FILL_BOTH);
		gData.verticalAlignment = SWT.TOP;
		compButton.setLayout(layout);
		compButton.setLayoutData(gData);
		
		btnAddArxml = new Button(compButton, SWT.PUSH);
		btnAddArxml.setText(IDEText.WIZARD_INPORT_ARXML_ADD_BUTTON);
		btnAddArxml.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnRemoveArxml = new Button(compButton, SWT.PUSH);
		btnRemoveArxml.setText(IDEText.WIZARD_INPORT_ARXML_REMOVE_BUTTON);
		btnRemoveArxml.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btnAddArxml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(btnAddArxml.getShell(), SWT.SAVE|SWT.MULTI);
				fd.setFilterExtensions(new String[] {"*.arxml"});
				if(fd.open() != null) {
					ArxmlItem item = null;
					System.out.println("경로:"+fd.getFilterPath());
					for (int i = 0; i < fd.getFileNames().length; i++) {
						System.out.println("파일["+i+"]:"+fd.getFileNames()[i]);
						try {
							item = new ArxmlItem(fd.getFilterPath(), fd.getFileNames()[i]);
							if(!arxmlList.contains(item)) {
								arxmlList.add(item);
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
					tblArxmlList.refresh();
					updateArxmlList();
				}
			}
		});
		
		btnRemoveArxml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!tblArxmlList.getStructuredSelection().isEmpty()) {
					Iterator<ArxmlItem> i = tblArxmlList.getStructuredSelection().iterator();
					while(i.hasNext()) {
						arxmlList.remove(i.next());
					}
					tblArxmlList.refresh();
					updateArxmlList();
				}
			}
		});
	}
	
	public void init() {
		btnImportArxml.setSelection(true);
		updateArxmlImportState();
	}
	
	private void updateArxmlList() {
		TableItem[] items = tblArxmlList.getTable().getItems();
		ArxmlItem data = null;
		for (int i = 0; i < items.length; i++) {
			data = (ArxmlItem)items[i].getData();
			projectInfo.getImportArxmlList().add(data.getArxmlFile());
		}
	}
	
	private void updateArxmlImportState() {
		if(btnImportArxml.getSelection()) {
			lblImportArxml.setEnabled(true);
			tblArxmlList.getTable().setEnabled(true);
			btnAddArxml.setEnabled(true);
			btnRemoveArxml.setEnabled(true);
		} else {
			lblImportArxml.setEnabled(false);
			tblArxmlList.getTable().setEnabled(false);
			btnAddArxml.setEnabled(false);
			btnRemoveArxml.setEnabled(false);
		}
	}
	
	private class ArxmlContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
			return new Object[0];
		}
	}
	
	private class ArxmlLabelProvider extends LabelProvider implements ITableLabelProvider{
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof ArxmlItem) {
				switch(columnIndex) {
				case 0: return ((ArxmlItem) element).getArxmlFile().getName();  
				case 1: return ((ArxmlItem) element).getArxmlFile().getParent();
				}
			}
			return null;
		}
		
	}
	
	private class ArxmlItem {
		private final File arxmlFile;

		ArxmlItem(String dir, String file) throws FileNotFoundException {
			arxmlFile = new File(dir, file);
			if(!arxmlFile.isFile()) {
				throw new FileNotFoundException(dir + IPath.SEPARATOR + file);
			}
		}
		
		File getArxmlFile() {
			return arxmlFile;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof ArxmlItem) {
				return getArxmlFile().equals(((ArxmlItem) obj).getArxmlFile());
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return getArxmlFile().hashCode();
		}
	}
	
}
