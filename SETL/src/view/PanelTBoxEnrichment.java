package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import controller.TBoxDefinition;
import helper.FileMethods;
import model.SelectedLevel;
import net.miginfocom.swing.MigLayout;
import queries.TBoxExtraction;

public class PanelTBoxEnrichment extends JPanel {
	private static final String TEMP_TBOX = "TEMP_TBOX.ttl";
	private static final long serialVersionUID = 1L;
	private JTree treeObject;
	private JTree treeClass;
	private JTree treeData;
	private JTree treeIRI;
	private JTree treeDataset;
	private JTree treeLevel;
	private JTree treeHierarchyStep;
	private JTree treeDimension;
	private JTree treeHierarchy;
	private JTree treeAttribute;
	private JTree treeMeasure;
	private JTree treeRollUp;
	private JTree treeDataStructure;
	private JTree treeOntology;
	JTree selectedTree = null;
	private FileMethods fileMethods;
	private TBoxDefinition tBoxDefinition;
	private TBoxExtraction tBoxExtraction;
	private JLabel lblFilePath;
	private JTextArea textAreaTBox;

	private String selectedNode = "";
	private JComboBox comboBoxViewFileType;
	private JPanel panelAnnotation;
	private JPanel panelDescription;
	private JPanel panelMultiDimension;

	String measure = "", dimension = "", dimCar = "", level = "", levelCar = "", prefix = "", iri = "";
	ArrayList<String> functions = new ArrayList<>();

	public PanelTBoxEnrichment() {
		setBackground(Color.WHITE);
		initializeAll();
		setBounds(0, 0, 1500, 1000);
		setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelButton = new JPanel();
		add(panelButton, "cell 0 0 3 1,grow");
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnOpenTbox = new JButton("Open TBox");
		btnOpenTbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openButtonHandler();
			}
		});
		btnOpenTbox.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelButton.add(btnOpenTbox);

		JButton btnNewTbox = new JButton("New TBox");
		btnNewTbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newButtonHandler();
			}
		});
		btnNewTbox.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelButton.add(btnNewTbox);

		JButton btnSaveTbox = new JButton("Save TBox");
		btnSaveTbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveButtonHandler();
			}
		});
		btnSaveTbox.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelButton.add(btnSaveTbox);

		JSplitPane splitPaneFirst = new JSplitPane();
		splitPaneFirst.setOneTouchExpandable(true);
		splitPaneFirst.setResizeWeight(0.3);
		add(splitPaneFirst, "cell 0 1,grow");

		JPanel panelTree = new JPanel();
		// add(panelTree, "cell 0 1,grow");
		splitPaneFirst.setLeftComponent(panelTree);
		panelTree.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTabbedPane tabbedPaneTree = new JTabbedPane(JTabbedPane.TOP);
		panelTree.add(tabbedPaneTree, "cell 0 0,grow");

		JTabbedPane tabbedPaneConcept = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Owl Construct", null, tabbedPaneConcept, null);

		JPanel panelClassTree = new JPanel();
		panelClassTree.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Concepts", null, panelClassTree, null);

		JPanel panelObjectTree = new JPanel();
		panelObjectTree.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Object Properties", null, panelObjectTree, null);

		JPanel panelDataTree = new JPanel();
		panelDataTree.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Datatype Properties", null, panelDataTree, null);

		JTabbedPane tabbedPaneStructure = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Data Warehouse Construct", null, tabbedPaneStructure, null);

		JPanel panelOntologyTree = new JPanel();
		panelOntologyTree.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Ontology", null, panelOntologyTree, null);

		JPanel panelDataStructureTree = new JPanel();
		panelDataStructureTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("DataStructures", null, panelDataStructureTree, null);

		JPanel panelDatasetTree = new JPanel();
		panelDatasetTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Datasets", null, panelDatasetTree, null);

		JPanel panelDimensionTree = new JPanel();
		panelDimensionTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Dimensions", null, panelDimensionTree, null);

		JPanel panelHierarchyTree = new JPanel();
		panelHierarchyTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Hierarchies", null, panelHierarchyTree, null);

		JPanel panelHierarchyStepTree = new JPanel();
		panelHierarchyStepTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Hierarchy Steps", null, panelHierarchyStepTree, null);

		JPanel panelLevelTree = new JPanel();
		panelLevelTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Level Properties", null, panelLevelTree, null);

		JPanel panelAttributeTree = new JPanel();
		panelAttributeTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Level Attributes", null, panelAttributeTree, null);

		JPanel panelRollUpTree = new JPanel();
		panelRollUpTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Roll-up Properties", null, panelRollUpTree, null);

		JPanel panelMeasureTree = new JPanel();
		panelMeasureTree.setBackground(Color.WHITE);
		tabbedPaneStructure.addTab("Measures", null, panelMeasureTree, null);

		JPanel panelIRITree = new JPanel();
		panelIRITree.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Prefixes", null, panelIRITree, null);

		panelClassTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonClass = new JPanel();
		panelButtonClass.setBackground(Color.WHITE);
		panelClassTree.add(panelButtonClass, "cell 0 0,grow");
		panelButtonClass.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddClass = new JButton("+");
		buttonAddClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
				 * treeClass.getLastSelectedPathComponent(); if (parent == null) {
				 * JOptionPane.showMessageDialog(null, "Select a parent to add a class",
				 * "Error", JOptionPane.ERROR_MESSAGE);
				 * 
				 * return; } String name = JOptionPane.showInputDialog(null,
				 * "Enter Class Name: "); if ((name.startsWith("http://") && !name.endsWith("/")
				 * && !name.endsWith("#") && name != null) || name.contains(":")) {
				 * DefaultTreeModel model = (DefaultTreeModel) treeClass.getModel();
				 * model.insertNodeInto(new DefaultMutableTreeNode(name), parent,
				 * parent.getChildCount());
				 * 
				 * addResource("class", name);
				 * 
				 * 
				 * } else { JOptionPane.showMessageDialog(null,
				 * "Name must start with http:// and must include name at the end", "Error",
				 * JOptionPane.ERROR_MESSAGE); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addClassResource();
				}
			}
		});
		buttonAddClass.setBackground(Color.WHITE);
		panelButtonClass.add(buttonAddClass);

		JButton buttonRemoveClass = new JButton("-");
		buttonRemoveClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeClass.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeClass.getLastSelectedPathComponent();
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

		JLabel lblClassHierarchy = new JLabel("Class Explorer");
		panelClassTree.add(lblClassHierarchy, "cell 0 1");

		JScrollPane scrollPaneClass = new JScrollPane();
		panelClassTree.add(scrollPaneClass, "cell 0 2,grow");

		treeClass = new JTree();
		treeClass.setEditable(true);
		treeClass.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeClass.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeClass.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeClass.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeClass.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererClass = (DefaultTreeCellRenderer) treeClass.getCellRenderer();
		rendererClass.setLeafIcon(null);
		rendererClass.setClosedIcon(null);
		rendererClass.setOpenIcon(null);

		setEditatbleProperties(treeClass, rendererClass);

		scrollPaneClass.setViewportView(treeClass);

		panelObjectTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonObject = new JPanel();
		panelButtonObject.setBackground(Color.WHITE);
		panelObjectTree.add(panelButtonObject, "cell 0 0,grow");
		panelButtonObject.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddObject = new JButton("+");
		buttonAddObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
				 * treeObject.getLastSelectedPathComponent(); if (parent == null) {
				 * JOptionPane.showMessageDialog(null,
				 * "Select a parent to add an object property", "Error",
				 * JOptionPane.ERROR_MESSAGE);
				 * 
				 * return; } String name = JOptionPane.showInputDialog(null,
				 * "Enter Property Name: "); if ((name.startsWith("http://") &&
				 * !name.endsWith("/") && !name.endsWith("#") && name != null) ||
				 * name.contains(":")) { DefaultTreeModel model = (DefaultTreeModel)
				 * treeObject.getModel(); model.insertNodeInto(new DefaultMutableTreeNode(name),
				 * parent, parent.getChildCount());
				 * 
				 * addResource("object", name); } else { JOptionPane.showMessageDialog(null,
				 * "Name must start with http:// and must include name at the end", "Error",
				 * JOptionPane.ERROR_MESSAGE); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addObjectProperty();
				}
			}
		});
		buttonAddObject.setBackground(Color.WHITE);
		panelButtonObject.add(buttonAddObject);

		JButton buttonRemoveObject = new JButton("-");
		buttonRemoveObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeObject.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeObject
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveObject.setBackground(Color.WHITE);
		panelButtonObject.add(buttonRemoveObject);

		JLabel lblObjectPropertyHierarchy = new JLabel("Object Property Explorer");
		panelObjectTree.add(lblObjectPropertyHierarchy, "cell 0 1");

		JScrollPane scrollPaneObject = new JScrollPane();
		panelObjectTree.add(scrollPaneObject, "cell 0 2,grow");

		treeObject = new JTree();
		treeObject.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeObject
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeObject.setEditable(true);
		treeObject.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeObject.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererObject = (DefaultTreeCellRenderer) treeObject.getCellRenderer();
		rendererObject.setLeafIcon(null);
		rendererObject.setClosedIcon(null);
		rendererObject.setOpenIcon(null);

		setEditatbleProperties(treeObject, rendererObject);

		scrollPaneObject.setViewportView(treeObject);

		panelDataTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonData = new JPanel();
		panelButtonData.setBackground(Color.WHITE);
		panelDataTree.add(panelButtonData, "cell 0 0,grow");
		panelButtonData.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddData = new JButton("+");
		buttonAddData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
				 * treeData.getLastSelectedPathComponent(); if (parent == null) {
				 * JOptionPane.showMessageDialog(null,
				 * "Select a parent to add a datatype property", "Error",
				 * JOptionPane.ERROR_MESSAGE);
				 * 
				 * return; } String name = JOptionPane.showInputDialog(null,
				 * "Enter Property Name: "); if ((name.startsWith("http://") &&
				 * !name.endsWith("/") && !name.endsWith("#") && name != null) ||
				 * name.contains(":")) { DefaultTreeModel model = (DefaultTreeModel)
				 * treeData.getModel(); model.insertNodeInto(new DefaultMutableTreeNode(name),
				 * parent, parent.getChildCount());
				 * 
				 * addResource("data", name); } else { JOptionPane.showMessageDialog(null,
				 * "Name must start with http:// and must include name at the end", "Error",
				 * JOptionPane.ERROR_MESSAGE); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addDatatypeProperty();
				}
			}
		});
		buttonAddData.setBackground(Color.WHITE);
		panelButtonData.add(buttonAddData);

		JButton buttonRemoveData = new JButton("-");
		buttonRemoveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeData.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeData.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveData.setBackground(Color.WHITE);
		panelButtonData.add(buttonRemoveData);

		JLabel lblDatatypePropertyHierarchy = new JLabel("Datatype Property Explorer");
		panelDataTree.add(lblDatatypePropertyHierarchy, "cell 0 1");

		JScrollPane scrollPaneData = new JScrollPane();
		panelDataTree.add(scrollPaneData, "cell 0 2,grow");

		treeData = new JTree();
		treeData.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeData.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println(e1.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeData.setEditable(true);
		treeData.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeData.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererData = (DefaultTreeCellRenderer) treeData.getCellRenderer();
		rendererData.setLeafIcon(null);
		rendererData.setClosedIcon(null);
		rendererData.setOpenIcon(null);

		setEditatbleProperties(treeData, rendererData);

		scrollPaneObject.setViewportView(treeObject);

		scrollPaneData.setViewportView(treeData);

		panelIRITree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonIRI = new JPanel();
		panelButtonIRI.setBackground(Color.WHITE);
		panelIRITree.add(panelButtonIRI, "cell 0 0,grow");
		panelButtonIRI.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddIRI = new JButton("+");
		buttonAddIRI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addIRIHandler();
			}
		});
		buttonAddIRI.setBackground(Color.WHITE);
		panelButtonIRI.add(buttonAddIRI);

		JButton buttonRemoveIRI = new JButton("-");
		buttonRemoveIRI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeIRI.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();

					/*
					 * fileMethods.writeText(TEMP_TBOX, textAreaTBox.getText().toString());
					 * tBoxExtraction = new TBoxExtraction(TEMP_TBOX); initializeDefinition();
					 * reloadAll();
					 */

					String modelText = tBoxExtraction.getModelText("Turtle");

					String regEx = "(@prefix)(\\s+)(" + selectedResource + ")(\\s+)(<)("
							+ tBoxDefinition.getPrefixMap().get(selectedResource) + ")(>\\s*\\.)";
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(modelText);

					while (matcher.find()) {
						String prefix = matcher.group(0).trim();
						modelText = modelText.replace(prefix, "");
					}

					if (modelText.contains(selectedResource)) {
						int confirm = JOptionPane.showConfirmDialog(null,
								"There are occurrances of this prefix. Are you sure to remove");
						if (confirm == JOptionPane.OK_OPTION) {
							fileMethods.writeText(TEMP_TBOX, modelText);
							tBoxExtraction = new TBoxExtraction(TEMP_TBOX);
							initializeDefinition();
							reloadAll();
						}
					} else {
						fileMethods.writeText(TEMP_TBOX, modelText);
						tBoxExtraction = new TBoxExtraction(TEMP_TBOX);
						initializeDefinition();
						reloadAll();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Select something from the tree");
				}
			}
		});
		buttonRemoveIRI.setBackground(Color.WHITE);
		panelButtonIRI.add(buttonRemoveIRI);

		JLabel lblIri = new JLabel("Prefix");
		panelIRITree.add(lblIri, "cell 0 1");

		JScrollPane scrollPaneIRI = new JScrollPane();
		panelIRITree.add(scrollPaneIRI, "cell 0 2,grow");

		treeIRI = new JTree();
		treeIRI.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeIRI.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					editIRIHandler(selectedResource);
				}
			}
		});
		treeIRI.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeIRI.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererIRI = (DefaultTreeCellRenderer) treeIRI.getCellRenderer();
		rendererIRI.setLeafIcon(null);
		rendererIRI.setClosedIcon(null);
		rendererIRI.setOpenIcon(null);
		scrollPaneIRI.setViewportView(treeIRI);

		panelDataStructureTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonDataStructure = new JPanel();
		panelButtonDataStructure.setBackground(Color.WHITE);
		panelDataStructureTree.add(panelButtonDataStructure, "cell 0 0,grow");
		panelButtonDataStructure.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddDataStructure = new JButton("+");
		buttonAddDataStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				/*
				 * String name = addNodeToTree(treeDataStructure, "DataStructure");
				 * System.out.println(name); if (name != null && !name.equals("")) {
				 * addResource("dataStructure", name); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					if (treeDataStructure.getLastSelectedPathComponent() != null) {
						String last = treeDataStructure.getLastSelectedPathComponent().toString();
						System.out.println(last);
						String name = treeDataStructure.getSelectionPath().toString();
						System.out.println(name);

						if (name.contains("Cube")) {
							if (name.contains(":") && !name.contains("_:")) {
								addComponent("Cube", last);
							} else if (last.equals("Cube")) {
								addCube();
							}

						} else {
							if (name.contains(":") && !name.contains("_:")) {
								addComponent("Cuboid", last);
							} else if (last.equals("Cuboid")) {
								addCuboid();
							}

						}
					}
				}
			}
		});
		buttonAddDataStructure.setBackground(Color.WHITE);
		panelButtonDataStructure.add(buttonAddDataStructure);

		JButton buttonRemoveDataStructure = new JButton("-");
		buttonRemoveDataStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeDataStructure.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDataStructure
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a DataStructure property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveDataStructure.setBackground(Color.WHITE);
		panelButtonDataStructure.add(buttonRemoveDataStructure);

		JLabel lblDataStructure = new JLabel("DataStructure Explorer");
		panelDataStructureTree.add(lblDataStructure, "cell 0 1");

		JScrollPane scrollPaneDataStructure = new JScrollPane();
		panelDataStructureTree.add(scrollPaneDataStructure, "cell 0 2,grow");

		treeDataStructure = new JTree();
		treeDataStructure.setEditable(true);
		treeDataStructure.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDataStructure
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						if (getSelectedNode().contains("_:")) {
							String resource = "";
							if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							updateListValues(resource);
						} else {
							updateListValues();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeDataStructure.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeDataStructure.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererDataStructure = (DefaultTreeCellRenderer) treeDataStructure.getCellRenderer();
		rendererDataStructure.setLeafIcon(null);
		rendererDataStructure.setClosedIcon(null);
		rendererDataStructure.setOpenIcon(null);

		setEditatbleProperties(treeDataStructure, rendererDataStructure);

		scrollPaneDataStructure.setViewportView(treeDataStructure);

		panelHierarchyStepTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonHierarchyStep = new JPanel();
		panelButtonHierarchyStep.setBackground(Color.WHITE);
		panelHierarchyStepTree.add(panelButtonHierarchyStep, "cell 0 0,grow");
		panelButtonHierarchyStep.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddHierarchyStep = new JButton("+");
		buttonAddHierarchyStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addHierarchyStepProperty();
				}
			}
		});
		buttonAddHierarchyStep.setBackground(Color.WHITE);
		panelButtonHierarchyStep.add(buttonAddHierarchyStep);

		JButton buttonRemoveHierarchyStep = new JButton("-");
		buttonRemoveHierarchyStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeHierarchyStep.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeHierarchyStep
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					String selectedResource = selectedNode.toString();

					tBoxExtraction.getHierarchyStepsList().remove(selectedResource);

					fileMethods.writeText(TEMP_TBOX, tBoxExtraction.printAllComponents());

					tBoxExtraction = new TBoxExtraction(TEMP_TBOX);
					initializeDefinition();

				} else {
					JOptionPane.showMessageDialog(null, "Select a HierarchyStep property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveHierarchyStep.setBackground(Color.WHITE);
		panelButtonHierarchyStep.add(buttonRemoveHierarchyStep);

		JLabel lblHierarchyStep = new JLabel("HierarchyStep Explorer");
		panelHierarchyStepTree.add(lblHierarchyStep, "cell 0 1");

		JScrollPane scrollPaneHierarchyStep = new JScrollPane();
		panelHierarchyStepTree.add(scrollPaneHierarchyStep, "cell 0 2,grow");

		treeHierarchyStep = new JTree();
		treeHierarchyStep.setEditable(true);
		treeHierarchyStep.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeHierarchyStep
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						String resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
						updateListValues(resource);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeHierarchyStep.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeHierarchyStep.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererHierarchyStep = (DefaultTreeCellRenderer) treeHierarchyStep.getCellRenderer();
		rendererHierarchyStep.setLeafIcon(null);
		rendererHierarchyStep.setClosedIcon(null);
		rendererHierarchyStep.setOpenIcon(null);

		setEditatbleProperties(treeHierarchyStep, rendererHierarchyStep);

		scrollPaneHierarchyStep.setViewportView(treeHierarchyStep);

		panelOntologyTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonOntology = new JPanel();
		panelButtonOntology.setBackground(Color.WHITE);
		panelOntologyTree.add(panelButtonOntology, "cell 0 0,grow");
		panelButtonOntology.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddOntology = new JButton("+");
		buttonAddOntology.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addOntologyProperty();
				}
			}
		});
		buttonAddOntology.setBackground(Color.WHITE);
		panelButtonOntology.add(buttonAddOntology);

		JButton buttonRemoveOntology = new JButton("-");
		buttonRemoveOntology.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeOntology.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeOntology
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Ontology property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveOntology.setBackground(Color.WHITE);
		panelButtonOntology.add(buttonRemoveOntology);

		JLabel lblOntology = new JLabel("Ontology Explorer");
		panelOntologyTree.add(lblOntology, "cell 0 1");

		JScrollPane scrollPaneOntology = new JScrollPane();
		panelOntologyTree.add(scrollPaneOntology, "cell 0 2,grow");

		treeOntology = new JTree();
		treeOntology.setEditable(true);
		treeOntology.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeOntology
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeOntology.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeOntology.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererOntology = (DefaultTreeCellRenderer) treeOntology.getCellRenderer();
		rendererOntology.setLeafIcon(null);
		rendererOntology.setClosedIcon(null);
		rendererOntology.setOpenIcon(null);

		setEditatbleProperties(treeOntology, rendererOntology);

		scrollPaneOntology.setViewportView(treeOntology);

		panelLevelTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonLevel = new JPanel();
		panelButtonLevel.setBackground(Color.WHITE);
		panelLevelTree.add(panelButtonLevel, "cell 0 0,grow");
		panelButtonLevel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddLevel = new JButton("+");
		buttonAddLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addLevelProperty();
				}
			}
		});
		buttonAddLevel.setBackground(Color.WHITE);
		panelButtonLevel.add(buttonAddLevel);

		JButton buttonRemoveLevel = new JButton("-");
		buttonRemoveLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeLevel.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeLevel.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Level property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveLevel.setBackground(Color.WHITE);
		panelButtonLevel.add(buttonRemoveLevel);

		JLabel lblLevel = new JLabel("Level Explorer");
		panelLevelTree.add(lblLevel, "cell 0 1");

		JScrollPane scrollPaneLevel = new JScrollPane();
		panelLevelTree.add(scrollPaneLevel, "cell 0 2,grow");

		treeLevel = new JTree();
		treeLevel.setEditable(true);
		treeLevel.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeLevel.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeLevel.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeLevel.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererLevel = (DefaultTreeCellRenderer) treeLevel.getCellRenderer();
		rendererLevel.setLeafIcon(null);
		rendererLevel.setClosedIcon(null);
		rendererLevel.setOpenIcon(null);

		setEditatbleProperties(treeLevel, rendererLevel);

		scrollPaneLevel.setViewportView(treeLevel);

		panelAttributeTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonAttribute = new JPanel();
		panelButtonAttribute.setBackground(Color.WHITE);
		panelAttributeTree.add(panelButtonAttribute, "cell 0 0,grow");
		panelButtonAttribute.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddAttribute = new JButton("+");
		buttonAddAttribute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeAttribute, "Attribute"); if (name != null &&
				 * !name.equals("")) { addResource("Attribute", name); }
				 */
				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addLevelAttribute();
				}

			}
		});
		buttonAddAttribute.setBackground(Color.WHITE);
		panelButtonAttribute.add(buttonAddAttribute);

		JButton buttonRemoveAttribute = new JButton("-");
		buttonRemoveAttribute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeAttribute.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeAttribute
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Attribute property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveAttribute.setBackground(Color.WHITE);
		panelButtonAttribute.add(buttonRemoveAttribute);

		JLabel lblAttribute = new JLabel("Attribute Explorer");
		panelAttributeTree.add(lblAttribute, "cell 0 1");

		JScrollPane scrollPaneAttribute = new JScrollPane();
		panelAttributeTree.add(scrollPaneAttribute, "cell 0 2,grow");

		treeAttribute = new JTree();
		treeAttribute.setEditable(true);
		treeAttribute.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeAttribute
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeAttribute.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeAttribute.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererAttribute = (DefaultTreeCellRenderer) treeAttribute.getCellRenderer();
		rendererAttribute.setLeafIcon(null);
		rendererAttribute.setClosedIcon(null);
		rendererAttribute.setOpenIcon(null);

		setEditatbleProperties(treeAttribute, rendererAttribute);

		scrollPaneAttribute.setViewportView(treeAttribute);

		panelRollUpTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonRollUp = new JPanel();
		panelButtonRollUp.setBackground(Color.WHITE);
		panelRollUpTree.add(panelButtonRollUp, "cell 0 0,grow");
		panelButtonRollUp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddRollUp = new JButton("+");
		buttonAddRollUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeRollUp, "Roll-up property"); if (name != null
				 * && !name.equals("")) { addResource("rollUp", name); }
				 */
				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addRollUpProperty();
				}

			}
		});
		buttonAddRollUp.setBackground(Color.WHITE);
		panelButtonRollUp.add(buttonAddRollUp);

		JButton buttonRemoveRollUp = new JButton("-");
		buttonRemoveRollUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeRollUp.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeRollUp
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Roll-up property to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveRollUp.setBackground(Color.WHITE);
		panelButtonRollUp.add(buttonRemoveRollUp);

		JLabel lblRollUp = new JLabel("Roll-up Explorer");
		panelRollUpTree.add(lblRollUp, "cell 0 1");

		JScrollPane scrollPaneRollUp = new JScrollPane();
		panelRollUpTree.add(scrollPaneRollUp, "cell 0 2,grow");

		treeRollUp = new JTree();
		treeRollUp.setEditable(true);
		treeRollUp.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeRollUp
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeRollUp.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeRollUp.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererRollUp = (DefaultTreeCellRenderer) treeRollUp.getCellRenderer();
		rendererRollUp.setLeafIcon(null);
		rendererRollUp.setClosedIcon(null);
		rendererRollUp.setOpenIcon(null);

		setEditatbleProperties(treeRollUp, rendererRollUp);

		scrollPaneRollUp.setViewportView(treeRollUp);

		panelDimensionTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonDimension = new JPanel();
		panelButtonDimension.setBackground(Color.WHITE);
		panelDimensionTree.add(panelButtonDimension, "cell 0 0,grow");
		panelButtonDimension.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddDimension = new JButton("+");
		buttonAddDimension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeDimension, "Dimension"); if (name != null &&
				 * !name.equals("")) { addResource("Dimension", name); }
				 */
				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addDimension();
				}
			}
		});
		buttonAddDimension.setBackground(Color.WHITE);
		panelButtonDimension.add(buttonAddDimension);

		JButton buttonRemoveDimension = new JButton("-");
		buttonRemoveDimension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeDimension.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDimension
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Dimension to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveDimension.setBackground(Color.WHITE);
		panelButtonDimension.add(buttonRemoveDimension);

		JLabel lblDimension = new JLabel("Dimension Explorer");
		panelDimensionTree.add(lblDimension, "cell 0 1");

		JScrollPane scrollPaneDimension = new JScrollPane();
		panelDimensionTree.add(scrollPaneDimension, "cell 0 2,grow");

		treeDimension = new JTree();
		treeDimension.setEditable(true);
		treeDimension.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDimension
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeDimension.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeDimension.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererDimension = (DefaultTreeCellRenderer) treeDimension.getCellRenderer();
		rendererDimension.setLeafIcon(null);
		rendererDimension.setClosedIcon(null);
		rendererDimension.setOpenIcon(null);

		setEditatbleProperties(treeDimension, rendererDimension);

		scrollPaneDimension.setViewportView(treeDimension);

		panelHierarchyTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonHierarchy = new JPanel();
		panelButtonHierarchy.setBackground(Color.WHITE);
		panelHierarchyTree.add(panelButtonHierarchy, "cell 0 0,grow");
		panelButtonHierarchy.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddHierarchy = new JButton("+");
		buttonAddHierarchy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeHierarchy, "Explorer"); if (name != null &&
				 * !name.equals("")) { addResource("Hierarchies", name); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addHierarchy();
				}
			}
		});
		buttonAddHierarchy.setBackground(Color.WHITE);
		panelButtonHierarchy.add(buttonAddHierarchy);

		JButton buttonRemoveHierarchy = new JButton("-");
		buttonRemoveHierarchy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeHierarchy.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeHierarchy
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Hierarchy to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveHierarchy.setBackground(Color.WHITE);
		panelButtonHierarchy.add(buttonRemoveHierarchy);

		JLabel lblHierarchy = new JLabel("Explorer");
		panelHierarchyTree.add(lblHierarchy, "cell 0 1");

		JScrollPane scrollPaneHierarchy = new JScrollPane();
		panelHierarchyTree.add(scrollPaneHierarchy, "cell 0 2,grow");

		treeHierarchy = new JTree();
		treeHierarchy.setEditable(true);
		treeHierarchy.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeHierarchy
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeHierarchy.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeHierarchy.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererHierarchy = (DefaultTreeCellRenderer) treeHierarchy.getCellRenderer();
		rendererHierarchy.setLeafIcon(null);
		rendererHierarchy.setClosedIcon(null);
		rendererHierarchy.setOpenIcon(null);

		setEditatbleProperties(treeHierarchy, rendererHierarchy);

		scrollPaneHierarchy.setViewportView(treeHierarchy);

		panelMeasureTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonMeasure = new JPanel();
		panelButtonMeasure.setBackground(Color.WHITE);
		panelMeasureTree.add(panelButtonMeasure, "cell 0 0,grow");
		panelButtonMeasure.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddMeasure = new JButton("+");
		buttonAddMeasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeMeasure, "Measure"); if (name != null &&
				 * !name.equals("")) { addResource("Measure", name); }
				 */
				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addMeaure();
				}

			}
		});
		buttonAddMeasure.setBackground(Color.WHITE);
		panelButtonMeasure.add(buttonAddMeasure);

		JButton buttonRemoveMeasure = new JButton("-");
		buttonRemoveMeasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeMeasure.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMeasure
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Measure to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveMeasure.setBackground(Color.WHITE);
		panelButtonMeasure.add(buttonRemoveMeasure);

		JLabel lblMeasure = new JLabel("Measure Explorer");
		panelMeasureTree.add(lblMeasure, "cell 0 1");

		JScrollPane scrollPaneMeasure = new JScrollPane();
		panelMeasureTree.add(scrollPaneMeasure, "cell 0 2,grow");

		treeMeasure = new JTree();
		treeMeasure.setEditable(true);
		treeMeasure.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeMeasure
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeMeasure.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeMeasure.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererMeasure = (DefaultTreeCellRenderer) treeMeasure.getCellRenderer();
		rendererMeasure.setLeafIcon(null);
		rendererMeasure.setClosedIcon(null);
		rendererMeasure.setOpenIcon(null);

		setEditatbleProperties(treeMeasure, rendererMeasure);

		scrollPaneMeasure.setViewportView(treeMeasure);

		panelDatasetTree.setLayout(new MigLayout("", "[grow]", "[][][grow]"));

		JPanel panelButtonDataset = new JPanel();
		panelButtonDataset.setBackground(Color.WHITE);
		panelDatasetTree.add(panelButtonDataset, "cell 0 0,grow");
		panelButtonDataset.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddDataset = new JButton("+");
		buttonAddDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String name = addNodeToTree(treeDataset, "Dataset"); if (name != null &&
				 * !name.equals("")) { addResource("Dataset", name); }
				 */

				if (tBoxExtraction == null) {
					JOptionPane.showMessageDialog(null, "Please create/load a tbox first");
				} else {
					addDataset();
					reloadAll();
				}
			}
		});
		buttonAddDataset.setBackground(Color.WHITE);
		panelButtonDataset.add(buttonAddDataset);

		JButton buttonRemoveDataset = new JButton("-");
		buttonRemoveDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) treeDataset.getModel();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDataset
						.getLastSelectedPathComponent();
				if (selectedNode != null) {
					model.removeNodeFromParent(selectedNode);
					removeResource(selectedNode.toString());
				} else {
					JOptionPane.showMessageDialog(null, "Select a Dataset to remove", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonRemoveDataset.setBackground(Color.WHITE);
		panelButtonDataset.add(buttonRemoveDataset);

		JLabel lblDataset = new JLabel("Dataset Explorer");
		panelDatasetTree.add(lblDataset, "cell 0 1");

		JScrollPane scrollPaneDataset = new JScrollPane();
		panelDatasetTree.add(scrollPaneDataset, "cell 0 2,grow");

		treeDataset = new JTree();
		treeDataset.setEditable(true);
		treeDataset.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeDataset
						.getLastSelectedPathComponent();
				String selectedResource = "";
				if (selectedNode != null) {
					selectedResource = selectedNode.toString();
					setSelectedNode(selectedResource);
					try {
						updateListValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

					refreshLists();
				}
			}
		});
		treeDataset.setBorder(new EmptyBorder(5, 5, 5, 5));
		treeDataset.setFont(new Font("Tahoma", Font.BOLD, 11));
		DefaultTreeCellRenderer rendererDataset = (DefaultTreeCellRenderer) treeDataset.getCellRenderer();
		rendererDataset.setLeafIcon(null);
		rendererDataset.setClosedIcon(null);
		rendererDataset.setOpenIcon(null);

		setEditatbleProperties(treeDataset, rendererDataset);

		scrollPaneDataset.setViewportView(treeDataset);

		JSplitPane splitPaneSecond = new JSplitPane();
		splitPaneSecond.setOneTouchExpandable(true);
		splitPaneSecond.setResizeWeight(0.5);
		// add(splitPaneSecond, "cell 1 1,grow");
		splitPaneFirst.setRightComponent(splitPaneSecond);

		JPanel panelView = new JPanel();
		// add(panelView, "cell 1 1,grow");
		splitPaneSecond.setLeftComponent(panelView);
		panelView.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTabbedPane tabbedPaneView = new JTabbedPane(JTabbedPane.TOP);
		panelView.add(tabbedPaneView, "cell 0 0,grow");

		JPanel panelText = new JPanel();
		tabbedPaneView.addTab("Text", null, panelText, null);
		panelText.setBackground(Color.WHITE);
		panelText.setLayout(new MigLayout("", "[grow][][grow]", "[][grow]"));

		lblFilePath = new JLabel("File Path:");
		lblFilePath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelText.add(lblFilePath, "cell 0 0,growx");

		JLabel lblViewAs = new JLabel("View as: ");
		lblViewAs.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelText.add(lblViewAs, "cell 1 0,alignx trailing");

		comboBoxViewFileType = new JComboBox(fileMethods.getAllFileTypes().keySet().toArray());
		comboBoxViewFileType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String key = (String) comboBoxViewFileType.getSelectedItem();

				refreshAll();
			}
		});
		comboBoxViewFileType.setBackground(Color.WHITE);
		comboBoxViewFileType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelText.add(comboBoxViewFileType, "cell 2 0,growx");

		JScrollPane scrollPaneText = new JScrollPane();
		panelText.add(scrollPaneText, "cell 0 1 3 1,grow");

		textAreaTBox = new JTextArea();
		textAreaTBox.setEditable(false);
		scrollPaneText.setViewportView(textAreaTBox);

		JPanel panelGraph = new JPanel();
		tabbedPaneView.addTab("Graph", null, panelGraph, null);

		JPanel panelUpdate = new JPanel();
		panelUpdate.setBackground(Color.WHITE);
		// add(panelUpdate, "cell 2 1,grow");
		splitPaneSecond.setRightComponent(panelUpdate);
		panelUpdate.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPaneOne = new JSplitPane();
		splitPaneOne.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneOne.setOneTouchExpandable(true);
		splitPaneOne.setResizeWeight(0.3);
		panelUpdate.add(splitPaneOne, "cell 0 0,grow");

		JPanel panelAnnotationHolder = new JPanel();
		splitPaneOne.setLeftComponent(panelAnnotationHolder);
		panelAnnotationHolder.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)),
				"Annotation Properties", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(51, 51, 51)));
		panelAnnotationHolder.setBackground(Color.WHITE);
		panelAnnotationHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelAnnButton = new JPanel();
		panelAnnButton.setBackground(Color.WHITE);
		panelAnnotationHolder.add(panelAnnButton, "cell 0 0,grow");
		panelAnnButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddAnn = new JButton("+");
		buttonAddAnn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addAnnButtonHandler();
			}
		});
		buttonAddAnn.setBackground(Color.WHITE);
		panelAnnButton.add(buttonAddAnn);

		JButton buttonRemoveAnn = new JButton("-");
		buttonRemoveAnn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeAnnButtonHandler();
			}
		});
		buttonRemoveAnn.setBackground(Color.WHITE);
		panelAnnButton.add(buttonRemoveAnn);

		JScrollPane scrollPaneAnn = new JScrollPane();
		panelAnnotationHolder.add(scrollPaneAnn, "cell 0 1,grow");

		panelAnnotation = new JPanel();
		scrollPaneAnn.setViewportView(panelAnnotation);
		panelAnnotation.setBackground(Color.WHITE);
		panelAnnotation.setLayout(new MigLayout("", "[]", "[]"));

		JSplitPane splitPaneTwo = new JSplitPane();
		splitPaneOne.setRightComponent(splitPaneTwo);
		splitPaneTwo.setOneTouchExpandable(true);
		splitPaneTwo.setResizeWeight(0.5);
		splitPaneTwo.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel panelDescriptionHolder = new JPanel();
		splitPaneTwo.setLeftComponent(panelDescriptionHolder);
		panelDescriptionHolder.setBorder(new TitledBorder(null, "Description Properties", TitledBorder.LEADING,
				TitledBorder.ABOVE_TOP, null, null));
		panelDescriptionHolder.setBackground(Color.WHITE);
		panelDescriptionHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelDesButton = new JPanel();
		panelDesButton.setBackground(Color.WHITE);
		panelDescriptionHolder.add(panelDesButton, "cell 0 0,grow");
		panelDesButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddDes = new JButton("+");
		buttonAddDes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addDesButtonHandler();
			}
		});
		buttonAddDes.setBackground(Color.WHITE);
		panelDesButton.add(buttonAddDes);

		JButton buttonRemoveDes = new JButton("-");
		buttonRemoveDes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeDesButtonHandler();
			}
		});
		buttonRemoveDes.setBackground(Color.WHITE);
		panelDesButton.add(buttonRemoveDes);

		JScrollPane scrollPaneDes = new JScrollPane();
		panelDescriptionHolder.add(scrollPaneDes, "cell 0 1,grow");

		panelDescription = new JPanel();
		scrollPaneDes.setViewportView(panelDescription);
		panelDescription.setBackground(Color.WHITE);
		panelDescription.setLayout(new MigLayout("", "[]", "[]"));

		JPanel panelMultiDimensionHolder = new JPanel();
		splitPaneTwo.setRightComponent(panelMultiDimensionHolder);
		panelMultiDimensionHolder.setBorder(new TitledBorder(null, "Multi-Dimensional Properties", TitledBorder.LEADING,
				TitledBorder.ABOVE_TOP, null, null));
		panelMultiDimensionHolder.setBackground(Color.WHITE);
		panelMultiDimensionHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JPanel panelMDButton = new JPanel();
		panelMDButton.setBackground(Color.WHITE);
		panelMultiDimensionHolder.add(panelMDButton, "cell 0 0,grow");
		panelMDButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton buttonAddMD = new JButton("+");
		buttonAddMD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addMDButtonHandler();
			}
		});
		buttonAddMD.setBackground(Color.WHITE);
		panelMDButton.add(buttonAddMD);

		JButton buttonRemoveMD = new JButton("-");
		buttonRemoveMD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeMDButtonHandler();
			}
		});
		buttonRemoveMD.setBackground(Color.WHITE);
		panelMDButton.add(buttonRemoveMD);

		JScrollPane scrollPaneMD = new JScrollPane();
		panelMultiDimensionHolder.add(scrollPaneMD, "cell 0 1,grow");

		panelMultiDimension = new JPanel();
		scrollPaneMD.setViewportView(panelMultiDimension);
		panelMultiDimension.setBackground(Color.WHITE);
		panelMultiDimension.setLayout(new MigLayout("", "[]", "[]"));

		instantiateAllTree();
	}

	protected void addDatatypeProperty() {
		// TODO Auto-generated method stub
		JPanel panelAddProperty = new JPanel();
		panelAddProperty.setLayout(new MigLayout("", "[600px, center, grow]", "[][][][][][]"));
		panelAddProperty.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblIri, "split 2");

		JComboBox comboBoxIRI = new JComboBox();
		comboBoxIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddProperty.add(comboBoxIRI, "growx, pushx, wrap");

		JLabel lblPropertyName = new JLabel("Property Name: ");
		lblPropertyName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblPropertyName, "split 2");

		JTextField textFieldPropertyName = new JTextField();
		textFieldPropertyName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddProperty.add(textFieldPropertyName, "growx, pushx, wrap");
		textFieldPropertyName.setColumns(10);

		JLabel lblDomain = new JLabel("Domain: ");
		lblDomain.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblDomain, "split 2");

		ArrayList<String> xsdPrefixes = tBoxDefinition.getClassList();
		for (String string : tBoxDefinition.getLevelList()) {
			xsdPrefixes.add(string);
		}

		for (String dimension : tBoxDefinition.getDimensionList()) {
			xsdPrefixes.add(dimension);
		}

		for (String dataset : tBoxDefinition.getDatasetList()) {
			xsdPrefixes.add(dataset);
		}

		/*
		 * JComboBox comboBoxDomain = new JComboBox(); comboBoxDomain.setFont(new
		 * Font("Tahoma", Font.PLAIN, 12)); comboBoxDomain.setModel(new
		 * DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		 * panelAddProperty.add(comboBoxDomain, "growx, pushx, wrap");
		 */

		JList listAttributes = new JList();
		listAttributes.setFont(new Font("Tahoma", Font.PLAIN, 12));

		if (xsdPrefixes.size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listAttributes.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String attribute : xsdPrefixes) {
				listModel.addElement(attribute);
			}
			listAttributes.setModel(listModel);
		}

		listAttributes.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelAddProperty.add(listAttributes, "growx, pushx, wrap");

		JLabel lblAttributeRange = new JLabel("Range: ");
		lblAttributeRange.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblAttributeRange, "split 2");

		JComboBox comboBoxRange = new JComboBox();
		comboBoxRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxRange.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getXsdRanges().toArray()));
		panelAddProperty.add(comboBoxRange, "growx, pushx, wrap 30px");

		/*
		 * JButton btnSave = new JButton("Save"); btnSave.setFont(new Font("Tahoma",
		 * Font.BOLD, 12)); btnSave.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * } }); panelAddProperty.add(btnSave, "");
		 */

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddProperty,
				"Please enter Values for Datatype Property", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String iri = comboBoxIRI.getSelectedItem().toString();
			String name = textFieldPropertyName.getText().toString().trim();
			List<String> domains = listAttributes.getSelectedValuesList();
			String range = comboBoxRange.getSelectedItem().toString().trim();

			if (name.length() > 0) {
				tBoxExtraction.addODatatypePropertyResource(iri, name, domains, range);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide all values correctly.");
			}
		}
	}

	protected void addObjectProperty() {
		// TODO Auto-generated method stub
		JPanel panelAddProperty = new JPanel();
		panelAddProperty.setLayout(new MigLayout("", "[600px, center, grow]", "[][][][][][]"));
		panelAddProperty.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblIri, "split 2");

		JComboBox comboBoxIRI = new JComboBox();
		comboBoxIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddProperty.add(comboBoxIRI, "growx, pushx, wrap");

		JLabel lblPropertyName = new JLabel("Property Name: ");
		lblPropertyName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblPropertyName, "split 2");

		JTextField textFieldPropertyName = new JTextField();
		textFieldPropertyName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddProperty.add(textFieldPropertyName, "growx, pushx, wrap");
		textFieldPropertyName.setColumns(10);

		JLabel lblDomain = new JLabel("Domain: ");
		lblDomain.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblDomain, "split 2");

		ArrayList<String> xsdPrefixes = tBoxDefinition.getClassList();
		for (String string : tBoxDefinition.getLevelList()) {
			xsdPrefixes.add(string);
		}

		for (String dimension : tBoxDefinition.getDimensionList()) {
			xsdPrefixes.add(dimension);
		}

		for (String dataset : tBoxDefinition.getDatasetList()) {
			xsdPrefixes.add(dataset);
		}

		/*
		 * JComboBox comboBoxDomain = new JComboBox(); comboBoxDomain.setFont(new
		 * Font("Tahoma", Font.PLAIN, 12)); comboBoxDomain.setModel(new
		 * DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		 * panelAddProperty.add(comboBoxDomain, "growx, pushx, wrap");
		 */

		JList listAttributes = new JList();
		listAttributes.setFont(new Font("Tahoma", Font.PLAIN, 12));

		if (xsdPrefixes.size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listAttributes.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String attribute : xsdPrefixes) {
				listModel.addElement(attribute);
			}
			listAttributes.setModel(listModel);
		}

		listAttributes.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelAddProperty.add(listAttributes, "growx, pushx, wrap");

		JLabel lblAttributeRange = new JLabel("Range: ");
		lblAttributeRange.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddProperty.add(lblAttributeRange, "split 2");

		JComboBox comboBoxRange = new JComboBox();
		comboBoxRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxRange.setModel(new DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		panelAddProperty.add(comboBoxRange, "growx, pushx, wrap 30px");

		/*
		 * JButton btnSave = new JButton("Save"); btnSave.setFont(new Font("Tahoma",
		 * Font.BOLD, 12)); btnSave.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * } }); panelAddProperty.add(btnSave, "");
		 */

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddProperty,
				"Please enter Values for Object Property", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String iri = comboBoxIRI.getSelectedItem().toString();
			String name = textFieldPropertyName.getText().toString().trim();
			List<String> domains = listAttributes.getSelectedValuesList();
			String range = comboBoxRange.getSelectedItem().toString().trim();

			if (name.length() > 0) {
				tBoxExtraction.addObjectPropertyResource(iri, name, domains, range);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide all values correctly.");
			}
		}
	}

	protected void addClassResource() {
		// TODO Auto-generated method stub
		JPanel panelAddLevel = new JPanel();
		panelAddLevel.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddLevel.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblIri, "split 2");

		JComboBox comboBoxLevelIRI = new JComboBox();
		comboBoxLevelIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));

		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxLevelIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));

		panelAddLevel.add(comboBoxLevelIRI, "growx, pushx, wrap");

		JLabel lblLevelName = new JLabel("Concept Name: ");
		lblLevelName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLevelName, "split 2");

		JTextField textFieldLevelName = new JTextField();
		textFieldLevelName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddLevel.add(textFieldLevelName, "growx, pushx, wrap");
		textFieldLevelName.setColumns(10);

		JLabel lblLevelLabel = new JLabel("Concept Label: ");
		lblLevelLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLevelLabel, "split 2");

		JTextField textFieldLevelLabel = new JTextField();
		textFieldLevelLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddLevel.add(textFieldLevelLabel, "growx, pushx, wrap");
		textFieldLevelLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddLevel.add(comboBoxLanguage, "growx, pushx, wrap");

		JScrollPane scrollPaneLevel = new JScrollPane();
		scrollPaneLevel.setViewportView(panelAddLevel);

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPaneLevel, "Please enter Values for Concept",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String leveliri = comboBoxLevelIRI.getSelectedItem().toString();
			String levelName = textFieldLevelName.getText().toString().trim();
			String levelLabel = textFieldLevelLabel.getText().toString().trim();
			String lang = comboBoxLanguage.getSelectedItem().toString();

			if (levelName.length() > 0 && levelLabel.length() > 0) {
				tBoxExtraction.addConceptResource(leveliri, levelName, levelLabel, lang);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Level");
			}
		}
	}

	protected void addHierarchy() {
		// TODO Auto-generated method stub

		JScrollPane scrollPane = new JScrollPane();
		// scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "cell 0 0,grow");

		JPanel panelAddHierarchy = new JPanel();
		// panelAddHierarchy.setPreferredSize(new Dimension(800, 500));
		scrollPane.setViewportView(panelAddHierarchy);
		panelAddHierarchy.setBackground(Color.WHITE);
		panelAddHierarchy.setLayout(new MigLayout("", "[][800px, grow]", "[][][][][][]"));

		JLabel lblPrefix = new JLabel("Prefix:");
		lblPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblPrefix, "cell 0 0,alignx trailing");

		JComboBox comboBoxPrefix = new JComboBox();
		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxPrefix.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		comboBoxPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(comboBoxPrefix, "cell 1 0,growx");

		JLabel lblHierarchyName = new JLabel("Hierarchy Name:");
		lblHierarchyName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierarchyName, "cell 0 1,alignx trailing");

		JTextField textFieldHierName = new JTextField();
		textFieldHierName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(textFieldHierName, "cell 1 1,growx");
		textFieldHierName.setColumns(10);

		JLabel lblHierarchyLabel = new JLabel("Hierarchy Label:");
		lblHierarchyLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierarchyLabel, "cell 0 2,alignx trailing");

		JTextField textFieldHierLabel = new JTextField();
		textFieldHierLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(textFieldHierLabel, "cell 1 2,growx");
		textFieldHierLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language:");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblLanguage, "cell 0 3,alignx trailing");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		comboBoxLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(comboBoxLanguage, "cell 1 3,growx");

		JLabel lblDimension = new JLabel("Dimension:");
		lblDimension.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblDimension, "cell 0 4,alignx trailing");

		JComboBox comboBoxDimension = new JComboBox();
		comboBoxDimension.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getDimensionList().toArray()));
		comboBoxDimension.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(comboBoxDimension, "cell 1 4,growx");

		JLabel lblLevels = new JLabel("Levels:");
		lblLevels.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblLevels, "cell 0 5,alignx trailing");

		JList listHierLevels = new JList();
		listHierLevels.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listHierLevels.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelAddHierarchy.add(scrollPane_1, "cell 1 5,grow");
		scrollPane_1.setViewportView(listHierLevels);
		// panelAddHierarchy.add(listHierLevels, "cell 1 5,grow");

		if (tBoxDefinition.getLevelList().size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listHierLevels.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String level : tBoxDefinition.getLevelList()) {
				listModel.addElement(level);
			}
			listHierLevels.setModel(listModel);
		}/*

		JPanel panelAddHierarchy = new JPanel();
		panelAddHierarchy.setLayout(new MigLayout("", "[800px,center]", "[][][][][][][][][][][]"));
		panelAddHierarchy.setBackground(Color.WHITE);
		// add(panelAddHierarchy, "grow, push");

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblIri, "split 2");

		JComboBox comboBoxHierIRI = new JComboBox();
		comboBoxHierIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxHierIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddHierarchy.add(comboBoxHierIRI, "growx, pushx, wrap");

		JLabel lblHierName = new JLabel("Hierarchy Name: ");
		lblHierName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierName, "split 2");

		JTextField textFieldHierName = new JTextField();
		textFieldHierName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddHierarchy.add(textFieldHierName, "growx, pushx, wrap");
		textFieldHierName.setColumns(10);

		JLabel lblHierLabel = new JLabel("Hierarchy Label: ");
		lblHierLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierLabel, "split 2");

		JTextField textFieldHierLabel = new JTextField();
		textFieldHierLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddHierarchy.add(textFieldHierLabel, "growx, pushx, wrap");
		textFieldHierLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddHierarchy.add(comboBoxLanguage, "growx, pushx, wrap");

		JLabel lblHierDims = new JLabel("Dimension: ");
		lblHierDims.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierDims, "split 2");

		JComboBox comboBoxDimension = new JComboBox();
		comboBoxDimension.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxDimension.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getDimensionList().toArray()));
		panelAddHierarchy.add(comboBoxDimension, "growx, pushx, wrap");

		JLabel lblHierLevels = new JLabel("Levels: ");
		lblHierLevels.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblHierLevels, "split 2");

		JList listHierLevels = new JList();
		listHierLevels.setBorder(new LineBorder(new Color(0, 0, 0)));
		listHierLevels.setFont(new Font("Tahoma", Font.PLAIN, 12));

		if (tBoxDefinition.getLevelList().size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listHierLevels.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String level : tBoxDefinition.getLevelList()) {
				listModel.addElement(level);
			}
			listHierLevels.setModel(listModel);
		}

		panelAddHierarchy.add(listHierLevels, "growx, pushx, wrap");*/

		/*
		 * JLabel lblHierSteps = new JLabel("Hierarchy Steps: ");
		 * lblHierSteps.setFont(new Font("Tahoma", Font.BOLD, 16));
		 * panelAddHierarchy.add(lblHierSteps, "split 2");
		 * 
		 * JList listHierSteps = new JList(); listHierSteps.setBorder(new LineBorder(new
		 * Color(0, 0, 0))); listHierSteps.setFont(new Font("Tahoma", Font.PLAIN, 16));
		 * listModel = new DefaultListModel(); for (HierarchyStep hierarchyStep :
		 * dwSchemaDefinition.getSchemaHierSteps()) {
		 * listModel.addElement(hierarchyStep.getHierStepName()); }
		 * listHierSteps.setModel(listModel); panelAddHierarchy.add(listHierSteps,
		 * "growx, pushx, wrap");
		 */

		/*
		 * JButton btnSave = new JButton("Save"); btnSave.setFont(new Font("Tahoma",
		 * Font.BOLD, 16)); btnSave.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { // TODO Auto-generated
		 * method stub String iriString = comboBoxHierIRI.getSelectedItem().toString();
		 * 
		 * String hierName = textFieldHierName.getText().toString().trim(); String
		 * hierLabel = textFieldHierLabel.getText().toString().trim(); String
		 * dimensionString = comboBoxDimension.getSelectedItem().toString();
		 * 
		 * if (hierName.length() > 0 && hierLabel.length() > 0) { // hierName =
		 * iriString + hierName; List<String> hierLevelNames =
		 * listHierLevels.getSelectedValuesList();
		 * tBoxExtraction.addHierarchyResource(iriString, hierName, hierLabel,
		 * dimension, hierLevelNames); reloadAll(); } else {
		 * JOptionPane.showMessageDialog(null,
		 * "Please provide valid values for adding new Level."); } } });
		 * panelAddHierarchy.add(btnSave, "wrap");
		 */

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPane, "Please enter Values for Hierarchy",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String iriString = comboBoxPrefix.getSelectedItem().toString();

			String hierName = textFieldHierName.getText().toString().trim();
			String hierLabel = textFieldHierLabel.getText().toString().trim();
			String dimensionString = comboBoxDimension.getSelectedItem().toString();

			if (hierName.length() > 0 && hierLabel.length() > 0) {
				// hierName = iriString + hierName;
				List<String> hierLevelNames = listHierLevels.getSelectedValuesList();
				tBoxExtraction.addHierarchyResource(iriString, hierName, hierLabel, dimensionString, hierLevelNames);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Level.");
			}
		}
	}

	protected void addComponent(String type, String last) {
		// TODO Auto-generated method stub
		functions = new ArrayList<>();

		JPanel panelParent = new JPanel();
		panelParent.setBackground(Color.WHITE);
		// contentPane.add(panelParent, "cell 0 0,grow");
		panelParent.setLayout(new MigLayout("", "[][800px, grow]", "[][grow]"));

		JLabel lblComponentType = new JLabel("Component Type: ");
		setFont(lblComponentType);
		panelParent.add(lblComponentType, "cell 0 0,alignx trailing");

		JPanel panelHolder = new JPanel();
		JPanel panelLevel = new JPanel();
		JPanel panelDimension = new JPanel();
		JPanel panelMeasure = new JPanel();

		String[] types = null;

		if (type.equals("Cube")) {
			types = new String[] { "Measure", "Dimension" };
		} else {
			types = new String[] { "Measure", "Level" };
		}

		JComboBox comboBoxType = new JComboBox(types);
		setFont(comboBoxType);
		comboBoxType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = comboBoxType.getSelectedItem().toString().trim();
				if (selectedItem.equals("Measure")) {
					panelHolder.removeAll();

					panelHolder.add(panelMeasure, BorderLayout.CENTER);
					panelHolder.repaint();
					panelHolder.revalidate();
				} else if (selectedItem.equals("Dimension")) {
					panelHolder.removeAll();
					panelHolder.repaint();
					panelHolder.revalidate();

					panelHolder.add(panelDimension, BorderLayout.CENTER);
				} else {
					panelHolder.removeAll();
					panelHolder.repaint();
					panelHolder.revalidate();

					panelHolder.add(panelLevel, BorderLayout.CENTER);
				}
			}
		});
		panelParent.add(comboBoxType, "cell 1 0,growx");

		panelHolder.setBackground(Color.WHITE);
		panelParent.add(panelHolder, "cell 0 1 2 1,grow");
		panelHolder.setLayout(new BorderLayout(0, 0));

		panelLevel.setBackground(Color.WHITE);
		// panelHolder.add(panelLevel, BorderLayout.CENTER);
		panelLevel.setLayout(new MigLayout("", "[][grow][]", "[][][]"));

		JLabel lblLevel = new JLabel("Level:");
		setFont(lblLevel);
		panelLevel.add(lblLevel, "cell 0 0,alignx trailing");

		JTextField textFieldLevel = new JTextField();
		setFont(textFieldLevel);

		JComboBox comboBoxLevel = new JComboBox(tBoxDefinition.getLevelList().toArray());
		setFont(comboBoxLevel);
		comboBoxLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				level = comboBoxLevel.getSelectedItem().toString();
				textFieldLevel.setText(level);
			}
		});
		panelLevel.add(comboBoxLevel, "cell 1 0 2 1,growx");

		JLabel lblCardinality2 = new JLabel("Cardinality:");
		setFont(lblCardinality2);
		panelLevel.add(lblCardinality2, "cell 0 1,alignx trailing");

		JComboBox comboBoxCardinality2 = new JComboBox(tBoxDefinition.getCardinalities().toArray());
		setFont(comboBoxCardinality2);
		panelLevel.add(comboBoxCardinality2, "cell 1 1,growx");

		JButton btnAddLevel = new JButton("Add Level");
		setFont(btnAddLevel);
		btnAddLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				level = comboBoxLevel.getSelectedItem().toString();
				levelCar = comboBoxCardinality2.getSelectedItem().toString();

				textFieldLevel.setText(level + " - " + levelCar);
			}
		});
		panelLevel.add(btnAddLevel, "cell 2 1,growx");

		JLabel lblLevelComponent = new JLabel("Level Component:");
		setFont(lblLevelComponent);
		panelLevel.add(lblLevelComponent, "cell 0 2,alignx trailing");

		panelLevel.add(textFieldLevel, "cell 1 2,growx");
		textFieldLevel.setColumns(10);

		JButton btnDeleteLevel = new JButton("Delete Level");
		setFont(btnDeleteLevel);
		btnDeleteLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				level = "";
				levelCar = "";

				textFieldLevel.setText("");
			}
		});
		panelLevel.add(btnDeleteLevel, "cell 2 2");

		panelDimension.setBackground(Color.WHITE);
		// panelHolder.add(panelDimension, BorderLayout.CENTER);
		panelDimension.setLayout(new MigLayout("", "[][grow][]", "[][][]"));

		JLabel lblDimension = new JLabel("Dimension:");
		setFont(lblDimension);
		panelDimension.add(lblDimension, "cell 0 0,alignx trailing");

		JTextField textFieldDimension = new JTextField();
		setFont(textFieldDimension);

		JComboBox comboBoxDimension = new JComboBox(tBoxDefinition.getDimensionList().toArray());
		setFont(comboBoxDimension);
		comboBoxDimension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dimension = comboBoxDimension.getSelectedItem().toString();
				textFieldDimension.setText(dimension);
			}
		});
		panelDimension.add(comboBoxDimension, "cell 1 0 2 1,growx");

		JLabel lblCardinality = new JLabel("Cardinality:");
		setFont(lblCardinality);
		panelDimension.add(lblCardinality, "cell 0 1,alignx trailing");

		JComboBox comboBoxCardinality = new JComboBox(tBoxDefinition.getCardinalities().toArray());
		setFont(comboBoxCardinality);
		panelDimension.add(comboBoxCardinality, "cell 1 1,growx");

		JButton btnAddDimension = new JButton("Add Dimension");
		setFont(btnAddDimension);
		btnAddDimension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dimension = comboBoxDimension.getSelectedItem().toString();
				dimCar = comboBoxCardinality.getSelectedItem().toString();

				textFieldDimension.setText(dimension + " - " + dimCar);
			}
		});
		panelDimension.add(btnAddDimension, "cell 2 1,growx");

		JLabel lblDimensionComponent = new JLabel("Dimension Component:");
		setFont(lblDimensionComponent);
		panelDimension.add(lblDimensionComponent, "cell 0 2,alignx trailing");

		panelDimension.add(textFieldDimension, "cell 1 2,growx");
		textFieldDimension.setColumns(10);

		JButton btnDeleteDimension = new JButton("Delete Dimension");
		setFont(btnDeleteDimension);
		btnDeleteDimension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dimension = "";
				dimCar = "";

				textFieldDimension.setText("");
			}
		});
		panelDimension.add(btnDeleteDimension, "cell 2 2");

		panelMeasure.setBackground(Color.WHITE);
		panelHolder.add(panelMeasure, BorderLayout.CENTER);
		panelMeasure.setLayout(new MigLayout("", "[][grow][]", "[][][]"));

		JLabel lblMeasure = new JLabel("Measure:");
		setFont(lblMeasure);
		panelMeasure.add(lblMeasure, "cell 0 0,alignx trailing");

		JTextField textFieldMeasure = new JTextField();
		setFont(textFieldMeasure);

		JComboBox comboBoxMeasure = new JComboBox(tBoxDefinition.getMeasureList().toArray());
		setFont(comboBoxMeasure);
		comboBoxMeasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				measure = comboBoxMeasure.getSelectedItem().toString();
				textFieldMeasure.setText(measure);
			}
		});
		panelMeasure.add(comboBoxMeasure, "cell 1 0 2 1,growx");

		JLabel lblAggregateFunction = new JLabel("Aggregate Function:");
		setFont(lblAggregateFunction);
		panelMeasure.add(lblAggregateFunction, "cell 0 1,alignx trailing");

		JComboBox comboBoxFunction = new JComboBox(tBoxDefinition.getAggregatedFunctions().toArray());
		setFont(comboBoxFunction);
		panelMeasure.add(comboBoxFunction, "cell 1 1,growx");

		JButton btnAddMeasure = new JButton("Add Measure");
		setFont(btnAddMeasure);
		btnAddMeasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				measure = comboBoxMeasure.getSelectedItem().toString();
				if (!functions.contains(comboBoxFunction.getSelectedItem().toString())) {
					functions.add(comboBoxFunction.getSelectedItem().toString());
				}

				textFieldMeasure.setText(measure + " - " + fileMethods.convertToString(functions));
			}
		});
		panelMeasure.add(btnAddMeasure, "cell 2 1,growx");

		JLabel lblMeasureComponent = new JLabel("Measure Component:");
		setFont(lblMeasureComponent);
		panelMeasure.add(lblMeasureComponent, "cell 0 2,alignx trailing");

		panelMeasure.add(textFieldMeasure, "cell 1 2,growx");
		textFieldMeasure.setColumns(10);

		JButton btnDeleteMeasure = new JButton("Delete Measure");
		setFont(btnDeleteMeasure);
		btnDeleteMeasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				measure = "";
				functions = new ArrayList<>();

				textFieldMeasure.setText("");
			}
		});
		panelMeasure.add(btnDeleteMeasure, "cell 2 2");

		int confirmation = JOptionPane.showConfirmDialog(null, panelParent, "Please enter Values for Component",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			if (type.equals("Cube")) {
				String selectedItem = comboBoxType.getSelectedItem().toString();

				if (selectedItem.equals("Measure")) {
					if (!measure.equals("") && functions.size() != 0) {
						tBoxExtraction.addQbComponent(last, measure, functions);

						measure = "";
						functions = new ArrayList<>();

						reloadAll();
					} else {
						JOptionPane.showMessageDialog(null, "Please check all values");
					}
				} else {
					if (!dimension.equals("") && !dimCar.equals("")) {
						tBoxExtraction.addQbComponent(last, dimension, dimCar, "Dim");

						dimension = "";
						dimCar = "";

						reloadAll();
					} else {
						JOptionPane.showMessageDialog(null, "Please check all values");
					}
				}
			} else {
				String selectedItem = comboBoxType.getSelectedItem().toString();

				if (selectedItem.equals("Measure")) {
					if (!measure.equals("") && functions.size() != 0) {
						tBoxExtraction.addQbComponent(last, measure, functions);

						measure = "";
						functions = new ArrayList<>();

						reloadAll();
					} else {
						JOptionPane.showMessageDialog(null, "Please check all values");
					}
				} else {
					if (!level.equals("") && !levelCar.equals("")) {
						tBoxExtraction.addQbComponent(last, level, levelCar, "Lev");

						level = "";
						levelCar = "";

						reloadAll();
					} else {
						JOptionPane.showMessageDialog(null, "Please check all values");
					}
				}
			}
		}
	}

	protected void addOntologyProperty() {
		// TODO Auto-generated method stub
		JScrollPane scrollPane = new JScrollPane();
		// contentPane.add(scrollPane, "cell 0 0,grow");

		JPanel panelOntologyCreation = new JPanel();
		scrollPane.setViewportView(panelOntologyCreation);
		panelOntologyCreation.setBackground(Color.WHITE);
		panelOntologyCreation.setLayout(new MigLayout("", "[][grow][][grow]", "[][][][][][][][grow][][]"));

		JLabel lblPrefix = new JLabel("Prefix:");
		setFont(lblPrefix);
		panelOntologyCreation.add(lblPrefix, "cell 0 0,alignx trailing");

		JComboBox comboBoxIRI = new JComboBox(tBoxDefinition.getPrefixMap().keySet().toArray());
		setFont(comboBoxIRI);
		panelOntologyCreation.add(comboBoxIRI, "cell 1 0,growx");

		JLabel lblOntologyName = new JLabel("Ontology Name:");
		setFont(lblOntologyName);
		panelOntologyCreation.add(lblOntologyName, "cell 2 0,alignx trailing");

		JTextField textFieldOntologyName = new JTextField();
		setFont(textFieldOntologyName);
		panelOntologyCreation.add(textFieldOntologyName, "cell 3 0,growx");
		textFieldOntologyName.setColumns(10);

		JLabel lblPreferredNamespacePrefix = new JLabel("Ontology IRI:");
		setFont(lblPreferredNamespacePrefix);
		panelOntologyCreation.add(lblPreferredNamespacePrefix, "cell 0 1");

		JTextField textFieldOntologyIRI = new JTextField();
		setFont(textFieldOntologyIRI);
		panelOntologyCreation.add(textFieldOntologyIRI, "cell 1 1 3 1,growx");
		textFieldOntologyIRI.setColumns(10);

		JLabel lblPreferredNamespaceUri = new JLabel("Preferred Namespace URI:");
		setFont(lblPreferredNamespaceUri);
		panelOntologyCreation.add(lblPreferredNamespaceUri, "cell 0 2 2 1");

		JTextField textFieldNamespaceURI = new JTextField();
		setFont(textFieldNamespaceURI);
		panelOntologyCreation.add(textFieldNamespaceURI, "cell 2 2 2 1,growx");
		textFieldNamespaceURI.setColumns(10);

		JLabel lblLabel = new JLabel("Label:");
		setFont(lblLabel);
		panelOntologyCreation.add(lblLabel, "cell 0 3,alignx trailing");

		JTextField textFieldLabel = new JTextField();
		setFont(textFieldLabel);
		panelOntologyCreation.add(textFieldLabel, "cell 1 3 3 1,growx");
		textFieldLabel.setColumns(10);

		JLabel lblDctermsCreated = new JLabel("Created:");
		setFont(lblDctermsCreated);
		panelOntologyCreation.add(lblDctermsCreated, "cell 0 4,alignx trailing");

		JTextField textFieldCreated = new JTextField();
		setFont(textFieldCreated);
		panelOntologyCreation.add(textFieldCreated, "cell 1 4 3 1,growx");
		textFieldCreated.setColumns(10);

		JLabel lblModified = new JLabel("Modified:");
		setFont(lblModified);
		panelOntologyCreation.add(lblModified, "cell 0 5,alignx trailing");

		JTextField textFieldModified = new JTextField();
		setFont(textFieldModified);
		panelOntologyCreation.add(textFieldModified, "cell 1 5 3 1,growx");
		textFieldModified.setColumns(10);

		JLabel lblTitle = new JLabel("Title:");
		setFont(lblTitle);
		panelOntologyCreation.add(lblTitle, "cell 0 6,alignx trailing");

		JTextField textFieldTitle = new JTextField();
		setFont(textFieldTitle);
		panelOntologyCreation.add(textFieldTitle, "cell 1 6 3 1,growx");
		textFieldTitle.setColumns(10);

		JLabel lblComment = new JLabel("Comment:");
		setFont(lblComment);
		panelOntologyCreation.add(lblComment, "cell 0 7");

		JTextArea textAreaComment = new JTextArea();
		setFont(textAreaComment);
		textAreaComment.setColumns(70);
		textAreaComment.setRows(5);
		JScrollPane queryScrollPane = new JScrollPane(textAreaComment);
		panelOntologyCreation.add(queryScrollPane, "cell 1 7 3 1,grow");

		/*
		 * JTextArea textAreaComment = new JTextArea(); setFont(textAreaComment); //
		 * textAreaComment.setBorder(new LineBorder(new Color(0, 0, 0)));
		 * panelOntologyCreation.add(textAreaComment, "cell 1 7 3 1,grow");
		 */

		JLabel lblVersionInfo = new JLabel("Version Info:");
		setFont(lblVersionInfo);
		panelOntologyCreation.add(lblVersionInfo, "cell 0 8,alignx trailing");

		JTextField textFieldVersionInfo = new JTextField();
		setFont(textFieldVersionInfo);
		panelOntologyCreation.add(textFieldVersionInfo, "cell 1 8 3 1,growx");
		textFieldVersionInfo.setColumns(10);

		JLabel lblImports = new JLabel("Imports:");
		setFont(lblImports);
		panelOntologyCreation.add(lblImports, "cell 0 9,alignx trailing");

		JTextField textFieldImports = new JTextField();
		setFont(textFieldImports);
		panelOntologyCreation.add(textFieldImports, "cell 1 9 3 1,growx");
		textFieldImports.setColumns(10);

		/*
		 * textAreaComment.setText("asd"); textFieldCreated.setText("asd");
		 * textFieldImports.setText("asd"); textFieldLabel.setText("asd");
		 * textFieldModified.setText("asd"); textFieldNamespaceURI.setText("asd");
		 * textFieldOntologyIRI.setText("asd"); textFieldOntologyName.setText("asd");
		 * textFieldTitle.setText("asd"); textFieldVersionInfo.setText("asd");
		 */

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPane, "Please enter Values for Ontology",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String iri = "", name = "", namespaceIRI = "", namespaceURI = "", label = "", created = "", modified = "",
					title = "", comment = "", versionInfo = "", imports = "";

			iri = comboBoxIRI.getSelectedItem().toString();
			name = textFieldOntologyName.getText().toString().trim();
			namespaceIRI = textFieldOntologyIRI.getText().toString().trim();
			namespaceURI = textFieldNamespaceURI.getText().toString().trim();
			label = textFieldLabel.getText().toString().trim();
			created = textFieldCreated.getText().toString().trim();
			modified = textFieldModified.getText().toString().trim();
			title = textFieldTitle.getText().toString().trim();
			comment = textAreaComment.getText().toString().trim();
			versionInfo = textFieldVersionInfo.getText().toString().trim();
			imports = textFieldImports.getText().toString().trim();

			if (name.length() > 0) {

				tBoxExtraction.addOntology(iri, name, namespaceIRI, namespaceURI, label, created, modified, title,
						comment, versionInfo, imports);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new ontology");
			}
		}
	}

	private void setFont(JComponent component) {
		// TODO Auto-generated method stub
		component.setFont(new Font("Tahoma", Font.BOLD, 12));
	}

	protected void addHierarchyStepProperty() {
		// TODO Auto-generated method stub
		JPanel panelAddHierStep = new JPanel();
		panelAddHierStep.setLayout(new MigLayout("", "[600px, center, grow]", "[][][][][][]"));
		panelAddHierStep.setBackground(Color.WHITE);

		/*
		 * JLabel lblStepName = new JLabel("Step Name: "); lblStepName.setFont(new
		 * Font("Tahoma", Font.BOLD, 12)); panelAddHierStep.add(lblStepName, "split 2");
		 * 
		 * JTextField textFieldStepName = new JTextField();
		 * textFieldStepName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		 * panelAddHierStep.add(textFieldStepName, "growx, pushx, wrap");
		 * textFieldStepName.setColumns(10);
		 */

		JLabel lblHierarchy = new JLabel("Hierarchy: ");
		lblHierarchy.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(lblHierarchy, "split 2");

		JComboBox comboBoxChildLevel = new JComboBox();
		JComboBox comboBoxParentLevel = new JComboBox();

		JComboBox comboBoxHierarchies = new JComboBox();
		comboBoxHierarchies.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxHierarchies.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getHierarchyList().toArray()));
		panelAddHierStep.add(comboBoxHierarchies, "growx, pushx, wrap");
		comboBoxHierarchies.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String value = (String) comboBoxHierarchies.getSelectedItem();

				ArrayList<String> list = new ArrayList<>();
				list = tBoxExtraction.extractHierarchyLevels(value);
				comboBoxChildLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));
				comboBoxParentLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));
			}
		});
		String value = (String) comboBoxHierarchies.getSelectedItem();

		ArrayList<String> list = new ArrayList<>();
		list = tBoxExtraction.extractHierarchyLevels(value);
		comboBoxChildLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));
		comboBoxParentLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));

		JLabel lblParentLevel = new JLabel("Parent Level: ");
		lblParentLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(lblParentLevel, "split 2");

		comboBoxParentLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxParentLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));
		panelAddHierStep.add(comboBoxParentLevel, "growx, pushx, wrap");

		JLabel lblChildLevel = new JLabel("Child Level: ");
		lblChildLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(lblChildLevel, "split 2");

		comboBoxChildLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxChildLevel.setModel(new DefaultComboBoxModel<>(list.toArray()));
		panelAddHierStep.add(comboBoxChildLevel, "growx, pushx, wrap");

		JLabel lblCardinality = new JLabel("Cardinality: ");
		lblCardinality.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(lblCardinality, "split 2");

		JComboBox comboBoxCardinality = new JComboBox();
		comboBoxCardinality.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getCardinalities().toArray()));
		comboBoxCardinality.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(comboBoxCardinality, "growx, pushx, wrap");

		JLabel lblRollupProperty = new JLabel("Rollup Property: ");
		lblRollupProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierStep.add(lblRollupProperty, "split 2");

		JComboBox comboBoxRollupProperty = new JComboBox();
		comboBoxRollupProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxRollupProperty.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getRollupList().toArray()));
		panelAddHierStep.add(comboBoxRollupProperty, "growx, pushx, wrap");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddHierStep,
				"Please enter Values for Hierarchy step", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			/*
			 * CommonMethods commonMethods = new CommonMethods(); if
			 * (!commonMethods.checkHasNullString(stepName)) { } else {
			 * JOptionPane.showMessageDialog(null,
			 * "Please provide valid values for adding Hierarchy Step."); }
			 */

			String stepHierName = "", childLevelName = "", parentLevelName = "", cardinality = "", rollUpProperty = "";

			if (comboBoxHierarchies.getSelectedItem() != null)
				stepHierName = comboBoxHierarchies.getSelectedItem().toString();
			if (comboBoxChildLevel.getSelectedItem() != null)
				childLevelName = comboBoxChildLevel.getSelectedItem().toString();
			if (comboBoxParentLevel.getSelectedItem() != null)
				parentLevelName = comboBoxParentLevel.getSelectedItem().toString();

			if (comboBoxCardinality.getSelectedItem() != null)
				cardinality = comboBoxCardinality.getSelectedItem().toString();

			if (comboBoxRollupProperty.getSelectedItem() != null)
				rollUpProperty = comboBoxRollupProperty.getSelectedItem().toString();

			tBoxExtraction.addHierarchyStepResource(stepHierName, childLevelName, parentLevelName, cardinality,
					rollUpProperty);
			reloadAll();
		}
	}

	private void setEditatbleProperties(JTree tree, DefaultTreeCellRenderer rendererObject) {
		// TODO Auto-generated method stub
		JTextField editedNodeObject = new JTextField();
		TreeCellEditor textEditorObject = new DefaultCellEditor(editedNodeObject);
		TreeCellEditor editorObject = new DefaultTreeCellEditor(tree, rendererObject, textEditorObject);
		editorObject.addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent e) {
				// TODO Auto-generated method stub
				updateResource(getSelectedNode(), editedNodeObject.getText().toString().trim());
			}

			@Override
			public void editingCanceled(ChangeEvent e) {
				// TODO Auto-generated method stub
				updateResource(getSelectedNode(), editedNodeObject.getText().toString().trim());
			}
		});
		tree.setCellEditor(editorObject);
	}

	protected void addCuboid() {
		// TODO Auto-generated method stub
		JScrollPane scrollPane = new JScrollPane();
		// contentPane.add(scrollPane, "cell 0 0,grow");

		JPanel panelAddCube = new JPanel();
		scrollPane.setViewportView(panelAddCube);
		panelAddCube.setBackground(Color.WHITE);
		panelAddCube.setLayout(new MigLayout("", "[][400px, grow][][400px, grow]", "[][][][][]"));

		LinkedHashMap<String, ArrayList<String>> measureMap = new LinkedHashMap<>();
		LinkedHashMap<String, String> dimensionMap = new LinkedHashMap<>();

		JLabel lblIri = new JLabel("Prefix:");
		setFont(lblIri);
		panelAddCube.add(lblIri, "cell 0 0,alignx trailing");

		JComboBox comboBoxCuboidIRI = new JComboBox();
		setFont(comboBoxCuboidIRI);
		comboBoxCuboidIRI.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getPrefixMap().keySet().toArray()));
		panelAddCube.add(comboBoxCuboidIRI, "cell 1 0,growx");

		JLabel lblCubeName = new JLabel("Cuboid Name:");
		setFont(lblCubeName);
		panelAddCube.add(lblCubeName, "cell 2 0,alignx trailing");

		JTextField textFieldCuboidName = new JTextField();
		setFont(textFieldCuboidName);
		panelAddCube.add(textFieldCuboidName, "cell 3 0,growx");
		textFieldCuboidName.setColumns(10);

		JLabel lblCube = new JLabel("Cube:");
		setFont(lblCube);
		panelAddCube.add(lblCube, "cell 0 1,alignx trailing");

		JComboBox comboBoxCube = new JComboBox();
		setFont(comboBoxCube);
		comboBoxCube.setModel(
				new DefaultComboBoxModel<>(tBoxDefinition.getHashMapStrings(tBoxDefinition.getCubeList()).toArray()));
		panelAddCube.add(comboBoxCube, "cell 1 1 3 1,growx");

		JPanel panelMeasure = new JPanel();
		panelMeasure.setBorder(new TitledBorder(null, "Measure Component Creation", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panelMeasure.setBackground(Color.WHITE);
		panelAddCube.add(panelMeasure, "cell 0 2 4 1,grow");
		panelMeasure.setLayout(new MigLayout("", "[][grow][]", "[][][grow][grow]"));

		JLabel lblMeasure = new JLabel("Measure:");
		setFont(lblMeasure);
		panelMeasure.add(lblMeasure, "cell 0 0,alignx trailing");

		JComboBox comboBoxCubeMeasure = new JComboBox();
		setFont(comboBoxCubeMeasure);
		comboBoxCubeMeasure.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getMeasureList().toArray()));
		panelMeasure.add(comboBoxCubeMeasure, "cell 1 0 2 1,growx");

		JLabel lblAggregateFunction = new JLabel("Aggregate Function:");
		setFont(lblAggregateFunction);
		panelMeasure.add(lblAggregateFunction, "cell 0 1,alignx trailing");

		/*
		 * JComboBox comboBoxFunction = new JComboBox(); setFont(comboBoxFunction);
		 * comboBoxFunction.setModel(new
		 * DefaultComboBoxModel<>(tBoxDefinition.getAggregatedFunctions().toArray()));
		 * panelMeasure.add(comboBoxFunction, "cell 1 1,growx");
		 */

		JList listFunction = new JList();
		listFunction.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getAggregatedFunctions().toArray()));
		listFunction.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listFunction.setFont(new Font("Tahoma", Font.BOLD, 12));
		listFunction.setBackground(Color.WHITE);
		panelMeasure.add(listFunction, "cell 1 1,grow");

		JList listMeasure = new JList();
		setFont(listMeasure);

		JButton btnAddMeasure = new JButton("Add Measure");
		setFont(btnAddMeasure);
		btnAddMeasure.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAddMeasure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String measure = comboBoxCubeMeasure.getSelectedItem().toString();
				// String function = comboBoxFunction.getSelectedItem().toString();
				ArrayList<String> functions = (ArrayList<String>) listFunction.getSelectedValuesList();
				measureMap.put(measure, functions);

				/*
				 * if (measureMap.containsKey(measure)) { if
				 * (!measureMap.get(measure).contains(function)) {
				 * measureMap.get(measure).add(function); } } else { ArrayList<String> arrayList
				 * = new ArrayList<>(); arrayList.add(function); measureMap.put(measure,
				 * arrayList); }
				 */

				DefaultListModel<String> listModel = new DefaultListModel();
				for (Entry<String, ArrayList<String>> entry : measureMap.entrySet()) {
					String key = entry.getKey();
					ArrayList<String> value = entry.getValue();

					listModel.addElement(key + " - " + fileMethods.convertToString(value));
					listMeasure.setModel(listModel);
				}
			}
		});
		panelMeasure.add(btnAddMeasure, "cell 2 1,growx");

		JLabel lblMeasureComponents = new JLabel("Measure Components:");
		setFont(lblMeasureComponents);
		panelMeasure.add(lblMeasureComponents, "cell 0 2");

		DefaultListModel<String> model = new DefaultListModel();
		model.addElement("\n\n");
		listMeasure.setModel(model);
		listMeasure.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMeasure.add(listMeasure, "cell 1 2,grow");

		JButton btnDltMeasure = new JButton("Delete Measure");
		setFont(btnDltMeasure);
		btnDltMeasure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!listMeasure.isSelectionEmpty()) {
					String[] parts = listMeasure.getSelectedValue().toString().split("-");

					measureMap.remove(parts[0].trim());

					DefaultListModel<String> listModel = (DefaultListModel<String>) listMeasure.getModel();
					listModel.removeElementAt(listMeasure.getSelectedIndex());
					if (listModel.isEmpty()) {
						DefaultListModel<String> model = new DefaultListModel();
						model.addElement("\n\n");
						listMeasure.setModel(model);
					}
				}
			}
		});
		panelMeasure.add(btnDltMeasure, "cell 2 2");

		JPanel panelLevel = new JPanel();
		panelLevel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Level Component Creation",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelLevel.setBackground(Color.WHITE);
		panelAddCube.add(panelLevel, "cell 0 3 4 1,grow");
		panelLevel.setLayout(new MigLayout("", "[][grow][]", "[][][grow]"));

		JLabel lblLevel = new JLabel("Levels:");
		setFont(lblLevel);
		panelLevel.add(lblLevel, "cell 0 0,alignx trailing");

		JComboBox comboBoxLevel = new JComboBox();
		setFont(comboBoxLevel);
		comboBoxLevel.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getLevelList().toArray()));
		panelLevel.add(comboBoxLevel, "cell 1 0 2 1,growx");

		JLabel lblCardinality = new JLabel("Cardinality:");
		setFont(lblCardinality);
		panelLevel.add(lblCardinality, "cell 0 1,alignx trailing");

		JComboBox comboBoxCardinality = new JComboBox();
		setFont(comboBoxCardinality);
		comboBoxCardinality.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getCardinalities().toArray()));
		panelLevel.add(comboBoxCardinality, "cell 1 1,growx");

		JList listCuboidLevel = new JList();
		setFont(listCuboidLevel);

		JButton btnAddLevel = new JButton("Add Level");
		setFont(btnAddLevel);
		btnAddLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				String dimension = comboBoxLevel.getSelectedItem().toString();
				String cardinality = comboBoxCardinality.getSelectedItem().toString();

				dimensionMap.put(dimension, cardinality);

				DefaultListModel<String> listModel = new DefaultListModel();
				for (Entry<String, String> entry : dimensionMap.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					listModel.addElement(key + " - " + value);
					listCuboidLevel.setModel(listModel);
				}
			}
		});
		panelLevel.add(btnAddLevel, "cell 2 1,growx");

		JLabel lblDimensionComponents = new JLabel("Level Components:");
		setFont(lblDimensionComponents);
		panelLevel.add(lblDimensionComponents, "cell 0 2");

		model = new DefaultListModel();
		model.addElement("\n\n");
		listCuboidLevel.setModel(model);
		listCuboidLevel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelLevel.add(listCuboidLevel, "cell 1 2,grow");

		JButton btnDltLevel = new JButton("Delete Level");
		setFont(btnDltLevel);
		btnDltLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				if (!listCuboidLevel.isSelectionEmpty()) {
					dimensionMap.remove(listCuboidLevel.getSelectedValue().toString());

					DefaultListModel<String> listModel = (DefaultListModel<String>) listCuboidLevel.getModel();
					listModel.removeElementAt(listCuboidLevel.getSelectedIndex());
					if (listModel.isEmpty()) {
						DefaultListModel<String> model = new DefaultListModel();
						model.addElement("\n\n");
						listMeasure.setModel(model);
					}
				}
			}
		});
		panelLevel.add(btnDltLevel, "cell 2 2");

		JLabel lblNotation = new JLabel("Notation:");
		setFont(lblNotation);
		panelAddCube.add(lblNotation, "cell 0 4,alignx trailing");

		JTextField textFieldNotation = new JTextField();
		setFont(textFieldNotation);
		panelAddCube.add(textFieldNotation, "cell 1 4 3 1,growx");
		textFieldNotation.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPane, "Please enter Values for Cuboid",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String nameIRI = (String) comboBoxCuboidIRI.getSelectedItem();
			String name = textFieldCuboidName.getText().toString().trim();
			String cube = comboBoxCube.getSelectedItem().toString();
			String notation = textFieldNotation.getText().toString().trim();

			if (name.length() == 0) {
				JOptionPane.showMessageDialog(null, "Enter a valid name");
			} else {
				nameIRI = tBoxDefinition.getPrefixMap().get(nameIRI).trim();
				tBoxExtraction.addCuboid(nameIRI, name, cube, notation, measureMap, dimensionMap);
				reloadAll();
			}
		}
	}

	protected void addCube() {
		// TODO Auto-generated method stub
		JPanel panelAddCube = new JPanel();
		panelAddCube.setBackground(Color.WHITE);
		// contentPane.add(panelAddCube, "cell 0 0,grow");
		panelAddCube.setLayout(new MigLayout("", "[][400px, grow][][400px, grow]", "[][][][]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(panelAddCube);

		LinkedHashMap<String, ArrayList<String>> measureMap = new LinkedHashMap<>();
		LinkedHashMap<String, String> dimensionMap = new LinkedHashMap<>();

		JLabel lblIri = new JLabel("Prefix:");
		setFont(lblIri);
		panelAddCube.add(lblIri, "cell 0 0,alignx trailing");

		JComboBox comboBoxCubeIRI = new JComboBox();
		setFont(comboBoxCubeIRI);
		comboBoxCubeIRI.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getPrefixMap().keySet().toArray()));
		panelAddCube.add(comboBoxCubeIRI, "cell 1 0,growx");

		JLabel lblCubeName = new JLabel("Cube Name:");
		setFont(lblCubeName);
		panelAddCube.add(lblCubeName, "cell 2 0,alignx trailing");

		JTextField textFieldCubeName = new JTextField();
		setFont(textFieldCubeName);
		panelAddCube.add(textFieldCubeName, "cell 3 0,growx");
		textFieldCubeName.setColumns(10);

		JPanel panelMeasure = new JPanel();
		panelMeasure.setBorder(new TitledBorder(null, "Measure Component Creation", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panelMeasure.setBackground(Color.WHITE);
		panelAddCube.add(panelMeasure, "cell 0 1 4 1,grow");
		panelMeasure.setLayout(new MigLayout("", "[][grow][]", "[][][grow][grow]"));

		JLabel lblMeasure = new JLabel("Measure:");
		setFont(lblMeasure);
		panelMeasure.add(lblMeasure, "cell 0 0,alignx trailing");

		JComboBox comboBoxCubeMeasure = new JComboBox();
		setFont(comboBoxCubeMeasure);
		comboBoxCubeMeasure.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getMeasureList().toArray()));
		panelMeasure.add(comboBoxCubeMeasure, "cell 1 0 2 1,growx");

		JLabel lblAggregateFunction = new JLabel("Aggregate Function:");
		panelMeasure.add(lblAggregateFunction, "cell 0 1,alignx trailing");

		/*
		 * JComboBox comboBoxCubeFunction = new JComboBox();
		 * setFont(comboBoxCubeFunction); comboBoxCubeFunction.setModel(new
		 * DefaultComboBoxModel<>(tBoxDefinition.getAggregatedFunctions().toArray()));
		 * panelMeasure.add(comboBoxCubeFunction, "cell 1 1,growx");
		 */

		JList listFunction = new JList();
		listFunction.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getAggregatedFunctions().toArray()));
		listFunction.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listFunction.setFont(new Font("Tahoma", Font.BOLD, 12));
		listFunction.setBackground(Color.WHITE);
		panelMeasure.add(listFunction, "cell 1 1,grow");

		JList listCubeMeasure = new JList();
		setFont(listCubeMeasure);

		JButton btnAddMeasure = new JButton("Add Measure");
		setFont(btnAddMeasure);
		btnAddMeasure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String measure = (String) comboBoxCubeMeasure.getSelectedItem();
				ArrayList<String> functions = (ArrayList<String>) listFunction.getSelectedValuesList();
				// String function = (String) comboBoxCubeFunction.getSelectedItem();

				/*
				 * if (measureMap.containsKey(measure)) { if
				 * (!measureMap.get(measure).contains(function)) {
				 * measureMap.get(measure).add(function); } } else { ArrayList<String> arrayList
				 * = new ArrayList<>(); arrayList.add(function); measureMap.put(measure,
				 * arrayList); }
				 */

				measureMap.put(measure, functions);

				DefaultListModel<String> listModel = new DefaultListModel();
				for (Entry<String, ArrayList<String>> entry : measureMap.entrySet()) {
					String key = entry.getKey();
					ArrayList<String> value = entry.getValue();

					listModel.addElement(key + " - " + fileMethods.convertToString(value));
					listCubeMeasure.setModel(listModel);
				}
			}
		});
		panelMeasure.add(btnAddMeasure, "cell 2 1,growx");

		JLabel lblMeasureComponents = new JLabel("Measure Components:");
		setFont(lblMeasureComponents);
		panelMeasure.add(lblMeasureComponents, "cell 0 2");

		listCubeMeasure.setBorder(new LineBorder(new Color(0, 0, 0)));
		DefaultListModel<String> model = new DefaultListModel();
		model.addElement("\n\n");
		listCubeMeasure.setModel(model);
		panelMeasure.add(listCubeMeasure, "cell 1 2,grow");

		JButton btnMeasureDlt = new JButton("Delete Measure");
		setFont(btnMeasureDlt);
		btnMeasureDlt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!listCubeMeasure.isSelectionEmpty()) {
					String[] parts = listCubeMeasure.getSelectedValue().toString().split("-");

					measureMap.remove(parts[0].trim());

					DefaultListModel<String> listModel = (DefaultListModel<String>) listCubeMeasure.getModel();
					listModel.removeElementAt(listCubeMeasure.getSelectedIndex());
					if (listModel.isEmpty()) {
						listCubeMeasure.setModel(model);
					}
				}
			}
		});
		panelMeasure.add(btnMeasureDlt, "cell 2 2");

		JPanel panelDimension = new JPanel();
		setFont(panelDimension);
		panelDimension.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Dimension Component Creation", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelDimension.setBackground(Color.WHITE);
		panelAddCube.add(panelDimension, "cell 0 2 4 1,grow");
		panelDimension.setLayout(new MigLayout("", "[][grow][]", "[][][grow]"));

		JLabel lblDimension = new JLabel("Dimension:");
		setFont(lblDimension);
		panelDimension.add(lblDimension, "cell 0 0,alignx trailing");

		JComboBox comboBoxDimension = new JComboBox();
		setFont(comboBoxDimension);
		ArrayList<String> temp = new ArrayList<>();
		temp = tBoxDefinition.getDimensionList();
		comboBoxDimension.setModel(new DefaultComboBoxModel<>(temp.toArray()));
		panelDimension.add(comboBoxDimension, "cell 1 0 2 1,growx");

		JLabel lblCardinality = new JLabel("Cardinality:");
		setFont(lblCardinality);
		panelDimension.add(lblCardinality, "cell 0 1,alignx trailing");

		JComboBox comboBoxCardinality = new JComboBox();
		setFont(comboBoxCardinality);
		comboBoxCardinality.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getCardinalities().toArray()));
		panelDimension.add(comboBoxCardinality, "cell 1 1,growx");

		JList listDimCube = new JList();
		setFont(listDimCube);

		JButton btnAddDim = new JButton("Add Dimension");
		setFont(btnAddDim);
		btnAddDim.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String dimension = comboBoxDimension.getSelectedItem().toString();
				String cardinality = (String) comboBoxCardinality.getSelectedItem();

				dimensionMap.put(dimension, cardinality);

				DefaultListModel<String> listModel = new DefaultListModel();
				for (Entry<String, String> entry : dimensionMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					listModel.addElement(key + " - " + value);
					listDimCube.setModel(listModel);
				}
			}
		});
		panelDimension.add(btnAddDim, "cell 2 1,growx");

		JLabel lblDimensionComponents = new JLabel("Dimension Components:");
		setFont(lblDimensionComponents);
		panelDimension.add(lblDimensionComponents, "cell 0 2");

		listDimCube.setBorder(new LineBorder(new Color(0, 0, 0)));
		listDimCube.setModel(model);
		panelDimension.add(listDimCube, "cell 1 2,grow");

		JButton btnDltDim = new JButton("Delete Dimension");
		setFont(btnDltDim);
		btnDltDim.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!listDimCube.isSelectionEmpty()) {
					dimensionMap.remove(listDimCube.getSelectedValue().toString());

					DefaultListModel<String> listModel = (DefaultListModel<String>) listDimCube.getModel();
					listModel.removeElementAt(listDimCube.getSelectedIndex());
					if (listModel.isEmpty()) {
						listDimCube.setModel(model);
					}
				}
			}
		});
		panelDimension.add(btnDltDim, "cell 2 2");

		JLabel lblNotation = new JLabel("Notation:");
		setFont(lblNotation);
		panelAddCube.add(lblNotation, "cell 0 3,alignx trailing");

		JTextField textFieldNotation = new JTextField();
		setFont(textFieldNotation);
		panelAddCube.add(textFieldNotation, "cell 1 3 3 1,growx");
		textFieldNotation.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPane, "Please enter Values for Cube",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String nameIRI = (String) comboBoxCubeIRI.getSelectedItem();
			String name = textFieldCubeName.getText().toString().trim();
			String notation = textFieldNotation.getText().toString().trim();

			if (name.length() == 0) {
				JOptionPane.showMessageDialog(null, "Enter a valid name");
			} else {
				nameIRI = tBoxDefinition.getPrefixMap().get(nameIRI).trim();
				tBoxExtraction.addCube(nameIRI, name, notation, measureMap, dimensionMap);
				reloadAll();
			}
		}
	}

	protected void addDataset() {
		// TODO Auto-generated method stub
		JPanel panelAddCube = new JPanel();
		panelAddCube.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][30px]"));
		panelAddCube.setBackground(Color.WHITE);

		JLabel lblDatasetIRI = new JLabel("Prefix: ");
		lblDatasetIRI.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelAddCube.add(lblDatasetIRI, "split 4");

		JComboBox comboBoxDatasetIRI = new JComboBox();
		comboBoxDatasetIRI.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxDatasetIRI.setModel(new DefaultComboBoxModel<>(tBoxDefinition.getPrefixMap().keySet().toArray()));
		panelAddCube.add(comboBoxDatasetIRI, "growx, pushx");

		JLabel lblDatasetName = new JLabel("Dataset Name: ");
		lblDatasetName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblDatasetName, "split 2");

		JTextField textFieldDatasetName = new JTextField();
		textFieldDatasetName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(textFieldDatasetName, "growx, pushx, wrap");
		textFieldDatasetName.setColumns(10);

		String[] options = { "Cube", "Cuboid" };

		JLabel lblType = new JLabel("Type: ");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblType, "split 2");

		JPanel panelDataset = new JPanel();
		panelDataset.setLayout(new MigLayout("", "[center, grow]", "[][grow]"));
		panelDataset.setBackground(Color.WHITE);

		JComboBox comboBoxCube = new JComboBox();
		JComboBox comboBoxCuboid = new JComboBox();

		JComboBox comboBoxSelection = new JComboBox(options);
		comboBoxSelection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				switch (comboBoxSelection.getSelectedIndex()) {
				case 0:
					panelDataset.removeAll();
					panelDataset.repaint();
					panelDataset.revalidate();

					JLabel lblCube = new JLabel("Cube: ");
					lblCube.setFont(new Font("Tahoma", Font.BOLD, 12));
					panelDataset.add(lblCube, "split 2");

					comboBoxCube.setFont(new Font("Tahoma", Font.PLAIN, 12));
					comboBoxCube.setModel(new DefaultComboBoxModel<>(
							tBoxDefinition.getHashMapStrings(tBoxDefinition.getCubeList()).toArray()));
					panelDataset.add(comboBoxCube, "growx, pushx, wrap");

					break;
				case 1:
					panelDataset.removeAll();
					panelDataset.repaint();
					panelDataset.revalidate();

					JLabel lblCuboid = new JLabel("Cuboid: ");
					lblCuboid.setFont(new Font("Tahoma", Font.BOLD, 12));
					panelDataset.add(lblCuboid, "split 2");

					comboBoxCuboid.setFont(new Font("Tahoma", Font.PLAIN, 12));
					comboBoxCuboid.setModel(new DefaultComboBoxModel<>(
							tBoxDefinition.getHashMapStrings(tBoxDefinition.getCuboidList()).toArray()));
					panelDataset.add(comboBoxCuboid, "growx, pushx, wrap");

					break;
				default:
					break;
				}
			}
		});

		panelAddCube.add(comboBoxSelection, "growx, pushx, wrap");
		panelAddCube.add(panelDataset, "grow, push");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddCube, "Please enter Values for Dataset",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String datasetIRI = comboBoxDatasetIRI.getSelectedItem().toString();
			String datasetName = textFieldDatasetName.getText().toString().trim();
			String datasetcube = "";

			if (comboBoxSelection.getSelectedItem().toString().equals("Cube")) {
				datasetcube = comboBoxCube.getSelectedItem().toString().trim();
			} else {
				datasetcube = comboBoxCuboid.getSelectedItem().toString().trim();
			}

			datasetIRI = tBoxDefinition.getPrefixMap().get(datasetIRI).trim();

			if (datasetName.length() > 0 && datasetcube.length() > 0) {
				tBoxExtraction.addDatasetProperty(datasetIRI, datasetName, datasetcube);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new dimension");
			}
		}
	}

	protected void addDimension() {
		// TODO Auto-generated method stub
		JPanel panelAddCube = new JPanel();
		panelAddCube.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddCube.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblIri, "split 2");

		JComboBox comboBoxDiemsionIRI = new JComboBox();
		comboBoxDiemsionIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));

		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxDiemsionIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddCube.add(comboBoxDiemsionIRI, "growx, pushx, wrap");

		JLabel lblDimName = new JLabel("Dimension Name: ");
		lblDimName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblDimName, "split 2");

		JTextField textFieldDimName = new JTextField();
		textFieldDimName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(textFieldDimName, "growx, pushx, wrap");
		textFieldDimName.setColumns(10);

		JLabel lblDimLabel = new JLabel("Dimension Label: ");
		lblDimLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblDimLabel, "split 2");

		JTextField textFieldDimLabel = new JTextField();
		textFieldDimLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(textFieldDimLabel, "growx, pushx, wrap");
		textFieldDimLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddCube.add(comboBoxLanguage, "growx, pushx, wrap");

		JLabel lblDimHiers = new JLabel("Hierarchies: ");
		lblDimHiers.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblDimHiers, "split 2");

		JList listDimHiers = new JList();
		listDimHiers.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listDimHiers.setBorder(new LineBorder(new Color(0, 0, 0)));
		if (tBoxDefinition.getHierarchyList().size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listDimHiers.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String hierarchy : tBoxDefinition.getHierarchyList()) {
				listModel.addElement(hierarchy);
			}
			listDimHiers.setModel(listModel);
		}

		panelAddCube.add(listDimHiers, "growx, pushx, wrap");

		JLabel lblRange = new JLabel("Range: ");
		lblRange.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddCube.add(lblRange, "split 2");

		JComboBox comboBoxRange = new JComboBox();
		comboBoxRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> xsdPrefixes = tBoxDefinition.getXsdRanges();
		comboBoxRange.setModel(new DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		panelAddCube.add(comboBoxRange, "growx, pushx, wrap 30px");

		for (String string : tBoxDefinition.getLevelList()) {
			comboBoxRange.addItem(string);
		}

		for (String dataset : tBoxDefinition.getDataList()) {
			comboBoxRange.addItem(dataset);
		}

		for (String owlClass : tBoxDefinition.getClassList()) {
			comboBoxRange.addItem(owlClass);
		}

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddCube, "Please enter Values for Dimension",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String dimiri = comboBoxDiemsionIRI.getSelectedItem().toString();
			String dimName = textFieldDimName.getText().toString().trim();
			String dimLabel = textFieldDimLabel.getText().toString().trim();
			String dimlang = comboBoxLanguage.getSelectedItem().toString();
			List<String> hierarchies = listDimHiers.getSelectedValuesList();
			String range = comboBoxRange.getSelectedItem().toString();

			dimiri = tBoxDefinition.getPrefixMap().get(dimiri);

			if (dimName.length() > 0 && dimLabel.length() > 0) {
				tBoxExtraction.addDimensionResource(dimiri, dimName, dimLabel, dimlang, hierarchies, range);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new dimension");
			}
		}
	}

	protected void addLevelProperty() {
		// TODO Auto-generated method stub
		JPanel panelAddLevel = new JPanel();
		panelAddLevel.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddLevel.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblIri, "split 2");

		JComboBox comboBoxLevelIRI = new JComboBox();
		comboBoxLevelIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));

		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());
		comboBoxLevelIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));

		panelAddLevel.add(comboBoxLevelIRI, "growx, pushx, wrap");

		JLabel lblLevelName = new JLabel("Level Name: ");
		lblLevelName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLevelName, "split 2");

		JTextField textFieldLevelName = new JTextField();
		textFieldLevelName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddLevel.add(textFieldLevelName, "growx, pushx, wrap");
		textFieldLevelName.setColumns(10);

		JLabel lblLevelLabel = new JLabel("Level Label: ");
		lblLevelLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLevelLabel, "split 2");

		JTextField textFieldLevelLabel = new JTextField();
		textFieldLevelLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddLevel.add(textFieldLevelLabel, "growx, pushx, wrap");
		textFieldLevelLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddLevel.add(comboBoxLanguage, "growx, pushx, wrap");

		JLabel lblAttributes = new JLabel("Attributes: ");
		lblAttributes.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblAttributes, "split 2");

		JList listAttributes = new JList();
		listAttributes.setFont(new Font("Tahoma", Font.PLAIN, 12));

		if (tBoxDefinition.getAttributeList().size() == 0) {
			DefaultListModel<String> model = new DefaultListModel();
			model.addElement("\n\n");
			listAttributes.setModel(model);
		} else {
			DefaultListModel<String> listModel = new DefaultListModel();
			for (String attribute : tBoxDefinition.getAttributeList()) {
				listModel.addElement(attribute);
			}
			listAttributes.setModel(listModel);
		}

		listAttributes.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelAddLevel.add(listAttributes, "growx, pushx, wrap");

		JLabel lblDatatype = new JLabel("Range: ");
		lblDatatype.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddLevel.add(lblDatatype, "split 2");

		JComboBox comboboxDatatype = new JComboBox();
		comboboxDatatype.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddLevel.add(comboboxDatatype, "growx, pushx, wrap");

		ArrayList<String> xsdPrefixes = tBoxDefinition.getXsdRanges();
		comboboxDatatype.setModel(new DefaultComboBoxModel<>(xsdPrefixes.toArray()));

		for (String string : tBoxDefinition.getClassList()) {
			comboboxDatatype.addItem(string);
		}

		JScrollPane scrollPaneLevel = new JScrollPane();
		scrollPaneLevel.setViewportView(panelAddLevel);

		int confirmation = JOptionPane.showConfirmDialog(null, scrollPaneLevel,
				"Please enter Values for Level Property", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String leveliri = comboBoxLevelIRI.getSelectedItem().toString();

			List<String> selectedAttributes = listAttributes.getSelectedValuesList();
			String levelName = textFieldLevelName.getText().toString().trim();
			String levelLabel = textFieldLevelLabel.getText().toString().trim();
			String lang = comboBoxLanguage.getSelectedItem().toString();
			String datatype = comboboxDatatype.getSelectedItem().toString();

			leveliri = tBoxDefinition.getPrefixMap().get(leveliri);

			if (levelName.length() > 0 && levelLabel.length() > 0) {
				tBoxExtraction.addLevelResource(leveliri, levelName, levelLabel, lang, selectedAttributes, datatype);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Level");
			}
		}
	}

	protected void addRollUpProperty() {
		// TODO Auto-generated method stub
		JPanel panelAddRUPProperty = new JPanel();
		panelAddRUPProperty.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddRUPProperty.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddRUPProperty.add(lblIri, "split 2");

		JComboBox comboBoxRUPIRI = new JComboBox();
		comboBoxRUPIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));

		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());

		comboBoxRUPIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddRUPProperty.add(comboBoxRUPIRI, "growx, pushx, wrap");

		JLabel lblRUPPropertyName = new JLabel("Property Name: ");
		lblRUPPropertyName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddRUPProperty.add(lblRUPPropertyName, "split 2");

		JTextField textFieldRUPPropertyName = new JTextField();
		textFieldRUPPropertyName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddRUPProperty.add(textFieldRUPPropertyName, "growx, pushx, wrap");
		textFieldRUPPropertyName.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddRUPProperty,
				"Please enter Values for Roll-up property", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String rupPropertyName = textFieldRUPPropertyName.getText().toString().trim();
			String rupiri = comboBoxRUPIRI.getSelectedItem().toString().trim();

			rupiri = tBoxDefinition.getPrefixMap().get(rupiri);

			if (rupPropertyName.length() > 0) {
				tBoxExtraction.addRollUpProperty(rupiri, rupPropertyName);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Roll-up property");
			}
		}

		/*
		 * JButton btnSave = new JButton("Save"); btnSave.setFont(new Font("Tahoma",
		 * Font.BOLD, 16)); btnSave.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { String rupPropertyName
		 * = textFieldRUPPropertyName.getText().toString();
		 * 
		 * if (!commonMethods.checkHasNullString(rupPropertyName)) {
		 * 
		 * dwSchemaDefinition.getSchemaRollUpProperties()
		 * .add(comboBoxRUPIRI.getSelectedItem().toString().trim() + rupPropertyName);
		 * showSchema(); loadSchemaTree(); showUpdatePanel(); } else {
		 * JOptionPane.showMessageDialog(null,
		 * "Please provide valid values for adding Hierarchy Step."); } } });
		 * panelAddRUPProperty.add(btnSave, "");
		 * 
		 * panelComponent.add(panelAddRUPProperty, "grow, push");
		 */
	}

	protected void addMeaure() {
		// TODO Auto-generated method stub
		JPanel panelAddMeasure = new JPanel();
		panelAddMeasure.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddMeasure.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddMeasure.add(lblIri, "split 2");

		JComboBox comboBoxIRI = new JComboBox();
		comboBoxIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));

		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());

		comboBoxIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddMeasure.add(comboBoxIRI, "growx, pushx, wrap");

		JLabel lblMeasureName = new JLabel("Measure Name: ");
		lblMeasureName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddMeasure.add(lblMeasureName, "split 2");

		JTextField textFieldAtbrName = new JTextField();
		textFieldAtbrName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddMeasure.add(textFieldAtbrName, "growx, pushx, wrap");
		textFieldAtbrName.setColumns(10);

		JLabel lblMeasureLabel = new JLabel("Measure Label: ");
		lblMeasureLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddMeasure.add(lblMeasureLabel, "split 2");

		JTextField textFieldAtbrLabel = new JTextField();
		textFieldAtbrLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddMeasure.add(textFieldAtbrLabel, "growx, pushx, wrap");
		textFieldAtbrLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddMeasure.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddMeasure.add(comboBoxLanguage, "growx, pushx, wrap");

		JLabel lblMeasureRange = new JLabel("Measure Range: ");
		lblMeasureRange.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddMeasure.add(lblMeasureRange, "split 2");

		JComboBox comboBoxRange = new JComboBox();
		comboBoxRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> xsdPrefixes = tBoxDefinition.getXsdRanges();
		comboBoxRange.setModel(new DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		panelAddMeasure.add(comboBoxRange, "growx, pushx, wrap 30px");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddMeasure, "Please enter Values for Measure",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String leveliri = comboBoxIRI.getSelectedItem().toString();
			String levelName = textFieldAtbrName.getText().toString().trim();
			String levelLabel = textFieldAtbrLabel.getText().toString().trim();
			String lang = comboBoxLanguage.getSelectedItem().toString();
			String range = comboBoxRange.getSelectedItem().toString();

			leveliri = tBoxDefinition.getPrefixMap().get(leveliri);

			if (levelName.length() > 0 && levelLabel.length() > 0) {
				tBoxExtraction.addMeasureProperty(leveliri, levelName, levelLabel, lang, range);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Measure");
			}
		}
	}

	protected void addLevelAttribute() {
		// TODO Auto-generated method stub
		JPanel panelAddAttribute = new JPanel();
		panelAddAttribute.setLayout(new MigLayout("", "[800px, center, grow]", "[][][][][][]"));
		panelAddAttribute.setBackground(Color.WHITE);

		JLabel lblIri = new JLabel("Prefix: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddAttribute.add(lblIri, "split 2");

		JComboBox comboBoxIRI = new JComboBox();
		comboBoxIRI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> iriPrefixList = new ArrayList<>(tBoxDefinition.getPrefixMap().keySet());

		comboBoxIRI.setModel(new DefaultComboBoxModel<>(iriPrefixList.toArray()));
		panelAddAttribute.add(comboBoxIRI, "growx, pushx, wrap");

		JLabel lblAttributeName = new JLabel("Attribute Name: ");
		lblAttributeName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddAttribute.add(lblAttributeName, "split 2");

		JTextField textFieldAtbrName = new JTextField();
		textFieldAtbrName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddAttribute.add(textFieldAtbrName, "growx, pushx, wrap");
		textFieldAtbrName.setColumns(10);

		JLabel lblAttributeLabel = new JLabel("Attribute Label: ");
		lblAttributeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddAttribute.add(lblAttributeLabel, "split 2");

		JTextField textFieldAtbrLabel = new JTextField();
		textFieldAtbrLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelAddAttribute.add(textFieldAtbrLabel, "growx, pushx, wrap");
		textFieldAtbrLabel.setColumns(10);

		JLabel lblLanguage = new JLabel("Language: ");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddAttribute.add(lblLanguage, "split 2");

		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxLanguage.setModel(new DefaultComboBoxModel(new String[] { "English" }));
		panelAddAttribute.add(comboBoxLanguage, "growx, pushx, wrap");

		JLabel lblAttributeRange = new JLabel("Attribute Range: ");
		lblAttributeRange.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddAttribute.add(lblAttributeRange, "split 2");

		JComboBox comboBoxRange = new JComboBox();
		comboBoxRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ArrayList<String> xsdPrefixes = tBoxDefinition.getXsdRanges();
		comboBoxRange.setModel(new DefaultComboBoxModel<>(xsdPrefixes.toArray()));
		panelAddAttribute.add(comboBoxRange, "growx, pushx, wrap 30px");

		for (String string : tBoxDefinition.getLevelList()) {
			comboBoxRange.addItem(string);
		}

		for (String dataset : tBoxDefinition.getDataList()) {
			comboBoxRange.addItem(dataset);
		}

		for (String owlClass : tBoxDefinition.getClassList()) {
			comboBoxRange.addItem(owlClass);
		}

		int confirmation = JOptionPane.showConfirmDialog(null, panelAddAttribute,
				"Please enter Values for Level Attribute", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String leveliri = comboBoxIRI.getSelectedItem().toString();
			String levelName = textFieldAtbrName.getText().toString().trim();
			String levelLabel = textFieldAtbrLabel.getText().toString().trim();
			String lang = comboBoxLanguage.getSelectedItem().toString();
			String range = comboBoxRange.getSelectedItem().toString();

			leveliri = tBoxDefinition.getPrefixMap().get(leveliri);

			if (levelName.length() > 0 && levelLabel.length() > 0) {
				tBoxExtraction.addLevelAttribute(leveliri, levelName, levelLabel, lang, range);
				reloadAll();
			} else {
				JOptionPane.showMessageDialog(null, "Please provide valid values for adding new Level Attribute");
			}
		}
	}

	public void removeMDButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getMdMap().keySet()) {
			listModel.addElement(string);
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Delete property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (!annList.isSelectionEmpty()) {
				String key = annList.getSelectedValue().toString();
				if (tBoxDefinition.getMdMap().get(key).size() == 0) {
					tBoxDefinition.getMdMap().remove(key);
					refreshLists();
				} else {
					tBoxExtraction.removeProperty(getSelectedNode(), key);

					updateListValues();
					refreshTextArea();
					refreshLists();
				}
			}
		}
	}

	public void addMDButtonHandler() {
		// TODO Auto-generated method stub
		if (tBoxExtraction != null) {
			if (tBoxExtraction.getType(getSelectedNode()).equals("Class")
					|| tBoxExtraction.getType(getSelectedNode()).equals("ObjectProperty")
					|| tBoxExtraction.getType(getSelectedNode()).equals("DatatypeProperty")) {
				showAllMDTypes();
			} else {
				showAllMDProperties();
			}
		}
	}

	private void showAllMDProperties() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[][]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		JTextField textFieldAnnName = new JTextField();

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getAllMDProperties().keySet()) {
			listModel.addElement(string);
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				textFieldAnnName.setText(annList.getSelectedValue().toString());
			}
		});
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setVisible(false);
		panelAnn.add(lblName, "cell 0 1,alignx trailing");

		textFieldAnnName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(textFieldAnnName, "cell 1 1,growx");
		textFieldAnnName.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Add New property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (textFieldAnnName.getText().toString().trim().length() != 0) {
				String key = textFieldAnnName.getText().toString().trim();
				tBoxDefinition.getMdMap().put(key, new ArrayList<>());
				refreshLists();
			}
		}
	}

	private void showAllMDTypes() {
		// TODO Auto-generated method stub
		JPanel panelMD = new JPanel();
		panelMD.setBackground(Color.WHITE);
		panelMD.setLayout(new MigLayout("", "[][500px]", "[]"));

		JLabel lblProperty = new JLabel("Type: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMD.add(lblProperty, "cell 0 0");

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getAllMDType()) {
			listModel.addElement(string);
		}

		JList mdList = new JList(listModel);
		mdList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mdList.setBorder(new LineBorder(new Color(0, 0, 0)));
		mdList.setBackground(Color.WHITE);
		mdList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMD.add(mdList, "cell 1 0,grow");

		int confirmation = JOptionPane.showConfirmDialog(null, panelMD, "Select a type", JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (!mdList.isSelectionEmpty()) {
				String key = mdList.getSelectedValue().toString();
				if (key != null && !key.equals("")) {
					tBoxExtraction.addProperty(getSelectedNode(), key);

					reloadAll();
					updateListValues();
					refreshAll();
				}
			}
		}
	}

	public void removeDesButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getDescriptionMap().keySet()) {
			listModel.addElement(string);
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Delete property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (!annList.isSelectionEmpty()) {
				String key = annList.getSelectedValue().toString();

				if (tBoxDefinition.getDescriptionMap().get(key).size() == 0) {
					tBoxDefinition.getDescriptionMap().remove(key);
					refreshLists();
				} else {
					tBoxExtraction.removeProperty(getSelectedNode(), key);

					updateListValues();
					refreshTextArea();
					refreshLists();
				}
			}
		}
	}

	public void addDesButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[][]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		JTextField textFieldAnnName = new JTextField();

		DefaultListModel listModel = new DefaultListModel<>();
		if (tBoxExtraction.getType(getSelectedNode()).equals("Class")) {
			for (String string : tBoxDefinition.getAllClassProperties().keySet()) {
				listModel.addElement(string);
			}
		} else {
			for (String string : tBoxDefinition.getAllNativeProperties().keySet()) {
				listModel.addElement(string);
			}
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				textFieldAnnName.setText(annList.getSelectedValue().toString());
			}
		});
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setVisible(false);
		panelAnn.add(lblName, "cell 0 1,alignx trailing");

		textFieldAnnName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(textFieldAnnName, "cell 1 1,growx");
		textFieldAnnName.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Add new property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (textFieldAnnName.getText().toString().trim().length() != 0) {
				String key = textFieldAnnName.getText().toString().trim();
				tBoxDefinition.getDescriptionMap().put(key, new ArrayList<>());
				refreshLists();
			}
		}
	}

	public void removeAnnButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getAnnotationMap().keySet()) {
			listModel.addElement(string);
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Delete property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (!annList.isSelectionEmpty()) {
				String key = annList.getSelectedValue().toString();
				if (tBoxDefinition.getAnnotationMap().get(key).size() == 0) {
					tBoxDefinition.getAnnotationMap().remove(key);
					refreshLists();
				} else {
					tBoxExtraction.removeProperty(getSelectedNode(), key);

					updateListValues();
					refreshTextArea();
					refreshLists();
				}
			}
		}
	}

	public void addAnnButtonHandler() {
		// TODO Auto-generated method stub
		JPanel panelAnn = new JPanel();
		panelAnn.setBackground(Color.WHITE);
		panelAnn.setLayout(new MigLayout("", "[][500px]", "[][]"));

		JLabel lblProperty = new JLabel("Property: ");
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(lblProperty, "cell 0 0");

		JTextField textFieldAnnName = new JTextField();

		DefaultListModel listModel = new DefaultListModel<>();
		for (String string : tBoxDefinition.getAllAnnotationProperties().keySet()) {
			listModel.addElement(string);
		}

		JList annList = new JList(listModel);
		annList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				textFieldAnnName.setText(annList.getSelectedValue().toString());
			}
		});
		annList.setBorder(new LineBorder(new Color(0, 0, 0)));
		annList.setBackground(Color.WHITE);
		annList.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(annList, "cell 1 0,grow");

		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setVisible(false);
		panelAnn.add(lblName, "cell 0 1,alignx trailing");

		textFieldAnnName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAnn.add(textFieldAnnName, "cell 1 1,growx");
		textFieldAnnName.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panelAnn, "Add New property",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (textFieldAnnName.getText().toString().trim().length() != 0) {
				String key = textFieldAnnName.getText().toString().trim();
				tBoxDefinition.getAnnotationMap().put(key, new ArrayList<>());
				refreshLists();
			}
		}
	}

	public void addIRIHandler() {
		// TODO Auto-generated method stub
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[][500px]", "[][]"));

		JLabel lblPrefix = new JLabel("Prefix: ");
		lblPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblPrefix, "cell 0 0,alignx trailing");

		JTextField textFieldPrefix = new JTextField(prefix);
		textFieldPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(textFieldPrefix, "cell 1 0,growx");
		textFieldPrefix.setColumns(10);

		JLabel lblIri = new JLabel("IRI: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblIri, "cell 0 1,alignx trailing");

		JTextField textFieldIRI = new JTextField("http://www.");
		if (iri.trim().length() > 0) {
			textFieldIRI.setText(iri);
		}
		textFieldIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(textFieldIRI, "cell 1 1,growx");
		textFieldIRI.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panel, "Select a value", JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (textFieldIRI.getText().toString().trim().length() != 0
					&& textFieldPrefix.getText().toString().trim().length() != 0) {
				prefix = textFieldPrefix.getText().toString().trim();
				iri = textFieldIRI.getText().toString().trim();

				if (prefix.endsWith(":") && (iri.endsWith("/") || iri.endsWith("#"))) {
					tBoxDefinition.getPrefixMap().put(prefix, iri);

					if (tBoxExtraction == null) {
						// String prefixString =
						// tBoxDefinition.getPrefixStrings(tBoxDefinition.getPrefixMap());
						JOptionPane.showMessageDialog(null, "Create/Load a Tbox first", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						/*
						 * String prefixText = "@prefix " + prefix + " <" + iri + ">.\n"; String
						 * modelText = tBoxExtraction.getModelText("Turtle"); String finalText =
						 * prefixText + modelText; fileMethods.writeText(TEMP_TBOX, finalText);
						 * tBoxExtraction = new TBoxExtraction(TEMP_TBOX); initializeDefinition();
						 */

						tBoxExtraction.getPrefixMap().put(prefix, iri);

						/*
						 * fileMethods.writeText(TEMP_TBOX, tBoxExtraction.printAllComponents());
						 * tBoxExtraction = new TBoxExtraction(TEMP_TBOX);
						 */

						reloadAll();
						initializeDefinition();

						prefix = "";
						iri = "";
					}
				} else {
					JOptionPane.showMessageDialog(null, "Prefix must end with : and IRI must end with / or #", "Error",
							JOptionPane.ERROR_MESSAGE);
					treeIRI.setSelectionPath(null);
				}
			}
		}
	}

	public void editIRIHandler(String selectedResource) {
		// TODO Auto-generated method stub
		String selectedIri = tBoxDefinition.getPrefixMap().get(selectedResource);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[][500px]", "[][]"));

		JLabel lblPrefix = new JLabel("Prefix: ");
		lblPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblPrefix, "cell 0 0,alignx trailing");

		JTextField textFieldPrefix = new JTextField(selectedResource);
		textFieldPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(textFieldPrefix, "cell 1 0,growx");
		textFieldPrefix.setColumns(10);

		JLabel lblIri = new JLabel("IRI: ");
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblIri, "cell 0 1,alignx trailing");

		JTextField textFieldIRI = new JTextField(selectedIri);
		textFieldIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(textFieldIRI, "cell 1 1,growx");
		textFieldIRI.setColumns(10);

		int confirmation = JOptionPane.showConfirmDialog(null, panel, "Select a value", JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			String prefix = "", iri = "";
			if (textFieldIRI.getText().toString().trim().length() != 0
					&& textFieldPrefix.getText().toString().trim().length() != 0) {
				prefix = textFieldPrefix.getText().toString().trim();
				iri = textFieldIRI.getText().toString().trim();

				if (prefix.endsWith(":") && (iri.endsWith("/") || iri.endsWith("#"))) {
					tBoxExtraction.getPrefixMap().remove(selectedResource);
					tBoxExtraction.getPrefixMap().put(prefix, iri);
					reloadAll();
				} else {
					JOptionPane.showMessageDialog(null, "Prefix must end with : and IRI must end with / or #", "Error",
							JOptionPane.ERROR_MESSAGE);
					treeIRI.setSelectionPath(null);
				}
			}
		}
	}

	public void removeResource(String name) {
		// TODO Auto-generated method stub
		tBoxExtraction.removeResource(name);
		reloadAll();
	}

	public String addNodeToTree(JTree tree, String type) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (parent == null) {
			JOptionPane.showMessageDialog(null, "Select a parent to add a class", "Error", JOptionPane.ERROR_MESSAGE);

			return null;
		}
		String name = JOptionPane.showInputDialog(null, "Enter " + type + " Name: ");
		if ((name.startsWith("http://") && !name.endsWith("/") && !name.endsWith("#") && name != null)
				|| name.contains(":")) {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.insertNodeInto(new DefaultMutableTreeNode(name), parent, parent.getChildCount());

			return name;
		} else {
			JOptionPane.showMessageDialog(null, "Name must start with http:// and must include name at the end",
					"Error", JOptionPane.ERROR_MESSAGE);

			return null;
		}
	}

	public void addResource(String type, String name) {
		// TODO Auto-generated method stub
		if (type.equals("class")) {
			if (!tBoxExtraction.addClassResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("object")) {
			if (!tBoxExtraction.addObjectResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("data")) {
			if (!tBoxExtraction.addDataResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("dataStructure")) {
			if (!tBoxExtraction.addDataStructureResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("level")) {
			if (!tBoxExtraction.addLevelResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("Dataset")) {
			if (!tBoxExtraction.addDatasetResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("Measure")) {
			if (!tBoxExtraction.addMeasureResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("Explorer")) {
			if (!tBoxExtraction.addHierarchyResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("Dimension")) {
			if (!tBoxExtraction.addDimensionResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("rollUp")) {
			if (!tBoxExtraction.addRollUpResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else if (type.equals("Attribute")) {
			if (!tBoxExtraction.addAttributeResource(name)) {
				JOptionPane.showMessageDialog(null, "Resource exists");
			}
		} else {
			System.out.println("Unknown resource type");
		}

		reloadAll();
	}

	public void updateResource(String previousResource, String currentResource) {
		// TODO Auto-generated method stub
		tBoxExtraction.updateResource(previousResource, currentResource);
		reloadAll();
	}

	public String listEditButtonHandler(String resource) {
		// TODO Auto-generated method stub
		JPanel panelTree = new JPanel();
		panelTree.setLayout(new MigLayout("", "[800px]", "[500px][]"));

		JTextField textFieldValue = new JTextField(resource);

		JTabbedPane tabbedPaneTree = new JTabbedPane(JTabbedPane.TOP);
		panelTree.add(tabbedPaneTree, "cell 0 0,grow");

		JTabbedPane tabbedPaneConcept = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Concept Constructs", null, tabbedPaneConcept, null);

		JPanel panelClass = new JPanel();
		panelClass.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Concepts", null, panelClass, null);
		panelClass.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneClass = new JScrollPane();
		panelClass.add(scrollPaneClass, "cell 0 0,grow");

		JTree treeClass = new JTree();
		treeClass.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeClass, textFieldValue);
			}
		});
		treeClass.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneClass.setViewportView(treeClass);

		JPanel panelObject = new JPanel();
		panelObject.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Object Properties", null, panelObject, null);
		panelObject.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneObject = new JScrollPane();
		panelObject.add(scrollPaneObject, "cell 0 0,grow");

		JTree treeObject = new JTree();
		treeObject.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeObject, textFieldValue);
			}
		});
		treeObject.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneObject.setViewportView(treeObject);

		JPanel panelData = new JPanel();
		panelData.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Datatype Properties", null, panelData, null);
		panelData.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneData = new JScrollPane();
		panelData.add(scrollPaneData, "cell 0 0,grow");

		JTree treeData = new JTree();
		treeData.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeData, textFieldValue);
			}
		});
		treeData.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneData.setViewportView(treeData);

		JTabbedPane tabbedPaneDW = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Data Warehouse Constructs", null, tabbedPaneDW, null);

		JPanel panelDatasets = new JPanel();
		panelDatasets.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Datasets", null, panelDatasets, null);
		panelDatasets.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneDataset = new JScrollPane();
		panelDatasets.add(scrollPaneDataset, "cell 0 0,grow");

		JTree treeDataset = new JTree();
		treeDataset.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeDataset, textFieldValue);
			}
		});
		treeDataset.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneDataset.setViewportView(treeDataset);

		JPanel panelDimensions = new JPanel();
		panelDimensions.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Dimensions", null, panelDimensions, null);
		panelDimensions.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneDimension = new JScrollPane();
		panelDimensions.add(scrollPaneDimension, "cell 0 0,grow");

		JTree treeDimension = new JTree();
		treeDimension.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeDimension, textFieldValue);
			}
		});
		treeDimension.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneDimension.setViewportView(treeDimension);

		JPanel panelHierarchies = new JPanel();
		panelHierarchies.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Hierarchies", null, panelHierarchies, null);
		panelHierarchies.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneHierarchy = new JScrollPane();
		panelHierarchies.add(scrollPaneHierarchy, "cell 0 0,grow");

		JTree treeHierarchy = new JTree();
		treeHierarchy.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeHierarchy, textFieldValue);
			}
		});
		treeHierarchy.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneHierarchy.setViewportView(treeHierarchy);

		JPanel panelHierarchySteps = new JPanel();
		panelHierarchySteps.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Hierarchy Steps", null, panelHierarchySteps, null);
		panelHierarchySteps.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneHierarchyStep = new JScrollPane();
		panelHierarchySteps.add(scrollPaneHierarchyStep, "cell 0 0,grow");

		JTree treeHierarchyStep = new JTree();
		treeHierarchyStep.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeHierarchyStep, textFieldValue);
			}
		});
		treeHierarchyStep.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneHierarchyStep.setViewportView(treeHierarchyStep);

		JPanel panelLevel = new JPanel();
		panelLevel.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Level Properties", null, panelLevel, null);
		panelLevel.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneLevel = new JScrollPane();
		panelLevel.add(scrollPaneLevel, "cell 0 0,grow");

		JTree treeLevel = new JTree();
		treeLevel.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeLevel, textFieldValue);
			}
		});
		treeLevel.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneLevel.setViewportView(treeLevel);

		JPanel panelLevelAttribute = new JPanel();
		panelLevelAttribute.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Level Attributes", null, panelLevelAttribute, null);
		panelLevelAttribute.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneAttribute = new JScrollPane();
		panelLevelAttribute.add(scrollPaneAttribute, "cell 0 0,grow");

		JTree treeAttribute = new JTree();
		treeAttribute.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeAttribute, textFieldValue);
			}
		});
		treeAttribute.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneAttribute.setViewportView(treeAttribute);

		JPanel panelRollUps = new JPanel();
		panelRollUps.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Roll-up Properties", null, panelRollUps, null);
		panelRollUps.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneRollup = new JScrollPane();
		panelRollUps.add(scrollPaneRollup, "cell 0 0,grow");

		JTree treeRollUp = new JTree();
		treeRollUp.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeRollUp, textFieldValue);
			}
		});
		treeRollUp.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneRollup.setViewportView(treeRollUp);

		JPanel panelMeasure = new JPanel();
		panelMeasure.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Measures", null, panelMeasure, null);
		panelMeasure.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneMeasure = new JScrollPane();
		panelMeasure.add(scrollPaneMeasure, "cell 0 0,grow");

		JTree treeMeasure = new JTree();
		treeMeasure.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeMeasure, textFieldValue);
			}
		});
		treeMeasure.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneMeasure.setViewportView(treeMeasure);

		JPanel panelCardinality = new JPanel();
		panelCardinality.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Cardinalities", null, panelCardinality, null);
		panelCardinality.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneCardinality = new JScrollPane();
		panelCardinality.add(scrollPaneCardinality, "cell 0 0,grow");

		JTree treeCardinality = new JTree();
		setSelection(treeCardinality);
		treeCardinality.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeCardinality;
				setSelectedText(treeCardinality, textFieldValue);
			}
		});
		treeCardinality.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneCardinality.setViewportView(treeCardinality);

		JPanel panelRange = new JPanel();
		panelRange.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Datatypes", null, panelRange, null);
		panelRange.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneRange = new JScrollPane();
		panelRange.add(scrollPaneRange, "cell 0 0,grow");

		JTree treeRange = new JTree();
		setSelection(treeRange);
		treeRange.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeRange;
				setSelectedText(treeRange, textFieldValue);
			}
		});
		treeRange.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneRange.setViewportView(treeRange);

		JPanel panelPrefix = new JPanel();
		panelPrefix.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Prefixes", null, panelPrefix, null);
		panelPrefix.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPanePrefix = new JScrollPane();
		panelPrefix.add(scrollPanePrefix, "cell 0 0,grow");

		JTree treePrefix = new JTree();
		setSelection(treePrefix);
		treePrefix.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treePrefix;
				setSelectedText(treePrefix, textFieldValue);
			}
		});
		treePrefix.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPanePrefix.setViewportView(treePrefix);

		JPanel panelOntology = new JPanel();
		panelOntology.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Ontology", null, panelOntology, null);
		panelOntology.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneOntology = new JScrollPane();
		panelOntology.add(scrollPaneOntology, "cell 0 0,grow");

		JTree treeOntology = new JTree();
		treeOntology.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeOntology, textFieldValue);
			}
		});
		treeOntology.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneOntology.setViewportView(treeOntology);

		textFieldValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTree.add(textFieldValue, "cell 0 1,growx");
		textFieldValue.setColumns(10);

		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("owl:Thing");

		for (String string : tBoxDefinition.getClassList()) {
			classNode.add(new DefaultMutableTreeNode(string));
		}

		treeClass.setModel(new DefaultTreeModel(classNode));

		// Adding all object properties to the tree
		DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode("owl:topObjectProperty");

		for (String string : tBoxDefinition.getObjectList()) {
			objectNode.add(new DefaultMutableTreeNode(string));
		}

		treeObject.setModel(new DefaultTreeModel(objectNode));

		// Adding all data properties to the tree
		DefaultMutableTreeNode dataPropNode = new DefaultMutableTreeNode("owl:topDataProperty");

		for (String string : tBoxDefinition.getDataList()) {
			dataPropNode.add(new DefaultMutableTreeNode(string));
		}

		treeData.setModel(new DefaultTreeModel(dataPropNode));

		// Adding all prefixes to the tree
		DefaultMutableTreeNode prefixNode = new DefaultMutableTreeNode("Prefix");

		for (String string : tBoxDefinition.getPrefixMap().keySet()) {
			prefixNode.add(new DefaultMutableTreeNode(string));
		}

		treePrefix.setModel(new DefaultTreeModel(prefixNode));

		// Adding all hierarchyStepNode to the tree
		DefaultMutableTreeNode hierarchyStepNode = new DefaultMutableTreeNode("HierarchyStep");

		for (String string : tBoxDefinition.getHierarchyStepsList().keySet()) {
			hierarchyStepNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchyStep.setModel(new DefaultTreeModel(hierarchyStepNode));

		DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode("Ontology");

		for (String string : tBoxDefinition.getOntologyList()) {
			ontologyNode.add(new DefaultMutableTreeNode(string));
		}

		treeOntology.setModel(new DefaultTreeModel(ontologyNode));

		// Adding all levels to the tree
		DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level");

		for (String string : tBoxDefinition.getLevelList()) {
			levelNode.add(new DefaultMutableTreeNode(string));
		}

		treeLevel.setModel(new DefaultTreeModel(levelNode));

		// Adding all attributes to the tree
		DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode("Attribute");

		for (String string : tBoxDefinition.getAttributeList()) {
			attributeNode.add(new DefaultMutableTreeNode(string));
		}

		treeAttribute.setModel(new DefaultTreeModel(attributeNode));

		// Adding all dimensions to the tree
		DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode("Dimension");

		for (String string : tBoxDefinition.getDimensionList()) {
			dimensionNode.add(new DefaultMutableTreeNode(string));
		}

		treeDimension.setModel(new DefaultTreeModel(dimensionNode));

		// Adding all hierarchies to the tree
		DefaultMutableTreeNode hierarchyNode = new DefaultMutableTreeNode("Explorer");

		for (String string : tBoxDefinition.getHierarchyList()) {
			hierarchyNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchy.setModel(new DefaultTreeModel(hierarchyNode));

		// Adding all measures to the tree
		DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode("Measure");

		for (String string : tBoxDefinition.getMeasureList()) {
			measureNode.add(new DefaultMutableTreeNode(string));
		}

		treeMeasure.setModel(new DefaultTreeModel(measureNode));

		// Adding all roll up properties to the tree
		DefaultMutableTreeNode rollUpNode = new DefaultMutableTreeNode("Roll Up Properties");

		for (String string : tBoxDefinition.getRollupList()) {
			rollUpNode.add(new DefaultMutableTreeNode(string));
		}

		treeRollUp.setModel(new DefaultTreeModel(rollUpNode));

		// Adding all data sets to the tree
		DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode("Dataset");

		for (String string : tBoxDefinition.getDatasetList()) {
			datasetNode.add(new DefaultMutableTreeNode(string));
		}

		treeDataset.setModel(new DefaultTreeModel(datasetNode));

		// Adding all xsd ranges to the tree
		DefaultMutableTreeNode cardinalityNode = new DefaultMutableTreeNode("Cardinalities");

		for (String string : tBoxDefinition.getCardinalities()) {
			cardinalityNode.add(new DefaultMutableTreeNode(string));
		}

		treeCardinality.setModel(new DefaultTreeModel(cardinalityNode));

		// Adding all xsd ranges to the tree
		DefaultMutableTreeNode rangeNode = new DefaultMutableTreeNode("Ranges");

		for (String string : tBoxDefinition.getXsdRanges()) {
			rangeNode.add(new DefaultMutableTreeNode(string));
		}

		treeRange.setModel(new DefaultTreeModel(rangeNode));

		int confirmation = JOptionPane.showConfirmDialog(null, panelTree, "Please enter correct values of Attribute.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			return textFieldValue.getText().toString().trim();
		}

		return null;
	}

	public TreePath[] listAddButtonHandler() {
		// TODO Auto-generated method stub
		selectedTree = null;

		JPanel panelTree = new JPanel();
		panelTree.setLayout(new MigLayout("", "[800px]", "[500px][]"));

		JTextField textFieldValue = new JTextField();

		JTabbedPane tabbedPaneTree = new JTabbedPane(JTabbedPane.TOP);
		panelTree.add(tabbedPaneTree, "cell 0 0,grow");

		JTabbedPane tabbedPaneConcept = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Owl Constructs", null, tabbedPaneConcept, null);

		JPanel panelClass = new JPanel();
		panelClass.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Concepts", null, panelClass, null);
		panelClass.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneClass = new JScrollPane();
		panelClass.add(scrollPaneClass, "cell 0 0,grow");

		JTree treeClass = new JTree();
		setSelection(treeClass);
		treeClass.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeClass;

				setSelectedText(treeClass, textFieldValue);
			}
		});
		treeClass.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneClass.setViewportView(treeClass);

		JPanel panelObject = new JPanel();
		panelObject.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Object Properties", null, panelObject, null);
		panelObject.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneObject = new JScrollPane();
		panelObject.add(scrollPaneObject, "cell 0 0,grow");

		JTree treeObject = new JTree();
		setSelection(treeObject);
		treeObject.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeObject;

				setSelectedText(treeObject, textFieldValue);
			}
		});
		treeObject.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneObject.setViewportView(treeObject);

		JPanel panelData = new JPanel();
		panelData.setBackground(Color.WHITE);
		tabbedPaneConcept.addTab("Datatype Properties", null, panelData, null);
		panelData.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneData = new JScrollPane();
		panelData.add(scrollPaneData, "cell 0 0,grow");

		JTree treeData = new JTree();
		setSelection(treeData);
		treeData.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeData;

				setSelectedText(treeData, textFieldValue);
			}
		});
		treeData.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneData.setViewportView(treeData);

		JTabbedPane tabbedPaneDW = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTree.addTab("Data Warehouse Constructs", null, tabbedPaneDW, null);

		JPanel panelDatasets = new JPanel();
		panelDatasets.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Datasets", null, panelDatasets, null);
		panelDatasets.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneDataset = new JScrollPane();
		panelDatasets.add(scrollPaneDataset, "cell 0 0,grow");

		JTree treeDataset = new JTree();
		setSelection(treeDataset);
		treeDataset.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeDataset;

				setSelectedText(treeDataset, textFieldValue);
			}
		});
		treeDataset.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneDataset.setViewportView(treeDataset);

		JPanel panelDimensions = new JPanel();
		panelDimensions.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Dimensions", null, panelDimensions, null);
		panelDimensions.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneDimension = new JScrollPane();
		panelDimensions.add(scrollPaneDimension, "cell 0 0,grow");

		JTree treeDimension = new JTree();
		setSelection(treeDimension);
		treeDimension.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeDimension;

				setSelectedText(treeDimension, textFieldValue);
			}
		});
		treeDimension.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneDimension.setViewportView(treeDimension);

		JPanel panelHierarchies = new JPanel();
		panelHierarchies.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Hierarchies", null, panelHierarchies, null);
		panelHierarchies.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneHierarchy = new JScrollPane();
		panelHierarchies.add(scrollPaneHierarchy, "cell 0 0,grow");

		JTree treeHierarchy = new JTree();
		setSelection(treeHierarchy);
		treeHierarchy.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeHierarchy;
				setSelectedText(treeHierarchy, textFieldValue);
			}
		});
		treeHierarchy.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneHierarchy.setViewportView(treeHierarchy);

		JPanel panelHierarchySteps = new JPanel();
		panelHierarchySteps.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Hierarchy Steps", null, panelHierarchySteps, null);
		panelHierarchySteps.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneHierarchyStep = new JScrollPane();
		panelHierarchySteps.add(scrollPaneHierarchyStep, "cell 0 0,grow");

		JTree treeHierarchyStep = new JTree();
		setSelection(treeHierarchyStep);
		treeHierarchyStep.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeHierarchyStep;
				setSelectedText(treeHierarchyStep, textFieldValue);
			}
		});
		treeHierarchyStep.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneHierarchyStep.setViewportView(treeHierarchyStep);

		JPanel panelLevel = new JPanel();
		panelLevel.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Level Properties", null, panelLevel, null);
		panelLevel.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneLevel = new JScrollPane();
		panelLevel.add(scrollPaneLevel, "cell 0 0,grow");

		JTree treeLevel = new JTree();
		setSelection(treeLevel);
		treeLevel.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeLevel;
				setSelectedText(treeLevel, textFieldValue);
			}
		});
		treeLevel.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneLevel.setViewportView(treeLevel);

		JPanel panelLevelAttribute = new JPanel();
		panelLevelAttribute.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Level Attributes", null, panelLevelAttribute, null);
		panelLevelAttribute.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneAttribute = new JScrollPane();
		panelLevelAttribute.add(scrollPaneAttribute, "cell 0 0,grow");

		JTree treeAttribute = new JTree();
		setSelection(treeAttribute);
		treeAttribute.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeAttribute;
				setSelectedText(treeAttribute, textFieldValue);
			}
		});
		treeAttribute.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneAttribute.setViewportView(treeAttribute);

		JPanel panelRollUps = new JPanel();
		panelRollUps.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Roll-up Properties", null, panelRollUps, null);
		panelRollUps.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneRollup = new JScrollPane();
		panelRollUps.add(scrollPaneRollup, "cell 0 0,grow");

		JTree treeRollUp = new JTree();
		setSelection(treeRollUp);
		treeRollUp.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeRollUp;
				setSelectedText(treeRollUp, textFieldValue);
			}
		});
		treeRollUp.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneRollup.setViewportView(treeRollUp);

		JPanel panelMeasure = new JPanel();
		panelMeasure.setBackground(Color.WHITE);
		tabbedPaneDW.addTab("Measures", null, panelMeasure, null);
		panelMeasure.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneMeasure = new JScrollPane();
		panelMeasure.add(scrollPaneMeasure, "cell 0 0,grow");

		JTree treeMeasure = new JTree();
		setSelection(treeMeasure);
		treeMeasure.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeMeasure;
				setSelectedText(treeMeasure, textFieldValue);
			}
		});
		treeMeasure.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneMeasure.setViewportView(treeMeasure);

		JPanel panelCardinality = new JPanel();
		panelCardinality.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Cardinalities", null, panelCardinality, null);
		panelCardinality.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneCardinality = new JScrollPane();
		panelCardinality.add(scrollPaneCardinality, "cell 0 0,grow");

		JTree treeCardinality = new JTree();
		setSelection(treeCardinality);
		treeCardinality.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeCardinality;
				setSelectedText(treeCardinality, textFieldValue);
			}
		});
		treeCardinality.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneCardinality.setViewportView(treeCardinality);

		JPanel panelRange = new JPanel();
		panelRange.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Datatypes", null, panelRange, null);
		panelRange.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneRange = new JScrollPane();
		panelRange.add(scrollPaneRange, "cell 0 0,grow");

		JTree treeRange = new JTree();
		setSelection(treeRange);
		treeRange.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treeRange;
				setSelectedText(treeRange, textFieldValue);
			}
		});
		treeRange.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneRange.setViewportView(treeRange);

		JPanel panelPrefix = new JPanel();
		panelPrefix.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Prefixes", null, panelPrefix, null);
		panelPrefix.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPanePrefix = new JScrollPane();
		panelPrefix.add(scrollPanePrefix, "cell 0 0,grow");

		JTree treePrefix = new JTree();
		setSelection(treePrefix);
		treePrefix.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				selectedTree = treePrefix;
				setSelectedText(treePrefix, textFieldValue);
			}
		});
		treePrefix.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPanePrefix.setViewportView(treePrefix);

		JPanel panelOntology = new JPanel();
		panelOntology.setBackground(Color.WHITE);
		tabbedPaneTree.addTab("Ontology", null, panelOntology, null);
		panelOntology.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPaneOntology = new JScrollPane();
		panelOntology.add(scrollPaneOntology, "cell 0 0,grow");

		JTree treeOntology = new JTree();
		treeOntology.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setSelectedText(treeOntology, textFieldValue);
			}
		});
		treeOntology.setFont(new Font("Tahoma", Font.BOLD, 11));
		scrollPaneOntology.setViewportView(treeOntology);

		textFieldValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTree.add(textFieldValue, "cell 0 1,growx");
		textFieldValue.setColumns(10);

		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("owl:Thing");

		for (String string : tBoxDefinition.getClassList()) {
			classNode.add(new DefaultMutableTreeNode(string));
		}

		treeClass.setModel(new DefaultTreeModel(classNode));

		// Adding all object properties to the tree
		DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode("owl:topObjectProperty");

		for (String string : tBoxDefinition.getObjectList()) {
			objectNode.add(new DefaultMutableTreeNode(string));
		}

		treeObject.setModel(new DefaultTreeModel(objectNode));

		// Adding all data properties to the tree
		DefaultMutableTreeNode dataPropNode = new DefaultMutableTreeNode("owl:topDataProperty");

		for (String string : tBoxDefinition.getDataList()) {
			dataPropNode.add(new DefaultMutableTreeNode(string));
		}

		treeData.setModel(new DefaultTreeModel(dataPropNode));

		// Adding all prefixes to the tree
		DefaultMutableTreeNode prefixNode = new DefaultMutableTreeNode("Prefix");

		for (String string : tBoxDefinition.getPrefixMap().keySet()) {
			prefixNode.add(new DefaultMutableTreeNode(string));
		}

		treePrefix.setModel(new DefaultTreeModel(prefixNode));

		// Adding all hierarchyStepNode to the tree
		DefaultMutableTreeNode hierarchyStepNode = new DefaultMutableTreeNode("HierarchyStep");

		for (String string : tBoxDefinition.getHierarchyStepsList().keySet()) {
			hierarchyStepNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchyStep.setModel(new DefaultTreeModel(hierarchyStepNode));

		DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode("Ontology");

		for (String string : tBoxDefinition.getOntologyList()) {
			ontologyNode.add(new DefaultMutableTreeNode(string));
		}

		treeOntology.setModel(new DefaultTreeModel(ontologyNode));

		// Adding all levels to the tree
		DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level");

		for (String string : tBoxDefinition.getLevelList()) {
			levelNode.add(new DefaultMutableTreeNode(string));
		}

		treeLevel.setModel(new DefaultTreeModel(levelNode));

		// Adding all attributes to the tree
		DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode("Attribute");

		for (String string : tBoxDefinition.getAttributeList()) {
			attributeNode.add(new DefaultMutableTreeNode(string));
		}

		treeAttribute.setModel(new DefaultTreeModel(attributeNode));

		// Adding all dimensions to the tree
		DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode("Dimension");

		for (String string : tBoxDefinition.getDimensionList()) {
			dimensionNode.add(new DefaultMutableTreeNode(string));
		}

		treeDimension.setModel(new DefaultTreeModel(dimensionNode));

		// Adding all hierarchies to the tree
		DefaultMutableTreeNode hierarchyNode = new DefaultMutableTreeNode("Explorer");

		for (String string : tBoxDefinition.getHierarchyList()) {
			hierarchyNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchy.setModel(new DefaultTreeModel(hierarchyNode));

		// Adding all measures to the tree
		DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode("Measure");

		for (String string : tBoxDefinition.getMeasureList()) {
			measureNode.add(new DefaultMutableTreeNode(string));
		}

		treeMeasure.setModel(new DefaultTreeModel(measureNode));

		// Adding all roll up properties to the tree
		DefaultMutableTreeNode rollUpNode = new DefaultMutableTreeNode("Roll Up Properties");

		for (String string : tBoxDefinition.getRollupList()) {
			rollUpNode.add(new DefaultMutableTreeNode(string));
		}

		treeRollUp.setModel(new DefaultTreeModel(rollUpNode));

		// Adding all data sets to the tree
		DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode("Dataset");

		for (String string : tBoxDefinition.getDatasetList()) {
			datasetNode.add(new DefaultMutableTreeNode(string));
		}

		treeDataset.setModel(new DefaultTreeModel(datasetNode));

		// Adding all xsd ranges to the tree
		DefaultMutableTreeNode cardinalityNode = new DefaultMutableTreeNode("Cardinalities");

		for (String string : tBoxDefinition.getCardinalities()) {
			cardinalityNode.add(new DefaultMutableTreeNode(string));
		}

		treeCardinality.setModel(new DefaultTreeModel(cardinalityNode));

		// Adding all xsd ranges to the tree
		DefaultMutableTreeNode rangeNode = new DefaultMutableTreeNode("Ranges");

		for (String string : tBoxDefinition.getXsdRanges()) {
			rangeNode.add(new DefaultMutableTreeNode(string));
		}

		treeRange.setModel(new DefaultTreeModel(rangeNode));

		int confirmation = JOptionPane.showConfirmDialog(null, panelTree, "Please enter new value of Attribute.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			if (selectedTree != null) {
				TreePath[] paths = selectedTree.getSelectionPaths();

				return paths;
			}
		}

		return null;
	}

	private void setSelection(JTree tree) {
		// TODO Auto-generated method stub
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
	}

	public void setSelectedText(JTree tree, JTextField textField) {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		String selectedResource = "";
		if (selectedNode != null) {
			selectedResource = selectedNode.toString();
			textField.setText(selectedResource);
		}
	}

	public void saveButtonHandler() {
		// TODO Auto-generated method stub
		if (fileMethods.getFilePath().trim().length() > 0) {
			int confirmation = JOptionPane.showConfirmDialog(null, "Do you want to replace the current file");

			if (confirmation == JOptionPane.OK_OPTION) {
				String[] parts = fileMethods.getFilePath().split("\\.");
				if (parts.length == 2) {
					String extension = parts[1];
					String type = fileMethods.getAllTypes().get(extension);

					fileMethods.promtToSaveFile(tBoxExtraction.printAllComponents(), type, fileMethods.getFilePath());
				} else {
					JOptionPane.showMessageDialog(null, "Check the name/path of file");
				}
			} else {
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setLayout(new MigLayout("", "[][grow]", "[]"));

				JLabel lblFileType = new JLabel("File Type: ");
				lblFileType.setFont(new Font("Tahoma", Font.BOLD, 12));
				panel.add(lblFileType, "cell 0 0,alignx trailing");

				JComboBox comboBoxFileType = new JComboBox(fileMethods.getAllFileTypes().keySet().toArray());
				comboBoxFileType.setFont(new Font("Tahoma", Font.BOLD, 12));
				comboBoxFileType.setBackground(Color.WHITE);
				panel.add(comboBoxFileType, "cell 1 0,growx");

				int confirmation2 = JOptionPane.showConfirmDialog(null, panel, "Please correct values of Attribute.",
						JOptionPane.OK_CANCEL_OPTION);

				if (confirmation2 == JOptionPane.OK_OPTION) {
					String key = (String) comboBoxFileType.getSelectedItem();
					String extension = fileMethods.getAllFileTypes().get(key);
					fileMethods.promtToSaveFile(tBoxExtraction.printAllComponents(), "tbox", key, extension);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Create/Load a TBox First");
		}
	}

	public void newButtonHandler() {
		// TODO Auto-generated method stub
		initializeAll();

		JPanel panelNew = new JPanel();
		panelNew.setLayout(new MigLayout("", "[][600px][]", "[][][]"));

		JLabel lblType = new JLabel("Type: ");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelNew.add(lblType, "cell 0 0,alignx trailing");

		JComboBox comboBoxType = new JComboBox(fileMethods.getAllFileTypes().keySet().toArray());
		comboBoxType.setBackground(Color.WHITE);
		panelNew.add(comboBoxType, "cell 1 0 2 1,growx");

		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelNew.add(lblName, "cell 0 1,alignx trailing");

		JTextField textFieldFileName = new JTextField();
		textFieldFileName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelNew.add(textFieldFileName, "cell 1 1 2 1,growx");
		textFieldFileName.setColumns(10);

		JLabel lblDirectory = new JLabel("Directory: ");
		lblDirectory.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelNew.add(lblDirectory, "cell 0 2");

		JLabel lblDirectoryPath = new JLabel("None");
		lblDirectoryPath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panelNew.add(lblDirectoryPath, "cell 1 2,growx");

		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fileMethods.chooseDirectory();
				lblDirectoryPath.setText(fileMethods.getDirectory());
			}
		});
		btnSelect.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelNew.add(btnSelect, "cell 2 2");

		int confirmation = JOptionPane.showConfirmDialog(null, panelNew, "New File", JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.OK_OPTION) {
			if (textFieldFileName.getText().toString().trim().length() != 0) {
				String key = (String) comboBoxType.getSelectedItem();
				String extension = fileMethods.getAllFileTypes().get(key);

				if (lblDirectoryPath.getText().toString().trim().equals("None")) {
					fileMethods.setFilePath(textFieldFileName.getText().toString().trim() + extension);
					fileMethods.setFileName(textFieldFileName.getText().toString().trim() + extension);
				} else {
					fileMethods.setFilePath(lblDirectoryPath.getText().toString().trim() + "\\"
							+ textFieldFileName.getText().toString().trim() + extension);
					fileMethods.setFileName(textFieldFileName.getText().toString().trim() + extension);
				}

				lblFilePath.setText(fileMethods.getFilePath());

				fileMethods.createFileInDirectory();

				tBoxExtraction = new TBoxExtraction(fileMethods.getFilePath());
				initializeDefinition();
			}
		}
	}

	public void openButtonHandler() {
		// TODO Auto-generated method stub
		fileMethods.chooseFile();
		String filePath = fileMethods.getFilePath();
		lblFilePath.setText(filePath);

		textAreaTBox.setText("");

		if (filePath != null) {
			tBoxExtraction = new TBoxExtraction(filePath);
			initializeDefinition();
		} else {
			JOptionPane.showMessageDialog(null, "No selection");
		}
	}

	private void reloadAll() {
		// TODO Auto-generated method stub
		tBoxExtraction.reloadAll();
		initializeDefinition();
	}

	public void refreshAll() {
		// TODO Auto-generated method stub
		refreshTextArea();
		refreshLists();
		refreshAllTree();
	}

	public void refreshLists() {
		// TODO Auto-generated method stub
		panelAnnotation.removeAll();
		panelDescription.removeAll();
		panelMultiDimension.removeAll();

		panelAnnotation.repaint();
		panelAnnotation.revalidate();
		panelDescription.repaint();
		panelDescription.revalidate();
		panelMultiDimension.repaint();
		panelMultiDimension.revalidate();

		panelAnnotation.setLayout(new GridLayout(tBoxDefinition.getAnnotationMap().size(), 1, 0, 0));
		panelDescription.setLayout(new GridLayout(tBoxDefinition.getDescriptionMap().size(), 1, 0, 0));
		panelMultiDimension.setLayout(new GridLayout(tBoxDefinition.getMdMap().size(), 1, 0, 0));

		for (Map.Entry<String, ArrayList<String>> map : tBoxDefinition.getAnnotationMap().entrySet()) {
			String key = map.getKey();
			ArrayList<String> list = map.getValue();

			JPanel panelHolder = new JPanel();
			panelHolder.setBackground(Color.WHITE);
			panelHolder.setLayout(new MigLayout("", "[grow]", "[][]"));

			JLabel lblKey = new JLabel(key);
			lblKey.setForeground(Color.BLUE);
			panelHolder.add(lblKey, "flowx,cell 0 0,growx,pushx");

			DefaultListModel listModel = new DefaultListModel<>();
			for (String string : list) {
				listModel.addElement(string);
			}
			JList valueList = new JList(listModel);
			panelHolder.add(valueList, "cell 0 1,grow");

			JButton buttonAdd = new JButton("+");
			buttonAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					TreePath[] paths = listAddButtonHandler();

					if (paths != null) {
						if (paths.length > 0) {
							for (TreePath path : paths) {
								if (getSelectedNode().contains("_:")) {
									String resource = "";

									if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
									} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
									} else {
										resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
									}

									tBoxExtraction.addProperty(resource, key, path.getLastPathComponent().toString(),
											false);
								} else {
									tBoxExtraction.addProperty(getSelectedNode(), key,
											path.getLastPathComponent().toString());
								}
							}
						}

						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							updateListValues();
							refreshTextArea();
							refreshLists();
						}
					}
				}
			});
			buttonAdd.setToolTipText("Add");
			buttonAdd.setBackground(Color.WHITE);
			panelHolder.add(buttonAdd, "cell 0 0");

			JButton buttonRemove = new JButton("-");
			buttonRemove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {

						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							tBoxExtraction.removeProperty(resource, key, (String) valueList.getSelectedValue(), false);

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							tBoxExtraction.removeProperty(getSelectedNode(), key,
									(String) valueList.getSelectedValue());

							updateListValues();
							refreshTextArea();
							refreshLists();
						}

					}
				}
			});
			buttonRemove.setToolTipText("Remove");
			buttonRemove.setBackground(Color.WHITE);
			panelHolder.add(buttonRemove, "cell 0 0");

			JButton btnEdit = new JButton("u");
			btnEdit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {
						String previousValue = (String) valueList.getSelectedValue();
						String value = listEditButtonHandler(previousValue);
						if (value != null) {

							if (getSelectedNode().contains("_:")) {
								String resource = "";

								if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
								} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
								} else {
									resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
								}

								tBoxExtraction.editProperty(resource, key, previousValue, value, false);

								updateListValues(resource);
								refreshTextArea();
								refreshLists();
							} else {
								tBoxExtraction.editProperty(getSelectedNode(), key, previousValue, value);

								updateListValues();
								refreshTextArea();
								refreshLists();
							}
						}
					}
				}
			});
			btnEdit.setToolTipText("Edit");
			btnEdit.setBackground(Color.WHITE);
			panelHolder.add(btnEdit, "cell 0 0");

			panelAnnotation.add(panelHolder);
		}

		for (Map.Entry<String, ArrayList<String>> map : tBoxDefinition.getDescriptionMap().entrySet()) {
			String key = map.getKey();
			ArrayList<String> list = map.getValue();

			JPanel panelHolder = new JPanel();
			panelHolder.setBackground(Color.WHITE);
			panelHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

			JLabel lblKey = new JLabel(key);
			lblKey.setForeground(Color.BLUE);
			panelHolder.add(lblKey, "flowx,cell 0 0,growx,pushx");

			DefaultListModel listModel = new DefaultListModel<>();
			for (String string : list) {
				listModel.addElement(string);
			}
			JList valueList = new JList(listModel);
			panelHolder.add(valueList, "cell 0 1,grow");

			JButton buttonAdd = new JButton("+");
			buttonAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					TreePath[] paths = listAddButtonHandler();

					if (paths != null) {
						if (paths.length > 0) {
							for (TreePath path : paths) {
								if (getSelectedNode().contains("_:")) {
									String resource = "";

									if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
									} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
									} else {
										resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
									}

									tBoxExtraction.addProperty(resource, key, path.getLastPathComponent().toString(),
											false);
								} else {
									tBoxExtraction.addProperty(getSelectedNode(), key,
											path.getLastPathComponent().toString());
								}
							}
						}

						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							updateListValues();
							refreshTextArea();
							refreshLists();
						}
					}
				}
			});
			buttonAdd.setToolTipText("Add");
			buttonAdd.setBackground(Color.WHITE);
			panelHolder.add(buttonAdd, "cell 0 0");

			JButton buttonRemove = new JButton("-");
			buttonRemove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {
						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							tBoxExtraction.removeProperty(resource, key, (String) valueList.getSelectedValue(), false);

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							tBoxExtraction.removeProperty(getSelectedNode(), key,
									(String) valueList.getSelectedValue());

							updateListValues();
							refreshTextArea();
							refreshLists();
						}
					}
				}
			});
			buttonRemove.setToolTipText("Remove");
			buttonRemove.setBackground(Color.WHITE);
			panelHolder.add(buttonRemove, "cell 0 0");

			JButton btnEdit = new JButton("u");
			btnEdit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {
						String previousValue = (String) valueList.getSelectedValue();
						String value = listEditButtonHandler(previousValue);
						if (value != null) {
							if (getSelectedNode().contains("_:")) {
								String resource = "";

								if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
								} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
								} else {
									resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
								}

								tBoxExtraction.editProperty(resource, key, previousValue, value, false);

								updateListValues(resource);
								refreshTextArea();
								refreshLists();
							} else {
								tBoxExtraction.editProperty(getSelectedNode(), key, previousValue, value);

								updateListValues();
								refreshTextArea();
								refreshLists();
							}
						}
					}
				}
			});
			btnEdit.setToolTipText("Edit");
			btnEdit.setBackground(Color.WHITE);
			panelHolder.add(btnEdit, "cell 0 0");

			panelDescription.add(panelHolder);
		}

		for (Map.Entry<String, ArrayList<String>> map : tBoxDefinition.getMdMap().entrySet()) {
			String key = map.getKey();
			ArrayList<String> list = map.getValue();

			JPanel panelHolder = new JPanel();
			panelHolder.setBackground(Color.WHITE);
			panelHolder.setLayout(new MigLayout("", "[grow]", "[][grow]"));

			JLabel lblKey = new JLabel(key);
			lblKey.setForeground(Color.BLUE);
			panelHolder.add(lblKey, "flowx,cell 0 0,growx,pushx");

			DefaultListModel listModel = new DefaultListModel<>();
			for (String string : list) {
				listModel.addElement(string);
			}
			JList valueList = new JList(listModel);
			panelHolder.add(valueList, "cell 0 1,grow");

			JButton buttonAdd = new JButton("+");
			buttonAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					TreePath[] paths = listAddButtonHandler();

					if (paths != null) {
						if (paths.length > 0) {
							for (TreePath path : paths) {
								if (getSelectedNode().contains("_:")) {
									String resource = "";

									if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
									} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
										resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
									} else {
										resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
									}

									tBoxExtraction.addProperty(resource, key, path.getLastPathComponent().toString(),
											false);
								} else {
									tBoxExtraction.addProperty(getSelectedNode(), key,
											path.getLastPathComponent().toString());
								}
							}
						}

						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							updateListValues();
							refreshTextArea();
							refreshLists();
						}
					}

				}
			});
			buttonAdd.setToolTipText("Add");
			buttonAdd.setBackground(Color.WHITE);
			panelHolder.add(buttonAdd, "cell 0 0");

			JButton buttonRemove = new JButton("-");
			buttonRemove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {
						if (getSelectedNode().contains("_:")) {
							String resource = "";

							if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
							} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
								resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
							} else {
								resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
							}

							tBoxExtraction.removeProperty(resource, key, (String) valueList.getSelectedValue(), false);

							updateListValues(resource);
							refreshTextArea();
							refreshLists();
						} else {
							tBoxExtraction.removeProperty(getSelectedNode(), key,
									(String) valueList.getSelectedValue());

							updateListValues();
							refreshTextArea();
							refreshLists();
						}
					}
				}
			});
			buttonRemove.setToolTipText("Remove");
			buttonRemove.setBackground(Color.WHITE);
			panelHolder.add(buttonRemove, "cell 0 0");

			JButton btnEdit = new JButton("u");
			btnEdit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (valueList.isSelectionEmpty()) {
						JOptionPane.showMessageDialog(null, "Select a value from the list");
					} else {
						String previousValue = (String) valueList.getSelectedValue();
						String value = listEditButtonHandler(previousValue);
						if (value != null) {

							if (getSelectedNode().contains("_:")) {
								String resource = "";

								if (tBoxDefinition.getHierarchyStepsList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
								} else if (tBoxDefinition.getCubeNodeList().containsKey(getSelectedNode())) {
									resource = tBoxDefinition.getCubeNodeList().get(getSelectedNode());
								} else {
									resource = tBoxDefinition.getCuboidNodeList().get(getSelectedNode());
								}

								tBoxExtraction.editProperty(resource, key, previousValue, value, false);

								updateListValues(resource);
								refreshTextArea();
								refreshLists();
							} else {
								tBoxExtraction.editProperty(getSelectedNode(), key, previousValue, value);

								updateListValues();
								refreshTextArea();
								refreshLists();
							}
						}
					}
				}
			});
			btnEdit.setToolTipText("Edit");
			btnEdit.setBackground(Color.WHITE);
			panelHolder.add(btnEdit, "cell 0 0");

			panelMultiDimension.add(panelHolder);
		}
	}

	private void updateListValues() {
		tBoxExtraction.generateMaps(getSelectedNode());
		tBoxDefinition.setAnnotationMap(tBoxExtraction.getAnnotationMap());
		tBoxDefinition.setDescriptionMap(tBoxExtraction.getDescriptionMap());
		tBoxDefinition.setMdMap(tBoxExtraction.getMdMap());
	}

	protected void updateListValues(String resource) {
		// TODO Auto-generated method stub
		// String resource =
		// tBoxDefinition.getHierarchyStepsList().get(getSelectedNode());
		tBoxExtraction.generateMaps(resource, false);
		tBoxDefinition.setAnnotationMap(tBoxExtraction.getAnnotationMap());
		tBoxDefinition.setDescriptionMap(tBoxExtraction.getDescriptionMap());
		tBoxDefinition.setMdMap(tBoxExtraction.getMdMap());
	}

	private void initializeAll() {
		// TODO Auto-generated method stub
		fileMethods = new FileMethods();
		tBoxDefinition = new TBoxDefinition();
	}

	private void initializeDefinition() {
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
		tBoxDefinition.setCubeNodeList(tBoxExtraction.getCubeNodeList());
		tBoxDefinition.setCuboidNodeList(tBoxExtraction.getCuboidNodeList());
		tBoxDefinition.setHierarchyStepsList(tBoxExtraction.getHierarchyStepsList());
		tBoxDefinition.setOntologyList(tBoxExtraction.getOntologyList());

		tBoxDefinition.setModelText(tBoxExtraction.getModelText("Turtle"));

		refreshAll();
	}

	private void instantiateAllTree() {
		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("owl:Thing");
		treeClass.setModel(new DefaultTreeModel(classNode));

		DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode("owl:topObjectProperty");
		treeObject.setModel(new DefaultTreeModel(objectNode));

		DefaultMutableTreeNode dataPropNode = new DefaultMutableTreeNode("owl:topDataProperty");
		treeData.setModel(new DefaultTreeModel(dataPropNode));

		DefaultMutableTreeNode prefixNode = new DefaultMutableTreeNode("Prefix");
		treeIRI.setModel(new DefaultTreeModel(prefixNode));

		DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level");
		treeLevel.setModel(new DefaultTreeModel(levelNode));

		DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode("Ontology");
		treeOntology.setModel(new DefaultTreeModel(ontologyNode));

		DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode("Attribute");
		treeAttribute.setModel(new DefaultTreeModel(attributeNode));

		DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode("Dimension");
		treeDimension.setModel(new DefaultTreeModel(dimensionNode));

		DefaultMutableTreeNode hierarchyNode = new DefaultMutableTreeNode("Explorer");
		treeHierarchy.setModel(new DefaultTreeModel(hierarchyNode));

		DefaultMutableTreeNode hierarchyStepNode = new DefaultMutableTreeNode("Explorer");
		treeHierarchyStep.setModel(new DefaultTreeModel(hierarchyStepNode));

		DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode("Measure");
		treeMeasure.setModel(new DefaultTreeModel(measureNode));

		DefaultMutableTreeNode rollUpNode = new DefaultMutableTreeNode("Roll Up Property");
		treeRollUp.setModel(new DefaultTreeModel(rollUpNode));

		DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode("Dataset");
		treeDataset.setModel(new DefaultTreeModel(datasetNode));

		DefaultMutableTreeNode dataStructureNode = new DefaultMutableTreeNode("DataStructure");
		DefaultMutableTreeNode cubeNode = new DefaultMutableTreeNode("Cube");
		DefaultMutableTreeNode cuboidNode = new DefaultMutableTreeNode("Cuboid");
		dataStructureNode.add(cubeNode);
		dataStructureNode.add(cuboidNode);
		treeDataStructure.setModel(new DefaultTreeModel(dataStructureNode));
	}

	private void refreshAllTree() {
		// TODO Auto-generated method stub
		// Adding all classes to the tree
		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("owl:Thing");

		for (String string : tBoxDefinition.getClassList()) {
			classNode.add(new DefaultMutableTreeNode(string));
		}

		treeClass.setModel(new DefaultTreeModel(classNode));

		// Adding all object properties to the tree
		DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode("owl:topObjectProperty");

		for (String string : tBoxDefinition.getObjectList()) {
			objectNode.add(new DefaultMutableTreeNode(string));
		}

		treeObject.setModel(new DefaultTreeModel(objectNode));

		// Adding all data properties to the tree
		DefaultMutableTreeNode dataPropNode = new DefaultMutableTreeNode("owl:topDataProperty");

		for (String string : tBoxDefinition.getDataList()) {
			dataPropNode.add(new DefaultMutableTreeNode(string));
		}

		treeData.setModel(new DefaultTreeModel(dataPropNode));

		// Adding all prefixes to the tree
		DefaultMutableTreeNode prefixNode = new DefaultMutableTreeNode("Prefix");

		for (String string : tBoxDefinition.getPrefixMap().keySet()) {
			prefixNode.add(new DefaultMutableTreeNode(string));
		}

		treeIRI.setModel(new DefaultTreeModel(prefixNode));

		// Adding all data sets to the tree
		DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode("Dataset");

		for (String string : tBoxDefinition.getDatasetList()) {
			datasetNode.add(new DefaultMutableTreeNode(string));
		}

		treeDataset.setModel(new DefaultTreeModel(datasetNode));

		// Adding all dimensions to the tree
		DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode("Dimension");

		for (String string : tBoxDefinition.getDimensionList()) {
			dimensionNode.add(new DefaultMutableTreeNode(string));
		}

		treeDimension.setModel(new DefaultTreeModel(dimensionNode));

		// Adding all hierarchies to the tree
		DefaultMutableTreeNode hierarchyNode = new DefaultMutableTreeNode("Explorer");

		for (String string : tBoxDefinition.getHierarchyList()) {
			hierarchyNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchy.setModel(new DefaultTreeModel(hierarchyNode));

		// Adding all hierarchyStepNode to the tree
		DefaultMutableTreeNode hierarchyStepNode = new DefaultMutableTreeNode("HierarchyStep");

		for (String string : tBoxDefinition.getHierarchyStepsList().keySet()) {
			hierarchyStepNode.add(new DefaultMutableTreeNode(string));
		}

		treeHierarchyStep.setModel(new DefaultTreeModel(hierarchyStepNode));

		DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode("Ontology");

		for (String string : tBoxDefinition.getOntologyList()) {
			ontologyNode.add(new DefaultMutableTreeNode(string));
		}

		treeOntology.setModel(new DefaultTreeModel(ontologyNode));

		// Adding all levels to the tree *
		DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level");

		for (String string : tBoxDefinition.getLevelList()) {
			levelNode.add(new DefaultMutableTreeNode(string));
		}

		treeLevel.setModel(new DefaultTreeModel(levelNode));

		// Adding all attributes to the tree
		DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode("Attribute");

		for (String string : tBoxDefinition.getAttributeList()) {
			attributeNode.add(new DefaultMutableTreeNode(string));
		}

		treeAttribute.setModel(new DefaultTreeModel(attributeNode));

		// Adding all measures to the tree
		DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode("Measure");

		for (String string : tBoxDefinition.getMeasureList()) {
			measureNode.add(new DefaultMutableTreeNode(string));
		}

		treeMeasure.setModel(new DefaultTreeModel(measureNode));

		// Adding all roll up properties to the tree
		DefaultMutableTreeNode rollUpNode = new DefaultMutableTreeNode("Roll Up Properties");

		for (String string : tBoxDefinition.getRollupList()) {
			rollUpNode.add(new DefaultMutableTreeNode(string));
		}

		treeRollUp.setModel(new DefaultTreeModel(rollUpNode));

		DefaultMutableTreeNode dataStructureNode = new DefaultMutableTreeNode("DataStructure");
		DefaultMutableTreeNode cubeNode = new DefaultMutableTreeNode("Cube");
		DefaultMutableTreeNode cuboidNode = new DefaultMutableTreeNode("Cuboid");
		dataStructureNode.add(cubeNode);
		dataStructureNode.add(cuboidNode);

		for (String string : tBoxDefinition.getCubeList().keySet()) {
			DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(string);

			ArrayList<String> list = tBoxDefinition.getCubeList().get(string);
			for (String string2 : list) {
				defaultMutableTreeNode.add(new DefaultMutableTreeNode(string2));
			}

			cubeNode.add(defaultMutableTreeNode);
		}

		for (String string : tBoxDefinition.getCuboidList().keySet()) {
			DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(string);

			ArrayList<String> list = tBoxDefinition.getCuboidList().get(string);
			for (String string2 : list) {
				defaultMutableTreeNode.add(new DefaultMutableTreeNode(string2));
			}

			cuboidNode.add(defaultMutableTreeNode);
		}

		treeDataStructure.setModel(new DefaultTreeModel(dataStructureNode));
	}

	private void refreshTextArea() {
		// String selectedType = comboBoxViewFileType.getSelectedItem().toString();
		/*
		 * if (selectedType.equals("Turtle")) {
		 * textAreaTBox.setText(tBoxExtraction.printAllComponents()); } else {
		 * tBoxDefinition.setModelText(tBoxExtraction.getModelText(selectedType));
		 * textAreaTBox.setText(tBoxDefinition.getModelText()); }
		 */

		tBoxDefinition.setModelText(tBoxExtraction.getModelText(comboBoxViewFileType.getSelectedItem().toString()));
		textAreaTBox.setText(tBoxDefinition.getModelText());
	}

	public String getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(String selectedNode) {
		this.selectedNode = selectedNode;
	}
}
