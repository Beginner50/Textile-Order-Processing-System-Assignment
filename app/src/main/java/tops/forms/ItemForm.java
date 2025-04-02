package tops.forms;

import tops.components.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemForm extends JDialog {
    private final Connection conn;
    private final JFrame parentFrame;
    
    private JTextField itemNoField;
    private JTextField nameField;
    private JTextField categoryField;
    private JTextField sizeField;
    private JTextField costPriceField;
    private JSpinner stockLevelSpinner;
    private JSpinner reorderThresholdSpinner;

    private JButton saveButton;
    private JButton cancelButton;

    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;

    public ItemForm(Connection conn, Object[] data, Table parentTable, JFrame parentFrame) {
        this.conn = conn;
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);
        this.parentFrame = parentFrame;

        setTitle(isEditMode ? "Edit Item" : "Create Item");
        setSize(400, 450);
        setLocationRelativeTo(parentFrame);
        setModal(true);
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Item No
        formPanel.add(new JLabel("Item No:"));
        itemNoField = new JTextField();
        if (isEditMode) {
            itemNoField.setText(data[0].toString());
            itemNoField.setEditable(false);
        }
        formPanel.add(itemNoField);

        // Name
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        if (isEditMode) {
            nameField.setText(data[1].toString());
        }
        formPanel.add(nameField);

        // Category
        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        if (isEditMode) {
            categoryField.setText(data[2].toString());
        }
        formPanel.add(categoryField);

        // Size
        formPanel.add(new JLabel("Size:"));
        sizeField = new JTextField();
        if (isEditMode) {
            sizeField.setText(data[3].toString());
        }
        formPanel.add(sizeField);

        // Cost Price
        formPanel.add(new JLabel("Cost Price:"));
        costPriceField = new JTextField();
        if (isEditMode) {
            costPriceField.setText(data[4].toString());
        }
        formPanel.add(costPriceField);

        // Stock Level
        formPanel.add(new JLabel("Stock Level:"));
        stockLevelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        if (isEditMode) {
            stockLevelSpinner.setValue(data[5]);
        }
        formPanel.add(stockLevelSpinner);

        // Reorder Threshold
        formPanel.add(new JLabel("Reorder Threshold:"));
        reorderThresholdSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        if (isEditMode) {
            reorderThresholdSpinner.setValue(data[6]);
        }
        formPanel.add(reorderThresholdSpinner);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveItem();
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

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveItem() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                    categoryField.getText().trim().isEmpty() ||
                    sizeField.getText().trim().isEmpty() ||
                    costPriceField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse numeric values
            double costPrice = Double.parseDouble(costPriceField.getText().trim());
            int stockLevel = (int) stockLevelSpinner.getValue();
            int reorderThreshold = (int) reorderThresholdSpinner.getValue();

            if (isEditMode) {
                // Update existing item in database
                updateItemInDatabase(
                    Integer.parseInt(itemNoField.getText()),
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    sizeField.getText().trim(),
                    costPrice,
                    stockLevel,
                    reorderThreshold
                );
            } else {
                // Insert new item into database
                insertItemIntoDatabase(
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    sizeField.getText().trim(),
                    costPrice,
                    stockLevel,
                    reorderThreshold
                );
            }

            // Refresh the item table data
            if (parentFrame instanceof tops.InventoryOfficerScreen) {
                ((tops.InventoryOfficerScreen) parentFrame).loadItemsData();
            }

            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void insertItemIntoDatabase(String name, String category, String size, 
                                       double costPrice, int stockLevel, int reorderThreshold) 
                                       throws SQLException {
        String query = "INSERT INTO Items (name, category, size, cost_price, stock_level, reorder_threshold) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, size);
            stmt.setDouble(4, costPrice);
            stmt.setInt(5, stockLevel);
            stmt.setInt(6, reorderThreshold);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Item added successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void updateItemInDatabase(int itemNo, String name, String category, String size,
                                     double costPrice, int stockLevel, int reorderThreshold) 
                                     throws SQLException {
        String query = "UPDATE Items SET name = ?, category = ?, size = ?, " +
                       "cost_price = ?, stock_level = ?, reorder_threshold = ? " +
                       "WHERE ItemNo = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, size);
            stmt.setDouble(4, costPrice);
            stmt.setInt(5, stockLevel);
            stmt.setInt(6, reorderThreshold);
            stmt.setInt(7, itemNo);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Item updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
} 