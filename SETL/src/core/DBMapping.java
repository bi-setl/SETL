package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import controller.DirectMapping;
import controller.RMLProcessor;
import helper.DBConnection;
import helper.Methods;
import model.DBRDF;
import model.DBTable;
import model.ForeignKey;

public class DBMapping {
	Connection connection;

	public String performDirectMapping(String dbName, String username, String password, String baseIRI,
			String targetPath) {
		// TODO Auto-generated method stub
		Methods methods = new Methods();
		String startTimeString = methods.getCurrentTime();
		
		DBConnection dbConnection = new DBConnection();
		connection = dbConnection.getConnection(dbName, username, password);

		if (connection != null) {
			ArrayList<DBTable> allDBTableStructures = getAllDBTableStructure();
			DirectMapping directMapping = new DirectMapping(this);
			ArrayList<DBRDF> directRDFMappings = directMapping.getDirectMapping(baseIRI, allDBTableStructures);

			String rdfTripleMap = "@prefix rr: <http://www.w3.org/ns/r2rml#>.\n";
			rdfTripleMap += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n";
			rdfTripleMap += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n";
			rdfTripleMap += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n";
			rdfTripleMap += "@prefix base: <" + baseIRI + ">.\n";

			for (DBRDF dbRDF : directRDFMappings) {
				ArrayList<String> rdfTriples = dbRDF.getRdfTriples();

				for (String triple : rdfTriples) {
					rdfTripleMap += triple + "\n";
				}
			}
			
			if (new Methods().writeText(targetPath, rdfTripleMap)) {
				String endTimeString = methods.getCurrentTime();
				return startTimeString + "\nMapping successful.\nFile saved: " + targetPath + "\n" + endTimeString;
			} else {
				return "Error. Please try again";
			}
		} else {
			return "Couldn't establish db connection";
		}
	}
	
	public String performRMLMapping(String dbName, String username, String password, String rmlPath,
			String targetPath) {
		// TODO Auto-generated method stub
		Methods methods = new Methods();
		String startTimeString = methods.getCurrentTime();
		
		DBConnection dbConnection = new DBConnection();
		connection = dbConnection.getConnection(dbName, username, password);

		if (connection != null) {
			RMLProcessor rmlProcessor = new RMLProcessor(this);
			ArrayList<String> lines = rmlProcessor.readRML(rmlPath);
			ArrayList<String> rdfTriples = rmlProcessor.getRDFTriples(lines);
			
			String rdfString = "";
			
			for(String rdfTriplesString: rdfTriples){
				rdfString+=rdfTriplesString+"\n";							
			}
			
			if (new Methods().writeText(targetPath, rdfString)) {
				String endTimeString = methods.getCurrentTime();
				return startTimeString + "\nMapping successful.\nFile saved: " + targetPath + "\n" + endTimeString;
			} else {
				return "Error. Please try again";
			}
		} else {
			return "Couldn't establish db connection";
		}
	}

	public ArrayList<DBTable> getAllDBTableStructure() {

		ArrayList<String> tableNames, primaryKeys, foreignKeyNames, dataValues;
		ArrayList<ForeignKey> foreignKeys;
		ArrayList<DBTable> tableConfigurations = new ArrayList<>();

		tableNames = getAllTableNames();

		for (String tableName : tableNames) {
			primaryKeys = getPrimaryKey(tableName);
			foreignKeys = new ArrayList<>();
			dataValues = getAllDataColumnNames(tableName);
			foreignKeyNames = getAllForeignKeyNames(tableName);

			// remove the duplicate elements
			ArrayList<String> duplicates = new ArrayList<String>();

			for (String temp : dataValues) {
				if (primaryKeys.contains(temp) || foreignKeyNames.contains(temp))
					duplicates.add(temp);
			}

			for (int i = 0; i < duplicates.size(); i++) {
				dataValues.remove(duplicates.get(i));
			}

			ArrayList<ArrayList<String>> tempForeignKeys = getAllForeignKeys(tableName);

			int size = tempForeignKeys.size();
			for (int i = 0; i < size; i++) {

				String tName = tempForeignKeys.get(i).get(1);
				ArrayList<String> sourceCols = new ArrayList<>();
				ArrayList<String> targetCols = new ArrayList<>();
				sourceCols.add(tempForeignKeys.get(i).get(0));
				targetCols.add(tempForeignKeys.get(i).get(2));

				if (i + 1 < size) {
					while (tempForeignKeys.get(i + 1).get(1).equals(tName)) {
						i = i + 1;
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

	public ArrayList<String> getAllTableNames() {
		ArrayList<String> allTables = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE' ORDER BY table_name ASC;");

			while (resultSet.next()) {
				allTables.add(resultSet.getString(1));
			}

			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allTables;
	}

	public ArrayList<String> getPrimaryKey(String tableName) {
		ArrayList<String> primaryKeys = new ArrayList<>();

		try {
			Statement statement = connection.createStatement();

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

	private ArrayList<String> getAllDataColumnNames(String tableName) {
		ArrayList<String> allDataColumns = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"select column_name from information_schema.columns where table_name='" + tableName + "';");

			while (resultSet.next()) {
				allDataColumns.add(resultSet.getString(1));
			}

			resultSet.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allDataColumns;
	}

	private ArrayList<ArrayList<String>> getAllForeignKeys(String tableName) {

		ArrayList<ArrayList<String>> foreignKeys = new ArrayList<>();

		try {
			Statement statement = connection.createStatement();

			String query = "SELECT conrelid::regclass AS FK_Table, "
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), 14, position(')' in pg_get_constraintdef(c.oid))-14) END AS FK_Column ,"
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), position(' REFERENCES ' in pg_get_constraintdef(c.oid))+12, position('(' in substring(pg_get_constraintdef(c.oid), 14))-position(' REFERENCES ' in pg_get_constraintdef(c.oid))+1) END AS PK_Table ,"
					+ "CASE WHEN pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' THEN substring(pg_get_constraintdef(c.oid), position('(' in substring(pg_get_constraintdef(c.oid), 14))+14, position(')' in substring(pg_get_constraintdef(c.oid), position('(' in substring(pg_get_constraintdef(c.oid), 14))+14))-1) END AS PK_Column "
					+ "FROM   pg_constraint c JOIN   pg_namespace n ON n.oid = c.connamespace "
					+ "WHERE  contype IN ('f', 'p ')  AND conrelid::regclass::text = '" + tableName
					+ "' AND pg_get_constraintdef(c.oid) LIKE 'FOREIGN KEY %' "
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
			// System.out.println("Size of Foreign Keys: " +foreignKeys.size());
			return foreignKeys;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foreignKeys;
	}

	private ArrayList<String> getAllForeignKeyNames(String tableName) {

		ArrayList<String> foreignKeys = new ArrayList<>();

		try {
			Statement statement = connection.createStatement();

			String query = "select tc.table_schema, tc.table_name, kc.column_name"
					+ " from  information_schema.table_constraints tc,  information_schema.key_column_usage kc  "
					+ "where tc.constraint_type = 'FOREIGN KEY' and kc.table_name = '" + tableName
					+ "' and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name "
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

	private HashMap<String, String> getColumnDataTypes(String tableName) {
		HashMap<String, String> columnDataTypes = new HashMap<>();

		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement
					.executeQuery("select column_name, data_type from information_schema.columns where table_name = \'"
							+ tableName + "\';");

			while (resultSet.next()) {

				String colName = resultSet.getString(1).toString();
				String datatype = resultSet.getString(2).toString();

				columnDataTypes.put(colName, datatype);

			}
			resultSet.close();
			statement.close();
			return columnDataTypes;

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Column Datatype Retreiv Failed.");
		}

		return columnDataTypes;
	}

	public ArrayList<ArrayList<Object>> getTableDataByColumns(String tableName, ArrayList<String> columns) {

		ArrayList<ArrayList<Object>> tableRows = new ArrayList<>();

		String query = "SELECT " + columns.get(0);
		int numOfCols = columns.size();
		for (int i = 1; i < numOfCols; i++)
			query += ", " + columns.get(i);
		query += " FROM " + tableName + ";";
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				ArrayList<Object> row = new ArrayList<>();

				for (int i = 1; i <= columns.size(); i++) {
					row.add(resultSet.getObject(i));
				}

				tableRows.add(row);
			}

			resultSet.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tableRows;

	}
}
