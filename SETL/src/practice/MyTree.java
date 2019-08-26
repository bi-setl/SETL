package practice;

import javax.swing.JTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;

public class MyTree extends JTree implements TreeExpansionListener {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

	public MyTree() {
		this.setTree();
		this.setModel(new DefaultTreeModel(root));
		this.setCellRenderer(new MyCellRenderer());
		this.addTreeExpansionListener(this);
	}

	public void setTree() {
		DefaultMutableTreeNode parent;
		for (int i = 1; i <= 5; i++) {
			parent = new DefaultMutableTreeNode("Parent " + i);
			for (int j = 1; j <= 3; j++) {
				parent.add(new DefaultMutableTreeNode("Child " + j));
			}
			root.add(parent);
		}
	}

	public void treeExpanded(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		String str = (String) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
		if (str.equals("Parent 3")) {
			this.setExpandedState(path, false);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {

	}

}

class MyCellRenderer extends DefaultTreeCellRenderer
{
    private boolean is_selected;
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, 
                                                boolean expanded, boolean leaf, int row, boolean hasFocus)        
  {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
    String name = (String)node.getUserObject();
    is_selected = false;
    if(name.equals("Parent 3")) {
        this.setEnabled(false);
        this.setDisabledIcon(this.getClosedIcon()); // I used the standard      
        if(sel)
            is_selected = true;
    }
    return this;
  }
   
  public void paintComponent(Graphics g) {
      g.setColor(Color.white);
      int offset = 0;
      if (is_selected) {
          g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
          g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
      }
      super.paintComponent(g);
  }
}