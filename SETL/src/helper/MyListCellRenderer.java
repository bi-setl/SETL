package helper;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MyListCellRenderer extends JLabel implements ListCellRenderer {
	public MyListCellRenderer() {
        setOpaque(true);
        setVerticalAlignment(CENTER);
        setFont(new Font("Tahoma", Font.BOLD, 12));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// TODO Auto-generated method stub
		if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		
		setText(value.toString());
		return this;
	}	
}