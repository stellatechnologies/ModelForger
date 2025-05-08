package panels.MissionHierarchy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import com.adkin.cyberdiagramer.CyberDiagramer;

public class ToolPanel extends JPanel {
    private final CyberDiagramer shapeDrawer;
    private final ButtonGroup buttonGroup;
    private final Map<String, JToggleButton> buttons;

    public ToolPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
        this.buttonGroup = new ButtonGroup();
        this.buttons = new HashMap<>();
        setPreferredSize(new Dimension(200, 800));
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(0, 2)); // Two columns layout

        // Create toggle buttons
        createToggleButton("Mouse", "mouse_icon.png");
        createToggleButton("Operational Data", "op_data_icon.png");
        createToggleButton("Mission", "mission_icon.png");
        createToggleButton("Mission_Connection", "mission_mission_connection_icon.png");
        createToggleButton("Data_Mission_Connection", "op_data_mission_connection_icon.png");
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
