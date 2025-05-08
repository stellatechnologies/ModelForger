
package non_shape_objects;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.UUID;

import org.json.JSONObject;

import com.google.gson.Gson;

import panels.PanelWithShapes;
import shapes.Oval_Class;
import utils.ShapeUtils;



public class Mission extends Oval_Class {
    public String uuid = UUID.randomUUID().toString();
    public String name;
    public String description;
    public Color color = new Color(204, 153, 255);
    public boolean isConnectionSelected = false;
    public Point velocity = new Point(0, 0);



    private static class ExportedMission {
        public String name;
        public String description;
        public Point position;
        // public Color color;
        public String uuid;

        ExportedMission(Mission mission) {
            this.name = mission.name;
            this.description = mission.description;
            this.position = mission.position;
            // this.color = mission.color;
            this.uuid = mission.uuid;
        }
    }


    public Mission(String name, String description, Point position) {
        super(position);  // Call the Oval_Class constructor

        this.name = name;
        this.description = description;
        this.position = position;



        // Random text for the system object
        if (this.name == ""){
        this.name = "M" + String.valueOf((char) (Math.random() * 26 + 'a')) +
                    String.valueOf((char) (Math.random() * 26 + 'a')) +
                    String.valueOf((char) (Math.random() * 26 + 'a'));

        }


        // Set the text of the oval to be the name of the operational data
        setText(name);

        // Set the color of the oval (convert the color string to a Color object if necessary)
    }

    public String toString() {
        return name;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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



    @Override
    public void draw(Graphics g, double zoomFactor , boolean drawConnectionPoints, PanelWithShapes panel) {
        
        if(isSelected){
            g.setColor(Color.GREEN);
        } else {
            g.setColor(color);
        }

        if(isConnectionSelected){
            g.setColor(new Color(155, 250, 243));
        }

        // g.setColor(color);
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
        ShapeUtils.addAttribute(json, description, false, "Description");

        // Custom attributes

        ShapeUtils.addAttribute(json, position.x, true, "x");
        ShapeUtils.addAttribute(json, position.y, true, "y");
        ShapeUtils.addAttribute(json, "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")", true, "color");



        return json;
    }
}
