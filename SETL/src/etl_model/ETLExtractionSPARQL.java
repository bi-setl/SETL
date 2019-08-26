package etl_model;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import core.LocalKBExtraction;
import core.SparqlEndPoint;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLExtractionSPARQL implements ETLOperation {

	Methods methods;
	private String rdfFilePath, sparqlQuery, fileSavingPath;
	private boolean isEndPoint;

	private final ButtonGroup buttonGroup = new ButtonGroup();

	public ETLExtractionSPARQL() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		if (isEndPoint()) {
			SparqlEndPoint sparqlEndPoint = new SparqlEndPoint();
			String result = sparqlEndPoint.fetchInfo(getRdfFilePath(), getSparqlQuery(), getFileSavingPath());
			textPane.setText(textPane.getText() + "\n" + result);
			return true;
		} else {
			try {
				LocalKBExtraction localKBExtraction = new LocalKBExtraction();
				String result = localKBExtraction.fetchInfo(getRdfFilePath(), getSparqlQuery(), getFileSavingPath());
				textPane.setText(textPane.getText() + "\n" + result);
				return true;
				
				/*Model tripleModel = ModelFactory.createDefaultModel();
				tripleModel.read(rdfFilePath);

				Query query = QueryFactory.create(sparqlQuery);
				QueryExecution qe = QueryExecutionFactory.create(query, tripleModel);
				ResultSet resultSet = ResultSetFactory.copyResults(qe.execSelect());

				ArrayList<String> selectedVars = (ArrayList<String>) resultSet.getResultVars();
				ArrayList<String> resultStringList = new ArrayList<>();
				ArrayList<String> resultRDFStringList = new ArrayList<>();

				while (resultSet.hasNext()) {

					QuerySolution querySolution = resultSet.next();
					// System.out.println(querySolution.get("s")+" "+querySolution.get("property")
					// +" "+querySolution.get("o"));

					String resultString = "";
					String resultRDFString = "";
					for (String selectedVar : selectedVars) {

						// System.out.print(querySolution.get(selectedVar)+"\t");
						resultString += querySolution.get(selectedVar).asNode().toString() + "\t";

						Node tempNode = querySolution.get(selectedVar).asNode();

						if (tempNode.isURI()) {

							resultRDFString += "<" + tempNode.toString() + "> ";

						} else {
							resultRDFString += tempNode.toString() + " ";
						}

					}

					if (selectedVars.size() == 3) {
						resultRDFString = resultRDFString.trim();
						resultRDFString += ".";
					}
					resultRDFStringList.add(resultRDFString);
					resultStringList.add(resultString);

				}
				qe.close();

				String resultString = "";
				int numOfResultString = resultStringList.size();

				for (int i = numOfResultString - 1; i >= 0; i--) {
					resultString += resultStringList.get(i) + "\n";
				}

				int numOfTriples = resultRDFStringList.size();

				String tempString = "";
				for (int i = numOfTriples - 1; i >= 0; i--) {
					tempString += resultRDFStringList.get(i) + "\n";
				}

				if (!fileSavingPath.equals("")) {
					commonMethods.writeFile(fileSavingPath, tempString);
				}

				textPane.setText(textPane.getText() + "\nExtraction Successful. File Saved: " + fileSavingPath);
				
				return true;*/
			} catch (Exception e) {

				textPane.setText(
						textPane.getText() + "\nExtraction Failed. Please provide correct input file and query");
				e.printStackTrace();
			}
			return false;
		}
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panelExtSPARQLInput = new JPanel();
		panelExtSPARQLInput.setLayout(new MigLayout("", "[][][][grow][][]", "[][grow]"));

		JLabel lblRdfFile = new JLabel("RDF File:");
		JLabel lblRDFFilePath = new JLabel("None");
		JButton btnOpen = new JButton("Open");

		JLabel lblSourceAbox = new JLabel("SPARQL End-Point: ");
		JTextField textFieldSource = new JTextField();

		JPanel panelChoice = new JPanel();
		panelChoice.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Source ABox Type",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelExtSPARQLInput.add(panelChoice, "cell 1 0 5 1, growx,pushx");
		panelChoice.setLayout(new MigLayout("", "[]", "[]"));

		JRadioButton rdbtnLocalFile = new JRadioButton("Local File");
		rdbtnLocalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					panelExtSPARQLInput.remove(lblSourceAbox);
					panelExtSPARQLInput.remove(textFieldSource);
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
				panelExtSPARQLInput.add(lblRdfFile, "cell 1 1,alignx right");
				panelExtSPARQLInput.add(lblRDFFilePath, "cell 3 1");
				panelExtSPARQLInput.add(btnOpen, "cell 5 1");

				panelExtSPARQLInput.repaint();
				panelExtSPARQLInput.revalidate();
			}
		});
		buttonGroup.add(rdbtnLocalFile);
		rdbtnLocalFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		rdbtnLocalFile.setSelected(true);
		panelChoice.add(rdbtnLocalFile, "flowy,cell 0 0");

		JRadioButton rdbtnSparqlEndpoint = new JRadioButton("SPARQL End-Point");
		rdbtnSparqlEndpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					panelExtSPARQLInput.remove(lblRdfFile);
					panelExtSPARQLInput.remove(lblRDFFilePath);
					panelExtSPARQLInput.remove(btnOpen);
				} catch (Exception e1) {
					// TODO: handle exception
					System.out.println(e1.getMessage());
				}
				panelExtSPARQLInput.add(lblSourceAbox, "cell 1 1,alignx right");
				panelExtSPARQLInput.add(textFieldSource, "cell 3 1, growx, pushx");

				panelExtSPARQLInput.repaint();
				panelExtSPARQLInput.revalidate();
			}
		});
		buttonGroup.add(rdbtnSparqlEndpoint);
		rdbtnSparqlEndpoint.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelChoice.add(rdbtnSparqlEndpoint, "cell 0 0");

		// JLabel lblRdfFile = new JLabel("RDF File:");
		// lblRdfFile.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRdfFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelExtSPARQLInput.add(lblRdfFile, "cell 1 1,alignx right");

		// JLabel lblRDFFilePath = new JLabel("None");
		lblRDFFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelExtSPARQLInput.add(lblRDFFilePath, "cell 3 1");

		if (rdfFilePath != null) {
			lblRDFFilePath.setText(rdfFilePath);
		}

		// JButton btnOpen = new JButton("Open");
		btnOpen.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelExtSPARQLInput.add(btnOpen, "cell 5 1");
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select RDF File");
				if (!filePath.equals("")) {
					lblRDFFilePath.setText(filePath);
				}

			}
		});

		lblSourceAbox.setFont(new Font("Tahoma", Font.PLAIN, 16));

		textFieldSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textFieldSource.setColumns(10);

		JLabel lblSparqlQuery = new JLabel("SPARQL Query:");
		lblSparqlQuery.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSparqlQuery.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelExtSPARQLInput.add(lblSparqlQuery, "cell 1 2,alignx right");

		JTextArea textAreaSPARQLQuery = new JTextArea();
		textAreaSPARQLQuery.setFont(new Font("Monospaced", Font.PLAIN, 16));
		textAreaSPARQLQuery.setColumns(70);
		textAreaSPARQLQuery.setRows(10);
		JScrollPane queryScrollPane = new JScrollPane(textAreaSPARQLQuery);
		panelExtSPARQLInput.add(queryScrollPane, "cell 3 2");

		if (sparqlQuery != null) {
			try {
				sparqlQuery = URLDecoder.decode(sparqlQuery, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			textAreaSPARQLQuery.setText(sparqlQuery);
		}

		JLabel lblRdfSavingFile = new JLabel("Saving File:");
		// lblRdfFile.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRdfSavingFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelExtSPARQLInput.add(lblRdfSavingFile, "cell 1 3,alignx right");

		JTextField lblSavingFilePath = new JTextField();
		lblSavingFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelExtSPARQLInput.add(lblSavingFilePath, "cell 3 3,growx,pushx");

		if (fileSavingPath != null) {
			lblSavingFilePath.setText(fileSavingPath);
		}

		JButton btnOpenSavingFile = new JButton("Open");
		btnOpenSavingFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelExtSPARQLInput.add(btnOpenSavingFile, "cell 5 3");
		btnOpenSavingFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String defaultName = "Ext_SPARQL";
				defaultName += "_" + methods.getDateTime() + ".n3";

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to extracted file");

				if (!filePath.equals("")) {
					lblSavingFilePath.setText(filePath);
				}

			}
		});

		int option = JOptionPane.showConfirmDialog(null, panelExtSPARQLInput,
				"Please provide inputs for Extraction from SPARQL", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			String path = "";
			if (rdbtnLocalFile.isSelected()) {
				path = lblRDFFilePath.getText().toString();
				setEndPoint(false);
			} else {
				path = textFieldSource.getText().toString().trim();
				setEndPoint(true);
			}
			String query = textAreaSPARQLQuery.getText().toString();
			String savingPath = lblSavingFilePath.getText().toString();

			ArrayList inputList = new ArrayList<>();
			inputList.add(path);
			inputList.add(query);
			inputList.add(savingPath);

			if (!methods.hasEmptyString(inputList)) {

				if (path.equals("None")) {
					methods.showDialog("Please select the RDF file to extract");
					return false;
				}

				if (savingPath.equals("")) {
					methods.showDialog("Please select path to save the extracted file");
					return false;
				}

				setRdfFilePath(path);
				setSparqlQuery(query);
				setFileSavingPath(savingPath);

				inputParamsMap.get(SPARQL_FILE).add(getFileSavingPath());

				return true;
			}

		} else {
			methods.showDialog("Please provide input for Extraction from SPARQL");
			return false;
		}

		return false;
	}

	public String getRdfFilePath() {
		return rdfFilePath;
	}

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

	public String getSparqlQuery() {
		return sparqlQuery;
	}

	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}

	public String getFileSavingPath() {
		return fileSavingPath;
	}

	public void setFileSavingPath(String fileSavingPath) {
		this.fileSavingPath = fileSavingPath;
	}

	public boolean isEndPoint() {
		return isEndPoint;
	}

	public void setEndPoint(boolean isEndPoint) {
		this.isEndPoint = isEndPoint;
	}
}
