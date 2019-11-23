package etl_model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;

import core.LevelEntryNew;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

//Concrete class for ABox Gen operation
public class ETLLevelEntryGenerator implements ETLOperation {

	private String mappingFile, sourceABoxFile, targetABoxFile, targetTBoxFile, provFile, sourceCSV, delimiter, fileType, targetType;
	private String[] delimiters = new String[] { "Comma (,)", "Space ( )", "Semicolon (;)", "Tab (	)", "Pipe (|)" };

	private Methods methods;
	// private DatabaseOperations databaseOperations;
	
	private JTextField textFieldMapper;
	private JTextField textFieldProv;
	private JTextField textFieldMapper2;
	private JTextField textFieldProv2;
	
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public ETLLevelEntryGenerator() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		JPanel panelHolder = new JPanel();
		panelHolder.setBackground(Color.WHITE);
		panelHolder.setLayout(new MigLayout("", "[][grow]", "[][grow]"));
		
		JLabel lblSourceType = new JLabel("Source Type: ");
		lblSourceType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHolder.add(lblSourceType, "cell 0 0,alignx trailing");
		
		JPanel panelContainer = new JPanel();
		panelContainer.setLayout(new BorderLayout(0, 0));
		JPanel panelCSV = new JPanel();
		JPanel panelEntry = new JPanel();
		
		String[] types = new String[] {"CSV", "RDF"};
		JComboBox comboBoxFileType = new JComboBox(types);
		setFont(comboBoxFileType);
		comboBoxFileType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedItem = comboBoxFileType.getSelectedItem().toString();
				setFileType(selectedItem);
				
				if (selectedItem.equals("CSV")) {
					panelContainer.removeAll();
					panelContainer.repaint();
					panelContainer.revalidate();
					
					// panelContainer.add(panelCSV, "cell 0 0 2 1,grow");
					panelContainer.add(panelCSV, BorderLayout.CENTER);
				} else {
					panelContainer.removeAll();
					panelContainer.repaint();
					panelContainer.revalidate();
					
					// panelContainer.add(panelEntry, "cell 0 0 2 1,grow");
					panelContainer.add(panelEntry, BorderLayout.CENTER);
				}
			}
		});
		panelHolder.add(comboBoxFileType, "cell 1 0,growx");
		
		if (getFileType() != null) {
			comboBoxFileType.setSelectedItem(getFileType());
			
			if (getFileType().equals("CSV")) {
				panelContainer.removeAll();
				panelContainer.repaint();
				panelContainer.revalidate();
				
				// panelContainer.add(panelCSV, "cell 0 0 2 1,grow");
				panelContainer.add(panelCSV, BorderLayout.CENTER);
			} else {
				panelContainer.removeAll();
				panelContainer.repaint();
				panelContainer.revalidate();
				
				// panelContainer.add(panelEntry, "cell 0 0 2 1,grow");
				panelContainer.add(panelEntry, BorderLayout.CENTER);
			}
			
			panelContainer.repaint();
			panelContainer.revalidate();
		} else {
			panelContainer.add(panelCSV, BorderLayout.CENTER);
		}
		
		panelHolder.add(panelContainer, "cell 0 1 2 1,grow");
		// panelContainer.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		// panelContainer.add(panelCSV, "cell 0 0 2 1,grow");
		// panelContainer.add(panelCSV, BorderLayout.CENTER);
		panelCSV.setBackground(Color.WHITE);
		panelCSV.setLayout(new MigLayout("", "[][800px,grow][]", "[][][][][][][]"));
		
		JLabel lblSourceAbox = new JLabel("Source ABox:");
		setFont(lblSourceAbox);
		panelCSV.add(lblSourceAbox, "cell 0 0,alignx trailing");
		
		JComboBox comboBoxSourceCSV = new JComboBox();
		setFont(comboBoxSourceCSV);
		panelCSV.add(comboBoxSourceCSV, "cell 1 0 2 1,growx");
	
		ArrayList<String> csvList  = new ArrayList<>();
		if (getSourceCSV() != null) {
			csvList.add(getSourceCSV());
		}
		comboBoxSourceCSV.setModel(new DefaultComboBoxModel<>(csvList.toArray()));
		
		JButton btnOpenSourceCSV = new JButton("Open");
		setFont(btnOpenSourceCSV);
		btnOpenSourceCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Source ABox File");
				if (!(filePath == null)) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxSourceCSV.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxSourceCSV.setModel(comboBoxModel);
					comboBoxSourceCSV.setSelectedItem(filePath);
					setSourceCSV(filePath);
				}
			}
		});
		panelCSV.add(btnOpenSourceCSV, "cell 3 0");
		
		JLabel lblDelimiter = new JLabel("Delimiter:");
		setFont(lblDelimiter);
		panelCSV.add(lblDelimiter, "cell 0 1,alignx trailing");	
		
		JComboBox comboBoxDelimiter = new JComboBox(delimiters);
		setFont(comboBoxDelimiter);
		comboBoxDelimiter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setDelimiter(comboBoxDelimiter.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxDelimiter, "cell 1 1 3 1,growx");
		
		if (getDelimiter() != null) {
			comboBoxDelimiter.setSelectedItem(getDelimiter());
		}
		
		JLabel lblMapper = new JLabel("Mapper:");
		setFont(lblMapper);
		panelCSV.add(lblMapper, "cell 0 2,alignx trailing");
		
		textFieldMapper = new JTextField();
		setFont(textFieldMapper);
		panelCSV.add(textFieldMapper, "cell 1 2 2 1,growx");
		textFieldMapper.setColumns(10);
		
		if (getMappingFile() != null) {
			textFieldMapper.setText(getMappingFile());
		}
		
		JButton btnOpenMapper = new JButton("Open");
		setFont(btnOpenMapper);
		btnOpenMapper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Mapping File");
				if (!(filePath == null)) {
					setMappingFile(filePath);
					textFieldMapper.setText(getMappingFile());
				}
			}
		});
		panelCSV.add(btnOpenMapper, "cell 3 2");
		
		JLabel lblProvGraphFile = new JLabel("Prov. Graph:");
		setFont(lblProvGraphFile);
		panelCSV.add(lblProvGraphFile, "cell 0 3,alignx trailing");
		
		textFieldProv = new JTextField();
		setFont(textFieldProv);
		panelCSV.add(textFieldProv, "cell 1 3 2 1,growx");
		textFieldProv.setColumns(10);
		
		if (getProvFile() != null) {
			textFieldProv.setText(getProvFile());
		}
		
		JButton btnOpenProv = new JButton("Open");
		btnOpenProv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Prov Graph File");
				if (!(filePath == null)) {
					setProvFile(filePath);
					textFieldProv.setText(provFile);
				}
			}
		});
		setFont(btnOpenProv);
		panelCSV.add(btnOpenProv, "cell 3 3");
		
		JLabel lblTargetTbox = new JLabel("Target TBox: ");
		setFont(lblTargetTbox);
		panelCSV.add(lblTargetTbox, "cell 0 4,alignx trailing");
		
		JComboBox comboBoxTargetTBox = new JComboBox();
		setFont(comboBoxTargetTBox);
		panelCSV.add(comboBoxTargetTBox, "cell 1 4 2 1,growx");
		
		if (getTargetTBoxFile() != null) {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add(getTargetTBoxFile());
			comboBoxTargetTBox.setModel(new DefaultComboBoxModel<>(arrayList.toArray()));
		}
		
		JButton btnOpenTargetTBox = new JButton("Open");
		setFont(btnOpenTargetTBox);
		btnOpenTargetTBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Target TBox File");
				if (!(filePath == null)) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxTargetTBox.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxTargetTBox.setModel(comboBoxModel);
					comboBoxTargetTBox.setSelectedItem(filePath);
					setTargetTBoxFile(filePath);
				}
			}
		});
		panelCSV.add(btnOpenTargetTBox, "cell 3 4");
		
		JLabel lblTargetAboxType = new JLabel("Target ABox Type:");
		setFont(lblTargetAboxType);
		panelCSV.add(lblTargetAboxType, "cell 0 5");
		
		JComboBox comboBoxTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxTargetType);
		comboBoxTargetType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setTargetType(comboBoxTargetType.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxTargetType, "cell 1 5 3 1,growx");
		
		if (getTargetType() != null) {
			comboBoxTargetType.setSelectedItem(getTargetType());
		}
		
		JLabel lblTargetAbox = new JLabel("Target ABox:");
		setFont(lblTargetAbox);
		panelCSV.add(lblTargetAbox, "cell 0 6,alignx trailing");
		
		JComboBox comboBoxTargetABox = new JComboBox();
		setFont(comboBoxTargetABox);
		panelCSV.add(comboBoxTargetABox, "cell 1 6 2 1,growx");
		
		LinkedHashSet hashSet = inputParamsMap.get(LEVEL_FILE);
		
		ArrayList<String> aboxList;
		if (hashSet != null) {
			aboxList = new ArrayList<>(hashSet);
		} else {
			aboxList = new ArrayList<>();
		}
		
		if (getTargetABoxFile() != null) {
			aboxList.add(getTargetABoxFile());
		}
		
		comboBoxTargetABox.setModel(new DefaultComboBoxModel<>(aboxList.toArray()));
		
		if (getTargetABoxFile() != null) {
			comboBoxTargetABox.setSelectedItem(getTargetABoxFile());
		}
		
		JButton btnOpenTargetABox = new JButton("Open");
		btnOpenTargetABox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save target ABox File");

				if (!filePath.equals("")) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxTargetABox.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxTargetABox.setModel(comboBoxModel);
					comboBoxTargetABox.setSelectedItem(filePath);
					setTargetABoxFile(filePath);
				}
			}
		});
		setFont(btnOpenTargetABox);
		panelCSV.add(btnOpenTargetABox, "cell 3 6");
		
		// panel.add(panelEntry, "cell 0 1 2 1,grow");
		panelEntry.setBackground(Color.WHITE);
		panelEntry.setLayout(new MigLayout("", "[][800px,grow][]", "[][][][][][]"));
		
		JLabel lblSoureceAbox = new JLabel("Source ABox:");
		setFont(lblSoureceAbox);
		panelEntry.add(lblSoureceAbox, "cell 0 0,alignx trailing");
		
		JComboBox comboBoxSourceABox2 = new JComboBox();
		setFont(comboBoxSourceABox2);
		panelEntry.add(comboBoxSourceABox2, "cell 1 0,growx");
		
		LinkedHashSet sourceHashSet = inputParamsMap.get(SPARQL_FILE);
		LinkedHashSet sourceHashSet2 = inputParamsMap.get(EXPRESSION_FILE);
		
		ArrayList<String> sourceAboxList = new ArrayList<String>();
		for (Object object : sourceHashSet) {
			sourceAboxList.add(object.toString());
		}
		
		for (Object object : sourceHashSet2) {
			sourceAboxList.add(object.toString());
		}
		
		if (getSourceABoxFile() != null) {
			sourceAboxList.add(getSourceABoxFile());
			comboBoxSourceABox2.setSelectedItem(getSourceABoxFile());
		}
		
		comboBoxSourceABox2.setModel(new DefaultComboBoxModel<>(sourceAboxList.toArray()));
		
		JButton btnOpenSourceABox2 = new JButton("Open");
		btnOpenSourceABox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Source ABox File");
				if (!(filePath == null)) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxSourceABox2.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxSourceABox2.setModel(comboBoxModel);
					comboBoxSourceABox2.setSelectedItem(filePath);
					setSourceABoxFile(filePath);
				}
			}
		});
		setFont(btnOpenSourceABox2);
		panelEntry.add(btnOpenSourceABox2, "cell 2 0");
		
		JLabel lblMapper_1 = new JLabel("Mapper:");
		setFont(lblMapper_1);
		panelEntry.add(lblMapper_1, "cell 0 1,alignx trailing");
		
		textFieldMapper2 = new JTextField();
		setFont(textFieldMapper2);
		panelEntry.add(textFieldMapper2, "cell 1 1,growx");
		textFieldMapper2.setColumns(10);
		
		if (getMappingFile() != null) {
			textFieldMapper2.setText(getMappingFile());
		}
		
		JButton btnOpenMapper2 = new JButton("Open");
		btnOpenMapper2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Mapping File");
				if (!(filePath == null)) {
					setMappingFile(filePath);
					textFieldMapper2.setText(getMappingFile());
				}
			}
		});
		setFont(btnOpenMapper2);
		panelEntry.add(btnOpenMapper2, "cell 2 1");
		
		JLabel lblProvGraph = new JLabel("Prov. Graph:");
		setFont(lblProvGraph);
		panelEntry.add(lblProvGraph, "cell 0 2,alignx trailing");
		
		textFieldProv2 = new JTextField();
		setFont(textFieldProv2);
		panelEntry.add(textFieldProv2, "cell 1 2,growx");
		textFieldProv2.setColumns(10);
		
		if (getProvFile() != null) {
			textFieldProv2.setText(getProvFile());
		}
		
		JButton btnOpenProv2 = new JButton("Open");
		btnOpenProv2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Prov Graph File");
				if (!(filePath == null)) {
					setProvFile(filePath);
					textFieldProv2.setText(provFile);
				}
			}
		});
		setFont(btnOpenProv2);
		panelEntry.add(btnOpenProv2, "cell 2 2");
		
		JLabel lblTargetTbox_1 = new JLabel("Target TBox:");
		setFont(lblTargetTbox_1);
		panelEntry.add(lblTargetTbox_1, "cell 0 3,alignx trailing");
		
		JComboBox comboBoxTargetTBox2 = new JComboBox();
		setFont(comboBoxTargetTBox2);
		panelEntry.add(comboBoxTargetTBox2, "cell 1 3,growx");
		
		if (getTargetTBoxFile() != null) {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add(getTargetTBoxFile());
			comboBoxTargetTBox2.setModel(new DefaultComboBoxModel<>(arrayList.toArray()));
		}
		
		JButton btnOpenTargetTBox2 = new JButton("Open");
		btnOpenTargetTBox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Target TBox File");
				if (!(filePath == null)) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxTargetTBox2.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxTargetTBox2.setModel(comboBoxModel);
					comboBoxTargetTBox2.setSelectedItem(filePath);
					setTargetTBoxFile(filePath);
				}
			}
		});
		setFont(btnOpenTargetTBox2);
		panelEntry.add(btnOpenTargetTBox2, "cell 2 3");
		
		JLabel lblTargetAboxType_1 = new JLabel("Target ABox Type:");
		setFont(lblTargetAboxType_1);
		panelEntry.add(lblTargetAboxType_1, "cell 0 4,alignx trailing");
		
		JComboBox comboBoxTargetType2 = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxTargetType2);
		comboBoxTargetType2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setTargetType(comboBoxTargetType2.getSelectedItem().toString());
			}
		});	
		panelEntry.add(comboBoxTargetType2, "cell 1 4 2 1,growx");
		
		if (getTargetType() != null) {
			comboBoxTargetType2.setSelectedItem(getTargetType());
		}
		
		JLabel lblTargetAbox_1 = new JLabel("Target ABox:");
		setFont(lblTargetAbox_1);
		panelEntry.add(lblTargetAbox_1, "cell 0 5,alignx trailing");
		
		JComboBox comboBoxTargetABox2 = new JComboBox();
		setFont(comboBoxTargetABox2);
		panelEntry.add(comboBoxTargetABox2, "cell 1 5,growx");
		
		comboBoxTargetABox2.setModel(new DefaultComboBoxModel<>(aboxList.toArray()));
		
		if (getTargetABoxFile() != null) {
			comboBoxTargetABox2.setSelectedItem(getTargetABoxFile());
		}
		
		JButton btnOpenTargetABox2 = new JButton("Open");
		btnOpenTargetABox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetType2.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save target ABox File");

				if (!filePath.equals("")) {
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxTargetABox.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxTargetABox2.setModel(comboBoxModel);
					comboBoxTargetABox2.setSelectedItem(filePath);
					setTargetABoxFile(filePath);
				}
			}
		});
		setFont(btnOpenTargetABox2);
		panelEntry.add(btnOpenTargetABox2, "cell 2 5");
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelHolder,
				"Please Input Values for Level Entry Generator.", JOptionPane.OK_CANCEL_OPTION);
		
		if (confirmation == JOptionPane.OK_OPTION) {
			setFileType(comboBoxFileType.getSelectedItem().toString());
			
			if (getFileType().equals("CSV")) {
				setSourceCSV(comboBoxSourceCSV.getSelectedItem().toString());
				setDelimiter(comboBoxDelimiter.getSelectedItem().toString());
				setMappingFile(textFieldMapper.getText().toString());
				setProvFile(textFieldProv.getText().toString());
				setTargetTBoxFile(comboBoxTargetTBox.getSelectedItem().toString());
				setTargetType(comboBoxTargetType.getSelectedItem().toString());
				setTargetABoxFile(comboBoxTargetABox.getSelectedItem().toString());
				
				if (getSourceCSV() != null && getMappingFile() != null && 
						getProvFile() != null && getTargetTBoxFile() != null && getTargetABoxFile() != null && getDelimiter() != null) {
					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(getSourceCSV());
					inputList.add(getDelimiter());
					inputList.add(getMappingFile());
					inputList.add(getProvFile());
					inputList.add(getTargetTBoxFile());
					inputList.add(getTargetABoxFile());
					
					inputParamsMap.get(LEVEL_FILE).add(getTargetABoxFile());
					
					return true;
				} else {
					methods.showDialog("Please provide value to all input.");
					return false;
				}
			} else {
				setSourceABoxFile(comboBoxSourceABox2.getSelectedItem().toString());
				setMappingFile(textFieldMapper2.getText().toString());
				setProvFile(textFieldProv2.getText().toString());
				setTargetTBoxFile(comboBoxTargetTBox2.getSelectedItem().toString());
				setTargetType(comboBoxTargetType2.getSelectedItem().toString());
				setTargetABoxFile(comboBoxTargetABox2.getSelectedItem().toString());
				
				if (getSourceABoxFile() != null && getMappingFile() != null && 
						getProvFile() != null && getTargetTBoxFile() != null && getTargetABoxFile() != null) {
					ArrayList<String> inputList = new ArrayList<>();
					inputList.add(getSourceABoxFile());
					inputList.add(getMappingFile());
					inputList.add(getProvFile());
					inputList.add(getTargetTBoxFile());
					inputList.add(getTargetABoxFile());
					
					inputParamsMap.get(LEVEL_FILE).add(getTargetABoxFile());
					
					return true;
				} else {
					methods.showDialog("Please provide value to all input.");
					return false;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean execute(JTextPane textPane) {
		final JDialog dialog = new JDialog();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String result = "";
					result += Calendar.getInstance().getTime().toString() + "\n";
					
					if (getFileType().equals("CSV")) {
						// LevelEntryGenerator entryGenerator = new LevelEntryGenerator();
						// result += entryGenerator.generateLevelEntryFromCSV(getSourceCSV(), getMappingFile(), getTargetTBoxFile(), getProvFile(), getTargetABoxFile(), getDelimiter());
						LevelEntryNew entryNew = new LevelEntryNew();
						result += entryNew.generateLevelEntryFromCSV(getSourceCSV(), getMappingFile(), getTargetTBoxFile(), getProvFile(), getTargetABoxFile(), getDelimiter());
					} else {
						// LevelEntryGenerator entryGenerator = new LevelEntryGenerator();
						LevelEntryNew entryNew = new LevelEntryNew();
						result += entryNew.generateLevelEntry(getSourceABoxFile(), getMappingFile(),
								getTargetTBoxFile(), getProvFile(), getTargetABoxFile());
					}
					
					result += "\n" + Calendar.getInstance().getTime();

					textPane.setText(textPane.getText().toString() + "\n" + result);
					dialog.dispose();
					dialog.setVisible(false);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow]", "[]"));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel.add(progressBar, "cell 0 0,grow");

		final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		dialog.setTitle("Progress");
		dialog.setModal(true);

		dialog.setContentPane(optionPane);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
		
		return true;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
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

	public String getTargetABoxFile() {
		return targetABoxFile;
	}

	public void setTargetABoxFile(String targetABoxFile) {
		this.targetABoxFile = targetABoxFile;
	}

	public String getTargetTBoxFile() {
		return targetTBoxFile;
	}

	public void setTargetTBoxFile(String targetTBoxFile) {
		this.targetTBoxFile = targetTBoxFile;
	}

	public String getProvFile() {
		return provFile;
	}

	public void setProvFile(String provFile) {
		this.provFile = provFile;
	}

	private void setFont(JComponent component) {
		// TODO Auto-generated method stub
		component.setFont(new Font("Tahoma", Font.BOLD, 12));
	}

	public String getSourceCSV() {
		return sourceCSV;
	}

	public void setSourceCSV(String sourceCSV) {
		this.sourceCSV = sourceCSV;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
