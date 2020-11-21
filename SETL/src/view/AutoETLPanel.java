package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
import queries.Extraction;
import view.PanelETL.Arrow;
import view.PanelETL.Operation;

import javax.swing.JSplitPane;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionListener;

import controller.Definition;
import core.PrefixExtraction;
import model.ConceptTransform;

import javax.swing.event.ListSelectionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class AutoETLPanel extends JPanel {
	String basePath = "C:\\Users\\Amrit\\Documents\\1\\";
	// String basePath = "C:\\Users\\USER\\Documents\\SETL\\AutoETL\\";
	
	String mapFile = basePath + "map_version_1605008727221.ttl";
	String targetTBoxFile = basePath + "exiobase.ttl";

	/**
	 * Create the panel.
	 */
	
	Definition definition;
	PrefixExtraction prefixExtraction;
	private LinkedHashMap<Integer, ConceptTransform> conceptMap;
	private JList levelList;
	private JList datasetList;
	
	PanelETL panelETL;
	
	public AutoETLPanel() {
		initAll();
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtonHolder = new JPanel();
		panelButtonHolder.setBackground(Color.WHITE);
		panel.add(panelButtonHolder, BorderLayout.NORTH);
		
		JButton btnMap = new JButton("Mapping File");
		btnMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				definition.setMapPath(mapFile);
				
				prefixExtraction.extractPrefix(mapFile);
			}
		});
		panelButtonHolder.add(btnMap);
		
		JButton btnTargetTBox = new JButton("Target TBox File");
		btnTargetTBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				definition.setMapPath(mapFile);
				prefixExtraction.extractPrefix(mapFile);
				
				definition.setTboxPath(targetTBoxFile);
				
				prefixExtraction.extractPrefix(targetTBoxFile);
				
				ArrayList<String> levels = definition.extractLevels();
				
				DefaultListModel<String> levelModel = new DefaultListModel<>();
				for (String string : levels) {
					levelModel.addElement(prefixExtraction.assignPrefix(string));
				}

				levelList.setModel(levelModel);
				
				ArrayList<String> datasets = definition.extractDatasets();
				
				DefaultListModel<String> instanceModel = new DefaultListModel<>();
				for (String string : datasets) {
					instanceModel.addElement(prefixExtraction.assignPrefix(string));
				}

				datasetList.setModel(instanceModel);
			}
		});
		panelButtonHolder.add(btnTargetTBox);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		panel_1.add(splitPane, "cell 0 0,grow");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		splitPane.setLeftComponent(panel_2);
		panel_2.setLayout(new MigLayout("", "[grow]", "[grow][grow][]"));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, "cell 0 0,grow");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel_4);
		panel_4.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JLabel lblNewLabel = new JLabel("Levels");
		panel_4.add(lblNewLabel, "cell 0 0");
		
		levelList = new JList();
		panel_4.add(levelList, "cell 0 1,grow");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1, "cell 0 1,grow");
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		scrollPane_1.setViewportView(panel_5);
		panel_5.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JLabel lblNewLabel_1 = new JLabel("Datasets");
		panel_5.add(lblNewLabel_1, "cell 0 0");
		
		datasetList = new JList();
		panel_5.add(datasetList, "cell 0 1,grow");
		
		JButton btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<String> levels = (ArrayList<String>) levelList.getSelectedValuesList();
//				ArrayList<String> datasets = (ArrayList<String>) datasetList.getSelectedValuesList();
				
				LinkedHashMap<String, ArrayList<ConceptTransform>> dependencyMap = definition.extractDependency(levels, prefixExtraction);
				
				for (String selectedString : dependencyMap.keySet()) {
					ArrayList<ConceptTransform> conceptTransforms = dependencyMap.get(selectedString);
					
					for (ConceptTransform conceptTransform : conceptTransforms) {
						System.out.println("-------------------------------");
						
						System.out.println(conceptTransform.getConcept());
						System.out.println(conceptTransform.getOperationName());
						System.out.println(conceptTransform.getSourceABoxLocationString());
						System.out.println(conceptTransform.getTargetFileLocation());
					}
				}
				
				int xCount = 200, yCount = 200;
				
				for (String selectedString : dependencyMap.keySet()) {
					xCount = 200;
					ArrayList<ConceptTransform> conceptList = dependencyMap.get(selectedString);
					
					PanelETL.Operation previousOperation = null;
					for (ConceptTransform conceptTransform : conceptList) {
						
						
						Operation newOperation = panelETL.graphPanel.addNewConceptOperation(conceptTransform, xCount, yCount, mapFile, targetTBoxFile);
						
						if (previousOperation == null) {
							previousOperation = panelETL.allOperations.get(0);	
						}
						
						panelETL.addNewArrow(previousOperation, newOperation);
						previousOperation = newOperation;
						
						xCount += 300;
					}
					
					yCount += 150;
				}
			}
		});
		panel_2.add(btnContinue, "flowx,cell 0 2,alignx center");
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelETL.executeETL(true);
			}
		});
		panel_2.add(btnRun, "cell 0 2");
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.9);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel_3.add(splitPane_1, "cell 0 0,grow");
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		splitPane_1.setLeftComponent(panel_6);
		panel_6.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_6.add(scrollPane_2, "cell 0 0,grow");
		
		JPanel panel_8 = new JPanel();
		scrollPane_2.setViewportView(panelETL.graphPanel);
		panel_8.setBackground(Color.WHITE);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		splitPane_1.setRightComponent(panel_7);
		panel_7.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panel_9 = new JPanel();
		panel_9.setBackground(Color.WHITE);
		panel_7.add(panelETL.panelTextContainer, "cell 0 0,grow");
	}

	private void initAll() {
		// TODO Auto-generated method stub
		definition = new Definition();
		prefixExtraction = new PrefixExtraction();
		conceptMap = new LinkedHashMap<Integer, ConceptTransform>();
		
		panelETL = new PanelETL();
	}

}
