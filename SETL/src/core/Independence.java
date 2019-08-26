package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import helper.AllFileOperations;
import model.ModelDouble;
import model.Single;
import model.Weight;

public class Independence {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		dbpediaResourceRetriever("Albertslund", 5, "dbpediaData.json", "dbpediaResources.txt");
//		// sparqlEndResourceRetriever("Copenhagen", 5, "sparqlEndResources.txt");
//		localKBResourceRetriever("municipality.ttl", 5, "localKBResources.txt");
//
//		localKBPropertyWeightGenerator("municipalityId, region, population, area", "municipality.ttl", "local.txt");
//		localKBsemanticBagGenerator("localKBResources.txt", "municipality.ttl", "localKBsemanticBagGenerator.txt");
//		dbpediaSemanticBagGenerator("dbpediaData.json", "dbpediaSemanticBagGenerator.txt");
//		matchSemanticBag(0.1, "localKBsemanticBagGenerator.txt", "dbpediaSemanticBagGenerator.txt", "local.txt", "matcher.txt");
//	}
	
	public boolean matchSemanticBag(double threshold, String localSemanticFile, String externalSemanticFile, String localWeightFile, String dumpFile) {
		// TODO Auto-generated method stub
		AllFileOperations allFileOperations = new AllFileOperations();
		allFileOperations.deleteFile(dumpFile);
		
		ArrayList<Object> localBag = allFileOperations.readFromBinaryFile(localSemanticFile);
		ArrayList<Object> externalBag = allFileOperations.readFromBinaryFile(externalSemanticFile);
		ArrayList<Object> weightBag = allFileOperations.readFromBinaryFile(localWeightFile);
		
		for (int i = 0; i < localBag.size(); i++) {
			ModelDouble modelDouble = (ModelDouble) localBag.get(i);
			Object internalResource = modelDouble.getObjectOne();
			
			// System.out.println(internalResource);
			ArrayList<Object> internalSemanticList = (ArrayList<Object>) modelDouble.getObjectTwo();
			
			for (int j = 0; j < externalBag.size(); j++) {
				ModelDouble modelDouble2 = (ModelDouble) externalBag.get(j);
				Object externalResource = modelDouble2.getObjectOne();
				ArrayList<Object> externalSemanticList = (ArrayList<Object>) modelDouble2.getObjectTwo();
				
				ArrayList<ModelDouble> matchList = new ArrayList<>();
				
				for (int k = 0; k < internalSemanticList.size(); k++) {
					ModelDouble modelDouble3 = (ModelDouble) internalSemanticList.get(k);
					String internalProperty = modelDouble3.getObjectOne().toString().trim().toLowerCase();
					String internalValue = modelDouble3.getObjectTwo().toString().trim().toLowerCase();
					
					for (int l = 0; l < externalSemanticList.size(); l++) {
						String externalValue = externalSemanticList.get(l).toString().trim().toLowerCase();
						Pattern pattern = Pattern.compile("\\b" + internalValue + "\\b");
						Matcher matcher = pattern.matcher(externalValue);
						
						while (matcher.find()) {
							int count = 0;
							
							for (int m = 0; m < matchList.size(); m++) {
								ModelDouble modelDouble4 = matchList.get(m);
								String value = modelDouble4.getObjectTwo().toString();
								
								if (internalValue.equals(value)) {
									count++;
								}
							}
							
							if (count == 0) {
								ModelDouble modelDouble4 = new ModelDouble();
								modelDouble4.setObjectOne(internalProperty);
								modelDouble4.setObjectTwo(internalValue);
								matchList.add(modelDouble4);
							}
						}
					}
				}
				
				float totalWeight = 0, matchWeight = 0;
				
				for (int l = 0; l < matchList.size(); l++) {
					ModelDouble modelDouble3 = matchList.get(l);
					String internalProperty = modelDouble3.getObjectOne().toString().trim().toLowerCase();
					String internalValue = modelDouble3.getObjectTwo().toString().trim().toLowerCase();
					
					for (int m = 0; m < weightBag.size(); m++) {
						Weight weight = (Weight) weightBag.get(m);
						String property = weight.getObject().toString().trim().toLowerCase();
						float weightValue = weight.getWeight();
						
						totalWeight = totalWeight + weightValue;
						
						if (property.equals(internalProperty)) {
							matchWeight = matchWeight + weightValue;
						}
					}
					
				}
				
				float matchResult = (matchWeight * 100) / totalWeight;
				if (matchResult >= threshold) {
					String matched = internalResource + " owl:sameAs " +externalResource;
					System.out.println(matched);
					
					Single single = new Single();
					single.setString(matched);
					allFileOperations.writeToBinary(dumpFile, single, true);
				} else {
					System.out.println("Not matched");
				}
			}
		}
		
		return true;
		//System.out.println("Works have been saved to " + dumpFile);
	}

	public boolean dbpediaSemanticBagGenerator(String filename, String dumpfile) {
		// TODO Auto-generated method stub

		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);
		
		JSONParser parser = new JSONParser();
		try {
			JSONObject object = (JSONObject) parser.parse(new FileReader(filename));
			ArrayList<Object> keyList = new ArrayList<>(object.keySet());

			for (int i = 0; i < keyList.size(); i++) {
				JSONArray jsonArray = (JSONArray) object.get(keyList.get(i));

				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(j);
					Object value = jsonObject.get("uri");
					
					ArrayList<String> arrayList = new ArrayList<>();
					arrayList = getObjects(jsonObject, arrayList);
					
					ModelDouble modelDouble = new ModelDouble();
					modelDouble.setObjectOne(value);
					modelDouble.setObjectTwo(arrayList);
					
					AllFileOperations allFileOperations = new AllFileOperations();
					allFileOperations.writeToBinary(dumpfile, modelDouble, true);
				}
			}
			
			System.out.println("Works have been saved to " + dumpfile);
			
			ArrayList<Object> objectList = operations.readFromBinaryFile(dumpfile);
			for (int i = 0; i < objectList.size(); i++) {
				ModelDouble double1 = (ModelDouble) objectList.get(i);
				System.out.println(double1.getObjectOne());
				
				ArrayList<Object> arrayList = (ArrayList<Object>) double1.getObjectTwo();
				
				for (int j = 0; j < arrayList.size(); j++) {
					System.out.println(arrayList.get(j));
				}
			}
			return true;
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error occured");
			return false;
		}
		
	}
	
	private static ArrayList<String> getObjects(JSONObject jsonObject, ArrayList<String> arrayList) {
		// TODO Auto-generated method stub
		ArrayList<Object> keyList = new ArrayList<>(jsonObject.keySet());
		for (int i = 0; i < keyList.size(); i++) {
			Object value = jsonObject.get(keyList.get(i));
			
			if ((value instanceof Long) || (value instanceof String)) {
				if (!arrayList.contains(value.toString())) {
					arrayList.add(value.toString());
				}
			} else if (value instanceof Object) {
				getObjects((JSONArray) value, arrayList);
			}
		}
		return arrayList;
	}

	private static ArrayList<String> getObjects(JSONArray value, ArrayList<String> arrayList) {
		// TODO Auto-generated method stub
		for (int i = 0; i < value.size(); i++) {
			JSONObject object = (JSONObject) value.get(i);
			arrayList = getObjects(object, arrayList);
		}
		return arrayList;
	}

	public boolean localKBsemanticBagGenerator(String resourcefile, String filename, String dumpfile) {
		// TODO Auto-generated method stub
		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);

		AllFileOperations allFileOperations = new AllFileOperations();
		ArrayList<Object> list = allFileOperations.readFromBinaryFile(resourcefile);
		
		Model model = ModelFactory.createDefaultModel();
		model.read(filename);

		for (int i = 0; i < list.size(); i++) {
			Single single = (Single) list.get(i);
			String resource = single.getString();
			
			String queryString = "SELECT ?p ?o WHERE {?s ?p ?o. " + "FILTER regex(str(?s), '" + resource + "')}";

			Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet results = ResultSetFactory.copyResults(qe.execSelect());
			qe.close();
			
			ArrayList<ModelDouble> arrayList = new ArrayList<>();

			while (results.hasNext()) {
				QuerySolution querySolution = (QuerySolution) results.next();

				RDFNode property = querySolution.get("p");
				RDFNode value = querySolution.get("o");

				String propertyString = extractStringFromUri(property);
				String valueString = extractStringFromUri(value);
				
				int count = 0;
				for (int j = 0; j < arrayList.size(); j++) {
					ModelDouble modelDouble = arrayList.get(j);
					if (valueString.trim().toLowerCase().equals
							(modelDouble.getObjectTwo().toString().trim().toLowerCase())) {
						count++;
					}
				}
				
				if (count == 0) {
					ModelDouble modelDouble = new ModelDouble();
					modelDouble.setObjectOne(propertyString);
					modelDouble.setObjectTwo(valueString);
					arrayList.add(modelDouble);
				}
			}
			
			ModelDouble double1 = new ModelDouble();
			double1.setObjectOne(resource);
			double1.setObjectTwo(arrayList);
			allFileOperations.writeToBinary(dumpfile, double1, true);
		}
		
		System.out.println("Works have been saved to " + dumpfile);
		return true;
		
		/*ArrayList<Object> objectList = allFileOperations.readFromBinaryFile(dumpfile);
		for (int i = 0; i < objectList.size(); i++) {
			ModelDouble double1 = (ModelDouble) objectList.get(i);
			System.out.println(double1.getObjectOne());
			
			ArrayList<Object> arrayList = (ArrayList<Object>) double1.getObjectTwo();
			
			for (int j = 0; j < arrayList.size(); j++) {
				System.out.println(arrayList.get(j));
			}
		}*/
	}

	private static String extractStringFromUri(Object value) {
		// TODO Auto-generated method stub
		String valueString = value.toString();
		valueString = new StringBuilder(valueString).reverse().toString();

		String regEx = "(.*?)/(.*?)$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(valueString);

		while (matcher.find()) {
			valueString = matcher.group(1);
		}
		valueString = new StringBuilder(valueString).reverse().toString();
		return valueString;
	}

	public boolean localKBResourceRetriever(String filename, int hits, String dumpfile) {
		// TODO Auto-generated method stub
		Model model = ModelFactory.createDefaultModel();
		model.read(filename);

		String queryString = "SELECT DISTINCT ?s WHERE {" + " ?s ?property ?o ." + "} LIMIT " + hits;

		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = ResultSetFactory.copyResults(qe.execSelect());
		qe.close();

		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);

		while (results.hasNext()) {
			Object object = results.next().get("s");
			Single single = new Single();
			single.setString(object.toString());

			AllFileOperations allFileOperations = new AllFileOperations();
			allFileOperations.writeToBinary(dumpfile, single, true);
			System.out.println(object);
		}

		System.out.println("Works have been saved to " + dumpfile);
		return true;
	}

	public  boolean sparqlEndResourceRetriever(String key, int hits, String dumpfile) {
		// TODO Auto-generated method stub
		ParameterizedSparqlString qs = new ParameterizedSparqlString(
				"" + "prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n" + "\n" + "select ?resource where {\n"
						+ "  ?resource rdfs:label ?label\n" + "} LIMIT " + hits);

		Literal london = ResourceFactory.createLangLiteral(key, "en");
		qs.setParam("label", london);

		QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", qs.asQuery());
		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);

		while (results.hasNext()) {
			Object object = results.next().get("resource");

			AllFileOperations allFileOperations = new AllFileOperations();
			allFileOperations.writeToBinary(dumpfile, object.toString(), true);
			System.out.println(object);
		}

		System.out.println("Works have been saved to " + dumpfile);
		return true;
	}

	public boolean dbpediaResourceRetriever(String key, int hits, String webFileName, String dumpfile) {
		// TODO Auto-generated method stub
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {
			URL url = new URL("http://lookup.dbpedia.org/api/search/KeywordSearch?QueryClass=&MaxHits=" + hits
					+ "&QueryString=" + key);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(7000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			fw = new FileWriter(webFileName);
			bw = new BufferedWriter(fw);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				bw.write(output);
			}

			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);

		JSONParser parser = new JSONParser();
		try {
			JSONObject object = (JSONObject) parser.parse(new FileReader(webFileName));
			ArrayList<Object> keyList = new ArrayList<>(object.keySet());

			for (int i = 0; i < keyList.size(); i++) {
				JSONArray jsonArray = (JSONArray) object.get(keyList.get(i));

				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(j);
					Object value = jsonObject.get("uri");

					AllFileOperations allFileOperations = new AllFileOperations();
					allFileOperations.writeToBinary(dumpfile, value.toString(), true);
					System.out.println(value);
				}
			}
			//System.out.println("Works have been saved to " + dumpfile);
			return true;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error occured");
			return false;
		}
	}

	public boolean localKBPropertyWeightGenerator(String string, String filename, String dumpfile) {
		// TODO Auto-generated method stub
		AllFileOperations operations = new AllFileOperations();
		operations.deleteFile(dumpfile);

		Model model = ModelFactory.createDefaultModel();
		model.read(filename);

		ArrayList<String> arrayList = new ArrayList<>();

		String[] strings = string.split(",");
		for (int i = 0; i < strings.length; i++) {
			System.out.println(strings[i].trim());
			arrayList.add(strings[i].trim());
		}

		for (int i = 0; i < arrayList.size(); i++) {
			String key = arrayList.get(i);

			int total = getTotal(key, filename);
			int unique = getUnique(key, filename);

			float uniqueness = 0;
			if (total != 0) {
				uniqueness = (float) unique / (float) total;
			}

			float density = 0;

			if (model.size() != 0) {
				density = (float) total / (float) model.size();
			}

			float keyness = 0;
			if (uniqueness != 0 && density != 0) {
				keyness = (2 * uniqueness * density) / (uniqueness + density);
			}

			System.out.println(key);

			System.out.println("Total uniqueness: (Total unique occurance / Occurance of the property)" + " " + unique
					+ " / " + total);
			System.out.println(
					"Total density: (Occurance of the property / Total Triples)" + " " + total + " / " + model.size());
			System.out.printf("Keyness: (2*uniqueness*density / uniqueness + density) %f\n\n", keyness);

			Weight weight = new Weight(key, keyness);

			AllFileOperations allFileOperations = new AllFileOperations();
			allFileOperations.writeToBinary(dumpfile, weight, true);
		}

		/*
		 * AllFileOperations allFileOperations = new AllFileOperations();
		 * ArrayList<Object> list = allFileOperations.readFromBinaryFile(dumpfile);
		 * 
		 * for (int i = 0; i < list.size(); i++) { Weight weight = (Weight) list.get(i);
		 * 
		 * System.out.println(weight.getObject());
		 * System.out.println(weight.getWeight()); }
		 */

		System.out.println("Works have been saved to " + dumpfile);
		return true;
	}

	private static int getUnique(String property, String filename) {
		// TODO Auto-generated method stub
		int unique = 0;
		Model model = ModelFactory.createDefaultModel();
		model.read(filename);

		String string = "SELECT DISTINCT ?o WHERE {?s ?p ?o. " + "FILTER regex(str(?p), '" + property + "')}";
		Query query = QueryFactory.create(string);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(qe.execSelect());
		qe.close();

		while (resultSet.hasNext()) {
			unique++;
			resultSet.next();
		}

		return unique;
	}

	private static int getTotal(String property, String filename) {
		// TODO Auto-generated method stub
		int total = 0;

		Model model = ModelFactory.createDefaultModel();
		model.read(filename);

		String string = "SELECT ?p ?o WHERE {?s ?p ?o. " + "FILTER regex(str(?p), '" + property + "')}";
		Query query = QueryFactory.create(string);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(qe.execSelect());

		while (resultSet.hasNext()) {
			total++;
			resultSet.next();
		}

		return total;
	}

}
