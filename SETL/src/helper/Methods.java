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

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import helper.Variables;
import net.miginfocom.swing.MigLayout;

public class Methods {
	private LinkedHashMap<String, String> prefixMap;
    static Long totalDifference = 0L;
    static Long startTimeLong = 0L;
    static Long endTimeLong = 0L;

    public boolean checkString(String string) {
        if (string == null) {
            return false;
        } else return string.length() != 0;
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
            hashMap = new LinkedHashMap<>();
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
        StringBuilder stringBuilder = new StringBuilder(string);

        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                stringBuilder.append(arrayList.get(i));

                if (i < arrayList.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        return stringBuilder.toString();
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
        StringBuilder stringBuilder = new StringBuilder(string);

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i));

                if (i < list.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        return stringBuilder.toString();
    }

    public boolean checkStrings(String...strings) {
        // TODO Auto-generated method stub
        for (String string : strings) {
            if (!checkString(string)) {
                return false;
            }
        }
        return true;
    }

    public String chooseSaveFile(String path, String defaultName, String message) {

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setDialogTitle(message);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
        StringBuilder stringBuilder = new StringBuilder(tripleString);

        Set<String> prefixes = getAllPredefinedPrefixes().keySet();

        for (String prefix : prefixes) {

            String iri = getAllPredefinedPrefixes().get(prefix);

            String value = "@prefix " + prefix + " <" + iri + ">.\n";
            stringBuilder.append(value);
        }

        return stringBuilder.toString();
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
        hashMap.put("map:", "http://www.map.org/example#");
        hashMap.put("onto:", "http://www.onto.org/schema#");

        return hashMap;
    }

    public HashMap<String, String> getAllFileTypes() {
        // TODO Auto-generated method stub
        HashMap<String, String> fileMap = new HashMap<>();
        fileMap.put("Turtle", ".ttl");
        fileMap.put("N-Triples", ".nt");
        fileMap.put("N-Quads", ".nq");
        fileMap.put("TriG", ".trig");
        fileMap.put("RDF/XML", ".owl");
        fileMap.put("JSON-LD", ".jsonld");
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
        return dateFormat.format(date);

    }

    public static String getFileExtension(String filePath) {
        // TODO Auto-generated method stub
        String[] parts = filePath.split("\\.");
        if (parts.length == 2) {
            return parts[1];
        } else {
            return "";
        }
    }

    public InputStream getEncodedInputStream(String filePath) {
        // TODO Auto-generated method stub
        try {
        	String fileString = new String(Files.readAllBytes(Paths.get(filePath)),
					StandardCharsets.ISO_8859_1);
            return new ByteArrayInputStream(fileString.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
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

                    if ((string = bufferedReader.readLine()) != null) {
                    	String[] parts = string.split(delimiter);
						for (String part : parts) {
							keys.add(part);
						}
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

    public static Model readModelFromPath(String filePath) {
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

    public static void createNewFile(String fileName) {
        // TODO Auto-generated method stub
        File file = new File(fileName);
        try {
            boolean s1 = file.delete();
            boolean s2 = file.createNewFile();

            if (s1)
                print("File deleted");

            if (s2)
                print("File created");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static String modelToString(Model model, String extension) {
        // TODO Auto-generated method stub
        StringWriter out = new StringWriter();
        model.write(out, extension.toUpperCase());
        return out.toString();
    }

    public static void appendToFile(String string, String csvTarget) {
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

    public void extractAllPrefixes(String filepath) {
        prefixMap = new LinkedHashMap<>();
        File file = new File(filepath);
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String text = "", s;

            StringBuilder stringBuilder = new StringBuilder(text);
            while ((s = bufferedReader.readLine()) != null) {
                text = text + s;
            }

            String regEx = "(@prefix\\s+)([^:.]*:\\s+)(<)([^>]*)(>)";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                String prefix = matcher.group(2).trim();
                String iri = matcher.group(4).trim();

                prefixMap.put(prefix, iri);
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
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
        expression = expression.replaceAll("[^\\w:/.#,]", "");
        expression = expression.replace("_", "");
        expression = expression.replace("CONCAT", "");
        expression = expression.replace("CONTAINS", "");
        expression = expression.replace("SPLIT", "");
        expression = expression.replace("REPLACE", "");
        expression = expression.replace("ToNumber", "");
        expression = expression.replace("ToString", "");
        expression = expression.replace("COMPARE", "");

        String[] parts = expression.split(",");
        String key = "";

        StringBuilder stringBuilder = new StringBuilder(key);

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 0) {
                stringBuilder.append(getLastSegmentOfIRI(parts[i]));

                if (i < parts.length - 1) {
                    stringBuilder.append("_");
                }
            }
        }

        return stringBuilder.toString();
    }

    public static String getLastNameOfFilePath(String filePath) {
        String[] parts = filePath.split("\\\\");
        String name = parts[parts.length - 1];
        String[] segments = name.split("\\.");
        return segments[0];
    }

    public static String assignPrefix(LinkedHashMap<String, String> prefixMap, String iriValue) {
        if (iriValue.contains("#")) {
            String[] parts = iriValue.split("#");
            if (parts.length == 2) {
                String firstSegment = parts[0].trim() + "#";
                String prefix = getMapValue(prefixMap, firstSegment);

                if (prefix != null) {
                    return prefix + parts[1].trim();
                }
            }
        } else {
            String[] parts = iriValue.split("/");
            String lastSegment = parts[parts.length - 1];
            String firstSegment = iriValue.replace(lastSegment, "");

            String prefix = getMapValue(prefixMap, firstSegment);

            if (prefix != null) {
                return prefix + lastSegment;
            }
        }
        return iriValue;
    }

    private static String getMapValue(LinkedHashMap<String, String> prefixMap, String secondValue) {
        // TODO Auto-generated method stub
        if (prefixMap.containsValue(secondValue)) {
            for (Map.Entry<String, String> map : prefixMap.entrySet()) {
                String key = map.getKey();
                String value = map.getValue();

                if (secondValue.equals(value.trim())) {
                    return key;
                }
            }
        }
        return null;
    }

    public static String assignIRI(LinkedHashMap<String, String> prefixMap, String prefixValue) {
        if (prefixValue.contains("http") || prefixValue.contains("www")) {
            return prefixValue;
        } else {
            String[] segments = prefixValue.split(":");
            if (segments.length == 2) {
                String firstSegment = segments[0].trim() + ":";
                return prefixMap.get(firstSegment) + segments[1];
            } else {
                return prefixValue;
            }
        }
    }

    public static String getPrefixStrings(LinkedHashMap<String, String> prefixMap) {
        String tripleString = "";
        Set<String> prefixes = prefixMap.keySet();
        Iterator<String> iterator = prefixes.iterator();

        StringBuilder stringBuilder = new StringBuilder(tripleString);
        while (iterator.hasNext()) {

            String prefix = iterator.next();
            String iri = prefixMap.get(prefix);

            String value = "@prefix " + prefix + " <" + iri + ">.\n";
            stringBuilder.append(value);
        }

        return stringBuilder.toString();
    }

    public static LinkedHashMap<String, String> extractPrefixes(String filePath) {
        // TODO Auto-generated method stub
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        File file = new File(filePath);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String textString = "", s;
            StringBuilder stringBuilder = new StringBuilder(textString);
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }

            String regEx = "(@prefix\\s*)([^:]+:)(\\s+)(<)([^\\s+^>]+)";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(stringBuilder.toString());

            while (matcher.find()) {
                String prefix = matcher.group(2).trim();
                String iri = matcher.group(5).trim();

                hashMap.put(prefix, iri);
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return hashMap;
    }

    public static boolean isIRI(String keyAttribute) {
        // TODO Auto-generated method stub
        if (keyAttribute.contains("http") || keyAttribute.contains("www")) {
            return true;
        } else return keyAttribute.contains(":");
    }

    public static void printTime() {
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());
    }

    public Object[][] runSparqlQuery(Model model, String sparql, ArrayList<String> selectedColumns) {
        // TODO Auto-generated method stub
        ArrayList<Object> valueList = new ArrayList<>();

        ResultSet resultSet = executeQuery(model, sparql);

        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();

            ArrayList<Object> arrayList = new ArrayList<>();
            for (String keyString : selectedColumns) {
                if (querySolution.get(keyString.substring(1)) == null) {
                    arrayList.add("");
                } else {
                    RDFNode node = querySolution.get(keyString.substring(1));
                    arrayList.add(getRDFNodeValue(node));
                }
            }

            valueList.add(arrayList.toArray());
        }

        return valueList.toArray(new Object[valueList.size()][selectedColumns.size()]);
    }

    public static ArrayList<String> getAllUpdateTypes() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Type1");
        list.add("Type2");
        list.add("Type3");
        list.add("Attributal Update");

        return list;
    }

    public static String getCSVDelimiter(String csvDelimiter) {
        String delimiter = ",";

        if (csvDelimiter.contains("Space") || csvDelimiter.contains("Tab")) {
            delimiter = "\\s";
        } else if (csvDelimiter.contains("Semicolon")) {
            delimiter = ";";
        } else if (csvDelimiter.contains("Pipe")) {
            delimiter = "|";
        } else {
            delimiter = ",";
        }

        return delimiter;
    }

    public static String formatURL(String urlString) {
        urlString = urlString.replace("\"", "");

        return urlString.replaceAll("[^a-zA-Z0-9/]" , "-");
    }

    public static String replaceQuote(String urlString) {
        urlString = urlString.replace("\"", "");

        return urlString;
    }

    public static String validatePrefix(String prefix) {
        // TODO Auto-generated method stub
        if (!prefix.endsWith("/") && !prefix.endsWith("#")) {
            prefix += "/";
        }

        return prefix;
    }

    public static String validateIRI(String iriString) {
        // I have to validate the IRI with regex
        return iriString;
    }

    public static boolean checkToSaveModel(int count, int numOfFiles, Model model) {
        // TODO Auto-generated method stub
        if (count % Variables.SAVE_LIMIT == 0) {
            String tempPath = Variables.TEMP_DIR + numOfFiles + ".ttl";

            if (!saveTempModel(model, tempPath)) {
                System.out.println("Couldn't save this temp model");
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean saveTempModel(Model model, String filePath) {
        // TODO Auto-generated method stub
        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            model.write(outputStream, "TTL");

            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static void checkToSaveModel(int numOfFiles, Model model) {
        // TODO Auto-generated method stub
        String tempPath = Variables.TEMP_DIR + numOfFiles + ".ttl";

        if (!saveTempModel(model, tempPath)) {
            System.out.println("Couldn't save this temp model");
        }
    }

    public static String mergeAllTempFiles(int numOfFiles, String resultFile) {
        // TODO Auto-generated method stub
        createNewFile(resultFile);

        for (int i = 1; i <= numOfFiles; i++) {
            String filePath = Variables.TEMP_DIR + i + ".ttl";

            try {
//				System.out.println(filePath);

                Model model = readModelFromPath(filePath);
                if (model != null) {
                    String string = modelToString(model, getFileExtension(resultFile));
                    appendToFile(string, resultFile);

                    File file = new File(filePath);
                    boolean status = file.delete();

                    if (status) {
                        print("File deleted.");
                    }
                }
                // System.out.println(filePath + " deleted");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Error in merging file. Check file: " + filePath);
            }
        }
        return "Success.\nFile Saved: " + resultFile;
    }

    public static String getFileName(String filePath) {
        // TODO Auto-generated method stub
        File file = new File(filePath);

        String name = file.getName();
        String[] parts = name.split("\\.");

        return parts[0];
    }

    public static String createHashTypeString(String prefix, String typeString) {
        // TODO Auto-generated method stub
        typeString = typeString.substring(0, 1).toUpperCase() + typeString.substring(1);
        String type = "";

        if (prefix.endsWith("/")) {
            type = prefix.substring(0, prefix.length() - 1) + "#" + typeString;
        } else if (prefix.endsWith("#")) {
            type = prefix + typeString;
        } else {
            type = prefix + "#" + typeString;
        }

        return type;
    }

    public static String createSlashTypeString(String prefix, String typeString) {
        // TODO Auto-generated method stub
        typeString = typeString.substring(0, 1).toUpperCase() + typeString.substring(1);
        String type = "";

        if (prefix.endsWith("#")) {
            type = prefix.substring(0, prefix.length() - 1) + "/" + typeString;
        } else if (prefix.endsWith("/")) {
            type = prefix + typeString;
        } else {
            type = prefix + "/" + typeString;
        }

        return type;
    }



    public static String createSlashTypeString(String typeString) {
        // TODO Auto-generated method stub
        if (typeString.contains("#")) {
            String[] parts = typeString.split("#");
            if (parts.length == 2) {
                return parts[0].trim() + "/" + parts[1].trim();
            } else {
                return typeString;
            }
        } else {
            return typeString;
        }
    }

    public static ResultSet executeQuery(Model model, String sparql) {
        // TODO Auto-generated method stub
        Query query = QueryFactory.create(sparql);
        QueryExecution execution = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
        execution.close();
        return resultSet;
    }

    public static void print(Object object) {
        // TODO Auto-generated method stub
        if (object instanceof LinkedHashMap) {
            for (Map.Entry<Object, Object> map : ((LinkedHashMap<Object, Object>) object).entrySet()) {
                Object key = map.getKey();
                Object value = map.getValue();

                print(key);
                print(value);
            }
        } else if (object instanceof String) {
            System.out.println(object);
        } else if (object instanceof ResultSet) {
            ResultSetFormatter.out(ResultSetFactory.copyResults((ResultSet) object));
        } else if (object instanceof Model) {
            ((Model) object).write(System.out, "TTL");
        } else if (object instanceof Boolean) {

        } else if (object instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList<String>) object).size(); i++) {
                String string = ((ArrayList<String>) object).get(i);
                System.out.println(string);
            }
        }
    }

    public static String getLastSegmentOfIRI(String iriString) {
        if (iriString.contains("#")) {
            String[] parts = iriString.split("#");
            if (parts.length == 2) {
                return parts[1].trim();
            } else {
                return iriString.trim();
            }
        } else {
            if (iriString.contains("http")) {
                String[] parts = iriString.split("/");
                return parts[parts.length - 1].trim();
            } else {
                String[] parts = iriString.split(":");
                return parts[parts.length - 1].trim();
            }
        }
    }

    public static String saveModel(Model model, String filePath) {
        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            String[] parts = filePath.split("\\.");
            model.write(outputStream, parts[parts.length - 1].toUpperCase());

            return "Success.\nFile Saved: " + filePath;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "File save error.";
        }
    }

    public static Long getTime() {
        // TODO Auto-generated method stub
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getTimeInSeconds(Long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return minutes + " minutes and " + seconds + " seconds.";
    }

    public static boolean checkToSaveModel(int count, String resultFile, Model model, String type) {
        // TODO Auto-generated method stub
        if (type.equals("rdf")) {
            if (count % Variables.RDF_SAVE_LIMIT == 0) {
                String string = modelToString(model, getFileExtension(resultFile));

                appendToFile(string, resultFile);

                return true;
            } else {
                return false;
            }
        } else {
            if (count % Variables.SAVE_LIMIT == 0) {
                String string = modelToString(model, getFileExtension(resultFile));

                appendToFile(string, resultFile);

                return true;
            } else {
                return false;
            }
        }
    }

    public static void checkToSaveModel(String resultFile, Model model) {
        // TODO Auto-generated method stub
        String string = modelToString(model, getFileExtension(resultFile));

        appendToFile(string, resultFile);
    }

    public static void startProcessingTime() {
        // TODO Auto-generated method stub
        totalDifference = 0L;
        startTimeLong = Methods.getTime();
    }

    public static void endProcessingTime() {
        // TODO Auto-generated method stub
        endTimeLong = Methods.getTime();
        totalDifference += endTimeLong - startTimeLong;

        String timeStringOne = "Required Time for processing: " + Methods.getTimeInSeconds(totalDifference);

        System.out.println(timeStringOne);
    }

    public static void deleteAndCreateFile(String filePath) {
        // TODO Auto-generated method stub
        File file = new File(filePath);

        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            boolean success = file.createNewFile();
            if (success)
                print("File created.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getUppercaseWord(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String getEncodedString(String filePath) {
        // TODO Auto-generated method stub
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)),
					StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String encodeString(String string) {
        byte[] pText = string.getBytes(StandardCharsets.US_ASCII);
        return new String(pText, StandardCharsets.UTF_8);
    }

    public static String bracketString(String string) {
        // TODO Auto-generated method stub
        return "<" + string + ">";
    }

    public static Object getRDFNodeValue(RDFNode node) {
        // TODO Auto-generated method stub
        if (node != null) {
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
        } else {
            return "";
        }
    }

    public static String readStringFromFile(String filePath) {
        File file = new File(filePath);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String text = "", s;
            StringBuilder stringBuilder = new StringBuilder(text);
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        return "";
    }

    public static boolean writeText(String filePath, String text) {
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

    private static Double getFileSizeMegaBytes(String filePath) {
        File file = new File(filePath);
        return (double) file.length() / (1024 * 1024);
    }

    public static boolean isJenaAccessible(String filePath) {
        return getFileSizeMegaBytes(filePath) < Variables.JENA_MB_LIMIT;
    }
}
