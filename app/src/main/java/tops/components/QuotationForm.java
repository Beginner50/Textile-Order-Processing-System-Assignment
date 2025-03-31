package tops.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class QuotationForm extends JDialog {
    private JTextField quotationNoField;
    private JComboBox<String> itemNoComboBox;
    private JSpinner qtySpinner;
    private JComboBox<String> clientComboBox;
    private JFormattedTextField priceField;
    private JFormattedTextField transportCostsField;
    private JFormattedTextField totalCostsField;
    private JSpinner quotationDateSpinner;

    private JButton calculateButton;
    private JButton saveButton;
    private JButton cancelButton;

    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;
    private int editRowIndex;
    private Connection conn;

    public QuotationForm(Object[] data, Table parentTable) {
        this(data, parentTable, -1);
    }

    public QuotationForm(Object[] data, Table parentTable, int rowIndex) {
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);
        this.editRowIndex = rowIndex;

        setTitle(isEditMode ? "Edit Quotation" : "Create Quotation");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // Get database connection from parent's ancestor
        Window windowAncestor = SwingUtilities.getWindowAncestor(parentTable);
        if (windowAncestor instanceof tops.ISAdminScreen) {
            conn = ((tops.ISAdminScreen) windowAncestor).getConnection();
        }

        // Main content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create numerical formatters
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setMinimum(0.0);
        currencyFormatter.setAllowsInvalid(false);
        
        // Quotation Information Section
        JPanel quotationInfoPanel = new JPanel(new GridBagLayout());
        quotationInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Quotation Information", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        // Quotation No
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        quotationInfoPanel.add(new JLabel("Quotation No:"), gbc);
        
        quotationNoField = new JTextField();
        quotationNoField.setToolTipText("Unique identifier for this quotation");
        if (isEditMode) {
            quotationNoField.setText(data[0].toString());
            quotationNoField.setEditable(false);
        } else {
            // Generate a new quotation number
            quotationNoField.setText("QUO-" + System.currentTimeMillis() % 10000);
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        quotationInfoPanel.add(quotationNoField, gbc);
        
        // Quotation Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        quotationInfoPanel.add(new JLabel("Quotation Date:"), gbc);
        
        // Use JSpinner with SpinnerDateModel for date selection
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), 
                null, null, java.util.Calendar.DAY_OF_MONTH);
        quotationDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(quotationDateSpinner, "yyyy-MM-dd");
        quotationDateSpinner.setEditor(dateEditor);
        quotationDateSpinner.setToolTipText("Select the date for this quotation");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        quotationInfoPanel.add(quotationDateSpinner, gbc);
        
        // Item and Client Section
        JPanel itemClientPanel = new JPanel(new GridBagLayout());
        itemClientPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Item and Client", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        // Item No
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        itemClientPanel.add(new JLabel("Item:"), gbc);
        
        // Load items from database into combo box
        itemNoComboBox = new JComboBox<>();
        populateItemsComboBox();
        itemNoComboBox.setEditable(false);
        itemNoComboBox.setToolTipText("Select the item");
        itemNoComboBox.addActionListener(e -> {
            if (itemNoComboBox.getSelectedItem() != null) {
                String selectedValue = itemNoComboBox.getSelectedItem().toString();
                if (selectedValue != null && selectedValue.contains("-")) {
                    // Extract item price from the selected item
                    try {
                        int itemId = Integer.parseInt(selectedValue.split("-")[0].trim());
                        double price = getItemPrice(itemId);
                        priceField.setValue(price);
                        calculateTotal();
                    } catch (Exception ex) {
                        // Ignore, just don't update price
                    }
                }
            }
        });
        
        if (isEditMode && data[1] != null) {
            // Try to select the correct item in the combo box
            int itemId = Integer.parseInt(data[1].toString());
            String itemName = getItemName(itemId); 
            if (itemName != null) {
                String displayText = itemId + " - " + itemName;
                itemNoComboBox.setSelectedItem(displayText);
            }
        }
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        itemClientPanel.add(itemNoComboBox, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        itemClientPanel.add(new JLabel("Quantity:"), gbc);
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        qtySpinner.setToolTipText("Number of items for quotation");
        if (isEditMode && data[2] != null)
            qtySpinner.setValue(Integer.parseInt(data[2].toString()));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        itemClientPanel.add(qtySpinner, gbc);
        
        // Client Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        itemClientPanel.add(new JLabel("Client:"), gbc);
        
        // Use a combo box for client selection
        clientComboBox = new JComboBox<>();
        populateClientComboBox();
        clientComboBox.setEditable(true);
        clientComboBox.setToolTipText("Select or enter client name");
        if (isEditMode && data[3] != null)
            clientComboBox.setSelectedItem(data[3].toString());
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        itemClientPanel.add(clientComboBox, gbc);
        
        // Pricing Section
        JPanel pricingPanel = new JPanel(new GridBagLayout());
        pricingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Pricing", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        // Price
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        pricingPanel.add(new JLabel("Item Cost:"), gbc);
        
        priceField = new JFormattedTextField(currencyFormatter);
        priceField.setColumns(10);
        priceField.setToolTipText("Item cost (excluding transport)");
        if (isEditMode && data[4] != null)
            priceField.setValue(Double.parseDouble(data[4].toString()));
        else
            priceField.setValue(0.0);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pricingPanel.add(priceField, gbc);
        
        // Transport Costs
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        pricingPanel.add(new JLabel("Transport Costs:"), gbc);
        
        transportCostsField = new JFormattedTextField(currencyFormatter);
        transportCostsField.setColumns(10);
        transportCostsField.setToolTipText("Cost of transporting the items");
        if (isEditMode && data[5] != null)
            transportCostsField.setValue(Double.parseDouble(data[5].toString()));
        else
            transportCostsField.setValue(0.0);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pricingPanel.add(transportCostsField, gbc);
        
        // Calculate Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        calculateButton = new JButton("Calculate Total");
        calculateButton.setToolTipText("Calculate the total costs");
        calculateButton.addActionListener(e -> calculateTotal());
        pricingPanel.add(calculateButton, gbc);
        
        // Total Costs
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        pricingPanel.add(new JLabel("Total Costs:"), gbc);
        
        totalCostsField = new JFormattedTextField(currencyFormatter);
        totalCostsField.setEditable(false);
        totalCostsField.setBackground(new Color(240, 240, 240));
        totalCostsField.setToolTipText("Total cost including items and transport");
        if (isEditMode && data[6] != null)
            totalCostsField.setValue(Double.parseDouble(data[6].toString()));
        else
            totalCostsField.setValue(0.0);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pricingPanel.add(totalCostsField, gbc);
        
        // Add all sections to the main form
        JPanel formSectionsPanel = new JPanel();
        formSectionsPanel.setLayout(new BoxLayout(formSectionsPanel, BoxLayout.Y_AXIS));
        formSectionsPanel.add(quotationInfoPanel);
        formSectionsPanel.add(Box.createVerticalStrut(10));
        formSectionsPanel.add(itemClientPanel);
        formSectionsPanel.add(Box.createVerticalStrut(10));
        formSectionsPanel.add(pricingPanel);
        
        contentPanel.add(formSectionsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuotation();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main content to dialog
        add(contentPanel, BorderLayout.CENTER);
        
        // Calculate the initial total
        calculateTotal();
    }

    private void calculateTotal() {
        try {
            double price = ((Number)priceField.getValue()).doubleValue();
            int qty = (int) qtySpinner.getValue();
            double transportCosts = ((Number)transportCostsField.getValue()).doubleValue();
            double totalCosts = (price * qty) + transportCosts;
            
            totalCostsField.setValue(totalCosts);
        } catch (Exception e) {
            // In case of parsing errors, don't update the total
        }
    }

    private void saveQuotation() {
        try {
            // Validate inputs
            if (quotationNoField.getText().trim().isEmpty() ||
                    itemNoComboBox.getSelectedItem() == null ||
                    (int) qtySpinner.getValue() <= 0 ||
                    clientComboBox.getSelectedItem() == null ||
                    clientComboBox.getSelectedItem().toString().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ensure total has been calculated
            calculateTotal();
            
            // Get values
            String quotationNo = quotationNoField.getText().trim();
            String selectedItem = itemNoComboBox.getSelectedItem().toString();
            int itemNoInt = Integer.parseInt(selectedItem.split("-")[0].trim());
            int qty = (int) qtySpinner.getValue();
            String clientName = clientComboBox.getSelectedItem().toString().trim();
            double price = ((Number)priceField.getValue()).doubleValue();
            double transportCosts = ((Number)transportCostsField.getValue()).doubleValue();
            double totalCosts = ((Number)totalCostsField.getValue()).doubleValue();
            
            LocalDate quotationDate = ((Date) quotationDateSpinner.getValue()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

            // Get a connection if not already available
            if (conn == null) {
                Window windowAncestor = SwingUtilities.getWindowAncestor(parentTable);
                if (windowAncestor instanceof tops.ISAdminScreen) {
                    conn = ((tops.ISAdminScreen) windowAncestor).getConnection();
                }
            }
            
            if (conn == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not get database connection",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert clientName to CustomerId
            int customerId = getCustomerIdByName(conn, clientName);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Customer not found: " + clientName,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create data array - make sure this matches the expected table columns
            // Based on QUOTATION_TABLE_COLUMNS = { "QuotationNo", "ItemNo", "CustomerId", "Quantity", "Transport Costs", "Item Costs", "Total Costs", "Date Created"}
            Object[] rowData = {
                    quotationNo,
                    itemNoInt,
                    customerId,  // Use the numeric customerId, not the name
                    qty,
                    transportCosts,
                    price,
                    totalCosts,
                    quotationDate
            };

            // Save/update in database
            boolean success;
            if (isEditMode) {
                // Update existing quotation in database
                success = updateQuotationInDatabase(quotationNo, itemNoInt, customerId, qty, 
                                                    price, transportCosts, totalCosts, quotationDate);
                
                if (success) {
                    // Update existing row in table with IDs (not names)
                    DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                    for (int i = 0; i < rowData.length; i++) {
                        model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(editRowIndex), i);
                    }
                }
            } else {
                // Add new quotation to database
                success = insertQuotationIntoDatabase(itemNoInt, customerId, qty, 
                                                     price, transportCosts, totalCosts, quotationDate);
                
                if (success) {
                    // Add new row to table with IDs (not names)
                    parentTable.addRow(rowData);
                }
            }
            
            if (success) {
            dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving quotation: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Populates the item combo box with items from the database
     */
    private void populateItemsComboBox() {
        if (conn == null) return;
        
        try {
            String query = "SELECT ItemNo, name, cost_price FROM Items";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                itemNoComboBox.removeAllItems();
                
                while (rs.next()) {
                    int itemNo = rs.getInt("ItemNo");
                    String itemName = rs.getString("name");
                    double price = rs.getDouble("cost_price");
                    
                    itemNoComboBox.addItem(itemNo + " - " + itemName + " ($" + price + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Populates the client combo box with customers from the database
     */
    private void populateClientComboBox() {
        if (conn == null) return;
        
        try {
            String query = "SELECT CustomerId, name FROM Customers";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                clientComboBox.removeAllItems();
                
                while (rs.next()) {
                    String clientName = rs.getString("name");
                    clientComboBox.addItem(clientName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the item price based on item ID
     */
    private double getItemPrice(int itemId) {
        if (conn == null) return 0.0;
        
        try {
            String query = "SELECT cost_price FROM Items WHERE ItemNo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getDouble("cost_price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Get the item name based on item ID
     */
    private String getItemName(int itemId) {
        if (conn == null) return null;
        
        try {
            String query = "SELECT name, cost_price FROM Items WHERE ItemNo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String name = rs.getString("name");
                    double price = rs.getDouble("cost_price");
                    return itemId + " - " + name + " ($" + price + ")";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private int getCustomerIdByName(Connection conn, String customerName) {
        try {
            String query = "SELECT CustomerId FROM Customers WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, customerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("CustomerId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Customer not found
    }
    
    private boolean insertQuotationIntoDatabase(int itemNo, int customerId, int quantity,
                                          double itemCosts, double transportCosts, double totalCosts,
                                          LocalDate dateCreated) {
        try {
            String query = "INSERT INTO Quotations (ItemNo, CustomerId, quantity, transport_costs, "
                          + "item_costs, total_costs, date_created) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemNo);
                stmt.setInt(2, customerId);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, transportCosts);
                stmt.setDouble(5, itemCosts);
                stmt.setDouble(6, totalCosts);
                stmt.setDate(7, java.sql.Date.valueOf(dateCreated));
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Quotation added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean updateQuotationInDatabase(String quotationNo, int itemNo, int customerId, int quantity,
                                        double itemCosts, double transportCosts, double totalCosts,
                                        LocalDate dateCreated) {
        try {
            String query = "UPDATE Quotations SET ItemNo = ?, CustomerId = ?, quantity = ?, "
                          + "transport_costs = ?, item_costs = ?, total_costs = ?, date_created = ? "
                          + "WHERE QuotationNo = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemNo);
                stmt.setInt(2, customerId);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, transportCosts);
                stmt.setDouble(5, itemCosts);
                stmt.setDouble(6, totalCosts);
                stmt.setDate(7, java.sql.Date.valueOf(dateCreated));
                stmt.setInt(8, Integer.parseInt(quotationNo));
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Quotation updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
}