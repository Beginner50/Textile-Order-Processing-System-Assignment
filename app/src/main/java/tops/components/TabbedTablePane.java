package tops.components;

import javax.swing.*;
import java.util.HashMap;

public class TabbedTablePane extends JTabbedPane {
    private final HashMap<String, ScrollableTable> tabbedTables;

    public TabbedTablePane(){
        tabbedTables = new HashMap<>();
    }

    public Table getTableFromTab(String tabName){
        return tabbedTables.get(tabName).table;
    }

    public void setTableInTab(String tabName, Table table){
        tabbedTables.get(tabName).table = table;
    }

    public void addTableToTab(String tabName, String[] columns){
        tabbedTables.put(tabName, new ScrollableTable(new Table(columns)));
        this.add(tabName, tabbedTables.get(tabName));
    }

    public void removeTableFromTab(String tabName){
        this.remove(tabbedTables.get(tabName));
        this.revalidate();
        this.repaint();
    }

    class ScrollableTable extends JScrollPane{
        public Table table;

        public ScrollableTable(Table table){
            super(table);
            this.table = table;
        }
    }
}

