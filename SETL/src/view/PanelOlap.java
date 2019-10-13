package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import controller.Definition;
import helper.AccessVariables;
import helper.CheckListRenderer;
import helper.EnabledComboBoxRenderer;
import helper.Methods;
import helper.MyDisabledTreeRenderer;
import helper.MyListCellRenderer;
import model.CheckListItem;
import model.SelectedLevel;
import model.SelectedLevelInstance;
import model.TwinValue;
import net.miginfocom.swing.MigLayout;

public class PanelOlap extends JPanel {
	private static final long serialVersionUID = 1L;
	private Methods methods;
	private Definition definition;
	private JDialog dialog;
	private JLabel lblTboxPath;
	private JLabel lblAboxPath;
	private JLabel lblCurrentLevelValue;
	private JList listSchemaIRI;
	private JList listSchemaGraph;
	private JList listInstanceGraph;
	private JList listInstance;
	private JLabel lblDatasetIriValue;
	private JLabel lblObservationValue;
	private JLabel lblQbolapVersionValue;
	private JTree treeDimension;
	private JTree treeMeasure;
	private JComboBox<String> comboBoxProperty;
	private JComboBox<String> comboBoxFilterCondition;
	private JComboBox<String> comboBoxDataset;
	private JTextArea textAreaQuery;
	private boolean isSingle;
	private boolean isEnabled;
	private JPanel panelMeasureHolder;
	private JPanel panelLevelHolder;
	private JList listSelectionProperty;
	private static final int[] SELECTION_INTERVAL = { 0, 1 };
    private DefaultListSelectionModel model = new DefaultListSelectionModel();
    private EnabledComboBoxRenderer enableRenderer = new EnabledComboBoxRenderer();

    private EnabledListener enabledListener = new EnabledListener();
    private DisabledListener disabledListener = new DisabledListener();
    private ArrayList<String> bannedLevels = new ArrayList<>();

	/**
	 * Create the panel.
	 */
	public PanelOlap() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));

		initializeAll();

		JPanel panelOlapButtons = new JPanel();
		panelOlapButtons.setBackground(Color.WHITE);
		add(panelOlapButtons, BorderLayout.NORTH);

		JButton btnLocalFile = new JButton("Local File");
		btnLocalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				isSingle = false;

				JPanel panelHolder = new JPanel();
				panelHolder.setBackground(Color.WHITE);
				panelHolder.setLayout(new BorderLayout(0, 0));

				JPanel panelBoth = new JPanel();
				JPanel panelSingle = new JPanel();
				panelSingle.setBackground(Color.WHITE);
				panelHolder.add(panelSingle, BorderLayout.CENTER);
				panelSingle.setLayout(new MigLayout("", "[][800px, grow][]", "[][]"));

				JLabel lblLocalFile = new JLabel("Local RDF File:");
				lblLocalFile.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelSingle.add(lblLocalFile, "cell 0 0,alignx trailing");

				JTextField textFieldLocalFile = new JTextField();
				textFieldLocalFile.setMargin(new Insets(5, 5, 5, 5));
				textFieldLocalFile.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelSingle.add(textFieldLocalFile, "cell 1 0,growx");
				textFieldLocalFile.setColumns(10);

				JButton btnOpenLocal = new JButton("Open");
				btnOpenLocal.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String filePath = methods.chooseFile("Select Local File");

						if (filePath != null) {
							textFieldLocalFile.setText(filePath);
						}
					}
				});
				btnOpenLocal.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelSingle.add(btnOpenLocal, "cell 2 0");

				JCheckBox chckbxUseSameFile = new JCheckBox("Use same file for both");
				JCheckBox chckbxUseTwoSeperate = new JCheckBox("Use two seperate Files");
				chckbxUseTwoSeperate.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (chckbxUseTwoSeperate.isSelected()) {
							panelHolder.removeAll();
							panelHolder.add(panelBoth, BorderLayout.CENTER);
							panelHolder.repaint();
							panelHolder.revalidate();
							chckbxUseSameFile.setSelected(false);

							isSingle = false;
						}
					}
				});
				chckbxUseTwoSeperate.setBackground(Color.WHITE);
				chckbxUseTwoSeperate.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelSingle.add(chckbxUseTwoSeperate, "cell 1 1");

				panelBoth.setBackground(Color.WHITE);
				panelHolder.add(panelBoth, BorderLayout.CENTER);
				panelBoth.setLayout(new MigLayout("", "[][800px, grow]", "[][][]"));

				JLabel lblTbox = new JLabel("TBox File:");
				lblTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(lblTbox, "cell 0 0,alignx trailing");

				JTextField textFieldTBox = new JTextField("C:\\Users\\Amrit\\Documents\\OnDemandETL\\bd_tbox.ttl");
				textFieldTBox.setMargin(new Insets(5, 5, 5, 5));
				textFieldTBox.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(textFieldTBox, "flowx,cell 1 0,growx");
				textFieldTBox.setColumns(10);

				JButton btnTBoxOpen = new JButton("Open");
				btnTBoxOpen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String filePath = methods.chooseFile("Select TBox File");

						if (filePath != null) {
							textFieldTBox.setText(filePath);
						}
					}
				});
				btnTBoxOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(btnTBoxOpen, "cell 1 0");

				JLabel lblAbox = new JLabel("ABox  File:");
				lblAbox.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(lblAbox, "cell 0 1,alignx trailing");

				JTextField textFieldABox = new JTextField("C:\\Users\\Amrit\\Documents\\OnDemandETL\\Fact_Census_C04_TargetABox.ttl");
				textFieldABox.setMargin(new Insets(5, 5, 5, 5));
				textFieldABox.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(textFieldABox, "flowx,cell 1 1,growx");
				textFieldABox.setColumns(10);

				JButton btnABoxOpen = new JButton("Open");
				btnABoxOpen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String filePath = methods.chooseFile("Select ABox File");

						if (filePath != null) {
							textFieldABox.setText(filePath);
						}
					}
				});
				btnABoxOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(btnABoxOpen, "cell 1 1");

				chckbxUseSameFile.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						if (chckbxUseSameFile.isSelected()) {
							panelHolder.removeAll();
							panelHolder.add(panelSingle, BorderLayout.CENTER);
							panelHolder.repaint();
							panelHolder.revalidate();
							chckbxUseTwoSeperate.setSelected(false);

							isSingle = true;
						}
					}
				});
				chckbxUseSameFile.setBackground(Color.WHITE);
				chckbxUseSameFile.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelBoth.add(chckbxUseSameFile, "cell 1 2");

				int confirmation = JOptionPane.showConfirmDialog(null, panelHolder, "Please Select Files",
						JOptionPane.OK_CANCEL_OPTION);

				if (confirmation == JOptionPane.OK_OPTION) {
					if (isSingle) {
						String filepath = textFieldLocalFile.getText().toString().trim();

						if (filepath != null) {
							lblTboxPath.setText("TBox File: " + filepath);
							lblAboxPath.setText("ABox File: " + filepath);

							dialog = methods.getProgressDialog(getParent());

							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (definition.readTBoxModel(filepath) && definition.readABoxModel(filepath)) {
										definition.initializeOlapEndPoint(null);
									}

									comboBoxDataset.removeAllItems();
									for (String dataset : definition.getDatasetProperties().keySet()) {
										comboBoxDataset.addItem(dataset.trim());
									}

									dialog.dispose();
								}
							});
						}
					} else {
						String tboxPath = textFieldTBox.getText().toString().trim();
						String aboxPath = textFieldABox.getText().toString().trim();

						if (tboxPath != null && aboxPath != null) {
							lblTboxPath.setText("TBox File: " + tboxPath);
							lblAboxPath.setText("ABox File: " + aboxPath);

							dialog = methods.getProgressDialog(getParent());

							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (definition.readTBoxModel(tboxPath) && definition.readABoxModel(aboxPath)) {
										definition.initializeOlapEndPoint(null);
									}

									comboBoxDataset.removeAllItems();
									for (String dataset : definition.getDatasetProperties().keySet()) {
										comboBoxDataset.addItem(dataset.trim());
									}

									dialog.dispose();
								}
							});
						}
					}
				}
			}
		});
		btnLocalFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOlapButtons.add(btnLocalFile);

		JButton btnEndpoint = new JButton("EndPoint");
		btnEndpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JPanel panelInput = new JPanel();
				panelInput.setBackground(Color.WHITE);
				panelInput.setLayout(new MigLayout("", "[][600px, grow]", "[]"));

				JLabel lblEndpoint = new JLabel("EndPoint:");
				lblEndpoint.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelInput.add(lblEndpoint, "cell 0 0,alignx trailing");

				JTextField textFieldEndPoint = new JTextField(AccessVariables.ENDPOINT_TWO);
				textFieldEndPoint.setMargin(new Insets(5, 5, 5, 5));
				textFieldEndPoint.setFont(new Font("Tahoma", Font.BOLD, 12));
				panelInput.add(textFieldEndPoint, "cell 1 0,growx");
				textFieldEndPoint.setColumns(10);

				int confirmation = JOptionPane.showConfirmDialog(getParent(), panelInput, "EndPoint",
						JOptionPane.OK_CANCEL_OPTION);

				if (confirmation == JOptionPane.OK_OPTION) {
					String endPoint = textFieldEndPoint.getText().toString().trim();

					if (methods.checkString(endPoint)) {
						lblTboxPath.setText("TBox EndPoint: " + endPoint);
						lblAboxPath.setText("ABox EndPoint: " + endPoint);

						dialog = methods.getProgressDialog(getParent());

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								definition.initializeOlapEndPoint(endPoint);

								comboBoxDataset.removeAllItems();
								for (String dataset : definition.getDatasetProperties().keySet()) {
									comboBoxDataset.addItem(dataset.trim());
								}

								dialog.dispose();
							}
						});
					} else {
						methods.showDialog("Check File Path");
					}
				}
			}
		});
		btnEndpoint.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelOlapButtons.add(btnEndpoint);

		JPanel panelContainer = new JPanel();
		panelContainer.setBackground(Color.WHITE);
		add(panelContainer, BorderLayout.CENTER);
		panelContainer.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneFirst = new JSplitPane();
		splitPaneFirst.setResizeWeight(0.4);
		panelContainer.add(splitPaneFirst, "cell 0 0,grow");

		JPanel panelTreeHolder = new JPanel();
		splitPaneFirst.setLeftComponent(panelTreeHolder);
		panelTreeHolder.setBackground(Color.WHITE);
		panelTreeHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneOne = new JSplitPane();
		splitPaneOne.setResizeWeight(0.4);
		splitPaneOne.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneOne.setOneTouchExpandable(true);
		panelTreeHolder.add(splitPaneOne, "cell 0 0,grow");

		JScrollPane scrollPaneDataset = new JScrollPane();
		splitPaneOne.setLeftComponent(scrollPaneDataset);

		JPanel panelDataset = new JPanel();
		scrollPaneDataset.setViewportView(panelDataset);
		panelDataset.setBackground(Color.WHITE);
		panelDataset.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][]"));

		lblTboxPath = new JLabel("TBox Path:");
		lblTboxPath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelDataset.add(lblTboxPath, "cell 0 0 2 1,grow");

		lblAboxPath = new JLabel("ABox Path:");
		lblAboxPath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelDataset.add(lblAboxPath, "cell 0 1 2 1,grow");

		JLabel lblDataset = new JLabel("Dataset:");
		lblDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblDataset, "cell 0 2");

		comboBoxDataset = new JComboBox();
		comboBoxDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxDataset.getSelectedItem() != null) {
					String datasetName = comboBoxDataset.getSelectedItem().toString();

					LinkedHashMap<String, ArrayList<String>> hashMap = definition.getDatasetProperties()
							.get(datasetName);
					if (hashMap.containsKey("numobs")) {
						String numobs = methods.convertArrayListToString(hashMap.get("numobs"));
						lblObservationValue.setText(numobs);
					}

					if (hashMap.containsKey("version")) {
						String version = methods.convertArrayListToString(hashMap.get("version"));
						lblQbolapVersionValue.setText(version);
					}

					DefaultListModel<String> iriModel = new DefaultListModel<>();
					for (String string : hashMap.get("cubeuriString")) {
						iriModel.addElement(string);
					}
					listSchemaIRI.setModel(iriModel);

					DefaultListModel<String> schemaModel = new DefaultListModel<>();
					if (hashMap.containsKey("schemagraphString")) {
						for (String string : hashMap.get("schemagraphString")) {
							schemaModel.addElement(string);
						}
					}
					listSchemaGraph.setModel(schemaModel);

					DefaultListModel<String> instanceModel = new DefaultListModel<>();
					if (hashMap.containsKey("instancegraphString")) {
						for (String string : hashMap.get("instancegraphString")) {
							instanceModel.addElement(string);
						}
					}

					listInstanceGraph.setModel(instanceModel);
					lblDatasetIriValue.setText(datasetName);
				}
			}
		});
		comboBoxDataset.setBackground(Color.WHITE);
		comboBoxDataset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(comboBoxDataset, "cell 1 2,growx");

		JButton btnExtractCube = new JButton("Extract Cube");
		btnExtractCube.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBoxDataset.getSelectedItem() != null) {
					clearAllSelection();

					String dataset = comboBoxDataset.getSelectedItem().toString().trim();

					int schemaGraphSize = listSchemaGraph.getModel().getSize();
					int instanceGraphSize = listInstanceGraph.getModel().getSize();

					List<String> selectedGraphs = listSchemaGraph.getSelectedValuesList();
					List<String> selectedInstances = listInstanceGraph.getSelectedValuesList();

					if (schemaGraphSize == 0 || instanceGraphSize == 0) {
						dialog = methods.getProgressDialog(getParent());

						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								definition.extractDatasetCube(dataset, null, null);
								refreshDimensionTree();
								refreshMeasureTree();
								dialog.dispose();
							}
						});
					} else {
						if (selectedGraphs.size() == 0 || selectedInstances.size() == 0) {
							methods.showDialog("Select both schema graphs and instances");
						} else {
							dialog = methods.getProgressDialog(getParent());

							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									definition.extractDatasetCube(null, selectedGraphs, selectedInstances);
									refreshDimensionTree();
									refreshMeasureTree();
									dialog.dispose();
								}
							});
						}
					}
				}
			}
		});
		btnExtractCube.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(btnExtractCube, "cell 1 3,right");

		JLabel lblSchemaIri = new JLabel("Schema IRI:");
		lblSchemaIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblSchemaIri, "cell 0 4,aligny top");

		listSchemaIRI = new JList();
		listSchemaIRI.setValueIsAdjusting(true);
		listSchemaIRI.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listSchemaIRI.setBackground(Color.WHITE);
		MyListCellRenderer cellRenderer = new MyListCellRenderer();
		listSchemaIRI.setCellRenderer(cellRenderer);
		listSchemaIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(listSchemaIRI, "cell 1 4,grow");

		JLabel lblDatasetIri = new JLabel("Dataset IRI:");
		lblDatasetIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblDatasetIri, "cell 0 5");

		lblDatasetIriValue = new JLabel("");
		lblDatasetIriValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblDatasetIriValue, "cell 1 5");

		JLabel lblSchemaGraph = new JLabel("Schema Graph:");
		lblSchemaGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblSchemaGraph, "cell 0 6,aligny top");

		listSchemaGraph = new JList();
		listSchemaGraph.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSchemaGraph.setValueIsAdjusting(true);
		listSchemaGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		listSchemaGraph.setBackground(Color.WHITE);
		listSchemaGraph.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listSchemaGraph.setCellRenderer(cellRenderer);
		panelDataset.add(listSchemaGraph, "cell 1 6,grow");

		JLabel lblInstanceGraph = new JLabel("Instance Graph:");
		lblInstanceGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblInstanceGraph, "cell 0 7,aligny top");

		listInstanceGraph = new JList();
		listInstanceGraph.setValueIsAdjusting(true);
		listInstanceGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		listInstanceGraph.setBackground(Color.WHITE);
		listInstanceGraph.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listInstanceGraph.setCellRenderer(cellRenderer);
		panelDataset.add(listInstanceGraph, "cell 1 7,grow");

		JLabel lblQbolapVersion = new JLabel("QB4OLAP version:");
		lblQbolapVersion.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblQbolapVersion, "cell 0 8");

		lblQbolapVersionValue = new JLabel("");
		lblQbolapVersionValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblQbolapVersionValue, "cell 1 8");

		JLabel lblNoOfObservation = new JLabel("No. of observation:");
		lblNoOfObservation.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblNoOfObservation, "cell 0 9");

		lblObservationValue = new JLabel("");
		lblObservationValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDataset.add(lblObservationValue, "cell 1 9");

		JPanel panelCubeStructure = new JPanel();
		panelCubeStructure.setBackground(Color.WHITE);
		splitPaneOne.setRightComponent(panelCubeStructure);
		panelCubeStructure.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneTwo = new JSplitPane();
		splitPaneTwo.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneTwo.setResizeWeight(0.5);
		panelCubeStructure.add(splitPaneTwo, "cell 0 0,grow");

		JScrollPane scrollPaneDim = new JScrollPane();
		splitPaneTwo.setLeftComponent(scrollPaneDim);

		JPanel panelDimension = new JPanel();
		scrollPaneDim.setViewportView(panelDimension);
		panelDimension.setBackground(Color.WHITE);
		panelDimension.setLayout(new MigLayout("", "[grow]", "[grow]"));

		treeDimension = new JTree();
		treeDimension.setCellRenderer(new MyDisabledTreeRenderer(bannedLevels));
		treeDimension.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDimension
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					if (selectedNode.isLeaf()) {
						if (bannedLevels.contains(selectedNode.toString())) {
							JOptionPane.showMessageDialog(null,
			                        "You can't Select that Level", "ERROR",
			                        JOptionPane.ERROR_MESSAGE);
						} else {
							String selectedPath = treeDimension.getSelectionPath().toString();
							String selectedLevelName = treeDimension.getLastSelectedPathComponent().toString();

							SelectedLevel selectedLevel = new SelectedLevel(selectedLevelName, selectedPath);
							lblCurrentLevelValue.setText(selectedLevelName);
							if (!definition.getSelectedLevelList().contains(selectedLevel)) {
								definition.getSelectedLevelList().add(selectedLevel);

								refreshSelectedLevels();
							}

							dialog = methods.getProgressDialog(getParent());

							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									definition.extractLevelProperties(selectedLevelName);
									comboBoxProperty.removeAllItems();

									DefaultListModel<String> model = new DefaultListModel<String>();
									for (String property : definition.getLevelProperties()) {
										comboBoxProperty.addItem(property);
										model.addElement(property);
									}
									listSelectionProperty.setModel(model);

									comboBoxFilterCondition.setSelectedIndex(0);
									dialog.dispose();
								}
							});
						}
					}
				}
			}
		});
		treeDimension.setBackground(Color.WHITE);
		treeDimension.setFont(new Font("Tahoma", Font.BOLD, 12));
		treeDimension.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Dimensions") {
			{
			}
		}));
		panelDimension.add(treeDimension, "cell 0 0,grow");

		JScrollPane scrollPaneMeasure = new JScrollPane();
		splitPaneTwo.setRightComponent(scrollPaneMeasure);

		JPanel panelMeasure = new JPanel();
		scrollPaneMeasure.setViewportView(panelMeasure);
		panelMeasure.setBackground(Color.WHITE);
		panelMeasure.setLayout(new MigLayout("", "[grow]", "[grow]"));

		treeMeasure = new JTree();
		treeMeasure.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if (treeMeasure.getSelectionPath() != null) {
					String measurePath = treeMeasure.getSelectionPath().toString();
					String[] parts = measurePath.split(",");

					if (parts.length == 2) {
						String measureString = parts[1].trim();
						measureString = measureString.substring(0, parts[1].length() - 1);

						methods.showDialog("Select aggregate functions only");
					} else if (parts.length == 4) {
						String measureString = parts[1].trim();
						measureString = measureString.substring(0, parts[1].length() - 1);

						String functionString = parts[3].trim();
						functionString = functionString.substring(0, parts[3].length() - 2);

						methods.addToComplexHashMap(definition.getSelectedMeasureFunctionMap(), measureString, functionString);
						refreshSelectedMeasure();
					}
				}
			}
		});
		treeMeasure.setFont(new Font("Tahoma", Font.BOLD, 12));
		treeMeasure.setBackground(Color.WHITE);
		treeMeasure.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Measures") {
			{
			}
		}));
		panelMeasure.add(treeMeasure, "cell 0 0,grow");

		JPanel panelComponentHolder = new JPanel();
		splitPaneFirst.setRightComponent(panelComponentHolder);
		panelComponentHolder.setBackground(Color.WHITE);
		panelComponentHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelComponentHolder.add(tabbedPane, "cell 0 0,grow");

		JPanel panelDataHolder = new JPanel();
		tabbedPane.addTab("Olap Query Generation by MD Construct Selection", null, panelDataHolder, null);
		panelDataHolder.setBackground(Color.WHITE);
		panelDataHolder.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneThree = new JSplitPane();
		splitPaneThree.setResizeWeight(0.4);
		panelDataHolder.add(splitPaneThree, "cell 0 0,grow");

		JPanel panelSelectionHolder = new JPanel();
		splitPaneThree.setLeftComponent(panelSelectionHolder);
		panelSelectionHolder.setBackground(Color.WHITE);
		panelSelectionHolder.setLayout(new MigLayout("", "[][grow]", "[][][][][grow]"));

		JLabel lblCurrentLabel = new JLabel("Current Level:");
		lblCurrentLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(lblCurrentLabel, "cell 0 0");

		lblCurrentLevelValue = new JLabel("");
		lblCurrentLevelValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(lblCurrentLevelValue, "cell 1 0,grow");

		JLabel lblProperty = new JLabel("Property:");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(lblProperty, "cell 0 1,alignx left");

		comboBoxProperty = new JComboBox();
		comboBoxProperty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxProperty.getSelectedItem() != null) {
					// TODO Auto-generated method stub
					String selectedProperty = comboBoxProperty.getSelectedItem().toString();
					String selectedLevel = lblCurrentLevelValue.getText().toString().trim();

					if (methods.checkString(selectedProperty) && methods.checkString(selectedLevel)) {
						definition.extractLevelInstances(selectedLevel, selectedProperty);
					}

					SelectedLevelInstance selectedLevelInstance = new SelectedLevelInstance();
					if (definition.getInstancesMap().containsKey(selectedLevel)) {
						if (definition.getInstancesMap().get(selectedLevel).containsKey(selectedProperty)) {
							selectedLevelInstance = definition.getInstancesMap().get(selectedLevel)
									.get(selectedProperty);
						}
					}

					DefaultListModel<CheckListItem> defaultListModel = new DefaultListModel<>();
					
					if (definition.getLevelInstanceObjects().size() > 0) {
						if (definition.getLevelInstanceObjects().get(0) instanceof String) {
							// System.out.println("String");
							disableItemsInComboBox();
						} else {
							// System.out.println(definition.getLevelInstanceObjects().get(0));
							// System.out.println("Something else");
							enableItemsInComboBox();
						}
					}
					
					
					for (Object object : definition.getLevelInstanceObjects()) {
						CheckListItem checkListItem = new CheckListItem(object);
						if (selectedLevelInstance.containInstance(object)) {
							checkListItem.setSelected(true);
						}
						defaultListModel.addElement(checkListItem);
					}
					listInstance.setModel(defaultListModel);
				}
			}
		});
		comboBoxProperty.setBackground(Color.WHITE);
		comboBoxProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(comboBoxProperty, "cell 1 1,growx");

		JLabel lblFilterCondition = new JLabel("Filter Condition:");
		lblFilterCondition.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(lblFilterCondition, "cell 0 2,alignx leading");

		comboBoxFilterCondition = new JComboBox();
		model.addSelectionInterval(SELECTION_INTERVAL[0], SELECTION_INTERVAL[0]);
        enableRenderer.setEnabledItems(model);
        comboBoxFilterCondition.setRenderer(enableRenderer);
		comboBoxFilterCondition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isEnabled) {
					if (listInstance != null) {
						ListModel<CheckListItem> listModel = listInstance.getModel();

						for (int i = 0; i < listModel.getSize(); i++) {
							CheckListItem checkListItem = listModel.getElementAt(i);
							checkListItem.setSelected(false);
							listInstance.repaint(listInstance.getCellBounds(i, i));
						}

						if (comboBoxProperty.getSelectedItem() != null) {
							copyInstances();
						}
					}
				}
			}
		});
		comboBoxFilterCondition.setBackground(Color.WHITE);
		comboBoxFilterCondition.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(comboBoxFilterCondition, "cell 1 2,growx");

		int count = 0;
		for (String conditionString : definition.getOlapConditionalHashMap().keySet()) {
			if (count > 0) {
				comboBoxFilterCondition.addItem(conditionString);
			}
			count++;
		}
		
		JLabel lblPropertiesForSelection = new JLabel("Properties to be Viewed:");
		lblPropertiesForSelection.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSelectionHolder.add(lblPropertiesForSelection, "cell 0 3");
		
		listSelectionProperty = new JList();
		listSelectionProperty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String levelString = lblCurrentLevelValue.getText().toString().trim();
				List<String> strings = listSelectionProperty.getSelectedValuesList();
				
				for (int i = 0; i < definition.getSelectedLevelList().size(); i++) {
					SelectedLevel level = definition.getSelectedLevelList().get(i);

					if (level.match(levelString)) {
						level.setViewProperties(strings);
						definition.getSelectedLevelList().set(i, level);
						break;
					}
				}
				
				refreshSelectedLevels();
			}
		});
		listSelectionProperty.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listSelectionProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		listSelectionProperty.setBackground(Color.WHITE);
		panelSelectionHolder.add(listSelectionProperty, "cell 1 3,grow");

		JPanel panelInstanceSelection = new JPanel();
		panelInstanceSelection.setBackground(Color.WHITE);
		panelSelectionHolder.add(panelInstanceSelection, "cell 0 4 2 1,grow");
		panelInstanceSelection.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneInstance = new JScrollPane();
		panelInstanceSelection.add(scrollPaneInstance, "cell 0 0,grow");

		listInstance = new JList();
		listInstance.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JList list = (JList) arg0.getSource();
				int index = list.locationToIndex(arg0.getPoint());

				String selectedCondition = comboBoxFilterCondition.getSelectedItem().toString();
				TwinValue twinValue = definition.getOlapConditionalHashMap().get(selectedCondition);

				ListModel<CheckListItem> listModel = listInstance.getModel();

				int selected = 0;
				for (int i = 0; i < listModel.getSize(); i++) {
					CheckListItem checkListItem = listModel.getElementAt(i);
					if (checkListItem.isSelected()) {
						selected++;
					}
				}

				if (twinValue.getSecondValue().equals("single")) {
					if (selected < 1) {
						CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
						item.setSelected(!item.isSelected());
						list.repaint(list.getCellBounds(index, index));
					} else {
						methods.showDialog("This filter condition doesn't allow multiple selection");

						for (int i = 0; i < listModel.getSize(); i++) {
							CheckListItem checkListItem = listModel.getElementAt(i);
							checkListItem.setSelected(false);
							list.repaint(list.getCellBounds(i, i));
						}

						copyInstances();
						return;
					}
				} else {
					CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
					item.setSelected(!item.isSelected());
					list.repaint(list.getCellBounds(index, index));
				}

				copyInstances();
			}
		});
		listInstance.setCellRenderer(new CheckListRenderer());
		scrollPaneInstance.setViewportView(listInstance);
		listInstance.setBackground(Color.WHITE);
		listInstance.setFont(new Font("Tahoma", Font.BOLD, 12));

		JPanel panelSummaryHolder = new JPanel();
		panelSummaryHolder.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Selection Summary",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		splitPaneThree.setRightComponent(panelSummaryHolder);
		panelSummaryHolder.setBackground(Color.WHITE);
		panelSummaryHolder.setLayout(new MigLayout("", "[grow]", "[grow][][]"));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelSummaryHolder.add(splitPane, "cell 0 0,grow");
		
		JScrollPane scrollPaneMeasureContainer = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneMeasureContainer);
		
		JPanel panelMeasureContainer = new JPanel();
		panelMeasureContainer.setBackground(Color.WHITE);
		scrollPaneMeasureContainer.setViewportView(panelMeasureContainer);
		panelMeasureContainer.setLayout(new MigLayout("", "[grow]", "[]"));
		
		panelMeasureHolder = new JPanel();
		panelMeasureHolder.setBackground(Color.WHITE);
		panelMeasureContainer.add(panelMeasureHolder, "cell 0 0,grow");
		panelMeasureHolder.setLayout(new BoxLayout(panelMeasureHolder, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPaneLevelContainer = new JScrollPane();
		splitPane.setRightComponent(scrollPaneLevelContainer);
		
		JPanel panelLevelContainer = new JPanel();
		panelLevelContainer.setBackground(Color.WHITE);
		scrollPaneLevelContainer.setViewportView(panelLevelContainer);
		panelLevelContainer.setLayout(new MigLayout("", "[grow]", "[]"));
		
		panelLevelHolder = new JPanel();
		panelLevelHolder.setBackground(Color.WHITE);
		panelLevelContainer.add(panelLevelHolder, "cell 0 0,grow");
		panelLevelHolder.setLayout(new BoxLayout(panelLevelHolder, BoxLayout.Y_AXIS));
		
		JButton btnGenerateOlapQuery = new JButton("Generate OLAP Query");
		btnGenerateOlapQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String datasetName = comboBoxDataset.getSelectedItem().toString().trim();

				if (methods.checkString(datasetName) && definition.getSelectedMeasureFunctionMap().size() > 0) {
					dialog = methods.getProgressDialog(getParent());

					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String queryString = definition.generateOlapQuery(datasetName, bannedLevels);
							textAreaQuery.setText(queryString);
							// definition.runSparqlQuery(queryString);
							tabbedPane.setSelectedIndex(1);
							dialog.dispose();
						}
					});
				} else {
					methods.showDialog("You must select a dataset, measure and functions");
				}
			}
		});
		btnGenerateOlapQuery.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSummaryHolder.add(btnGenerateOlapQuery, "cell 0 1,grow");
		
		JButton btnRunQuery = new JButton("Run Query");
		btnRunQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String datasetName = comboBoxDataset.getSelectedItem().toString().trim();

				if (methods.checkString(datasetName) && definition.getSelectedMeasureFunctionMap().size() > 0) {
					dialog = methods.getProgressDialog(getParent());

					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String queryString = definition.generateOlapQuery(datasetName, bannedLevels);
							textAreaQuery.setText(queryString);
							// tabbedPane.setSelectedIndex(1);

							Object[][] valueArray = definition.runSparqlQuery(queryString);

							dialog.dispose();

							JScrollPane scrollPaneTable = new JScrollPane();
							scrollPaneTable.setPreferredSize(new Dimension(1200, 600));

							JTable tableResult = new JTable(valueArray, definition.getSelectedColumns().toArray());
							tableResult.setFont(new Font("Tahoma", Font.PLAIN, 14));
							tableResult.setBackground(Color.WHITE);
							scrollPaneTable.setViewportView(tableResult);

							JOptionPane.showMessageDialog(null, scrollPaneTable, "OLAP Result",
									JOptionPane.INFORMATION_MESSAGE);
						}
					});
				} else {
					methods.showDialog("You must select a dataset, measure and functions");
				}
			}
		});
		btnRunQuery.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSummaryHolder.add(btnRunQuery, "cell 0 2,grow");

		JPanel panelResultHolder = new JPanel();
		tabbedPane.addTab("Result", null, panelResultHolder, null);
		panelResultHolder.setBackground(Color.WHITE);
		panelResultHolder.setLayout(new MigLayout("", "[grow]", "[grow][]"));

		JScrollPane scrollPaneQuery = new JScrollPane();
		panelResultHolder.add(scrollPaneQuery, "cell 0 0,grow");

		textAreaQuery = new JTextArea();
		textAreaQuery.setFont(new Font("Tahoma", Font.BOLD, 14));
		textAreaQuery.setBackground(Color.WHITE);
		scrollPaneQuery.setViewportView(textAreaQuery);
		
		JButton btnRunQuery_1 = new JButton("Run Query");
		btnRunQuery_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*Object[][] valueArray = definition.runSparqlQuery(textAreaQuery.getText().toString().trim());

				JScrollPane scrollPaneTable = new JScrollPane();
				scrollPaneTable.setPreferredSize(new Dimension(1200, 600));

				JTable tableResult = new JTable(valueArray, definition.getSelectedColumns().toArray());
				tableResult.setFont(new Font("Tahoma", Font.PLAIN, 14));
				tableResult.setBackground(Color.WHITE);
				scrollPaneTable.setViewportView(tableResult);

				JOptionPane.showMessageDialog(null, scrollPaneTable, "OLAP Result",
						JOptionPane.INFORMATION_MESSAGE);*/
				
				definition.runSparqlQuery(textAreaQuery.getText().toString().trim(), true);
			}
		});
		btnRunQuery_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelResultHolder.add(btnRunQuery_1, "cell 0 1,alignx center");
	}

	protected void clearAllSelection() {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Dimensions");
		treeDimension.setModel(new DefaultTreeModel(rootNode));

		DefaultMutableTreeNode rootNode2 = new DefaultMutableTreeNode("Measures");
		treeMeasure.setModel(new DefaultTreeModel(rootNode2));

		treeDimension.clearSelection();
		treeMeasure.clearSelection();
		lblCurrentLevelValue.setText("");
		comboBoxProperty.removeAllItems();

		DefaultListModel model = new DefaultListModel();
		listInstance.setModel(model);
		
		definition.resetOlapSelection();
		refreshSelectedLevels();
		refreshSelectedMeasure();

		textAreaQuery.setText("");
	}

	private void refreshSelectedMeasure() {
		// TODO Auto-generated method stub
		panelMeasureHolder.removeAll();

		for (String measureString : definition.getSelectedMeasureFunctionMap().keySet()) {
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panelMeasureHolder.add(panel);
			panel.setLayout(new MigLayout("", "[grow][]", "[][]"));

			JLabel lblSelectedLevel = new JLabel(measureString);
			lblSelectedLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(lblSelectedLevel, "cell 0 0,grow");

			JButton btnRemove = new JButton("Remove");
			btnRemove.setMargin(new Insets(10, 10, 10, 10));
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					definition.getSelectedMeasureFunctionMap().remove(measureString);
					refreshSelectedMeasure();

					DefaultListModel model = new DefaultListModel();
					listInstance.setModel(model);
				}
			});
			btnRemove.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(btnRemove, "cell 1 0 1 2,aligny center");

			String filterText = "<function>";

			ArrayList<String> functionList = definition.getSelectedMeasureFunctionMap().get(measureString);
			String functionString = methods.convertArrayListToString(functionList);
			if (methods.checkString(functionString)) {
				filterText = "Aggregated function(s): " + functionString;
			}
			JLabel lblFilterCondition = new JLabel(filterText);
			lblFilterCondition.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
			panel.add(lblFilterCondition, "cell 0 1,grow");
		}

		panelMeasureHolder.repaint();
		panelMeasureHolder.revalidate();
	}

	protected void copyInstances() {
		// TODO Auto-generated method stub
		if (isEnabled) {
			String selectedLevel = lblCurrentLevelValue.getText().toString().trim();
			String selectedProperty = comboBoxProperty.getSelectedItem().toString();
			String selectedCondition = comboBoxFilterCondition.getSelectedItem().toString();
			TwinValue twinValue = definition.getOlapConditionalHashMap().get(selectedCondition);
			selectedCondition = twinValue.getFirstValue();

			LinkedHashMap<String, SelectedLevelInstance> propertyMap = new LinkedHashMap();
			if (definition.getInstancesMap().containsKey(selectedLevel)) {
				propertyMap = definition.getInstancesMap().get(selectedLevel);
			}

			SelectedLevelInstance selectedLevelInstance = new SelectedLevelInstance(selectedCondition);

			ListModel<CheckListItem> listModel = listInstance.getModel();
			for (int i = 0; i < listModel.getSize(); i++) {
				CheckListItem checkListItem = listModel.getElementAt(i);
				
				if (checkListItem.isSelected()) {
					if (!selectedLevelInstance.containInstance(checkListItem.getValue())) {
						selectedLevelInstance.addInstance(checkListItem.getValue());
					}
				}
			}

			propertyMap.put(selectedProperty, selectedLevelInstance);
			definition.getInstancesMap().put(selectedLevel, propertyMap);

			updateFilterCondition();
		}
	}

	private void updateFilterCondition() {
		// TODO Auto-generated method stub

		String selectedLevel = lblCurrentLevelValue.getText().toString().trim();
		String filterConditionString = "";
		if (definition.getInstancesMap().containsKey(selectedLevel)) {
			LinkedHashMap<String, SelectedLevelInstance> propertyMap = definition.getInstancesMap().get(selectedLevel);

			for (String propertyString : propertyMap.keySet()) {
				SelectedLevelInstance selectedLevelInstance = propertyMap.get(propertyString);
				if (selectedLevelInstance.getInstances().size() > 0) {
					filterConditionString += propertyString + ": ";

					String instanceString = "";
					String condition = "";
					if (selectedLevelInstance.getFilterCondition() != "no") {
						condition = selectedLevelInstance.getFilterCondition();
					}

					for (int i = 0; i < selectedLevelInstance.getInstances().size(); i++) {
						String instance = selectedLevelInstance.getInstances().get(i).toString();

						instanceString += condition + " " + instance;
						instanceString = instanceString.trim();

						if (i < selectedLevelInstance.getInstances().size() - 1) {
							instanceString += ",";
						}
					}

					filterConditionString += instanceString + "\n";
				}
			}
		}

		for (int i = 0; i < definition.getSelectedLevelList().size(); i++) {
			SelectedLevel level = definition.getSelectedLevelList().get(i);

			if (level.match(selectedLevel)) {
				level.setFilterCondition(filterConditionString);
				definition.getSelectedLevelList().set(i, level);
				break;
			}
		}

		refreshSelectedLevels();
	}

	protected void refreshSelectedLevels() {
		// TODO Auto-generated method stub
		panelLevelHolder.removeAll();

		for (SelectedLevel selectedLevel : definition.getSelectedLevelList()) {
			JPanel panel = new JPanel();
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panel.setBackground(Color.WHITE);
			panelLevelHolder.add(panel);
			panel.setLayout(new MigLayout("", "[grow][]", "[][][]"));

			JLabel lblSelectedLevel = new JLabel(selectedLevel.getLevelName());
			lblSelectedLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(lblSelectedLevel, "cell 0 0,grow");

			JButton btnRemove = new JButton("Remove");
			btnRemove.setMargin(new Insets(10, 10, 10, 10));
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					definition.getSelectedLevelList().remove(selectedLevel);
					// definition.getFilterPropertyMap().remove(selectedLevel);
					definition.getInstancesMap().remove(selectedLevel.getLevelName());
					refreshSelectedLevels();

					DefaultListModel model = new DefaultListModel();
					listInstance.setModel(model);
				}
			});
			btnRemove.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(btnRemove, "cell 1 0 1 3,alignx center,aligny center");

			String filterText = "<filter condition>";

			if (methods.checkString(selectedLevel.getFilterCondition())) {
				filterText = "Filter Condition: " + selectedLevel.getFilterCondition();
			}
			
			JLabel lblFilterCondition = new JLabel(filterText);
			lblFilterCondition.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
			panel.add(lblFilterCondition, "cell 0 1,grow");
			
			JLabel lblPropertiesForSelection = new JLabel("Properties to be Viewed: ");
			lblPropertiesForSelection.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
			panel.add(lblPropertiesForSelection, "cell 0 2,grow");
			
			String value = "Properties to be Viewed: " + methods.convertArrayListToString(selectedLevel.getViewProperties());
			lblPropertiesForSelection.setText(value);
			
			
		}

		panelLevelHolder.repaint();
		panelLevelHolder.revalidate();
	}

	protected void refreshMeasureTree() {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Measures");

		for (String measureString : definition.getMeasureMap().keySet()) {
			DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode(measureString);

			ArrayList<String> functionList = definition.getMeasureMap().get(measureString);

			if (functionList.size() > 0) {
				DefaultMutableTreeNode functionRootNode = new DefaultMutableTreeNode("Aggregate Functions");

				for (String functionString : functionList) {
					DefaultMutableTreeNode functionNode = new DefaultMutableTreeNode(functionString);
					functionRootNode.add(functionNode);
				}

				measureNode.add(functionRootNode);
			}

			rootNode.add(measureNode);
		}

		treeMeasure.setModel(new DefaultTreeModel(rootNode));
	}

	protected void refreshDimensionTree() {
		// TODO Auto-generated method stub
		bannedLevels = new ArrayList<>();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Dimensions");

		for (String dimensionString : definition.getDimHierMap().keySet()) {
			DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode(dimensionString);

			ArrayList<String> hierList = definition.getDimHierMap().get(dimensionString);
			for (String hierarchyString : hierList) {
				DefaultMutableTreeNode hierarchyNode = new DefaultMutableTreeNode(hierarchyString);

				ArrayList<String> levelList = definition.getHierLevelMap().get(hierarchyString);

				boolean isBanned = false;
				for (String levelString : levelList) {
					DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode(levelString);
					hierarchyNode.add(levelNode);
					
					if (isBanned) {
						if (!bannedLevels.contains(levelString)) {
							bannedLevels.add(levelString);
						}
					}
					
	
					
					if (definition.getAllCubeLevels().contains(levelString)) {
						isBanned = true;
					}
				}

				dimensionNode.add(hierarchyNode);
			}

			rootNode.add(dimensionNode);
		}
		
		treeDimension.setCellRenderer(new MyDisabledTreeRenderer(bannedLevels));
		treeDimension.setModel(new DefaultTreeModel(rootNode));
	}
	
	private class EnabledListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isEnabled = true;
        }
    }

    private class DisabledListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JComboBox) e.getSource()).getSelectedIndex() != SELECTION_INTERVAL[0]) {
                JOptionPane.showMessageDialog(null,
                        "You can't Select that Item", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                isEnabled = false;
                comboBoxFilterCondition.setSelectedIndex(0);
            } else {
                isEnabled = true;
            }
        }
    }

    protected void disableItemsInComboBox() {
        comboBoxFilterCondition.removeActionListener(enabledListener);
        comboBoxFilterCondition.addActionListener(disabledListener);
        model.setSelectionInterval(SELECTION_INTERVAL[0], SELECTION_INTERVAL[0]);
    }

    protected void enableItemsInComboBox() {
    	comboBoxFilterCondition.removeActionListener(disabledListener);
    	comboBoxFilterCondition.addActionListener(enabledListener);
        model.setSelectionInterval(SELECTION_INTERVAL[0], comboBoxFilterCondition.getModel()
                .getSize() - 1);
    }

	private void initializeAll() {
		// TODO Auto-generated method stub
		methods = new Methods();
		definition = new Definition();
	}
}
