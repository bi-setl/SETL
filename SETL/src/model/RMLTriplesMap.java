package model;

import java.util.ArrayList;

public class RMLTriplesMap {
	
	private String triplesMapName, tableName, subjectMap;
	private ArrayList<String> predicateObjectMaps, columnNames;
	ArrayList<ArrayList<Object>> columnValues;
	
	public RMLTriplesMap(String triplesMapName, String tableName, String subjectMap,
			ArrayList<String> predicateObjectMaps, ArrayList<String> columnNames) {
		
		this.triplesMapName = triplesMapName;
		this.tableName = tableName;
		this.subjectMap = subjectMap;
		this.predicateObjectMaps = predicateObjectMaps;
		this.columnNames = columnNames;
		this.columnValues = new ArrayList<>();
		
	}

	public ArrayList<ArrayList<Object>> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(ArrayList<ArrayList<Object>> columnValues) {
		this.columnValues = columnValues;
	}

	public String getTriplesMapName() {
		return triplesMapName;
	}

	public void setTriplesMapName(String triplesMapName) {
		this.triplesMapName = triplesMapName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(String subjectMap) {
		this.subjectMap = subjectMap;
	}

	public ArrayList<String> getPredicateObjectMaps() {
		return predicateObjectMaps;
	}

	public void setPredicateObjectMaps(ArrayList<String> predicateObjectMaps) {
		this.predicateObjectMaps = predicateObjectMaps;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	void printTriplesMap(){
		
		System.out.print("\nTRIPLE MAP NAME: ");
		System.out.println(triplesMapName);
		System.out.print("TABLE NAME: ");
		System.out.println(tableName);
		System.out.println("SUBJECT MAP");
		System.out.println(subjectMap);
		System.out.println("PREDICATE OBJECT MAPS");
		for(String predicateObjectMap:predicateObjectMaps){
			System.out.println(predicateObjectMap);
		}
		
		System.out.print("Column Names:");
		for(String colName: columnNames){
			System.out.print("\t" + colName);
		}
		System.out.println();
		
		System.out.print("COLUMN VALUES:");
		int size = columnValues.size();
		
		if(size >0){
			
			ArrayList<Object> colValues = columnValues.get(0);
			
			for(Object obj: colValues){
				System.out.print("\t" + obj);
			}
			System.out.println();
		}else{
			System.out.println(" Not Assigned");
		}
		
	}
	
}
