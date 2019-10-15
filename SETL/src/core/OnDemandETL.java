package core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import helper.Methods;
import model.ConceptTransform;
import model.MapperTransform;

public class OnDemandETL {
	Methods methods;
	String datasetString = "";
	
	static String targetABoxString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\target_abox.ttl";
	static String targetTBoxString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\bd_tbox.ttl";
	static String sourceABoxString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\source_abox.ttl";
	static String mapString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\map.ttl";
	static String demoMapString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\demo_map.ttl";
	static String demoTargetABoxString = "C:\\Users\\Amrit\\Documents\\OnDemandETL\\demo_target.ttl";

	static String sparqlQueryString = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
			"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
			"SELECT ?admGeographyDim_AdmUnitTwo ?ageDim_ageGroup ?residenceDim_residence (SUM(<http://www.w3.org/2001/XMLSchema#long>(?m1)) as ?numberOfPopulation_sum) \r\n" + 
			"WHERE {\r\n" + 
			"?o a qb:Observation .\r\n" + 
			"?o qb:dataSet <http://linked-statistics-bd.org/2011/data#populationByAdm5ResidenceAgeGroup> .\r\n" + 
			"?o <http://linked-statistics-bd.org/2011/mdProperty#numberOfPopulation> ?m1 .\r\n" + 
			"?o <http://linked-statistics-bd.org/2011/mdProperty#admUnitFive> ?admGeographyDim_admUnitFive .\r\n" + 
			"?admGeographyDim_admUnitFive qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#admUnitFive> .\r\n" + 
			"?admGeographyDim_admUnitFive <http://linked-statistics-bd.org/2011/mdAttribute#inAdmFour> ?admGeographyDim_admUnitFour .\r\n" + 
			"?admGeographyDim_admUnitFour qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#admUnitFour> .\r\n" + 
			"?admGeographyDim_admUnitFour <http://linked-statistics-bd.org/2011/mdAttribute#inAdmThree> ?admGeographyDim_admUnitThree .\r\n" + 
			"?admGeographyDim_admUnitThree qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#admUnitThree> .\r\n" + 
			"?admGeographyDim_admUnitThree <http://linked-statistics-bd.org/2011/mdAttribute#inAdmTwo> ?admGeographyDim_AdmUnitTwo .\r\n" + 
			"?admGeographyDim_AdmUnitTwo qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#AdmUnitTwo> .\r\n" + 
			"?o <http://linked-statistics-bd.org/2011/mdProperty#ageGroup> ?ageDim_ageGroup .\r\n" + 
			"?ageDim_ageGroup qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#ageGroup> .\r\n" + 
			"?o <http://linked-statistics-bd.org/2011/mdProperty#residence> ?residenceDim_residence .\r\n" + 
			"}\r\n" + 
			"GROUP BY ?admGeographyDim_AdmUnitTwo ?ageDim_ageGroup ?residenceDim_residence\r\n" + 
			"ORDER BY ?admGeographyDim_AdmUnitTwo ?ageDim_ageGroup ?residenceDim_residence";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		  String resultString = new OnDemandETL().performOnDemandETL(sparqlQueryString,
		  sourceABoxString, mapString, targetABoxString, targetTBoxString);
		  System.out.println(resultString);
		 
		
		
		String sparqlQueryString = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
				"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
				"SELECT ?ageDim_ageGroup (SUM(<http://www.w3.org/2001/XMLSchema#long>(?m1)) as ?numberOfPopulation_sum) \r\n" + 
				"WHERE {\r\n" + 
				"?o a qb:Observation .\r\n" + 
				"?o qb:dataSet <http://linked-statistics-bd.org/2011/data#populationByAdm5ResidenceAgeGroup> .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#numberOfPopulation> ?m1 .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#ageGroup> ?ageDim_ageGroup .\r\n" + 
				"?ageDim_ageGroup qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#ageGroup> .\r\n" +  
				"}\r\n" + 
				"GROUP BY ?ageDim_ageGroup\r\n"; 

		
		Methods methods = new Methods();
		Model model = methods.readModelFromPath(demoTargetABoxString);
		
		ResultSet resultSet = methods.executeQuery(model, sparqlQueryString);
		methods.print(resultSet);
	}

	
	private String performOnDemandETL(String sparqlQueryString, String sourceABoxString, String mapString,
			String targetABoxString, String targetTBoxString) {
		// TODO Auto-generated method stub
		methods = new Methods();
		// System.out.println("Extract Levels");
		// Methods.printTime();
		ArrayList<String> queryLevelsArrayList = extractRequiredLevels(sparqlQueryString);
		// Methods.printTime();
		// Methods.print(queryLevelsArrayList);

		// System.out.println("Extract Observation");
		// Methods.printTime();
		String observationString = extractObservation(sparqlQueryString);
		// Methods.printTime();

		if (observationString == null) {
			return "No observation";
		}

		// System.out.println("Extract Facts");
		// Methods.printTime();
		ArrayList<String> queryFactArrayList = extractRequiredFacts(sparqlQueryString, observationString);
		// Methods.printTime();
		// Methods.print(queryFactArrayList);

		Model targetABoxModel = methods.readModelFromPath(targetABoxString);

		// System.out.println("Check levels");
		// Methods.printTime();
		ArrayList<String> requiredLevelLArrayList = checkRequiredLevels(targetABoxModel, queryLevelsArrayList);
		// Methods.printTime();

		// System.out.println("Check facts");
		// Methods.printTime();
		ArrayList<String> requiredFactArrayList = checkRequiredFacts(targetABoxModel, queryFactArrayList);
		// Methods.printTime();

		// System.out.println("\nAll required levels");
		// Methods.print(requiredLevelLArrayList);

		// System.out.println("\nAll required facts");
		// Methods.print(requiredFactArrayList);

		Model mapModel = methods.readModelFromPath(mapString);
		Model sourceABoxModel = methods.readModelFromPath(sourceABoxString);
		Model targetTBoxModel = methods.readModelFromPath(targetTBoxString);
		Model targetModel = ModelFactory.createDefaultModel();

		Model model = ModelFactory.createDefaultModel();
		model.add(sourceABoxModel);
		model.add(mapModel);

		LinkedHashMap<String, String> prefixMap = Methods.extractPrefixes(mapString);
		// prefixMap.putAll(Methods.extractPrefixes(mapString));

		generateFactData(datasetString, model, mapModel, sourceABoxModel, targetTBoxModel, targetModel, prefixMap,
				requiredFactArrayList);

		for (String levelString : requiredLevelLArrayList) {
			generateLevelData(Methods.bracketString(levelString), model, mapModel, sourceABoxModel, targetTBoxModel,
					targetModel, prefixMap, null);
		}

		// Methods.print(targetModel);

		targetModel.add(targetABoxModel);
		
		methods.saveModel(targetModel, demoTargetABoxString);

		ResultSet finalResultSet = Methods.executeQuery(targetModel, sparqlQueryString);
		Methods.print(finalResultSet);

		return "Done";
	}

	private void generateLevelData(String datasetString, Model model, Model mapModel, Model sourceABoxModel,
			Model targetTBoxModel, Model targetModel, LinkedHashMap<String, String> prefixMap,
			ArrayList<String> requiredFactArrayList) {
		// TODO Auto-generated method stub
		System.out.println(datasetString);
		
		String sparql = "\nPREFIX map: <http://www.map.org/example#>\n" + "SELECT * WHERE {"
				+ "?concept a map:ConceptMapper."
				+ "?concept map:targetConcept " + datasetString + "."
				+ "?concept map:sourceConcept ?sourcetype."
				+ "?concept map:iriValue ?iri."
				+ "?concept map:iriValueType ?iritype."
				+ "?mapper a map:PropertyMapper."
				+ "?mapper map:ConceptMapper ?concept."
				+ "?mapper map:sourceProperty ?source."
				+ "?mapper map:sourcePropertyType ?propertytype."
				+ "?mapper map:targetProperty ?target." + "}";

		ResultSet resultSet = Methods.executeQuery(mapModel, sparql);
		// Methods.print(resultSet);

		String targetTypeString = datasetString.substring(1, datasetString.length() - 1);
		
		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String conceptString = querySolution.get("concept").toString();
			String sourceTypeString = querySolution.get("sourcetype").toString();
			String iriValueString = querySolution.get("iri").toString();
			String iriValueTypeString = querySolution.get("iritype").toString();

			String mapperString = querySolution.get("mapper").toString();
			String targetPropertyString = querySolution.get("target").toString();
			String sourcePropertyString = querySolution.get("source").toString();
			String sourcePropertyTypeString = querySolution.get("propertytype").toString();

			MapperTransform mapperTransform = new MapperTransform(sourcePropertyString, sourcePropertyTypeString,
					targetPropertyString);

			if (conceptMap.containsKey(conceptString)) {
				ConceptTransform conceptTransform = conceptMap.get(conceptString);
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.replace(conceptString, conceptTransform);
			} else {
				ConceptTransform conceptTransform = new ConceptTransform();
				conceptTransform.setConcept(conceptString);
				conceptTransform.setTargetType(targetTypeString);
				conceptTransform.setSourceType(sourceTypeString);
				conceptTransform.setIriValue(iriValueString);
				conceptTransform.setIriValueType(iriValueTypeString);
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.put(conceptString, conceptTransform);
			}
		}

		for (String conceptString : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(conceptString);
			String typeString = Methods.bracketString(conceptTransform.getSourceType());

			String sparqlString = "SELECT * WHERE {" + "?s a " + typeString + "." + "?s ?p ?o.}";

			// System.out.println(sparqlString);
			ResultSet set = Methods.executeQuery(sourceABoxModel, sparqlString);
			// Methods.print(set);

			String currentSubjectString = "";
			LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();

			Model provModel = ModelFactory.createDefaultModel();

			while (set.hasNext()) {
				QuerySolution querySolution = (QuerySolution) set.next();
				String resourceString = querySolution.get("s").toString();
				String predicateString = querySolution.get("p").toString();
				RDFNode object = querySolution.get("o");

				predicateString = Methods.assignPrefix(prefixMap, predicateString);

				if (currentSubjectString.equals(resourceString)) {
					valueMap.put(predicateString, methods.getRDFNodeValue(object));
				} else {
					if (currentSubjectString.equals("")) {
						currentSubjectString = resourceString;
						valueMap.put(predicateString, methods.getRDFNodeValue(object));
					} else {

						IRIGenerator generator = new IRIGenerator();
						
						String iriValue = generator.getIRIValue(conceptTransform.getIriValueType(),
								methods.assignPrefix(prefixMap, conceptTransform.getIriValue()), mapModel, valueMap, provModel);
						String provIRI = "";

						String rangeValue = generator.getRangeValue(targetTypeString, targetTBoxModel);

						iriValue = iriValue.replaceAll("\\s+", "_").toLowerCase();
						if (rangeValue == null) {
							provIRI = targetTypeString + "#" + iriValue;
						} else {
							provIRI = rangeValue + "#" + iriValue;
						}
						
						/*
						 * if (datasetString.equals(
						 * "<http://linked-statistics-bd.org/2011/mdProperty#admUnitFive>")) {
						 * System.out.println(provIRI); }
						 */

						boolean isAdded = false;
						Resource resource = null;
						for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
							MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
									.get(mapperString);

							Property property = targetModel.createProperty(mapperTransform.getTargetProperty());

							String propertyType = mapperTransform.getSourcePropertyType();

							Object valueObject = null;

							if (propertyType.contains("Expression")) {
								EquationHandler equationHandler = new EquationHandler();
								valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
										valueMap);
							} else {
								valueObject = valueMap
										.get(Methods.assignPrefix(prefixMap, mapperTransform.getSourceProperty()));
							}

							String rangeValueTarget = generator.getRangeValue(mapperTransform.getTargetProperty(),
									targetTBoxModel);

							if (valueObject != null) {
								if (!isAdded) {
									resource = targetModel.createResource(provIRI);

									Property property2 = targetModel.createProperty("http://purl.org/qb4olap/cubes#memberOf");
									resource.addProperty(property2, targetModel.createResource(targetTypeString));

									resource.addProperty(RDF.type, ResourceFactory.createResource("http://purl.org/qb4olap/cubes#LevelMember"));
									
									isAdded = true;
								}

								if (rangeValueTarget.contains("http://www.w3.org/2001/XMLSchema#")) {
									Literal literal = targetModel.createTypedLiteral(valueObject);
									resource.addLiteral(property, literal);
								} else {
									valueObject = valueObject.toString().replaceAll("\\s+", "_").toLowerCase();
									String propertyValueIRI = rangeValueTarget + "#" + valueObject;
									resource.addProperty(property, targetModel.createResource(propertyValueIRI));
								}
							} else {
								System.out.println("Value Object null");
							}
						}

						currentSubjectString = resourceString;
						valueMap = new LinkedHashMap<String, Object>();
						valueMap.put(predicateString, methods.getRDFNodeValue(object));
					}
				}
			}
		}
	}

	private void generateFactData(String datasetString, Model model, Model mapModel, Model sourceABoxModel,
			Model targetTBoxModel, Model targetModel, LinkedHashMap<String, String> prefixMap,
			ArrayList<String> requiredFactArrayList) {
		// TODO Auto-generated method stub
		String sparql = "\nPREFIX map: <http://www.map.org/example#>\n" + "SELECT * WHERE {"
				+ "?concept a map:ConceptMapper." + "?concept map:targetConcept " + datasetString + "."
				+ "?concept map:sourceConcept ?sourcetype." + "?concept map:iriValue ?iri."
				+ "?concept map:iriValueType ?iritype." + "?mapper a map:PropertyMapper."
				+ "?mapper map:ConceptMapper ?concept." + "?mapper map:sourceProperty ?source."
				+ "?mapper map:sourcePropertyType ?propertytype." + "?mapper map:targetProperty ?target." + "}";

		ResultSet resultSet = Methods.executeQuery(mapModel, sparql);
		// Methods.print(resultSet);

		String targetTypeString = datasetString.substring(1, datasetString.length() - 1);
		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String conceptString = querySolution.get("concept").toString();
			String sourceTypeString = querySolution.get("sourcetype").toString();
			String iriValueString = querySolution.get("iri").toString();
			String iriValueTypeString = querySolution.get("iritype").toString();

			String mapperString = querySolution.get("mapper").toString();
			String targetPropertyString = querySolution.get("target").toString();
			String sourcePropertyString = querySolution.get("source").toString();
			String sourcePropertyTypeString = querySolution.get("propertytype").toString();

			MapperTransform mapperTransform = new MapperTransform(sourcePropertyString, sourcePropertyTypeString,
					targetPropertyString);

			if (conceptMap.containsKey(conceptString)) {
				ConceptTransform conceptTransform = conceptMap.get(conceptString);
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.replace(conceptString, conceptTransform);
			} else {
				ConceptTransform conceptTransform = new ConceptTransform();
				conceptTransform.setConcept(conceptString);
				conceptTransform.setTargetType(targetTypeString);
				conceptTransform.setSourceType(sourceTypeString);
				conceptTransform.setIriValue(iriValueString);
				conceptTransform.setIriValueType(iriValueTypeString);
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.put(conceptString, conceptTransform);
			}
		}

		for (String conceptString : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(conceptString);
			String typeString = Methods.bracketString(conceptTransform.getSourceType());

			String sparqlString = "SELECT * WHERE {" + "?s a " + typeString + "." + "?s ?p ?o.}";

			// System.out.println(sparqlString);
			ResultSet set = Methods.executeQuery(sourceABoxModel, sparqlString);
			// Methods.print(set);

			String currentSubjectString = "";
			LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();

			Model provModel = ModelFactory.createDefaultModel();

			while (set.hasNext()) {
				QuerySolution querySolution = (QuerySolution) set.next();
				String resourceString = querySolution.get("s").toString();
				String predicateString = querySolution.get("p").toString();
				RDFNode object = querySolution.get("o");

				predicateString = Methods.assignPrefix(prefixMap, predicateString);

				if (currentSubjectString.equals(resourceString)) {
					valueMap.put(predicateString, methods.getRDFNodeValue(object));
				} else {
					if (currentSubjectString.equals("")) {
						currentSubjectString = resourceString;
						valueMap.put(predicateString, methods.getRDFNodeValue(object));
					} else {

						IRIGenerator generator = new IRIGenerator();
						
						String iriValue = generator.getIRIValue(conceptTransform.getIriValueType(),
								conceptTransform.getIriValue(), mapModel, valueMap, provModel);
						String provIRI = "";

						String rangeValue = generator.getRangeValue(targetTypeString, targetTBoxModel);

						if (rangeValue == null) {
							provIRI = targetTypeString + "#" + iriValue;
						} else {
							provIRI = rangeValue + "#" + iriValue;
						}

						boolean isAdded = false;
						Resource resource = null;
						for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
							MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
									.get(mapperString);

							if (requiredFactArrayList.contains(mapperTransform.getTargetProperty())) {
								Property property = targetModel.createProperty(mapperTransform.getTargetProperty());

								String propertyType = mapperTransform.getSourcePropertyType();

								Object valueObject = null;

								if (propertyType.contains("Expression")) {
									EquationHandler equationHandler = new EquationHandler();
									valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
											valueMap);
								} else {
									valueObject = valueMap
											.get(Methods.assignPrefix(prefixMap, mapperTransform.getSourceProperty()));
								}

								String rangeValueTarget = generator.getRangeValue(mapperTransform.getTargetProperty(),
										targetTBoxModel);

								if (valueObject != null) {
									if (!isAdded) {
										resource = targetModel.createResource(provIRI);

										Property property2 = targetModel
												.createProperty("http://purl.org/linked-data/cube#dataSet");
										resource.addProperty(property2, targetModel.createResource(targetTypeString));

										resource.addProperty(RDF.type, ResourceFactory
												.createResource("http://purl.org/linked-data/cube#Observation"));

										isAdded = true;
									}

									if (rangeValueTarget.contains("http://www.w3.org/2001/XMLSchema#")) {
										Literal literal = targetModel.createTypedLiteral(valueObject);
										resource.addLiteral(property, literal);
									} else {
										valueObject = valueObject.toString().replaceAll("\\s+", "_").toLowerCase();
										String propertyValueIRI = rangeValueTarget + "#" + valueObject;
										resource.addProperty(property, targetModel.createResource(propertyValueIRI));
									}
								} else {
									System.out.println("Value Object null");
								}
							}
						}

						currentSubjectString = resourceString;
						valueMap = new LinkedHashMap<String, Object>();
						valueMap.put(predicateString, methods.getRDFNodeValue(object));
					}
				}
			}
		}
	}

	private ArrayList<String> checkRequiredFacts(Model targetABoxModel, ArrayList<String> queryFactArrayList) {
		// TODO Auto-generated method stub
		ArrayList<String> requiredArrayList = new ArrayList<String>();

		for (String factString : queryFactArrayList) {
			String sparql = "SELECT * WHERE {" + "?s a <http://purl.org/linked-data/cube#Observation>." + " ?s "
					+ factString + " ?o.} LIMIT 1";

			ResultSet resultSet = Methods.executeQuery(targetABoxModel, sparql);
			// Methods.print(resultSet);
			
			int count = 0;
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subjectString = querySolution.get("?s").toString();

				count++;
			}

			if (count == 0) {
				// System.out.println(factString);
				requiredArrayList.add(factString.substring(1, factString.length() - 1));
			}
		}

		return requiredArrayList;
	}

	private ArrayList<String> checkRequiredLevels(Model targetABoxModel,
			ArrayList<String> queryLevelsArrayList) {
		// TODO Auto-generated method stub
		ArrayList<String> requiredArrayList = new ArrayList<String>();

		for (String levelString : queryLevelsArrayList) {
			String sparql = "SELECT DISTINCT ?s WHERE {" + "?s <http://purl.org/qb4olap/cubes#memberOf> <" + levelString
					+ ">." + " ?s ?p ?o.}  LIMIT 1";

			ResultSet resultSet = Methods.executeQuery(targetABoxModel, sparql);
			// Methods.print(resultSet);

			int count = 0;
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subjectString = querySolution.get("?s").toString();

				count++;
			}

			if (count == 0) {
				requiredArrayList.add(levelString);
			}
		}

		return requiredArrayList;
	}

	private ArrayList<String> extractRequiredFacts(String sparqlQueryString, String observationString) {
		// TODO Auto-generated method stub
		ArrayList<String> propertyList = new ArrayList<String>();

		String regEx = "(\\" + observationString + ")(\\s+)(\\S+)(\\s+)(\\S+)";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sparqlQueryString);

		while (matcher.find()) {
			String propertyString = matcher.group(3).trim();

			// System.out.println(propertyString);

			if (propertyString.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#") || propertyString.equals("a")) {
				continue;
			} else if (propertyString.contains("dataSet")) {
				datasetString = matcher.group(5).trim();
			} else {
				// System.out.println("added");
				propertyList.add(propertyString);
			}
		}
		return propertyList;
	}

	private ArrayList<String> extractRequiredLevels(String sparqlQueryString) {
		// TODO Auto-generated method stub
		ArrayList<String> requiredLevelsArrayList = new ArrayList<String>();

		String regEx = "(\\?\\S+)(\\s+)(qb4o:memberOf)(\\s+)(<)([^>]+)(>)";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sparqlQueryString);

		while (matcher.find()) {
			String observationString = matcher.group(6).trim();
			requiredLevelsArrayList.add(observationString);
		}

		return requiredLevelsArrayList;
	}

	private String extractObservation(String sparqlQueryString) {
		// TODO Auto-generated method stub
		// System.out.println(sparqlQueryString);
		String regEx = "(\\{)(\\s*)(\\S+)(\\s+)(a)(\\s+)(qb:Observation)";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(sparqlQueryString);

		while (matcher.find()) {
			String observationString = matcher.group(3).trim();
			return observationString;
		}
		return null;
	}
}
