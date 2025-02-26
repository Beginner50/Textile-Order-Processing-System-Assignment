import Components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainScreen extends JFrame {
    private JPanel topPanel;
    private JLabel applicationName;

    private JPanel mainPanel;
    private Toolbar toolbar;
    private TabbedTablePane tabbedTablePane;

    private final static String[] QUOTATION_TABLE_COLUMNS = { "QuotationNo", "ItemNo", "Qty", "Client Name", "Price",
            "Transport Costs", "Total Costs" };
    private final static String[] ORDER_TABLE_COLUMNS = { "OrderNo", "ItemNo", "Qty", "Client Name", "Price",
            "Transport Costs", "Total Costs" };
    private final static String[] BILL_TABLE_COLUMNS = { "BillNo", "OrderNo", "Client Name", "Billing Price" };

    public MainScreen() {
        super("Order Screen");
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(0, 0));

        // Top
        topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 7));

        applicationName = new JLabel("Textile Order Processing System");
        applicationName.setFont(new Font("Sans serif", Font.BOLD, 22));
        applicationName.setForeground(Color.WHITE);

        topPanel.add(applicationName);

        // Main
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 0));

        // Create the toolbar
        toolbar = new Toolbar();

        // Create a TabbedPane containing a table for every tab
        tabbedTablePane = new TabbedTablePane();
        tabbedTablePane.addChangeListener(new TabbedPaneListener());
        tabbedTablePane.setBorder(new EmptyBorder(30, 60, 50, 60)); // Padding

        // Initialise data in the tables
        tabbedTablePane.addTabbedTable("Quotation", QUOTATION_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Order", ORDER_TABLE_COLUMNS);
        tabbedTablePane.addTabbedTable("Bill", BILL_TABLE_COLUMNS);

        Table quotationTable = tabbedTablePane.getTableFromTab("Quotation");
        quotationTable.addRow(new Object[] { "1", "2", "5", "Charles", 2500, 700, 3200 });

        // Load the default configuration of the toolbar: Quotation
        toolbar.loadConfiguration("Quotation", tabbedTablePane.getTableFromTab("Quotation"));

        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(tabbedTablePane, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(mainPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class TabbedPaneListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            int selectedIndex = tabbedTablePane.getSelectedIndex();
            String selectedTab = tabbedTablePane.getTitleAt(selectedIndex);

            // selectedTab E {Quotation, Order, Bill}
            toolbar.loadConfiguration(selectedTab, tabbedTablePane.getTableFromTab(selectedTab));
        }
    }

    public TabbedTablePane getTabbedTablePane() {
        return tabbedTablePane;
    }
}
