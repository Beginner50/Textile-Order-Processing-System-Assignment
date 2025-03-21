package tops.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateQuotation extends JFrame {
    private Table table;
    private Object[] rowData;
    private JTextField quotationNoField, itemNoField, qtyField, clientNameField, priceField, transportCostsField;
    private JLabel totalCostsLabel;

    public UpdateQuotation(Object[] rowData, Table table) {
        this.rowData = rowData;
        this.table = table;

        setTitle("Edit Quotation");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add form fields with data from rowData
        formPanel.add(new JLabel("Quotation No:"));
        quotationNoField = new JTextField(rowData[0].toString());
        formPanel.add(quotationNoField);

        formPanel.add(new JLabel("Item No:"));
        itemNoField = new JTextField(rowData[1].toString());
        formPanel.add(itemNoField);

        formPanel.add(new JLabel("Quantity:"));
        qtyField = new JTextField(rowData[2].toString());
        formPanel.add(qtyField);

        formPanel.add(new JLabel("Client Name:"));
        clientNameField = new JTextField(rowData[3].toString());
        formPanel.add(clientNameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField(rowData[4].toString());
        formPanel.add(priceField);

        formPanel.add(new JLabel("Transport Costs:"));
        transportCostsField = new JTextField(rowData[5].toString());
        formPanel.add(transportCostsField);

        formPanel.add(new JLabel("Total Costs:"));
        totalCostsLabel = new JLabel(rowData[6].toString());
        formPanel.add(totalCostsLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Calculate total costs
                double price = Double.parseDouble(priceField.getText());
                double transportCosts = Double.parseDouble(transportCostsField.getText());
                double totalCosts = price + transportCosts;

                // Get the selected row
                int selectedRow = table.getSelectedRow();

                // Update the table data
                table.setValueAt(quotationNoField.getText(), selectedRow, 0);
                table.setValueAt(itemNoField.getText(), selectedRow, 1);
                table.setValueAt(qtyField.getText(), selectedRow, 2);
                table.setValueAt(clientNameField.getText(), selectedRow, 3);
                table.setValueAt(price, selectedRow, 4);
                table.setValueAt(transportCosts, selectedRow, 5);
                table.setValueAt(totalCosts, selectedRow, 6);

                // Close the form
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}