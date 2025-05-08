package shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONObject;

import panels.DrawingPanel;
import panels.PanelWithShapes;
import utils.ShapeUtils;
import non_shape_objects.OperationalData;

public class SystemConnection extends Shape{
    private Connection_Point_Obj connectionStart;
    private Connection_Point_Obj connectionEnd;
    public boolean rightAngle; // true for right-angled, false for straight line
    public boolean solidLine;
    public Color color = Color.BLACK;
    public boolean visible = true;
    public boolean isSelected = false;
    public String UUID;

    public ArrayList<OperationalData> sys1_sys2_Data = new ArrayList<OperationalData>();
    public ArrayList<OperationalData> sys2_sys1_Data = new ArrayList<OperationalData>();

    public SystemConnection(Connection_Point_Obj connectionStart, Connection_Point_Obj connectionEnd, boolean rightAngle, boolean solidLine, String UUID) {
        super(new Point(0, 0));
        this.connectionStart = connectionStart;
        this.connectionEnd = connectionEnd;
        this.rightAngle = rightAngle;
        this.solidLine = solidLine;
        if(UUID == null) {
            this.UUID = java.util.UUID.randomUUID().toString();
        } else {
            this.UUID = UUID;
        }
        // this.drawingPanel = drawingPanel;
    }

    @Override
    public void draw(Graphics g, double scale, boolean drawConnectionPoints, PanelWithShapes panel) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(isSelected ? Color.GREEN : color);

        Point start = panel.worldToScreen(connectionStart.position);
        Point end = panel.worldToScreen(connectionEnd.position);

        if (rightAngle) {
            drawRightAngledConnection(g2d, start, end);
        } else {
            drawStraightConnection(g2d, start, end);
        }

        // Draw arrows based on data flow direction
        if (!sys1_sys2_Data.isEmpty()) {
            drawArrow(g2d, start, end, true, rightAngle); // Arrow from start to end
        }
        if (!sys2_sys1_Data.isEmpty()) {
            drawArrow(g2d, end, start, true, rightAngle); // Arrow from end to start
        }
        

        g2d.dispose();
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
    
    private void drawRightAngledConnection(Graphics g, Point start, Point end) {
        Graphics2D g2d = (Graphics2D) g.create();
        int midX = (start.x + end.x) / 2;
    
        if (solidLine) {
            g2d.setStroke(new BasicStroke(thickness));
        } else {
            float[] dashPattern = {5, 5};
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, dashPattern, 0));
        }
    
        g2d.drawLine(start.x, start.y, midX, start.y);
        g2d.drawLine(midX, start.y, midX, end.y);
        g2d.drawLine(midX, end.y, end.x, end.y);
    
        g2d.dispose();
    }
    
    
    private void drawArrow(Graphics2D g2d, Point start, Point end, boolean largerArrow, boolean isRightAngle) {
        double angle;
        if (isRightAngle) {
            if (Math.abs(end.x - start.x) > Math.abs(end.y - start.y)) {
                // The horizontal segment is closer to the end point
                angle = (start.x > end.x) ? Math.PI : 0; // Point left or right
            } else {
                // The vertical segment is closer to the end point
                angle = (start.y > end.y) ? -Math.PI / 2 : Math.PI / 2; // Point up or down
            }
        } else {
            // Straight line
            angle = Math.atan2(end.y - start.y, end.x - start.x);
        }
    
        int arrowLength = largerArrow ? 15 : 10; // Larger arrow size
        int arrowWidth = largerArrow ? 7 : 5;
    
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

        if (rightAngle) {
            return containsRightAngledConnection(point, connectionStart.position, connectionEnd.position);
        } else {
            return containsStraightConnection(point, connectionStart.position, connectionEnd.position);
        }
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

    private boolean containsRightAngledConnection(Point point, Point start, Point end) {
        int midX = (start.x + end.x) / 2;
        int midY = (start.y + end.y) / 2;
    
        // Check if the point is close to the first horizontal segment
        boolean closeToFirstSegment = point.y >= start.y - 5 && point.y <= start.y + 5
                                      && point.x >= Math.min(start.x, midX) && point.x <= Math.max(start.x, midX);
    
        // Check if the point is close to the vertical segment
        boolean closeToVerticalSegment = point.x >= midX - 5 && point.x <= midX + 5
                                         && point.y >= Math.min(start.y, end.y) && point.y <= Math.max(start.y, end.y);
    
        // Check if the point is close to the second horizontal segment
        boolean closeToSecondSegment = point.y >= end.y - 5 && point.y <= end.y + 5
                                       && point.x >= Math.min(midX, end.x) && point.x <= Math.max(midX, end.x);
    
                
        return closeToFirstSegment || closeToVerticalSegment || closeToSecondSegment;
    }
    
    
    // Point[] getHandles() {
    //     // Return point in the middle of the connection as the handle if the connection is straight
    //     // Otherwise, return the point in the middle of the horizontal segment from start to mid-point
        
    //     Point start = panel.worldToScreen(connectionStart.position);
    //     Point end = panel.worldToScreen(connectionEnd.position);

    //     if (rightAngle) {
    //         int midX = (start.x + end.x) / 2;
    //         int midY = (start.y + end.y) / 2;
    //         return new Point[] { new Point(midX, midY) };
    //     } else {
    //         return new Point[] { new Point((start.x + end.x) / 2, (start.y + end.y) / 2) };
    //     }

    // }

    @Override
    public boolean isHandle(Point point) {
        // for (Point handle : getHandles()) {
        //     if (point.distance(handle) <= 5) {
        //         return true;
        //     }
        // }
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

    public boolean isRightAngle() {
        return rightAngle;
    }

    public Shape getOwner(int i) {
        // Return the system that owns the connection point at index i
        Shape owner;
        if (i == 0) {
            owner = connectionStart.getOwner(); 
        } else {
            owner = connectionEnd.getOwner();
        }

        return owner;
    }

    public ArrayList<OperationalData> getSys1ToSys2Data() {
        return sys1_sys2_Data;
    }

    public ArrayList<OperationalData> getSys2ToSys1Data() {
        return sys2_sys1_Data;
    }

    //setSys1ToSys2Data
    public void setSys1ToSys2Data(ArrayList<OperationalData> sys1_sys2_Data) {
        this.sys1_sys2_Data = sys1_sys2_Data;
    }

    //setSys2ToSys1Data
    public void setSys2ToSys1Data(ArrayList<OperationalData> sys2_sys1_Data) {
        this.sys2_sys1_Data = sys2_sys1_Data;
    }

    public void setRightAngle(boolean selected) {
        rightAngle = selected;
    }

    public void setSolidLine(boolean b) {
        solidLine = b;
    }

    public boolean isSolidLine() {
        return solidLine;
    }

    public String getUUID() {
        return UUID;
    }


    public System_Obj getFirstSystem() {
        Shape connectionStartOwner = connectionStart.getOwner();
        String connectionStartOwnerType = connectionStartOwner.getClass().getSimpleName();

        // If Connection Start is a Interface_Obj
        if (connectionStartOwnerType.equals("Interface_Obj")) {

            Interface_Obj startingInterface = (Interface_Obj) connectionStartOwner;
            System_Obj startingSystem = startingInterface.system;

            return startingSystem;

        } else { // If Connection Start is a System_Obj

            System_Obj startingSystem = (System_Obj) connectionStartOwner;

            return startingSystem;
        }
    }

    public System_Obj getSecondSystem() {
        Shape connectionEndOwner = connectionEnd.getOwner();
        String connectionEndOwnerType = connectionEndOwner.getClass().getSimpleName();

        // If Connection End is a Interface_Obj
        if (connectionEndOwnerType.equals("Interface_Obj")) {

            Interface_Obj endingInterface = (Interface_Obj) connectionEndOwner;
            System_Obj endingSystem = endingInterface.system;

            return endingSystem;

        } else { // If Connection End is a System_Obj

            System_Obj endingSystem = (System_Obj) connectionEndOwner;

            return endingSystem;
        }
    }

    public JSONObject export_object() {

        JSONObject json = new JSONObject();
    
        // Schema compliant
        ShapeUtils.addAttribute(json, uuid, false, "UUID");

        // Get connection end point owners and determine if System or Interface (System_Obj or Interface_Obj)
        Shape connectionStartOwner = connectionStart.getOwner();
        String connectionStartOwnerType = connectionStartOwner.getClass().getSimpleName();
        Shape connectionEndOwner = connectionEnd.getOwner();
        String connectionEndOwnerType = connectionEndOwner.getClass().getSimpleName();


        // If Connection Start is a Interface_Obj
        if (connectionStartOwnerType.equals("Interface_Obj")) {

            Interface_Obj startingInterface = (Interface_Obj) connectionStartOwner;
            System_Obj startingSystem = startingInterface.system;

            ShapeUtils.addAttribute(json, startingInterface.getIdentifier(), false, "Interface_1_UUID");
            ShapeUtils.addAttribute(json, startingSystem.getIdentifier(), false, "System_1_UUID");

        } else { // If Connection Start is a System_Obj

            System_Obj startingSystem = (System_Obj) connectionStartOwner;

            ShapeUtils.addAttribute(json, null, false, "Interface_1_UUID");
            ShapeUtils.addAttribute(json, startingSystem.getIdentifier(), false, "System_1_UUID");
        }

        // If Connection End is a Interface_Obj
        if (connectionEndOwnerType.equals("Interface_Obj")) {

            Interface_Obj endingInterface = (Interface_Obj) connectionEndOwner;
            System_Obj endingSystem = endingInterface.system;

            ShapeUtils.addAttribute(json, endingInterface.getIdentifier(), false, "Interface_2_UUID");
            ShapeUtils.addAttribute(json, endingSystem.getIdentifier(), false, "System_2_UUID");

        } else { // If Connection End is a System_Obj

            System_Obj endingSystem = (System_Obj) connectionEndOwner;

            ShapeUtils.addAttribute(json, null, false, "Interface_2_UUID");
            ShapeUtils.addAttribute(json, endingSystem.getIdentifier(), false, "System_2_UUID");
        }
        
        


        return json;
    }
    

}
