package core;

import java.util.ArrayList;

import helper.Methods;

public class CSVExtraction {
	// private static String filePathString = "C:\\Users\\Amrit\\Documents\\UpdateConstruct\\temp\\temp_city.csv";
	private static String filePathString = "C:\\Users\\Amrit\\Documents\\UpdateConstruct\\recipient.csv";
	private static String delimiterString = "Comma";

//	public static void main(String[] args) {
//		Methods.startProcessingTime();
//		
//		
//		CSVExtraction csvExtraction = new CSVExtraction();
//		csvExtraction.extractCSV(filePathString, Methods.getCSVDelimiter(delimiterString));
//		
//		Methods.endProcessingTime();
//	}

	public ArrayList<String> parseCSVLine(String line, String delimiter, boolean isKey) {
		// isKey will be used to check if any column has space
		// so that it can be replaced.
		
		ArrayList<String> nameList = new ArrayList<>();
		String[] parts = line.split(delimiter);

		ArrayList<String> tempList = new ArrayList<>();
		
		for (String part : parts) {
			part = Methods.encodeString(part);
			
			if (part.length() == 1 && part.equals("\"")) {
				String nameString = getTempString(delimiter, tempList, part);

				tempList = new ArrayList<>();
				nameList.add(Methods.replaceQuote(nameString));
			} else {
				if (part.startsWith("\"") && !part.endsWith("\"")) {
					tempList.add(part);
				} else if (!part.startsWith("\"") && part.endsWith("\"")) {
					String nameString = getTempString(delimiter, tempList, part);

					tempList = new ArrayList<>();
					nameList.add(Methods.replaceQuote(nameString));
				} else {
					
					if (tempList.size() > 0) {
						tempList.add(Methods.replaceQuote(part));
					} else {
						nameList.add(Methods.replaceQuote(part));
					}
				}
			}
		}

		return nameList;
	}

	private String getTempString(String delimiter, ArrayList<String> tempList, String part) {
		String nameString = "";

		StringBuilder stringBuilder = new StringBuilder(nameString);
		for (String name : tempList) {

			if (stringBuilder.toString().isEmpty()) {
				stringBuilder.append(name);
			} else {
				String value = delimiter + name;
				stringBuilder.append(value);
			}
		}

		String value = delimiter + part;
		stringBuilder.append(value);

		return stringBuilder.toString();
	}
}
