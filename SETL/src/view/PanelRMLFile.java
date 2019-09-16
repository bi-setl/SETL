package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import model.DBTable;
import model.RMLFile;
import model.TripleMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class PanelRMLFile extends JPanel {

	/**
	 * Create the panel.
	 */

	private JPanel mainContainer;
	private CardLayout appLayout;
	ArrayList<DBTable> allDBTableStructure;
	String baseURL;
	String rmlFileString;

	PanelRMLRDF rmlRDFPanel;

	RMLFile rmlFile;

	JTextArea textAreaRML;

	final static String DB_RML_RDF_PANEL_KEY = "PanelRMLRDF";

	public PanelRMLFile(JPanel mainContainerPanel, ArrayList<DBTable> tableConfigurations, String baseURL) {

		this.mainContainer = mainContainerPanel;
		this.appLayout = (CardLayout) mainContainer.getLayout();
		this.allDBTableStructure = tableConfigurations;
		this.baseURL = baseURL;

		setLayout(new BorderLayout(0, 0));

		JLabel lblRmlFile = new JLabel("RML File");
		lblRmlFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblRmlFile.setFont(new Font("Tahoma", Font.BOLD, 20));
		add(lblRmlFile, BorderLayout.NORTH);

		JPanel panelRMLButtons = new JPanel();
		add(panelRMLButtons, BorderLayout.SOUTH);

		JButton btnBack = new JButton("< Back");
		btnBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				appLayout.show(mainContainer, PanelDatabase.DB_SUMMERY_PANEL_KEY);
			}
		});
		panelRMLButtons.add(btnBack);

		JButton btnEdit = new JButton("Edit");
		btnEdit.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelRMLButtons.add(btnEdit);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				PrintWriter out;
				try {
					//Create a file chooser
					final JFileChooser fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
					
					String rmlFilePath = "";
					
					int returnVal = fileChooser.showOpenDialog(mainContainer);
					if(returnVal == JFileChooser.APPROVE_OPTION){
						
						File rmlFile = fileChooser.getSelectedFile();
						rmlFilePath = rmlFile.getPath();
						//fileName = csvFile.getName();
					}

					out = new PrintWriter(rmlFilePath+"/DB_RML.ttl");
					out.println(rmlFileString);
					out.close();
					
				} catch (FileNotFoundException e) {

					JOptionPane.showMessageDialog(null, "File Saving Failed");
				}

			}
		});
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelRMLButtons.add(btnSave);

		JButton btnTransform = new JButton("Transform >");
		btnTransform.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnTransform.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String rmlString = textAreaRML.getText().toString();
				rmlRDFPanel = new PanelRMLRDF(mainContainer, baseURL,rmlString );
				mainContainer.add(rmlRDFPanel, DB_RML_RDF_PANEL_KEY);
				appLayout.show(mainContainerPanel, DB_RML_RDF_PANEL_KEY);
			}
		});
		panelRMLButtons.add(btnTransform);

		JScrollPane scrollPaneRMLFile = new JScrollPane();
		add(scrollPaneRMLFile, BorderLayout.CENTER);

		textAreaRML = new JTextArea();
		textAreaRML.setTabSize(4);
		textAreaRML.setFont(new Font("Monospaced", Font.PLAIN, 16));
		scrollPaneRMLFile.setViewportView(textAreaRML);

		rmlFile = new RMLFile();

		displayRMLFile();
	}

	private void printRMLFile() {

		ArrayList<TripleMap> tripleMaps = rmlFile.getTripleMapsTC(allDBTableStructure, baseURL);

		System.out.println("Size of Triple Maps: " + tripleMaps.size());
		 for(TripleMap tripleMap: tripleMaps){
		
		 tripleMap.printTripleMap();
		 }

	}

	private void displayRMLFile() {

		ArrayList<TripleMap> tripleMaps = rmlFile.getTripleMapsTC(allDBTableStructure, baseURL);

		rmlFileString = rmlFile.getRMLFile(tripleMaps, baseURL);
		textAreaRML.setText(rmlFileString);

	}

}
