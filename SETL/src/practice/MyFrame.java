package practice;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import helper.Methods;
import helper.Variables;

import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;

public class MyFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldCSVSource;
	private JTextField textFieldCSVPrefix;
	private JTextField textFieldCSVKeyAttribute;
	private JTextField textFieldCSVTarget;
	private JTextField textFieldXMLSource;
	private JTextField textFieldXMLTarget;
	private JTextField textFieldExcelSource;
	private JTextField textFieldExcelPrefix;
	private JTextField textFieldExcelKeyAttribute;
	private JTextField textFieldExcelTarget;
	private JTextField textFieldJsonSource;
	private JTextField textFieldJsonTarget;
	private JTextField textFieldDbName;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textFieldDirectBaseIRI;
	private JTextField textFieldTargetPathDirect;
	private JTextField textFieldDbNameRml;
	private JTextField textFieldRmlUsername;
	private JTextField textFieldRmlPassword;
	private JTextField textFieldRmlFilePath;
	private JTextField textFieldRmlTarget;
	
	private String[] strings = new String[] { Variables.CSV, Variables.XML, Variables.EXCEL, Variables.JSON, Variables.DB };
	private String[] delimiters = new String[] { Variables.COMMA, Variables.SPACE, Variables.SEMICOLON, Variables.TAB, Variables.PIPE };
	private String[] keyAttributes = new String[] { Variables.EXISTING_ATTRIBUTE, Variables.EXPRESSION, Variables.INCREMENTAL };

	private String csvSource, csvPrefix, csvColumn, csvDelimiter, csvTarget, csvTargetType, csvKeyAttributeType;
	private String excelSource, excelPrefix, excelColumn, excelTarget, excelTargetType, excelKeyAttributeType;
	private String xmlSource, xmlTarget, xmlTargetType;
	private String jsonSource, jsonTarget, jsonTargetType;
	private String dbName, dbUsername, dbPassword, dbDirectBaseIRI, dbDirectTargetPath, dbRmlFilePath, dbRMLTargetPath, fileType, dbMappingType;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 0 1,grow");
		
		
	}

}
