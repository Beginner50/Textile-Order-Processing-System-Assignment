package tops;

import tops.components.Table;
import tops.components.ISAdminToolbar;

import java.sql.Connection;

public class ISAdminScreen extends AbstractCustomScreen{
    private final Connection conn;
    private final static String[] QUOTATION_TABLE_COLUMNS = { "QuotationNo", "ItemNo", "Qty", "Client Name", "Price",
            "Transport Costs", "Total Costs" };
    private final static String[] ORDER_TABLE_COLUMNS = { "OrderNo", "ItemNo", "Qty", "Client Name", "Price",
            "Transport Costs", "Total Costs" };
    private final static String[] BILL_TABLE_COLUMNS = { "BillNo", "OrderNo", "Client Name", "Billing Price" };

    public ISAdminScreen(Connection conn){
        this.conn = conn;

        // Set the toolbar
        this.setToolbar(new ISAdminToolbar());

        // Initialise data in the tables
        tabbedTablePane.addTabbedTable("Quotation", QUOTATION_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Order", ORDER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Bill", BILL_TABLE_COLUMNS);

        Table quotationTable = tabbedTablePane.getTableFromTab("Quotation");
        quotationTable.addRow(new Object[] { "1", "2", "5", "Charles", 2500, 700, 3200 });

        // Load the default configuration of the toolbar: Quotation
        toolbar.loadConfiguration("Quotation", tabbedTablePane.getTableFromTab("Quotation"));
    }
}
