package shapes;

import java.awt.*;

import panels.DrawingPanel;
import panels.PanelWithShapes;
import utils.ShapeUtils;

import org.json.JSONObject;

public class Entry_Point extends Circle_Class {
    public SystemConnection systemConnectionOwner;
    public AuthorizationBoundary authorizationBoundaryOwner;
    public Shape owner1 = null;
    public Shape owner2 = null;

    public int RADIUS = 20;
    public String text = "";
    // FFFCBB color
    public Color color = new Color(255, 252, 187);

    public Entry_Point(Point position, SystemConnection systemConnectionOwner,
            AuthorizationBoundary authorizationBoundaryOwner) {
        super(position);
        this.systemConnectionOwner = systemConnectionOwner;
        this.authorizationBoundaryOwner = authorizationBoundaryOwner;

        // Update system owners
        if (systemConnectionOwner != null) {
            owner1 = systemConnectionOwner.getOwner(0);
            owner2 = systemConnectionOwner.getOwner(1);
        }
    }



    // Setters and getters for the SystemConnection owner
    public void setSystemConnectionOwner(SystemConnection owner) {
        this.systemConnectionOwner = owner;
    }

    public SystemConnection getSystemConnectionOwner() {
        return systemConnectionOwner;
    }

    // Setters and getters for the AuthorizationBoundary owner
    public void setAuthorizationBoundaryOwner(AuthorizationBoundary owner) {
        this.authorizationBoundaryOwner = owner;
    }

    public AuthorizationBoundary getAuthorizationBoundaryOwner() {
        return authorizationBoundaryOwner;
    }

    // To String
    public String toString() {
        return text;
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean drawConnectionPoints, PanelWithShapes panel) {
        // Set the color before drawing
        // If selected, mke outline slightly thicker and neon green
        // if (isSelected) {
        // g.setColor(Color.GREEN); // Neon green
        // int radius = (int) (RADIUS * zoomFactor);
        // Point screenPoint = worldToScreen(position);
        // g.drawOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius
        // * 2);

        // getUpdatedPosition();

        // } else {
        g.setColor(color);
        int radius = (int) (RADIUS * zoomFactor);
        Point screenPoint = panel.worldToScreen(position);
        // Fill the circle
        g.fillOval(screenPoint.x - radius, screenPoint.y - radius, radius * 2, radius * 2);

        // }

        // Draw text in the center
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.drawString(text, screenPoint.x - textWidth / 2, screenPoint.y + textHeight / 2);

    }

    // Additional methods specific to Entry_Point can be added here
    // Get Updated Position
    public void updatePosition() {
        /*
         * Recalculate the position of the Entry_Point based on the position of the
         * AuthorizationBoundary and SystemConnection that it belongs to./
         * This is done by finding the intersection of the AuthorizationBoundary and
         * SystemConnection
         * Since the Boundary is a Oval and the Connection is a line, the intersection
         * will be a point
         * Need to account for whether the connection is a right angle or straight line
         */
        // Step 1: Get boundary center and radius (approximating oval as a circle)
        Point boundaryCenter = new Point(
                authorizationBoundaryOwner.getPosition().x + authorizationBoundaryOwner.getWidth() / 2,
                authorizationBoundaryOwner.getPosition().y + authorizationBoundaryOwner.getHeight() / 2);
        int radius = Math.min(authorizationBoundaryOwner.getWidth(), authorizationBoundaryOwner.getHeight()) / 2;

        // Step 2: Get connection line details
        Point connectionStart = systemConnectionOwner.getConnectionStart();
        Point connectionEnd = systemConnectionOwner.getConnectionEnd();
        boolean isRightAngle = systemConnectionOwner.isRightAngle();

        if (isRightAngle) {
            // Calculate intersection for right-angled connection
            // This may involve checking which segment of the right-angled line
            // intersects with the circle
            Point new_position = calculateRightAngleIntersection(authorizationBoundaryOwner, systemConnectionOwner);
            if (new_position != null) {
                position = new_position;
            }
        } else {
            // Calculate intersection for straight line connection
            Point new_position = calculateStraightLineIntersection(authorizationBoundaryOwner, connectionStart, connectionEnd);
            if (new_position != null) {
                position = new_position;
            }
        }

    }

    public Point calculateStraightLineIntersection(AuthorizationBoundary ellipse, Point start, Point end) {
        double a = ellipse.getWidth() / 2.0;
        double b = ellipse.getHeight() / 2.0;
        double h = ellipse.getPosition().x + a;
        double k = ellipse.getPosition().y + b;
    
        // Handle vertical lines separately
        if (start.x == end.x) {
            // Vertical line: x = constant
            double x = start.x;
            // Ellipse equation solved for y
            double term = (1 - Math.pow(x - h, 2) / Math.pow(a, 2)) * Math.pow(b, 2);
            if (term < 0) {
                return null; // No intersection
            }
            double y1 = k + Math.sqrt(term);
            double y2 = k - Math.sqrt(term);
    
            Point point1 = new Point((int) x, (int) y1);
            Point point2 = new Point((int) x, (int) y2);
    
            if (isPointWithinSegment(point1, start, end)) {
                return point1;
            } else if (isPointWithinSegment(point2, start, end)) {
                return point2;
            } else {
                return null; // No intersection within segment
            }
        } else {
            // Non-vertical line: proceed as before
            double epsilon = 1e-6;  // To avoid division by zero
            double m = (double) (end.y - start.y) / (end.x - start.x + epsilon);
            double c = start.y - m * start.x;
    
            double A = b * b + a * a * m * m;
            double B = -2 * h * b * b + 2 * m * a * a * (c - k);
            double C = b * b * h * h + a * a * (c - k) * (c - k) - a * a * b * b;
    
            double delta = B * B - 4 * A * C;
            if (delta < 0) {
                return null; // No intersection
            }
    
            double x1 = (-B + Math.sqrt(delta)) / (2 * A);
            double x2 = (-B - Math.sqrt(delta)) / (2 * A);
    
            double y1 = m * x1 + c;
            double y2 = m * x2 + c;
    
            Point point1 = new Point((int) x1, (int) y1);
            Point point2 = new Point((int) x2, (int) y2);
    
            if (isPointWithinSegment(point1, start, end)) {
                return point1;
            } else if (isPointWithinSegment(point2, start, end)) {
                return point2;
            } else {
                return null; // No intersection within segment
            }
        }
    }
    

    public Point calculateRightAngleIntersection(AuthorizationBoundary ellipse, SystemConnection line) {
        Point start = line.getConnectionStart();
        Point end = line.getConnectionEnd();
        int midX = (start.x + end.x) / 2;
        int midY = (start.y + end.y) / 2;
    
        // Check intersection with first horizontal segment
        Point intersection1 = calculateStraightLineIntersection(ellipse, start, new Point(midX, start.y));
        if (intersection1 != null && isPointWithinSegment(intersection1, start, new Point(midX, start.y))) {
            return intersection1;
        }

        
        // Check intersection with second horizontal segment
        Point intersection3 = calculateStraightLineIntersection(ellipse, new Point(midX, end.y), end);
        if (intersection3 != null && isPointWithinSegment(intersection3, new Point(midX, end.y), end)) {
            return intersection3;
        }
    
        // Check intersection with vertical segment
        Point intersection2 = calculateStraightLineIntersection(ellipse, new Point(midX, start.y), new Point(midX, end.y));
        if (intersection2 != null && isPointWithinSegment(intersection2, new Point(midX, start.y), new Point(midX, end.y))) {
            return intersection2;
        }
    
    
        return null; // No valid intersection found
    }
    
    private boolean isPointWithinSegment(Point intersection, Point segmentStart, Point segmentEnd) {
        // Check if the segment is vertical (constant x-coordinate)
        if (segmentStart.x == segmentEnd.x) {
            // Check if intersection y-coordinate is within segment's y-coordinate range
            return Math.min(segmentStart.y, segmentEnd.y) <= intersection.y && intersection.y <= Math.max(segmentStart.y, segmentEnd.y);
        }
        // Check if the segment is horizontal (constant y-coordinate)
        else if (segmentStart.y == segmentEnd.y) {
            // Check if intersection x-coordinate is within segment's x-coordinate range
            return Math.min(segmentStart.x, segmentEnd.x) <= intersection.x && intersection.x <= Math.max(segmentStart.x, segmentEnd.x);
        }
        // For diagonal segments (not used in right-angle connections, but useful for completeness)
        else {
            return Math.min(segmentStart.x, segmentEnd.x) <= intersection.x && intersection.x <= Math.max(segmentStart.x, segmentEnd.x) &&
                   Math.min(segmentStart.y, segmentEnd.y) <= intersection.y && intersection.y <= Math.max(segmentStart.y, segmentEnd.y);
        }
    }


    //  Setters and getters for the text
    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // Setters and getters for the color
    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    
    
    public JSONObject export_object() {
        JSONObject json = new JSONObject();
    
        // Schema compliant
        ShapeUtils.addAttribute(json, uuid, false, "UUID");
        ShapeUtils.addAttribute(json, systemConnectionOwner != null ? systemConnectionOwner.getUUID() : JSONObject.NULL, false, "System_Connection_ID");
        ShapeUtils.addAttribute(json, text, false, "Text");
        ShapeUtils.addAttribute(json, authorizationBoundaryOwner != null ? authorizationBoundaryOwner.getUUID() : JSONObject.NULL, false, "Authorization_Boundary_ID");


        // Model Forger specific
        ShapeUtils.addAttribute(json, RADIUS, true, "RADIUS");
        ShapeUtils.addAttribute(json, String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()), false, "Color");
        // position
        JSONObject positionJson = new JSONObject();
        ShapeUtils.addAttribute(positionJson, position.x, true, "x");
        ShapeUtils.addAttribute(positionJson, position.y, true, "y");
        ShapeUtils.addAttribute(json, positionJson, true, "Position");

        return json;
    }


}
