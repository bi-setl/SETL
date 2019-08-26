package helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import controller.TBoxDefinition;

public class FileMethods {
	private static final String TEMP_FILE_NAME = "temp_data.ttl";
	private String fileName, filePath, directory;

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(null);
		chooser.setDialogTitle("Choose File");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			setFilePath(chooser.getSelectedFile().getAbsolutePath());
			setFileName(chooser.getSelectedFile().getName());
		} else {
			System.out.println("No Selection ");
		}
	}

	public void chooseDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(null);
		chooser.setDialogTitle("Choose TBox Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			setDirectory(chooser.getSelectedFile().getAbsolutePath());
		} else {
			System.out.println("No Selection ");
		}
	}

	public HashMap<String, String> getAllFileTypes() {
		// TODO Auto-generated method stub
		HashMap<String, String> fileMap = new HashMap<>();
		fileMap.put("Turtle", ".ttl");
		fileMap.put("N-Triples", ".nt");
		fileMap.put("N-Quads", ".nq");
		fileMap.put("TriG", ".trig");
		fileMap.put("RDF/XML", ".rdf");
		fileMap.put("RDF/XML", ".owl");
		fileMap.put("JSON-LD", ".jsonld");
		fileMap.put("RDF Thrift", ".trdf");
		fileMap.put("RDF Thrift", ".rt");
		fileMap.put("RDF/JSON", ".rj");
		fileMap.put("TriX", ".trix");
		return fileMap;
	}
	
	public HashMap<String, String> getAllTypes() {
		// TODO Auto-generated method stub
		HashMap<String, String> fileMap = new HashMap<>();
		fileMap.put("ttl", "TTL");
		fileMap.put("nt", "NT");
		fileMap.put("nq", "N3");
		fileMap.put("trig", "TRIG");
		fileMap.put("rdf", "RDF/XML");
		fileMap.put("owl", "RDF/XML");
		fileMap.put("jsonld", "JSON-LD");
		fileMap.put("trdf", "RDFTHRFT");
		fileMap.put("rt", "RDFTHRFT");
		fileMap.put("rj", "RDF/JSON");
		fileMap.put("trix", "TRIX");
		return fileMap;
	}

	public void writeText(String filePath, String text) {
		// TODO Auto-generated method stub
		try (PrintWriter out = new PrintWriter(filePath)) {
			out.println(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public void promtToSaveFile(String data, String genre, String key, String extension) {
		// TODO Auto-generated method stub
		String filePath = new Methods().chooseSaveFile("", genre + "_version_" + System.currentTimeMillis() + extension,
				"Select Directory to save File");
		
		if (filePath != null) {
			if (filePath.trim().length() != 0) {
				if (extension.equals(".ttl")) {
					try (PrintWriter out = new PrintWriter(filePath)) {
						out.println(data);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					try (PrintWriter out = new PrintWriter(TEMP_FILE_NAME)) {
						out.println(data);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					try {
						Model model = ModelFactory.createDefaultModel();
						model.read(TEMP_FILE_NAME);
						OutputStream outputStream = new FileOutputStream(filePath);
						model.write(outputStream, key);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	public Model writeNReadNTurtle(String filePath) {
		Model model = ModelFactory.createDefaultModel();
		model.read(filePath);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(TEMP_FILE_NAME);
			model.write(outputStream, "TTL");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	public void createFileInDirectory() {
		// TODO Auto-generated method stub
		File file = new File(directory, fileName);
		try {
			file.createNewFile();
			String data = new TBoxDefinition().getPrefixStrings();
			try (PrintWriter out = new PrintWriter(filePath)) {
				out.println(data);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void createNewFile(String fileName) {
		// TODO Auto-generated method stub
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public ArrayList<String> sortArrayList(ArrayList<String> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			for (int j = 0; j < arrayList.size(); j++) {
				if (arrayList.get(i).compareTo(arrayList.get(j)) < 0) {
					String temp = arrayList.get(i);
					arrayList.set(i, arrayList.get(j));
					arrayList.set(j, temp);
				}
			}
		}
		return arrayList;
	}

	public boolean checkFile(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		return file.exists();
	}

	public Model readModelFromPath(String filePath) {
		// TODO Auto-generated method stub
		Model model = ModelFactory.createDefaultModel();
		try {
			model.read(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return model;
	}

	public String getFileExtension(String filePath) {
		// TODO Auto-generated method stub
		String[] parts = filePath.split("\\.");
		if (parts.length == 2) {
			return parts[1];
		} else {
			return null;
		}
	}

	public InputStream getEncodedInputStream(String filePath) {
		// TODO Auto-generated method stub
		try {
			String fileString = new String(Files.readAllBytes(Paths.get(filePath)),
					StandardCharsets.UTF_8);
			InputStream targetStream = new ByteArrayInputStream(fileString.getBytes());
			return targetStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public String getEncodedString(String filePath) {
		// TODO Auto-generated method stub
		try {
			String fileString = new String(Files.readAllBytes(Paths.get(filePath)),
					StandardCharsets.UTF_8);
			return fileString;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}

	public String saveModel(Model model, String filePath) {
		// TODO Auto-generated method stub
		try {
			OutputStream outputStream = new FileOutputStream(filePath);
			String[] parts = filePath.split("\\.");
			model.write(outputStream, parts[parts.length - 1].toUpperCase());
			
			return "Success.\nFile Saved: " + filePath;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return "File save error.";
		}
	}

	public void printModel(Model model) {
		// TODO Auto-generated method stub
		model.write(System.out, "TTL");
	}

	public String modelToString(Model model, String extension) {
		// TODO Auto-generated method stub
		StringWriter out = new StringWriter();
		model.write(out, extension.toUpperCase());
		return out.toString();
	}

	public void appendToFile(String string, String csvTarget) {
		// TODO Auto-generated method stub
		try {
			FileWriter fileWriter = new FileWriter(csvTarget, true);
			fileWriter.write(string);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public ArrayList<String> getColumnNames(String filePath, String usedDelimiter) {
		// TODO Auto-generated method stub
		ArrayList<String> keys = new ArrayList<>();
		
		String fileExtension = getFileExtension(filePath);
		if (fileExtension != null && fileExtension.equals("csv")) {
			String inputStream = getEncodedString(filePath);
			
			if (inputStream != null) {
				BufferedReader bufferedReader = null;
				try {
					String delimiter = ",";
					if (usedDelimiter.contains("Space") || usedDelimiter.contains("Tab")) {
						delimiter = "\\s";
					} else if (usedDelimiter.contains("Semicolon")) {
						delimiter = ";";
					} else if (usedDelimiter.contains("Pipe")) {
						delimiter = "|";
					} else {
						delimiter = ",";
					}
					
					Reader inputString = new StringReader(inputStream);
					bufferedReader = new BufferedReader(inputString);
					String string = "";

					while ((string = bufferedReader.readLine()) != null) {
						String[] parts = string.split(delimiter);
						for (String part : parts) {
							keys.add(part);
						}
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
			} else {
				System.out.println("Encoding Error");
			}
		}
		return keys;
	}

	public ArrayList getColumnNames(String filePath) {
		// TODO Auto-generated method stub
		ArrayList<String> keys = new ArrayList<>();
		String fileExtension = getFileExtension(filePath);

		if (fileExtension != null && (fileExtension.equals("xls") || fileExtension.equals("xlsx")
				|| fileExtension.equals("xlsm") || fileExtension.equals("xlsb"))) {
			try {
				FileInputStream fileInStream = new FileInputStream(filePath);
				
				XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
				for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
					XSSFSheet sheet = workBook.getSheetAt(i);
					XSSFRow xssfRow = sheet.getRow(0);
					for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
						keys.add(xssfRow.getCell(j).toString());
					}
				}
				workBook.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("Invalid File Type");
		}
		return keys;
	}

	public String encodeString(String provValue) {
		// TODO Auto-generated method stub
		try {
			return URLEncoder.encode(provValue, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return provValue;
		}
	}
	
	private LinkedHashMap<String, String> extractAllPrefixes(String filePath) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> hashedMap = new LinkedHashMap<>();
		File file = new File(filePath);
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

	public void printHashMap(LinkedHashMap<String, String> prefixMap) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    System.out.println(key + "\t" + value);
		}
	}
	
	public void printHashMapArrayList(LinkedHashMap<String, ArrayList<String>> prefixMap) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList<String>> entry : prefixMap.entrySet()) {
		    String key = entry.getKey();
		    ArrayList<String> value = entry.getValue();
		    
		    for (String string : value) {
		    	System.out.println(key + "\t" + string);
			}
		}
	}

	public void showDialog(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, message);
	}

	public void printResultSet(ResultSet resultSet) {
		// TODO Auto-generated method stub
		ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));
	}

	public void promtToSaveFile(String data, String key, String filePath) {
		// TODO Auto-generated method stub
		if (filePath != null) {
			if (filePath.trim().length() != 0) {
				if (filePath.contains(".ttl")) {
					try (PrintWriter out = new PrintWriter(filePath)) {
						out.println(data);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					
					try (PrintWriter out = new PrintWriter(TEMP_FILE_NAME)) {
						out.println(data);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					try {
						Model model = ModelFactory.createDefaultModel();
						model.read(TEMP_FILE_NAME);
						OutputStream outputStream = new FileOutputStream(filePath);
						model.write(outputStream, key);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}
	
	public String getPrefixStrings(LinkedHashMap<String, String> prefixesMap) {

		String tripleString = "";
		Set<String> prefixes = prefixesMap.keySet();
		Iterator iterator = prefixes.iterator();

		while (iterator.hasNext()) {

			String prefix = (String) iterator.next();
			String iri = (String) prefixesMap.get(prefix);

			tripleString += "@prefix " + prefix + " <" + iri + ">.\n";
		}
		
		tripleString += "\n";

		return tripleString;
	}

	public String convertToString(ArrayList<String> arrayList) {
		// TODO Auto-generated method stub
		String text = "";
		
		for (int i = 0; i < arrayList.size(); i++) {
			text += arrayList.get(i);
			
			if (i != arrayList.size() - 1) {
				text += ", ";
			}
		}
		
		return text;
	}

	public void printHashMapComplex(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> filterProperties) {
		// TODO Auto-generated method stub
		for (String string : filterProperties.keySet()) {
			System.out.println("Filter Level: " + string);
			LinkedHashMap<String, ArrayList<String>> filterMap = filterProperties.get(string);
			
			for (String string2 : filterMap.keySet()) {
				System.out.println("\tFilter Property: " + string2);
				ArrayList<String> arrayList = filterMap.get(string2);
				
				for (String string3 : arrayList) {
					System.out.println("\t\tFilter Value: " + string3);
				}
			}
		}
	}
}
