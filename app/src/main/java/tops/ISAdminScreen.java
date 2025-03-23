package tops;

import tops.components.Table;
import tops.components.ISAdminToolbar;

import javax.swing.table.DefaultTableModel;
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

        // Set the toolbar
        this.setToolbar(new ISAdminToolbar());

        // Initialise data in the tables
        tabbedTablePane.addTabbedTable("Quotations", QUOTATION_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Orders", ORDER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Bills", BILL_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Customers", CUSTOMER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("TransportCharges", TRANSPORT_CHARGES_TABLE_COLUMNS);

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
