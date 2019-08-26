package model;

import java.util.ArrayList;

public class TabModel {
	private int tabIndex;
	private Object tabObject;
	
	public TabModel(int tabIndex, Object tabObject) {
		super();
		this.tabIndex = tabIndex;
		this.tabObject = tabObject;
	}

	public TabModel() {
		super();
		this.tabIndex = -1;
		this.tabObject = new Object();
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public Object getTabObject() {
		return tabObject;
	}

	public void setTabObject(Object tabObject) {
		this.tabObject = tabObject;
	}
}
