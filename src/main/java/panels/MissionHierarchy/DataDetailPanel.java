package panels.MissionHierarchy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.adkin.cyberdiagramer.CyberDiagramer;

import non_shape_objects.Mission;
import non_shape_objects.OperationalData;

public class DataDetailPanel extends JPanel {
    private final CyberDiagramer shapeDrawer;

    private static JLabel nameLabel;
    private static JLabel descriptionLabel;
    private static JLabel confidentialityLabel;
    private static JLabel integrityLabel;
    private static JLabel availabilityLabel;
    private static JLabel HTML_Label;

    public DataDetailPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;

        setLayout(new GridLayout(5, 2));
        setPreferredSize(new Dimension(200, 150));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        HTML_Label = new JLabel("<html>");

        add(HTML_Label);

    }

    public static void setOperationalData(OperationalData data) {

        HTML_Label.setText("<html>" +
            "<strong><b style='color:#336699;'>Name:</b></strong>" + data.getName() + "<br>" +
            "<strong><b style='color:#336699;'>Description:</b></strong> " + data.getDescription() + "<br>" +
            "<strong><b style='color:#336699;'>Conf:</b></strong> " + data.getConfidentialityValue() + "<br>" +
            "<strong><b style='color:#336699;'>Int:</b></strong> " + data.getIntegrityValue() + "<br>" +
            "<strong><b style='color:#336699;'>Avail:</b></strong> " + data.getAvailabilityValue() +
            "</html>");

    }

    public static void setMissionData(Mission mission) {

        HTML_Label.setText("<html>" +
            "<b style='color:#669933;'>Name:</b> " + mission.getName() + "<br>" +
            "<b style='color:#669933;'>Description:</b> " + mission.getDescription() +
            "</html>");

    }
}
