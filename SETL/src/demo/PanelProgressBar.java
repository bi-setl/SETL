package demo;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JProgressBar;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;

public class PanelProgressBar extends JPanel {

	/**
	 * Create the panel.
	 */
	public PanelProgressBar() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[grow]", "[]"));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel.add(progressBar, "cell 0 0,grow");
	}
}
