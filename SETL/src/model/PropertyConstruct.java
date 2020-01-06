package model;

import java.util.ArrayList;

import org.apache.jena.rdf.model.RDFNode;

public class PropertyConstruct {
	private String propertyString;
	private String updateTypeString;
	private String rangeString;
	private ArrayList<RDFNode> valueList;
	
	public PropertyConstruct() {
		super();
		
		propertyString = "";
		updateTypeString = "";
		rangeString = "";
		valueList = new ArrayList<RDFNode>();
	}

	public String getPropertyString() {
		return propertyString;
	}

	public void setPropertyString(String propertyString) {
		this.propertyString = propertyString;
	}

	public String getUpdateTypeString() {
		return updateTypeString;
	}

	public void setUpdateTypeString(String updateTypeString) {
		this.updateTypeString = updateTypeString;
	}

	public ArrayList<RDFNode> getValueList() {
		return valueList;
	}

	public void setValueList(ArrayList<RDFNode> valueList) {
		this.valueList = valueList;
	}

	@Override
	public String toString() {
		return propertyString;
	}

	public String getRangeString() {
		return rangeString;
	}

	public void setRangeString(String rangeString) {
		this.rangeString = rangeString;
	}
}
