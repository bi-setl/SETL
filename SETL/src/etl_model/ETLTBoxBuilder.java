package etl_model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

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

import core.TBoxBuilder;
import core.XMLParsing;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLTBoxBuilder implements ETLOperation {
	private String csvSource = "", csvTarget = "", csvPrefix = "", csvDelimiter = "";
	private String excelSource = "", excelTarget = "", excelPrefix = "";
	private String xmlSource = "", xmlTarget = "", xmlPrefix = "";
	private String jsonSource = "", jsonTarget = "", jsonPrefix = "";
	private String fileType = "", csvTargetType = "", xmlTargetType = "", excelTargetType = "", jsonTargetType = "";

	private Methods methods;
	
	private JComboBox comboBoxCSVSourceFile;
	private JTextField textFieldCSVPrefix;
	private JComboBox comboBoxCSVDelimiter;
	private JComboBox comboBoxCSVTargetType;
	private JComboBox comboBoxCSVTargetFile;
	
	private JComboBox<String> comboBoxExcelSourceFile;
	private JTextField textFieldExcelPrefix;
	private JComboBox<String> comboBoxExcelTargetType;
	private JComboBox<String> comboBoxExcelTargetFile;
	
	private JComboBox comboBoxXmlSourceFile;
	private JTextField textFieldXmlPrefix;
	private JComboBox comboBoxXmlTargetType;
	private JComboBox comboBoxXmlTargetFile;
	
	private JComboBox comboBoxJsonSourceFile;
	private JTextField textFieldJsonPrefix;
	private JComboBox comboBoxJsonTargetType;
	private JComboBox comboBoxJsonTargetFile;
	
	private String[] delimiters = new String[] { "Comma (,)", "Space ( )", "Semicolon (;)", "Tab (	)", "Pipe (|)" };

	public ETLTBoxBuilder() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		final JDialog dialog = new JDialog();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String result = "";
					TBoxBuilder boxBuilder = new TBoxBuilder();
					switch (getFileType()) {
					case "CSV":
						result = boxBuilder.parseCSV(getCsvSource(), getCsvPrefix(), getCsvDelimiter(), getCsvTarget());
						break;
					case "XML":
						XMLParsing xmlParsing = new XMLParsing();
						result = xmlParsing.parseXML(getXmlSource(), getXmlTarget(), getXmlPrefix());
						break;
					case "Excel":
						result = boxBuilder.parseExcel(getExcelSource(), getExcelTarget(), getExcelPrefix());
						break;
					case "JSON":
						// result = rdfWrapper.parseJSON(getJsonSource(), getJsonTarget());
						break;
					case "DB":
						// status = extractDBButtonHandler();
					default:
						result = "Unknown File Type";
						break;
					}

					textPane.setText(textPane.getText() + "\n" + result);

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
		dialog.setTitle("Message");
		dialog.setModal(true);

		dialog.setContentPane(optionPane);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);

		if (getFileType().equals("DB")) {
			// return status;
			return true;
		} else {
			return true;
		}
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
		JPanel panelTBoxBuilder = new JPanel();
		panelTBoxBuilder.setBackground(Color.WHITE);
		// contentPane.add(panelTBoxBuilder, BorderLayout.CENTER);
		panelTBoxBuilder.setLayout(new MigLayout("", "[][800px]", "[][grow]"));
		
		JLabel lblFileType = new JLabel("File Type:");
		setFont(lblFileType);
		panelTBoxBuilder.add(lblFileType, "cell 0 0,alignx trailing");
		
		String[] values = {"CSV", "Excel", "XML", "JSON"};
		JPanel panelHolder = new JPanel();
				
		JComboBox comboBoxFileType = new JComboBox(values);
		setFont(comboBoxFileType);
		comboBoxFileType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = comboBoxFileType.getSelectedItem().toString();
				setFileType(value);
				
				if (value.equals("CSV")) {
					setPanelCSV(panelHolder);
				} else if (value.equals("Excel")) {
					setPanelExcel(panelHolder);
				} else if (value.equals("XML")) {
					setPanelXML(panelHolder);
				} else if (value.equals("JSON")) {
					setPanelJSON(panelHolder);
				}
			}
		});
		panelTBoxBuilder.add(comboBoxFileType, "cell 1 0,growx");
		
		panelHolder.setBackground(Color.WHITE);
		panelTBoxBuilder.add(panelHolder, "cell 0 1 2 1,grow");
		panelHolder.setLayout(new BorderLayout(0, 0));
		
		setPanelJSON(panelHolder);
		setPanelExcel(panelHolder);
		setPanelXML(panelHolder);
		setPanelCSV(panelHolder);
		
		if (getFileType() != null) {
			comboBoxFileType.setSelectedItem(getFileType());
		}
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelTBoxBuilder, "Please Input Values for TBox Builder",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String selection = comboBoxFileType.getSelectedItem().toString();
			setFileType(selection);
			
			System.out.println(selection);
			
			if (selection.equals("CSV")) {
				setCsvSource(comboBoxCSVSourceFile.getSelectedItem().toString());
				setCsvPrefix(textFieldCSVPrefix.getText().toString().trim());
				setCsvDelimiter(comboBoxCSVDelimiter.getSelectedItem().toString());
				setCsvTargetType(comboBoxCSVTargetType.getSelectedItem().toString());
				setCsvTarget(comboBoxCSVTargetFile.getSelectedItem().toString());
				
				ArrayList<String> inputList = new ArrayList<String>();
				inputList.add(getCsvSource());
				inputList.add(getCsvPrefix());
				inputList.add(getCsvDelimiter());
				inputList.add(getCsvTarget());
				inputList.add(getCsvTargetType());

				try {
					inputParamsMap.get(T_BOX_BUILDER).add(getCsvTarget());
				} catch (Exception e) {
					// TODO: handle exception
					if (T_BOX_BUILDER == null) {
						System.out.println("T_BOX_BUILDER null");
					}
					
					if (getCsvTarget() == null) {
						System.out.println("csv file null");
					} 
				}
				if (getCsvSource().length() == 0 || getCsvTarget().length() == 0 || getCsvPrefix().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			} else if (selection.equals("Excel")) {
				setExcelSource(comboBoxExcelSourceFile.getSelectedItem().toString());
				setExcelPrefix(textFieldExcelPrefix.getText().toString().trim());
				setExcelTargetType(comboBoxExcelTargetType.getSelectedItem().toString());
				setExcelTarget(comboBoxExcelTargetFile.getSelectedItem().toString());
				
				ArrayList<String> inputList = new ArrayList<String>();
				inputList = new ArrayList<String>();
				inputList.add(getExcelSource());
				inputList.add(getExcelTarget());
				inputList.add(getExcelPrefix());

				inputParamsMap.get(T_BOX_BUILDER).add(getExcelTarget());

				if (getExcelSource().length() == 0 || getExcelTarget().length() == 0 || getExcelPrefix().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			} else if (selection.equals("XML")) {
				setXmlSource(comboBoxXmlSourceFile.getSelectedItem().toString());
				setXmlPrefix(textFieldXmlPrefix.getText().toString().trim());
				setXmlTargetType(comboBoxXmlTargetType.getSelectedItem().toString());
				setXmlTarget(comboBoxXmlTargetFile.getSelectedItem().toString());
				
				ArrayList<String> inputList = new ArrayList<String>();
				inputList = new ArrayList<String>();
				inputList.add(getXmlSource());
				inputList.add(getXmlTarget());
				inputList.add(getXmlPrefix());

				inputParamsMap.get(T_BOX_BUILDER).add(getXmlSource());

				if (getXmlSource().length() == 0 || getXmlTarget().length() == 0 || getXmlPrefix().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			} else if (selection.equals("JSON")) {
				setJsonSource(comboBoxJsonSourceFile.getSelectedItem().toString());
				setJsonPrefix(textFieldJsonPrefix.getText().toString().trim());
				setJsonTargetType(comboBoxJsonTargetType.getSelectedItem().toString());
				setJsonTarget(comboBoxJsonTargetFile.getSelectedItem().toString());
				
				ArrayList<String> inputList = new ArrayList<String>();
				inputList = new ArrayList<String>();
				inputList.add(getJsonSource());
				inputList.add(getJsonTarget());
				inputList.add(getJsonPrefix());

				inputParamsMap.get(T_BOX_BUILDER).add(getJsonSource());

				if (getJsonSource().length() == 0 || getJsonTarget().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			}
		} else {
			methods.showDialog("Please select the all values");
			return false;
		}
		return false;
	}

	private void setPanelJSON(JPanel panelHolder) {
		// TODO Auto-generated method stub
		panelHolder.removeAll();
		panelHolder.repaint();
		panelHolder.revalidate();
		
		JPanel panelCSV = new JPanel();
		panelHolder.add(panelCSV, BorderLayout.CENTER);
		panelCSV.setBackground(Color.WHITE);
		panelCSV.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));
		
		JLabel lblSourceFile = new JLabel("Source File:");
		setFont(lblSourceFile);
		panelCSV.add(lblSourceFile, "cell 0 0,alignx trailing");
		
		comboBoxJsonSourceFile = new JComboBox();
		setFont(comboBoxJsonSourceFile);
		panelCSV.add(comboBoxJsonSourceFile, "cell 1 0,growx");
		
		JButton btnOpenSource = new JButton("Open");
		setFont(btnOpenSource);
		btnOpenSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Source JSON File");
				if (!(filePath == null)) {
					setJsonSource(filePath);
					if (!filePath.equals("")) {
						DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxJsonSourceFile.getModel();
						comboBoxModel.addElement(filePath);
						comboBoxJsonSourceFile.setModel(comboBoxModel);
						comboBoxJsonSourceFile.setSelectedItem(filePath);
					}
				}
			}
		});
		panelCSV.add(btnOpenSource, "cell 2 0");
		
		JLabel lblPrefix = new JLabel("Prefix:");
		setFont(lblPrefix);
		panelCSV.add(lblPrefix, "cell 0 1,alignx trailing");
		
		textFieldJsonPrefix = new JTextField();
		setFont(textFieldJsonPrefix);
		panelCSV.add(textFieldJsonPrefix, "cell 1 1 2 1,growx");
		textFieldJsonPrefix.setColumns(10);
		
		JLabel lblTargetType_2 = new JLabel("Target Type:");
		setFont(lblTargetType_2);
		panelCSV.add(lblTargetType_2, "cell 0 2,alignx trailing");
		
		comboBoxJsonTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxJsonTargetType);
		comboBoxJsonTargetType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setJsonTargetType(comboBoxJsonTargetType.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxJsonTargetType, "cell 1 2 2 1,growx");
		
		if (getJsonTargetType() != null) {
			comboBoxJsonTargetType.setSelectedItem(getJsonTargetType());
		}
		
		JLabel lblTargetFile = new JLabel("Target File:");
		setFont(lblTargetFile);
		panelCSV.add(lblTargetFile, "cell 0 3,alignx trailing");
		
		comboBoxJsonTargetFile = new JComboBox();
		setFont(comboBoxJsonTargetFile);
		panelCSV.add(comboBoxJsonTargetFile, "cell 1 3,growx");
		
		JButton btnOpenTarget = new JButton("Open");
		setFont(btnOpenTarget);
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxJsonTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save target File");

				if (!filePath.equals("")) {
					setXmlTarget(filePath);
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxJsonTargetFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxJsonTargetFile.setModel(comboBoxModel);
					comboBoxJsonTargetFile.setSelectedItem(filePath);
				}
			}
		});
		panelCSV.add(btnOpenTarget, "cell 2 3");
		
		if (getJsonSource() != null) {
			ArrayList<String> arrayList = new ArrayList<>();
			arrayList.add(getJsonSource());
			
			comboBoxJsonSourceFile.setModel(new DefaultComboBoxModel<>(arrayList.toArray()));
		}
		
		if (getJsonPrefix() != null) {
			textFieldJsonPrefix.setText(getJsonPrefix());
		}
		
		if (getJsonTarget() != null) {
			ArrayList<String> arrayList = new ArrayList<>();
			arrayList.add(getJsonTarget());
			
			comboBoxJsonTargetFile.setModel(new DefaultComboBoxModel<>(arrayList.toArray()));
		}
	}

	private void setPanelXML(JPanel panelHolder) {
		// TODO Auto-generated method stub
		panelHolder.removeAll();
		panelHolder.repaint();
		panelHolder.revalidate();
		
		JPanel panelCSV = new JPanel();
		panelHolder.add(panelCSV, BorderLayout.CENTER);
		panelCSV.setBackground(Color.WHITE);
		panelCSV.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));
		
		JLabel lblSourceFile = new JLabel("Source File:");
		setFont(lblSourceFile);
		panelCSV.add(lblSourceFile, "cell 0 0,alignx trailing");
		
		comboBoxXmlSourceFile = new JComboBox();
		setFont(comboBoxXmlSourceFile);
		panelCSV.add(comboBoxXmlSourceFile, "cell 1 0,growx");
		
		JButton btnOpenSource = new JButton("Open");
		setFont(btnOpenSource);
		btnOpenSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Source XML File");
				if (!(filePath == null)) {
					setXmlSource(filePath);
					if (!filePath.equals("")) {
						DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxXmlSourceFile.getModel();
						comboBoxModel.addElement(filePath);
						comboBoxXmlSourceFile.setModel(comboBoxModel);
						comboBoxXmlSourceFile.setSelectedItem(filePath);
					}
				}
			}
		});
		panelCSV.add(btnOpenSource, "cell 2 0");
		
		JLabel lblPrefix = new JLabel("Prefix:");
		setFont(lblPrefix);
		panelCSV.add(lblPrefix, "cell 0 1,alignx trailing");
		
		textFieldXmlPrefix = new JTextField();
		setFont(textFieldXmlPrefix);
		panelCSV.add(textFieldXmlPrefix, "cell 1 1 2 1,growx");
		textFieldXmlPrefix.setColumns(10);
		
		JLabel lblTargetType_2 = new JLabel("Target Type:");
		setFont(lblTargetType_2);
		panelCSV.add(lblTargetType_2, "cell 0 2,alignx trailing");
		
		comboBoxXmlTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxXmlTargetType);
		comboBoxXmlTargetType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setXmlTargetType(comboBoxXmlTargetType.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxXmlTargetType, "cell 1 2 2 1,growx");
		
		JLabel lblTargetFile = new JLabel("Target File:");
		setFont(lblTargetFile);
		panelCSV.add(lblTargetFile, "cell 0 3,alignx trailing");
		
		comboBoxXmlTargetFile = new JComboBox();
		setFont(comboBoxXmlTargetFile);
		panelCSV.add(comboBoxXmlTargetFile, "cell 1 3,growx");
		
		JButton btnOpenTarget = new JButton("Open");
		setFont(btnOpenTarget);
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxXmlTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save target File");

				if (!filePath.equals("")) {
					setXmlTarget(filePath);
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxXmlTargetFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxXmlTargetFile.setModel(comboBoxModel);
					comboBoxXmlTargetFile.setSelectedItem(filePath);
				}
			}
		});
		panelCSV.add(btnOpenTarget, "cell 2 3");
		
		if (getXmlSource() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getXmlSource());
			comboBoxXmlSourceFile.setModel(comboBoxModel);
		}
		
		if (getXmlPrefix() != null) {
			textFieldXmlPrefix.setText(getXmlPrefix());
		}
		
		if (getXmlTargetType() != null) {
			comboBoxXmlTargetType.setSelectedItem(getXmlTargetType());
		}
		
		if (getXmlTarget() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getXmlTarget());
			comboBoxXmlTargetFile.setModel(comboBoxModel);
		}
	}

	private void setPanelExcel(JPanel panelHolder) {
		// TODO Auto-generated method stub
		panelHolder.removeAll();
		panelHolder.repaint();
		panelHolder.revalidate();
		
		JPanel panelCSV = new JPanel();
		panelHolder.add(panelCSV, BorderLayout.CENTER);
		panelCSV.setBackground(Color.WHITE);
		panelCSV.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));
		
		JLabel lblSourceFile = new JLabel("Source File:");
		setFont(lblSourceFile);
		panelCSV.add(lblSourceFile, "cell 0 0,alignx trailing");
		
		comboBoxExcelSourceFile = new JComboBox();
		setFont(comboBoxExcelSourceFile);
		panelCSV.add(comboBoxExcelSourceFile, "cell 1 0,growx");
		
		JButton btnOpenSource = new JButton("Open");
		setFont(btnOpenSource);
		btnOpenSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Source Excel File");
				if (!(filePath == null)) {
					setExcelSource(filePath);
					if (!filePath.equals("")) {
						DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxExcelSourceFile.getModel();
						comboBoxModel.addElement(filePath);
						comboBoxExcelSourceFile.setModel(comboBoxModel);
						comboBoxExcelSourceFile.setSelectedItem(filePath);
					}
				}
			}
		});
		panelCSV.add(btnOpenSource, "cell 2 0");
		
		JLabel lblPrefix = new JLabel("Prefix:");
		setFont(lblPrefix);
		panelCSV.add(lblPrefix, "cell 0 1,alignx trailing");
		
		textFieldExcelPrefix = new JTextField();
		setFont(textFieldExcelPrefix);
		panelCSV.add(textFieldExcelPrefix, "cell 1 1 2 1,growx");
		textFieldExcelPrefix.setColumns(10);
		
		JLabel lblTargetType_1 = new JLabel("Target Type:");
		setFont(lblPrefix);
		panelCSV.add(lblTargetType_1, "cell 0 2,alignx trailing");
		
		comboBoxExcelTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxExcelTargetType);
		comboBoxExcelTargetType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setExcelTargetType(comboBoxExcelTargetType.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxExcelTargetType, "cell 1 2 2 1,growx");
		
		JLabel lblTargetFile = new JLabel("Target File:");
		setFont(lblTargetFile);
		panelCSV.add(lblTargetFile, "cell 0 3,alignx trailing");
		
		comboBoxExcelTargetFile = new JComboBox();
		setFont(comboBoxExcelTargetFile);
		panelCSV.add(comboBoxExcelTargetFile, "cell 1 3,growx");
		
		JButton btnOpenTarget = new JButton("Open");
		setFont(btnOpenTarget);
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxExcelTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save target File");

				if (!filePath.equals("")) {
					setExcelTarget(filePath);
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxExcelTargetFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxExcelTargetFile.setModel(comboBoxModel);
					comboBoxExcelTargetFile.setSelectedItem(filePath);
				}
			}
		});
		panelCSV.add(btnOpenTarget, "cell 2 3");
		
		if (getExcelSource() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getExcelSource());
			comboBoxExcelSourceFile.setModel(comboBoxModel);
		}
		
		if (getExcelPrefix() != null) {
			textFieldExcelPrefix.setText(getExcelPrefix());
		}
		
		if (getExcelTargetType() != null) {
			comboBoxExcelTargetType.setSelectedItem(getExcelTargetType());
		}
		
		if (getExcelTarget() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getExcelTarget());
			comboBoxExcelTargetFile.setModel(comboBoxModel);
		}
	}

	private void setPanelCSV(JPanel panelHolder) {
		// TODO Auto-generated method stub
		panelHolder.removeAll();
		panelHolder.repaint();
		panelHolder.revalidate();
		
		JPanel panelCSV = new JPanel();
		panelHolder.add(panelCSV, BorderLayout.CENTER);
		panelCSV.setBackground(Color.WHITE);
		panelCSV.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
		
		JLabel lblSourceFile = new JLabel("Source File:");
		setFont(lblSourceFile);
		panelCSV.add(lblSourceFile, "cell 0 0,alignx trailing");
		
		comboBoxCSVSourceFile = new JComboBox();
		setFont(comboBoxCSVSourceFile);
		panelCSV.add(comboBoxCSVSourceFile, "cell 1 0,growx");
		
		JButton btnOpenSource = new JButton("Open");
		setFont(btnOpenSource);
		btnOpenSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = methods.chooseFile("Select Source CSV File");
				if (!(filePath == null)) {
					setCsvSource(filePath);
					if (!filePath.equals("")) {
						DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxCSVSourceFile.getModel();
						comboBoxModel.addElement(filePath);
						comboBoxCSVSourceFile.setModel(comboBoxModel);
						comboBoxCSVSourceFile.setSelectedItem(filePath);
					}
				}
			}
		});
		panelCSV.add(btnOpenSource, "cell 2 0");
		
		JLabel lblPrefix = new JLabel("Prefix:");
		setFont(lblPrefix);
		panelCSV.add(lblPrefix, "cell 0 1,alignx trailing");
		
		textFieldCSVPrefix = new JTextField();
		setFont(textFieldCSVPrefix);
		panelCSV.add(textFieldCSVPrefix, "cell 1 1 2 1,growx");
		textFieldCSVPrefix.setColumns(10);
		
		JLabel lblDelimiter = new JLabel("Delimiter:");
		setFont(lblDelimiter);
		panelCSV.add(lblDelimiter, "cell 0 2,alignx trailing");
		
		comboBoxCSVDelimiter = new JComboBox(delimiters);
		setFont(comboBoxCSVDelimiter);
		panelCSV.add(comboBoxCSVDelimiter, "cell 1 2 2 1,growx");
		
		JLabel lblTargetType = new JLabel("Target Type:");
		setFont(lblTargetType);
		panelCSV.add(lblTargetType, "cell 0 3,alignx trailing");
		
		comboBoxCSVTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		setFont(comboBoxCSVTargetType);
		comboBoxCSVTargetType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setCsvTargetType(comboBoxCSVTargetType.getSelectedItem().toString());
			}
		});
		panelCSV.add(comboBoxCSVTargetType, "cell 1 3 2 1,growx");
		
		JLabel lblTargetFile = new JLabel("Target File:");
		setFont(lblTargetFile);
		panelCSV.add(lblTargetFile, "cell 0 4,alignx trailing");
		
		comboBoxCSVTargetFile = new JComboBox();
		setFont(comboBoxCSVTargetFile);
		panelCSV.add(comboBoxCSVTargetFile, "cell 1 4,growx");
		
		JButton btnOpenTarget = new JButton("Open");
		setFont(btnOpenTarget);
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxCSVTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save target File");

				if (!filePath.equals("")) {
					setCsvTarget(filePath);
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxCSVTargetFile.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxCSVTargetFile.setModel(comboBoxModel);
					comboBoxCSVTargetFile.setSelectedItem(filePath);
				}
			}
		});
		panelCSV.add(btnOpenTarget, "cell 2 4");
		
		if (getCsvSource() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getCsvSource());
			comboBoxCSVSourceFile.setModel(comboBoxModel);
		}
		
		if (getCsvPrefix() != null) {
			textFieldCSVPrefix.setText(getCsvPrefix());
		}
		
		if (getCsvDelimiter() != null) {
			comboBoxCSVDelimiter.setSelectedItem(getCsvDelimiter());
		}
		
		if (getCsvTargetType() != null) {
			comboBoxCSVTargetType.setSelectedItem(getCsvTargetType());
		}
		
		if (getCsvTarget() != null) {
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			comboBoxModel.addElement(getCsvTarget());
			comboBoxCSVTargetFile.setModel(comboBoxModel);
		}
	}

	private void setFont(JComponent component) {
		// TODO Auto-generated method stub
		component.setFont(new Font("Tahoma", Font.BOLD, 12));
	}

	public String getCsvSource() {
		return csvSource;
	}

	public void setCsvSource(String csvSource) {
		this.csvSource = csvSource;
	}

	public String getCsvTarget() {
		return csvTarget;
	}

	public void setCsvTarget(String csvTarget) {
		this.csvTarget = csvTarget;
	}

	public String getCsvPrefix() {
		return csvPrefix;
	}

	public void setCsvPrefix(String csvPrefix) {
		this.csvPrefix = csvPrefix;
	}

	public String getCsvDelimiter() {
		return csvDelimiter;
	}

	public void setCsvDelimiter(String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	public String getExcelSource() {
		return excelSource;
	}

	public void setExcelSource(String excelSource) {
		this.excelSource = excelSource;
	}

	public String getExcelTarget() {
		return excelTarget;
	}

	public void setExcelTarget(String excelTarget) {
		this.excelTarget = excelTarget;
	}

	public String getExcelPrefix() {
		return excelPrefix;
	}

	public void setExcelPrefix(String excelPrefix) {
		this.excelPrefix = excelPrefix;
	}

	public String getXmlSource() {
		return xmlSource;
	}

	public void setXmlSource(String xmlSource) {
		this.xmlSource = xmlSource;
	}

	public String getXmlTarget() {
		return xmlTarget;
	}

	public void setXmlTarget(String xmlTarget) {
		this.xmlTarget = xmlTarget;
	}

	public String getXmlPrefix() {
		return xmlPrefix;
	}

	public void setXmlPrefix(String xmlPrefix) {
		this.xmlPrefix = xmlPrefix;
	}

	public String getJsonSource() {
		return jsonSource;
	}

	public void setJsonSource(String jsonSource) {
		this.jsonSource = jsonSource;
	}

	public String getJsonTarget() {
		return jsonTarget;
	}

	public void setJsonTarget(String jsonTarget) {
		this.jsonTarget = jsonTarget;
	}

	public String getJsonPrefix() {
		return jsonPrefix;
	}

	public void setJsonPrefix(String jsonPrefix) {
		this.jsonPrefix = jsonPrefix;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getJsonTargetType() {
		return jsonTargetType;
	}

	public void setJsonTargetType(String jsonTargetType) {
		this.jsonTargetType = jsonTargetType;
	}
	
	public String getCsvTargetType() {
		return csvTargetType;
	}

	public void setCsvTargetType(String csvTargetType) {
		this.csvTargetType = csvTargetType;
	}

	public String getXmlTargetType() {
		return xmlTargetType;
	}

	public void setXmlTargetType(String xmlTargetType) {
		this.xmlTargetType = xmlTargetType;
	}

	public String getExcelTargetType() {
		return excelTargetType;
	}

	public void setExcelTargetType(String excelTargetType) {
		this.excelTargetType = excelTargetType;
	}
}
