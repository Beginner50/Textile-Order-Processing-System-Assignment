package tops.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;

public abstract class AbstractToolbar extends JPanel {
    private ActionListener searchButtonHandler;

    private JPanel searchPanel;
    private JTextField searchBar;
    private JButton searchButton;

    private JPanel crudButtonsPanel;
    protected HashMap<String, JButton> crudButtons;

    protected Table currentTable;

    public AbstractToolbar() {
        super();
        this.setBackground(Color.lightGray);
        this.setLayout(new BorderLayout());

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.lightGray);

        searchBar = new JTextField("", 40);
        searchButton = new JButton("", new ImageIcon( ButtonFactory.class.getClassLoader().getResource("search.png")));
        searchButtonHandler = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentTable.filterByRegex(searchBar.getText());
            }
        };
        searchButton.addActionListener(searchButtonHandler);
        searchButton.setToolTipText("Search");

        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        crudButtonsPanel = new JPanel();
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        crudButtonsPanel.setBackground(Color.lightGray);

        crudButtons = new HashMap<>();

        this.add(searchPanel, BorderLayout.WEST);
        this.add(crudButtonsPanel, BorderLayout.EAST);
    }

    public void loadConfiguration(String tabName, Table table){
        currentTable = table;

        // Hide all crud buttons
        for (JButton toolbarButton : crudButtons.values())
            toolbarButton.setVisible(false);
    }

    public void addCrudButton(String buttonName, JButton button, ActionListener buttonHandler){
        crudButtons.put(buttonName, button);
        crudButtonsPanel.add(button);
        button.addActionListener(buttonHandler);
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }

    static class ButtonFactory {
        public static JButton createButton(String tooltipText, String imagePath) {
            URL imageUrl = ButtonFactory.class.getClassLoader().getResource(imagePath);
            if (imageUrl == null) {
                throw new RuntimeException("Image not found:" + imagePath);
            }
            ImageIcon icon = new ImageIcon(imageUrl);
            JButton button = new JButton("", new ImageIcon(imageUrl));
            button.setToolTipText(tooltipText);
            button.setVisible(false);
            return button;
        }

    }
}
