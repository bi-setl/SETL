package etl_model;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import core.DBMapping;
import core.RDFWrapper;
import helper.Methods;
import helper.Variables;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;
import view.SETLFrame;

public class ETLRDFWrapper implements ETLOperation {
	private JTextField textFieldCSVSource, textFieldExcelSource;
	private JTextField textFieldCSVPrefix, textFieldExcelPrefix;
	private JTextField textFieldExcelKeyAttribute;
	private JTextField textFieldCSVTarget, textFieldExcelTarget;
	private JTextField textFieldXMLSource, textFieldJsonSource;
	private JTextField textFieldXMLTarget, textFieldJsonTarget;
	private JTextField textFieldDBName, textFieldCSVKeyAttribute;
	private JTextField textFieldDBUserName;
	private JTextField textFieldDBPassword;
	private JTextField textFieldDBTarget;
	private JTextArea textAreaDBQuery;
	private ArrayList allColumnNames, selectedColumns;
	private static final String R2RML = "R2RML";
	private static final String DIRECT_MAPPING = "Direct Mapping";
	private JTextField textFieldDbName;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textFieldDbNameRml;
	private JTextField textFieldRmlUsername;
	private JTextField textFieldRmlPassword;
	private JTextField textFieldRmlTarget;
	private JTextField textFieldTargetPathDirect;
	private JTextField textFieldDirectBaseIRI;
	private JTextField textFieldRmlFilePath;

	private boolean status = false;

	private Methods methods;

	private String[] strings = new String[] { Variables.CSV, Variables.XML, Variables.EXCEL, Variables.JSON, Variables.DB };
	private String[] delimiters = new String[] { Variables.COMMA, Variables.SPACE, Variables.SEMICOLON, Variables.TAB, Variables.PIPE };
	private String[] keyAttributes = new String[] { Variables.EXISTING_ATTRIBUTE, Variables.EXPRESSION, Variables.INCREMENTAL };

	private String csvSource, csvPrefix, csvColumn, csvDelimiter, csvTarget, csvTargetType, csvKeyAttributeType;
	private String excelSource, excelPrefix, excelColumn, excelTarget, excelTargetType, excelKeyAttributeType;
	private String xmlSource, xmlTarget, xmlTargetType;
	private String jsonSource, jsonTarget, jsonTargetType;
	private String dbName, dbUsername, dbPassword, dbDirectBaseIRI, dbDirectTargetPath, dbRmlFilePath, dbRMLTargetPath, fileType, dbMappingType;

	public ETLRDFWrapper() {
		super();
		methods = new Methods();
		initializeAll();
	}

	public ArrayList getAllColumnNames() {
		return allColumnNames;
	}

	public void setAllColumnNames(ArrayList allColumnNames) {
		this.allColumnNames = allColumnNames;
	}

	public String getExcelKeyAttributeType() {
		return excelKeyAttributeType;
	}

	public void setExcelKeyAttributeType(String excelKeyAttributeType) {
		this.excelKeyAttributeType = excelKeyAttributeType;
	}

	public String getCsvKeyAttributeType() {
		return csvKeyAttributeType;
	}

	public void setCsvKeyAttributeType(String csvKeyAttributeType) {
		this.csvKeyAttributeType = csvKeyAttributeType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getCsvSource() {
		return csvSource;
	}

	public void setCsvSource(String csvSource) {
		this.csvSource = csvSource;
	}

	public String getCsvPrefix() {
		return csvPrefix;
	}

	public void setCsvPrefix(String csvPrefix) {
		this.csvPrefix = csvPrefix;
	}

	public String getCsvColumn() {
		return csvColumn;
	}

	public void setCsvColumn(String csvColumn) {
		this.csvColumn = csvColumn;
	}

	public String getCsvDelimiter() {
		return csvDelimiter;
	}

	public void setCsvDelimiter(String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	public String getCsvTarget() {
		return csvTarget;
	}

	public void setCsvTarget(String csvTarget) {
		this.csvTarget = csvTarget;
	}

	public String getCsvTargetType() {
		return csvTargetType;
	}

	public void setCsvTargetType(String csvTargetType) {
		this.csvTargetType = csvTargetType;
	}

	public String getExcelSource() {
		return excelSource;
	}

	public void setExcelSource(String excelSource) {
		this.excelSource = excelSource;
	}

	public String getExcelPrefix() {
		return excelPrefix;
	}

	public void setExcelPrefix(String excelPrefix) {
		this.excelPrefix = excelPrefix;
	}

	public String getExcelColumn() {
		return excelColumn;
	}

	public void setExcelColumn(String excelColumn) {
		this.excelColumn = excelColumn;
	}

	public String getExcelTarget() {
		return excelTarget;
	}

	public void setExcelTarget(String excelTarget) {
		this.excelTarget = excelTarget;
	}

	public String getExcelTargetType() {
		return excelTargetType;
	}

	public void setExcelTargetType(String excelTargetType) {
		this.excelTargetType = excelTargetType;
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

	public String getXmlTargetType() {
		return xmlTargetType;
	}

	public void setXmlTargetType(String xmlTargetType) {
		this.xmlTargetType = xmlTargetType;
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

	public String getJsonTargetType() {
		return jsonTargetType;
	}

	public void setJsonTargetType(String jsonTargetType) {
		this.jsonTargetType = jsonTargetType;
	}

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		final JDialog dialog = new JDialog();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String result = "";
					RDFWrapper rdfWrapper = new RDFWrapper();
					if (getFileType() != null) {
						switch (getFileType()) {
						case "CSV":
							result = rdfWrapper.parseCSVNew(getCsvSource(), getCsvPrefix(), getCsvColumn(),
									getCsvDelimiter(), getCsvTarget());
							break;
						case "XML":
							result = rdfWrapper.parseXML(getXmlSource(), getXmlTarget());
							break;
						case "Excel":
							result = rdfWrapper.parseExcel(getExcelSource(), getExcelTarget(), getExcelPrefix(),
									getExcelColumn());
							break;
						case "JSON":
							result = rdfWrapper.parseJSON(getJsonSource(), getJsonTarget());
							break;
						case "DB":
							if (dbMappingType.equals(DIRECT_MAPPING)) {
								DBMapping dbMapping = new DBMapping();
								result = dbMapping.performDirectMapping(dbName, dbUsername, dbPassword, dbDirectBaseIRI, dbDirectTargetPath);
								break;
							} else {
								DBMapping dbMapping = new DBMapping();
								result = dbMapping.performRMLMapping(dbName, dbUsername, dbPassword, dbRmlFilePath, dbRMLTargetPath);
								break;
							}
							// status = extractDBButtonHandler();
						default:
							break;
						}
					} else {
						textPane.setText(textPane.getText() + "\n" + "Check Input");
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

		/*if (getFileType().equals("DB")) {
			return status;
		} else {
			return true;
		}*/
		
		return true;
	}

	private void initializeAll() {
		// TODO Auto-generated method stub
		allColumnNames = new ArrayList<>();
		selectedColumns = new ArrayList<>();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
//		JPanel panelMain = new JPanel();
//		panelMain.setBackground(Color.WHITE);
//		panelMain.setLayout(new MigLayout("", "[800px, grow, fill]", "[grow]"));
		
		JPanel panelRDFWrapper = new JPanel();
		panelRDFWrapper.setBackground(Color.WHITE);
//		panelMain.add(panelRDFWrapper, "cell 0 0,grow");
		panelRDFWrapper.setLayout(new MigLayout("", "[800px,grow]", "[][grow]"));
//		panelRDFWrapper.setLayout(new BorderLayout(0, 0));
		
		JPanel panelComboBox = new JPanel();
		panelComboBox.setBackground(Color.WHITE);
		panelRDFWrapper.add(panelComboBox, "cell 0 0,grow");
		panelComboBox.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel lblFileType = new JLabel("File Type: ");
		lblFileType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelComboBox.add(lblFileType, "cell 0 0,alignx trailing");
		
		JPanel panelHolder = new JPanel();
		panelHolder.setLayout(new CardLayout(0, 0));
		
		JComboBox comboBox = new JComboBox(strings);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = comboBox.getSelectedItem().toString();
				
				showMenuCard(panelHolder, selectedItem);
			}
		});
		comboBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBox.setBackground(Color.WHITE);
		panelComboBox.add(comboBox, "cell 1 0,growx");
		
		if (getFileType() != null) {
			String selectedItem = getFileType();
			
			showMenuCard(panelHolder, selectedItem);
		}
		
		panelHolder.setBackground(Color.WHITE);
		panelRDFWrapper.add(panelHolder, "cell 0 1,grow");
		
		JPanel panelCSV = new JPanel();
		panelCSV.setBackground(Color.WHITE);
		panelHolder.add(panelCSV, Variables.CSV);
		panelCSV.setLayout(new MigLayout("", "[][grow][]", "[][][][][][grow][][]"));
		
		JLabel lblCSVSource = new JLabel("Source File:");
		lblCSVSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVSource, "cell 0 0,alignx trailing");
		
		textFieldCSVSource = new JTextField();
		Methods.setMargin(textFieldCSVSource);
		textFieldCSVSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(textFieldCSVSource, "cell 1 0,growx");
		textFieldCSVSource.setColumns(10);
		
		if (getCsvSource() != null) {
			textFieldCSVSource.setText(getCsvSource());
		}
		
		JButton btnCSVSourceOpen = new JButton("Open");
		btnCSVSourceOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select CSV Source File");
				if (!(filePath == null)) {
					setCsvSource(filePath);
					textFieldCSVSource.setText(filePath);
				}
			}
		});
		btnCSVSourceOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(btnCSVSourceOpen, "cell 2 0");
		
		JLabel lblCSVPrefix = new JLabel("Prefix:");
		lblCSVPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVPrefix, "cell 0 1,alignx trailing");
		
		textFieldCSVPrefix = new JTextField();
		Methods.setMargin(textFieldCSVPrefix);
		textFieldCSVPrefix.setToolTipText("http://www.something.com");
		textFieldCSVPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(textFieldCSVPrefix, "cell 1 1 2 1,growx");
		textFieldCSVPrefix.setColumns(10);
		
		if (getCsvPrefix() != null) {
			textFieldCSVPrefix.setText(getCsvPrefix());
		}
		
		JLabel lblCSVDelimiter = new JLabel("Delimiter:");
		lblCSVDelimiter.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVDelimiter, "cell 0 2,alignx trailing");
		
		JScrollPane scrollPaneCSV = new JScrollPane();
		JPanel panelCSVColumns = new JPanel();
		panelCSVColumns.setLayout(new GridLayout(0, 2));
		scrollPaneCSV.setViewportView(panelCSVColumns);
		
		JComboBox comboBoxCSVKeyAttribute = new JComboBox(keyAttributes);
		
		JComboBox comboBoxCSVDelimiter = new JComboBox(delimiters);
		comboBoxCSVDelimiter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filePath = textFieldCSVSource.getText().toString().trim();
				String usedDelimiter = comboBoxCSVDelimiter.getSelectedItem().toString();
				
				panelCSVColumns.removeAll();

				allColumnNames = new ArrayList<>();
				selectedColumns = new ArrayList<>();
				
				if (filePath.length() != 0 && usedDelimiter != null) {
					allColumnNames = methods.getColumnNames(filePath, usedDelimiter);

					if (allColumnNames != null) {
						for (int i = 0; i < allColumnNames.size(); i++) {
							JButton btnColumn = new JButton(allColumnNames.get(i).toString());
							btnColumn.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent arg0) {
									setCsvKeyAttributeType(comboBoxCSVKeyAttribute.getSelectedItem().toString());
									if (getCsvKeyAttributeType().equals("Existing Attribute")) {
										textFieldCSVKeyAttribute.setText(btnColumn.getText().toString().trim());
									} else if (getCsvKeyAttributeType().equals("Expression")) {
										selectedColumns.add(btnColumn.getText().toString().trim());
										String concatString = "CONCAT(";
										for (int j = 0; j < selectedColumns.size(); j++) {
											if (j == selectedColumns.size() - 1) {
												concatString += selectedColumns.get(j) + ")";
											} else {
												concatString += selectedColumns.get(j) + ", ";
											}
										}
										
										concatString = concatString.replace("\"", "");
										textFieldCSVKeyAttribute.setText(concatString);
									}
								}
							});
							btnColumn.setBackground(Color.decode("#FFFFCC"));
							btnColumn.setContentAreaFilled(false);
							btnColumn.setOpaque(true);
							panelCSVColumns.add(btnColumn);
						}

						panelCSV.repaint();
						panelCSV.revalidate();
					}
				}
			}
		});
		comboBoxCSVDelimiter.setBackground(Color.WHITE);
		comboBoxCSVDelimiter.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(comboBoxCSVDelimiter, "cell 1 2 2 1,growx");
		
		if (getCsvDelimiter() != null) {
			comboBoxCSVDelimiter.setSelectedItem(getCsvDelimiter());
		}
		
		JLabel lblCSVKeyAttributeType = new JLabel("Key Attribute Type:");
		lblCSVKeyAttributeType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVKeyAttributeType, "cell 0 3,alignx trailing");
		
		textFieldCSVKeyAttribute = new JTextField();
		
		comboBoxCSVKeyAttribute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setCsvKeyAttributeType(comboBoxCSVKeyAttribute.getSelectedItem().toString());
				if (getCsvKeyAttributeType().equals("Incremental")) {
					textFieldCSVKeyAttribute.setText("Incremental");
					panelCSV.remove(scrollPaneCSV);
				} else if (getCsvKeyAttributeType().equals("Expression")) {
					selectedColumns = new ArrayList<>();
					textFieldCSVKeyAttribute.setText("CONCAT(columnName1, columnName2, ...)");
					panelCSV.add(scrollPaneCSV, "cell 1 5 2 1,grow");
				} else {
					textFieldCSVKeyAttribute.setText("");
					panelCSV.add(scrollPaneCSV, "cell 1 5 2 1,grow");
				}

				panelCSV.repaint();
				panelCSV.revalidate();
			}
		});
		comboBoxCSVKeyAttribute.setBackground(Color.WHITE);
		comboBoxCSVKeyAttribute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		comboBoxCSVKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(comboBoxCSVKeyAttribute, "cell 1 3 2 1,growx");
		
		if (getCsvKeyAttributeType() != null) {
			comboBoxCSVKeyAttribute.setSelectedItem(getCsvKeyAttributeType());
		}
		
		JLabel lblCSVKeyAttribute = new JLabel("Key Attribute:");
		lblCSVKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVKeyAttribute, "cell 0 4,alignx trailing");
		
		Methods.setMargin(textFieldCSVKeyAttribute);
		textFieldCSVKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(textFieldCSVKeyAttribute, "cell 1 4 2 1,growx");
		textFieldCSVKeyAttribute.setColumns(10);
		
		if (getCsvColumn() != null) {
			textFieldCSVKeyAttribute.setText(getCsvColumn());
		}
		
		panelCSV.add(scrollPaneCSV, "cell 1 5 2 1,grow");
		panelCSVColumns.setBackground(Color.WHITE);
		
		scrollPaneCSV.setPreferredSize(new Dimension(panelCSVColumns.getPreferredSize().width, 200));
		
		
		JLabel lblCSVTargetType = new JLabel("Target Type:");
		lblCSVTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVTargetType, "cell 0 6,alignx trailing");
		
		JComboBox comboBoxCSVTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxCSVTargetType.setBackground(Color.WHITE);
		comboBoxCSVTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(comboBoxCSVTargetType, "cell 1 6 2 1,growx");
		
		if (getCsvTargetType() != null) {
			comboBoxCSVTargetType.setSelectedItem(getCsvTargetType());
		}
		
		JLabel lblCSVTarget = new JLabel("Target File:");
		lblCSVTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(lblCSVTarget, "cell 0 7,alignx trailing");
		
		textFieldCSVTarget = new JTextField();
		Methods.setMargin(textFieldCSVTarget);
		textFieldCSVTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(textFieldCSVTarget, "cell 1 7,growx");
		textFieldCSVTarget.setColumns(10);
		
		if (getCsvTarget() != null) {
			textFieldCSVTarget.setText(getCsvTarget());
		}
		
		JButton btnCSVTargetOpen = new JButton("Open");
		btnCSVTargetOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.getTargetFileName(comboBoxCSVTargetType, Variables.RDF_WRAPPER);

				if (!filePath.equals("")) {
					setCsvTarget(filePath);
					textFieldCSVTarget.setText(filePath);
				}
			}
		});
		btnCSVTargetOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelCSV.add(btnCSVTargetOpen, "cell 2 7");
		
		JPanel panelXML = new JPanel();
		panelXML.setBackground(Color.WHITE);
		panelHolder.add(panelXML, Variables.XML);
		panelXML.setLayout(new MigLayout("", "[][grow][]", "[][][]"));
		
		JLabel lblXMLSourceFile = new JLabel("Source File:");
		lblXMLSourceFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(lblXMLSourceFile, "cell 0 0,alignx trailing");
		
		textFieldXMLSource = new JTextField();
		Methods.setMargin(textFieldXMLSource);
		textFieldXMLSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(textFieldXMLSource, "cell 1 0,growx");
		textFieldXMLSource.setColumns(10);
		
		if (getXmlSource() != null) {
			textFieldXMLSource.setText(getXmlSource());
		}
		
		JButton btnXMLOpenSource = new JButton("Open");
		btnXMLOpenSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(btnXMLOpenSource, "cell 2 0");
		
		JLabel lblXMLTargetType = new JLabel("Target Type:");
		lblXMLTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(lblXMLTargetType, "cell 0 1,alignx trailing");
		
		JComboBox comboBoxXMLTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxXMLTargetType.setBackground(Color.WHITE);
		comboBoxXMLTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(comboBoxXMLTargetType, "cell 1 1 2 1,growx");
		
		if (getXmlTargetType() != null) {
			comboBoxXMLTargetType.setSelectedItem(getXmlTargetType());
		}
		
		JLabel lblXMLTargetFile = new JLabel("Target File:");
		lblXMLTargetFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(lblXMLTargetFile, "cell 0 2,alignx trailing");
		
		textFieldXMLTarget = new JTextField();
		Methods.setMargin(textFieldXMLTarget);
		textFieldXMLTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(textFieldXMLTarget, "cell 1 2,growx");
		textFieldXMLTarget.setColumns(10);
		
		if (getXmlTarget() != null) {
			textFieldXMLTarget.setText(getXmlTarget());
		}
		
		JButton btnXMLOpenTarget = new JButton("Open");
		btnXMLOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.getTargetFileName(comboBoxCSVTargetType, Variables.RDF_WRAPPER);

				if (!filePath.equals("")) {
					setXmlTarget(filePath);
					textFieldXMLTarget.setText(filePath);
				}
			}
		});
		btnXMLOpenTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelXML.add(btnXMLOpenTarget, "cell 2 2");
		
		JPanel panelExcel = new JPanel();
		panelExcel.setBackground(Color.WHITE);
		panelHolder.add(panelExcel, Variables.EXCEL);
		panelExcel.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][]"));
		
		JLabel lblExcelSource = new JLabel("Source File:");
		lblExcelSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelSource, "cell 0 0,alignx trailing");
		
		textFieldExcelSource = new JTextField();
		Methods.setMargin(textFieldExcelSource);
		textFieldExcelSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(textFieldExcelSource, "cell 1 0,growx");
		textFieldExcelSource.setColumns(10);
		
		if (getExcelSource() != null) {
			textFieldExcelSource.setText(getExcelSource());
		}
		
		JPanel panelExcelColumns = new JPanel();
		panelExcelColumns.setLayout(new GridLayout(0, 2));
		
		JComboBox comboBoxExcelKeyAttributeType = new JComboBox(keyAttributes);
		
		JButton btnExcelSourceOpen = new JButton("Open");
		btnExcelSourceOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select XML Source File");
				if (!(filePath == null)) {
					setExcelSource(filePath);
					textFieldExcelSource.setText(filePath);
					
					panelExcelColumns.removeAll();

					allColumnNames = new ArrayList<>();
					selectedColumns = new ArrayList<>();
					if (filePath.length() != 0) {
						allColumnNames = methods.getColumnNames(filePath);

						if (allColumnNames != null) {
							for (int i = 0; i < allColumnNames.size(); i++) {
								JButton btnColumn = new JButton(allColumnNames.get(i).toString());
								btnColumn.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										if (comboBoxExcelKeyAttributeType.getSelectedItem().toString().equals("Existing Attribute")) {
											textFieldExcelKeyAttribute.setText(btnColumn.getText().toString().trim());
										} else if (comboBoxExcelKeyAttributeType.getSelectedItem().toString().equals("Expression")) {
											selectedColumns.add(btnColumn.getText().toString().trim());
											String concatString = "CONCAT(";
											for (int j = 0; j < selectedColumns.size(); j++) {
												if (j == selectedColumns.size() - 1) {
													concatString += selectedColumns.get(j) + ")";
												} else {
													concatString += selectedColumns.get(j) + ", ";
												}
											}
											textFieldExcelKeyAttribute.setText(concatString);
										}
									}
								});
								btnColumn.setBackground(Color.decode("#FFFFCC"));
								btnColumn.setContentAreaFilled(false);
								btnColumn.setOpaque(true);
								panelExcelColumns.add(btnColumn);
							}
							
							panelExcel.repaint();
							panelExcel.revalidate();
						}
					}
				}
			}
		});
		btnExcelSourceOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(btnExcelSourceOpen, "cell 2 0");
		
		JLabel lblExcelPrefix = new JLabel("Prefix:");
		lblExcelPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelPrefix, "cell 0 1,alignx trailing");
		
		textFieldExcelPrefix = new JTextField();
		Methods.setMargin(textFieldExcelPrefix);
		textFieldExcelPrefix.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(textFieldExcelPrefix, "cell 1 1 2 1,growx");
		textFieldExcelPrefix.setColumns(10);
		
		if (getExcelPrefix() != null) {
			textFieldExcelPrefix.setText(getExcelPrefix());
		}
		
		JLabel lblExcelKeyAttributeType = new JLabel("Key Attribute Type:");
		lblExcelKeyAttributeType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelKeyAttributeType, "cell 0 2,alignx trailing");
		
		comboBoxExcelKeyAttributeType.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBoxExcelKeyAttributeType.setBackground(Color.WHITE);
		panelExcel.add(comboBoxExcelKeyAttributeType, "cell 1 2 2 1,growx");
		
		JLabel lblExcelColumn = new JLabel("Key Attribute:");
		lblExcelColumn.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelColumn, "cell 0 3,alignx trailing");
		
		textFieldExcelKeyAttribute = new JTextField();
		Methods.setMargin(textFieldExcelKeyAttribute);
		textFieldExcelKeyAttribute.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(textFieldExcelKeyAttribute, "cell 1 3 2 1,growx");
		textFieldExcelKeyAttribute.setColumns(10);
		
		if (getExcelColumn() != null) {
			textFieldExcelKeyAttribute.setText(getExcelColumn());
		}
		
		JScrollPane scrollPaneExcel = new JScrollPane();
		panelExcel.add(scrollPaneExcel, "cell 1 4 2 1,grow");
		
		scrollPaneExcel.setViewportView(panelExcelColumns);
		panelExcelColumns.setBackground(Color.WHITE);
		
		JLabel lblExcelTargetType = new JLabel("Target Type:");
		lblExcelTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelTargetType, "cell 0 5,alignx trailing");
		
		JComboBox comboBoxExcelTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxExcelTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(comboBoxExcelTargetType, "cell 1 5 2 1,growx");
		
		if (getExcelTargetType() != null) {
			comboBoxExcelTargetType.setSelectedItem(getExcelTargetType());
		}
		
		JLabel lblExcelTarget = new JLabel("Target File:");
		lblExcelTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(lblExcelTarget, "cell 0 6,alignx trailing");
		
		textFieldExcelTarget = new JTextField();
		Methods.setMargin(textFieldExcelTarget);
		textFieldExcelTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(textFieldExcelTarget, "cell 1 6,growx");
		textFieldExcelTarget.setColumns(10);
		
		if (getExcelTarget() != null) {
			textFieldExcelTarget.setText(getExcelTarget());
		}
		
		JButton btnExcelTargetOpen = new JButton("Open");
		btnExcelTargetOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.getTargetFileName(comboBoxCSVTargetType, Variables.RDF_WRAPPER);

				if (!filePath.equals("")) {
					setExcelTarget(filePath);
					textFieldExcelTarget.setText(filePath);
				}

			}
		});
		btnExcelTargetOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelExcel.add(btnExcelTargetOpen, "cell 2 6");
		
		JPanel panelJson = new JPanel();
		panelJson.setBackground(Color.WHITE);
		panelHolder.add(panelJson, Variables.JSON);
		panelJson.setLayout(new MigLayout("", "[][grow][]", "[][][]"));
		
		JLabel lblJsonSourceFile = new JLabel("Source File:");
		lblJsonSourceFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(lblJsonSourceFile, "cell 0 0,alignx trailing");
		
		textFieldJsonSource = new JTextField();
		Methods.setMargin(textFieldJsonSource);
		textFieldJsonSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(textFieldJsonSource, "cell 1 0,growx");
		textFieldJsonSource.setColumns(10);
		
		if (getJsonSource() != null) {
			textFieldJsonSource.setText(getJsonSource());
		}
		
		JButton btnJsonOpenSource = new JButton("Open");
		btnJsonOpenSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(btnJsonOpenSource, "cell 2 0");
		
		JLabel lblJsonTargetType = new JLabel("Target Type:");
		lblJsonTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(lblJsonTargetType, "cell 0 1,alignx trailing");
		
		JComboBox comboBoxJsonTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxJsonTargetType.setBackground(Color.WHITE);
		comboBoxJsonTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(comboBoxJsonTargetType, "cell 1 1 2 1,growx");
		
		if (getJsonTargetType() != null) {
			comboBoxJsonTargetType.setSelectedItem(getJsonTargetType());
		}
		
		JLabel lblJsonTargetFile = new JLabel("Target File:");
		lblJsonTargetFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(lblJsonTargetFile, "cell 0 2,alignx trailing");
		
		textFieldJsonTarget = new JTextField();
		Methods.setMargin(textFieldJsonTarget);
		textFieldJsonTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(textFieldJsonTarget, "cell 1 2,growx");
		textFieldJsonTarget.setColumns(10);

		if (getJsonTarget() != null) {
			textFieldJsonTarget.setText(getJsonTarget());
		}
		
		JButton btnJsonOpenTarget = new JButton("Open");
		btnJsonOpenTarget.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String filePath = methods.getTargetFileName(comboBoxCSVTargetType, Variables.RDF_WRAPPER);

				if (!filePath.equals("")) {
					setJsonTarget(filePath);
					textFieldJsonTarget.setText(filePath);
				}
			}
		});
		btnJsonOpenTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelJson.add(btnJsonOpenTarget, "cell 2 2");
		
		JPanel panelDB = new JPanel();
		panelDB.setBackground(Color.WHITE);
		panelHolder.add(panelDB, Variables.DB);
		panelDB.setLayout(new MigLayout("", "[][grow]", "[][grow]"));
		
		JLabel lblType = new JLabel("Type:");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDB.add(lblType, "cell 0 0,alignx trailing");
		
		JPanel panelTypeHolder = new JPanel();
		panelTypeHolder.setLayout(new CardLayout(0, 0));
		
		String[] types = new String[] {Variables.DIRECT_MAPPING, Variables.R2RML};
		JComboBox comboBoxType = new JComboBox(types);
		comboBoxType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = comboBoxType.getSelectedItem().toString();
				
				showDBMenuCard(panelTypeHolder, selectedItem);
			}
		});
		comboBoxType.setBackground(Color.WHITE);
		comboBoxType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDB.add(comboBoxType, "cell 1 0,growx");
		
		if (getDbMappingType() != null) {
			String selectedItem = getDbMappingType();
			showDBMenuCard(panelTypeHolder, selectedItem);
		}
		
		panelTypeHolder.setBackground(Color.WHITE);
		panelDB.add(panelTypeHolder, "cell 0 1 2 1,grow");
		
		JPanel panelDirect = new JPanel();
		panelDirect.setBackground(Color.WHITE);
		panelTypeHolder.add(panelDirect, Variables.DIRECT_MAPPING);
		panelDirect.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
		
		JLabel lblDbName = new JLabel("DB Name:");
		lblDbName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbName, "cell 0 0,alignx trailing");
		
		textFieldDbName = new JTextField();
		Methods.setMargin(textFieldDbName);
		textFieldDbName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldDbName, "cell 1 0 2 1,growx");
		textFieldDbName.setColumns(10);
		
		if (getDbName() != null) {
			textFieldDbName.setText(getDbName());
		}
		
		JLabel lblDbUsername = new JLabel("DB Username:");
		lblDbUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbUsername, "cell 0 1,alignx trailing");
		
		textFieldUsername = new JTextField();
		Methods.setMargin(textFieldUsername);
		textFieldUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldUsername, "cell 1 1 2 1,growx");
		textFieldUsername.setColumns(10);
		
		if (getDbUsername() != null) {
			textFieldUsername.setText(getDbUsername());
		}
		
		JLabel lblDbUserPassword = new JLabel("DB User Password:");
		lblDbUserPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbUserPassword, "cell 0 2,alignx trailing");
		
		textFieldPassword = new JPasswordField();
		Methods.setMargin(textFieldPassword);
		textFieldPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldPassword, "cell 1 2 2 1,growx");
		textFieldPassword.setColumns(10);
		
		if (getDbPassword() != null) {
			textFieldPassword.setText(getDbPassword());
		}
		
		JLabel lblBaseIri_1 = new JLabel("Base IRI:");
		lblBaseIri_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblBaseIri_1, "cell 0 3,alignx trailing");
		
		textFieldDirectBaseIRI = new JTextField();
		Methods.setMargin(textFieldDirectBaseIRI);
		textFieldDirectBaseIRI.setToolTipText("Base URL is the URL for primary keys");
		textFieldDirectBaseIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldDirectBaseIRI, "cell 1 3 2 1,growx");
		textFieldDirectBaseIRI.setColumns(10);
		
		if (getDbDirectBaseIRI() != null) {
			textFieldDirectBaseIRI.setText(getDbDirectBaseIRI());
		}
		
		JLabel lblTargetPath_1 = new JLabel("Target Path:");
		lblTargetPath_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblTargetPath_1, "cell 0 4,alignx trailing");
		
		textFieldTargetPathDirect = new JTextField();
		Methods.setMargin(textFieldTargetPathDirect);
		textFieldTargetPathDirect.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldTargetPathDirect, "cell 1 4,growx");
		textFieldTargetPathDirect.setColumns(10);
		
		if (getDbDirectTargetPath() != null) {
			textFieldTargetPathDirect.setText(getDbDirectTargetPath());
		}
		
		JButton btnOpen = new JButton("Open");
		btnOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(btnOpen, "cell 2 4");
		
		JPanel panelRML = new JPanel();
		panelRML.setBackground(Color.WHITE);
		panelTypeHolder.add(panelRML, Variables.R2RML);
		panelRML.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
		
		JLabel lblDbName_1 = new JLabel("DB Name:");
		lblDbName_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbName_1, "cell 0 0,alignx trailing");
		
		textFieldDbNameRml = new JTextField();
		Methods.setMargin(textFieldDbNameRml);
		textFieldDbNameRml.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldDbNameRml, "cell 1 0 2 1,growx");
		textFieldDbNameRml.setColumns(10);
		
		if (getDbName() != null) {
			textFieldDbNameRml.setText(getDbName());
		}
		
		JLabel lblDbUsername_1 = new JLabel("DB Username:");
		lblDbUsername_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbUsername_1, "cell 0 1,alignx trailing");
		
		textFieldRmlUsername = new JTextField();
		Methods.setMargin(textFieldRmlUsername);
		textFieldRmlUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlUsername, "cell 1 1 2 1,growx");
		textFieldRmlUsername.setColumns(10);
		
		if (getDbUsername() != null) {
			textFieldRmlUsername.setText(getDbUsername());
		}
		
		JLabel lblDbUserPassword_1 = new JLabel("DB User Password");
		lblDbUserPassword_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbUserPassword_1, "cell 0 2,alignx trailing");
		
		textFieldRmlPassword = new JPasswordField();
		Methods.setMargin(textFieldRmlPassword);
		textFieldRmlPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlPassword, "cell 1 2 2 1,growx");
		textFieldRmlPassword.setColumns(10);
		
		if (getDbPassword() != null) {
			textFieldRmlPassword.setText(getDbPassword());
		}
		
		JLabel lblRmlFilePath = new JLabel("RML File Path:");
		lblRmlFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblRmlFilePath, "cell 0 3,alignx trailing");
		
		textFieldRmlFilePath = new JTextField();
		Methods.setMargin(textFieldRmlFilePath);
		textFieldRmlFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlFilePath, "cell 1 3,growx");
		textFieldRmlFilePath.setColumns(10);
		
		if (getDbRmlFilePath() != null) {
			textFieldRmlFilePath.setText(getDbRmlFilePath());
		}
		
		JButton btnOpenRMLFile = new JButton("Open");
		btnOpenRMLFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select R2RML Mapping File");
				textFieldRmlFilePath.setText(filePath);
			}
		});
		btnOpenRMLFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(btnOpenRMLFile, "cell 2 3");
		
		JLabel lblTargetPath = new JLabel("Target Path:");
		lblTargetPath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblTargetPath, "cell 0 4,alignx trailing");
		
		textFieldRmlTarget = new JTextField();
		Methods.setMargin(textFieldRmlTarget);
		textFieldRmlTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlTarget, "cell 1 4,growx");
		textFieldRmlTarget.setColumns(10);
		
		if (getDbRMLTargetPath() != null) {
			textFieldRmlFilePath.setText(getDbRMLTargetPath());
		}
		
		JButton btnOpenTarget = new JButton("Open");
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameString = Variables.RDF_WRAPPER + "_" + Calendar.getInstance().getTimeInMillis() +".n3";
				String filePath = methods.chooseSaveFile("", fileNameString, "Choose File");
				textFieldRmlTarget.setText(filePath);
			}
		});
		btnOpenTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(btnOpenTarget, "cell 2 4");
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelRDFWrapper, "Please Input Values for RDF wrapper.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {
			String selection = comboBox.getSelectedItem().toString();
			setFileType(selection);
			switch (selection) {
			case "CSV":
				setCsvSource(textFieldCSVSource.getText().toString().trim());
				setCsvTarget(textFieldCSVTarget.getText().toString().trim());
				setCsvPrefix(textFieldCSVPrefix.getText().toString().trim());
				setCsvColumn(textFieldCSVKeyAttribute.getText().toString().trim());
				setCsvDelimiter(comboBoxCSVDelimiter.getSelectedItem().toString());
				setCsvTargetType(comboBoxCSVTargetType.getSelectedItem().toString());
				
				System.out.println(getCsvColumn());

				ArrayList<String> inputList = new ArrayList<String>();
				inputList.add(getCsvSource());
				inputList.add(getCsvTarget());
				inputList.add(getCsvPrefix());
				inputList.add(getCsvColumn());
				inputList.add(getCsvDelimiter());

				inputParamsMap.get(RDF_MAPPER).add(getCsvTarget());

				if (getCsvSource().length() == 0 || getCsvTarget().length() == 0 || getCsvPrefix().length() == 0
						|| getCsvColumn().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			case "XML":
				setXmlSource(textFieldXMLSource.getText().toString().trim());
				setXmlTarget(textFieldXMLTarget.getText().toString().trim());
				setXmlTargetType(comboBoxXMLTargetType.getSelectedItem().toString());

				inputList = new ArrayList<String>();
				inputList.add(getXmlSource());
				inputList.add(getXmlTarget());

				inputParamsMap.get(RDF_MAPPER).add(getXmlSource());

				if (getXmlSource().length() == 0 || getXmlTarget().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			case "Excel":
				setExcelSource(textFieldExcelSource.getText().toString().trim());
				setExcelTarget(textFieldExcelTarget.getText().toString().trim());
				setExcelPrefix(textFieldExcelPrefix.getText().toString().trim());
				setExcelColumn(textFieldExcelKeyAttribute.getText().toString().trim());
				setExcelTargetType(comboBoxExcelTargetType.getSelectedItem().toString());

				inputList = new ArrayList<String>();
				inputList.add(getExcelSource());
				inputList.add(getExcelTarget());
				inputList.add(getExcelPrefix());
				inputList.add(getExcelColumn());

				inputParamsMap.get(RDF_MAPPER).add(getExcelTarget());

				if (getExcelSource().length() == 0 || getExcelTarget().length() == 0 || getExcelPrefix().length() == 0
						|| getExcelColumn().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			case "JSON":
				setJsonSource(textFieldJsonSource.getText().toString().trim());
				setJsonTarget(textFieldJsonTarget.getText().toString().trim());
				setJsonTargetType(comboBoxJsonTargetType.getSelectedItem().toString());

				inputList = new ArrayList<String>();
				inputList.add(getJsonSource());
				inputList.add(getJsonTarget());

				inputParamsMap.get(RDF_MAPPER).add(getJsonSource());

				if (getJsonSource().length() == 0 || getJsonTarget().length() == 0) {
					methods.showDialog("Please select the all data.");
					return false;
				} else {
					return true;
				}
			case "DB":
				dbMappingType = comboBoxType.getSelectedItem().toString();
				
				if (dbMappingType.equals(DIRECT_MAPPING)) {
					
					dbName = textFieldDbName.getText().toString().trim();
					dbUsername = textFieldUsername.getText().toString().trim();
					dbPassword = textFieldPassword.getText().toString().trim();
					dbDirectBaseIRI = textFieldDirectBaseIRI.getText().toString().trim();
					dbDirectTargetPath = textFieldTargetPathDirect.getText().toString().trim();
					
					if (methods.checkStrings(dbName, dbUsername, dbPassword, dbDirectBaseIRI, dbDirectTargetPath)) {
						inputParamsMap.get(RDF_MAPPER).add(dbDirectTargetPath);
						return true;
					} else {
						methods.showDialog("Check all input values");
						return false;
					}
				} else {
					dbName = textFieldDbNameRml.getText().toString().trim();
					dbUsername = textFieldRmlUsername.getText().toString().trim();
					dbPassword = textFieldRmlPassword.getText().toString().trim();
					dbRmlFilePath = textFieldRmlFilePath.getText().toString().trim();
					dbRMLTargetPath = textFieldRmlTarget.getText().toString().trim();
					
					if (methods.checkStrings(dbName, dbUsername, dbPassword, dbRmlFilePath, dbRMLTargetPath)) {
						inputParamsMap.get(RDF_MAPPER).add(dbRMLTargetPath);
						return true;
					} else {
						methods.showDialog("Check all input values");
						return false;
					}
				}
			default:
				break;
			}

		} else {
			methods.showDialog("Please Provide input for RDF wrapper");
			return false;
		}
		return true;
	}

	private boolean extractDBButtonHandler() {
		/*DatabaseConnection databaseConnection = new DatabaseConnection();
		Connection connection = databaseConnection.getConnection(dbName, dbUsername, dbPassword);
		System.out.println(dbName + " " + dbUsername + " " + dbPassword);

		if (connection == null) {
			methods.showDialog("Database connection not established.\nExtraction failed!");
		} else {
			SETLFrame.dbURL = dbName;
			SETLFrame.dbUserName = dbUsername;
			SETLFrame.dbPassword = dbPassword;

		}

		DatabaseOperations dbOperations = new DatabaseOperations(dbName, dbUsername, dbPassword);*/
		// dbOperations.importDataToCSV(dbQuery, filePath);

		return true;
	}

	public JTextField getTextFieldCSVSource() {
		return textFieldCSVSource;
	}

	public void setTextFieldCSVSource(JTextField textFieldCSVSource) {
		this.textFieldCSVSource = textFieldCSVSource;
	}

	public JTextField getTextFieldExcelSource() {
		return textFieldExcelSource;
	}

	public void setTextFieldExcelSource(JTextField textFieldExcelSource) {
		this.textFieldExcelSource = textFieldExcelSource;
	}

	public JTextField getTextFieldCSVPrefix() {
		return textFieldCSVPrefix;
	}

	public void setTextFieldCSVPrefix(JTextField textFieldCSVPrefix) {
		this.textFieldCSVPrefix = textFieldCSVPrefix;
	}

	public JTextField getTextFieldExcelPrefix() {
		return textFieldExcelPrefix;
	}

	public void setTextFieldExcelPrefix(JTextField textFieldExcelPrefix) {
		this.textFieldExcelPrefix = textFieldExcelPrefix;
	}

	public JTextField getTextFieldExcelKeyAttribute() {
		return textFieldExcelKeyAttribute;
	}

	public void setTextFieldExcelKeyAttribute(JTextField textFieldExcelKeyAttribute) {
		this.textFieldExcelKeyAttribute = textFieldExcelKeyAttribute;
	}

	public JTextField getTextFieldCSVTarget() {
		return textFieldCSVTarget;
	}

	public void setTextFieldCSVTarget(JTextField textFieldCSVTarget) {
		this.textFieldCSVTarget = textFieldCSVTarget;
	}

	public JTextField getTextFieldExcelTarget() {
		return textFieldExcelTarget;
	}

	public void setTextFieldExcelTarget(JTextField textFieldExcelTarget) {
		this.textFieldExcelTarget = textFieldExcelTarget;
	}

	public JTextField getTextFieldXMLSource() {
		return textFieldXMLSource;
	}

	public void setTextFieldXMLSource(JTextField textFieldXMLSource) {
		this.textFieldXMLSource = textFieldXMLSource;
	}

	public JTextField getTextFieldJsonSource() {
		return textFieldJsonSource;
	}

	public void setTextFieldJsonSource(JTextField textFieldJsonSource) {
		this.textFieldJsonSource = textFieldJsonSource;
	}

	public JTextField getTextFieldXMLTarget() {
		return textFieldXMLTarget;
	}

	public void setTextFieldXMLTarget(JTextField textFieldXMLTarget) {
		this.textFieldXMLTarget = textFieldXMLTarget;
	}

	public JTextField getTextFieldJsonTarget() {
		return textFieldJsonTarget;
	}

	public void setTextFieldJsonTarget(JTextField textFieldJsonTarget) {
		this.textFieldJsonTarget = textFieldJsonTarget;
	}

	public JTextField getTextFieldDBName() {
		return textFieldDBName;
	}

	public void setTextFieldDBName(JTextField textFieldDBName) {
		this.textFieldDBName = textFieldDBName;
	}

	public JTextField getTextFieldCSVKeyAttribute() {
		return textFieldCSVKeyAttribute;
	}

	public void setTextFieldCSVKeyAttribute(JTextField textFieldCSVKeyAttribute) {
		this.textFieldCSVKeyAttribute = textFieldCSVKeyAttribute;
	}

	public JTextField getTextFieldDBUserName() {
		return textFieldDBUserName;
	}

	public void setTextFieldDBUserName(JTextField textFieldDBUserName) {
		this.textFieldDBUserName = textFieldDBUserName;
	}

	public JTextField getTextFieldDBPassword() {
		return textFieldDBPassword;
	}

	public void setTextFieldDBPassword(JTextField textFieldDBPassword) {
		this.textFieldDBPassword = textFieldDBPassword;
	}

	public JTextField getTextFieldDBTarget() {
		return textFieldDBTarget;
	}

	public void setTextFieldDBTarget(JTextField textFieldDBTarget) {
		this.textFieldDBTarget = textFieldDBTarget;
	}

	public JTextArea getTextAreaDBQuery() {
		return textAreaDBQuery;
	}

	public void setTextAreaDBQuery(JTextArea textAreaDBQuery) {
		this.textAreaDBQuery = textAreaDBQuery;
	}

	public ArrayList getSelectedColumns() {
		return selectedColumns;
	}

	public void setSelectedColumns(ArrayList selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	public JTextField getTextFieldDbName() {
		return textFieldDbName;
	}

	public void setTextFieldDbName(JTextField textFieldDbName) {
		this.textFieldDbName = textFieldDbName;
	}

	public JTextField getTextFieldUsername() {
		return textFieldUsername;
	}

	public void setTextFieldUsername(JTextField textFieldUsername) {
		this.textFieldUsername = textFieldUsername;
	}

	public JTextField getTextFieldPassword() {
		return textFieldPassword;
	}

	public void setTextFieldPassword(JTextField textFieldPassword) {
		this.textFieldPassword = textFieldPassword;
	}

	public JTextField getTextFieldDbNameRml() {
		return textFieldDbNameRml;
	}

	public void setTextFieldDbNameRml(JTextField textFieldDbNameRml) {
		this.textFieldDbNameRml = textFieldDbNameRml;
	}

	public JTextField getTextFieldRmlUsername() {
		return textFieldRmlUsername;
	}

	public void setTextFieldRmlUsername(JTextField textFieldRmlUsername) {
		this.textFieldRmlUsername = textFieldRmlUsername;
	}

	public JTextField getTextFieldRmlPassword() {
		return textFieldRmlPassword;
	}

	public void setTextFieldRmlPassword(JTextField textFieldRmlPassword) {
		this.textFieldRmlPassword = textFieldRmlPassword;
	}

	public JTextField getTextFieldRmlTarget() {
		return textFieldRmlTarget;
	}

	public void setTextFieldRmlTarget(JTextField textFieldRmlTarget) {
		this.textFieldRmlTarget = textFieldRmlTarget;
	}

	public JTextField getTextFieldTargetPathDirect() {
		return textFieldTargetPathDirect;
	}

	public void setTextFieldTargetPathDirect(JTextField textFieldTargetPathDirect) {
		this.textFieldTargetPathDirect = textFieldTargetPathDirect;
	}

	public JTextField getTextFieldDirectBaseIRI() {
		return textFieldDirectBaseIRI;
	}

	public void setTextFieldDirectBaseIRI(JTextField textFieldDirectBaseIRI) {
		this.textFieldDirectBaseIRI = textFieldDirectBaseIRI;
	}

	public JTextField getTextFieldRmlFilePath() {
		return textFieldRmlFilePath;
	}

	public void setTextFieldRmlFilePath(JTextField textFieldRmlFilePath) {
		this.textFieldRmlFilePath = textFieldRmlFilePath;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Methods getMethods() {
		return methods;
	}

	public void setMethods(Methods methods) {
		this.methods = methods;
	}

	public String[] getStrings() {
		return strings;
	}

	public void setStrings(String[] strings) {
		this.strings = strings;
	}

	public String[] getDelimiters() {
		return delimiters;
	}

	public void setDelimiters(String[] delimiters) {
		this.delimiters = delimiters;
	}

	public String[] getKeyAttributes() {
		return keyAttributes;
	}

	public void setKeyAttributes(String[] keyAttributes) {
		this.keyAttributes = keyAttributes;
	}

	public String getDbDirectBaseIRI() {
		return dbDirectBaseIRI;
	}

	public void setDbDirectBaseIRI(String dbDirectBaseIRI) {
		this.dbDirectBaseIRI = dbDirectBaseIRI;
	}

	public String getDbDirectTargetPath() {
		return dbDirectTargetPath;
	}

	public void setDbDirectTargetPath(String dbDirectTargetPath) {
		this.dbDirectTargetPath = dbDirectTargetPath;
	}

	public String getDbRmlFilePath() {
		return dbRmlFilePath;
	}

	public void setDbRmlFilePath(String dbRmlFilePath) {
		this.dbRmlFilePath = dbRmlFilePath;
	}

	public String getDbRMLTargetPath() {
		return dbRMLTargetPath;
	}

	public void setDbRMLTargetPath(String dbRMLTargetPath) {
		this.dbRMLTargetPath = dbRMLTargetPath;
	}

	public String getDbMappingType() {
		return dbMappingType;
	}

	public void setDbMappingType(String dbMappingType) {
		this.dbMappingType = dbMappingType;
	}

	public static String getR2rml() {
		return R2RML;
	}

	public static String getDirectMapping() {
		return DIRECT_MAPPING;
	}

	private void showMenuCard(JPanel panelHolder, String selectedItem) {
		CardLayout cardLayout = (CardLayout) panelHolder.getLayout();
		
		if (selectedItem.equals(Variables.CSV)) {
			cardLayout.show(panelHolder, Variables.CSV);
		} else if (selectedItem.equals(Variables.XML)) {
			cardLayout.show(panelHolder, Variables.XML);
		} else if (selectedItem.equals(Variables.EXCEL)) {
			cardLayout.show(panelHolder, Variables.EXCEL);
		} else if (selectedItem.equals(Variables.JSON)) {
			cardLayout.show(panelHolder, Variables.JSON);
		} else if (selectedItem.equals(Variables.DB)) {
			cardLayout.show(panelHolder, Variables.DB);
		} else {
			cardLayout.show(panelHolder, Variables.CSV);
		}
	}
	
	private void showDBMenuCard(JPanel panelTypeHolder, String selectedItem) {
		CardLayout cardLayout = (CardLayout) panelTypeHolder.getLayout();
		if (selectedItem.equals(Variables.DIRECT_MAPPING)) {
			cardLayout.show(panelTypeHolder, Variables.DIRECT_MAPPING);
		} else {
			cardLayout.show(panelTypeHolder, Variables.R2RML);
		}
	}
}