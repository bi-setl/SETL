package demo;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class CustomRenderer extends DefaultTreeCellRenderer {
	ArrayList<String> list;
	public CustomRenderer(ArrayList<String> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		// TODO Auto-generated method stub
		boolean enabled = false;
		
		if (list.contains(value.toString())) {
			enabled = true;
		}

        sel = enabled;
        hasFocus = enabled;

        // Component treeCellRendererComponent = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        // treeCellRendererComponent.setEnabled(enabled);

        return this;
	}
}
