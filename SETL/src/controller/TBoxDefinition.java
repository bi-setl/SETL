package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TBoxDefinition {
	private ArrayList<String> classList;
	private ArrayList<String> objectList;
	private ArrayList<String> dataList;
	private ArrayList<String> levelList;
	private ArrayList<String> dimensionList;
	private ArrayList<String> hierarchyList;
	private LinkedHashMap<String, String> hierarchyStepsList;
	private ArrayList<String> measureList;
	private ArrayList<String> datasetList;
	private ArrayList<String> attributeList;
	private ArrayList<String> rollupList;
	private ArrayList<String> ontologyList;
	private LinkedHashMap<String, ArrayList<String>> cubeList;
	private LinkedHashMap<String, ArrayList<String>> cuboidList;
	private LinkedHashMap<String, String> cubeNodeList;
	private LinkedHashMap<String, String> cuboidNodeList;
	private String modelText;
	private LinkedHashMap<String, ArrayList<String>> annotationMap;
	private LinkedHashMap<String, ArrayList<String>> descriptionMap;
	private LinkedHashMap<String, ArrayList<String>> mdMap;
	private LinkedHashMap<String, String> prefixMap;

	public TBoxDefinition() {
		// TODO Auto-generated constructor stub
		initializeAll();
	}

	public LinkedHashMap<String, String> getPrefixMap() {
		return prefixMap;
	}

	public void setPrefixMap(LinkedHashMap<String, String> prefixMap) {
		this.prefixMap = prefixMap;
	}

	public String getModelText() {
		return modelText;
	}

	public void setModelText(String modelText) {
		this.modelText = modelText;
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

	private void initializeAll() {
		// TODO Auto-generated method stub
		setClassList(new ArrayList<>());
		setObjectList(new ArrayList<>());
		setDataList(new ArrayList<>());
		setLevelList(new ArrayList<>());
		setDimensionList(new ArrayList<>());
		setHierarchyList(new ArrayList<>());
		setHierarchyStepsList(new LinkedHashMap<>());
		setMeasureList(new ArrayList<>());
		setDatasetList(new ArrayList<>());
		setAttributeList(new ArrayList<>());
		setRollupList(new ArrayList<>());
		setOntologyList(new ArrayList<>());

		setAnnotationMap(new LinkedHashMap<>());
		setDescriptionMap(new LinkedHashMap<>());
		setMdMap(new LinkedHashMap<>());
		setCubeList(new LinkedHashMap<>());
		setCuboidList(new LinkedHashMap<>());
		setCubeNodeList(new LinkedHashMap<>());
		setCuboidNodeList(new LinkedHashMap<>());
		setPrefixMap(getAllPredefinedPrefixes());
	}

	public LinkedHashMap<String, ArrayList<String>> getAllAnnotationProperties() {
		LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
		hashMap.put("owl:backwardCompatibleWith", new ArrayList<>());
		hashMap.put("owl:deprecated", new ArrayList<>());
		hashMap.put("owl:incompatibleWith", new ArrayList<>());
		hashMap.put("owl:priorVersion", new ArrayList<>());
		hashMap.put("owl:versionInfo", new ArrayList<>());
		hashMap.put("rdfs:comment", new ArrayList<>());
		hashMap.put("rdfs:isDefinedBy", new ArrayList<>());
		hashMap.put("rdfs:label", new ArrayList<>());
		hashMap.put("rdfs:seeAlso", new ArrayList<>());
		return hashMap;
	}

	public ArrayList<String> getAllMDType() {
		ArrayList<String> list = new ArrayList<>();
		list.add("qb:DataStructureDefinition");
		list.add("qb:DataSet");
		list.add("qb:DimensionProperty");
		list.add("qb4o:Hierarchy");
		list.add("qb4o:HierarchyStep");
		list.add("qb4o:LevelAttribute");
		list.add("qb4o:LevelProperty");
		list.add("qb4o:RollupProperty");
		list.add("qb:MeasureProperty");
		list.add("qb:inLevel");
		return list;
	}

	public LinkedHashMap<String, ArrayList<String>> getAllMDProperties() {
		LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
		hashMap.put("qb4o:cardinality", new ArrayList<>());
		hashMap.put("qb4o:dimension", new ArrayList<>());
		hashMap.put("qb4o:hasAttribute", new ArrayList<>());
		hashMap.put("qb4o:rollup", new ArrayList<>());
		hashMap.put("qb4o:pcCardinality", new ArrayList<>());
		hashMap.put("qb4o:parentLevel", new ArrayList<>());
		hashMap.put("qb4o:childLevel", new ArrayList<>());
		hashMap.put("qb4o:inHierarchy", new ArrayList<>());
		hashMap.put("qb4o:level", new ArrayList<>());
		hashMap.put("qb4o:inDimension", new ArrayList<>());
		hashMap.put("qb4o:aggregateFunction", new ArrayList<>());
		hashMap.put("qb:measure", new ArrayList<>());
		hashMap.put("dct:conformsTo", new ArrayList<>());
		hashMap.put("qb:component", new ArrayList<>());
		hashMap.put("qb4o:isCuboidOf", new ArrayList<>());
		hashMap.put("qb4o:hasLevel", new ArrayList<>());
		hashMap.put("qb4o:hasHierarchy", new ArrayList<>());
		hashMap.put("qb:structure", new ArrayList<>());
		hashMap.put("qb4o:updateType", new ArrayList<>());

		return hashMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getAllClassProperties() {
		LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
		hashMap.put("owl:equivalentTo", new ArrayList<>());
		hashMap.put("owl:disjointWith", new ArrayList<>());
		hashMap.put("rdfs:subClassOf", new ArrayList<>());
		hashMap.put("rdf:type", new ArrayList<>());
		hashMap.put("owl:equivalentClass", new ArrayList<>());

		return hashMap;
	}

	public LinkedHashMap<String, ArrayList<String>> getAllNativeProperties() {
		LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();

		hashMap.put("rdfs:range", new ArrayList<>());
		hashMap.put("rdfs:domain", new ArrayList<>());
		hashMap.put("rdfs:subPropertyOf", new ArrayList<>());
		hashMap.put("owl:inverseOf", new ArrayList<>());
		hashMap.put("owl:memberOf", new ArrayList<>());
		return hashMap;
	}

	public LinkedHashMap<String, String> getAllPredefinedPrefixes() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		hashMap.put("sdw:", "http://example.com/sdw/");
		hashMap.put("owl:", "http://www.w3.org/2002/07/owl#");
		hashMap.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		hashMap.put("skos:", "http://www.w3.org/2004/02/skos/core#");
		hashMap.put("qb:", "http://purl.org/linked-data/cube#");
		hashMap.put("xsd:", "http://www.w3.org/2001/XMLSchema#");
		hashMap.put("qb4o:", "http://purl.org/qb4olap/cubes#");
		hashMap.put("dct:", "http://purl.org/dc/terms/");
		hashMap.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
		hashMap.put("bus:", "http://extbi.lab.aau.dk/ontology/business/");
		hashMap.put("xml:", "http://www.w3.org/XML/1998/namespace");
		hashMap.put("foaf:", "http://xmlns.com/foaf/0.1/");
		hashMap.put("dbpcat:", "http://dbpedia.org/resource/Category:");
		hashMap.put("dbpedia:", "http://dbpedia.org/resource/");
		hashMap.put("business:", "http://extbi.lab.aau.dk/ontology/sdw/");

		return hashMap;
	}

	public ArrayList<String> getXsdRanges() {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("xsd:string");
		arrayList.add("xsd:int");
		arrayList.add("xsd:short");
		arrayList.add("xsd:long");
		arrayList.add("xsd:byte");
		arrayList.add("xsd:boolean");
		arrayList.add("xsd:decimal");
		arrayList.add("xsd:float");
		arrayList.add("xsd:double");
		arrayList.add("xsd:dateTime");
		arrayList.add("xsd:time");
		arrayList.add("xsd:date");
		arrayList.add("xsd:gYearMonth");
		arrayList.add("xsd:gYear");
		arrayList.add("xsd:gMonthDay");
		arrayList.add("xsd:gDay");
		arrayList.add("xsd:gMonth");
		arrayList.add("xsd:hexBinary");
		arrayList.add("xsd:base64Binary");
		arrayList.add("xsd:anyURI");
		arrayList.add("xsd:normalizedString");
		arrayList.add("xsd:token");
		arrayList.add("xsd:language");
		arrayList.add("xsd:unsignedByte");
		arrayList.add("xsd:positiveInteger");
		arrayList.add("xsd:nonNegativeInteger");
		arrayList.add("xsd:unsignedLong");
		arrayList.add("xsd:unsignedInt");
		arrayList.add("xsd:unsignedShort");
		arrayList.add("xsd:NMTOKEN");
		arrayList.add("xsd:Name");
		arrayList.add("xsd:NCName");
		arrayList.add("xsd:integer");
		arrayList.add("xsd:nonPositiveInteger");
		arrayList.add("xsd:negativeInteger");
		return arrayList;
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
		if (!prefix.contains("http") && !prefix.contains("www")) {
			String[] segments = prefix.split(":");
			if (segments.length == 2) {
				String firstSegment = segments[0] + ":";
				return getPrefixMap().get(firstSegment) + segments[1];
			} else {
				return prefix;
			}
		} else 
			return prefix;
	}

	public String getPrefixStrings() {
		String tripleString = "";
		Set<String> prefixes = getAllPredefinedPrefixes().keySet();
		Iterator<String> iterator = prefixes.iterator();

		while (iterator.hasNext()) {

			String prefix = (String) iterator.next();
			String iri = (String) getAllPredefinedPrefixes().get(prefix);

			tripleString += "@prefix " + prefix + " <" + iri + ">.\n";
		}

		return tripleString;
	}
	
	public String getPrefixStrings(LinkedHashMap<String, String> hashMap) {
		String tripleString = "";
		Set<String> prefixes = hashMap.keySet();
		Iterator<String> iterator = prefixes.iterator();

		while (iterator.hasNext()) {

			String prefix = (String) iterator.next();
			String iri = (String) hashMap.get(prefix);

			tripleString += "@prefix " + prefix + " <" + iri + ">.\n";
		}

		return tripleString;
	}
	
	public ArrayList<String> getAggregatedFunctions(){
		
		ArrayList<String> cardinalities = new ArrayList<>();
		
		cardinalities.add("qb4o:avg");
		cardinalities.add("qb4o:count");
		cardinalities.add("qb4o:min");
		cardinalities.add("qb4o:max");
		cardinalities.add("qb4o:sum");
		return cardinalities;
	}
	
	public ArrayList<String> getCardinalities(){
		
		ArrayList<String> cardinalities = new ArrayList<>();
		
		cardinalities.add("qb4o:OneToOne");
		cardinalities.add("qb4o:OneToMany");
		cardinalities.add("qb4o:ManyToMany");
		cardinalities.add("qb4o:ManyToOne");
		return cardinalities;
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

	public LinkedHashMap<String, ArrayList<String>> getMdMap() {
		return mdMap;
	}

	public void setMdMap(LinkedHashMap<String, ArrayList<String>> mdMap) {
		this.mdMap = mdMap;
	}

	public LinkedHashMap<String, String> getHierarchyStepsList() {
		return hierarchyStepsList;
	}

	public void setHierarchyStepsList(LinkedHashMap<String, String> hierarchyStepsList) {
		this.hierarchyStepsList = hierarchyStepsList;
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

	public ArrayList<String> getHashMapStrings(LinkedHashMap<String, ArrayList<String>> cubeList2) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();
		for (Map.Entry<String, ArrayList<String>> map : cubeList2.entrySet()) {
			String key = map.getKey();

			arrayList.add(key);
		}
		return arrayList;
	}

	public ArrayList<String> getOntologyList() {
		return ontologyList;
	}

	public void setOntologyList(ArrayList<String> ontologyList) {
		this.ontologyList = ontologyList;
	}
}
