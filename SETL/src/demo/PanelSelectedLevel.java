package demo;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class PanelSelectedLevel extends JPanel {

	/**
	 * Create the panel.
	 */
	public PanelSelectedLevel() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		// for (int i = 0; i < 10; i++) {
			JPanel panel = new JPanel();
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panel.setBackground(Color.WHITE);
			panel.setLayout(new MigLayout("", "[grow][]", "[][][]"));
			
			JLabel lblSelectedLevel = new JLabel("Selected Level");
			lblSelectedLevel.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(lblSelectedLevel, "cell 0 0,grow");
			
			JButton btnRemove = new JButton("Remove");
			btnRemove.setMargin(new Insets(10, 10, 10, 10));
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnRemove.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(btnRemove, "cell 1 0 1 3,alignx center,aligny center");
			
			JLabel lblFilterCondition = new JLabel("Filter Condition");
			lblFilterCondition.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
			panel.add(lblFilterCondition, "cell 0 1,grow");
			
			panel_1.add(panel);
			
			JLabel lblPropertiesForSelection = new JLabel("Properties for Selection:");
			lblPropertiesForSelection.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
			panel.add(lblPropertiesForSelection, "cell 0 2");
		 // }
	}

}
