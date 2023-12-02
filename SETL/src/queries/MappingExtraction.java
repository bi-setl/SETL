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
import helper.Methods;
import helper.Variables;

public class MappingExtraction {
	private static final String MAP_OPERATION = "map:operation";
	private static final String CONCEPT_MAPPER = "ConceptMapper";
	private static final String MAP_TARGET_A_BOX_LOCATION = "map:targetABoxLocation";
	private static final String MAP_SOURCE_A_BOX_LOCATION = "map:sourceABoxLocation";
	private static final String MAP_TARGET_COMMON_PROPERTY = "map:targetCommonProperty";
	private static final String MAP_SOURCE_COMMON_PROPERTY = "map:sourceCommonProperty";
	private static final String MAP_RELATION = "map:relation";
	private static final String MAP_IRI_VALUE_TYPE = "map:iriValueType";
	private static final String MAP_IRI_VALUE = "map:iriValue";
	private static final String MAP_TARGET_CONCEPT = "map:targetConcept";
	private static final String MAP_MATCHED_INSTANCES = "map:matchedInstances";
	private static final String MAP_SOURCE_CONCEPT = "map:sourceConcept";
	private static final String MAP_DATASET = "map:dataset";
	private static final String MAP_PREFIX = "map:";
	private static final String DATASET = "Dataset";
	private static final String MAP_TARGET = "map:target";
	private static final String MAP_SOURCE = "map:source";
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

	public void addNewDataset(String dataset, String source, String target, String mapIRI) {
		// TODO Auto-generated method stub
		dataset = Methods.assignIRI(getPrefixMap(), dataset);
		source = Methods.assignIRI(getPrefixMap(), source);
		target = Methods.assignIRI(getPrefixMap(), target);

		Model model = getModel();

		Resource classResource = ResourceFactory.createResource(mapIRI + DATASET);
		Resource newResource = model.createResource(dataset);
		newResource.addProperty(RDF.type, classResource);
		Property sourceProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_SOURCE));
		Property targetProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_TARGET));

		newResource.addProperty(sourceProperty, model.createResource(source));
		newResource.addProperty(targetProperty, model.createResource(target));
	}

	public void addNewConcept(String sourceType, String dataset, String source, String target, String relation,
			String key, String operation, String keyType, String sourceComProperty,
			String targetComProperty, String filePath, String targetABoxPathString, String mapIRI) {

		dataset = Methods.assignIRI(getPrefixMap(), dataset);
		source = Methods.assignIRI(getPrefixMap(), source);
		target = Methods.assignIRI(getPrefixMap(), target);
		relation = Methods.assignIRI(getPrefixMap(), relation);
		sourceComProperty = Methods.assignIRI(getPrefixMap(), sourceComProperty);
		targetComProperty = Methods.assignIRI(getPrefixMap(), targetComProperty);
		keyType = Methods.assignIRI(getPrefixMap(), MAP_PREFIX + keyType.replace(" ", ""));

		Model model = getModel();

		String recordName = "";

		String sourceSegment = getProvValue(source);
		String targetSegment = getProvValue(target);
		recordName = MAP_PREFIX + sourceSegment + "_" + targetSegment;

		Resource classResource = model.createResource(mapIRI + CONCEPT_MAPPER);
		Resource newResource = model.createResource(Methods.assignIRI(getPrefixMap(), recordName));
		newResource.addProperty(RDF.type, classResource);

		Property datasetProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_DATASET));
		Property sourceProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_SOURCE_CONCEPT));
		Property sourceTypeProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_MATCHED_INSTANCES));
		Property targetProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_TARGET_CONCEPT));
		Property keyProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_IRI_VALUE));
		Property keyPropertyType = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_IRI_VALUE_TYPE));
		Property relationProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_RELATION));
		Property sourceCommonProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_SOURCE_COMMON_PROPERTY));
		Property targetCommonProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_TARGET_COMMON_PROPERTY));
		Property sourceABoxPath = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_SOURCE_A_BOX_LOCATION));
		Property targetABoxPath = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_TARGET_A_BOX_LOCATION));

		newResource.addProperty(sourceTypeProperty, model.createLiteral(sourceType));
		newResource.addProperty(datasetProperty, model.createResource(dataset));
		newResource.addProperty(sourceProperty, model.createResource(source));
		newResource.addProperty(targetProperty, model.createResource(target));
		newResource.addProperty(relationProperty, model.createResource(relation));
		newResource.addProperty(keyPropertyType, model.createResource(keyType));

		Methods methods = new Methods();
		if (methods.checkString(filePath)) {
			newResource.addProperty(sourceABoxPath, model.createTypedLiteral(filePath));
			newResource.addProperty(targetABoxPath, model.createTypedLiteral(targetABoxPathString));
		}

		if (methods.checkString(sourceComProperty) && methods.checkString(targetComProperty)) {
			newResource.addProperty(sourceCommonProperty, model.createResource(sourceComProperty));
			newResource.addProperty(targetCommonProperty, model.createResource(targetComProperty));
		}

		if (key.trim().length() > 0) {
			if (keyType.contains(Variables.EXPRESSION)) {
				key = key.replaceAll("\"", "");
				newResource.addProperty(keyProperty, model.createLiteral(key));
			} else {
				key = Methods.assignIRI(getPrefixMap(), key);
				newResource.addProperty(keyProperty, model.createResource(key));
			}
		}

		if (operation.trim().length() != 0) {
			Property operationProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), MAP_OPERATION));

			if (operation.contains("http") || operation.contains("www") || operation.contains(":")) {
				if (operation.matches(".*[\"+,\\-*/\\)}(].*")) {
					newResource.addProperty(operationProperty, model.createLiteral(operation));
				} else {
					operation = Methods.assignIRI(getPrefixMap(), operation);
					newResource.addProperty(operationProperty, model.createResource(operation));
				}
			} else {
				newResource.addProperty(operationProperty, model.createLiteral(operation));
			}
		}
	}

	public void addNewRecord(String mapper, String target, String source, String sourceType) {
		// TODO Auto-generated method stub
		target = Methods.assignIRI(getPrefixMap(), target);
		mapper = Methods.assignIRI(getPrefixMap(), mapper);
		sourceType = Methods.assignIRI(getPrefixMap(), MAP_PREFIX + sourceType.replaceAll(" ", ""));

		Model model = getModel();

		Resource classResource = ResourceFactory.createResource("http://www.map.org/example#PropertyMapper");

		String recordName = "";
		int totalSize = getRecordList().size();

		String sourceKey = Methods.extractKeyWordFromIRI(source);
		String targetKey = Methods.extractKeyWordFromIRI(target);

		if (totalSize < 9) {
			recordName = "map:PropertyMapper_0" + ( totalSize + 1 ); 
		} else {
			recordName = "map:PropertyMapper_" + ( totalSize + 1 );
		}

		recordName += "_" + sourceKey + "_" + targetKey;

		Resource newResource = model.createResource(Methods.assignIRI(getPrefixMap(), recordName));
		newResource.addProperty(RDF.type, classResource);

		Property mapProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), "map:ConceptMapper"));
		Property sourceProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), "map:sourceProperty"));
		Property targetProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), "map:targetProperty"));
		Property sourceTypeProperty = model.createProperty(Methods.assignIRI(getPrefixMap(), "map:sourcePropertyType"));

		newResource.addProperty(sourceTypeProperty, model.createResource(sourceType));

		newResource.addProperty(mapProperty, model.createResource(mapper));

		newResource.addProperty(targetProperty, model.createResource(target));

		if (sourceType.contains("Expression")) {
			source = source.replaceAll("\"", "");
			newResource.addProperty(sourceProperty, model.createLiteral(source));
		} else {
			source = Methods.assignIRI(getPrefixMap(), source);
			newResource.addProperty(sourceProperty, model.createResource(source));
		}
	}

	public LinkedHashMap<String, String> extractAssociatedRecordProperties(String selectedResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		selectedResource = Methods.assignIRI(getPrefixMap(), selectedResource);

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

		previousResource = Methods.assignIRI(getPrefixMap(), previousResource);
		currentResource = Methods.assignIRI(getPrefixMap(), currentResource);

		Resource resource = model.getResource(previousResource);
		ResourceUtils.renameResource(resource, currentResource);
	}

	public void editProperty(String name, String key, String previousValue, String value) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = Methods.assignIRI(getPrefixMap(), name);
		key = Methods.assignIRI(getPrefixMap(), key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		RDFNode node = null, rdfNode = null;
		if (value.contains("http://") || value.contains(":")) {
			value = Methods.assignIRI(getPrefixMap(), value);
			node = ResourceFactory.createResource(value);
		} else {
			node = ResourceFactory.createStringLiteral(value);
		}

		if (previousValue.contains("http://") || previousValue.contains(":")) {
			previousValue = Methods.assignIRI(getPrefixMap(), previousValue);
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

		name = Methods.assignIRI(getPrefixMap(), name);

		Resource resource = ResourceFactory.createResource(name);

		model.removeAll(resource, null, (RDFNode) null);
		model.removeAll(null, null, resource);
	}

	public ArrayList<String> extractAssociatedMapperList(String selectedResource) {
		// TODO Auto-generated method stub
		selectedResource = Methods.assignIRI(getPrefixMap(), selectedResource);
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
		dataset = Methods.assignIRI(getPrefixMap(), dataset);
		mapper = Methods.assignIRI(getPrefixMap(), mapper);

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
}
