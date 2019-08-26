package controller;

import java.util.ArrayList;

import core.DBMapping;
import model.DBRDF;
import model.DBTable;
import model.ForeignKey;
import model.TableData;

public class DirectMapping {
	DBMapping databaseOperations;
	public DirectMapping(DBMapping databaseOperations) {
		// TODO Auto-generated constructor stub
		this.databaseOperations = databaseOperations;
	}

	public ArrayList<DBRDF> getDirectMapping(String baseURL, ArrayList<DBTable> allTableStructure) {

		ArrayList<DBRDF> directMapping = new ArrayList<>();
		ArrayList<TableData> allTableData = new ArrayList<>();

		// For Each Table Of DB
		for (DBTable dbTable : allTableStructure) {

			ArrayList<ArrayList<Object>> tableRows;

			String tableName = dbTable.getTableName();

			ArrayList<String> columnNames = new ArrayList<>();

			ArrayList<String> primaryKeys = dbTable.getPrimaryKeys();
			ArrayList<ForeignKey> foreignKeys = dbTable.getForeignKeys();
			ArrayList<String> dataColumnNames = dbTable.getDataColumns();
			ArrayList<String> foreignKeyNames = new ArrayList<>();
			
			
			for (ForeignKey fk : foreignKeys) {
				ArrayList<String> sourceNames = fk.getColumnNames();
				for (String temp : sourceNames) {
					foreignKeyNames.add(temp);
				}
			}

			for (String colName : primaryKeys) {
				columnNames.add(colName);

			}

			for (String colName : foreignKeyNames) {
				columnNames.add(colName);

			}

			for (String colName : dataColumnNames) {
				columnNames.add(colName);

			}

			tableRows = databaseOperations.getTableDataByColumns(tableName, columnNames);

			allTableData.add(new TableData(tableName, columnNames, tableRows));

		}

		directMapping = getRDFTriples(allTableStructure, allTableData);
		return directMapping;

	}

	private ArrayList<DBRDF> getRDFTriples(ArrayList<DBTable> allTableStructures, ArrayList<TableData> allTableData){
		
		ArrayList<DBRDF> tablesRDF = new ArrayList<>();
		
		
		for(TableData tableData:allTableData){	
			
//			if(!tableData.getTableName().equals("staff"))
//				continue;
			
			String tableName = tableData.getTableName();
			ArrayList<String> columnNames = tableData.getColumnNames();
			ArrayList<String> tableRDFTriples = new ArrayList<>();
			
			
			DBTable tableStructure = getDBTableStrucureObject(tableName, allTableStructures);
			
			ArrayList<ForeignKey> foreignKeys = tableStructure.getForeignKeys();
			
			int numOfPrimarKeys = tableStructure.getPrimaryKeys().size();
			int numOfForeignKeys  = foreignKeys.size();
			int numOfDataColumns = tableStructure.getDataColumns().size();
			
			
			//int test = 1;
			
			for(ArrayList<Object> tableRow: tableData.getColumnValues()){
				
				String subjectNode = "<"+tableName+"/";
				String propertyNode = "<";
				String objectNode = "<";
				
//				if(test>10)
//					break;
//				test++;
				
				
				//Generating the subject Node
				if(numOfPrimarKeys<=0){
					subjectNode = "_a:";
				}else{
					
					for(int i=0; i<numOfPrimarKeys; i++){
						
						String temp = columnNames.get(i);
						if(i==0){
							subjectNode+=temp+"="+tableRow.get(i);
						}else{
							
							subjectNode+=";"+temp+"="+tableRow.get(i);
						}
						
					}
					subjectNode+=">";
					
				}
				//Subject Node Completed
				
				String tableInitial = subjectNode + " rdf:type <" +tableName + ">.";  
				tableRDFTriples.add(tableInitial);
				
		
				//Generating RDF Triples for primary key values
				int j;
				for(j = 0; j<numOfPrimarKeys; j++){
					
					String triple = subjectNode;
					triple += " <"+tableName+"#"+columnNames.get(j)+"> " + tableRow.get(j)+".";
					tableRDFTriples.add(triple);
				}
				
				
				for(ForeignKey fk:foreignKeys){
					
					int fkNum = 0;
					
					ArrayList<String> fkNames = fk.getColumnNames();
					
					String foreignKeyREFTriple = subjectNode+ " <" + tableName+"#ref-";
					
					ArrayList<Object> fkNameValues = new ArrayList<>();
					
					for(fkNum=0; fkNum < fkNames.size(); fkNum++){
						
						
						String fkName = fkNames.get(fkNum);
						String triple = subjectNode;
						triple+= " <"+tableName+"#"+fkName+"> " + tableRow.get(j)+ ".";
						
						if(fkNum==0)
							foreignKeyREFTriple+=fkName;
						else {
							foreignKeyREFTriple+=";"+fkName;
						}
						
						fkNameValues.add(fkName);
						fkNameValues.add(tableRow.get(j));
						
						j++;
						
						tableRDFTriples.add(triple);
						
					}
					
					foreignKeyREFTriple+="> <"+fk.getTableName()+"/";
					
					TableData targetTableData = getTableData(fk.getTableName(), allTableData);
					
					ArrayList<String> targetColumnNames = fk.getTargetTableColumnNames();
					
					ArrayList<Object> primaryKeyValues = getPrimaryKeyValues(targetColumnNames, fkNameValues, targetTableData);
					
					for(int l=0; l<fk.getTargetTableColumnNames().size(); l++){
						
						if(l==0){
							
							foreignKeyREFTriple+=targetColumnNames.get(l)+"="+primaryKeyValues.get(l);
							
						}else{
							
							foreignKeyREFTriple+=";"+targetColumnNames.get(l)+"="+primaryKeyValues.get(l);
						}
					}
					
					foreignKeyREFTriple+=">.";
					tableRDFTriples.add(foreignKeyREFTriple);
					
				}
				
				//Generating RDF Triples for 
				
				
				for(int loopIndex = 0; loopIndex<numOfDataColumns; loopIndex++){
					
					String triple = subjectNode;
					triple += " <"+tableName+"#"+columnNames.get(j)+"> " + tableRow.get(j)+".";
					tableRDFTriples.add(triple);
					
					j++;
				}
				
			}
			
			tablesRDF.add(new DBRDF(tableName, tableRDFTriples));
			
		}
		
		return tablesRDF;
		
	}

	// Return the PK values for given FK values

	ArrayList<Object> getPrimaryKeyValues(ArrayList<String> pkNames, ArrayList<Object> fkNameValues,
			TableData targetTableData) {

		ArrayList<Object> pkValues = new ArrayList<>();

		//all column names of the table
		ArrayList<String> columnNames = targetTableData.getColumnNames();
		
		ArrayList<ArrayList<Object>> rowValues = targetTableData.getColumnValues();

		for (ArrayList<Object> row : rowValues) {

			boolean found = true;
			
			for (int i = 0; i < fkNameValues.size(); i += 2) {

				int index = columnNames.indexOf(pkNames.get(i));
				
				if (!fkNameValues.get(i + 1).equals(row.get(index))) {
					found = false;
					break;
				}
			}

			if (found) {

				for (String pkName : pkNames) {

					int index = columnNames.indexOf(pkName);
					pkValues.add(row.get(index));

				}

				return pkValues;
			}

		}

		return pkValues;

	}

	private String getPercentEncodedString(String string) {

		// string = string.trim();
		String str = string.replace(" ", "%20");
		return str;

	}

	private DBTable getDBTableStrucureObject(String tableName, ArrayList<DBTable> allDBTableStructure) {
		DBTable dbTable = new DBTable();

		for (DBTable temp : allDBTableStructure) {

			if (tableName.equals(temp.getTableName())) {
				dbTable = temp;
				break;
			}
		}

		return dbTable;
	}

	private TableData getTableData(String tableName, ArrayList<TableData> allTableData) {

		TableData tableData = new TableData();

		for (TableData temp : allTableData) {

			if (tableName.equals(temp.getTableName())) {
				tableData = temp;
				break;
			}
		}

		return tableData;

	}

}
