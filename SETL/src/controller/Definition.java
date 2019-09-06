package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import helper.Methods;
import model.SelectedLevel;
import model.SelectedLevelInstance;
import model.TwinValue;
import queries.Extraction;

public class Definition {
	private Methods methods;
	private Extraction extraction;
	private LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> datasetProperties;
	private String endpointString;
	private int totalUniqueGraph;
	private List<String> selectedGraphs;
	private List<String> selectedInstances;
	private LinkedHashMap<String, ArrayList<String>> dimHierMap;
	private LinkedHashMap<String, ArrayList<String>> measureMap;
	private LinkedHashMap<String, ArrayList<String>> hierLevelMap;
	private ArrayList<SelectedLevel> selectedLevelList;
	private ArrayList<String> levelProperties;
	private ArrayList<Object> levelInstanceObjects;
	private LinkedHashMap<String, LinkedHashMap<String, SelectedLevelInstance>> instancesMap;
	private ArrayList<String> selectedColumns;
	private LinkedHashMap<String, String> filterPropertyMap;
	private String aboxPath;
	private String tboxPath;
	private LinkedHashMap<String, String> prefixMap;
	private LinkedHashMap<String, ArrayList<String>> selectedMeasureFunctionMap;
	private ArrayList<String> allCubeLevels;
	private ArrayList<String> bannedLevels;

	public Definition() {
		// TODO Auto-generated constructor stub
		methods = new Methods();
		extraction = new Extraction();
	}

	public void initializeOlapEndPoint(String endPoint) {
		// TODO Auto-generated method stub
		setDatasetProperties(new LinkedHashMap<>());
		setEndpointString(endPoint);
		setDimHierMap(new LinkedHashMap<>());
		setMeasureMap(new LinkedHashMap<>());
		setHierLevelMap(new LinkedHashMap<>());
		setSelectedLevelList(new ArrayList<>());
		setInstancesMap(new LinkedHashMap<>());
		setSelectedMeasureFunctionMap(new LinkedHashMap<>());
		setAllCubeLevels(new ArrayList<>());
		setBannedLevels(new ArrayList<>());

		if (endPoint != null) {
			setTotalUniqueGraph(extraction.getTotalUniqueGraph(endPoint));
			setDatasetProperties(extraction.extractEndPointDatasets(endPoint));
		} else {
			setDatasetProperties(extraction.extractEndPointDatasets(null));
		}
	}

	public void extractDatasetCube(String dataset, List<String> selectedGraphs, List<String> selectedInstances) {
		// TODO Auto-generated method stub
		setSelectedGraphs(checkSelectedGraph(selectedGraphs));
		setSelectedInstances(checkSelectedGraph(selectedInstances));

		extraction.extractOlapDatasetCube(getEndpointString(), dataset, getSelectedGraphs(), getSelectedInstances());
		setDimHierMap(extraction.getDimHierMap());
		setMeasureMap(extraction.getMeasureMap());
		setHierLevelMap(extraction.getHierLevelMap());
		
		if (extraction.getAllCubeLevels() != null) {
			setAllCubeLevels(extraction.getAllCubeLevels());
		}
	}

	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> getDatasetProperties() {
		return datasetProperties;
	}

	public void setDatasetProperties(
			LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> datasetProperties) {
		this.datasetProperties = datasetProperties;
	}

	public String getEndpointString() {
		return endpointString;
	}

	public void setEndpointString(String endpointString) {
		this.endpointString = endpointString;
	}

	public int getTotalUniqueGraph() {
		return totalUniqueGraph;
	}

	public void setTotalUniqueGraph(int totalUniqueGraph) {
		this.totalUniqueGraph = totalUniqueGraph;
	}

	public List<String> getSelectedGraphs() {
		return selectedGraphs;
	}

	public void setSelectedGraphs(List<String> selectedGraphs) {
		this.selectedGraphs = selectedGraphs;
	}

	public List<String> getSelectedInstances() {
		return selectedInstances;
	}

	public void setSelectedInstances(List<String> selectedInstances) {
		this.selectedInstances = selectedInstances;
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

	public ArrayList<SelectedLevel> getSelectedLevelList() {
		return selectedLevelList;
	}

	public void setSelectedLevelList(ArrayList<SelectedLevel> selectedLevelList) {
		this.selectedLevelList = selectedLevelList;
	}

	public ArrayList<String> getLevelProperties() {
		return levelProperties;
	}

	public void setLevelProperties(ArrayList<String> levelProperties) {
		this.levelProperties = levelProperties;
	}

	public ArrayList<Object> getLevelInstanceObjects() {
		return levelInstanceObjects;
	}

	public void setLevelInstanceObjects(ArrayList<Object> levelInstanceObjects) {
		this.levelInstanceObjects = levelInstanceObjects;
	}

	public LinkedHashMap<String, LinkedHashMap<String, SelectedLevelInstance>> getInstancesMap() {
		return instancesMap;
	}

	public void setInstancesMap(LinkedHashMap<String, LinkedHashMap<String, SelectedLevelInstance>> instancesMap) {
		this.instancesMap = instancesMap;
	}

	public void extractLevelProperties(String selectedLevelName) {
		// TODO Auto-generated method stub
		setLevelProperties(
				extraction.extractEndPointLevelProperties(endpointString, selectedGraphs, selectedLevelName));
	}

	public LinkedHashMap<String, TwinValue> getOlapConditionalHashMap() {
		LinkedHashMap<String, TwinValue> conditionalHashMap = new LinkedHashMap<>();

		conditionalHashMap.put("No Selection", new TwinValue("no", "multiple"));
		conditionalHashMap.put("equal to (=)", new TwinValue("=", "multiple"));
		conditionalHashMap.put("not equal to (!=)", new TwinValue("!=", "multiple"));
		conditionalHashMap.put("greater than (>)", new TwinValue(">", "single"));
		conditionalHashMap.put("greater than or equal to (>=)", new TwinValue(">=", "single"));
		conditionalHashMap.put("less than (<)", new TwinValue("<", "single"));
		conditionalHashMap.put("less than or equal to (<=)", new TwinValue("<=", "single"));

		return conditionalHashMap;
	}

	public void extractLevelInstances(String selectedLevel, String selectedProperty) {
		// TODO Auto-generated method stub
		setLevelInstanceObjects(extraction.extractOlapLevelInstances(endpointString, selectedLevel, selectedProperty,
				selectedGraphs, selectedInstances));
	}

	public String generateOlapQuery(String datasetName) {
		// TODO Auto-generated method stub
		datasetName = extraction.assignIRI(datasetName);

		setFilterPropertyMap(new LinkedHashMap<>());

		String queryString = "PREFIX qb: <http://purl.org/linked-data/cube#>\n";
		queryString += "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\n";
		queryString += "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n";
		queryString += "SELECT ";

		setSelectedColumns(new ArrayList<>());

		// GET ALL LEVEL TAGS
		String levelTagString = "";
		for (SelectedLevel selectedLevel : selectedLevelList) {
			String levelNameSegment = methods.getLastSegment(selectedLevel.getLevelName());
			String tagString = selectedLevel.getLevelName();
			String levelPath = selectedLevel.getLevelPath();

			String[] parts = levelPath.split(",");
			String dimString = parts[1].trim();
			dimString = methods.getLastSegment(dimString);
			tagString = methods.getLastSegment(tagString);
			
			if (selectedLevel.getViewProperties().size() == 0) {
				tagString = "?" + dimString + "_" + tagString;
				levelTagString += tagString + " ";
				selectedColumns.add(tagString);
				
			} else {
				for (String propertyString : selectedLevel.getViewProperties()) {
					String propertyTag = "?" + dimString + "_" + levelNameSegment + "_"
							+ methods.getLastSegment(propertyString);
					selectedColumns.add(propertyTag);
					levelTagString += propertyTag + " ";
				}
			}
		}
		queryString += levelTagString;

		int count = 1;
		for (String measureName : getSelectedMeasureFunctionMap().keySet()) {
			String measureRangeString = extraction.getOlapMeasureRange(endpointString, selectedGraphs, measureName);

			// GET ALL MEASURE TAGS
			ArrayList<String> aggregateFunctions = getSelectedMeasureFunctionMap().get(measureName);
			String measureTagString = "";

			for (String aggregateFunction : aggregateFunctions) {
				String functionName = methods.getLastSegment(aggregateFunction);
				String functionQuery = "(" + functionName.toUpperCase() + "(<" + measureRangeString + ">(?m" + count
						+ ")) as ";
				String functionTag = "?" + methods.getLastSegment(measureName) + "_" + functionName.toLowerCase();
				functionQuery += functionTag + ") ";
				measureTagString += functionQuery;
				selectedColumns.add(functionTag);
			}
			queryString += measureTagString + "\n";
			count++;
		}

		// GET ALL SCHEMA AND INSTANCE STRING
		queryString += getSchemaAndInstanceString();

		queryString += "WHERE {\n";
		queryString += "?o a qb:Observation .\n";
		queryString += "?o qb:dataSet <" + datasetName + "> .\n";

		count = 1;
		for (String measureName : getSelectedMeasureFunctionMap().keySet()) {
			queryString += "?o <" + extraction.assignIRI(measureName) + "> ?m" + count + " .\n";
			count++;
		}

		// GET ALL LEVELS STRING
		LinkedHashMap<String, String> queryMap = new LinkedHashMap<>();
		for (SelectedLevel selectedLevel : getSelectedLevelList()) {
			LinkedHashMap<String, String> levelQueryMap = generateLevelOlapQuery(selectedLevel);

			for (String levelQuery : levelQueryMap.keySet()) {
				queryMap.put(levelQuery, levelQuery);
			}
		}

		for (String levelQuery : queryMap.keySet()) {
			queryString += levelQuery;
		}

		// GET FILTER QUERY
		queryString += getFilterOlapQuery();

		queryString += "}\n";

		// GET GROUP BY AND ORDER BY QUERY
		queryString += getGroupByOlapQuery(selectedColumns);

		setSelectedColumns(selectedColumns);

		return queryString;
	}

	private String getGroupByOlapQuery(ArrayList<String> selectedColumns) {
		// TODO Auto-generated method stub
		String groupByString = "GROUP BY ";
		String orderByString = "ORDER BY ";

		for (int i = 0; i < selectedColumns.size() - 1; i++) {
			groupByString += selectedColumns.get(i);
			orderByString += selectedColumns.get(i);

			if (i < selectedColumns.size() - 2) {
				groupByString += " ";
				orderByString += " ";
			}
		}
		return groupByString + "\n" + orderByString;
	}

	private String getFilterOlapQuery() {
		// TODO Auto-generated method stub
		String filterQuery = "";
		String levelFilterQuery = "";

		if (selectedLevelList.size() > 0) {
			for (int i = 0; i < selectedLevelList.size(); i++) {
				SelectedLevel selectedLevel = selectedLevelList.get(i);
				LinkedHashMap<String, SelectedLevelInstance> linkedHashMap = instancesMap
						.get(selectedLevel.getLevelName());

				for (String propertyString : linkedHashMap.keySet()) {
					SelectedLevelInstance selectedLevelInstance = linkedHashMap.get(propertyString);
					if (selectedLevelInstance.getInstances().size() > 0) {
						if (levelFilterQuery.length() > 0) {
							levelFilterQuery = "(" + levelFilterQuery + ")";

							if (filterQuery.length() > 0) {
								filterQuery += " && ";
							}

							filterQuery += levelFilterQuery;
							levelFilterQuery = "";
						}

						String filterProperty = selectedLevelInstance.getFilterCondition();

						String propertyTag = getFilterPropertyMap().get(extraction.assignPrefix(propertyString));
						ArrayList<Object> instanceObjects = selectedLevelInstance.getInstances();

						/*for (int j = 0; j < instanceObjects.size(); j++) {
							if (filterProperty.equals("no")) {
								levelFilterQuery += "REGEX (" + propertyTag + ", \"" + instanceObjects.get(j)
										+ "\", \"i\")";

								if (j < instanceObjects.size() - 1) {
									levelFilterQuery += " || ";
								}
							} else {
								levelFilterQuery += "(" + propertyTag + " " + filterProperty + " "
										+ instanceObjects.get(j) + ")";

								if (j < instanceObjects.size() - 1) {
									levelFilterQuery += " || ";
								}
							}
						}*/
						
						if ((instanceObjects.get(0) instanceof String) && (filterProperty.equals("="))) {
							filterProperty = "no";
						}
						
						if (filterProperty.equals("no")) {
							for (int j = 0; j < instanceObjects.size(); j++) {
								levelFilterQuery += "REGEX (" + propertyTag + ", \"" + instanceObjects.get(j)
								+ "\", \"i\")";

								if (j < instanceObjects.size() - 1) {
									levelFilterQuery += " || ";
								}
							}
						} else {
							for (int j = 0; j < instanceObjects.size(); j++) {
								levelFilterQuery += "(" + propertyTag + " " + filterProperty + " "
										+ instanceObjects.get(j) + ")";

								if (j < instanceObjects.size() - 1) {
									levelFilterQuery += " || ";
								}
							}
						}
					}
				}
			}
		}

		if (filterQuery.length() > 0) {
			if (levelFilterQuery.length() > 0) {
				filterQuery += " && " + levelFilterQuery;
			}

			filterQuery = "FILTER (" + filterQuery + ")";
		} else {
			if (levelFilterQuery.length() > 0) {
				filterQuery += levelFilterQuery;
				filterQuery = "FILTER (" + filterQuery + ")";
			}
		}

		return filterQuery;
	}

	private LinkedHashMap<String, String> generateLevelOlapQuery(SelectedLevel selectedLevel) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> levelQueryMap = new LinkedHashMap<>();
		
		String selectedLevelString = selectedLevel.getLevelName();
		selectedLevelString = extraction.assignIRI(selectedLevelString);

		String[] segments = selectedLevel.getLevelPath().split(",");
		String selectedDimensionString = segments[1].trim();
		String selectedHierarchyString = segments[2].trim();

		String levelTagKey = "?" + methods.getLastSegment(selectedDimensionString) + "_"
				+ methods.getLastSegment(selectedLevelString);

		ArrayList<String> hierLevels = getHierLevelMap().get(selectedHierarchyString);
		ArrayList<String> requiredLevels = new ArrayList<>();

		if (hierLevels.size() == 1) {
			String queryString = "?o <" + selectedLevelString + "> " + levelTagKey + " .\n";
			levelQueryMap.put(queryString, queryString);

			if (instancesMap.containsKey(extraction.assignPrefix(selectedLevelString))) {
				LinkedHashMap<String, String> filterQueryMap = getFilterPropertyQuery(
						extraction.assignPrefix(selectedLevelString), levelTagKey);

				for (String filterString : filterQueryMap.keySet()) {
					levelQueryMap.put(filterString, filterString);
				}
			}
			
			for (String propertyString : selectedLevel.getViewProperties()) {
				String levelNameSegment = methods.getLastSegment(selectedLevel.getLevelName());
				String propertyTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
				+ levelNameSegment + "_" + methods.getLastSegment(propertyString);
				String queryText = levelTagKey + " <" + extraction.assignIRI(propertyString) + "> " + propertyTag + " .\n";
				levelQueryMap.put(queryText, queryText);
			}
			return levelQueryMap;
		} else {
			int position = hierLevels.indexOf(extraction.assignPrefix(selectedLevelString));
			for (int i = position; i < hierLevels.size(); i++) {
				if (!getBannedLevels().contains(hierLevels.get(i))) {
					requiredLevels.add(hierLevels.get(i));
				}
			}

			if (requiredLevels.size() == 1) {
				String queryString = "?o <" + selectedLevelString + "> " + levelTagKey + " .\n";
				levelQueryMap.put(queryString, queryString);

				if (instancesMap.containsKey(extraction.assignPrefix(selectedLevelString))) {
					LinkedHashMap<String, String> filterQueryMap = getFilterPropertyQuery(
							extraction.assignPrefix(selectedLevelString), levelTagKey);

					for (String filterString : filterQueryMap.keySet()) {
						levelQueryMap.put(filterString, filterString);
					}
				}
				
				for (String propertyString : selectedLevel.getViewProperties()) {
					String levelNameSegment = methods.getLastSegment(selectedLevel.getLevelName());
					String propertyTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
					+ levelNameSegment + "_" + methods.getLastSegment(propertyString);
					String queryText = levelTagKey + " <" + extraction.assignIRI(propertyString) + "> " + propertyTag + " .\n";
					levelQueryMap.put(queryText, queryText);
				}
				return levelQueryMap;
			} else {
				ArrayList<String> checkedList = new ArrayList<>();

				for (int i = requiredLevels.size() - 1; i > -1; i--) {
					String levelString = requiredLevels.get(i);
					levelString = extraction.assignIRI(levelString);

					if (!checkedList.contains(levelString)) {
						if (checkedList.size() == 0) {
							LinkedHashMap<String, String> hierStepValue = extraction.getHierarchyStepWithChild(
									endpointString, selectedGraphs, levelString, selectedHierarchyString);

							String parentLevel = hierStepValue.get("parent");
							String childLevel = levelString;
							String rollUp = hierStepValue.get("rollup");

							String parentTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
									+ methods.getLastSegment(parentLevel);
							String childTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
									+ methods.getLastSegment(childLevel);

							String queryString = "?o <" + childLevel + "> " + childTag + " .\n";
							levelQueryMap.put(queryString, queryString);
							queryString = childTag + " qb4o:memberOf <" + childLevel + "> .\n";
							levelQueryMap.put(queryString, queryString);
							queryString = childTag + " <" + rollUp + "> " + parentTag + " .\n";
							levelQueryMap.put(queryString, queryString);
							queryString = parentTag + " qb4o:memberOf <" + parentLevel + "> .\n";
							levelQueryMap.put(queryString, queryString);

							if (instancesMap.containsKey(extraction.assignPrefix(childLevel))) {
								LinkedHashMap<String, String> filterQueryMap = getFilterPropertyQuery(
										extraction.assignPrefix(childLevel), childTag);

								for (String filterString : filterQueryMap.keySet()) {
									levelQueryMap.put(filterString, filterString);
								}
							}

							if (instancesMap.containsKey(extraction.assignPrefix(parentLevel))) {
								LinkedHashMap<String, String> filterQueryMap = getFilterPropertyQuery(
										extraction.assignPrefix(parentLevel), parentTag);

								for (String filterString : filterQueryMap.keySet()) {
									levelQueryMap.put(filterString, filterString);
								}
							}

							checkedList.add(childLevel);
							checkedList.add(parentLevel);
						} else {
							LinkedHashMap<String, String> hierStepValue = extraction.getHierarchyStepWithParent(
									endpointString, selectedGraphs, levelString, selectedHierarchyString);

							String childLevel = hierStepValue.get("child");
							String parentLevel = levelString;
							String rollUp = hierStepValue.get("rollup");

							String parentTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
									+ methods.getLastSegment(parentLevel);
							String childTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
									+ methods.getLastSegment(childLevel);

							String queryString = childTag + " <" + rollUp + "> " + parentTag + " .\n";
							levelQueryMap.put(queryString, queryString);
							queryString = parentTag + " qb4o:memberOf <" + parentLevel + "> .\n";
							levelQueryMap.put(queryString, queryString);

							if (instancesMap.containsKey(extraction.assignPrefix(parentLevel))) {
								LinkedHashMap<String, String> filterQueryMap = getFilterPropertyQuery(
										extraction.assignPrefix(parentLevel), parentTag);

								for (String filterString : filterQueryMap.keySet()) {
									levelQueryMap.put(filterString, filterString);
								}
							}

							checkedList.add(parentLevel);
						}
					}
				}
				
				for (String propertyString : selectedLevel.getViewProperties()) {
					String levelNameSegment = methods.getLastSegment(selectedLevel.getLevelName());
					String propertyTag = "?" + methods.getLastSegment(selectedDimensionString) + "_"
					+ levelNameSegment + "_" + methods.getLastSegment(propertyString);
					String queryText = levelTagKey + " <" + extraction.assignIRI(propertyString) + "> " + propertyTag + " .\n";
					levelQueryMap.put(queryText, queryText);
				}
				return levelQueryMap;
			}
		}
	}

	private LinkedHashMap<String, String> getFilterPropertyQuery(String selectedLevelString, String levelTagKey) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> filterQueryMap = new LinkedHashMap<>();

		LinkedHashMap<String, SelectedLevelInstance> selectedInstances = instancesMap.get(selectedLevelString);
		LinkedHashMap<String, String> filterMap = getFilterPropertyMap();

		for (String selectedProperty : selectedInstances.keySet()) {
			if (selectedInstances.get(extraction.assignPrefix(selectedProperty)) != null) {
				if (selectedInstances.get(selectedProperty).getInstances().size() > 0) {
					selectedProperty = extraction.assignIRI(selectedProperty);
					String propertyTag = "?" + methods.getLastSegment(selectedLevelString) + "_"
							+ methods.getLastSegment(selectedProperty);
					String queryString = levelTagKey + " <" + selectedProperty + "> " + propertyTag + " .\n";
					filterQueryMap.put(queryString, queryString);
					filterMap.put(extraction.assignPrefix(selectedProperty), propertyTag);
				}
			}
		}

		setFilterPropertyMap(filterMap);
		return filterQueryMap;
	}

	private String getSchemaAndInstanceString() {
		// TODO Auto-generated method stub
		String totalString = "";
		String fromString = "FROM ";

		for (String schemaGraph : selectedGraphs) {
			if (methods.checkString(schemaGraph)) {
				totalString += fromString + "<" + schemaGraph + ">\n";
			}
		}

		for (String instanceGraph : selectedInstances) {
			if (methods.checkString(instanceGraph)) {
				totalString += fromString + "<" + instanceGraph + ">\n";
			}
		}

		return totalString;
	}

	public void resetOlapSelection() {
		// TODO Auto-generated method stub
		selectedLevelList = new ArrayList<>();
		instancesMap = new LinkedHashMap<>();
		selectedColumns = new ArrayList<>();
		filterPropertyMap = new LinkedHashMap<>();
		selectedMeasureFunctionMap = new LinkedHashMap<>();
	}

	public Object[][] runSparqlQuery(String queryString) {
		// TODO Auto-generated method stub
		return extraction.runSparqlQuery(endpointString, queryString, selectedColumns);
	}

	public boolean readTBoxModel(String filePath) {
		// TODO Auto-generated method stub
		extraction.initializeModel();
		boolean status = extraction.readModel(filePath, "tbox");
		if (status) {
			tboxPath = filePath;
			setPrefixMap(extraction.getPrefixMap());
		}
		return status;
	}

	public boolean readABoxModel(String filePath) {
		// TODO Auto-generated method stub
		boolean status = extraction.readModel(filePath, "abox");
		if (status) {
			aboxPath = filePath;
		}
		return status;
	}

	public ArrayList<String> getSelectedColumns() {
		return selectedColumns;
	}

	public void setSelectedColumns(ArrayList<String> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	public LinkedHashMap<String, String> getFilterPropertyMap() {
		return filterPropertyMap;
	}

	public void setFilterPropertyMap(LinkedHashMap<String, String> filterPropertyMap) {
		this.filterPropertyMap = filterPropertyMap;
	}

	public String getAboxPath() {
		return aboxPath;
	}

	public void setAboxPath(String aboxPath) {
		this.aboxPath = aboxPath;
	}

	public String getTboxPath() {
		return tboxPath;
	}

	public void setTboxPath(String tboxPath) {
		this.tboxPath = tboxPath;
	}

	public LinkedHashMap<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(LinkedHashMap<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getSelectedMeasureFunctionMap() {
		return selectedMeasureFunctionMap;
	}

	public void setSelectedMeasureFunctionMap(LinkedHashMap<String, ArrayList<String>> selectedMeasureFunctionMap) {
		this.selectedMeasureFunctionMap = selectedMeasureFunctionMap;
	}

	private List<String> checkSelectedGraph(List<String> selectedGraphs) {
		// TODO Auto-generated method stub
		if (selectedGraphs == null) {
			List<String> list = new ArrayList<String>();
			list.add("");
			return list;
		} else {
			return selectedGraphs;
		}
		
	}

	public void runSparqlQuery(String query, boolean b) {
		// TODO Auto-generated method stub
		extraction.runSparqlQuery(query);
	}

	public ArrayList<String> getAllCubeLevels() {
		return allCubeLevels;
	}

	public void setAllCubeLevels(ArrayList<String> allCubeLevels) {
		this.allCubeLevels = allCubeLevels;
	}

	public ArrayList<String> getBannedLevels() {
		return bannedLevels;
	}

	public void setBannedLevels(ArrayList<String> bannedLevels) {
		this.bannedLevels = bannedLevels;
	}
}
