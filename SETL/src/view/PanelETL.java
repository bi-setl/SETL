package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import etl.ETLOperationToXML;
import etl.ParseXML;
import etl_model.ETLABox2TBox;
import etl_model.ETLABoxGenOperation;
import etl_model.ETLExpressionHandler;
import etl_model.ETLExtractionDB;
import etl_model.ETLExtractionSPARQL;
import etl_model.ETLFactEntryGenerator;
import etl_model.ETLInstanceEntryGenerator;
import etl_model.ETLLevelEntryGenerator;
import etl_model.ETLLoadingOperation;
import etl_model.ETLMappingGenOperation;
import etl_model.ETLMatcher;
import etl_model.ETLMultipleTransform;
import etl_model.ETLPWeightGenerator;
import etl_model.ETLRDFWrapper;
import etl_model.ETLResourceRetreiver;
import etl_model.ETLSBagGenerator;
import etl_model.ETLTBoxBuilder;
import etl_model.ETLUpdateDimensionalConstruct;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class PanelETL extends JPanel {
	JTextPane textPaneETLStatus;
	ArrayList<Operation> allOperations;
	ArrayList<Arrow> allArrows;
	Graph graphPanel;
	JPanel panelETLButtons;
	ButtonGroup etlButtonGroup;
	JButton selectedButton = null;
	JPanel panelComponentPalette;

	Color defaultButtonBGColor;

	// Strings for oepration names
	public static final String START = "Start";
	public static final String INSTANCE_ENTRY_GENERATOR = "InstanceEntryGenerator";
	public static final String NonSemanticToTBoxDeriver = "NonSemanticToTBoxDeriver";
	public static final String RDF_WRAPPER = "RDFWrapper";
	public static final String UPDATE_DIMENSION_CONSTRUCT = "UpdateDimensionalConstruct";
	public static final String TransformationOnLiteral = "TransformationOnLiteral";
	public static final String FACT_ENTRY_GENERATOR = "FactEntryGenerator";
	public static final String LEVEL_ENTRY_GENERATOR = "LevelEntryGenerator";
	public static final String MULTIPLE_TRANFORM = "MultipleTransform";
	public static final String MAPPING_GEN = "Direct Mapping Generator";
	public static final String ABOX2TBOX = "ABoxToTBoxDeriver";
	public static final String LOADER = "Loader";
	public static final String SemanticSourceExtractor = "SemanticSourceExtractor";
	public static final String EXT_DB = "DBExtractor";
	public static final String RESOURCE_RETRIEVER = "Resource Retriever";
	public static final String PWEIGHT_GENERATOR = "PWeight Generator";
	public static final String SBAG_GENERATOR = "SBag Generator";
	public static final String MATCHER = "Matcher";
	private JTabbedPane tabbedPane;

	/**
	 * Create the panel.
	 */
	public PanelETL() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		panelETLButtons = new JPanel();
		panelETLButtons.setBackground(Color.WHITE);
		add(panelETLButtons, BorderLayout.NORTH);

		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				etlNewButtonHandler();
			}
		});
		btnNew.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnNew);

		defaultButtonBGColor = btnNew.getBackground();

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				etlOpenButtonHandler();
			}
		});
		btnOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnOpen);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveButtonHandler();
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnSave);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearButtonHandler();
			}
		});
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnClear);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshButtonHandler();
			}
		});
		btnRefresh.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnRefresh);

		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runButtonHandler();
			}
		});
		btnRun.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelETLButtons.add(btnRun);

		JPanel panelParentHolder = new JPanel();
		panelParentHolder.setBackground(Color.WHITE);
		add(panelParentHolder, BorderLayout.CENTER);
		panelParentHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneFirst = new JSplitPane();
		splitPaneFirst.setResizeWeight(0.9);
		splitPaneFirst.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelParentHolder.add(splitPaneFirst, "cell 0 0,grow");

		JPanel panelContainer = new JPanel();
		splitPaneFirst.setLeftComponent(panelContainer);
		panelContainer.setBackground(Color.WHITE);
		panelContainer.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		panelContainer.add(splitPane, "cell 0 0,grow");

		JPanel panelComponent = new JPanel();
		splitPane.setLeftComponent(panelComponent);
		panelComponent.setBackground(Color.WHITE);
		panelComponent.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPanePallete = new JScrollPane();
		panelComponent.add(scrollPanePallete, "cell 0 0,grow");

		JPanel panelComponentPalette = new JPanel();
		scrollPanePallete.setViewportView(panelComponentPalette);
		panelComponentPalette.setBackground(Color.WHITE);
		panelComponentPalette.setLayout(new MigLayout("", "[grow]", "[][][][][]"));

		etlButtonGroup = new ButtonGroup();

		JPanel panelExtraction = new JPanel();
		panelExtraction.setBorder(
				new TitledBorder(null, "Extraction", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		panelExtraction.setBackground(Color.WHITE);
		panelComponentPalette.add(panelExtraction, "cell 0 0,grow");
		panelExtraction.setLayout(new GridLayout(0, 2, 5, 5));

		JButton btnExtsparql = new JButton(SemanticSourceExtractor);
		setMargin(btnExtsparql);
		etlButtonGroup.add(btnExtsparql);
		btnExtsparql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);

			}
		});
		btnExtsparql.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExtraction.add(btnExtsparql);

		JButton btnExtdb = new JButton(EXT_DB);
		setMargin(btnExtdb);
		etlButtonGroup.add(btnExtdb);
		btnExtdb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		btnExtdb.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExtraction.add(btnExtdb);

		JPanel panelTransformation = new JPanel();
		panelTransformation.setBorder(
				new TitledBorder(null, "Transformation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTransformation.setBackground(Color.WHITE);
		panelComponentPalette.add(panelTransformation, "cell 0 1,grow");
		panelTransformation.setLayout(new GridLayout(0, 2, 5, 5));

		JButton btnAboxtbox = new JButton(ABOX2TBOX);
		setMargin(btnAboxtbox);
		btnAboxtbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnAboxtbox);
		btnAboxtbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnAboxtbox);

		JButton btnExpressionHandler = new JButton(TransformationOnLiteral);
		setMargin(btnExpressionHandler);
		btnExpressionHandler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnExpressionHandler);
		btnExpressionHandler.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnExpressionHandler);

		/*
		 * JButton btnDirectMappingGenerator = new JButton(MAPPING_GEN);
		 * setMargin(btnDirectMappingGenerator);
		 * btnDirectMappingGenerator.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) {
		 * paletteButtonHandler(arg0); } });
		 * etlButtonGroup.add(btnDirectMappingGenerator);
		 * btnDirectMappingGenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		 * panelTransformation.add(btnDirectMappingGenerator);
		 */
		
		JButton btnMultipleTransform = new JButton(MULTIPLE_TRANFORM);
		setMargin(btnMultipleTransform);
		btnMultipleTransform.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnMultipleTransform);
		btnMultipleTransform.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnMultipleTransform);

		JButton btnLevelentryGenerator = new JButton(LEVEL_ENTRY_GENERATOR);
		setMargin(btnLevelentryGenerator);
		btnLevelentryGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnLevelentryGenerator);
		btnLevelentryGenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnLevelentryGenerator);

		JButton btnFactentryGenerator = new JButton(FACT_ENTRY_GENERATOR);
		setMargin(btnFactentryGenerator);
		btnFactentryGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnFactentryGenerator);
		btnFactentryGenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnFactentryGenerator);

		JButton btnTboxBuilder = new JButton(NonSemanticToTBoxDeriver);
		setMargin(btnTboxBuilder);
		btnTboxBuilder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paletteButtonHandler(e);
			}
		});
		etlButtonGroup.add(btnTboxBuilder);
		btnTboxBuilder.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnTboxBuilder);

		JButton btnUpdateDimensionConstruct = new JButton(UPDATE_DIMENSION_CONSTRUCT);
		setMargin(btnUpdateDimensionConstruct);
		btnUpdateDimensionConstruct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnUpdateDimensionConstruct);
		btnUpdateDimensionConstruct.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnUpdateDimensionConstruct);

		JButton btnRdfWrapper = new JButton(RDF_WRAPPER);
		setMargin(btnRdfWrapper);
		btnRdfWrapper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnRdfWrapper);
		btnRdfWrapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnRdfWrapper);

		JButton btnInstanceentrygenerator = new JButton(INSTANCE_ENTRY_GENERATOR);
		setMargin(btnInstanceentrygenerator);
		btnInstanceentrygenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnInstanceentrygenerator);
		btnInstanceentrygenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTransformation.add(btnInstanceentrygenerator);

		JPanel panelLinking = new JPanel();
		panelLinking.setBorder(new TitledBorder(null, "Linking", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLinking.setBackground(Color.WHITE);
		panelComponentPalette.add(panelLinking, "cell 0 2,grow");
		panelLinking.setLayout(new GridLayout(0, 2, 5, 5));

		JButton btnResourceRetriever = new JButton(RESOURCE_RETRIEVER);
		setMargin(btnResourceRetriever);
		btnResourceRetriever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnResourceRetriever);
		btnResourceRetriever.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelLinking.add(btnResourceRetriever);

		JButton btnPweightGenerator = new JButton(PWEIGHT_GENERATOR);
		setMargin(btnPweightGenerator);
		btnPweightGenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPweightGenerator.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnPweightGenerator);
		panelLinking.add(btnPweightGenerator);

		JButton btnSbagGenerator = new JButton(SBAG_GENERATOR);
		setMargin(btnSbagGenerator);
		btnSbagGenerator.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnSbagGenerator);
		btnSbagGenerator.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelLinking.add(btnSbagGenerator);

		JButton btnMatcher = new JButton("Matcher");
		setMargin(btnMatcher);
		btnMatcher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnMatcher);
		btnMatcher.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelLinking.add(btnMatcher);

		JPanel panelLoading = new JPanel();
		panelLoading.setBackground(Color.WHITE);
		panelLoading.setBorder(new TitledBorder(null, "Load", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelComponentPalette.add(panelLoading, "cell 0 3,grow");
		panelLoading.setLayout(new GridLayout(1, 0, 5, 5));

		JButton btnLoading = new JButton(LOADER);
		setMargin(btnLoading);
		btnLoading.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnLoading);
		btnLoading.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelLoading.add(btnLoading);

		JPanel panelAssociation = new JPanel();
		panelAssociation
				.setBorder(new TitledBorder(null, "Control Flow", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAssociation.setBackground(Color.WHITE);
		panelComponentPalette.add(panelAssociation, "cell 0 4,grow");
		panelAssociation.setLayout(new GridLayout(1, 0, 5, 5));

		JButton btnAssociation = new JButton();
		btnAssociation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				paletteButtonHandler(arg0);
			}
		});
		etlButtonGroup.add(btnAssociation);
		btnAssociation.setIcon(new ImageIcon(PanelETL.class.getResource("/images/arrow.png")));
		panelAssociation.add(btnAssociation);

		JPanel panelGraphContainer = new JPanel();
		splitPane.setRightComponent(panelGraphContainer);
		panelGraphContainer.setBackground(Color.WHITE);
		panelGraphContainer.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneHolder = new JScrollPane();
		scrollPaneHolder.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneHolder.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelGraphContainer.add(scrollPaneHolder, "cell 0 0,grow");

		/*
		 * JPanel panelGraphHolder = new JPanel();
		 * scrollPaneHolder.setViewportView(panelGraphHolder);
		 * panelGraphHolder.setBackground(Color.WHITE); panelGraphHolder.setLayout(new
		 * MigLayout("", "[grow]", "[grow]"));
		 */

		graphPanel = new Graph();
		scrollPaneHolder.setViewportView(graphPanel);
		// panelGraphHolder.add(graphPanel, "cell 0 0,grow");

		JPanel panelTextContainer = new JPanel();
		splitPaneFirst.setRightComponent(panelTextContainer);
		panelTextContainer.setBackground(Color.WHITE);
		panelTextContainer.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneText = new JScrollPane();
		panelTextContainer.add(scrollPaneText, "cell 0 0,grow");

		textPaneETLStatus = new JTextPane();
		textPaneETLStatus.setEditable(false);
		textPaneETLStatus.setText("ETL STATUS:");
		textPaneETLStatus.setFont(new Font("Tahoma", Font.BOLD, 14));
		scrollPaneText.setViewportView(textPaneETLStatus);

		allOperations = new ArrayList<>();
		allArrows = new ArrayList<>();
	}

	private void setMargin(JButton button) {
		// TODO Auto-generated method stub
		button.setMargin(new Insets(10, 5, 10, 5));
	}

	protected void etlNewButtonHandler() {
		// TODO Auto-generated method stub

	}

	public void saveButtonHandler() {
		Methods methods = new Methods();
		ETLOperationToXML etlOperationToXML = new ETLOperationToXML();
		
		String etlXMLString = etlOperationToXML.getXMLOfOperations(allOperations, allArrows);
		String defaultName = "ETL_Flow_" + methods.getDateTime() + ".xml";
		String filePath = methods.chooseSaveFile("", defaultName, "Select location to save ETL");
		if (!filePath.endsWith(".xml"))
			filePath += ".xml";
		
		//System.out.println(filePath);
		//System.out.println(etlXMLString);
		methods.writeText(filePath, etlXMLString);
	}

	public void clearButtonHandler() {
		graphPanel.clearPanel();
		textPaneETLStatus.setText("ETL STATUS:");
		desectAllPaletteButton();
	}

	// handler for etlOpen Button

	public void etlOpenButtonHandler() {

		Methods methods = new Methods();
		String filePath = methods.chooseFile("Open XML file of ETL");
		ParseXML parseXML = new ParseXML();
		// String xml =

		ArrayList<Operation> allOperationsFromXML = parseXML.getETLFromXML(filePath, this);
		// System.out.println(xml);

		this.allOperations = allOperationsFromXML;

		this.allArrows = parseXML.getETLAssiciationsFromXML(filePath, this, allOperationsFromXML);

		this.repaint();
	}

	// handler for click on paltte buttons
	public void paletteButtonHandler(ActionEvent arg0) {

		selectedButton = (JButton) arg0.getSource();
		updateColor((JButton) arg0.getSource());
	}

	// update the color of buttons
	private void updateColor(JButton selectedButton) {

		Enumeration<AbstractButton> paletteButton = etlButtonGroup.getElements();

		while (paletteButton.hasMoreElements()) {
			JButton button = (JButton) paletteButton.nextElement();

			if (button == selectedButton) {
				button.setBackground(Color.decode("#FFFFFF"));
			} else {
				button.setBackground(defaultButtonBGColor);

			}
		}

	}

	private void desectAllPaletteButton() {

		selectedButton = null;
		Enumeration<AbstractButton> paletteButton = etlButtonGroup.getElements();

		while (paletteButton.hasMoreElements()) {
			JButton button = (JButton) paletteButton.nextElement();
			button.setBackground(defaultButtonBGColor);
		}

	}

	// check whether the associations between the operations of ETL are done
	// correctly
	protected boolean checkAssociation() {

		ArrayList<Operation> associatedOperation = new ArrayList<>();

		for (Arrow arrow : allArrows) {
			associatedOperation.add(arrow.getTargetOperation());
		}

		for (Operation operation : allOperations) {
			if (!associatedOperation.contains(operation) && !operation.getOperationName().equals("Start")) {
				return false;
			}
		}

		return true;
	}

	// Execute the ETL operation
	protected boolean executeETL() {

		// Execute the operations as in order..
		// First all mapping gen, then TBox... etc
		ArrayList<Operation> tboxBuilderOperation = getNamedOperations(NonSemanticToTBoxDeriver);
		ArrayList<Operation> aBoxToTBoxOperations = getNamedOperations(ABOX2TBOX);
		ArrayList<Operation> rdfWrapperOperation = getNamedOperations(RDF_WRAPPER);
		ArrayList<Operation> extractDBOpertations = getNamedOperations(EXT_DB);
		ArrayList<Operation> extractSPAQRQLOpertations = getNamedOperations(SemanticSourceExtractor);
		ArrayList<Operation> expressionHandlerOperations = getNamedOperations(TransformationOnLiteral);
		ArrayList<Operation> expressionMultipleTransform = getNamedOperations(MULTIPLE_TRANFORM);
		ArrayList<Operation> levelEntryGenOpertations = getNamedOperations(LEVEL_ENTRY_GENERATOR);
		ArrayList<Operation> factEntryGenOpertations = getNamedOperations(FACT_ENTRY_GENERATOR);
		ArrayList<Operation> instanceEntryGenOpertations = getNamedOperations(INSTANCE_ENTRY_GENERATOR);
		ArrayList<Operation> updateDimensionOpertations = getNamedOperations(UPDATE_DIMENSION_CONSTRUCT);
		ArrayList<Operation> mappingGenOperations = getNamedOperations(MAPPING_GEN);
		// ArrayList<Operation> tBoxGenOperations = getNamedOperations(TBOX_GEN);
		// ArrayList<Operation> aBoxGenOperations = getNamedOperations(ABOX_GEN);

		ArrayList<Operation> resourceRetrieverOperations = getNamedOperations(RESOURCE_RETRIEVER);
		ArrayList<Operation> pWeightGenOperations = getNamedOperations(PWEIGHT_GENERATOR);
		ArrayList<Operation> sBagGenOperations = getNamedOperations(SBAG_GENERATOR);
		ArrayList<Operation> matcherOperations = getNamedOperations(MATCHER);
		ArrayList<Operation> loadingOperations = getNamedOperations(LOADER);

		for (Operation operation : tboxBuilderOperation) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : aBoxToTBoxOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : rdfWrapperOperation) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : extractDBOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : extractSPAQRQLOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : expressionHandlerOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}
		
		for (Operation operation : expressionMultipleTransform) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : levelEntryGenOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : factEntryGenOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : instanceEntryGenOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : updateDimensionOpertations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : mappingGenOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		/*
		 * for (Operation operation : tBoxGenOperations) {
		 * operation.getEtlOperation().execute(textPaneETLStatus); }
		 * 
		 * for (Operation operation : aBoxGenOperations) {
		 * operation.getEtlOperation().execute(textPaneETLStatus); }
		 */

		for (Operation operation : resourceRetrieverOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : pWeightGenOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : sBagGenOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : matcherOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		for (Operation operation : loadingOperations) {
			operation.getEtlOperation().execute(textPaneETLStatus);
		}

		// Loading: get RDFs from loading operations and load them in a common
		// Jena model

		// Commenting this as I don't think it is necessary ---- Amrit
		/*
		 * ArrayList<String> rdfFilePaths = new ArrayList<>();
		 * 
		 * boolean isLoadingFound = false; for (Operation operation : loadingOperations)
		 * {
		 * 
		 * ETLLoadingOperation etlLoadingOperation = (ETLLoadingOperation)
		 * operation.getEtlOperation();
		 * rdfFilePaths.add(etlLoadingOperation.getInputFilePath()); isLoadingFound =
		 * true; }
		 * 
		 * // if Loading operation found, show the Query panel if (isLoadingFound) {
		 * queryPanel = new ETLSparqlQuery(rdfFilePaths); this.add(queryPanel,
		 * BorderLayout.CENTER); graphPanel.setVisible(false);
		 * panelETLButtons.setVisible(false); panelComponentPalette.setVisible(false); }
		 */

		return false;
	}

	// return operation object by name
	private ArrayList<Operation> getNamedOperations(String opName) {
		// System.out.println(opName);
		ArrayList<Operation> temp = new ArrayList<>();

		for (Operation operation : allOperations) {
			// System.out.println(operation.operationName);
			if (operation.getOperationName().equals(opName))
				temp.add(operation);
		}

		// System.out.println(temp.size());
		return temp;
	}

	// check whether all inputs to all operations are done or not
	protected boolean checkStatus() {

		for (Operation operation : allOperations) {
			if (!operation.inputStatus)
				return false;
		}
		return true;
	}

	public void refreshButtonHandler() {
		graphPanel.refreshPanel();
		textPaneETLStatus.setText("ETL STATUS:");
	}

	public void runButtonHandler() {
		textPaneETLStatus.setText("ETL STATUS:");

		boolean checkStatus = checkStatus();
		boolean checkAssociation = checkAssociation();
		if (checkStatus) {
			if (checkAssociation) {
				executeETL();
			} else {
				JOptionPane.showMessageDialog(null, "Please connect all Operation to flow.");
			}

		} else {
			JOptionPane.showMessageDialog(null, "Please Configure all Operations.");
		}
	}

	// Panel to draw the graph
	class Graph extends JPanel implements MouseListener, MouseMotionListener {

		Graphics2D g2d;

		// Association map contains association, i.e., which operation can
		// follow which one
		HashMap<String, ArrayList<String>> associationMap;

		// Inputs given to a operation is are saved in some category so that
		// they can be suggested in other operations

		HashMap<String, LinkedHashSet<String>> inputMap;

		final static String DB_NAME = "DB Name:";
		final static String DB_USER_NAME = "DB User Name:";
		final static String DB_USER_PASSWORD = "DB User Passoword:";
		final static String BASE_IRI = "Base IRI:";
		final static String FILE_PATH = "File Location:";
		final static String FILE_NAME = "File Name:";

		final static String KEY_WORD = "Key Word:";
		final static String DBPEDIA_DATA_FILE = "DBpedia Data File:";
		final static String RESOURCE_FILE = "Resource File:";
		final static String PROPERTY_WEIGHT_FILE = "Property Weight File:";
		final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";
		final static String RDF_FILE = "RDF File:";
		final static String MAPPING_GRAPH_FILE = "Mapping Graph File:";
		final static String TBox_FILE = "TBox File:";
		final static String LEVEL_FILE = "Level File:";
		final static String FACT_FILE = "Fact File:";
		final static String INSTANCE_FILE = "Instance File:";
		final static String DB_FILE = "Db File:";
		final static String SPARQL_FILE = "Sparql File:";
		final static String EXPRESSION_FILE = "Expression File:";
		final static String RDF_MAPPER = "RDF Mapper:";

		boolean isDragged = false;

		Operation selectedOperation;
		String dbName, dbUserName, dbUserPassword, r2rmlPath, r2rmlMappingFileName, tBoxPath, tBoxName;

		public Graph() {
			setBorder(new LineBorder(new Color(0, 0, 0), 1));
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			setLayout(new BorderLayout(0, 0));
			allOperations = new ArrayList<>();
			allArrows = new ArrayList<>();
			associationMap = getAssociationMap();
			inputMap = getInitializedInputMap();
			setBackground(Color.WHITE);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(3000, 3000);
		}

		public void refreshPanel() {

			desectAllPaletteButton();
			repaint();

		}

		public void clearPanel() {
			int option = JOptionPane.showConfirmDialog(null, "Are you sure?\nThis will clear all the ETL Flow.",
					"Confirmation", JOptionPane.YES_NO_OPTION);
			if (option == 0) {

				allOperations = new ArrayList<>();
				allArrows = new ArrayList<>();
				repaint();

			}
		}

		// initilize the input map
		private HashMap<String, LinkedHashSet<String>> getInitializedInputMap() {

			HashMap<String, LinkedHashSet<String>> inputMap = new HashMap<>();

			LinkedHashSet<String> stringList = new LinkedHashSet<>();
			inputMap.put(DB_NAME, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(DB_USER_NAME, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(DB_USER_PASSWORD, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(BASE_IRI, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(FILE_PATH, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(FILE_NAME, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(MAPPING_GRAPH_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(RDF_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(TBox_FILE, stringList);

			// Params for for level entry

			stringList = new LinkedHashSet<>();
			inputMap.put(LEVEL_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(FACT_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(SPARQL_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(EXPRESSION_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(RDF_MAPPER, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(NonSemanticToTBoxDeriver, stringList);

			// Params for for linking

			stringList = new LinkedHashSet<>();
			inputMap.put(KEY_WORD, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(DBPEDIA_DATA_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(RESOURCE_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(PROPERTY_WEIGHT_FILE, stringList);

			stringList = new LinkedHashSet<>();
			inputMap.put(SEMANTIC_BAG_FILE, stringList);

			return inputMap;
		}

		private HashMap<String, ArrayList<String>> getAssociationMap() {

			// This method returns the map containing the association between
			// the operations

			HashMap<String, ArrayList<String>> associations = new HashMap<>();

			// This array list contains the operation that can be done after the
			// particular operation

			// Association for Start
			ArrayList<String> association = new ArrayList<>();
			association.add(SemanticSourceExtractor);
			association.add(EXT_DB);
			association.add(MAPPING_GEN);
			association.add(TransformationOnLiteral);
			association.add(MULTIPLE_TRANFORM);
			association.add(RDF_WRAPPER);
			association.add(NonSemanticToTBoxDeriver);
			association.add(ABOX2TBOX);
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(INSTANCE_ENTRY_GENERATOR);
			association.add(UPDATE_DIMENSION_CONSTRUCT);
			association.add(RESOURCE_RETRIEVER);
			association.add(PWEIGHT_GENERATOR);
			association.add(SBAG_GENERATOR);
			association.add(MATCHER);
			association.add(LOADER);
			associations.put(START, association);

			// Association for T_BOX_BUILDER
			association = new ArrayList<>();
			associations.put(NonSemanticToTBoxDeriver, association);

			// Association for Abox2tbox
			association = new ArrayList<>();
			associations.put(ABOX2TBOX, association);

			// Association for RDF_WRAPPER
			association = new ArrayList<>();
			association.add(SemanticSourceExtractor);
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(INSTANCE_ENTRY_GENERATOR);
			association.add(UPDATE_DIMENSION_CONSTRUCT);
			associations.put(RDF_WRAPPER, association);

			// Association for ExtDB
			association = new ArrayList<>();
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(INSTANCE_ENTRY_GENERATOR);
			associations.put(EXT_DB, association);

			// Association for ExtSPARQL
			association = new ArrayList<>();
			association.add(TransformationOnLiteral);
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(INSTANCE_ENTRY_GENERATOR);
			association.add(UPDATE_DIMENSION_CONSTRUCT);
			associations.put(SemanticSourceExtractor, association);

			// Association for EXPRESSION_HANDLER
			association = new ArrayList<>();
			association.add(RDF_WRAPPER);
			association.add(NonSemanticToTBoxDeriver);
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(UPDATE_DIMENSION_CONSTRUCT);
			associations.put(TransformationOnLiteral, association);
			
			// Association for MULTIPLE TRANSFORM
			association = new ArrayList<>();
			association.add(RDF_WRAPPER);
			association.add(NonSemanticToTBoxDeriver);
			association.add(LEVEL_ENTRY_GENERATOR);
			association.add(FACT_ENTRY_GENERATOR);
			association.add(UPDATE_DIMENSION_CONSTRUCT);
			associations.put(MULTIPLE_TRANFORM, association);

			// Association for Mapping generation
			association = new ArrayList<>();
			association.add(RDF_WRAPPER);
			association.add(NonSemanticToTBoxDeriver);
			association.add(ABOX2TBOX);
			associations.put(MAPPING_GEN, association);

			// Association for LEVEL_ENTRY_GENERATOR
			association = new ArrayList<>();
			association.add(LOADER);
			associations.put(LEVEL_ENTRY_GENERATOR, association);

			// Association for FACT_ENTRY_GENERATOR
			association = new ArrayList<>();
			association.add(LOADER);
			associations.put(FACT_ENTRY_GENERATOR, association);

			// Association for INSTANCE_GENERATOR
			association = new ArrayList<>();
			association.add(LOADER);
			associations.put(INSTANCE_ENTRY_GENERATOR, association);

			// Association for Update Dimensional Construct
			association = new ArrayList<>();
			association.add(LOADER);
			associations.put(UPDATE_DIMENSION_CONSTRUCT, association);

			// Association for Resource Retriever
			association = new ArrayList<>();
			association.add(PWEIGHT_GENERATOR);
			association.add(SBAG_GENERATOR);
			association.add(MATCHER);
			association.add(LOADER);
			associations.put(RESOURCE_RETRIEVER, association);

			// Association for PWeight Generator

			association = new ArrayList<>();
			association.add(SBAG_GENERATOR);
			association.add(MATCHER);
			association.add(LOADER);
			associations.put(PWEIGHT_GENERATOR, association);

			// Association for SBag Generator

			association = new ArrayList<>();
			association.add(MATCHER);
			association.add(LOADER);
			associations.put(SBAG_GENERATOR, association);

			// Association for Matcher
			association = new ArrayList<>();
			association.add(LOADER);
			associations.put(MATCHER, association);

			// Association for Loading
			association = new ArrayList<>();
			associations.put(LOADER, association);

			return associations;
		}

		public void paintComponent(Graphics g) {

			// System.out.println("Paint Called");
			g2d = (Graphics2D) g;
			this.removeAll();
			super.paintComponent(g);
			// this.updateUI();

			// For first time initialization of panel
			if (allOperations.size() <= 0) {

				Operation startOperation = new Operation("Start", 50, 150);
				startOperation.setInputStatus(true);
				startOperation.setOperationShape(
						drawStart(g2d, startOperation.getUpperLeftX(), startOperation.getUpperLeftY()));

				allOperations.add(startOperation);
			} else {

				for (Operation operation : allOperations) {

					String operationName = operation.getOperationName();

					if (operationName.equals("Start")) {
						Shape newShape = drawStart(g2d, operation.getUpperLeftX(), operation.getUpperLeftY());
						operation.setOperationShape(newShape);

					} else {

						Shape oldShape = operation.getOperationShape();

						Color rectColor;

						if (operation.isInputStatus()) {
							rectColor = Color.decode("#0000FF");
						} else {
							rectColor = Color.RED;
						}

						Shape newShape = drawRoundRectangle(g2d, operation.getUpperLeftX(), operation.getUpperLeftY(),
								operationName, rectColor);
						operation.setOperationShape(newShape);
					}
				}
			}

			for (Arrow arrow : allArrows) {

				// draw the arrows

				ArrayList<Point> drawPoints = getDrawPoints(arrow.getSourceOperation().getOperationShape(),
						arrow.getTargetOperation().getOperationShape());

				Point startPoint = drawPoints.get(0);
				Point endPoint = drawPoints.get(1);

				ArrayList<Shape> arrowSeg = drawArrow(g2d, startPoint.x, startPoint.y, endPoint.x, endPoint.y,
						Color.BLACK);

				arrow.setArrowSegments(arrowSeg);
			}

		}

		private Shape drawStart(Graphics2D graphics2d, double d, double e) {

			Ellipse2D startShape = new Ellipse2D.Double(d, e, 80, 80);

			graphics2d.setStroke(new BasicStroke(4));
			graphics2d.setColor(Color.BLACK);
			graphics2d.draw(startShape);
			Ellipse2D startShapeFill = new Ellipse2D.Double(d + 2, e + 2, 76, 76);
			graphics2d.setColor(Color.GREEN);
			graphics2d.fill(startShapeFill);
			graphics2d.setColor(Color.BLACK);

			Font font = new Font("Tahoma", Font.BOLD, 18);
			graphics2d.setFont(font);

			FontMetrics fontMetrics = graphics2d.getFontMetrics();

			// Determine the X coordinate for the text
			int xCor = (int) (startShape.getX() + (startShape.getWidth() - fontMetrics.stringWidth("Start")) / 2);
			// Determine the Y coordinate for the text (note we add the ascent,
			// as
			// in java 2d 0 is top of the screen)
			int yCor = (int) (startShape.getY() + ((startShape.getHeight() - fontMetrics.getHeight()) / 2)
					+ fontMetrics.getAscent());
			// Draw the String
			graphics2d.drawString("Start", xCor, yCor);

			return startShape;
		}

		private ArrayList<Shape> drawArrow(Graphics2D graphics2d, int startX, int startY, int endX, int endY,
				Color color) {

			// contains the segments of an arrow
			ArrayList<Shape> arrowSegments = new ArrayList<>();

			graphics2d.setStroke(new BasicStroke(3));
			graphics2d.setColor(color);
			// temp variable to store line segments
			Line2D tempLine2d;

			if (startX == endX && startY < endY) {
				// Vertical Down

				if (endY - startY > 10) {

					tempLine2d = new Line2D.Double(startX, startY, endX, endY - 10);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 5, endY - 10);
					arrowHead.addPoint(startX + 5, endY - 10);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {

					// lowDistance, just draw the arrow head

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 5, startY);
					arrowHead.addPoint(startX + 5, startY);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startX == endX && startY > endY) {
				// vertical Up

				if (startY - endY > 10) {

					tempLine2d = new Line2D.Double(startX, startY, endX, endY + 10);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 5, endY + 10);
					arrowHead.addPoint(startX + 5, endY + 10);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {

					// lowDistance, just draw the arrow head

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 5, startY);
					arrowHead.addPoint(startX + 5, startY);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY == endY && startX < endX) {
				// Horizontal right

				if (endX - startX > 10) {

					tempLine2d = new Line2D.Double(startX, startY, endX - 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(endX - 10, endY - 5);
					arrowHead.addPoint(endX - 10, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {

					// lowDistance, just draw the arrow head

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX, startY - 5);
					arrowHead.addPoint(startX, startY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY == endY && startX > endX) {
				// Horizontal left

				if (startX - endX > 10) {

					tempLine2d = new Line2D.Double(startX, startY, endX + 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 5, startY);
					arrowHead.addPoint(startX + 5, startY);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {

					// lowDistance, just draw the arrow head

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX, startY - 5);
					arrowHead.addPoint(startX, startY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY < endY && startX > endX) {
				// Lower Left

				int distance = startX - endX;
				if (distance >= 12) {

					int movePos = (distance - 10) / 2;

					tempLine2d = new Line2D.Double(startX, startY, startX - movePos, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - movePos, startY, startX - movePos, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - movePos, endY, endX + 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(endX + 10, endY - 5);
					arrowHead.addPoint(endX + 10, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {
					// not enough distance

					tempLine2d = new Line2D.Double(startX, startY, startX - 1, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - 1, startY, startX - 1, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - 1, endY, startX - 2, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 2, endY - 5);
					arrowHead.addPoint(startX - 2, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY < endY && startX < endX) {
				// Lower right

				int distance = endX - startX;
				if (distance >= 12) {

					int movePos = (distance - 10) / 2;

					tempLine2d = new Line2D.Double(startX, startY, startX + movePos, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + movePos, startY, startX + movePos, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + movePos, endY, endX - 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(endX - 10, endY - 5);
					arrowHead.addPoint(endX - 10, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {
					// not enough distance

					tempLine2d = new Line2D.Double(startX, startY, startX + 1, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + 1, startY, startX + 1, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + 1, endY, startX + 2, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX + 2, endY - 5);
					arrowHead.addPoint(startX + 2, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY > endY && startX > endX) {
				// Upper Left

				int distance = startX - endX;
				if (distance >= 12) {

					int movePos = (distance - 10) / 2;

					tempLine2d = new Line2D.Double(startX, startY, startX - movePos, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - movePos, startY, startX - movePos, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - movePos, endY, endX + 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(endX + 10, endY - 5);
					arrowHead.addPoint(endX + 10, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {
					// not enough distance

					tempLine2d = new Line2D.Double(startX, startY, startX - 1, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - 1, startY, startX - 1, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX - 1, endY, startX - 2, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX - 2, endY - 5);
					arrowHead.addPoint(startX - 2, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			} else if (startY > endY && startX < endX) {
				// Upper right

				int distance = endX - startX;
				if (distance >= 12) {

					int movePos = (distance - 10) / 2;

					tempLine2d = new Line2D.Double(startX, startY, startX + movePos, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + movePos, startY, startX + movePos, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + movePos, endY, endX - 10, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(endX - 10, endY - 5);
					arrowHead.addPoint(endX - 10, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				} else {
					// not enough distance

					tempLine2d = new Line2D.Double(startX, startY, startX + 1, startY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + 1, startY, startX + 1, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					tempLine2d = new Line2D.Double(startX + 1, endY, startX + 2, endY);
					graphics2d.draw(tempLine2d);
					arrowSegments.add(tempLine2d);

					Polygon arrowHead = new Polygon();
					arrowHead.addPoint(startX + 2, endY - 5);
					arrowHead.addPoint(startX + 2, endY + 5);
					arrowHead.addPoint(endX, endY);
					graphics2d.fill(arrowHead);
					arrowSegments.add(arrowHead);

				}

			}
			return arrowSegments;
		}

		private Operation getSelectedOperation(int x, int y) {

			Shape selectedShape = null;
			for (Operation operation : allOperations) {
				selectedShape = operation.getOperationShape();
				if (selectedShape.contains(x, y))
					break;
			}

			for (Operation operation : allOperations) {

				Shape shape = operation.getOperationShape();

				if (shape == selectedShape) {
					return operation;
				}

			}

			return null;
		}

		private RoundRectangle2D drawRoundRectangle(Graphics2D graphics2d, double x, double y, String text,
				Color color) {

			graphics2d.setColor(color);
			graphics2d.setStroke(new BasicStroke(3));

			// graphics2d.setColor(Color.RED);

			// Default Height = 100, default width = 200
			RoundRectangle2D rectangle = new RoundRectangle2D.Double(x, y, 200, 50, 10, 10);
			graphics2d.draw(rectangle);

			Font font = new Font("Tahoma", Font.BOLD, 12);
			graphics2d.setFont(font);

			FontMetrics fontMetrics = graphics2d.getFontMetrics();

			// Determine the X coordinate for the text
			int xCor = (int) (rectangle.getX() + (rectangle.getWidth() - fontMetrics.stringWidth(text)) / 2);
			// Determine the Y coordinate for the text (note we add the ascent,
			// as
			// in java 2d 0 is top of the screen)
			int yCor = (int) (rectangle.getY() + ((rectangle.getHeight() - fontMetrics.getHeight()) / 2)
					+ fontMetrics.getAscent());
			// Draw the String
			graphics2d.setColor(Color.BLACK);
			graphics2d.drawString(text, xCor, yCor);

			return rectangle;

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {

			if (arg0.getClickCount() == 2) {

				if (SwingUtilities.isLeftMouseButton(arg0)) {

					if (selectedButton != null) {

						String selectedOpName = "";

						if (selectedButton.getIcon() != null) {
							selectedOpName = "Association";
						} else {
							selectedOpName = selectedButton.getText().toString();
						}

						if (!selectedOpName.equals("Association")) {
							Operation newOperation = new Operation(selectedOpName, arg0.getX(), arg0.getY());
							setOperation(newOperation);
							allOperations.add(newOperation);
							desectAllPaletteButton();
							repaint();
						}
					} else {

						Operation operation = getSelectedOperation(arg0.getX(), arg0.getY());

						if (!operation.getOperationName().equals("Start")) {
							// initialize the input file list
							// setInputFileList(operation.getEtlOperation());
							// take input

							boolean isInputTaken = operation.getEtlOperation().getInput(this, inputMap);

							if (isInputTaken) {
								if (!operation.isInputStatus()) {
									operation.setInputStatus(isInputTaken);
									repaint();
								}
							}

						}

					}

				} else {

					if (SwingUtilities.isRightMouseButton(arg0)) {

						boolean found = false;
						int option = -1;
						ArrayList<Integer> arrowIndexes = new ArrayList<>();
						int operationIndex = -1;

						for (Operation operation : allOperations) {

							Shape shape = operation.getOperationShape();

							if (shape.contains(arg0.getX(), arg0.getY())) {

								found = true;
								option = JOptionPane.showConfirmDialog(null, "Are you sure to delete this "
										+ operation.getOperationName()
										+ " operation?\nAll association to this operation also will be deleted.",
										"Confirmation", JOptionPane.YES_NO_OPTION);
								if (option == 0) {
									for (Arrow arrow : allArrows) {
										if (arrow.targetOperation == operation || arrow.sourceOperation == operation) {
											arrowIndexes.add(allArrows.indexOf(arrow));
										}
									}
									operationIndex = allOperations.indexOf(operation);
									break;
								}
							}
						}

						if (found) {

							if (option == 0) {

								for (int index : arrowIndexes) {

									allArrows.remove(index);
								}
								allOperations.remove(operationIndex);
								repaint();
								return;

							}

						} else {

							int arrowIndex = -1;
							for (Arrow arrow : allArrows) {

								ArrayList<Shape> arrowSegments = arrow.getArrowSegments();

								for (Shape shape : arrowSegments) {
									if (shape.contains(arg0.getX(), arg0.getY())) {

										found = true;
										break;
									}
								}

								arrowIndex = allArrows.indexOf(arrow);
								if (found)
									break;

							}

							if (found) {

								int op = JOptionPane.showConfirmDialog(null,
										"Are you sure to delete association between \n"
												+ allArrows.get(arrowIndex).getSourceOperation().getOperationName()
												+ " and "
												+ allArrows.get(arrowIndex).getTargetOperation().getOperationName(),
										"Confirmation", JOptionPane.YES_NO_OPTION);

								if (op == 0) {

									allArrows.remove(arrowIndex);
									repaint();
									return;

								}

							}
						}

					}

				}

			} else {

				if (SwingUtilities.isLeftMouseButton(arg0)) {

					for (Operation operation : allOperations) {

						Shape shape = operation.getOperationShape();

						if (shape.contains(arg0.getX(), arg0.getY())) {
							break;
						}
					}

					for (Arrow arrow : allArrows) {

						boolean found = false;
						ArrayList<Shape> arrowSegments = arrow.getArrowSegments();

						for (Shape shape : arrowSegments) {
							if (shape.contains(arg0.getX(), arg0.getY())) {

								found = true;
								break;
							}
						}

						if (found)
							break;

					}
				}

			}

			if (selectedButton != null) {
				if (SwingUtilities.isLeftMouseButton(arg0)) {
					String selectedOpName = "";

					if (selectedButton.getIcon() != null) {
						selectedOpName = "Association";
					} else {
						selectedOpName = selectedButton.getText().toString();
					}
				}
			}

		}

		// return the path of all mapping graph output files
		private ArrayList<String> getMappingGraphFiles() {

			ArrayList<String> mappingGraphFilesPath = new ArrayList<>();
			for (Operation operation : allOperations) {

				if (operation.getOperationName().equals("Mapping Generation")) {

					ETLMappingGenOperation mapGenOperation = (ETLMappingGenOperation) operation.getEtlOperation();
					mappingGraphFilesPath.add(mapGenOperation.getFileSavingPath());
				}
			}

			return mappingGraphFilesPath;
		}

		// return the path of all RDF output Files
		private ArrayList<String> getRDFFiles() {

			ArrayList<String> rdfFilesPath = new ArrayList<>();
			for (Operation operation : allOperations) {

				if (operation.getOperationName().equals("ABox Generation")) {

					ETLABoxGenOperation aBoxGenOperation = (ETLABoxGenOperation) operation.getEtlOperation();
					rdfFilesPath.add(aBoxGenOperation.getFileSavingPath());
				}

			}

			return rdfFilesPath;
		}

		private boolean setOperation(Operation operation) {

			String operationName = operation.getOperationName();
			boolean isMatched = false;
			switch (operationName) {

			case SemanticSourceExtractor:
				operation.setEtlOperation(new ETLExtractionSPARQL());
				isMatched = true;
				break;

			case EXT_DB:
				operation.setEtlOperation(new ETLExtractionDB());
				isMatched = true;
				break;

			case TransformationOnLiteral:
				operation.setEtlOperation(new ETLExpressionHandler());
				isMatched = true;
				break;

			case RDF_WRAPPER:
				operation.setEtlOperation(new ETLRDFWrapper());
				isMatched = true;
				break;

			case NonSemanticToTBoxDeriver:
				operation.setEtlOperation(new ETLTBoxBuilder());
				isMatched = true;
				break;

			case ABOX2TBOX:
				operation.setEtlOperation(new ETLABox2TBox());
				isMatched = true;
				break;
				
			case MULTIPLE_TRANFORM:
				operation.setEtlOperation(new ETLMultipleTransform());
				isMatched = true;
				break;

			case LEVEL_ENTRY_GENERATOR:
				operation.setEtlOperation(new ETLLevelEntryGenerator());
				isMatched = true;
				break;

			case INSTANCE_ENTRY_GENERATOR:
				operation.setEtlOperation(new ETLInstanceEntryGenerator());
				isMatched = true;
				break;

			case FACT_ENTRY_GENERATOR:
				operation.setEtlOperation(new ETLFactEntryGenerator());
				isMatched = true;
				break;

			case UPDATE_DIMENSION_CONSTRUCT:
				operation.setEtlOperation(new ETLUpdateDimensionalConstruct());
				isMatched = true;
				break;

			case MAPPING_GEN:
				operation.setEtlOperation(new ETLMappingGenOperation());
				isMatched = true;
				break;

			case LOADER:
				operation.setEtlOperation(new ETLLoadingOperation());
				isMatched = true;
				break;

			case RESOURCE_RETRIEVER:
				operation.setEtlOperation(new ETLResourceRetreiver());
				isMatched = true;
				break;

			case PWEIGHT_GENERATOR:
				operation.setEtlOperation(new ETLPWeightGenerator());
				isMatched = true;
				break;

			case SBAG_GENERATOR:
				operation.setEtlOperation(new ETLSBagGenerator());
				isMatched = true;
				break;

			case MATCHER:
				operation.setEtlOperation(new ETLMatcher());
				isMatched = true;
				break;

			}

			if (isMatched)
				return true;
			else
				return false;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			// System.out.println("Entered");
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			// System.out.println("Exited");

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// System.out.println("Pressed");

			if (SwingUtilities.isLeftMouseButton(e)) {
				selectedOperation = getSelectedOperation(e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// System.out.println("Released");

			if (SwingUtilities.isLeftMouseButton(e)) {
				String selectedOpName = "";

				if (selectedButton != null) {

					if (selectedButton.getIcon() != null) {
						selectedOpName = "Association";
					} else {
						selectedOpName = selectedButton.getText().toString();
					}

				}

				if (isDragged && selectedOpName.equals("Association")) {

					int x = e.getX();
					int y = e.getY();

					for (Operation operation : allOperations) {

						Shape shape = operation.getOperationShape();

						if (shape.contains(x, y) && selectedOperation != getSelectedOperation(e.getX(), e.getY())) {

							ArrayList<String> associationList = associationMap
									.get(selectedOperation.getOperationName().toString());

							if (associationList != null && associationList.contains(operation.getOperationName())) {

								selectedOperation.getNextOperations().add(operation);
								Arrow arrow = new Arrow(selectedOperation, operation);
								allArrows.add(arrow);
								repaint();
								isDragged = false;

								desectAllPaletteButton();

								break;

							} else {

								// System.out.println("Association List = " + associationList);
								// System.out.println(operation.getOperationName());
								JOptionPane.showMessageDialog(null,
										"Association from " + selectedOperation.getOperationName() + " to "
												+ operation.getOperationName() + " is invalid!");
								break;
							}
						}

					}

				}

				selectedOperation = null;
				isDragged = false;

			}

		}

		@Override
		public void mouseDragged(MouseEvent arg0) {

			if (SwingUtilities.isLeftMouseButton(arg0)) {

				isDragged = true;

				if (selectedOperation != null && selectedButton == null) {
					selectedOperation.setUpperLeftX(arg0.getX());
					selectedOperation.setUpperLeftY(arg0.getY());

					repaint();
				}
			}

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {

		}

		private Operation getOperation(Shape shape) {

			for (Operation operation : allOperations) {

				if (operation.getOperationShape() == shape) {
					return operation;
				}
			}
			return null;
		}

		private ArrayList<Point> getDrawPoints(Shape sourceShape, Shape targetShape) {

			ArrayList<Point> points = new ArrayList<>();

			Point pointSource = new Point(0, 0);
			Point pointDestination = new Point(0, 0);

			if (sourceShape instanceof Ellipse2D) {

				Ellipse2D sourceEllipse = (Ellipse2D) sourceShape;

				double sourceMinX = sourceEllipse.getMinX();
				double sourceMinY = sourceEllipse.getMinY();
				double sourceMaxX = sourceEllipse.getMaxX();
				double sourceMaxY = sourceEllipse.getMaxY();

				if (targetShape instanceof RoundRectangle2D) {

					RoundRectangle2D targetRectange = (RoundRectangle2D) targetShape;

					double targetMinX = targetRectange.getMinX();
					double targetMinY = targetRectange.getMinY();
					double targetMaxX = targetRectange.getMaxX();
					double targetMaxY = targetRectange.getMaxY();

					if (targetMinX > sourceMaxX) {
						// perfect Right to the source

						// target points
						pointDestination.x = (int) targetRectange.getMinX();
						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));

						// System.out.println("Right of the source");
						pointSource.x = (int) sourceEllipse.getMaxX();
						pointSource.y = (int) (sourceEllipse.getMinY() + (sourceEllipse.getHeight() / 2));

					} else if (targetMaxX < sourceMinX) {
						// perfect left to the source

						// target points
						pointDestination.x = (int) targetRectange.getMaxX();
						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));

						// System.out.println("Left of the Source");
						pointSource.x = (int) sourceEllipse.getMinX();
						pointSource.y = (int) (sourceEllipse.getMinY() + (sourceEllipse.getHeight() / 2));

					} else {

						pointSource.x = (int) sourceMinX;
						pointSource.y = (int) (sourceMinY + (sourceEllipse.getHeight() / 2));

						pointDestination.x = (int) targetMinX;
						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));
					}
				}

			} else {

				// Source is a rectangle

				RoundRectangle2D sourceRectengle = (RoundRectangle2D) sourceShape;
				RoundRectangle2D targetRectangle = (RoundRectangle2D) targetShape;

				double sourceMinX = sourceRectengle.getMinX();
				double sourceMinY = sourceRectengle.getMinY();
				double sourceMaxX = sourceRectengle.getMaxX();
				double sourceMaxY = sourceRectengle.getMaxY();

				double targetMinX = targetRectangle.getMinX();
				double targetMinY = targetRectangle.getMinY();
				double targetMaxX = targetRectangle.getMaxX();
				double targetMaxY = targetRectangle.getMaxY();

				if (targetMinX >= sourceMaxX) {
					// right

					pointSource.x = (int) sourceMaxX;
					pointSource.y = (int) (sourceMinY + (sourceRectengle.getHeight() / 2));

					pointDestination.x = (int) targetMinX;
					pointDestination.y = (int) (targetMinY + (targetRectangle.getHeight() / 2));

				} else if (targetMaxX < sourceMinX) {
					// left

					pointSource.x = (int) sourceMinX;
					pointSource.y = (int) (sourceMinY + (sourceRectengle.getHeight() / 2));

					pointDestination.x = (int) targetMaxX;
					pointDestination.y = (int) (targetMinY + (targetRectangle.getHeight() / 2));

				} else {

					if (targetMaxY <= sourceMinY || targetMaxY <= sourceMaxY) {
						// top

						pointSource.y = (int) sourceMinY;
						pointDestination.y = (int) targetMaxY;

						int commonX = getCommonX(sourceMinX, sourceMaxX, targetMinX, targetMaxX);

						pointSource.x = commonX;
						pointDestination.x = commonX;

					} else {

						// bottom
						pointSource.y = (int) sourceMaxY;
						pointDestination.y = (int) targetMinY;

						int commonX = getCommonX(sourceMinX, sourceMaxX, targetMinX, targetMaxX);

						pointSource.x = commonX;
						pointDestination.x = commonX;

					}
				}
			}
			points.add(pointSource);
			points.add(pointDestination);
			return points;
		}

		private int getCommonX(double line1MinX, double line1MaxX, double line2MinX, double line2MaxX) {

			if (line1MinX == line2MinX && line1MaxX == line2MaxX) {
				return (int) (line1MinX + ((line1MaxX - line1MinX) / 2));
			} else {

				double line1Length = line1MaxX - line1MinX;
				double line2Length = line2MaxX - line2MinX;

				if (line1MinX < line2MinX) {
					// line 1 start left

					double commonX1 = line2MinX;
					double commonX2;
					if (line1Length <= line2Length) {

						commonX2 = commonX1 + (line1Length - (line2MinX - line1MinX));

					} else {

						commonX2 = commonX1 + (line2Length - (line2MinX - line1MinX));
					}

					return (int) (line2MinX + Math.ceil((commonX2 - commonX1) / 2));

				} else {
					// line 2 start left

					double commonX1 = line1MinX;
					double commonX2;
					if (line1Length <= line2Length) {

						commonX2 = commonX1 + (line1Length - (line1MinX - line2MinX));

					} else {

						commonX2 = commonX1 + (line2Length - (line1MinX - line2MinX));
					}

					return (int) (line1MinX + Math.ceil((commonX2 - commonX1) / 2));
				}

			}

		}

		// return where to draw arrow
		private Point getDrawPoint(Shape shape) {
			Point tempPoint = new Point(-1, -1);
			if (shape instanceof RoundRectangle2D) {
				// System.out.println("Shape is a rectengle");
				RoundRectangle2D tempRect = (RoundRectangle2D) shape;
				tempPoint.x = (int) tempRect.getMinX();

				tempPoint.y = (int) (tempRect.getMinY() + (tempRect.getHeight() / 2));

			} else if (shape instanceof Ellipse2D) {
				Ellipse2D tempEllipse = (Ellipse2D) shape;
				tempPoint.x = (int) tempEllipse.getMaxX();
				tempPoint.y = (int) (tempEllipse.getMinY() + (tempEllipse.getHeight() / 2));
			}

			return tempPoint;
		}

	}

	public class Operation {

		// This class represents an activity in the ETL flow

		String operationName;
		Shape operationShape;
		int upperLeftX, upperLeftY;
		ETLOperation etlOperation;
		ArrayList<Operation> nextOperations;

		// flag to check whether all input to the operation is given
		boolean inputStatus, isExecuted;

		// --------------------------------------Change made for Saving ETL
		// ------------------------

		String xmlOperationKey;

		// --------------------------------------Change made for Saving ETL
		// ------------------------
		public Operation(String operationName, int upperLeftX, int upperLeftY) {

			this.operationName = operationName;
			this.upperLeftX = upperLeftX;
			this.upperLeftY = upperLeftY;

			nextOperations = new ArrayList<>();
			inputStatus = false;
			isExecuted = false;

			// --------------------------------------Change made for Saving ETL
			// ------------------------
			Calendar calendar = new GregorianCalendar();
			xmlOperationKey = calendar.getTimeInMillis() + "";
			// --------------------------------------Change made for Saving ETL
			// ------------------------
		}

		public boolean isInputStatus() {
			return inputStatus;
		}

		public void setInputStatus(boolean inputStatus) {
			this.inputStatus = inputStatus;
		}

		public boolean isExecuted() {
			return isExecuted;
		}

		public void setExecuted(boolean isExecuted) {
			this.isExecuted = isExecuted;
		}

		public int getUpperLeftX() {
			return upperLeftX;
		}

		public void setUpperLeftX(int upperLeftX) {
			this.upperLeftX = upperLeftX;
		}

		public int getUpperLeftY() {
			return upperLeftY;
		}

		public void setUpperLeftY(int upperLeftY) {
			this.upperLeftY = upperLeftY;
		}

		public String getOperationName() {
			return operationName;
		}

		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}

		public Shape getOperationShape() {
			return operationShape;
		}

		public void setOperationShape(Shape operationShape) {
			this.operationShape = operationShape;
		}

		public ArrayList<Operation> getNextOperations() {
			return nextOperations;
		}

		public void setNextOperations(ArrayList<Operation> nextOperations) {
			this.nextOperations = nextOperations;
		}

		public ETLOperation getEtlOperation() {
			return etlOperation;
		}

		public void setEtlOperation(ETLOperation etlOperation) {
			this.etlOperation = etlOperation;
		}

		// --------------------------------------Change made for Saving ETL
		// ------------------------
		public String getXmlOperationKey() {
			return xmlOperationKey;
		}

		public void setXmlOperationKey(String xmlOperationKey) {
			this.xmlOperationKey = xmlOperationKey;
		}
		// --------------------------------------Change made for Saving ETL
		// ------------------------

	}

	// contains data and method of arrow
	public class Arrow {

		Operation sourceOperation, targetOperation;
		ArrayList<Shape> arrowSegments;

		public Arrow(Operation sourceOperation, Operation targetOperation) {
			super();
			this.sourceOperation = sourceOperation;
			this.targetOperation = targetOperation;
			this.arrowSegments = new ArrayList<>();
		}

		private boolean containsPoint(Double x, Double y) {

			for (Shape segment : arrowSegments) {
				if (segment.contains(x, y)) {
					return true;
				}
			}
			return false;
		}

		public Operation getSourceOperation() {
			return sourceOperation;
		}

		public void setSourceOperation(Operation sourceOperation) {
			this.sourceOperation = sourceOperation;
		}

		public Operation getTargetOperation() {
			return targetOperation;
		}

		public void setTargetOperation(Operation targetOperation) {
			this.targetOperation = targetOperation;
		}

		public ArrayList<Shape> getArrowSegments() {
			return arrowSegments;
		}

		public void setArrowSegments(ArrayList<Shape> arrowSegments) {
			this.arrowSegments = arrowSegments;
		}

	}
}
