package model;

import java.util.LinkedHashMap;

public class SubjectConstruct {
	private String subjectString;
	private String provString;
	private boolean hasProvIRI;
	private boolean hasRange;
	private String typeString;
	private String rangeString;
	private LinkedHashMap<String, PropertyConstruct> propertyMap;
	
	public SubjectConstruct() {
		super();
		subjectString = "";
		provString = "";
		hasProvIRI = false;
		hasRange = false;
		typeString = "";
		rangeString = "";
		propertyMap = new LinkedHashMap<String, PropertyConstruct>();
	}

	public String getSubjectString() {
		return subjectString;
	}

	public void setSubjectString(String subjectString) {
		this.subjectString = subjectString;
	}

	public String getProvString() {
		return provString;
	}

	public void setProvString(String provString) {
		this.provString = provString;
	}

	public LinkedHashMap<String, PropertyConstruct> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(LinkedHashMap<String, PropertyConstruct> propertyMap) {
		this.propertyMap = propertyMap;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return subjectString;
	}

	public boolean isHasProvIRI() {
		return hasProvIRI;
	}

	public void setHasProvIRI(boolean hasProvIRI) {
		this.hasProvIRI = hasProvIRI;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getRangeString() {
		return rangeString;
	}

	public void setRangeString(String rangeString) {
		this.rangeString = rangeString;
	}

	public boolean isHasRange() {
		return hasRange;
	}

	public void setHasRange(boolean hasRange) {
		this.hasRange = hasRange;
	}
}
