package Components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Toolbar extends JPanel {
    private ButtonHandler buttonHandler;

    private JPanel searchPanel;
    private JTextField searchBar;
    private JButton searchButton;

    private JPanel crudButtonsPanel;
    private HashMap<String, ToolbarButton> crudButtons;

    private Object[] orderFromQuotation;
    private Table currentTable;

    public Toolbar() {
        super();
        this.setBackground(Color.lightGray);
        this.setLayout(new BorderLayout());
        buttonHandler = new ButtonHandler();

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.lightGray);

        searchBar = new JTextField("", 40);
        searchButton = new JButton("", new ImageIcon("search-interface-symbol.png"));
        searchButton.addActionListener(buttonHandler);
        searchButton.setToolTipText("Search");

        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        crudButtonsPanel = new JPanel();
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        crudButtonsPanel.setBackground(Color.lightGray);

        crudButtons = new HashMap<>();
        crudButtons.put("CreateQuotation", new ToolbarButton("Add Quotation", "add.png"));
        crudButtons.put("UpdateQuotation", new ToolbarButton("Edit Quotation", "edit.png"));
        crudButtons.put("DeleteQuotation", new ToolbarButton("Delete Quotation", "delete.png"));
        crudButtons.put("ConvertToOrder", new ToolbarButton("Convert to Order", "convert.png"));
        crudButtons.put("CreateOrder", new ToolbarButton("Add Order", "add.png"));
        crudButtons.put("UpdateOrder", new ToolbarButton("Edit Order", "edit.png"));
        crudButtons.put("DeleteOrder", new ToolbarButton("Delete Order", "delete.png"));

        for (ToolbarButton button : crudButtons.values()) {
            button.addActionListener(buttonHandler);
            crudButtonsPanel.add(button);
        }

        this.add(searchPanel, BorderLayout.WEST);
        this.add(crudButtonsPanel, BorderLayout.EAST);
    }

    public void loadConfiguration(String tabName, Table table) {
        currentTable = table;

        // Hide all crud buttons
        for (ToolbarButton toolbarButton : crudButtons.values())
            toolbarButton.setVisible(false);

        switch (tabName) {
            case "Quotation":
                crudButtons.get("CreateQuotation").setVisible(true);
                crudButtons.get("UpdateQuotation").setVisible(true);
                crudButtons.get("DeleteQuotation").setVisible(true);
                crudButtons.get("ConvertToOrder").setVisible(true);
                break;
            case "Order":
                if(orderFromQuotation != null){
                    currentTable.addRow(orderFromQuotation);
                    orderFromQuotation = null;
                }
                crudButtons.get("CreateOrder").setVisible(true);
                crudButtons.get("UpdateOrder").setVisible(true);
                crudButtons.get("DeleteOrder").setVisible(true);
                break;
            case "Bill":
                break;
            default:
                break;
        }
    }

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            if (source == searchButton) {
                currentTable.filterByRegex(searchBar.getText());
            } else if (source == crudButtons.get("CreateQuotation")) {
                new QuotationForm(null, currentTable).setVisible(true);
            } else if (source == crudButtons.get("UpdateQuotation")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get data from selected row
                    Object[] rowData = new Object[currentTable.getColumnCount()];
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = currentTable.getValueAt(selectedRow, i);
                    }
                    new QuotationForm(rowData, currentTable).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quotation to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == crudButtons.get("DeleteQuotation")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this quotation?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) currentTable.getModel()).removeRow(
                                currentTable.convertRowIndexToModel(selectedRow));
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quotation to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == crudButtons.get("ConvertToOrder")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Convert this quotation to an order?",
                            "Confirm Conversion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Get the quotation data
                        Object[] quotationData = new Object[currentTable.getColumnCount()];
                        for (int i = 0; i < quotationData.length; i++) {
                            quotationData[i] = currentTable.getValueAt(selectedRow, i);
                        }

                        // Create order data (replace quotation number with order number)
                        orderFromQuotation = quotationData.clone();
                        orderFromQuotation[0] = "ORD-" + quotationData[0].toString().replace("QUO-", "");

                        // Optionally delete the quotation
                        int deleteQuotation = JOptionPane.showConfirmDialog(null,
                                    "Do you want to delete the original quotation?",
                                    "Delete Original", JOptionPane.YES_NO_OPTION);
                        if (deleteQuotation == JOptionPane.YES_OPTION) {
                                ((DefaultTableModel) currentTable.getModel()).removeRow(
                                        currentTable.convertRowIndexToModel(selectedRow));
                        }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Could not locate the main application window.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quotation to convert",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == crudButtons.get("CreateOrder")) {
                new OrderForm(null, currentTable).setVisible(true);
            } else if (source == crudButtons.get("UpdateOrder")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get data from selected row
                    Object[] rowData = new Object[currentTable.getColumnCount()];
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = currentTable.getValueAt(selectedRow, i);
                    }
                    new OrderForm(rowData, currentTable).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == crudButtons.get("DeleteOrder")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this order?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) currentTable.getModel()).removeRow(
                                currentTable.convertRowIndexToModel(selectedRow));
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    class ToolbarButton extends JButton {
        public ToolbarButton(String tooltipText, String imagePath) {
            super("", new ImageIcon(imagePath));
            this.setToolTipText(tooltipText);
            this.setVisible(false);

            // Set a consistent preferred size for all buttons
            Dimension buttonSize = new Dimension(40, 40);
            this.setPreferredSize(buttonSize);
        }
    }
}
