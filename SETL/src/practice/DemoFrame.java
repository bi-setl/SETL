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
import javax.swing.JComboBox;

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
		
		/*ArrayList<String> list2 = new ArrayList<>();
		list2.add("Rasu");
		
		tree = new JTree();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (list2.contains(tree.getLastSelectedPathComponent().toString())) {
					System.out.println("contains");
				}
			}
		});
		tree.setCellRenderer(new MyRen(list2));
		contentPane.add(tree, "cell 0 0,grow");
		
		ArrayList<String> list = new ArrayList<>();
		list.add("Rasu");
		list.add("Ritu");
		list.add("Rasel");
		list.add("Abhi");
		list.add("Rafi");
		list.add("Sayem");
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Friends");
		for (String string : list) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(string);
			rootNode.add(node);
		}
		tree.setModel(new DefaultTreeModel(rootNode));*/
		
		/*PanelETL panelETL = new PanelETL();
		contentPane.add(panelETL, "cell 0 0,grow");*/
		
		PanelOlap panelOlap = new PanelOlap();
		contentPane.add(panelOlap, "cell 0 0,grow");
	}
}
