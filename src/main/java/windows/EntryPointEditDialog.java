package windows;

import shapes.Entry_Point;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class EntryPointEditDialog extends JDialog {
    private final Entry_Point entryPoint;
    private final JTextField nameField;
    private final JColorChooser colorChooser;

    public EntryPointEditDialog(JFrame owner, Entry_Point entryPoint) {
        super(owner, "Edit Entry Point", true);
        this.entryPoint = entryPoint;

        // Setup UI
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Name field panel
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(new TitledBorder("Name"));
        nameField = new JTextField(entryPoint.getText(), 10);
        namePanel.add(nameField, BorderLayout.CENTER);

        add(namePanel, BorderLayout.NORTH);

        // Color chooser
        colorChooser = new JColorChooser(entryPoint.getColor());
        add(colorChooser, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        entryPoint.setText(nameField.getText());
        entryPoint.setColor(colorChooser.getColor());
        setVisible(false);
    }
}
