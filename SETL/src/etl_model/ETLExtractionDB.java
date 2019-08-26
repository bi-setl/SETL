package etl_model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import helper.DBConnection;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLExtractionDB implements ETLOperation {

	Methods methods;
	String dbName, dbUser, dbPassword, dbQuery, filePath;
	private JTable tableDBContent;

	public ETLExtractionDB() {
		super();
		methods = new Methods();

	}

	@Override
	public boolean execute(JTextPane textPane) {
		boolean status = extractDBButtonHandler();

		return status;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		JPanel panelExtDBInput = new JPanel();

		panelExtDBInput.setLayout(new MigLayout("", "[][][grow]", "[][][][grow][][]"));
		panelExtDBInput.setPreferredSize(new Dimension(600, 500));

		JLabel lblDbName = new JLabel("DB Name");
		panelExtDBInput.add(lblDbName, "cell 0 0");

		JTextField DbName = new JTextField();
		panelExtDBInput.add(DbName, "cell 1 0,growx");
		DbName.setColumns(10);
		if (dbName != null) {
			DbName.setText(dbName);
		}

		JLabel lblDbUserName = new JLabel("DB User name");
		panelExtDBInput.add(lblDbUserName, "cell 0 1");

		JTextField DbUser = new JTextField();
		panelExtDBInput.add(DbUser, "cell 1 1,growx");
		DbUser.setColumns(10);

		if (dbUser != null) {
			DbUser.setText(dbUser);
		}

		JLabel lblDbPassword = new JLabel("DB Password");
		panelExtDBInput.add(lblDbPassword, "cell 0 2");

		JPasswordField DbPassword = new JPasswordField();
		panelExtDBInput.add(DbPassword, "cell 1 2,growx");

		if (dbPassword != null) {
			DbPassword.setText(dbPassword);
		}

		JLabel lblDbQuery = new JLabel("DB Query");
		panelExtDBInput.add(lblDbQuery, "cell 0 3,aligny top");

		JTextArea DbQuery = new JTextArea();
		DbQuery.setColumns(35);
		DbQuery.setRows(15);
		JScrollPane queryScrollPane = new JScrollPane(DbQuery);
		panelExtDBInput.add(queryScrollPane, "cell 1 3,aligny top");

		if (dbQuery != null) {
			DbQuery.setText(dbQuery);
		}

		JLabel lblFilePath = new JLabel("File Path");
		panelExtDBInput.add(lblFilePath, "cell 0 4");

		JLabel lblNone = new JLabel("None");
		panelExtDBInput.add(lblNone, "cell 1 4");

		if (filePath != null) {
			lblNone.setText(filePath);
		}

		JButton btnNew = new JButton("Save");
		panelExtDBInput.add(btnNew, "cell 2 4");
		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String defaultName = "extractiondb";
				/*
				 * if(comboBoxDBName.getSelectedItem()!=null){
				 * defaultName+=comboBoxDBName.getSelectedItem().toString(); }
				 */
				defaultName += "_" + methods.getDateTime();
				defaultName += "Data.csv";

				String filePath = methods.chooseSaveFile("", defaultName, "Select Directory to save TBox file");

				if (!filePath.equals("")) {
					if (!filePath.endsWith(".csv")) {
						filePath += ".csv";
					}
					lblNone.setText(filePath);
				}

			}
		});

		int option = JOptionPane.showConfirmDialog(null, panelExtDBInput, "Please provide all input",
				JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			dbName = DbName.getText().toString();
			dbUser = DbUser.getText().toString();
			dbPassword = DbPassword.getText().toString();
			dbQuery = DbQuery.getText().toString();
			filePath = lblNone.getText().toString();

			ArrayList<String> inputList = new ArrayList<String>();
			inputList.add(dbName);
			inputList.add(dbUser);
			inputList.add(dbPassword);
			inputList.add(dbQuery);

			if (!methods.hasEmptyString(inputList)) {
				if (filePath.equals("None")) {
					methods.showDialog("Please select the file path where the extracted data to be saved.");
					return false;
				}

				setDbName(dbName);
				setDbUser(dbUser);
				setDbPassword(dbPassword);
				setDbQuery(dbQuery);
				setFilePath(filePath);
				return true;

			}

		} else {
			methods.showDialog("Please Provide input for Extraction from DB");
			return false;
		}
		return false;
	}

	private boolean extractDBButtonHandler() {
		DBConnection databaseConnection = new DBConnection();
		Connection connection = databaseConnection.getConnection(dbName, dbUser, dbPassword);
		System.out.println(dbName + " " + dbUser + " " + dbPassword);

		if (connection == null) {
			methods.showDialog("Database connection not established.\nExtraction failed!");
		} else {
			/*MainFrame.dbURL = dbName;
			MainFrame.dbUserName = dbUser;
			MainFrame.dbPassword = dbPassword;*/
			
			// check this file --- Amrit
		}

		// DatabaseOperations dbOperations = new DatabaseOperations(dbName, dbUser, dbPassword);
		// dbOperations.importDataToCSV(dbQuery, filePath);

		return true;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbQuery() {
		return dbQuery;
	}

	public void setDbQuery(String dbQuery) {
		this.dbQuery = dbQuery;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
