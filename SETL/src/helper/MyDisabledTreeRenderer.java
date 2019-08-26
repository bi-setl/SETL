package helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyDisabledTreeRenderer extends DefaultTreeCellRenderer {
	ArrayList<String> list = new ArrayList<>();

	public MyDisabledTreeRenderer(ArrayList<String> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
	}

	@Override
	public void firePropertyChange(String arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, byte arg1, byte arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, char arg1, char arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, float arg1, float arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	protected void firePropertyChange(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public void firePropertyChange(String arg0, short arg1, short arg2) {
		// TODO Auto-generated method stub
		super.firePropertyChange(arg0, arg1, arg2);
	}

	@Override
	public Color getBackgroundNonSelectionColor() {
		// TODO Auto-generated method stub
		return super.getBackgroundNonSelectionColor();
	}

	@Override
	public Color getBackgroundSelectionColor() {
		// TODO Auto-generated method stub
		return super.getBackgroundSelectionColor();
	}

	@Override
	public Color getBorderSelectionColor() {
		// TODO Auto-generated method stub
		return super.getBorderSelectionColor();
	}

	@Override
	public Icon getClosedIcon() {
		// TODO Auto-generated method stub
		return super.getClosedIcon();
	}

	@Override
	public Icon getDefaultClosedIcon() {
		// TODO Auto-generated method stub
		return super.getDefaultClosedIcon();
	}

	@Override
	public Icon getDefaultLeafIcon() {
		// TODO Auto-generated method stub
		return super.getDefaultLeafIcon();
	}

	@Override
	public Icon getDefaultOpenIcon() {
		// TODO Auto-generated method stub
		return super.getDefaultOpenIcon();
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return super.getFont();
	}

	@Override
	public Icon getLeafIcon() {
		// TODO Auto-generated method stub
		return super.getLeafIcon();
	}

	@Override
	public Icon getOpenIcon() {
		// TODO Auto-generated method stub
		return super.getOpenIcon();
	}

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return super.getPreferredSize();
	}

	@Override
	public Color getTextNonSelectionColor() {
		// TODO Auto-generated method stub
		return super.getTextNonSelectionColor();
	}

	@Override
	public Color getTextSelectionColor() {
		// TODO Auto-generated method stub
		return super.getTextSelectionColor();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		// TODO Auto-generated method stub
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if (list.contains(value.toString())) {
			this.setEnabled(false);
	        this.setDisabledIcon(this.getLeafIcon());
	        
			// return super.getTreeCellRendererComponent(arg0, value, false, false, arg4, arg5, arg6);
		} /*else {
			// return super.getTreeCellRendererComponent(arg0, value, sel, expanded, arg4, arg5, arg6);
		}*/
		
		return this;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		super.invalidate();
	}

	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
	}

	@Override
	public void repaint() {
		// TODO Auto-generated method stub
		super.repaint();
	}

	@Override
	public void repaint(long arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		super.repaint(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public void repaint(Rectangle arg0) {
		// TODO Auto-generated method stub
		super.repaint(arg0);
	}

	@Override
	public void revalidate() {
		// TODO Auto-generated method stub
		super.revalidate();
	}

	@Override
	public void setBackground(Color arg0) {
		// TODO Auto-generated method stub
		super.setBackground(arg0);
	}

	@Override
	public void setBackgroundNonSelectionColor(Color arg0) {
		// TODO Auto-generated method stub
		super.setBackgroundNonSelectionColor(arg0);
	}

	@Override
	public void setBackgroundSelectionColor(Color arg0) {
		// TODO Auto-generated method stub
		super.setBackgroundSelectionColor(arg0);
	}

	@Override
	public void setBorderSelectionColor(Color arg0) {
		// TODO Auto-generated method stub
		super.setBorderSelectionColor(arg0);
	}

	@Override
	public void setClosedIcon(Icon arg0) {
		// TODO Auto-generated method stub
		super.setClosedIcon(arg0);
	}

	@Override
	public void setFont(Font arg0) {
		// TODO Auto-generated method stub
		super.setFont(arg0);
	}

	@Override
	public void setLeafIcon(Icon arg0) {
		// TODO Auto-generated method stub
		super.setLeafIcon(arg0);
	}

	@Override
	public void setOpenIcon(Icon arg0) {
		// TODO Auto-generated method stub
		super.setOpenIcon(arg0);
	}

	@Override
	public void setTextNonSelectionColor(Color arg0) {
		// TODO Auto-generated method stub
		super.setTextNonSelectionColor(arg0);
	}

	@Override
	public void setTextSelectionColor(Color arg0) {
		// TODO Auto-generated method stub
		super.setTextSelectionColor(arg0);
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		super.updateUI();
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		super.validate();
	}

}
