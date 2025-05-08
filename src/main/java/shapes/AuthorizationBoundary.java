package shapes;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;

import com.google.gson.Gson;

import panels.DrawingPanel;
import panels.PanelWithShapes;
import utils.ShapeUtils;

public class AuthorizationBoundary extends Oval_Class {
    private String uuid;
    public String acronym = "";
    public String name = "";
    public boolean dashed = false;
    // List arrays for children and parents
    public ArrayList<AuthorizationBoundary> children = new ArrayList<>();
    public AuthorizationBoundary parent = null;
    public int width = 600;
    public int height = 300;
    public boolean displayAcronym = false;

    public int text_x_position = 1;
    public int text_y_position = 1;

    private static class ExportObject {
        String id;
        String text;
        String width;
        String height;

        ExportObject(String id, String text, String width, String height) {
            this.id = id;
            this.text = text;
            this.width = width;
            this.height = height;
        }
    }

    public AuthorizationBoundary(Point position) {
        super(position);
        // this.panelWithShapes = panelWithShapes;
        this.uuid = UUID.randomUUID().toString();
        
        // Set name to random 3-letter acronym
        this.name = String.valueOf((char) (Math.random() * 26 + 'a'))
        + String.valueOf((char) (Math.random() * 26 + 'a'))
        + String.valueOf((char) (Math.random() * 26 + 'a'));
    }

    public void addChild(AuthorizationBoundary child, ArrayList<Shape> shapes) {
        children.add(child);
        child.setParent(this);

        auto_size(shapes);
    }

    public void setParent(AuthorizationBoundary parent) {
        this.parent = parent;
    }

    public void auto_size(ArrayList<Shape> shapes) {
        if (!children.isEmpty() || hasSystems(shapes)) {
            // Initialize with current bounds
            int minX = position.x;
            int minY = position.y;
            int maxX = position.x + width;
            int maxY = position.y + height;

            // Extend bounds to include all child boundaries
            for (AuthorizationBoundary child : children) {
                Point childPos = child.getPosition();
                minX = Math.min(minX, childPos.x);
                minY = Math.min(minY, childPos.y);
                maxX = Math.max(maxX, childPos.x + child.getWidth());
                maxY = Math.max(maxY, childPos.y + child.getHeight());
            }

            // Extend bounds to include all Systems associated with this boundary
            for (Shape shape : shapes) {
                if (shape instanceof System_Obj) {
                    System_Obj system = (System_Obj) shape;
                    if (system.boundary == this) {
                        Point systemPos = system.getPosition();
                        int systemWidth = system.getWidth();
                        int systemHeight = system.getHeight();
                        minX = Math.min(minX, systemPos.x);
                        minY = Math.min(minY, systemPos.y);
                        maxX = Math.max(maxX, systemPos.x + systemWidth);
                        maxY = Math.max(maxY, systemPos.y + systemHeight);
                    }
                }
            }

            // Calculate the new center point
            int centerX = (minX + maxX) / 2;
            int centerY = (minY + maxY) / 2;

            // Calculate new width and height
            int newWidth = maxX - minX;
            int newHeight = maxY - minY;

            // Update position to be the top-left of the new bounds
            position = new Point(centerX - newWidth / 2, centerY - newHeight / 2);

            // Update size
            width = newWidth;
            height = newHeight;
        } else {
            // Default size when no children or associated systems are present
            this.width = 100;
            this.height = 100;
        }

        // If this boundary has a parent, recursively adjust its size too
        if (parent != null) {
            parent.auto_size(shapes);
        }
    }

    private boolean hasSystems(ArrayList<Shape> shapes) {
        for (Shape shape : shapes) {
            if (shape instanceof System_Obj) {
                System_Obj system = (System_Obj) shape;

                if (system.boundary == this) {
                    return true;
                }
            }
        }
        return false;
    }

    // getChildren
    public List<AuthorizationBoundary> getChildren() {
        return children;
    }

    // getPosition
    public Point getPosition() {
        return position;
    }

    public List<AuthorizationBoundary> getParents() {
        List<AuthorizationBoundary> parents = new ArrayList<>();
        AuthorizationBoundary current = this.parent;
        while (current != null) {
            parents.add(current);
            current = current.parent;
        }
        return parents;
    }

    public List<AuthorizationBoundary> getAllDescendants() {
        List<AuthorizationBoundary> allDescendants = new ArrayList<>();
        for (AuthorizationBoundary child : children) {
            allDescendants.add(child);
            allDescendants.addAll(child.getAllDescendants());
        }
        return allDescendants;
    }

    @Override
    public String toString() {
        return this.text; // Assuming 'text' is the name of the system
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean drawConnectionPoints, PanelWithShapes panel) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Set color
        g2d.setColor(color);

        // Set the stroke for drawing
        if (dashed) {
            // Dashed line style
            float dash[] = { 10.0f };
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        } else {
            // Solid line style
            g2d.setStroke(new BasicStroke(thickness));
        }

        // Draw the oval
        Point screenPoint = panel.worldToScreen(position);
        int scaledWidth = (int) (width * zoomFactor);
        int scaledHeight = (int) (height * zoomFactor);
        g2d.drawOval(screenPoint.x, screenPoint.y, scaledWidth, scaledHeight);








        // Draw text in the center of the oval
        g2d.setColor(Color.BLACK);
        FontMetrics metrics = g2d.getFontMetrics();
        
        int textHeight = metrics.getHeight();
        int textWidth;
        String text_to_draw;
        int text_x_draw_pos;
        int text_y_draw_pos;

        if (displayAcronym) {
             textWidth = metrics.stringWidth(acronym);
             text_to_draw = acronym;
        } else {
            textWidth = metrics.stringWidth(name);
             text_to_draw = name;
        }

        // Middle X Position
        if (text_x_position == 1) {
            // Middle Y Position (Center)
            if (text_y_position == 1) {
                g2d.drawString(text_to_draw, screenPoint.x + scaledWidth / 2 - textWidth / 2, screenPoint.y + scaledHeight / 2 + textHeight / 4);
            }
            // Middle Top Position
            else if (text_y_position == 0) {
                g2d.drawString(text_to_draw, screenPoint.x + scaledWidth / 2 - textWidth / 2, screenPoint.y + textHeight);
            }
            // Middle Bottom Position
            else if (text_y_position == 2) {
                g2d.drawString(text_to_draw, screenPoint.x + scaledWidth / 2 - textWidth / 2, screenPoint.y + scaledHeight - textHeight / 4);
            }
        } else if (text_x_position == 0) { // Left X Position
            // Left Y Position (Center)
            if (text_y_position == 1) {
                g2d.drawString(text_to_draw, screenPoint.x + textWidth / 4, screenPoint.y + scaledHeight / 2 + textHeight / 4);
            }
        } else if (text_x_position == 2) { // Right X Position
            // Right Y Position (Center)
            if (text_y_position == 1) {
                g2d.drawString(text_to_draw, screenPoint.x +((int)(scaledWidth * .95)) - textWidth, screenPoint.y + scaledHeight / 2 + textHeight / 4);
            }
        }








        

        // If selected, draw handles and dashed bounding box
        if (isSelected) {
            // Draw handles
            g2d.setColor(Color.BLACK);
            for (Point handle : getHandles()) {
                Point screenHandle = panel.worldToScreen(handle);
                g2d.fillRect(screenHandle.x - 5, screenHandle.y - 5, 10, 10);
            }

            // Draw dashed bounding box (only if not already dashed)
            if (!dashed) {
                g2d.setStroke(
                        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 10 }, 0));
                g2d.drawRect(screenPoint.x, screenPoint.y, scaledWidth, scaledHeight);
            }
        }

        g2d.dispose();
    }

    public Point[] getHandles() {
        // Put handles on perimeter of oval
        return new Point[] {
                new Point(position.x + width / 2, position.y),
                new Point(position.x + width, position.y + height / 2),
                new Point(position.x + width / 2, position.y + height),
                new Point(position.x, position.y + height / 2)
        };
    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        // Check if point is within the oval
        double dx = Math.pow(point.x - (position.x + width / 2.0), 2) / Math.pow(width / 2.0, 2);
        double dy = Math.pow(point.y - (position.y + height / 2.0), 2) / Math.pow(height / 2.0, 2);
        return dx + dy <= 1;
    }

    public boolean isPointOnPerimeter(Point point) {
        // Calculate the oval's center
        int centerX = position.x + width / 2;
        int centerY = position.y + height / 2;
    
        // Calculate normalized distances from the center
        double dx = (double) (point.x - centerX) / (width / 2);
        double dy = (double) (point.y - centerY) / (height / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);
    
        // Define a tolerance factor for easier click detection
        double tolerance = 0.05; // Adjust this value based on desired sensitivity
    
        // Calculate the range for the perimeter based on thickness and tolerance
        double innerRadius = 1 - ((double) thickness / Math.max(width, height)) - tolerance;
        double outerRadius = 1 + ((double) thickness / Math.max(width, height)) + tolerance;
    
        // Check if the point is within the range of the perimeter
        return distance >= innerRadius && distance <= outerRadius;
    }
    

    public String getUUID() {
        return uuid;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public boolean isDashed() {
        return dashed;
    }

    public void setDashed(boolean dashed) {
        this.dashed = dashed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.text = name;
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDashedLine() {
        return false;
    }

    public String getText() {
        return text;
    }

    // getWidth
    @Override
    public int getWidth() {
        return width;
    }

    // getHeight
    @Override
    public int getHeight() {
        return height;
    }

    public String exportToJson() {
        Gson gson = new Gson();
        // Create a simple object to represent the data you want to export
        String iden = this.getIdentifier();
        String text = this.text;

        String with = String.valueOf(this.width);
        String hei = String.valueOf(this.height);

        // Create object to represent the data you want to export
        ExportObject exportedObject = new ExportObject(iden, text, with, hei);

        // Convert the object to a JSON string
        String jsonString = gson.toJson(exportedObject);

        return jsonString;
        
    }

    public void setId(String id) {
        this.uuid = id;
    }

    public Boolean getDisplayAcronym() {
        return displayAcronym;
    }

    public void setDisplayAcronym(Boolean displayAcronym) {
        this.displayAcronym = displayAcronym;
    }

    public Point getSelectedTextPosition(){
        return new Point(text_x_position, text_y_position);
    }

    public void setSelectedTextPosition(Point selectedPos){
        text_x_position = selectedPos.x;
        text_y_position = selectedPos.y;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    

    public JSONObject export_object() {
        JSONObject json = new JSONObject();

        

        // Schema compliant attributes
        ShapeUtils.addAttribute(json, uuid, false, "UUID");
        ShapeUtils.addAttribute(json, name, false, "Name");
        ShapeUtils.addAttribute(json, acronym, false, "Acronym");
        ShapeUtils.addAttribute(json, "", false, "Description");
        ShapeUtils.addAttribute(json, null, false, "Program_ID");


        ShapeUtils.addAttribute(json, dashed, true, "dashed");
        ShapeUtils.addAttribute(json, text_x_position, true, "text_x_position");
        ShapeUtils.addAttribute(json, text_y_position, true, "text_y_position");
        ShapeUtils.addAttribute(json, displayAcronym, true, "displayAcronym");
        ShapeUtils.addAttribute(json, position.x, true, "x");
        ShapeUtils.addAttribute(json, position.y, true, "y");
        ShapeUtils.addAttribute(json, "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")", true, "color");
        ShapeUtils.addAttribute(json, width, true, "width");
        ShapeUtils.addAttribute(json, height, true, "height");
        ShapeUtils.addAttribute(json, thickness, true, "thickness");


        return json;
    }
}
