package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import model.ButtonTabComponent;
import model.TabModel;
import model.TabModelList;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class SETLFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String dbURL;
	public static String dbUserName;
	public static String dbPassword;
	private JPanel contentPane;
	private JTabbedPane tabbedPaneContainer;
	private TabModelList tabModelList;
	private JPanel panelToolbar;
	private JToolBar toolBarETL;
	private JToolBar toolBarMapping;
	private boolean firstStart = true;
	final static String DB_PANEL_KEY = "DBPanel";
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SETLFrame frame = new SETLFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SETLFrame() {
		// setting default values to the frame -- Amrit
		setBackground(Color.WHITE);
		setTitle("SETL");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		initializeAll();

		// This is for menu bars and menu items -- Amrit
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnNew = new JMenu("New");
		mnFile.add(mnNew);

		/*JMenuItem mntmEtl = new JMenuItem("ETL");
		mntmEtl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelETL) {
						count++;
					}
				}
				
				PanelETL panelETL = new PanelETL();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelETL));
				addPanelToContainer("ETL" + count, panelETL);
			}
		});
		mnNew.add(mntmEtl);*/
		
		JMenuItem mntmQetl = new JMenuItem("QETL");
		mntmQetl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelOnDemandETL) {
						count++;
					}
				}
				
				PanelOnDemandETL panelTBoxEnrichment = new PanelOnDemandETL();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelTBoxEnrichment));
				addPanelToContainer("QETL" + count,panelTBoxEnrichment);
			}
		});
		// mnNew.add(mntmQetl);
		
		JMenu mnDefinitionLayer = new JMenu("Definition Layer");
		mnNew.add(mnDefinitionLayer);
		
		JMenuItem mntmTargetTboxDefiner = new JMenuItem("Target TBox Definer");
		mntmTargetTboxDefiner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelTBoxEnrichment) {
						count++;
					}
				}
				
				PanelTBoxEnrichment panelTBoxEnrichment = new PanelTBoxEnrichment();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelTBoxEnrichment));
				addPanelToContainer("TBox" + count,panelTBoxEnrichment);
			}
		});
		mnDefinitionLayer.add(mntmTargetTboxDefiner);
		
		JMenuItem mntmSourcetargetMappingDefiner = new JMenuItem("Source2Target Mapping Definer");
		mntmSourcetargetMappingDefiner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
						count++;
					}
				}
				
				PanelMapSource2TargetNew panelMapSource2Target = new PanelMapSource2TargetNew();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelMapSource2Target));
				addPanelToContainer("Source2Target" + count, panelMapSource2Target);
			}
		});
		mnDefinitionLayer.add(mntmSourcetargetMappingDefiner);
		
		JMenuItem mntmRmlDefiner = new JMenuItem("RML Definer");
		mntmRmlDefiner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
						count++;
					}
				}
				
				PanelRML panelRML = new PanelRML();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelRML));
				addPanelToContainer("RML" + count, panelRML);
			}
		});
		mnDefinitionLayer.add(mntmRmlDefiner);
		
		JMenuItem mntmEtlLayer = new JMenuItem("ETL Layer");
		mntmEtlLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelETL) {
						count++;
					}
				}
				
				PanelETL panelETL = new PanelETL();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelETL));
				addPanelToContainer("ETL" + count, panelETL);
			}
		});
		mnNew.add(mntmEtlLayer);
		
		JMenuItem mntmOlapLayer = new JMenuItem("OLAP Layer");
		mntmOlapLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelOlap) {
						count++;
					}
				}
				
				PanelOlap panelOlapNew = new PanelOlap();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelOlapNew));
				addPanelToContainer("OLAP" + count, panelOlapNew);
			}
		});
		mnNew.add(mntmOlapLayer);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);

		JMenuItem mntmOpenRecent = new JMenuItem("Open Recent");
		mnFile.add(mntmOpenRecent);

		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmUndo = new JMenuItem("Undo");
		mnEdit.add(mntmUndo);

		JMenuItem mntmRedo = new JMenuItem("Redo");
		mnEdit.add(mntmRedo);

		JMenuItem mntmCut = new JMenuItem("Cut");
		mnEdit.add(mntmCut);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mnEdit.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mnEdit.add(mntmPaste);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mnEdit.add(mntmSelectAll);

		JMenuItem mntmClear = new JMenuItem("Clear");
		mnEdit.add(mntmClear);

		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mnEdit.add(mntmRefresh);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenuItem mntmZoomIn = new JMenuItem("Zoom In");
		mnView.add(mntmZoomIn);

		JMenuItem mntmZoomOut = new JMenuItem("Zoom Out");
		mnView.add(mntmZoomOut);

		JCheckBoxMenuItem chckbxmntmEtlToolbar = new JCheckBoxMenuItem("ETL Toolbar");
		chckbxmntmEtlToolbar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (toolBarETL.getParent() != null) {
					panelToolbar.remove(toolBarETL);
				} else {
					panelToolbar.add(toolBarETL);
				}

				panelToolbar.repaint();
				panelToolbar.revalidate();
			}
		});
		chckbxmntmEtlToolbar.setSelected(true);
		mnView.add(chckbxmntmEtlToolbar);
		
		JCheckBoxMenuItem chckbxmntmMappingToolbar = new JCheckBoxMenuItem("Mapping Toolbar");
		chckbxmntmMappingToolbar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (toolBarMapping.getParent() != null) {
					panelToolbar.remove(toolBarMapping);
				} else {
					panelToolbar.add(toolBarMapping);
				}

				panelToolbar.repaint();
				panelToolbar.revalidate();
			}
		});
		chckbxmntmMappingToolbar.setSelected(true);
		mnView.add(chckbxmntmMappingToolbar);

		JMenu mnAction = new JMenu("Action");
		menuBar.add(mnAction);

		JMenuItem mntmRun = new JMenuItem("Run");
		mnAction.add(mntmRun);

		JMenuItem mntmStop = new JMenuItem("Stop");
		mnAction.add(mntmStop);

		JMenu mnOperations = new JMenu("Operations");
		menuBar.add(mnOperations);

		JMenu mnDefinition = new JMenu("Definition");
		mnOperations.add(mnDefinition);

		JMenuItem mntmTboxDefinition = new JMenuItem("TBox Definition");
		mntmTboxDefinition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelTBoxEnrichment) {
						count++;
					}
				}
				
				PanelTBoxEnrichment panelTBoxEnrichment = new PanelTBoxEnrichment();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelTBoxEnrichment));
				addPanelToContainer("TBox" + count,panelTBoxEnrichment);
			}
		});
		mnDefinition.add(mntmTboxDefinition);

		JMenu mnMapping = new JMenu("Mapping");
		mnOperations.add(mnMapping);

		JMenuItem mntmSourcetarget = new JMenuItem("Source2Target");
		mntmSourcetarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// To add a panel as Tab, you only need to create an object of that panel
				// and pass it to the following method as parameter -- Amrit
				int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
						count++;
					}
				}
				
				PanelMapSource2TargetNew panelMapSource2Target = new PanelMapSource2TargetNew();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelMapSource2Target));
				addPanelToContainer("Source2Target" + count, panelMapSource2Target);
			}
		});
		mnMapping.add(mntmSourcetarget);

		JMenuItem mntmDirectMapping = new JMenuItem("Direct Mapping");
		mntmDirectMapping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				/*int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelDirectRDF) {
						count++;
					}
				}
				
				PanelDirectRDF directRDFPanel = new PanelDirectRDF();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), directRDFPanel));
				addPanelToContainer("Mapping" + count, directRDFPanel);*/
			}
		});
		mnMapping.add(mntmDirectMapping);

		JMenuItem mntmRrml = new JMenuItem("R2RML");
		mntmRrml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				/*int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelRMLProcessing) {
						count++;
					}
				}
				
				PanelRMLProcessing rmlProcessingPanel = new PanelRMLProcessing();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), rmlProcessingPanel));
				addPanelToContainer("R2RML" + count, rmlProcessingPanel);*/
			}
		});
		mnMapping.add(mntmRrml);

		JMenu mnExtraction = new JMenu("Extraction");
		mnOperations.add(mnExtraction);

		JMenuItem mntmExtsparql = new JMenuItem("ExtSPARQL");
		mntmExtsparql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				/*int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelSparqlQuery) {
						count++;
					}
				}
				
				PanelSparqlQuery panelSparqlQuery = new PanelSparqlQuery();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelSparqlQuery));
				addPanelToContainer("Sparql" + count, panelSparqlQuery);*/
			}
		});
		mnExtraction.add(mntmExtsparql);

		JMenuItem mntmExtdb = new JMenuItem("ExtDB");
		mntmExtdb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*int count = 1;
				
				for (TabModel tabModel : tabModelList.getTabModels()) {
					if (tabModel.getTabObject() instanceof PanelExtractDB) {
						count++;
					}
				}
				
				
				PanelExtractDB panelExtractDB = new PanelExtractDB();
				tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelExtractDB));
				addPanelToContainer("Db" + count, panelExtractDB);*/
			}
		});
		mnExtraction.add(mntmExtdb);

		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		// default panel attached to the frame -- Amrit
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelToolbar = new JPanel();
		contentPane.add(panelToolbar, BorderLayout.NORTH);
		panelToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		// designing the toolbar -- Amrit
		JToolBar toolBar = new JToolBar();
		panelToolbar.add(toolBar);

		JButton btnNew = new JButton("");
		btnNew.setToolTipText("New");
		btnNew.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/new_file.png")));
		toolBar.add(btnNew);

		JButton btnOpen = new JButton("");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openButtonHandler();
			}
		});
		btnOpen.setToolTipText("Open");
		btnOpen.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/open_file.png")));
		toolBar.add(btnOpen);

		JButton btnSave = new JButton("");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveButtonHandler();
			}
		});
		btnSave.setToolTipText("Save");
		btnSave.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/save_file.png")));
		toolBar.add(btnSave);

		JButton btnCut = new JButton("");
		btnCut.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/file.png")));
		btnCut.setToolTipText("Cut");
		toolBar.add(btnCut);

		JButton btnCopy = new JButton("");
		btnCopy.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/copy.png")));
		btnCopy.setToolTipText("Copy");
		toolBar.add(btnCopy);

		toolBarETL = new JToolBar();
		toolBarETL.setToolTipText("ETL Toolbar");
		panelToolbar.add(toolBarETL);

		JButton btnETLRun = new JButton("");
		btnETLRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				if (tabModel.getTabObject() instanceof PanelETL) {
					PanelETL panelETL = (PanelETL) tabModel.getTabObject();
					panelETL.runButtonHandler();
				}
			}
		});
		
		JButton btnEtlRefresh = new JButton("");
		btnEtlRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				if (tabModel.getTabObject() instanceof PanelETL) {
					PanelETL panelETL = (PanelETL) tabModel.getTabObject();
					panelETL.refreshButtonHandler();
				}
			}
		});
		
		JButton btnEtlClear = new JButton("");
		btnEtlClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				if (tabModel.getTabObject() instanceof PanelETL) {
					PanelETL panelETL = (PanelETL) tabModel.getTabObject();
					panelETL.clearButtonHandler();
				}
			}
		});
		btnEtlClear.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/clear.png")));
		btnEtlClear.setToolTipText("ETL Clear");
		toolBarETL.add(btnEtlClear);
		btnEtlRefresh.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/refresh.png")));
		btnEtlRefresh.setToolTipText("ETL Refresh");
		toolBarETL.add(btnEtlRefresh);
		btnETLRun.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/run.png")));
		btnETLRun.setToolTipText("Run");
		toolBarETL.add(btnETLRun);
		
		toolBarMapping = new JToolBar();
		panelToolbar.add(toolBarMapping);
		
		JButton btnOpenSourceFile = new JButton("");
		btnOpenSourceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
					PanelMapSource2TargetNew mapSource2Target = (PanelMapSource2TargetNew) tabModel.getTabObject();
					mapSource2Target.openSourceFileHandler();
				}
			}
		});
		btnOpenSourceFile.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/open_file.png")));
		btnOpenSourceFile.setToolTipText("Open Source File");
		toolBarMapping.add(btnOpenSourceFile);
		
		JButton btnOpenTargetFile = new JButton("");
		toolBarMapping.add(btnOpenTargetFile);
		btnOpenTargetFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
					PanelMapSource2TargetNew mapSource2Target = (PanelMapSource2TargetNew) tabModel.getTabObject();
					mapSource2Target.openTargetFileHandler();
				}
			}
		});
		btnOpenTargetFile.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/open_file.png")));
		btnOpenTargetFile.setToolTipText("Open Target File");
		
		JButton btnOpenRecordFile = new JButton("");
		btnOpenRecordFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
				
			}
		});
		toolBarMapping.add(btnOpenRecordFile);
		btnOpenRecordFile.setIcon(new ImageIcon(SETLFrame.class.getResource("/images/open_file.png")));
		btnOpenRecordFile.setToolTipText("Open Record File");

		JPanel panelOne = new JPanel();
		contentPane.add(panelOne, BorderLayout.CENTER);
		panelOne.setLayout(new BorderLayout(0, 0));

		JPanel panelTwo = new JPanel();
		panelOne.add(panelTwo, BorderLayout.CENTER);
		panelTwo.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		tabbedPaneContainer = new JTabbedPane(JTabbedPane.TOP);
		panelTwo.add(tabbedPaneContainer, "cell 0 1,grow");
		
		JPanel panelTextOne = new JPanel();
		panelTextOne.setBackground(Color.WHITE);
		tabbedPaneContainer.addTab(null, null, panelTextOne, null);
		GridBagLayout gbl_panelTextOne = new GridBagLayout();
		gbl_panelTextOne.columnWidths = new int[]{0, 0};
		gbl_panelTextOne.rowHeights = new int[]{0, 0};
		gbl_panelTextOne.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelTextOne.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelTextOne.setLayout(gbl_panelTextOne);
		
		JPanel panelTextTwo = new JPanel();
		panelTextTwo.setBackground(Color.WHITE);
		GridBagConstraints gbc_panelTextTwo = new GridBagConstraints();
		gbc_panelTextTwo.fill = GridBagConstraints.CENTER;
		gbc_panelTextTwo.gridx = 0;
		gbc_panelTextTwo.gridy = 0;
		panelTextOne.add(panelTextTwo, gbc_panelTextTwo);
		panelTextTwo.setLayout(new MigLayout("", "[center]", "[][]"));
		
		JLabel lblSetl = new JLabel("<html><body><p><u>SETL</u><sub>BI</sub></p></body></html>");
		lblSetl.setFont(new Font("Tahoma", Font.BOLD, 44));
		panelTextTwo.add(lblSetl, "cell 0 0");
		
		JLabel lblAnIntegratedPlatform = new JLabel("An Integrated Platform for Semantic Business Intelligence");
		lblAnIntegratedPlatform.setFont(new Font("Tahoma", Font.BOLD, 36));
		panelTextTwo.add(lblAnIntegratedPlatform, "cell 0 1");
		
		/*JLabel lblTexttwo = new JLabel("TextTwo");
		lblTexttwo.setHorizontalAlignment(SwingConstants.CENTER);
		panelTextOne.add(lblTexttwo);
		
		JLabel lblTextone = new JLabel("TextOne");
		lblTextone.setHorizontalAlignment(SwingConstants.CENTER);
		panelTextOne.add(lblTextone);*/
		
		/*JLabel lblSetl = new JLabel("<html><body><p style=\"text-align: center; font-size: 40px\"><u>SETL</u></p><p style=\"text-align: center;\">An Integrated Platform for Semantic Business Intelligence</p></body></html>");
		lblSetl.setHorizontalAlignment(SwingConstants.CENTER);
		lblSetl.setFont(new Font("Tahoma", Font.BOLD, 40));
		panel.add(lblSetl, BorderLayout.CENTER);*/

		// added a new ETL tab as default at first -- Amrit
		/*PanelETL panelETL = new PanelETL();
		tabModelList.getTabModels().add(new TabModel(tabbedPaneContainer.getTabCount(), panelETL));
		addPanelToContainer("ETL1", panelETL);*/
	}

	private void initializeAll() {
		// TODO Auto-generated method stub
		tabModelList = new TabModelList();
	}

	private void addPanelToContainer(String title, JPanel panel) {
		// TODO Auto-generated method stub
		// JScrollPane scrollPane = new JScrollPane();
		// tabbedPaneContainer.addTab("New" + index, null, scrollPane, null);
		
		if (firstStart) {
			tabbedPaneContainer.removeAll();
			tabbedPaneContainer.repaint();
			tabbedPaneContainer.revalidate();
			
			firstStart = false;
		}
		
		JPanel panelContainer = new JPanel();
		// scrollPane.setViewportView(panelContainer);
		tabbedPaneContainer.addTab(title, null, panelContainer, null);
		panelContainer.setLayout(new BorderLayout(0, 0));

		panelContainer.add(panel, BorderLayout.CENTER);
		tabbedPaneContainer.setTabComponentAt((tabbedPaneContainer.getTabCount() - 1),
				new ButtonTabComponent(tabbedPaneContainer, tabModelList));
		tabbedPaneContainer.setSelectedIndex((tabbedPaneContainer.getTabCount() - 1));
	}

	public void openButtonHandler() {
		TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
		if (tabModel.getTabObject() instanceof PanelTBoxEnrichment) {
			PanelTBoxEnrichment panelTBoxEnrichment = (PanelTBoxEnrichment) tabModel.getTabObject();
			panelTBoxEnrichment.openButtonHandler();
		} else if (tabModel.getTabObject() instanceof PanelETL) {
			PanelETL panelETL = (PanelETL) tabModel.getTabObject();
			panelETL.etlOpenButtonHandler();
		} else if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
			PanelMapSource2TargetNew mapSource2Target = (PanelMapSource2TargetNew) tabModel.getTabObject();
			mapSource2Target.openButtonHandler();
		}
	}

	public void saveButtonHandler() {
		TabModel tabModel = tabModelList.getTabModels().get(tabbedPaneContainer.getSelectedIndex());
		if (tabModel.getTabObject() instanceof PanelTBoxEnrichment) {
			PanelTBoxEnrichment panelTBoxEnrichment = (PanelTBoxEnrichment) tabModel.getTabObject();
			panelTBoxEnrichment.saveButtonHandler();
		} else if (tabModel.getTabObject() instanceof PanelETL) {
			PanelETL panelETL = (PanelETL) tabModel.getTabObject();
			panelETL.saveButtonHandler();
		} else if (tabModel.getTabObject() instanceof PanelMapSource2TargetNew) {
			PanelMapSource2TargetNew mapSource2Target = (PanelMapSource2TargetNew) tabModel.getTabObject();
			mapSource2Target.saveButtonHandler();
		}
	}
}
