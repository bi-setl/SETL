package etl_model;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.Box;
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

public class ETLPWeightGenerator implements ETLOperation {

	private Methods methods;

	final static String RESOURCE_FILE = "Resource File:";
	final static String PROPERTY_WEIGHT_FILE = "Property Weight File:";
	final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";
	final static String RDF_FILE = "RDF File:";

	private String  rdfFilePath, pwFilePath, seletedPropertiesString;

	//private String resource;
	// GUI components
	JComboBox  comboBoxPWeightFilePath, comboBoxRDFFilePath;
	//JComboBox comboBoxResources;
	JTextField txtKeyword, textFieldNumOfHit;

	public ETLPWeightGenerator() {
		super();
		//resource = "";
		rdfFilePath = "";
		pwFilePath = "";
		seletedPropertiesString="";
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {

		Independence independence = new Independence();
		boolean status = independence.localKBPropertyWeightGenerator(seletedPropertiesString, rdfFilePath, pwFilePath);

		if (status) {
			textPane.setText(
					textPane.getText().toString() + "\nProperty Weight generated. Saved as: " + getPwFilePath());
		} else {
			textPane.setText(textPane.getText().toString() + "\nProperty Weight generation Failed");
		}
		return status;

	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panelPWeightGenerator = new JPanel();
		panelPWeightGenerator.setLayout(new MigLayout("", "[][][800]", "[][][][][][10][][][][][]"));

		JLabel lblSelecteddProperties = new JLabel("Property Names:");
		lblSelecteddProperties.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelPWeightGenerator.add(lblSelecteddProperties, "cell 0 1,alignx right");
		
		JTextField textFieldSelectedProperties = new JTextField();
		textFieldSelectedProperties.setToolTipText("Selected property names separated by COMMA");
		textFieldSelectedProperties.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelPWeightGenerator.add(textFieldSelectedProperties, "cell 2 1,growx");
		
		if(this.seletedPropertiesString!=""){
			textFieldSelectedProperties.setText(this.seletedPropertiesString);
		}

//		JComboBox comboBoxResources = new JComboBox();
//		comboBoxResources.setFont(new Font("Tahoma", Font.PLAIN, 16));
//		panelPWeightGenerator.add(comboBoxResources, "cell 2 1,growx");
//
//		if (!this.resource.equals("")) {
//
//			ArrayList<String> resourceList = new ArrayList<>();
//			resourceList.add(this.resource);
//
//			DefaultComboBoxModel resourcesComboBoxModel = new DefaultComboBoxModel<>(resourceList.toArray());
//			comboBoxResources.setModel(resourcesComboBoxModel);
//
//		}

		JLabel lblRDFFile = new JLabel("RDF File:");
		lblRDFFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelPWeightGenerator.add(lblRDFFile, "cell 0 2,alignx right");

		JComboBox comboBoxRDFFilePath = new JComboBox();
		comboBoxRDFFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));

		LinkedHashSet rdfFileHashSet = inputParamsMap.get(RDF_FILE);
		ArrayList<String> rdfFileHashList = new ArrayList<>(rdfFileHashSet);
		comboBoxRDFFilePath.setModel(new DefaultComboBoxModel<>(rdfFileHashList.toArray()));

		if (!this.rdfFilePath.equals("")) {
			comboBoxRDFFilePath.setSelectedItem(this.rdfFilePath);
		}

		panelPWeightGenerator.add(comboBoxRDFFilePath, "flowx,cell 2 2,growx");

		JLabel lblPWeightFile = new JLabel("PWeight File:");
		lblPWeightFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelPWeightGenerator.add(lblPWeightFile, "cell 0 3,alignx right");

		JLabel lblPWeightFilePath = new JLabel("None");
		lblPWeightFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelPWeightGenerator.add(lblPWeightFilePath, "flowx,cell 2 3,growx");

		if (!this.pwFilePath.equals("")) {
			lblPWeightFilePath.setText(this.pwFilePath);
		}

		JButton btnOpenRDFFile = new JButton("Open");
		btnOpenRDFFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnOpenRDFFile.addActionListener(new ActionListener() {

			@Override
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
		panelPWeightGenerator.add(btnOpenRDFFile, "cell 2 2");

		JButton btnNewPWeight = new JButton("New");
		btnNewPWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String defaultName = methods.getDateTime() + "_PWeight.txt";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save property weight File");

				if (!filePath.equals("")) {

					lblPWeightFilePath.setText(filePath);
				}
			}
		});
		btnNewPWeight.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelPWeightGenerator.add(btnNewPWeight, "cell 2 3");

//		JButton btnOpenResourceFile = new JButton("Open Resource File");
//		btnOpenResourceFile.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				String filePath = methods.getFilePath("Select Resource file to open");
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
//					DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(resourceStringList.toArray());
//					comboBoxResources.setModel(comboBoxModel);
//				}
//			}
//		});
//		btnOpenResourceFile.setFont(new Font("Tahoma", Font.BOLD, 16));
//		panelPWeightGenerator.add(btnOpenResourceFile, "cell 0 0 2 1,growx, span 3");

		int confirmation = JOptionPane.showConfirmDialog(null, panelPWeightGenerator,
				"Please Input Values for Property Weight Generator.", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			try {

				String selectedPropertiesString = textFieldSelectedProperties.getText().toString();
				String rdfFilePathString = comboBoxRDFFilePath.getSelectedItem().toString();
				String pweightFilePathString = lblPWeightFilePath.getText().toString();

				ArrayList<String> inputList = new ArrayList<>();
				inputList.add(selectedPropertiesString);
				inputList.add(rdfFilePathString);
				inputList.add(pweightFilePathString);

				if (!methods.hasEmptyString(inputList)) {

					if (pweightFilePathString.equals("None")) {

						methods.showDialog("Please select path for saving Pweight file.");
						return false;

					}

					setPwFilePath(pweightFilePathString);
					setRdfFilePath(rdfFilePathString);
					setSeletedPropertiesString(selectedPropertiesString);

					inputParamsMap.get(PROPERTY_WEIGHT_FILE).add(pweightFilePathString);
					inputParamsMap.get(RDF_FILE).add(rdfFilePathString);

				} else {
					methods.showDialog("Please provide correct value to all input.");
					return false;
				}
			} catch (Exception e) {

				e.printStackTrace();
				methods.showDialog("Please provide correct value to all input.");
				return false;
			}

			return true;
		}

		return false;
	}

//	public String getResource() {
//		return resource;
//	}
//
//	public void setResource(String resource) {
//		this.resource = resource;
//	}

	public String getRdfFilePath() {
		return rdfFilePath;
	}

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

	public String getPwFilePath() {
		return pwFilePath;
	}

	public void setPwFilePath(String pwFilePath) {
		this.pwFilePath = pwFilePath;
	}

	public String getSeletedPropertiesString() {
		return seletedPropertiesString;
	}

	public void setSeletedPropertiesString(String seletedPropertiesString) {
		this.seletedPropertiesString = seletedPropertiesString;
	}
	
}
