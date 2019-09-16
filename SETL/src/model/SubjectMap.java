package model;

import java.util.ArrayList;

public class SubjectMap {
	
	//template or column or constant
	String termMapType;
	
	//column names of primary keys
	String termMapValue;
	//may have one or more class: must be IRI
	ArrayList<String> classes;
	
	
	public SubjectMap(String termMapType, String termMapValue, ArrayList<String> classes) {
		this.termMapType = termMapType;
		this.termMapValue = termMapValue;
		this.classes = classes;
	}
	
	public SubjectMap(String termMapType, String termMapValue) {
		this.termMapType = termMapType;
		this.termMapValue = termMapValue;
		this.classes = new ArrayList<>();
	}

	public String getTermMapType() {
		return termMapType;
	}

	public void setTermMapType(String termMapType) {
		this.termMapType = termMapType;
	}

	public String getTermMapValue() {
		return termMapValue;
	}

	public void setTermMapValue(String termMapValue) {
		this.termMapValue = termMapValue;
	}

	public ArrayList<String> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}
	
	
	
}
