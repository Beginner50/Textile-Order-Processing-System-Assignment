package tops;

import tops.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ISAdminScreen extends AbstractCustomScreen{
    private final Connection conn;
    private final static String[] QUOTATION_TABLE_COLUMNS = { "QuotationNo", "ItemNo", "CustomerId", "Quantity", "Transport Costs", "Item Costs", "Total Costs", "Date Created"};
    private final static String[] ORDER_TABLE_COLUMNS = { "OrderNo", "ItemNo", "CustomerId", "Quantity", "Transport Costs", "Item Costs", "Total Costs", "Order Date", "Status" };
    private final static String[] BILL_TABLE_COLUMNS = { "BillNo", "OrderNo", "Billing Price", "Status" };
    private final static String[] TRANSPORT_CHARGES_TABLE_COLUMNS = { "TransportId", "Transport Name", "Cost Per Distance"  };
    private final static String[] CUSTOMER_TABLE_COLUMNS = { "CustomerId", "Customer Name", "Address", "Contact"  };

    public ISAdminScreen() throws SQLException {
        this.conn = DatabaseConnection.getConnection();

        // Create and add all the tables by their tabs
        tabbedTablePane.addTabbedTable("Quotations", QUOTATION_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Orders", ORDER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Bills", BILL_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Customers", CUSTOMER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("TransportCharges", TRANSPORT_CHARGES_TABLE_COLUMNS);

        // Create and add the toolbar buttons by their tabs
        ButtonHandler buttonHandler = new ButtonHandler();
        toolbar.addTabButton("Quotations", "CreateQuotation", ButtonFactory.createButton("Add Quotation", "add.png"), buttonHandler);
        toolbar.addTabButton("Quotations","UpdateQuotation", ButtonFactory.createButton("Edit Quotation", "edit.png"), buttonHandler);
        toolbar.addTabButton("Quotations","DeleteQuotation", ButtonFactory.createButton("Delete Quotation", "delete.png"), buttonHandler);
        toolbar.addTabButton("Quotations","ConvertQuotation", ButtonFactory.createButton("Convert to Order", "convert.png"), buttonHandler);
        toolbar.addTabButton("Orders","CreateOrder", ButtonFactory.createButton("Add Order", "add.png"), buttonHandler);
        toolbar.addTabButton("Orders","UpdateOrder", ButtonFactory.createButton("Edit Order", "edit.png"), buttonHandler);
        toolbar.addTabButton("Customers","CreateCustomer", ButtonFactory.createButton("Add Customer", "add.png"), buttonHandler);
        toolbar.addTabButton("Customers","UpdateCustomer", ButtonFactory.createButton("Edit Customer", "edit.png"), buttonHandler);
        toolbar.addTabButton("TransportCharges","CreateTransportCharge", ButtonFactory.createButton("Add Transport Charge", "add.png"), buttonHandler);
        toolbar.addTabButton("TransportCharges","UpdateTransportCharge", ButtonFactory.createButton("Edit Transport Charge", "edit.png"), buttonHandler);

        // Populate the tables with data
        populateTables();

        // Load the default configuration of the toolbar: Quotation
        toolbar.loadConfiguration("Quotations", tabbedTablePane.getTableFromTab("Quotations"));
    }

    public void populateTables() {
        populateQuotationTable();

        populateOrderTable();

        populateBillTable();

        populateCustomerTable();

        populateTransportChargesTable();
    }

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            Table currentTable = toolbar.getCurrentTable();
            if (source == toolbar.getTabButton("CreateQuotation")) {
                // Pass null for new quotations
                new QuotationForm(null, currentTable).setVisible(true);
            } else if (source == toolbar.getTabButton("UpdateQuotation")) {
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
            } else if (source == toolbar.getTabButton("DeleteQuotation")) {
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
            } else if (source == toolbar.getTabButton("ConvertQuotation")) {
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
//                        orderFromQuotation = quotationData.clone();
//                        orderFromQuotation[0] = "ORD-" + quotationData[0].toString().replace("QUO-", "");

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
            } else if (source == toolbar.getTabButton("CreateOrder")) {
                new OrderForm(null, currentTable).setVisible(true);
            } else if (source == toolbar.getTabButton("UpdateOrder")) {
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

    private void populateQuotationTable() {
        String query = "SELECT QuotationNo, ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, date_created FROM Quotations";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("QuotationNo"),
                        rs.getInt("ItemNo"),
                        rs.getInt("CustomerId"),
                        rs.getInt("quantity"),
                        rs.getDouble("transport_costs"),
                        rs.getDouble("item_costs"),
                        rs.getDouble("total_costs"),
                        rs.getDate("date_created")
                };
                tabbedTablePane.getTableFromTab("Quotations").addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateOrderTable() {
        String query = "SELECT OrderNo, ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, order_date, status FROM Orders";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("OrderNo"),
                        rs.getInt("ItemNo"),
                        rs.getInt("CustomerId"),
                        rs.getInt("quantity"),
                        rs.getDouble("transport_costs"),
                        rs.getDouble("item_costs"),
                        rs.getDouble("total_costs"),
                        rs.getDate("order_date"),
                        rs.getString("status")
                };
                tabbedTablePane.getTableFromTab("Orders").addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateBillTable(){
        String query = "SELECT BillNo, OrderNo, billing_price, status FROM Bills";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                Object[] row = {
                    rs.getInt("BillNo"),
                    rs.getInt("OrderNo"),
                    rs.getDouble("billing_price"),
                    rs.getString("status")
                };
                tabbedTablePane.getTableFromTab("Bills").addRow(row);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void populateCustomerTable() {
        String query = "SELECT CustomerId, name, address, contact FROM Customers";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("CustomerId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact")
                };
                tabbedTablePane.getTableFromTab("Customers").addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateTransportChargesTable() {
        String query = "SELECT TransportId, transport_name, charge_per_distance FROM Transport_Charges";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("TransportId"),
                        rs.getString("transport_name"),
                        rs.getDouble("charge_per_distance")
                };
                tabbedTablePane.getTableFromTab("TransportCharges").addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
