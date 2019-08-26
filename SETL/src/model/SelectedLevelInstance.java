package model;

import java.util.ArrayList;

public class SelectedLevelInstance {
	private String filterCondition;
	private ArrayList<Object> instances;
	
	public SelectedLevelInstance(String filterCondition, ArrayList<Object> instances) {
		super();
		this.filterCondition = filterCondition;
		this.instances = instances;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public ArrayList<Object> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<Object> instances) {
		this.instances = instances;
	}

	public SelectedLevelInstance() {
		super();
	}
	
	public boolean containInstance(Object levelInstance) {
		if (instances == null) {
			return false;
		} else {
			if (instances.contains(levelInstance)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public void addInstance(Object levelInstance) {
		if (instances == null) {
			instances = new ArrayList<>();
			instances.add(levelInstance);
		} else {
			instances.add(levelInstance);
		}
	}

	public SelectedLevelInstance(String filterCondition) {
		super();
		this.filterCondition = filterCondition;
		this.instances = new ArrayList<>();
	}
}
