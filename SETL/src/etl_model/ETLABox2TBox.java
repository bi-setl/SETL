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
import javax.swing.JTextPane;

import core.ABoxToTBox;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLABox2TBox implements ETLOperation {
	private String sourceABoxFile, targetTBoxFile;
	Methods methods;

	public ETLABox2TBox() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		ABoxToTBox aBoxToTBox = new ABoxToTBox();
		String result = aBoxToTBox.generateTBox(getSourceABoxFile(), getTargetTBoxFile());
		textPane.setText(textPane.getText() + "\n" + result);
		return true;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
		JPanel panelLevelEntry = new JPanel();
		panelLevelEntry.setLayout(new MigLayout("", "[][800px,grow][]", "[][][][][][]"));

		JLabel lblSourceAbox = new JLabel("Source ABox: ");
		lblSourceAbox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		// panelSourceABoxLocal.add(lblSourceAbox, "cell 0 0");
		panelLevelEntry.add(lblSourceAbox, "cell 0 0");

		JComboBox comboBoxSourceABox = new JComboBox();
		comboBoxSourceABox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		// panelSourceABoxLocal.add(comboBoxSourceABox, "cell 1 0,grow");
		panelLevelEntry.add(comboBoxSourceABox, "cell 1 0,growx");

		LinkedHashSet sourceHashSet = inputParamsMap.get(SPARQL_FILE);
		LinkedHashSet sourceHashSet2 = inputParamsMap.get(EXPRESSION_FILE);
		LinkedHashSet sourceHashSet3 = inputParamsMap.get(RDF_MAPPER);
		ArrayList<String> sourceAboxList;
		ArrayList<String> sourceAboxList2;
		ArrayList<String> sourceAboxList3;
		if (sourceHashSet != null) {
			sourceAboxList = new ArrayList<>(sourceHashSet);
		} else {
			sourceAboxList = new ArrayList<>();
		}

		if (sourceHashSet2 != null) {
			sourceAboxList2 = new ArrayList<>(sourceHashSet2);
		} else {
			sourceAboxList2 = new ArrayList<>();
		}
		
		if (sourceHashSet3 != null) {
			sourceAboxList3 = new ArrayList<>(sourceHashSet3);
		} else {
			sourceAboxList3 = new ArrayList<>();
		}

		for (String string : sourceAboxList2) {
			sourceAboxList.add(string);
		}
		
		for (String string : sourceAboxList3) {
			sourceAboxList.add(string);
		}
		
		if (getSourceABoxFile() != null) {
			sourceAboxList.add(getSourceABoxFile());
		}

		comboBoxSourceABox.setModel(new DefaultComboBoxModel<>(sourceAboxList.toArray()));

		if (getSourceABoxFile() != null) {
			inputParamsMap.get(SPARQL_FILE).add(getSourceABoxFile());
			inputParamsMap.get(EXPRESSION_FILE).add(getSourceABoxFile());
			comboBoxSourceABox.setSelectedItem(getSourceABoxFile());
		}

		JButton btnOpenAboxFile = new JButton("Open");
		btnOpenAboxFile.setFont(new Font("Tahoma", Font.BOLD, 16));
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
		// panelSourceABoxLocal.add(btnOpenAboxFile, "cell 2 0,growx");
		panelLevelEntry.add(btnOpenAboxFile, "cell 2 0,growx");

		JLabel lblTargetABoxType = new JLabel("Target TBox Type: ");
		lblTargetABoxType.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblTargetABoxType, "cell 0 1,alignx trailing");

		JComboBox comboBoxTargetABoxType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxTargetABoxType.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(comboBoxTargetABoxType, "cell 1 1 2 1,growx");

		JLabel lblTargetAbox = new JLabel("Target TBox: ");
		lblTargetAbox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(lblTargetAbox, "cell 0 2");

		JComboBox comboBoxTargetABox = new JComboBox();
		comboBoxTargetABox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelLevelEntry.add(comboBoxTargetABox, "cell 1 2,grow");

		LinkedHashSet hashSet = inputParamsMap.get(TBox_FILE);
		ArrayList<String> aboxList;
		if (hashSet != null) {
			aboxList = new ArrayList<>(hashSet);
		} else {
			aboxList = new ArrayList<>();
		}
		comboBoxTargetABox.setModel(new DefaultComboBoxModel<>(aboxList.toArray()));

		if (this.targetTBoxFile != null) {
			inputParamsMap.get(TBox_FILE).add(getTargetTBoxFile());
			comboBoxTargetABox.setSelectedItem(this.targetTBoxFile);
		}

		JButton btnNewTargetAbox = new JButton("New");
		btnNewTargetAbox.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnNewTargetAbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetABoxType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = "ABoxToTBoxDeriver_".toLowerCase() + methods.getDateTime() + "_TargetTBox" + extension;

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
		panelLevelEntry.add(btnNewTargetAbox, "cell 2 2,growx");

		int confirmation = JOptionPane.showConfirmDialog(null, panelLevelEntry, "Please Input Values for ABox2TBox.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String sourceABoxString = (String) comboBoxSourceABox.getSelectedItem();
			String targetABoxString = comboBoxTargetABox.getSelectedItem().toString();

			ArrayList<String> inputList = new ArrayList<>();
			inputList.add(sourceABoxString);
			inputList.add(targetABoxString);

			boolean hasEmpty = methods.hasEmptyString(inputList);
			if (!hasEmpty) {

				if (sourceABoxString.equals("None")) {
					methods.showDialog("Please select Source ABox file.");
					return false;
				}

				if (targetABoxString.equals("")) {
					methods.showDialog("Please select Target TBox file.");
					return false;

				}

				setSourceABoxFile(sourceABoxString);
				setTargetTBoxFile(targetABoxString);

				inputParamsMap.get(TBox_FILE).add(getTargetTBoxFile());

				return true;

			} else {
				methods.showDialog("Please provide value to all input.");
				return false;
			}

		}

		return false;
	}

	public String getSourceABoxFile() {
		return sourceABoxFile;
	}

	public void setSourceABoxFile(String sourceABoxFile) {
		this.sourceABoxFile = sourceABoxFile;
	}

	public String getTargetTBoxFile() {
		return targetTBoxFile;
	}

	public void setTargetTBoxFile(String targetTBoxFile) {
		this.targetTBoxFile = targetTBoxFile;
	}
}
