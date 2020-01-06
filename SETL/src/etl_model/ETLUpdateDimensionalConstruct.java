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

import core.ETLLevelConstuct;
import core.UpdateConstruct;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLUpdateDimensionalConstruct implements ETLOperation {
	private String dimensionalConstruct, newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper, provGraph, resultFile, updateType;
	private Methods methods;
	
	public ETLUpdateDimensionalConstruct() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		// ETLDimensionConstruct dimensionConstruct = new ETLDimensionConstruct();
		// dimensionConstruct.updateDimensionalConstruct(dimensionalConstruct, newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper, provGraph, updateType);
		
		UpdateConstruct updateConstruct = new UpdateConstruct();
		String message = updateConstruct.updateDimensionConstruct(dimensionalConstruct, newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper,
				 provGraph, resultFile, updateType);
		textPane.setText(textPane.getText() + "\n" + message);
		return true;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
		
		JPanel panelDimensionConstruct = new JPanel();
		panelDimensionConstruct.setLayout(new MigLayout("", "[][800px][]", "[][][][][][][][][][]"));
		
		JLabel lblDimensionalConstruct = new JLabel("Dimensional Construct:");
		lblDimensionalConstruct.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblDimensionalConstruct, "cell 0 0,alignx trailing");
		
		JTextField textFieldDimensionConstruct = new JTextField();
		textFieldDimensionConstruct.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldDimensionConstruct, "cell 1 0 2 1,growx");
		textFieldDimensionConstruct.setColumns(10);
		
		if (getDimensionalConstruct() != null) {
			textFieldDimensionConstruct.setText(getDimensionalConstruct());
		}
		
		JLabel lblNewSourceData = new JLabel("New Source Data:");
		lblNewSourceData.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblNewSourceData, "cell 0 1,alignx trailing");
		
		JTextField textFieldNewSourceData = new JTextField();
		textFieldNewSourceData.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldNewSourceData, "cell 1 1,growx");
		textFieldNewSourceData.setColumns(10);
		
		if (getNewSourceData() != null) {
			textFieldNewSourceData.setText(getNewSourceData());
		}
		
		JButton btnOpenNewSource = new JButton("Open");
		btnOpenNewSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select New Source File");
				if (!(filePath == null)) {
					setNewSourceData(filePath);
					textFieldNewSourceData.setText(filePath);
				}
			}
		});
		btnOpenNewSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenNewSource, "cell 2 1");
		
		JLabel lblOldSourceData = new JLabel("Old Source Data:");
		lblOldSourceData.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblOldSourceData, "cell 0 2,alignx trailing");
		
		JTextField textFieldOldSourceData = new JTextField();
		textFieldOldSourceData.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldOldSourceData, "cell 1 2,growx");
		textFieldOldSourceData.setColumns(10);
		
		if (getOldSourceData() != null) {
			textFieldOldSourceData.setText(getOldSourceData());
		}
		
		JButton btnOpenOldSource = new JButton("Open");
		btnOpenOldSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Old Source File");
				if (!(filePath == null)) {
					setOldSourceData(filePath);
					textFieldOldSourceData.setText(filePath);
				}
			}
		});
		btnOpenOldSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenOldSource, "cell 2 2");
		
		JLabel lblSourceTbox = new JLabel("Source TBox:");
		lblSourceTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblSourceTbox, "cell 0 3,alignx trailing");
		
		JTextField textFieldSourceTbox = new JTextField();
		textFieldSourceTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldSourceTbox, "cell 1 3,growx");
		textFieldSourceTbox.setColumns(10);
		
		if (getSourceTBox() != null) {
			textFieldSourceTbox.setText(getSourceTBox());
		}
		
		JButton btnOpenSourceTbox = new JButton("Open");
		btnOpenSourceTbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Source TBox File");
				if (!(filePath == null)) {
					setSourceTBox(filePath);
					textFieldSourceTbox.setText(filePath);
				}
			}
		});
		btnOpenSourceTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenSourceTbox, "cell 2 3");
		
		JLabel lblTargetTbox = new JLabel("Target TBox:");
		lblTargetTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblTargetTbox, "cell 0 4,alignx trailing");
		
		JTextField textFieldTargetTBox = new JTextField();
		textFieldTargetTBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldTargetTBox, "cell 1 4,growx");
		textFieldTargetTBox.setColumns(10);
		
		if (getTargetTBox() != null) {
			textFieldTargetTBox.setText(getTargetTBox());
		}
		
		JButton btnOpenTargetTbox = new JButton("Open");
		btnOpenTargetTbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Target TBox File");
				if (!(filePath == null)) {
					setTargetTBox(filePath);
					textFieldTargetTBox.setText(filePath);
				}
			}
		});
		btnOpenTargetTbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenTargetTbox, "cell 2 4");
		
		JLabel lblTargetAbox = new JLabel("Target ABox:");
		lblTargetAbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblTargetAbox, "cell 0 5,alignx trailing");
		
		JTextField textFieldTargetABox = new JTextField();
		textFieldTargetABox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldTargetABox, "cell 1 5,growx");
		textFieldTargetABox.setColumns(10);
		
		if (getTargetABox() != null) {
			textFieldTargetABox.setText(getTargetABox());
		}
		
		JButton btnOpenTargetAbox = new JButton("Open");
		btnOpenTargetAbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Target ABox File");
				if (!(filePath == null)) {
					setTargetABox(filePath);
					textFieldTargetABox.setText(filePath);
				}
			}
		});
		btnOpenTargetAbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenTargetAbox, "cell 2 5");
		
		JLabel lblPrefix = new JLabel("Prefix:");
		lblPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblPrefix, "cell 0 6,alignx trailing");
		
		JTextField textFieldPrefix = new JTextField();
		textFieldPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldPrefix, "cell 1 6 2 1,growx");
		textFieldPrefix.setColumns(10);
		
		if (getPrefix() != null) {
			textFieldPrefix.setText(getPrefix());
		}
		
		JLabel lblMapper = new JLabel("Mapper:");
		lblMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblMapper, "cell 0 7,alignx trailing");
		
		JTextField textFieldMapper = new JTextField();
		textFieldMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldMapper, "cell 1 7,growx");
		textFieldMapper.setColumns(10);
		
		if (getMapper() != null) {
			textFieldMapper.setText(getMapper());
		}
		
		JButton btnOpenMapper = new JButton("Open");
		btnOpenMapper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Mapper File");
				if (!(filePath == null)) {
					setMapper(filePath);
					textFieldMapper.setText(filePath);
				}
			}
		});
		btnOpenMapper.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(btnOpenMapper, "cell 2 7");
		
		JLabel lblProvGraph = new JLabel("Prov Graph:");
		lblProvGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblProvGraph, "cell 0 8,alignx trailing");
		
		JTextField textFieldProvGraph = new JTextField();
		textFieldProvGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(textFieldProvGraph, "cell 1 8,growx");
		textFieldProvGraph.setColumns(10);
		
		if (getProvGraph() != null) {
			textFieldProvGraph.setText(getProvGraph());
		}
		
		JButton btnOpenProvGraph = new JButton("Open");
		btnOpenProvGraph.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOpenProvGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Prov Graph File");
				if (!(filePath == null)) {
					setProvGraph(filePath);
					textFieldProvGraph.setText(filePath);
				}
			}
		});
		panelDimensionConstruct.add(btnOpenProvGraph, "cell 2 8");
		
		JLabel lblUpdateType = new JLabel("Update Type:");
		lblUpdateType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblUpdateType, "cell 0 9,alignx trailing");
		
		JComboBox comboBoxUpdateType = new JComboBox(Methods.getAllUpdateTypes().toArray());
		comboBoxUpdateType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(comboBoxUpdateType, "cell 1 9 2 1,growx");
		
		if (getUpdateType() != null) {
			comboBoxUpdateType.setSelectedItem(getUpdateType());
		}
		
		JLabel lblTargetAboxType = new JLabel("Result File Type:");
		lblTargetAboxType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblTargetAboxType, "cell 0 10,alignx trailing");
		
		JComboBox comboBoxTargetABoxType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxTargetABoxType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(comboBoxTargetABoxType, "cell 1 10 2 1,growx");
		
		JLabel lblResultType = new JLabel("Result File:");
		lblResultType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(lblResultType, "cell 0 11,alignx trailing");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDimensionConstruct.add(comboBox, "cell 1 11,growx");
		
		JButton btnNew = new JButton("New");
		btnNew.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetABoxType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = "dimconstruct_" + methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save target ABox File");

				if (!filePath.equals("")) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
					comboBoxModel.addElement(filePath);
					comboBox.setModel(comboBoxModel);
					comboBox.setSelectedItem(filePath);
					setResultFile(filePath);
				}
			}
		});
		panelDimensionConstruct.add(btnNew, "cell 2 11, growx");
		
		ArrayList<String> aboxList = new ArrayList<>();
		
		if (getResultFile() != null) {
			aboxList.add(getResultFile());
		}
		comboBox.setModel(new DefaultComboBoxModel<>(aboxList.toArray()));
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelDimensionConstruct,
				"Please Input Values for Update Dimensional Construct", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			setDimensionalConstruct(textFieldDimensionConstruct.getText().toString().trim());
			setNewSourceData(textFieldNewSourceData.getText().toString().trim());
			setOldSourceData(textFieldOldSourceData.getText().toString().trim());
			setSourceTBox(textFieldSourceTbox.getText().toString().trim());
			setTargetTBox(textFieldTargetTBox.getText().toString().trim());
			setTargetABox(textFieldTargetABox.getText().toString().trim());
			setPrefix(textFieldPrefix.getText().toString().trim());
			setMapper(textFieldMapper.getText().toString().trim());
			setProvGraph(textFieldProvGraph.getText().toString().trim());
			setResultFile(comboBox.getSelectedItem().toString());
			setUpdateType(comboBoxUpdateType.getSelectedItem().toString());

			ArrayList<String> inputList = new ArrayList<>();
			inputList.add(getDimensionalConstruct());
			inputList.add(getNewSourceData());
			inputList.add(getOldSourceData());
			inputList.add(getSourceTBox());
			
			inputList.add(getTargetTBox());
			inputList.add(getTargetABox());
			inputList.add(getPrefix());
			inputList.add(getMapper());
			
			inputList.add(getProvGraph());
			inputList.add(getResultFile());
			
			boolean hasEmpty = methods.hasEmptyString(inputList);
			if (!hasEmpty) {
				return true;
			} else {
				methods.showDialog("Please provide value to all input.");
				return false;
			}
		}
		
		return false;
	}

	public String getDimensionalConstruct() {
		return dimensionalConstruct;
	}

	public void setDimensionalConstruct(String dimensionalConstruct) {
		this.dimensionalConstruct = dimensionalConstruct;
	}

	public String getNewSourceData() {
		return newSourceData;
	}

	public void setNewSourceData(String newSourceData) {
		this.newSourceData = newSourceData;
	}

	public String getOldSourceData() {
		return oldSourceData;
	}

	public void setOldSourceData(String oldSourceData) {
		this.oldSourceData = oldSourceData;
	}

	public String getSourceTBox() {
		return sourceTBox;
	}

	public void setSourceTBox(String sourceTBox) {
		this.sourceTBox = sourceTBox;
	}

	public String getTargetTBox() {
		return targetTBox;
	}

	public void setTargetTBox(String targetTBox) {
		this.targetTBox = targetTBox;
	}

	public String getTargetABox() {
		return targetABox;
	}

	public void setTargetABox(String targetABox) {
		this.targetABox = targetABox;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMapper() {
		return mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}

	public String getProvGraph() {
		return provGraph;
	}

	public void setProvGraph(String provGraph) {
		this.provGraph = provGraph;
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}
}
