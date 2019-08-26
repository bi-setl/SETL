package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class MappingDefinition {
	private ArrayList<String> datasetList;
	private ArrayList<String> mapperList;
	private ArrayList<String> recordList;
	private LinkedHashMap<String, String> prefixMap;
	private String modelText;
	
	private void initializeAll() {
		// TODO Auto-generated method stub
		setDatasetList(new ArrayList<>());
		setMapperList(new ArrayList<>());
		setRecordList(new ArrayList<>());
	}
	
	public MappingDefinition() {
		initializeAll();
	}
	
	public LinkedHashMap<String, String> getAllPredefinedPrefixes() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		hashMap.put("map:", "http://www.map.org/example#");
		hashMap.put("onto:", "http://www.onto.org/schema#");
		hashMap.put("owl:", "http://www.w3.org/2002/07/owl#");
		hashMap.put("qb:", "http://purl.org/linked-data/cube#");
		hashMap.put("qb4o:", "http://purl.org/qb4olap/cubes#");
		hashMap.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
		hashMap.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		hashMap.put("xsd:", "http://www.w3.org/2001/XMLSchema#");
		hashMap.put("skos:", "http://www.w3.org/2004/02/skos/core#");
		return hashMap;
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
	
	public String getModelText() {
		return modelText;
	}

	public void setModelText(String modelText) {
		this.modelText = modelText;
	}
	
	public ArrayList<String> getSourceRecordType() {
		ArrayList<String> sources = new ArrayList<>();
		sources.add("Source Property");
		// sources.add("SPARQL Query");
		sources.add("Source Expression");
		return sources;
	}
	
	public String[] getRelations() {
		String[] operators = { "skos:exact", "skos:narrower", "skos:broader", "owl:SameAs", "rdfs:subClassOf",
		"rdfs:subPropertyOf" };
		return operators;
	}

	public ArrayList<String> getSourceMapperType() {
		// TODO Auto-generated method stub
		ArrayList<String> sources = new ArrayList<>();
		sources.add("All");
		sources.add("SPARQL Query");
		
		return sources;
	}

	public String getPrefixStrings(LinkedHashMap<String, String> allPrefixes) {
		// TODO Auto-generated method stub
		String tripleString = "";
		Set<String> prefixes = allPrefixes.keySet();
		Iterator<String> iterator = prefixes.iterator();

		while (iterator.hasNext()) {

			String prefix = (String) iterator.next();
			String iri = (String) allPrefixes.get(prefix);

			tripleString += "@prefix " + prefix + " <" + iri + ">.\n";
		}

		return tripleString;
	}
}
