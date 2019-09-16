package model;

public class ObjectMap {

	//template or column or constant
	String termMapType;
	String termMapValue;
	//rr:Literal, IRI, BlankNode
	String termType;

	
	public ObjectMap() {

	}

	public ObjectMap(String termMapType, String termMapValue, String termType) {
		this.termMapType = termMapType;
		this.termMapValue = termMapValue;
		this.termType = termType;
	}
	
	public ObjectMap(String termMapType, String termMapValue) {
		this.termMapType = termMapType;
		this.termMapValue = termMapValue;
		this.termType = "";
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

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}
	
	
}
