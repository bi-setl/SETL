package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import helper.Methods;

public class LevelEntryNew {
	private Methods fileMethods;
	private LinkedHashMap<String, String> prefixMap;
	private LinkedHashMap<String, String> keyAttributesMap;
	
	public LevelEntryNew() {
		// TODO Auto-generated constructor stub
		fileMethods = new Methods();
		prefixMap = new LinkedHashMap<>();
		keyAttributesMap = new LinkedHashMap<>();
	}
	
	/*public static void main(String[] args) {
		LevelEntryNew levelEntryNew = new LevelEntryNew();
		
		levelEntryNew.generateFactEntryFromCSV("C:\\Users\\Amrit\\Documents\\Census\\C06\\Census_06.csv",
				"C:\\Users\\Amrit\\Documents\\Census\\map_version_1559666299853.ttl",
				"C:\\Users\\Amrit\\Documents\\bd_tbox.ttl",
				"C:\\Users\\Amrit\\Documents\\Census\\prov.ttl",
				"C:\\Users\\Amrit\\Documents\\Census\\190805_044321_TargetABox.ttl",
				"Space ( )");
	}*/
	
	public String generateLevelEntry(String sourceABoxFile, String mappingFile, String targetTBoxFile, String provGraphFile,
			String targetABoxFile) {
		String checkFileResult = checkFiles(sourceABoxFile, mappingFile, targetTBoxFile, provGraphFile);
		if (checkFileResult.equals("OK")) {
			Model sourceABoxModel = fileMethods.readModelFromPath(sourceABoxFile);
			
			if (sourceABoxModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}
			
			Model mapModel = fileMethods.readModelFromPath(mappingFile);
			
			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}
			
			Model targetTBoxModel = fileMethods.readModelFromPath(targetTBoxFile);
			
			if (targetTBoxModel == null) {
				return "Error in reading the target tbox file. Please check syntaxes.";
			}
			
			Model provModel = fileMethods.readModelFromPath(provGraphFile);
			
			if (provModel == null) {
				return "Error in reading the prov graph file. Please check syntaxes.";
			}
			
			// ProvGraph provGraph = new ProvGraph(provGraphFile);
			prefixMap = extractAllPrefixes(targetTBoxFile);
			keyAttributesMap = new LinkedHashMap<>();
			
			Model model = ModelFactory.createDefaultModel();
			model.add(sourceABoxModel);
			model.add(mapModel);
			model.add(targetTBoxModel);
			
			Model targetModel = ModelFactory.createDefaultModel();
			
			String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?head a map:ConceptMapper. "
					+ "?head map:sourceConcept ?type. "
					+ "?head map:targetConcept ?target. "
					+ "?head map:iriValueType ?keyType. "
					+ "?target a qb4o:LevelProperty. "
					+ "?record a map:PropertyMapper. "
					+ "?record map:ConceptMapper ?head. "
					+ "?record map:sourceProperty ?property. "
					+ "?record map:targetProperty ?targetProperty. "
					+ "?subject a ?type. "
					+ "?subject ?property ?object. "
					+ "}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			// fileMethods.printResultSet(resultSet);
			LinkedHashMap<String, String> provHashMap = new LinkedHashMap<>();
			
			int numOfFiles = 1, count = 0;
			while (resultSet.hasNext()) {
                QuerySolution querySolution = (QuerySolution) resultSet.next();
                
                String concept = querySolution.get("head").toString();
                // String sourceType = querySolution.get("type").toString();
                String targetType = querySolution.get("target").toString();
                String iriValueType = querySolution.get("keyType").toString();
                // String mapper = querySolution.get("record").toString();
                // String sourceProperty = querySolution.get("property").toString();
                String targetProperty = querySolution.get("targetProperty").toString();
                String subject = querySolution.get("subject").toString();
                String object = querySolution.get("object").toString();
                
                if (provHashMap.containsKey(subject)) {
                	String provIRI = provHashMap.get(subject);
                	Resource resource = targetModel.createResource(provIRI);
					
					Property property = targetModel.createProperty(targetProperty);
					String rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
				} else {
					String provValue = getIRIValue(iriValueType, concept, mapModel, subject, sourceABoxModel, provModel);
					
					String provIRI = "";
					/*if (provValue == null) {
						// AUTOMATIC
						return "Empty Provinance value";
					} else {
						// LOOK UP PROV GRAPH
						String rangeValue = getRangeValue(targetType, targetTBoxModel);
						
						if (rangeValue == null) {
							provIRI = targetType + "#" + provValue;
						} else {
							provIRI = rangeValue + "#" + provValue;
						}
					}*/
					
					if (provValue == null) {
						provValue = getProvValue(subject);
					}
					
					String rangeValue = getRangeValue(targetType, targetTBoxModel);
					
					if (rangeValue == null) {
						provIRI = targetType + "#" + provValue;
					} else {
						provIRI = rangeValue + "#" + provValue;
					}
					
					
					Resource resource = targetModel.createResource(provIRI);
					
					Property property2 = targetModel.createProperty("http://purl.org/qb4olap/cubes#memberOf");
					resource.addProperty(property2, targetModel.createResource(targetType));

					resource.addProperty(RDF.type, ResourceFactory.createResource("http://purl.org/qb4olap/cubes#LevelMember"));
					
					Property property = targetModel.createProperty(targetProperty);
					rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					// System.out.println("Target Property: " + targetProperty);
					// System.out.println("Range: " + rangeValue);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
					
					count++;
					provHashMap.put(subject, provIRI);
				}
                
                if (count % 1000 == 0) {
					String tempPath = numOfFiles + ".ttl";
					// System.out.println(tempPath);
					fileMethods.saveModel(targetModel, tempPath);
					targetModel = ModelFactory.createDefaultModel();
					numOfFiles++;
				}
            }
			
			if (count == 0) {
				return "Sparql Query returns 0 result";
			}

			if (targetModel.size() > 0) {
				String tempPath = numOfFiles + ".ttl";
				// System.out.println(tempPath);
				fileMethods.saveModel(targetModel, tempPath);
				targetModel = ModelFactory.createDefaultModel();
				numOfFiles++;
			}
			
			// fileMethods.saveModel(provGraph.model, provGraphFile);
			return mergeAllTempFiles(numOfFiles, targetABoxFile);
		} else {
			return checkFileResult;
		}
	}
	
	public String generateInstanceEntry(String sourceABoxFile, String mappingFile, String targetTBoxFile, String provGraphFile,
			String targetABoxFile) {
		String checkFileResult = checkFiles(sourceABoxFile, mappingFile, targetTBoxFile, provGraphFile);
		if (checkFileResult.equals("OK")) {
			Model sourceABoxModel = fileMethods.readModelFromPath(sourceABoxFile);
			
			if (sourceABoxModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}
			
			Model mapModel = fileMethods.readModelFromPath(mappingFile);
			
			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}
			
			Model targetTBoxModel = fileMethods.readModelFromPath(targetTBoxFile);
			
			if (targetTBoxModel == null) {
				return "Error in reading the target tbox file. Please check syntaxes.";
			}
			
			Model provModel = fileMethods.readModelFromPath(provGraphFile);
			
			if (provModel == null) {
				return "Error in reading the prov graph file. Please check syntaxes.";
			}
			
			// ProvGraph provGraph = new ProvGraph(provGraphFile);
			prefixMap = extractAllPrefixes(targetTBoxFile);
			keyAttributesMap = new LinkedHashMap<>();
			
			Model model = ModelFactory.createDefaultModel();
			model.add(sourceABoxModel);
			model.add(mapModel);
			model.add(targetTBoxModel);
			
			Model targetModel = ModelFactory.createDefaultModel();
			
			String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?head a map:ConceptMapper. "
					+ "?head map:sourceConcept ?type. "
					+ "?head map:targetConcept ?target. "
					+ "?head map:iriValueType ?keyType. "
					+ "?target a owl:Class. "
					+ "?record a map:PropertyMapper. "
					+ "?record map:ConceptMapper ?head. "
					+ "?record map:sourceProperty ?property. "
					+ "?record map:targetProperty ?targetProperty. "
					+ "?subject a ?type. "
					+ "?subject ?property ?object. "
					+ "}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			// fileMethods.printResultSet(resultSet);
			LinkedHashMap<String, String> provHashMap = new LinkedHashMap<>();
			
			int numOfFiles = 1, count = 0;
			while (resultSet.hasNext()) {
                QuerySolution querySolution = (QuerySolution) resultSet.next();
                
                String concept = querySolution.get("head").toString();
                String targetType = querySolution.get("target").toString();
                String iriValueType = querySolution.get("keyType").toString();
                String targetProperty = querySolution.get("targetProperty").toString();
                String subject = querySolution.get("subject").toString();
                String object = querySolution.get("object").toString();
                
                if (provHashMap.containsKey(subject)) {
                	String provIRI = provHashMap.get(subject);
                	Resource resource = targetModel.createResource(provIRI);
					
					Property property = targetModel.createProperty(targetProperty);
					String rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
				} else {
					String provValue = getIRIValue(iriValueType, concept, mapModel, subject, sourceABoxModel, provModel);
					
					String provIRI = "";
					
					if (provValue == null) {
						provValue = getProvValue(subject);
					}
					
					String rangeValue = getRangeValue(targetType, targetTBoxModel);
					
					if (rangeValue == null) {
						provIRI = targetType + "#" + provValue;
					} else {
						provIRI = rangeValue + "#" + provValue;
					}
					
					
					Resource resource = targetModel.createResource(provIRI);

					resource.addProperty(RDF.type, targetModel.createResource(targetType));
					
					Property property = targetModel.createProperty(targetProperty);
					rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
					
					count++;
					provHashMap.put(subject, provIRI);
				}
                
                if (count % 1000 == 0) {
					String tempPath = numOfFiles + ".ttl";
					// System.out.println(tempPath);
					fileMethods.saveModel(targetModel, tempPath);
					targetModel = ModelFactory.createDefaultModel();
					numOfFiles++;
				}
            }
			
			if (count == 0) {
				return "Sparql Query returns 0 result";
			}

			if (targetModel.size() > 0) {
				String tempPath = numOfFiles + ".ttl";
				// System.out.println(tempPath);
				fileMethods.saveModel(targetModel, tempPath);
				targetModel = ModelFactory.createDefaultModel();
				numOfFiles++;
			}
			
			// fileMethods.saveModel(provGraph.model, provGraphFile);
			return mergeAllTempFiles(numOfFiles, targetABoxFile);
		} else {
			return checkFileResult;
		}
	}
	
	public String generateFactEntry(String sourceABoxFile, String mappingFile, String targetTBoxFile, String provGraphFile,
			String targetABoxFile) {
		String checkFileResult = checkFiles(sourceABoxFile, mappingFile, targetTBoxFile, provGraphFile);
		if (checkFileResult.equals("OK")) {
			Model sourceABoxModel = fileMethods.readModelFromPath(sourceABoxFile);
			
			if (sourceABoxModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}
			
			Model mapModel = fileMethods.readModelFromPath(mappingFile);
			
			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}
			
			Model targetTBoxModel = fileMethods.readModelFromPath(targetTBoxFile);
			
			if (targetTBoxModel == null) {
				return "Error in reading the target tbox file. Please check syntaxes.";
			}
			
			Model provModel = fileMethods.readModelFromPath(provGraphFile);
			
			if (provModel == null) {
				return "Error in reading the prov graph file. Please check syntaxes.";
			}
			
			// ProvGraph provGraph = new ProvGraph(provGraphFile);
			prefixMap = extractAllPrefixes(targetTBoxFile);
			keyAttributesMap = new LinkedHashMap<>();
			
			Model model = ModelFactory.createDefaultModel();
			model.add(sourceABoxModel);
			model.add(mapModel);
			model.add(targetTBoxModel);
			
			Model targetModel = ModelFactory.createDefaultModel();
			
			String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?head a map:ConceptMapper. "
					+ "?head map:sourceConcept ?type. "
					+ "?head map:targetConcept ?target. "
					+ "?head map:iriValueType ?keyType. "
					+ "?target a qb:DataSet. "
					+ "?record a map:PropertyMapper. "
					+ "?record map:ConceptMapper ?head. "
					+ "?record map:sourceProperty ?property. "
					+ "?record map:targetProperty ?targetProperty. "
					+ "?subject a ?type. "
					+ "?subject ?property ?object. "
					+ "}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			// fileMethods.printResultSet(resultSet);
			LinkedHashMap<String, String> provHashMap = new LinkedHashMap<>();
			
			int numOfFiles = 1, count = 0;
			while (resultSet.hasNext()) {
                QuerySolution querySolution = (QuerySolution) resultSet.next();
                
                String concept = querySolution.get("head").toString();
                // String sourceType = querySolution.get("type").toString();
                String targetType = querySolution.get("target").toString();
                String iriValueType = querySolution.get("keyType").toString();
                // String mapper = querySolution.get("record").toString();
                // String sourceProperty = querySolution.get("property").toString();
                String targetProperty = querySolution.get("targetProperty").toString();
                String subject = querySolution.get("subject").toString();
                String object = querySolution.get("object").toString();
                
                if (provHashMap.containsKey(subject)) {
                	String provIRI = provHashMap.get(subject);
                	Resource resource = targetModel.createResource(provIRI);
					
					Property property = targetModel.createProperty(targetProperty);
					String rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
				} else {
					String provValue = getIRIValue(iriValueType, concept, mapModel, subject, sourceABoxModel, provModel);
					
					String provIRI = "";
					/*if (provValue == null) {
						// AUTOMATIC
						return "Empty Provinance value";
					} else {
						// LOOK UP PROV GRAPH
						String rangeValue = getRangeValue(targetType, targetTBoxModel);
						
						if (rangeValue == null) {
							provIRI = targetType + "#" + provValue;
						} else {
							provIRI = rangeValue + "#" + provValue;
						}
					}*/
					
					if (provValue == null) {
						provValue = getProvValue(subject);
					}
					
					String rangeValue = getRangeValue(targetType, targetTBoxModel);
					
					if (rangeValue == null) {
						provIRI = targetType + "#" + provValue;
					} else {
						provIRI = rangeValue + "#" + provValue;
					}
					
					Resource resource = targetModel.createResource(provIRI);
					
					Property property2 = targetModel.createProperty("http://purl.org/linked-data/cube#dataSet");
					resource.addProperty(property2, targetModel.createResource(targetType));

					resource.addProperty(RDF.type, ResourceFactory.createResource("http://purl.org/linked-data/cube#Observation"));
					
					Property property = targetModel.createProperty(targetProperty);
					rangeValue = getRangeValue(targetProperty, targetTBoxModel);
					
					if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
						Literal literal = targetModel.createTypedLiteral(object);
						resource.addLiteral(property, literal);
					} else {
						String propertyValueIRI = rangeValue + "#" + getProvValue(object);
						resource.addProperty(property,
								targetModel.createResource(propertyValueIRI));
					}
					
					count++;
					provHashMap.put(subject, provIRI);
				}
                
                if (count % 1000 == 0) {
					String tempPath = numOfFiles + ".ttl";
					// System.out.println(tempPath);
					fileMethods.saveModel(targetModel, tempPath);
					targetModel = ModelFactory.createDefaultModel();
					numOfFiles++;
				}
            }
			
			if (count == 0) {
				return "Sparql Query returns 0 result";
			}

			if (targetModel.size() > 0) {
				String tempPath = numOfFiles + ".ttl";
				// System.out.println(tempPath);
				fileMethods.saveModel(targetModel, tempPath);
				targetModel = ModelFactory.createDefaultModel();
				numOfFiles++;
			}
			
			// fileMethods.saveModel(provGraph.model, provGraphFile);
			return mergeAllTempFiles(numOfFiles, targetABoxFile);
		} else {
			return checkFileResult;
		}
	}

	public String generateLevelEntryFromCSV(String sourceABoxFile, String mappingFile, String targetTBoxFile, String provGraphFile,
			String targetABoxFile, String csvDelimiter) {
		// TODO Auto-generated method stub
		String checkFileResult = checkFiles(sourceABoxFile, mappingFile, targetTBoxFile, provGraphFile);
		
		if (checkFileResult.equals("OK")) {
			/*Model sourceABoxModel = fileMethods.readModelFromPath(sourceABoxFile);
			
			if (sourceABoxModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}*/
			
			if (!fileMethods.getFileExtension(sourceABoxFile).equals("csv")) {
				return "Check source file type";
			}
			
			Model mapModel = fileMethods.readModelFromPath(mappingFile);
			
			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}
			
			Model targetTBoxModel = fileMethods.readModelFromPath(targetTBoxFile);
			
			if (targetTBoxModel == null) {
				return "Error in reading the target tbox file. Please check syntaxes.";
			}
			
			Model provModel = fileMethods.readModelFromPath(provGraphFile);
			
			if (provModel == null) {
				return "Error in reading the prov graph file. Please check syntaxes.";
			}
			
			ProvGraph provGraph = new ProvGraph(provGraphFile);
			prefixMap = extractAllPrefixes(targetTBoxFile);
			
			String delimiter = ",";
			if (csvDelimiter.contains("Space") || csvDelimiter.contains("Tab")) {
				delimiter = "\\s";
			} else if (csvDelimiter.contains("Semicolon")) {
				delimiter = ";";
			} else if (csvDelimiter.contains("Pipe")) {
				delimiter = "|";
			} else {
				delimiter = ",";
			}
			
			String inputStream = fileMethods.getEncodedString(sourceABoxFile);
	        
	        Reader inputString = new StringReader(inputStream);
	        BufferedReader bufferedReader = new BufferedReader(inputString);
	        
	        ArrayList<String> keys = new ArrayList<>();
	        try {
	        	String eachLine = "";
	        	
				while ((eachLine = bufferedReader.readLine()) != null) {
				    // String[] parts = eachLine.split(delimiter);
					eachLine = eachLine + delimiter;
				    
				    String regEx = "([^" + delimiter + "]*)(" + delimiter + ")";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(eachLine);

                    while (matcher.find()) {
                    	keys.add(cleanString(matcher.group(1)));
                    }
				    
				    break;
				}
				
				String sourceFileName = getFileName(sourceABoxFile);
				
				Model model = ModelFactory.createDefaultModel();
				model.add(mapModel);
				model.add(targetTBoxModel);
				
				String concept = "";
				String sourceType = "";
	            String targetType = "";
	            String keyAttributeType = "";
	            LinkedHashMap<String, String> propertiesMap = new LinkedHashMap<>();
				
				String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
	                    + "PREFIX   qb4o:   <http://purl.org/qb4olap/cubes#>\r\n"
	                    + "SELECT * WHERE { \r\n"
	                    + "?head a map:ConceptMapper. \r\n"
	                    + "?head map:sourceConcept ?type. \r\n"
	                    + "?head map:targetConcept ?target. \r\n"
	                    + "?head map:iriValueType ?keyType. \r\n"
	                    + "?target a qb4o:LevelProperty. \r\n"
	                    + "?record a map:PropertyMapper. \r\n"
	                    + "?record map:ConceptMapper ?head. \r\n"
	                    + "?record map:sourceProperty ?property. \r\n"
	                    + "?record map:targetProperty ?targetProperty. \r\n"
	                    + "FILTER regex(str(?type), '" + sourceFileName + "').}";

				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, model);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
				
				// fileMethods.printResultSet(resultSet);
				while (resultSet.hasNext()) {
	                QuerySolution querySolution = (QuerySolution) resultSet.next();
	                sourceType = querySolution.get("type").toString();
	                
	                if (getProvValue(sourceType).equals(sourceFileName)) {
	                	concept = querySolution.get("head").toString();
		                targetType = querySolution.get("target").toString();
		                keyAttributeType = querySolution.get("keyType").toString();
		                String sourceProperty = querySolution.get("property").toString();
		                String targetProperty = querySolution.get("targetProperty").toString();
		                
		                propertiesMap.put(sourceProperty, targetProperty);
					}
	            }
				
				Model targetModel = ModelFactory.createDefaultModel();
				
				int numOfFiles = 1, count = 0, lineCount = 0;
				while ((eachLine = bufferedReader.readLine()) != null) {
					count++;
					lineCount++;
					
					eachLine = eachLine + delimiter;
					LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
					
					String regEx = "([^" + delimiter + "]*)(" + delimiter + ")";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(eachLine);

                    ArrayList<String> values = new ArrayList<>();
                    while (matcher.find()) {
                    	values.add(cleanString(matcher.group(1)));
                    }
                    
                    if (values.size() != keys.size()) {
                    	System.out.println("Skipped Line No: " + lineCount + " = " + eachLine);
					} else {
						for (int i = 0; i < keys.size(); i++) {
							linkedHashMap.put(keys.get(i), values.get(i));
						}
						
						String provValue = getIRIValue(concept, keyAttributeType, mapModel, linkedHashMap, provModel);
						
						String provIRI = "";
						if (provValue == null) {
							// AUTOMATIC
							return "Empty Provinance value";
						} else {
							// LOOK UP PROV GRAPH
							String rangeValue = getRangeValue(targetType, targetTBoxModel);
							
							if (rangeValue == null) {
								provIRI = targetType + "#" + provValue;
							} else {
								provIRI = rangeValue + "#" + provValue;
							}
						}
						
						Resource resource = targetModel.createResource(provIRI);
						
						Property property2 = targetModel.createProperty("http://purl.org/qb4olap/cubes#memberOf");
						resource.addProperty(property2, targetModel.createResource(targetType));

						resource.addProperty(RDF.type, ResourceFactory.createResource("http://purl.org/qb4olap/cubes#LevelMember"));
						
						for (Map.Entry<String, String> map : propertiesMap.entrySet()) {
							String sourceProperty = map.getKey();
	                        String targetProperty = map.getValue();
	                        String targetValue = linkedHashMap.get(getProvValue(sourceProperty));
	                        
	                        Property property = targetModel.createProperty(targetProperty);
	                        
	                        if (targetValue != null) {
								String rangeValue = getRangeValue(targetProperty, targetTBoxModel);
								
								if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
									Literal literal = targetModel.createTypedLiteral(targetValue);
									resource.addLiteral(property, literal);
								} else {
									String propertyValueIRI = rangeValue + "#" + targetValue;
									resource.addProperty(property,
											targetModel.createResource(propertyValueIRI));
								}
							}
						}
					}
                    
                    if (count % 10000 == 0) {
						String tempPath = numOfFiles + ".ttl";
						// System.out.println(tempPath);
						fileMethods.saveModel(targetModel, tempPath);
						targetModel = ModelFactory.createDefaultModel();
						numOfFiles++;
					}
				}
				
				if (count == 0) {
					return "Sparql Query returns 0 result";
				}

				if (targetModel.size() > 0) {
					String tempPath = numOfFiles + ".ttl";
					// System.out.println(tempPath);
					fileMethods.saveModel(targetModel, tempPath);
					targetModel = ModelFactory.createDefaultModel();
					numOfFiles++;
				}
				
				fileMethods.saveModel(provGraph.model, provGraphFile);
				return mergeAllTempFiles(numOfFiles, targetABoxFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return "Error in parsing the csv file";
			}
		} else {
			return checkFileResult;
		}
	}
	
	public String generateFactEntryFromCSV(String sourceABoxFile, String mappingFile, String targetTBoxFile,
			String provGraphFile, String targetABoxFile, String csvDelimiter) {
		/*
		 * System.out.println(sourceABoxFile); System.out.println(mappingFile);
		 * System.out.println(targetTBoxFile); System.out.println(provGraphFile);
		 * System.out.println(targetABoxFile); System.out.println(csvDelimiter);
		 */
		
		String checkFileResult = checkFiles(sourceABoxFile, mappingFile, targetTBoxFile, provGraphFile);
		
		if (checkFileResult.equals("OK")) {
			/*Model sourceABoxModel = fileMethods.readModelFromPath(sourceABoxFile);
			
			if (sourceABoxModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}*/
			
			if (!fileMethods.getFileExtension(sourceABoxFile).equals("csv")) {
				return "Check source file type";
			}
			
			Model mapModel = fileMethods.readModelFromPath(mappingFile);
			
			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}
			
			Model targetTBoxModel = fileMethods.readModelFromPath(targetTBoxFile);
			
			if (targetTBoxModel == null) {
				return "Error in reading the target tbox file. Please check syntaxes.";
			}
			
			Model provModel = fileMethods.readModelFromPath(provGraphFile);
			
			if (provModel == null) {
				return "Error in reading the prov graph file. Please check syntaxes.";
			}
			
			ProvGraph provGraph = new ProvGraph(provGraphFile);
			prefixMap = extractAllPrefixes(targetTBoxFile);
			
			String delimiter = ",";
			if (csvDelimiter.contains("Space") || csvDelimiter.contains("Tab")) {
				delimiter = "\\s";
			} else if (csvDelimiter.contains("Semicolon")) {
				delimiter = ";";
			} else if (csvDelimiter.contains("Pipe")) {
				delimiter = "|";
			} else {
				delimiter = ",";
			}
			
			String inputStream = fileMethods.getEncodedString(sourceABoxFile);
	        
	        Reader inputString = new StringReader(inputStream);
	        BufferedReader bufferedReader = new BufferedReader(inputString);
	        
	        ArrayList<String> keys = new ArrayList<>();
	        try {
	        	String eachLine = "";
	        	
				while ((eachLine = bufferedReader.readLine()) != null) {
				    
				    /*String regEx = "([^" + delimiter + "]*)(" + delimiter + ")";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(eachLine);

                    while (matcher.find()) {
                    	keys.add(cleanString(matcher.group(1)));
                    }*/
					
					keys = extractValuesFromEachLine(eachLine, delimiter);
				    
				    break;
				}
				
				String sourceFileName = getFileName(sourceABoxFile);
				
				Model model = ModelFactory.createDefaultModel();
				model.add(mapModel);
				model.add(targetTBoxModel);
				
				String concept = "";
				String sourceType = "";
	            String targetType = "";
	            String keyAttributeType = "";
	            LinkedHashMap<String, String> propertiesMap = new LinkedHashMap<>();
	            
	            // System.out.println(sourceFileName);
				
	            String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
	            		+ "PREFIX	qb:	<http://purl.org/linked-data/cube#>\r\n"
	                    + "PREFIX   qb4o:   <http://purl.org/qb4olap/cubes#>\r\n"
	                    + "SELECT * WHERE { \r\n"
	                    + "?head a map:ConceptMapper. \r\n"
	                    + "?head map:sourceConcept ?type. \r\n"
	                    + "?head map:targetConcept ?target. \r\n"
	                    + "?head map:iriValueType ?keyType. \r\n"
	                    + "?target a qb:DataSet. \r\n"
	                    + "?record a map:PropertyMapper. \r\n"
	                    + "?record map:ConceptMapper ?head. \r\n"
	                    + "?record map:sourceProperty ?property. \r\n"
	                    + "?record map:targetProperty ?targetProperty. \r\n"
	                    + "}";
	                    // + "FILTER regex(str(?type), '" + sourceFileName + "').}";

				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, model);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
				
				// fileMethods.printResultSet(resultSet);
				
				while (resultSet.hasNext()) {
	                QuerySolution querySolution = (QuerySolution) resultSet.next();
	                sourceType = querySolution.get("type").toString();
	                
	                if (getProvValue(sourceType).equals(sourceFileName)) {
	                	concept = querySolution.get("head").toString();
		                targetType = querySolution.get("target").toString();
		                keyAttributeType = querySolution.get("keyType").toString();
		                String sourceProperty = querySolution.get("property").toString();
		                String targetProperty = querySolution.get("targetProperty").toString();
		                
		                propertiesMap.put(sourceProperty, targetProperty);
					}
	            }
				
				/*for (Map.Entry<String, String> map : propertiesMap.entrySet()) {
					System.out.println(map.getKey() + " = " + map.getValue());
				}*/
				
				Model targetModel = ModelFactory.createDefaultModel();
				
				int numOfFiles = 1, count = 0, lineCount = 0;
				while ((eachLine = bufferedReader.readLine()) != null) {
					// System.out.println(eachLine);
					count++;
					lineCount++;
					
					LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
					
					ArrayList<String> values = extractValuesFromEachLine(eachLine, delimiter);
                    
                    if (values.size() != keys.size()) {
                    	System.out.println("Skipped Line No: " + lineCount + " = " + eachLine);
					} else {
						for (int i = 0; i < keys.size(); i++) {
							// System.out.println(values.get(i));
							linkedHashMap.put(keys.get(i), values.get(i));
						}
						
						String provValue = getIRIValue(concept, keyAttributeType, mapModel, linkedHashMap, provModel);
						
						String provIRI = "";
						if (provValue == null) {
							// AUTOMATIC
							return "Empty Provinance value";
						} else {
							// LOOK UP PROV GRAPH
							String rangeValue = getRangeValue(targetType, targetTBoxModel);
							
							if (rangeValue == null) {
								provIRI = targetType + "#" + provValue;
							} else {
								provIRI = rangeValue + "#" + provValue;
							}
						}
						
						Resource resource = targetModel.createResource(provIRI);
						
						Property property2 = targetModel.createProperty("http://purl.org/linked-data/cube#dataSet");
						resource.addProperty(property2, targetModel.createResource(targetType));

						resource.addProperty(RDF.type, ResourceFactory.createResource("http://purl.org/linked-data/cube#Observation"));
						
						for (Map.Entry<String, String> map : propertiesMap.entrySet()) {
							String sourceProperty = map.getKey();
	                        String targetProperty = map.getValue();
	                        String targetValue = linkedHashMap.get(getProvValue(sourceProperty));
	                        
	                        Property property = targetModel.createProperty(targetProperty);
	                        
	                        // System.out.println(sourceProperty + " - " + targetProperty + " - " + targetValue);
	                        
	                        if (targetValue != null) {
								String rangeValue = getRangeValue(targetProperty, targetTBoxModel);
								
								if (rangeValue == null) {
									System.out.println(targetProperty);
								} else {
									if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
										Literal literal = targetModel.createTypedLiteral(targetValue);
										resource.addLiteral(property, literal);
									} else {
										String propertyValueIRI = rangeValue + "#" + targetValue;
										resource.addProperty(property,
												targetModel.createResource(propertyValueIRI));
									}
								}
								
								/*
								 * if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) { Literal
								 * literal = targetModel.createTypedLiteral(targetValue);
								 * resource.addLiteral(property, literal); } else { String propertyValueIRI =
								 * rangeValue + "#" + targetValue; resource.addProperty(property,
								 * targetModel.createResource(propertyValueIRI)); }
								 */
							}
						}
					}
                    
                    if (count % 10000 == 0) {
						String tempPath = numOfFiles + ".ttl";
						System.out.println(tempPath);
						fileMethods.saveModel(targetModel, tempPath);
						targetModel = ModelFactory.createDefaultModel();
						numOfFiles++;
					}
				}
				
				if (count == 0) {
					return "Sparql Query returns 0 result";
				}

				if (targetModel.size() > 0) {
					String tempPath = numOfFiles + ".ttl";
					// System.out.println(tempPath);
					fileMethods.saveModel(targetModel, tempPath);
					targetModel = ModelFactory.createDefaultModel();
					numOfFiles++;
				}
				
				fileMethods.saveModel(provGraph.model, provGraphFile);
				return mergeAllTempFiles(numOfFiles, targetABoxFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return "Error in parsing the csv file";
			}
		} else {
			return checkFileResult;
		}
	}

	private ArrayList<String> extractValuesFromEachLine(String eachLine, String delimiter) {
		// TODO Auto-generated method stub
		if (!delimiter.equals("\\s")) {
			eachLine = eachLine + delimiter;
		}
		
		/*String regEx = "([^" + delimiter + "]*)(" + delimiter + ")";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(eachLine);

        ArrayList<String> values = new ArrayList<>();
        while (matcher.find()) {
        	values.add(cleanString(matcher.group(1)));
        }*/
		
		ArrayList<String> values = new ArrayList<>();
		String[] parts = eachLine.split(delimiter);
		for (int i = 0; i < parts.length; i++) {
			values.add(cleanString(parts[i]));
		}
		
		return values;
	}

	private String mergeAllTempFiles(int numOfFiles, String targetABoxFile) {
		// TODO Auto-generated method stub
		Methods fileMethods = new Methods();
		fileMethods.createNewFile(targetABoxFile);
		
		for (int i = 1; i < numOfFiles; i++) {
			String filePath = i + ".ttl";
			
			try {
				Model model = fileMethods.readModelFromPath(filePath);
				String string = fileMethods.modelToString(model, fileMethods.getFileExtension(targetABoxFile));
				fileMethods.appendToFile(string, targetABoxFile);
				
				File file = new File(filePath);
				file.delete();
				// System.out.println(filePath + " deleted");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Invalid File Content";
			}
		}
		return "Success.\nFile Saved: " + targetABoxFile;
	}

	private String getRangeValue(String targetType, Model targetTBoxModel) {
		// TODO Auto-generated method stub
		String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?s ?o WHERE {"
				+ "?s rdfs:range ?o."
				+ "FILTER regex(str(?s), '" + targetType + "').}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, targetTBoxModel);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();

			if (subject.equals(targetType)) {
				return querySolution.get("o").toString();
			}
		}
		return null;
	}

	private String getIRIValue(String iriValueType, String concept, Model mapModel, String subject,
			Model sourceABoxModel, Model provModel) {
		// TODO Auto-generated method stub
		if (iriValueType.contains("SourceAttribute")) {
			if (keyAttributesMap.containsKey(iriValueType)) {
				String value = keyAttributesMap.get(iriValueType);
				
				String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {"
						+ "?subject ?value ?object. "
						+ "FILTER (regex(str(?value), '" + value + "') && (regex(str(?subject), '" + subject + "'))).}";

				Query query2 = QueryFactory.create(sparql2);
				QueryExecution execution2 = QueryExecutionFactory.create(query2, sourceABoxModel);
				ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
				
				// fileMethods.printResultSet(resultSet2);
				while (resultSet2.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet2.next();
					String valueQuery = querySolution.get("value").toString();
					String object = querySolution.get("object").toString();
					String instance = querySolution.get("subject").toString();
					
					if (value.equals(valueQuery)) {
						if (instance.equals(subject)) {
							return object;
						}
					}
				}
			} else {
				Model model = ModelFactory.createDefaultModel();
				model.add(mapModel);
				model.add(sourceABoxModel);
				
				String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {"
						+ "?head a map:ConceptMapper. "
						+ "?head map:iriValue ?value. "
						+ "?subject ?value ?object. "
						+ "FILTER (regex(str(?head), '" + concept + "') && (regex(str(?subject), '" + subject + "'))).}";

				Query query2 = QueryFactory.create(sparql2);
				QueryExecution execution2 = QueryExecutionFactory.create(query2, model);
				ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
				
				// fileMethods.printResultSet(resultSet2);
				while (resultSet2.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet2.next();
					String head = querySolution.get("head").toString();
					String value = querySolution.get("value").toString();
					String object = querySolution.get("object").toString();
					String instance = querySolution.get("subject").toString();
					
					if (head.equals(concept)) {
						if (instance.equals(subject)) {
							keyAttributesMap.put(iriValueType, value);
							return object;
						}
					}
				}
			}
		} else if (iriValueType.toLowerCase().contains("Expression".toLowerCase())) {
			LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
			
			String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ "SELECT * WHERE {"
					+ "?s ?p ?o."
					+ "FILTER regex(str(?s), '" + subject + "')."
					+ "}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, sourceABoxModel);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String instance = querySolution.get("s").toString();
				String predicate = querySolution.get("p").toString();
				String object = querySolution.get("o").toString();

				if (subject.equals(instance)) {
					linkedHashMap.put(predicate, object);
				}
			}
			
			if (keyAttributesMap.containsKey(iriValueType)) {
				String value = keyAttributesMap.get(iriValueType);
				
				ExpressionHandler expressionHandler = new ExpressionHandler();
				return expressionHandler.handleExpression(value, linkedHashMap).toString();
			} else {
				String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {"
						+ "?head a map:ConceptMapper. "
						+ "?head map:iriValue ?value. "
						+ "FILTER regex(str(?head), '" + concept + "').}";

				Query query2 = QueryFactory.create(sparql2);
				QueryExecution execution2 = QueryExecutionFactory.create(query2, mapModel);
				ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
				
				while (resultSet2.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet2.next();
					String head = querySolution.get("head").toString();
					String value = querySolution.get("value").toString();
					
					if (head.equals(concept)) {
						value = assignPrefix(value);
						keyAttributesMap.put(concept, value);
						
						ExpressionHandler expressionHandler = new ExpressionHandler();
						return expressionHandler.handleExpression(value, linkedHashMap).toString();
					}
				}
			}
		} else if (iriValueType.contains("Incremental")) {
			if (keyAttributesMap.containsKey(concept)) {
				String key = keyAttributesMap.get(concept);
				int count = Integer.parseInt(key);
				count++;
				keyAttributesMap.put(concept, String.valueOf(count));
				return String.valueOf(count);
			} else {
				String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
						+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT DISTINCT ?s WHERE {"
						+ "?s ?p ?o."
						+ "}";
				
				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, provModel);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

				int count = 1;
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					String sub = querySolution.get("s").toString();
					count++;
				}

				keyAttributesMap.put(concept, String.valueOf(count));
				return String.valueOf(count);
			}
		}
		
		return null;
	}

	private String getIRIValue(String concept, String keyAttributeType, Model mapModel,
			LinkedHashMap<String, String> linkedHashMap, Model provModel) {
		// TODO Auto-generated method stub
		if (keyAttributeType.contains("SourceAttribute")) {
			if (keyAttributesMap.containsKey(concept)) {
				return linkedHashMap.get(keyAttributesMap.get(concept));
			} else {
				String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {"
						+ "?head a map:ConceptMapper. "
						+ "?head map:iriValue ?value. "
						+ "FILTER regex(str(?head), '" + concept + "').}";

				Query query2 = QueryFactory.create(sparql2);
				QueryExecution execution2 = QueryExecutionFactory.create(query2, mapModel);
				ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
				
				// fileMethods.printResultSet(resultSet2);
				while (resultSet2.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet2.next();
					String head = querySolution.get("head").toString();
					String value = querySolution.get("value").toString();
					
					// fileMethods.printHashMap(linkedHashMap);
					
					if (head.equals(concept)) {
						value = getProvValue(value);
						// System.out.println(value);
						
						keyAttributesMap.put(concept, value);
						return linkedHashMap.get(value);
					}
				}
			}
		} else if (keyAttributeType.contains("Expression")) {
			if (keyAttributesMap.containsKey(concept)) {
				String key = keyAttributesMap.get(concept);
				ExpressionHandler expressionHandler = new ExpressionHandler();
				return expressionHandler.handleExpression(key, linkedHashMap).toString();
			} else {
				String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {"
						+ "?head a map:ConceptMapper. "
						+ "?head map:iriValue ?value. "
						+ "FILTER regex(str(?head), '" + concept + "').}";

				Query query2 = QueryFactory.create(sparql2);
				QueryExecution execution2 = QueryExecutionFactory.create(query2, mapModel);
				ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
				
				while (resultSet2.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet2.next();
					String head = querySolution.get("head").toString();
					String value = querySolution.get("value").toString();
					
					if (head.equals(concept)) {
						value = assignPrefix(value);
						keyAttributesMap.put(concept, value);
						
						ExpressionHandler expressionHandler = new ExpressionHandler();
						return expressionHandler.handleExpression(value, linkedHashMap).toString();
					}
				}
			}
		} else if (keyAttributeType.contains("Incremental")) {
			if (keyAttributesMap.containsKey(concept)) {
				String key = keyAttributesMap.get(concept);
				int count = Integer.parseInt(key);
				count++;
				keyAttributesMap.put(concept, String.valueOf(count));
				return String.valueOf(count);
			} else {
				String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
						+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT DISTINCT ?s WHERE {"
						+ "?s ?p ?o."
						+ "}";
				
				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, provModel);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

				int count = 1;
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					String subject = querySolution.get("s").toString();
					count++;
				}

				keyAttributesMap.put(concept, String.valueOf(count));
				return String.valueOf(count);
			}
		}
		
		return null;
	}

	private String getFileName(String filePath) {
		// TODO Auto-generated method stub
		String[] parts = filePath.split("\\\\");
		String fileName = parts[parts.length - 1];
		String[] segments = fileName.split("\\.");
		
		return segments[0];
	}

	private String cleanString(String group) {
		// TODO Auto-generated method stub
		// System.out.println("The string: " + group);
		group = group.replace("\"", "");
		// System.out.println("The string: " + group);
		return group.trim();
	}

	private String checkFiles(String sourceABoxFile, String mappingFile, String targetTBoxFile, String provGraphFile) {
		// TODO Auto-generated method stub
		if (!fileMethods.checkFile(sourceABoxFile)) {
			return "Check source file path";
		} else if (!fileMethods.checkFile(mappingFile)) {
			return "Check map file path";
		} else if (!fileMethods.checkFile(targetTBoxFile)) {
			return "Check target tbox file path";
		} else if (!fileMethods.checkFile(provGraphFile)) {
			return "Check prov graph file path";
		} else {
			return "OK";
		}
	}
	
	private String getProvValue(String subject) {
		// TODO Auto-generated method stub
		// System.out.println(subject);
		if (subject.contains("#")) {
			String[] parts = subject.split("#");
			if (parts.length == 2) {
				return parts[1].trim();
			} else {
				return subject.trim();
			}
		} else {
			if (subject.contains("http")) {
				String[] parts = subject.split("/");
				return parts[parts.length - 1].trim();
			} else {
				String[] parts = subject.split(":");
				return parts[parts.length - 1].trim();
			}
		}
	}
	
	private LinkedHashMap<String, String> extractAllPrefixes(String filepath) {
		LinkedHashMap<String, String> hashedMap = new LinkedHashMap<>();
		File file = new File(filepath);
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(file));

			String string = "", s;
			while ((s = bufferedReader.readLine()) != null) {
				string = string + s;
			}

			String regEx = "(@prefix\\s+)([^\\:.]*:\\s+)(<)([^>]*)(>)";
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(string);

			while (matcher.find()) {
				String prefix = matcher.group(2).trim();
				String iri = matcher.group(4).trim();

				hashedMap.put(prefix, iri);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return hashedMap;
	}
	
	private String assignPrefix(String iri) {
		if (iri.contains("#")) {
			String[] segments = iri.split("#");
			if (segments.length == 2) {
				String firstSegment = segments[0].trim() + "#";

				for (Map.Entry<String, String> map : prefixMap.entrySet()) {
					String key = map.getKey();
					String value = map.getValue();

					if (firstSegment.equals(value.trim())) {
						return key + segments[1];
					}
				}

				return iri;
			} else {
				return iri;
			}
		} else {
			String[] segments = iri.split("/");
			String lastSegment = segments[segments.length - 1];

			String firstSegment = "";
			if (iri.endsWith(lastSegment)) {
				firstSegment = iri.replace(lastSegment, "");
			}

			for (Map.Entry<String, String> map : prefixMap.entrySet()) {
				String key = map.getKey();
				String value = map.getValue();

				if (firstSegment.equals(value.trim())) {
					return key + lastSegment;
				}
			}

			return iri;
		}
	}

	private String assignIRI(String prefix) {
		if (prefix.contains("http") || prefix.contains("www")) {
			return prefix;
		} else {
			String[] segments = prefix.split(":");
			if (segments.length == 2) {
				String firstSegment = segments[0] + ":";
				return prefixMap.get(firstSegment) + segments[1];
			} else {
				return prefix;
			}
		}
	}
}
