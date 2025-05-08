package non_shape_objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.json.JSONObject;

import panels.PanelWithShapes;
import shapes.Shape;
import utils.ShapeUtils;

public class Mission_Connection extends Connection_Object {
    private Mission connectionStart;
    private Mission connectionEnd;
    public boolean solidLine;
    public Color color = Color.BLACK;
    public boolean hidden = false;
    public boolean isSelected = false;

    public Mission_Connection(Mission connectionStart, Mission connectionEnd) {
        super(connectionStart, connectionEnd);
        this.connectionStart = connectionStart;
        this.connectionEnd = connectionEnd;
    }

    @Override
    public void draw(Graphics g, double scale, boolean drawConnectionPoints, PanelWithShapes panel) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(isSelected ? Color.GREEN : color);

        Point start = getClosestPointOnPerimeter(connectionStart, connectionEnd, panel);
        Point end = getClosestPointOnPerimeter(connectionEnd, connectionStart, panel);

        drawStraightConnection(g2d, start, end);
        drawArrow(g2d, start, end, true); // Arrow from start to end

        g2d.dispose();
    }

    private Point getClosestPointOnPerimeter(Shape source, Shape target, PanelWithShapes panel) {
        // Assuming the shape has a method to get its center and dimensions
        Point sourceCenter = panel.worldToScreen(source.position);
        Point targetCenter = panel.worldToScreen(target.position);
        double radius = source.getWidth() * panel.getZoomFactor();

        double dx = targetCenter.x - sourceCenter.x;
        double dy = targetCenter.y - sourceCenter.y;
        double angle = Math.atan2(dy, dx);

        return new Point((int) (sourceCenter.x + radius * Math.cos(angle)),
                (int) (sourceCenter.y + radius * Math.sin(angle)));
    }

    private void drawStraightConnection(Graphics g, Point start, Point end) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (solidLine) {
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawLine(start.x, start.y, end.x, end.y);
        } else {
            float[] dashPattern = { 5, 5 };
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, dashPattern, 0));
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
        g2d.dispose();
    }

    private void drawArrow(Graphics2D g2d, Point start, Point end, boolean largerArrow) {
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        int arrowLength = largerArrow ? 15 : 10; // Larger arrow size

        double x1 = end.x - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = end.y - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = end.x - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = end.y - arrowLength * Math.sin(angle + Math.PI / 6);

        g2d.setStroke(new BasicStroke(2)); // Thicker arrow line
        g2d.drawLine(end.x, end.y, (int) x1, (int) y1);
        g2d.drawLine(end.x, end.y, (int) x2, (int) y2);
    }

    // Contains function to see if the connection contains a point
    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        Point start = panel.worldToScreen(connectionStart.position);
        Point end = panel.worldToScreen(connectionEnd.position);

        return containsStraightConnection(point, start, end);
    }

    private boolean containsStraightConnection(Point point, Point start, Point end) {
        int x1 = start.x;
        int y1 = start.y;
        int x2 = end.x;
        int y2 = end.y;

        // Handle vertical lines separately
        if (x1 == x2) {
            return point.x == x1 && point.y >= Math.min(y1, y2) && point.y <= Math.max(y1, y2);
        }

        double slope = (double) (y2 - y1) / (x2 - x1);
        double yIntercept = y1 - slope * x1;

        double distance = Math.abs(slope * point.x - point.y + yIntercept) / Math.sqrt(slope * slope + 1);

        return distance <= 5 && point.x >= Math.min(x1, x2) && point.x <= Math.max(x1, x2);
    }

    @Override
    public boolean isHandle(Point point) {
        return false;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Point getConnectionStart() {
        return connectionStart.position;
    }

    public Point getConnectionEnd() {
        return connectionEnd.position;
    }

    public void setSolidLine(boolean b) {
        solidLine = b;
    }

    public boolean isSolidLine() {
        return solidLine;
    }

    public JSONObject export_object() {
        JSONObject json = new JSONObject();
    
        // Add the connection start and end UUIDs
        ShapeUtils.addAttribute(json, connectionStart.getIdentifier(), false, "Child_Mission");
        ShapeUtils.addAttribute(json, connectionEnd.getIdentifier(), false, "Parent_Mission");
      
        return json;
    }

}
