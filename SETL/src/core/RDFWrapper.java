package core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import helper.Methods;
import helper.Variables;

public class RDFWrapper {
	private static final String BASE_PATH = "";
	private static final String INCREMENTAL = "Incremental";
	private static final String FILE_NOT_FOUND = "File not found";
	private static final String INAVLID_COLUMN_NAME = "Inavlid column name";
	private static final String INVALID_FILE_CONTENT = "Invalid file content";
	private static final String INVALID_PREFIX = "Invalid Prefix";
	private static final String FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS = "File Encoding Error. It may contain some special characters";
	private static final String TRANSFORMATION_SUCCESSFUL = "Transformation Successful";
	private static final String ERROR_IN_READING_THE_SOURCE_FILE = "Error in reading the source file";
	private static final String INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME = "Invalid file. Check file type or file name";
	Methods fileMethods;
	CSVExtraction csvExtraction;
	
	public static void main(String[] args) {
		String basePath = "C:\\Users\\Amrit\\Documents\\SETL\\AutoETL\\";
		String sourceFile = basePath + "city.csv";
		String prefix = "http://extbi.lab.aau.dk/ontolgoy/subsidy";
		String columnName = "cityName";
		String delimiter = "Comma (,)";
		String targetFile = basePath + "city_wrapper.ttl";
		
//		String sourceFile = basePath + "recipient.csv";
//		String prefix = "http://extbi.lab.aau.dk/ontolgoy/subsidy";
//		String columnName = "recipientid";
//		String delimiter = "Comma (,)";
//		String targetFile = basePath + "recipient_wrapper.nt";
		
//		String basePath = "I:\\Data\\fact\\rdf\\tiny\\";		
//		
//		String sourceFile = basePath + "Subsidy.csv";
//		String prefix = "http://extbi.lab.aau.dk/ontolgoy";
//		String columnName = "subsidyid";
//		String delimiter = "Comma (,)";
//		String targetFile = basePath + "subsidy_wrapper.nt";
		
		RDFWrapper rdfWrapper = new RDFWrapper();
		String string = rdfWrapper.parseCSVNew(sourceFile, prefix, columnName, delimiter, targetFile);
		Methods.print(string);
	}

	public RDFWrapper() {
		// TODO Auto-generated constructor stub
		fileMethods = new Methods();
		csvExtraction = new CSVExtraction();
	}

	public String parseCSVNew(String sourceFile, String prefix, String columnName, String delimiter, String targetFile) {
		// TODO Auto-generated method stub
		
		columnName = columnName.replace("\"", "");

		String fileName = Methods.getFileName(sourceFile);
		String type = Methods.createHashTypeString(prefix, fileName);

		String slashPrefixString = Methods.createSlashTypeString(prefix, fileName);
		
//		System.out.println(slashPrefixString);

		delimiter = Methods.getCSVDelimiter(delimiter);
		prefix = Methods.validatePrefix(prefix);

		BufferedReader br = null;
		String line = "";
		Methods methods = new Methods();

		try {
			br = new BufferedReader(new FileReader(sourceFile));

			line = br.readLine();
			line = line + delimiter;

			ArrayList<String> columnNames = methods.getColumnNames(sourceFile, delimiter);
//			System.out.println("Total Columns: " + columnNames.size());
			
			if (!columnNames.contains(columnName) &&!columnName.equals(INCREMENTAL)
					&& !columnName.contains("CONCAT")) {
				br.close();
				
				return INAVLID_COLUMN_NAME;
			}
			
			if (fileName.contains(" ")) {
				br.close();
				return "File name shouldn't contain spaces";
			}

			int index = columnNames.indexOf(columnName);

			ArrayList<Integer> faultList = new ArrayList<Integer>();

			Model model = ModelFactory.createDefaultModel();

			int count = 0, numOfFiles = 1;
			while ((line = br.readLine()) != null) {
//				line = br.readLine();
//				System.out.println(line);

				ArrayList<String> columnValues = csvExtraction.parseCSVLine(line, delimiter, false);

				if (columnNames.size() == columnValues.size()) {
//					System.out.println(columnNames.size() + " - " + columnValues.size());

					String iriValue = "";

					if (columnName.equals(Variables.INCREMENTAL)) {
						iriValue = "" + (count + 1);
					} else if (columnName.contains("(")) {
						LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
						
						for (int i = 0; i < columnNames.size(); i++) {
							String key = columnNames.get(i);
							String value = columnValues.get(i);
							
							valueMap.put(key, value);
						}
						
						String expressionString = columnName;
						
						EquationHandler equationHandler = new EquationHandler();
						Object valueObject = equationHandler.handleExpression(expressionString,
								valueMap, true);
						
						iriValue = valueObject.toString();
					} else {
						iriValue = columnValues.get(index);
					}

					iriValue = Methods.formatURL(iriValue);
					// iriValue = iriValue.substring(0, 1).toUpperCase() + iriValue.substring(1);

					UrlValidator urlValidator = new UrlValidator();
					String iriString = slashPrefixString + "#" + iriValue;

					if (!urlValidator.isValid(iriString)) {
						iriString = Methods.validateIRI(iriString);
						System.out.println("No valid URL for " + line);
					}

					Resource resource = model.createResource(iriString);
					Resource typeResource = model.createResource(type);
					resource.addProperty(RDF.type, typeResource);

					for (int i = 0; i < columnNames.size(); i++) {
						String value = columnValues.get(i);

						if (!value.equals("NA")) {
							String column = columnNames.get(i);

							String propertyString = prefix + column;
							Property property = model.createProperty(propertyString);

							Literal literal = model.createLiteral(value);
							resource.addLiteral(property, literal);
						}
					}

					count++;
				} else {
					// System.out.println("Skipping Line: " + count + " - " + line);
					// System.out.println("Found Columns: " + columnValues.size());

					faultList.add(count);

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
				}

				/*
				 * if (Methods.checkToSaveModel(count, numOfFiles, model)) { model =
				 * ModelFactory.createDefaultModel(); numOfFiles++; }
				 */

				if (Methods.checkToSaveModel(count, targetFile, model, "csv")) {
					model = ModelFactory.createDefaultModel();
				}
			}

			/*
			 * if (model.size() > 0) { Methods.checkToSaveModel(numOfFiles, model); }
			 */

			if (model.size() > 0) {
				Methods.checkToSaveModel(targetFile, model);
			}

			System.out.println("Total count: " + count);

			System.out.println("Total line skipped: " + faultList.size());
			// return Methods.mergeAllTempFiles(numOfFiles, targetFile);
			return "Success.\nFile saved: " + targetFile;
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
	}

	public String parseCSV(String csvSource, String csvTarget, String csvPrefix, String csvColumn,
			String csvDelimiter) {
		// TODO Auto-generated method stub
		
		  System.out.println(csvSource); System.out.println(csvTarget);
		  System.out.println(csvPrefix); System.out.println(csvColumn);
		  System.out.println(csvDelimiter);
		 
		
		csvColumn = csvColumn.replace("\"", "");
		Model model = ModelFactory.createDefaultModel();
		String fileExtension = fileMethods.getFileExtension(csvSource);

		if (fileExtension != null && fileExtension.equals("csv")) {
			String inputStream = fileMethods.getEncodedString(csvSource);

			if (inputStream != null) {
				if (!csvPrefix.startsWith("<")) {
					ArrayList<String> keys = new ArrayList<>();

					BufferedReader bufferedReader = null;
					try {
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
						
						Reader inputString = new StringReader(inputStream);
						bufferedReader = new BufferedReader(inputString);
						String string = "";

						while ((string = bufferedReader.readLine()) != null) {
							String[] parts = string.split(delimiter);
							for (String part : parts) {
								part = part.replace("\"", "");
								keys.add(part);
							}
							break;
						}

						if (!keys.contains(csvColumn) &&!csvColumn.equals(INCREMENTAL)
								&& !csvColumn.contains("CONCAT")) {
							bufferedReader.close();
							return INAVLID_COLUMN_NAME;
						}
						
						String type = getType(csvSource).toLowerCase();
						
						if (type.contains(" ")) {
							bufferedReader.close();
							return "File name shouldn't contain spaces";
						}
						
						int count = 0, numOfFiles = 1;
						if (csvColumn.equals(INCREMENTAL)) {
							while ((string = bufferedReader.readLine()) != null) {
								count++;
								string = string + " ";
								String resourceString = csvPrefix + "/" + type + "#" + count;
								Resource resource = model.createResource(resourceString);
								String parts[] = string.split(delimiter);
								if (parts.length == keys.size()) {
									for (int i = 0; i < parts.length; i++) {
										if (parts[i] != null ||  parts[i] != "") {
											if (parts[i].trim().length() != 0) {
												Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
												String literalString = parts[i];
												literalString = literalString.replace("\"", "");
												Literal literal = model.createLiteral(literalString.trim());
												resource.addProperty(property, literal);
											}
										}
									}
								} else {
									ArrayList<String> arrayList = new ArrayList<>();
									ArrayList<String> savedList = new ArrayList<>();
									String[] fragments = string.split(delimiter);
									for (String fragment : fragments) {
										if (fragment.startsWith("\"")) {
											savedList.add(fragment);
										} else if (fragment.endsWith("\"")) {
											if (!savedList.isEmpty()) {
												String value = "";
												for (String string2 : savedList) {
													value += string2;
												}
												savedList = new ArrayList<>();
												value += fragment;
												arrayList.add(value);
											}
										} else {
											if (savedList.size() != 0) {
												savedList.add(fragment);
											} else {
												arrayList.add(fragment);
											}
										}
									}
									
									if (arrayList.size() == keys.size()) {
										for (int i = 0; i < arrayList.size(); i++) {
											if (arrayList.get(i) != null || arrayList.get(i) != "") {
												if (arrayList.get(i).trim().length() != 0) {
													Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
													String literalString = parts[i];
													literalString = literalString.replace("\"", "");
													Literal literal = model.createLiteral(literalString.trim());
													resource.addProperty(property, literal);
												}
											}
										}
									} else {
										String regEx = "(" + delimiter + ")(" + delimiter + ")";
										Pattern pattern = Pattern.compile(regEx);
										Matcher matcher = pattern.matcher(string);

										while (matcher.find()) {
											string = string.replaceAll(regEx, delimiter + " " + delimiter);
										}
										
										System.out.println(string);
									}
								}
								
								if (count % 10000 == 0) {
									String tempPath = BASE_PATH + numOfFiles + ".ttl";
									System.out.println(tempPath);
									createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
									fileMethods.saveModel(model, tempPath);
									model = ModelFactory.createDefaultModel();
									numOfFiles++;
								}
							}
							
							if (model.size() != 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
								fileMethods.saveModel(model, tempPath);
								numOfFiles++;
							}
							
							bufferedReader.close();
							return mergeAllTempFiles(BASE_PATH, numOfFiles, csvTarget);
						} else if (csvColumn.contains("CONCAT")) {
							System.out.println("Expression");
							ArrayList<String> usedKeys = getUsedColumnNames(csvColumn);
							while ((string = bufferedReader.readLine()) != null) {
								count++;
								LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
								string = string + " ";
								Resource resource = model.createResource();
								String parts[] = string.split(delimiter);
								if (parts.length == keys.size()) {
									for (int i = 0; i < parts.length; i++) {
										if (parts[i] != null ||  parts[i] != "") {
											if (parts[i].trim().length() != 0) {
												Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
												String literalString = parts[i];
												literalString = literalString.replace("\"", "");
												Literal literal = model.createLiteral(literalString.trim());
												resource.addProperty(property, literal);
												if (usedKeys.contains(keys.get(i))) {
													linkedHashMap.put(keys.get(i), parts[i]);
												}
											}
										}
									}
								} else {
									ArrayList<String> arrayList = new ArrayList<>();
									ArrayList<String> savedList = new ArrayList<>();
									String[] fragments = string.split(delimiter);
									for (String fragment : fragments) {
										if (fragment.startsWith("\"")) {
											savedList.add(fragment);
										} else if (fragment.endsWith("\"")) {
											if (!savedList.isEmpty()) {
												String value = "";
												for (String string2 : savedList) {
													value += string2;
												}
												savedList = new ArrayList<>();
												value += fragment;
												arrayList.add(fragment);
											}
										} else {
											if (savedList.size() != 0) {
												savedList.add(fragment);
											} else {
												arrayList.add(fragment);
											}
										}
									}
									
									if (arrayList.size() == keys.size()) {
										for (int i = 0; i < arrayList.size(); i++) {
											if (arrayList.get(i) != null || arrayList.get(i) != "") {
												if (arrayList.get(i).trim().length() != 0) {
													Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
													String literalString = parts[i];
													literalString = literalString.replace("\"", "");
													Literal literal = model.createLiteral(literalString.trim());
													resource.addProperty(property, literal);
												}
											}
										}
									} else {
										System.out.println(string);
									}
								}
								
								String resourceString = csvPrefix + "/" + type + "#";
								
								for (String key : usedKeys) {
									if (keys.contains(key)) {
										if (linkedHashMap.get(key) != null) {
											resourceString += URLEncoder.encode(linkedHashMap.get(key), "UTF-8");
										}
									} else {
										resourceString += key;
									}
								}
								
								ResourceUtils.renameResource(resource, resourceString);
								
								if (count % 10000 == 0) {
									String tempPath = BASE_PATH + numOfFiles + ".ttl";
									System.out.println(tempPath);
									createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
									fileMethods.saveModel(model, tempPath);
									model = ModelFactory.createDefaultModel();
									numOfFiles++;
								}
							}
							
							if (model.size() != 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
								fileMethods.saveModel(model, tempPath);
								numOfFiles++;
							}
							
							bufferedReader.close();
							return mergeAllTempFiles(BASE_PATH, numOfFiles, csvTarget);
						} else {
							while ((string = bufferedReader.readLine()) != null) {
								count++;
								string = string + " ";
								Resource resource = model.createResource();
								String parts[] = string.split(delimiter);
								if (parts.length == keys.size()) {
									for (int i = 0; i < parts.length; i++) {
										if (parts[i] != null ||  parts[i] != "") {
											if (parts[i].trim().length() != 0) {
												Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
												String literalString = parts[i];
												literalString = literalString.replace("\"", "");
												Literal literal = model.createLiteral(literalString.trim());
												resource.addProperty(property, literal);
											}
										}
									}
								} else {
									ArrayList<String> arrayList = new ArrayList<>();
									ArrayList<String> savedList = new ArrayList<>();
									String[] fragments = string.split(delimiter);
									for (String fragment : fragments) {
										if (fragment.startsWith("\"")) {
											savedList.add(fragment);
										} else if (fragment.endsWith("\"")) {
											if (!savedList.isEmpty()) {
												String value = "";
												for (String string2 : savedList) {
													value += string2;
												}
												savedList = new ArrayList<>();
												value += fragment;
												arrayList.add(fragment);
											}
										} else {
											if (savedList.size() != 0) {
												savedList.add(fragment);
											} else {
												arrayList.add(fragment);
											}
										}
									}
									
									if (arrayList.size() == keys.size()) {
										for (int i = 0; i < arrayList.size(); i++) {
											if (arrayList.get(i) != null || arrayList.get(i) != "") {
												if (arrayList.get(i).trim().length() != 0) {
													Property property = model.createProperty(csvPrefix + "#" + keys.get(i));
													String literalString = parts[i];
													literalString = literalString.replace("\"", "");
													Literal literal = model.createLiteral(literalString.trim());
													resource.addProperty(property, literal);
												}
											}
										}
									} else {
										for (String string2 : savedList) {
											System.out.println(string2);
										}
										System.out.println(string);
									}
								}
								
								if (count % 10000 == 0) {
									String tempPath = BASE_PATH + numOfFiles + ".ttl";
									System.out.println(tempPath);
									createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
									fileMethods.saveModel(model, tempPath);
									model = ModelFactory.createDefaultModel();
									numOfFiles++;
								}
							}
							
							if (model.size() != 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								createResourceModel(model, csvPrefix, csvColumn, type, tempPath);
								numOfFiles++;
							}
							
							bufferedReader.close();
							return mergeAllTempFiles(BASE_PATH, numOfFiles, csvTarget);
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						return FILE_NOT_FOUND;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
					}
				} else {
					return INVALID_PREFIX;
				}
			} else {
				return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
			}
		} else {
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
	}

	private String mergeAllTempFiles(String basePath, int numOfFiles, String csvTarget) {
		// TODO Auto-generated method stub
		fileMethods.createNewFile(csvTarget);
		for (int i = 1; i < numOfFiles; i++) {
			String filePath = basePath + i + ".ttl";
			Model model = ModelFactory.createDefaultModel();
			try {
				model = fileMethods.readModelFromPath(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return INVALID_FILE_CONTENT;
			}
			String string = fileMethods.modelToString(model, fileMethods.getFileExtension(csvTarget));
			model.close();
			fileMethods.appendToFile(string, csvTarget);
			try {
				File file = new File(filePath);
				file.delete();
				System.out.println(filePath + " deleted");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		return "Success.\nFile Saved: " + csvTarget;
		
	}

	private ArrayList<String> getUsedColumnNames(String string) {
		// TODO Auto-generated method stub
		ArrayList<String> usedColumns = new ArrayList<>();
		String regEx = "(CONCAT\\()([^\\)]*)(\\))";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(string);
		
		if (matcher.find()) {
			String columnNames = matcher.group(2).trim();
			String[] columns = columnNames.split(",");
			for (String column : columns) {
				usedColumns.add(column.trim());
			}
		}
		return usedColumns;
	}

	public String parseXML(String xmlSource, String xmlTarget) {
		// TODO Auto-generated method stub
		String fileExtension = fileMethods.getFileExtension(xmlSource);

		if (fileExtension != null && fileExtension.equals("xml")) {
			try {
				Model model = ModelFactory.createDefaultModel();
				model.read(xmlSource);

				OutputStream outputStream = new FileOutputStream(xmlTarget);
				String[] parts = xmlTarget.split("\\.");
				model.write(outputStream, parts[parts.length - 1].toUpperCase());

				return TRANSFORMATION_SUCCESSFUL + "\nFileSaved: " + xmlTarget;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
				return "Error";
			}
		} else {
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
	}

	public String parseExcel(String excelSource, String excelTarget, String excelPrefix, String excelColumn) {
		// TODO Auto-generated method stub
		excelColumn = excelColumn.replaceAll("\"", "");
		String type = getType(excelSource).toLowerCase();

		Model model = ModelFactory.createDefaultModel();
		
		String fileExtension = fileMethods.getFileExtension(excelSource);
		
		if (fileExtension != null && (fileExtension.equals("xls") || fileExtension.equals("xlsx")
				|| fileExtension.equals("xlsm") || fileExtension.equals("xlsb"))) {
			try {
				FileInputStream fileInStream = new FileInputStream(excelSource);
				XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
				
				int count = 0, numOfFiles = 1;
				if (excelColumn.equals(INCREMENTAL)) {
					for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
						ArrayList<String> keyList = new ArrayList<>();

						XSSFSheet sheet = workBook.getSheetAt(i);
						XSSFRow xssfRow = sheet.getRow(0);
						for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
							keyList.add(xssfRow.getCell(j).toString());
						}
						
						for (int j = 1; j < sheet.getLastRowNum(); j++) {
							count++;
							xssfRow = sheet.getRow(j);

							String resourceString = excelPrefix + "/" + type + "#" + count;
							Resource resource = model.createResource(resourceString);

							for (int k = 0; k < xssfRow.getLastCellNum(); k++) {
								Cell cell = xssfRow.getCell(k);
								Property property = model.createProperty(excelPrefix + "#" + keyList.get(k));
								// System.out.println(prefix + "/" + keyList.get(k));
								try {
									switch (cell.getCellType()) {
									case STRING:
										String stringValue = cell.getStringCellValue();
										if (stringValue.contains("http")) {
											resource.addProperty(property, model.createResource(stringValue));
										} else {
											resource.addProperty(property, stringValue);
										}
										break;
									case NUMERIC:
										double numeric = cell.getNumericCellValue();
										BigDecimal bigDecimal = new BigDecimal(numeric);
										resource.addLiteral(property, bigDecimal);
										break;
									case BOOLEAN:
										boolean value = cell.getBooleanCellValue();
										resource.addLiteral(property, value);
										break;
									case _NONE:
										System.out.println("None\t");
										break;
									case BLANK:
										System.out.println("Blank\t");
										break;
									case ERROR:
										System.out.println("Error\t");
										break;
									default:
										System.out.println("default\t");
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								}
							}
							
							if (count % 10000 == 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								fileMethods.saveModel(model, tempPath);
								model = ModelFactory.createDefaultModel();
								numOfFiles++;
							}
						}
						
						if (model.size() != 0) {
							String tempPath = BASE_PATH + numOfFiles + ".ttl";
							System.out.println(tempPath);
							fileMethods.saveModel(model, tempPath);
							model = ModelFactory.createDefaultModel();
							numOfFiles++;
						}
					}
					fileInStream.close();
					workBook.close();
					return mergeAllTempFiles(BASE_PATH, numOfFiles, excelTarget);
				} else if (excelColumn.contains("CONCAT")) {
					ArrayList<String> usedKeys = getUsedColumnNames(excelColumn);
					for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
						ArrayList<String> keyList = new ArrayList<>();

						XSSFSheet sheet = workBook.getSheetAt(i);
						XSSFRow xssfRow = sheet.getRow(0);
						for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
							keyList.add(xssfRow.getCell(j).toString());
						}
						
						for (int j = 1; j < sheet.getLastRowNum(); j++) {
							count++;
							LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
							
							xssfRow = sheet.getRow(j);
							Resource resource = model.createResource();

							for (int k = 0; k < xssfRow.getLastCellNum(); k++) {
								Cell cell = xssfRow.getCell(k);
								Property property = model.createProperty(excelPrefix + "#" + keyList.get(k));
								// System.out.println(prefix + "/" + keyList.get(k));
								try {
									switch (cell.getCellType()) {
									case STRING:
										String stringValue = cell.getStringCellValue();
										if (stringValue.contains("http")) {
											resource.addProperty(property, model.createResource(stringValue));
										} else {
											resource.addProperty(property, stringValue);
										}
										if (usedKeys.contains(keyList.get(k))) {
											linkedHashMap.put(keyList.get(k), stringValue);
										}
										break;
									case NUMERIC:
										double numeric = cell.getNumericCellValue();
										BigDecimal bigDecimal = new BigDecimal(numeric);
										resource.addLiteral(property, bigDecimal);
										
										if (usedKeys.contains(keyList.get(k))) {
											linkedHashMap.put(keyList.get(k), String.valueOf(bigDecimal));
										}
										break;
									case BOOLEAN:
										boolean value = cell.getBooleanCellValue();
										resource.addLiteral(property, value);
										
										if (usedKeys.contains(keyList.get(k))) {
											linkedHashMap.put(keyList.get(k), String.valueOf(value));
										}
										break;
									case _NONE:
										System.out.println("None\t");
										break;
									case BLANK:
										System.out.println("Blank\t");
										break;
									case ERROR:
										System.out.println("Error\t");
										break;
									default:
										System.out.println("default\t");
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								}
							}
							
							String resourceString = excelPrefix + "/" + type + "#";
							
							for (String key : usedKeys) {
								if (keyList.contains(key)) {
									if (linkedHashMap.get(key) != null) {
										resourceString += URLEncoder.encode(linkedHashMap.get(key), "UTF-8");
									}
								} else {
									resourceString += key;
								}
							}
							
							ResourceUtils.renameResource(resource, resourceString);
							
							if (count % 10000 == 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								fileMethods.saveModel(model, tempPath);
								model = ModelFactory.createDefaultModel();
								numOfFiles++;
							}
						}
						
						if (model.size() != 0) {
							String tempPath = BASE_PATH + numOfFiles + ".ttl";
							System.out.println(tempPath);
							fileMethods.saveModel(model, tempPath);
							model = ModelFactory.createDefaultModel();
							numOfFiles++;
						}
						
					}
					fileInStream.close();
					workBook.close();
					return mergeAllTempFiles(BASE_PATH, numOfFiles, excelTarget);
				} else {
					for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
						ArrayList<String> keyList = new ArrayList<>();

						XSSFSheet sheet = workBook.getSheetAt(i);
						XSSFRow xssfRow = sheet.getRow(0);
						for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
							keyList.add(xssfRow.getCell(j).toString());
						}
						
						for (int j = 1; j < sheet.getLastRowNum(); j++) {
							count++;
							xssfRow = sheet.getRow(j);
							Resource resource = model.createResource();

							for (int k = 0; k < xssfRow.getLastCellNum(); k++) {
								Cell cell = xssfRow.getCell(k);
								Property property = model.createProperty(excelPrefix + "#" + keyList.get(k));
								// System.out.println(prefix + "/" + keyList.get(k));
								try {
									switch (cell.getCellType()) {
									case STRING:
										String stringValue = cell.getStringCellValue();
										if (stringValue.contains("http")) {
											resource.addProperty(property, model.createResource(stringValue));
										} else {
											resource.addProperty(property, stringValue);
										}
										break;
									case NUMERIC:
										double numeric = cell.getNumericCellValue();
										BigDecimal bigDecimal = new BigDecimal(numeric);
										resource.addLiteral(property, bigDecimal);
										break;
									case BOOLEAN:
										boolean value = cell.getBooleanCellValue();
										resource.addLiteral(property, value);
										break;
									case _NONE:
										System.out.println("None\t");
										break;
									case BLANK:
										System.out.println("Blank\t");
										break;
									case ERROR:
										System.out.println("Error\t");
										break;
									default:
										System.out.println("default\t");
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								}
							}
							
							if (count % 10000 == 0) {
								String tempPath = BASE_PATH + numOfFiles + ".ttl";
								System.out.println(tempPath);
								createResourceModel(model, excelPrefix, excelColumn, type, tempPath);
								model = ModelFactory.createDefaultModel();
								numOfFiles++;
							}
						}
						
						if (model.size() != 0) {
							String tempPath = BASE_PATH + numOfFiles + ".ttl";
							System.out.println(tempPath);
							fileMethods.saveModel(model, tempPath);
							model = ModelFactory.createDefaultModel();
							numOfFiles++;
						}
						
					}
					fileInStream.close();
					workBook.close();
					return mergeAllTempFiles(BASE_PATH, numOfFiles, excelTarget);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return INVALID_FILE_CONTENT;
			}
		} else {
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
	}

	private String createResourceModel(Model model, String prefix, String column, String type, String targetPath) {
		// TODO Auto-generated method stub
		/*String sparql = "PREFIX test: <" + prefix + "#>"
				+ "PREFIX iri: <" + prefix + "/" + type + "#>"
				+ "SELECT ?Y ?P ?O WHERE {?S ?P ?O. ?S test:"
				+ column + " ?X. BIND(concat(str(iri:), str(?X)) AS ?Y)}";*/
		
		String sparql = "PREFIX test: <" + prefix + "#>"
				+ "PREFIX iri: <" + prefix + "/" + type + "#>"
				+ "SELECT * WHERE {?S ?P ?O. ?S test:" + column + " ?X."
						+ " BIND(concat(str(iri:), str(?X)) AS ?Y)}";
		
		// System.out.println(sparql);
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(resultSet);

		Model targetModel = ModelFactory.createDefaultModel();
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("Y").toString();
			String predicate = querySolution.get("P").toString();
			RDFNode object = querySolution.get("O");

			Resource resource = targetModel.createResource(subject);
			Property property = targetModel.createProperty(predicate);
			resource.addProperty(property, object);
		}

		sparql = "SELECT DISTINCT ?S WHERE {?S ?P ?O.}";

		query = QueryFactory.create(sparql);
		execution = QueryExecutionFactory.create(query, targetModel);
		resultSet = ResultSetFactory.copyResults(execution.execSelect());

		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("S").toString();

			Resource classResource = ResourceFactory.createResource(prefix + "#" + type);
			Resource resource = targetModel.createResource(subject);
			if (!resource.hasProperty(RDF.type)) {
				resource.addProperty(RDF.type, classResource);
			}
			count++;
		}
		
		System.out.println(count++);

		return fileMethods.saveModel(targetModel, targetPath);
	}

	public String parseJSON(String jsonSource, String jsonTarget) {
		// TODO Auto-generated method stub
		String fileExtension = fileMethods.getFileExtension(jsonSource);

		if (fileExtension != null && fileExtension.equals("json")) {
			try {
				Model model = ModelFactory.createDefaultModel();
				model.read(jsonSource, "JSON-LD");

				OutputStream outputStream = new FileOutputStream(jsonTarget);
				String[] parts = jsonTarget.split("\\.");
				model.write(outputStream, parts[parts.length - 1].toUpperCase());

				return TRANSFORMATION_SUCCESSFUL + "\nFile Saved: " + jsonTarget;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
				return ERROR_IN_READING_THE_SOURCE_FILE;
			}
		} else {
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
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
	
	private String parseCSVNew(String csvSource, String csvTarget, String csvPrefix, String csvColumn,
			String csvDelimiter, String iriType) {
		csvColumn = csvColumn.replace("\"", "");
		Model model = ModelFactory.createDefaultModel();
		
		String fileExtension = fileMethods.getFileExtension(csvSource);
		if (fileExtension != null && fileExtension.equals("csv")) {
			String inputStream = fileMethods.getEncodedString(csvSource);
			
			if (inputStream != null) {
				if (!csvPrefix.startsWith("<")) {
					ArrayList<String> keys = new ArrayList<>();
					
					BufferedReader bufferedReader = null;
					
					try {
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
						
						Reader inputString = new StringReader(inputStream);
						bufferedReader = new BufferedReader(inputString);
						String string = "";
						
						while ((string = bufferedReader.readLine()) != null) {
							String[] parts = string.split(delimiter);
							for (String part : parts) {
								part = part.replace("\"", "");
								keys.add(part);
							}
							break;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						return FILE_NOT_FOUND;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
					}
				} else {
					return INVALID_PREFIX;
				}
			} else {
				return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
			}
		} else {
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
		return null;
	}
}
