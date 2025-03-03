import Components.InvOfficerToolbar;

public class InvOfficerScreen extends AbstractCustomScreen{
    private final static String[] ITEM_TABLE_COLUMNS = {"ItemNo", "Description", "Unit Price", "QtyInStock"};

    public InvOfficerScreen(){
        this.setToolbar(new InvOfficerToolbar());

        tabbedTablePane.addTabbedTable("Item", ITEM_TABLE_COLUMNS);

        toolbar.loadConfiguration("Item", tabbedTablePane.getTableFromTab("Item"));
    }
}
