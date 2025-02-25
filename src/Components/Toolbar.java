package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Toolbar extends JPanel {
    private ButtonHandler buttonHandler;

    private JPanel searchPanel;
    private JTextField searchBar;
    private JButton searchButton;

    private JPanel crudButtonsPanel;
    private HashMap<String, ToolbarButton> crudButtons;

    private Table currentTable;

    public Toolbar(){
        super();
        this.setBackground(Color.lightGray);
        this.setLayout(new BorderLayout());
        buttonHandler = new ButtonHandler();

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.lightGray);

        searchBar = new JTextField("",40);
        searchButton = new JButton("", new ImageIcon("search-interface-symbol.png"));
        searchButton.addActionListener(buttonHandler);
        searchButton.setToolTipText("Search" );

        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        crudButtonsPanel = new JPanel();
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        crudButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        crudButtonsPanel.setBackground(Color.lightGray);

        crudButtons = new HashMap<>();
        crudButtons.put("CreateQuotation", new ToolbarButton("Add Quotation", "add.png"));
        crudButtons.put("UpdateQuotation", new ToolbarButton("Edit Quotation", "edit.png"));
        crudButtons.put("DeleteQuotation", new ToolbarButton("Delete Quotation", "delete.png"));
        crudButtons.put("CreateOrder", new ToolbarButton("Add Order", "add.png"));
        crudButtons.put("UpdateOrder", new ToolbarButton("Edit Order", "edit.png"));

        for(ToolbarButton button : crudButtons.values()){
            button.addActionListener(buttonHandler);
            crudButtonsPanel.add(button);
        }

        this.add(searchPanel, BorderLayout.WEST);
        this.add(crudButtonsPanel, BorderLayout.EAST);
    }

    public void loadConfiguration(String tabName, Table table){
        // Hide all crud buttons first
        for(ToolbarButton toolbarButton : crudButtons.values())
            toolbarButton.setVisible(false);

        switch (tabName){
            case "Quotation":
                crudButtons.get("CreateQuotation").setVisible(true);
                crudButtons.get("UpdateQuotation").setVisible(true);
                crudButtons.get("DeleteQuotation").setVisible(true);
                break;
            case "Order":
                crudButtons.get("CreateOrder").setVisible(true);
                crudButtons.get("UpdateOrder").setVisible(true);
                break;
            case "Bill":
                break;
            default:
                break;
        }
        currentTable = table;
    }

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            if (source == searchButton){
                currentTable.filterByRegex(searchBar.getText());
            }
            else if (source == crudButtons.get("CreateQuotation")){
                System.out.println("Create Quotation Button clicked!");
            }
            else if (source == crudButtons.get("UpdateQuotation")){
                System.out.println("Update Quotation Button clicked!");
            }
            else if(source == crudButtons.get("DeleteQuotation")){
                System.out.println("Delete Quotation Button clicked!");
            }
            else if(source == crudButtons.get("CreateOrder")){
                System.out.println("Create Order Button clicked!");
            }
            else if(source == crudButtons.get("UpdateOrder")){
                System.out.println("Update Order Button clicked!");
            }
        }
    }

    class ToolbarButton extends JButton{
        public ToolbarButton(String tooltipText, String imagePath){
            super("", new ImageIcon(imagePath));
            this.setToolTipText(tooltipText);
            this.setVisible(false);
        }
    }
}
