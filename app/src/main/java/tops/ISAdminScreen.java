package tops;

import tops.components.*;
import tops.forms.CustomerForm;
import tops.forms.OrderForm;
import tops.forms.QuotationForm;
import tops.forms.TransportChargeForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ISAdminScreen extends AbstractCustomScreen {
    private final Connection conn;
    private final static String[] QUOTATION_TABLE_COLUMNS = { "QuotationNo", "ItemNo", "CustomerId", "Quantity",
            "Transport Costs", "Item Costs", "Total Costs", "Date Created" };
    private final static String[] ORDER_TABLE_COLUMNS = { "OrderNo", "ItemNo", "CustomerId", "Quantity",
            "Transport Costs", "Item Costs", "Total Costs", "Order Date", "Status" };
    private final static String[] BILL_TABLE_COLUMNS = { "BillNo", "OrderNo", "Billing Price", "Status" };
    private final static String[] TRANSPORT_CHARGES_TABLE_COLUMNS = { "TransportId", "Transport Name",
            "Cost Per Distance" };
    private final static String[] CUSTOMER_TABLE_COLUMNS = { "CustomerId", "Customer Name", "Address", "Contact" };

    public ISAdminScreen(Connection conn) {
        this.conn = conn;

        // Create and add all the tables by their tabs
        tabbedTablePane.addTableToTab("Quotations", QUOTATION_TABLE_COLUMNS);
        tabbedTablePane.addTableToTab("Orders", ORDER_TABLE_COLUMNS);
        tabbedTablePane.addTableToTab("Bills", BILL_TABLE_COLUMNS);
        tabbedTablePane.addTableToTab("Customers", CUSTOMER_TABLE_COLUMNS);
        tabbedTablePane.addTableToTab("TransportCharges", TRANSPORT_CHARGES_TABLE_COLUMNS);

        // Create and add the toolbar buttons by their tabs
        ButtonHandler buttonHandler = new ButtonHandler();
        toolbar.addTabButton("Quotations", "CreateQuotation", ButtonFactory.createButton("Add Quotation", "add.png"),
                buttonHandler);
        toolbar.addTabButton("Quotations", "UpdateQuotation", ButtonFactory.createButton("Edit Quotation", "edit.png"),
                buttonHandler);
        toolbar.addTabButton("Quotations", "DeleteQuotation",
                ButtonFactory.createButton("Delete Quotation", "delete.png"), buttonHandler);
        toolbar.addTabButton("Quotations", "ConvertQuotation",
                ButtonFactory.createButton("Convert to Order", "convert.png"), buttonHandler);
        toolbar.addTabButton("Orders", "CreateOrder", ButtonFactory.createButton("Add Order", "add.png"),
                buttonHandler);
        toolbar.addTabButton("Orders", "UpdateOrder", ButtonFactory.createButton("Edit Order", "edit.png"),
                buttonHandler);
        toolbar.addTabButton("Customers", "CreateCustomer", ButtonFactory.createButton("Add Customer", "add.png"),
                buttonHandler);
        toolbar.addTabButton("Customers", "UpdateCustomer", ButtonFactory.createButton("Edit Customer", "edit.png"),
                buttonHandler);
        toolbar.addTabButton("Customers", "DeleteCustomer", ButtonFactory.createButton("Delete Customer", "delete.png"),
                buttonHandler);
        toolbar.addTabButton("TransportCharges", "CreateTransportCharge",
                ButtonFactory.createButton("Add Transport Charge", "add.png"), buttonHandler);
        toolbar.addTabButton("TransportCharges", "UpdateTransportCharge",
                ButtonFactory.createButton("Edit Transport Charge", "edit.png"), buttonHandler);
        toolbar.addTabButton("TransportCharges", "DeleteTransportCharge",
                ButtonFactory.createButton("Delete Transport Charge", "delete.png"), buttonHandler);

        // Populate the tables with data
        populateTables();

        // Load the default configuration of the toolbar: Quotation
        selectedTab = "Quotations";
        toolbar.loadConfiguration("Quotations");
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
            // Quotations Table Variables
            int quotationNo, itemNo, customerId, quantity;
            double transport_costs, item_costs, total_costs;
            LocalDate date = null;
            quotationNo = itemNo = customerId = quantity = 0;
            transport_costs = item_costs = total_costs = 0;

            // Order Table Variables
            int orderNo = 0;
            String status = "";

            Object source = actionEvent.getSource();
            Table currentTable = tabbedTablePane.getTableFromTab(selectedTab);
            Object[] rowData = null;    

            // Get row data if selected
            if (selectedTab.equals("Quotations") && currentTable.getSelectedRow() != -1) {
                quotationNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 0);
                itemNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 1);
                customerId = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 2);
                quantity = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 3);
                transport_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 4);
                item_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 5);
                total_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 6);
                date = getLocalDate(currentTable.getValueAt(currentTable.getSelectedRow(), 7));
                rowData = new Object[] { quotationNo, itemNo, customerId, quantity, transport_costs, item_costs,
                        total_costs, date };
            } else if (selectedTab.equals("Orders") && currentTable.getSelectedRow() != -1) {
                orderNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 0);
                itemNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 1);
                customerId = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 2);
                quantity = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 3);
                transport_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 4);
                item_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 5);
                total_costs = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 6);
                date = getLocalDate(currentTable.getValueAt(currentTable.getSelectedRow(), 7));
                status = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 8);
                rowData = new Object[] { orderNo, itemNo, customerId, quantity, transport_costs, item_costs,
                        total_costs, date, status };
            } else if (selectedTab.equals("Bills") && currentTable.getSelectedRow() != -1) {
                int billNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 0);
                orderNo = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 1);
                double billingPrice = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 2);
                status = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 3);
                rowData = new Object[] { billNo, orderNo, billingPrice, status };
            } else if (selectedTab.equals("Customers") && currentTable.getSelectedRow() != -1) {
                customerId = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 0);
                String name = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 1);
                String address = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 2);
                String contact = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 3);
                rowData = new Object[] { customerId, name, address, contact };
            } else if (selectedTab.equals("TransportCharges") && currentTable.getSelectedRow() != -1) {
                int transportId = (int) currentTable.getValueAt(currentTable.getSelectedRow(), 0);
                String transportName = (String) currentTable.getValueAt(currentTable.getSelectedRow(), 1);
                double chargePerDistance = (double) currentTable.getValueAt(currentTable.getSelectedRow(), 2);
                rowData = new Object[] { transportId, transportName, chargePerDistance };
            }

            if (source == toolbar.getTabButton("CreateQuotation")) {
                // Pass null for new quotations
                new QuotationForm(null, currentTable).setVisible(true);

            } else if (source == toolbar.getTabButton("UpdateQuotation")) {
                // If row is not selected, warn user and return
                if (currentTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a quotation to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Pass the row index to the QuotationForm constructor
                new QuotationForm(rowData, currentTable, currentTable.getSelectedRow()).setVisible(true);

            } else if (source == toolbar.getTabButton("DeleteQuotation")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this quotation?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION)
                        deleteQuotation(quotationNo);
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
                        // Remove row from quotation table
                        deleteQuotation(quotationNo);

                        // Insert new order
                        addOrder(itemNo, customerId, quantity, transport_costs, item_costs, total_costs, date);
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
                    new OrderForm(rowData, currentTable).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == toolbar.getTabButton("CreateCustomer")) {
                new CustomerForm(conn, null, currentTable, ISAdminScreen.this).setVisible(true);
            } else if (source == toolbar.getTabButton("UpdateCustomer")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    new CustomerForm(conn, rowData, currentTable, ISAdminScreen.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a customer to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == toolbar.getTabButton("DeleteCustomer")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this customer?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        customerId = Integer.parseInt(currentTable.getValueAt(selectedRow, 0).toString());

                        try {
                            // Delete the customer from the database
                            String query = "DELETE FROM Customers WHERE CustomerId = ?";
                            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                                stmt.setInt(1, customerId);
                                int rowsAffected = stmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    // If successful, remove from the table model
                                    ((DefaultTableModel) currentTable.getModel()).removeRow(
                                            currentTable.convertRowIndexToModel(selectedRow));

                                    JOptionPane.showMessageDialog(null,
                                            "Customer deleted successfully",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Failed to delete customer. The record may no longer exist.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();

                            if (e.getMessage().contains("foreign key constraint")) {
                                JOptionPane.showMessageDialog(null,
                                        "Cannot delete this customer because it is referenced by existing orders or quotations.",
                                        "Constraint Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Database error: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a customer to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == toolbar.getTabButton("CreateTransportCharge")) {
                new TransportChargeForm(conn, null, currentTable, ISAdminScreen.this).setVisible(true);
            } else if (source == toolbar.getTabButton("UpdateTransportCharge")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    new TransportChargeForm(conn, rowData, currentTable, ISAdminScreen.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a transport charge to edit",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == toolbar.getTabButton("DeleteTransportCharge")) {
                int selectedRow = currentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this transport charge?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int transportId = Integer.parseInt(currentTable.getValueAt(selectedRow, 0).toString());

                        try {
                            // Delete the transport charge from the database
                            String query = "DELETE FROM Transport_Charges WHERE TransportId = ?";
                            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                                stmt.setInt(1, transportId);
                                int rowsAffected = stmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    // If successful, remove from the table model
                                    ((DefaultTableModel) currentTable.getModel()).removeRow(
                                            currentTable.convertRowIndexToModel(selectedRow));

                                    JOptionPane.showMessageDialog(null,
                                            "Transport charge deleted successfully",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Failed to delete transport charge. The record may no longer exist.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();

                            if (e.getMessage().contains("foreign key constraint")) {
                                JOptionPane.showMessageDialog(null,
                                        "Cannot delete this transport charge because it is referenced by existing orders or quotations.",
                                        "Constraint Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Database error: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a transport charge to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an item to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void deleteQuotation(int quotationNo) {
            try {
                // Delete quotation in database
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Quotations WHERE QuotationNo=?;");
                stmt.setInt(1, quotationNo);
                stmt.execute();
                stmt.close();

                // Search for the row with matching QuotationNo and remove it from Quotations
                // table
                DefaultTableModel model = (DefaultTableModel) tabbedTablePane.getTableFromTab("Quotations").getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    int currentNo = (Integer) model.getValueAt(i, 0);
                    if (currentNo == quotationNo) {
                        model.removeRow(i);
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void addOrder(int itemNo, int customerId, int quantity, double transport_costs, double item_costs,
                double total_costs, LocalDate order_date) {
            String status = "Shipped";
            try {
                // Get item stock_level and reorder_threshold
                PreparedStatement stmt = conn.prepareStatement("SELECT stock_level FROM Items WHERE ItemNo = ?;");
                stmt.setInt(1, itemNo);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int stock_level = rs.getInt(1);
                stmt.close();

                // Update item stock_level if quantity < stock_level
                if (stock_level >= quantity) {
                    stock_level -= quantity;
                    stmt = conn.prepareStatement("UPDATE Items SET stock_level = ? WHERE ItemNo = ?;");
                    stmt.setInt(1, stock_level);
                    stmt.setInt(2, itemNo);
                    stmt.execute();
                    stmt.close();
                } else
                    status = "Pending";

                // Insert new order
                stmt = conn.prepareStatement(
                        "INSERT INTO Orders (ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, order_date, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, itemNo);
                stmt.setInt(2, customerId);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, transport_costs);
                stmt.setDouble(5, item_costs);
                stmt.setDouble(6, total_costs);
                stmt.setDate(7, java.sql.Date.valueOf(order_date));
                stmt.setString(8, status);
                stmt.executeUpdate();
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                generatedKeys.next();
                int orderNo = generatedKeys.getInt(1);
                stmt.close();

                // Add bill if status is shipped
                if (status.equals("Shipped")) {
                    // Insert Bill into database
                    stmt = conn.prepareStatement("INSERT INTO Bills (OrderNo, billing_price, status) VALUES(?, ?, ?);",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, orderNo);
                    stmt.setDouble(2, total_costs);
                    stmt.setString(3, "Unpaid");
                    stmt.executeUpdate();

                    generatedKeys = stmt.getGeneratedKeys();
                    generatedKeys.next();
                    int billNo = generatedKeys.getInt(1);
                    stmt.close();

                    // Update Bills table
                    DefaultTableModel billTableModel = (DefaultTableModel) tabbedTablePane.getTableFromTab("Bills")
                            .getModel();
                    billTableModel.addRow(new Object[] { billNo, orderNo, total_costs, "Unpaid" });
                }

                // Add order row to order table
                DefaultTableModel orderTableModel = (DefaultTableModel) tabbedTablePane.getTableFromTab("Orders")
                        .getModel();
                orderTableModel.addRow(new Object[] { orderNo, itemNo, customerId, quantity, transport_costs,
                        item_costs, total_costs, order_date, status });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private LocalDate getLocalDate(Object dateVal) {
            if (dateVal instanceof java.sql.Date) {
                return ((java.sql.Date) dateVal).toLocalDate();
            } else if (dateVal instanceof Date) {
                return ((Date) dateVal).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } else if (dateVal instanceof String) {
                return LocalDate.parse((String) dateVal);
            } else if (dateVal instanceof LocalDate) {
                return (LocalDate) dateVal;
            }
            return null; // or throw exception
        }
    }

    // These methods need to be public so they can be called from the forms
    public void populateCustomerTable() {
        String query = "SELECT CustomerId, name, address, contact FROM Customers";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            // Clear the table first
            DefaultTableModel model = (DefaultTableModel) tabbedTablePane.getTableFromTab("Customers").getModel();
            model.setRowCount(0);

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

    public void populateTransportChargesTable() {
        String query = "SELECT TransportId, transport_name, charge_per_distance FROM Transport_Charges";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            // Clear the table first
            DefaultTableModel model = (DefaultTableModel) tabbedTablePane.getTableFromTab("TransportCharges")
                    .getModel();
            model.setRowCount(0);

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

    private void populateBillTable() {
        String query = "SELECT BillNo, OrderNo, billing_price, status FROM Bills";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("BillNo"),
                        rs.getInt("OrderNo"),
                        rs.getDouble("billing_price"),
                        rs.getString("status")
                };
                tabbedTablePane.getTableFromTab("Bills").addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the database connection
     * 
     * @return the database connection object
     */
    public Connection getConnection() {
        return conn;
    }
}
