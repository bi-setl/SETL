package etl_model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
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

import helper.DBConnection;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLMappingGenOperation implements ETLOperation {

	private String dbName, dbUserName, dbPassword, baseIRI, mgFilePath;
	//String fileName;
	private Methods methods;
	private DBConnection databaseOperations;

	public ETLMappingGenOperation(String dbName, String dbUserName, String dbPassword, String baseIRI,
			String fileSavingPath, String fileName) {
		super();
		this.dbName = dbName;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
		this.mgFilePath = fileSavingPath;
		this.baseIRI = baseIRI;
		methods = new Methods();
	}

	public ETLMappingGenOperation() {
		super();
		methods = new Methods();
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		
		
		JPanel panelMGInput = new JPanel();
		
		panelMGInput.setLayout(new MigLayout("", "[grow][][750]", "[][][10][][][][]"));
		
		JLabel lblBaseIri_1 = new JLabel("Base IRI:");
		lblBaseIri_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblBaseIri_1, "cell 0 0,alignx right");
		
		JComboBox comboBoxBaseIRI = new JComboBox();
		comboBoxBaseIRI.setEditable(true);
		comboBoxBaseIRI.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(comboBoxBaseIRI, "cell 2 0,growx");
		
		LinkedHashSet baseIRISet = inputParamsMap.get(BASE_IRI);
		ArrayList<String> baseIRIList = new ArrayList<>(baseIRISet);
		comboBoxBaseIRI.setModel(new DefaultComboBoxModel<>(baseIRIList.toArray()));
		
		if (this.baseIRI != null) {
			comboBoxBaseIRI.setSelectedItem(this.baseIRI);
		}
		
		JLabel lblDbName_1 = new JLabel("DB Name:");
		lblDbName_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblDbName_1, "cell 0 1,alignx right");
		
		JComboBox comboBoxDBName = new JComboBox();
		comboBoxDBName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxDBName.setEditable(true);
		panelMGInput.add(comboBoxDBName, "cell 2 1,growx");
		
		LinkedHashSet dbNameSet = inputParamsMap.get(DB_NAME);
		ArrayList<String> dbNameList = new ArrayList<>(dbNameSet);
		comboBoxDBName.setModel(new DefaultComboBoxModel<>(dbNameList.toArray()));
		
		if (this.dbName!= null) {
			comboBoxDBName.setSelectedItem(this.dbName);
		}
		
		
		JLabel lblDbUserName_1 = new JLabel("DB User Name:");
		lblDbUserName_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblDbUserName_1, "cell 0 2,alignx right");
		
		JComboBox comboBoxDBUserName = new JComboBox();
		comboBoxDBUserName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxDBUserName.setEditable(true);
		panelMGInput.add(comboBoxDBUserName, "cell 2 2,growx");
		
		LinkedHashSet dbUserNameSet = inputParamsMap.get(DB_USER_NAME);
		ArrayList<String> dbUserNameList = new ArrayList<>(dbUserNameSet);
		comboBoxDBUserName.setModel(new DefaultComboBoxModel<>(dbUserNameList.toArray()));
		
		if(this.dbUserName != null){
			comboBoxDBUserName.setSelectedItem(this.dbUserName);
		}
		
		
		JLabel lblDbPassword_1 = new JLabel("DB Password:");
		lblDbPassword_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblDbPassword_1, "cell 0 3,alignx right");
		
		
		JComboBox comboBoxDBPassword = new JComboBox();
		comboBoxDBPassword.setEditable(true);
		comboBoxDBPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(comboBoxDBPassword, "cell 2 3,growx");
		
		LinkedHashSet<String> dbPasswordSet = inputParamsMap.get(DB_USER_PASSWORD);
		ArrayList<String> dbPasswordList = new ArrayList<>(dbPasswordSet);
		comboBoxDBPassword.setModel(new DefaultComboBoxModel<>(dbPasswordList.toArray()));
		
		if(this.dbPassword != null){
			comboBoxDBPassword.setSelectedItem(this.dbPassword);
		}
		
		JLabel lblOutputFilePath = new JLabel("Mapping Graph File:");
		lblOutputFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblOutputFilePath, "cell 0 5,alignx right");
		
		JLabel lblMGFilePath = new JLabel("None");
		lblMGFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblMGFilePath, "cell 2 5,alignx left, growx");
		
		if(this.mgFilePath != null)
			lblMGFilePath.setText(this.mgFilePath);
		
		
		JButton btnNew = new JButton("Open");
		btnNew.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMGInput.add(btnNew, "cell 2 5");
		btnNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String defaultName = "";
				if(comboBoxDBName.getSelectedItem()!=null){
					defaultName+=comboBoxDBName.getSelectedItem().toString();
				}
				defaultName+="_"+methods.getDateTime();
				defaultName+="_mapping_graph.ttl";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save mapping Graph file");

				if (!filePath.equals("")) {
					
					if (!filePath.endsWith(".ttl")) {
						filePath+=".ttl";
					}
					lblMGFilePath.setText(filePath);
				}	
			}
		});
		
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelMGInput, "Please Input Values for Mapping Generation.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			String dbName = comboBoxDBName.getSelectedItem().toString();
			String dbUserName = comboBoxDBUserName.getSelectedItem().toString();
			String dbPassword = comboBoxDBPassword.getSelectedItem().toString();
			String baseIRI = comboBoxBaseIRI.getSelectedItem().toString();
			String filePath = lblMGFilePath.getText().toString();
			
			ArrayList<String> stringList = new ArrayList<>();
			stringList.add(dbName);
			stringList.add(dbUserName);
			stringList.add(dbPassword);
			stringList.add(baseIRI);
			stringList.add(filePath);
			
			boolean hasEmpty = methods.hasEmptyString(stringList);
		//	System.out.println("Has Empty "+hasEmpty);
			if(!hasEmpty){
				
				this.setDbName(dbName);
				this.setDbUserName(dbUserName);
				this.setDbPassword(dbPassword);
				this.setBaseIRI(baseIRI);
				this.setFileSavingPath(filePath);
				
				if (filePath.equals("None")) {

					methods.showDialog("Please select path for saving mapping graph file.");
					return false;

				}
				
				inputParamsMap.get(DB_NAME).add(dbName);
				inputParamsMap.get(DB_USER_NAME).add(dbUserName);
				inputParamsMap.get(DB_USER_PASSWORD).add(dbPassword);
				inputParamsMap.get(BASE_IRI).add(baseIRI);
				inputParamsMap.get(MAPPING_GRAPH_FILE).add(filePath);
				//inputParamsMap.get()
	
				return true;
			
			}else{
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
			statusPane.setText(statusPane.getText()+"\nMapping Generation Failed! Database connection not extablished");
			return false; 
		} else {

			// Initialize the connection variables
			/*MainFrame.dbURL = dbName;
			MainFrame.dbUserName = dbUserName;
			MainFrame.dbPassword = dbPassword;
			
			databaseOperations = new DatabaseOperations(dbName, dbUserName, dbPassword);
			
			//getAll Table Structure
			ArrayList<String> rmlMappingFileList = new ArrayList<>();
			ArrayList<DBTable> dbTableStructures = databaseOperations.getAllDBTableStructure();		
			
			//getRMLFile
			RMLFile rmlFile = new RMLFile();
			ArrayList<TripleMap> tripleMaps = rmlFile.getTripleMapsTC(dbTableStructures, baseIRI);
			String rmlFileString = rmlFile.getRMLFile(tripleMaps, baseIRI);
			
			boolean flag = methods.writeText(mgFilePath, rmlFileString);
			if(flag){
				statusPane.setText(statusPane.getText().toString()+"\nR2RML Mapping Graph Saved: "+ mgFilePath);
				return true;
			}
			else{
				
				statusPane.setText(statusPane.getText().toString()+"\nR2RML Mapping Graph Saving Failed.");
				return false;
			}*/
			return false;
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
		return mgFilePath;
	}

	public void setFileSavingPath(String fileSavingPath) {
		this.mgFilePath = fileSavingPath;
	}

}
