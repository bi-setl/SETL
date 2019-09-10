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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import helper.Methods;

public class Expression {
	LinkedHashMap<String, String> prefixMap;
	Model model;
	Model sourceModel;
	Model mapModel;
	Methods methods;
	
	public Expression() {
		// TODO Auto-generated constructor stub
		prefixMap = new LinkedHashMap<>();
		model = ModelFactory.createDefaultModel();
		sourceModel = ModelFactory.createDefaultModel();
		mapModel = ModelFactory.createDefaultModel();
		methods = new Methods();
	}
	
	public boolean handleExpression(String mapFile, String sourceFile, String resultFile) {
		try {
			// TODO Auto-generated method stub
			prefixMap = methods.getAllPredefinedPrefixes();
			extractAllPrefixes(mapFile);
			extractAllPrefixes(sourceFile);
			getModel().read(mapFile);
			
			getMapModel().read(mapFile);
			getSourceModel().read(sourceFile);
			
			getModel().add(ModelFactory.createDefaultModel().read(sourceFile));
			String sparql = "\nPREFIX map: <http://www.map.org/example#>\n"
					+ "SELECT ?r ?sub ?pred ?obj ?exp WHERE {\n"
					+ "?m a map:ConceptMapper. \n"
					+ "?m map:sourceConcept ?c. \n"
					+ "?r a map:PropertyMapper. \n"
					+ "?r map:ConceptMapper ?m. \n"
					+ "?r map:sourcePropertyType map:SourceExpression. \n"
					+ "?r map:sourceProperty ?exp. \n"
					+ "?sub a ?c. \n"
					+ "?sub ?pred ?obj. }\n";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			ResultSet set = ResultSetFactory.copyResults(resultSet);
			//ResultSetFormatter.out(set);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String record = String.valueOf(querySolution.get("r"));
				String subject = String.valueOf(querySolution.get("sub"));
				String property = String.valueOf(querySolution.get("pred"));
				String object = String.valueOf(querySolution.get("obj"));
				String expression = String.valueOf(querySolution.get("exp"));
				
				handleExpression(record, subject, property, object, expression);
			} 
			
			try {
				OutputStream outputStream = new FileOutputStream(resultFile);
				String[] parts = resultFile.split("\\.");
				getSourceModel().write(outputStream, parts[parts.length - 1].toUpperCase());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*getMapModel().write(System.out, "TTL");
			// .out.println(mapFile);
			
			try {
				OutputStream outputStream = new FileOutputStream(mapFile);
				String[] parts = mapFile.split("\\.");
				getMapModel().write(outputStream, parts[parts.length - 1].toUpperCase());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	private void handleExpression(String record, String subject, String predicate, String object, String expression) {
		// TODO Auto-generated method stub
		if (expression.contains("(") || expression.contains("+") || expression.contains("-") || expression.contains("*")
				|| expression.contains("/")) {
			if (expression.contains(assignPrefix(predicate))) {
				
				Object value = handleExpression(expression, object);
				System.out.println("Expression: " + expression + " - " + object);
				System.out.println("Value: " + value);
				
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

				/*double first = Double.parseDouble(firstValue.trim());
				double second = Double.parseDouble(secondValue.trim());*/
				
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
				/*for (Object object : arrayList) {
					System.out.println(object);
				}*/
				String first = (String) arrayList.get(startIndex + 4);
				
				String[] parts = new String[] {};
				int position = 6;
				if (!first.equals(",")) {
					parts = String.valueOf(value).split(first);
				} else {
					position = 5;
					parts = String.valueOf(value).split("\\s+");
				}
				
				/*System.out.println(position);
				for (String string : parts) {
					System.out.println(string);
				}
				
				System.out.println("---"+parts[Integer.parseInt(arrayList.get(position).toString())]);*/

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
				String firstSegment = segments[0].trim() + "#";

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
}
