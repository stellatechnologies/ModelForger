package windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

import shapes.System_Obj;

public class SystemEditDialog extends JDialog {
    private Color selectedColor;
    private String enteredName;
    private String enteredAcronym;
    private boolean displayAcronym;
    private Point selectedTextPosition;
    private TextPlacementGrid textPlacementGrid;

    public SystemEditDialog(JFrame owner, System_Obj system) {
        super(owner, "Edit System", true);
        this.selectedColor = system.getColor(); // Assuming getColor() method exists in System_Obj
        this.enteredName = system.getName(); // Assuming getName() method exists in System_Obj
        this.enteredAcronym = system.getAcronym(); // Assuming getAcronym() method exists in System_Obj
        this.displayAcronym = system.getDisplayAcronym(); // Assuming getDisplayAcronym() method exists in System_Obj
        this.selectedTextPosition = system.getSelectedTextPosition();

        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));


        // Name field
        JTextField nameField = new JTextField(enteredName, 10);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Name:"));
        namePanel.add(nameField);
        mainPanel.add(namePanel);

        // Acronym field
        JTextField acronymField = new JTextField(enteredAcronym, 10);
        JPanel acronymPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acronymPanel.add(new JLabel("Acronym:"));
        acronymPanel.add(acronymField);
        mainPanel.add(acronymPanel);

        // Checkbox for display acronym
        JCheckBox acronymCheckBox = new JCheckBox("Display Acronym", displayAcronym);
        JPanel displayAcronymPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        displayAcronymPanel.add(acronymCheckBox);
        mainPanel.add(displayAcronymPanel);

        // Custom grid panel
        textPlacementGrid = new TextPlacementGrid();
        mainPanel.add(textPlacementGrid);


        // Color chooser
        JColorChooser colorChooser = new JColorChooser(selectedColor);
        mainPanel.add(colorChooser);

        // Add the main panel to the dialog
        add(mainPanel, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            selectedColor = colorChooser.getColor();
            enteredName = nameField.getText();
            enteredAcronym = acronymField.getText();
            displayAcronym = acronymCheckBox.isSelected();
            selectedTextPosition = textPlacementGrid.getSelectedPosition();
            setVisible(false);
        });
        add(saveButton, BorderLayout.SOUTH);

        pack();
    }

    public Point getSelectedTextPosition() {
        return textPlacementGrid.getSelectedPosition();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public String getEnteredName() {
        return enteredName;
    }

    public String getEnteredAcronym() {
        return enteredAcronym;
    }

    public boolean getDisplayAcronym() {
        return displayAcronym;
    }

    // Nested class for TextPlacementGrid
    class TextPlacementGrid extends JPanel {
        private CustomButton selectedButton = null;

        public TextPlacementGrid() {
            setLayout(new GridLayout(3, 3, 3, 3)); // Reduced spacing
            setBackground(Color.GRAY);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    CustomButton button = new CustomButton(row, col);
                    button.addActionListener(e -> onButtonClick(button));
                    add(button);
                    if (col == selectedTextPosition.x && row == selectedTextPosition.y) {
                        onButtonClick(button);
                    }
                }
            }
        }

        private void onButtonClick(CustomButton button) {
            if (selectedButton != null) {
                selectedButton.setSelected(false);
                selectedButton.repaint();
            }
            selectedButton = button;
            selectedButton.setSelected(true);
            selectedButton.repaint();
        }

        public Point getSelectedPosition() {
            if (selectedButton == null) return null;
            return new Point(selectedButton.getCol(), selectedButton.getRow());
        }

        class CustomButton extends JButton {
            private int row, col;
            private boolean isSelected = false;

            public CustomButton(int row, int col) {
                this.row = row;
                this.col = col;
                setPreferredSize(new Dimension(50, 50)); // Smaller button size
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(true);
            }

            public void setSelected(boolean isSelected) {
                this.isSelected = isSelected;
            }

            public int getRow() {
                return row;
            }

            public int getCol() {
                return col;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isSelected) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(Color.RED);
                    g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 10, 10);
                    g2d.dispose();
                }
            }
        }
    }
}
