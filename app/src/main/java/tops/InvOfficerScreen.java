package tops;

import tops.components.InvOfficerToolbar;

import java.sql.Connection;

public class InvOfficerScreen extends AbstractCustomScreen{
    private final Connection conn;
    private final static String[] ITEM_TABLE_COLUMNS = {"ItemNo", "Description", "Unit Price", "QtyInStock"};

    public InvOfficerScreen(Connection conn){
        this.conn = conn;
        this.setToolbar(new InvOfficerToolbar());

        tabbedTablePane.addTabbedTable("Item", ITEM_TABLE_COLUMNS);

        toolbar.loadConfiguration("Item", tabbedTablePane.getTableFromTab("Item"));
    }
}
