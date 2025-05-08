package shapes;

import java.awt.*;
import panels.PanelWithShapes;

public abstract class Shape {
    public Point position;
    public Color color = Color.BLACK;
    public int thickness = 2;
    public String text = "";
    public boolean isSelected = false;
    // protected PanelWithShapes panel; // Reference to a panel that implements PanelWithShapes
    public boolean hidden = false;
    public String uuid = java.util.UUID.randomUUID().toString();
    public Point velocity = new Point(0, 0);
    public Object isCollapsed = false;

    public Shape(Point position) {
        this.position = position;
        // this.panel = panel; // Store the reference to the panel
    }

    public abstract boolean contains(Point point, PanelWithShapes panel);
    public abstract boolean isHandle(Point point);
    public abstract void draw(Graphics g, double zoomFactor, boolean drawConnectionPoints, PanelWithShapes panel);


    // protected Point worldToScreen(Point worldPoint) {
    //     return drawingPanel.worldToScreen(worldPoint);
    // }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getIdentifier() {
        // Implement logic to return a unique name or identifier for each shape
        // For example, use 'System 1', 'Circle A', etc.
        return this.uuid;
    }

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getThickness() {
        return thickness;
    }

    public boolean isCollapseToggleClicked(Point point, PanelWithShapes panel, double zoomFactor) {
        return false;
    }

    public void toggleCollapse() {
    }

    
    public void setHiddenStatus(boolean hidden) {
        this.hidden = hidden;
    }
}
