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

public class OpData_Miss_Conn extends Connection_Object {
    public OpData_Miss_Conn(Shape connectionStart, Shape connectionEnd) {
        super(connectionStart, connectionEnd);
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
    
        private Point getClosestPointOnPerimeter(Shape mission, Shape opData, PanelWithShapes panel) {
            Point missionCenter = panel.worldToScreen(mission.position);
            Point opDataCenter = panel.worldToScreen(opData.position);
            double radius = mission.getWidth() * panel.getZoomFactor();
    
            double dx = opDataCenter.x - missionCenter.x;
            double dy = opDataCenter.y - missionCenter.y;
            double angle = Math.atan2(dy, dx);
    
            return new Point((int) (missionCenter.x + radius * Math.cos(angle)), 
                             (int) (missionCenter.y + radius * Math.sin(angle)));
        }
    
        private void drawStraightConnection(Graphics g, Point start, Point end) {
            Graphics2D g2d = (Graphics2D) g.create();
            if (solidLine) {
                g2d.setStroke(new BasicStroke(thickness));
                g2d.drawLine(start.x, start.y, end.x, end.y);
            } else {
                float[] dashPattern = {5, 5};
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
        public boolean contains(Point point, PanelWithShapes panel) {
            Point start = panel.worldToScreen(connectionStart.position);
            Point end = getClosestPointOnPerimeter(connectionEnd, connectionStart, panel);
    
            return containsStraightConnection(point, start, end);
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

    public void clear() {
        connectionStart = null;
        connectionEnd = null;
    }


    public JSONObject export_object() {
        JSONObject json = new JSONObject();
    
        // Add the connection start and end UUIDs
        ShapeUtils.addAttribute(json, uuid, false, "UUID");
        ShapeUtils.addAttribute(json, connectionStart.uuid, false, "Operational_Data_ID");
        ShapeUtils.addAttribute(json, connectionEnd.uuid, false, "Mission_ID");
      
        return json;
    }
    

}
