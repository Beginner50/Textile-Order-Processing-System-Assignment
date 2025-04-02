package tops.forms;

import tops.components.TabbedTablePane;
import tops.components.Table;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OrderForm extends JDialog {
    private JTextField orderNoField;
    private JTextField itemNoField;
    private JSpinner qtySpinner;
    private JComboBox<String> clientComboBox;
    private JFormattedTextField priceField;
    private JFormattedTextField transportCostsField;
    private JFormattedTextField totalCostsField;
    private JSpinner orderDateSpinner;
    private JComboBox<String> statusComboBox;

    private JButton calculateButton;
    private JButton saveButton;
    private JButton cancelButton;

    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;

    public OrderForm(Object[] data, Table parentTable) {
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);

        setTitle(isEditMode ? "Edit Order" : "Create Order");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

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
        
        // Order Information Section
        JPanel orderInfoPanel = new JPanel(new GridBagLayout());
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Order Information", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        // Order No
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        orderInfoPanel.add(new JLabel("Order No:"), gbc);
        
        orderNoField = new JTextField();
        orderNoField.setToolTipText("Unique identifier for this order");
        if (isEditMode) {
            orderNoField.setText(data[0].toString());
            orderNoField.setEditable(false);
        } else {
            // Generate a new order number
            orderNoField.setText("ORD-" + System.currentTimeMillis() % 10000);
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        orderInfoPanel.add(orderNoField, gbc);
        
        // Order Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        orderInfoPanel.add(new JLabel("Order Date:"), gbc);
        
        // Use JSpinner with SpinnerDateModel for date selection
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), 
                null, null, java.util.Calendar.DAY_OF_MONTH);
        orderDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(orderDateSpinner, "yyyy-MM-dd");
        orderDateSpinner.setEditor(dateEditor);
        orderDateSpinner.setToolTipText("Select the date for this order");
        
        // If in edit mode and we have a date, use it
        if (isEditMode && data.length > 7 && data[7] != null) {
            try {
                if (data[7] instanceof LocalDate) {
                    LocalDate localDate = (LocalDate) data[7];
                    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    orderDateSpinner.setValue(date);
                } else if (data[7] instanceof Date) {
                    orderDateSpinner.setValue(data[7]);
                }
            } catch (Exception e) {
                // In case of error, keep the default date
            }
        }
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        orderInfoPanel.add(orderDateSpinner, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        orderInfoPanel.add(new JLabel("Status:"), gbc);
        
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem("Pending");
        statusComboBox.setToolTipText("Current status of the order");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        orderInfoPanel.add(statusComboBox, gbc);
        
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
        itemClientPanel.add(new JLabel("Item No:"), gbc);
        
        itemNoField = new JTextField();
        itemNoField.setToolTipText("Enter the item number");
        if (isEditMode)
            itemNoField.setText(data[1].toString());
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        itemClientPanel.add(itemNoField, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        itemClientPanel.add(new JLabel("Quantity:"), gbc);
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        qtySpinner.setToolTipText("Number of items to order");
        if (isEditMode)
            qtySpinner.setValue(data[2]);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        itemClientPanel.add(qtySpinner, gbc);
        
        // Client Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        itemClientPanel.add(new JLabel("Client Name:"), gbc);
        
        // Use a combo box that can also be edited
        String[] clients = {"John Doe", "Jane Smith", "Acme Corp", "GlobalTech Ltd."};
        clientComboBox = new JComboBox<>(clients);
        clientComboBox.setEditable(true);
        clientComboBox.setToolTipText("Select or enter client name");
        if (isEditMode)
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
        pricingPanel.add(new JLabel("Price:"), gbc);
        
        priceField = new JFormattedTextField(currencyFormatter);
        priceField.setColumns(10);
        priceField.setToolTipText("Item price (excluding transport costs)");
        if (isEditMode)
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
        transportCostsField.setToolTipText("Cost of shipping the order");
        if (isEditMode)
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
        if (isEditMode)
            totalCostsField.setValue(Double.parseDouble(data[6].toString()));
        else
            totalCostsField.setValue(0.0);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pricingPanel.add(totalCostsField, gbc);
        
        // Add all sections to the main form
        JPanel formSectionsPanel = new JPanel();
        formSectionsPanel.setLayout(new BoxLayout(formSectionsPanel, BoxLayout.Y_AXIS));
        formSectionsPanel.add(orderInfoPanel);
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
                saveOrder();
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

    private void saveOrder() {
        try {
            // Validate inputs
            if (orderNoField.getText().trim().isEmpty() ||
                    itemNoField.getText().trim().isEmpty() ||
                    (int) qtySpinner.getValue() <= 0 ||
                    clientComboBox.getSelectedItem() == null ||
                    clientComboBox.getSelectedItem().toString().trim().isEmpty() ||
                    orderDateSpinner.getValue() == null) {

                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ensure total has been calculated
            calculateTotal();
            
            // Get values
            String orderNo = orderNoField.getText().trim();
            String itemNo = itemNoField.getText().trim();
            int qty = (int) qtySpinner.getValue();
            String clientName = clientComboBox.getSelectedItem().toString().trim();
            double price = ((Number)priceField.getValue()).doubleValue();
            double transportCosts = ((Number)transportCostsField.getValue()).doubleValue();
            double totalCosts = ((Number)totalCostsField.getValue()).doubleValue();
            String status = (String) statusComboBox.getSelectedItem();
            LocalDate orderDate = ((Date) orderDateSpinner.getValue()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

            // Get a connection from the parent table's window
            java.sql.Connection conn = null;
            Window windowAncestor = SwingUtilities.getWindowAncestor(parentTable);
            if (windowAncestor instanceof tops.ISAdminScreen) {
                conn = ((tops.ISAdminScreen) windowAncestor).getConnection();
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
            
            // Convert itemNo to integer
            int itemNoInt;
            try {
                itemNoInt = Integer.parseInt(itemNo);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Item number must be a valid number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create data array - make sure this matches the expected table columns
            Object[] rowData = {
                    orderNo,
                    itemNoInt,
                    customerId,
                    qty,
                    transportCosts,
                    price,
                    totalCosts,
                    orderDate,
                    status
            };

            // Save/update in database
            boolean success;
            if (isEditMode) {
                // Update existing order in database
                success = updateOrderInDatabase(conn, rowData);
                
                if (success) {
                    // Update existing row in table
                    int selectedRow = parentTable.getSelectedRow();
                    DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                    for (int i = 0; i < rowData.length; i++) {
                        model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(selectedRow), i);
                    }
                }
            } else {
                // Add new order to database
                success = insertOrderIntoDatabase(conn, rowData);
                
                if (success) {
                    // Add new row to table
                    parentTable.addRow(rowData);
                    
                    // Create a bill for this order
                    createBillForOrder(rowData);
                }
            }
            
            if (success) {
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private int getCustomerIdByName(java.sql.Connection conn, String customerName) {
        try {
            String query = "SELECT CustomerId FROM Customers WHERE name = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, customerName);
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("CustomerId");
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1; // Customer not found
    }
    
    private boolean insertOrderIntoDatabase(java.sql.Connection conn, Object[] orderData) {
        try {
            String query = "INSERT INTO Orders (ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, order_date, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, (int) orderData[1]);
                stmt.setInt(2, (int) orderData[2]);
                stmt.setInt(3, (int) orderData[3]);
                stmt.setDouble(4, (double) orderData[4]);
                stmt.setDouble(5, (double) orderData[5]);
                stmt.setDouble(6, (double) orderData[6]);
                
                // Convert LocalDate to java.sql.Date
                LocalDate localDate = (LocalDate) orderData[7];
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                stmt.setDate(7, sqlDate);
                
                stmt.setString(8, (String) orderData[8]);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get generated ID
                    java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        // Update the OrderNo in our data array
                        orderData[0] = orderId;
                        return true;
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean updateOrderInDatabase(java.sql.Connection conn, Object[] orderData) {
        try {
            String query = "UPDATE Orders SET ItemNo = ?, CustomerId = ?, quantity = ?, " +
                          "transport_costs = ?, item_costs = ?, total_costs = ?, " +
                          "order_date = ?, status = ? WHERE OrderNo = ?";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, (int) orderData[1]);
                stmt.setInt(2, (int) orderData[2]);
                stmt.setInt(3, (int) orderData[3]);
                stmt.setDouble(4, (double) orderData[4]);
                stmt.setDouble(5, (double) orderData[5]);
                stmt.setDouble(6, (double) orderData[6]);
                
                // Convert LocalDate to java.sql.Date
                LocalDate localDate = (LocalDate) orderData[7];
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                stmt.setDate(7, sqlDate);
                
                stmt.setString(8, (String) orderData[8]);
                stmt.setInt(9, Integer.parseInt(orderData[0].toString()));
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    private void createBillForOrder(Object[] orderData) {
        try {
            // Get the window ancestor and check if it's the main application window
            Window windowAncestor = SwingUtilities.getWindowAncestor(parentTable);
            
            java.sql.Connection conn = null;
            if (windowAncestor instanceof tops.ISAdminScreen) {
                conn = ((tops.ISAdminScreen) windowAncestor).getConnection();
            }
            
            if (conn == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not get database connection",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Try to find the TabbedTablePane by traversing the component hierarchy
            TabbedTablePane tabbedPane = null;
            if (windowAncestor != null) {
                // Search for TabbedTablePane in the component hierarchy
                for (Component comp : windowAncestor.getComponents()) {
                    tabbedPane = findTabbedTablePane(comp);
                    if (tabbedPane != null)
                        break;
                }
            }

            if (tabbedPane == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not locate the tabbed pane to create bill",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Table billTable = tabbedPane.getTableFromTab("Bills");

            if (billTable == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not locate the Bills table",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create bill data
            int orderNo = Integer.parseInt(orderData[0].toString());
            double billingPrice = (double) orderData[6]; // Total costs
            String status = "Pending"; // Set default status for new bills
            
            // Save bill to database
            String query = "INSERT INTO Bills (OrderNo, billing_price, status) VALUES (?, ?, ?)";
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, orderNo);
                stmt.setDouble(2, billingPrice);
                stmt.setString(3, status);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get the generated bill ID
                    java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int billNo = generatedKeys.getInt(1);
                        
                        // Add bill to bill table
                        billTable.addRow(new Object[] { billNo, orderNo, billingPrice, status });
                        
                        JOptionPane.showMessageDialog(this,
                                "Order and bill created successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to create bill in database",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating bill: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Helper method to find TabbedTablePane in component hierarchy
    private TabbedTablePane findTabbedTablePane(Component component) {
        if (component instanceof TabbedTablePane) {
            return (TabbedTablePane) component;
        }

        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                TabbedTablePane found = findTabbedTablePane(child);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}