package practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import core.OnDemandETL;
import helper.Methods;

public class RunODETLQuery {
	String queryDir = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\queries";
	String resultsDir = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\results\\";
	String tempsDir = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\temps\\";
	String mapFilePath = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\map.ttl";
	String targetTBoxFilePath = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\bd_tbox.ttl";
	String targetABoxFilePath = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\target_abox.ttl";
	static String tempString = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\temp_on_demand_etl.ttl";
	String resultFilePath = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\JSON_result.json";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunODETLQuery runODETLQuery = new RunODETLQuery();
		runODETLQuery.performODETL();
	}

	private void performODETL() {
		// TODO Auto-generated method stub
		File folder = new File(queryDir);
		File[] listOfFiles = folder.listFiles();

		JSONArray jsonArray = new JSONArray();
		
		Methods methods = new Methods();
		methods.createNewFile(resultFilePath);

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				JSONParser parser = new JSONParser();
				try {
					jsonArray = (JSONArray) parser.parse(new FileReader(resultFilePath));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println("Processing: " + listOfFiles[i].getPath());
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("File: ", listOfFiles[i].getPath());

				File file = new File(listOfFiles[i].getPath());
				BufferedReader bufferedReader = null;
				try {
					bufferedReader = new BufferedReader(new FileReader(file));

					StringBuilder stringBuilder = new StringBuilder();
					String line = bufferedReader.readLine();
					
					while (line != null) {
						stringBuilder.append(line);
						stringBuilder.append("\n");
			            line = bufferedReader.readLine();
			        }
					
					String queryString = stringBuilder.toString();
					
					jsonObject.put("Query: ", queryString);
					
					runODETLQuery(queryString, jsonObject, listOfFiles[i].getName());

					jsonArray.add(jsonObject);
					
					try (FileWriter newFile = new FileWriter(resultFilePath)) {
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						JsonParser jsonParser = new JsonParser();
						JsonElement jsonElement = jsonParser.parse(jsonArray.toJSONString());
						String prettyJsonString = gson.toJson(jsonElement);
						newFile.write(prettyJsonString);
						newFile.flush();
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
					
					System.out.println("Done");
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				} finally {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				}
			}
		}
		
		System.out.println("Complete");
	}

	private void runODETLQuery(String sparqlString, JSONObject jsonObject, String fileName) {
		try {
			// TODO Auto-generated method stub
			Methods methods = new Methods();
			
			Long totalDifference = 0L;
			OnDemandETL demandETL = new OnDemandETL();
			Long startTimeLong = methods.getTime();
			
			LinkedHashMap<String, ArrayList<String>> queryLevelsArrayList = demandETL
					.extractRequiredLevels(sparqlString);
			
			/*
			 * System.out.println("Query Level"); if (queryLevelsArrayList.size() > 0) {
			 * methods.print(queryLevelsArrayList); return; }
			 */
			
			String observationString = demandETL.extractObservation(sparqlString);
			
			ArrayList<String> queryFactArrayList = demandETL.extractRequiredFacts(sparqlString, observationString);
			
			Model targetABoxModel = methods.readModelFromPath(targetABoxFilePath);
			
			LinkedHashMap<String, ArrayList<String>> requiredLevelArrayList = demandETL
					.checkRequiredLevels(targetABoxModel, queryLevelsArrayList);
			
			ArrayList<String> requiredFactArrayList = demandETL.checkRequiredFacts(targetABoxModel, queryFactArrayList);
			
			Model mapModel = methods.readModelFromPath(mapFilePath);
			Model targetTBoxModel = methods.readModelFromPath(targetTBoxFilePath);
			
			LinkedHashMap<String, String> prefixMap = Methods.extractPrefixes(mapFilePath);
			demandETL.generateFactData(demandETL.datasetString, mapModel, targetTBoxModel, prefixMap,
					requiredFactArrayList);
			
			System.out.println("Fact Generated");
			
			int numOfLevelFiles = 1;
			for (String levelString : requiredLevelArrayList.keySet()) {
				ArrayList<String> propertyList = requiredLevelArrayList.get(levelString);
				demandETL.generateLevelData(Methods.bracketString(levelString), mapModel, targetTBoxModel, prefixMap,
						propertyList, numOfLevelFiles);
				numOfLevelFiles++;
			}
			methods.createNewFile("level.ttl");
			demandETL.mergeAllFiles("level.ttl", numOfLevelFiles, true);
			
			System.out.println("Level Generated");
			
			Model factModel = methods.readModelFromPath("fact.ttl");
			Model levelModel = methods.readModelFromPath("level.ttl");
			
			Model targetModel = ModelFactory.createDefaultModel();
			targetModel.add(factModel);
			targetModel.add(levelModel);
			methods.saveModel(targetModel, tempString);
			
			Long endTimeLong = methods.getTime();
			totalDifference = endTimeLong - startTimeLong;
			
			jsonObject.put("Required Time for processing: ", methods.getTimeInSeconds(totalDifference));
			
			String fileNameString = fileName.split(".")[0];
			String tempDir = tempsDir + "\\" + fileNameString;
			System.out.println("Temp Dir: " + tempDir);
			File tempFile = new File(tempDir);
			if (tempFile.mkdir()) {
				String tempFactString = tempDir + "\\fact.ttl";
				System.out.println("Fact temp: " + tempFactString);
				methods.saveModel(factModel, tempFactString);
				
				String tempLevelString = tempDir + "\\level.ttl";
				System.out.println("Level temp: " + tempLevelString);
				methods.saveModel(levelModel, tempLevelString);
			}
			
			startTimeLong = methods.getTime();
			ResultSet resultSet = Methods.executeQuery(targetModel, sparqlString);
			endTimeLong = methods.getTime();
			totalDifference = endTimeLong - startTimeLong;
			
			jsonObject.put("Required Time to execute: ", methods.getTimeInSeconds(totalDifference));
			String resultString = ResultSetFormatter.asText(resultSet);
			String queryResultFilePath = resultsDir + fileName;
			methods.writeText(queryResultFilePath, resultString);
			jsonObject.put("Result File: ", queryResultFilePath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
