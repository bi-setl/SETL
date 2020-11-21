package queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import controller.TBoxDefinition;
import helper.FileMethods;
import helper.Methods;

public class TBoxExtraction {
	public static final String TEMP_TBOX_MODEL_TTL = "TEMP_TBOX_MODEL.ttl";
	private static final String TEMPHIER_IRI = "https://www.temphier.com/";
	private ArrayList<String> classList;
	private ArrayList<String> objectList;
	private ArrayList<String> dataList;
	private ArrayList<String> levelList;
	private LinkedHashMap<String, ArrayList<String>> cubeList;
	private LinkedHashMap<String, ArrayList<String>> cuboidList;
	private LinkedHashMap<String, String> cubeNodeList;
	private LinkedHashMap<String, String> cuboidNodeList;
	private ArrayList<String> dimensionList;
	private ArrayList<String> hierarchyList;
	private LinkedHashMap<String, String> hierarchyStepsList;
	private ArrayList<String> measureList;
	private ArrayList<String> datasetList;
	private ArrayList<String> attributeList;
	private ArrayList<String> rollupList;
	private ArrayList<String> ontologyList;
	private LinkedHashMap<String, String> prefixMap;
	private LinkedHashMap<String, ArrayList<String>> annotationMap;
	private LinkedHashMap<String, ArrayList<String>> descriptionMap;
	private LinkedHashMap<String, ArrayList<String>> mdMap;
	private Model model;
	private Model tempModel;

	private void initializeAll() {
		// TODO Auto-generated method stub
		setClassList(new ArrayList<>());
		setObjectList(new ArrayList<>());
		setDataList(new ArrayList<>());
		setLevelList(new ArrayList<>());
		setDimensionList(new ArrayList<>());
		setHierarchyList(new ArrayList<>());
		setHierarchyStepsList(new LinkedHashMap());
		setMeasureList(new ArrayList<>());
		setDatasetList(new ArrayList<>());
		setAttributeList(new ArrayList<>());
		setRollupList(new ArrayList<>());
		setAnnotationMap(new LinkedHashMap<>());
		setDescriptionMap(new LinkedHashMap<>());
		setMdMap(new LinkedHashMap<>());
		setCubeList(new LinkedHashMap<>());
		setCuboidList(new LinkedHashMap<>());
		setCubeNodeList(new LinkedHashMap<>());
		setCuboidNodeList(new LinkedHashMap<>());
		setOntologyList(new ArrayList<>());

		tempModel = ModelFactory.createDefaultModel();
	}

	public LinkedHashMap<String, ArrayList<String>> getAnnotationMap() {
		return annotationMap;
	}

	public void setAnnotationMap(LinkedHashMap<String, ArrayList<String>> annotationMap) {
		this.annotationMap = annotationMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getDescriptionMap() {
		return descriptionMap;
	}

	public void setDescriptionMap(LinkedHashMap<String, ArrayList<String>> descriptionMap) {
		this.descriptionMap = descriptionMap;
	}

	public TBoxExtraction(String filePath) {
		// TODO Auto-generated constructor stub
		initializeAll();
		prefixMap = new LinkedHashMap<>();

		File file = new File(filePath);
		if (file.exists() && !file.isDirectory()) {
			setModel(readFileFromPath(filePath));

			setPrefixMap(extractAllPrefixes(filePath));

			if (getPrefixMap().size() == 0) {
				setPrefixMap(new TBoxDefinition().getAllPredefinedPrefixes());
			}

			setClassList(extractAllConceptClasses());
			setObjectList(extractAllObjectProperties());
			setDataList(extractAllDataProperties());
			setLevelList(extractAllLevels());
			setMeasureList(extractAllMeasures());
			generateDataStructure();
			setDimensionList(extractAllDimensions());
			setHierarchyList(extractAllHierarchies());
			setHierarchyStepsList(extractAllHierarchySteps());
			setAttributeList(extractAllAttributes());
			setRollupList(extractAllRollUps());
			setDatasetList(extractAllDatasets());
			setOntologyList(extractAllOntologies());
		} else {
			System.out.println("Check file path");
		}

		printAllComponents();
	}

	private ArrayList<String> extractAllOntologies() {
		// TODO Auto-generated method stub
		ArrayList<String> strings = new ArrayList<>();
		// String value = "RollupProperty";

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a owl:Ontology; ?p ?o.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				strings.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return strings;
	}

	private void generateDataStructure() {
		// TODO Auto-generated method stub
		Model model = getModel();

		ArrayList<String> cubes = new ArrayList<>();
		ArrayList<String> cuboids = new ArrayList<>();

		LinkedHashMap<String, ArrayList<String>> cubeList = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> cuboidList = new LinkedHashMap<>();

		LinkedHashMap<String, String> cubeNodeList = new LinkedHashMap<>();
		LinkedHashMap<String, String> cuboidNodeList = new LinkedHashMap<>();
		LinkedHashMap<String, String> nodeHier = new LinkedHashMap<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb:DataStructureDefinition; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = String.valueOf(querySolution.get("s"));

			String sparql2 = "PREFIX qb:		<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n" + "SELECT ?s ?p ?o WHERE { ?s ?p ?o. "
					+ "FILTER regex(str(?s), '" + name + "').}";

			Query query2 = QueryFactory.create(sparql2);
			QueryExecution execution2 = QueryExecutionFactory.create(query2, model);
			ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());

			boolean status = false;
			while (resultSet2.hasNext()) {
				QuerySolution querySolution2 = (QuerySolution) resultSet2.next();
				String cube = String.valueOf(querySolution2.get("s"));
				if (cube.equals(name)) {
					String property = String.valueOf(querySolution2.get("p"));
					if (property.contains("isCuboidOf")) {
						status = true;
					}
				}
			}

			if (status) {
				cuboids.add(assignPrefix(name));
			} else {
				cubes.add(assignPrefix(name));
			}
		}

		for (String string : cubes) {
			string = assignIRI(string);
			String nodePrefix = "_:cn";
			ArrayList<String> blankNodes = new ArrayList<>();

			String sparql2 = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE { ?s a qb:DataStructureDefinition." + "?s qb:component ?o. " + "?o ?x ?y."
					+ "FILTER regex(str(?s), '" + string + "').}";
			Query query2 = QueryFactory.create(sparql2);
			QueryExecution execution2 = QueryExecutionFactory.create(query2, model);
			ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());

			while (resultSet2.hasNext()) {
				QuerySolution querySolution2 = (QuerySolution) resultSet2.next();
				String cube = String.valueOf(querySolution2.get("s"));
				String subject = String.valueOf(querySolution2.get("o"));
				String predicate = String.valueOf(querySolution2.get("x"));
				String value = String.valueOf(querySolution2.get("y"));

				if (cube.equals(string)) {
					if (nodeHier.containsKey(subject)) {
						String hierName = nodeHier.get(subject);
						String resourceIRI = cubeNodeList.get(hierName);

						Resource resource = tempModel.createResource(resourceIRI);
						Property property = tempModel.createProperty(predicate);

						if (value.contains(":") || value.contains("www.")) {
							resource.addProperty(property, tempModel.createResource(value));
						} else {
							resource.addProperty(property, tempModel.createLiteral(value));
						}

					} else {
						String hierName = nodePrefix + (nodeHier.size() + 1);
						nodeHier.put(subject, hierName);

						blankNodes.add(hierName);

						String resourceIRI = TEMPHIER_IRI + hierName;
						cubeNodeList.put(hierName, resourceIRI);

						Resource resource = tempModel.createResource(resourceIRI);
						Property property = tempModel.createProperty(predicate);

						if (value.contains(":") || value.contains("www.")) {
							resource.addProperty(property, tempModel.createResource(value));
						} else {
							resource.addProperty(property, tempModel.createLiteral(value));
						}
					}
				}
			}

			cubeList.put(assignPrefix(string), blankNodes);
		}

		/*
		 * for (Map.Entry<String, ArrayList<String>> map : cubeList.entrySet()) { String
		 * key = map.getKey(); ArrayList<String> arrayList = map.getValue();
		 * 
		 * System.out.println(key + " - " + arrayList.size()); }
		 */

		for (String string : cuboids) {
			string = assignIRI(string);
			String nodePrefix = "_:cn";
			ArrayList<String> blankNodes = new ArrayList<>();

			String sparql2 = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE { ?s a qb:DataStructureDefinition." + "?s qb:component ?o. " + "?o ?x ?y."
					+ "FILTER regex(str(?s), '" + string + "').}";
			Query query2 = QueryFactory.create(sparql2);
			QueryExecution execution2 = QueryExecutionFactory.create(query2, model);
			ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());

			// new FileMethods().printResultSet(resultSet2);

			while (resultSet2.hasNext()) {
				QuerySolution querySolution2 = (QuerySolution) resultSet2.next();
				String cube = String.valueOf(querySolution2.get("s"));
				String subject = String.valueOf(querySolution2.get("o"));
				String predicate = String.valueOf(querySolution2.get("x"));
				String value = String.valueOf(querySolution2.get("y"));

				if (cube.equals(string)) {
					if (nodeHier.containsKey(subject)) {
						String hierName = nodeHier.get(subject);
						String resourceIRI = cuboidNodeList.get(hierName);

						Resource resource = tempModel.createResource(resourceIRI);
						Property property = tempModel.createProperty(predicate);

						if (value.contains(":") || value.contains("www.")) {
							resource.addProperty(property, tempModel.createResource(value));
						} else {
							resource.addProperty(property, tempModel.createLiteral(value));
						}

					} else {
						String hierName = nodePrefix + (nodeHier.size() + 1);
						nodeHier.put(subject, hierName);

						blankNodes.add(hierName);

						String resourceIRI = TEMPHIER_IRI + hierName;
						cuboidNodeList.put(hierName, resourceIRI);

						Resource resource = tempModel.createResource(resourceIRI);
						Property property = tempModel.createProperty(predicate);

						if (value.contains(":") || value.contains("www.")) {
							resource.addProperty(property, tempModel.createResource(value));
						} else {
							resource.addProperty(property, tempModel.createLiteral(value));
						}
					}
				}
			}

			cuboidList.put(assignPrefix(string), blankNodes);
		}

		setCubeList(cubeList);
		setCuboidList(cuboidList);
		setCubeNodeList(cubeNodeList);
		setCuboidNodeList(cuboidNodeList);
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

	public void reloadAll() {
		// TODO Auto-generated constructor stub
		initializeAll();
		Model model = getModel();
		if (model != null) {
			generateDataStructure();
			setClassList(extractAllConceptClasses());
			setObjectList(extractAllObjectProperties());
			setDataList(extractAllDataProperties());
			setLevelList(extractAllLevels());
			setMeasureList(extractAllMeasures());
			setDimensionList(extractAllDimensions());
			setHierarchyList(extractAllHierarchies());
			setHierarchyStepsList(extractAllHierarchySteps());
			setAttributeList(extractAllAttributes());
			setRollupList(extractAllRollUps());
			setDatasetList(extractAllDatasets());
			setOntologyList(extractAllOntologies());
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

	public ArrayList<String> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<String> classList) {
		this.classList = classList;
	}

	public ArrayList<String> getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList<String> objectList) {
		this.objectList = objectList;
	}

	public ArrayList<String> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<String> dataList) {
		this.dataList = dataList;
	}

	public ArrayList<String> extractAllDataProperties() {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();

		Model model = getModel();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a owl:DatatypeProperty.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String className = String.valueOf(querySolution.get("s"));
			arrayList.add(assignPrefix(className));
		}
		return arrayList;
	}

	private ArrayList<String> extractAllObjectProperties() {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();

		Model model = getModel();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a owl:ObjectProperty.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String className = String.valueOf(querySolution.get("s"));
			arrayList.add(assignPrefix(className));
		}
		return arrayList;
	}

	private ArrayList<String> extractAllConceptClasses() {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();
		Model model = getModel();

		String sparql = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a owl:Class. }";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String className = String.valueOf(querySolution.get("s"));
			arrayList.add(assignPrefix(className));
		}
		return arrayList;
	}

	private ArrayList<String> extractAllDatasets() {
		// TODO Auto-generated method stub
		ArrayList<String> datasets = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb:DataSet; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = String.valueOf(querySolution.get("s"));
			datasets.add(assignPrefix(name));
		}

		return datasets;
	}

	private ArrayList<String> extractAllRollUps() {
		// TODO Auto-generated method stub
		ArrayList<String> strings = new ArrayList<>();
		// String value = "RollupProperty";

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb4o:RollupProperty; ?p ?o.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				strings.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return strings;
	}

	private ArrayList<String> extractAllAttributes() {
		// TODO Auto-generated method stub
		ArrayList<String> attributes = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb4o:LevelAttribute; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				attributes.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return attributes;
	}

	private ArrayList<String> extractAllHierarchies() {
		// TODO Auto-generated method stub
		ArrayList<String> hierarchies = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb4o:Hierarchy; ?p ?o.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				hierarchies.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return hierarchies;
	}

	private ArrayList<String> extractAllDimensions() {
		// TODO Auto-generated method stub
		ArrayList<String> dimensions = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb:DimensionProperty; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				dimensions.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dimensions;
	}

	private ArrayList<String> extractAllMeasures() {
		// TODO Auto-generated method stub
		ArrayList<String> measures = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb:MeasureProperty; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				measures.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return measures;
	}

	private ArrayList<String> extractAllLevels() {
		// TODO Auto-generated method stub
		ArrayList<String> levels = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?s WHERE { ?s a qb4o:LevelProperty; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				levels.add(assignPrefix(name));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return levels;
	}

	private LinkedHashMap<String, String> extractAllHierarchySteps() {
		// TODO Auto-generated method stub
		String hierPrefix = "_:hs";

		LinkedHashMap<String, String> nodeHier = new LinkedHashMap<>();
		LinkedHashMap<String, String> hierSub = new LinkedHashMap<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE { ?s a qb4o:HierarchyStep; ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString().trim();

			if (nodeHier.containsKey(subject)) {
				String hierName = nodeHier.get(subject);
				String resourceIRI = hierSub.get(hierName);

				Resource resource = tempModel.createResource(resourceIRI);
				Property property = tempModel.createProperty(querySolution.get("p").toString().trim());

				String value = querySolution.get("o").toString().trim();

				if (value.contains(":") || value.contains("www.")) {
					resource.addProperty(property, tempModel.createResource(value));
				} else {
					resource.addProperty(property, tempModel.createLiteral(value));
				}

			} else {
				String hierName = hierPrefix + (nodeHier.size() + 1);
				nodeHier.put(subject, hierName);
				String resourceIRI = TEMPHIER_IRI + hierName;
				hierSub.put(hierName, resourceIRI);

				Resource resource = tempModel.createResource(resourceIRI);
				Property property = tempModel.createProperty(querySolution.get("p").toString().trim());

				String value = querySolution.get("o").toString().trim();

				if (value.contains(":") || value.contains("www.")) {
					resource.addProperty(property, tempModel.createResource(value));
				} else {
					resource.addProperty(property, tempModel.createLiteral(value));
				}
			}
		}

		// new FileMethods().printModel(tempModel);

		return hierSub;
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

	public String getModelText(String type) {
		// TODO Auto-generated method stub
		if (type.equals("Turtle")) {
			return printAllComponents();
		} else {
			String text = printAllComponents();
			new FileMethods().writeText(TEMP_TBOX_MODEL_TTL, text);

			Model model = readFileFromPath(TEMP_TBOX_MODEL_TTL);

			StringWriter out = new StringWriter();
			model.write(out, type);
			String result = out.toString();
			return result;
		}
	}

	public void generateMaps(String selectedResource, boolean isStep) {
		// TODO Auto-generated method stub
		Model model = tempModel;
		selectedResource = assignIRI(selectedResource);

		TBoxDefinition boxDefinition = new TBoxDefinition();
		LinkedHashMap<String, ArrayList<String>> annMap = boxDefinition.getAllAnnotationProperties();
		LinkedHashMap<String, ArrayList<String>> desMap = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> mdMap = new LinkedHashMap<>();

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

				if (property.equals("rdf:type")) {
					switch (object) {
					case "qb:DimensionProperty":
					case "qb4o:Hierarchy":
					case "qb4o:LevelProperty":
					case "qb4o:LevelAttribute":
					case "qb:MeasureProperty":
					case "qb4o:RollupProperty":
					case "qb:DataSet":
						ArrayList<String> value = new ArrayList<>();
						value.add(object);
						mdMap.put(property, value);
						break;
					default:
						value = new ArrayList<>();
						value.add(object);
						desMap.put(property, value);
						break;
					}
				} else if (property.startsWith("qb:") || property.startsWith("qb4o:") || property.startsWith("dct:")) {
					boolean status = false;
					for (Map.Entry<String, ArrayList<String>> map : mdMap.entrySet()) {
						String key = map.getKey();

						if (key.equals(property)) {
							mdMap.get(key).add(object);
							status = true;
						}
					}

					if (!status) {
						ArrayList<String> value = new ArrayList<>();
						value.add(object);
						mdMap.put(property, value);
					}
				} else {
					boolean status = false;
					for (Map.Entry<String, ArrayList<String>> map : annMap.entrySet()) {
						String key = map.getKey();

						if (key.equals(property)) {
							annMap.get(key).add(object);
							status = true;
						}
					}

					if (!status) {
						for (Map.Entry<String, ArrayList<String>> map : desMap.entrySet()) {
							String key = map.getKey();

							if (key.equals(property)) {
								desMap.get(key).add(object);
								status = true;
							}
						}

						if (!status) {
							ArrayList<String> value = new ArrayList<>();
							value.add(object);
							desMap.put(property, value);
						}
					}
				}
			}
		}

		LinkedHashMap<String, ArrayList<String>> newHashMap = new LinkedHashMap<>();
		for (Map.Entry<String, ArrayList<String>> map : annMap.entrySet()) {
			String key = map.getKey();
			ArrayList<String> valueList = map.getValue();

			if (valueList.size() != 0) {
				newHashMap.put(key, valueList);
			}
		}

		setDescriptionMap(desMap);
		setAnnotationMap(newHashMap);
		setMdMap(mdMap);
	}

	public void generateMaps(String selectedResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		selectedResource = assignIRI(selectedResource);

		TBoxDefinition boxDefinition = new TBoxDefinition();
		LinkedHashMap<String, ArrayList<String>> annMap = boxDefinition.getAllAnnotationProperties();
		LinkedHashMap<String, ArrayList<String>> desMap = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> mdMap = new LinkedHashMap<>();

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

				if (property.equals("rdf:type")) {
					switch (object) {
					case "qb:DimensionProperty":
					case "qb4o:Hierarchy":
					case "qb4o:LevelProperty":
					case "qb4o:LevelAttribute":
					case "qb:MeasureProperty":
					case "qb4o:RollupProperty":
					case "qb:DataSet":
						ArrayList<String> value = new ArrayList<>();
						value.add(object);
						mdMap.put(property, value);
						break;
					default:
						value = new ArrayList<>();
						value.add(object);
						desMap.put(property, value);
						break;
					}
				} else if (property.startsWith("qb:") || property.startsWith("qb4o:") || property.startsWith("dct:")) {
					boolean status = false;
					for (Map.Entry<String, ArrayList<String>> map : mdMap.entrySet()) {
						String key = map.getKey();

						if (key.equals(property)) {
							mdMap.get(key).add(object);
							status = true;
						}
					}

					if (!status) {
						ArrayList<String> value = new ArrayList<>();
						value.add(object);
						mdMap.put(property, value);
					}
				} else {
					boolean status = false;
					for (Map.Entry<String, ArrayList<String>> map : annMap.entrySet()) {
						String key = map.getKey();

						if (key.equals(property)) {
							annMap.get(key).add(object);
							status = true;
						}
					}

					if (!status) {
						for (Map.Entry<String, ArrayList<String>> map : desMap.entrySet()) {
							String key = map.getKey();

							if (key.equals(property)) {
								desMap.get(key).add(object);
								status = true;
							}
						}

						if (!status) {
							ArrayList<String> value = new ArrayList<>();
							value.add(object);
							desMap.put(property, value);
						}
					}
				}
			}
		}

		LinkedHashMap<String, ArrayList<String>> newHashMap = new LinkedHashMap<>();
		for (Map.Entry<String, ArrayList<String>> map : annMap.entrySet()) {
			String key = map.getKey();
			ArrayList<String> valueList = map.getValue();

			if (valueList.size() != 0) {
				newHashMap.put(key, valueList);
			}
		}

		setDescriptionMap(desMap);
		setAnnotationMap(newHashMap);
		setMdMap(mdMap);
	}

	public void updateResource(String previousResource, String currentResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		previousResource = assignIRI(previousResource);
		currentResource = assignIRI(currentResource);

		Resource resource = model.getResource(previousResource);
		ResourceUtils.renameResource(resource, currentResource);
	}

	public boolean addClassResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class");
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addObjectResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#ObjectProperty");
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addDataResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public void removeResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource resource = ResourceFactory.createResource(name);

		model.removeAll(resource, null, (RDFNode) null);
		model.removeAll(null, null, resource);
	}

	public void addProperty(String name, String key, String value) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		if (value.contains("http://") || value.contains(":")) {
			value = assignIRI(value);
			resource.addProperty(property, model.createResource(value));
		} else {
			resource.addLiteral(property, value);
		}
	}

	public void removeProperty(String name, String key, String value) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		RDFNode node = null;
		if (value.contains("http://") || value.contains(":")) {
			value = assignIRI(value);
			node = ResourceFactory.createResource(value);
		} else {
			node = ResourceFactory.createStringLiteral(value);
		}

		model.remove(resource, property, node);
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

	public void removeProperty(String name, String key) {
		// TODO Auto-generated method stub
		key = assignIRI(key);
		Model model = getModel();

		name = assignIRI(name);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		resource.removeAll(property);
	}

	public ArrayList<String> getLevelList() {
		return levelList;
	}

	public void setLevelList(ArrayList<String> levelList) {
		this.levelList = levelList;
	}

	public ArrayList<String> getDimensionList() {
		return dimensionList;
	}

	public void setDimensionList(ArrayList<String> dimensionList) {
		this.dimensionList = dimensionList;
	}

	public ArrayList<String> getHierarchyList() {
		return hierarchyList;
	}

	public void setHierarchyList(ArrayList<String> hierarchyList) {
		this.hierarchyList = hierarchyList;
	}

	public ArrayList<String> getMeasureList() {
		return measureList;
	}

	public void setMeasureList(ArrayList<String> measureList) {
		this.measureList = measureList;
	}

	public ArrayList<String> getDatasetList() {
		return datasetList;
	}

	public void setDatasetList(ArrayList<String> datasetList) {
		this.datasetList = datasetList;
	}

	public ArrayList<String> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(ArrayList<String> attributeList) {
		this.attributeList = attributeList;
	}

	public ArrayList<String> getRollupList() {
		return rollupList;
	}

	public void setRollupList(ArrayList<String> rollupList) {
		this.rollupList = rollupList;
	}

	public String getType(String selectedResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		selectedResource = assignIRI(selectedResource);

		String sparql = "SELECT ?s ?x WHERE {?s ?p ?o. ?s a ?x. FILTER regex(str(?s), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String subject = String.valueOf(querySolution.get("s"));
			if (subject.equals(selectedResource)) {
				String type = String.valueOf(querySolution.get("x"));

				if (type.contains("Class") || type.contains("ObjectProperty") || type.contains("DatatypeProperty")) {
					String[] segments = type.split("#");
					if (segments.length == 2) {
						return segments[1];
					} else {
						return type;
					}
				}
			}
		}
		return "";
	}

	public LinkedHashMap<String, ArrayList<String>> getMdMap() {
		return mdMap;
	}

	public void setMdMap(LinkedHashMap<String, ArrayList<String>> mdMap) {
		this.mdMap = mdMap;
	}

	public void addProperty(String name, String key) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Resource classResource = ResourceFactory.createResource(key);
		resource.addProperty(RDF.type, classResource);
	}

	public boolean addDatasetResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb:DataSet"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addLevelResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb4o:LevelProperty"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addDataStructureResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb:DataStructureDefinition"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addMeasureResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb:MeasureProperty"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addHierarchyResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb4o:Hierarchy"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addDimensionResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb:DimensionProperty"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addRollUpResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb4o:RollupProperty"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addAttributeResource(String name) {
		// TODO Auto-generated method stub
		Model model = getModel();

		name = assignIRI(name);

		Resource classResource = ResourceFactory.createResource(assignIRI("qb4o:LevelAttribute"));
		Resource resource = ResourceFactory.createResource(name);
		if (model.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = model.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public ArrayList<String> getAllAssociatedObjectProperties(String string) {
		// TODO Auto-generated method stub
		ArrayList<String> objects = new ArrayList<>();

		string = assignIRI(string);

		String sparql = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "SELECT * WHERE { ?s a owl:ObjectProperty;.\r\n" + "?s rdfs:domain ?o. }";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// new FileMethods().printResultSet(resultSet);

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String name = null;
			try {
				name = String.valueOf(querySolution.get("s"));
				String domain = String.valueOf(querySolution.get("o"));

				if (string.contains(":")) {
					string = assignIRI(string);
				}

				// System.out.println(domain + " - " + string);

				if (domain.equals(string)) {
					objects.add(assignPrefix(name));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return objects;
	}

	public ArrayList<String> getAllAssociatedDataProperties(String string) {
		// TODO Auto-generated method stub
		ArrayList<String> owlDatatypeProperties = new ArrayList<>();

		string = assignIRI(string);

		String sparql = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "SELECT * WHERE { ?s a owl:DatatypeProperty;.\r\n" + "?s rdfs:domain ?o. }";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		// new FileMethods().printResultSet(set);

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String datatypeProperty = String.valueOf(querySolution.get("s"));
			String subject = String.valueOf(querySolution.get("o"));

			if (string.contains(":")) {
				string = assignIRI(string);
			}

			// System.out.println(subject + " - " + string);

			if (string.matches(subject)) {
				owlDatatypeProperties.add(assignPrefix(datatypeProperty));
			}
		}

		return owlDatatypeProperties;
	}

	public ArrayList<String> getAllAssociatedProperties(String string) {
		// TODO Auto-generated method stub
		ArrayList<String> properties = new ArrayList<>();

		string = assignIRI(string);

		String sparql = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?o WHERE { ?s a qb4o:LevelProperty;.\r\n"
				+ "?s qb4o:hasAttribute ?o. FILTER regex(str(?s), '" + string + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String datatypeProperty = String.valueOf(querySolution.get("o"));

			properties.add(assignPrefix(datatypeProperty));
		}

		return properties;
	}

	public ArrayList<String> getAllAssociatedDataStructureProperties(String string) {
		// TODO Auto-generated method stub
		ArrayList<String> properties = new ArrayList<>();

		string = assignIRI(string);

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE { ?s ?p ?o. ?s a qb:DataStructureDefinition. ?o ?x ?y. FILTER regex(str(?s), '"
				+ string + "').}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		// new FileMethods().printResultSet(set);

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String datatypeProperty = String.valueOf(querySolution.get("o"));

			properties.add(assignPrefix(datatypeProperty));
		}

		return properties;
	}

	private List<Resource> exploreAnonymousResource(Resource resource) {
		List<Property> collectionProperties = new LinkedList<Property>(
				Arrays.asList(OWL.unionOf, OWL.intersectionOf, RDF.first, RDF.rest));

		List<Resource> resources = new LinkedList<Resource>();
		Boolean needToTraverseNext = false;

		if (resource.isAnon()) {
			for (Property cp : collectionProperties) {
				if (resource.hasProperty(cp) && !resource.getPropertyResourceValue(cp).equals(RDF.nil)) {
					Resource nextResource = resource.getPropertyResourceValue(cp);
					resources.addAll(exploreAnonymousResource(nextResource));

					needToTraverseNext = true;
				}
			}

			if (!needToTraverseNext) {
				resources.add(resource);
			}
		} else {
			resources.add(resource);
		}

		return resources;
	}

	public String getResourceType(String selectedResource) {
		// TODO Auto-generated method stub
		Model model = getModel();

		selectedResource = assignIRI(selectedResource);

		String sparql = "SELECT ?s ?x WHERE {?s ?p ?o. ?s a ?x. FILTER regex(str(?s), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet set = ResultSetFactory.copyResults(execution.execSelect());

		while (set.hasNext()) {
			QuerySolution querySolution = (QuerySolution) set.next();
			String subject = String.valueOf(querySolution.get("s"));
			if (subject.equals(selectedResource)) {
				String type = String.valueOf(querySolution.get("x"));
				return type;
			}
		}
		return "";
	}

	public ArrayList<String> getOntologyList() {
		return ontologyList;
	}

	public void setOntologyList(ArrayList<String> ontologyList) {
		this.ontologyList = ontologyList;
	}

	public LinkedHashMap<String, HashMap<String, String>> extractDatasetProperties(String datasetName) {
		LinkedHashMap<String, HashMap<String, String>> hashMap = new LinkedHashMap<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT * WHERE { ?s a qb:DataSet."
				+ "?s ?p ?o." + "?o a qb:DataStructureDefinition." + "?o qb:component ?x." + "?x ?y ?z.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();

			if (getProvValue(subject).equals(getProvValue(datasetName))) {
				String xString = querySolution.get("x").toString();
				String yString = querySolution.get("y").toString();
				String zString = querySolution.get("z").toString();

				if (hashMap.containsKey(xString)) {
					hashMap.get(xString).put(yString, zString);
				} else {
					HashMap<String, String> hashedMap = new HashMap<>();
					hashedMap.put(yString, zString);
					hashMap.put(xString, hashedMap);
				}
			}
		}

		return hashMap;
	}

	public ArrayList<String> extractHierarchyLevels(String value) {
		// TODO Auto-generated method stub
		value = assignIRI(value);
		ArrayList<String> levelList = new ArrayList<>();
		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT ?s ?o WHERE {"
				+ " ?s a qb4o:Hierarchy." + "?s qb4o:hasLevel ?o." + "FILTER regex(str(?s), '" + value + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String object = querySolution.get("o").toString();

			if (value.equals(subject)) {
				levelList.add(object);
			}
		}
		return levelList;
	}

	public ArrayList<String> extractDatasetLevels(String datasetName) {
		ArrayList<String> levels = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT ?s ?z WHERE { ?s a qb:DataSet."
				+ "?s ?p ?o." + "?o a qb:DataStructureDefinition." + "?o qb:component ?x." + "?x qb4o:dimension ?z.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String object = querySolution.get("z").toString();

			if (getProvValue(subject).equals(getProvValue(datasetName))) {
				object = assignPrefix(object);
				levels.add(object);
			}
		}

		String sparql2 = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT ?s ?z WHERE { ?s a qb:DataSet."
				+ "?s ?p ?o." + "?o a qb:DataStructureDefinition." + "?o qb:component ?x." + "?x qb4o:level ?z.}";
		Query query2 = QueryFactory.create(sparql2);
		QueryExecution execution2 = QueryExecutionFactory.create(query2, model);
		ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet2));

		while (resultSet2.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet2.next();
			String subject = querySolution.get("s").toString();
			String object = querySolution.get("z").toString();

			if (getProvValue(subject).equals(getProvValue(datasetName))) {
				object = assignPrefix(object);
				levels.add(object);
			}
		}

		return levels;
	}

	public ArrayList<String> extractDatasetMeasures(String datasetName) {
		ArrayList<String> levels = new ArrayList<>();

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT ?s ?z WHERE { ?s a qb:DataSet."
				+ "?s ?p ?o." + "?o a qb:DataStructureDefinition." + "?o qb:component ?x." + "?x qb:measure ?z.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String object = querySolution.get("z").toString();

			if (getProvValue(subject).equals(getProvValue(datasetName))) {
				object = assignPrefix(object);
				levels.add(object);
			}
		}

		return levels;
	}

	public String getProvValue(String subject) {
		// TODO Auto-generated method stub
		if (subject.contains("#")) {
			String[] parts = subject.split("#");
			if (parts.length == 2) {
				return parts[1];
			} else {
				return subject;
			}
		} else if (subject.contains(":") && !(subject.contains("http") || subject.contains("www"))) {
			String[] parts = subject.split(":");
			if (parts.length == 2) {
				return parts[1];
			} else {
				return subject;
			}
		} else {
			String[] parts = subject.split("/");
			return parts[parts.length - 1];
		}
	}

	public String getUniqueSubjects(String provFile) {
		// TODO Auto-generated method stub
		Model model = ModelFactory.createDefaultModel();
		model.read(provFile);

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT DISTINCT ?s WHERE {" + "?s ?p ?o."
				+ "}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

		int count = 1;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			count++;
		}

		return String.valueOf(count);
	}

	public void addDimensionResource(String dimiri, String dimName, String dimLabel, String dimlang,
			List<String> hierarchies, String range) {
		// TODO Auto-generated method stub
		dimName = dimiri + dimName;

		Resource resource = getModel().createResource(dimName);

		Resource classResource = getModel().createResource("http://purl.org/linked-data/cube#DimensionProperty");

		resource.addProperty(RDF.type, classResource);

		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(dimLabel, "en");

		resource.addProperty(labelProperty, labelLiteral);

		if (hierarchies.size() > 0) {
			Property hierarchyProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#hasHierarchy");

			for (String string : hierarchies) {
				resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(string)));
			}
		}

		if (range.trim().length() > 0) {
			Property hierarchyProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
			resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(range)));
		}
	}

	public void addLevelResource(String leveliri, String levelName, String levelLabel, String lang,
			List<String> selectedAttributes, String datatype) {
		// TODO Auto-generated method stub
		levelName = leveliri + levelName;

		Resource resource = getModel().createResource(levelName);

		Resource classResource = getModel().createResource("http://purl.org/qb4olap/cubes#LevelProperty");

		resource.addProperty(RDF.type, classResource);

		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(levelLabel, "en");

		resource.addProperty(labelProperty, labelLiteral);

		if (selectedAttributes.size() > 0) {
			Property hierarchyProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#hasAttribute");

			for (String string : selectedAttributes) {
				resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(string)));
			}
		}

		if (datatype.trim().length() > 0) {
			Property hierarchyProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
			resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(datatype)));
		}
	}

	public void addRollUpProperty(String rupiri, String rupPropertyName) {
		// TODO Auto-generated method stub
		rupPropertyName = rupiri + rupPropertyName;

		Resource resource = getModel().createResource(rupPropertyName);

		Resource classResource = getModel().createResource("http://purl.org/qb4olap/cubes#RollupProperty");

		resource.addProperty(RDF.type, classResource);
	}

	public void addMeasureProperty(String leveliri, String levelName, String levelLabel, String lang, String range) {
		// TODO Auto-generated method stub
		levelName = leveliri + levelName;

		Resource resource = getModel().createResource(levelName);

		Resource classResource = getModel().createResource("http://purl.org/linked-data/cube#MeasureProperty");

		resource.addProperty(RDF.type, classResource);

		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(levelLabel, "en");

		resource.addProperty(labelProperty, labelLiteral);

		Property hierarchyProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
		resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(range)));
	}

	public void addLevelAttribute(String leveliri, String levelName, String levelLabel, String lang, String range) {
		// TODO Auto-generated method stub
		levelName = leveliri + levelName;

		Resource resource = getModel().createResource(levelName);

		Resource classResource = getModel().createResource("http://purl.org/qb4olap/cubes#LevelAttribute");

		resource.addProperty(RDF.type, classResource);

		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(levelLabel, "en");

		resource.addProperty(labelProperty, labelLiteral);

		Property hierarchyProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
		resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(range)));
	}

	public void addDatasetProperty(String datasetIRI, String datasetName, String datasetcube) {
		// TODO Auto-generated method stub
		
		datasetName = datasetIRI + datasetName;

		Resource resource = getModel().createResource(datasetName);
		Resource classResource = getModel().createResource("http://purl.org/linked-data/cube#DataSet");

		resource.addProperty(RDF.type, classResource);

		Property hierarchyProperty = getModel().createProperty("http://purl.org/linked-data/cube#structure");
		resource.addProperty(hierarchyProperty, getModel().createResource(assignIRI(datasetcube)));
	}

	public void addCube(String nameIRI, String name, String notation,
			LinkedHashMap<String, ArrayList<String>> measureMap, LinkedHashMap<String, String> dimensionMap) {
		// TODO Auto-generated method stub
		name = nameIRI + name;
		Resource resource = getModel().createResource(name);

		Resource classResource = getModel().createResource("http://purl.org/linked-data/cube#DataStructureDefinition");

		resource.addProperty(RDF.type, classResource);

		Property conformsToProperty = getModel().createProperty("http://purl.org/dc/terms/conformsTo");
		resource.addProperty(conformsToProperty, getModel().createResource("http://purl.org/qb4olap/cubes"));

		Property labelProperty = getModel().createProperty("http://www.w3.org/2004/02/skos/core#notation");
		Literal labelLiteral = getModel().createLiteral(notation);

		resource.addProperty(labelProperty, labelLiteral);

		Property componentProperty = getModel().createProperty("http://purl.org/linked-data/cube#component");

		if (measureMap.size() > 0) {
			for (Entry<String, ArrayList<String>> entry : measureMap.entrySet()) {
				Resource measureResource = getModel().createResource();

				resource.addProperty(componentProperty, measureResource);

				String key = entry.getKey();
				ArrayList<String> value = entry.getValue();

				Property property = getModel().createProperty("http://purl.org/linked-data/cube#measure");
				measureResource.addProperty(property, assignIRI(key));

				Property property2 = getModel().createProperty("http://purl.org/qb4olap/cubes#aggregateFunction");
				for (String string : value) {
					measureResource.addProperty(property2, assignIRI(string));
				}
			}
		}

		if (dimensionMap.size() > 0) {
			for (Entry<String, String> entry : dimensionMap.entrySet()) {
				Resource dimResource = getModel().createResource();

				String key = entry.getKey();
				String value = entry.getValue();

				Property property = getModel().createProperty("http://purl.org/qb4olap/cubes#dimension");
				dimResource.addProperty(property, assignIRI(key));

				Property property2 = getModel().createProperty("http://purl.org/qb4olap/cubes#cardinality");
				dimResource.addProperty(property2, assignIRI(value));

				resource.addProperty(componentProperty, dimResource);
			}
		}
	}

	public void addCuboid(String nameIRI, String name, String cube, String notation,
			LinkedHashMap<String, ArrayList<String>> measureMap, LinkedHashMap<String, String> dimensionMap) {
		// TODO Auto-generated method stub
		name = nameIRI + name;
		Resource resource = getModel().createResource(name);

		Resource classResource = getModel().createResource("http://purl.org/linked-data/cube#DataStructureDefinition");

		resource.addProperty(RDF.type, classResource);

		Property conformsToProperty = getModel().createProperty("http://purl.org/dc/terms/conformsTo");
		resource.addProperty(conformsToProperty, getModel().createResource("http://purl.org/qb4olap/cubes"));

		Property cubeProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#isCuboidOf");
		resource.addProperty(cubeProperty, getModel().createResource(assignIRI(cube)));

		Property labelProperty = getModel().createProperty("http://www.w3.org/2004/02/skos/core#notation");
		Literal labelLiteral = getModel().createLiteral(notation);

		resource.addProperty(labelProperty, labelLiteral);

		Property componentProperty = getModel().createProperty("http://purl.org/linked-data/cube#component");

		if (measureMap.size() > 0) {
			for (Entry<String, ArrayList<String>> entry : measureMap.entrySet()) {
				Resource measureResource = getModel().createResource();

				resource.addProperty(componentProperty, measureResource);

				String key = entry.getKey();
				ArrayList<String> value = entry.getValue();

				Property property = getModel().createProperty("http://purl.org/linked-data/cube#measure");
				measureResource.addProperty(property, assignIRI(key));

				Property property2 = getModel().createProperty("http://purl.org/qb4olap/cubes#aggregateFunction");
				for (String string : value) {
					measureResource.addProperty(property2, assignIRI(string));
				}
			}
		}

		if (dimensionMap.size() > 0) {
			for (Entry<String, String> entry : dimensionMap.entrySet()) {
				Resource dimResource = getModel().createResource();

				String key = entry.getKey();
				String value = entry.getValue();

				Property property = getModel().createProperty("http://purl.org/qb4olap/cubes#level");
				dimResource.addProperty(property, assignIRI(key));

				Property property2 = getModel().createProperty("http://purl.org/qb4olap/cubes#cardinality");
				dimResource.addProperty(property2, assignIRI(value));

				resource.addProperty(componentProperty, dimResource);
			}
		}
	}

	public LinkedHashMap<String, String> getHierarchyStepsList() {
		return hierarchyStepsList;
	}

	public void setHierarchyStepsList(LinkedHashMap<String, String> hierarchyStepsList) {
		this.hierarchyStepsList = hierarchyStepsList;
	}

	public void editProperty(String name, String key, String previousValue, String value, boolean isStep) {
		// TODO Auto-generated method stub
		Model model = tempModel;

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

	public void addProperty(String name, String key, String value, boolean b) {
		// TODO Auto-generated method stub
		Model model = tempModel;

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		if (value.contains("http://") || value.contains(":")) {
			value = assignIRI(value);
			resource.addProperty(property, model.createResource(value));
		} else {
			resource.addLiteral(property, value);
		}
	}

	public void removeProperty(String name, String key, String value, boolean b) {
		// TODO Auto-generated method stub
		Model model = tempModel;

		name = assignIRI(name);
		key = assignIRI(key);

		Resource resource = model.getResource(name);
		Property property = model.createProperty(key);

		RDFNode node = null;
		if (value.contains("http://") || value.contains(":")) {
			value = assignIRI(value);
			node = ResourceFactory.createResource(value);
		} else {
			node = ResourceFactory.createStringLiteral(value);
		}

		model.remove(resource, property, node);
	}

	public void addHierarchyStepResource(String stepHierName, String childLevelName, String parentLevelName,
			String cardinality, String rollUpProperty) {
		// TODO Auto-generated method stub

		Resource resource = getModel().createResource();
		Property hierProperty = getModel().createProperty(assignIRI("qb4o:inHierarchy"));
		Property childProperty = getModel().createProperty(assignIRI("qb4o:childLevel"));
		Property parentProperty = getModel().createProperty(assignIRI("qb4o:parentLevel"));
		Property cardinalityProperty = getModel().createProperty(assignIRI("qb4o:pcCardinality"));
		Property rollupProperty = getModel().createProperty(assignIRI("qb4o:rollup"));

		resource.addProperty(hierProperty, getModel().createResource(assignIRI(stepHierName)));
		resource.addProperty(childProperty, getModel().createResource(assignIRI(childLevelName)));
		resource.addProperty(parentProperty, getModel().createResource(assignIRI(parentLevelName)));
		resource.addProperty(cardinalityProperty, getModel().createResource(assignIRI(cardinality)));
		resource.addProperty(rollupProperty, getModel().createResource(assignIRI(rollUpProperty)));
		resource.addProperty(RDF.type, getModel().createResource(assignIRI("qb4o:HierarchyStep")));
	}

	public LinkedHashMap<String, ArrayList<String>> getCubeList() {
		return cubeList;
	}

	public void setCubeList(LinkedHashMap<String, ArrayList<String>> cubeList) {
		this.cubeList = cubeList;
	}

	public LinkedHashMap<String, ArrayList<String>> getCuboidList() {
		return cuboidList;
	}

	public void setCuboidList(LinkedHashMap<String, ArrayList<String>> cuboidList) {
		this.cuboidList = cuboidList;
	}

	public LinkedHashMap<String, String> getCubeNodeList() {
		return cubeNodeList;
	}

	public void setCubeNodeList(LinkedHashMap<String, String> cubeNodeList) {
		this.cubeNodeList = cubeNodeList;
	}

	public LinkedHashMap<String, String> getCuboidNodeList() {
		return cuboidNodeList;
	}

	public void setCuboidNodeList(LinkedHashMap<String, String> cuboidNodeList) {
		this.cuboidNodeList = cuboidNodeList;
	}

	public String printAllComponents() {
		String text = "";

		text += new FileMethods().getPrefixStrings(prefixMap);

		text += iterateAllItems(ontologyList, "ONTOLOGIES");
		text += iterateAllItems(classList, "CLASSES");
		text += iterateAllItems(objectList, "Object Properties".toUpperCase());
		text += iterateAllItems(dataList, "Data properties".toUpperCase());
		text += iterateAllItems(cubeList, cubeNodeList, "CUBES");
		text += iterateAllItems(cuboidList, cuboidNodeList, "CUBOIDS");
		text += iterateAllItems(datasetList, "DATASETS");
		text += iterateAllItems(dimensionList, "DIMENSIONS");
		text += iterateAllItems(hierarchyList, "HIERARCHIES");

		if (hierarchyStepsList.size() > 0) {
			text += "#HIERARCHY STEPS\n\n";

			for (Map.Entry<String, String> map : hierarchyStepsList.entrySet()) {

				text += getSubjectProperties(map.getKey(), map.getValue());
				text += "\n";
			}
			text += "\n";
		}

		text += iterateAllItems(levelList, "LEVELS");
		text += iterateAllItems(attributeList, "ATTRIBUTES");
		text += iterateAllItems(rollupList, "ROLLUP RELATIONSHIPS");
		text += iterateAllItems(measureList, "MEASURES");

		// new FileMethods().writeText("C:\\Users\\Amrit\\Documents\\1.ttl", text);

		return text;
	}

	private String iterateAllItems(LinkedHashMap<String, ArrayList<String>> cubeList,
			LinkedHashMap<String, String> cubeNodeList, String type) {
		// TODO Auto-generated method stub
		String text = "";
		if (cubeList.size() > 0) {
			text += "#" + type + "\n\n";
			for (Map.Entry<String, ArrayList<String>> map : cubeList.entrySet()) {
				String cubeName = map.getKey();
				ArrayList<String> arrayList = map.getValue();
				text += getSubjectProperties(cubeName, arrayList, cubeNodeList);
				text += "\n";
			}

			text += "\n";
		}
		return text;
	}

	private String iterateAllItems(ArrayList<String> classList, String type) {
		// TODO Auto-generated method stub

		String text = "";
		if (classList.size() > 0) {
			text += "#" + type + "\n\n";
			for (String string : classList) {
				text += getSubjectProperties(string);
				text += "\n";
			}

			text += "\n";
		}
		return text;
	}

	private String getSubjectProperties(String cubeName, ArrayList<String> blankNodes,
			LinkedHashMap<String, String> cubeNodeList) {
		// TODO Auto-generated method stub
		cubeName = assignIRI(cubeName);

		String sparql = "SELECT ?s ?p ?o WHERE {?s ?p ?o. FILTER regex(str(?s), '" + cubeName + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		String text = "";
		String typeString = "";
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String predicate = querySolution.get("p").toString();
			String object = querySolution.get("o").toString();

			if (subject.equals(cubeName)) {
				if (predicate.equals(assignIRI("qb:component"))) {
					continue;
				} else if (predicate.equals(RDF.type.toString())) {
					if (typeString.trim().length() == 0) {
						typeString += assignPrefix(object);
					} else {
						typeString += ", " + assignPrefix(object);
					}
				} else {
					object = assignPrefix(object).trim();
					predicate = assignPrefix(predicate).trim();
					if (linkedHashMap.containsKey(predicate)) {
						object = linkedHashMap.get(predicate) + ", " + object;
					}

					linkedHashMap.put(predicate, object);
				}
			}
		}

		text += assignPrefix(cubeName) + " a " + typeString;

		for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
			String key = map.getKey();
			String value = map.getValue();

			if (value.contains("http") || value.contains("www.")) {
				String valueString = ";\n\t" + key + " <" + value + ">";
				text += valueString;
			} else {
				if (value.contains(":")) {
					String valueString = ";\n\t" + key + " " + value;
					text += valueString;
				} else {
					if (value.contains("@")) {
						String[] parts = value.split("@");
						value = "\"" + parts[0] + "\"" + "@" + parts[1];

						String valueString = ";\n\t" + key + " " + value;
						text += valueString;
					}
				}
			}
		}

		for (String string : blankNodes) {
			String resource = cubeNodeList.get(string);

			text += getBlankNodesSubject(resource);
		}

		text += ".\n";

		return text;
	}

	private String getBlankNodesSubject(String resource) {
		// TODO Auto-generated method stub
		resource = assignIRI(resource);

		String sparql = "SELECT ?s ?p ?o WHERE {?s ?p ?o. FILTER regex(str(?s), '" + resource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, tempModel);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		String text = "";
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String predicate = querySolution.get("p").toString();
			String object = querySolution.get("o").toString();

			if (subject.equals(resource)) {
				predicate = assignPrefix(predicate).trim();
				object = assignPrefix(object).trim();

				if (linkedHashMap.containsKey(predicate)) {
					object = linkedHashMap.get(predicate) + ", " + object;
				}

				linkedHashMap.put(predicate, object);
			}
		}

		if (linkedHashMap.size() > 0) {
			text += ";\n\tqb:component [ ";

			ArrayList<String> compStrings = new ArrayList<>();
			for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
				String key = map.getKey();
				String value = map.getValue();

				if (value.contains("http") || value.contains("www.")) {
					String valueString = key + " <" + value + ">";

					if (!compStrings.contains(valueString)) {
						compStrings.add(valueString);
					}
				} else {
					if (value.contains(":")) {
						String valueString = key + " " + value;

						if (!compStrings.contains(valueString)) {
							compStrings.add(valueString);
						}
					} else {
						if (value.contains("@")) {
							String[] parts = value.split("@");
							value = "\"" + parts[0] + "\"" + "@" + parts[1];

							String valueString = key + " " + value;

							if (!compStrings.contains(valueString)) {
								compStrings.add(valueString);
							}
						}
					}
				}
			}

			for (int i = 0; i < compStrings.size(); i++) {
				text += compStrings.get(i);

				if (i == compStrings.size() - 1) {
					text += "]";
				} else {
					text += "; ";
				}
			}
		}
		return text;
	}

	private String getSubjectProperties(String mainResource, String selectedResource) {
		// TODO Auto-generated method stub
		selectedResource = assignIRI(selectedResource);

		String sparql = "SELECT ?s ?p ?o WHERE {?s ?p ?o. FILTER regex(str(?s), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, tempModel);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		String text = "";
		String typeString = "";
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

		/*
		 * System.out.println("\n\n\n\n"); new FileMethods().printModel(tempModel);
		 */

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String predicate = querySolution.get("p").toString();
			String object = querySolution.get("o").toString();

			if (subject.equals(selectedResource)) {
				if (predicate.equals(RDF.type.toString())) {
					if (typeString.trim().length() == 0) {
						typeString += assignPrefix(object);
					} else {
						typeString += ", " + assignPrefix(object);
					}
				} else {
					object = assignPrefix(object).trim();
					predicate = assignPrefix(predicate).trim();
					if (linkedHashMap.containsKey(predicate)) {
						object = linkedHashMap.get(predicate) + ", " + object;
					}

					linkedHashMap.put(predicate, object);
				}
			}
		}

		text += mainResource + " a " + typeString;

		for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
			String key = map.getKey();
			String value = map.getValue();

			if (value.contains("http") || value.contains("www.")) {
				String valueString = ";\n\t" + key + " <" + value + ">";
				text += valueString;
			} else {
				if (value.contains(":")) {
					String valueString = ";\n\t" + key + " " + value;
					text += valueString;
				} else {
					if (value.contains("@")) {
						String[] parts = value.split("@");
						value = "\"" + parts[0] + "\"" + "@" + parts[1];

						String valueString = ";\n\t" + key + " " + value;
						text += valueString;
					}
				}
			}
		}

		text += ".\n";

		return text;
	}

	private String getSubjectProperties(String selectedResource) {
		// TODO Auto-generated method stub
		selectedResource = assignIRI(selectedResource);

		String sparql = "SELECT ?s ?p ?o WHERE {?s ?p ?o. FILTER regex(str(?s), '" + selectedResource + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// new FileMethods().printResultSet(resultSet);

		String text = "";
		String typeString = "";
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();
			String predicate = querySolution.get("p").toString();
			String object = querySolution.get("o").toString();

			if (subject.equals(selectedResource)) {
				if (predicate.equals(RDF.type.toString())) {
					if (typeString.trim().length() == 0) {
						typeString += assignPrefix(object);
					} else {
						typeString += ", " + assignPrefix(object);
					}

				} else {
					object = assignPrefix(object).trim();
					predicate = assignPrefix(predicate).trim();
					if (linkedHashMap.containsKey(predicate)) {
						object = linkedHashMap.get(predicate) + ", " + object;
					}

					linkedHashMap.put(predicate, object);
				}
			}
		}
		
		String subjectPrefix = assignPrefix(selectedResource);
		if (Methods.containsWWW(subjectPrefix)) {
			text += "<" + subjectPrefix + ">" + " a " + typeString;
		} else {
			text += subjectPrefix + " a " + typeString;
		}

		for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
			String key = map.getKey();
			String valueText = map.getValue();
			
			if (valueText.contains("@")) {
				String[] parts = valueText.split("@");
				valueText = "\"" + parts[0] + "\"" + "@" + parts[1];

				String valueString = ";\n\t" + key + " " + valueText;
				text += valueString;
			} else {
				String value = Methods.getSplitValues(valueText);
				
				String valueString = ";\n\t" + key + " " + value;
				text += valueString;
			}

//			if (Methods.isIRI(value)) {
//				String valueString = ";\n\t" + key + " <" + value + ">";
//				text += valueString;
//			} else {
//				if (value.contains(":")) {
//					String valueString = ";\n\t" + key + " " + value;
//					text += valueString;
//				} else {
//					if (value.contains("@")) {
//						String[] parts = value.split("@");
//						value = "\"" + parts[0] + "\"" + "@" + parts[1];
//
//						String valueString = ";\n\t" + key + " " + value;
//						text += valueString;
//					}
//				}
//			}
		}

		text += ".\n";

		return text;
	}

	public void addOntology(String iri, String name, String namespaceIRI, String namespaceURI, String label,
			String created, String modified, String title, String comment, String versionInfo, String imports) {
		// TODO Auto-generated method stub

		name = iri + name;
		Resource resource = getModel().createResource(assignIRI(name));
		Resource classResource = getModel().createResource("http://www.w3.org/2002/07/owl#Ontology");
		resource.addProperty(RDF.type, classResource);

		addPropertyToResource(getModel(), resource, "http://purl.org/vocab/vann/preferredNamespacePrefix",
				namespaceIRI);
		addPropertyToResource(getModel(), resource, "http://purl.org/vocab/vann/preferredNamespaceUri", namespaceURI);
		addPropertyToResource(getModel(), resource, "http://www.w3.org/2000/01/rdf-schema#label", label);
		addPropertyToResource(getModel(), resource, "http://purl.org/dc/terms/created", created);
		addPropertyToResource(getModel(), resource, "http://purl.org/dc/terms/modified", modified);
		addPropertyToResource(getModel(), resource, "http://purl.org/dc/terms/title", title);
		addPropertyToResource(getModel(), resource, "http://www.w3.org/2000/01/rdf-schema#comment", comment);
		addPropertyToResource(getModel(), resource, "http://www.w3.org/2002/07/owl#versionInfo", versionInfo);
		addPropertyToResource(getModel(), resource, "http://www.w3.org/2002/07/owl#imports", imports);
	}

	private void addPropertyToResource(Model model, Resource resource, String propertyString, String value) {
		// TODO Auto-generated method stub

		if (value.trim().length() > 0) {
			Property property = model.createProperty(propertyString);

			if (value.contains("http://") || value.contains(":")) {
				value = assignIRI(value);
				resource.addProperty(property, model.createResource(value));
			} else {
				resource.addLiteral(property, value);
			}
		}
	}

	public void addQbComponent(String resourceString, String measureString, ArrayList<String> functions) {
		// TODO Auto-generated method stub
		Resource resource = getModel().createResource(assignIRI(resourceString));

		Property property = getModel().createProperty("http://purl.org/linked-data/cube#component");

		Resource resourceSecond = getModel().createResource();

		Property dimProperty = getModel().createProperty("http://purl.org/linked-data/cube#measure");
		Property carProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#aggregateFunction");

		resourceSecond.addProperty(dimProperty, model.createResource(assignIRI(measureString)));

		for (String string : functions) {
			resourceSecond.addProperty(carProperty, model.createResource(assignIRI(string)));
		}

		resource.addProperty(property, resourceSecond);
	}

	public void addQbComponent(String resourceString, String dimensionString, String dimCar, String type) {
		// TODO Auto-generated method stub
		Resource resource = getModel().createResource(assignIRI(resourceString));

		Property property = getModel().createProperty("http://purl.org/linked-data/cube#component");

		Resource resourceSecond = getModel().createResource();

		if (type.equals("Dim")) {
			Property dimProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#dimension");
			Property carProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#cardinality");

			resourceSecond.addProperty(dimProperty, model.createResource(assignIRI(dimensionString)));
			resourceSecond.addProperty(carProperty, model.createResource(assignPrefix(dimCar)));
		} else {
			Property dimProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#level");
			Property carProperty = getModel().createProperty("http://purl.org/qb4olap/cubes#cardinality");

			resourceSecond.addProperty(dimProperty, model.createResource(assignIRI(dimensionString)));
			resourceSecond.addProperty(carProperty, model.createResource(assignPrefix(dimCar)));
		}

		resource.addProperty(property, resourceSecond);
	}

	public void addHierarchyResource(String iriString, String hierName, String hierLabel, String dimension,
			List<String> hierLevelNames) {
		// TODO Auto-generated method stub

		hierName = iriString + hierName;
		Resource resource = getModel().createResource(assignIRI(hierName));
		Resource classResource = getModel().createResource("http://purl.org/qb4olap/cubes#Hierarchy");
		resource.addProperty(RDF.type, classResource);
		
		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(hierLabel, "en");
		resource.addProperty(labelProperty, labelLiteral);
		
		addPropertyToResource(getModel(), resource, "http://purl.org/qb4olap/cubes#inDimension", dimension);
		
		for (String levelString : hierLevelNames) {
			addPropertyToResource(getModel(), resource, "http://purl.org/qb4olap/cubes#hasLevel", levelString);
		}
		
		// new FileMethods().printModel(model);
	}

	public void addConceptResource(String leveliri, String levelName, String levelLabel, String lang) {
		// TODO Auto-generated method stub
		
		levelName = leveliri + levelName;
		Resource resource = getModel().createResource(assignIRI(levelName));
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class");
		resource.addProperty(RDF.type, classResource);
		
		Property labelProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		Literal labelLiteral = getModel().createLiteral(levelLabel, "en");
		resource.addProperty(labelProperty, labelLiteral);
	}

	public void addObjectPropertyResource(String iri, String name, List<String> domains, String range) {
		// TODO Auto-generated method stub
		name = iri + name;
		Resource resource = getModel().createResource(assignIRI(name));
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#ObjectProperty");
		resource.addProperty(RDF.type, classResource);
		
		Property rangeProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
		Resource rangeResource = getModel().createResource(assignIRI(range));
		resource.addProperty(rangeProperty, rangeResource);
		
		Property domainProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#domain");
		for (String domain : domains) {
			Resource domainResource = getModel().createResource(assignIRI(domain));
			resource.addProperty(domainProperty, domainResource);
		}
	}

	public void addODatatypePropertyResource(String iri, String name, List<String> domains, String range) {
		// TODO Auto-generated method stub
		name = iri + name;
		Resource resource = getModel().createResource(assignIRI(name));
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
		resource.addProperty(RDF.type, classResource);
		
		Property rangeProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#range");
		Resource rangeResource = getModel().createResource(assignIRI(range));
		resource.addProperty(rangeProperty, rangeResource);
		
		Property domainProperty = getModel().createProperty("http://www.w3.org/2000/01/rdf-schema#domain");
		for (String domain : domains) {
			Resource domainResource = getModel().createResource(assignIRI(domain));
			resource.addProperty(domainProperty, domainResource);
		}
	}
}