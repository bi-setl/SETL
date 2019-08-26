package demo;

import javax.swing.JPanel;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

public class PanelEndPointInput extends JPanel {

	/**
	 * Create the panel.
	 */
	public PanelEndPointInput() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panelInput = new JPanel();
		panelInput.setBackground(Color.WHITE);
		add(panelInput, "cell 0 0,grow");
		panelInput.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel lblEndpoint = new JLabel("EndPoint:");
		lblEndpoint.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelInput.add(lblEndpoint, "cell 0 0,alignx trailing");
		
		JTextField textFieldEndPoint = new JTextField();
		textFieldEndPoint.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelInput.add(textFieldEndPoint, "cell 1 0,growx");
		textFieldEndPoint.setColumns(10);
	}

}
