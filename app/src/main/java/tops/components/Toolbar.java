package tops.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Toolbar extends JPanel {
    private JPanel searchPanel;
    public JTextField searchBar;
    public JButton searchButton;

    private JPanel tabButtonsPanel;
    protected HashMap<String, HashMap<String, JButton>> buttonsByTab;

    public Toolbar() {
        super();
        this.setBackground(Color.lightGray);
        this.setLayout(new BorderLayout());

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.lightGray);

        searchBar = new JTextField("", 40);
        searchButton = ButtonFactory.createButton("Search", "search.png");
        searchButton.setVisible(true);

        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        tabButtonsPanel = new JPanel();
        tabButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        tabButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        tabButtonsPanel.setBackground(Color.lightGray);

        buttonsByTab = new HashMap<>();

        this.add(searchPanel, BorderLayout.WEST);
        this.add(tabButtonsPanel, BorderLayout.EAST);
    }

    public void loadConfiguration(String tabName){
        // Hide buttons for all tabs
        for (var buttons : buttonsByTab.values())
            for(JButton button : buttons.values())
                button.setVisible(false);

        // Show buttons for current tab if any
        if(buttonsByTab.containsKey(tabName))
            for(JButton tabButton : buttonsByTab.get(tabName).values())
                tabButton.setVisible(true);
    }

    public void addTabButton(String tabName, String buttonName, JButton button, ActionListener buttonHandler){
        if(!buttonsByTab.containsKey(tabName))
            buttonsByTab.put(tabName, new HashMap<>());
        buttonsByTab.get(tabName).put(buttonName, button);

        tabButtonsPanel.add(button);
        button.addActionListener(buttonHandler);
    }

    public void removeTabButton(String tabName, String buttonName){
        buttonsByTab.get(tabName).remove(buttonName);
        if(buttonsByTab.get(tabName).isEmpty()) buttonsByTab.remove(tabName);
    }

    public JButton getTabButton(String buttonName){
        for(var buttons : buttonsByTab.values())
            if(buttons.containsKey(buttonName))
                return buttons.get(buttonName);
        return null;
    }
}

