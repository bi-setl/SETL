package core;

import java.io.ObjectOutputStream.PutField;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.library.print;

import helper.Methods;
import model.ConceptTransform;
import model.MapperTransform;

public class MultipleTransformation {
	private LinkedHashMap<String, String> prefixMap;
	private Methods methods;
	
	public MultipleTransformation() {
		super();
		prefixMap = new LinkedHashMap<String, String>();
		methods = new Methods();
	}

	public String transformMultipleLiteral(String firstSourcePath, String secondSourcePath, String mappingPath,
			String targetPath) {
		Model firstModel = methods.readModelFromPath(firstSourcePath);

		if (firstModel == null) {
			return "Error in first source abox file";
		}

		Model secondModel = methods.readModelFromPath(secondSourcePath);

		if (secondModel == null) {
			return "Error in second source abox file";
		}

		Model mapModel = methods.readModelFromPath(mappingPath);

		if (mapModel == null) {
			return "Error in map file";
		}
		
		Model targetModel = ModelFactory.createDefaultModel();
		
		prefixMap = methods.extractPrefixes(firstSourcePath);
		prefixMap.putAll(methods.extractPrefixes(secondSourcePath));
		prefixMap.putAll(methods.extractPrefixes(mappingPath));
		
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
		// Methods.printResultSet(resultSet);
		
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
		
		for (String concept : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(concept);
			transformMultipleLiteral(firstModel, secondModel, conceptTransform, targetModel);
		}
		
		String prefixString = Methods.getPrefixStrings(prefixMap);
		
		if (methods.writeText(targetPath, prefixString)) {
			Model finalModel = methods.readModelFromPath(targetPath);
			finalModel.add(targetModel);
			return methods.saveModel(finalModel, targetPath);
		} else {
			return "File save error.";
		}
	}

	private void transformMultipleLiteral(Model firstModel, Model secondModel, ConceptTransform conceptTransform,
			Model targetModel) {
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
			// Methods.print(resultSet);
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
			
			generateABox(secondSubject, conceptTransform, propertyMap, targetModel);
		}
	}

	private void generateABox(String secondSubject, ConceptTransform conceptTransform,
			LinkedHashMap<String, Object> propertyMap, Model targetModel) {
		// TODO Auto-generated method stub
		Resource mainResource = targetModel.createResource(secondSubject);
		
		for (Map.Entry<String, MapperTransform> map : conceptTransform.getMapperTransformMap().entrySet()) {
			MapperTransform mapTransform = map.getValue();
			
			Property property = targetModel.createProperty(Methods.assignIRI(prefixMap, mapTransform.getTargetProperty()));
			Object propertyValue = null;
			
			if (mapTransform.getSourcePropertyType().contains("SourceExpression")) {
				ExpressionHandler expressionHandler = new ExpressionHandler();
				propertyValue = expressionHandler.handleExpression(mapTransform.getSourceProperty(), propertyMap);
			} else {
				propertyValue = propertyMap.get(Methods.assignPrefix(prefixMap, mapTransform.getSourceProperty()));
			}
			
			// System.out.println(mapTransform.getSourceProperty());
			// System.out.println(propertyValue);
			
			if (propertyValue != null) {
				if (propertyValue.toString().contains("http") || propertyValue.toString().contains("www")) {
					// System.out.println("Resource");	
					Resource resource = targetModel.createResource(propertyValue.toString());
					mainResource.addProperty(property, resource);
				} else {
					/// System.out.println("Literal");
					mainResource.addLiteral(property, propertyValue);
				}
			}
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
			
			propertyMap.put(Methods.assignPrefix(prefixMap, property), methods.getRDFNodeValue(value));
		}
		return propertyMap;
	}
}
