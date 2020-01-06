package etl;

import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import etl_model.ETLABox2TBox;
import etl_model.ETLABoxGenOperation;
import etl_model.ETLExpressionHandler;
import etl_model.ETLExtractionDB;
import etl_model.ETLExtractionSPARQL;
import etl_model.ETLFactEntryGenerator;
import etl_model.ETLInstanceEntryGenerator;
import etl_model.ETLLevelEntryGenerator;
import etl_model.ETLLoadingOperation;
import etl_model.ETLMatcher;
import etl_model.ETLMultipleTransform;
import etl_model.ETLPWeightGenerator;
import etl_model.ETLRDFWrapper;
import etl_model.ETLResourceRetreiver;
import etl_model.ETLSBagGenerator;
import etl_model.ETLTBoxBuilder;
import etl_model.ETLTBoxGenOperation;
import etl_model.ETLUpdateDimensionalConstruct;
import view.PanelETL;
import view.PanelETL.Arrow;
import view.PanelETL.Operation;

public class ParseXML {

	public ArrayList<Operation> getETLFromXML(String filePath, PanelETL panelETLObj) {

		ArrayList<Operation> allGeneratedOperations = new ArrayList<>();

		try {

			File etlFile = new File(filePath);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = dBuilder.parse(etlFile);

			document.getDocumentElement().normalize();

			// start of code for extraction SPARQL

			NodeList startNodeList = document.getElementsByTagName(ETLOperationToXML.START);
			int length = startNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node start = startNodeList.item(i);

				if (start.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) start;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();

					Operation startOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					startOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					startOperation.setXmlOperationKey(xmlOpKey);

					allGeneratedOperations.add(startOperation);

				}

			} // End of code for Extraction SPARQL

			// start of code for extraction SPARQL

			NodeList extSPARQLNodeList = document.getElementsByTagName(ETLOperationToXML.EXT_SPARQL);
			length = extSPARQLNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node extSPARQL = extSPARQLNodeList.item(i);

				if (extSPARQL.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) extSPARQL;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String rdfFilePath = element.getElementsByTagName("RDFFilePath").item(0).getTextContent();
					String sparqlQuery = element.getElementsByTagName("SPARQLQuery").item(0).getTextContent();
					String fileSavingPath = element.getElementsByTagName("FileSavingPath").item(0).getTextContent();

					Operation extSPARQLOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					extSPARQLOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					extSPARQLOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					extSPARQLOperation.setXmlOperationKey(xmlOpKey);

					ETLExtractionSPARQL etlExtSPARQLOperation = new ETLExtractionSPARQL();

					etlExtSPARQLOperation.setRdfFilePath(rdfFilePath);
					etlExtSPARQLOperation.setSparqlQuery(sparqlQuery);
					etlExtSPARQLOperation.setFileSavingPath(fileSavingPath);

					extSPARQLOperation.setEtlOperation(etlExtSPARQLOperation);

					allGeneratedOperations.add(extSPARQLOperation);

				}

			} // End of code for Extraction SPARQL

			// start of code for extraction DB

			NodeList extDBNodeList = document.getElementsByTagName(ETLOperationToXML.EXT_DB);
			length = extDBNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node extDB = extDBNodeList.item(i);

				if (extDB.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) extDB;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dbName = element.getElementsByTagName("DbName").item(0).getTextContent();
					String dbUser = element.getElementsByTagName("DbUser").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DbPassword").item(0).getTextContent();
					String dbQuery = element.getElementsByTagName("DbQuery").item(0).getTextContent();
					String filesPath = element.getElementsByTagName("FilePath").item(0).getTextContent();

					Operation extDBOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					extDBOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					extDBOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					extDBOperation.setXmlOperationKey(xmlOpKey);

					ETLExtractionDB etlExtDBOperation = new ETLExtractionDB();

					etlExtDBOperation.setDbName(dbName);
					etlExtDBOperation.setDbUser(dbUser);
					etlExtDBOperation.setDbPassword(dbPassword);
					etlExtDBOperation.setDbQuery(dbQuery);
					etlExtDBOperation.setFilePath(filesPath);

					extDBOperation.setEtlOperation(etlExtDBOperation);

					allGeneratedOperations.add(extDBOperation);

				}

			} // End of code for Extraction DB

			// Start of code for mapping generation

			/*NodeList mapGenNodeList = document.getElementsByTagName(ETLOperationToXML.MAPPING_GEN);
			length = mapGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node mapGen = mapGenNodeList.item(i);

				if (mapGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) mapGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dbName = element.getElementsByTagName("DBName").item(0).getTextContent();
					String dbUserName = element.getElementsByTagName("DBUserName").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DBUserPassword").item(0).getTextContent();
					String baseIRI = element.getElementsByTagName("BaseIRI").item(0).getTextContent();
					String fileSavingPath = element.getElementsByTagName("FileSavingPath").item(0).getTextContent();

					Operation mapGenOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					mapGenOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					mapGenOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					mapGenOperation.setXmlOperationKey(xmlOpKey);

					ETLMappingGenOperation etlMappingGenOperation = new ETLMappingGenOperation();
					etlMappingGenOperation.setBaseIRI(baseIRI);
					etlMappingGenOperation.setDbName(dbName);
					etlMappingGenOperation.setDbUserName(dbUserName);
					etlMappingGenOperation.setDbPassword(dbPassword);
					etlMappingGenOperation.setFileSavingPath(fileSavingPath);

					mapGenOperation.setEtlOperation(etlMappingGenOperation);

					allGeneratedOperations.add(mapGenOperation);

				}

			} // End of code for Mapping generation
*/
			// Start of code for TBox generation
			NodeList tBoxGenNodeList = document.getElementsByTagName(ETLOperationToXML.TBOX_GEN);
			length = tBoxGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node tBoxGen = tBoxGenNodeList.item(i);

				if (tBoxGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) tBoxGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dbName = element.getElementsByTagName("DBName").item(0).getTextContent();
					String dbUserName = element.getElementsByTagName("DBUserName").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DBUserPassword").item(0).getTextContent();
					String baseIRI = element.getElementsByTagName("BaseIRI").item(0).getTextContent();
					String tBoxFileSavingPath = element.getElementsByTagName("TBoxFileSavingPath").item(0)
							.getTextContent();

					Operation tBoxGenOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					tBoxGenOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					tBoxGenOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					tBoxGenOperation.setXmlOperationKey(xmlOpKey);

					ETLTBoxGenOperation etlTBoxGenOperation = new ETLTBoxGenOperation();
					etlTBoxGenOperation.setDbName(dbName);
					etlTBoxGenOperation.setDbUserName(dbUserName);
					etlTBoxGenOperation.setDbPassword(dbPassword);
					etlTBoxGenOperation.setBaseIRI(baseIRI);
					etlTBoxGenOperation.setFileSavingPath(tBoxFileSavingPath);

					tBoxGenOperation.setEtlOperation(etlTBoxGenOperation);

					allGeneratedOperations.add(tBoxGenOperation);

				}

			} // End of code for TBox generation

			// start of code for ABox generation
			NodeList aBoxGenNodeList = document.getElementsByTagName(ETLOperationToXML.ABOX_GEN);
			length = aBoxGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node aBoxGen = aBoxGenNodeList.item(i);

				if (aBoxGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) aBoxGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dbName = element.getElementsByTagName("DBName").item(0).getTextContent();
					String dbUserName = element.getElementsByTagName("DBUserName").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DBUserPassword").item(0).getTextContent();
					String mappingGraphFilePath = element.getElementsByTagName("MappingGraphFilePath").item(0)
							.getTextContent();
					String aBoxFileSavingPath = element.getElementsByTagName("ABoxFileSavingPath").item(0)
							.getTextContent();

					Operation aBoxGenOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					aBoxGenOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					aBoxGenOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					aBoxGenOperation.setXmlOperationKey(xmlOpKey);

					ETLABoxGenOperation etlABoxGenOperation = new ETLABoxGenOperation();
					etlABoxGenOperation.setDbName(dbName);
					etlABoxGenOperation.setDbUserName(dbUserName);
					etlABoxGenOperation.setDbPassword(dbPassword);
					etlABoxGenOperation.setMgFilePath(mappingGraphFilePath);
					etlABoxGenOperation.setFileSavingPath(aBoxFileSavingPath);

					aBoxGenOperation.setEtlOperation(etlABoxGenOperation);

					allGeneratedOperations.add(aBoxGenOperation);

				}

			} // End of code for ABox generation

			// start of code for Expression handler
			NodeList expressionNodeList = document.getElementsByTagName(ETLOperationToXML.EXPRESSION_HANDLER);
			length = expressionNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = expressionNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String mappingFile = element.getElementsByTagName("MappingFile").item(0).getTextContent();
					String sourceABoxFile = element.getElementsByTagName("SourceABoxFile").item(0).getTextContent();
					String outputFile = element.getElementsByTagName("OutputFile").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLExpressionHandler etllevelEntryGenOperation = new ETLExpressionHandler();
					etllevelEntryGenOperation.setMappingFile(mappingFile);
					etllevelEntryGenOperation.setSourceABoxFile(sourceABoxFile);
					etllevelEntryGenOperation.setResultFile(outputFile);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);
					allGeneratedOperations.add(levelEntryGenOperations);
				}

			} // End of code for Expression handler

			NodeList rdfWrapperNodeList = document.getElementsByTagName(ETLOperationToXML.RDF_WRAPPER);
			length = rdfWrapperNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = rdfWrapperNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dbName = element.getElementsByTagName("DbName").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DbPassword").item(0).getTextContent();
					String dbUsername = element.getElementsByTagName("DbUsername").item(0).getTextContent();
					String dbDirectBaseIRI = element.getElementsByTagName("DbDirectBaseIRI").item(0).getTextContent();
					String dbDirectTargetPath = element.getElementsByTagName("DbDirectTargetPath").item(0).getTextContent();
					String dbRmlFilePath = element.getElementsByTagName("DbRmlFilePath").item(0).getTextContent();
					String dbRMLTargetPath = element.getElementsByTagName("DbRMLTargetPath").item(0).getTextContent();
					String dbMappingType = element.getElementsByTagName("DbMappingType").item(0).getTextContent();

					String jsonSource = element.getElementsByTagName("JsonSource").item(0).getTextContent();
					String jsonTarget = element.getElementsByTagName("JsonTarget").item(0).getTextContent();
					String jsonTargetType = element.getElementsByTagName("JsonTargetType").item(0).getTextContent();

					String xmlSource = element.getElementsByTagName("XmlSource").item(0).getTextContent();
					String xmlTarget = element.getElementsByTagName("XmlTarget").item(0).getTextContent();
					String xmlTargetType = element.getElementsByTagName("XmlTargetType").item(0).getTextContent();

					String excelColumn = element.getElementsByTagName("ExcelColumn").item(0).getTextContent();
					String excelPrefix = element.getElementsByTagName("ExcelPrefix").item(0).getTextContent();
					String excelSource = element.getElementsByTagName("ExcelSource").item(0).getTextContent();
					String excelTarget = element.getElementsByTagName("ExcelTarget").item(0).getTextContent();
					String excelKeyAttributeType = element.getElementsByTagName("ExcelKeyAttributeType").item(0).getTextContent();
					String excelTargetType = element.getElementsByTagName("ExcelTargetType").item(0).getTextContent();

					String csvColumn = element.getElementsByTagName("CsvColumn").item(0).getTextContent();
					String csvDelimiter = element.getElementsByTagName("CsvDelimiter").item(0).getTextContent();
					String csvKeyAttributeType = element.getElementsByTagName("CsvKeyAttributeType").item(0)
							.getTextContent();
					String csvPrefix = element.getElementsByTagName("CsvPrefix").item(0).getTextContent();
					String csvSource = element.getElementsByTagName("CsvSource").item(0).getTextContent();
					String csvTarget = element.getElementsByTagName("CsvTarget").item(0).getTextContent();
					String csvTargetType = element.getElementsByTagName("CsvTargetType").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLRDFWrapper etlrdfWrapper = new ETLRDFWrapper();
					etlrdfWrapper.setCsvColumn(csvColumn);
					etlrdfWrapper.setCsvDelimiter(csvDelimiter);
					etlrdfWrapper.setCsvKeyAttributeType(csvKeyAttributeType);
					etlrdfWrapper.setCsvPrefix(csvPrefix);
					etlrdfWrapper.setCsvSource(csvSource);
					etlrdfWrapper.setCsvTarget(csvTarget);
					etlrdfWrapper.setCsvTargetType(csvTargetType);
					etlrdfWrapper.setDbName(dbName);
					etlrdfWrapper.setDbPassword(dbPassword);
					etlrdfWrapper.setDbDirectBaseIRI(dbDirectBaseIRI);
					etlrdfWrapper.setDbDirectTargetPath(dbDirectTargetPath);
					etlrdfWrapper.setDbRmlFilePath(dbRmlFilePath);
					etlrdfWrapper.setDbRMLTargetPath(dbRMLTargetPath);
					etlrdfWrapper.setDbMappingType(dbMappingType);
					etlrdfWrapper.setDbUsername(dbUsername);
					etlrdfWrapper.setExcelColumn(excelColumn);
					etlrdfWrapper.setExcelPrefix(excelPrefix);
					etlrdfWrapper.setExcelSource(excelSource);
					etlrdfWrapper.setExcelTarget(excelTarget);
					etlrdfWrapper.setExcelTargetType(excelTargetType);
					etlrdfWrapper.setJsonSource(jsonSource);
					etlrdfWrapper.setJsonTarget(jsonTarget);
					etlrdfWrapper.setJsonTargetType(jsonTargetType);
					etlrdfWrapper.setXmlSource(xmlSource);
					etlrdfWrapper.setXmlTarget(xmlTarget);
					etlrdfWrapper.setXmlTargetType(xmlTargetType);

					levelEntryGenOperations.setEtlOperation(etlrdfWrapper);
					allGeneratedOperations.add(levelEntryGenOperations);
				}

			}
			
			NodeList tboxBuilderNodeList = document.getElementsByTagName(ETLOperationToXML.T_BOX_BUILDER);
			length = tboxBuilderNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = tboxBuilderNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					/*String dbName = element.getElementsByTagName("DbName").item(0).getTextContent();
					String dbPassword = element.getElementsByTagName("DbPassword").item(0).getTextContent();
					String dbQuery = element.getElementsByTagName("DbQuery").item(0).getTextContent();
					String dbTarget = element.getElementsByTagName("DbTarget").item(0).getTextContent();
					String dbUsername = element.getElementsByTagName("DbUsername").item(0).getTextContent();*/

					String fileType = element.getElementsByTagName("FileType").item(0).getTextContent();
					String jsonSource = element.getElementsByTagName("JsonSource").item(0).getTextContent();
					String jsonTarget = element.getElementsByTagName("JsonTarget").item(0).getTextContent();
					String jsonPrefix = element.getElementsByTagName("JsonPrefix").item(0).getTextContent();
					String jsonTargetType = element.getElementsByTagName("JsonTargetType").item(0).getTextContent();

					String xmlSource = element.getElementsByTagName("XmlSource").item(0).getTextContent();
					String xmlTarget = element.getElementsByTagName("XmlTarget").item(0).getTextContent();
					String xmlPrefix = element.getElementsByTagName("XmlPrefix").item(0).getTextContent();
					String xmlTargetType = element.getElementsByTagName("XmlTargetType").item(0).getTextContent();

					String excelPrefix = element.getElementsByTagName("ExcelPrefix").item(0).getTextContent();
					String excelSource = element.getElementsByTagName("ExcelSource").item(0).getTextContent();
					String excelTarget = element.getElementsByTagName("ExcelTarget").item(0).getTextContent();
					String excelTargetType = element.getElementsByTagName("ExcelTargetType").item(0).getTextContent();

					String csvDelimiter = element.getElementsByTagName("CsvDelimiter").item(0).getTextContent();
					String csvPrefix = element.getElementsByTagName("CsvPrefix").item(0).getTextContent();
					String csvSource = element.getElementsByTagName("CsvSource").item(0).getTextContent();
					String csvTarget = element.getElementsByTagName("CsvTarget").item(0).getTextContent();
					String csvTargetType = element.getElementsByTagName("CsvTargetType").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLTBoxBuilder etlrdfWrapper = new ETLTBoxBuilder();
					etlrdfWrapper.setCsvDelimiter(csvDelimiter);
					etlrdfWrapper.setCsvPrefix(csvPrefix);
					etlrdfWrapper.setCsvSource(csvSource);
					etlrdfWrapper.setCsvTarget(csvTarget);
					etlrdfWrapper.setCsvTargetType(csvTargetType);
					etlrdfWrapper.setFileType(fileType);
					
					/*etlrdfWrapper.setDbName(dbName);
					etlrdfWrapper.setDbPassword(dbPassword);
					etlrdfWrapper.setDbQuery(dbQuery);
					etlrdfWrapper.setDbTarget(dbTarget);
					etlrdfWrapper.setDbUsername(dbUsername);*/
					
					etlrdfWrapper.setExcelPrefix(excelPrefix);
					etlrdfWrapper.setExcelSource(excelSource);
					etlrdfWrapper.setExcelTarget(excelTarget);
					etlrdfWrapper.setExcelTargetType(excelTargetType);
					etlrdfWrapper.setJsonSource(jsonSource);
					etlrdfWrapper.setJsonTarget(jsonTarget);
					etlrdfWrapper.setJsonPrefix(jsonPrefix);
					etlrdfWrapper.setJsonTargetType(jsonTargetType);
					etlrdfWrapper.setXmlSource(xmlSource);
					etlrdfWrapper.setXmlTarget(xmlTarget);
					etlrdfWrapper.setXmlPrefix(xmlPrefix);
					etlrdfWrapper.setXmlTargetType(xmlTargetType);

					levelEntryGenOperations.setEtlOperation(etlrdfWrapper);
					allGeneratedOperations.add(levelEntryGenOperations);
				}

			}

			// start of code for Level Entry generation
			NodeList abox2TboxNodeList = document.getElementsByTagName(ETLOperationToXML.ABOX2TBOX);
			length = abox2TboxNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = abox2TboxNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String sourceABoxFile = element.getElementsByTagName("SourceABoxFile").item(0).getTextContent();
					String targetTBoxFile = element.getElementsByTagName("TargetTBoxFile").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLABox2TBox etllevelEntryGenOperation = new ETLABox2TBox();
					etllevelEntryGenOperation.setSourceABoxFile(sourceABoxFile);
					etllevelEntryGenOperation.setTargetTBoxFile(targetTBoxFile);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);

					allGeneratedOperations.add(levelEntryGenOperations);

				}

			} // End of code for Level Entry generation
			
			// start of code for MULTIPLE TRANSFORM
			NodeList multipleTransformList = document.getElementsByTagName(ETLOperationToXML.MULTIPLE_TRANSFORM);
			length = multipleTransformList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = multipleTransformList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String firstSourcePath = element.getElementsByTagName("FirstSourcePath").item(0).getTextContent();
					String secondSourcePath = element.getElementsByTagName("SecondSourcePath").item(0).getTextContent();
					String mapPath = element.getElementsByTagName("MapPath").item(0).getTextContent();
					String targetType = element.getElementsByTagName("TargetType").item(0).getTextContent();
					String targetFile = element.getElementsByTagName("TargetFile").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLMultipleTransform etllevelEntryGenOperation = new ETLMultipleTransform();
					etllevelEntryGenOperation.setFirstSourcePath(firstSourcePath);
					etllevelEntryGenOperation.setSecondSourcePath(secondSourcePath);
					etllevelEntryGenOperation.setMapPath(mapPath);
					etllevelEntryGenOperation.setTargetType(targetType);
					etllevelEntryGenOperation.setTargetPath(targetFile);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);

					allGeneratedOperations.add(levelEntryGenOperations);

				}

			} // End of code for MULTIPLE TRANSFORM

			NodeList updateDimensionNodeList = document.getElementsByTagName(ETLOperationToXML.UPDATE_DIMENSION_CONSTRUCT);
			length = updateDimensionNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = updateDimensionNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String dimensionalConstruct = element.getElementsByTagName("DimensionalConstruct").item(0).getTextContent();
					String mapper = element.getElementsByTagName("Mapper").item(0).getTextContent();
					String newSourceData = element.getElementsByTagName("NewSourceData").item(0).getTextContent();
					String oldSourceData = element.getElementsByTagName("OldSourceData").item(0).getTextContent();
					String prefix = element.getElementsByTagName("Prefix").item(0).getTextContent();
					String provGraph = element.getElementsByTagName("ProvGraph").item(0).getTextContent();
					String sourceTBox = element.getElementsByTagName("SourceTBox").item(0).getTextContent();
					String targetABox = element.getElementsByTagName("TargetABox").item(0).getTextContent();
					String targetTBox = element.getElementsByTagName("TargetTBox").item(0).getTextContent();
					String resultFile = element.getElementsByTagName("ResultFile").item(0).getTextContent();
					String updateType = element.getElementsByTagName("UpdateType").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLUpdateDimensionalConstruct etllevelEntryGenOperation = new ETLUpdateDimensionalConstruct();
					etllevelEntryGenOperation.setDimensionalConstruct(dimensionalConstruct);
					etllevelEntryGenOperation.setMapper(mapper);
					etllevelEntryGenOperation.setNewSourceData(newSourceData);
					etllevelEntryGenOperation.setOldSourceData(oldSourceData);
					etllevelEntryGenOperation.setPrefix(prefix);
					etllevelEntryGenOperation.setProvGraph(provGraph);
					etllevelEntryGenOperation.setSourceTBox(sourceTBox);
					etllevelEntryGenOperation.setTargetABox(targetABox);
					etllevelEntryGenOperation.setTargetTBox(targetTBox);
					etllevelEntryGenOperation.setResultFile(resultFile);
					etllevelEntryGenOperation.setUpdateType(updateType);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);

					allGeneratedOperations.add(levelEntryGenOperations);

				}

			}
			
			// start of code for Instance Entry generation
			NodeList instanceEntryGenNodeList = document.getElementsByTagName(ETLOperationToXML.INSTANCE_ENTRY_GENERATOR);
			length = instanceEntryGenNodeList.getLength();
			for (int i = 0; i < length; i++) {
				Node levelEntryGen = instanceEntryGenNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String targetTypeFile = element.getElementsByTagName("TargetType").item(0).getTextContent();					
					String mappingFile = element.getElementsByTagName("MappingFile").item(0).getTextContent();
					String provFile = element.getElementsByTagName("ProvFile").item(0).getTextContent();
					String sourceABoxFile = element.getElementsByTagName("SourceABoxFile").item(0).getTextContent();
					String targetTBoxFile = element.getElementsByTagName("TargetTBoxFile").item(0).getTextContent();
					String targetABoxFile = element.getElementsByTagName("TargetABoxFile").item(0).getTextContent();

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLInstanceEntryGenerator etllevelEntryGenOperation = new ETLInstanceEntryGenerator();
					etllevelEntryGenOperation.setMappingFile(mappingFile);
					etllevelEntryGenOperation.setProvFile(provFile);
					etllevelEntryGenOperation.setSourceABoxFile(sourceABoxFile);
					etllevelEntryGenOperation.setTargetTBoxFile(targetTBoxFile);
					etllevelEntryGenOperation.setTargetABoxFile(targetABoxFile);
					etllevelEntryGenOperation.setTargetType(targetTypeFile);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);

					allGeneratedOperations.add(levelEntryGenOperations);

				}

			} // End of code for Instance Entry generation

			// start of code for Level Entry generation
			NodeList levelEntryGenNodeList = document.getElementsByTagName(ETLOperationToXML.LEVEL_ENTRY_GENERATOR);
			length = levelEntryGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node levelEntryGen = levelEntryGenNodeList.item(i);

				if (levelEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) levelEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String sourceCSVFile = element.getElementsByTagName("SourceCSV").item(0).getTextContent();
					String delimiterFile = element.getElementsByTagName("Delimiter").item(0).getTextContent();
					String fileTypeFile = element.getElementsByTagName("FileType").item(0).getTextContent();
					String targetTypeFile = element.getElementsByTagName("TargetType").item(0).getTextContent();
					
					String mappingFile = element.getElementsByTagName("MappingFile").item(0).getTextContent();
					String provFile = element.getElementsByTagName("ProvFile").item(0).getTextContent();
					String sourceABoxFile = element.getElementsByTagName("SourceABoxFile").item(0).getTextContent();
					String targetTBoxFile = element.getElementsByTagName("TargetTBoxFile").item(0).getTextContent();
					String targetABoxFile = element.getElementsByTagName("TargetABoxFile").item(0).getTextContent();

//					xmlString+="\t\t<MappingFile>"+temp.getMappingFile() +"</MappingFile>\n";
//					xmlString+="\t\t<SourceABoxFile>"+temp.getSourceABoxFile()+"</SourceABoxFile>\n";
//					xmlString+="\t\t<TargetTBoxFile>"+temp.getTargetTBoxFile()+"</TargetTBoxFile>\n";
//					//xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
//					xmlString+="\t\t<TargetABoxFile>"+temp.getTargetABoxFile()+"</TargetABoxFile>\n";

					Operation levelEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					levelEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					levelEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					levelEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLLevelEntryGenerator etllevelEntryGenOperation = new ETLLevelEntryGenerator();
					etllevelEntryGenOperation.setMappingFile(mappingFile);
					etllevelEntryGenOperation.setProvFile(provFile);
					etllevelEntryGenOperation.setSourceABoxFile(sourceABoxFile);
					etllevelEntryGenOperation.setTargetTBoxFile(targetTBoxFile);
					etllevelEntryGenOperation.setTargetABoxFile(targetABoxFile);
					etllevelEntryGenOperation.setSourceCSV(sourceCSVFile);
					etllevelEntryGenOperation.setDelimiter(delimiterFile);
					etllevelEntryGenOperation.setFileType(fileTypeFile);
					etllevelEntryGenOperation.setTargetType(targetTypeFile);

					levelEntryGenOperations.setEtlOperation(etllevelEntryGenOperation);

					allGeneratedOperations.add(levelEntryGenOperations);

				}

			} // End of code for Level Entry generation

			// start of code for Fact Entry generation
			NodeList factEntryGenNodeList = document.getElementsByTagName(ETLOperationToXML.FACT_ENTRY_GENERATOR);
			length = factEntryGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node factEntryGen = factEntryGenNodeList.item(i);

				if (factEntryGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) factEntryGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String sourceCSVFile = element.getElementsByTagName("SourceCSV").item(0).getTextContent();
					String delimiterFile = element.getElementsByTagName("Delimiter").item(0).getTextContent();
					String fileTypeFile = element.getElementsByTagName("FileType").item(0).getTextContent();
					String targetTypeFile = element.getElementsByTagName("TargetType").item(0).getTextContent();
					
					String mappingFile = element.getElementsByTagName("mappingFile").item(0).getTextContent();
					String provFile = element.getElementsByTagName("ProvFile").item(0).getTextContent();
					String sourceABoxFile = element.getElementsByTagName("sourceABoxFile").item(0).getTextContent();
					String targetTBoxFile = element.getElementsByTagName("targetTBoxFile").item(0).getTextContent();
					String targetABoxFile = element.getElementsByTagName("targetABoxFile").item(0).getTextContent();

//					xmlString+="\t\t<MappingFile>"+temp.getMappingFile() +"</MappingFile>\n";
//					xmlString+="\t\t<SourceABoxFile>"+temp.getSourceABoxFile()+"</SourceABoxFile>\n";
//					xmlString+="\t\t<TargetTBoxFile>"+temp.getTargetTBoxFile()+"</TargetTBoxFile>\n";
//					//xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
//					xmlString+="\t\t<TargetABoxFile>"+temp.getTargetABoxFile()+"</TargetABoxFile>\n";

					Operation factEntryGenOperations = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					factEntryGenOperations.setInputStatus(Boolean.parseBoolean(inputStatus));
					factEntryGenOperations.setExecuted(Boolean.parseBoolean(isExecuted));
					factEntryGenOperations.setXmlOperationKey(xmlOpKey);

					ETLFactEntryGenerator etlfactEntryGenOperation = new ETLFactEntryGenerator();
					etlfactEntryGenOperation.setMappingFile(mappingFile);
					etlfactEntryGenOperation.setProvFile(provFile);
					etlfactEntryGenOperation.setSourceABoxFile(sourceABoxFile);
					etlfactEntryGenOperation.setTargetTBoxFile(targetTBoxFile);
					etlfactEntryGenOperation.setTargetABoxFile(targetABoxFile);
					etlfactEntryGenOperation.setSourceCSV(sourceCSVFile);
					etlfactEntryGenOperation.setDelimiter(delimiterFile);
					etlfactEntryGenOperation.setFileType(fileTypeFile);
					etlfactEntryGenOperation.setTargetType(targetTypeFile);

					factEntryGenOperations.setEtlOperation(etlfactEntryGenOperation);

					allGeneratedOperations.add(factEntryGenOperations);

				}

			} // End of code for Fact Entry generation

			// start of code for Resource Retriever
			NodeList resRetrieverNodeList = document.getElementsByTagName(ETLOperationToXML.RESOURCE_RETRIEVER);
			length = resRetrieverNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node resRetriever = resRetrieverNodeList.item(i);

				if (resRetriever.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) resRetriever;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String operationType = element.getElementsByTagName("OperationType").item(0).getTextContent();
					String keyword = element.getElementsByTagName("Keyword").item(0).getTextContent();
					String numOfHit = element.getElementsByTagName("NumOfHit").item(0).getTextContent();
					String rdfFilePath = element.getElementsByTagName("RDFFilePath").item(0).getTextContent();
					String resourceFilePath = element.getElementsByTagName("ResourceFilePath").item(0).getTextContent();
					String dbPediaFilePath = element.getElementsByTagName("DBPediaDataFilePath").item(0)
							.getTextContent();

					Operation resRetrieverOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					resRetrieverOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					resRetrieverOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					resRetrieverOperation.setXmlOperationKey(xmlOpKey);

					ETLResourceRetreiver etlResRetriever = new ETLResourceRetreiver();

					etlResRetriever.setOperationType(Integer.parseInt(operationType));
					etlResRetriever.setKeyWord(keyword);
					etlResRetriever.setNumOfHit(Integer.parseInt(numOfHit));
					etlResRetriever.setRdfFilePath(rdfFilePath);
					etlResRetriever.setResourceFilePath(resourceFilePath);
					etlResRetriever.setDbPediaDataFilePath(dbPediaFilePath);

					resRetrieverOperation.setEtlOperation(etlResRetriever);

					allGeneratedOperations.add(resRetrieverOperation);

				}

			} // End of code for resource retriever

			// Start of code for Property Weight Generator

			NodeList pWeightGenNodeList = document.getElementsByTagName(ETLOperationToXML.PWEIGHT_GENERATOR);
			length = pWeightGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node pWeightGen = pWeightGenNodeList.item(i);

				if (pWeightGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) pWeightGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String rdfFilePath = element.getElementsByTagName("RDFFilePath").item(0).getTextContent();
					String pWeightFilePath = element.getElementsByTagName("PWeightFilePath").item(0).getTextContent();
					String selectedPropertiesString = element.getElementsByTagName("SelectedPropertiesString").item(0)
							.getTextContent();

					Operation pWeightGenOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					pWeightGenOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					pWeightGenOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					pWeightGenOperation.setXmlOperationKey(xmlOpKey);

					ETLPWeightGenerator etlPWeightGenerator = new ETLPWeightGenerator();

					etlPWeightGenerator.setRdfFilePath(rdfFilePath);
					etlPWeightGenerator.setPwFilePath(rdfFilePath);
					etlPWeightGenerator.setSeletedPropertiesString(selectedPropertiesString);

					pWeightGenOperation.setEtlOperation(etlPWeightGenerator);

					allGeneratedOperations.add(pWeightGenOperation);

				}

			} // End of code for Property Weight Generator

			// Start of code for Semantic Bag Generator
			NodeList sBagGenNodeList = document.getElementsByTagName(ETLOperationToXML.SBAG_GENERATOR);
			length = sBagGenNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node sBagGen = sBagGenNodeList.item(i);

				if (sBagGen.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) sBagGen;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String operationType = element.getElementsByTagName("OperationType").item(0).getTextContent();
					String rdfFilePath = element.getElementsByTagName("RDFFilePath").item(0).getTextContent();
					String resourceFilePath = element.getElementsByTagName("ResourceFilePath").item(0).getTextContent();
					String dbPediaDataFilePath = element.getElementsByTagName("DBPediaDataFilePath").item(0)
							.getTextContent();
					String semanticBagFilePath = element.getElementsByTagName("SemanticBagFilePath").item(0)
							.getTextContent();

					Operation sBagGenOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					sBagGenOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					sBagGenOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					sBagGenOperation.setXmlOperationKey(xmlOpKey);

					ETLSBagGenerator etlSBagGenerator = new ETLSBagGenerator();

					etlSBagGenerator.setOperationType(Integer.parseInt(operationType));
					etlSBagGenerator.setRdfFilePath(rdfFilePath);
					etlSBagGenerator.setResourceFilePath(resourceFilePath);
					etlSBagGenerator.setDbpediaDataFilePath(dbPediaDataFilePath);
					etlSBagGenerator.setSemanticBagFilePath(semanticBagFilePath);

					sBagGenOperation.setEtlOperation(etlSBagGenerator);

					allGeneratedOperations.add(sBagGenOperation);

				}

			} // end of code for Semantic Bag Generator

			// Start of code for Matcher

			NodeList matcherNodeList = document.getElementsByTagName(ETLOperationToXML.MATCHER);
			length = matcherNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node matcher = matcherNodeList.item(i);

				if (matcher.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) matcher;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String threshold = element.getElementsByTagName("Threshold").item(0).getTextContent();
					String localKBPWeightFilePath = element.getElementsByTagName("LocalKBPWeightFilePath").item(0)
							.getTextContent();
					String dbPediaSBagFilePath = element.getElementsByTagName("DBPediaSBagFilePath").item(0)
							.getTextContent();
					String localKBSBagFilePath = element.getElementsByTagName("LocalKBSbagFilePath").item(0)
							.getTextContent();
					String matcherFilePath = element.getElementsByTagName("MatcherFilePath").item(0).getTextContent();

					Operation matcherOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					matcherOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					matcherOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					matcherOperation.setXmlOperationKey(xmlOpKey);

					ETLMatcher etlMatcher = new ETLMatcher();

					etlMatcher.setThreshHold(Double.parseDouble(threshold));
					etlMatcher.setLocalKBPWeightFilePath(localKBPWeightFilePath);
					etlMatcher.setDbPediaSBagFilePath(dbPediaSBagFilePath);
					etlMatcher.setLocalKBSBagFilePath(localKBSBagFilePath);
					etlMatcher.setMatcherFilePath(matcherFilePath);

					matcherOperation.setEtlOperation(etlMatcher);

					allGeneratedOperations.add(matcherOperation);

				}

			} // end of code for Matcher

			// Start of code for Loader

			NodeList loaderNodeList = document.getElementsByTagName(ETLOperationToXML.LOADER);
			length = loaderNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node loader = loaderNodeList.item(i);

				if (loader.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) loader;

					String xmlOpKey = element.getAttribute("id");
					String opName = element.getElementsByTagName("OperationName").item(0).getTextContent();
					String upLeftX = element.getElementsByTagName("UpperLeftX").item(0).getTextContent();
					String upLeftY = element.getElementsByTagName("UpperLeftY").item(0).getTextContent();
					String inputStatus = element.getElementsByTagName("InputStatus").item(0).getTextContent();
					String isExecuted = element.getElementsByTagName("IsExecuted").item(0).getTextContent();

					String inputFilePath = element.getElementsByTagName("InputFilePath").item(0).getTextContent();
					String outputDir = element.getElementsByTagName("OutputDir").item(0).getTextContent();

					Operation loaderOperation = panelETLObj.new Operation(opName, Integer.parseInt(upLeftX),
							Integer.parseInt(upLeftY));
					loaderOperation.setInputStatus(Boolean.parseBoolean(inputStatus));
					loaderOperation.setExecuted(Boolean.parseBoolean(isExecuted));
					loaderOperation.setXmlOperationKey(xmlOpKey);

					ETLLoadingOperation etlLoader = new ETLLoadingOperation();
					
					etlLoader.setInputFilePath(inputFilePath);
					etlLoader.setOutputDir(outputDir);

					loaderOperation.setEtlOperation(etlLoader);

					allGeneratedOperations.add(loaderOperation);

				}

			} // end of code for Loader

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Opening ETL Failed!");
		}

		return allGeneratedOperations;
	}

	public ArrayList<Arrow> getETLAssiciationsFromXML(String filePath, PanelETL panelETLObj,
			ArrayList<Operation> allOperations) {

		ArrayList<Arrow> allGeneratedArrows = new ArrayList<>();

		try {

			File etlFile = new File(filePath);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = dBuilder.parse(etlFile);

			document.getDocumentElement().normalize();

			// xmlString+="Root: "+ document.getDocumentElement().getNodeName();

			// Start of code for Loader

			NodeList loaderNodeList = document.getElementsByTagName("Arrow");
			int length = loaderNodeList.getLength();
			for (int i = 0; i < length; i++) {

				Node loader = loaderNodeList.item(i);

				if (loader.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) loader;

					String sourceOperationKey = element.getElementsByTagName("SourceOperation").item(0)
							.getTextContent();
					String targetOprationKey = element.getElementsByTagName("TargetOperation").item(0).getTextContent();

					Operation sourceOperation = getOperationByKey(allOperations, sourceOperationKey);
					Operation targetOperation = getOperationByKey(allOperations, targetOprationKey);

//					System.out.println("Source Key: " + sourceOperationKey + " Target Key: "+ targetOprationKey);
//					
//					if(sourceOperation==null)
//						System.out.println("Source Operation is null");
//					if(targetOperation == null)
//						System.out.println("Target Operation is null");
//					System.out.println("Source Op: " + sourceOperation.getOperationName()+" Target Op: " + targetOperation.getOperationName());
//					
					Arrow arrow = panelETLObj.new Arrow(sourceOperation, targetOperation);
					allGeneratedArrows.add(arrow);

				}

			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Opening ETL Failed!");
		}

		return allGeneratedArrows;
	}

	Operation getOperationByKey(ArrayList<Operation> allOperations, String key) {

		for (Operation op : allOperations) {
			if (op.getXmlOperationKey().equals(key))
				return op;
		}

		return null;
	}
}
