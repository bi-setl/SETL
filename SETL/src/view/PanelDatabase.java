package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import controller.DBPanelController;

import model.DBTable;

import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import net.miginfocom.swing.MigLayout;

public class PanelDatabase extends JPanel {

	private JTextField textFieldDBURL;
	private JTextField textFieldDBUserName;
	private JPasswordField passwordFieldDBPassword;
	private JTable tableDBTables;

	private JList<String> listDBTables;

	private ArrayList<String> tableNames;
	private ArrayList<String> tableStatus;

	private CardLayout appLayout;
	private JPanel mainContainer;
	private PanelDBSummary panelDBSummary;

	final static String DB_SUMMERY_PANEL_KEY = "DBPanelSummer";

	private DBPanelController dbPanelController;
	private JTextField textFieldBaseURL;

	private JPanel panelCenterContent;
	private JButton btnOk;
	private ArrayList<DBTable> allDBTableStructures;

	private ArrayList<JTextField> attributeValues;

	private int selectedTableIndex = -1;
	private ArrayList<Boolean> propertyType;
	private String baseURL = "";

	/**
	 * Create the panel.
	 */
	public PanelDatabase(JPanel panelMainContainer) {

		this.mainContainer = panelMainContainer;
		appLayout = (CardLayout) panelMainContainer.getLayout();
		//
		
		allDBTableStructures = new ArrayList<>();

		setLayout(new BorderLayout(0, 5));

		JPanel panelDBButtons = new JPanel();
		add(panelDBButtons, BorderLayout.SOUTH);

		JButton btnDBConnect = new JButton("Connect");
		btnDBConnect.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnDBConnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				String url = textFieldDBURL.getText().toString();
				String userName = textFieldDBUserName.getText().toString();
				String password = passwordFieldDBPassword.getText().toString();

				SETLFrame.dbURL = url;
				SETLFrame.dbUserName = userName;
				SETLFrame.dbPassword = password;

				tableNames = dbPanelController.connectButtonHandler();

				
				if (tableNames != null) {
					loadTableList();
					panelCenterContent.removeAll();
					panelCenterContent.updateUI();
					allDBTableStructures = dbPanelController.getAllDBTableStructures();
				}else{
					JOptionPane.showMessageDialog(null, "Table Names not Found!");
				}

			}
		});
		panelDBButtons.add(btnDBConnect);

		JButton btnConvertDB = new JButton("Proceed >");
		btnConvertDB.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnConvertDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				
				if(allDBTableStructures == null){
					
					JOptionPane.showMessageDialog(null, "Please Connect a database");
					
				}else{
					baseURL = textFieldBaseURL.getText().toString();
					panelDBSummary = new PanelDBSummary(mainContainer, allDBTableStructures, baseURL);
					mainContainer.add(panelDBSummary, DB_SUMMERY_PANEL_KEY);
					appLayout.show(mainContainer, DB_SUMMERY_PANEL_KEY);
					
//					for (DBTable table : allDBTableStructures) {
//
//						table.printTableConfigurations();
//					}
				}

			}
		});
		panelDBButtons.add(btnConvertDB);

		JPanel panelDBCenter = new JPanel();
		panelDBCenter.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		add(panelDBCenter, BorderLayout.CENTER);
		panelDBCenter.setLayout(new BorderLayout(0, 0));

		btnOk = new JButton("Save Changes");
		btnOk.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnOk.setEnabled(false);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				okButtonHandler();
			}

		});
		panelDBCenter.add(btnOk, BorderLayout.SOUTH);
		
		JPanel panelOne = new JPanel();
		panelOne.setBackground(Color.WHITE);
		panelDBCenter.add(panelOne, BorderLayout.CENTER);
		panelOne.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		panelOne.add(scrollPane, "cell 0 0,grow");
		
		panelCenterContent = new JPanel();
		panelCenterContent.setBackground(Color.WHITE);
		scrollPane.setViewportView(panelCenterContent);
		panelCenterContent.setLayout(new MigLayout("", "[][][grow]", "[]"));

		JPanel panelDBWest = new JPanel();
		panelDBWest.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		add(panelDBWest, BorderLayout.WEST);
		panelDBWest.setLayout(new BorderLayout(0, 0));

		JLabel lblTablesInThe = new JLabel("Tables in the Database");
		lblTablesInThe.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelDBWest.add(lblTablesInThe, BorderLayout.NORTH);

		listDBTables = new JList<String>();
		listDBTables.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelDBWest.add(listDBTables, BorderLayout.CENTER);
		listDBTables.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// JOptionPane.showMessageDialog(null, "selected "+
				// listDBTables.getSelectedValue());
				// tableDBTables.setModel(dbPanelController.getDBTableTableModel(listDBTables.getSelectedValue().toString()));
				if (textFieldBaseURL.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please provide base url");

				} else {
					baseURL = textFieldBaseURL.getText().toString();
					btnOk.setEnabled(true);
					//JOptionPane.showMessageDialog(null, "Selected Value: " + listDBTables.getSelectedValue());
					//String tName = listDBTables.getSelectedValue().toString();
					if (listDBTables.getSelectedValue() != null)
						loadDataColumns(listDBTables.getSelectedValue().toString());
				}

			}
		});

		JPanel panelDBParamsNorth = new JPanel();
		add(panelDBParamsNorth, BorderLayout.NORTH);
		GridBagLayout gbl_panelDBParamsNorth = new GridBagLayout();
		gbl_panelDBParamsNorth.columnWidths = new int[] { 0, 0 };
		gbl_panelDBParamsNorth.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelDBParamsNorth.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		gbl_panelDBParamsNorth.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MAX_VALUE };
		panelDBParamsNorth.setLayout(gbl_panelDBParamsNorth);

		JLabel lblDbUrl = new JLabel("DB Name:");
		lblDbUrl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDbUrl.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblDbUrl = new GridBagConstraints();
		gbc_lblDbUrl.anchor = GridBagConstraints.EAST;
		gbc_lblDbUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbUrl.gridx = 0;
		gbc_lblDbUrl.gridy = 0;
		panelDBParamsNorth.add(lblDbUrl, gbc_lblDbUrl);

		textFieldDBURL = new JTextField();
		textFieldDBURL.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textFieldDBURL.setColumns(20);
		textFieldDBURL.setToolTipText("DB Name");
		GridBagConstraints gbc_textFieldDBURL = new GridBagConstraints();
		gbc_textFieldDBURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDBURL.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDBURL.gridx = 1;
		gbc_textFieldDBURL.gridy = 0;
		panelDBParamsNorth.add(textFieldDBURL, gbc_textFieldDBURL);

		JLabel lblDbName = new JLabel("DB User Name:");
		lblDbName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblDbName = new GridBagConstraints();
		gbc_lblDbName.anchor = GridBagConstraints.EAST;
		gbc_lblDbName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbName.gridx = 2;
		gbc_lblDbName.gridy = 0;
		panelDBParamsNorth.add(lblDbName, gbc_lblDbName);

		textFieldDBUserName = new JTextField();
		textFieldDBUserName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textFieldDBUserName.setColumns(20);
		textFieldDBUserName.setToolTipText("DB User Name");
		GridBagConstraints gbc_textFieldDBUserName = new GridBagConstraints();
		gbc_textFieldDBUserName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDBUserName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDBUserName.gridx = 3;
		gbc_textFieldDBUserName.gridy = 0;
		panelDBParamsNorth.add(textFieldDBUserName, gbc_textFieldDBUserName);

		JLabel lblDbPassword = new JLabel("DB User Password:");
		lblDbPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblDbPassword = new GridBagConstraints();
		gbc_lblDbPassword.anchor = GridBagConstraints.EAST;
		gbc_lblDbPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbPassword.gridx = 4;
		gbc_lblDbPassword.gridy = 0;
		panelDBParamsNorth.add(lblDbPassword, gbc_lblDbPassword);

		passwordFieldDBPassword = new JPasswordField();
		passwordFieldDBPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordFieldDBPassword.setColumns(20);
		passwordFieldDBPassword.setToolTipText("DB User Password");
		GridBagConstraints gbc_passwordFieldDBPassword = new GridBagConstraints();
		gbc_passwordFieldDBPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordFieldDBPassword.insets = new Insets(0, 0, 5, 10);
		gbc_passwordFieldDBPassword.gridx = 5;
		gbc_passwordFieldDBPassword.gridy = 0;
		panelDBParamsNorth.add(passwordFieldDBPassword, gbc_passwordFieldDBPassword);

		JLabel lblBaseUrl = new JLabel("Base IRI: ");
		lblBaseUrl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblBaseUrl = new GridBagConstraints();
		gbc_lblBaseUrl.anchor = GridBagConstraints.EAST;
		gbc_lblBaseUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblBaseUrl.gridx = 0;
		gbc_lblBaseUrl.gridy = 2;
		panelDBParamsNorth.add(lblBaseUrl, gbc_lblBaseUrl);

		textFieldBaseURL = new JTextField();
		textFieldBaseURL.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_textFieldBaseURL = new GridBagConstraints();
		gbc_textFieldBaseURL.gridwidth = 3;
		gbc_textFieldBaseURL.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldBaseURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldBaseURL.gridx = 1;
		gbc_textFieldBaseURL.gridy = 2;
		panelDBParamsNorth.add(textFieldBaseURL, gbc_textFieldBaseURL);
		textFieldBaseURL.setColumns(10);

		JButton buttonWhatIsBaseURL = new JButton("?");
		buttonWhatIsBaseURL.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonWhatIsBaseURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JOptionPane.showMessageDialog(null, "Base IRI is used to dereference Relative IRIs.");
			}
		});
		GridBagConstraints gbc_buttonWhatIsBaseURL = new GridBagConstraints();
		gbc_buttonWhatIsBaseURL.anchor = GridBagConstraints.WEST;
		gbc_buttonWhatIsBaseURL.insets = new Insets(0, 0, 5, 5);
		gbc_buttonWhatIsBaseURL.gridx = 4;
		gbc_buttonWhatIsBaseURL.gridy = 2;
		panelDBParamsNorth.add(buttonWhatIsBaseURL, gbc_buttonWhatIsBaseURL);

		dbPanelController = new DBPanelController();
		attributeValues = new ArrayList<>();

	}

	private void loadTableList() {

		DefaultListModel<String> tableListModel = new DefaultListModel<>();
		
		for (String tableName : tableNames) {
			tableListModel.addElement(tableName);
		}
		listDBTables.setModel(tableListModel);

	}
	
	private void okButtonHandler() {

		if (textFieldBaseURL.getText().equals("")) {

			JOptionPane.showMessageDialog(null, "Please priovide a base URL");
			return;
		} else {
			baseURL = textFieldBaseURL.getText().toString();
		}

		String tableName = listDBTables.getSelectedValue().toString();
		DBTable temp = getDBTableObject(tableName);
		temp.getDataColumnsIsURL().clear();

		for (Boolean b : propertyType) {
			temp.getDataColumnsIsURL().add(b);
		}

		String s = "";
		for (Boolean dummy : temp.getDataColumnsIsURL()) {
			s += dummy + "\n";
		}

		// saving the column values
		int numOfDataColumns = temp.getDataColumnsIsURL().size();
		for (int i = 0; i < numOfDataColumns; i++) {

			if ((propertyType.get(i))) {

				String url = attributeValues.get(i).getText();

				if (url.equals("")) {
					JOptionPane.showMessageDialog(null, "Please provide value of all URIs");
					return;
				}
			}

		}

		ArrayList<String> dataCols = temp.getDataColumnValues();
		int size = temp.getDataColumns().size();

		for (int loopIndex = 0; loopIndex < size; loopIndex++) {

			if (propertyType.get(loopIndex)) {
				
				dataCols.set(loopIndex, attributeValues.get(loopIndex).getText().toString());

			} else {
				dataCols.set(loopIndex, "L");
			}
		}

		int numOfCols = propertyType.size();
		for (int j = 0; j < numOfCols; j++) {
			s += dataCols.get(j) + "\n";

		}

		//JOptionPane.showMessageDialog(null, listDBTables.getSelectedValue().toString() + "\n" + s);

		int i = listDBTables.getSelectedIndex();
		// tableStatus.remove(i);
		// tableStatus.add(i, "OK");
		//tableStatus.set(i, "OK");
		//loadTableStatusList();

		panelCenterContent.removeAll();
		panelCenterContent.updateUI();

		btnOk.setEnabled(false);

	}

	private DBTable getDBTableObject(String tableName) {

		for (DBTable temp : allDBTableStructures) {
			if (temp.getTableName().equals(tableName)) {

				return temp;
			}
		}
		return null;
	}

	private void loadDataColumns(String tableName) {

		panelCenterContent.removeAll();
		panelCenterContent.updateUI();
		
		JLabel lblDbColumn = new JLabel("DB Column");
		lblDbColumn.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelCenterContent.add(lblDbColumn, "cell 0 0,alignx center");
		
		JLabel lblProperty = new JLabel("Property");
		lblDbColumn.setBorder(new EmptyBorder(0, 40, 0, 40));
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelCenterContent.add(lblProperty, "cell 1 0,alignx center");
		
		JLabel lblIri = new JLabel("IRI");
		lblDbColumn.setBorder(new EmptyBorder(0, 40, 0, 40));
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelCenterContent.add(lblIri, "cell 2 0,alignx center");

		attributeValues = new ArrayList<>();
		propertyType = new ArrayList<>();

		DBTable dbTable = getDBTableObject(tableName);

		// initilizing the property values
		for (Boolean type : dbTable.getDataColumnsIsURL()) {
			propertyType.add(type);
		}

		selectedTableIndex = allDBTableStructures.indexOf(dbTable);

		int i = 0;
		for (String columnName : dbTable.getDataColumns()) {

			final int index = i;
			
			JPanel panel11 = new JPanel();
			panel11.setBackground(Color.WHITE);
			// panel11.setBorder(new EmptyBorder(0, 0, 0, 20));
			panelCenterContent.add(panel11, "cell 0 "+ (i + 1) +",grow");
			panel11.setLayout(new BorderLayout(0, 0));
			
			JLabel lblColumnHeader = new JLabel(columnName);
			lblColumnHeader.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblColumnHeader.setHorizontalAlignment(SwingConstants.CENTER);
			panel11.add(lblColumnHeader, BorderLayout.CENTER);
			
			JPanel panel12 = new JPanel();
			panel12.setBackground(Color.WHITE);
			panel12.setBorder(new EmptyBorder(0, 20, 0, 20));
			panelCenterContent.add(panel12, "cell 1 "+ (i + 1) +",grow");
			
			JRadioButton rdbtnLiteral = new JRadioButton("Literal");
			rdbtnLiteral.setBackground(Color.WHITE);
			rdbtnLiteral.setFont(new Font("Tahoma", Font.PLAIN, 16));
			panel12.add(rdbtnLiteral);
			
			JRadioButton rdbtnInternalURL = new JRadioButton("Internal IRI");
			rdbtnInternalURL.setBackground(Color.WHITE);
			rdbtnInternalURL.setFont(new Font("Tahoma", Font.PLAIN, 16));
			panel12.add(rdbtnInternalURL);
			
			JRadioButton rdbtnExternalURL = new JRadioButton("External IRI");
			rdbtnExternalURL.setBackground(Color.WHITE);
			rdbtnExternalURL.setFont(new Font("Tahoma", Font.PLAIN, 16));
			panel12.add(rdbtnExternalURL);
			
			JPanel panel13 = new JPanel();
			panel13.setBackground(Color.WHITE);
			panel13.setBorder(new EmptyBorder(0, 20, 0, 0));
			panelCenterContent.add(panel13, "cell 2 "+ (i + 1) +",grow");
			panel13.setLayout(new BorderLayout(0, 0));
			
			JTextField textField = new JTextField();
			textField.setFont(new Font("Tahoma", Font.PLAIN, 16));
			panel13.add(textField, BorderLayout.CENTER);
			textField.setColumns(10);

			ButtonGroup radioButtonGroup = new ButtonGroup();
			radioButtonGroup.add(rdbtnLiteral);
			radioButtonGroup.add(rdbtnInternalURL);
			radioButtonGroup.add(rdbtnExternalURL);

			attributeValues.add(textField);

			rdbtnInternalURL.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					textField.setEnabled(true);
					textField.setText(textFieldBaseURL.getText().toString());
					propertyType.set(index, true);

				}
			});

			rdbtnExternalURL.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					textField.setEnabled(true);
					textField.setText("");
					propertyType.set(index, true);
				}
			});

			rdbtnLiteral.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					textField.setEnabled(false);
					textField.setText("Literal");
					// propertyType.add(index, false);
					// propertyType.remove(index + 1);

					propertyType.set(index, false);

				}
			});

			ArrayList<String> columProperties = dbTable.getDataColumnValues();

			if (propertyType.get(i)) {

				if (dbTable.getDataColumnValues().get(i).equals(baseURL)) {
					rdbtnInternalURL.setSelected(true);
					textField.setText(dbTable.getDataColumnValues().get(i));

					// System.out.println("IsURL: True" );
				} else {
					rdbtnExternalURL.setSelected(true);
					textField.setText(dbTable.getDataColumnValues().get(i));

					// System.out.println("IsURL: True" );
				}
			} else {
				// System.out.println("exec");
				rdbtnLiteral.setSelected(true);
				textField.setText("Literal");
				textField.setEnabled(false);
			}
			// }

			i++;

		}

	}
}
