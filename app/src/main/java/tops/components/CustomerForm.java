package tops.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerForm extends JDialog {
    private final Connection conn;
    private final JFrame parentFrame;
    
    private JTextField customerIdField;
    private JTextField nameField;
    private JTextArea addressArea;
    private JTextField contactField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;
    
    public CustomerForm(Connection conn, Object[] data, Table parentTable, JFrame parentFrame) {
        this.conn = conn;
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);
        this.parentFrame = parentFrame;
        
        setTitle(isEditMode ? "Edit Customer" : "Create Customer");
        setSize(450, 350);
        setLocationRelativeTo(parentFrame);
        setModal(true);
        setLayout(new BorderLayout());
        
        // Main content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Customer Information",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Customer ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        customerPanel.add(new JLabel("Customer ID:"), gbc);
        
        customerIdField = new JTextField();
        customerIdField.setToolTipText("Unique identifier for this customer");
        customerIdField.setEditable(false); // ID is auto-generated or read-only in edit mode
        if (isEditMode) {
            customerIdField.setText(data[0].toString());
        } else {
            customerIdField.setText("Auto-generated");
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerPanel.add(customerIdField, gbc);
        
        // Customer Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        customerPanel.add(new JLabel("Name:"), gbc);
        
        nameField = new JTextField();
        nameField.setToolTipText("Full name of the customer");
        if (isEditMode) {
            nameField.setText(data[1].toString());
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerPanel.add(nameField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        customerPanel.add(new JLabel("Address:"), gbc);
        
        addressArea = new JTextArea(3, 20);
        addressArea.setToolTipText("Full address of the customer");
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        if (isEditMode) {
            addressArea.setText(data[2].toString());
        }
        
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerPanel.add(addressScrollPane, gbc);
        
        // Contact
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        customerPanel.add(new JLabel("Contact:"), gbc);
        
        contactField = new JTextField();
        contactField.setToolTipText("Phone number or email address");
        if (isEditMode) {
            contactField.setText(data[3].toString());
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerPanel.add(contactField, gbc);
        
        // Add customer panel to content
        contentPanel.add(customerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
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
        
        // Add the main content panel to the dialog
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void saveCustomer() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                    addressArea.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get values
            String name = nameField.getText().trim();
            String address = addressArea.getText().trim();
            String contact = contactField.getText().trim();
            
            if (isEditMode) {
                int customerId = Integer.parseInt(customerIdField.getText());
                updateCustomerInDatabase(customerId, name, address, contact);
            } else {
                insertCustomerIntoDatabase(name, address, contact);
            }
            
            // Refresh the customer table
            if (parentFrame instanceof tops.ISAdminScreen) {
                ((tops.ISAdminScreen) parentFrame).populateCustomerTable();
            }
            
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void insertCustomerIntoDatabase(String name, String address, String contact) throws SQLException {
        String query = "INSERT INTO Customers (name, address, contact) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, contact);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);
                    
                    // Add to table
                    Object[] rowData = {
                            customerId,
                            name,
                            address,
                            contact
                    };
                    parentTable.addRow(rowData);
                    
                    JOptionPane.showMessageDialog(this,
                            "Customer added successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private void updateCustomerInDatabase(int customerId, String name, String address, String contact) throws SQLException {
        String query = "UPDATE Customers SET name = ?, address = ?, contact = ? WHERE CustomerId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, contact);
            stmt.setInt(4, customerId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update the table row
                int selectedRow = parentTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                
                Object[] rowData = {
                        customerId,
                        name,
                        address,
                        contact
                };
                
                for (int i = 0; i < rowData.length; i++) {
                    model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(selectedRow), i);
                }
                
                JOptionPane.showMessageDialog(this,
                        "Customer updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No customer was updated. The record may no longer exist.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
} 