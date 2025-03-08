package Components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.EventObject;

public class Table extends JTable {
    private TableRowSorter<DefaultTableModel> sorter;

    public Table(String[] columnNames) {
        this(new Object[][]{}, columnNames);
    }

    public Table(Object[][] data, String[] columnNames) {
        super(new DefaultTableModel(data, columnNames));

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>((DefaultTableModel) this.getModel());
        this.setRowSorter(sorter);
    }

    public void addRow(Object[] data) {
        DefaultTableModel model = (DefaultTableModel) this.getModel();
        model.addRow(data);
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
