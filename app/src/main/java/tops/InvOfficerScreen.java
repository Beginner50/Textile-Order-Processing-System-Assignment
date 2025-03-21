package tops;

import tops.components.InvOfficerToolbar;

import java.sql.Connection;
import java.sql.SQLException;

public class InvOfficerScreen extends AbstractCustomScreen{
    private final Connection conn;
    private final static String[] ITEM_TABLE_COLUMNS = {"ItemNo", "Description", "Unit Price", "QtyInStock"};

    public InvOfficerScreen() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
        this.setToolbar(new InvOfficerToolbar());

        tabbedTablePane.addTabbedTable("Item", ITEM_TABLE_COLUMNS);

        toolbar.loadConfiguration("Item", tabbedTablePane.getTableFromTab("Item"));
    }
}
