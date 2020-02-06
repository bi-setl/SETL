package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import helper.Methods;
import helper.Variables;
import model.ConceptTransform;
import model.MapperTransform;

public class Expression {
	LinkedHashMap<String, String> prefixMap;
	Model model;
	Model sourceModel;
	Model mapModel;
	Methods methods;
	PrefixExtraction prefixExtraction;
	
	String demoPropertyPrefixString = "http://www.temp.org#tempProperty";

	public static void main(String[] args) {
		String basePath = "I:\\Data\\transform\\large\\";
		
		String mapFileString = basePath + "map_current.ttl";
		String sourceFileString = basePath + "model1.nt";
		String targetFileString = basePath + "subsidy_transform.ttl";

		Methods.startProcessingTime();
		Expression expression = new Expression(true);
		String result  = expression.transformOnLiteral(mapFileString, sourceFileString, targetFileString, true);
		System.out.println(result);
		Methods.endProcessingTime();
	}

	public Expression() {
		// TODO Auto-generated constructor stub
		prefixMap = new LinkedHashMap<>();
		model = ModelFactory.createDefaultModel();
		sourceModel = ModelFactory.createDefaultModel();
		mapModel = ModelFactory.createDefaultModel();
		methods = new Methods();
	}

	public String transformOnliteral(String mapFile, String sourceFile, String targetABoxFile) {
		prefixMap = methods.getAllPredefinedPrefixes();
		extractAllPrefixes(mapFile);
		extractAllPrefixes(sourceFile);
		
		Model mapModel = methods.readModelFromPath(mapFile);
		Model sourceModel = methods.readModelFromPath(sourceFile);
		
		String sparql = "\nPREFIX map: <http://www.map.org/example#>\n"
				+ "SELECT * WHERE {"
				+ "?concept a map:ConceptMapper."
				+ "?concept map:targetConcept ?targetType."
				+ "?concept map:sourceConcept ?sourcetype."
				+ "?mapper a map:PropertyMapper."
				+ "?mapper map:ConceptMapper ?concept."
				+ "?mapper map:sourceProperty ?source."
				+ "?mapper map:sourcePropertyType map:SourceExpression."
				+ "?mapper map:targetProperty ?target." + "}";

		ResultSet resultSet = Methods.executeQuery(mapModel, sparql);
		
		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();
		LinkedHashMap<String, Integer> propertyMap = new LinkedHashMap<String, Integer>();
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String conceptString = querySolution.get("concept").toString();
			String targetTypeString = querySolution.get("targetType").toString();
			String sourceTypeString = querySolution.get("sourcetype").toString();

			String mapperString = querySolution.get("mapper").toString();
			String targetPropertyString = querySolution.get("target").toString();
			String sourcePropertyString = querySolution.get("source").toString();
			String sourcePropertyTypeString = "map:SourceExpression";

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
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.put(conceptString, conceptTransform);
			}
		}
		
		for (String conceptString : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(conceptString);
			String typeString = Methods.bracketString(conceptTransform.getSourceType());
			

			String sparqlString = "SELECT * WHERE {" + "?s a " + typeString + "." + "?s ?p ?o.}";

			ResultSet set = Methods.executeQuery(sourceModel, sparqlString);
			
			String currentSubjectString = "";
			LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();
			
			while (set.hasNext()) {
				
				QuerySolution querySolution = (QuerySolution) set.next();
				String resourceString = querySolution.get("s").toString();
				String predicateString = querySolution.get("p").toString();
				RDFNode object = querySolution.get("o");
				
				// String prefixPredicate = assignPrefix(predicateString);
				// System.out.println("Prefix predicate: " + prefixPredicate + " Subject: " + resourceString);
				
				if (currentSubjectString.equals(resourceString)) {
					valueMap.put(assignPrefix(predicateString), methods.getRDFNodeValue(object));
					
					// System.out.println("Added");
				} else {
					if (currentSubjectString.equals("")) {
						currentSubjectString = resourceString;
						valueMap.put(assignPrefix(predicateString), methods.getRDFNodeValue(object));
						
					} else {

						Resource resource = sourceModel.createResource(currentSubjectString);
						
						for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
							MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
									.get(mapperString);
							
							
//							int index = 0;
//							if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//								index = propertyMap.get(mapperTransform.getSourceProperty());
//							} else {
//								index = propertyMap.size() + 1;
//								propertyMap.put(mapperTransform.getSourceProperty(), index);
//							}
							
							
							Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());
							
							if (!resource.hasProperty(property)) {
								String iri = mapperTransform.getTargetProperty();
								if (iri.contains("#")) {
									String[] segments = iri.split("#");
									if (segments.length == 2) {
										String firstSegment = segments[0].trim() + "/";
										String secondSegment = segments[1];
										
										property = sourceModel.createProperty(firstSegment + secondSegment);
									}
								} else {
									String[] segments = iri.split("/");
									String lastSegment = segments[segments.length - 1];

									String firstSegment = "";
									if (iri.endsWith(lastSegment)) {
										firstSegment = iri.replace(lastSegment, "");
									}
									
									firstSegment = firstSegment.substring(0, firstSegment.length() - 1) + "#";

									property = sourceModel.createProperty(firstSegment + lastSegment);
								}
							}
							
							resource.removeAll(property);
							
							propertyMap.put(mapperString, (propertyMap.size() + 1));
							
							// System.out.println("Current: " + currentSubjectString);
							
							EquationHandler equationHandler = new EquationHandler();
							Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
									valueMap);
							
							Literal literal = sourceModel.createTypedLiteral(valueObject);
							resource.addProperty(property, literal);
						}
						
						currentSubjectString = resourceString;
						valueMap = new LinkedHashMap<String, Object>();
						valueMap.put(assignPrefix(predicateString), methods.getRDFNodeValue(object));
					}
				}
			}
			
			if (!currentSubjectString.equals("")) {
				// System.out.println("Current subject: " + currentSubjectString);
				Resource resource = sourceModel.createResource(currentSubjectString);
				
				for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
					MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
							.get(mapperString);
					
					
//					int index = 0;
//					if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//						index = propertyMap.get(mapperTransform.getSourceProperty());
//					} else {
//						index = propertyMap.size() + 1;
//						propertyMap.put(mapperTransform.getSourceProperty(), index);
//					}
					
					
					Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());
					resource.removeAll(property);
					propertyMap.put(mapperString, (propertyMap.size() + 1));
					
					// methods.print("Size: " + valueMap.size());
					// methods.print(valueMap);
					
					EquationHandler equationHandler = new EquationHandler();
					Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
							valueMap);
					
					Literal literal = sourceModel.createTypedLiteral(valueObject);
					resource.addProperty(property, literal);
				}
			}
		}
		
		methods.saveModel(sourceModel, targetABoxFile);
		
		return "Successful.\nFile Saved: " + targetABoxFile;
	}

	public boolean handleExpression(String mapFile, String sourceFile, String resultFile) {
		try {
			/*
			 * System.out.println("Map: " + mapFile); System.out.println("Source: " +
			 * sourceFile); System.out.println("Target: " + resultFile);
			 */

			// TODO Auto-generated method stub
			prefixMap = methods.getAllPredefinedPrefixes();
			extractAllPrefixes(mapFile);
			extractAllPrefixes(sourceFile);
			getModel().read(mapFile);

			getMapModel().read(mapFile);
			getSourceModel().read(sourceFile);

			getModel().add(ModelFactory.createDefaultModel().read(sourceFile));
			String sparql = "\nPREFIX map: <http://www.map.org/example#>\n" + "SELECT ?r ?sub ?pred ?obj ?exp WHERE {\n"
					+ "?m a map:ConceptMapper. \n" + "?m map:sourceConcept ?c. \n" + "?r a map:PropertyMapper. \n"
					+ "?r map:ConceptMapper ?m. \n" + "?r map:sourcePropertyType map:SourceExpression. \n"
					+ "?r map:sourceProperty ?exp. \n" + "?sub a ?c. \n" + "?sub ?pred ?obj. }\n";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			// Methods.print(resultSet);

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String record = String.valueOf(querySolution.get("r"));
				String subject = String.valueOf(querySolution.get("sub"));
				String property = String.valueOf(querySolution.get("pred"));
				String object = String.valueOf(querySolution.get("obj"));
				String expression = String.valueOf(querySolution.get("exp"));

				handleExpression(record, subject, property, object, expression);
			}

			Methods methods = new Methods();
			methods.saveModel(getSourceModel(), resultFile);

			/*
			 * try { OutputStream outputStream = new FileOutputStream(resultFile); String[]
			 * parts = resultFile.split("\\."); getSourceModel().write(outputStream,
			 * parts[parts.length - 1].toUpperCase()); } catch (FileNotFoundException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 */

			/*
			 * getMapModel().write(System.out, "TTL"); // .out.println(mapFile);
			 * 
			 * try { OutputStream outputStream = new FileOutputStream(mapFile); String[]
			 * parts = mapFile.split("\\."); getMapModel().write(outputStream,
			 * parts[parts.length - 1].toUpperCase()); } catch (FileNotFoundException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 */

			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	private void handleExpression(String record, String subject, String predicate, String object, String expression) {
		// TODO Auto-generated method stub
		System.out.println("Handle Expression");
		System.out.println("Predicate: " + predicate);
		if (expression.contains("(") || expression.contains("+") || expression.contains("-") || expression.contains("*")
				|| expression.contains("/")) {
			System.out.println("Here");
			System.out.println("Predicate: " + assignPrefix(predicate));

			if (expression.contains(assignPrefix(predicate))) {
				Object value = handleExpression(expression, object);

				Resource resource = getSourceModel().getResource(subject);
				Property property = getSourceModel().createProperty(predicate);

				RDFNode node = null, rdfNode = null;
				if (object.contains("http://") || object.contains(":")) {
					node = ResourceFactory.createResource(object);
				} else {
					node = ResourceFactory.createStringLiteral(object);
				}

				String valueString = String.valueOf(value);
				if (valueString.contains("http://") || valueString.contains(":")) {
					rdfNode = ResourceFactory.createResource(valueString);
				} else {
					rdfNode = ResourceFactory.createTypedLiteral(value);
				}

				getSourceModel().remove(resource, property, node);
				getSourceModel().add(resource, property, rdfNode);

				replaceMapProperty(record, predicate);
			}
		}
	}

	private void replaceMapProperty(String subject, String object) {
		// TODO Auto-generated method stub
		// System.out.println(subject + " - " + object);
		Resource resource = getMapModel().getResource(subject);
		Property property = getMapModel().getProperty(assignIRI("map:sourceProperty"));
		resource.removeAll(property);

		RDFNode node = null;
		if (object.contains("http://") || object.contains(":")) {
			node = ResourceFactory.createResource(object);
		} else {
			node = ResourceFactory.createStringLiteral(object);
		}

		getMapModel().add(resource, property, node);
	}

	public Object handleExpression(String expression, Object value) {
		// TODO Auto-generated method stub
		ArrayList<Object> arrayList = new ArrayList<>();

		String word = "";

		for (int i = 0; i < expression.length(); i++) {
			String character = Character.toString(expression.charAt(i));

			if (character.matches("[a-zA-Z0-9://#\\.\\\\]")) {
				word = word + character;
			} else if (character.matches("[\\(]")) {
				if (!word.equals("")) {
					arrayList.add(word);
				}

				arrayList.add(character);
				word = "";
			} else if (character.matches("[,]")) {
				if (!word.equals("")) {
					arrayList.add(word);
				}

				arrayList.add(character);
				word = "";
			} else if (character.matches("[\\)]")) {
				if (!word.equals("")) {
					arrayList.add(word);
				}

				arrayList = decodeExpression(arrayList, value);
				word = "";
			} else if (character.equals("+") || character.equals("-") || character.equals("*")
					|| character.equals("/")) {
				boolean status = false;
				for (Object object : arrayList) {
					if (String.valueOf(object).toLowerCase().equals("split".toLowerCase())) {
						status = true;
					}
				}

				if (!status) {
					if (!word.equals("")) {
						arrayList.add(word);
					}

					arrayList.add(character);
					word = "";
				} else {
					word = word + character;
				}

			}
		}

		if (!word.equals("")) {
			arrayList.add(word);
			return crackExpression(arrayList, value);
		}

		if (arrayList.size() == 1) {
			if (String.valueOf(arrayList.get(0)).trim().equals(expression.trim())) {
				return value;
			} else {
				return arrayList.get(0);
			}
		} else {
			return value;
		}
	}

	private Object crackExpression(ArrayList<Object> arrayList, Object value) {
		// TODO Auto-generated method stub
		if (arrayList.size() == 1) {
			return value;
		} else {
			int add = 0, sub = 0, mul = 0, div = 0;
			for (Object object : arrayList) {
				String string = String.valueOf(object);

				if (string.equals("*")) {
					mul++;
				} else if (string.equals("/")) {
					div++;
				} else if (string.equals("+")) {
					add++;
				} else if (string.equals("-")) {
					sub++;
				}
			}

			for (int i = 0; i < mul; i++) {
				int position = -1;
				for (int j = 0; j < arrayList.size(); j++) {
					String string = String.valueOf(arrayList.get(j));
					if (string.equals("*")) {
						position = j;
						break;
					}
				}

				String result = "";
				String firstValue = String.valueOf(arrayList.get(position - 1));
				String secondValue = String.valueOf(arrayList.get(position + 1));

				if (firstValue.contains("http") || firstValue.contains(":")) {
					firstValue = String.valueOf(value);
				} else if (secondValue.contains("http") || secondValue.contains(":")) {
					secondValue = String.valueOf(value);
				}

				long first = Integer.parseInt(firstValue.trim());
				long second = Integer.parseInt(secondValue.trim());

				long res = first * second;
				result = String.valueOf(res);

				ArrayList<Object> objects = new ArrayList<>();

				for (int j = 0; j < position - 1; j++) {
					objects.add(arrayList.get(j));
				}

				objects.add(result);

				for (int j = position + 2; j < arrayList.size(); j++) {
					objects.add(arrayList.get(j));
				}

				arrayList = objects;
			}

			for (int i = 0; i < div; i++) {
				int position = -1;
				for (int j = 0; j < arrayList.size(); j++) {
					String string = String.valueOf(arrayList.get(j));
					if (string.equals("/")) {
						position = j;
						break;
					}
				}

				String result = "";
				String firstValue = String.valueOf(arrayList.get(position - 1));
				String secondValue = String.valueOf(arrayList.get(position + 1));

				if (firstValue.contains("http") || firstValue.contains(":")) {
					firstValue = String.valueOf(value);
				} else if (secondValue.contains("http") || secondValue.contains(":")) {
					secondValue = String.valueOf(value);
				}

				int first = Integer.parseInt(firstValue.trim());
				int second = Integer.parseInt(secondValue.trim());

				result = String.valueOf(first / second);

				ArrayList<Object> objects = new ArrayList<>();

				for (int j = 0; j < position - 1; j++) {
					objects.add(arrayList.get(j));
				}

				objects.add(result);

				for (int j = position + 2; j < arrayList.size(); j++) {
					objects.add(arrayList.get(j));
				}

				arrayList = objects;
			}

			for (int i = 0; i < add; i++) {
				int position = -1;
				for (int j = 0; j < arrayList.size(); j++) {
					String string = String.valueOf(arrayList.get(j));
					if (string.equals("+")) {
						position = j;
						break;
					}
				}

				String result = "";
				String firstValue = String.valueOf(arrayList.get(position - 1));
				String secondValue = String.valueOf(arrayList.get(position + 1));

				if (firstValue.contains("http") || firstValue.contains(":")) {
					firstValue = String.valueOf(value);
				} else if (secondValue.contains("http") || secondValue.contains(":")) {
					secondValue = String.valueOf(value);
				}

				/*
				 * double first = Double.parseDouble(firstValue.trim()); double second =
				 * Double.parseDouble(secondValue.trim());
				 */

				int first = Integer.parseInt(firstValue.trim());
				int second = Integer.parseInt(secondValue.trim());

				result = String.valueOf(first + second);

				ArrayList<Object> objects = new ArrayList<>();

				for (int j = 0; j < position - 1; j++) {
					objects.add(arrayList.get(j));
				}

				objects.add(result);

				for (int j = position + 2; j < arrayList.size(); j++) {
					objects.add(arrayList.get(j));
				}

				arrayList = objects;
			}

			for (int i = 0; i < sub; i++) {
				int position = -1;
				for (int j = 0; j < arrayList.size(); j++) {
					String string = String.valueOf(arrayList.get(j));
					if (string.equals("-")) {
						position = j;
						break;
					}
				}

				String result = "";
				String firstValue = String.valueOf(arrayList.get(position - 1));
				String secondValue = String.valueOf(arrayList.get(position + 1));

				if (firstValue.contains("http") || firstValue.contains(":")) {
					firstValue = String.valueOf(value);
				} else if (secondValue.contains("http") || secondValue.contains(":")) {
					secondValue = String.valueOf(value);
				}

				int first = Integer.parseInt(firstValue.trim());
				int second = Integer.parseInt(secondValue.trim());

				result = String.valueOf(first - second);

				ArrayList<Object> objects = new ArrayList<>();

				for (int j = 0; j < position - 1; j++) {
					objects.add(arrayList.get(j));
				}

				objects.add(result);

				for (int j = position + 2; j < arrayList.size(); j++) {
					objects.add(arrayList.get(j));
				}

				arrayList = objects;
			}

			if (arrayList.size() == 1) {
				return arrayList.get(0);
			} else {
				return value;
			}
		}
	}

	private ArrayList<Object> decodeExpression(ArrayList<Object> arrayList, Object value) {
		// TODO Auto-generated method stub
		if (arrayList.size() != 0) {
			int startIndex = -1;
			for (int i = arrayList.size() - 1; i > 0; i--) {
				if (arrayList.get(i).equals("(")) {
					startIndex = i - 1;
					break;
				}
			}

			String operation = (String) arrayList.get(startIndex);

			String result = "";
			if (operation.toLowerCase().equals("CONCAT".toLowerCase())) {
				String first = (String) arrayList.get(startIndex + 2);
				String second = (String) arrayList.get(startIndex + 4);

				if (first.contains("http") || first.contains(":")) {
					result = value + second;
				} else if (second.contains("http") || second.contains(":")) {
					result = first + value;
				} else {
					result = first + second;
				}
			} else if (operation.toLowerCase().equals("Replace".toLowerCase())) {
				String first = (String) arrayList.get(startIndex + 4);
				String second = (String) arrayList.get(startIndex + 6);

				result = String.valueOf(value).replaceAll(first, second);
			} else if (operation.toLowerCase().equals("split".toLowerCase())) {
				/*
				 * for (Object object : arrayList) { System.out.println(object); }
				 */
				String first = (String) arrayList.get(startIndex + 4);

				String[] parts = new String[] {};
				int position = 6;
				if (!first.equals(",")) {
					parts = String.valueOf(value).split(first);
				} else {
					position = 5;
					parts = String.valueOf(value).split("\\s+");
				}

				/*
				 * System.out.println(position); for (String string : parts) {
				 * System.out.println(string); }
				 * 
				 * System.out.println("---"+parts[Integer.parseInt(arrayList.get(position).
				 * toString())]);
				 */

				for (int i = position; i < arrayList.size(); i += 2) {
					String string = String.valueOf(arrayList.get(i)).trim();

					try {
						int index = Integer.parseInt(string);
						result = result + parts[index];
					} catch (Exception e) {
						// TODO: handle exception
						return arrayList;
					}
				}
			} else if (operation.toLowerCase().equals("ToNumber".toLowerCase())) {
				String expression = "";
				for (int i = startIndex + 2; i < arrayList.size(); i++) {
					expression = expression + arrayList.get(i);
				}

				Object object = handleExpression(expression, value);
				int number = 0;
				try {
					result = String.valueOf(object);
					Pattern pattern = Pattern.compile("[0-9]+");

					Matcher matcher = pattern.matcher(result);
					if (matcher.find()) {
						number = Integer.parseInt(matcher.group(0));
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
					for (int i = arrayList.size() - 1; i > startIndex; i--) {
						arrayList.remove(i);
					}

					arrayList.set(startIndex, value);
					return arrayList;
				}

				for (int i = arrayList.size() - 1; i > startIndex; i--) {
					arrayList.remove(i);
				}

				arrayList.set(startIndex, number);
				return arrayList;
			} else if (operation.toLowerCase().equals("ToString".toLowerCase())) {
				String expression = "";
				for (int i = startIndex + 2; i < arrayList.size(); i++) {
					expression = expression + arrayList.get(i);
				}

				Object object = handleExpression(expression, value);
				result = String.valueOf(object);
			}

			for (int i = arrayList.size() - 1; i > startIndex; i--) {
				arrayList.remove(i);
			}

			arrayList.set(startIndex, result);
		}

		return arrayList;
	}

	private void extractAllPrefixes(String filepath) {
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

				getPrefixMap().put(prefix, iri);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	public String assignIRI(String prefix) {
		String[] segments = prefix.split(":");
		if (segments.length == 2) {
			String firstSegment = segments[0] + ":";
			return getPrefixMap().get(firstSegment) + segments[1];
		} else {
			return prefix;
		}
	}

	public String assignPrefix(String iri) {
		if (iri.contains("#")) {
			String[] segments = iri.split("#");
			if (segments.length == 2) {
				String firstSegment = segments[0].trim() + "/";

				for (Map.Entry<String, String> map : getPrefixMap().entrySet()) {
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

			for (Map.Entry<String, String> map : getPrefixMap().entrySet()) {
				String key = map.getKey();
				String value = map.getValue();

				if (firstSegment.equals(value.trim())) {
					return key + lastSegment;
				}
			}

			return iri;
		}
	}

	public LinkedHashMap<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(LinkedHashMap<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getSourceModel() {
		return sourceModel;
	}

	public void setSourceModel(Model sourceModel) {
		this.sourceModel = sourceModel;
	}

	public Model getMapModel() {
		return mapModel;
	}

	public void setMapModel(Model mapModel) {
		this.mapModel = mapModel;
	}
	
	


	public Expression(boolean isNew) {
		// TODO Auto-generated constructor stub
		prefixExtraction = new PrefixExtraction();
	}

	public String transformOnLiteral(String mapFileString, String sourceFileString, String targetFileString, boolean isNew) {
		// TODO Auto-generated method stub
		if (Methods.isJenaAccessible(sourceFileString)) {
			return transformOnLiteralFromTinyFile(mapFileString, sourceFileString, targetFileString, true);
		} else {
//			System.out.println("Large");
			return transformOnLiteralFromLargeFile(mapFileString, sourceFileString, targetFileString, true);
		}
	}

	public String transformOnLiteralFromTinyFile(String mapFile, String sourceFile, String targetABoxFile, boolean isNew) {
		prefixExtraction.extractPrefix(mapFile);
		prefixExtraction.extractPrefix(sourceFile);

		Model mapModel = ModelFactory.createDefaultModel();
		try {
			mapModel.read(mapFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Check Mapping file";
		}
		
		Model sourceModel = Methods.readModelFromPath(sourceFile);
		
		if (sourceModel == null) {
			return "Check Source File";
		}

		String sparql = "\nPREFIX map: <http://www.map.org/example#>\n" + "SELECT * WHERE {"
				+ "?concept a map:ConceptMapper." + "?concept map:targetConcept ?targetType."
				+ "?concept map:sourceConcept ?sourcetype." + "?mapper a map:PropertyMapper."
				+ "?mapper map:ConceptMapper ?concept." + "?mapper map:sourceProperty ?source."
				+ "?mapper map:sourcePropertyType map:SourceExpression." + "?mapper map:targetProperty ?target." + "}";

		ResultSet resultSet = Methods.executeQuery(mapModel, sparql);

		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();
		LinkedHashMap<String, Integer> propertyMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String conceptString = querySolution.get("concept").toString();
			String targetTypeString = querySolution.get("targetType").toString();
			String sourceTypeString = querySolution.get("sourcetype").toString();

			String mapperString = querySolution.get("mapper").toString();
			String targetPropertyString = querySolution.get("target").toString();
			String sourcePropertyString = querySolution.get("source").toString();
			String sourcePropertyTypeString = "map:SourceExpression";

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
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.put(conceptString, conceptTransform);
			}
		}

		for (String conceptString : conceptMap.keySet()) {
			ConceptTransform conceptTransform = conceptMap.get(conceptString);
			String typeString = Methods.bracketString(conceptTransform.getSourceType());

			String sparqlString = "SELECT * WHERE {" + "?s a " + typeString + "." + "?s ?p ?o.}";

			ResultSet set = Methods.executeQuery(sourceModel, sparqlString);

			String currentSubjectString = "";
			LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();

			while (set.hasNext()) {

				QuerySolution querySolution = set.next();
				String resourceString = querySolution.get("s").toString();
				String predicateString = querySolution.get("p").toString();
				RDFNode object = querySolution.get("o");

				// String prefixPredicate = assignPrefix(predicateString);
				// System.out.println("Prefix predicate: " + prefixPredicate + " Subject: " +
				// resourceString);

				if (currentSubjectString.equals(resourceString)) {
					valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));

					// System.out.println("Added");
				} else {
					if (currentSubjectString.equals("")) {
						currentSubjectString = resourceString;
						valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));

					} else {

						Resource resource = sourceModel.createResource(currentSubjectString);

						for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
							MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
									.get(mapperString);

//							int index = 0;
//							if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//								index = propertyMap.get(mapperTransform.getSourceProperty());
//							} else {
//								index = propertyMap.size() + 1;
//								propertyMap.put(mapperTransform.getSourceProperty(), index);
//							}

							Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());

							if (!resource.hasProperty(property)) {
								String iri = mapperTransform.getTargetProperty();
								if (iri.contains("#")) {
									String[] segments = iri.split("#");
									if (segments.length == 2) {
										String firstSegment = segments[0].trim() + "/";
										String secondSegment = segments[1];

										property = sourceModel.createProperty(firstSegment + secondSegment);
									}
								} else {
									String[] segments = iri.split("/");
									String lastSegment = segments[segments.length - 1];

									String firstSegment = "";
									if (iri.endsWith(lastSegment)) {
										firstSegment = iri.replace(lastSegment, "");
									}

									firstSegment = firstSegment.substring(0, firstSegment.length() - 1) + "#";

									property = sourceModel.createProperty(firstSegment + lastSegment);
								}
							}

							resource.removeAll(property);

							propertyMap.put(mapperString, (propertyMap.size() + 1));

							// System.out.println("Current: " + currentSubjectString);

							EquationHandler equationHandler = new EquationHandler();
							Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
									valueMap);

							Literal literal = sourceModel.createTypedLiteral(valueObject);
							resource.addProperty(property, literal);
						}

						currentSubjectString = resourceString;
						valueMap = new LinkedHashMap<String, Object>();
						valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));
					}
				}
			}

			if (!currentSubjectString.equals("")) {
				// System.out.println("Current subject: " + currentSubjectString);
				Resource resource = sourceModel.createResource(currentSubjectString);

				for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
					MapperTransform mapperTransform = conceptTransform.getMapperTransformMap().get(mapperString);

//					int index = 0;
//					if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//						index = propertyMap.get(mapperTransform.getSourceProperty());
//					} else {
//						index = propertyMap.size() + 1;
//						propertyMap.put(mapperTransform.getSourceProperty(), index);
//					}

					Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());
					resource.removeAll(property);
					propertyMap.put(mapperString, (propertyMap.size() + 1));

					// methods.print("Size: " + valueMap.size());
					// methods.print(valueMap);

					EquationHandler equationHandler = new EquationHandler();
					Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
							valueMap);

					Literal literal = sourceModel.createTypedLiteral(valueObject);
					resource.addProperty(property, literal);
				}
			}
		}

		Methods.saveModel(sourceModel, targetABoxFile);

		return "Successful.\nFile Saved: " + targetABoxFile;
	}
	
	public String transformOnLiteralFromLargeFile(String mapFile, String sourceFile, String targetABoxFile, boolean isNew) {
		prefixExtraction.extractPrefix(mapFile);
		// prefixExtraction.extractPrefix(sourceFile);

		Model mapModel = Methods.readModelFromPath(mapFile);
		
		if (mapModel == null) {
			return "Check Map File";
		}

		String sparql = "\nPREFIX map: <http://www.map.org/example#>\n"
				+ "SELECT * WHERE {"
				+ "?concept a map:ConceptMapper."
				+ "?concept map:targetConcept ?targetType."
				+ "?concept map:sourceConcept ?sourcetype."
				+ "?mapper a map:PropertyMapper."
				+ "?mapper map:ConceptMapper ?concept."
				+ "?mapper map:sourceProperty ?source."
				+ "?mapper map:sourcePropertyType map:SourceExpression."
				+ "?mapper map:targetProperty ?target."
				+ "}";

		ResultSet resultSet = Methods.executeQuery(mapModel, sparql);
//		Methods.print(resultSet);

		LinkedHashMap<String, ConceptTransform> conceptMap = new LinkedHashMap<String, ConceptTransform>();
		LinkedHashMap<String, Integer> propertyMap = new LinkedHashMap<String, Integer>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String conceptString = querySolution.get("concept").toString();
			String targetTypeString = querySolution.get("targetType").toString();
			String sourceTypeString = querySolution.get("sourcetype").toString();

			String mapperString = querySolution.get("mapper").toString();
			String targetPropertyString = querySolution.get("target").toString();
			String sourcePropertyString = querySolution.get("source").toString();
			String sourcePropertyTypeString = "map:SourceExpression";

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
				conceptTransform.getMapperTransformMap().put(mapperString, mapperTransform);
				conceptMap.put(conceptString, conceptTransform);
			}
		}
		
		int numOfFiles = FileSeparation.splitFileByLine(sourceFile, Variables.MAX_SPLIT_LINE);
		System.out.println("Num of Files: " + numOfFiles);

		for (int i = 1; i <= numOfFiles; i++) {
			String tinySourcePath = Variables.MODEL_DIR + "\\model" + i + ".nt";
			
			Model sourceModel = Methods.readModelFromPath(tinySourcePath);
			
			if (sourceModel == null) {
				return "Check Source File: " + tinySourcePath;
			}

			for (String conceptString : conceptMap.keySet()) {
				ConceptTransform conceptTransform = conceptMap.get(conceptString);
				String typeString = Methods.bracketString(conceptTransform.getSourceType());

				String sparqlString = "SELECT * WHERE {" + "?s a " + typeString + "." + "?s ?p ?o.}";

				ResultSet set = Methods.executeQuery(sourceModel, sparqlString);
//				Methods.print(set);

				String currentSubjectString = "";
				LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();

				while (set.hasNext()) {

					QuerySolution querySolution = set.next();
					String resourceString = querySolution.get("s").toString();
					String predicateString = querySolution.get("p").toString();
					RDFNode object = querySolution.get("o");

					// String prefixPredicate = assignPrefix(predicateString);
					// System.out.println("Prefix predicate: " + prefixPredicate + " Subject: " +
					// resourceString);

					if (currentSubjectString.equals(resourceString)) {
						valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));

						// System.out.println("Added");
					} else {
						if (currentSubjectString.equals("")) {
							currentSubjectString = resourceString;
							valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));

						} else {

							Resource resource = sourceModel.createResource(currentSubjectString);

							for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
								MapperTransform mapperTransform = conceptTransform.getMapperTransformMap()
										.get(mapperString);

//							int index = 0;
//							if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//								index = propertyMap.get(mapperTransform.getSourceProperty());
//							} else {
//								index = propertyMap.size() + 1;
//								propertyMap.put(mapperTransform.getSourceProperty(), index);
//							}

								Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());

								if (!resource.hasProperty(property)) {
									String iri = mapperTransform.getTargetProperty();
									if (iri.contains("#")) {
										String[] segments = iri.split("#");
										if (segments.length == 2) {
											String firstSegment = segments[0].trim() + "/";
											String secondSegment = segments[1];

											property = sourceModel.createProperty(firstSegment + secondSegment);
										}
									} else {
										String[] segments = iri.split("/");
										String lastSegment = segments[segments.length - 1];

										String firstSegment = "";
										if (iri.endsWith(lastSegment)) {
											firstSegment = iri.replace(lastSegment, "");
										}

										firstSegment = firstSegment.substring(0, firstSegment.length() - 1) + "#";

										property = sourceModel.createProperty(firstSegment + lastSegment);
									}
								}

								resource.removeAll(property);

								propertyMap.put(mapperString, (propertyMap.size() + 1));

								// System.out.println("Current: " + currentSubjectString);

								EquationHandler equationHandler = new EquationHandler();
								Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
										valueMap);

								Literal literal = sourceModel.createTypedLiteral(valueObject);
								resource.addProperty(property, literal);
							}

							currentSubjectString = resourceString;
							valueMap = new LinkedHashMap<String, Object>();
							valueMap.put(prefixExtraction.assignPrefix(predicateString), Methods.getRDFNodeValue(object));
						}
					}
				}

				if (!currentSubjectString.equals("")) {
					// System.out.println("Current subject: " + currentSubjectString);
					Resource resource = sourceModel.createResource(currentSubjectString);

					for (String mapperString : conceptTransform.getMapperTransformMap().keySet()) {
						MapperTransform mapperTransform = conceptTransform.getMapperTransformMap().get(mapperString);

//					int index = 0;
//					if (propertyMap.containsKey(mapperTransform.getSourceProperty())) {
//						index = propertyMap.get(mapperTransform.getSourceProperty());
//					} else {
//						index = propertyMap.size() + 1;
//						propertyMap.put(mapperTransform.getSourceProperty(), index);
//					}

						Property property = sourceModel.createProperty(mapperTransform.getTargetProperty());
						resource.removeAll(property);
						propertyMap.put(mapperString, (propertyMap.size() + 1));

						// methods.print("Size: " + valueMap.size());
						// methods.print(valueMap);

						EquationHandler equationHandler = new EquationHandler();
						Object valueObject = equationHandler.handleExpression(mapperTransform.getSourceProperty(),
								valueMap);

						Literal literal = sourceModel.createTypedLiteral(valueObject);
						resource.addProperty(property, literal);
					}
				}
			}

			if (sourceModel.size() > 0) {
				Methods.checkToSaveModel(targetABoxFile, sourceModel);
			}
		}

		return "Successful.\nFile Saved: " + targetABoxFile;
	}
	
	
}
