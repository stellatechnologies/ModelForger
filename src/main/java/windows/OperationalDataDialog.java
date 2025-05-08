package windows;

import javax.swing.*;
import java.awt.*;
import non_shape_objects.OperationalData;
import java.util.UUID;

public class OperationalDataDialog extends JDialog {
    private OperationalData operationalData;
    private JTextField nameField, descriptionField, classifField;
    private JSpinner confidentialitySpinner, integritySpinner, availabilitySpinner;

    public OperationalDataDialog(Window owner, OperationalData operationalData) {
        super(owner, "Operational Data", ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        this.operationalData = operationalData != null ? operationalData : new OperationalData("", "", 1, 1, 1, new Point(100, 100), "");
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField(operationalData != null ? operationalData.getName() : "");
        add(nameField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField(operationalData != null ? operationalData.getDescription() : "");
        add(descriptionField);

        add(new JLabel("Confidentiality Value:"));
        confidentialitySpinner = new JSpinner(new SpinnerNumberModel(
                operationalData != null ? operationalData.getConfidentialityValue() : 1, 1, 5, 1));
        add(confidentialitySpinner);

        add(new JLabel("Integrity Value:"));
        integritySpinner = new JSpinner(new SpinnerNumberModel(
                operationalData != null ? operationalData.getIntegrityValue() : 1, 1, 5, 1));
        add(integritySpinner);

        add(new JLabel("Availability Value:"));
        availabilitySpinner = new JSpinner(new SpinnerNumberModel(
                operationalData != null ? operationalData.getAvailabilityValue() : 1, 1, 5, 1));
        add(availabilitySpinner);

        add(new JLabel("Color:"));
        classifField = new JTextField(operationalData != null ? operationalData.getClassif() : "");
        add(classifField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        add(saveButton);
    }

    private void onSave() {
        if (operationalData == null) {
            operationalData = new OperationalData("", "", 1, 1, 1, new Point(100, 100), "");
        }

        operationalData.setName(nameField.getText());
        operationalData.setDescription(descriptionField.getText());
        operationalData.setConfidentialityValue((Integer) confidentialitySpinner.getValue());
        operationalData.setIntegrityValue((Integer) integritySpinner.getValue());
        operationalData.setAvailabilityValue((Integer) availabilitySpinner.getValue());
        operationalData.setClassif(classifField.getText());
        setVisible(false);
    }

    public OperationalData getOperationalData() {
        return operationalData;
    }
}
