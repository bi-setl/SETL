package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import core.EquationHandler;
import core.IRIGenerator;
import helper.FileMethods;
import helper.Methods;

public class CSVABox {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * CSVABox csvaBox = new CSVABox(); String resultString =
		 * csvaBox.parseCSV("C:\\Users\\Amrit\\Documents\\Census\\Census04.csv",
		 * "http://linked-statistics-bd.org/2011/data", "Space ( )", "Expression",
		 * "CONCAT(admUnitFiveId,residence,ageGroup)", "TTL",
		 * "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\191022_102457_TargetABox.ttl");
		 * 
		 * System.out.println(resultString);
		 */
	}

	public String parseCSV(String sourceFilePath, String prefixString, String delimiterString, String keyTypeString, String keyAttribute, String targetType, String targetFilePath) {
		FileMethods fileMethods = new FileMethods();
		Model model = ModelFactory.createDefaultModel();
		String inputStream = fileMethods.getEncodedString(sourceFilePath);
		
		ArrayList<String> allColumnNames = new ArrayList<>();
		BufferedReader bufferedReader = null;
		
		String delimiter = ",";
		if (delimiterString.contains("Space") || delimiterString.contains("Tab")) {
			delimiter = "\\s";
		} else if (delimiterString.contains("Semicolon")) {
			delimiter = ";";
		} else if (delimiterString.contains("Pipe")) {
			delimiter = "|";
		} else {
			delimiter = ",";
		}
		
		Reader inputString = new StringReader(inputStream);
		bufferedReader = new BufferedReader(inputString);
		String text = "";
		
		if (prefixString.endsWith("/")) {
			prefixString = prefixString.substring(0, prefixString.length() - 1);
		}
		
		try {
			while ((text = bufferedReader.readLine()) != null) {
				String[] parts = text.split(delimiter);
				for (String part : parts) {
					part = part.replace("\"", "");
					allColumnNames.add(part);
				}
				break;
			}
			
			String sourceTypeString = getType(sourceFilePath);
			
			int count = 0, numOfFiles = 1;
			while ((text = bufferedReader.readLine()) != null) {
				count++;
				
				String[] segments = text.split(delimiter);
				
				if (segments.length == allColumnNames.size()) {
					LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
					
					for (int i = 0; i < allColumnNames.size(); i++) {
						String keyString = allColumnNames.get(i);
						String valueString = segments[i];
						
						valueMap.put(keyString, valueString);
					}
					
					String typeString = prefixString + "#" + sourceTypeString;
					
					String resourceKeyString = prefixString + "/" + sourceTypeString + "/" + solveExpression(keyAttribute, valueMap);
					Resource resource = model.createResource(resourceKeyString);
					Resource classResource = model.createResource(typeString);
					resource.addProperty(RDF.type, classResource);
					
					for (String keyString : valueMap.keySet()) {
						Property property = model.createProperty(prefixString + "#" + keyString);
						Literal literal = model.createTypedLiteral(valueMap.get(keyString));
						
						resource.addProperty(property, literal);
					}
				} else {
					System.out.println("Skipping: " + text);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return "Can't extract columns";
		}
		
		if (fileMethods.checkFile(targetFilePath)) {	
			Model sourceModel = fileMethods.readModelFromPath(targetFilePath);
			model = sourceModel.add(model);
		} else {
			fileMethods.createNewFile(targetFilePath);
		}
		
		fileMethods.saveModel(model, targetFilePath);
		
		return "Done";
	}
	
	private String solveExpression(String keyAttribute, LinkedHashMap<String, String> valueMap) {
		// TODO Auto-generated method stub
		keyAttribute = keyAttribute.replace("CONCAT", "");
		keyAttribute = keyAttribute.substring(1, keyAttribute.length() - 1);
		
		String[] partStrings = keyAttribute.split(",");
		
		String resourceKeyString = "";
		
		for (String part : partStrings) {
			String valueString = valueMap.get(part.trim());
			resourceKeyString += valueString;
		}
		return resourceKeyString;
	}

	private String getType(String csvSource) {
		// TODO Auto-generated method stub
		if (csvSource.contains("\\")) {
				String[] portions = csvSource.split("\\\\");
				String[] segments = portions[portions.length - 1].split("\\.");
				String type = segments[0].toLowerCase();
				return type;
		} else {
			String[] portions = csvSource.split("/");
			String[] segments = portions[portions.length - 1].split("\\.");
			String type = segments[0].toLowerCase();
			return type;
		}
	}
}
