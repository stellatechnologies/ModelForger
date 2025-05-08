package shapes;

import java.awt.*;
import panels.PanelWithShapes;

public class Square_Class extends Shape {
    private static final int SIDE = 100;
    private PanelWithShapes panelWithShapes;

    public Square_Class(Point position) {
        super(position);
        // this.panelWithShapes = panelWithShapes;
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return Math.abs(point.x - position.x) <= SIDE / 2 && Math.abs(point.y - position.y) <= SIDE / 2;
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean drawConnectionPoints, PanelWithShapes panel) {
        int sideLength = (int) (SIDE * zoomFactor);
        Point screenPoint = panel.worldToScreen(position);
        g.setColor(color);
        g.drawRect(screenPoint.x - sideLength / 2, screenPoint.y - sideLength / 2, sideLength, sideLength);
    }

    Point[] getHandles() {
        return new Point[] {
            new Point(position.x + SIDE / 2, position.y + SIDE / 2), // Bottom right
            new Point(position.x - SIDE / 2, position.y + SIDE / 2), // Bottom left
            new Point(position.x + SIDE / 2, position.y - SIDE / 2), // Top right
            new Point(position.x - SIDE / 2, position.y - SIDE / 2)  // Top left
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

    // Function getPosition
    Point getPosition() {
        return position;
    }

    // Function getSide
    int getSide() {
        return SIDE;
    }

    // Get width
    @Override
    public int getWidth() {
        return SIDE;
    }

    // Get height
    @Override
    public int getHeight() {
        return SIDE;
    }
}
