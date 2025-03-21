package tops.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateQuotation extends JDialog {
    private JTextField quotationNoField;
    private JComboBox<String> itemNoField;
    private JSpinner qtySpinner;
    private JTextField clientNameField;
    private JFormattedTextField priceField;
    private JFormattedTextField transportCostsField;
    private JTextField totalCostsField;

    private JButton saveButton;
    private JButton cancelButton;

    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;

    public UpdateQuotation(Object[] data, Table parentTable) {
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);

        setTitle(isEditMode ? "Update Quotation" : "Create Quotation");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Quotation No:"));
        quotationNoField = new JTextField();
        if (isEditMode) {
            quotationNoField.setText(data[0].toString());
            quotationNoField.setEditable(false);
        } else {
            quotationNoField.setText("QUO-" + System.currentTimeMillis() % 10000);
        }
        formPanel.add(quotationNoField);

        formPanel.add(new JLabel("Item No:"));
        String[] items = { "Item 1", "Item 2", "Item 3" }; // Example items
        itemNoField = new JComboBox<>(items);
        if (isEditMode)
            itemNoField.setSelectedItem(data[1].toString());
        formPanel.add(itemNoField);

        formPanel.add(new JLabel("Quantity:"));
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        if (isEditMode)
            qtySpinner.setValue(data[2]);
        formPanel.add(qtySpinner);

        formPanel.add(new JLabel("Client Name:"));
        clientNameField = new JTextField();
        if (isEditMode)
            clientNameField.setText(data[3].toString());
        formPanel.add(clientNameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JFormattedTextField();
        priceField.setColumns(10);
        if (isEditMode)
            priceField.setText(data[4].toString());
        formPanel.add(priceField);

        formPanel.add(new JLabel("Transport Costs:"));
        transportCostsField = new JFormattedTextField();
        transportCostsField.setColumns(10);
        if (isEditMode)
            transportCostsField.setText(data[5].toString());
        formPanel.add(transportCostsField);

        formPanel.add(new JLabel("Total Costs:"));
        totalCostsField = new JTextField();
        if (isEditMode)
            totalCostsField.setText(data[6].toString());
        totalCostsField.setEditable(false);
        formPanel.add(totalCostsField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveQuotation() {
        try {
            // Validate inputs
            if (quotationNoField.getText().trim().isEmpty() ||
                    itemNoField.getSelectedItem() == null ||
                    (int) qtySpinner.getValue() <= 0 ||
                    clientNameField.getText().trim().isEmpty() ||
                    priceField.getText().trim().isEmpty() ||
                    transportCostsField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "All fields are required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse numeric values
            int qty = (int) qtySpinner.getValue();
            double price = Double.parseDouble(priceField.getText().trim());
            double transportCosts = Double.parseDouble(transportCostsField.getText().trim());
            double totalCosts = price + transportCosts;

            // Create data array
            Object[] rowData = {
                    quotationNoField.getText().trim(),
                    itemNoField.getSelectedItem().toString(),
                    qty,
                    clientNameField.getText().trim(),
                    price,
                    transportCosts,
                    totalCosts
            };

            // Update existing row
            if (isEditMode) {
                int selectedRow = parentTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                for (int i = 0; i < rowData.length; i++) {
                    model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(selectedRow), i);
                }
            } else {
                // Add new row
                parentTable.addRow(rowData);
            }

            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for Quantity, Price, and Transport Costs",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}