package windows;

import java.awt.*;
import javax.swing.*;
import shapes.Shape; // Make sure to import your Shape class

public class ShapeEditDialog extends JDialog {
    private Color selectedColor;
    private String enteredText;

    public ShapeEditDialog(JFrame owner, Shape shape) {
        super(owner, "Edit Shape", true);
        this.selectedColor = shape.getColor(); // Assuming getColor() method exists in Shape
        this.enteredText = shape.getText(); // Assuming getText() method exists in Shape

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Color chooser
        JColorChooser colorChooser = new JColorChooser(selectedColor);
        add(colorChooser, BorderLayout.CENTER);

        // Text field
        JTextField textField = new JTextField(enteredText, 10);
        add(textField, BorderLayout.NORTH);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            selectedColor = colorChooser.getColor();
            enteredText = textField.getText();
            setVisible(false);
        });
        add(saveButton, BorderLayout.SOUTH);

        pack();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public String getEnteredText() {
        return enteredText;
    }
}
