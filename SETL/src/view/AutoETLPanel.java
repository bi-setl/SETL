package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.event.ListSelectionEvent;

public class AutoETLPanel extends JPanel {
	String basePath = "C:\\Users\\Amrit\\Documents\\SETL\\AutoETL\\";
	// String basePath = "C:\\Users\\USER\\Documents\\SETL\\AutoETL\\";
	
	String mapFile = basePath + "map_current.ttl";
	String targetTBoxFile = "subsidy.ttl";

	/**
	 * Create the panel.
	 */
	
	Definition definition;
	
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
				ArrayList<String> levels = definition.extractLevels();
			}
		});
		panelButtonHolder.add(btnMap);
		
		JButton btnTargetTBox = new JButton("Target TBox File");
		btnTargetTBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				definition.setTboxPath(targetTBoxFile);
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
		
		JList list = new JList();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
			}
		});
		list.setBackground(Color.WHITE);
		panel_3.add(list, "cell 0 0,grow");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		splitPane.setRightComponent(panel_4);

	}

	private void initAll() {
		// TODO Auto-generated method stub
		definition = new Definition();
	}

}
