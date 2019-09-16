package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import model.DBTable;
import model.RMLProcessor;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;

public class PanelRMLRDF extends JPanel {

	/**
	 * Create the panel.
	 */
	
	private JPanel mainContainer;
	private CardLayout appLayout;
	ArrayList<DBTable> allDBTableStructure;
	String baseURL;
	private RMLProcessor rmlProcessor;
	private String rmlFileString;
	JTextArea textAreaRMLRDF;
	
	public PanelRMLRDF(JPanel mainContainerPanel, String baseURL, String rmlString) {
		
		this.mainContainer = mainContainerPanel;
		this.appLayout = (CardLayout) mainContainer.getLayout();
		this.baseURL = baseURL;
		this.rmlFileString = rmlString;
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		add(panelButtons, BorderLayout.SOUTH);
		
		JButton buttonRMLRDFBack = new JButton("< Back");
		buttonRMLRDFBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		buttonRMLRDFBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//JOptionPane.showMessageDialog(null, "Botton Hitted");
				appLayout.show(mainContainer, PanelDBSummary.DB_RML_PANEL_KEY);
			}
		});
		panelButtons.add(buttonRMLRDFBack);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				PrintWriter out;
				try {
					// Create a file chooser
					final JFileChooser fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);

					String rmlFilePath = "";

					int returnVal = fileChooser.showOpenDialog(mainContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {

						File rmlFile = fileChooser.getSelectedFile();
						fileChooser.setApproveButtonText("Save");
						fileChooser.setDialogTitle("Select directory to save");
						rmlFilePath = rmlFile.getPath();
						// fileName = csvFile.getName();
					}

					out = new PrintWriter(rmlFilePath + "/DB_RML_RDF.n3");
					out.println(textAreaRMLRDF.getText().toString());
					out.close();

				} catch (FileNotFoundException error) {

					JOptionPane.showMessageDialog(null, "File Saving Failed");
				}
				
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelButtons.add(btnSave);
		
		JLabel lblRmlMappingOf = new JLabel("RML Mapping of The Database");
		lblRmlMappingOf.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblRmlMappingOf.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblRmlMappingOf, BorderLayout.NORTH);
		
		JScrollPane scrollPaneRMLRDF = new JScrollPane();
		add(scrollPaneRMLRDF, BorderLayout.CENTER);
		
		textAreaRMLRDF = new JTextArea();
		textAreaRMLRDF.setFont(new Font("Monospaced", Font.PLAIN, 16));
		scrollPaneRMLRDF.setViewportView(textAreaRMLRDF);
		
		rmlProcessor = new RMLProcessor();
		
		String[] lines = rmlFileString.split("\n");
		
		ArrayList<String> rmlFileLines = new ArrayList<>();
		
		int size = lines.length;
		for(int i=0; i<size; i++){
			rmlFileLines.add(lines[i]);
			
		}
		
		textAreaRMLRDF.setText("Triple Generation in Progress.\r\nPlease wait...");
		JOptionPane.showMessageDialog(null, "Triple Generation in Progress.\nPlease wait...");
		ArrayList<String> rdfTriples  = rmlProcessor.getRDFTriples(rmlFileLines);
		
		String rdfTriplesString = "";
		
		for(String str: rdfTriples){
			
			rdfTriplesString+=str+"\n";
			
		}
		textAreaRMLRDF.setText("");
		textAreaRMLRDF.setText(rdfTriplesString);
	}

}
