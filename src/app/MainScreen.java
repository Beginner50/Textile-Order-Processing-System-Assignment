package app;

import javax.swing.*;
import Components.*;
// ... other imports ...

public class MainScreen extends JFrame {
    // ... existing code ...

    private TabbedTablePane tabbedTablePane; // Ensure this is defined

    public MainScreen() {
        // ... existing constructor code ...
        tabbedTablePane = new TabbedTablePane(); // Initialize it
        // ... existing constructor code ...
    }

    // Ensure this method is present
    public TabbedTablePane getTabbedTablePane() {
        return tabbedTablePane;
    }

    // ... existing methods ...
}