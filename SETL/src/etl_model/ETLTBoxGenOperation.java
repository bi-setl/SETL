package etl_model;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import helper.DBConnection;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLTBoxGenOperation implements ETLOperation {

	private String dbName, dbUserName, dbPassword, baseIRI, tBoxFileSavingPath;
	private Methods methods;
	private DBConnection databaseOperations;

	public ETLTBoxGenOperation() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {

		JPanel panelTBoxGenInput = new JPanel();

		panelTBoxGenInput.setLayout(new MigLayout("", "[grow][][750]", "[][][10][][][][]"));

		JLabel lblBaseIri = new JLabel("Base IRI:");
		lblBaseIri.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblBaseIri, "cell 0 0,alignx right");

		JComboBox comboBoxBaseIRI = new JComboBox();
		comboBoxBaseIRI.setEditable(true);
		comboBoxBaseIRI.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(comboBoxBaseIRI, "cell 2 0,growx");

		LinkedHashSet baseIRISet = inputParamsMap.get(BASE_IRI);
		ArrayList<String> baseIRIList = new ArrayList<>(baseIRISet);
		comboBoxBaseIRI.setModel(new DefaultComboBoxModel<>(baseIRIList.toArray()));

		if (this.baseIRI != null) {
			comboBoxBaseIRI.setSelectedItem(this.baseIRI);
		}

		JLabel lblDbName = new JLabel("DB Name:");
		lblDbName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblDbName, "cell 0 1,alignx right");

		JComboBox comboBoxDBName = new JComboBox();
		comboBoxDBName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxDBName.setEditable(true);
		panelTBoxGenInput.add(comboBoxDBName, "cell 2 1,growx");

		LinkedHashSet dbNameSet = inputParamsMap.get(DB_NAME);
		ArrayList<String> dbNameList = new ArrayList<>(dbNameSet);
		comboBoxDBName.setModel(new DefaultComboBoxModel<>(dbNameList.toArray()));

		if (this.dbName != null) {
			comboBoxDBName.setSelectedItem(this.dbName);
		}

		JLabel lblDbUserName = new JLabel("DB User Name:");
		lblDbUserName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblDbUserName, "cell 0 2,alignx right");

		JComboBox comboBoxDBUserName = new JComboBox();
		comboBoxDBUserName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxDBUserName.setEditable(true);
		panelTBoxGenInput.add(comboBoxDBUserName, "cell 2 2,growx");

		LinkedHashSet dbUserNameSet = inputParamsMap.get(DB_USER_NAME);
		ArrayList<String> dbUserNameList = new ArrayList<>(dbUserNameSet);
		comboBoxDBUserName.setModel(new DefaultComboBoxModel<>(dbUserNameList.toArray()));

		if (this.dbUserName != null) {
			comboBoxDBUserName.setSelectedItem(this.dbUserName);
		}

		JLabel lblDbPassword = new JLabel("DB Password:");
		lblDbPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblDbPassword, "cell 0 3,alignx right");

		JComboBox comboBoxDBPassword = new JComboBox();
		comboBoxDBPassword.setEditable(true);
		comboBoxDBPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(comboBoxDBPassword, "cell 2 3,growx");

		LinkedHashSet<String> dbPasswordSet = inputParamsMap.get(DB_USER_PASSWORD);
		ArrayList<String> dbPasswordList = new ArrayList<>(dbPasswordSet);
		comboBoxDBPassword.setModel(new DefaultComboBoxModel<>(dbPasswordList.toArray()));

		if (this.dbPassword != null) {
			comboBoxDBPassword.setSelectedItem(this.dbPassword);
		}

		JLabel lblOutputFilePath = new JLabel("TBox File:");
		lblOutputFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblOutputFilePath, "cell 0 5,alignx right");

		JLabel lblTBoxFilePath = new JLabel("None");
		lblTBoxFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelTBoxGenInput.add(lblTBoxFilePath, "cell 2 5,alignx left, growx");

		if (this.tBoxFileSavingPath != null)
			lblTBoxFilePath.setText(this.tBoxFileSavingPath);

		JButton btnNew = new JButton("Open");
		btnNew.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelTBoxGenInput.add(btnNew, "cell 2 5");
		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String defaultName = "";
				if (comboBoxDBName.getSelectedItem() != null) {
					defaultName += comboBoxDBName.getSelectedItem().toString();
				}
				defaultName += "_" + methods.getDateTime();
				defaultName += "_TBox.n3";

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save TBox file");

				if (!filePath.equals("")) {

					if (!filePath.endsWith(".n3")) {
						filePath += ".n3";
					}
					lblTBoxFilePath.setText(filePath);
				}

			}
		});

		int confirmation = JOptionPane.showConfirmDialog(null, panelTBoxGenInput,
				"Please Input Values for TBox Generation.", JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			String dbName = comboBoxDBName.getSelectedItem().toString();
			String dbUserName = comboBoxDBUserName.getSelectedItem().toString();
			String dbPassword = comboBoxDBPassword.getSelectedItem().toString();
			String baseIRI = comboBoxBaseIRI.getSelectedItem().toString();
			String filePath = lblTBoxFilePath.getText().toString();

			ArrayList<String> stringList = new ArrayList<>();
			stringList.add(dbName);
			stringList.add(dbUserName);
			stringList.add(dbPassword);
			stringList.add(baseIRI);
			stringList.add(filePath);

			boolean hasEmpty = methods.hasEmptyString(stringList);
			// System.out.println("Has Empty " + hasEmpty);
			if (!hasEmpty) {

				this.setDbName(dbName);
				this.setDbUserName(dbUserName);
				this.setDbPassword(dbPassword);
				this.setBaseIRI(baseIRI);
				this.setFileSavingPath(filePath);

				if (filePath.equals("None")) {
					methods.showDialog("Please select path for saving TBox file.");
					return false;

				}

				inputParamsMap.get(DB_NAME).add(dbName);
				inputParamsMap.get(DB_USER_NAME).add(dbUserName);
				inputParamsMap.get(DB_USER_PASSWORD).add(dbPassword);
				inputParamsMap.get(BASE_IRI).add(baseIRI);
				inputParamsMap.get(TBox_FILE).add(filePath);

				return true;

			} else {
				methods.showDialog("Please provide value to all input.");
				return false;
			}

		}

		return false;
	}

	@Override
	public boolean execute(JTextPane statusPane) {

		Connection dbConnection = getDBConnection(dbName, dbUserName, dbPassword);
		if (dbConnection == null) {
			statusPane.setText(statusPane.getText() + "\nTBox Generation Failed! Database connection not extablished");
			return false;
		} else {

			// Initialize the connection variables
			/*MainFrame.dbURL = dbName;
			MainFrame.dbUserName = dbUserName;
			MainFrame.dbPassword = dbPassword;

			databaseOperations = new DBConnection(dbName, dbUserName, dbPassword);

			// getAll Table Structure
			ArrayList<DBTable> dbTableStructures = databaseOperations.getAllDBTableStructure();

			TBoxMethods tBoxMethod = new TBoxMethods();
			ArrayList<TableAnnotations> tBox = tBoxMethod.getDefaultTBox(dbTableStructures, this.baseIRI);

			String tBoxString = "@prefix base: <" + baseIRI + ">.\n";
			tBoxString += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n";
			tBoxString += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n";
			tBoxString += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n";
			tBoxString += "@prefix owl: <http://www.w3.org/2002/07/owl#>.\n";

			for (TableAnnotations tableAnnotations : tBox) {

				HashMap<String, ArrayList<String>> conceptMap, propertyMap;

				conceptMap = tableAnnotations.getConceptAnnotations();
				propertyMap = tableAnnotations.getPropertyAnnotations();

				for (Map.Entry<String, ArrayList<String>> mapEntry : conceptMap.entrySet()) {

					ArrayList<String> annotations = mapEntry.getValue();
					for (String ann : annotations) {
						tBoxString += ann + "\n";
					}
				}

				for (Map.Entry<String, ArrayList<String>> mapEntry : propertyMap.entrySet()) {
					ArrayList<String> annotations = mapEntry.getValue();

					for (String ann : annotations) {
						tBoxString += ann + "\n";
					}
				}
			}*/
			
			// For demo --- Amrit
			String tBoxString = "";

			boolean flag = methods.writeText(tBoxFileSavingPath, tBoxString);
			if (flag) {

				statusPane.setText(statusPane.getText().toString() + "\nTBox Saved: " + tBoxFileSavingPath);
				return true;
			} else {

				statusPane.setText(statusPane.getText().toString() + "\nTBox Saving Failed.");
				return false;
			}

		}

	}

	private Connection getDBConnection(String dbName, String dbUserName, String dbUserPassword) {

		DBConnection databaseConnection = new DBConnection();
		Connection dbConnection = databaseConnection.getConnection(dbName, dbUserName, dbUserPassword);

		return dbConnection;

	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getBaseIRI() {
		return baseIRI;
	}

	public void setBaseIRI(String baseIRI) {
		this.baseIRI = baseIRI;
	}

	public String getFileSavingPath() {
		return tBoxFileSavingPath;
	}

	public void setFileSavingPath(String fileSavingPath) {
		this.tBoxFileSavingPath = fileSavingPath;
	}
}
