package demo;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MyFrame extends JFrame {
	MyTree tree = new MyTree();
	JScrollPane treeScrollPane = new JScrollPane(tree);

	public MyFrame() {

		this.getContentPane().add(treeScrollPane);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 400);
		this.setLocation(200, 200);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new MyFrame();
	}
}
