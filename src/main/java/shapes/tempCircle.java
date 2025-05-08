package shapes;

import java.awt.*;
import panels.DrawingPanel;
import panels.PanelWithShapes;

public class tempCircle extends Shape {
    public int RADIUS = 5;
    // private PanelWithShapes panelWithShapes;

    public tempCircle(Point position) {
        super(position);
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return point.distance(position) <= RADIUS;
    }

    @Override
    public void draw(Graphics g, double zoomFactor , boolean drawConnectionPoints, PanelWithShapes panel) {
                // hot pink solid color circle
                g.setColor(Color.PINK);
                int radius = (int) (RADIUS * zoomFactor);
                Point screenPoint = panel.worldToScreen(position);
                g.fillOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius * 2);

    
            }

    Point[] getHandles() {
        return new Point[] {
        };
    }

    // Function to check whether a point is within a handle
    @Override
    public
    boolean isHandle(Point point) {
        return false;
    }
}
