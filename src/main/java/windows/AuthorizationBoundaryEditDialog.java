package windows;

import shapes.AuthorizationBoundary;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AuthorizationBoundaryEditDialog extends JDialog {
    private final AuthorizationBoundary boundary;
    private final JTextField nameField;
    private final JTextField acronymField;
    private final JCheckBox displayAcronymCheckBox; 
    private final JColorChooser colorChooser;
    private final JCheckBox dashedLineCheckBox;
    private final JSpinner thicknessSpinner;
    private Point selectedTextPosition;
    private TextPlacementGrid textPlacementGrid;

    public AuthorizationBoundaryEditDialog(JFrame owner, AuthorizationBoundary boundary) {
        super(owner, "Edit Authorization Boundary", true);
        this.boundary = boundary;
        this.selectedTextPosition = boundary.getSelectedTextPosition();

        // Setup UI
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Name and acronym fields panel
        JPanel nameAcronymPanel = new JPanel();
        nameAcronymPanel.setLayout(new BoxLayout(nameAcronymPanel, BoxLayout.Y_AXIS));
        nameAcronymPanel.setBorder(new TitledBorder("Details"));

        // Name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Name:"), BorderLayout.WEST);
        nameField = new JTextField(boundary.getName(), 10); 
        namePanel.add(nameField, BorderLayout.CENTER);
        nameAcronymPanel.add(namePanel);

        // Acronym field
        JPanel acronymPanel = new JPanel(new BorderLayout());
        acronymPanel.add(new JLabel("Acronym:"), BorderLayout.WEST);
        acronymField = new JTextField(boundary.getAcronym(), 10);
        acronymPanel.add(acronymField, BorderLayout.CENTER);
        nameAcronymPanel.add(acronymPanel);

        // Display Acronym Checkbox
        displayAcronymCheckBox = new JCheckBox("Display Acronym", boundary.getDisplayAcronym());
        nameAcronymPanel.add(displayAcronymCheckBox);

        add(nameAcronymPanel, BorderLayout.NORTH);

        // Dashed line check box
        dashedLineCheckBox = new JCheckBox("Dashed Line", boundary.isDashed());
        add(dashedLineCheckBox, BorderLayout.WEST);

        // Thickness control
        JPanel thicknessPanel = new JPanel(new BorderLayout());
        thicknessPanel.add(new JLabel("Thickness:"), BorderLayout.WEST);
        thicknessSpinner = new JSpinner(new SpinnerNumberModel(boundary.getThickness(), 1, 10, 1));
        thicknessPanel.add(thicknessSpinner, BorderLayout.CENTER);
        add(thicknessPanel, BorderLayout.EAST);

        // Text position grid
        textPlacementGrid = new TextPlacementGrid();

        // Color chooser
        colorChooser = new JColorChooser(boundary.getColor());

        // Add the color chooser and text position grid to a new panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(colorChooser);
        centerPanel.add(textPlacementGrid);
        add(centerPanel, BorderLayout.CENTER);


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
        boundary.setName(nameField.getText());
        boundary.setAcronym(acronymField.getText());
        boundary.setDisplayAcronym(displayAcronymCheckBox.isSelected());
        boundary.setColor(colorChooser.getColor());
        boundary.setDashed(dashedLineCheckBox.isSelected());
        boundary.setThickness((Integer) thicknessSpinner.getValue());
        boundary.setSelectedTextPosition(textPlacementGrid.getSelectedPosition());
        setVisible(false);
    }

    class TextPlacementGrid extends JPanel {
        private CustomButton selectedButton = null;

    public TextPlacementGrid() {
        setLayout(new GridLayout(3, 3, 3, 3));
        setBackground(Color.GRAY);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                CustomButton button = new CustomButton(row, col);
                if (isCorner(row, col)) {
                    button.setEnabled(false);
                    button.setBackground(Color.GRAY);
                } else {
                    button.addActionListener(e -> onButtonClick(button));
                    if (col == selectedTextPosition.x && row == selectedTextPosition.y) {
                        onButtonClick(button);
                    }
                }
                add(button);
            }
        }
    }

        private boolean isCorner(int row, int col) {
            return (row == 0 || row == 2) && (col == 0 || col == 2);
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
                setPreferredSize(new Dimension(50, 50)); 
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
