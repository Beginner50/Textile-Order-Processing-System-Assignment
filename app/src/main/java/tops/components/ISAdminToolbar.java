package tops.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ISAdminToolbar extends AbstractToolbar {
    private Object[] orderFromQuotation;
    private ButtonHandler crudButtonHandler;

    public ISAdminToolbar() {
        crudButtonHandler = new ButtonHandler();

        this.addCrudButton("CreateQuotation", ButtonFactory.createButton("Add Quotation", "add.png"),
                crudButtonHandler);
        this.addCrudButton("UpdateQuotation", ButtonFactory.createButton("Edit Quotation", "edit.png"),
                crudButtonHandler);
        this.addCrudButton("DeleteQuotation", ButtonFactory.createButton("Delete Quotation", "delete.png"),
                crudButtonHandler);
        this.addCrudButton("ConvertQuotation", ButtonFactory.createButton("Convert to Order", "convert.png"),
                crudButtonHandler);
        this.addCrudButton("CreateOrder", ButtonFactory.createButton("Add Order", "add.png"), crudButtonHandler);
        this.addCrudButton("UpdateOrder", ButtonFactory.createButton("Edit Order", "edit.png"), crudButtonHandler);
        // this.addCrudButton("DeleteOrder", ButtonFactory.createButton("Delete Order",
        // "delete.png"), crudButtonHandler);
    }

    @Override
    public void loadConfiguration(String tabName, Table table) {
        super.loadConfiguration(tabName, table);

        switch (tabName) {
            case "Quotation":
                crudButtons.get("CreateQuotation").setVisible(true);
                crudButtons.get("UpdateQuotation").setVisible(true);
                crudButtons.get("DeleteQuotation").setVisible(true);
                crudButtons.get("ConvertQuotation").setVisible(true);
                break;
            case "Order":
                if (orderFromQuotation != null) {
                    currentTable.addRow(orderFromQuotation);
                    orderFromQuotation = null;
                }
                crudButtons.get("CreateOrder").setVisible(true);
                crudButtons.get("UpdateOrder").setVisible(true);
                // crudButtons.get("DeleteOrder").setVisible(true);
                break;
            case "Bill":
                break;
            case "Customer":
                break;
            case "TransportCharges":
                break;
            default:
                break;
        }
    }

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            if (source == crudButtons.get("CreateQuotation")) {
                // Pass null for new quotations
                new QuotationForm(null, currentTable).setVisible(true);
            } else if (source == crudButtons.get("UpdateQuotation")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get data from selected row
                    Object[] rowData = new Object[currentTable.getColumnCount()];
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = currentTable.getValueAt(selectedRow, i);
                    }
                    // Pass the row index to the QuotationForm constructor
                    new QuotationForm(rowData, currentTable, selectedRow).setVisible(true);
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
            } else if (source == crudButtons.get("ConvertQuotation")) {
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

                        // Delete the quotation
                        ((DefaultTableModel) currentTable.getModel()).removeRow(currentTable.convertRowIndexToModel(selectedRow));
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
            }
            // } else if (source == crudButtons.get("DeleteOrder")) {
            // int selectedRow = currentTable.getSelectedRow();
            // if (selectedRow != -1) {
            // int confirm = JOptionPane.showConfirmDialog(null,
            // "Are you sure you want to delete this order?",
            // "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            // if (confirm == JOptionPane.YES_OPTION) {
            // ((DefaultTableModel) currentTable.getModel()).removeRow(
            // currentTable.convertRowIndexToModel(selectedRow));
            // }
            else {
                JOptionPane.showMessageDialog(null, "Please select an order to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}