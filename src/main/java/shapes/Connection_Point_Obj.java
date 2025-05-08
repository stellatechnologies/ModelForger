package shapes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import panels.DrawingPanel;
import panels.PanelWithShapes;
import utils.ShapeUtils;

public class Connection_Point_Obj extends Rectangle_Class {
    private String name = ""; // Name of the interface
    private Shape owner; // System that the interface belongs to
    public boolean isConnectionSelected;
    private static int DEFAULT_WIDTH = 4; // Default width
    private static int DEFAULT_HEIGHT = 4; // Default height
    public boolean isHighlighted = false;
    public boolean isMovementMode = false;
    public ArrayList<Point> archivedPositions = new ArrayList<>();
    // ArchivedPositionPercentage (%, %) tuple
    public ArrayList<Double> archivedPositionPercentage = new ArrayList<>();
    public Color color;

    public Connection_Point_Obj(Point position, Shape owner) {
        super(position, DEFAULT_HEIGHT, DEFAULT_HEIGHT);
        this.owner = owner;

        // Choose randomly from a list of 10 colors
        Color[] colors = {
            new Color(255, 0, 255),
            new Color(255, 255, 0),
            new Color(128, 200, 50),
            new Color(50, 150, 200),
            new Color(210, 30, 100),
            new Color(20, 180, 10),
            new Color(70, 90, 240),
            new Color(255, 128, 0),
            new Color(180, 50, 210),
            new Color(0, 255, 128),
            new Color(100, 60, 20),
            new Color(190, 0, 180),
            new Color(40, 220, 120),
            new Color(255, 60, 30),
            new Color(70, 200, 60),
            new Color(0, 128, 255),
            new Color(150, 100, 10),
            new Color(255, 0, 128),
            new Color(90, 20, 220),
            new Color(10, 170, 190),
            new Color(30, 50, 255),
            new Color(100, 210, 80),
            new Color(200, 0, 60),
            new Color(255, 160, 20),
            new Color(90, 120, 30),
            new Color(10, 190, 160),
            new Color(160, 20, 220),
            new Color(50, 255, 100)
        };
        int randomIndex = (int) (Math.random() * colors.length);
        color = colors[randomIndex];
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean drawConnectionPoints, PanelWithShapes panel) {
        // Adjust width and height according to zoom factor
        int newWidth = (int) (DEFAULT_WIDTH * zoomFactor);
        int newHeight = (int) (DEFAULT_HEIGHT * zoomFactor);
        Point screenPoint = panel.worldToScreen(position);

        // Set the color for drawing Red filled in
        if(isMovementMode){
            g.setColor(new Color(191,141,255));
        }
        else if(isHighlighted){
            g.setColor(new Color(137, 207, 240));
        }else{
            // Generate random color
            g.setColor(color);
        }
        g.fillRect(screenPoint.x, screenPoint.y, newWidth, newHeight);

        // If isConnectionSelected is true, draw red circle around connection point
        if (isConnectionSelected) {
            g.setColor(Color.RED);
            g.drawOval(screenPoint.x - 5, screenPoint.y - 5, newWidth + 10, newHeight + 10);
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

    public void setOwner(Shape owner) {
        this.owner = owner;
    }

    public Shape getOwner() {
        return owner;
    }

    public Point getPosition() {
        return position;
    }

    public void moveLocation(Point newPosition) {
        // Move the location of the connection point towards the new position
        // But keep the connection point on the system perimeter


        // Get the owning system
        Shape owner = getOwner();

        if (owner instanceof System_Obj){
            System_Obj system = (System_Obj) owner;
            Point closestPoint = system.getClosestPointOnPerimeter(newPosition);
            setPosition(closestPoint);
        }
        else{
            Interface_Obj interfaceObj = (Interface_Obj) owner;
            Point closestPoint = interfaceObj.getClosestPointOnPerimeter(newPosition);
            setPosition(closestPoint);
        }

        // Point closestPoint = owner.getClosestPointOnPerimeter(newPosition);

        // setPosition(closestPoint);

    }

    public JSONObject export_object() {
 
        // Create a JSON object
        JSONObject json = new JSONObject();

        
        ShapeUtils.addAttribute(json, this.name, true, "name");
        ShapeUtils.addAttribute(json, this.position.x, true, "x");
        ShapeUtils.addAttribute(json, this.position.y, true, "y");
        ShapeUtils.addAttribute(json, this.owner.getIdentifier(), true, "owner");

        // archivedPositionPercentage
        ShapeUtils.addAttribute(json, this.archivedPositionPercentage, true, "archivedPositionPercentage");

        // Convert archivedPositions to a JSON array
       JSONArray archivedPositionsJsonArray = new JSONArray();
        for (Point point : this.archivedPositions) {
            String pointString = "(" + point.x + "," + point.y + ")";
            archivedPositionsJsonArray.put(pointString);
        }
        // json.put("jea::archivedPositions", archivedPositionsJsonArray);
        ShapeUtils.addAttribute(json, archivedPositionsJsonArray, true, "archivedPositions");

        return json;
    }

    public void setArchivedPositions(List<Point> archivedPositions2) {
        this.archivedPositions = (ArrayList<Point>) archivedPositions2;
    }

    public void setArchivedPositionPercentage(List<Double> archivedPositionPercentage2) {
        this.archivedPositionPercentage = (ArrayList<Double>) archivedPositionPercentage2;
    }


}
