package view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import model.DBTable;
import net.miginfocom.swing.MigLayout;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelDBSummary extends JPanel {

	/**
	 * Create the panel.
	 */
	
	private ArrayList<DBTable> allDBTableStructures;
	private JPanel mainContainer;
	private CardLayout appLayout;
	private JPanel panelCenterContent;
	private String baseURL;
	private PanelRMLFile dbRMLFilePanel;
	
	final static String DB_RML_PANEL_KEY = "PanelRMLFile";
	
	public PanelDBSummary(JPanel panelMainContainer, ArrayList<DBTable> allTableStructures, String baseURL) {
		
		if(panelMainContainer == null){
			JOptionPane.showMessageDialog(null, "Main Containe Object is null");
		}
		this.allDBTableStructures = allTableStructures;
		this.mainContainer = panelMainContainer;
		appLayout = (CardLayout)mainContainer.getLayout();
		this.baseURL = baseURL;
		
	
		setLayout(new BorderLayout(0, 0));
		JLabel lblDbPropertiesSummery = new JLabel("DB Properties Summery");
		lblDbPropertiesSummery.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblDbPropertiesSummery.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblDbPropertiesSummery, BorderLayout.NORTH);
		
		JPanel Buttons = new JPanel();
		add(Buttons, BorderLayout.SOUTH);
		
		JButton buttonBack = new JButton("< Back");
		buttonBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		buttonBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				appLayout.show(mainContainer, SETLFrame.DB_PANEL_KEY);
			}
		});
		Buttons.add(buttonBack);
		
		JButton btnConfirm = new JButton("Confirm >");
		btnConfirm.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				dbRMLFilePanel = new PanelRMLFile(mainContainer, allTableStructures, baseURL);		
				mainContainer.add(dbRMLFilePanel, DB_RML_PANEL_KEY);
				appLayout.show(mainContainer, DB_RML_PANEL_KEY);	
				
			}
		});
		Buttons.add(btnConfirm);
		
		
		JScrollPane scrollPaneSummery = new JScrollPane();
		add(scrollPaneSummery, BorderLayout.CENTER);
		
		panelCenterContent = new JPanel();
		scrollPaneSummery.setViewportView(panelCenterContent);
		panelCenterContent.setLayout(new MigLayout("", "[20][][][]", "[30][][][][]"));
		

		JLabel lblColumnName = new JLabel("Base IRI: "+baseURL);
		lblColumnName.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelCenterContent.add(lblColumnName, "flowx,cell 1 0");
		
		int rowNum = 1, colNum = 1;
		
		for(DBTable tableStructure: allDBTableStructures){
			
			colNum = 1;
			JLabel lblNewLabel = new JLabel(tableStructure.getTableName()+" Table");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
			panelCenterContent.add(lblNewLabel, "flowx,cell "+colNum+" "+rowNum+" 3 1,alignx left,gapy 30 10");
			
			rowNum+=1;
			
			JLabel lblDbColumn = new JLabel("DB Column");
			lblDbColumn.setFont(new Font("Tahoma", Font.BOLD, 16));
			panelCenterContent.add(lblDbColumn, "cell "+colNum+" "+rowNum+",alignx left");
			
			colNum+=2;
			
			JLabel lblProperty_1 = new JLabel("Property");
			lblProperty_1.setFont(new Font("Tahoma", Font.BOLD, 16));
			panelCenterContent.add(lblProperty_1, "cell "+colNum+" "+rowNum+",alignx left");
			
			int index = 0;
			for(String dataColName: tableStructure.getDataColumns()){
				
				rowNum+=1;
				colNum = 1;
				
				String value  = tableStructure.getDataColumnValues().get(index);
				if(value.equals("L"))
					value = "Literal";
				
				
				JLabel lblEmpName = new JLabel(dataColName);
				lblEmpName.setFont(new Font("Tahoma", Font.PLAIN, 16));
				panelCenterContent.add(lblEmpName, "cell "+colNum+" "+rowNum);
				
				colNum +=2;
				
				JLabel lblLiteral = new JLabel(value);
				lblLiteral.setFont(new Font("Tahoma", Font.PLAIN, 16));
				panelCenterContent.add(lblLiteral, "cell "+colNum+" "+rowNum);
				
				index++;
			}
			rowNum+=1;
			
		}
		
	}

}
