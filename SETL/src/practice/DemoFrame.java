package practice;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import net.miginfocom.swing.MigLayout;
import view.PanelETL;
import view.PanelOlap;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TreeSelectionEvent;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class DemoFrame extends JFrame {

	private JPanel contentPane;
	private JTree tree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DemoFrame frame = new DemoFrame();
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
	public DemoFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 400);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JPanel panelOne = new JPanel();
		panelOne.setBackground(Color.WHITE);
		contentPane.add(panelOne, "cell 0 0,grow");
		panelOne.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		panelOne.add(scrollPane, "cell 0 0,grow");
		
		JPanel panelTwo = new JPanel();
		panelTwo.setBackground(Color.WHITE);
		scrollPane.setViewportView(panelTwo);
		panelTwo.setLayout(new MigLayout("", "[][][grow]", "[]"));
		
		JLabel lblDbColumn = new JLabel("DB Column");
		lblDbColumn.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelTwo.add(lblDbColumn, "cell 0 0,alignx center");
		
		JLabel lblProperty = new JLabel("Property");
		lblDbColumn.setBorder(new EmptyBorder(0, 40, 0, 40));
		lblProperty.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelTwo.add(lblProperty, "cell 1 0,alignx center");
		
		JLabel lblIri = new JLabel("IRI");
		lblDbColumn.setBorder(new EmptyBorder(0, 40, 0, 40));
		lblIri.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelTwo.add(lblIri, "cell 2 0,alignx center");
		
		
		
		for (int i = 1; i < 20; i++) {
			JPanel panel11 = new JPanel();
			panel11.setBackground(Color.WHITE);
			// panel11.setBorder(new EmptyBorder(0, 0, 0, 20));
			panelTwo.add(panel11, "cell 0 "+ i +",grow");
			panel11.setLayout(new BorderLayout(0, 0));
			
			JLabel lblName = new JLabel("Name");
			lblName.setHorizontalAlignment(SwingConstants.CENTER);
			panel11.add(lblName, BorderLayout.CENTER);
			
			JPanel panel12 = new JPanel();
			panel12.setBackground(Color.CYAN);
			panel12.setBorder(new EmptyBorder(0, 20, 0, 20));
			panelTwo.add(panel12, "cell 1 "+ i +",grow");
			
			JRadioButton rdbtnTv = new JRadioButton("TV");
			panel12.add(rdbtnTv);
			
			JRadioButton rdbtnRadio = new JRadioButton("Radio");
			panel12.add(rdbtnRadio);
			
			JRadioButton rdbtnMobile = new JRadioButton("Mobile");
			panel12.add(rdbtnMobile);
			
			JPanel panel13 = new JPanel();
			panel13.setBackground(Color.WHITE);
			panel13.setBorder(new EmptyBorder(0, 20, 0, 0));
			panelTwo.add(panel13, "cell 2 "+ i +",grow");
			panel13.setLayout(new BorderLayout(0, 0));
			
			JTextField textField = new JTextField();
			panel13.add(textField, BorderLayout.CENTER);
			textField.setColumns(10);
		}
	}
}
