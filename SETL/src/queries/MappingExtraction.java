package queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
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
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;

import controller.MappingDefinition;
import helper.FileMethods;

public class MappingExtraction {
	private ArrayList<String> datasetList;
	private ArrayList<String> mapperList;
	private ArrayList<String> recordList;
	private LinkedHashMap<String, String> prefixMap;
	private Model model;

	private void initializeAll() {
		// TODO Auto-generated method stub
		setDatasetList(new ArrayList<>());
		setMapperList(new ArrayList<>());
		setRecordList(new ArrayList<>());
	}

	public MappingExtraction(String filePath) {
		initializeAll();
		setPrefixMap(new LinkedHashMap<>());

		File file = new File(filePath);
		if (file.exists() && !file.isDirectory()) {
			setModel(readFileFromPath(filePath));

			setPrefixMap(extractAllPrefixes(filePath));

			if (getPrefixMap().size() == 0) {
				setPrefixMap(new MappingDefinition().getAllPredefinedPrefixes());
			} else {
				for (Map.Entry<String, String> map : new MappingDefinition().getAllPredefinedPrefixes().entrySet()) {
					getPrefixMap().put(map.getKey(), map.getValue());
				}
			}

			setDatasetList(extractAllDatasets());
			setMapperList(extractAllMappers());
			setRecordList(extractAllRecords());
		} else {
			System.out.println("Check file path");
		}
	}
	
	public void reloadAll() {
		setDatasetList(extractAllDatasets());
		setMapperList(extractAllMappers());
		setRecordList(extractAllRecords());
	}

	public String getModelText(String type) {
		// TODO Auto-generated method stub
		Model model = getModel();

		StringWriter out = new StringWriter();
		model.write(out, type);
		String result = out.toString();
		return result;
	}

	private ArrayList<String> extractAllRecords() {
		// TODO Auto-generated method stub
		ArrayList<String> records = new ArrayList<>();

		String sparql = "PREFIX map:	<http://www.map.org/example#>\r\n"
				+ "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a map:PropertyMapper; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				records.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return records;
	}

	private ArrayList<String> extractAllMappers() {
		// TODO Auto-generated method stub
		ArrayList<String> heads = new ArrayList<>();

		String sparql = "PREFIX map:	<http://www.map.org/example#>\r\n"
				+ "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a map:ConceptMapper; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				heads.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return heads;
	}

	private ArrayList<String> extractAllDatasets() {
		// TODO Auto-generated method stub
		ArrayList<String> datasets = new ArrayList<>();

		String sparql = "PREFIX map:	<http://www.map.org/example#>\r\n"
				+ "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a map:Dataset; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				datasets.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return datasets;
	}

	private LinkedHashMap<String, String> extractAllPrefixes(String filePath) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> hashedMap = new LinkedHashMap<>();
		File file = new File(filePath);
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

	private Model readFileFromPath(String filePath) {
		// TODO Auto-generated method stub
		Model model = ModelFactory.createDefaultModel();

		if (filePath.endsWith(".owl")) {
			File file = new File(filePath);
			BufferedReader bufferedReader = null;

			try {
				bufferedReader = new BufferedReader(new FileReader(file));

				String string = "", s;
				while ((s = bufferedReader.readLine()) != null) {
					string = string + s;
				}

				bufferedReader.close();

				String regEx = "(<)([^>]*)(>)(\\s+)(<)([^>]*)(>)(\\s+)(<)([^>]*)(>)";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(string);

				int count = 0;
				while (matcher.find()) {
					count++;
				}

				if (count != 0) {
					String[] segments = filePath.split("\\.");

					if (segments.length == 2) {
						String newPath = segments[0] + ".nt";
						new FileMethods().writeText(newPath, string);
						model = new FileMethods().readModelFromPath(newPath);
						if (model == null) {
							new FileMethods().showDialog("Problem in reading the file");
						}
						return model;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
		} else if (filePath.endsWith(".ttl")) {
			model = new FileMethods().readModelFromPath(filePath);
			if (model == null) {
				new FileMethods().showDialog("Problem in reading the file");
			}
			return model;
		} else {
			return new FileMethods().writeNReadNTurtle(filePath);
		}
		return model;
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

	public String assignIRI(String prefix) {
		if (prefix.contains("http") || prefix.contains("www")) {
			return prefix;
		} else {
			String[] segments = prefix.split(":");
			if (segments.length == 2) {
				String firstSegment = segments[0] + ":";
				return getPrefixMap().get(firstSegment) + segments[1];
			} else {
				return prefix;
			}
		}
	}

	public ArrayList<String> getDatasetList() {
		return datasetList;
	}

	public void setDatasetList(ArrayList<String> datasetList) {
		this.datasetList = datasetList;
	}

	public ArrayList<String> getMapperList() {
		return mapperList;
	}

	public void setMapperList(ArrayList<String> mapperList) {
		this.mapperList = mapperList;
	}

	public ArrayList<String> getRecordList() {
		return recordList;
	}

	public void setRecordList(ArrayList<String> recordList) {
		this.recordList = recordList;
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

	public void addNewDataset(String dataset, String source, String target) {
		// TODO Auto-generated method stub
		dataset = assignIRI(dataset);
		source = assignIRI(source);
		target = assignIRI(target);
		
		Model model = getModel();

		Resource classResource = ResourceFactory.createResource("http://www.map.org/example#Dataset");
		Resource newResource = model.createResource(dataset);
		newResource.addProperty(RDF.type, classResource);
		Property sourceProperty = model.createProperty(assignIRI("map:source"));
		Property targetProperty = model.createProperty(assignIRI("map:target"));
		
		newResource.addProperty(sourceProperty, model.createResource(source));
		newResource.addProperty(targetProperty, model.createResource(target));
	}

	public void addNewHead(String sourceType, String dataset, String source, String target, String relation,
			String key, String operation, String keyType) {
		// TODO Auto-generated method stub
		dataset = assignIRI(dataset);
		source = assignIRI(source);
		target = assignIRI(target);
		relation = assignIRI(relation);
		// sourceType = assignIRI("map:" + sourceType);
		keyType = assignIRI("map:" + keyType.replace(" ", ""));
		
		Model model = getModel();

		String recordName = "";
		/*int totalSize = getMapperList().size();
		
		if (totalSize < 9) {
			recordName = "map:ConceptMapper_0" + ( totalSize + 1 ); 
		} else {
			recordName = "map:ConceptMapper_" + ( totalSize + 1 );
		}*/
		
		String sourceSegment = assignPrefix(source).replaceAll(":", "_");
		String targetSegment = assignPrefix(target).replaceAll(":", "_");
		recordName = "map:" + sourceSegment + "_" + targetSegment;
		
		Resource classResource = ResourceFactory.createResource("http://www.map.org/example#ConceptMapper");
		Resource newResource = model.createResource(assignIRI(recordName));
		newResource.addProperty(RDF.type, classResource);
		
		Property datasetProperty = model.createProperty(assignIRI("map:dataset"));
		Property sourceProperty = model.createProperty(assignIRI("map:sourceConcept"));
		Property sourceTypeProperty = model.createProperty(assignIRI("map:matchedInstances"));
		Property targetProperty = model.createProperty(assignIRI("map:targetConcept"));
		Property keyProperty = model.createProperty(assignIRI("map:iriValue"));
		Property keyPropertyType = model.createProperty(assignIRI("map:iriValueType"));
		Property relationProperty = model.createProperty(assignIRI("map:relation"));
		
		newResource.addProperty(sourceTypeProperty, model.createLiteral(sourceType));
		
		newResource.addProperty(datasetProperty, model.createResource(dataset));
		newResource.addProperty(sourceProperty, model.createResource(source));
		newResource.addProperty(targetProperty, model.createResource(target));
		newResource.addProperty(relationProperty, model.createResource(relation));
		newResource.addProperty(keyPropertyType, model.createResource(keyType));
		
		/*if (keyType.contains("Direct") || keyType.contains("Expression")) {
			if (key.contains("http") || key.contains("www") || key.contains(":")) {
				if (key.contains("\"") || key.contains(",")  || key.contains("+")  || key.contains("-")
						|| key.contains("*")  || key.contains("/") || key.contains("}")  || key.contains("(")) {
					newResource.addProperty(keyProperty, model.createLiteral(key));
				} else {
					key = assignIRI(key);
					newResource.addProperty(keyProperty, model.createResource(key));
				}
			} else {
				newResource.addProperty(keyProperty, model.createLiteral(key));
			}
		}*/
		
		if (key.trim().length() > 0) {
			if (keyType.contains("Expression")) {
				key = key.replaceAll("\"", "");
				newResource.addProperty(keyProperty, model.createLiteral(key));
			} else {
				key = assignIRI(key);
				newResource.addProperty(keyProperty, model.createResource(key));
			}
		}
		
		if (operation.trim().length() != 0) {
			Property operationProperty = model.createProperty(assignIRI("map:operation"));
			
			if (operation.contains("http") || operation.contains("www") || operation.contains(":")) {
				if (operation.contains("\"") || operation.contains(",")  || operation.contains("+")  || operation.contains("-")
						|| operation.contains("*")  || operation.contains("/") || operation.contains("}")  || operation.contains("(")) {
					newResource.addProperty(operationProperty, model.createLiteral(operation));
				} else {
					operation = assignIRI(operation);
					newResource.addProperty(operationProperty, model.createResource(operation));
				}
			} else {
				newResource.addProperty(operationProperty, model.createLiteral(operation));
			}
		}
	}

	public void addNewRecord(String mapper, String target, String source, String sourceType) {
		// TODO Auto-generated method stub
		target = assignIRI(target);
		mapper = assignIRI(mapper);
		sourceType = assignIRI("map:" + sourceType.replaceAll(" ", ""));
		
		Model model = getModel();

		Resource classResource = ResourceFactory.createResource("http://www.map.org/example#PropertyMapper");
		
		String recordName = "";
		int totalSize = getRecordList().size();
		
		if (totalSize < 9) {
			recordName = "map:PropertyMapper_0" + ( totalSize + 1 ); 
		} else {
			recordName = "map:PropertyMapper_" + ( totalSize + 1 );
		}
		
		Resource newResource = model.createResource(assignIRI(recordName));
		newResource.addProperty(RDF.type, classResource);
		
		Property mapProperty = model.createProperty(assignIRI("map:ConceptMapper"));
		Property sourceProperty = model.createProperty(assignIRI("map:sourceProperty"));
		Property targetProperty = model.createProperty(assignIRI("map:targetProperty"));
		Property sourceTypeProperty = model.createProperty(assignIRI("map:sourcePropertyType"));
		
		newResource.addProperty(sourceTypeProperty, model.createResource(sourceType));
		
		newResource.addProperty(mapProperty, model.createResource(mapper));
		
		newResource.addProperty(targetProperty, model.createResource(target));
		
		if (sourceType.contains("Expression")) {
			source = source.replaceAll("\"", "");
			newResource.addProperty(sourceProperty, model.createLiteral(source));
		} else {
			source = assignIRI(source);
			newResource.addProperty(sourceProperty, model.createResource(source));
		}
	}

	public LinkedHashMap<String, String> extractAssociatedRecordProperties(String selectedResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		selectedResource = assignIRI(selectedResource);
		
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		
		String sparql = "SELECT ?s ?p ?o WHERE {?s ?p ?o. FILTER regex(str(?s), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = String.valueOf(querySolution.get("s"));

			if (subject.trim().equals(selectedResource.trim())) {
				String property = String.valueOf(querySolution.get("p"));
				String object = String.valueOf(querySolution.get("o"));

				property = assignPrefix(property);
				object = assignPrefix(object);
				
				hashMap.put(property, object);
			}
		}
		return hashMap;
	}
	
	public void updateResource(String previousResource, String currentResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		previousResource = assignIRI(previousResource);
		currentResource = assignIRI(currentResource);

		Resource resource = model.getResource(previousResource);
		ResourceUtils.renameResource(resource, currentResource);
	}
	
	public void editProperty(String name, String key, String previousValue, String value) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		RDFNode node = null, rdfNode = null;
		if (value.contains("http://") || value.contains(":")) {
			value = assignIRI(value);
			node = ResourceFactory.createResource(value);
		} else {
			node = ResourceFactory.createStringLiteral(value);
		}

		if (previousValue.contains("http://") || previousValue.contains(":")) {
			previousValue = assignIRI(previousValue);
			rdfNode = ResourceFactory.createResource(previousValue);
		} else {
			rdfNode = ResourceFactory.createStringLiteral(previousValue);
		}

		model.remove(resource, property, rdfNode);
		model.add(resource, property, node);
	}

	public void removeResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource resource = ResourceFactory.createResource(name);

		model.removeAll(resource, null, (RDFNode) null);
		model.removeAll(null, null, resource);
	}

	public ArrayList<String> extractAssociatedMapperList(String selectedResource) {
		// TODO Auto-generated method stub
		selectedResource = assignIRI(selectedResource);
		ArrayList<String> arrayList = new ArrayList<String>();
		String sparql = "PREFIX map:	<http://www.map.org/example#>\r\n"
				+ "SELECT ?s ?d WHERE {?s a map:ConceptMapper. "
				+ "?s map:dataset ?d. "
				+ "FILTER regex(str(?d), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String datasetString = String.valueOf(querySolution.get("d"));

			if (datasetString.trim().equals(selectedResource.trim())) {
				String subjectString = String.valueOf(querySolution.get("s"));
				
				arrayList.add(subjectString);
			}
		}
		return arrayList;
	}
	
	public ArrayList<String> extractAssociatedRecordList(String dataset, String mapper) {
		// TODO Auto-generated method stub
		dataset = assignIRI(dataset);
		mapper = assignIRI(mapper);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		String sparql = "PREFIX map:	<http://www.map.org/example#>\r\n"
				+ "SELECT ?s ?d ?m WHERE {?s a map:PropertyMapper. "
				+ "?s map:ConceptMapper ?m. "
				+ "?m map:dataset ?d. "
				+ "FILTER regex(str(?m), '" + mapper + "')."
				+ "FILTER regex(str(?d), '" + dataset + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String datasetString = String.valueOf(querySolution.get("d"));
			String mapperString = String.valueOf(querySolution.get("m"));

			if (datasetString.trim().equals(dataset.trim())) {
				if (mapperString.trim().equals(mapper.trim())) {
					String subjectString = String.valueOf(querySolution.get("s"));
					
					arrayList.add(subjectString);
				}
			}
		}
		return arrayList;
	}
}
