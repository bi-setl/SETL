package etl_model;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

//concrete class for matcher
public class ETLMatcher implements ETLOperation {

	private Methods methods;

	private double threshHold;
	private String localKBPWeightFilePath, dbPediaSBagFilePath, localKBSBagFilePath, matcherFilePath;

	final static String PROPERTY_WEIGHT_FILE = "Property Weight File:";
	final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";

	public ETLMatcher() {
		super();
		methods = new Methods();
		threshHold = -1;
		localKBPWeightFilePath = "";
		dbPediaSBagFilePath = "";
		localKBSBagFilePath = "";
		matcherFilePath = "";

	}

	@Override
	public boolean execute(JTextPane textPane) {
		
		
		Independence independence = new Independence();
		boolean status = independence.matchSemanticBag(threshHold, localKBSBagFilePath, dbPediaSBagFilePath, localKBPWeightFilePath, matcherFilePath);
		
		
		if(status){
			textPane.setText(textPane.getText().toString()+"\nMatching File Generated. Saved as: "+ matcherFilePath);
		}else{
			textPane.setText(textPane.getText().toString()+"\nSMatching File Generation Failed");
		}
		
		return status;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		JPanel panelMatcher = new JPanel();
		panelMatcher.setLayout(new MigLayout("", "[grow][][grow]", "[][][][][][][10][][][][][]"));

		JLabel lblThreshold = new JLabel("Threshold:");
		lblThreshold.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblThreshold, "cell 0 0,alignx right");

		JTextField textFieldThreshold = new JTextField();
		textFieldThreshold.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(textFieldThreshold, "cell 2 0,growx");
		textFieldThreshold.setColumns(60);

		JLabel lblPWeight = new JLabel("Pweight File:");
		lblPWeight.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblPWeight, "cell 0 1,alignx right");

		JComboBox comboBoxPWeightFile = new JComboBox();
		comboBoxPWeightFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(comboBoxPWeightFile, "flowx,cell 2 1,growx");

		LinkedHashSet pWeightFileHashSet = inputParamsMap.get(PROPERTY_WEIGHT_FILE);
		ArrayList<String> pWeightFileHashList = new ArrayList<>(pWeightFileHashSet);
		comboBoxPWeightFile.setModel(new DefaultComboBoxModel<>(pWeightFileHashList.toArray()));

		if (!localKBPWeightFilePath.equals("")) {
			comboBoxPWeightFile.setSelectedItem(localKBPWeightFilePath);
		}

		JLabel lblDBpediaSBag = new JLabel("DBpedia KB SBag File:");
		lblDBpediaSBag.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblDBpediaSBag, "cell 0 2,alignx right");

		JComboBox comboBoxDBpediaSBag = new JComboBox();
		comboBoxDBpediaSBag.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(comboBoxDBpediaSBag, "flowx,cell 2 2,growx");

		LinkedHashSet dbpediaSBagFileHashSet = inputParamsMap.get(SEMANTIC_BAG_FILE);
		ArrayList<String> dbpediaSBagFileList = new ArrayList<>(dbpediaSBagFileHashSet);
		comboBoxDBpediaSBag.setModel(new DefaultComboBoxModel<>(dbpediaSBagFileList.toArray()));

		if (!dbPediaSBagFilePath.equals("")) {
			comboBoxDBpediaSBag.setSelectedItem(dbPediaSBagFilePath);
		}

		JLabel lblLocalKBSbag = new JLabel("Local KB SBag File:");
		lblLocalKBSbag.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblLocalKBSbag, "cell 0 3,alignx right");

		JComboBox comboBoxLocalKBSbagFilePath = new JComboBox();
		comboBoxLocalKBSbagFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(comboBoxLocalKBSbagFilePath, "flowx,cell 2 3,growx");

		LinkedHashSet localKBSBagFileHashSet = inputParamsMap.get(SEMANTIC_BAG_FILE);
		ArrayList<String> localKBSBagFileList = new ArrayList<>(localKBSBagFileHashSet);
		comboBoxLocalKBSbagFilePath.setModel(new DefaultComboBoxModel<>(localKBSBagFileList.toArray()));

		if (!localKBSBagFilePath.equals("")) {
			comboBoxLocalKBSbagFilePath.setSelectedItem(localKBSBagFilePath);
		}
		
		JLabel lblMatcherFileFile = new JLabel("Mathcher File:");
		lblMatcherFileFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblMatcherFileFile, "cell 0 4,alignx right");

		JLabel lblMactherFilePath = new JLabel("None");
		lblMactherFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMatcher.add(lblMactherFilePath, "flowx,cell 2 4,growx");

		if (!matcherFilePath.equals("")) {
			lblMactherFilePath.setText(matcherFilePath);
		}

		JButton btnOpenMatcher = new JButton("Open");
		btnOpenMatcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				String defaultName = methods.getDateTime() + "_Matcher.txt";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to Matcher File");

				if (!filePath.equals("")) {

					lblMactherFilePath.setText(filePath);
				}

			}
		});
		btnOpenMatcher.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMatcher.add(btnOpenMatcher, "cell 2 4");
		
		
		JButton btnOpenDBPediaSBag = new JButton("Open");
		btnOpenDBPediaSBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select DBpedia Semantic Bag File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxDBpediaSBag.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxDBpediaSBag.setModel(comboBoxModel);
					comboBoxDBpediaSBag.setSelectedItem(filePath);
				}

			}
		});
		btnOpenDBPediaSBag.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMatcher.add(btnOpenDBPediaSBag, "cell 2 2");

		JButton btnOpenLKSBag = new JButton("Open");
		btnOpenLKSBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select Local KB Semantic Bag File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxLocalKBSbagFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxLocalKBSbagFilePath.setModel(comboBoxModel);
					comboBoxLocalKBSbagFilePath.setSelectedItem(filePath);
				}

			}

		});

		btnOpenLKSBag.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMatcher.add(btnOpenLKSBag, "cell 2 3");

		JButton btnOpenPWeightFile = new JButton("Open");
		btnOpenPWeightFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath = methods.chooseFile("Select Property Weight File");

				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxPWeightFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxPWeightFile.setModel(comboBoxModel);
					comboBoxPWeightFile.setSelectedItem(filePath);
				}
			}
		});
		btnOpenPWeightFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMatcher.add(btnOpenPWeightFile, "cell 2 1");

		if (threshHold != -1) {
			textFieldThreshold.setText("" + threshHold);
		}

		int confirmation = JOptionPane.showConfirmDialog(null, panelMatcher, "Please Input Values for Matcher.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			try {

				String thresHoldString = textFieldThreshold.getText().toString();
				String pweightPath = comboBoxPWeightFile.getSelectedItem().toString();
				String dbpediaSBagPath = comboBoxDBpediaSBag.getSelectedItem().toString();
				String localKBSbagPath = comboBoxLocalKBSbagFilePath.getSelectedItem().toString();
				String matcherPath = lblMactherFilePath.getText().toString();
				

				ArrayList<String> inputList = new ArrayList<>();
				inputList.add(pweightPath);
				inputList.add(dbpediaSBagPath);
				inputList.add(localKBSbagPath);
				inputList.add(matcherPath);

				if (!methods.hasEmptyString(inputList)) {

					if (!methods.isDoubleParseable(thresHoldString)) {
						methods.showDialog("Please provide number for threshold.");
						return false;
					}

					if (matcherPath.equals("None")) {
						methods.showDialog("Please give path for matcher file.");
						return false;

					}
					
					setThreshHold(methods.getDouble(thresHoldString));
					setLocalKBPWeightFilePath(pweightPath);
					setDbPediaSBagFilePath(dbpediaSBagPath);
					setLocalKBSBagFilePath(localKBSbagPath);
					setMatcherFilePath(matcherPath);

					inputParamsMap.get(PROPERTY_WEIGHT_FILE).add(pweightPath);
					inputParamsMap.get(SEMANTIC_BAG_FILE).add(dbpediaSBagPath);
					inputParamsMap.get(SEMANTIC_BAG_FILE).add(localKBSbagPath);

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

	public String getLocalKBPWeightFilePath() {
		return localKBPWeightFilePath;
	}

	public void setLocalKBPWeightFilePath(String localKBPWeightFilePath) {
		this.localKBPWeightFilePath = localKBPWeightFilePath;
	}

	public String getDbPediaSBagFilePath() {
		return dbPediaSBagFilePath;
	}

	public void setDbPediaSBagFilePath(String dbPediaSBagFilePath) {
		this.dbPediaSBagFilePath = dbPediaSBagFilePath;
	}

	public double getThreshHold() {
		return threshHold;
	}

	public void setThreshHold(double threshHold) {
		this.threshHold = threshHold;
	}

	public String getLocalKBSBagFilePath() {
		return localKBSBagFilePath;
	}

	public void setLocalKBSBagFilePath(String localKBSBagFilePath) {
		this.localKBSBagFilePath = localKBSBagFilePath;
	}

	public String getMatcherFilePath() {
		return matcherFilePath;
	}

	public void setMatcherFilePath(String matcherFilePath) {
		this.matcherFilePath = matcherFilePath;
	}
	

}
