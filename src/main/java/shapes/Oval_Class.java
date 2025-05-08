package shapes;

import java.awt.*;

import panels.DrawingPanel;
import panels.PanelWithShapes;

public class Oval_Class extends Shape {
    public int width = 30;
    public int height = 30;

    public Oval_Class(Point position) {
        super(position);

        // Generate a random text identifier
        this.text = String.valueOf((char) (Math.random() * 26 + 'a'))
                + String.valueOf((char) (Math.random() * 26 + 'a'))
                + String.valueOf((char) (Math.random() * 26 + 'a'));
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        // Check if point is within the oval
        double dx = Math.pow(point.x - (position.x + width / 2.0), 2) / Math.pow(width / 2.0, 2);
        double dy = Math.pow(point.y - (position.y + height / 2.0), 2) / Math.pow(height / 2.0, 2);
        return dx + dy <= 1;
    }

    @Override
    public void draw(Graphics g, double zoomFactor , boolean drawConnectionPoints, PanelWithShapes panel) {
        // Adjust width and height according to zoom factor
        int newWidth = (int) (width * zoomFactor);
        int newHeight = (int) (height * zoomFactor);
        int newThickness = (int) (thickness * zoomFactor); // Use thickness from Shape
        Point screenPoint = panel.worldToScreen(position);

        Graphics2D g2d = (Graphics2D) g.create();
        
        // Set the color for drawing
        g2d.setColor(isSelected ? Color.GREEN : color);
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(newThickness)); // Set thickness for the oval border
        g2d.drawOval(screenPoint.x, screenPoint.y, newWidth, newHeight);
        g2d.setStroke(originalStroke); // Reset to original stroke

        // Draw handles if selected
        if (isSelected) {
            g2d.setColor(Color.BLACK);
            for (Point handle : getHandles()) {
                Point screenHandle = panel.worldToScreen(handle);
                g2d.fillRect(screenHandle.x - 5, screenHandle.y - 5, 10, 10);
            }
        }

        // Draw text in the center
        g2d.setColor(Color.BLACK);
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2d.drawString(text, screenPoint.x + newWidth / 2 - textWidth / 2, screenPoint.y + newHeight / 2 + textHeight / 2);

        g2d.dispose(); // Dispose to avoid resource leak
    }


    public Point[] getHandles() {
        // Put handles on perimeter of oval
        return new Point[] {
                new Point(position.x + width / 2, position.y),
                new Point(position.x + width, position.y + height / 2),
                new Point(position.x + width / 2, position.y + height),
                new Point(position.x, position.y + height / 2)
        };
    }

    @Override
    public boolean isHandle(Point point) {
        for (Point handle : getHandles()) {
            if (point.distance(handle) <= 5) {
                return true;
            }
        }
        return false;
    }

    @Override
    // get width
    public int getWidth() {
        return width;
    }

    // set width
    public void setWidth(int width) {
        this.width = width;
    }

    // get height
    public int getHeight() {
        return height;
    }

    // set height
    public void setHeight(int height) {
        this.height = height;
    }
}
