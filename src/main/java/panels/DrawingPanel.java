package panels;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import shapes.Shape;
import shapes.AuthorizationBoundary;
import shapes.Circle_Class;
import shapes.Connection_Point_Obj;
import shapes.Interface_Obj;
import shapes.Square_Class;
import shapes.Entry_Point;
import shapes.SystemConnection;
import shapes.System_Obj;
import shapes.tempCircle;

import non_shape_objects.*;

import windows.*;

import com.adkin.cyberdiagramer.CyberDiagramer;

public class DrawingPanel extends JPanel implements PanelWithShapes {
    public final CyberDiagramer shapeDrawer; // Add a reference to ShapeDrawer
    public final ArrayList<Shape> shapes;
    private final ArrayList<SystemConnection> system_connections = new ArrayList<>();
    private Shape draggingShape = null;
    private Shape resizingShape = null;
    private Point handlePressed = null;
    private double zoomFactor = 1.0; // Zoom level
    private int offsetX = 0, offsetY = 0; // Offset of the drawing panel
    private Point dragOffset;

    private boolean isPanning = false;
    private Point lastDragPoint;

    private System_Obj highlightedSystem = null;

    public DrawingPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer; // Store the reference
        shapes = new ArrayList<>();
        setFocusable(true);
        requestFocusInWindow();

        DrawingPanel self = this; // Reference to the DrawingPanel instance


        // /*
        //  * STARTER SHAPES FOR TESTING Remove me in Production
        //  */

        // // Add authorization boundary
        // AuthorizationBoundary boundary = new AuthorizationBoundary(new Point(100, 100));

        // // Add two systems (one with a parent boundary)
        // System_Obj system1 = new System_Obj(new Point(250, 200), self, null);
        // system1.boundary = boundary;
        // shapes.add(boundary);
        // shapes.add(system1);
        // boundary.auto_size(shapes);

        // // Add Interface to system1
        // Interface_Obj inter1 = new Interface_Obj(new Point(340, 250), system1);
        // system1.addInterface(inter1);
        // shapes.add(inter1);

        // System_Obj system2 = new System_Obj(new Point(400, 500), self, null);
        // shapes.add(system2);

        // // Add SystemConnection
        // // Create a new SystemConnection object
        // SystemConnection connection = new SystemConnection(inter1.connectionPoints.get(1),
        //         system2.connectionPoints.get(2), true, true, null);
        // // Add the connection to the list of system connections
        // system_connections.add(connection);

        // // Add an entry point
        // Entry_Point entryPoint = new Entry_Point(new Point(300, 300), connection, boundary);
        // shapes.add(entryPoint);
        // entryPoint.updatePosition();

        

        addMouseListener(new MouseAdapter() {
            /**
             * Called when a mouse button is pressed.
             * 
             * @param e the MouseEvent representing the mouse press event
             */
            @Override
            public void mousePressed(MouseEvent e) {
                // Get the World Point of the mouse press
                Point worldPoint = screenToWorld(e.getX(), e.getY());

                // If Mouse is Selected
                if (shapeDrawer.getButtonSelected().equals("Mouse")) {

                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        // Draw temporary circle to show where the mouse is
                        // shapes.add(new tempCircle(worldPoint));

                        // If double clicked
                        if (e.getClickCount() == 2) {
                            Shape deepestShape = findDeepestShapeUnderPoint(worldPoint, e);

                            // If shape is authorization boundary
                            if (deepestShape instanceof AuthorizationBoundary) {
                                AuthorizationBoundary authBoundary = (AuthorizationBoundary) deepestShape;
                                // Open a dialog to edit the authorization boundary using the updated
                                // constructor
                                AuthorizationBoundaryEditDialog dialog = new AuthorizationBoundaryEditDialog(
                                        shapeDrawer, authBoundary);
                                dialog.setLocationRelativeTo(shapeDrawer);
                                dialog.setVisible(true);

                                // After closing the dialog, the authBoundary object will already have updated
                                // properties
                                // No need to manually set them as they are already set within the dialog

                                repaint();
                                return;
                            }

                            // If shape is a system
                            if (deepestShape instanceof System_Obj) {
                                System_Obj shape = (System_Obj) deepestShape;
                                // Open a dialog to edit the system
                                SystemEditDialog dialog = new SystemEditDialog(shapeDrawer, shape);
                                dialog.setLocationRelativeTo(shapeDrawer);
                                dialog.setVisible(true);

                                Color newColor = dialog.getSelectedColor();
                                String newName = dialog.getEnteredName();
                                String newAcronym = dialog.getEnteredAcronym();

                                if (newColor != null) {
                                    shape.setColor(newColor);
                                }
                                shape.setName(newName);
                                shape.setAcronym(newAcronym);
                                shape.setDisplayAcronym(dialog.getDisplayAcronym());
                                shape.setSelectedTextPosition(dialog.getSelectedTextPosition());
                                repaint();
                                return;
                            }

                            // If shape is a system connection
                            if (deepestShape instanceof SystemConnection) {
                                SystemConnection connection = (SystemConnection) deepestShape;
                                // Open a dialog to edit the connection
                                ConnectionEditDialog dialog = new ConnectionEditDialog(shapeDrawer, connection,
                                        shapeDrawer.operationalDataList);
                                dialog.setLocationRelativeTo(shapeDrawer);
                                dialog.setVisible(true);

                                repaint();
                                return;

                            }

                            if (deepestShape instanceof Entry_Point) {
                                Entry_Point entryPoint = (Entry_Point) deepestShape;
                                // Open a dialog to edit the connection
                                EntryPointEditDialog dialog = new EntryPointEditDialog(shapeDrawer, entryPoint);
                                dialog.setLocationRelativeTo(shapeDrawer);
                                dialog.setVisible(true);

                                repaint();
                                return;

                            }

                            // Notify shape changes
                            notifyShapeChanged();
                        }

                        // Deselct all shapes
                        deselectAll();

                        // Find the deepest shape under the mouse pointer
                        Shape deepestShape = findDeepestShapeUnderPoint(worldPoint, e);

                        if (deepestShape != null) {
                            if (deepestShape.isHandle(worldPoint)) {
                                resizingShape = deepestShape;
                                handlePressed = worldPoint;
                            } else if (deepestShape.isCollapseToggleClicked(worldPoint, self, zoomFactor)) {
                                deepestShape.toggleCollapse();
                            } else {
                                deepestShape.setSelected(true);
                                draggingShape = deepestShape;
                                dragOffset = new Point(worldPoint.x - deepestShape.position.x,
                                        worldPoint.y - deepestShape.position.y);
                            }
                        } else {
                            // Deselect all shapes if no shape is under the mouse pointer
                            for (Shape shape : shapes) {
                                shape.setSelected(false);
                            }
                        }

                    }

                    // If middle mouse button was clicked
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        isPanning = true;
                        lastDragPoint = e.getPoint();

                    }
                }

                else if (shapeDrawer.getButtonSelected() == "System") {
                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        // Add a system but adjust the position so that the mouse is in the center of
                        // instead of top left corner
                        shapes.add(new System_Obj(new Point(worldPoint.x - 50, worldPoint.y - 50), self, null));
                    }
                } else if (shapeDrawer.getButtonSelected() == "Circle") {

                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        shapes.add(new Circle_Class(worldPoint));
                    }
                } else if (shapeDrawer.getButtonSelected() == "Authorization Boundary") {

                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        // Add an authorization boundary but adjust the position so that the mouse is in
                        // the center of instead of top left corner
                        shapes.add(new AuthorizationBoundary(new Point(worldPoint.x - 100, worldPoint.y - 75)));
                    }
                }

                else if (shapeDrawer.getButtonSelected().equals("Connection")) {
                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // If mouse is pressed on a system

                        System_Obj deepestSystem = findDeepestSystemUnderPoint(worldPoint);

                        Connection_Point_Obj deepestConnectionPoint = findDeepestConnectionPointUnderPoint(worldPoint,
                                null);

                        if (deepestConnectionPoint != null) {
                            // Add the system to the connection hopper if it's not already in there
                            if (!shapeDrawer.getConnection_hopper().contains(deepestConnectionPoint)) {
                                shapeDrawer.addConnectionPointToConnectionHopper(
                                        (Connection_Point_Obj) deepestConnectionPoint);
                                deepestConnectionPoint.isConnectionSelected = true;
                            }
                        }
                        // If two systems are in the hopper, make a connection
                        if (shapeDrawer.getConnection_hopper().size() == 2) {
                            Connection_Point_Obj cp1 = shapeDrawer.getConnection_hopper().get(0);
                            Connection_Point_Obj cp2 = shapeDrawer.getConnection_hopper().get(1);
                            // Create a connection between the two systems
                            system_connections.add(new SystemConnection(cp1, cp2, true, true, null));
                            // Deselect all connection points
                            for (Connection_Point_Obj connectionPoint : shapeDrawer.getConnection_hopper()) {
                                connectionPoint.isConnectionSelected = false;
                            }

                            // Clear the connection hopper
                            shapeDrawer.clearConnectionHopper();
                        }
                    }

                } else if (shapeDrawer.getButtonSelected().equals("Interface")) {
                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Check if there is a system under the mouse pointer
                        System_Obj deepestSystem = findDeepestSystemUnderPoint(worldPoint);

                        // If there is a system under the mouse pointer
                        if (deepestSystem instanceof System_Obj) {

                            // Get point on System perimeter closest to mouse pointer
                            Point closestPoint = deepestSystem.getClosestPointOnPerimeter(worldPoint);

                            // TODO: Make this a static variable that can be changed in the settings
                            int interface_default_width = 20;
                            int interface_default_height = 20;

                            // Account for center of the 30w 20h interface
                            closestPoint.translate(-interface_default_width / 2, -interface_default_height / 2);

                            // Create a new Interface_Obj object
                            // Interface_Obj newInterface = new Interface_Obj(new Point(closestPoint.x -
                            // interface_default_width/2, closestPoint.y - interface_default_height/2),
                            // deepestSystem);

                            Interface_Obj newInterface = new Interface_Obj(closestPoint, deepestSystem);

                            // Add the new interface object to the shapes array
                            shapes.add(newInterface);

                            // Add an interface to the system
                            deepestSystem.addInterface(newInterface);

                        }

                    }
                } else if (shapeDrawer.getButtonSelected().equals("Connection Point")) {
                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        // If shift key is pressed
                        if (e.isShiftDown()) {

                            // Check if there is a connection point under the mouse pointer
                            Connection_Point_Obj deepestConnectionPoint = findDeepestConnectionPointUnderPoint(
                                    worldPoint, null);

                            // If there is a connection point under the mouse pointer
                            if (deepestConnectionPoint != null) {

                                // Set the connection point to movement mode
                                deepestConnectionPoint.isMovementMode = true;

                                draggingShape = deepestConnectionPoint;

                                dragOffset = new Point(worldPoint.x - deepestConnectionPoint.position.x,
                                        worldPoint.y - deepestConnectionPoint.position.y);

                                repaint();

                            }

                        } else {

                            // Check if there is a system under the mouse pointer
                            System_Obj deepestSystem = findDeepestSystemUnderPoint(worldPoint);

                            // If there is a system under the mouse pointer
                            if (deepestSystem instanceof System_Obj) {

                                // Get point on System perimeter closest to mouse pointer
                                Point closestPoint = deepestSystem.getClosestPointOnPerimeter(worldPoint);

                                // Account for center of the 30w 20h interface
                                // closestPoint.translate(-2, -2);

                                // Create a new Connection_Point_Obj object
                                Connection_Point_Obj newConnectionPoint = new Connection_Point_Obj(closestPoint,
                                        deepestSystem);

                                // Add an interface to the system
                                deepestSystem.addConnectionPoint(newConnectionPoint);

                            }

                        }

                    }
                } else if (shapeDrawer.getButtonSelected().equals("Entry Point")) {
                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        // Check if the point is on the perimeter of a boundary using isPointOnPerimeter
                        AuthorizationBoundary foundBoundary = null;
                        for (Shape shape : shapes) {
                            if (shape instanceof AuthorizationBoundary) {
                                AuthorizationBoundary boundary = (AuthorizationBoundary) shape;
                                if (boundary.isPointOnPerimeter(worldPoint)) {
                                    foundBoundary = boundary;
                                    break;
                                }
                            }
                        }

                        // Check to see if a connection was found
                        SystemConnection foundConnection = null;
                        for (SystemConnection connection : system_connections) {
                            if (connection.contains(worldPoint, self)) {
                                foundConnection = connection;
                                break;
                            }
                        }

                        // If connection and boundary were both found
                        if (foundBoundary != null && foundConnection != null) {
                            // Create a new entry point
                            shapes.add(new Entry_Point(worldPoint, foundConnection, foundBoundary));

                        }

                    }
                }

                repaint();

                // Notify shape changes
                notifyShapeChanged();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = false;
                }

                if (draggingShape instanceof System_Obj) {
                    System_Obj draggedSystem = (System_Obj) draggingShape;
                    updateSystemParentChildRelationship(draggedSystem);
                    updateSystemBoundaryRelationship(draggedSystem);
                    updateSystemEntryPoints(draggedSystem);
                }

                if (draggingShape instanceof AuthorizationBoundary) {
                    AuthorizationBoundary draggedBoundary = (AuthorizationBoundary) draggingShape;
                    updateBoundaryParentChildRelationship(draggedBoundary);
                    updateBoundaryEntryPoints(draggedBoundary);
                }

                draggingShape = null;
                resizingShape = null;
                handlePressed = null;
                repaint();
            }

            /**
             * Updates the parent-child relationship of a dragged system object.
             * 
             * @param draggedSystem The system object being dragged.
             */
            private void updateSystemParentChildRelationship(System_Obj draggedSystem) {
                // Find the deepest system under the dragged system's position, excluding the
                // dragged system itself
                System_Obj deepestSystemFound = findDeepestSystemUnderPoint(draggedSystem.position, draggedSystem);

                if (deepestSystemFound != null) {
                    // If the dragged system already has a different parent
                    if (draggedSystem.parent != null && draggedSystem.parent != deepestSystemFound) {
                        draggedSystem.parent.children.remove(draggedSystem);
                    }

                    // Set new parent only if it's different
                    if (draggedSystem.parent != deepestSystemFound) {
                        deepestSystemFound.children.add(draggedSystem);
                        draggedSystem.parent = deepestSystemFound;
                    }
                } else if (draggedSystem.parent != null) {
                    // If no new parent found and it already has a parent, remove it from the
                    // current parent
                    draggedSystem.parent.children.remove(draggedSystem);
                    draggedSystem.parent = null;
                }

                // Update the size of the parent if not collapsed
                if (draggedSystem.parent != null && !draggedSystem.parent.collapsed) {
                    draggedSystem.parent.auto_size();
                } // If the parent is collapsed make the dragged system hidden
                else if (draggedSystem.parent != null && draggedSystem.parent.collapsed) {
                    draggedSystem.hidden = true;
                }

            }

            private void updateSystemBoundaryRelationship(System_Obj draggedSystem) {
                AuthorizationBoundary deepestBoundary = findDeepestBoundaryUnderPoint(draggedSystem.position, null);

                if (deepestBoundary != null) {
                    // If the dragged system moves into a new boundary
                    if (draggedSystem.boundary != deepestBoundary) {
                        draggedSystem.boundary = deepestBoundary;
                    }
                    deepestBoundary.auto_size(shapes);
                } else {
                    // If the dragged system moves out of its current boundary
                    if (draggedSystem.boundary != null) {
                        draggedSystem.boundary = null;
                    }

                }
            }

            private void updateBoundaryParentChildRelationship(AuthorizationBoundary draggedBoundary) {
                AuthorizationBoundary deepestBoundary = findDeepestBoundaryUnderPoint(draggedBoundary.position,
                        draggedBoundary);

                if (deepestBoundary != null) {
                    // If the dragged system already has a different parent
                    if (draggedBoundary.parent != null && draggedBoundary.parent != deepestBoundary) {
                        draggedBoundary.parent.children.remove(draggedBoundary);
                    }

                    // Set new parent only if it's different
                    if (draggedBoundary.parent != deepestBoundary) {
                        deepestBoundary.children.add(draggedBoundary);
                        draggedBoundary.parent = deepestBoundary;
                    }
                } else if (draggedBoundary.parent != null) {
                    // If no new parent found and it already has a parent, remove it from the
                    // current parent
                    draggedBoundary.parent.children.remove(draggedBoundary);
                    draggedBoundary.parent = null;
                }

                // Update the size of the parent
                if (draggedBoundary.parent != null) {
                    draggedBoundary.parent.auto_size(shapes);
                }
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning) {
                    Point currentPoint = e.getPoint();
                    // Adjust the offset based on the zoom factor
                    offsetX += (currentPoint.x - lastDragPoint.x) / zoomFactor;
                    offsetY += (currentPoint.y - lastDragPoint.y) / zoomFactor;
                    lastDragPoint = currentPoint;
                    repaint();
                }

                Point worldPoint = screenToWorld(e.getX(), e.getY());
                if (resizingShape != null) {
                    // Handle resizing logic
                    int dx = worldPoint.x - handlePressed.x;
                    int dy = worldPoint.y - handlePressed.y;
                    if (resizingShape instanceof System_Obj) {
                        System_Obj system = (System_Obj) resizingShape;
                        // Make sure the system is not smaller than minimumWidth and minimumHeight
                        if (system.width + dx < system.minimumWidth) {
                            dx = system.minimumWidth - system.width;
                        }
                        if (system.height + dy < system.minimumHeight) {
                            dy = system.minimumHeight - system.height;
                        }
                        system.fixInterfaces(dx, dy);
                        system.fixConnectionPoints(dx, dy);
                        system.width += dx;
                        system.height += dy;
                        handlePressed = worldPoint;
                    } else if (resizingShape instanceof AuthorizationBoundary) {
                        AuthorizationBoundary boundary = (AuthorizationBoundary) resizingShape;
                        boundary.width += dx;
                        boundary.height += dy;
                        handlePressed = worldPoint;
                    }
                    repaint();
                }
                if (draggingShape != null) {
                    Point newPosition = new Point(worldPoint.x - dragOffset.x, worldPoint.y - dragOffset.y);
                    Point delta = new Point(newPosition.x - draggingShape.position.x,
                            newPosition.y - draggingShape.position.y);

                    if (draggingShape instanceof System_Obj) {
                        translateSystemAndChildren((System_Obj) draggingShape, delta);
                    } else if (draggingShape instanceof AuthorizationBoundary) {
                        translateBoundaryAndChildren((AuthorizationBoundary) draggingShape, delta, shapes);
                    } else if (draggingShape instanceof Connection_Point_Obj) {
                        ((Connection_Point_Obj) draggingShape).moveLocation(newPosition);
                    } else if (draggingShape instanceof Interface_Obj) {
                        // translateInterfaceAndChildren((Interface_Obj) draggingShape, delta);
                        ((Interface_Obj) draggingShape).moveLocation(newPosition);

                    } else {
                        draggingShape.position.setLocation(newPosition);
                    }

                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // If connection button is selected
                if (shapeDrawer.getButtonSelected().equals("Connection")) {
                    Point worldPoint = screenToWorld(e.getX(), e.getY());
                    System_Obj deepestSystem = findDeepestSystemUnderPoint(worldPoint);
                    setHighlightedSystem(deepestSystem);
                    repaint();
                }

                // If shapeDrawer.getButtonSelected().equals("Connection Point"
                if (shapeDrawer.getButtonSelected().equals("Connection Point")) {
                    // If the shift key is pressed
                    if (e.isShiftDown()) {
                        Point worldPoint = screenToWorld(e.getX(), e.getY());
                        Connection_Point_Obj deepestConn = findDeepestConnectionPointUnderPoint(worldPoint, null);
                        if (deepestConn != null) {
                            deepestConn.isHighlighted = true;
                        } else {
                            for (Shape shape : shapes) {
                                if (shape instanceof System_Obj) {
                                    System_Obj system = (System_Obj) shape;
                                    for (Connection_Point_Obj connectionPoint : system.connectionPoints) {
                                        connectionPoint.isHighlighted = false;
                                    }
                                }
                            }
                        }
                        repaint();
                    }
                }
            }

        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    zoomFactor *= 1.1; // Zoom in
                } else {
                    zoomFactor /= 1.1; // Zoom out
                }
                repaint();
            }
        });

    }

    protected void updateBoundaryEntryPoints(AuthorizationBoundary draggedBoundary) {
        // Get all entry points that belong to a authorizationBoundaryOwner
        for (Shape shape : shapes) {
            if (shape instanceof Entry_Point) {
                Entry_Point entryPoint = (Entry_Point) shape;
                // If system1_owner or system2_owner is draggedSystem
                if (entryPoint.authorizationBoundaryOwner == draggedBoundary) {
                    // Update the position of the entry point
                    entryPoint.updatePosition();
                }
            }
        }
    }

    protected void updateSystemEntryPoints(System_Obj draggedSystem) {
        // Get all entry points that belong to a connection
        for (Shape shape : shapes) {
            if (shape instanceof Entry_Point) {
                Entry_Point entryPoint = (Entry_Point) shape;
                // If system1_owner or system2_owner is draggedSystem
                if (entryPoint.owner1 == draggedSystem || entryPoint.owner2 == draggedSystem) {
                    // Update the position of the entry point
                    entryPoint.updatePosition();
                }
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // boolean showConnectionPoints =
        // shapeDrawer.getButtonSelected().equals("Connection")
        // || shapeDrawer.getButtonSelected().equals("Connection Point");

        boolean showConnectionPoints = true;

        for (Shape shape : shapes) {
            // If shape is not a Entry_Point_Obj
            if (!(shape instanceof Entry_Point)) {
                shape.draw(g, zoomFactor, showConnectionPoints, this);
            }
        }

        // Draw system connections
        for (SystemConnection connection : system_connections) {
            connection.draw(g, zoomFactor, showConnectionPoints, this);
        }

        // Draw Entry_Point_Obj
        for (Shape shape : shapes) {
            if (shape instanceof Entry_Point) {
                shape.draw(g, zoomFactor, showConnectionPoints, this);
            }
        }

    }

    private System_Obj findDeepestSystemUnderPoint(Point point, System_Obj ignoreSystem) {
        System_Obj deepestSystem = null;
        int maxDepth = -1;
        for (Shape shape : shapes) {
            if (shape instanceof System_Obj && shape != ignoreSystem && shape.contains(point, this)) {
                int depth = calculateSystemDepth((System_Obj) shape);
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepestSystem = (System_Obj) shape;
                }
            }
        }
        return deepestSystem;
    }

    private Connection_Point_Obj findDeepestConnectionPointUnderPoint(Point point,
            Connection_Point_Obj ignoreConnectionPoint) {
        for (Shape shape : shapes) {
            if (!shape.hidden) {
                if (shape instanceof System_Obj) {
                    System_Obj system = (System_Obj) shape;
                    for (Connection_Point_Obj connectionPoint : system.connectionPoints) {
                        if (connectionPoint != ignoreConnectionPoint && connectionPoint.contains(point, this)) {
                            return connectionPoint;
                        }
                    }
                } else if (shape instanceof Interface_Obj) {
                    Interface_Obj inter = (Interface_Obj) shape;
                    for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
                        if (connectionPoint != ignoreConnectionPoint && connectionPoint.contains(point, this)) {
                            return connectionPoint;
                        }
                    }
                }
            }
        }
        return null;
    }

    private int calculateSystemDepth(System_Obj system) {
        int depth = 0;
        System_Obj current = system;
        while (current.parent != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }

    private AuthorizationBoundary findDeepestBoundaryUnderPoint(Point point, AuthorizationBoundary ignoreBoundary) {
        AuthorizationBoundary deepestBoundary = null;
        int maxDepth = -1;
        for (Shape shape : shapes) {
            if (shape instanceof AuthorizationBoundary && shape.contains(point, this) & shape != ignoreBoundary) {
                int depth = calculateBoundaryDepth((AuthorizationBoundary) shape);
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepestBoundary = (AuthorizationBoundary) shape;
                }
            }
        }
        return deepestBoundary;
    }

    private int calculateBoundaryDepth(AuthorizationBoundary boundary) {
        int depth = 0;
        AuthorizationBoundary current = boundary;
        while (current.parent != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }

    // Method to notify ShapeDrawer about shape changes
    public void notifyShapeChanged() {
        shapeDrawer.updateLeftPanel();
    }

    private void translateInterfaceAndChildren(Interface_Obj inter, Point delta) {
        // inter.position.translate(delta.x, delta.y);

        // Move the interface requires a new point so need to calculate the new point of
        // delta + old point
        inter.moveLocation(new Point(inter.position.x + delta.x, inter.position.y + delta.y));

        // Also move connection points
        for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
            connectionPoint.position.translate(delta.x, delta.y);
        }

    }

    private void translateSystemAndChildren(System_Obj system, Point delta) {
        system.position.translate(delta.x, delta.y);
        // Also move interfaces
        for (Interface_Obj inter : system.interfaces) {
            inter.position.translate(delta.x, delta.y);

            // Move the connection points of the interface
            for (Connection_Point_Obj connectionPoint : inter.connectionPoints) {
                connectionPoint.position.translate(delta.x, delta.y);
            }
        }
        // Also move connection points
        for (Connection_Point_Obj connectionPoint : system.connectionPoints) {
            connectionPoint.position.translate(delta.x, delta.y);

            // Move the archived positions (for hidden systems)
            for (Point archivedPosition : connectionPoint.archivedPositions) {
                archivedPosition.translate(delta.x, delta.y);
            }
        }

        // Move entry points associated with this system
        updateSystemEntryPoints(system);

        // Recursively move child systems
        for (System_Obj child : system.getChildren()) {
            translateSystemAndChildren(child, delta); // Recursive call for each child
        }

    }

    private void translateBoundaryAndChildren(AuthorizationBoundary boundary, Point delta, ArrayList<Shape> allShapes) {
        boundary.position.translate(delta.x, delta.y);

        // Move entry points associated with this boundary
        updateBoundaryEntryPoints(boundary);

        // get list of systems that do not have a parent
        ArrayList<System_Obj> systemsWithoutParent = new ArrayList<>();
        for (Shape shape : allShapes) {
            if (shape instanceof System_Obj) {
                System_Obj system = (System_Obj) shape;
                if (system.parent == null) {
                    systemsWithoutParent.add(system);
                }
            }
        }

        // Move all systems associated with this boundary
        for (Shape shape : systemsWithoutParent) {
            if (shape instanceof System_Obj) {
                System_Obj system = (System_Obj) shape;
                if (system.boundary == boundary) {
                    translateSystemAndChildren(system, delta);
                }
            }
        }

        // Recursively move child boundaries and their associated systems
        for (AuthorizationBoundary child : boundary.getChildren()) {
            translateBoundaryAndChildren(child, delta, allShapes);
        }
    }

    private System_Obj findDeepestSystemUnderPoint(Point point) {
        System_Obj deepestSystem = null;
        int maxDepth = -1;
        for (Shape shape : shapes) {
            if (shape instanceof System_Obj && shape.contains(point, this)) {
                int depth = calculateSystemDepth((System_Obj) shape);
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepestSystem = (System_Obj) shape;
                }
            }
        }
        return deepestSystem;
    }

    private Shape findDeepestShapeUnderPoint(Point point, MouseEvent e) {
        // Check for Entry_Point
        for (Shape shape : shapes) {
            if (shape instanceof Entry_Point) {
                Entry_Point entryPoint = (Entry_Point) shape;
                if (entryPoint.contains(point, this)) {
                    return entryPoint;
                }
            }
        }

        // check for SystemConnection
        for (SystemConnection connection : system_connections) {
            if (connection.contains(point, this)) {
                return connection;
            }
        }

        // Check for Interface
        for (Shape shape : shapes) {
            if (shape instanceof Interface_Obj) {
                Interface_Obj inter = (Interface_Obj) shape;
                if (inter.contains(point, this)) {
                    return inter;
                }
            }
        }

        // Next, check for System_Obj and AuthorizationBoundary
        System_Obj deepestSystem = findDeepestSystemUnderPoint(point);
        if (deepestSystem != null) {
            return deepestSystem;
        }

        // If no deepest system is found, check AuthorizationBoundary
        AuthorizationBoundary deepestBoundary = findDeepestBoundaryUnderPoint(point, null);
        if (deepestBoundary != null) {
            return deepestBoundary;
        }

        return null; // No shape found
    }

    private void setHighlightedSystem(System_Obj system) {
        if (highlightedSystem != system) {
            if (highlightedSystem != null) {
                highlightedSystem.isConnectionHovered = false;
            }
            highlightedSystem = system;
            if (highlightedSystem != null) {
                highlightedSystem.isConnectionHovered = true;
            }
        }
    }

    // getShapes function
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public Point screenToWorld(int x, int y) {
        return new Point(
                (int) ((x / zoomFactor) - offsetX),
                (int) ((y / zoomFactor) - offsetY));
    }

    public Point worldToScreen(Point worldPoint) {
        return new Point(
                (int) ((worldPoint.x + offsetX) * zoomFactor),
                (int) ((worldPoint.y + offsetY) * zoomFactor));
    }

    public void deselectAll() {
        for (Shape shape : shapes) {
            shape.setSelected(false);

            // If shape is system object make all the connection points not highlighted and
            // not in movement mode
            if (shape instanceof System_Obj) {
                System_Obj system = (System_Obj) shape;
                for (Connection_Point_Obj connectionPoint : system.connectionPoints) {
                    connectionPoint.isHighlighted = false;
                    connectionPoint.isMovementMode = false;
                }
            }
        }
        for (SystemConnection connection : system_connections) {
            connection.setSelected(false);
        }

        // Clear the connection hopper
        for (Shape shape : shapeDrawer.getConnection_hopper()) {
            if (shape instanceof System_Obj) {
                ((System_Obj) shape).isConnectionSelected = false;
            }
        }

        // Clear the connection hopper
        shapeDrawer.clearConnectionHopper();

        repaint();
    }

    public void loadShapes(List<Shape> loadShapesFromFile) {
        shapes.clear();
        shapes.addAll(loadShapesFromFile);
        repaint();
    }

    public List<SystemConnection> getConnections() {
        return system_connections;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

}
