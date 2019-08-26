package model;

import java.util.ArrayList;

public class TabModelList {
	ArrayList<TabModel> tabModels;

	public ArrayList<TabModel> getTabModels() {
		return tabModels;
	}

	public void setTabModels(ArrayList<TabModel> tabModels) {
		this.tabModels = tabModels;
	}

	public TabModelList() {
		super();
		this.tabModels = new ArrayList<>();
	}
}
