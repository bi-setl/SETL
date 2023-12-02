package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import controller.MappingDefinition;
import controller.TBoxDefinition;
import helper.FileMethods;
import helper.Methods;
import helper.Variables;
import net.miginfocom.swing.MigLayout;
import queries.MappingExtraction;
import queries.TBoxExtraction;

import java.awt.CardLayout;

public class PanelMapSource2TargetNew extends JPanel {
	private static final String ENTER_MAP_IRI = "Enter Map IRI:";
	private static final String VALUE_TYPE = "Value Type:";
	private static final String SAVE_CONCEPT = "Save Concept";
	private static final String BOTH_SAME = "Both Same";
	private static final String TARGET_COMMON_PROPERTY = "Target Common Property:";
	private static final String SOURCE_COMMON_PROPERTY = "Source Common Property:";
	private static final String COMMON_PROPERTY = "Common Property";
	private static final String OPERATION = "Operation:";
	private static final String VALUE = "Value:";
	private static final String VALUE_FOR_CREATING_TARGET_INSTANCES_IRI = "Value for Creating Target Instances' IRI";
	private static final String SOURCE_QUERY = "Source Query:";
	private static final String SPARQL_QUERY = "SPARQL Query";
	private static final String ALL = "All";
	private static final String INSTANCES_TO_BE_MAPPED = "Instances to be mapped:";
	private static final String INSTANCE_MAPPING = "Instance Mapping";
	private static final String SELECT_SOURCE_A_BOX_FILE = "Select Source ABox File";
	private static final String TARGET_A_BOX_LOCATION = "Target ABox Location:";
	private static final String OPEN = "Open";
	private static final String SOURCE_A_BOX_LOCATION = "Source ABox Location:";
	private static final String RELATION = "Relation:";
	private static final String TARGET_CONCEPT = "Target Concept:";
	private static final String SOURCE_CONCEPT = "Source Concept:";
	private static final String CONCEPT_MAPPING = "Concept Mapping";
	private static final String DATASET = "Dataset:";
	private static final String MAP_PREFIX = "map:";
	private static final String SAVE = "Save";
	private static final String TARGET_NAME = "Target Name:";
	private static final String SOURCE_NAME = "Source Name:";
	private static final String DATASET_NAME = "Dataset Name:";
	private static final String PANEL_MAP_EXPRESSION = "PANEL_MAP_EXPRESSION";
	private static final String PANEL_MAP_TARGET = "PANEL_MAP_TARGET";
	private static final String PANEL_CONCEPT_OTHER = "PANEL_CONCEPT_OTHER";
	private static final String PANEL_CONCEPT_EXPRESSION = "PANEL_CONCEPT_EXPRESSION";
	private static final String PANEL_SOURCE_ATTRIBUTE = "PANEL_SOURCE_ATTRIBUTE";
	private static final String PANEL_QUERY = "PANEL_QUERY";
	private static final String PANEL_DATASET = "PANEL_DATASET";
	private static final String PANEL_CONCEPT = "PANEL_CONCEPT";
	private static final String PANEL_MAPPER = "PANEL_MAPPER";
	private static final String RECORD_TEMP_FILE_TTL = Variables.TEMP_DIR + "record_temp_file.ttl";
	
	private final String SOURCE_FILE_PREFIX = "onto:";
	private final String TARGET_FILE_PREFIX = "onto:";
	
	public static String mapIRI = "http://www.map.org/example#";
	
	private String sourceConstruct = "";

	private JTree treeSource;
	private JTree treeTarget;
	private JLabel lblFilePath;
	private JTree treeMap;

	private TBoxDefinition sourceDefinition;
	private TBoxDefinition targetDefinition;
	private MappingDefinition recordDefinition;
	private TBoxExtraction sourceExtraction;
	private TBoxExtraction targetExtraction;
	private MappingExtraction recordExtraction;
	private FileMethods sourceMethods;
	private FileMethods targetMethods;
	private FileMethods recordMethods;
	private JLabel lblSourcePath;
	private JLabel lblTargetPath;
	private JPanel panelOperation;
	private JTextArea textAreaMap;
	private JTextField textFieldSource;
	private JTextField textFieldTarget;
	private JTextField textFieldSourceMapper;
	private JTextField textFieldTargetMapper;
	private JTextField textFieldKey;
	private JTextArea textAreaSourceExpression;
	private JTextField textFieldSourceProperty;
	private JTextField textFieldTargetProperty;
	private JTextField textFieldTargetSparql;
	private JTextField textFieldTargetExpression;
	private JPanel panelUpdate;
	private JTextArea textAreaKeyAttribute;
	private JTextField textFieldOperation;
	private JTextField textFieldSourceConcept;
	private JTextField textFieldTargetConcept;
	private JComboBox comboBoxRelation;
	private JComboBox comboBoxMapped;
	private JTextArea textAreaSparql;
	private JComboBox comboBoxValueType;
	private JTextField textFieldValue;
	private JComboBox comboBoxDataset;
	private JTextField textFieldDataset;
	private JComboBox comboBoxConceptMapper;
	private JComboBox comboBoxSelectionType;
	private JPanel panelOperationRecord;
	private JPanel panelHead;
	private JPanel panelDataset;
	private String currentPanel = "";
	
	private LinkedHashMap<String, String> sourcePrefixes = new LinkedHashMap<>();
	private LinkedHashMap<String, String> targetPrefixes = new LinkedHashMap<>();

	/**
	 * Create the panel.
	 */
	public PanelMapSource2TargetNew() {
		initializeAll();
		setLayout(new MigLayout("", "[grow]", "[grow]"));

		JPanel panelParent = new JPanel();
		add(panelParent, "cell 0 0,grow");
		panelParent.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTabbedPane tabbedPaneParent = new JTabbedPane(JTabbedPane.TOP);
		panelParent.add(tabbedPaneParent, "cell 0 0,grow");

		JPanel panelRelation = new JPanel();
		tabbedPaneParent.addTab("Mapping Window", null, panelRelation, null);
		panelRelation.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelRelationButton = new JPanel();
		panelRelationButton.setBackground(Color.WHITE);
		panelRelation.add(panelRelationButton, "cell 0 0,grow");

		JButton btnSourceFile = new JButton("Source File");
		btnSourceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openSourceFileHandler();
			}
		});
		btnSourceFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRelationButton.add(btnSourceFile);

		JButton btnTargetFile = new JButton("Target File");
		btnTargetFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openTargetFileHandler();
			}
		});
		btnTargetFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRelationButton.add(btnTargetFile);
		
		JButton btnSetMapIRI = new JButton("Set Map IRI");
		btnSetMapIRI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMapIRIHandler();
			}
		});
		btnSetMapIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRelationButton.add(btnSetMapIRI);

		JPanel panelRelationHolder = new JPanel();
		panelRelationHolder.setBackground(Color.WHITE);
		panelRelation.add(panelRelationHolder, "cell 0 1,grow");
		panelRelationHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneFirst = new JSplitPane();
		splitPaneFirst.setResizeWeight(0.3);
		panelRelationHolder.add(splitPaneFirst, "cell 0 0,grow");

		JPanel panelSource = new JPanel();
		panelSource.setBackground(Color.WHITE);
		splitPaneFirst.setLeftComponent(panelSource);
		panelSource.setLayout(new MigLayout("", "[grow][]", "[][][grow]"));

		lblSourcePath = new JLabel("Source Path");
		lblSourcePath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelSource.add(lblSourcePath, "cell 0 0 2 1,growx");
		
		JButton btnExpandSource = new JButton("Expand");
		btnExpandSource.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnExpandSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSource.getModel().getRoot();
				setNodeExpandedState(treeSource, node, true);
			}
		});
		panelSource.add(btnExpandSource, "flowx,cell 0 1");

		JScrollPane scrollPaneSource = new JScrollPane();
		panelSource.add(scrollPaneSource, "cell 0 2 2 1,grow");
		
		treeSource = new JTree();
		treeSource.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeSource.getLastSelectedPathComponent();
				String selectedResource = "";

				if (selectedNode != null) {
				    selectedResource = selectedNode.toString();
				    String type = sourceExtraction.getResourceType(selectedResource);

				    setSourceConstruct(selectedResource); // Common operation for all types

				    switch (type) {
				        case Variables.DIMENSION_PROPERTY:
				        case Variables.LEVEL_PROPERTY:
				        case Variables.DATA_SET:
				        case Variables.CLASS:
				            textFieldSourceMapper.setText(selectedResource);
				            textFieldSourceConcept.setText(selectedResource);
				            break;

				        case Variables.LEVEL_ATTRIBUTE:
				        case Variables.OBJECT_PROPERTY:
				        case Variables.DATATYPE_PROPERTY:
				        case Variables.FUNCTIONAL_PROPERTY:
				        case Variables.ROLLUP_PROPERTY:
				            textFieldKey.setText(selectedResource);
				            textFieldValue.setText(selectedResource);
				            textAreaKeyAttribute.append(selectedResource);
				            break;

				        case Variables.MEASURE_PROPERTY:
				            // Additional actions for MeasureProperty, if needed
				            break;

				        default:
				            System.out.println(type);
				            break;
				    }
				}
			}
		});
		treeSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		setRenderer(treeSource);
		scrollPaneSource.setViewportView(treeSource);
		
		JButton btnCollapseSource = new JButton("Collapse");
		btnCollapseSource.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCollapseSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSource.getModel().getRoot();
				setNodeExpandedState(treeSource, node, false);
			}
		});
		panelSource.add(btnCollapseSource, "cell 0 1");

		JSplitPane splitPaneSecond = new JSplitPane();
		splitPaneSecond.setResizeWeight(0.5);
		splitPaneFirst.setRightComponent(splitPaneSecond);

		JPanel panelOperationHolder = new JPanel();
		panelOperationHolder.setBackground(Color.WHITE);
		splitPaneSecond.setLeftComponent(panelOperationHolder);
		panelOperationHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelOperationButton = new JPanel();
		panelOperationButton.setBackground(Color.WHITE);
		panelOperationHolder.add(panelOperationButton, "cell 0 0,grow");

		JButton btnMapDataset = new JButton("+ Map Dataset");
		btnMapDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addDatasetButtonHandler();
				CardLayout cl = (CardLayout)(panelOperation.getLayout());
		        cl.show(panelOperation, PANEL_DATASET);
		        currentPanel = PANEL_DATASET;
			}
		});
		btnMapDataset.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelOperationButton.add(btnMapDataset);

		JButton btnMapHead = new JButton("+ Map Concept");
		btnMapHead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addMapperButtonHandler();
				CardLayout cl = (CardLayout)(panelOperation.getLayout());
		        cl.show(panelOperation, PANEL_CONCEPT);
		        currentPanel = PANEL_CONCEPT;
			}
		});
		btnMapHead.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelOperationButton.add(btnMapHead);

		JButton btnMapRecord = new JButton("+ Map Property");
		btnMapRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRecordButtonHandler();
				CardLayout cl = (CardLayout)(panelOperation.getLayout());
		        cl.show(panelOperation, PANEL_MAPPER);
		        currentPanel = PANEL_MAPPER;
			}
		});
		btnMapRecord.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelOperationButton.add(btnMapRecord);
		
		JScrollPane scrollPaneOperation = new JScrollPane();
		panelOperationHolder.add(scrollPaneOperation, "cell 0 1,grow");

		panelOperation = new JPanel();
		scrollPaneOperation.setViewportView(panelOperation);
		panelOperation.setBackground(Color.WHITE);
		panelOperation.setLayout(new CardLayout(0, 0));
		
		panelOperationRecord = new JPanel();
		panelOperationRecord.setBackground(Color.WHITE);
		panelOperationRecord.setLayout(new MigLayout("", "[][grow]", "[][][grow]"));
		
		panelHead = new JPanel();
		panelHead.setBackground(Color.WHITE);
		panelHead.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		
		panelDataset = new JPanel();
		panelDataset.setBackground(Color.WHITE);
		panelDataset.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		
		panelOperation.add(panelDataset, PANEL_DATASET);
		panelOperation.add(panelHead, PANEL_CONCEPT);
		panelOperation.add(panelOperationRecord, PANEL_MAPPER);
		
		addDatasetButtonHandler();

		JPanel panelTarget = new JPanel();
		panelTarget.setBackground(Color.WHITE);
		splitPaneSecond.setRightComponent(panelTarget);
		panelTarget.setLayout(new MigLayout("", "[grow][]", "[][][grow]"));

		lblTargetPath = new JLabel("Target Path");
		lblTargetPath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelTarget.add(lblTargetPath, "cell 0 0 2 1,growx");
		
		JButton btnExpandTarget = new JButton("Expand");
		btnExpandTarget.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnExpandTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeTarget.getModel().getRoot();
				setNodeExpandedState(treeTarget, node, true);
			}
		});
		panelTarget.add(btnExpandTarget, "flowx,cell 0 1");

		JScrollPane scrollPaneTarget = new JScrollPane();
		panelTarget.add(scrollPaneTarget, "cell 0 2 2 1,grow");

		treeTarget = new JTree();
		treeTarget.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeTarget
						.getLastSelectedPathComponent();
				String selectedResource = "";

				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					String type = targetExtraction.getResourceType(selectedResource);

					if (type.contains("DimensionProperty") || type.contains("LevelProperty") || type.contains("DataSet")
							|| type.contains("Class")) {
						textFieldTargetMapper.setText(selectedResource);
						textFieldTargetProperty.setText(selectedResource);
						textFieldTargetSparql.setText(selectedResource);
						textFieldTargetExpression.setText(selectedResource);
						textFieldTargetConcept.setText(selectedResource);
					} else if (type.contains("LevelAttribute") || type.contains("ObjectProperty")
							|| type.contains("DatatypeProperty") || type.contains("FunctionalProperty") || type.contains("RollupProperty")) {
						textFieldTargetProperty.setText(selectedResource);
						textFieldTargetSparql.setText(selectedResource);
						textFieldTargetExpression.setText(selectedResource);
						textFieldKey.setText(selectedResource);
					} else if (type.contains("MeasureProperty")) {
						textFieldTargetProperty.setText(selectedResource);
						textFieldTargetSparql.setText(selectedResource);
						textFieldTargetExpression.setText(selectedResource);
					} else {
						System.out.println(type);
					}
				}
			}
		});
		treeTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		setRenderer(treeTarget);
		scrollPaneTarget.setViewportView(treeTarget);
		
		JButton btnCollapseTarget = new JButton("Collapse");
		btnCollapseTarget.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCollapseTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeTarget.getModel().getRoot();
				setNodeExpandedState(treeTarget, node, false);
			}
		});
		panelTarget.add(btnCollapseTarget, "cell 0 1");

		JPanel panelRecord = new JPanel();
		tabbedPaneParent.addTab("Mapping File Editor", null, panelRecord, null);
		panelRecord.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelRecordButton = new JPanel();
		panelRecordButton.setBackground(Color.WHITE);
		panelRecord.add(panelRecordButton, "cell 0 0,grow");

		JButton btnOpenFile = new JButton("Open File");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openButtonHandler();
			}
		});
		btnOpenFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRecordButton.add(btnOpenFile);

		JButton btnSaveFile = new JButton("Save File");
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveButtonHandler();
			}
		});
		btnSaveFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRecordButton.add(btnSaveFile);

		JPanel panelRecordHolder = new JPanel();
		panelRecordHolder.setBackground(Color.WHITE);
		panelRecord.add(panelRecordHolder, "cell 0 1,grow");
		panelRecordHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneOne = new JSplitPane();
		splitPaneOne.setResizeWeight(0.3);
		panelRecordHolder.add(splitPaneOne, "cell 0 0,grow");

		JPanel panelMapTree = new JPanel();
		splitPaneOne.setLeftComponent(panelMapTree);
		panelMapTree.setBackground(Color.WHITE);
		panelMapTree.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelButtonClass = new JPanel();
		panelButtonClass.setBackground(Color.WHITE);
		panelMapTree.add(panelButtonClass, "cell 0 0,grow");
		panelButtonClass.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonRemoveClass = new JButton("-");
		buttonRemoveClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeMap.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMap.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a class to remove", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveClass.setBackground(Color.WHITE);
		panelButtonClass.add(buttonRemoveClass);

		JScrollPane scrollPaneMap = new JScrollPane();
		panelMapTree.add(scrollPaneMap, "cell 0 1,grow");

		treeMap = new JTree();
		treeMap.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMap.getLastSelectedPathComponent();
				String selectedResource = "";

				if (selectedNode != null) {
					selectedResource = selectedNode.toString();

					LinkedHashMap<String, String> hashMap = recordExtraction
							.extractAssociatedRecordProperties(selectedResource);
					updateResource(selectedResource, hashMap);
				}
			}
		});
		treeMap.setFont(new Font("Tahoma", Font.BOLD, 12));
		setRenderer(treeMap);
		scrollPaneMap.setViewportView(treeMap);

		JSplitPane splitPaneTwo = new JSplitPane();
		splitPaneTwo.setResizeWeight(0.5);
		splitPaneOne.setRightComponent(splitPaneTwo);

		JPanel panelMapText = new JPanel();
		splitPaneTwo.setLeftComponent(panelMapText);
		panelMapText.setBackground(Color.WHITE);
		panelMapText.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		lblFilePath = new JLabel("File Path");
		lblFilePath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelMapText.add(lblFilePath, "cell 0 0,growx");

		JScrollPane scrollPaneText = new JScrollPane();
		panelMapText.add(scrollPaneText, "cell 0 1,grow");

		textAreaMap = new JTextArea();
		textAreaMap.setFont(new Font("Monospaced", Font.BOLD, 13));
		scrollPaneText.setViewportView(textAreaMap);

		JPanel panelMapUpdate = new JPanel();
		splitPaneTwo.setRightComponent(panelMapUpdate);
		panelMapUpdate.setBackground(Color.WHITE);
		panelMapUpdate.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneUpdate = new JScrollPane();
		panelMapUpdate.add(scrollPaneUpdate, "cell 0 0,grow");

		panelUpdate = new JPanel();
		scrollPaneUpdate.setViewportView(panelUpdate);
		panelUpdate.setBackground(Color.WHITE);
		panelUpdate.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		// initialize All Tree with default value
		instantiateAllTree();
	}
	
	private void setMapIRIHandler() {
		// TODO Auto-generated method stub
		String userInput = JOptionPane.showInputDialog(this, ENTER_MAP_IRI);
        
        if (userInput != null) {
            // The user clicked "OK" and entered some text
            mapIRI = userInput;
            if (recordExtraction == null) {
            	createNewFileIfNotExists();
			}

            recordExtraction.getPrefixMap().put(MAP_PREFIX, mapIRI);
            
            recordExtraction.reloadAll();
            refreshMappingDefinition();
        }
	}

	private JPanel getMapExpressionPanel() {
		// TODO Auto-generated method stub
		JPanel panelExpression = new JPanel();
		panelExpression.setBackground(Color.WHITE);
		// panelSourceOperation.add(panelExpression, BorderLayout.CENTER);
		panelExpression.setLayout(new MigLayout("", "[][grow]", "[grow][grow][grow][][]"));

		JPanel panelMath = new JPanel();
		panelMath.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Mathematical Operations",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelMath.setBackground(Color.WHITE);
		panelExpression.add(panelMath, "cell 0 0 2 1,growx,aligny top");

		JButton buttonAdd = new JButton("+");
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setSourceOperator("+");
			}
		});
		buttonAdd.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonAdd);

		JButton buttonSub = new JButton("-");
		buttonSub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("-");
			}
		});
		buttonSub.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonSub);

		JButton buttonMul = new JButton("*");
		buttonMul.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("*");
			}
		});
		buttonMul.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonMul);

		JButton buttonDiv = new JButton("/");
		buttonDiv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("/");
			}
		});
		buttonDiv.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonDiv);

		JButton buttonLss = new JButton("<");
		buttonLss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("<");
			}
		});
		buttonLss.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonLss);

		JButton buttonGrt = new JButton(">");
		buttonGrt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator(">");
			}
		});
		buttonGrt.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonGrt);

		JButton buttonLssEql = new JButton("<=");
		buttonLssEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("<=");
			}
		});
		buttonLssEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonLssEql);

		JButton buttonGrtEql = new JButton(">=");
		buttonGrtEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator(">=");
			}
		});
		buttonGrtEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonGrtEql);

		JButton buttonEql = new JButton("=");
		buttonEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperator("=");
			}
		});
		buttonEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonEql);

		JPanel panelString = new JPanel();
		panelString.setBorder(
				new TitledBorder(null, "String Operations", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panelString.setBackground(Color.WHITE);
		panelExpression.add(panelString, "cell 0 1 2 1,grow");

		JButton btnContains = new JButton("CONTAINS");
		btnContains.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("CONTAINS");
			}
		});
		btnContains.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnContains);

		JButton btnSplit = new JButton("SPLIT");
		btnSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("SPLIT");
			}
		});
		btnSplit.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnSplit);

		JButton btnConcat = new JButton("CONCAT");
		btnConcat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("CONCAT");
			}
		});
		btnConcat.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnConcat);

		JButton btnReplace = new JButton("REPLACE");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("REPLACE");
			}
		});
		btnReplace.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnReplace);

		JButton btnTonumber = new JButton("ToNumber");
		btnTonumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("ToNumber");
			}
		});
		btnTonumber.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnTonumber);

		JButton btnTostring = new JButton("ToString");
		btnTostring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSourceOperation("ToString");
			}
		});
		btnTostring.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnTostring);
		
		JButton btnCompare = new JButton("COMPARE");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setSourceOperation("COMPARE");
			}
		});
		btnCompare.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnCompare);

		JLabel lblSourceExpression = new JLabel("Source Expression:");
		lblSourceExpression.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelExpression.add(lblSourceExpression, "cell 0 2");

		// textAreaSourceExpression = new JTextArea();
		textAreaSourceExpression.setRows(5);
		textAreaSourceExpression.setLineWrap(true);
		textAreaSourceExpression.setWrapStyleWord(true);
		textAreaSourceExpression.setFont(new Font("Tahoma", Font.BOLD, 11));
		textAreaSourceExpression.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelExpression.add(textAreaSourceExpression, "cell 1 2,grow");
		return panelExpression;
	}

	protected JPanel getConceptExpressionPanel() {
		// TODO Auto-generated method stub
		JPanel panelExpression = new JPanel();
		panelExpression.setBackground(Color.WHITE);
		// panelHolder.add(panelExpression, BorderLayout.CENTER);
		panelExpression.setLayout(new MigLayout("", "[][grow]", "[grow][grow][grow][][]"));

		JPanel panelMath = new JPanel();
		panelMath.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Mathematical Operations",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelMath.setBackground(Color.WHITE);
		panelExpression.add(panelMath, "cell 0 0 2 1,growx,aligny top");

		JButton buttonAdd = new JButton("+");
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setKeyOperator("+");
			}
		});
		buttonAdd.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonAdd);

		JButton buttonSub = new JButton("-");
		buttonSub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("-");
			}
		});
		buttonSub.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonSub);

		JButton buttonMul = new JButton("*");
		buttonMul.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("*");
			}
		});
		buttonMul.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonMul);

		JButton buttonDiv = new JButton("/");
		buttonDiv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("/");
			}
		});
		buttonDiv.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonDiv);

		JButton buttonLss = new JButton("<");
		buttonLss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("<");
			}
		});
		buttonLss.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonLss);

		JButton buttonGrt = new JButton(">");
		buttonGrt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator(">");
			}
		});
		buttonGrt.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonGrt);

		JButton buttonLssEql = new JButton("<=");
		buttonLssEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("<=");
			}
		});
		buttonLssEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonLssEql);

		JButton buttonGrtEql = new JButton(">=");
		buttonGrtEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator(">=");
			}
		});
		buttonGrtEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonGrtEql);

		JButton buttonEql = new JButton("=");
		buttonEql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperator("=");
			}
		});
		buttonEql.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelMath.add(buttonEql);

		JPanel panelString = new JPanel();
		panelString.setBorder(
				new TitledBorder(null, "String Operations", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panelString.setBackground(Color.WHITE);
		panelExpression.add(panelString, "cell 0 1 2 1,grow");

		JButton btnContains = new JButton("CONTAINS");
		btnContains.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("CONTAINS");
			}
		});
		btnContains.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnContains);

		JButton btnSplit = new JButton("SPLIT");
		btnSplit.setToolTipText("SPLIT(string, splitBy, index)");
		btnSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("SPLIT");
			}
		});
		btnSplit.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnSplit);

		JButton btnConcat = new JButton("CONCAT");
		btnConcat.setToolTipText("CONCAT(string, string)");
		btnConcat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("CONCAT");
			}
		});
		btnConcat.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnConcat);

		JButton btnReplace = new JButton("REPLACE");
		btnReplace.setToolTipText("REPLACE(string, newString)");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("REPLACE");
			}
		});
		btnReplace.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnReplace);

		JButton btnTonumber = new JButton("ToNumber");
		btnTonumber.setToolTipText("ToNumber(string)");
		btnTonumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("ToNumber");
			}
		});
		btnTonumber.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnTonumber);

		JButton btnTostring = new JButton("ToString");
		btnTostring.setToolTipText("ToString(number)");
		btnTostring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setKeyOperation("ToString");
			}
		});
		btnTostring.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnTostring);
		
		JButton btnCompare = new JButton("COMPARE");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setKeyOperation("COMPARE");
			}
		});
		btnCompare.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelString.add(btnCompare);

		JLabel lblKeyAttribute = new JLabel(VALUE);
		lblKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelExpression.add(lblKeyAttribute, "cell 0 2");

		textAreaKeyAttribute = new JTextArea();
		textAreaKeyAttribute.setRows(5);
		textAreaKeyAttribute.setLineWrap(true);
		textAreaKeyAttribute.setWrapStyleWord(true);
		textAreaKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 11));
		textAreaKeyAttribute.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelExpression.add(textAreaKeyAttribute, "cell 1 2,grow");
		
		return panelExpression;
	}

	public void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
	    Enumeration<?> enumeration = node.breadthFirstEnumeration();
	    while (enumeration.hasMoreElements()) {
	        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) enumeration.nextElement();
	        if (!expanded && treeNode.isRoot()) {
	            continue;  // Skip collapsing root if not expanded
	        }
	        TreePath path = new TreePath(treeNode.getPath());
	        if (expanded) {
	            tree.expandPath(path);
	        } else {
	            tree.collapsePath(path);
	        }
	    }
	}

	public void openSourceFileHandler() {
	    sourceMethods.chooseFile();
	    String filePath = sourceMethods.getFilePath();

	    if (filePath == null) {
	        JOptionPane.showMessageDialog(null, "No selection");
	        return;
	    }

	    lblSourcePath.setText(filePath);
	    sourceExtraction = new TBoxExtraction(filePath);
	    
	    // Fetch all from the source file
	    initializeDefinition(sourceMethods, sourceDefinition, sourceExtraction, treeSource, Variables.SOURCE);

	    String[] parts = sourceMethods.getFileName().split("\\.");
	    if (parts.length == 2) {
	        textFieldSource.setText(SOURCE_FILE_PREFIX + parts[0]);
	    }
	}


	public void openTargetFileHandler() {
	    targetMethods.chooseFile();
	    String filePath = targetMethods.getFilePath();

	    if (filePath == null) {
	        JOptionPane.showMessageDialog(null, "No selection");
	        return;
	    }

	    lblTargetPath.setText(filePath);
	    targetExtraction = new TBoxExtraction(filePath);

	    // Fetch all from the target file
	    initializeDefinition(targetMethods, targetDefinition, targetExtraction, treeTarget, Variables.TARGET);

	    String[] parts = targetMethods.getFileName().split("\\.");
	    if (parts.length == 2) {
	        textFieldTarget.setText(TARGET_FILE_PREFIX + parts[0]);
	    }
	}


	protected void removeResource(String name) {
		// TODO Auto-generated method stub
		recordExtraction.removeResource(name);
		recordExtraction.reloadAll();
		refreshMappingDefinition();
	}

	protected void updateResource(String selectedResource, LinkedHashMap<String, String> hashMap) {
		// TODO Auto-generated method stub
		panelUpdate.removeAll();
		panelUpdate.repaint();
		panelUpdate.revalidate();

		panelUpdate.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		int size = hashMap.size() + 2;

		JPanel panelGen = new JPanel();
		panelGen.setBackground(Color.WHITE);
		panelUpdate.add(panelGen, "cell 0 0,grow");
		panelGen.setLayout(new MigLayout("", "[grow]", "[][][]"));

		JLabel lblName = new JLabel("Name: ");
		lblName.setForeground(Color.BLUE);
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGen.add(lblName, "cell 0 0");

		JTextField textFieldName = new JTextField(selectedResource);
		textFieldName.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGen.add(textFieldName, "cell 0 1,growx");
		textFieldName.setColumns(10);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateResource(selectedResource, textFieldName.getText().toString().trim());
			}
		});
		btnUpdate.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGen.add(btnUpdate, "cell 0 2,center");

		JScrollPane scrollPane = new JScrollPane();
		panelUpdate.add(scrollPane, "cell 0 1,grow");

		JPanel panelComp = new JPanel();
		scrollPane.setViewportView(panelComp);
		panelComp.setBackground(Color.WHITE);

		panelComp.setLayout(new GridLayout(hashMap.size(), 1, 0, 0));

		for (Map.Entry<String, String> map : hashMap.entrySet()) {
			JPanel panelHolder = new JPanel();
			panelHolder.setBackground(Color.WHITE);
			panelComp.add(panelHolder);
			panelHolder.setLayout(new MigLayout("", "[grow][]", "[][]"));

			JLabel lblProperty = new JLabel(map.getKey());
			lblProperty.setForeground(Color.BLUE);
			lblProperty.setFont(new Font("Tahoma", Font.BOLD, 11));
			panelHolder.add(lblProperty, "cell 0 0");

			JTextField textFieldProperty = new JTextField(map.getValue());

			JButton btnU = new JButton("u");
			btnU.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					recordExtraction.editProperty(selectedResource, map.getKey(), map.getValue(),
							textFieldProperty.getText().toString().trim());
					recordExtraction.reloadAll();
					refreshMappingDefinition();
				}
			});
			btnU.setBackground(Color.WHITE);
			panelHolder.add(btnU, "cell 1 0");

			textFieldProperty.setFont(new Font("Tahoma", Font.BOLD, 11));
			panelHolder.add(textFieldProperty, "cell 0 1 2 1,growx");
			textFieldProperty.setColumns(10);
		}
	}

	protected void updateResource(String previousResource, String currentResource) {
		// TODO Auto-generated method stub
		recordExtraction.updateResource(previousResource, currentResource);
		recordExtraction.reloadAll();
		refreshMappingDefinition();
	}

	public void openButtonHandler() {
		// TODO Auto-generated method stub
		recordMethods.chooseFile();
		String filePath = recordMethods.getFilePath();
		lblFilePath.setText(filePath);

		textAreaMap.setText("");

		if (filePath != null) {
			recordExtraction = new MappingExtraction(filePath);
			refreshMappingDefinition();
		} else {
			JOptionPane.showMessageDialog(null, "No selection");
		}
	}

	public void saveButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new MigLayout("", "[][grow]", "[]"));

		JLabel lblFileType = new JLabel("File Type: ");
		lblFileType.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lblFileType, "cell 0 0,alignx trailing");

		JComboBox comboBoxFileType = new JComboBox(recordMethods.getAllFileTypes().keySet().toArray());
		comboBoxFileType.setFont(new Font("Tahoma", Font.BOLD, 11));
		comboBoxFileType.setBackground(Color.WHITE);
		panel.add(comboBoxFileType, "cell 1 0,growx");

		int confirmation = JOptionPane.showConfirmDialog(null, panel, "Please correct values of Attribute.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String key = (String) comboBoxFileType.getSelectedItem();
			String extension = recordMethods.getAllFileTypes().get(key);
			recordMethods.promtToSaveFile(textAreaMap.getText().toString(), "map", key, extension);
		}
	}

	protected void addRecordButtonHandler() {
		// TODO Auto-generated method stub
		panelOperationRecord.removeAll();
		panelOperationRecord.repaint();
		panelOperationRecord.revalidate();
		
		JLabel lblConceptMapper = new JLabel("Concept Mapper:");
		lblConceptMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOperationRecord.add(lblConceptMapper, "cell 0 0,alignx trailing");
		
		comboBoxConceptMapper = new JComboBox(recordDefinition.getMapperList().toArray());
		comboBoxConceptMapper.setBackground(Color.WHITE);
		comboBoxConceptMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOperationRecord.add(comboBoxConceptMapper, "cell 1 0,growx");
		
		JLabel lblTargetProperty = new JLabel("Target Property:");
		lblTargetProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOperationRecord.add(lblTargetProperty, "cell 0 1,alignx trailing");
		
		// textFieldTargetProperty = new JTextField();
		textFieldTargetProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOperationRecord.add(textFieldTargetProperty, "cell 1 1,growx");
		textFieldTargetProperty.setColumns(10);
		
		JPanel panelValueSelectionType = new JPanel();
		panelValueSelectionType.setBackground(Color.WHITE);
		panelOperationRecord.add(panelValueSelectionType, "cell 0 2 2 1,grow");
		panelValueSelectionType.setLayout(new MigLayout("", "[][grow]", "[][]"));
		
		JLabel lblTargetPropertyValue = new JLabel("Target Property Value Selection Type:");
		lblTargetPropertyValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelectionType.add(lblTargetPropertyValue, "cell 0 0,alignx trailing");
		
		JPanel panelTargetType = new JPanel();
		JPanel panelTargetProperty = new JPanel();
		JPanel panelMapExpression = getMapExpressionPanel();
		
		comboBoxSelectionType = new JComboBox(recordDefinition.getSourceRecordType().toArray());
		comboBoxSelectionType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxSelectionType.getSelectedItem().equals("Source Property")) {
					panelTargetType.removeAll();
					panelTargetType.add(panelTargetProperty, PANEL_MAP_TARGET);
				} else if (comboBoxSelectionType.getSelectedItem().equals("Source Expression")) {
					panelTargetType.removeAll();
					panelTargetType.add(panelMapExpression, PANEL_MAP_EXPRESSION);
				}
				
				panelTargetType.repaint();
				panelTargetType.revalidate();
			}
		});
		comboBoxSelectionType.setBackground(Color.WHITE);
		comboBoxSelectionType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelectionType.add(comboBoxSelectionType, "cell 1 0,growx");
		
		// JPanel panelTargetType = new JPanel();
		panelTargetType.setBackground(Color.WHITE);
		panelValueSelectionType.add(panelTargetType, "cell 0 1 2 1,grow");
		panelTargetType.setLayout(new CardLayout(0, 0));
		
		// JPanel panelTargetProperty = new JPanel();
		panelTargetProperty.setBackground(Color.WHITE);
		panelTargetType.add(panelTargetProperty, PANEL_MAP_TARGET);
		panelTargetProperty.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel lblSourceProperty = new JLabel("Source Property:");
		lblSourceProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTargetProperty.add(lblSourceProperty, "cell 0 0,alignx trailing");
		
		/*textFieldSourceProperty = new JTextField();
		textFieldTargetProperty = new JTextField();*/
		textFieldSourceProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTargetProperty.add(textFieldSourceProperty, "cell 1 0,growx");
		textFieldSourceProperty.setColumns(10);
		
		JButton btnSaveMapper = new JButton(SAVE);
		btnSaveMapper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String conceptMapper = comboBoxConceptMapper.getSelectedItem().toString();
				String targetProperty = textFieldTargetProperty.getText().toString().trim();
				String selectionType = comboBoxSelectionType.getSelectedItem().toString();
				String sourceProperty = "";
				
				if (selectionType.equals("Source Property")) {
					sourceProperty = textFieldSourceProperty.getText().toString().trim();
				} else if (selectionType.equals("Source Expression")) {
					sourceProperty = textAreaSourceExpression.getText().toString().trim();
				}
				
				if (targetProperty.length() == 0 || sourceProperty.length() == 0 || conceptMapper.length() == 0) {
					JOptionPane.showMessageDialog(null, "Check all inputs", "Error Message", JOptionPane.ERROR_MESSAGE);
				} else {
					if (recordExtraction == null) {
						createNewFileIfNotExists();
					}
					
					recordExtraction.addNewRecord(conceptMapper, targetProperty, sourceProperty, selectionType);
					recordExtraction.reloadAll();
					refreshMappingDefinition();
					
					textFieldTargetProperty.setText("");
					textFieldSourceProperty.setText("");
					textAreaSourceExpression.setText("");
					
					// comboBoxConceptMapper.setSelectedIndex(0);
				}
			}
		});
		btnSaveMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelectionType.add(btnSaveMapper, "cell 0 2 2 1,center");
	}

	protected void addMapperButtonHandler() {
		// TODO Auto-generated method stub
		panelHead.removeAll();
		panelHead.repaint();
		panelHead.revalidate();
		
		JLabel lblDataset = new JLabel(DATASET);
		lblDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHead.add(lblDataset, "cell 0 0,alignx trailing");
		
		comboBoxDataset = new JComboBox(recordDefinition.getDatasetList().toArray());
		comboBoxDataset.setBackground(Color.WHITE);
		comboBoxDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHead.add(comboBoxDataset, "cell 1 0,growx");
		
		JPanel panelConceptMapping = new JPanel();
		panelConceptMapping.setBorder(new TitledBorder(null, CONCEPT_MAPPING, TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLACK));
		panelConceptMapping.setBackground(Color.WHITE);
		panelHead.add(panelConceptMapping, "cell 0 1 2 1,grow");
		panelConceptMapping.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
		
		JLabel lblSourceConcept = new JLabel(SOURCE_CONCEPT);
		lblSourceConcept.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(lblSourceConcept, "cell 0 1,alignx trailing");
		
		// textFieldSourceConcept = new JTextField();
		textFieldSourceConcept.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(textFieldSourceConcept, "cell 1 1 2 1,growx");
		textFieldSourceConcept.setColumns(10);
		
		JLabel lblTargetConcept = new JLabel(TARGET_CONCEPT);
		lblTargetConcept.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(lblTargetConcept, "cell 0 0,alignx trailing");
		
		// textFieldTargetConcept = new JTextField();
		textFieldTargetConcept.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(textFieldTargetConcept, "cell 1 0 2 1,growx");
		textFieldTargetConcept.setColumns(10);
		
		JLabel lblRelation = new JLabel(RELATION);
		lblRelation.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(lblRelation, "cell 0 2,alignx trailing");
		
		JLabel lblSourceABox = new JLabel(SOURCE_A_BOX_LOCATION);
		lblSourceABox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(lblSourceABox, "cell 0 3,alignx trailing");
		
		JTextField textFieldSourceABoxPath = new JTextField();
		textFieldSourceABoxPath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(textFieldSourceABoxPath, "cell 1 3,growx");
		textFieldSourceABoxPath.setColumns(10);
		
		JButton btnSourceABox = new JButton(OPEN);
		btnSourceABox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Methods methods = new Methods();
				String sourceABoxLocationString = methods.chooseFile(SELECT_SOURCE_A_BOX_FILE);
				
				if (methods.checkString(sourceABoxLocationString)) {
					textFieldSourceABoxPath.setText(sourceABoxLocationString);
				}
			}
		});
		btnSourceABox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(btnSourceABox, "cell 2 3");
		
		JLabel lblTargetABox = new JLabel(TARGET_A_BOX_LOCATION);
		lblTargetABox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(lblTargetABox, "cell 0 4,alignx trailing");
		
		JTextField textFieldTargetABoxPath = new JTextField();
		textFieldTargetABoxPath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(textFieldTargetABoxPath, "cell 1 4,growx");
		textFieldTargetABoxPath.setColumns(10);
		
		JButton btnTargetABox = new JButton(OPEN);
		btnTargetABox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Methods methods = new Methods();
				String sourceABoxLocationString = methods.chooseFile(SELECT_SOURCE_A_BOX_FILE);
				
				if (methods.checkString(sourceABoxLocationString)) {
					textFieldTargetABoxPath.setText(sourceABoxLocationString);
				}
			}
		});
		btnTargetABox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(btnTargetABox, "cell 2 4");
		
		comboBoxRelation = new JComboBox(recordDefinition.getRelations());
		comboBoxRelation.setBackground(Color.WHITE);
		comboBoxRelation.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelConceptMapping.add(comboBoxRelation, "cell 1 2 2 1,growx");
		
		JPanel panelInstanceMatching = new JPanel();
		panelInstanceMatching.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), INSTANCE_MAPPING, TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelInstanceMatching.setBackground(Color.WHITE);
		panelHead.add(panelInstanceMatching, "cell 0 2 2 1,grow");
		panelInstanceMatching.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		
		JLabel lblInstancesToBe = new JLabel(INSTANCES_TO_BE_MAPPED);
		lblInstancesToBe.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelInstanceMatching.add(lblInstancesToBe, "cell 0 0,alignx trailing");
		
		JPanel panelMapHolder = new JPanel();
		JPanel panelQuery = new JPanel();
		
		comboBoxMapped = new JComboBox(recordDefinition.getSourceMapperType().toArray());
		comboBoxMapped.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBoxMapped.getSelectedItem().equals(ALL)) {
					panelMapHolder.remove(panelQuery);
				} else if (comboBoxMapped.getSelectedItem().equals(SPARQL_QUERY)) {
					panelMapHolder.add(panelQuery, PANEL_QUERY);
				}
				
				panelMapHolder.validate();
				panelMapHolder.repaint();
				panelMapHolder.revalidate();
			}
		});
		comboBoxMapped.setBackground(Color.WHITE);
		comboBoxMapped.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelInstanceMatching.add(comboBoxMapped, "cell 1 0,growx");
		
		panelMapHolder.setBackground(Color.WHITE);
		panelInstanceMatching.add(panelMapHolder, "cell 0 1 2 1,grow");
		panelMapHolder.setLayout(new CardLayout(0, 0));
		
		panelQuery.setBackground(Color.WHITE);
		// panelMapHolder.add(panelQuery, PANEL_QUERY);
		panelQuery.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel lblSourceQuery = new JLabel(SOURCE_QUERY);
		lblSourceQuery.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelQuery.add(lblSourceQuery, "cell 0 0");
		
		JScrollPane scrollPaneSparql = new JScrollPane();
		scrollPaneSparql.setMinimumSize(new Dimension(panelQuery.getWidth(), 50));
		panelQuery.add(scrollPaneSparql, "cell 1 0,grow");
		
		textAreaSparql = new JTextArea();
		textAreaSparql.setWrapStyleWord(true);
		textAreaSparql.setRows(5);
		textAreaSparql.setLineWrap(true);
		textAreaSparql.setFont(new Font("Tahoma", Font.BOLD, 12));
		// panelQuery.add(textAreaSparql, "cell 1 0,grow");
		scrollPaneSparql.setViewportView(textAreaSparql);
		
		JPanel panelValueSelection = new JPanel();
		panelValueSelection.setBorder(new TitledBorder(null, VALUE_FOR_CREATING_TARGET_INSTANCES_IRI, TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLACK));
		panelValueSelection.setBackground(Color.WHITE);
		panelInstanceMatching.add(panelValueSelection, "cell 0 2 2 1,grow");
		panelValueSelection.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		
		JPanel panelValueHolder = new JPanel();
		JPanel panelValue = new JPanel();
		JPanel panelOther = new JPanel();
		JPanel panelExpression = getConceptExpressionPanel();
		
		JLabel lblValueType = new JLabel(VALUE_TYPE);
		lblValueType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelection.add(lblValueType, "cell 0 0,alignx trailing");
		
		comboBoxValueType = new JComboBox(Methods.getKeyTypes());
		comboBoxValueType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String type = comboBoxValueType.getSelectedItem().toString();

				switch (type) {
				    case Variables.SOURCE_ATTRIBUTE:
				        setPanel(PANEL_SOURCE_ATTRIBUTE, panelValue);
				        break;

				    case Variables.EXPRESSION:
				        setPanel(PANEL_CONCEPT_EXPRESSION, panelExpression);
				        break;

				    default:
				        setPanel(PANEL_CONCEPT_OTHER, panelOther);
				        break;
				}

				panelValueHolder.repaint();
				panelValueHolder.revalidate();

			}

			private void setPanel(String panelType, JPanel panel) {
			    panelValueHolder.removeAll();
			    panelValueHolder.add(panel, panelType);
			}
		});
		comboBoxValueType.setBackground(Color.WHITE);
		comboBoxValueType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelection.add(comboBoxValueType, "cell 1 0,growx");
		
		panelValueHolder.setBackground(Color.WHITE);
		panelValueSelection.add(panelValueHolder, "cell 0 1 2 1,grow");
		panelValueHolder.setLayout(new CardLayout(0, 0));
		
		// panelValueHolder.add(panelExpression, PANEL_CONCEPT_EXPRESSION);
		
		panelOther.setBackground(Color.WHITE);
		// panelValueHolder.add(panelOther, PANEL_CONCEPT_OTHER);
		panelOther.setLayout(new MigLayout("", "[]", "[]"));
		
		panelValue.setBackground(Color.WHITE);
		panelValueHolder.add(panelValue, PANEL_SOURCE_ATTRIBUTE);
		panelValue.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel lblValue = new JLabel(VALUE);
		lblValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValue.add(lblValue, "cell 0 0,alignx trailing");
		
		textFieldValue = new JTextField();
		textFieldValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValue.add(textFieldValue, "cell 1 0,growx");
		textFieldValue.setColumns(10);
		
		JLabel lblOperation = new JLabel(OPERATION);
		lblOperation.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelection.add(lblOperation, "cell 0 2,alignx trailing");
		
		textFieldOperation = new JTextField();
		textFieldOperation.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelValueSelection.add(textFieldOperation, "cell 1 2,growx");
		textFieldOperation.setColumns(10);
		
		JPanel panelCommon = new JPanel();
		panelCommon.setBorder(new TitledBorder(null, COMMON_PROPERTY, TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLACK));
		panelCommon.setBackground(Color.WHITE);
		panelHead.add(panelCommon, "cell 0 3 2 1,grow");
		panelCommon.setLayout(new MigLayout("", "[][grow]", "[][]"));
		
		JLabel lblSourceCommonProperty = new JLabel(SOURCE_COMMON_PROPERTY);
		lblSourceCommonProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(lblSourceCommonProperty, "cell 0 0,alignx trailing");
		
		ArrayList<String> propertyList = getAllCommonProperties(true);
		
		JComboBox comboBoxSourceProperty = new JComboBox(propertyList.toArray());
		comboBoxSourceProperty.setBackground(Color.WHITE);
		comboBoxSourceProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(comboBoxSourceProperty, "cell 1 0,growx");
		
		JLabel lblTargetCommonProperty = new JLabel(TARGET_COMMON_PROPERTY);
		lblTargetCommonProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(lblTargetCommonProperty, "cell 0 2,alignx trailing");
		
		ArrayList<String> propertyList2 = getAllCommonProperties(false);
		
		JComboBox comboBoxTargetProperty = new JComboBox(propertyList2.toArray());
		comboBoxTargetProperty.setBackground(Color.WHITE);
		comboBoxTargetProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(comboBoxTargetProperty, "cell 1 2,growx");
		
		JCheckBox chckbxBothSame = new JCheckBox(BOTH_SAME);
		chckbxBothSame.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxBothSame.isSelected()) {
					comboBoxTargetProperty.setSelectedItem(comboBoxSourceProperty.getSelectedItem());
				}
			}
		});
		chckbxBothSame.setBackground(Color.WHITE);
		panelCommon.add(chckbxBothSame, "cell 1 1");
		
		JButton btnSaveConcept = new JButton(SAVE_CONCEPT);
		btnSaveConcept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dataset = comboBoxDataset.getSelectedItem().toString().trim();
				String sourceConcept = textFieldSourceConcept.getText().toString().trim();
				String targetConcept = textFieldTargetConcept.getText().toString().trim();
				String relation = comboBoxRelation.getSelectedItem().toString();
				String instanceToBeMapped = comboBoxMapped.getSelectedItem().toString();
				String operation = textFieldOperation.getText().toString().trim();
				String sourceCommonProperty = comboBoxSourceProperty.getSelectedItem().toString();
				String targetCommonProperty = comboBoxTargetProperty.getSelectedItem().toString();
				String sourceABoxPathString = textFieldSourceABoxPath.getText().toString().trim();
				String targetABoxPathString = textFieldTargetABoxPath.getText().toString().trim();

				if (!areInputsValid(dataset, sourceConcept, targetConcept, instanceToBeMapped)) {
				    JOptionPane.showMessageDialog(null, "Check all inputs", "Error Message", JOptionPane.ERROR_MESSAGE);
				} else {
				    String valueType = comboBoxValueType.getSelectedItem().toString();
				    String value = "";

				    switch (valueType) {
				        case Variables.SOURCE_ATTRIBUTE:
				            value = textFieldValue.getText().toString().trim();
				            break;
				        case Variables.EXPRESSION:
				            value = textAreaKeyAttribute.getText().toString().trim();
				            break;
				    }

				    if (value.length() == 0) {
				        JOptionPane.showMessageDialog(null, "Check all inputs", "Error Message", JOptionPane.ERROR_MESSAGE);
				    } else {
				        if (recordExtraction == null) {
				            createNewFileIfNotExists();
				        }

				        recordExtraction.addNewConcept(instanceToBeMapped, dataset, sourceConcept, targetConcept, relation, value, operation, valueType, sourceCommonProperty, targetCommonProperty, sourceABoxPathString, targetABoxPathString, mapIRI);
				        recordExtraction.reloadAll();
				        refreshMappingDefinition();

				        resetFields();
				    }
				}
			}

			private void resetFields() {
			    textFieldSourceConcept.setText("");
			    textFieldTargetConcept.setText("");
			    textFieldOperation.setText("");
			    textAreaSparql.setText("");
			    textFieldValue.setText("");
			    textAreaKeyAttribute.setText("");
			    textFieldSourceABoxPath.setText("");

			    comboBoxDataset.setSelectedIndex(0);
			    comboBoxRelation.setSelectedIndex(0);
			    comboBoxMapped.setSelectedIndex(0);
			    comboBoxValueType.setSelectedIndex(0);
			}

			private boolean areInputsValid(String dataset, String sourceConcept,
					String targetConcept, String instanceToBeMapped) {
			    return sourceConcept.length() != 0 && targetConcept.length() != 0 &&
			    		instanceToBeMapped.length() != 0 && dataset.length() != 0;
			}
		});
		btnSaveConcept.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHead.add(btnSaveConcept, "cell 0 4 2 1,center");
	}

	private ArrayList<String> getAllCommonProperties(boolean isSource) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<String>();
		
		if (isSource) {
			arrayList.add("onto:SourceIRI");
		} else {
			arrayList.add("onto:TargetIRI");
		}
		
		ArrayList<String> arrayListOne = getDefinitionCommonProperties(sourceDefinition);
		ArrayList<String> arrayListTwo = getDefinitionCommonProperties(targetDefinition);
		
		for (String string : arrayListOne) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		for (String string : arrayListTwo) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		FileMethods fileMethods = new FileMethods();
		return fileMethods.sortArrayList(arrayList);
	}
	
	private ArrayList<String> getDefinitionCommonProperties(TBoxDefinition definition) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (String string : definition.getObjectList()) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		for (String string : definition.getDataList()) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		for (String string : definition.getAttributeList()) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		for (String string : definition.getRollupList()) {
			if (!arrayList.contains(string)) {
				arrayList.add(string);
			}
		}
		
		return arrayList;
	}

	protected void addDatasetButtonHandler() {
		// TODO Auto-generated method stub
		panelDataset.removeAll();
		panelDataset.repaint();
		panelDataset.revalidate();
		
		JLabel lblDatasetName = new JLabel(DATASET_NAME);
		lblDatasetName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblDatasetName, "cell 0 0,alignx trailing");
		
		textFieldDataset = new JTextField();
		textFieldDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(textFieldDataset, "cell 1 0,growx");
		textFieldDataset.setColumns(10);
		
		JLabel lblSourceName = new JLabel(SOURCE_NAME);
		lblSourceName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblSourceName, "cell 0 1,alignx trailing");
		
		// textFieldSource = new JTextField();
		textFieldSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(textFieldSource, "cell 1 1,growx");
		textFieldSource.setColumns(10);
		
		JLabel lblTargetName = new JLabel(TARGET_NAME);
		lblTargetName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblTargetName, "cell 0 2,alignx trailing");
		
		// textFieldTarget = new JTextField();
		textFieldTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(textFieldTarget, "cell 1 2,growx");
		textFieldTarget.setColumns(10);
		
		JButton btnSaveDataset = new JButton(SAVE);
		btnSaveDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dataset = textFieldDataset.getText().toString().trim();
				String source = textFieldSource.getText().toString().trim();
				String target = textFieldTarget.getText().toString().trim();

				if (isValidInput(dataset, source, target)) {
				    if (recordExtraction != null) {
				        recordExtraction.addNewDataset(MAP_PREFIX + dataset, source, target, mapIRI);
				        recordExtraction.reloadAll();
				        refreshMappingDefinition();
				    } else {
				        createNewFileIfNotExists();
				        recordExtraction.addNewDataset(MAP_PREFIX + dataset, source, target, mapIRI);
				        recordExtraction.reloadAll();
				        refreshMappingDefinition();
				    }

				    resetInputFields();
				} else {
				    showMessageDialog("Check all inputs");
				}
			}
		});
		btnSaveDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(btnSaveDataset, "cell 0 3 2 1,center");
	}
	
	private boolean isValidInput(String dataset, String source, String target) {
	    return !dataset.isEmpty() && !dataset.equals(MAP_PREFIX) &&
	           !source.isEmpty() && !source.equals("onto:") &&
	           !target.isEmpty() && !target.equals("onto:");
	}

	private void resetInputFields() {
	    textFieldDataset.setText("");
	    textFieldSource.setText("");
	    textFieldTarget.setText("");
	}

	protected void createNewFileIfNotExists() {
	    recordMethods.createNewFile(RECORD_TEMP_FILE_TTL);
	    LinkedHashMap<String, String> allPrefixes = new LinkedHashMap<>();

	    collectPrefixes(sourceDefinition, allPrefixes);
	    collectPrefixes(targetDefinition, allPrefixes);
	    allPrefixes.putAll(recordDefinition.getAllPredefinedPrefixes());

	    String data = recordDefinition.getPrefixStrings(allPrefixes);

	    recordMethods.writeText(RECORD_TEMP_FILE_TTL, data);
	    recordExtraction = new MappingExtraction(RECORD_TEMP_FILE_TTL);
	    refreshMappingDefinition();
	}

	private void collectPrefixes(TBoxDefinition tBoxDefinition, LinkedHashMap<String, String> allPrefixes) {
	    if (tBoxDefinition != null) {
	        allPrefixes.putAll(tBoxDefinition.getPrefixMap());
	    }
	}

	protected void showMessageDialog(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, message);
	}

	protected boolean checkSourceConstruct(String source) {
		// TODO Auto-generated method stub
		int countFirst = 0, countSecond = 0;
		String regEx = "\\(";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(source);

		while (matcher.find()) {
			countFirst++;
		}

		String regEx2 = "\\)";
		Pattern pattern2 = Pattern.compile(regEx2);
		Matcher matcher2 = pattern2.matcher(source);

		while (matcher2.find()) {
			countSecond++;
		}

		if (countFirst == countSecond) {
			return true;
		} else {
			return false;
		}
	}

	protected void initializeDefinition(FileMethods fileMethods, TBoxDefinition tBoxDefinition,
			TBoxExtraction tBoxExtraction, JTree tree, String type) {
		// TODO Auto-generated method stub
		tBoxDefinition.setClassList(fileMethods.sortArrayList(tBoxExtraction.getClassList()));
		tBoxDefinition.setDataList(fileMethods.sortArrayList(tBoxExtraction.getDataList()));
		tBoxDefinition.setObjectList(fileMethods.sortArrayList(tBoxExtraction.getObjectList()));
		tBoxDefinition.setPrefixMap(tBoxExtraction.getPrefixMap());
		tBoxDefinition.setDimensionList(fileMethods.sortArrayList(tBoxExtraction.getDimensionList()));
		tBoxDefinition.setHierarchyList(fileMethods.sortArrayList(tBoxExtraction.getHierarchyList()));
		tBoxDefinition.setLevelList(fileMethods.sortArrayList(tBoxExtraction.getLevelList()));
		tBoxDefinition.setAttributeList(fileMethods.sortArrayList(tBoxExtraction.getAttributeList()));
		tBoxDefinition.setMeasureList(fileMethods.sortArrayList(tBoxExtraction.getMeasureList()));
		tBoxDefinition.setRollupList(fileMethods.sortArrayList(tBoxExtraction.getRollupList()));
		tBoxDefinition.setDatasetList(fileMethods.sortArrayList(tBoxExtraction.getDatasetList()));
		tBoxDefinition.setCubeList(tBoxExtraction.getCubeList());
		tBoxDefinition.setCuboidList(tBoxExtraction.getCuboidList());

		refreshTree(tBoxDefinition, tBoxExtraction, tree, type);
	}

	private void refreshMappingDefinition() {
		// TODO Auto-generated method stub
		recordDefinition.setDatasetList(recordMethods.sortArrayList(recordExtraction.getDatasetList()));
		recordDefinition.setMapperList(recordMethods.sortArrayList(recordExtraction.getMapperList()));
		recordDefinition.setRecordList(recordMethods.sortArrayList(recordExtraction.getRecordList()));
		recordDefinition.setPrefixMap(recordExtraction.getPrefixMap());

		recordDefinition.setModelText(recordExtraction.getModelText("TTL"));

		refreshAll();
	}

	private void refreshAll() {
		// TODO Auto-generated method stub
		refreshMapTree();
		refreshTextArea();
	}

	private void refreshTextArea() {
		// TODO Auto-generated method stub
		textAreaMap.setText(recordDefinition.getModelText());
	}

	private void refreshMapTree() {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Mapping");
		DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode("Datasets");
		rootNode.add(datasetNode);

		for (int i = 0; i < recordDefinition.getDatasetList().size(); i++) {
			DefaultMutableTreeNode eachDatasetNode = new DefaultMutableTreeNode(recordDefinition.getDatasetList().get(i));
			datasetNode.add(eachDatasetNode);
			
			ArrayList<String> mapperList = recordExtraction.extractAssociatedMapperList(recordDefinition.getDatasetList().get(i));
			if (mapperList.size() > 0) {
				DefaultMutableTreeNode mapperNode = new DefaultMutableTreeNode("ConceptMappers");
				eachDatasetNode.add(mapperNode);
				
				for (int j = 0; j < mapperList.size(); j++) {	
					DefaultMutableTreeNode eachMapperNode = new DefaultMutableTreeNode(mapperList.get(j));
					mapperNode.add(eachMapperNode);
					ArrayList<String> recordList = recordExtraction.extractAssociatedRecordList(recordDefinition.getDatasetList().get(i), mapperList.get(j));
					if (recordList.size() > 0) {
						DefaultMutableTreeNode associatedRecordNode = new DefaultMutableTreeNode("PropertyRecords");
						for (int k = 0; k < recordList.size(); k++) {	
							associatedRecordNode.add(new DefaultMutableTreeNode(recordList.get(k)));
							eachMapperNode.add(associatedRecordNode);
						}
					}
				}
			}
		}

		treeMap.setModel(new DefaultTreeModel(rootNode));
	}
	
	private void refreshTree(TBoxDefinition tBoxDefinition, TBoxExtraction tBoxExtraction, JTree tree, String type) {
	    DefaultMutableTreeNode sourceNode = new DefaultMutableTreeNode(type);

	    buildClassNodes(sourceNode, tBoxDefinition, tBoxExtraction);
	    buildDataStructureNodes(sourceNode, tBoxDefinition, tBoxExtraction);

	    tree.setModel(new DefaultTreeModel(sourceNode));
	}

	private void buildClassNodes(DefaultMutableTreeNode sourceNode, TBoxDefinition tBoxDefinition, TBoxExtraction tBoxExtraction) {
	    for (String className : tBoxDefinition.getClassList()) {
	        DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(className);
	        sourceNode.add(classNode);

	        buildPropertyNodes(classNode, Variables.OBJECT_PROPERTIES, tBoxExtraction.getAllAssociatedObjectProperties(className));
	        buildPropertyNodes(classNode, Variables.DATATYPE_PROPERTIES, tBoxExtraction.getAllAssociatedDataProperties(className));
	    }
	}

	private void buildPropertyNodes(DefaultMutableTreeNode classNode, String nodeName, ArrayList<String> properties) {
	    if (!properties.isEmpty()) {
	        DefaultMutableTreeNode propertyNode = new DefaultMutableTreeNode(nodeName);
	        classNode.add(propertyNode);

	        for (String property : properties) {
	            propertyNode.add(new DefaultMutableTreeNode(property));
	        }
	    }
	}
	
	private void buildDataStructureNodes(DefaultMutableTreeNode sourceNode, TBoxDefinition tBoxDefinition, TBoxExtraction tBoxExtraction) {
	    DefaultMutableTreeNode dataStructureNode = new DefaultMutableTreeNode(Variables.DATA_STRUCTURE);
	    sourceNode.add(dataStructureNode);

	    buildCubeNodes(dataStructureNode, tBoxDefinition.getCubeList().keySet());
	    buildCuboidNodes(dataStructureNode, tBoxDefinition.getCuboidList().keySet());
	    buildDatasetNodes(dataStructureNode, tBoxDefinition.getDatasetList(), tBoxExtraction);
	    buildDimensionNodes(sourceNode, tBoxDefinition.getDimensionList());
	    buildLevelNodes(sourceNode, tBoxDefinition.getLevelList(), tBoxExtraction, Variables.LEVEL);
	}
	
	private void buildDimensionNodes(DefaultMutableTreeNode sourceNode, ArrayList<String> dimensionList) {
	    if (!dimensionList.isEmpty()) {
	        DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode(Variables.DIMENSION);

	        for (String dimension : dimensionList) {
	            dimensionNode.add(new DefaultMutableTreeNode(dimension));
	        }

	        sourceNode.add(dimensionNode);
	    }
	}

	
	private void buildDatasetNodes(DefaultMutableTreeNode dataStructureNode, ArrayList<String> datasetList, TBoxExtraction tBoxExtraction) {
	    DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode(Variables.DATASET);
	    dataStructureNode.add(datasetNode);

	    for (String dataset : datasetList) {
	        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(dataset);

	        ArrayList<String> levels = tBoxExtraction.extractDatasetLevels(dataset);
	        ArrayList<String> measures = tBoxExtraction.extractDatasetMeasures(dataset);

	        buildLevelNodes(rootNode, levels, tBoxExtraction, Variables.DIM_OR_LEVEL_PROPERTIES);
	        buildMeasureNodes(rootNode, measures, tBoxExtraction);

	        datasetNode.add(rootNode);
	    }
	}

	private void buildLevelNodes(DefaultMutableTreeNode parentNode, ArrayList<String> levels, TBoxExtraction tBoxExtraction, String nodeName) {
	    if (!levels.isEmpty()) {
	        DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode(nodeName);

	        for (String level : levels) {
	            levelNode.add(new DefaultMutableTreeNode(level));
	        }

	        parentNode.add(levelNode);
	    }
	}

	private void buildMeasureNodes(DefaultMutableTreeNode parentNode, ArrayList<String> measures, TBoxExtraction tBoxExtraction) {
	    if (!measures.isEmpty()) {
	        DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode(Variables.MEASURE_PROPERTIES);

	        for (String measure : measures) {
	            measureNode.add(new DefaultMutableTreeNode(measure));
	        }

	        parentNode.add(measureNode);
	    }
	}


	private void buildCubeNodes(DefaultMutableTreeNode dataStructureNode, Set<String> cubeList) {
	    DefaultMutableTreeNode cubeNode = new DefaultMutableTreeNode(Variables.CUBE);
	    dataStructureNode.add(cubeNode);

	    for (String cube : cubeList) {
	        cubeNode.add(new DefaultMutableTreeNode(cube));
	    }
	}
	
	private void buildCuboidNodes(DefaultMutableTreeNode dataStructureNode, Set<String> cuboidList) {
	    DefaultMutableTreeNode cuboidNode = new DefaultMutableTreeNode(Variables.CUBOID);
	    dataStructureNode.add(cuboidNode);

	    for (String cuboid : cuboidList) {
	        cuboidNode.add(new DefaultMutableTreeNode(cuboid));
	    }
	}
	
	private void initializeAll() {
		// TODO Auto-generated method stub
		sourceDefinition = new TBoxDefinition();
		targetDefinition = new TBoxDefinition();
		recordDefinition = new MappingDefinition();

		sourceMethods = new FileMethods();
		targetMethods = new FileMethods();
		recordMethods = new FileMethods();

		textFieldSource = new JTextField();
		textFieldTarget = new JTextField();

		textFieldSourceMapper = new JTextField();
		textFieldTargetMapper = new JTextField();

		textFieldKey = new JTextField();
		textFieldTargetProperty = new JTextField();

		textAreaSourceExpression = new JTextArea();
		textFieldSourceProperty = new JTextField();
		textFieldTargetProperty = new JTextField();
		textFieldTargetSparql = new JTextField();
		textFieldTargetExpression = new JTextField();
		
		textFieldOperation = new JTextField();
		textFieldValue = new JTextField();
		textAreaSparql = new JTextArea();
		textFieldSourceConcept = new JTextField();
		textFieldTargetConcept = new JTextField();
		
		textFieldSourceProperty = new JTextField();
		textFieldTargetProperty = new JTextField();
	}

	private void instantiateAllTree() {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode sourceNode = new DefaultMutableTreeNode("Source");
		treeSource.setModel(new DefaultTreeModel(sourceNode));

		DefaultMutableTreeNode targetNode = new DefaultMutableTreeNode("Target");
		treeTarget.setModel(new DefaultTreeModel(targetNode));

		DefaultMutableTreeNode mapNode = new DefaultMutableTreeNode("Mapping");
		treeMap.setModel(new DefaultTreeModel(mapNode));
	}

	private void setRenderer(JTree tree) {
		// TODO Auto-generated method stub
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
	}

	public String getSourceConstruct() {
		return sourceConstruct;
	}

	protected void setSourceConstruct(String selectedResource) {
		// TODO Auto-generated method stub
		textFieldSourceProperty.setText(selectedResource);
		setSourceExpression(selectedResource);
	}

	protected void setKeyOperation(String op) {
		// TODO Auto-generated method stub
		String text = textAreaKeyAttribute.getText().toString().trim();
		if (text.length() != 0) {
			String lastIndex = text.substring(text.length() - 1);

			if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && !text.contains(")")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && text.contains(")")) {
				textAreaKeyAttribute.setText(op + "(");
			} else {
				textAreaKeyAttribute.setText(op + "(" + textAreaKeyAttribute.getText().toString().trim());
			}
		} else {
			textAreaKeyAttribute.setText(op + "(");
		}
	}
	
	protected void setKeyOperator(String syntax) {
		// TODO Auto-generated method stub
		String text = textAreaKeyAttribute.getText().toString().trim();
		if (text.length() != 0) {
			String lastIndex = text.substring(text.length() - 1);

			if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && !text.contains(")")) {
				textAreaKeyAttribute.setText(textAreaKeyAttribute.getText().toString().trim() + " " + syntax);
			} else if (text.contains("(") && text.contains(")")) {
				showMessageDialog("Syntax Error");
			} else {
				textAreaKeyAttribute.setText(textAreaKeyAttribute.getText().toString().trim() + " " + syntax);
			}
		} else {
			showMessageDialog("Syntax Error");
		}
	}

	private void setSourceExpression(String selectedResource) {
		// TODO Auto-generated method stub
		String text = textAreaSourceExpression.getText().toString().trim();
		if (text.length() != 0) {
			String lastIndex = text.substring(text.length() - 1);

			if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")
					|| lastIndex.equals("(")) {
				textAreaSourceExpression
						.setText(textAreaSourceExpression.getText().toString().trim() + " " + selectedResource);
			} else if (text.contains("(") && !text.contains(")")) {
				textAreaSourceExpression
						.setText(textAreaSourceExpression.getText().toString().trim() + ", " + selectedResource + ")");
			} else {
				textAreaSourceExpression.setText(selectedResource);
			}
		} else {
			textAreaSourceExpression.setText(selectedResource);
		}
	}

	protected void setSourceOperator(String syntax) {
		// TODO Auto-generated method stub
		String text = textAreaSourceExpression.getText().toString().trim();
		if (text.length() != 0) {
			String lastIndex = text.substring(text.length() - 1);

			if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && !text.contains(")")) {
				textAreaSourceExpression.setText(textAreaSourceExpression.getText().toString().trim() + " " + syntax);
			} else if (text.contains("(") && text.contains(")")) {
				showMessageDialog("Syntax Error");
			} else {
				textAreaSourceExpression.setText(textAreaSourceExpression.getText().toString().trim() + " " + syntax);
			}
		} else {
			showMessageDialog("Syntax Error");
		}
	}

	protected void setSourceOperation(String op) {
		// TODO Auto-generated method stub
		String text = textAreaSourceExpression.getText().toString().trim();
		if (text.length() != 0) {
			String lastIndex = text.substring(text.length() - 1);

			if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && !text.contains(")")) {
				showMessageDialog("Syntax Error");
			} else if (text.contains("(") && text.contains(")")) {
				textAreaSourceExpression.setText(op + "(");
			} else {
				textAreaSourceExpression.setText(op + "(" + textAreaSourceExpression.getText().toString().trim());
			}
		} else {
			textAreaSourceExpression.setText(op + "(");
		}
	}
}
