package demo;

import javax.swing.JPanel;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.ScrollPaneConstants;

public class PanelAddHierarchyNew extends JPanel {

	/**
	 * Create the panel.
	 */
	public PanelAddHierarchyNew() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "cell 0 0,grow");
		
		JPanel panelAddHierarchy = new JPanel();
		scrollPane.setViewportView(panelAddHierarchy);
		panelAddHierarchy.setBackground(Color.WHITE);
		panelAddHierarchy.setLayout(new MigLayout("", "[][grow]", "[][][][][][]"));
		
		JLabel lblPrefix = new JLabel("Prefix:");
		lblPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblPrefix, "cell 0 0,alignx trailing");
		
		JComboBox comboBoxPrefix = new JComboBox();
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
		panelAddHierarchy.add(textFieldHierLabel, "cell 1 2,growx");
		textFieldHierLabel.setColumns(10);
		
		JLabel lblLanguage = new JLabel("Language:");
		lblLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblLanguage, "cell 0 3,alignx trailing");
		
		JComboBox comboBoxLanguage = new JComboBox();
		comboBoxLanguage.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(comboBoxLanguage, "cell 1 3,growx");
		
		JLabel lblDimension = new JLabel("Dimension:");
		lblDimension.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblDimension, "cell 0 4,alignx trailing");
		
		JComboBox comboBoxDimension = new JComboBox();
		comboBoxDimension.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(comboBoxDimension, "cell 1 4,growx");
		
		JLabel lblLevels = new JLabel("Levels:");
		lblLevels.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAddHierarchy.add(lblLevels, "cell 0 5,alignx trailing");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelAddHierarchy.add(scrollPane_1, "cell 1 5,grow");
		
		JList listHierLevels = new JList();
		scrollPane_1.setViewportView(listHierLevels);
		listHierLevels.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		listHierLevels.setFont(new Font("Tahoma", Font.BOLD, 12));

	}

}
