package etl_model;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import core.Expression;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLExpressionHandler implements ETLOperation {
	private String mappingFile, sourceABoxFile, resultFile;

	private Methods methods;

	private final ButtonGroup buttonGroup = new ButtonGroup();

	public ETLExpressionHandler() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		Expression expressionHandler = new Expression();
		boolean status = expressionHandler.handleExpression(getMappingFile(), getSourceABoxFile(), getResultFile());

		if (status) {
			textPane.setText(
					textPane.getText().toString() + "\nExpression File Generated. Saved as: " + getResultFile());
		} else {
			textPane.setText(textPane.getText().toString() + "\nExpression File Generation Failed");
		}

		return status;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
		JPanel panelLevelEntry = new JPanel();
		panelLevelEntry.setLayout(new MigLayout("", "[][800][]", "[][][][]"));

		JLabel lblMappingFile = new JLabel("Mapping File: ");
		lblMappingFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblMappingFile, "cell 0 0, alignx right");

		JLabel lblMappingFilePath = new JLabel("None");
		lblMappingFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblMappingFilePath, "flowx, cell 1 0, growx");

		if (getMappingFile() != null) {
			lblMappingFilePath.setText(getMappingFile());
		}

		JButton btnOpenMappingFile = new JButton("Open");
		btnOpenMappingFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnOpenMappingFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Mapping File");
				if (!(filePath == null)) {
					lblMappingFilePath.setText(filePath);
				}
			}
		});
		panelLevelEntry.add(btnOpenMappingFile, "cell 2 0, growx");

		JLabel lblSourceAbox = new JLabel("Source ABox: ");
		lblSourceAbox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblSourceAbox, "cell 0 1,alignx right");

		JComboBox comboBoxSourceABox = new JComboBox();
		comboBoxSourceABox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		// panelSourceABoxLocal.add(comboBoxSourceABox, "cell 1 0,grow");
		panelLevelEntry.add(comboBoxSourceABox, "cell 1 1,growx");

		LinkedHashSet sourceHashSet = inputParamsMap.get(SPARQL_FILE);
		ArrayList<String> sourceAboxList = new ArrayList<>(sourceHashSet);
		comboBoxSourceABox.setModel(new DefaultComboBoxModel<>(sourceAboxList.toArray()));

		if (getSourceABoxFile() != null) {
			inputParamsMap.get(SPARQL_FILE).add(getSourceABoxFile());
			
			comboBoxSourceABox.setSelectedItem(getSourceABoxFile());
		}
		
		if (getSourceABoxFile() != null) {
			comboBoxSourceABox.setSelectedItem(getSourceABoxFile());
		}

		JButton btnOpenAboxFile = new JButton("Open");
		btnOpenAboxFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Source ABox File");
				if (!(filePath == null)) {
					setSourceABoxFile(filePath);
					if (!filePath.equals("")) {
						DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxSourceABox.getModel();
						comboBoxModel.addElement(filePath);
						comboBoxSourceABox.setModel(comboBoxModel);
						comboBoxSourceABox.setSelectedItem(filePath);
					}
				}
			}
		});
		btnOpenAboxFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelLevelEntry.add(btnOpenAboxFile, "cell 2 1, growx");

		JLabel lblTargetABoxType = new JLabel("Output Type: ");
		lblTargetABoxType.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblTargetABoxType, "cell 0 3,alignx trailing");

		JComboBox comboBoxTargetABoxType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxTargetABoxType.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(comboBoxTargetABoxType, "cell 1 3 2 1,growx");

		JLabel lblTargetAbox = new JLabel("Output: ");
		lblTargetAbox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblTargetAbox, "cell 0 4,alignx right");

		JComboBox comboBoxTargetABox = new JComboBox();
		comboBoxTargetABox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(comboBoxTargetABox, "flowx,cell 1 4,growx");

		LinkedHashSet hashSet = inputParamsMap.get(EXPRESSION_FILE);
		ArrayList<String> aboxList;
		if (hashSet != null) {
			aboxList = new ArrayList<>(hashSet);
		} else {
			aboxList = new ArrayList<>();
		}
		comboBoxTargetABox.setModel(new DefaultComboBoxModel<>(aboxList.toArray()));

		if (this.resultFile != null) {
			inputParamsMap.get(EXPRESSION_FILE).add(getResultFile());
			comboBoxTargetABox.setSelectedItem(this.resultFile);
		}
		
		if (this.resultFile != null) {
			comboBoxTargetABox.setSelectedItem(this.resultFile);
		}

		JButton btnNewTargetAbox = new JButton("New");
		btnNewTargetAbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetABoxType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);

				String defaultName = methods.getDateTime() + "_Output" + extension;

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save property weight File");

				if (!filePath.equals("")) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxTargetABox.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxTargetABox.setModel(comboBoxModel);
					comboBoxTargetABox.setSelectedItem(filePath);
				}
			}
		});
		btnNewTargetAbox.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelLevelEntry.add(btnNewTargetAbox, "cell 2 4, growx");

		int confirmation = JOptionPane.showConfirmDialog(null, panelLevelEntry,
				"Please Input Values for Expression Handling Operation", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			try {
				String mappingFileString = lblMappingFilePath.getText().toString();
				String sourceABoxString = (String) comboBoxSourceABox.getSelectedItem();
				String resultFileString = comboBoxTargetABox.getSelectedItem().toString();

				ArrayList<String> inputList = new ArrayList<>();
				inputList.add(mappingFileString);
				inputList.add(sourceABoxString);
				inputList.add(resultFileString);

				if (!methods.hasEmptyString(inputList)) {

					if (mappingFileString.equals("None")) {
						methods.showDialog("Please select Mapping file.");
						return false;
					}
					if (sourceABoxString.equals("None")) {
						methods.showDialog("Please select Source ABox file.");
						return false;
					}

					if (resultFileString.equals("")) {
						methods.showDialog("Please select Target ABox file.");
						return false;
					}

					setMappingFile(mappingFileString);
					setSourceABoxFile(sourceABoxString);
					setResultFile(resultFileString);

					inputParamsMap.get(EXPRESSION_FILE).add(getResultFile());
					inputParamsMap.get(SPARQL_FILE).add(getSourceABoxFile());

					return true;

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
		return false;
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public String getMappingFile() {
		return mappingFile;
	}

	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	public String getSourceABoxFile() {
		return sourceABoxFile;
	}

	public void setSourceABoxFile(String sourceABoxFile) {
		this.sourceABoxFile = sourceABoxFile;
	}

}
