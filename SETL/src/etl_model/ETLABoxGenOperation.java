package etl_model;

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

//Concrete class for ABox Gen operation
public class ETLABoxGenOperation implements ETLOperation{

	private String dbName, dbUserName, dbPassword, aBoxFileSavingPath, mgFilePath;

	private Methods methods;
	private DBConnection databaseOperations;
	
	public ETLABoxGenOperation() {
		super();
		methods = new Methods();
		
	}
	
	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		
		
		JPanel panelMGInput = new JPanel();
		panelMGInput.setLayout(new MigLayout("", "[grow][][750]", "[][][10][][][][]"));
		
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
		
		JLabel lblMGFilePath = new JLabel("Mapping Graph File:");
		lblMGFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblMGFilePath, "cell 0 5,alignx right");
		
		JComboBox comboBoxMGFilePath = new JComboBox();
		comboBoxMGFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(comboBoxMGFilePath, "flowx,cell 2 5,growx");
		
		LinkedHashSet mappingGraphFileHashSet = inputParamsMap.get(MAPPING_GRAPH_FILE);
		ArrayList<String> mappingGraphFileList = new ArrayList<>(mappingGraphFileHashSet);
		comboBoxMGFilePath.setModel(new DefaultComboBoxModel<>(mappingGraphFileList.toArray()));

		if (this.mgFilePath!=null) {
			comboBoxMGFilePath.setSelectedItem(this.mgFilePath);
		}
		
		JButton btnNewMGFile = new JButton("Open");
		btnNewMGFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMGInput.add(btnNewMGFile, "cell 2 5");
		btnNewMGFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String filePath = methods.chooseFile("Select R2RML Mapping File");
				if(!filePath.equals("")){
					
					DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBoxMGFilePath.getModel();
					comboBoxModel.addElement(filePath);
					comboBoxMGFilePath.setModel(comboBoxModel);
					comboBoxMGFilePath.setSelectedItem(filePath);
				}
				
			}
		});

		
		JLabel lblOutputFilePath = new JLabel("ABox File Path:");
		lblOutputFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblOutputFilePath, "cell 0 6,alignx right");
		
		JLabel lblABoxFilePath = new JLabel("None");
		lblABoxFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelMGInput.add(lblABoxFilePath, "cell 2 6,alignx left, growx");
		
		if(this.aBoxFileSavingPath != null)
			lblABoxFilePath.setText(this.aBoxFileSavingPath);
		
		JButton btnNewOutputDir = new JButton("Open");
		btnNewOutputDir.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelMGInput.add(btnNewOutputDir, "cell 2 6");
		btnNewOutputDir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String defaultName = "";
				if(comboBoxDBName.getSelectedItem()!=null){
					defaultName+=comboBoxDBName.getSelectedItem().toString();
				}
				defaultName+="_"+methods.getDateTime();
				defaultName+="_ABox.n3";

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save ABox file");

				if (!filePath.equals("")) {
					
					if (!filePath.endsWith(".n3")) {
						filePath+=".n3";
					}
					lblABoxFilePath.setText(filePath);
				}	
				
			}
		});
		
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelMGInput, "Please Input Values for ABox Generation.",
				JOptionPane.OK_CANCEL_OPTION);

		if (confirmation == JOptionPane.OK_OPTION) {

			String dbName = comboBoxDBName.getSelectedItem().toString();
			String dbUserName = comboBoxDBUserName.getSelectedItem().toString();
			String dbPassword = comboBoxDBPassword.getSelectedItem().toString();
			String aBoxFilePath = lblABoxFilePath.getText().toString();
			String mappingGraphPath = comboBoxMGFilePath.getSelectedItem().toString();
			
			ArrayList<String> stringList = new ArrayList<>();
			stringList.add(dbName);
			stringList.add(dbUserName);
			stringList.add(dbPassword);
			stringList.add(aBoxFilePath);
			stringList.add(mappingGraphPath);
			
			boolean hasEmpty = methods.hasEmptyString(stringList);
			if(!hasEmpty){
				
				this.setDbName(dbName);
				this.setDbUserName(dbUserName);
				this.setDbPassword(dbPassword);
				this.setFileSavingPath(aBoxFilePath);
				this.setMgFilePath(mappingGraphPath);
				
				if (aBoxFilePath.equals("None")) {
					methods.showDialog("Please select path for saving ABox file.");
					return false;

				}
				
				inputParamsMap.get(DB_NAME).add(dbName);
				inputParamsMap.get(DB_USER_NAME).add(dbUserName);
				inputParamsMap.get(DB_USER_PASSWORD).add(dbPassword);
				inputParamsMap.get(RDF_FILE).add(aBoxFilePath);
				
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
			statusPane.setText(statusPane.getText()+"\nABox Generation Failed! Database connection not extablished");
			return false; 
		} else {

			// Initialize the connection variables
			/*MainFrame.dbURL = dbName;
			MainFrame.dbUserName = dbUserName;
			MainFrame.dbPassword = dbPassword;
			
			databaseOperations = new DatabaseOperations(dbName, dbUserName, dbPassword);
			String outFilePath = aBoxFileSavingPath;
			
			RMLProcessor rmlProcessor = new RMLProcessor(dbName, dbUserName, dbPassword);
			ArrayList<String> lines = rmlProcessor.readRML(this.mgFilePath);
			
			ArrayList<String> rdfTriples = rmlProcessor.getRDFTriples(lines);
			
			if(rdfTriples.size()<=0){
				statusPane.setText(statusPane.getText().toString()+"\nABox Saving Failed. Misformated Mapping Graph.");
				return false;

			}
			String rdfString = "";
			for(String rdfTriplesString: rdfTriples){
				rdfString+=rdfTriplesString+"\n";							
			}
			boolean flag = methods.writeFile(outFilePath, rdfString);
			if(flag){
			
				statusPane.setText(statusPane.getText().toString()+"\nABox Saved: "+ outFilePath);
				return true;
			}
			else{
				
				statusPane.setText(statusPane.getText().toString()+"\nABox Saving Failed.");
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

	public String getFileSavingPath() {
		return aBoxFileSavingPath;
	}

	public void setFileSavingPath(String fileSavingPath) {
		this.aBoxFileSavingPath = fileSavingPath;
	}
	
	public String getMgFilePath() {
		return mgFilePath;
	}

	public void setMgFilePath(String mgFilePath) {
		this.mgFilePath = mgFilePath;
	}

}
