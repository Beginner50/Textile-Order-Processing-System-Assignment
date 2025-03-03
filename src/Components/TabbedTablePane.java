package Components;

import javax.swing.*;
import java.util.HashMap;

public class TabbedTablePane extends JTabbedPane {
    private final HashMap<String, TabbedTable> tabbedTables;

    public TabbedTablePane(){
        tabbedTables = new HashMap<>();
    }

    public CustomTable getTableFromTab(String tabName){
        return tabbedTables.get(tabName).table;
    }

    public void setTableInTab(String tabName, CustomTable table){
        tabbedTables.get(tabName).table = table;
    }

    public void addTabbedTable(String tabName, String[] columns){
        tabbedTables.put(tabName, new TabbedTable(new CustomTable(columns)));
        this.add(tabName, tabbedTables.get(tabName));
    }

    public void removeTabbedTable(String tabName){
        this.remove(tabbedTables.get(tabName));
        this.revalidate();
        this.repaint();
    }

    class TabbedTable extends JScrollPane{
        public CustomTable table;

        public TabbedTable(CustomTable table){
            super(table);
            this.table = table;
        }
    }
}

