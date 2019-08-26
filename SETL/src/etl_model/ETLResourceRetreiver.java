package etl_model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import core.Independence;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLResourceRetreiver implements ETLOperation {

	private Methods methods;
	final static String DBPEDIA_RESOURCE_RETRIEVER = "DBpedia Resource Retriever";
	final static String SPARQL_END_RESOURCE_RETRIEVER = "SPARQL Resource Retriever";
	final static String LOCAL_KB_RESOURCE_RETRIEVER = "Local KB Resource Retriever";

	final static String KEY_WORD = "Key Word:";
	final static String DBPEDIA_DATA_FILE = "DBpedia Data File:";
	final static String RESOURCE_FILE = "Resource File:";
	final static String PROPERTY_WEIGHT_FILE = "Property Weight File:";
	final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";
	final static String RDF_FILE = "RDF File:";

	private String keyWord, dbPediaDataFilePath, resourceFilePath, rdfFilePath;
	private int numOfHit, operationType;
	private JLabel lblKeyword, lblnumOfHit, lblDBPediaDataFile, lblRdfFile, lblResourceFile;
	private JButton btnNewDBPediaDataFile, btnNewResFile, btnOpenRDF;

	// GUI components

	JComboBox comboBoxOperation, comboBoxDBpediaData, comboBoxRDFFile;
	JTextField txtKeyword, textFieldNumOfHit;

	public ETLResourceRetreiver() {

		operationType = -1;
		keyWord = "";
		dbPediaDataFilePath = "";
		resourceFilePath = "";
		rdfFilePath = "";
		numOfHit = -1;
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {

		Independence independence = new Independence();
		
		boolean status = false;
		
		switch (operationType) {
			
			case 0:
				status = independence.dbpediaResourceRetriever(keyWord, numOfHit, dbPediaDataFilePath, resourceFilePath);	
				break;
			case 1:
				
				status = independence.sparqlEndResourceRetriever(keyWord, numOfHit, resourceFilePath);
				break;
			case 2:
				status = independence.localKBResourceRetriever(rdfFilePath, numOfHit, resourceFilePath);
				break;
		}
		
		if(status){
			textPane.setText(textPane.getText().toString()+"\nResource Retrieved. Saved as: "+ resourceFilePath);
		}else{
			textPane.setText(textPane.getText().toString()+"\nResource Retriever Failed");
		}
		return status;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panelResourceExtractor = new JPanel();
		panelResourceExtractor.setLayout(new MigLayout("", "[grow][][grow]", "[][][][10][][][][][]"));

		JLabel lblOperation = new JLabel("Operation:");
		lblOperation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblOperation, "cell 0 0,alignx right");

		comboBoxOperation = new JComboBox();
		comboBoxOperation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxOperation.setModel(new DefaultComboBoxModel(new String[] { "DBpedia Resource Retriever",
				"SPARQL End Resource Retriever", "Local KB Resource Retriever" }));

		panelResourceExtractor.add(comboBoxOperation, "cell 2 0,growx");

		if (this.operationType != -1) {
			comboBoxOperation.setSelectedIndex(operationType);
		}

		lblKeyword = new JLabel("Key Word:");
		lblKeyword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblKeyword, "cell 0 1,alignx right");

		txtKeyword = new JTextField();
		txtKeyword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(txtKeyword, "cell 2 1,growx");
		txtKeyword.setColumns(60);

		lblnumOfHit = new JLabel("Number of Hit:");
		lblnumOfHit.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblnumOfHit, "cell 0 2,alignx right");

		textFieldNumOfHit = new JTextField();
		textFieldNumOfHit.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(textFieldNumOfHit, "cell 2 2,growx");

		lblDBPediaDataFile = new JLabel("DBpedia Data File:");
		lblDBPediaDataFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblDBPediaDataFile, "cell 0 3,alignx right");

		comboBoxDBpediaData = new JComboBox();
		comboBoxDBpediaData.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(comboBoxDBpediaData, "flowx,cell 2 3,growx");

		LinkedHashSet dbPediaDataNameSet = inputParamsMap.get(DBPEDIA_DATA_FILE);
		ArrayList<String> dbpediaDataNameList = new ArrayList<>(dbPediaDataNameSet);
		comboBoxDBpediaData.setModel(new DefaultComboBoxModel<>(dbpediaDataNameList.toArray()));

		if (!this.dbPediaDataFilePath.equals("")) {
			comboBoxDBpediaData.setSelectedItem(this.dbPediaDataFilePath);
		}

		lblRdfFile = new JLabel("RDF File:");
		lblRdfFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblRdfFile, "cell 0 4,alignx right");

		comboBoxRDFFile = new JComboBox<String>();
		comboBoxRDFFile.setFont(new Font("Tahoma", Font.PLAIN, 16));

		LinkedHashSet<String> rdfFilePathSet = inputParamsMap.get(RDF_FILE);
		ArrayList<String> rdfFilePathList = new ArrayList<>(rdfFilePathSet);
		comboBoxRDFFile.setModel(new DefaultComboBoxModel<>(rdfFilePathList.toArray()));

		if (!this.rdfFilePath.equals("")) {
			comboBoxRDFFile.setSelectedItem(this.rdfFilePath);
		}
		panelResourceExtractor.add(comboBoxRDFFile, "flowx,cell 2 4,growx");

		lblResourceFile = new JLabel("Resource File:");
		lblResourceFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelResourceExtractor.add(lblResourceFile, "cell 0 5,alignx right");

		btnNewDBPediaDataFile = new JButton("New");
		btnNewDBPediaDataFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelResourceExtractor.add(btnNewDBPediaDataFile, "cell 2 3");

		btnNewDBPediaDataFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				// String[] str = new String[]{"Hello"};

				String defaultName = methods.getDateTime() + "_DBPediaData.json";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save DBPedia Data File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxDBpediaData.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxDBpediaData.setModel(comboBoxModel);
					comboBoxDBpediaData.setSelectedItem(filePath);
					// JOptionPane.showMessageDialog(null, "Path: "+ filePath +
					// " Name: "+ commonMethods.getFileName(filePath));
				}
			}
		});

		JLabel resourceFilePath = new JLabel("None");
		resourceFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));

		if (!this.resourceFilePath.equals("")) {
			resourceFilePath.setText(this.resourceFilePath);
		}
		panelResourceExtractor.add(resourceFilePath, "flowx,cell 2 5,growx");

		btnNewResFile = new JButton("New");
		btnNewResFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelResourceExtractor.add(btnNewResFile, "cell 2 5");

		btnNewResFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				// String[] str = new String[]{"Hello"};

				String midName = "";
				int selectedInd = comboBoxOperation.getSelectedIndex();
				
				if(selectedInd == 0){
					midName = "DBpedia";
				}else if(selectedInd == 1){
					midName = "SPARQLEnd";
				}else{
					midName = "LocalKB";
				}
				
				String defaultName = methods.getDateTime() +"_"+midName+"_Resources.txt";

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save Resources File");

				if (!filePath.equals("")) {
					resourceFilePath.setText(filePath);
				}

			}
		});

		btnOpenRDF = new JButton("Open");
		btnOpenRDF.setFont(new Font("Tahoma", Font.BOLD, 16));

		btnOpenRDF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select RDF File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxRDFFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxRDFFile.setModel(comboBoxModel);
					comboBoxRDFFile.setSelectedItem(filePath);
					// JOptionPane.showMessageDialog(null, "Path: "+ filePath +
					// " Name: "+ commonMethods.getFileName(filePath));
				}

			}
		});
		panelResourceExtractor.add(btnOpenRDF, "cell 2 4");

		// set the default setting
		lblRdfFile.setEnabled(false);
		comboBoxRDFFile.setEnabled(false);
		btnOpenRDF.setEnabled(false);

		comboBoxOperation.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int selectedIndex = comboBoxOperation.getSelectedIndex();
				updateInputDailog(selectedIndex);
			}
		});

		// Set the GUIs
		setInputDailogGUI();

		updateInputDailog(this.operationType);

		int confirmation = JOptionPane.showConfirmDialog(null, panelResourceExtractor,
				"Please Input Values for Resource Retriever.", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			int selectedOperationIndex = comboBoxOperation.getSelectedIndex();

			if (selectedOperationIndex == 0) {

				try {
					String dbPediaDataFilePath = comboBoxDBpediaData.getSelectedItem().toString();
					String keyword = txtKeyword.getText().toString();
					String resourceFilePathString = resourceFilePath.getText().toString();
					String temp = textFieldNumOfHit.getText().toString();

					if (!methods.isIntParseable(temp)) {
						methods.showDialog("Num of Hit must be a integer.");
						return false;
					}

					int numOfHit = methods.getInt(temp);

					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(keyword);
					inputList.add(resourceFilePathString);
					inputList.add(dbPediaDataFilePath);

					if (!methods.hasEmptyString(inputList)) {

						setDbPediaDataFilePath(dbPediaDataFilePath);
						setKeyWord(keyword);
						setOperationType(selectedOperationIndex);
						setResourceFilePath(resourceFilePathString);
						setNumOfHit(numOfHit);

						// addding items to the input params list to show on
						// next openup
						inputParamsMap.get(DBPEDIA_DATA_FILE).add(dbPediaDataFilePath);
						inputParamsMap.get(RESOURCE_FILE).add(resourceFilePathString);

					} else {
						methods.showDialog("Please provide correct value to all input.");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();

					methods.showDialog("Please provide correct value to all input.");
					return false;
				}

			} else if (selectedOperationIndex == 1) {

				try {
					String keyword = txtKeyword.getText().toString();
					String resourceFilePathString = resourceFilePath.getText().toString();
					String temp = textFieldNumOfHit.getText().toString();

					if (!methods.isIntParseable(temp)) {
						methods.showDialog("Num of Hit must be a integer.");
						return false;
					}

					int numOfHit = methods.getInt(temp);

					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(keyword);
					inputList.add(resourceFilePathString);
					if (!methods.hasEmptyString(inputList)) {

						setKeyWord(keyword);
						setOperationType(selectedOperationIndex);
						setNumOfHit(numOfHit);
						setResourceFilePath(resourceFilePathString);

						inputParamsMap.get(RESOURCE_FILE).add(resourceFilePathString);

					} else {
						methods.showDialog("Please provide correct value to all input.");
						return false;
					}
				} catch (Exception e) {
				
					e.printStackTrace();
					methods.showDialog("Please provide correct value to all input.");
					return false;
				}

			} else if (selectedOperationIndex == 2) {

				try {
					String rdfFilePath = comboBoxRDFFile.getSelectedItem().toString();
					String resourceFilePathString = resourceFilePath.getText().toString();
					String temp = textFieldNumOfHit.getText().toString();

					if (!methods.isIntParseable(temp)) {
						methods.showDialog("Num of Hit must be a integer.");
						return false;
					}

					int numOfHit = methods.getInt(temp);

					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(rdfFilePath);
					inputList.add(resourceFilePathString);
					if (!methods.hasEmptyString(inputList)) {

						setOperationType(selectedOperationIndex);
						setNumOfHit(numOfHit);
						setResourceFilePath(resourceFilePathString);
						setRdfFilePath(rdfFilePath);

						inputParamsMap.get(RESOURCE_FILE).add(resourceFilePathString);
						inputParamsMap.get(RDF_FILE).add(rdfFilePath);

					} else {
						methods.showDialog("Please provide correct value to all input.");
						return false;
					}
				} catch (Exception e) {
					
					e.printStackTrace();
					methods.showDialog("Please provide correct value to all input.");
					return false;

				}

			}

		} else {
			// commonMethods.showMessage("Please provide value to all input.");
			return false;
		}

		return true;
	}

	private void setInputDailogGUI() {

		if (!this.keyWord.equals("")) {
			txtKeyword.setText(this.keyWord);
		}

		if (numOfHit != -1) {
			String s = "" + numOfHit;
			textFieldNumOfHit.setText(s);
		}

	}

	//change the gui according to selection
	private void updateInputDailog(int selectedIndex) {

		if (selectedIndex == 1) {

			// SPARQL end resource retriever

			lblDBPediaDataFile.setEnabled(false);
			comboBoxDBpediaData.setEnabled(false);
			btnNewDBPediaDataFile.setEnabled(false);

			lblRdfFile.setEnabled(false);
			comboBoxRDFFile.setEnabled(false);
			btnOpenRDF.setEnabled(false);

			lblKeyword.setEnabled(true);
			txtKeyword.setEnabled(true);

		} else if (selectedIndex == 2) {

			// Local KB resource retriever

			lblDBPediaDataFile.setEnabled(false);
			comboBoxDBpediaData.setEnabled(false);
			btnNewDBPediaDataFile.setEnabled(false);

			lblRdfFile.setEnabled(true);
			comboBoxRDFFile.setEnabled(true);
			btnOpenRDF.setEnabled(true);

			lblKeyword.setEnabled(false);
			txtKeyword.setEnabled(false);

		} else {

			// DBPedia data resource retriever
			lblDBPediaDataFile.setEnabled(true);
			comboBoxDBpediaData.setEnabled(true);
			btnNewDBPediaDataFile.setEnabled(true);

			lblRdfFile.setEnabled(false);
			comboBoxRDFFile.setEnabled(false);
			btnOpenRDF.setEnabled(false);

			lblKeyword.setEnabled(true);
			txtKeyword.setEnabled(true);
		}

	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getDbPediaDataFilePath() {
		return dbPediaDataFilePath;
	}

	public void setDbPediaDataFilePath(String dbPediaDataFilePath) {
		this.dbPediaDataFilePath = dbPediaDataFilePath;
	}

	public String getRdfFilePath() {
		return rdfFilePath;
	}

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

	public int getNumOfHit() {
		return numOfHit;
	}

	public void setNumOfHit(int numOfHit) {
		this.numOfHit = numOfHit;
	}

	public String getResourceFilePath() {
		return resourceFilePath;
	}

	public void setResourceFilePath(String resourceFilePath) {
		this.resourceFilePath = resourceFilePath;
	}

}
