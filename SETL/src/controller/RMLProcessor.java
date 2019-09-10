package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.PrimitiveIterator.OfDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.RequestingUserName;
import javax.security.auth.Subject;
import javax.swing.JOptionPane;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.apache.jena.sparql.function.library.substring;

import com.github.jsonldjava.utils.Obj;

import core.DBMapping;
import model.RMLTriplesMap;

public class RMLProcessor {
	DBMapping databaseOperations;
	
	public RMLProcessor(DBMapping databaseOperations) {
		super();
		this.databaseOperations = databaseOperations;
	}


	public ArrayList<String> readRML(String filePath) {

		BufferedReader csvBufferReader;
		String line = "";
		ArrayList<String> lines = new ArrayList<String>();

		try {
			csvBufferReader = new BufferedReader(new FileReader(filePath));

			try {
				while ((line = csvBufferReader.readLine()) != null) {
					lines.add(line);
				}

			} catch (IOException e) {

				JOptionPane.showMessageDialog(null, "RML File Reading Failed");
			}

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, filePath + " not found!");
		}

		// String s = "";
		// for(String r: rows){
		// s+=r+"\n";
		// }
		// System.out.println(s);
		// JOptionPane.showMessageDialog(null, s);
		return lines;
	}

	
	public ArrayList<String> getRDFTriples(ArrayList<String> lines) {

		ArrayList<String> rdfTriples = new ArrayList<>();
		ArrayList<RMLTriplesMap> rmlTriplesMaps = new ArrayList<>();

		//ArrayList<String> lines = readRML(filePath);

		
		ArrayList<String> prefixList = new ArrayList<>();
		
		String rmlFileString = "";
		for (String line : lines) {
			if(line.contains("@prefix"))
				prefixList.add(line);
			rmlFileString += line;
		}

		ArrayList<String> tripleMaps = getStringTripleMaps(rmlFileString);
	
		// generating the TripleMaps; // problems lies here

		if (tripleMaps.size() <= 0) {
			return new ArrayList<String>();
		}
		
		for (String tripleMap : tripleMaps) {

			//System.out.println("TRIPLE MAP: " + tripleMap);
			
			String regEx = "<#TriplesMap(.*?)>";
			String tripleMapName = getMatchedString(regEx, tripleMap, 0);
			//System.out.println(tripleMapName);
			// Splitting the
			// System.out.print("TABLE NAME: ");
			regEx = "rr:tableName\\s\"(.*?)\"";
			String tableName = getMatchedString(regEx, tripleMap, 1);
			// System.out.println(tableName);
			regEx = "rr:subjectMap\\s(.*?)(\\s)*\\][.;]";
			// System.out.println("SUBJECT MAP");
			String subjectMap = getMatchedString(regEx, tripleMap, 0);
			regEx = "rr:predicateObjectMap\\s(.*?)\\](\\s)*;\\s][;.]";
			ArrayList<String> predicateObjectMaps = getAllMatchedStrings(regEx, tripleMap, 0);
			ArrayList<String> columnNames = getColumns(subjectMap, predicateObjectMaps);

			rmlTriplesMaps
					.add(new RMLTriplesMap(tripleMapName, tableName, subjectMap, predicateObjectMaps, columnNames));

		}

		rdfTriples = getRDFTriplesFromRML(prefixList,rmlTriplesMaps);

//		 System.out.println("TRIPLE MAPS:");
//			 for(RMLTriplesMap tripleMap: rmlTriplesMaps){
//	 tripleMap.printTriplesMap();
//		 }
//		
//		for(String rdfTriple: rdfTriples){
//			
//			System.out.println(rdfTriple);
//		}
		
		return rdfTriples;

	}

	private ArrayList<String> getRDFTriplesFromRML(ArrayList<String> prefixList, ArrayList<RMLTriplesMap> rmlTriplesMaps) {

		ArrayList<String> rdfTriples = new ArrayList<>();
		
		for(String prefix: prefixList){
			rdfTriples.add(prefix);
		}

		// extract dataValues from DB
		// initialize the triplesMaps
		for (RMLTriplesMap rmlTriplesMap : rmlTriplesMaps) {

			String tableName = rmlTriplesMap.getTableName();
			ArrayList<String> columns = rmlTriplesMap.getColumnNames();
			// System.out.println("TripleName : "+
			// rmlTriplesMap.getTriplesMapName()+" Num of Columns: " +
			// columns.size());

			// rmlTriplesMap.printTriplesMap();
			ArrayList<ArrayList<Object>> columnValues = databaseOperations.getTableDataByColumns(tableName, columns);
			rmlTriplesMap.setColumnValues(columnValues);
		}
		//End of initializations
		
		int totalRow = 0;
		int numOfCol = 0;
		// generating rdf triples
		for (RMLTriplesMap triplesMap : rmlTriplesMaps) {

			//System.out.println("\nTRIPLE MAP: " + triplesMap.getTriplesMapName()+"\n");
			ArrayList<ArrayList<Object>> columnValues = triplesMap.getColumnValues();
			ArrayList<String> columnNames = triplesMap.getColumnNames();

			
			numOfCol += columnValues.get(0).size();
			
			// System.out.println("Subject Template: "+subjectTemplate);

			int numOfRows = columnValues.size();
			totalRow+=numOfRows;
			
			/**
			 * Here modified the code for number of row...
			 * i < 500 && 
			 * 
			 * 
			 */
			
			for (int i = 0; i<numOfRows; i++) { // numOfRows
				
				//generating the subject node
				String subjectMap = triplesMap.getSubjectMap();
				String regEx = "\\{(.*?)\\}";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(subjectMap);
				int numOfPKColumns = 0;
				while (matcher.find()) {
					numOfPKColumns++;
				}

				regEx = "rr:template\\s\"(.*?)\";";
				String subjectTemplate = getMatchedString(regEx, subjectMap, 1);
				
				ArrayList<Object> rowValue = columnValues.get(i);

				for (int index = 0; index < numOfPKColumns; index++) {

					Object cellValue = rowValue.get(index);
					// System.out.println("Cell values: " + cellValue);

					if (cellValue instanceof Integer) {

						String src = "{" + columnNames.get(index) + "}";
						String target = cellValue.toString();
						//System.out.println("SRC = \"" +src +"\" TARGET = "+target);
						subjectTemplate = subjectTemplate.replace(src, target);

					} else {
						String src = "{" + columnNames.get(index) + "}";
						String target = "\"" + cellValue.toString() + "\"";

						subjectTemplate = subjectTemplate.replace(src, target);
					}

				}

				String subjectNode = "<" + subjectTemplate + ">";
				// System.out.println("Subject Node: " + subjectNode);
				String predicateNode = "rdf:type";
				String objectNode = "";
				String triple = "";

				if (subjectMap.contains("rr:class")) {
					regEx = "rr:class\\s(.*?)(\\s*?);";
					objectNode = getMatchedString(regEx, subjectMap, 1);
					triple = subjectNode + " " + predicateNode + " " + objectNode + ".";
					rdfTriples.add(triple);
					//System.out.println(triple);

				}

				ArrayList<String> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				int numOfPredicateObjectMap = predicateObjectMaps.size();

				for (int mapNumber = 0; mapNumber < numOfPredicateObjectMap; mapNumber++) {

					String predicateObjectMap = predicateObjectMaps.get(mapNumber);
					regEx = "(rr:predicate|rr:predicateMap)\\s(.*?);";
					String predicateMap = getMatchedString(regEx, predicateObjectMap, 0);
					predicateMap = predicateMap.replace("\t", " ");
					regEx = "(rr:object|rr:objectMap)\\s(.*?)](;|.)";

					String objectMap = getMatchedString(regEx, predicateObjectMap, 0);
					objectMap = objectMap.replace("\t", " ");
					// System.out.println("Predicate Map:
					// "+predicateMap+"\nObject Map: " + objectMap);

					// generating the predicate node
					if (containSubstring("[(.*?)]", predicateMap)) {

						// System.out.println("predicate map contains [] " );

						predicateMap = getMatchedString("[(.*?)]", predicateMap, 1);
						String predicate = getMatchedString("rr:(.*?)\\s(.*?);", predicateMap, 2);
						predicateNode = predicate;

						// System.out.println("Predicate Node: " + predicate);

					} else {

						// System.out.println("predicate map doesn't contain []
						// " );

						// predicateMap = getMatchedString("[(.*?)]",
						// predicateMap, 1);
						String predicate = getMatchedString("rr:predicate(Map?)\\s(.*?);", predicateMap, 2);
						predicateNode = predicate;
						// System.out.println("Predicate Node: " + predicate);
					}

					// generating object node

					if (objectMap.contains("rr:parentTriplesMap")) {

						String parentTriplesMapName = getMatchedString("rr:parentTriplesMap\\s(.*?);", objectMap, 1);

						if (parentTriplesMapName.equals("null")) {
							continue;
						} else {

							//String parenTableName = "";
							String parentTableSubjectMap = "";

							for (RMLTriplesMap rmlTriplesMap : rmlTriplesMaps) {
								if (rmlTriplesMap.getTriplesMapName().equals(parentTriplesMapName)) {
									//parenTableName = rmlTriplesMap.getTableName();
									parentTableSubjectMap = rmlTriplesMap.getSubjectMap();
								}

							}

							String parentColName = getMatchedString("rr:parent\\s\"(.*?)\";", objectMap, 1);
							String childColName = getMatchedString("rr:child\\s\"(.*?)\";", objectMap, 1);

							
							regEx = "\\{(.*?)\\}";
							ArrayList<String> pkColumnNames = getAllMatchedStrings(regEx, parentTableSubjectMap, 1);

							int index = columnNames.indexOf(childColName);
							Object childColValue = rowValue.get(index);
							//System.out.println("Child Col: " +childColName +" Child value: " + childColValue+ " Parent col Name: " + parentColName);
							
							
							RMLTriplesMap parentTriplesMap = getParentRMLTriplesMap(parentTriplesMapName,
									rmlTriplesMaps);

							ArrayList<Object> pkColumnValues = getParentTriplesMapPKValues(parentTriplesMap,
									pkColumnNames, parentColName, childColValue);

							//System.out.println("PK columns: " + pkColumnNames.size() + " PK values: " + pkColumnValues.size());

							regEx = "rr:template\\s\"(.*?)\";";
							String refObjectTemplate = getMatchedString(regEx, parentTableSubjectMap, 1);
							
							int limit = pkColumnValues.size();
							
							//System.out.println("PK column values: " + limit);
							for (int j = 0; j < limit; j++) {
								//
								Object obj = pkColumnValues.get(j);
							
																
								//-------------NEWLY ADDED ----------//
								String objectValueString = obj.toString();
								objectValueString= objectValueString.replace("\"", "\'");
								
								//System.out.println("OBJ: " + obj.toString());
								
								refObjectTemplate = refObjectTemplate.replace("{" + pkColumnNames.get(j) + "}",
										objectValueString);
								
//								refObjectTemplate = refObjectTemplate.replace("{" + pkColumnNames.get(j) + "}",
//										obj.toString());
								
								//System.out.println("Subject Template: " + subjectTemplate);
							}
							
							objectNode = "<" + refObjectTemplate + ">.";

							triple = subjectNode + " " + predicateNode + " " + objectNode;
							//System.out.println(triple);
							rdfTriples.add(triple);

						}

					} else {
						
						//NOT REFERENCING

						String objectValue = "";
						
						if (objectMap.contains("rr:column")) {

							objectValue = getMatchedString("rr:column\\s\"(.*?)\"", objectMap, 1);

							int index = columnNames.indexOf(objectValue);
							Object value = rowValue.get(index);
							
							if (value == null)
								continue;
							
							
							// System.out.println("Object Value: " + objectValue
							// + " index: " + index + " value: " + value);

							if (value instanceof Integer || value instanceof Double || value instanceof Float){
								
								objectNode = value.toString();
								objectNode= objectNode.replace("\"", "\'");
								
							}	
							else{
								
								//----- NEWLY ADDED -----//
								String valueString = value.toString();
								valueString = valueString.replace("\"", "\'");
								objectNode = "\"" + valueString + "\"";
								
								//objectNode = "\"" + value.toString() + "\"";
							}
							
							

						} else if (objectMap.contains("rr:template")) {

							objectValue = getMatchedString("rr:template\\s\"(.*?)\"", objectMap, 1);

							if (!containSubstring("{(.*?)}", objectValue))
								continue;

							String objectColName = getMatchedString("{.*?}", objectValue, 1);
							int index = columnNames.indexOf(objectColName);
							Object value = rowValue.get(index);
							if (value == null)
								continue;
							// System.out.println("Object Value: " + objectValue
							// + " index: " + index + " value: " + value);

							String val = value.toString();
							//------------NEWLY ADDED --------------------
							val = val.replace("\"", "\'");
							
							//objectValue = objectValue.replace(objectValue, "{" + objectColName + "}");
							objectValue = objectValue.replace(objectColName, objectValue);
							
							objectNode = objectValue;

						}
						
						triple = subjectNode + " " + predicateNode + " " + objectNode + ".";
						//System.out.println(triple);
						rdfTriples.add(triple);
					}

				}

			} // end of row processing

		} // end of each triple map
		//System.out.println("Total Number of Cols: " + numOfCol);
		//System.out.println("Total Number of Row: " + totalRow+"\n\n");
		return rdfTriples;
	}

	private RMLTriplesMap getParentRMLTriplesMap(String parentTripleMapName, ArrayList<RMLTriplesMap> rmlTriplesMaps) {

		for (RMLTriplesMap rmlTriplesMap : rmlTriplesMaps) {

			if ((rmlTriplesMap.getTriplesMapName().equals(parentTripleMapName))) {
				return rmlTriplesMap;
			}

		}

		return null;
	}

	ArrayList<Object> getParentTriplesMapPKValues(RMLTriplesMap parentTriplesMap, ArrayList<String> pkColumnNames,
			String parentColumnName, Object childColValue) {

		ArrayList<Object> pkValues = new ArrayList<>();

		ArrayList<String> columnNames = parentTriplesMap.getColumnNames();
		ArrayList<ArrayList<Object>> parentTriplesMapColValues = parentTriplesMap.getColumnValues();

		//System.out.println("SiZE: " + columnNames.size() + " PARENT Tripel Map: "+ parentTriplesMap.getTriplesMapName()+" col Name: \"" + parentColumnName+"\"");
		int indexOfCol = columnNames.indexOf(parentColumnName);

//		System.out.print("Column Names: ");
//		for(String name: columnNames){
//			System.out.print(" \"" + name+"\"");
//			
//		}
//		System.out.println();
		
		//System.out.println("Index of col: " + indexOfCol);
		
		for (ArrayList<Object> row : parentTriplesMapColValues) {

			
			//System.out.println("Child: " +childColValue +" row: " + row.get(indexOfCol));
			
			if (childColValue.toString().equals(row.get(indexOfCol).toString())) {

				for (String pkColumnName : pkColumnNames) {

					//System.out.println("Pk Coumn: " + pkColumnName);
					int index = columnNames.indexOf(pkColumnName);
					//System.out.println("Index = "  +index);
					pkValues.add(row.get(index));
				}
				
				break;
			}

		}

		return pkValues;

	}


	private boolean containSubstring(String regEx, String sourceString) {

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sourceString);

		while (matcher.find()) {
			return true;
		}

		return false;
	}

	ArrayList<String> getColumns(String subjectMap, ArrayList<String> predicateObjectMaps) {

		ArrayList<String> columnNames = new ArrayList<>();

		// regular expressions for primary key columns
		String regEx = "\\{(.*?)\\}";

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(subjectMap);

		while (matcher.find()) {
			columnNames.add(matcher.group(1));
		}

		// extracting column names from predicate Object maps

		for (String predicateObjectMap : predicateObjectMaps) {

			if (predicateObjectMap.contains("rr:parentTriplesMap")) {
				// the predicate object map is a referencing

				regEx = "rr:child\\s\"(.*?)\"";

				pattern = Pattern.compile(regEx);
				matcher = pattern.matcher(predicateObjectMap);

				while (matcher.find()) {
					columnNames.add(matcher.group(1));
				}

			} else {
				// the predicate object map is normal

				if (predicateObjectMap.contains("rr:column")) {

					regEx = "rr:column\\s\"(.*?)\"";

					pattern = Pattern.compile(regEx);
					matcher = pattern.matcher(predicateObjectMap);

					while (matcher.find()) {
						columnNames.add(matcher.group(1));
					}

				} else {

					regEx = "rr:template\\s\"(.*?)\"";

					pattern = Pattern.compile(regEx);
					matcher = pattern.matcher(predicateObjectMap);

					String url = "";
					while (matcher.find()) {
						url = matcher.group(1);
					}

					regEx = "\\{(.*?)\\}";
					pattern = Pattern.compile(regEx);
					matcher = pattern.matcher(url);

					while (matcher.find()) {
						columnNames.add(matcher.group(1));
					}

				}

			}
		}

		return columnNames;
	}

	ArrayList<String> getPrimaryKeys(String subjectMap) {

		ArrayList<String> pkNames = new ArrayList<>();

		int numOfPKcolumns = 0;

		return pkNames;
	}

	ArrayList<String> getAllMatchedStrings(String regEx, String sourceString, int group) {

		ArrayList<String> s = new ArrayList<>();

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sourceString);

		while (matcher.find()) {
			s.add(matcher.group(group));
		}

		return s;

	}

	String getMatchedString(String regEx, String sourceString, int group) {

		String s = "";

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sourceString);

		while (matcher.find()) {
			s = matcher.group(group);
			return s;
		}
		return s;
	}

	ArrayList<String> getStringTripleMaps(String rmlString) {

		// System.out.println("RML Triple Map\n"+rmlString);
		rmlString = rmlString.replaceAll("\t", " ");
		ArrayList<String> stringTripleMaps = new ArrayList<>();

		try {

			// String regEx = "<#TriplesMap";
			String regEx = "<#TriplesMap(.*?)>\\s(.*?)]\\.";

			// ArrayList<Integer> startingIndexes =
			// getStartingIndexes(rmlString, regEx);
			// stringTripleMaps = getSubStrings(startingIndexes, rmlString);
			stringTripleMaps = getAllMatchedStrings(regEx, rmlString, 0);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "RML file parsing failed.");

		}
		return stringTripleMaps;
	}

	ArrayList<String> getSubStrings(ArrayList<Integer> startingIndexes, String sourceString) {

		ArrayList<String> subStrings = new ArrayList<>();

		int numOfIndexes = startingIndexes.size();
		int sizeOfSourceString = sourceString.length();
		String subStr;

		for (int i = 0; i < numOfIndexes - 1; i++) {

			subStr = sourceString.substring(startingIndexes.get(i), startingIndexes.get(i + 1));
			subStr.trim();
			subStrings.add(subStr);
		}

		subStr = sourceString.substring(startingIndexes.get(numOfIndexes - 1), sizeOfSourceString - 1);
		subStr.replace("\t", " ");
		subStr.trim();
		subStrings.add(subStr);

		return subStrings;

	}

	ArrayList<Integer> getStartingIndexes(String inputString, String regEx) {

		ArrayList<Integer> indexes = new ArrayList<>();

		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(inputString);

		while (matcher.find()) {

			// System.out.printf("I found the text \"%s\" starting at index %d
			// and ending at index %d\n",
			// matcher.group(), matcher.start(), matcher.end());

			indexes.add(matcher.start());

		}

		return indexes;

	}
}
