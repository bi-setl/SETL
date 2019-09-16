package model;

public class PredicateMap {

	String termpMapType;
	String termMapvalue;
	
	
	public PredicateMap() {
	}

	public PredicateMap(String termpMapType, String termMapvalue) {
		
		this.termpMapType = termpMapType;
		this.termMapvalue = termMapvalue;
	}

	public String getTermpMapType() {
		return termpMapType;
	}

	public void setTermpMapType(String termpMapType) {
		this.termpMapType = termpMapType;
	}

	public String getTermMapvalue() {
		return termMapvalue;
	}

	public void setTermMapvalue(String termMapvalue) {
		this.termMapvalue = termMapvalue;
	}
	
	
}
