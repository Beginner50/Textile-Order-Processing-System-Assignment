package tops;

import tops.components.ButtonFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class InvOfficerScreen extends AbstractCustomScreen{
    private final Connection conn;
    private final static String[] ITEM_TABLE_COLUMNS = {"ItemNo", "Description", "Unit Price", "QtyInStock"};

    public InvOfficerScreen() throws SQLException {
        this.conn = DatabaseConnection.getConnection();

        ButtonHandler buttonHandler = new ButtonHandler();
        toolbar.addTabButton("Items","UpdateItemStock", ButtonFactory.createButton("Update item stock level", "edit.png"), buttonHandler);
        tabbedTablePane.addTabbedTable("Items", ITEM_TABLE_COLUMNS);

        toolbar.loadConfiguration("Items", tabbedTablePane.getTableFromTab("Item"));
    }

    class ButtonHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

        }
    }
}
