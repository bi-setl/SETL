package etl_model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import core.Independence;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLSBagGenerator implements ETLOperation {

	private JLabel lblResourceFilePath, lblRdfFile, lblDBPediaDataFile, lblSBagFile, lblSBagFilePath;
	private JButton btnOpenResourceFile, btnOpenRDFFile, btnOpenDBpediaDataFile, btnOpenSBagFile;
	private JComboBox comboBoxResourceFilePath, comboBoxRDFFilePath, comboBoxDBpediDataFilePath;
	private Methods methods;

	private String resourceFilePath, rdfFilePath, dbpediaDataFilePath, semanticBagFilePath;
	private int operationType;

	final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";
	final static String RDF_FILE = "RDF File:";
	final static String DBPEDIA_DATA_FILE = "DBpedia Data File:";
	final static String RESOURCE_FILE = "Resource File:";

	public ETLSBagGenerator() {
		super();
		methods = new Methods();
		resourceFilePath = "";
		rdfFilePath = "";
		dbpediaDataFilePath = "";
		semanticBagFilePath = "";

		operationType = -1;
	}

	@Override
	public boolean execute(JTextPane textPane) {

		Independence independence = new Independence();
		boolean status = false;
		switch (operationType) {
			
			case 0:
				status = independence.localKBsemanticBagGenerator(resourceFilePath, rdfFilePath, semanticBagFilePath);
				break;
//			case 1:
//				
//				status = independence.dbpediaSemanticBagGenerator(dbpediaDataFilePath, resourceFilePath, semanticBagFilePath);
//				break;
			case 1:
				status = independence.dbpediaSemanticBagGenerator(dbpediaDataFilePath, semanticBagFilePath);
				break;
		}

		if(status){
			textPane.setText(textPane.getText().toString()+"\nSemantic Bag Generated. Saved as: "+ semanticBagFilePath);
		}else{
			textPane.setText(textPane.getText().toString()+"\nSemantic Bag Generation Failed");
		}
		
		return status;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panelSBagGeneration = new JPanel();
		panelSBagGeneration.setLayout(new MigLayout("", "[][][700,grow]", "[][][][][][][10][][][][][]"));

		JLabel lblOperation = new JLabel("Operation:");
		lblOperation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblOperation, "cell 0 0,alignx right");

		JComboBox comboBoxOperation = new JComboBox();
		comboBoxOperation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxOperation.setModel(new DefaultComboBoxModel(new String[] { "Local KB Resource Semantic Bag Generation", "DBpedia Data Semantic Bag Generation" }));
		//"DBpedia Resource Semantic Bag Generation"
		panelSBagGeneration.add(comboBoxOperation, "cell 2 0,growx");

		if (operationType != -1) {
			comboBoxOperation.setSelectedIndex(operationType);
		}

		comboBoxOperation.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int selectedIndex = comboBoxOperation.getSelectedIndex();
				updateInputDailog(selectedIndex);
			}
		});

		lblResourceFilePath = new JLabel("Resource File:");
		lblResourceFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblResourceFilePath, "cell 0 2,alignx right");

		comboBoxResourceFilePath = new JComboBox();
		comboBoxResourceFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(comboBoxResourceFilePath, "cell 2 2,growx");

		LinkedHashSet resourceFilePathSet = inputParamsMap.get(RESOURCE_FILE);
		ArrayList<String> resourceFilePathList = new ArrayList<>(resourceFilePathSet);
		comboBoxResourceFilePath.setModel(new DefaultComboBoxModel<>(resourceFilePathList.toArray()));

		if (!resourceFilePath.equals("")) {
			comboBoxResourceFilePath.setSelectedItem(resourceFilePath);
		}
		
		
//		if (!resourceFilePath.equals("")) {
//			ArrayList<String> s = new ArrayList<>();
//			s.add(resourceFilePath);
//			DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(s.toArray());
//			comboBoxResourceFilePath.setModel(defaultComboBoxModel);
//		}

		btnOpenResourceFile = new JButton("Open");
		btnOpenResourceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select Resource File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxDBpediDataFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxResourceFilePath.setModel(comboBoxModel);
					comboBoxResourceFilePath.setSelectedItem(filePath);
				}

			}
		});
		btnOpenResourceFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSBagGeneration.add(btnOpenResourceFile, "cell 2 2");
		
		
		lblRdfFile = new JLabel("RDF File:");
		lblRdfFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblRdfFile, "cell 0 3,alignx right");

		comboBoxRDFFilePath = new JComboBox();
		comboBoxRDFFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(comboBoxRDFFilePath, "flowx,cell 2 3,growx");

		LinkedHashSet rdfFileHashSet = inputParamsMap.get(RDF_FILE);
		ArrayList<String> rdfFileList = new ArrayList<>(rdfFileHashSet);
		comboBoxRDFFilePath.setModel(new DefaultComboBoxModel<>(rdfFileList.toArray()));

		if (!rdfFilePath.equals("")) {
			comboBoxRDFFilePath.setSelectedItem(rdfFilePath);
		}

		lblDBPediaDataFile = new JLabel("DBpedia Data File:");
		lblDBPediaDataFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblDBPediaDataFile, "cell 0 4,alignx right");

		comboBoxDBpediDataFilePath = new JComboBox();
		comboBoxDBpediDataFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(comboBoxDBpediDataFilePath, "flowx,cell 2 4,growx");

		LinkedHashSet dbPediaDataFileHashSet = inputParamsMap.get(DBPEDIA_DATA_FILE);
		ArrayList<String> dbPediaDataFileList = new ArrayList<>(dbPediaDataFileHashSet);
		comboBoxDBpediDataFilePath.setModel(new DefaultComboBoxModel<>(dbPediaDataFileList.toArray()));

		if (!dbpediaDataFilePath.equals("")) {
			comboBoxDBpediDataFilePath.setSelectedItem(dbpediaDataFilePath);
		}

		lblSBagFile = new JLabel("Semantic Bag File:");
		lblSBagFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblSBagFile, "cell 0 5,alignx right");

		lblSBagFilePath = new JLabel("None");
		lblSBagFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelSBagGeneration.add(lblSBagFilePath, "flowx,cell 2 5,growx");

		if (!semanticBagFilePath.equals("")) {
			lblSBagFilePath.setText(semanticBagFilePath);
		}

		btnOpenRDFFile = new JButton("Open");
		btnOpenRDFFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select RDF File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxRDFFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxRDFFilePath.setModel(comboBoxModel);
					comboBoxRDFFilePath.setSelectedItem(filePath);
				}

			}
		});
		btnOpenRDFFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSBagGeneration.add(btnOpenRDFFile, "cell 2 3");

		btnOpenDBpediaDataFile = new JButton("Open");
		btnOpenDBpediaDataFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select DBpedia Data  File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxDBpediDataFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxDBpediDataFilePath.setModel(comboBoxModel);
					comboBoxDBpediDataFilePath.setSelectedItem(filePath);
				}

			}
		});
		btnOpenDBpediaDataFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSBagGeneration.add(btnOpenDBpediaDataFile, "cell 2 4");

		btnOpenSBagFile = new JButton("Open");
		btnOpenSBagFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String midName = "";
				int selectedInd = comboBoxOperation.getSelectedIndex();
				
				if(selectedInd == 0){
					midName = "LocalKB";
				}else if(selectedInd == 1){
					midName = "DBpediaResource";
				}else{
					midName = "DBpedia";
				}

				String defaultName = methods.getDateTime() + "_"+midName+"_SBag.txt";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save semantic bag File");

				if (!filePath.equals("")) {

					lblSBagFilePath.setText(filePath);
				}

			}
		});
		btnOpenSBagFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSBagGeneration.add(btnOpenSBagFile, "cell 2 5");

//		btnOpenResourceFile = new JButton("Open Resource File");
//		btnOpenResourceFile.setFont(new Font("Tahoma", Font.BOLD, 16));
//		btnOpenResourceFile.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//
//				String filePath = commonMethods.getFilePath("Select Resource File");
//
//				if (!filePath.equals("")) {
//
//					AllFileOperations fileOperations = new AllFileOperations();
//					ArrayList<Object> resources = fileOperations.readFromBinaryFile(filePath);
//					
//					ArrayList<String> resourceStringList = new ArrayList<>();
//					for(Object object: resources){
//						
//						Single single = (Single) object;
//						resourceStringList.add(single.getString());
//						
//					}
//					
//					DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(resourceStringList.toArray());
//					comboBoxResourceFilePath.setModel(comboBoxModel);
//				}
//			}
//		});
//		panelSBagGeneration.add(btnOpenResourceFile, "cell 2 1,growx");

		// visualize guis according to operation type
		updateInputDailog(this.operationType);

		int confirmation = JOptionPane.showConfirmDialog(null, panelSBagGeneration,
				"Please Input Values for Resource Retriever.", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			int selectedOperationIndex = comboBoxOperation.getSelectedIndex();

			if (selectedOperationIndex == 0) {

				try {

					String resourceFP = comboBoxResourceFilePath.getSelectedItem().toString();
					String rdfFilePathString = comboBoxRDFFilePath.getSelectedItem().toString();
					String semanticBagFilePathString = lblSBagFilePath.getText().toString();

					// Check null String
					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(resourceFP);
					inputList.add(rdfFilePathString);
					inputList.add(semanticBagFilePathString);

					if (!methods.hasEmptyString(inputList)) {
						if (semanticBagFilePathString.equals("None")) {
							methods.showDialog("Please give path for saving semantic bag file.");
							return false;
						}

						setOperationType(selectedOperationIndex);
						setResourceFilePath(resourceFP);
						setRdfFilePath(rdfFilePathString);
						setSemanticBagFilePath(semanticBagFilePathString);

						// addding items to the input params list to show on
						// next open-up
						inputParamsMap.get(RDF_FILE).add(rdfFilePathString);
						inputParamsMap.get(SEMANTIC_BAG_FILE).add(semanticBagFilePathString);
						inputParamsMap.get(RESOURCE_FILE).add(resourceFilePath);

					} else {
						methods.showDialog("Please provide correct value to all input.");
						return false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					methods.showDialog("Please provide correct value to all input.");
					return false;
				}

//			} else if (selectedOperationIndex == 1) {
//
//				try {
//
//					String resource = comboBoxResourceFilePath.getSelectedItem().toString();
//					String dbpediaDataFilePathString = comboBoxDBpediDataFilePath.getSelectedItem().toString();
//					String semanticBagFilePathString = lblSBagFilePath.getText().toString();
//
//					ArrayList<String> inputList = new ArrayList<>();
//					inputList.add(resource);
//					inputList.add(dbpediaDataFilePathString);
//					inputList.add(semanticBagFilePathString);
//
//					if (!commonMethods.hasEmptyString(inputList)) {
//
//						if (semanticBagFilePathString.equals("None")) {
//							commonMethods.showMessage("Please give path for saving semantic bag file.");
//							return false;
//						}
//
//						setResource(resource);
//						setOperationType(selectedOperationIndex);
//						setDbpediaDataFilePath(dbpediaDataFilePathString);
//						setSemanticBagFilePath(semanticBagFilePathString);
//
//						inputParamsMap.get(DBPEDIA_DATA_FILE).add(dbpediaDataFilePathString);
//						inputParamsMap.get(SEMANTIC_BAG_FILE).add(semanticBagFilePathString);
//
//					} else {
//						commonMethods.showMessage("Please provide correct value to all input.");
//						return false;
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					commonMethods.showMessage("Please provide correct value to all input.");
//					return false;
//				}

			} else if (selectedOperationIndex == 1) {

				try {

					String dbpediaDataFilePathString = comboBoxDBpediDataFilePath.getSelectedItem().toString();
					String semanticBagFilePathString = lblSBagFilePath.getText().toString();

					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(dbpediaDataFilePathString);
					inputList.add(semanticBagFilePathString);

					if (!methods.hasEmptyString(inputList)) {

						if (semanticBagFilePathString.equals("None")) {
							methods.showDialog("Please give path for saving semantic bag file.");
							return false;

						}

						setOperationType(selectedOperationIndex);
						setDbpediaDataFilePath(dbpediaDataFilePathString);
						setSemanticBagFilePath(semanticBagFilePathString);

						inputParamsMap.get(DBPEDIA_DATA_FILE).add(dbpediaDataFilePathString);
						inputParamsMap.get(SEMANTIC_BAG_FILE).add(semanticBagFilePathString);

					} else {
						methods.showDialog("Please provide correct value to all input.");
						return false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

	private void updateInputDailog(int selectedIndex) {

//		if (selectedIndex == 1) {
//
//			// DBPedia data Resource Semantic bag generation
//
//			btnOpenResourceFile.setEnabled(true);
//			lblResourceFilePath.setEnabled(true);
//			comboBoxResourceFilePath.setEnabled(true);
//
//			lblRdfFile.setEnabled(false);
//			comboBoxRDFFilePath.setEnabled(false);
//			btnOpenRDFFile.setEnabled(false);
//
//			lblDBPediaDataFile.setEnabled(true);
//			comboBoxDBpediDataFilePath.setEnabled(true);
//			btnOpenDBpediaDataFile.setEnabled(true);
//
//		} else
		if (selectedIndex == 1) {

			// DbPedia data semantic bag generator
			
			btnOpenResourceFile.setEnabled(false);
			lblResourceFilePath.setEnabled(false);
			comboBoxResourceFilePath.setEnabled(false);

			lblRdfFile.setEnabled(false);
			comboBoxRDFFilePath.setEnabled(false);
			btnOpenRDFFile.setEnabled(false);

			lblDBPediaDataFile.setEnabled(true);
			comboBoxDBpediDataFilePath.setEnabled(true);
			btnOpenDBpediaDataFile.setEnabled(true);

		} else {

			// Local KB Semantic Bag Generator

			btnOpenResourceFile.setEnabled(true);
			lblResourceFilePath.setEnabled(true);
			comboBoxResourceFilePath.setEnabled(true);

			lblRdfFile.setEnabled(true);
			comboBoxRDFFilePath.setEnabled(true);
			btnOpenRDFFile.setEnabled(true);

			lblDBPediaDataFile.setEnabled(false);
			comboBoxDBpediDataFilePath.setEnabled(false);
			btnOpenDBpediaDataFile.setEnabled(false);

		}

	}

	public String getResourceFilePath() {
		return resourceFilePath;
	}

	public void setResourceFilePath(String resourceFP) {
		this.resourceFilePath = resourceFP;
	}

	public String getRdfFilePath() {
		return rdfFilePath;
	}

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

	public String getDbpediaDataFilePath() {
		return dbpediaDataFilePath;
	}

	public void setDbpediaDataFilePath(String dbpediaDataFilePath) {
		this.dbpediaDataFilePath = dbpediaDataFilePath;
	}

	public String getSemanticBagFilePath() {
		return semanticBagFilePath;
	}

	public void setSemanticBagFilePath(String semanticBagFilePath) {
		this.semanticBagFilePath = semanticBagFilePath;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

}
