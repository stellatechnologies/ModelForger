package shapes;

import java.awt.*;
import panels.PanelWithShapes;

public class Rectangle_Class extends Shape {
    public int width;
    public int height;

    public Rectangle_Class(Point position, int width, int height) {
        super(position);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return point.x >= position.x && point.x <= position.x + width 
            && point.y >= position.y && point.y <= position.y + height;
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean drawConnectionPoint, PanelWithShapes panel) {
        Point screenPoint = panel.worldToScreen(position);
        int scaledWidth = (int) (width * zoomFactor);
        int scaledHeight = (int) (height * zoomFactor);
        int scaledThickness = (int) (thickness * zoomFactor);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(isSelected ? Color.GREEN : color);
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(scaledThickness));
        g2d.drawRect(screenPoint.x, screenPoint.y, scaledWidth, scaledHeight);
        g2d.setStroke(originalStroke);

        if (isSelected) {
            g2d.setColor(Color.BLACK);
            for (Point handle : getHandles()) {
                Point screenHandle = panel.worldToScreen(handle);
                g2d.fillRect(screenHandle.x - 5, screenHandle.y - 5, 10, 10);
            }
        }
        g2d.dispose();
    }

    Point[] getHandles() {
        return new Point[] {
            new Point(position.x + width, position.y + height),
            new Point(position.x, position.y + height),
            new Point(position.x + width, position.y),
            new Point(position.x, position.y)
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

    // Getters and setters for width and height
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
