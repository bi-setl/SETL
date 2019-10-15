package queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.ParameterizedSparqlString;
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
import org.apache.jena.rdf.model.RDFNode;

import helper.Methods;
import model.SelectedLevel;

public class Extraction {
	private Methods methods;
	private int totalUniqueGraph = 0;
	private LinkedHashMap<String, ArrayList<String>> dimHierMap;
	private LinkedHashMap<String, ArrayList<String>> measureMap;
	private LinkedHashMap<String, ArrayList<String>> hierLevelMap;
	private Model model;
	private LinkedHashMap<String, String> prefixMap;
	private ArrayList<String> allCubeLevels;

	public Extraction() {
		// TODO Auto-generated constructor stub
		methods = new Methods();
		prefixMap = new LinkedHashMap<>();
	}

	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> extractEndPointDatasets(String endPoint) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap = new LinkedHashMap<>();
		if (endPoint == null) {
			String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE { ?s a qb:DataSet; ?p ?o.\r\n"
					+ "?s qb:structure ?x.\r\n}";
			
			ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String datasetString = querySolution.get("?s").toString();
				String cube = String.valueOf(querySolution.get("x"));
				
				LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
				if (linkedHashMap.containsKey(datasetString)) {
					hashMap = linkedHashMap.get(datasetString);
				}
				
				String numobs = getObservation(datasetString);
				hashMap = methods.addToComplexHashMap(hashMap, "numobs", numobs);
				hashMap = methods.addToComplexHashMap(hashMap, "cubeuriString", assignPrefix(cube));
				linkedHashMap.put(assignPrefix(datasetString), hashMap);
			}
		} else {
			String sparql = "PREFIX qb: <http://purl.org/linked-data/cube#>"
					+ "PREFIX dct: <http://purl.org/dc/terms/>"
					+ "select distinct ?dataset ?schemagraph ?cubeuri ?cname ?instancegraph ?numobs ?version where {"
					+ "GRAPH ?schemagraph {"
					+ "?cubeuri a qb:DataStructureDefinition." + "?dataset qb:structure ?cubeuri."
					+ "?dataset dct:title ?cname." + "?cubeuri dct:conformsTo ?version."
					+ "{ SELECT distinct ?instancegraph ?dataset (count(?o) AS ?numobs) "
					+ "WHERE { GRAPH ?instancegraph { ?o qb:dataSet ?dataset. } } "
					+ "GROUP BY ?instancegraph ?dataset }"
					+ "}.}";

			ResultSet resultSet = getEndPointResultSet(endPoint, sparql);

			if (resultSet == null) {
				sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
						+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE { ?dataset a qb:DataSet."
						+ "?dataset qb:structure ?cubeuri.}";
				
				resultSet = getEndPointResultSet(endPoint, sparql);
			}
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				RDFNode datasetStringNode = querySolution.get("?dataset");
				String datasetString = methods.getRDFNodeValue(datasetStringNode).toString();
				
				LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
				if (linkedHashMap.containsKey(datasetString)) {
					hashMap = linkedHashMap.get(datasetString);
				}
				
				if (querySolution.get("?schemagraph") != null) {
					RDFNode schemagraphStringNode = querySolution.get("?schemagraph");
					String schemagraphString = methods.getRDFNodeValue(schemagraphStringNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "schemagraphString", schemagraphString);
				}
				
				if (querySolution.get("?cubeuri") != null) {
					RDFNode cubeuriStringNode = querySolution.get("?cubeuri");
					String cubeuriString = methods.getRDFNodeValue(cubeuriStringNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "cubeuriString", cubeuriString);
				}
				
				if (querySolution.get("?cname") != null) {
					RDFNode cnameStringNode = querySolution.get("?cname");
					String cnameString = methods.getRDFNodeValue(cnameStringNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "cnameString", cnameString);
				}
				
				if (querySolution.get("?instancegraph") != null) {
					RDFNode instancegraphStringNode = querySolution.get("?instancegraph");
					String instancegraphString = methods.getRDFNodeValue(instancegraphStringNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "instancegraphString", instancegraphString);
				}
				
				if (querySolution.get("?numobs") != null) {
					RDFNode numobsNode = querySolution.get("?numobs");
					String numobs = methods.getRDFNodeValue(numobsNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "numobs", numobs);
				}
				
				if (querySolution.get("?version") != null) {
					RDFNode versionNode = querySolution.get("?version");
					String version = methods.getRDFNodeValue(versionNode).toString();
					hashMap = methods.addToComplexHashMap(hashMap, "version", version);
				}
				
				linkedHashMap.put(datasetString, hashMap);
			}
		}

		return linkedHashMap;
	}
	
	public String getObservation(String datasetString) {
		// TODO Auto-generated method stub
		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT (count(?o) AS ?numobs) WHERE { ?o a qb:Observation.}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			RDFNode value = querySolution.get("?numobs");
			
			if (value.isLiteral()) {
				return value.asLiteral().getValue().toString();
			}
		}
		return null;
	}

	private ResultSet getEndPointResultSet(String endPoint, String sparql) {
		if (endPoint == null) {
			try {
			
				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, model);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
				return resultSet;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				// TODO Auto-generated method stub
				ParameterizedSparqlString qs = new ParameterizedSparqlString(sparql);
				QueryExecution exec = QueryExecutionFactory.sparqlService(endPoint, qs.asQuery());
				return ResultSetFactory.copyResults(exec.execSelect());
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				return null;
			}
		}
	}

	public void extractOlapDatasetCube(String endPoint, String dataset, List<String> selectedGraphs,
			List<String> selectedInstances) {
		// TODO Auto-generated method stub
		if (dataset != null) {
			dataset = assignIRI(dataset);
		}
		
		
		ArrayList<String> levels = new ArrayList<>();
		ArrayList<String> dimensions = new ArrayList<>();
		measureMap = new LinkedHashMap<>();
		
		for (String graphString : selectedGraphs) {
			if (endPoint != null) {
				
				String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
						+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * "
						+ "FROM <" + graphString + "> "
						+ "WHERE { ?cube a qb:DataStructureDefinition. "
						+ "?cube qb:component ?node. "
						+ "OPTIONAL {?node qb4o:level ?level.}."
						+ "OPTIONAL {?node qb4o:dimension ?dimension.}."
						+ "OPTIONAL {?node qb:measure ?measure.}."
						+ "OPTIONAL {?node qb4o:aggregateFunction ?function.}."
						+ "}";
				
				ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
				
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					
					if (querySolution.get("?level") != null) {
						String levelString = querySolution.get("?level").toString();
						
						if (methods.checkString(levelString) && !levels.contains(levelString)) {
							levels.add(levelString);
						}
					}
					
					if (querySolution.get("?dimension") != null) {
						String dimString = querySolution.get("?dimension").toString();

						if (methods.checkString(dimString) && !dimensions.contains(dimString)) {
							dimensions.add(dimString);
						}
					}
					
					if (querySolution.get("?measure") != null && querySolution.get("?function") != null) {
						String measureString = querySolution.get("?measure").toString();
						String functionString = querySolution.get("?function").toString();
						
						measureMap = methods.addToComplexHashMap(measureMap, measureString, functionString);
					}
				}
			} else {
				String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
						+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
						+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
						+ "SELECT * WHERE { <" + dataset + "> a qb:DataSet.\n"
						+ "<" + dataset + "> qb:structure ?cube.\n"
						+ "?cube a qb:DataStructureDefinition.\n"
						+ "?cube ?pred ?obj.\n"
						+ "}";
				
				Query query = QueryFactory.create(sparql);
				QueryExecution execution = QueryExecutionFactory.create(query, model);
				ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
				
				// methods.printResultSet(resultSet);
				
				String cubeName = "";
				boolean isCuboid = false;
				
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					cubeName = querySolution.get("?cube").toString();
					String pred = querySolution.get("?pred").toString();
					
					if (pred.contains("isCuboidOf")) {
						isCuboid = true;
						break;
					}
				}
				
				if (isCuboid) {
					levels = getCuboidLevels(cubeName);
				} else {
					dimensions = getCubeDimension(cubeName);
				}
			}
		}
		
		allCubeLevels = new ArrayList<>();
		for (String string : levels) {
			allCubeLevels.add(assignPrefix(string));
		}
		
		
		if (dimensions.size() > 0) {
			setDimHierMap(fetchCubeHierarchies(endPoint, dimensions, selectedGraphs));
		} else {
			if (levels.size() > 0) {
				setDimHierMap(fetchCubeDimensionFromLevels(endPoint, levels, selectedGraphs));
			}
		}
		
		LinkedHashMap<String, ArrayList<String>> hierLevelMap = new LinkedHashMap<>();
		for (String dimension : getDimHierMap().keySet()) {
			ArrayList<String> hierarchies = getDimHierMap().get(dimension);
			
			for (String hierarchyString : hierarchies) {
				ArrayList<String> hierLevels = fetchHierarchyStepLevels(endPoint, hierarchyString, selectedGraphs);
				
				if (hierLevels.size() == 0) {
					hierLevels = fetchHierarchyLevels(endPoint, hierarchyString, selectedGraphs);
				}
				
				hierLevelMap.put(hierarchyString, hierLevels);
			}
		}
		
		setHierLevelMap(hierLevelMap);
	}

	private ArrayList<String> getCubeDimension(String cubeName) {
		// TODO Auto-generated method stub
		ArrayList<String> dims = new ArrayList<String>();
		
		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?o WHERE { <" + cubeName + "> a qb:DataStructureDefinition.\n"
				+ "<" + cubeName + "> qb:component ?s.\n"
				+ "?s qb4o:dimension ?o.\n"
				+ "}";
		
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		// methods.printResultSet(resultSet);
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String object = querySolution.get("?o").toString();
			
			dims.add(object);
		}
		
		setCubeMeasures(cubeName);
		
		return dims;
	}

	private ArrayList<String> getCuboidLevels(String cuboidName) {
		// TODO Auto-generated method stub
		ArrayList<String> levels = new ArrayList<String>();
		
		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?o WHERE { <" + cuboidName + "> a qb:DataStructureDefinition.\n"
				+ "<" + cuboidName + "> qb:component ?s.\n"
				+ "?s qb4o:level ?o.\n"
				+ "}";
		
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		// methods.printResultSet(resultSet);
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String object = querySolution.get("?o").toString();
			
			levels.add(object);
		}
		
		setCubeMeasures(cuboidName);
		
		return levels;
	}

	private void setCubeMeasures(String cuboidName) {
		// TODO Auto-generated method stub
		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE { <" + cuboidName + "> a qb:DataStructureDefinition.\n"
				+ "<" + cuboidName + "> qb:component ?s.\n"
				+ "?s qb:measure ?o.\n"
				+ "?s qb4o:aggregateFunction ?f.\n"
				+ "}";
		
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String measure = querySolution.get("?o").toString();
			String aggregateFunction = querySolution.get("?f").toString();
		
			measureMap = methods.addToComplexHashMap(measureMap, assignPrefix(measure), assignPrefix(aggregateFunction));
		}
	}

	private LinkedHashMap<String, ArrayList<String>> fetchCubeHierarchies(String endPoint, ArrayList<String> dimensions,
			List<String> selectedGraphs) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, ArrayList<String>> dimHierMap = new LinkedHashMap<>();
		
		
		
		for (String dimension : dimensions) {
			dimension = assignIRI(dimension);
			String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT ?x ";
			
			String sparqlTwo = "WHERE { <" + dimension + "> a qb:DimensionProperty."
					+ "<" + dimension + "> qb4o:hasHierarchy ?x.}";
			
			for (String graphString : selectedGraphs) {
				if (graphString.length() > 0) {
					graphString = "FROM <" + graphString + "> ";
				}
				
				String sparql = sparqlOne + graphString + sparqlTwo;
				
				ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
				
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					
					String hierarchyString = querySolution.get("?x").toString();
					
					dimHierMap = methods.addToComplexHashMap(dimHierMap, assignPrefix(dimension), assignPrefix(hierarchyString));
				}
			}
		}
		
		return dimHierMap;
	}

	private ArrayList<String> fetchHierarchyLevels(String endPoint, String hierarchyString,
			List<String> selectedGraphs) {
		// TODO Auto-generated method stub
		hierarchyString = assignIRI(hierarchyString);
		ArrayList<String> levels = new ArrayList<>();
		
		String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * ";
		
		String sparqlTwo = "WHERE {"
				+ "?hier a qb4o:Hierarchy."
				+ "?hier qb4o:hasLevel ?level."
				+ "}";
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			String sparql = sparqlOne + graphString + sparqlTwo;
			
			ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String levelString = querySolution.get("?level").toString();
				String hierString = querySolution.get("?hier").toString();
				
				if (hierString.equals(hierarchyString)) {
					if (!levels.contains(assignPrefix(levelString))) {
						levels.add(assignPrefix(levelString));
					}
				}
			}
		}
		return levels;
	}

	private ArrayList<String> fetchHierarchyStepLevels(String endPoint, String hierarchyString, List<String> selectedGraphs) {
		// TODO Auto-generated method stub
		hierarchyString = assignIRI(hierarchyString);
		LinkedHashMap<String, String> parentChildMap = new LinkedHashMap<>();
		ArrayList<String> parentList = new ArrayList<>();
		
		String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * ";
		
		String sparqlTwo = "WHERE {"
				+ "?step a qb4o:HierarchyStep. \n"
				+ "?step qb4o:inHierarchy <" + hierarchyString + ">. \n"
				+ "?step qb4o:parentLevel ?parent. \n"
				+ "?step qb4o:childLevel ?child. \n"
				+ "}";
		
		
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			String sparql = sparqlOne + graphString + sparqlTwo;
			
			ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String parent = querySolution.get("?parent").toString();
				String child = querySolution.get("?child").toString();
				
				parentChildMap.put(assignPrefix(parent), assignPrefix(child));
				
				if (!parentList.contains(assignPrefix(parent))) {
					parentList.add(assignPrefix(parent));
				}
			}
		}
		
		String startNode = "";
		for (String parent : parentList) {
			if (!parentChildMap.containsValue(parent)) {
				startNode = parent;
				break;
			}
		}
		
		int limit = parentChildMap.size();
		
		ArrayList<String> hierLevels = new ArrayList<>();
		if (startNode.length() > 0) {
			hierLevels.add(startNode);
		}
		for (int i = 0; i < limit; i++) {
			String childLevel = parentChildMap.get(startNode);
			if (methods.checkString(childLevel)) {
				if (!hierLevels.contains(childLevel)) {
					hierLevels.add(childLevel);
				}
			}
			startNode = childLevel;
		}
		
		return hierLevels;
	}

	private LinkedHashMap<String, ArrayList<String>> fetchCubeDimensionFromLevels(String endPoint, ArrayList<String> levels, List<String> selectedGraphs) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, ArrayList<String>> dimHierMap = new LinkedHashMap<>();
		
		for (String levelString : levels) {
			levelString = assignIRI(levelString);
			String sparqlOne = "PREFIX qb: <http://purl.org/linked-data/cube#>"
					+ "PREFIX dct: <http://purl.org/dc/terms/>"
					+ "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>"
					+ "SELECT * ";
			
			String sparqlTwo = "WHERE {"
					+ "?step a qb4o:HierarchyStep."
					+ "?step qb4o:parentLevel ?parent."
					+ "?step qb4o:childLevel ?child."
					+ "?step qb4o:inHierarchy ?hierarchy."
					+ "?hierarchy a qb4o:Hierarchy."
					+ "?hierarchy qb4o:inDimension ?dim."
					+ "}";
			
			
			
			for (String graph : selectedGraphs) {
				if (graph.length() > 0) {
					graph = "FROM <" + graph + "> ";
				}
				
				String sparql = sparqlOne + graph + sparqlTwo;
				
				ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
				
				int count = 0;
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					String parentString = querySolution.get("?parent").toString();
					String childString = querySolution.get("?child").toString();
					
					if (parentString.equals(levelString) || childString.equals(levelString)) {
						count++;
						String dimensionString = querySolution.get("?dim").toString();
						String hierarchyString = querySolution.get("?hierarchy").toString();
						
						dimHierMap = methods.addToComplexHashMap(dimHierMap, assignPrefix(dimensionString), assignPrefix(hierarchyString));
					}
				}
				
				if (count == 0) {
					String sparqlOne2 = "PREFIX qb: <http://purl.org/linked-data/cube#>"
							+ "PREFIX dct: <http://purl.org/dc/terms/>"
							+ "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>"
							+ "SELECT * ";
					
					String sparqlTwo2 = "WHERE {"
							+ "?hierarchy a qb4o:Hierarchy."
							+ "?hierarchy qb4o:hasLevel ?level."
							+ "?hierarchy qb4o:inDimension ?dim."
							+ "}";
					
					String sparql2 = sparqlOne2 + graph + sparqlTwo2;
					
					ResultSet resultSet2 = null;
					if (endPoint == null) {
						Query query = QueryFactory.create(sparql2);
						QueryExecution execution = QueryExecutionFactory.create(query, model);
						resultSet2 = ResultSetFactory.copyResults(execution.execSelect());
					} else {
						ParameterizedSparqlString qs = new ParameterizedSparqlString(sparql2);
						QueryExecution exec = QueryExecutionFactory.sparqlService(endPoint, qs.asQuery());
						resultSet2 = ResultSetFactory.copyResults(exec.execSelect());
					}
					
					while (resultSet2.hasNext()) {
						QuerySolution querySolution = (QuerySolution) resultSet2.next();
						
						String dimensionString = querySolution.get("?dim").toString();
						String hierarchyString = querySolution.get("?hierarchy").toString();
						String parentString = querySolution.get("?level").toString();
						
						if (parentString.equals(levelString)) {
							dimHierMap = methods.addToComplexHashMap(dimHierMap, assignPrefix(dimensionString), assignPrefix(hierarchyString));
						}
					}
				}
			}
		}
		return dimHierMap;
	}

	public int getTotalUniqueGraph(String endPoint) {
		String sparql = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX dct: <http://purl.org/dc/terms/>"
				+ "select (COUNT(DISTINCT ?schemagraph) as ?count)  where {"
				+ "GRAPH ?schemagraph {?s ?p ?o.}.}";

		ResultSet resultSet = getEndPointResultSet(endPoint, sparql);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			RDFNode noOfGraphs = querySolution.get("?count");
			totalUniqueGraph = (int) methods.getRDFNodeValue(noOfGraphs);
		}

		return totalUniqueGraph;
	}

	public ArrayList<String> extractEndPointLevelProperties(String endpointString, List<String> selectedGraphs, String selectedLevelName) {
		// TODO Auto-generated method stub
		ArrayList<String> properties = new ArrayList<>();
		
		
		selectedLevelName = assignIRI(selectedLevelName);
		
		String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * ";
		
		String sparqlTwo = "WHERE {"
				+ "<" + selectedLevelName + "> a qb4o:LevelProperty.\n"
				+ "<" + selectedLevelName + "> qb4o:hasAttribute ?property.\n"
				+ "}";
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			String sparql = sparqlOne + graphString + sparqlTwo;
			ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String propertyString = querySolution.get("?property").toString();
				
				if (!properties.contains(assignPrefix(propertyString))) {
					properties.add(assignPrefix(propertyString));
				}
			}
		}
		return properties;
	}

	public ArrayList<Object> extractOlapLevelInstances(String endpointString, String selectedLevel,
			String selectedProperty, List<String> selectedGraphs, List<String> selectedInstances) {
		// TODO Auto-generated method stub
		selectedLevel = assignIRI(selectedLevel);
		selectedProperty = assignIRI(selectedProperty);
		
		ArrayList<Object> levelInstanceStrings = new ArrayList<>();
		
		String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?value ";
		
		String sparqlTwo = "WHERE {\n"
				+ "?sub qb4o:memberOf <"+ selectedLevel +">.\n"
				+ "?sub <"+ selectedProperty +"> ?value.\n"
				+ "} GROUP BY ?value \n"
				+ "ORDER BY ?value \n";
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			for (String instanceGraph : selectedInstances) {
				if (instanceGraph.length() > 0) {
					instanceGraph = "FROM <" + instanceGraph + "> ";
				}
				
				
				/*
				 * String demoSparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n" +
				 * "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n" +
				 * "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n" +
				 * "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT * \n" +
				 * "WHERE {\n" + "?sub qb4o:memberOf <"+ selectedLevel +">.\n" + "}";
				 * 
				 * System.out.println(demoSparql); ResultSet resultSet2 =
				 * getEndPointResultSet(endpointString, demoSparql);
				 * methods.printResultSet(resultSet2);
				 */
				 
				
				String sparql = sparqlOne + graphString + instanceGraph + sparqlTwo;
				ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
				// methods.printResultSet(resultSet);
				
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					RDFNode valueNode = querySolution.get("?value");
					levelInstanceStrings.add(methods.getRDFNodeValue(valueNode));
				}
			}
		}
		return levelInstanceStrings;
	}

	public String getOlapMeasureRange(String endpointString, List<String> selectedGraphs, String measureName) {
		// TODO Auto-generated method stub
		
		measureName = assignIRI(measureName);
		
		String sparqlOne = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT DISTINCT ?range ";
		
		String sparqlTwo = "WHERE {"
				+ "<" + measureName + "> a qb:MeasureProperty."
				+ "<" + measureName + "> rdfs:range ?range."
				+ "}";
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			String sparql = sparqlOne + graphString + sparqlTwo;
			ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String rangeString = querySolution.get("?range").toString();
				return rangeString;
			}
		}
		return null;
	}

	public LinkedHashMap<String, String> getHierarchyStepWithChild(String endpointString, List<String> selectedGraphs, String levelString, String selectedHierarchyString) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		
		selectedHierarchyString = assignIRI(selectedHierarchyString);
		
		
		
		String sparqlOne = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX dct: <http://purl.org/dc/terms/>"
				+ "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>"
				+ "SELECT * ";
		
		String sparqlTwo = "WHERE {"
				+ "?step a qb4o:HierarchyStep."
				+ "?step qb4o:parentLevel ?parent."
				+ "?step qb4o:childLevel <" + levelString + ">."
				+ "?step qb4o:inHierarchy <" + selectedHierarchyString + ">."
				+ "?step qb4o:rollup ?rollup."
				+ "}";
		
		for (String graph : selectedGraphs) {
			if (graph.length() > 0) {
				graph = "FROM <" + graph + "> ";
			}
			
			String sparql = sparqlOne + graph + sparqlTwo;
			ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				
				String parentString = querySolution.get("?parent").toString();
				String rollupString = querySolution.get("?rollup").toString();
				
				hashMap.put("parent", parentString);
				hashMap.put("rollup", rollupString);
				
				return hashMap;
			}
		}
		return hashMap;
	}

	public LinkedHashMap<String, String> getHierarchyStepWithParent(String endpointString, List<String> selectedGraphs,
			String levelString, String selectedHierarchyString) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		selectedHierarchyString = assignIRI(selectedHierarchyString);
		
		String sparqlOne = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX dct: <http://purl.org/dc/terms/>"
				+ "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>"
				+ "SELECT * ";
		
		String sparqlTwo = "WHERE {"
				+ "?step a qb4o:HierarchyStep."
				+ "?step qb4o:parentLevel <" + levelString + ">."
				+ "?step qb4o:childLevel ?child."
				+ "?step qb4o:inHierarchy <" + selectedHierarchyString + ">."
				+ "?step qb4o:rollup ?rollup."
				+ "}";
		
		for (String graphString : selectedGraphs) {
			if (graphString.length() > 0) {
				graphString = "FROM <" + graphString + "> ";
			}
			
			String sparql = sparqlOne + graphString + sparqlTwo;
			
			ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
			
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				
				String childString = querySolution.get("?child").toString();
				String rollupString = querySolution.get("?rollup").toString();
				
				hashMap.put("child", childString);
				hashMap.put("rollup", rollupString);
				
				return hashMap;
			}
		}
		return hashMap;
	}

	public Object[][] runSparqlQuery(String endpointString, String sparql, ArrayList<String> selectedColumns) {
		// TODO Auto-generated method stub
		ArrayList<Object> valueList = new ArrayList<>();
		
		ResultSet resultSet = getEndPointResultSet(endpointString, sparql);
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			
			ArrayList<Object> arrayList = new ArrayList<>();
			for (String column : selectedColumns) {
				RDFNode value = querySolution.get(column);
				arrayList.add(methods.getRDFNodeValue(value));
			}
			
			valueList.add(arrayList.toArray());
		}
		
		Object[][] arrayResult = valueList.toArray(new Object[valueList.size()][selectedColumns.size()]);
		
		return arrayResult;
	}

	public boolean readModel(String filePath, String type) {
		// TODO Auto-generated method stub
		
		try {
			Model newModel = ModelFactory.createDefaultModel().read(filePath);
			
			model.add(newModel);
			if (type.equals("tbox")) {
				setPrefixMap(extractAllPrefixes(filePath));
			}
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
		
		return false;
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

	public LinkedHashMap<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(LinkedHashMap<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public void initializeModel() {
		// TODO Auto-generated method stub
		model = ModelFactory.createDefaultModel();
	}

	public void setTotalUniqueGraph(int totalUniqueGraph) {
		this.totalUniqueGraph = totalUniqueGraph;
	}

	public LinkedHashMap<String, ArrayList<String>> getDimHierMap() {
		return dimHierMap;
	}

	public void setDimHierMap(LinkedHashMap<String, ArrayList<String>> dimHierMap) {
		this.dimHierMap = dimHierMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getMeasureMap() {
		return measureMap;
	}

	public void setMeasureMap(LinkedHashMap<String, ArrayList<String>> measureMap) {
		this.measureMap = measureMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getHierLevelMap() {
		return hierLevelMap;
	}

	public void setHierLevelMap(LinkedHashMap<String, ArrayList<String>> hierLevelMap) {
		this.hierLevelMap = hierLevelMap;
	}

	public void runSparqlQuery(String query) {
		// TODO Auto-generated method stub
		ResultSet resultSet = getEndPointResultSet(null, query);
		methods.printResultSet(resultSet);
	}

	public ArrayList<String> getAllCubeLevels() {
		return allCubeLevels;
	}

	public void setAllCubeLevels(ArrayList<String> allCubeLevels) {
		this.allCubeLevels = allCubeLevels;
	}
}
