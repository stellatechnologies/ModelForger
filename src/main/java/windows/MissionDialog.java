package windows;

import javax.swing.*;
import java.awt.*;
import non_shape_objects.Mission;

public class MissionDialog extends JDialog {
    private Mission mission;
    private JTextField nameField, descriptionField;

    public MissionDialog(Window owner, Mission mission) {
        super(owner, "Edit Mission", ModalityType.APPLICATION_MODAL);
        setSize(400, 200);
        this.mission = mission != null ? mission : new Mission("", "", new Point(100, 100));
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField(mission != null ? mission.getName() : "");
        add(nameField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField(mission != null ? mission.getDescription() : "");
        add(descriptionField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);
    }

    private void onSave() {
        if (mission == null) {
            mission = new Mission("", "", new Point(100, 100));
        }

        mission.setName(nameField.getText());
        mission.setDescription(descriptionField.getText());
        setVisible(false);
    }

    public Mission getMission() {
        return mission;
    }
}
