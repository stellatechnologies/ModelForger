package shapes;

import java.awt.*;
import panels.DrawingPanel;
import panels.PanelWithShapes;

public class Circle_Class extends Shape {
    public int RADIUS = 50;
    // private PanelWithShapes panelWithShapes;

    public Circle_Class(Point position) {
        super(position);
        // this.panelWithShapes = panelWithShapes;

        this.text = String.valueOf((char) (Math.random() * 26 + 'a'))
                    + String.valueOf((char) (Math.random() * 26 + 'a'))
                    + String.valueOf((char) (Math.random() * 26 + 'a'));
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return point.distance(position) <= RADIUS;
    }

    @Override
    public void draw(Graphics g, double zoomFactor , boolean drawConnectionPoints, PanelWithShapes panel) {
                // Set the color before drawing
                // If selected, mke outline slightly thicker and neon green
                if (isSelected) {
                    g.setColor(Color.GREEN); // Neon green
                    int radius = (int) (RADIUS * zoomFactor);
                    Point screenPoint = panel.worldToScreen(position);
                    g.drawOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius * 2);
    
                    // Draw handles
                    g.setColor(Color.BLACK);
                    for (Point handle : getHandles()) {
                        Point screenHandle = panel.worldToScreen(handle);
                        g.fillRect(screenHandle.x - 5, screenHandle.y - 5, 10, 10);
                    }
    
                } else {
                    g.setColor(color);
                    int radius = (int) (RADIUS * zoomFactor);
                    Point screenPoint = panel.worldToScreen(position);
                    g.drawOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius * 2);
                }
    
                // Draw text in the center
                g.setColor(Color.BLACK);
                FontMetrics metrics = g.getFontMetrics();
                int textWidth = metrics.stringWidth(text);
                int textHeight = metrics.getHeight();
                Point screenPoint = panel.worldToScreen(position);
                g.drawString(text, screenPoint.x - textWidth / 2, screenPoint.y + textHeight / 2);
    
            }

    Point[] getHandles() {
        return new Point[] {
                new Point(position.x + RADIUS, position.y), // Right
                // new Point(position.x - RADIUS, position.y), // Left
                // new Point(position.x, position.y + RADIUS), // Bottom
                new Point(position.x, position.y - RADIUS) // Top
        };
    }

    // Function to check whether a point is within a handle
    @Override
    public
    boolean isHandle(Point point) {
        for (Point handle : getHandles()) {
            if (point.distance(handle) <= 5) {
                return true;
            }
        }
        return false;
    }
}
