package etl_model;

import java.awt.BorderLayout;
import java.awt.Color;
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

import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;
import queries.LoaderMain;


//Concrete class for Loading operation
public class ETLLoadingOperation implements ETLOperation {

	String inputFilePath;
	String outputDir;
	private Methods methods;

	ArrayList<String> loadingRDFFileList;

	public ETLLoadingOperation() {
		super();
		methods = new Methods();
		loadingRDFFileList = new ArrayList<>();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		LoaderMain loaderMain = new LoaderMain();
		String result = loaderMain.loadFile(inputFilePath, getOutputDir());
		textPane.setText(textPane.getText() + "\n" + result);
		return true;
	}

	@Override
	public boolean getInput(JPanel parentPanel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		// contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][600px, grow][]", "[][]"));
		
		JLabel lblSourceFile = new JLabel("Source File:");
		lblSourceFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblSourceFile, "cell 0 0,alignx trailing");
		
		JComboBox comboBoxInputFilePath = new JComboBox();
		comboBoxInputFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxInputFilePath.setBackground(Color.WHITE);
		panel.add(comboBoxInputFilePath, "cell 1 0,growx");
		
		JButton btnNewInputFile = new JButton("Open ");
		btnNewInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select RDF File for Loading");
				if (!filePath.equals("")) {

					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxInputFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxInputFilePath.setModel(comboBoxModel);
					comboBoxInputFilePath.setSelectedItem(filePath);
				}
			}
		});
		btnNewInputFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(btnNewInputFile, "cell 2 0,grow");
		
		JLabel lblTdbDirectory = new JLabel("TDB Directory:");
		lblTdbDirectory.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblTdbDirectory, "cell 0 1");
		
		JLabel lblTdbDirectoryPath = new JLabel("");
		lblTdbDirectoryPath.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		panel.add(lblTdbDirectoryPath, "cell 1 1,grow");
		
		if (outputDir != null) {
			lblTdbDirectoryPath.setText(outputDir);
		}
		
		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dirName = methods.chooseDirectory("Select Directory");
				lblTdbDirectoryPath.setText(dirName);
				setOutputDir(dirName);
			}
		});
		btnOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(btnOpen, "cell 2 1,grow");
		
		if (this.inputFilePath != null) {
			this.loadingRDFFileList.add(this.inputFilePath);	
		}
		
		LinkedHashSet<String> rdfFileSet = inputParamsMap.get(RDF_FILE);
		if (inputFilePath != null) {
			rdfFileSet.add(inputFilePath);
		}
		try {
			rdfFileSet.addAll(inputParamsMap.get(TBox_FILE));
			rdfFileSet.addAll(inputParamsMap.get(LEVEL_FILE));
			rdfFileSet.addAll(inputParamsMap.get(FACT_FILE));
			rdfFileSet.addAll(inputParamsMap.get(INSTANCE_FILE));
			rdfFileSet.addAll(inputParamsMap.get(UPDATE_DIM_FILE));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> rdfFileList = new ArrayList<>(rdfFileSet);
		comboBoxInputFilePath.setModel(new DefaultComboBoxModel<>(rdfFileList.toArray()));
		
		if(this.inputFilePath != null){
			comboBoxInputFilePath.setSelectedItem(this.inputFilePath);
		}

		int confirmation = JOptionPane.showConfirmDialog(null, panel,
				"Please Input Values for Loading.", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			String selectedItem = comboBoxInputFilePath.getSelectedItem().toString();
			if (selectedItem != null && !selectedItem.equals("")) {
				this.inputFilePath = selectedItem;
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
	
	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	
	public ArrayList<String> getLoadingRDFFileList() {
		return loadingRDFFileList;
	}

	public void setLoadingRDFFileList(ArrayList<String> loadingRDFFileList) {
		this.loadingRDFFileList = loadingRDFFileList;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

}
