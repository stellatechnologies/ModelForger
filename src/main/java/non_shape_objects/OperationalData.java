package non_shape_objects;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.UUID;

import org.json.JSONObject;

import panels.PanelWithShapes;
import shapes.Oval_Class;
import utils.ShapeUtils;



public class OperationalData extends Oval_Class {
    private UUID uuid = UUID.randomUUID();
    private String name;
    private String description;
    private int confidentialityValue;
    private int integrityValue;
    private int availabilityValue;
    private Color color = Color.orange;
    private String classif;
    public boolean isConnectionSelected = false;
    public Point velocity = new Point(0, 0);

    public OperationalData(String name, String description, int confidentialityValue, 
                           int integrityValue, int availabilityValue, Point position, String classif) {
        super(position);  // Call the Oval_Class constructor

        this.name = name;
        this.description = description;
        this.confidentialityValue = confidentialityValue;
        this.integrityValue = integrityValue;
        this.availabilityValue = availabilityValue;
        this.classif = classif;



        // Random text for the system object
        if (this.name == ""){
        this.name = String.valueOf((char) (Math.random() * 26 + 'a')) +
                    String.valueOf((char) (Math.random() * 26 + 'a')) +
                    String.valueOf((char) (Math.random() * 26 + 'a'));

        }

        if (confidentialityValue == 1 & availabilityValue == 1 & integrityValue == 1){
            this.confidentialityValue =( int) (Math.random() * 5 + 1);
            this.integrityValue = (int) (Math.random() * 5 + 1);
            this. availabilityValue = (int) (Math.random() * 5 + 1);
            
            
        }


        // Set the text of the oval to be the name of the operational data
        setText(name);

        // Set the color of the oval (convert the color string to a Color object if necessary)
    }

    public String toString() {
        return name;
    }
    
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getConfidentialityValue() {
        return confidentialityValue;
    }

    public void setConfidentialityValue(int confidentialityValue) {
        this.confidentialityValue = confidentialityValue;
    }

    public int getIntegrityValue() {
        return integrityValue;
    }

    public void setIntegrityValue(int integrityValue) {
        this.integrityValue = integrityValue;
    }

    public int getAvailabilityValue() {
        return availabilityValue;
    }

    public void setAvailabilityValue(int availabilityValue) {
        this.availabilityValue = availabilityValue;
    }

    public String getClassif() {
        return classif;
    }

    public void setClassif(String classif) {
        this.classif = classif;
    }

    @Override
    public void draw(Graphics g, double zoomFactor , boolean drawConnectionPoints, PanelWithShapes panel) {
        if(isSelected){
            g.setColor(Color.GREEN);
        } else if(isConnectionSelected){
            g.setColor(new Color(155, 250, 243));
        } else {
            g.setColor(color);
        }

        int radius = (int) (width * zoomFactor);
        Point screenPoint = panel.worldToScreen(position);
        // Fill the circle
        g.fillOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius * 2);

        // }

        // Draw text in the center
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.drawString(name, screenPoint.x - textWidth / 2, screenPoint.y + textHeight / 2);

        

    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return point.distance(position) <= width;
    }

    public void setConnectionSelected(boolean isConnectionSelected) {
        this.isConnectionSelected = isConnectionSelected;
    }


    public JSONObject export_object() {
        JSONObject json = new JSONObject();
    
        // Schema compliant
        ShapeUtils.addAttribute(json, getIdentifier(), false, "UUID");
        ShapeUtils.addAttribute(json, name, false, "Name");
        ShapeUtils.addAttribute(json, description, false, "Data_Description");

        // Custom attributes

        ShapeUtils.addAttribute(json, position.x, true, "x");
        ShapeUtils.addAttribute(json, position.y, true, "y");
        ShapeUtils.addAttribute(json, "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")", true, "color");



        return json;
    }
}
