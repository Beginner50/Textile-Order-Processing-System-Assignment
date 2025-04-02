package tops;

import tops.components.Toolbar;
import tops.components.TabbedTablePane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AbstractCustomScreen extends JFrame {
    private JPanel topPanel;
    private JLabel applicationName;

    private JPanel mainPanel;

    protected Toolbar toolbar;
    protected TabbedTablePane tabbedTablePane;

    protected String selectedTab;

    public AbstractCustomScreen() {
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

        // Create and add the toolbar
        toolbar = new Toolbar();
        toolbar.searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tabbedTablePane.getTableFromTab(selectedTab).filterByRegex(toolbar.searchBar.getText());
            }
        });
        mainPanel.add(toolbar, BorderLayout.NORTH);

        // Create a TabbedPane containing a table for every tab
        tabbedTablePane = new TabbedTablePane();
        tabbedTablePane.addChangeListener(new TabbedPaneListener());
        tabbedTablePane.setBorder(new EmptyBorder(30, 60, 50, 60)); // Padding

        mainPanel.add(tabbedTablePane, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(mainPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class TabbedPaneListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            int selectedIndex = tabbedTablePane.getSelectedIndex();
            selectedTab = tabbedTablePane.getTitleAt(selectedIndex);

            toolbar.loadConfiguration(selectedTab);
        }
    }

    public TabbedTablePane getTabbedTablePane() {
        return tabbedTablePane;
    }
}
