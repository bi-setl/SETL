package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectOutputStream.PutField;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.library.print;
import org.apache.jena.vocabulary.RDF;

import helper.FileMethods;
import helper.Methods;
import model.ConceptTransform;
import model.MapperTransform;
import view.PanelETL;

public class MultipleTransformation {
	private Methods fileMethods;
	private LinkedHashMap<String, String> prefixMap;
	private LinkedHashMap<String, String> resourceMap;
	
	public MultipleTransformation() {
		super();
		fileMethods = new Methods();
		prefixMap = new LinkedHashMap<>();
		resourceMap = new LinkedHashMap<>();
	}
	
	public static void main(String[] args) {
		String basePath = "C:\\Users\\Amrit\\Documents\\1\\";
		
		String firstSourcePath = basePath + "InstanceGenerator_deterflow.ttl";
		String secondSourcePath = basePath + "InstanceGenerator_inputflow.ttl";
		String mapPath = basePath + "map_version_1605008727221.ttl";
		String targetPath = basePath + "joint.ttl";
		
		MultipleTransformation multipleTransformation = new MultipleTransformation();
		multipleTransformation.transformMultipleLiteral(firstSourcePath, secondSourcePath, mapPath, targetPath);
	}
	
	public String transformMultipleLiteral(String firstSourcePath, String secondSourcePath,
			String mappingPath, String targetPath) {
		String checkFileResult = checkFiles(firstSourcePath, mappingPath, secondSourcePath);
		
		if (checkFileResult.equals("OK")) {
			Model firstModel = fileMethods.readModelFromPath(firstSourcePath);

			if (firstModel == null) {
				return "Error in reading the source abox file. Please check syntaxes.";
			}

			Model mapModel = fileMethods.readModelFromPath(mappingPath);

			if (mapModel == null) {
				return "Error in reading the mapping file. Please check syntaxes.";
			}

			Model secondModel = fileMethods.readModelFromPath(secondSourcePath);

			if (secondModel == null) {
				return "Error in reading the target file. Please check syntaxes.";
			}
			
			LinkedHashMap<String, String> prefixMap1 = Methods.extractPrefixes(firstSourcePath);
			LinkedHashMap<String, String> prefixMap2 = Methods.extractPrefixes(secondSourcePath);
			LinkedHashMap<String, String> prefixMap3 = Methods.extractPrefixes(mappingPath);
			
			prefixMap.putAll(prefixMap1);
			prefixMap.putAll(prefixMap2);
			prefixMap.putAll(prefixMap3);
			
			Model targetModel = fileMethods.readModelFromPath(secondSourcePath);
			
			String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?concept a map:ConceptMapper. \r\n"
					+ "?concept map:sourceCommonProperty ?scommon. \r\n"
					+ "?concept map:targetCommonProperty ?tcommon. \r\n"
					+ "?concept map:sourceConcept ?stype. \r\n"
					+ "?concept map:targetConcept ?ttype. \r\n"
					+ "?concept map:iriValue ?irivalue. \r\n"
					+ "?concept map:iriValueType ?iritype. \r\n"
					+ "?concept map:operation \"" + PanelETL.MULTIPLE_TRANFORM + "\".\r\n"
					+ "?mapper a map:PropertyMapper. \r\n"
					+ "?mapper map:ConceptMapper ?concept. \r\n"
					+ "?mapper map:sourceProperty ?sprop. \r\n"
					+ "?mapper map:sourcePropertyType ?sproptype. \r\n"
					+ "?mapper map:targetProperty ?tprop. \r\n"
					+ "}\r\n";
			
			ResultSet resultSet = Methods.executeQuery(mapModel, sparqlString);
//			Methods.print(resultSet);
			
			LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String concept = querySolution.get("concept").toString();
				String sourceType = querySolution.get("stype").toString();
				String targetType = querySolution.get("ttype").toString();
				String sourceCommonProperty = querySolution.get("scommon").toString();
				String targetCommonProperty = querySolution.get("tcommon").toString();
				String iriValue = querySolution.get("irivalue").toString();
				String iriType = querySolution.get("iritype").toString();
				
				String mapper = querySolution.get("mapper").toString();
				String sourceProperty = querySolution.get("sprop").toString();
				String sourcePropertyType = querySolution.get("sproptype").toString();
				String targetProperty = querySolution.get("tprop").toString();
				
				MapperTransform mapperTransform = new MapperTransform(sourceProperty, sourcePropertyType, targetProperty);
				
				if (conceptMap.containsKey(concept)) {
					ConceptTransform conceptTransform = conceptMap.get(concept);
					conceptTransform.getMapperTransformMap().put(mapper, mapperTransform);
					conceptMap.replace(concept, conceptTransform);
				} else {
					ConceptTransform conceptTransform = new ConceptTransform();
					conceptTransform.setConcept(concept);
					conceptTransform.setSourceCommonProperty(sourceCommonProperty);
					conceptTransform.setSourceType(sourceType);
					conceptTransform.setTargetCommonProperty(targetCommonProperty);
					conceptTransform.setTargetType(targetType);
					conceptTransform.setIriValue(iriValue);
					conceptTransform.setIriValueType(iriType);
					conceptTransform.getMapperTransformMap().put(mapper, mapperTransform);
					conceptMap.put(concept, conceptTransform);
				}
			}
			
			int numOfFiles = 1, count = 0;
			
			String prefixString = Methods.getPrefixStrings(prefixMap);
		
			String prefixPath = numOfFiles + ".ttl";
			Methods.writeText(prefixPath, prefixString);
			numOfFiles++;
			
			for (String concept : conceptMap.keySet()) {
				ConceptTransform conceptTransform = conceptMap.get(concept);
				transformMultipleLiteral(firstModel, secondModel, conceptTransform, targetModel, count, numOfFiles);
			}
			
			if (targetModel.size() > 0) {
				String tempPath = numOfFiles + ".ttl";
				
				fileMethods.saveModel(targetModel, tempPath);
				targetModel = ModelFactory.createDefaultModel();
				numOfFiles++;
			}

			// fileMethods.saveModel(provGraph.model, provGraphFile);
			return Methods.mergeAllTempFiles(numOfFiles, targetPath, true);
		}
		
		return "";
	}
	
	private void transformMultipleLiteral(Model firstModel, Model secondModel, ConceptTransform conceptTransform,
			Model targetModel, int count, int numOfFiles) {
		String sourceType = Methods.bracketString(conceptTransform.getSourceType());
		String targetType = Methods.bracketString(conceptTransform.getTargetType());
		String sourceCommonProperty = Methods.bracketString(conceptTransform.getSourceCommonProperty());
		String targetCommonProperty = Methods.bracketString(conceptTransform.getTargetCommonProperty());
		
		Model model = ModelFactory.createDefaultModel();
		model.add(firstModel);
		model.add(secondModel);
		
		LinkedHashMap<String, String> subjectMap = new LinkedHashMap<String, String>();
		
		if (sourceCommonProperty.toLowerCase().contains("SourceIRI".toLowerCase()) || 
				targetCommonProperty.toLowerCase().contains("TargetIRI".toLowerCase())) {
				String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE {\r\n"
						+ "?fsub a " + sourceType + ". \r\n"
						+ "?fsub a " + targetType + ". \r\n"
						+ "}\r\n";
				
				ResultSet resultSet = Methods.executeQuery(model, sparqlString);
				
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					String firstSubject = querySolution.get("fsub").toString();
					String secondSubject = querySolution.get("ssub").toString();
					
					subjectMap.put(firstSubject, secondSubject);
				}
		} else {
			String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?fsub a " + sourceType + ". \r\n"
					+ "?fsub " + sourceCommonProperty + " ?scommon. \r\n"
					+ "?ssub a " + targetType + ". \r\n"
					+ "?ssub " + targetCommonProperty + " ?scommon. \r\n"
					+ "}\r\n";
			
//			System.out.println(sparqlString);
			ResultSet resultSet = Methods.executeQuery(model, sparqlString);
//			Methods.print(resultSet);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String firstSubject = querySolution.get("fsub").toString();
				String secondSubject = querySolution.get("ssub").toString();
				
				subjectMap.put(firstSubject, secondSubject);
			}
		}
		
		for (Map.Entry<String, String> map : subjectMap.entrySet()) {
			String firstSubject = map.getKey();
			String secondSubject = map.getValue();
			
			LinkedHashMap<String, Object> propertyMap = fetchSubjectPropertyValues(firstModel, firstSubject, sourceType);
			propertyMap.putAll(fetchSubjectPropertyValues(secondModel, secondSubject, targetType));
			
			generateABox(secondSubject, conceptTransform, propertyMap, targetModel, count, numOfFiles);
		}
	}

	public String transformMultipleLiteral(String firstSourcePath, String secondSourcePath,
			String mappingPath, String targetPath, boolean isNew) {
		Model firstModel = Methods.readModelFromPath(firstSourcePath);

		if (firstModel == null) {
			return "Error in first source abox file";
		}

		Model secondModel = Methods.readModelFromPath(secondSourcePath);

		if (secondModel == null) {
			return "Error in second source abox file";
		}

		Model mapModel = Methods.readModelFromPath(mappingPath);

		if (mapModel == null) {
			return "Error in map file";
		}
		
		Model targetModel = ModelFactory.createDefaultModel();
		
//		prefixExtraction.extractPrefix(firstSourcePath);
//		prefixExtraction.extractPrefix(secondSourcePath);
//		prefixExtraction.extractPrefix(mappingPath);
		
		/*
		 * Model model = ModelFactory.createDefaultModel(); model.add(firstModel);
		 * model.add(secondModel); model.add(mapModel);
		 */

		String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE {\r\n"
				+ "?concept a map:ConceptMapper. \r\n"
				+ "?concept map:sourceCommonProperty ?scommon. \r\n"
				+ "?concept map:targetCommonProperty ?tcommon. \r\n"
				+ "?concept map:sourceConcept ?stype. \r\n"
				+ "?concept map:targetConcept ?ttype. \r\n"
				+ "?concept map:iriValue ?irivalue. \r\n"
				+ "?concept map:iriValueType ?iritype. \r\n"
				+ "?mapper a map:PropertyMapper. \r\n"
				+ "?mapper map:ConceptMapper ?concept. \r\n"
				+ "?mapper map:sourceProperty ?sprop. \r\n"
				+ "?mapper map:sourcePropertyType ?sproptype. \r\n"
				+ "?mapper map:targetProperty ?tprop. \r\n"
				+ "}\r\n";
		
		ResultSet resultSet = Methods.executeQuery(mapModel, sparqlString);
//		Methods.print(resultSet);
		
		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String concept = querySolution.get("concept").toString();
			String sourceType = querySolution.get("stype").toString();
			String targetType = querySolution.get("ttype").toString();
			String sourceCommonProperty = querySolution.get("scommon").toString();
			String targetCommonProperty = querySolution.get("tcommon").toString();
			String iriValue = querySolution.get("irivalue").toString();
			String iriType = querySolution.get("iritype").toString();
			
			String mapper = querySolution.get("mapper").toString();
			String sourceProperty = querySolution.get("sprop").toString();
			String sourcePropertyType = querySolution.get("sproptype").toString();
			String targetProperty = querySolution.get("tprop").toString();
			
			MapperTransform mapperTransform = new MapperTransform(sourceProperty, sourcePropertyType, targetProperty);
			
			if (conceptMap.containsKey(concept)) {
				ConceptTransform conceptTransform = conceptMap.get(concept);
				conceptTransform.getMapperTransformMap().put(mapper, mapperTransform);
				conceptMap.replace(concept, conceptTransform);
			} else {
				ConceptTransform conceptTransform = new ConceptTransform();
				conceptTransform.setConcept(concept);
				conceptTransform.setSourceCommonProperty(sourceCommonProperty);
				conceptTransform.setSourceType(sourceType);
				conceptTransform.setTargetCommonProperty(targetCommonProperty);
				conceptTransform.setTargetType(targetType);
				conceptTransform.setIriValue(iriValue);
				conceptTransform.setIriValueType(iriType);
				conceptTransform.getMapperTransformMap().put(mapper, mapperTransform);
				conceptMap.put(concept, conceptTransform);
			}
		}
		
		int numOfFiles = 1, count = 0;
		for (String concept : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(concept);
			transformMultipleLiteral(firstModel, secondModel, conceptTransform, targetModel, count, numOfFiles);
		}
		
		String prefixString = Methods.getPrefixStrings(prefixMap);
		
		if (Methods.writeText(targetPath, prefixString)) {
			Model finalModel = Methods.readModelFromPath(targetPath);
			finalModel.add(targetModel);
			return Methods.saveModel(finalModel, targetPath);
		} else {
			return "File save error.";
		}
	}

	private void transformMultipleLiteral(Model firstModel, Model secondModel, ConceptTransform conceptTransform,
			Model targetModel, int count, int numOfFiles, boolean isNew) {
		// TODO Auto-generated method stub
		String sourceType = Methods.bracketString(conceptTransform.getSourceType());
		String targetType = Methods.bracketString(conceptTransform.getTargetType());
		String sourceCommonProperty = Methods.bracketString(conceptTransform.getSourceCommonProperty());
		String targetCommonProperty = Methods.bracketString(conceptTransform.getTargetCommonProperty());
		
		Model model = ModelFactory.createDefaultModel();
		model.add(firstModel);
		model.add(secondModel);
		
		ResultSet resultSet = null;
		
		if (sourceCommonProperty.toLowerCase().contains("SourceIRI".toLowerCase()) || 
				targetCommonProperty.toLowerCase().contains("TargetIRI".toLowerCase())) {
			String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?fsub a " + sourceType + ". \r\n"
					+ "?fsub a " + targetType + ". \r\n"
					+ "}\r\n";
			
			resultSet = Methods.executeQuery(model, sparqlString);
		} else {
			String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {\r\n"
					+ "?fsub a " + sourceType + ". \r\n"
					+ "?fsub " + sourceCommonProperty + " ?scommon. \r\n"
					+ "?ssub a " + targetType + ". \r\n"
					+ "?ssub " + targetCommonProperty + " ?scommon. \r\n"
					+ "}\r\n";
			
			resultSet = Methods.executeQuery(model, sparqlString);
		}
		
		LinkedHashMap<String, String> subjectMap = new LinkedHashMap<String, String>();
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String firstSubject = querySolution.get("fsub").toString();
			String secondSubject = querySolution.get("ssub").toString();
			
			subjectMap.put(firstSubject, secondSubject);
		}
		
		for (Map.Entry<String, String> map : subjectMap.entrySet()) {
			String firstSubject = map.getKey();
			String secondSubject = map.getValue();
			
			LinkedHashMap<String, Object> propertyMap = fetchSubjectPropertyValues(firstModel, firstSubject, sourceType);
			propertyMap.putAll(fetchSubjectPropertyValues(secondModel, secondSubject, targetType));
			
			// Methods.print(propertyMap);
			
			generateABox(secondSubject, conceptTransform, propertyMap, targetModel, count, numOfFiles);
		}
	}

	private void generateABox(String secondSubject, ConceptTransform conceptTransform,
			LinkedHashMap<String, Object> propertyMap, Model targetModel, int count, int numOfFiles) {
		// TODO Auto-generated method stub
		Resource mainResource = targetModel.createResource(secondSubject);
		
		if (!resourceMap.containsKey(secondSubject)) {
			targetModel.removeAll(mainResource, null, (RDFNode) null);
			
			targetModel.add(mainResource, RDF.type, targetModel.createResource(conceptTransform.getTargetType()));
			resourceMap.put(secondSubject, secondSubject);
			
			count++;
		}
		
		for (Map.Entry<String, MapperTransform> map : conceptTransform.getMapperTransformMap().entrySet()) {
			MapperTransform mapTransform = map.getValue();
			
			Property property = targetModel.createProperty(Methods.assignIRI(prefixMap, mapTransform.getTargetProperty()));
			Object propertyValue = null;
			
//			System.out.println("Source Type: " + mapTransform.getSourcePropertyType());
			
			if (mapTransform.getSourcePropertyType().contains("SourceExpression")) {
//				System.out.println("Expression: " + mapTransform.getSourceProperty());
//				propertyValue = expressionHandler.handleExpression(mapTransform.getSourceProperty(), propertyMap);
				
				EquationHandler equationHandler = new EquationHandler();
				propertyValue = equationHandler.handleExpression(mapTransform.getSourceProperty(),
						propertyMap);
				
//				System.out.println("***Received: " + secondSubject + " - " + propertyValue);
			} else {
				propertyValue = propertyMap.get(Methods.assignPrefix(prefixMap, mapTransform.getSourceProperty()));
			}
			
			if (propertyValue != null) {
				if (Methods.containsWWW(propertyValue.toString())) {
					Resource resource = targetModel.createResource(propertyValue.toString());
					mainResource.addProperty(property, resource);
				} else {
					mainResource.addLiteral(property, propertyValue);
				}
			}
		}
		
		if (count % 100 == 0) {
			String tempPath = numOfFiles + ".ttl";
			// System.out.println(tempPath);
			fileMethods.saveModel(targetModel, tempPath);
			targetModel = ModelFactory.createDefaultModel();
			numOfFiles++;
		}
		
		/*
		 * for (Map.Entry<String, Object> mapOne : propertyMap.entrySet()) { String
		 * propertyString = mapOne.getKey(); Object value = mapOne.getValue();
		 * 
		 * // System.out.println(propertyString); // System.out.println(value);
		 * 
		 * Property property = targetModel.createProperty(Methods.assignIRI(prefixMap,
		 * propertyString));
		 * 
		 * boolean isExpression = false; MapperTransform mapperTransform = null;
		 * 
		 * for (Map.Entry<String, MapperTransform> map :
		 * conceptTransform.getMapperTransformMap().entrySet()) { MapperTransform
		 * mapTransform = map.getValue(); if
		 * (mapTransform.getSourceProperty().contains(propertyString)) { mapperTransform
		 * = mapTransform; isExpression = true; break; } }
		 * 
		 * if (isExpression) { // System.out.println("Expression: " +
		 * mapperTransform.getSourceProperty()); ExpressionHandler expressionHandler =
		 * new ExpressionHandler(); Object propertyValue =
		 * expressionHandler.handleExpression(mapperTransform.getSourceProperty(),
		 * propertyMap);
		 * 
		 * // System.out.println("Property value: " + propertyValue);
		 * 
		 * if (propertyValue != null) { mainResource.addLiteral(property,
		 * propertyValue); } } else { if (value.toString().contains("http") ||
		 * value.toString().contains("www")) { // System.out.println("Resource");
		 * 
		 * Resource resource = targetModel.createResource(value.toString());
		 * mainResource.addProperty(property, resource); } else { ///
		 * System.out.println("Literal"); mainResource.addLiteral(property, value); } }
		 * 
		 * // Methods.print(targetModel); }
		 */
	}

	private LinkedHashMap<String, Object> fetchSubjectPropertyValues(Model model, String firstSubject, String sourceType) {
		// TODO Auto-generated method stub
		firstSubject = Methods.bracketString(firstSubject);
		
		String sparqlString = "PREFIX	map:	<http://www.map.org/example#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE {\r\n"
				+ firstSubject + " a " + sourceType + ". \r\n"
				+ firstSubject + " ?p ?o. \r\n"
				+ "}\r\n";
		
		ResultSet resultSet = Methods.executeQuery(model, sparqlString);
		// Methods.print(resultSet);
		
		LinkedHashMap<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String property = querySolution.get("p").toString();
			RDFNode value = querySolution.get("o");
			
			propertyMap.put(Methods.assignPrefix(prefixMap, property), Methods.getRDFNodeValue(value));
		}
		return propertyMap;
	}
	
	private String checkFiles(String sourceABoxFile, String mappingFile, String targetTBoxFile) {
		// TODO Auto-generated method stub
		FileMethods fileMethods = new FileMethods();
		
		if (!fileMethods.checkFile(sourceABoxFile)) {
			return "Check source file path";
		} else if (!fileMethods.checkFile(mappingFile)) {
			return "Check map file path";
		} else if (!fileMethods.checkFile(targetTBoxFile)) {
			return "Check target tbox file path";
		} else {
			return "OK";
		}
	}
}
