package kr.co.rtst.autosar.ap4x.ide.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdaptiveAutosarProjectCreationInfo {
	
	private String topPackageName;
	private boolean predefinedArxml;
	private List<File> importArxmlList;
	
	public AdaptiveAutosarProjectCreationInfo() {
		this.importArxmlList = new ArrayList<>();
	}
	
	public String getTopPackageName() {
		return topPackageName;
	}
	public void setTopPackageName(String topPackageName) {
		this.topPackageName = topPackageName;
	}
	public boolean isPredefinedArxml() {
		return predefinedArxml;
	}
	public void setPredefinedArxml(boolean predefinedArxml) {
		this.predefinedArxml = predefinedArxml;
	}
	public List<File> getImportArxmlList() {
		return importArxmlList;
	}
	public void setImportArxmlList(List<File> importArxmlList) {
		this.importArxmlList = importArxmlList;
	}
	
}
