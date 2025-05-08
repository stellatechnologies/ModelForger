package shapes;

import java.awt.*;
import java.util.ArrayList;

import org.json.JSONObject;

import panels.DrawingPanel;
import panels.PanelWithShapes;
import utils.ShapeUtils;

public class Interface_Obj extends Rectangle_Class {
    private String name = ""; // Name of the interface
    public System_Obj system; // System that the interface belongs to
    private static int DEFAULT_WIDTH = 20; // Default width
    private static int DEFAULT_HEIGHT = 20; // Default height
    private String system_side = null; // Side of the system that the interface is on

    public ArrayList<Connection_Point_Obj> connectionPoints = new ArrayList<>();

    public Interface_Obj(Point position, System_Obj system) {
        super(position, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.system = system;
        
        initializeConnectionPoints();
    }

    private void initializeConnectionPoints() {
        // 2 connection points, one on each side of the interface
        connectionPoints
                .add(new Connection_Point_Obj(new Point(position.x, position.y + height / 2), this)); // Left
                                                                                                      // middle
        connectionPoints.add(
                new Connection_Point_Obj(new Point(position.x + width, position.y + height / 2), this)); // Right
                                                                                                         // middle

    }

    // Override the draw method to include interface-specific drawing
    @Override
    public void draw(Graphics g, double zoomFactor, boolean showConnectionPoints, PanelWithShapes panel) {
        if(!hidden){
        super.draw(g, zoomFactor, showConnectionPoints, panel); // Call the base class draw method

        // Draw the interface name
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(name);
        Point screenPoint = panel.worldToScreen(new Point(position.x + width / 2, position.y + height / 2));
        g.drawString(name, screenPoint.x - textWidth / 2, screenPoint.y);

        }

        // Draw Connection Points if showConnectionPoints is true
        if (showConnectionPoints) {
            for (Connection_Point_Obj connectionPoint : connectionPoints) {
                // Draw the connection point
                connectionPoint.draw(g, zoomFactor, showConnectionPoints, panel);
            }
        }
    }

    // Getters and setters for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Point closestPoint) {
        this.position = closestPoint;
    }

    public void setSystem(System_Obj system) {
        this.system = system;
    }

    public System_Obj getSystem() {
        return system;
    }

    public void setSystemSide(String system_side) {
        this.system_side = system_side;
    }

    public String getSystemSide() {
        return system_side;
    }

    public Point getPosition() {
        return position;
    }

    public void moveLocation(Point newPosition) {
        // Get the owning system
        System_Obj system = getSystem();
    
        Point closestPoint = system.getClosestPointOnPerimeter(newPosition);
    
        // Center the interface at the closest point
        Point centeredPosition = new Point(
            closestPoint.x - (width / 2),
            closestPoint.y - (height / 2)
        );

        // Get the difference between the current position and the centered position
        Point difference = new Point(
            centeredPosition.x - position.x,
            centeredPosition.y - position.y
        );


    
        setPosition(centeredPosition);

        // Move the connection points the same amount
        for (Connection_Point_Obj connectionPoint : connectionPoints) {
            Point connectionPointPosition = connectionPoint.getPosition();
            connectionPoint.setPosition(new Point(
                connectionPointPosition.x + difference.x,
                connectionPointPosition.y + difference.y
            ));
        }


    }

    public Point getClosestPointOnPerimeter(Point worldPoint) {
        // Rectangle edges
        int leftEdge = position.x;
        int rightEdge = position.x + width;
        int topEdge = position.y;
        int bottomEdge = position.y + height;

        // Calculate distances to each edge
        int distanceToLeft = worldPoint.x - leftEdge;
        int distanceToRight = rightEdge - worldPoint.x;
        int distanceToTop = worldPoint.y - topEdge;
        int distanceToBottom = bottomEdge - worldPoint.y;

        // Find the minimum distance
        int minDistance = Math.min(Math.min(distanceToLeft, distanceToRight),
                Math.min(distanceToTop, distanceToBottom));

        // Determine the closest edge and return the corresponding point
        if (minDistance == distanceToLeft) {
            return new Point(leftEdge, worldPoint.y); // Closest to left edge
        } else if (minDistance == distanceToRight) {
            return new Point(rightEdge, worldPoint.y); // Closest to right edge
        } else if (minDistance == distanceToTop) {
            return new Point(worldPoint.x, topEdge); // Closest to top edge
        } else {
            return new Point(worldPoint.x, bottomEdge); // Closest to bottom edge
        }
    }

    // Additional interface-specific methods can be added here
    public void fixConnectionPoints() {
        // Make sure all connection points are always on the perimeter of the system
        for (Connection_Point_Obj connectionPoint : connectionPoints) {
            Point closestPoint = getClosestPointOnPerimeter(connectionPoint.getPosition());
            connectionPoint.setPosition(closestPoint);
        }
    }

    public JSONObject export_object() {
        JSONObject json = new JSONObject();
    
        // Schema compliant
        ShapeUtils.addAttribute(json, uuid, false, "UUID");
        ShapeUtils.addAttribute(json, system.getIdentifier(), false, "System_ID");
        ShapeUtils.addAttribute(json, name, false, "Name");



        return json;
    }
}
