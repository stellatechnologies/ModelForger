package shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.awt.*;
import panels.DrawingPanel;
import panels.PanelWithShapes;

import utils.CommonUtils;
import utils.ShapeUtils;

public class System_Obj extends Rectangle_Class {
    public ArrayList<System_Obj> children = new ArrayList<>();
    public System_Obj parent = null;
    public AuthorizationBoundary boundary = null;
    public boolean isConnectionHovered = false;
    public boolean isConnectionSelected = false;
    public ArrayList<Connection_Point_Obj> connectionPoints = new ArrayList<>();
    public String name = "";
    public String acronym = "";
    private PanelWithShapes panelWithShapes;
    public boolean collapsed = false;
    public boolean displayAcronym = false;

    private int preCollapsedWidth;
    private int preCollapsedHeight;

    public int minimumWidth = 50;
    public int minimumHeight = 50;

    public int text_x_position = 1;
    public int text_y_position = 1;

    // Collection of Interfaces
    public ArrayList<Interface_Obj> interfaces = new ArrayList<>();


    public System_Obj(Point position, PanelWithShapes panel, ArrayList<Connection_Point_Obj> connectionPoints) {
        super(position, 100, 100);
        // initializeConnectionPoints();

        if (connectionPoints == null) {
            initializeConnectionPoints();
        } else {
            this.connectionPoints = connectionPoints;
        }

        // Random text for the system object
        this.text = String.valueOf((char) (Math.random() * 26 + 'a')) +
                String.valueOf((char) (Math.random() * 26 + 'a')) +
                String.valueOf((char) (Math.random() * 26 + 'a'));

        this.name = this.text;

    }

    @Override
    public String toString() {
        return this.text; // Assuming 'text' is the name of the system
    }

    // add interface
    public void addInterface(Interface_Obj inter) {
        interfaces.add(inter);
    }

    public void addChild(System_Obj child, boolean autoSize) {
        children.add(child);
        child.setParent(this);
        if(autoSize){
            auto_size();
        }
    }

    public void setParent(System_Obj parent) {
        this.parent = parent;
    }

    public System_Obj getParent() {
        return parent;
    }



    public void auto_size() {
        if (!children.isEmpty()) {
            int maxX = position.x + width;
            int maxY = position.y + height;

            for (System_Obj child : children) {
                Point childPos = child.getPosition();
                maxX = Math.max(maxX, childPos.x + child.getWidth());
                maxY = Math.max(maxY, childPos.y + child.getHeight());
            }

            // Check if all children already fit inside the system
            if (maxX > position.x + width || maxY > position.y + height) {

                int newWidth = maxX - position.x + 16; // Added padding
                int newHeight = maxY - position.y + 16; // Added padding

                // Adjust the position of the connection points maintain the relative position
                // to the top left corner of the system
                for (Connection_Point_Obj connectionPoint : connectionPoints) {
                    double percentageX = (double) (connectionPoint.getPosition().x - position.x) / width;
                    double percentageY = (double) (connectionPoint.getPosition().y - position.y) / height;

                    connectionPoint.setPosition(new Point(
                            position.x + (int) (percentageX * newWidth),
                            position.y + (int) (percentageY * newHeight)));

                }

                // Adjust the position of the interfaces maintain the relative position to the
                // top left corner of the system
                for (Interface_Obj inter : interfaces) {
                    double percentageX = (double) ((inter.getPosition().x + inter.getWidth() / 2) - position.x) / width;
                    double percentageY = (double) (inter.getPosition().y + inter.getHeight() / 2 - position.y) / height;

                    int updatedX = 0;
                    int updatedY = 0;

                    // Set the position of the interface first figure out if it is on the left or
                    // right side of the system or top or bottom
                    if (percentageX == 0) {
                        // Left side
                        updatedX = position.x - inter.getWidth() / 2;
                        updatedY = position.y + (int) (percentageY * newHeight);

                    } else if (percentageX == 1) {
                        // Right side
                        updatedX = position.x + newWidth - inter.getWidth() / 2;
                        updatedY = position.y + (int) (percentageY * newHeight);

                    } else if (percentageY == 0) {
                        // Top side
                        updatedX = position.x + (int) (percentageX * newWidth);
                        updatedY = position.y - inter.getHeight() / 2;

                    } else if (percentageY > 0) {
                        // Bottom side
                        updatedX = position.x + (int) (percentageX * newWidth);
                        updatedY = position.y + newHeight - inter.getHeight() / 2;

                    }

                    // Set the position of the connection points of the interface
                    for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
                        double percentageX2 = (double) (connectionPoint.getPosition().x - inter.getPosition().x)
                                / inter.getWidth();
                        double percentageY2 = (double) (connectionPoint.getPosition().y - inter.getPosition().y)
                                / inter.getHeight();

                        connectionPoint.setPosition(new Point(
                                updatedX + (int) (percentageX2 * inter.getWidth()),
                                updatedY + (int) (percentageY2 * inter.getHeight())));
                    }

                    inter.setPosition(new Point(updatedX, updatedY));

                }

                this.width = newWidth;
                this.height = newHeight;

                // If this system has a parent, call auto_size on the parent as well
                if (parent != null) {
                    parent.auto_size();
                }
            }
        } else {
            // Set default size when no children are present
            this.width = 100;
            this.height = 100;
        }

    }

    private void initializeConnectionPoints() {
        // 4 connection points, one on each side of the system

        // Generate a random position for each connection point (percentage between .1
        // and .9)
        // int xPerc = (int) (Math.random() * (width - 20) + 10);
        // int yPerc = (int) (Math.random() * (height - 20) + 10);

        // connectionPoints.add(new Connection_Point_Obj(new Point(position.x + xPerc,
        // position.y), this)); // Top

        connectionPoints.add(new Connection_Point_Obj(new Point(position.x + width / 2, position.y), this)); // Top
        connectionPoints.add(new Connection_Point_Obj(new Point(position.x + width / 2, position.y + height), this)); // Bottom
        connectionPoints.add(new Connection_Point_Obj(new Point(position.x, position.y + height / 2), this)); // Left
        connectionPoints.add(new Connection_Point_Obj(new Point(position.x + width, position.y + height / 2), this)); // Right

    }

    public void setBoundary(AuthorizationBoundary boundary) {
        this.boundary = boundary;
    }

    public void fixInterfaces(int dx, int dy) {
        // Make sure all interfaces are always on the perimeter of the system
        for (Interface_Obj inter : interfaces) {
            // inter.getPosition());
            Point closestPoint = getClosestPointOnPerimeter(new Point(inter.getPosition().x + inter.getWidth() / 2,
                    inter.getPosition().y + inter.getHeight() / 2));

            inter.setPosition(new Point(closestPoint.x - inter.getWidth() / 2, closestPoint.y - inter.getHeight() / 2));
            inter.fixConnectionPoints();
        }
    }

    public void fixConnectionPoints(int dx, int dy) {

        // Determine what the new size of the system will be
        int newWidth = width + dx;
        int newHeight = height + dy;

        // Adjust the position of the connection points maintain the relative position
        // to the top left corner of the system
        for (Connection_Point_Obj connectionPoint : connectionPoints) {

            double percentageX = Math.round(((double) (connectionPoint.getPosition().x - position.x) / width) * 100.0)
                    / 100.0;
            double percentageY = Math.round(((double) (connectionPoint.getPosition().y - position.y) / height) * 100.0)
                    / 100.0;

            int updatedX = 0;
            int updatedY = 0;

            // If connection point y == system y then top side
            if (connectionPoint.getPosition().y == position.y) {
                // Top side
                updatedX = position.x + (int) (percentageX * newWidth);
                updatedY = position.y;

                connectionPoint.setPosition(new Point(updatedX, updatedY));
            } else if (connectionPoint.getPosition().y == position.y + height) {
                // Bottom side
                updatedX = position.x + (int) (percentageX * newWidth);
                updatedY = position.y + newHeight;

                connectionPoint.setPosition(new Point(updatedX, updatedY));
            } else if (connectionPoint.getPosition().x == position.x) {
                // Left side
                updatedX = position.x;
                updatedY = position.y + (int) (percentageY * newHeight);

                connectionPoint.setPosition(new Point(updatedX, updatedY));
            } else if (connectionPoint.getPosition().x == position.x + width) {
                // Right side
                updatedX = position.x + newWidth;
                updatedY = position.y + (int) (percentageY * newHeight);

                connectionPoint.setPosition(new Point(updatedX, updatedY));
            }
        }

    }

    @Override
    public boolean contains(Point point, PanelWithShapes panel) {
        return super.contains(point, panel); // Use Rectangle_Class's contains method
    }

    // getPosition
    public Point getPosition() {
        return position;
    }

    @Override
    public void draw(Graphics g, double zoomFactor, boolean showConnectionPoints, PanelWithShapes panel) {
        

        if (!hidden) {
           
            // Adjust width and height for collapsed state
            int displayWidth = width;
            int displayHeight = height;

            super.draw(g, zoomFactor, showConnectionPoints, panel); // Call the base class draw method

            // Draw collapse/expand toggle
            drawCollapseToggle(g, zoomFactor, panel);

            // Draw text in the center of the rectangle
            g.setColor(Color.BLACK);
            FontMetrics metrics = g.getFontMetrics();

            int textHeight = metrics.getHeight();
            Point centerPoint = panel.worldToScreen(new Point(position.x + width / 2, position.y + height / 2));

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

            // Middle X position
            if (text_x_position == 1) {
                text_x_draw_pos = centerPoint.x - textWidth / 2;
            }
            // Left X Position
            else if (text_x_position == 0) {
                text_x_draw_pos = panel.worldToScreen(new Point(position.x, position.y)).x + 5;
            }
            // Right X Position
            else {
                text_x_draw_pos = panel.worldToScreen(new Point(position.x + width, position.y)).x - textWidth;
            }

            // Middle Y Position
            if (text_y_position == 1) {
                text_y_draw_pos = centerPoint.y + textHeight / 2;
            }
            // Top Y Position
            else if (text_y_position == 0) {
                // text_y_draw_pos = position.y + height / 20 + textHeight / 2;
                text_y_draw_pos = panel.worldToScreen(new Point(position.x, position.y)).y + textHeight;
            }
            // Bottom Y Position
            else {
                // text_y_draw_pos = position.y + (19 * height / 20) - textHeight / 4;
                text_y_draw_pos = panel.worldToScreen(new Point(position.x, position.y + height)).y - textHeight / 4;
            }

            g.drawString(text_to_draw, text_x_draw_pos, text_y_draw_pos);

            // Calculate the adjusted width and height based on the zoom factor
            int adjustedWidth = (int) (displayWidth * zoomFactor);
            int adjustedHeight = (int) (displayHeight * zoomFactor);

            // Calculate the adjusted screen coordinates based on the zoom factor
            int adjustedX = (int) (centerPoint.x - adjustedWidth / 2);
            int adjustedY = (int) (centerPoint.y - adjustedHeight / 2);

            // Draw neon green outline if isSelected is true
            if (isSelected) {
                g.setColor(Color.GREEN);
            }
            // Draw neon blue highlight if isConnectionHovered is true
            else if (isConnectionHovered) {
                // set color to light blue #89CFF0
                g.setColor(new Color(137, 207, 240));
            } else if (isConnectionSelected) {
                // set color to light blue #89CFF0
                g.setColor(Color.BLUE);
            } else {
                g.setColor(color);
            }

            Graphics2D g2d = (Graphics2D) g;
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(adjustedX, adjustedY, adjustedWidth, adjustedHeight);
            g2d.setStroke(originalStroke);

        }

        // Draw Connection Points if showConnectionPoints is true
        if (showConnectionPoints) {
            for (Connection_Point_Obj connectionPoint : connectionPoints) {
                // Draw the connection point
                connectionPoint.draw(g, zoomFactor, showConnectionPoints, panel);
            }
        }

    }

    private void drawCollapseToggle(Graphics g, double zoomFactor, PanelWithShapes panel) {
        int boxSize = (int) (20 * zoomFactor); // Adjust box size based on zoom factor

        // Calculate the position of the toggle box, considering zoom
        Point screenPoint = panel.worldToScreen(new Point(
                position.x + width - (int) (boxSize / zoomFactor), // Adjust x-position for top-right corner
                position.y // y-position remains the same
        ));

        // Draw the toggle box
        g.setColor(Color.GRAY);
        g.fillRect(screenPoint.x, screenPoint.y, boxSize, boxSize);

        // Draw the minus or plus sign inside the box
        int lineLength = (int) (10 * zoomFactor); // Length of the lines in the toggle box
        int lineOffset = (boxSize - lineLength) / 2; // Position offset for the lines
        g.setColor(Color.BLACK);
        g.drawLine(screenPoint.x + lineOffset, screenPoint.y + boxSize / 2,
                screenPoint.x + lineOffset + lineLength, screenPoint.y + boxSize / 2); // Horizontal line

        if (collapsed) {
            g.drawLine(screenPoint.x + boxSize / 2, screenPoint.y + lineOffset,
                    screenPoint.x + boxSize / 2, screenPoint.y + lineOffset + lineLength); // Vertical line
        }
    }

    public System_Obj[] getChildren() {
        return children.toArray(new System_Obj[0]);
    }

    public void resize(int dWidth, int dHeight) {
        // Check if the new width and height are greater than the minimum width and
        // height
        if (width + dWidth < minimumWidth || height + dHeight < minimumHeight) {
            return;
        }

        this.width += dWidth;
        this.height += dHeight;
    }

    // public List<System_Obj> getParents() {
    //     List<System_Obj> parents = new ArrayList<>();
    //     System_Obj current = this.parent;
    //     while (current != null) {
    //         parents.add(current);
    //         current = current.parent;
    //     }
    //     return parents;
    // }

    public List<System_Obj> getAllDescendants() {
        List<System_Obj> allDescendants = new ArrayList<>();
        for (System_Obj child : children) {
            allDescendants.add(child);
            allDescendants.addAll(child.getAllDescendants());
        }
        return allDescendants;
    }

    public void hide_children_systems() {
        for (System_Obj child : getChildren()) {
            // child.hidden = true;

            child.setHiddenStatus(true);

            // Hide all interfaces
            for (Interface_Obj inter : child.interfaces) {
                // inter.hidden = true;
                inter.setHiddenStatus(true);
            }

            // Get the first non hidden parent
            System_Obj parent = child.parent;
            while (parent != null && parent.hidden) {
                parent = parent.parent;
            }

            // Push the connection points of the child to the first non hidden parent
            for (Connection_Point_Obj connectionPoint : child.connectionPoints) {
                // Archive the old position archivedPositions
                connectionPoint.archivedPositions.add(connectionPoint.getPosition());

                // if connectionPoint.archivedPositionPercentage is empty new ArrayList<>();
                if (connectionPoint.archivedPositionPercentage.isEmpty()) {
                    // Save the percentage of the connection point on the child
                    double percentageX = (double) (connectionPoint.getPosition().x - child.position.x) / child.width;
                    double percentageY = (double) (connectionPoint.getPosition().y - child.position.y) / child.height;
                    connectionPoint.archivedPositionPercentage.add(percentageX);
                    connectionPoint.archivedPositionPercentage.add(percentageY);
                }

                // Set the position of the connection point to the first non hidden parent
                connectionPoint.setPosition(new Point(
                        (int) (parent.position.x + parent.width * connectionPoint.archivedPositionPercentage
                                .get(connectionPoint.archivedPositionPercentage.size() - 2)),
                        (int) (parent.position.y + parent.height * connectionPoint.archivedPositionPercentage
                                .get(connectionPoint.archivedPositionPercentage.size() - 1))));

            }

            // Recursively hide all descendants
            child.hide_children_systems();

        }
    }

    public void show_children_systems() {
        for (System_Obj child : getChildren()) {
            if (!child.collapsed) {

                // child.hidden = false;
                child.setHiddenStatus(false);

                // Show all interfaces
                for (Interface_Obj inter : child.interfaces) {
                    // inter.hidden = false;
                    inter.setHiddenStatus(false);
                }

                // Push the connection points back to their original position
                fix_connection_points(child);

                // Recursively show all descendants
                child.show_children_systems();
            } else {

                // child.hidden = false;
                child.setHiddenStatus(false);

                // Hide all interfaces
                for (Interface_Obj inter : child.interfaces) {
                    // inter.hidden = true;
                    inter.setHiddenStatus(true);
                }

                fix_connection_points(child);

                if (child.getChildren().length > 0) {
                    for (System_Obj subChild : child.getChildren()) {
                        // subChild.setHiddenStatus(true);
                        fix_connection_points(subChild);
                    }
                }

            }
        }
    }

    public void fix_connection_points(System_Obj system) {
        for (Connection_Point_Obj connectionPoint : system.connectionPoints) {
            // connectionPoint.setPosition(connectionPoint.archivedPosition);
            // // Clear the archived position
            // connectionPoint.archivedPosition = null;

            // Pop the latest archived position
            if (!connectionPoint.archivedPositions.isEmpty()) {
                connectionPoint.setPosition(
                        connectionPoint.archivedPositions.get(connectionPoint.archivedPositions.size() - 1));
                connectionPoint.archivedPositions.remove(connectionPoint.archivedPositions.size() - 1);

                // Pop the latest archived position percentage
                if (!connectionPoint.archivedPositionPercentage.isEmpty()) {
                    connectionPoint.archivedPositionPercentage
                            .remove(connectionPoint.archivedPositionPercentage.size() - 1);
                    connectionPoint.archivedPositionPercentage
                            .remove(connectionPoint.archivedPositionPercentage.size() - 1);

                }
            }
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

    public void addConnectionPoint(Connection_Point_Obj newConnectionPoint) {
        connectionPoints.add(newConnectionPoint);
    }


    public void setUUID(String uUID) {
        this.uuid = uUID;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed, boolean autoSize) {
        this.collapsed = collapsed;
        if(autoSize){
            auto_size();
        }
    }

    @Override
    public void toggleCollapse() {

        collapsed = !collapsed;

        // If collapsed, save the pre-collapsed width and height
        if (collapsed) {
            // preCollapsedWidth = preCollapsedWidth == 0 ? width : preCollapsedWidth;
            // preCollapsedHeight = preCollapsedHeight == 0 ? height : preCollapsedHeight;

            preCollapsedWidth = width;
            preCollapsedHeight = height;

            // Adjust the position of the connection points maintain the relative position
            // to the top left corner of the system
            for (Connection_Point_Obj connectionPoint : connectionPoints) {
                double percentageX = (double) (connectionPoint.getPosition().x - position.x) / width;
                double percentageY = (double) (connectionPoint.getPosition().y - position.y) / height;

                connectionPoint.setPosition(new Point(position.x + (int) (percentageX * 50),
                        position.y + (int) (percentageY * 50)));
            }

            // Adjust the position of the interfaces maintain the relative position to the
            // top left corner of the system
            for (Interface_Obj inter : interfaces) {
                double percentageX = (double) ((inter.getPosition().x + inter.getWidth() / 2) - position.x) / width;
                double percentageY = (double) (inter.getPosition().y + inter.getHeight() / 2 - position.y) / height;

                int updatedX = 0;
                int updatedY = 0;

                // Set the position of the interface first figure out if it is on the left or
                // right side of the system or top or bottom
                if (percentageX == 0) {
                    // Left side
                    updatedX = position.x - inter.getWidth() / 2;
                    updatedY = position.y + (int) (percentageY * 50) - inter.getHeight() / 2;

                } else if (percentageX == 1) {
                    // Right side
                    updatedX = position.x + 50 - inter.getWidth() / 2;
                    updatedY = position.y + (int) (percentageY * 50) - inter.getHeight() / 2;

                } else if (percentageY == 0) {
                    // Top side
                    updatedX = position.x + (int) (percentageX * 50) - inter.getWidth() / 2;
                    updatedY = position.y - inter.getHeight() / 2;

                } else if (percentageY > 0) {
                    // Bottom side
                    updatedX = position.x + (int) (percentageX * 50) - inter.getWidth() / 2;
                    updatedY = position.y + 50 - inter.getHeight() / 2;

                }

                // Set the position of the connection points of the interface
                for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
                    double percentageX2 = (double) (connectionPoint.getPosition().x - inter.getPosition().x)
                            / inter.getWidth();
                    double percentageY2 = (double) (connectionPoint.getPosition().y - inter.getPosition().y)
                            / inter.getHeight();

                    connectionPoint.setPosition(new Point(
                            updatedX + (int) (percentageX2 * inter.getWidth()),
                            updatedY + (int) (percentageY2 * inter.getHeight())));
                }

                inter.setPosition(new Point(updatedX, updatedY));

            }

            width = 50;
            height = 50;
            hide_children_systems();

        } else {

            // Adjust the position of the connection points maintain the relative position
            // to the top left corner of the system
            for (Connection_Point_Obj connectionPoint : connectionPoints) {
                double percentageX = (double) (connectionPoint.getPosition().x - position.x) / width;
                double percentageY = (double) (connectionPoint.getPosition().y - position.y) / height;

                connectionPoint.setPosition(new Point(position.x + (int) (percentageX * preCollapsedWidth),
                        position.y + (int) (percentageY * preCollapsedHeight)));

            }

            // Adjust the position of the interfaces maintain the relative position to the
            // top left corner of the system
            for (Interface_Obj inter : interfaces) {
                double percentageX = (double) ((inter.getPosition().x + inter.getWidth() / 2) - position.x) / width;
                double percentageY = (double) (inter.getPosition().y + inter.getHeight() / 2 - position.y) / height;

                int updatedX = 0;
                int updatedY = 0;

                // Set the position of the interface first figure out if it is on the left or
                // right side of the system or top or bottom
                if (percentageX == 0) {
                    // Left side
                    updatedX = position.x - inter.getWidth() / 2;
                    updatedY = position.y + (int) (percentageY * preCollapsedHeight) - inter.getHeight() / 2;

                } else if (percentageX == 1) {
                    // Right side
                    updatedX = position.x + preCollapsedWidth - inter.getWidth() / 2;
                    updatedY = position.y + (int) (percentageY * preCollapsedHeight) - inter.getHeight() / 2;

                } else if (percentageY == 0) {
                    // Top side
                    updatedX = position.x + (int) (percentageX * preCollapsedWidth) - inter.getWidth() / 2;
                    updatedY = position.y - inter.getHeight() / 2;

                } else if (percentageY > 0) {
                    // Bottom side
                    updatedX = position.x + (int) (percentageX * preCollapsedWidth) - inter.getWidth() / 2;
                    updatedY = position.y + preCollapsedHeight - inter.getHeight() / 2;

                }

                // Set the position of the connection points of the interface
                for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
                    double percentageX2 = (double) (connectionPoint.getPosition().x - inter.getPosition().x)
                            / inter.getWidth();
                    double percentageY2 = (double) (connectionPoint.getPosition().y - inter.getPosition().y)
                            / inter.getHeight();

                    connectionPoint.setPosition(new Point(
                            updatedX + (int) (percentageX2 * inter.getWidth()),
                            updatedY + (int) (percentageY2 * inter.getHeight())));
                }

                inter.setPosition(new Point(updatedX, updatedY));

            }

            width = preCollapsedWidth;
            height = preCollapsedHeight;
            show_children_systems();

            auto_size();
        }

    }

    // Check if collapsed button is clicked
    @Override
    public boolean isCollapseToggleClicked(Point point, PanelWithShapes panel, double zoomFactor) {
        int boxSize = (int) (20 * zoomFactor); // Adjust box size based on zoom factor
        Point screenPoint = panel.worldToScreen(new Point(
                position.x + width - (int) (boxSize / zoomFactor), // Adjust x-position for top-right corner
                position.y // y-position remains the same
        ));

        Point mousePoint = panel.worldToScreen(point);

        return screenPoint.x <= mousePoint.x && mousePoint.x <= screenPoint.x + boxSize && screenPoint.y <= mousePoint.y
                && mousePoint.y <= screenPoint.y + boxSize;
    }

    public String getName() {
        return name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setName(String name) {
        this.name = name;
        this.text = name;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Boolean getDisplayAcronym() {
        return displayAcronym;
    }

    public void setDisplayAcronym(Boolean displayAcronym) {
        this.displayAcronym = displayAcronym;
    }

    public Point getSelectedTextPosition() {
        return new Point(text_x_position, text_y_position);
    }

    public void setSelectedTextPosition(Point selectedPos) {
        text_x_position = selectedPos.x;
        text_y_position = selectedPos.y;
    }

    public JSONObject export_object() {
        JSONObject json = new JSONObject();


        // Schema compliant
        ShapeUtils.addAttribute(json, uuid, false, "UUID");
        ShapeUtils.addAttribute(json, name, false, "Name");
        ShapeUtils.addAttribute(json, acronym, false, "Acronym");
        ShapeUtils.addAttribute(json, parent != null ? parent.getIdentifier() : "", false, "Parent_ID");
        ShapeUtils.addAttribute(json, boundary == null ? null : boundary.getIdentifier(), false, "AuthorizationBoundary_ID");
        ShapeUtils.addAttribute(json, null, true, "Description");

        ShapeUtils.addAttribute(json, width, true, "width");
        ShapeUtils.addAttribute(json, height, true, "height");
        ShapeUtils.addAttribute(json, position.x, true, "x");
        ShapeUtils.addAttribute(json, position.y, true, "y");
        ShapeUtils.addAttribute(json, "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")", true, "color");
        ShapeUtils.addAttribute(json, text_x_position, true, "text_x_position");
        ShapeUtils.addAttribute(json, text_y_position, true, "text_y_position");
        ShapeUtils.addAttribute(json, displayAcronym, true, "displayAcronym");
        ShapeUtils.addAttribute(json, collapsed, true, "collapsed");
        ShapeUtils.addAttribute(json, preCollapsedWidth, true, "preCollapsedWidth");
        ShapeUtils.addAttribute(json, preCollapsedHeight, true, "preCollapsedHeight");
        ShapeUtils.addAttribute(json, hidden, true, "hidden");

        
        // Add list of connection point export objects
        List<JSONObject> connectionPointExportObjects = new ArrayList<>();
        for (Connection_Point_Obj connectionPoint : connectionPoints) {
            connectionPointExportObjects.add(connectionPoint.export_object());
        }
        
        ShapeUtils.addAttribute(json, connectionPointExportObjects, true, "connectionPoints");


        return json;
        
    }

    public void clearConnectionPoints() {
        connectionPoints.clear();
    }
    

    public void setConnectionPoints(ArrayList<Connection_Point_Obj> connectionPoints2) {
        this.connectionPoints = connectionPoints2;
    }

    public void setPreCollapsedWidth(int preCollapsedWidth) {
        this.preCollapsedWidth = preCollapsedWidth;
    }

    public void setPreCollapsedHeight(int preCollapsedHeight) {
        this.preCollapsedHeight = preCollapsedHeight;
    }
}
