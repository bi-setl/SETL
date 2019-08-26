package demo;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxDisabledItemsDemo {
    

    private JComboBox comboBox;
    private JCheckBox disableCheckBox;
    
    private static final int[] SELECTION_INTERVAL = { 0, 1 };
    private DefaultListSelectionModel model = new DefaultListSelectionModel();
    private EnabledComboBoxRenderer enableRenderer = new EnabledComboBoxRenderer();

    private EnabledListener enabledListener = new EnabledListener();
    private DisabledListener disabledListener = new DisabledListener();

    public ComboBoxDisabledItemsDemo() {
        comboBox = createComboBox();

        disableCheckBox = createCheckBox();
        disableCheckBox.setSelected(true);

        JFrame frame = new JFrame("Disabled Combo Box Items");
        frame.setLayout(new GridBagLayout());
        frame.add(comboBox);
        frame.add(disableCheckBox);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JComboBox createComboBox() {
        String[] list = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
                "Item 6", "Item 7" };
        JComboBox cbox = new JComboBox(list);
        model.addSelectionInterval(SELECTION_INTERVAL[0], SELECTION_INTERVAL[1]);
        enableRenderer.setEnabledItems(model);
        cbox.setRenderer(enableRenderer);
        return cbox;
    }

    private class EnabledListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(((JComboBox) e.getSource()).getSelectedItem());
        }
    }

    private class DisabledListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JComboBox) e.getSource()).getSelectedIndex() != SELECTION_INTERVAL[0]
                    && ((JComboBox) e.getSource()).getSelectedIndex() != SELECTION_INTERVAL[1]) {
                JOptionPane.showMessageDialog(null,
                        "You can't Select that Item", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println(((JComboBox) e.getSource())
                        .getSelectedItem());
            }
        }
    }

    protected void disableItemsInComboBox() {
        comboBox.removeActionListener(enabledListener);
        comboBox.addActionListener(disabledListener);
        model.setSelectionInterval(SELECTION_INTERVAL[0], SELECTION_INTERVAL[1]);
    }

    protected void enableItemsInComboBox() {
        comboBox.removeActionListener(disabledListener);
        comboBox.addActionListener(enabledListener);
        model.setSelectionInterval(SELECTION_INTERVAL[0], comboBox.getModel()
                .getSize() - 1);
    }

    private JCheckBox createCheckBox() {
        JCheckBox checkBox = new JCheckBox("diabled");
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    disableItemsInComboBox();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    enableItemsInComboBox();
                }
            }
        });
        return checkBox;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ComboBoxDisabledItemsDemo();
            }
        });
    }
}

class EnabledComboBoxRenderer extends BasicComboBoxRenderer {

    private ListSelectionModel enabledItems;

    private Color disabledColor = Color.lightGray;

    public EnabledComboBoxRenderer() {}

    public EnabledComboBoxRenderer(ListSelectionModel enabled) {
        super();
        this.enabledItems = enabled;
    }

    public void setEnabledItems(ListSelectionModel enabled) {
        this.enabledItems = enabled;
    }

    public void setDisabledColor(Color disabledColor) {
        this.disabledColor = disabledColor;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (!enabledItems.isSelectedIndex(index)) {// not enabled
            if (isSelected) {
                c.setBackground(UIManager.getColor("ComboBox.background"));
            } else {
                c.setBackground(super.getBackground());
            }

            c.setForeground(disabledColor);

        } else {
            c.setBackground(super.getBackground());
            c.setForeground(super.getForeground());
        }
        return c;
    }
}