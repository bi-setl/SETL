package practice;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class PanelHead extends JPanel {

	/**
	 * Create the panel.
	 */
	public PanelHead() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panelHead = new JPanel();
		add(panelHead, "cell 0 0,grow");
		panelHead.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panelCommon = new JPanel();
		panelCommon.setBorder(new TitledBorder(null, "Common Property", TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLACK));
		panelCommon.setBackground(Color.WHITE);
		panelHead.add(panelCommon, "cell 0 0,grow");
		panelCommon.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		
		JLabel lblSourceCommonProperty = new JLabel("Source Common Property:");
		lblSourceCommonProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(lblSourceCommonProperty, "cell 0 0,alignx trailing");
		
		JComboBox comboBoxSourceProperty = new JComboBox();
		comboBoxSourceProperty.setBackground(Color.WHITE);
		comboBoxSourceProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(comboBoxSourceProperty, "cell 1 0,growx");
		
		JCheckBox chckbxBothSame = new JCheckBox("Both Same");
		chckbxBothSame.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxBothSame.isSelected()) {
					
				}
			}
		});
		chckbxBothSame.setBackground(Color.WHITE);
		panelCommon.add(chckbxBothSame, "cell 1 1");
		
		JLabel lblTargetCommonProperty = new JLabel("Target Common Property:");
		lblTargetCommonProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(lblTargetCommonProperty, "cell 0 2,alignx trailing");
		
		JComboBox comboBoxTargetProperty = new JComboBox();
		comboBoxTargetProperty.setBackground(Color.WHITE);
		comboBoxTargetProperty.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCommon.add(comboBoxTargetProperty, "cell 1 2,growx");

	}

}
