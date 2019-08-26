package model;

import java.util.ArrayList;


public class ForeignKey {
	
	private ArrayList<String> columnNames;
	private String tableName;
	private ArrayList<String> targetTableColumnNames;
	
	
	public ForeignKey(ArrayList<String> columnNames, String tableName, ArrayList<String> targetTableColumnNames) {
		this.columnNames = columnNames;
		this.tableName = tableName;
		this.targetTableColumnNames = targetTableColumnNames;
	}


	public ArrayList<String> getColumnNames() {
		return columnNames;
	}


	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public ArrayList<String> getTargetTableColumnNames() {
		return targetTableColumnNames;
	}


	public void setTargetTableColumnNames(ArrayList<String> targetTableColumnNames) {
		this.targetTableColumnNames = targetTableColumnNames;
	}
	
	

}
