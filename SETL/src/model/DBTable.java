package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.PrinterLocation;



public class DBTable {
	

	private String tableName;
	private ArrayList<String> primaryKeys;
	private ArrayList<ForeignKey> foreignKeys;
	private ArrayList<String> dataColumns;
	private ArrayList<String> dataColumnValues; //"L" or URL
	private ArrayList<Boolean> dataColumnsIsURL;
	private HashMap<String, String> columnDatatypes;

	public DBTable(String tableName, ArrayList<String> primaryKeys, ArrayList<ForeignKey> foreignKeys, ArrayList<String> dataColumns, HashMap<String, String> datatypes) {

		this.tableName = tableName;
		this.primaryKeys = primaryKeys;
		this.foreignKeys = foreignKeys;
		this.dataColumns = dataColumns;
		
		this.dataColumnValues = new ArrayList<>();
		this.dataColumnsIsURL = new ArrayList<>();
		
		for(String s: dataColumns){
			dataColumnValues.add("L");
			dataColumnsIsURL.add(false);		
		}
		this.columnDatatypes = datatypes;
	}


	public DBTable() {
		// TODO Auto-generated constructor stub
	}

	
	public HashMap<String, String> getColumnDatatypes() {
		return columnDatatypes;
	}


	public void setColumnDatatypes(HashMap<String, String> columnDatatypes) {
		this.columnDatatypes = columnDatatypes;
	}


	public ArrayList<String> getDataColumnValues() {
		return dataColumnValues;
	}


	public void setDataColumnValues(ArrayList<String> dataColumnValues) {
		this.dataColumnValues = dataColumnValues;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public ArrayList<String> getPrimaryKeys() {
		return primaryKeys;
	}


	public void setPrimaryKeys(ArrayList<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}


	public ArrayList<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}


	public void setForeignKeys(ArrayList<ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}


	public ArrayList<String> getDataColumns() {
		return dataColumns;
	}


	public void setDataColumns(ArrayList<String> dataColumns) {
		this.dataColumns = dataColumns;
	}
	
	
	public ArrayList<Boolean> getDataColumnsIsURL() {
		return dataColumnsIsURL;
	}


	public void setDataColumnsIsURL(ArrayList<Boolean> dataColumnsIsURL) {
		this.dataColumnsIsURL = dataColumnsIsURL;
	}

	
	public void printTableConfigurations(){
		
		System.out.println("\n\nTABLE NAME: "+tableName);
		System.out.println("\nPRIMARY KEY:");
		System.out.print(primaryKeys.get(0).toString());
		for(int i=1; i<primaryKeys.size(); i++){
			System.out.print(", "+primaryKeys.get(i));
		}
		System.out.println();
		System.out.println("\nFOREIGN KEYS:");
		
		for(ForeignKey fk: foreignKeys){
			
			for(String temp: fk.getColumnNames()){
				System.out.print(temp+" ");
			}
			
			System.out.print(fk.getTableName()+ "-> ");
		
			for(String temp: fk.getTargetTableColumnNames()){
				System.out.print(temp+" ");
			}
			
			System.out.println();
		}
		System.out.println("\nDATA COLUMNS: ");
		int index = 0;
		for(String temp: dataColumns){
			System.out.print(temp + " ");
			if(dataColumnsIsURL.size()>0)
				System.out.println(" URL: " + dataColumnsIsURL.get(index) + " Value: " );
			if(dataColumnValues.size()>0)
				System.out.println(dataColumnValues.get(index));
			index++;
		}
		
		System.out.println("DATATYPES:\n");
		
		//map.keySet(), map.values
		for(Map.Entry<String, String> map: columnDatatypes.entrySet()){
			System.out.println("Name: " + map.getKey()+ " Datatype: " + map.getValue());
			
		}
	}

}
