package model;
import java.util.ArrayList;

public class DBRDF {
	
	private String tableName;
	private ArrayList<String> rdfTriples;
	
	public DBRDF(String tableName, ArrayList<String> rdfTriples) {
		
		this.tableName = tableName;
		this.rdfTriples = rdfTriples;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getRdfTriples() {
		return rdfTriples;
	}

	public void setRdfTuples(ArrayList<String> rdfTriples) {
		this.rdfTriples = rdfTriples;
	}
	
}
