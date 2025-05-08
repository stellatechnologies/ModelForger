package shapes; 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class TextPlacementGrid extends JPanel {

    private static final int ROWS = 3, COLS = 3, SIZE = 100, GAP = 5, MARGIN = 10;
    private static final Color SELECTED_COLOR = Color.RED;
    private static final Color MARGIN_COLOR = Color.GRAY;
    private CustomButton selectedButton = null;

    public TextPlacementGrid() {
        setLayout(new GridLayout(ROWS, COLS, GAP, GAP));
        setBackground(MARGIN_COLOR);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                CustomButton button = new CustomButton();
                button.addActionListener(e -> onButtonClick(button));
                add(button);
            }
        }

        initUI();
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

    private void initUI() {
        JFrame frame = new JFrame("Text Placement Grid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextPlacementGrid());
    }

    static class CustomButton extends JButton {
        private boolean isSelected = false;

        public CustomButton() {
            setPreferredSize(new Dimension(SIZE, SIZE));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(true);
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(SELECTED_COLOR);
                g2d.fillRoundRect(MARGIN, MARGIN, getWidth() - 2 * MARGIN, getHeight() - 2 * MARGIN, MARGIN, MARGIN);
                g2d.dispose();
            }
        }
    }
}
