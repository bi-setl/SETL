package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import helper.Methods;

public class TBoxBuilder {
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
	private Methods methods;

	public TBoxBuilder() {
		methods = new Methods();
	}

	public String parseCSV(String csvSource, String csvPrefix, String csvDelimiter, String csvTarget) {
		Model model = ModelFactory.createDefaultModel();
		String fileExtension = methods.getFileExtension(csvSource);

		if (fileExtension != null && fileExtension.equals("csv")) {
			String inputStream = methods.getEncodedString(csvSource);

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
								keys.add(part.replaceAll("\"", ""));
							}
							break;
						}

						String type = getType(csvSource);

						if (type.contains(" ")) {
							bufferedReader.close();
							return "File name shouldn't contain spaces";
						}

						String resourceString = csvPrefix + "/" + type.replaceAll("\"", "");
						addClassResource(resourceString, model);

						for (int i = 0; i < keys.size(); i++) {
							String propertyString = csvPrefix + "#" + keys.get(i);
							
							addDataProperty(propertyString, model);
							createProperty(propertyString, resourceString, "http://www.w3.org/2000/01/rdf-schema#domain", model);
							Literal literal = model.createLiteral(type);
							createProperty(propertyString, literal.getDatatypeURI(), "http://www.w3.org/2000/01/rdf-schema#range", model);
						}

						bufferedReader.close();
						return methods.saveModel(model, csvTarget);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
						return FILE_NOT_FOUND;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
						return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
					}
				} else {
					return INVALID_PREFIX;
				}
			} else {
				return FILE_ENCODING_ERROR_IT_MAY_CONTAIN_SOME_SPECIAL_CHARACTERS;
			}
		} else

		{
			return INVALID_FILE_CHECK_FILE_TYPE_OR_FILE_NAME;
		}
	}
	
	public String parseExcel(String excelSource, String excelTarget, String excelPrefix) {
		// TODO Auto-generated method stub
		String type = getType(excelSource).toLowerCase();

		Model model = ModelFactory.createDefaultModel();
		
		String fileExtension = methods.getFileExtension(excelSource);
		
		if (fileExtension != null && (fileExtension.equals("xls") || fileExtension.equals("xlsx")
				|| fileExtension.equals("xlsm") || fileExtension.equals("xlsb"))) {
			try {
				/*
				 * String fileString = fileMethods.getEncodedString(excelSource); String
				 * tempFilePath = "TEMP_EXCEL" + fileExtension;
				 * fileMethods.writeText(tempFilePath, fileString);
				 */
				
				FileInputStream fileInStream = new FileInputStream(excelSource);
				XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
				
				for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
					ArrayList<String> keyList = new ArrayList<>();

					XSSFSheet sheet = workBook.getSheetAt(i);
					XSSFRow xssfRow = sheet.getRow(0);
					for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
						keyList.add(xssfRow.getCell(j).toString());
					}
					
					ArrayList<String> arrayList = new ArrayList<String>();
					for (int j = 1; j < sheet.getLastRowNum(); j++) {
						System.out.println("Row " + j);
						xssfRow = sheet.getRow(j);

						String resourceString = excelPrefix + "/" + type;
						addClassResource(resourceString, model);

						for (int k = 0; k < xssfRow.getLastCellNum(); k++) {
							if (arrayList.size() != keyList.size()) {
								
								if (!arrayList.contains(keyList.get(k))) {
									Cell cell = xssfRow.getCell(k);
									String propertyString = excelPrefix + "#" + keyList.get(k);
									
									addDataProperty(propertyString, model);
									createProperty(propertyString, resourceString, "http://www.w3.org/2000/01/rdf-schema#domain", model);
									
									try {
										if (cell != null) {
											if (cell.getCellType() != null) {
												arrayList.add(keyList.get(k));
												switch (cell.getCellType()) {
												case STRING:
													String stringValue = cell.getStringCellValue();
													Literal literal = model.createTypedLiteral(stringValue);
													createProperty(propertyString, literal.getDatatypeURI(), "http://www.w3.org/2000/01/rdf-schema#range", model);
													break;
												case NUMERIC:
													double numeric = cell.getNumericCellValue();
													literal = model.createTypedLiteral(numeric);
													createProperty(propertyString, literal.getDatatypeURI(), "http://www.w3.org/2000/01/rdf-schema#range", model);
													break;
												case BOOLEAN:
													boolean value = cell.getBooleanCellValue();
													literal = model.createTypedLiteral(value);
													createProperty(propertyString, literal.getDatatypeURI(), "http://www.w3.org/2000/01/rdf-schema#range", model);
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
											} else {
												System.out.println("CELL Type " + k + " contains null value");
											}
										} else {
											System.out.println("CELL " + k + " is null");
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										System.out.println(e.getMessage());
										System.out.println("FOUND ORIGIN");
									}
								}
							} else {
								fileInStream.close();
								workBook.close();
								return methods.saveModel(model, excelTarget);
							}
						}
					}
				}
				fileInStream.close();
				workBook.close();
				return methods.saveModel(model, excelTarget);
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

	private void createProperty(String string, String object, String property, Model classModel) {
		// TODO Auto-generated method stub
		// String propertyName = assignIRI(property);

		Resource resource = classModel.getResource(string);
		Property predicate = classModel.createProperty(property);
		resource.addProperty(predicate, classModel.createResource(object));
	}
	
	public boolean addClassResource(String name, Model classModel) {
		// TODO Auto-generated method stub
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class");
		Resource newResource = classModel.createResource(name);
		newResource.addProperty(RDF.type, classResource);
		return true;
	}
	
	public boolean addDataProperty(String name, Model classModel) {
		// TODO Auto-generated method stub
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
		Resource newResource = classModel.createResource(name);
		newResource.addProperty(RDF.type, classResource);
		return true;
	}
}
