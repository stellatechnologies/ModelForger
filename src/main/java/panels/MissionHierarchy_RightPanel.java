package panels;

import javax.swing.*;
import java.awt.*;

import com.adkin.cyberdiagramer.CyberDiagramer;

import panels.MissionHierarchy.DataDetailPanel;
import panels.MissionHierarchy.ToolPanel;

public class MissionHierarchy_RightPanel extends JPanel {
    public MissionHierarchy_RightPanel(CyberDiagramer shapeDrawer) {
        setPreferredSize(new Dimension(200, 800));
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(2, 1)); // Two rows layout

        // Add the mouse and system panels
        DataDetailPanel dataDetailPanel = new DataDetailPanel(shapeDrawer);
        add(dataDetailPanel);
        add(new ToolPanel(shapeDrawer));
    }
}
