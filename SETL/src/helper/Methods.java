package helper;

import java.awt.Color;
import java.awt.Component;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.miginfocom.swing.MigLayout;

public class Methods {
	private LinkedHashMap<String, String> prefixMap;
	
	public boolean checkString(String string) {
		if (string == null) {
			return false;
		} else if (string.length() == 0) {
			return false;
		} else
			return true;
	}
	
	public void showDialog(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
	
	public void printComplexHashMap(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap) {
		for (String key : linkedHashMap.keySet()) {
			System.out.println("Parent Key: " + key);
			LinkedHashMap<String, ArrayList<String>> hashMap = linkedHashMap.get(key);
			
			for (String secondKey : hashMap.keySet()) {
				System.out.println("Child Key: " + secondKey);
				
				ArrayList<String> childList = hashMap.get(secondKey);
				for (String value : childList) {
					System.out.println(value);
				}
			}
		}
	}
	
	public void printResultSet(ResultSet resultSet) {
		ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));
	}

	private LinkedHashMap<String, ArrayList<String>> addNewArrayListValue(
			LinkedHashMap<String, ArrayList<String>> hashMap, String property, String value) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(value);
		hashMap.put(property, arrayList);
		return hashMap;
	}

	private LinkedHashMap<String, ArrayList<String>> augmentArrayListValue(
			LinkedHashMap<String, ArrayList<String>> hashMap, String property, String value) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = hashMap.get(property);
		if (!arrayList.contains(value)) {
			arrayList.add(value);
			hashMap.put(property, arrayList);
		}
		return hashMap;
	}

	public LinkedHashMap<String, ArrayList<String>> addToComplexHashMap(
			LinkedHashMap<String, ArrayList<String>> hashMap, String property, String value) {
		// TODO Auto-generated method stub
		if (hashMap == null) {
			hashMap = new LinkedHashMap<String, ArrayList<String>>();
		}
		
		if (hashMap.containsKey(property)) {
			return augmentArrayListValue(hashMap, property, value);
		} else {
			return addNewArrayListValue(hashMap, property, value);
		}
	}

	public String convertArrayListToString(ArrayList<String> arrayList) {
		// TODO Auto-generated method stub
		String string = "";
		if (arrayList != null) {
			for (int i = 0; i < arrayList.size(); i++) {
				string += arrayList.get(i);
				
				if (i < arrayList.size() - 1) {
					string += ", ";
				}
			}
		}
		return string;
	}

	public Object getRDFNodeValue(RDFNode node) {
		// TODO Auto-generated method stub
		
		try {
			if (node.isLiteral()) {
				return node.asLiteral().getValue();
			} else {
				return node.asResource().toString();
			} 
		} catch (Exception e) {
			// TODO: handle exception
			return node.toString();
		}
	}
	
	public JDialog getProgressDialog(Component component) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		// add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[grow]", "[]"));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel.add(progressBar, "cell 0 0,grow");
		
		JDialog dialog = new JDialog();
		dialog.getContentPane().add(panel);
		dialog.setResizable(false);
        dialog.pack();
        dialog.setTitle("Task on progress...");
        dialog.setSize(500, dialog.getHeight());
        dialog.setLocationRelativeTo(component);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
		
		return dialog;
	}
	
	public boolean writeText(String filePath, String text) {
		// TODO Auto-generated method stub
		try (PrintWriter out = new PrintWriter(filePath)) {
			out.println(text);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}

	public void printArrayListHashMap(LinkedHashMap<String, ArrayList<String>> hashMap) {
		// TODO Auto-generated method stub
		for (String key : hashMap.keySet()) {
			System.out.println("Key: " + key);
			ArrayList<String> arrayList = hashMap.get(key);
			
			for (String string : arrayList) {
				System.out.println(string);
			}
		}
	}
	
	public String getLastSegment(String urlName) {
		if (urlName.contains("www") || urlName.contains("http")) {
			if (urlName.contains("#")) {
				String[] parts = urlName.split("#");
				return parts[1];
			} else {
				String[] parts = urlName.split("/");
				return parts[parts.length - 1];
			}
		} else {
			String[] parts = urlName.split(":");
			return parts[1];
		}
	}
	
	public String chooseFile(String message) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(null);
		chooser.setDialogTitle(message);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}

	public void printModel(Model model) {
		// TODO Auto-generated method stub
		model.write(System.out, "TTL");
	}

	public String convertArrayListToString(List<String> list) {
		// TODO Auto-generated method stub
		String string = "";
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				string += list.get(i);
				
				if (i < list.size() - 1) {
					string += ", ";
				}
			}
		}
		return string;
	}

	public boolean checkStrings(String...strings) {
		// TODO Auto-generated method stub
		for (String string : strings) {
			if (checkString(string) == false) {
				return false;
			}
		}
		return true;
	}
	
	public String chooseSaveFile(String path, String defaultName, String message) {

		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setDialogTitle(message);
		jFileChooser.setFileSelectionMode(jFileChooser.FILES_ONLY);
		jFileChooser.setSelectedFile(new File(path + defaultName));

		int retVal = jFileChooser.showSaveDialog(null);

		if (retVal == JFileChooser.APPROVE_OPTION) {
			return jFileChooser.getSelectedFile().getPath();
		} else {
			return "";
		}

	}

	public String getCurrentTime() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime().toString();
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
	
	public LinkedHashMap<String, String> getAllPredefinedPrefixes() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		hashMap.put("owl:", "http://www.w3.org/2002/07/owl#");
		hashMap.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		hashMap.put("skos:", "http://www.w3.org/2004/02/skos/core#");
		hashMap.put("qb:", "http://purl.org/linked-data/cube#");
		hashMap.put("xsd:", "http://www.w3.org/2001/XMLSchema#");
		hashMap.put("qb4o:", "http://purl.org/qb4olap/cubes#");
		hashMap.put("dct:", "http://purl.org/dc/terms/");
		hashMap.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
		hashMap.put("wgs:", "http://www.w3c.org/2003/01/geo/wgs84_pos#");
		hashMap.put("xml:", "http://www.w3.org/XML/1998/namespace");
		hashMap.put("aowl:", "http://bblfish.net/work/atom-owl/2006-06-06/");
		hashMap.put("foaf:", "http://xmlns.com/foaf/0.1/");
		hashMap.put("time:", "http://www.w3.org/2006/time#");
		hashMap.put("dbpcat:", "http://dbpedia.org/resource/Category:");
		hashMap.put("dbpedia:", "http://dbpedia.org/resource/");
		hashMap.put("virtrdf:", "http://www.openlinksw.com/schemas/virtrdf#");
		hashMap.put("geonames:", "http://www.geonames.org/ontology#");

		return hashMap;
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
	
	public boolean hasEmptyString(ArrayList<String> stringList) {

		for (String string : stringList) {

			if (string.equals(""))
				return true;
		}
		return false;
	}
	
	public String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd_hhmmss");
		Date date = new Date();
		String dateTime = dateFormat.format(date);
		return dateTime;

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
	
	public boolean isIntParseable(String numString) {
		try {
			Integer.parseInt(numString);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	public int getInt(String numString) {
		return Integer.parseInt(numString);
	}public ArrayList<String> getColumnNames(String filePath, String usedDelimiter) {
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

	public boolean isDoubleParseable(String numString) {
		try {
			Double.parseDouble(numString);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public double getDouble(String numString) {
		return Double.parseDouble(numString);
	}

	public String chooseDirectory(String message) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(null);
		chooser.setDialogTitle(message);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			return "No Selection ";
		}
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

	public boolean checkFile(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		return file.exists();
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
	
	public void extractAllPrefixes(String filepath) {
		prefixMap = new LinkedHashMap<>();
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

				prefixMap.put(prefix, iri);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
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
	
	public static String extractKeyWordFromIRI(String expression) {
		// TODO Auto-generated method stub
		expression = expression.replaceAll("[^\\w:\\/.#,]", "");
		expression = expression.replace("_", "");
		expression = expression.replace("CONCAT", "");
		expression = expression.replace("CONTAINS", "");
		expression = expression.replace("SPLIT", "");
		expression = expression.replace("REPLACE", "");
		expression = expression.replace("ToNumber", "");
		expression = expression.replace("ToString", "");
		expression = expression.replace("COMPARE", "");
		
		String parts[] = expression.split(",");
		String key = "";
		
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].length() > 0) {
				key += getLastSegmentOfIRI(parts[i]);
				
				if (i < parts.length - 1) {
					key += "_";
				}
			}
		}
		
		return key;
	}
	
	public static String getLastSegmentOfIRI(String urlName) {
		if (urlName.contains("www") || urlName.contains("http")) {
			if (urlName.contains("#")) {
				String[] parts = urlName.split("#");
				return parts[1];
			} else {
				String[] parts = urlName.split("/");
				return parts[parts.length - 1];
			}
		} else {
			String[] parts = urlName.split(":");
			return parts[1];
		}
	}
}
