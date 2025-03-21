package tops.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuotationForm extends JDialog {
    private JTextField quotationNoField;
    private JComboBox<String> itemNoField;
    private JSpinner qtySpinner;
    private JTextField clientNameField;
    private JTextField priceField;
    private JTextField transportCostsField;
    private JTextField totalCostsField;

    private JButton saveButton;
    private JButton cancelButton;

    private Table parentTable;
    private Object[] editData;
    private boolean isEditMode;
    private int editRowIndex; // New field to store the row index

    public QuotationForm(Object[] data, Table parentTable) {
        this(data, parentTable, -1); // Default constructor calls the new one with -1 index
    }

    public QuotationForm(Object[] data, Table parentTable, int rowIndex) {
        this.parentTable = parentTable;
        this.editData = data;
        this.isEditMode = (data != null);
        this.editRowIndex = rowIndex; // Store the row index

        setTitle(isEditMode ? "Edit Quotation" : "Create Quotation");
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
            // Generate a new quotation number
            quotationNoField.setText("QUO-" + System.currentTimeMillis() % 10000);
        }
        formPanel.add(quotationNoField);

        formPanel.add(new JLabel("Item No:"));
        String[] items = { "Item A", "Item B", "Item C" }; // Example items
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

            // Validate quotation number format
            if (!quotationNoField.getText().trim().startsWith("QUO-")) {
                JOptionPane.showMessageDialog(this,
                        "Quotation number must start with 'QUO-'",
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

            if (isEditMode) {
                // Update existing row - use stored row index instead of current selection
                DefaultTableModel model = (DefaultTableModel) parentTable.getModel();
                for (int i = 0; i < rowData.length; i++) {
                    model.setValueAt(rowData[i], parentTable.convertRowIndexToModel(editRowIndex), i);
                }
            } else {
                // Add new row
                parentTable.addRow(rowData);
            }

            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for Price and Transport Costs",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}