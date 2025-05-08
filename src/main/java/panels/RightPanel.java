package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import com.adkin.cyberdiagramer.CyberDiagramer;

public class RightPanel extends JPanel {
    private final CyberDiagramer shapeDrawer;
    private final ButtonGroup buttonGroup;
    private final Map<String, JToggleButton> buttons;

    public RightPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
        this.buttonGroup = new ButtonGroup();
        this.buttons = new HashMap<>();
        setPreferredSize(new Dimension(200, 800));
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(0, 2)); // Two columns layout

        // Create toggle buttons
        createToggleButton("Mouse", "mouse_icon.png");
        createToggleButton("System", "system_icon.png");
        createToggleButton("Connection", "connection_icon.png");
        createToggleButton("Authorization Boundary", "boundary_icon.png");
        createToggleButton("Interface", "interface_icon.png");
        createToggleButton("Connection Point", "connection_point_icon.png");
        createToggleButton("Entry Point", "entry_point_icon.png");
    }

    private void createToggleButton(String name, String iconFileName) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconFileName));
        JToggleButton button = new JToggleButton(icon);
        button.setActionCommand(name);
        button.setOpaque(true); // Make the button opaque to show background color
        button.setBorderPainted(false); // Optional, for visual style

        button.addActionListener((ActionEvent e) -> {
            shapeDrawer.onButtonSelected(e.getActionCommand());
            updateButtonColors();
        });

        buttonGroup.add(button);
        buttons.put(name, button);
        add(button);
    }

    private void updateButtonColors() {
        for (JToggleButton button : buttons.values()) {
            if (button.isSelected()) {
                button.setBackground(Color.GREEN);
            } else {
                button.setBackground(null);
            }
        }
    }
}
