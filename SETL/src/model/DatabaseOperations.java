package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import view.SETLFrame;

/*import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;*/


public class DatabaseOperations {

	Connection dbConnection = null;
	Statement statement = null;
	DatabaseConnection databaseConnection;

	private String dbURL;
	private String dbUserName;
	private String dbPassWord;
	
	
	public DatabaseOperations(String dbURL, String dbUserName, String dbPassWord) {
		
		this.dbURL = dbURL;
		this.dbUserName = dbUserName;
		this.dbPassWord = dbPassWord;
	}
	
	public DatabaseOperations() {
		this.dbURL = "";
		this.dbUserName = "";
		this.dbPassWord = "";
	}

	public ArrayList<ArrayList<Object>> getTableDataByColumns(String tableName, ArrayList<String> columns){
		
		ArrayList<ArrayList<Object>> tableRows = new ArrayList<>();
		
		
		databaseConnection = new DatabaseConnection();		
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);
		
		String query = "SELECT "+columns.get(0);
		int numOfCols = columns.size();
		for(int i=1; i<numOfCols; i++)
			query += ", " +columns.get(i);
		query += " FROM " + tableName + ";";
		
		//JOptionPane.showMessageDialog(null, query);
		
		
		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
			
			while (resultSet.next()) {
				
				ArrayList<Object> row = new ArrayList<>();
				
				for(int i = 1; i<=columns.size(); i++){	
					row.add(resultSet.getObject(i));	
				}
				
				tableRows.add(row);
			}

			resultSet.close();
			dbConnection.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tableRows;
		
		
	}
	
	private HashMap<String, String> getColumnDataTypes(String tableName){
		HashMap<String, String> columnDataTypes = new HashMap<>();
		
		databaseConnection = new DatabaseConnection();
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);

		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("select column_name, data_type from information_schema.columns where table_name = \'" + tableName + "\';");
			
			while (resultSet.next()) {
				
				String colName = resultSet.getString(1).toString();
				String datatype = resultSet.getString(2).toString();
				
				columnDataTypes.put(colName, datatype);
				
			}
			resultSet.close();
			dbConnection.close();
			statement.close();
			return columnDataTypes;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Column Datatype Retreiv Failed.");
		}
		
		return columnDataTypes;
	}
	
	public ArrayList<String> getAllTableNames() {

		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		
		//dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);
		
		ArrayList<String> allTables = new ArrayList<>();
		
		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE' ORDER BY table_name ASC;");

			while (resultSet.next()) {
				allTables.add(resultSet.getString(1));
			}

			resultSet.close();
			dbConnection.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allTables;
	}

	public DefaultTableModel getDBTableModel(String tableName) {

		DefaultTableModel tableModel;
		databaseConnection = new DatabaseConnection();
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + ";");

			tableModel = createTableModel(resultSet);

			resultSet.close();
			dbConnection.close();
			statement.close();

			return tableModel;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Data Retreived Failed.");
			return null;
		}

	}
	
	
	public DefaultTableModel getQueryTableModel(String query) {

		DefaultTableModel tableModel;
		databaseConnection = new DatabaseConnection();
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query+";");

			tableModel = createTableModel(resultSet);

			resultSet.close();
			dbConnection.close();
			statement.close();

			return tableModel;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Data Retreived Failed.");
			return null;
		}

	}
	
	/*public void importDataToCSV(String query, String path) {

		
		
		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);
		CopyManager copyManager = null;
		try {
			copyManager = new CopyManager((BaseConnection) dbConnection);
		} catch (SQLException e2) {
			System.out.println("Error1");
			e2.printStackTrace();
		}
		File file = new File(path);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			
			e2.printStackTrace();
		}

		//and finally execute the COPY command to the file with this method:
		try {
			try {
				System.out.println(query);
				copyManager.copyOut("COPY (" + query + ") TO STDOUT WITH (FORMAT CSV)", fileOutputStream);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		
	}*/


	private DefaultTableModel createTableModel(ResultSet resultSet) {

		try {
			ResultSetMetaData metaData = resultSet.getMetaData();

			// get the names of columns

			Vector<String> tableColumnNames = new Vector<>();
			int columnCount = metaData.getColumnCount();

			for (int i = 1; i <= columnCount; i++) {
				tableColumnNames.add(metaData.getColumnName(i));
			}

			// Data from the tables

			Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();

			while (resultSet.next()) {

				// creating the rows
				Vector<Object> row = new Vector<>();
				for (int i = 1; i <= columnCount; i++) {
					row.add(resultSet.getObject(i));
				}
				tableData.add(row);
			}

			return (new DefaultTableModel(tableData, tableColumnNames));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector<Vector<Object>> getTableData(String tableName) {

		databaseConnection = new DatabaseConnection();
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + ";");

			ResultSetMetaData metaData = resultSet.getMetaData();

			// get the names of columns

			Vector<String> tableColumnNames = new Vector<>();
			int columnCount = metaData.getColumnCount();

			for (int i = 1; i <= columnCount; i++) {
				tableColumnNames.add(metaData.getColumnName(i));
			}

			// Data from the tables

			Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();

			while (resultSet.next()) {

				// creating the rows
				Vector<Object> row = new Vector<>();
				for (int i = 1; i <= columnCount; i++) {
					row.add(resultSet.getObject(i));
				}
				tableData.add(row);
			}
			resultSet.close();
			dbConnection.close();
			statement.close();
			return tableData;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Data Retreived Failed.");
			return null;

		}

	}

	private ArrayList<String> getAllColumnNames(String tableName) {

		databaseConnection = new DatabaseConnection();
		//dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);
		ArrayList<String> allColumns = new ArrayList<>();
		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"select column_name from information_schema.columns where table_name='" + tableName + "';");

			while (resultSet.next()) {
				allColumns.add(resultSet.getString(1));
			}

			resultSet.close();
			dbConnection.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allColumns;

	}

	private ArrayList<String> getAllDataColumnNames(String tableName) {

		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		//dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);
		ArrayList<String> allDataColumns = new ArrayList<>();
		try {

			statement = dbConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"select column_name from information_schema.columns where table_name='" + tableName + "';");

			while (resultSet.next()) {
				allDataColumns.add(resultSet.getString(1));
			}

			resultSet.close();
			dbConnection.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allDataColumns;

	}
	
	
	public ArrayList<String> getPrimaryKey(String tableName) {

		ArrayList<String> primaryKeys = new ArrayList<>();

		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		//dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();

			String query = "select tc.table_schema, tc.table_name, kc.column_name"
					+ " from  information_schema.table_constraints tc,  information_schema.key_column_usage kc  "
					+ "where tc.constraint_type = 'PRIMARY KEY' and kc.table_name = '" + tableName
					+ "' and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name "
					+ "order by 1, 2;";

			ResultSet resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
					
				primaryKeys.add(resultSet.getString(3));
				
			}
			
			resultSet.close();
			statement.close();
			return primaryKeys;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return primaryKeys;
	}

	
	private ArrayList<String> getAllForeignKeyNames(String tableName) {

		ArrayList<String> foreignKeys = new ArrayList<>();

		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		//dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();

			String query = "select tc.table_schema, tc.table_name, kc.column_name"
					+ " from  information_schema.table_constraints tc,  information_schema.key_column_usage kc  "
					+ "where tc.constraint_type = 'FOREIGN KEY' and kc.table_name = '" +tableName+ "' and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name "
					+ "order by 1, 2;";

			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {

				foreignKeys.add(resultSet.getString(3));
			}
			
			resultSet.close();
			statement.close();
			return foreignKeys;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foreignKeys;

	}

	
	private ArrayList<ArrayList<String>> getAllForeignKeys(String tableName) {

		ArrayList<ArrayList<String>> foreignKeys = new ArrayList<>();

		databaseConnection = new DatabaseConnection();
		dbConnection = databaseConnection.getConnection(SETLFrame.dbURL, SETLFrame.dbUserName, SETLFrame.dbPassword);
		//dbConnection = databaseConnection.getConnection(dbURL, dbUserName, dbPassWord);

		try {

			statement = dbConnection.createStatement();

			String query = "SELECT conrelid::regclass AS FK_Table, "
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), 14, position(')' in pg_get_constraintdef(c.oid))-14) END AS FK_Column ,"
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), position(' REFERENCES ' in pg_get_constraintdef(c.oid))+12, position('(' in substring(pg_get_constraintdef(c.oid), 14))-position(' REFERENCES ' in pg_get_constraintdef(c.oid))+1) END AS PK_Table ,"
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), position('(' in substring(pg_get_constraintdef(c.oid), 14))+14, position(')' in substring(pg_get_constraintdef(c.oid), position('(' in substring(pg_get_constraintdef(c.oid), 14))+14))-1) END AS PK_Column "
					+ "FROM   pg_constraint c JOIN   pg_namespace n ON n.oid = c.connamespace "
					+ "WHERE  contype IN ('f', 'p ')  AND conrelid::regclass::text = '"+tableName+"' AND pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' "
							+ "ORDER  BY conrelid::regclass::text, contype DESC;";
					
			ResultSet resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				
				ArrayList<String> tempList = new ArrayList<>();
				tempList.add(resultSet.getString(2));
				tempList.add(resultSet.getString(3));
				tempList.add(resultSet.getString(4));
				
				foreignKeys.add(tempList);
				
			}
			
			resultSet.close();
			statement.close();
			//System.out.println("Size of Foreign Keys: " +foreignKeys.size());
			return foreignKeys;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foreignKeys;

	}
	
	
	
	public ArrayList<DBTable> getAllDBTableStructure() {

		ArrayList<String> tableNames,primaryKeys, foreignKeyNames, dataValues;
		ArrayList<ForeignKey> foreignKeys;
		ArrayList<DBTable> tableConfigurations = new ArrayList<>();

		tableNames = getAllTableNames();
		
		for(String tableName:tableNames){
						
			
			primaryKeys = getPrimaryKey(tableName);
			foreignKeys = new ArrayList<>();
			dataValues = getAllDataColumnNames(tableName);
			foreignKeyNames = getAllForeignKeyNames(tableName);
			
			//remove the duplicate elements
			ArrayList<String> duplicates  = new ArrayList<String>();
			
			for(String temp: dataValues){
				
				if(primaryKeys.contains(temp) || foreignKeyNames.contains(temp))
					duplicates.add(temp);
	
			}
			
			for(int i = 0; i<duplicates.size(); i++){
				dataValues.remove(duplicates.get(i));
			}
			
			
			ArrayList<ArrayList<String>> tempForeignKeys = getAllForeignKeys(tableName);
			
			int size = tempForeignKeys.size();
			for(int i=0; i<size; i++){
				
				String tName = tempForeignKeys.get(i).get(1);
				ArrayList<String> sourceCols = new ArrayList<>();
				ArrayList<String> targetCols = new ArrayList<>();
				sourceCols.add(tempForeignKeys.get(i).get(0));
				targetCols.add(tempForeignKeys.get(i).get(2));
				
				if(i+1<size){
					while(tempForeignKeys.get(i+1).get(1).equals(tName)) {
						i = i+1;
						sourceCols.add(tempForeignKeys.get(i).get(0));
						targetCols.add(tempForeignKeys.get(i).get(2));
					}
				}
				
				foreignKeys.add(new ForeignKey(sourceCols, tName, targetCols));
			}
			
			
			HashMap<String, String> datatypes = getColumnDataTypes(tableName);
			
			tableConfigurations.add(new DBTable(tableName, primaryKeys, foreignKeys, dataValues, datatypes));
		}
		return tableConfigurations;
	}

}
