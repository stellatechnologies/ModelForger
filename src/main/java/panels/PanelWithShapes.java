package panels;

import java.awt.Point;
import java.util.ArrayList;
import shapes.Shape;

public interface PanelWithShapes {
    void notifyShapeChanged();
    ArrayList<Shape> getShapes();
    Point worldToScreen(Point worldPoint);
    Point screenToWorld(int x, int y);
    void deselectAll();
    double getZoomFactor();
}
