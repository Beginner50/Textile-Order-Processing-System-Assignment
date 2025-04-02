package tops.forms;

import tops.components.Table;

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
import java.text.NumberFormat;

public class TransportChargeForm extends JDialog {
    private final Connection conn;
    private final JFrame parentFrame;
    
    private JTextField transportIdField;
    private JTextField transportNameField;
    private JFormattedTextField chargePerDistanceField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;
    
    public TransportChargeForm(Connection conn, Object[] data, Table parentTable, JFrame parentFrame) {
        this.conn = conn;
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);
        this.parentFrame = parentFrame;
        
        setTitle(isEditMode ? "Edit Transport Charge" : "Create Transport Charge");
        setSize(400, 250);
        setLocationRelativeTo(parentFrame);
        setModal(true);
        setLayout(new BorderLayout());
        
        // Main content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel transportPanel = new JPanel(new GridBagLayout());
        transportPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Transport Charge Information",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Sans-Serif", Font.BOLD, 12)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create currency formatter
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setMinimum(0.0);
        currencyFormatter.setAllowsInvalid(false);
        
        // Transport ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        transportPanel.add(new JLabel("Transport ID:"), gbc);
        
        transportIdField = new JTextField();
        transportIdField.setToolTipText("Unique identifier for this transport charge");
        transportIdField.setEditable(false); // ID is auto-generated or read-only in edit mode
        if (isEditMode) {
            transportIdField.setText(data[0].toString());
        } else {
            transportIdField.setText("Auto-generated");
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        transportPanel.add(transportIdField, gbc);
        
        // Transport Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        transportPanel.add(new JLabel("Transport Name:"), gbc);
        
        transportNameField = new JTextField();
        transportNameField.setToolTipText("Name of transport service");
        if (isEditMode) {
            transportNameField.setText(data[1].toString());
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        transportPanel.add(transportNameField, gbc);
        
        // Charge Per Distance
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        transportPanel.add(new JLabel("Charge Per Distance:"), gbc);
        
        chargePerDistanceField = new JFormattedTextField(currencyFormatter);
        chargePerDistanceField.setToolTipText("Cost per unit of distance");
        if (isEditMode) {
            chargePerDistanceField.setValue(Double.parseDouble(data[2].toString()));
        } else {
            chargePerDistanceField.setValue(0.0);
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        transportPanel.add(chargePerDistanceField, gbc);
        
        // Add transport panel to content
        contentPanel.add(transportPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTransportCharge();
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
    
    private void saveTransportCharge() {
        try {
            // Validate inputs
            if (transportNameField.getText().trim().isEmpty() ||
                    chargePerDistanceField.getValue() == null) {
                
                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get values
            String transportName = transportNameField.getText().trim();
            double chargePerDistance = ((Number)chargePerDistanceField.getValue()).doubleValue();
            
            if (isEditMode) {
                int transportId = Integer.parseInt(transportIdField.getText());
                updateTransportChargeInDatabase(transportId, transportName, chargePerDistance);
            } else {
                insertTransportChargeIntoDatabase(transportName, chargePerDistance);
            }
            
            // Refresh the transport charges table
            if (parentFrame instanceof tops.ISAdminScreen) {
                ((tops.ISAdminScreen) parentFrame).populateTransportChargesTable();
            }
            
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving transport charge: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void insertTransportChargeIntoDatabase(String transportName, double chargePerDistance) throws SQLException {
        String query = "INSERT INTO Transport_Charges (transport_name, charge_per_distance) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transportName);
            stmt.setDouble(2, chargePerDistance);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int transportId = generatedKeys.getInt(1);
                    
                    // Add to table
                    Object[] rowData = {
                            transportId,
                            transportName,
                            chargePerDistance
                    };
                    parentTable.addRow(rowData);
                    
                    JOptionPane.showMessageDialog(this,
                            "Transport charge added successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private void updateTransportChargeInDatabase(int transportId, String transportName, double chargePerDistance) throws SQLException {
        String query = "UPDATE Transport_Charges SET transport_name = ?, charge_per_distance = ? WHERE TransportId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, transportName);
            stmt.setDouble(2, chargePerDistance);
            stmt.setInt(3, transportId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update the table row
                int selectedRow = parentTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                
                Object[] rowData = {
                        transportId,
                        transportName,
                        chargePerDistance
                };
                
                for (int i = 0; i < rowData.length; i++) {
                    model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(selectedRow), i);
                }
                
                JOptionPane.showMessageDialog(this,
                        "Transport charge updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No transport charge was updated. The record may no longer exist.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
} 