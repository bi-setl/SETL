package helper;

public final class Variables {
	public static final String TEMP_DIR = "temp\\";
	public static final String SPLIT_DIR = "temp\\Splits";
	public static final String MODEL_DIR = "temp\\Models";
	
	public static final String ERROR_READING_FILE = "Sorry, couldn't read the file";
	
	public static final String INCREMENTAL = "Incremental";
	public static final int SAVE_LIMIT = 10000;
	public static final int RDF_SAVE_LIMIT = 100;
	public static final long BYTES_PER_SPLIT = 1024000;
	public static final int MAX_SPLIT_LINE = 1000000;
	public static final int JENA_MB_LIMIT = 100;
	
	public static final String CSV = "CSV";
	public static final String XML = "XML";
	public static final String EXCEL = "Excel";
	public static final String JSON = "JSON";
	public static final String DB = "DB";
	
	public static final String COMMA = "Comma (,)";
	public static final String SPACE = "Space ( )";
	public static final String SEMICOLON = "Semicolon (;)";
	public static final String TAB = "Tab (	)";
	public static final String PIPE = "Pipe (|)";
	
	public static final String EXISTING_ATTRIBUTE = "Existing Attribute";
	public static final String EXPRESSION = "Expression";
	public static final String SOURCE_ATTRIBUTE = "Source Attribute";
    public static final String AUTOMATIC = "Automatic";
    public static final String SAME_AS_SOURCE_IRI = "Same As Source IRI";
	
	public static final String R2RML = "R2RML";
	public static final String DIRECT_MAPPING = "Direct Mapping";
	
	public static final String RDF_WRAPPER = "RDFWrapper";
	public static final String NON_SEMANTIC_TO_TBOX_DERIVER = "NonSemanticToTBoxDeriver";
	

	public static final String TEMP_TBOX_MODEL_TTL = TEMP_DIR + "TEMP_TBOX_MODEL.ttl";
	public static final String TEMP_HIER_IRI = "https://www.temphier.com/";
	
	public static final String OBJECT_PROPERTIES = "Object Properties";
	public static final String DATATYPE_PROPERTIES = "Datatype Properties";
	public static final String DATA_STRUCTURE = "Data Structure";
	public static final String CUBE = "Cube";
	public static final String CUBOID = "Cuboid";
	public static final String DATASET = "Dataset";
	public static final String DIM_OR_LEVEL_PROPERTIES = "Dimension/Level Properties";
	public static final String MEASURE_PROPERTIES = "Measure Properties";
	public static final String DIMENSION = "Dimension";
	public static final String LEVEL = "Level";
	public static final String SOURCE = "Source";
	public static final String TARGET = "Target";
	
	
	public static final String DIMENSION_PROPERTY = "DimensionProperty";
	public static final String LEVEL_PROPERTY = "LevelProperty";
	public static final String DATA_SET = "DataSet";
	public static final String CLASS = "Class";
	public static final String LEVEL_ATTRIBUTE = "LevelAttribute";
	public static final String OBJECT_PROPERTY = "ObjectProperty";
	public static final String DATATYPE_PROPERTY = "DatatypeProperty";
	public static final String FUNCTIONAL_PROPERTY = "FunctionalProperty";
	public static final String ROLLUP_PROPERTY = "RollupProperty";
	public static final String MEASURE_PROPERTY = "MeasureProperty";
}
