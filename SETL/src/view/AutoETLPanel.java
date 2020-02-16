package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
import queries.Extraction;

import javax.swing.JSplitPane;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

import controller.Definition;
import core.PrefixExtraction;

import javax.swing.event.ListSelectionEvent;
import javax.swing.JLabel;

public class AutoETLPanel extends JPanel {
	String basePath = "C:\\Users\\Amrit\\Documents\\SETL\\AutoETL\\";
	// String basePath = "C:\\Users\\USER\\Documents\\SETL\\AutoETL\\";
	
	String mapFile = basePath + "map_current.ttl";
	String targetTBoxFile = basePath + "subsidy.ttl";

	/**
	 * Create the panel.
	 */
	
	Definition definition;
	PrefixExtraction prefixExtraction;
	private JList levelList;
	private JList datasetList;
	private JList selectionList;
	private ArrayList<String> selectedArrayList;
	
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
				selectedArrayList = new ArrayList<String>();
				refreshSelectionList();
				
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
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		panel_2.add(splitPane, "cell 0 0,grow");
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		splitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.5);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel_3.add(splitPane_1, "cell 0 0,grow");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		splitPane_1.setLeftComponent(panel_1);
		panel_1.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JLabel lblNewLabel = new JLabel("Levels");
		panel_1.add(lblNewLabel, "cell 0 0");
		
		levelList = new JList();
		levelList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String level = levelList.getSelectedValue().toString();
				
				if (!selectedArrayList.contains(level)) {
					selectedArrayList.add(level);
				}
				
				refreshSelectionList();
			}
		});
		panel_1.add(levelList, "cell 0 1,grow");
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		splitPane_1.setRightComponent(panel_5);
		panel_5.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JLabel lblNewLabel_1 = new JLabel("Datasets");
		panel_5.add(lblNewLabel_1, "cell 0 0");
		
		datasetList = new JList();
		datasetList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				String level = datasetList.getSelectedValue().toString();
				
				if (!selectedArrayList.contains(level)) {
					selectedArrayList.add(level);
				}
				
				refreshSelectionList();
			}
		});
		panel_5.add(datasetList, "cell 0 1,grow");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		splitPane.setRightComponent(panel_4);
		panel_4.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setResizeWeight(0.5);
		panel_4.add(splitPane_2, "cell 0 0,grow");
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		splitPane_2.setLeftComponent(panel_6);
		panel_6.setLayout(new MigLayout("", "[grow]", "[][grow][]"));
		
		JLabel lblNewLabel_2 = new JLabel("Selection");
		panel_6.add(lblNewLabel_2, "cell 0 0");
		
		selectionList = new JList();
		panel_6.add(selectionList, "cell 0 1,grow");
		
		JButton btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("ArrayList<String> selectedArrayList = new ArrayList();");
				
				for (String string : selectedArrayList) {
					System.out.println("selectedArrayList.add(\"" + string + "\");");
				}
			}
		});
		panel_6.add(btnContinue, "cell 0 2,alignx center");
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		splitPane_2.setRightComponent(panel_7);

	}

	protected void refreshSelectionList() {
		// TODO Auto-generated method stub
		DefaultListModel<String> instanceModel = new DefaultListModel<>();
		for (String string : selectedArrayList) {
			instanceModel.addElement(prefixExtraction.assignPrefix(string));
		}

		selectionList.setModel(instanceModel);
		
		definition.extractDependency(selectedArrayList);
	}

	private void initAll() {
		// TODO Auto-generated method stub
		definition = new Definition();
		prefixExtraction = new PrefixExtraction();
	}

}
