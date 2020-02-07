package core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import helper.Methods;
import helper.Variables;
import model.ConceptTransform;

public class LevelEntryGenerator {
	private CSVExtraction csvExtraction;
	private IRIGenerator iriGenerator;
	private PrefixExtraction prefixExtraction;

	public LevelEntryGenerator() {
		// TODO Auto-generated constructor stub
		csvExtraction = new CSVExtraction();
		iriGenerator = new IRIGenerator();
		prefixExtraction = new PrefixExtraction();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basePath = "I:\\Data\\level\\rdf\\large\\";

		String sourceFile = basePath + "recipient_wrapper.nt";
		String mapFile = basePath + "map_current.ttl";
		String targetTBox = basePath + "subsidy.ttl";
		String targetABox = basePath + "recipient_entry.ttl";
		String provGraph = basePath + "prov.ttl";

		Methods.startProcessingTime();

		LevelEntryGenerator levelEntryGenerator = new LevelEntryGenerator();
		String resultString = levelEntryGenerator.generateLevelEntryFromRDF(sourceFile, mapFile, targetTBox, targetABox,
				provGraph);

		System.out.println(resultString);
		Methods.endProcessingTime();

//		String basePath = "I:\\Data\\level\\csv\\large\\";
////
////		String sourceFile = basePath + "city.csv";
////		String mapFile = basePath + "map_current.ttl";
////		String targetTBox = basePath + "subsidy.ttl";
////		String targetABox = basePath + "city_entry.ttl";
////		String delimiter = "Comma (,)";
//		
//		String sourceFile = basePath + "recipient.csv";
//		String mapFile = basePath + "map_current.ttl";
//		String targetTBox = basePath + "subsidy.ttl";
//		String targetABox = basePath + "recipient_entry.ttl";
//		String delimiter = "Comma (,)";
//
//		Methods.startProcessingTime();
//		// Methods.createNewFile(provGraph);
//		LevelEntryGenerator levelEntryGenerator = new LevelEntryGenerator();
//		String resultString = levelEntryGenerator.generateLevelEntryFromCSV(sourceFile, mapFile, targetTBox, targetABox, delimiter);
//
//		System.out.println(resultString);
//		Methods.endProcessingTime();
	}

	public String generateLevelEntryFromCSV(String sourceFile, String mapFile, String targetTBoxFile,
			String targetABoxFile, String delimiter) {
		delimiter = Methods.getCSVDelimiter(delimiter);

		String sourceFileName = Methods.getFileName(sourceFile);
		
		Model targetTBoxModel = ModelFactory.createDefaultModel();
		try {
			targetTBoxModel.read(targetTBoxFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Target TBox file";
		}
		
		Model mapModel = ModelFactory.createDefaultModel();
		try {
			mapModel.read(mapFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Mapping file";
		}

		Model completeModel = ModelFactory.createDefaultModel();
		completeModel.add(mapModel);
		completeModel.add(targetTBoxModel);
		
		prefixExtraction.extractPrefix(mapFile);

		sourceFileName = Methods.getUppercaseWord(sourceFileName);

		String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
				+ "PREFIX	qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX   qb4o:   <http://purl.org/qb4olap/cubes#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + "SELECT * WHERE { \r\n"
				+ "?concept a map:ConceptMapper. \r\n" + "?concept map:sourceConcept ?stype. \r\n"
				+ "?concept map:targetConcept ?ttype. \r\n" + "?concept map:iriValueType ?iritype. \r\n"
				+ "?concept map:iriValue ?irivalue. \r\n" + "?ttype a qb4o:LevelProperty. \r\n"
				+ "OPTIONAL {?ttype rdfs:range ?range.}" + "?mapper a map:PropertyMapper. \r\n"
				+ "?mapper map:ConceptMapper ?concept. \r\n" + "?mapper map:sourceProperty ?sprop. \r\n"
				+ "?mapper map:targetProperty ?tprop. \r\n" + "FILTER regex(str(?stype), '" + sourceFileName
				+ "'). \r\n" + "}";

		ResultSet resultSet = Methods.executeQuery(completeModel, sparql);
		completeModel.close();

//		Methods.print(resultSet);

		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String sourceType = querySolution.get("stype").toString();

			if (Methods.getLastSegmentOfIRI(sourceType).equals(sourceFileName)) {
				String concept = querySolution.get("concept").toString();
				String targetType = querySolution.get("ttype").toString();
				String iriValue = querySolution.get("irivalue").toString();
				String iriType = querySolution.get("iritype").toString();

				String rangeString = "";
				if (querySolution.get("range") != null) {
					rangeString = querySolution.get("range").toString();
				}

//				String mapper = querySolution.get("mapper").toString();
				String sourceProperty = querySolution.get("sprop").toString();
				String targetProperty = querySolution.get("tprop").toString();

				if (conceptMap.containsKey(concept)) {
					ConceptTransform conceptTransform = conceptMap.get(concept);
					conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
					conceptMap.replace(concept, conceptTransform);
				} else {
					ConceptTransform conceptTransform = new ConceptTransform();
					conceptTransform.setConcept(concept);
					conceptTransform.setSourceType(sourceType);
					conceptTransform.setTargetType(targetType);
					conceptTransform.setIriValue(iriValue);
					conceptTransform.setIriValueType(iriType);
					conceptTransform.setRangeString(rangeString);
					conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
					conceptMap.put(concept, conceptTransform);
				}
			}
		}

		Model model = ModelFactory.createDefaultModel();
		
		if (conceptMap.size() == 0) {
			return "Mapping query returns zero result";
		}

		for (String concept : conceptMap.keySet()) {
			LinkedHashMap<String, String> rangeMap = new LinkedHashMap<>();
			ConceptTransform conceptTransform = conceptMap.get(concept);

			LinkedHashMap<Integer, String> targetPropertyMap = new LinkedHashMap<>();

			BufferedReader br = null;
			String line;

			try {
				// String inputStream = Methods.getEncodedString(sourceFile);;
				// Reader inputString = new StringReader(inputStream);

				// br = new BufferedReader(new FileReader(sourceFile));
				// br = new BufferedReader(inputString);

				br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8));

				line = br.readLine();
				line += delimiter;

				ArrayList<String> columnNames = csvExtraction.parseCSVLine(line, delimiter, true);
//				System.out.println("Total Columns: " + columnNames.size());

//				int index = columnNames.indexOf(columnName);

//				String iriPropertyName = Methods.getLastSegmentOfIRI(conceptTransform.getIriValue());

				for (int i = 0; i < columnNames.size(); i++) {
					String column = columnNames.get(i);
//					System.out.println("Column: " + column);

					for (Map.Entry<String, String> map : conceptTransform.getPropertyMap().entrySet()) {
						String sourceProperty = map.getKey();
						String targetProperty = map.getValue();

//						System.out.println("Source Property: " + sourceProperty + " - Target Property" + targetProperty);

						String propertyName = Methods.getLastSegmentOfIRI(sourceProperty);

						if (column.equals(propertyName)) {
//							System.out.println("Added");
							targetPropertyMap.put(i, targetProperty);

							break;
						}
					}
				}

//				System.out.println("Target Property Map: " + targetPropertyMap.size());

				ArrayList<Integer> faultList = new ArrayList<>();
				
//				System.out.println(conceptTransform.getIriValueType());

				int count = 0, numOfFiles = 1;
				while ((line = br.readLine()) != null) {
					// System.out.println(line);

					line += delimiter;
					ArrayList<String> columnValues = csvExtraction.parseCSVLine(line, delimiter, false);

					if (columnNames.size() == columnValues.size()) {
						String iriValue = "";

						if (conceptTransform.getIriValueType().contains("SourceAttribute")) {
							String iriPropertyName = Methods.getLastSegmentOfIRI(conceptTransform.getIriValue());

							if (columnNames.contains(iriPropertyName)) {
								int index = columnNames.indexOf(iriPropertyName);

								iriValue = columnValues.get(index);
							} else {
								System.out.println("No IRI Value");
							}
						} else if (conceptTransform.getIriValueType().contains("Expression")) {
							LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
							
							for (int i = 0; i < columnNames.size(); i++) {
								String key = columnNames.get(i);
								String value = columnValues.get(i);
								
								valueMap.put(key, value);
							}
							
							String expressionString = conceptTransform.getIriValue();
							
							EquationHandler equationHandler = new EquationHandler();
							Object valueObject = equationHandler.handleExpression(expressionString,
									valueMap, true);
							
							iriValue = valueObject.toString();
							
//							System.out.println("Ex IRIValue: " + valueObject);
						}

						iriValue = Methods.formatURL(iriValue);
						// iriValue = iriValue.substring(0, 1).toUpperCase() + iriValue.substring(1);

//						System.out.println(iriValue);

						String iriString;

						if (conceptTransform.getRangeString().equals("")) {
							iriString = Methods.createSlashTypeString(conceptTransform.getTargetType()) + "#"
									+ iriValue;
						} else {
							iriString = conceptTransform.getRangeString() + "#" + iriValue;
						}

//						System.out.println(iriString);

						UrlValidator urlValidator = new UrlValidator();
						if (!urlValidator.isValid(iriString)) {
							iriString = Methods.validateIRI(iriString);
							System.out.println("No valid URL for " + line);
						}

						Resource resource = model.createResource(iriString);

						Property memberProperty = model.createProperty("http://purl.org/qb4olap/cubes#memberOf");
						resource.addProperty(memberProperty, model.createResource(conceptTransform.getTargetType()));

						Resource typeResource = model.createResource("http://purl.org/qb4olap/cubes#LevelMember");
						resource.addProperty(RDF.type, typeResource);

						for (int i = 0; i < columnNames.size(); i++) {
							String value = columnValues.get(i);

							if (!value.equals("NA")) {
								if (targetPropertyMap.containsKey(i)) {
									String propertyString = targetPropertyMap.get(i);

									String rangeValue;

									if (rangeMap.containsKey(propertyString)) {
										rangeValue = rangeMap.get(propertyString);
									} else {
										rangeValue = iriGenerator.getRangeValue(propertyString, targetTBoxModel);
										rangeMap.put(propertyString, rangeValue);
									}

									Property property = model.createProperty(propertyString);

									if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
										Literal literal = model.createLiteral(value);
										resource.addLiteral(property, literal);
									} else {
										value = Methods.formatURL(value);
										String propertyValueIRI = rangeValue + "#" + value;
										resource.addProperty(property, model.createResource(propertyValueIRI));
									}
								}
							}
						}

						count++;
					} else {
						// System.out.println("Skipping Line: " + count + " - " + line);
						// System.out.println("Found Columns: " + columnValues.size());

						faultList.add(count);
					}

					/*
					 * if (Methods.checkToSaveModel(count, numOfFiles, model)) { model =
					 * ModelFactory.createDefaultModel(); numOfFiles++; }
					 */

					if (Methods.checkToSaveModel(count, targetABoxFile, model, "csv")) {
						model = ModelFactory.createDefaultModel();
					}
				}

				/*
				 * if (model.size() > 0) { Methods.checkToSaveModel(numOfFiles, model); }
				 */

				if (model.size() > 0) {
					Methods.checkToSaveModel(targetABoxFile, model);
				}

				System.out.println("Total count: " + count);

				System.out.println("Total line skipped: " + faultList.size());
//				 return Methods.mergeAllTempFiles(numOfFiles, targetABoxFile);

				Model finalModel = Methods.readModelFromPath(targetABoxFile);

				if (finalModel != null) {
					return Methods.saveModel(finalModel, targetABoxFile);
				} else {
					return "Failed to write the full model.";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Variables.ERROR_READING_FILE;
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return "";

	}

	public String generateLevelEntryFromLargeRDF(String sourceFile, String mapFile, String targetTBoxFile,
			String targetABoxFile, String provGraph) {
//		System.out.println(sourceFile);
//		System.out.println(mapFile);
//		System.out.println(targetTBoxFile);
//		System.out.println(targetABoxFile);
//		System.out.println(provGraph);
		
//		int numOfFiles = FileSeparation.splitFile(sourceFile, Variables.BYTES_PER_SPLIT);
//		System.out.println("Split Num: " + numOfFiles);
//		
//		FileSeparation.createModels(numOfFiles);
//		System.out.println("Model created");
		
		int numOfFiles = FileSeparation.splitFileByLine(sourceFile, Variables.MAX_SPLIT_LINE);
		
		Model provModel = ModelFactory.createDefaultModel();
		try {
			provModel.read(provGraph);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Prov file";
		}
		
		Model mapModel = ModelFactory.createDefaultModel();
		try {
			mapModel.read(mapFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Mapping file";
		}
		
		Model targetTBoxModel = ModelFactory.createDefaultModel();
		try {
			targetTBoxModel.read(targetTBoxFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Target TBox file";
		}

		Model completeModel = ModelFactory.createDefaultModel();
		completeModel.add(mapModel);
		completeModel.add(targetTBoxModel);
		
		prefixExtraction.extractPrefix(mapFile);

		String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
				+ "PREFIX	qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX   qb4o:   <http://purl.org/qb4olap/cubes#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + "SELECT * WHERE { \r\n"
				+ "?concept a map:ConceptMapper. \r\n" + "?concept map:sourceConcept ?stype. \r\n"
				+ "?concept map:targetConcept ?ttype. \r\n" + "?concept map:iriValueType ?iritype. \r\n"
				+ "?concept map:iriValue ?irivalue. \r\n" + "?ttype a qb4o:LevelProperty. \r\n"
				+ "OPTIONAL {?ttype rdfs:range ?range.}" + "?mapper a map:PropertyMapper. \r\n"
				+ "?mapper map:ConceptMapper ?concept. \r\n" + "?mapper map:sourceProperty ?sprop. \r\n"
				+ "?mapper map:targetProperty ?tprop. \r\n" + "}";

		ResultSet resultSet = Methods.executeQuery(completeModel, sparql);
//		Methods.print(resultSet);
		completeModel.close();

		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String sourceType = querySolution.get("stype").toString();

			String concept = querySolution.get("concept").toString();
			String targetType = querySolution.get("ttype").toString();
			String iriValue = querySolution.get("irivalue").toString();
			String iriType = querySolution.get("iritype").toString();

			String rangeString = "";
			if (querySolution.get("range") != null) {
				rangeString = querySolution.get("range").toString();
			}

//			String mapper = querySolution.get("mapper").toString();
			String sourceProperty = querySolution.get("sprop").toString();
			String targetProperty = querySolution.get("tprop").toString();

			if (conceptMap.containsKey(concept)) {
				ConceptTransform conceptTransform = conceptMap.get(concept);
				conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
				conceptMap.replace(concept, conceptTransform);
			} else {
				ConceptTransform conceptTransform = new ConceptTransform();
				conceptTransform.setConcept(concept);
				conceptTransform.setSourceType(sourceType);
				conceptTransform.setTargetType(targetType);
				conceptTransform.setIriValue(iriValue);
				conceptTransform.setIriValueType(iriType);
				conceptTransform.setRangeString(rangeString);
				conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
				conceptMap.put(concept, conceptTransform);
			}
		}
		
		// if you switch back to turtle from nt
		// change the ending loop condition to n + 1
		for (int i = 1; i <= numOfFiles; i++) {
			String tinySourcePath = Variables.MODEL_DIR + "\\model" + i + ".nt";
			
//			prefixExtraction.extractPrefix(tinySourcePath);
			
			try {
				//			System.out.println("Starting: " + tinySourcePath);
				Model model = ModelFactory.createDefaultModel();
				Model sourceModel = ModelFactory.createDefaultModel();
				sourceModel.read(tinySourcePath);
				model = extractLevelEntryData(targetABoxFile, sourceModel, targetTBoxModel, provModel, conceptMap,
						model);
				
				if (model.size() > 0) {
					Methods.checkToSaveModel(targetABoxFile, model);
				}
				Methods.saveModel(provModel, provGraph);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error: " + e.getMessage());
				System.out.println("This file has some errors: " + tinySourcePath);
			}
		}

//		return "Success.\nFile saved: " + targetABoxFile;
		Model finalModel = Methods.readModelFromPath(targetABoxFile);

		if (finalModel != null) {
			return Methods.saveModel(finalModel, targetABoxFile);
		} else {
			return "Failed to write the full model.";
		}
	}
	
	public String generateLevelEntryFromTinyRDF(String sourceFile, String mapFile, String targetTBoxFile,
			String targetABoxFile, String provGraph) {
		Model provModel = ModelFactory.createDefaultModel();
		try {
			provModel.read(provGraph);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Prov file";
		}
		
		Model mapModel = ModelFactory.createDefaultModel();
		try {
			mapModel.read(mapFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Mapping file";
		}
		
		Model targetTBoxModel = ModelFactory.createDefaultModel();
		try {
			targetTBoxModel.read(targetTBoxFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Target TBox file";
		}

		Model completeModel = ModelFactory.createDefaultModel();
		completeModel.add(mapModel);
		completeModel.add(targetTBoxModel);
		
		prefixExtraction.extractPrefix(mapFile);
		prefixExtraction.extractPrefix(sourceFile);

		String sparql = "PREFIX map: <http://www.map.org/example#>\r\n"
				+ "PREFIX	qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX   qb4o:   <http://purl.org/qb4olap/cubes#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + "SELECT * WHERE { \r\n"
				+ "?concept a map:ConceptMapper. \r\n" + "?concept map:sourceConcept ?stype. \r\n"
				+ "?concept map:targetConcept ?ttype. \r\n" + "?concept map:iriValueType ?iritype. \r\n"
				+ "?concept map:iriValue ?irivalue. \r\n" + "?ttype a qb4o:LevelProperty. \r\n"
				+ "OPTIONAL {?ttype rdfs:range ?range.}" + "?mapper a map:PropertyMapper. \r\n"
				+ "?mapper map:ConceptMapper ?concept. \r\n" + "?mapper map:sourceProperty ?sprop. \r\n"
				+ "?mapper map:targetProperty ?tprop. \r\n" + "}";

		ResultSet resultSet = Methods.executeQuery(completeModel, sparql);
//		Methods.print(resultSet);
		completeModel.close();

		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String sourceType = querySolution.get("stype").toString();

			String concept = querySolution.get("concept").toString();
			String targetType = querySolution.get("ttype").toString();
			String iriValue = querySolution.get("irivalue").toString();
			String iriType = querySolution.get("iritype").toString();

			String rangeString = "";
			if (querySolution.get("range") != null) {
				rangeString = querySolution.get("range").toString();
			}

//			String mapper = querySolution.get("mapper").toString();
			String sourceProperty = querySolution.get("sprop").toString();
			String targetProperty = querySolution.get("tprop").toString();

			if (conceptMap.containsKey(concept)) {
				ConceptTransform conceptTransform = conceptMap.get(concept);
				conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
				conceptMap.replace(concept, conceptTransform);
			} else {
				ConceptTransform conceptTransform = new ConceptTransform();
				conceptTransform.setConcept(concept);
				conceptTransform.setSourceType(sourceType);
				conceptTransform.setTargetType(targetType);
				conceptTransform.setIriValue(iriValue);
				conceptTransform.setIriValueType(iriType);
				conceptTransform.setRangeString(rangeString);
				conceptTransform.getPropertyMap().put(sourceProperty, targetProperty);
				conceptMap.put(concept, conceptTransform);
			}
		}

		Model model = ModelFactory.createDefaultModel();

		Model sourceModel = ModelFactory.createDefaultModel();
		sourceModel.read(sourceFile);
		

		model = extractLevelEntryData(targetABoxFile, sourceModel, targetTBoxModel, provModel, conceptMap, model);

		if (model.size() > 0) {
			Methods.checkToSaveModel(targetABoxFile, model);
		}
		
		Methods.saveModel(provModel, provGraph);

		Model finalModel = Methods.readModelFromPath(targetABoxFile);

		if (finalModel != null) {
			return Methods.saveModel(finalModel, targetABoxFile);
		} else {
			return "Failed to write the full model.";
		}
	}

	private Model extractLevelEntryData(String targetABoxFile, Model sourceModel, Model targetTBoxModel,
			Model provModel, LinkedHashMap<String, ConceptTransform> conceptMap, Model model) {
		for (String concept : conceptMap.keySet()) {
//				System.out.println(concept);
			
			LinkedHashMap<String, String> rangeMap = new LinkedHashMap<>();
			ConceptTransform conceptTransform = conceptMap.get(concept);

			String sparqlString = "SELECT DISTINCT ?s " + "WHERE " + "{" + "?s a <" + conceptTransform.getSourceType()
					+ ">." + "?s ?p ?o." + "}";
			
//				System.out.println(sparqlString);

			ResultSet set = Methods.executeQuery(sourceModel, sparqlString);
//				Methods.print(set);

			int count = 0;
			while (set.hasNext()) {
				QuerySolution querySolution = set.next();
				String subjectString = querySolution.get("s").toString();

				Resource sourceResource = sourceModel.createResource(subjectString);

				String iriValue = "";

				StringBuilder stringBuilder = new StringBuilder(iriValue);
				if (conceptTransform.getIriValueType().contains("SourceAttribute")) {
					String iriPropertyName = conceptTransform.getIriValue();

					Property tempProperty = sourceModel.createProperty(iriPropertyName);
					StmtIterator stmtIterator = sourceResource.listProperties(tempProperty);

					while (stmtIterator.hasNext()) {
						Statement statement = stmtIterator.nextStatement();

						stringBuilder.append(statement.getObject());
					}
					stmtIterator.close();

					iriValue = stringBuilder.toString();
				} else if (conceptTransform.getIriValueType().contains("Expression")) {
					String expressionString = conceptTransform.getIriValue();
					LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
					
					StmtIterator stmtIterator = sourceResource.listProperties();
					while (stmtIterator.hasNext()) {
						Statement statement = stmtIterator.nextStatement();
						
						valueMap.put(prefixExtraction.assignPrefix(statement.getPredicate().toString()), Methods.getRDFNodeValue(statement.getObject()));
					}
					stmtIterator.close();
					
					EquationHandler equationHandler = new EquationHandler();
					Object valueObject = equationHandler.handleExpression(expressionString,
							valueMap);
					
					iriValue = valueObject.toString();
				}

				iriValue = Methods.formatURL(iriValue);

//					System.out.println(iriValue);
				String iriString = "";

				if (conceptTransform.getRangeString().equals("")) {
					iriString = Methods.createSlashTypeString(conceptTransform.getTargetType()) + "#" + iriValue;
				} else {
					iriString = conceptTransform.getRangeString() + "#" + iriValue;
				}

//					System.out.println(iriString);

				UrlValidator urlValidator = new UrlValidator();
				if (!urlValidator.isValid(iriString)) {
					iriString = Methods.validateIRI(iriString);
					System.out.println("No valid URL for " + subjectString);
				}

				Resource targetResource = model.createResource(iriString);

				Resource provResource = provModel.createResource(subjectString);
				Property owlProperty = provModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");
				provResource.addProperty(owlProperty, provModel.createResource(iriString));

//					System.out.println(conceptTransform.getPropertyMap().size());
				for (String sourcePropertyString : conceptTransform.getPropertyMap().keySet()) {
					Property sourceProperty = sourceModel.createProperty(sourcePropertyString);
					String targetPropertyString = conceptTransform.getPropertyMap().get(sourcePropertyString);
					Property targetProperty = model.createProperty(targetPropertyString);

					StmtIterator stmtIterator = sourceResource.listProperties(sourceProperty);

					while (stmtIterator.hasNext()) {
						Statement statement = stmtIterator.nextStatement();

						// targetResource.addProperty(targetProperty, statement.getObject());

						String rangeValue;

						if (rangeMap.containsKey(targetPropertyString)) {
							rangeValue = rangeMap.get(targetPropertyString);
						} else {
							rangeValue = iriGenerator.getRangeValue(targetPropertyString, targetTBoxModel);
							rangeMap.put(targetPropertyString, rangeValue);
						}

						if (rangeValue.contains("http://www.w3.org/2001/XMLSchema#")) {
							targetResource.addProperty(targetProperty, statement.getObject());
						} else {
							String value = Methods.formatURL(statement.getObject().toString());
							String propertyValueIRI = rangeValue + "#" + value;
							targetResource.addProperty(targetProperty, model.createResource(propertyValueIRI));
						}
					}
					stmtIterator.close();

					Property memberProperty = model.createProperty("http://purl.org/qb4olap/cubes#memberOf");
					targetResource.addProperty(memberProperty, model.createResource(conceptTransform.getTargetType()));

					Resource typeResource = model.createResource("http://purl.org/qb4olap/cubes#LevelMember");
					targetResource.addProperty(RDF.type, typeResource);
				}

				count++;

				if (Methods.checkToSaveModel(count, targetABoxFile, model, "rdf")) {
					model = ModelFactory.createDefaultModel();
				}
			}
		}
		return model;
	}
	
	public String generateLevelEntryFromRDF(String sourceFile, String mapFile, String targetTBoxFile,
			String targetABoxFile, String provGraph) {
		
		if (Methods.isJenaAccessible(sourceFile)) {
			return generateLevelEntryFromTinyRDF(sourceFile, mapFile, targetTBoxFile, targetABoxFile, provGraph);
		} else {
			return generateLevelEntryFromLargeRDF(sourceFile, mapFile, targetTBoxFile, targetABoxFile, provGraph);
		}
	}
}
