package controller;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import model.DBTable;
import model.DatabaseOperations;

public class DBPanelController {

	
	DatabaseOperations databaseOperations = new DatabaseOperations();
	
	public ArrayList<String> connectButtonHandler(){
		
		ArrayList<String> tableNames = databaseOperations.getAllTableNames();
		return tableNames;
		
	}
	
	public DefaultTableModel getDBTableTableModel(String name)
	{
		return databaseOperations.getDBTableModel(name);
		
	}

	public ArrayList<DBTable> getAllDBTableStructures() {
		
		ArrayList<DBTable> allDBTables = databaseOperations.getAllDBTableStructure();
		return allDBTables;
	}
	
	
	public void convertButtonHandler() {
		
		ArrayList<DBTable> allDBTables = getAllDBTableStructures();
		
		for(DBTable table: allDBTables){
			table.printTableConfigurations();
		}
	}
	
}
