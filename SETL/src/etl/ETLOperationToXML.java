package etl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import etl_model.ETLABox2TBox;
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
import etl_model.ETLUpdateDimensionalConstruct;
import view.PanelETL;
import view.PanelETL.Arrow;
import view.PanelETL.Operation;

public class ETLOperationToXML {

	// XML property names for the operations

	
	public static final String NonSemanticToTBoxDeriver = "NonSemanticToTBoxDeriver";
	public static final String MULTIPLE_TRANSFORM = "MultipleTransform";
	public static final String FACT_ENTRY_GENERATOR = "FactEntryGenerator";
	public static final String LEVEL_ENTRY_GENERATOR = "LevelEntryGenerator";
	public static final String SemanticSourceExtractor = "SemanticSourceExtractor";
	public static final String EXT_DB = "DBExtractor";
	static final String ETL = "ETL";
	static final String START = "Start";
	static final String ABOX_GEN = "ABoxGenerator";
	static final String TBOX_GEN = "TBoxGenerator";
	static final String LOADER = "Loader";
	static final String EXT_SPARQL = "ExtSPARQL";
	static final String EXPRESSION_HANDLER = "ExpressionHandler";
	public static final String TransformationOnLiteral = "TransformationOnLiteral";
	static final String INSTANCE_ENTRY_GENERATOR = "InstanceEntryGenerator";
	static final String RDF_WRAPPER = "RDFWrapper";
	static final String UPDATE_DIMENSION_CONSTRUCT = "UpdateDimensionalConstruct";
	static final String ABOX2TBOX = "ABoxToTBoxDeriver";
	// private static final String INSTANCE_GENERATOR="Instance Generator";
	// private static final String DIMENSION_ENTRY_GENERATOR="DimensaionEntry
	// Generator";

	static final String RESOURCE_RETRIEVER = "ResourceRetriever";
	static final String PWEIGHT_GENERATOR = "PWeightGenerator";
	static final String SBAG_GENERATOR = "SBagGenerator";
	static final String MATCHER = "Matcher";
	static final String T_BOX_BUILDER = "NonSemanticToTBoxDeriver";

	public String getXMLOfOperations(ArrayList<Operation> operationsList, ArrayList<Arrow> arrowsList) {

		String xmlString = "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n";
		xmlString += "<" + ETL + ">\n";

		ArrayList<Operation> startOperation = getNamedOperations(operationsList, PanelETL.START);

		ArrayList<Operation> extractSPAQRQLOpertations = getNamedOperations(operationsList, PanelETL.SemanticSourceExtractor);
		ArrayList<Operation> extractDBOpertations = getNamedOperations(operationsList, PanelETL.EXT_DB);

		ArrayList<Operation> mappingGenOperations = getNamedOperations(operationsList, PanelETL.MAPPING_GEN);
		ArrayList<Operation> expressionOperations = getNamedOperations(operationsList, PanelETL.TransformationOnLiteral);
		ArrayList<Operation> rdfWrapperOperations = getNamedOperations(operationsList,
				PanelETL.RDF_WRAPPER);
		ArrayList<Operation> tboxBuilderOperations = getNamedOperations(operationsList,
				PanelETL.NonSemanticToTBoxDeriver);
		ArrayList<Operation> abox2TboxOperations = getNamedOperations(operationsList,
						PanelETL.ABOX2TBOX);
		ArrayList<Operation> multipleTransformOperations = getNamedOperations(operationsList,
				PanelETL.MULTIPLE_TRANFORM);
		ArrayList<Operation> levelEntryGenOperations = getNamedOperations(operationsList,
						PanelETL.LEVEL_ENTRY_GENERATOR);
		ArrayList<Operation> instanceEntryGenOperations = getNamedOperations(operationsList,
				PanelETL.INSTANCE_ENTRY_GENERATOR);
		ArrayList<Operation> updateDimensionalOperations = getNamedOperations(operationsList,
				PanelETL.UPDATE_DIMENSION_CONSTRUCT);
		ArrayList<Operation> factEntryGenOperations = getNamedOperations(operationsList, PanelETL.FACT_ENTRY_GENERATOR);

		ArrayList<Operation> resourceRetrieverOperations = getNamedOperations(operationsList,
				PanelETL.RESOURCE_RETRIEVER);
		ArrayList<Operation> pWeightGenOperations = getNamedOperations(operationsList, PanelETL.PWEIGHT_GENERATOR);
		ArrayList<Operation> sBagGenOperations = getNamedOperations(operationsList, PanelETL.SBAG_GENERATOR);
		ArrayList<Operation> matcherOperations = getNamedOperations(operationsList, PanelETL.MATCHER);
		ArrayList<Operation> loadingOperations = getNamedOperations(operationsList, PanelETL.LOADER);

		for (Operation op : startOperation) {

			xmlString += "\t<" + START + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";

			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + START + ">\n";
		}

		for (Operation op : extractSPAQRQLOpertations) {

			xmlString += "\t<" + EXT_SPARQL + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLExtractionSPARQL temp = (ETLExtractionSPARQL) op.getEtlOperation();
			xmlString += "\t\t<RDFFilePath>" + temp.getRdfFilePath() + "</RDFFilePath>\n";
			String encoded = "";
			try {
				encoded = URLEncoder.encode(temp.getSparqlQuery(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			xmlString += "\t\t<SPARQLQuery>" + encoded + "</SPARQLQuery>\n";
			xmlString += "\t\t<FileSavingPath>" + temp.getFileSavingPath() + "</FileSavingPath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + EXT_SPARQL + ">\n";
		}

		for (Operation op : extractDBOpertations) {

			xmlString += "\t<" + EXT_DB + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLExtractionDB temp = (ETLExtractionDB) op.getEtlOperation();
			xmlString += "\t\t<DbName>" + temp.getDbName() + "</DbName>\n";
			xmlString += "\t\t<DbUser>" + temp.getDbUser() + "</DbUser>\n";
			xmlString += "\t\t<DbPassword>" + temp.getDbPassword() + "</DbPassword>\n";
			xmlString += "\t\t<DbQuery>" + temp.getDbQuery() + "</DbQuery>\n";
			xmlString += "\t\t<FilePath>" + temp.getFilePath() + "</FilePath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + EXT_DB + ">\n";
		}

		/*for (Operation op : mappingGenOperations) {

			xmlString += "\t<" + MAPPING_GEN + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLMappingGenOperation temp = (ETLMappingGenOperation) op.getEtlOperation();
			xmlString += "\t\t<DBName>" + temp.getDbName() + "</DBName>\n";
			xmlString += "\t\t<DBUserName>" + temp.getDbUserName() + "</DBUserName>\n";
			xmlString += "\t\t<DBUserPassword>" + temp.getDbPassword() + "</DBUserPassword>\n";
			xmlString += "\t\t<BaseIRI>" + temp.getBaseIRI() + "</BaseIRI>\n";
			xmlString += "\t\t<FileSavingPath>" + temp.getFileSavingPath() + "</FileSavingPath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + MAPPING_GEN + ">\n";
		}*/

		// ArrayList<Operation> tBoxGenOperations = getNamedOperations(operationsList,
		// PanelETL.TBOX_GEN);
		
		/*for (Operation op : tBoxGenOperations) {

			xmlString += "\t<" + TBOX_GEN + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLTBoxGenOperation temp = (ETLTBoxGenOperation) op.getEtlOperation();
			xmlString += "\t\t<DBName>" + temp.getDbName() + "</DBName>\n";
			xmlString += "\t\t<DBUserName>" + temp.getDbUserName() + "</DBUserName>\n";
			xmlString += "\t\t<DBUserPassword>" + temp.getDbPassword() + "</DBUserPassword>\n";
			xmlString += "\t\t<BaseIRI>" + temp.getBaseIRI() + "</BaseIRI>\n";
			xmlString += "\t\t<TBoxFileSavingPath>" + temp.getFileSavingPath() + "</TBoxFileSavingPath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + TBOX_GEN + ">\n";

		}*/

		// ArrayList<Operation> aBoxGenOperations = getNamedOperations(operationsList,
		// PanelETL.ABOX_GEN);
		/*for (Operation op : aBoxGenOperations) {

			xmlString += "\t<" + ABOX_GEN + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLABoxGenOperation temp = (ETLABoxGenOperation) op.getEtlOperation();
			xmlString += "\t\t<DBName>" + temp.getDbName() + "</DBName>\n";
			xmlString += "\t\t<DBUserName>" + temp.getDbUserName() + "</DBUserName>\n";
			xmlString += "\t\t<DBUserPassword>" + temp.getDbPassword() + "</DBUserPassword>\n";
			// xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
			xmlString += "\t\t<MappingGraphFilePath>" + temp.getMgFilePath() + "</MappingGraphFilePath>\n";
			xmlString += "\t\t<ABoxFileSavingPath>" + temp.getFileSavingPath() + "</ABoxFileSavingPath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + ABOX_GEN + ">\n";

		}*/

		// ArrayList<Operation> levelEntryGenOperations =
		// getNamedOperations(operationsList, PanelETL.EXPRESSION_HANDLER);
		for (Operation op : expressionOperations) {

			xmlString += "\t<" + EXPRESSION_HANDLER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLExpressionHandler temp = (ETLExpressionHandler) op.getEtlOperation();
			xmlString += "\t\t<MappingFile>" + temp.getMappingFile() + "</MappingFile>\n";
			xmlString += "\t\t<SourceABoxFile>" + temp.getSourceABoxFile() + "</SourceABoxFile>\n";
			xmlString += "\t\t<OutputFile>" + temp.getResultFile() + "</OutputFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + EXPRESSION_HANDLER + ">\n";

		}
		
		for (Operation op : rdfWrapperOperations) {

			xmlString += "\t<" + RDF_WRAPPER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLRDFWrapper temp = (ETLRDFWrapper) op.getEtlOperation();
			xmlString += "\t\t<CsvColumn>" + temp.getCsvColumn() + "</CsvColumn>\n";
			xmlString += "\t\t<CsvDelimiter>" + temp.getCsvDelimiter() + "</CsvDelimiter>\n";
			xmlString += "\t\t<CsvKeyAttributeType>" + temp.getCsvKeyAttributeType() + "</CsvKeyAttributeType>\n";
			xmlString += "\t\t<CsvPrefix>" + temp.getCsvPrefix() + "</CsvPrefix>\n";
			xmlString += "\t\t<CsvSource>" + temp.getCsvSource() + "</CsvSource>\n";
			xmlString += "\t\t<CsvTarget>" + temp.getCsvTarget() + "</CsvTarget>\n";
			xmlString += "\t\t<CsvTargetType>" + temp.getCsvTargetType() + "</CsvTargetType>\n";
			
			xmlString += "\t\t<DbName>" + temp.getDbName() + "</DbName>\n";
			xmlString += "\t\t<DbUsername>" + temp.getDbUsername() + "</DbUsername>\n";
			xmlString += "\t\t<DbPassword>" + temp.getDbPassword() + "</DbPassword>\n";
			xmlString += "\t\t<DbDirectBaseIRI>" + temp.getDbDirectBaseIRI() + "</DbDirectBaseIRI>\n";
			xmlString += "\t\t<DbDirectTargetPath>" + temp.getDbDirectTargetPath() + "</DbDirectTargetPath>\n";
			xmlString += "\t\t<DbRmlFilePath>" + temp.getDbRmlFilePath() + "</DbRmlFilePath>\n";
			xmlString += "\t\t<DbRMLTargetPath>" + temp.getDbRMLTargetPath() + "</DbRMLTargetPath>\n";
			xmlString += "\t\t<DbMappingType>" + temp.getDbMappingType() + "</DbMappingType>\n";
			
			xmlString += "\t\t<JsonSource>" + temp.getJsonSource() + "</JsonSource>\n";
			xmlString += "\t\t<JsonTarget>" + temp.getJsonTarget() + "</JsonTarget>\n";
			xmlString += "\t\t<JsonTargetType>" + temp.getJsonTargetType() + "</JsonTargetType>\n";
			
			xmlString += "\t\t<XmlSource>" + temp.getXmlSource() + "</XmlSource>\n";
			xmlString += "\t\t<XmlTarget>" + temp.getXmlTarget() + "</XmlTarget>\n";
			xmlString += "\t\t<XmlTargetType>" + temp.getXmlTargetType() + "</XmlTargetType>\n";
			
			xmlString += "\t\t<ExcelColumn>" + temp.getExcelColumn() + "</ExcelColumn>\n";
			xmlString += "\t\t<ExcelPrefix>" + temp.getExcelPrefix() + "</ExcelPrefix>\n";
			xmlString += "\t\t<ExcelSource>" + temp.getExcelSource() + "</ExcelSource>\n";
			xmlString += "\t\t<ExcelKeyAttributeType>" + temp.getExcelKeyAttributeType() + "</ExcelKeyAttributeType>\n";
			xmlString += "\t\t<ExcelTarget>" + temp.getExcelTarget() + "</ExcelTarget>\n";
			xmlString += "\t\t<ExcelTargetType>" + temp.getExcelTargetType() + "</ExcelTargetType>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + RDF_WRAPPER + ">\n";

		}
		
		for (Operation op : tboxBuilderOperations) {

			xmlString += "\t<" + T_BOX_BUILDER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLTBoxBuilder temp = (ETLTBoxBuilder) op.getEtlOperation();
			xmlString += "\t\t<FileType>" + temp.getFileType() + "</FileType>\n";
			
			xmlString += "\t\t<CsvDelimiter>" + temp.getCsvDelimiter() + "</CsvDelimiter>\n";
			xmlString += "\t\t<CsvPrefix>" + temp.getCsvPrefix() + "</CsvPrefix>\n";
			xmlString += "\t\t<CsvSource>" + temp.getCsvSource() + "</CsvSource>\n";
			xmlString += "\t\t<CsvTarget>" + temp.getCsvTarget() + "</CsvTarget>\n";
			xmlString += "\t\t<CsvTargetType>" + temp.getCsvTargetType() + "</CsvTargetType>\n";
			
			/*xmlString += "\t\t<DbName>" + temp.getDbName() + "</DbName>\n";
			xmlString += "\t\t<DbPassword>" + temp.getDbPassword() + "</DbPassword>\n";
			xmlString += "\t\t<DbQuery>" + temp.getDbQuery() + "</DbQuery>\n";
			xmlString += "\t\t<DbTarget>" + temp.getDbTarget() + "</DbTarget>\n";
			xmlString += "\t\t<DbUsername>" + temp.getDbUsername() + "</DbUsername>\n";*/
			
			xmlString += "\t\t<JsonSource>" + temp.getJsonSource() + "</JsonSource>\n";
			xmlString += "\t\t<JsonTarget>" + temp.getJsonTarget() + "</JsonTarget>\n";
			xmlString += "\t\t<JsonPrefix>" + temp.getJsonPrefix() + "</JsonPrefix>\n";
			xmlString += "\t\t<JsonTargetType>" + temp.getJsonTargetType() + "</JsonTargetType>\n";
			
			xmlString += "\t\t<XmlSource>" + temp.getXmlSource() + "</XmlSource>\n";
			xmlString += "\t\t<XmlTarget>" + temp.getXmlTarget() + "</XmlTarget>\n";
			xmlString += "\t\t<XmlPrefix>" + temp.getXmlPrefix() + "</XmlPrefix>\n";
			xmlString += "\t\t<XmlTargetType>" + temp.getXmlTargetType() + "</XmlTargetType>\n";
			
			xmlString += "\t\t<ExcelPrefix>" + temp.getExcelPrefix() + "</ExcelPrefix>\n";
			xmlString += "\t\t<ExcelSource>" + temp.getExcelSource() + "</ExcelSource>\n";
			xmlString += "\t\t<ExcelTarget>" + temp.getExcelTarget() + "</ExcelTarget>\n";
			xmlString += "\t\t<ExcelTargetType>" + temp.getExcelTargetType() + "</ExcelTargetType>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + T_BOX_BUILDER + ">\n";

		}
		
		for (Operation op : abox2TboxOperations) {

			xmlString += "\t<" + ABOX2TBOX + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLABox2TBox temp = (ETLABox2TBox) op.getEtlOperation();
			xmlString += "\t\t<SourceABoxFile>" + temp.getTargetTBoxFile() + "</SourceABoxFile>\n";
			xmlString += "\t\t<TargetTBoxFile>" + temp.getTargetTBoxFile() + "</TargetTBoxFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + ABOX2TBOX + ">\n";

		}
		
		for (Operation op : multipleTransformOperations) {

			xmlString += "\t<" + MULTIPLE_TRANSFORM + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLMultipleTransform temp = (ETLMultipleTransform) op.getEtlOperation();
			xmlString += "\t\t<FirstSourcePath>" + temp.getFirstSourcePath() + "</FirstSourcePath>\n";
			xmlString += "\t\t<SecondSourcePath>" + temp.getSecondSourcePath() + "</SecondSourcePath>\n";
			xmlString += "\t\t<MapPath>" + temp.getMapPath() + "</MapPath>\n";
			xmlString += "\t\t<TargetType>" + temp.getTargetType() + "</TargetType>\n";
			xmlString += "\t\t<TargetFile>" + temp.getTargetPath() + "</TargetFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + MULTIPLE_TRANSFORM + ">\n";

		}
		
		for (Operation op : levelEntryGenOperations) {

			xmlString += "\t<" + LEVEL_ENTRY_GENERATOR + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLLevelEntryGenerator temp = (ETLLevelEntryGenerator) op.getEtlOperation();
			xmlString += "\t\t<SourceCSV>" + temp.getSourceCSV() + "</SourceCSV>\n";
			xmlString += "\t\t<Delimiter>" + temp.getDelimiter() + "</Delimiter>\n";
			xmlString += "\t\t<FileType>" + temp.getFileType() + "</FileType>\n";
			xmlString += "\t\t<TargetType>" + temp.getTargetType() + "</TargetType>\n";
			xmlString += "\t\t<MappingFile>" + temp.getMappingFile() + "</MappingFile>\n";
			xmlString += "\t\t<SourceABoxFile>" + temp.getSourceABoxFile() + "</SourceABoxFile>\n";
			xmlString += "\t\t<ProvFile>" + temp.getProvFile() + "</ProvFile>\n";
			xmlString += "\t\t<TargetTBoxFile>" + temp.getTargetTBoxFile() + "</TargetTBoxFile>\n";
			// xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
			xmlString += "\t\t<TargetABoxFile>" + temp.getTargetABoxFile() + "</TargetABoxFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + LEVEL_ENTRY_GENERATOR + ">\n";

		}
		
		for (Operation op : instanceEntryGenOperations) {
			xmlString += "\t<" + INSTANCE_ENTRY_GENERATOR + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLInstanceEntryGenerator temp = (ETLInstanceEntryGenerator) op.getEtlOperation();
			xmlString += "\t\t<SourceCSV>" + temp.getSourceCSV() + "</SourceCSV>\n";
			xmlString += "\t\t<Delimiter>" + temp.getDelimiter() + "</Delimiter>\n";
			xmlString += "\t\t<FileType>" + temp.getFileType() + "</FileType>\n";
			xmlString += "\t\t<TargetType>" + temp.getTargetType() + "</TargetType>\n";
			xmlString += "\t\t<MappingFile>" + temp.getMappingFile() + "</MappingFile>\n";
			xmlString += "\t\t<SourceABoxFile>" + temp.getSourceABoxFile() + "</SourceABoxFile>\n";
			xmlString += "\t\t<ProvFile>" + temp.getProvFile() + "</ProvFile>\n";
			xmlString += "\t\t<TargetTBoxFile>" + temp.getTargetTBoxFile() + "</TargetTBoxFile>\n";
			// xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
			xmlString += "\t\t<TargetABoxFile>" + temp.getTargetABoxFile() + "</TargetABoxFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + INSTANCE_ENTRY_GENERATOR + ">\n";

		}

		// ArrayList<Operation> levelEntryGenOperations =
		// getNamedOperations(operationsList, PanelETL.LEVEL_ENTRY_GENERATOR);
		for (Operation op : updateDimensionalOperations) {

			xmlString += "\t<" + UPDATE_DIMENSION_CONSTRUCT + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLUpdateDimensionalConstruct temp = (ETLUpdateDimensionalConstruct) op.getEtlOperation();
			xmlString += "\t\t<DimensionalConstruct>" + temp.getDimensionalConstruct() + "</DimensionalConstruct>\n";
			xmlString += "\t\t<Mapper>" + temp.getMapper() + "</Mapper>\n";
			xmlString += "\t\t<NewSourceData>" + temp.getNewSourceData() + "</NewSourceData>\n";
			xmlString += "\t\t<OldSourceData>" + temp.getOldSourceData() + "</OldSourceData>\n";
			xmlString += "\t\t<Prefix>" + temp.getPrefix() + "</Prefix>\n";
			xmlString += "\t\t<ProvGraph>" + temp.getProvGraph() + "</ProvGraph>\n";
			xmlString += "\t\t<SourceTBox>" + temp.getSourceTBox() + "</SourceTBox>\n";
			xmlString += "\t\t<TargetABox>" + temp.getTargetABox() + "</TargetABox>\n";
			xmlString += "\t\t<TargetTBox>" + temp.getTargetTBox() + "</TargetTBox>\n";
			xmlString += "\t\t<ResultFile>" + temp.getResultFile() + "</ResultFile>\n";
			xmlString += "\t\t<UpdateType>" + temp.getUpdateType() + "</UpdateType>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + UPDATE_DIMENSION_CONSTRUCT + ">\n";

		}

		// ArrayList<Operation> factEntryGenOperations =
		// getNamedOperations(operationsList, PanelETL.LEVEL_ENTRY_GENERATOR);
		for (Operation op : factEntryGenOperations) {

			xmlString += "\t<" + FACT_ENTRY_GENERATOR + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLFactEntryGenerator temp = (ETLFactEntryGenerator) op.getEtlOperation();
			xmlString += "\t\t<SourceCSV>" + temp.getSourceCSV() + "</SourceCSV>\n";
			xmlString += "\t\t<Delimiter>" + temp.getDelimiter() + "</Delimiter>\n";
			xmlString += "\t\t<FileType>" + temp.getFileType() + "</FileType>\n";
			xmlString += "\t\t<TargetType>" + temp.getTargetType() + "</TargetType>\n";
			xmlString += "\t\t<mappingFile>" + temp.getMappingFile() + "</mappingFile>\n";
			xmlString += "\t\t<sourceABoxFile>" + temp.getSourceABoxFile() + "</sourceABoxFile>\n";
			xmlString += "\t\t<ProvFile>" + temp.getProvFile() + "</ProvFile>\n";
			xmlString += "\t\t<targetTBoxFile>" + temp.getTargetTBoxFile() + "</targetTBoxFile>\n";
			// xmlString+="\t<BaseIRI>"+temp.getBaseIRI()+"<//BaseIRI>\n";
			xmlString += "\t\t<targetABoxFile>" + temp.getTargetABoxFile() + "</targetABoxFile>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + FACT_ENTRY_GENERATOR + ">\n";

		}

		// ArrayList<Operation> resourceRetrieverOperations =
		// getNamedOperations(operationsList, PanelETL.RESOURCE_RETRIEVER);

		for (Operation op : resourceRetrieverOperations) {

			xmlString += "\t<" + RESOURCE_RETRIEVER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLResourceRetreiver temp = (ETLResourceRetreiver) op.getEtlOperation();
			xmlString += "\t\t<OperationType>" + temp.getOperationType() + "</OperationType>\n";
			xmlString += "\t\t<Keyword>" + temp.getKeyWord() + "</Keyword>\n";
			xmlString += "\t\t<NumOfHit>" + temp.getNumOfHit() + "</NumOfHit>\n";
			xmlString += "\t\t<RDFFilePath>" + temp.getRdfFilePath() + "</RDFFilePath>\n";
			xmlString += "\t\t<ResourceFilePath>" + temp.getResourceFilePath() + "</ResourceFilePath>\n";
			xmlString += "\t\t<DBPediaDataFilePath>" + temp.getDbPediaDataFilePath() + "</DBPediaDataFilePath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + RESOURCE_RETRIEVER + ">\n";

		}

		// ArrayList<Operation> pWeightGenOperations =
		// getNamedOperations(operationsList, PanelETL.PWEIGHT_GENERATOR);

		for (Operation op : pWeightGenOperations) {

			xmlString += "\t<" + PWEIGHT_GENERATOR + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLPWeightGenerator temp = (ETLPWeightGenerator) op.getEtlOperation();
			xmlString += "\t\t<RDFFilePath>" + temp.getRdfFilePath() + "</RDFFilePath>\n";
			xmlString += "\t\t<PWeightFilePath>" + temp.getPwFilePath() + "</PWeightFilePath>\n";
			xmlString += "\t\t<SelectedPropertiesString>" + temp.getSeletedPropertiesString()
					+ "</SelectedPropertiesString>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + PWEIGHT_GENERATOR + ">\n";

		}

		// ArrayList<Operation> sBagGenOperations = getNamedOperations(operationsList,
		// PanelETL.SBAG_GENERATOR);

		for (Operation op : sBagGenOperations) {

			xmlString += "\t<" + SBAG_GENERATOR + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLSBagGenerator temp = (ETLSBagGenerator) op.getEtlOperation();
			xmlString += "\t\t<RDFFilePath>" + temp.getRdfFilePath() + "</RDFFilePath>\n";
			xmlString += "\t\t<OperationType>" + temp.getOperationType() + "</OperationType>\n";
			xmlString += "\t\t<ResourceFilePath>" + temp.getResourceFilePath() + "</ResourceFilePath>\n";
			xmlString += "\t\t<DBPediaDataFilePath>" + temp.getDbpediaDataFilePath() + "</DBPediaDataFilePath>\n";
			xmlString += "\t\t<SemanticBagFilePath>" + temp.getSemanticBagFilePath() + "</SemanticBagFilePath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + SBAG_GENERATOR + ">\n";

		}

		// ArrayList<Operation> matcherOperations = getNamedOperations(operationsList,
		// PanelETL.MATCHER);

		for (Operation op : matcherOperations) {

			xmlString += "\t<" + MATCHER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLMatcher temp = (ETLMatcher) op.getEtlOperation();
			xmlString += "\t\t<Threshold>" + temp.getThreshHold() + "</Threshold>\n";
			xmlString += "\t\t<LocalKBPWeightFilePath>" + temp.getLocalKBPWeightFilePath()
					+ "</LocalKBPWeightFilePath>\n";
			xmlString += "\t\t<DBPediaSBagFilePath>" + temp.getDbPediaSBagFilePath() + "</DBPediaSBagFilePath>\n";
			xmlString += "\t\t<LocalKBSbagFilePath>" + temp.getLocalKBSBagFilePath() + "</LocalKBSbagFilePath>\n";
			xmlString += "\t\t<MatcherFilePath>" + temp.getMatcherFilePath() + "</MatcherFilePath>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + MATCHER + ">\n";

		}

		// ArrayList<Operation> loadingOperations = getNamedOperations(operationsList,
		// PanelETL.LOADER);
		for (Operation op : loadingOperations) {

			xmlString += "\t<" + LOADER + " id=\"" + op.getXmlOperationKey() + "\">\n";
			xmlString += "\t\t<OperationName>" + op.getOperationName() + "</OperationName>\n";
			xmlString += "\t\t<UpperLeftX>" + op.getUpperLeftX() + "</UpperLeftX>\n";
			xmlString += "\t\t<UpperLeftY>" + op.getUpperLeftY() + "</UpperLeftY>\n";
			xmlString += "\t\t<InputStatus>" + op.isInputStatus() + "</InputStatus>\n";
			xmlString += "\t\t<IsExecuted>" + op.isExecuted() + "</IsExecuted>\n";

			ETLLoadingOperation temp = (ETLLoadingOperation) op.getEtlOperation();
			xmlString += "\t\t<InputFilePath>" + temp.getInputFilePath() + "</InputFilePath>\n";
			xmlString += "\t\t<OutputDir>" + temp.getOutputDir() + "</OutputDir>\n";

			// next operations, and shape
			ArrayList<Operation> nextOperations = op.getNextOperations();
			if (nextOperations.size() > 0)
				xmlString += "\t\t<NextOperations>\n";
			for (Operation nxtOp : nextOperations) {
				xmlString += "\t\t\t<item>" + nxtOp.getXmlOperationKey() + "</item>\n";
			}
			if (nextOperations.size() > 0)
				xmlString += "\t\t</NextOperations>\n";

			xmlString += "\t</" + LOADER + ">\n";

		}

		if (arrowsList.size() > 0)
			xmlString += "\t<Arrows>\n";

		for (Arrow arrow : arrowsList) {
			xmlString += "\t\t<Arrow>\n";

			xmlString += "\t\t\t<SourceOperation>" + arrow.getSourceOperation().getXmlOperationKey()
					+ "</SourceOperation>\n";
			xmlString += "\t\t\t<TargetOperation>" + arrow.getTargetOperation().getXmlOperationKey()
					+ "</TargetOperation>\n";

			xmlString += "\t\t</Arrow>\n";

		}
		if (arrowsList.size() > 0)
			xmlString += "\t</Arrows>\n";

		xmlString += "</" + ETL + ">\n";
		return xmlString;
	}

	private ArrayList<Operation> getNamedOperations(ArrayList<Operation> allOperations, String opName) {

		ArrayList<Operation> temp = new ArrayList<>();

		for (Operation operation : allOperations) {

			if (operation.getOperationName().equals(opName))
				temp.add(operation);
		}
		return temp;
	}

}
