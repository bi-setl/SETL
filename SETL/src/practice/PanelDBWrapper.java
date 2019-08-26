package practice;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComboBox;
import java.awt.CardLayout;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;

import core.DBMapping;
import helper.Methods;

public class PanelDBWrapper extends JPanel {
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
	private Methods methods;
	private JTextField textFieldDirectBaseIRI;
	private JTextField textFieldRmlFilePath;

	/**
	 * Create the panel.
	 */
	public PanelDBWrapper() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panelHolderTwo = new JPanel();
		panelHolderTwo.setBackground(Color.WHITE);
		add(panelHolderTwo, "cell 0 0,grow");
		panelHolderTwo.setLayout(new MigLayout("", "[][grow]", "[][grow]"));
		
		JLabel lblType = new JLabel("Type:");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHolderTwo.add(lblType, "cell 0 0,alignx trailing");
		
		JPanel panelTypeHolder = new JPanel();
		
		String[] types = new String[] {DIRECT_MAPPING, R2RML};
		JComboBox comboBoxType = new JComboBox(types);
		comboBoxType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedItem = comboBoxType.getSelectedItem().toString();
				
				CardLayout cardLayout = (CardLayout) panelTypeHolder.getLayout();
				if (selectedItem.equals(DIRECT_MAPPING)) {
					cardLayout.show(panelTypeHolder, DIRECT_MAPPING);
				} else {
					cardLayout.show(panelTypeHolder, R2RML);
				}
			}
		});
		comboBoxType.setBackground(Color.WHITE);
		comboBoxType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelHolderTwo.add(comboBoxType, "cell 1 0,growx");
		
		panelTypeHolder.setBackground(Color.WHITE);
		panelHolderTwo.add(panelTypeHolder, "cell 0 1 2 1,grow");
		panelTypeHolder.setLayout(new CardLayout(0, 0));
		
		JPanel panelDirect = new JPanel();
		panelDirect.setBackground(Color.WHITE);
		panelTypeHolder.add(panelDirect, DIRECT_MAPPING);
		panelDirect.setLayout(new MigLayout("", "[][grow][]", "[][][][][][]"));
		
		JLabel lblDbName = new JLabel("DB Name:");
		lblDbName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbName, "cell 0 0,alignx trailing");
		
		textFieldDbName = new JTextField();
		textFieldDbName.setMargin(new Insets(5, 5, 5, 5));
		textFieldDbName.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldDbName, "cell 1 0 2 1,growx");
		textFieldDbName.setColumns(10);
		
		JLabel lblDbUsername = new JLabel("DB Username:");
		lblDbUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbUsername, "cell 0 1,alignx trailing");
		
		textFieldUsername = new JTextField();
		textFieldUsername.setMargin(new Insets(5, 5, 5, 5));
		textFieldUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldUsername, "cell 1 1 2 1,growx");
		textFieldUsername.setColumns(10);
		
		JLabel lblDbUserPassword = new JLabel("DB User Password:");
		lblDbUserPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblDbUserPassword, "cell 0 2,alignx trailing");
		
		textFieldPassword = new JPasswordField();
		textFieldPassword.setMargin(new Insets(5, 5, 5, 5));
		textFieldPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldPassword, "cell 1 2 2 1,growx");
		textFieldPassword.setColumns(10);
		
		JLabel lblBaseIri_1 = new JLabel("Base IRI:");
		lblBaseIri_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblBaseIri_1, "cell 0 3,alignx trailing");
		
		textFieldDirectBaseIRI = new JTextField();
		textFieldDirectBaseIRI.setToolTipText("Base URL is the URL for primary keys");
		textFieldDirectBaseIRI.setMargin(new Insets(5, 5, 5, 5));
		textFieldDirectBaseIRI.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldDirectBaseIRI, "cell 1 3 2 1,growx");
		textFieldDirectBaseIRI.setColumns(10);
		
		JLabel lblTargetPath_1 = new JLabel("Target Path:");
		lblTargetPath_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(lblTargetPath_1, "cell 0 4,alignx trailing");
		
		textFieldTargetPathDirect = new JTextField();
		textFieldTargetPathDirect.setMargin(new Insets(5, 5, 5, 5));
		textFieldTargetPathDirect.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(textFieldTargetPathDirect, "cell 1 4,growx");
		textFieldTargetPathDirect.setColumns(10);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameString = "direct_mapping_" + Calendar.getInstance().getTimeInMillis() +".rdf";
				String filePath = methods.chooseSaveFile("", fileNameString, "Choose File");
				textFieldTargetPathDirect.setText(filePath);
			}
		});
		btnOpen.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(btnOpen, "cell 2 4");
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dbName = textFieldDbName.getText().toString().trim();
				String username = textFieldUsername.getText().toString().trim();
				String password = textFieldPassword.getText().toString().trim();
				String baseIRI = textFieldDirectBaseIRI.getText().toString().trim();
				String targetPath = textFieldTargetPathDirect.getText().toString().trim();
				
				if (methods.checkStrings(dbName, username, password, baseIRI, targetPath)) {
					DBMapping dbMapping = new DBMapping();
					String resultString = dbMapping.performDirectMapping(dbName, username, password, baseIRI, targetPath);
					System.out.println(resultString);
				} else {
					methods.showDialog("Check all input values");
				}
			}
		});
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelDirect.add(btnSave, "cell 1 5,alignx center");
		
		JPanel panelRML = new JPanel();
		panelRML.setBackground(Color.WHITE);
		panelTypeHolder.add(panelRML, R2RML);
		panelRML.setLayout(new MigLayout("", "[][grow][]", "[][][][][][]"));
		
		JLabel lblDbName_1 = new JLabel("DB Name:");
		lblDbName_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbName_1, "cell 0 0,alignx trailing");
		
		textFieldDbNameRml = new JTextField();
		textFieldDbNameRml.setMargin(new Insets(5, 5, 5, 5));
		textFieldDbNameRml.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldDbNameRml, "cell 1 0 2 1,growx");
		textFieldDbNameRml.setColumns(10);
		
		JLabel lblDbUsername_1 = new JLabel("DB Username:");
		lblDbUsername_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbUsername_1, "cell 0 1,alignx trailing");
		
		textFieldRmlUsername = new JTextField();
		textFieldRmlUsername.setMargin(new Insets(5, 5, 5, 5));
		textFieldRmlUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlUsername, "cell 1 1 2 1,growx");
		textFieldRmlUsername.setColumns(10);
		
		JLabel lblDbUserPassword_1 = new JLabel("DB User Password:");
		lblDbUserPassword_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblDbUserPassword_1, "cell 0 2,alignx trailing");
		
		textFieldRmlPassword = new JPasswordField();
		textFieldRmlPassword.setMargin(new Insets(5, 5, 5, 5));
		textFieldRmlPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlPassword, "cell 1 2 2 1,growx");
		textFieldRmlPassword.setColumns(10);
		
		JLabel lblRmlFilePath = new JLabel("RML File Path:");
		lblRmlFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(lblRmlFilePath, "cell 0 3,alignx trailing");
		
		textFieldRmlFilePath = new JTextField();
		textFieldRmlFilePath.setMargin(new Insets(5, 5, 5, 5));
		textFieldRmlFilePath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlFilePath, "cell 1 3,growx");
		textFieldRmlFilePath.setColumns(10);
		
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
		textFieldRmlTarget.setMargin(new Insets(5, 5, 5, 5));
		textFieldRmlTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(textFieldRmlTarget, "cell 1 4,growx");
		textFieldRmlTarget.setColumns(10);
		
		JButton btnOpenTarget = new JButton("Open");
		btnOpenTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileNameString = "r2rml_mapping_" + Calendar.getInstance().getTimeInMillis() +".n3";
				String filePath = methods.chooseSaveFile("", fileNameString, "Choose File");
				textFieldRmlTarget.setText(filePath);
			}
		});
		btnOpenTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(btnOpenTarget, "cell 2 4");
		
		JButton btnSaveRML = new JButton("Save");
		btnSaveRML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String dbName = textFieldDbNameRml.getText().toString().trim();
				String username = textFieldRmlUsername.getText().toString().trim();
				String password = textFieldRmlPassword.getText().toString().trim();
				String rmlPath = textFieldRmlFilePath.getText().toString().trim();
				String targetPath = textFieldRmlTarget.getText().toString().trim();
				
				if (methods.checkStrings(dbName, username, password, rmlPath, targetPath)) {
					DBMapping dbMapping = new DBMapping();
					String resultString = dbMapping.performRMLMapping(dbName, username, password, rmlPath, targetPath);
					System.out.println(resultString);
				} else {
					methods.showDialog("Check all input values");
				}
			}
		});
		btnSaveRML.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelRML.add(btnSaveRML, "cell 1 5,alignx center");
		
		initializeAll();
		comboBoxType.setSelectedIndex(1);
	}

	private void initializeAll() {
		// TODO Auto-generated method stub
		methods = new Methods();
		textFieldDbName.setText("dvdrental");
		textFieldUsername.setText("postgres");
		textFieldPassword.setText("12345");
		textFieldTargetPathDirect.setText("C:\\Users\\USER\\Documents\\direct_mapping_1563260532680.rdf");
		textFieldDirectBaseIRI.setText("https://www.facebook.com/");
		
		textFieldDbNameRml.setText("dvdrental");
		textFieldRmlUsername.setText("postgres");
		textFieldRmlPassword.setText("12345");
		textFieldRmlFilePath.setText("C:\\Users\\Amrit\\Documents\\DB_RML_RDF.n3");
		textFieldRmlTarget.setText("C:\\Users\\Amrit\\Documents\\r2rml_mapping_1563282857376.n3");
	}
}
