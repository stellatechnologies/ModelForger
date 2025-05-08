package panels.MissionHierarchy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.adkin.cyberdiagramer.CyberDiagramer;

import non_shape_objects.Mission;

public class MissionDetailPanel extends JPanel {
    private final CyberDiagramer shapeDrawer;
        
        private static JLabel nameLabel;
        private static JLabel descriptionLabel;

    public MissionDetailPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;

        setLayout(new GridLayout(5, 2));
        setPreferredSize(new Dimension(200, 150));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        nameLabel = new JLabel();
        descriptionLabel = new JLabel();

        add(new JLabel("Name: "));
        add(nameLabel);
        add(new JLabel("Description: "));
        add(descriptionLabel);

        
    }

    public static void setMission(Mission data){

        nameLabel.setText(data.getName());

        descriptionLabel.setText(data.getDescription());

    }
}
