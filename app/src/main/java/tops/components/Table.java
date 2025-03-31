package tops.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public class Table extends JTable {
    private TableRowSorter<DefaultTableModel> sorter;
    private Map<Integer, Color> rowColors = new HashMap<>();

    public Table(String[] columnNames) {
        this(new Object[][]{}, columnNames);
    }

    public Table(Object[][] data, String[] columnNames) {
        super(new DefaultTableModel(data, columnNames));

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>((DefaultTableModel) this.getModel());
        this.setRowSorter(sorter);
        
        // Set custom cell renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                int modelRow = table.convertRowIndexToModel(row);
                
                if (!isSelected && rowColors.containsKey(modelRow)) {
                    comp.setBackground(rowColors.get(modelRow));
                } else if (isSelected) {
                    comp.setBackground(table.getSelectionBackground());
                } else {
                    comp.setBackground(table.getBackground());
                }
                
                return comp;
            }
        });
    }

    public void addRow(Object[] data) {
        DefaultTableModel model = (DefaultTableModel) this.getModel();
        model.addRow(data);
    }
    
    public void setRowColor(int modelRowIndex, Color color) {
        rowColors.put(modelRowIndex, color);
        repaint();
    }
    
    public void clearRowColors() {
        rowColors.clear();
        repaint();
    }

    public void filterByRegex(String regex) {
        if (regex == null || regex.trim().isEmpty())
            sorter.setRowFilter(null);
        else
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + regex)); // (?i) added for case insensitivity
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        return false;
    }
}
