package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import helper.Methods;
import helper.Variables;

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

	private String extractCSV(String filePath, String delimiter) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		String line = "";

		int count = 0;
		try {
			br = new BufferedReader(new FileReader(filePath));

			line = br.readLine();
			line = line + delimiter;

			ArrayList<String> columnNames = parseCSVLine(line, delimiter, true);
			System.out.println("Total Columns: " + columnNames.size());

			ArrayList<Integer> faultList = new ArrayList<Integer>();
			
			while ((line = br.readLine()) != null) {
//				line = br.readLine();
//				System.out.println(line);
				
				ArrayList<String> columnValues = parseCSVLine(line, delimiter, false);
				
				if (line != null) {
					if (columnNames.size() != columnValues.size()) {
						// System.out.println("Skipping Line: " + count + " - " + line);
						// System.out.println("Found Columns: " + columnValues.size());
						
						faultList.add(count);
					}
					
//					if (count == 2) {
//						ArrayList<String> columnValues = parseCSVLine(line, delimiter);
//						
//						if (columnNames.size() != columnValues.size()) {
//							System.out.println("Skipping Line: " + count + " - " + line);
//							System.out.println("Found Columns: " + columnValues.size());
//							return "";
//						} else {
//							System.out.println("Matches");
//						}
//					}
				} else {
					break;
				}
				
				count++;
			}
			
			System.out.println("Total fault: " + faultList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Variables.ERROR_READING_FILE;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Variables.ERROR_READING_FILE;
			}
		}

		return "";
	}

	public ArrayList<String> parseCSVLine(String line, String delimiter, boolean isKey) {
		// isKey will be used to check if any column has space
		// so that it can be replaced.
		
		ArrayList<String> nameList = new ArrayList<String>();
		String[] parts = line.split(delimiter);

		ArrayList<String> tempList = new ArrayList<String>();
		
		for (String part : parts) {
//			System.out.println("Part: " + part);
			
			part = Methods.encodeString(part);
			
			if (part.length() == 1 && part.equals("\"")) {
				String nameString = getTempString(delimiter, tempList, part);

				tempList = new ArrayList<String>();
				nameList.add(Methods.replaceQuote(nameString));
			} else {
				if (part.startsWith("\"") && !part.endsWith("\"")) {
//					System.out.println("CASE 1");
					tempList.add(part);
				} else if (!part.startsWith("\"") && part.endsWith("\"")) {
//					System.out.println("CASE 2");
					String nameString = getTempString(delimiter, tempList, part);

					tempList = new ArrayList<String>();
					nameList.add(Methods.replaceQuote(nameString));
				} else {
//					System.out.println("CASE 3");
					
					if (tempList.size() > 0) {
						tempList.add(Methods.replaceQuote(part));
					} else {
						nameList.add(Methods.replaceQuote(part));
					}
				}
			}
		}
		
//		System.out.println("All Column Values");
//		
//		for (String string : nameList) {
//			System.out.println(string);
//		}

		return nameList;
	}

	private String getTempString(String delimiter, ArrayList<String> tempList, String part) {
		String nameString = "";

		for (String name : tempList) {
//			System.out.println("Temp Variable: " + name);
			if (nameString.isEmpty()) {
				nameString += name;
			} else {
				nameString += delimiter + name;
			}
		}

		nameString += delimiter + part;
		
//		System.out.println("Temp Result: " + nameString);
		return nameString;
	}
}
