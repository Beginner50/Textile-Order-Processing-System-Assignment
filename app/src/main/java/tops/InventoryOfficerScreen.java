package tops;

import tops.components.ButtonFactory;
import tops.components.Table;
import tops.components.ItemForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InventoryOfficerScreen extends AbstractCustomScreen {
    private final Connection conn;
    private final static String[] ITEM_TABLE_COLUMNS = {"ItemNo", "Name", "Category", "Size", "Cost Price", "Stock Level", "Reorder Threshold"};
    private JPanel statusPanel;
    private JLabel statusLabel;

    public InventoryOfficerScreen(Connection conn) {
        this.conn = conn;
        setTitle("Inventory Officer Dashboard");

        // Create status panel at the bottom
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.setBackground(new Color(240, 240, 240));
        add(statusPanel, BorderLayout.SOUTH);

        ButtonHandler buttonHandler = new ButtonHandler();
        toolbar.addTabButton("Items", "CreateItem", 
            ButtonFactory.createButton("Add Item", "add.png"), buttonHandler);
        toolbar.addTabButton("Items", "EditItem", 
            ButtonFactory.createButton("Edit Item", "edit.png"), buttonHandler);
        toolbar.addTabButton("Items", "DeleteItem", 
            ButtonFactory.createButton("Delete Item", "delete.png"), buttonHandler);
        toolbar.addTabButton("Items", "UpdateItemStock", 
            ButtonFactory.createButton("Update Stock Level", "update.png"), buttonHandler);
        tabbedTablePane.addTabbedTable("Items", ITEM_TABLE_COLUMNS);
        
        // Load data from database
        loadItemsData();
        
        toolbar.loadConfiguration("Items", tabbedTablePane.getTableFromTab("Items"));
    }
    
    public void loadItemsData() {
        try {
            String query = "SELECT * FROM Items";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            Table itemsTable = tabbedTablePane.getTableFromTab("Items");
            // Clear table by removing all rows
            DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
            model.setRowCount(0);
            
            // Clear any existing row colors
            itemsTable.clearRowColors();
            
            // List to store low stock items
            List<String> lowStockItems = new ArrayList<>();
            int rowCount = 0;
            
            while (rs.next()) {
                Object[] rowData = new Object[7];
                rowData[0] = rs.getInt("ItemNo");
                rowData[1] = rs.getString("name");
                rowData[2] = rs.getString("category");
                rowData[3] = rs.getString("size");
                rowData[4] = rs.getDouble("cost_price");
                rowData[5] = rs.getInt("stock_level");
                rowData[6] = rs.getInt("reorder_threshold");
                
                itemsTable.addRow(rowData);
                
                // Check if stock level is at or below reorder threshold
                int stockLevel = rs.getInt("stock_level");
                int reorderThreshold = rs.getInt("reorder_threshold");
                String itemName = rs.getString("name");
                
                // Now check if stock level is less than or equal to 10
                if (stockLevel <= 10) {
                    // Highlight row with warning color (light orange)
                    itemsTable.setRowColor(rowCount, new Color(255, 235, 156));
                    lowStockItems.add(itemName);
                }
                
                rowCount++;
            }
            
            // Update status label with low stock warning
            updateLowStockWarning(lowStockItems);
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateLowStockWarning(List<String> lowStockItems) {
        if (lowStockItems.isEmpty()) {
            statusLabel.setText("All items have stock levels above critical threshold (10).");
            statusLabel.setForeground(new Color(0, 128, 0)); // Green
        } else {
            StringBuilder message = new StringBuilder("<html>Critical stock warning (≤ 10): ");
            
            // Only show up to 3 items in the status bar
            for (int i = 0; i < Math.min(3, lowStockItems.size()); i++) {
                if (i > 0) message.append(", ");
                message.append("<b>").append(lowStockItems.get(i)).append("</b>");
            }
            
            // If there are more than 3 items, add a count of remaining items
            if (lowStockItems.size() > 3) {
                message.append(" and ").append(lowStockItems.size() - 3).append(" more item(s)");
            }
            
            message.append("</html>");
            statusLabel.setText(message.toString());
            statusLabel.setForeground(new Color(204, 0, 0)); // Red
            
            // Show a more detailed notification when there are low stock items
            if (!lowStockItems.isEmpty()) {
                StringBuilder detailedMessage = new StringBuilder("The following items have critical stock levels (≤ 10):\n\n");
                for (String item : lowStockItems) {
                    detailedMessage.append("• ").append(item).append("\n");
                }
                
                // Show a non-modal dialog that doesn't block the application
                JOptionPane optionPane = new JOptionPane(
                    detailedMessage.toString(),
                    JOptionPane.WARNING_MESSAGE
                );
                JDialog dialog = optionPane.createDialog(this, "Critical Stock Warning");
                dialog.setModal(false);
                dialog.setVisible(true);
            }
        }
    }

    class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            Table itemsTable = tabbedTablePane.getTableFromTab("Items");
            
            if (source == toolbar.getTabButton("CreateItem")) {
                new ItemForm(conn, null, itemsTable, InventoryOfficerScreen.this).setVisible(true);
            } else if (source == toolbar.getTabButton("EditItem")) {
                int selectedRow = itemsTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object[] rowData = new Object[itemsTable.getColumnCount()];
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = itemsTable.getValueAt(selectedRow, i);
                    }
                    new ItemForm(conn, rowData, itemsTable, InventoryOfficerScreen.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(InventoryOfficerScreen.this, 
                        "Please select an item to edit", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == toolbar.getTabButton("DeleteItem")) {
                deleteItem();
            } else if (source == toolbar.getTabButton("UpdateItemStock")) {
                updateItemStock();
            }
        }
        
        private void deleteItem() {
            Table itemsTable = tabbedTablePane.getTableFromTab("Items");
            int selectedRow = itemsTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(InventoryOfficerScreen.this, 
                    "Please select an item to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int itemNo = (int) itemsTable.getValueAt(selectedRow, 0);
            String itemName = (String) itemsTable.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(
                InventoryOfficerScreen.this,
                "Are you sure you want to delete item '" + itemName + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    deleteItemFromDatabase(itemNo);
                    loadItemsData(); // Refresh the table
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                        "Error deleting item: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
        
        private void deleteItemFromDatabase(int itemNo) throws SQLException {
            String query = "DELETE FROM Items WHERE ItemNo = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemNo);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                        "Item deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                        "No item was deleted. The item may no longer exist.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
        private void updateItemStock() {
            Table itemsTable = tabbedTablePane.getTableFromTab("Items");
            int selectedRow = itemsTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(InventoryOfficerScreen.this, 
                    "Please select an item to update", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int itemNo = (int) itemsTable.getValueAt(selectedRow, 0);
            String itemName = (String) itemsTable.getValueAt(selectedRow, 1);
            int currentStock = (int) itemsTable.getValueAt(selectedRow, 5);
            
            String input = JOptionPane.showInputDialog(
                InventoryOfficerScreen.this,
                "Enter new stock level for " + itemName + ":",
                currentStock);
                
            if (input != null) {
                try {
                    int newStock = Integer.parseInt(input);
                    updateStockInDatabase(itemNo, newStock);
                    loadItemsData(); // Refresh the table
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                        "Please enter a valid number",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void updateStockInDatabase(int itemNo, int newStock) {
            try {
                String query = "UPDATE Items SET stock_level = ? WHERE ItemNo = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, newStock);
                    stmt.setInt(2, itemNo);
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                            "Stock level updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Check if the new stock level is below reorder threshold
                        checkStockLevelAfterUpdate(itemNo, newStock);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                    "Error updating stock: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
        private void checkStockLevelAfterUpdate(int itemNo, int newStock) {
            try {
                String query = "SELECT name FROM Items WHERE ItemNo = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, itemNo);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        String itemName = rs.getString("name");
                        
                        if (newStock <= 10) {
                            JOptionPane.showMessageDialog(InventoryOfficerScreen.this,
                                "Warning: " + itemName + " stock level is now at or below the critical threshold.\n" +
                                "Current Stock: " + newStock + "\n" +
                                "Critical Threshold: 10",
                                "Low Stock Warning",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
