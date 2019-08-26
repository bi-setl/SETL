package model;

import java.util.ArrayList;

public class TableData {

	private String tableName;
	private ArrayList<String> columnNames;
	private ArrayList<ArrayList<Object>> columnValues;
	
	
	public TableData(String tableName, ArrayList<String> columnNames, ArrayList<ArrayList<Object>> columnValues) {
		this.tableName = tableName;
		this.columnNames = columnNames;
		this.columnValues = columnValues;
	}
	
	
	public TableData() {
		this.tableName = "";
		this.columnNames = new ArrayList<>();
		this.columnValues = new ArrayList<>();
	}
	
	
	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public ArrayList<String> getColumnNames() {
		return columnNames;
	}


	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}


	public ArrayList<ArrayList<Object>> getColumnValues() {
		return columnValues;
	}


	public void setColumnValues(ArrayList<ArrayList<Object>> columnValues) {
		this.columnValues = columnValues;
	}
	
}
