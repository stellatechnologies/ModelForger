package panels;

import javax.swing.*;

import org.w3c.dom.Node;

import java.awt.*;
import java.awt.event.*;
import java.rmi.server.Operation;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import shapes.Shape;
import windows.*;

import com.adkin.cyberdiagramer.CyberDiagramer;

import non_shape_objects.Connection_Object;
import non_shape_objects.Mission;
import non_shape_objects.Mission_Connection;
import non_shape_objects.OpData_Miss_Conn;
import non_shape_objects.OperationalData;

public class MissionHierarchyPanel extends JPanel implements PanelWithShapes {
    public final CyberDiagramer shapeDrawer; // Add a reference to ShapeDrawer
    public final ArrayList<Shape> shapes;
    public final ArrayList<Mission_Connection> mission_connections = new ArrayList<>();
    public final ArrayList<OpData_Miss_Conn> data_miss_connections = new ArrayList<>();
    private Shape draggingShape = null;
    private Shape resizingShape = null;
    private Point handlePressed = null;
    private double zoomFactor = 1.0; // Zoom level
    private int offsetX = 0, offsetY = 0; // Offset of the drawing panel
    private Point dragOffset;

    private boolean isPanning = false;
    private Point lastDragPoint;

    Timer physicsTimer = new Timer(16, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (shapeDrawer.isPhysicsOn()) {
                updatePhysics();
            }
            repaint();
        }
    });

    public MissionHierarchyPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer; // Store the reference
        shapes = new ArrayList<>();
        setFocusable(true);
        requestFocusInWindow();

        MissionHierarchyPanel self = this; // Reference to the DrawingPanel instance

        physicsTimer.start();

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

                        // If double clicked
                        if (e.getClickCount() == 2) {
                            Shape deepestShape = findDeepestShapeUnderPoint(worldPoint, e);

                            if (deepestShape instanceof OperationalData) {
                                OperationalDataDialog dialog = new OperationalDataDialog(shapeDrawer,
                                        (OperationalData) deepestShape);
                                dialog.setVisible(true);
                                OperationalData updatedData = dialog.getOperationalData();
                            }

                            else if (deepestShape instanceof Mission) {
                                MissionDialog dialog = new MissionDialog(shapeDrawer, (Mission) deepestShape);
                                dialog.setVisible(true);
                                Mission updatedMission = dialog.getMission();
                            }
                        }

                        // Deselct all shapes
                        deselectAll();

                        // Find the deepest shape under the mouse pointer
                        Shape deepestShape = findDeepestShapeUnderPoint(worldPoint, e);

                        if (deepestShape != null) {
                            if (deepestShape.isHandle(worldPoint)) {
                                resizingShape = deepestShape;
                                handlePressed = worldPoint;
                            } else {
                                // Print the deepest shape found and it's class
                                updateSelectedMissionOpData(deepestShape);
                                deepestShape.setSelected(true);
                                draggingShape = deepestShape;
                                dragOffset = new Point(worldPoint.x - deepestShape.position.x,
                                        worldPoint.y - deepestShape.position.y);
                            }
                        } else {

                        }

                    }

                    // If middle mouse button was clicked
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        isPanning = true;
                        lastDragPoint = e.getPoint();

                    }
                }

                else if (shapeDrawer.getButtonSelected() == "Operational Data") {
                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        shapeDrawer.operationalDataList.add(new OperationalData("", "", 1, 1, 1,
                                new Point(worldPoint.x, worldPoint.y), ""));
                    }
                }

                else if (shapeDrawer.getButtonSelected() == "Mission") {
                    // If the left buton was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        shapeDrawer.missionList
                                .add(new Mission("", "", new Point(worldPoint.x, worldPoint.y)));
                    }
                } else if (shapeDrawer.getButtonSelected().equals("Mission_Connection")) {
                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // If mouse is pressed on a system

                        Mission deepestMission = findDeepestMissionUnderPoint(worldPoint, null);

                        if (deepestMission != null) {
                            // Add the mission to the missino connection hopper if it's not already in there
                            if (!shapeDrawer.getMissionConnection_hopper().contains(deepestMission)) {
                                shapeDrawer.addMissionToMissionConnectionHopper((Mission) deepestMission);
                                deepestMission.isConnectionSelected = true;
                            }
                        }
                        // If two systems are in the hopper, make a connection
                        if (shapeDrawer.getMissionConnection_hopper().size() == 2) {
                            // Create a connection between the two systems
                            mission_connections
                                    .add(new Mission_Connection(shapeDrawer.getMissionConnection_hopper().get(0),
                                            shapeDrawer.getMissionConnection_hopper().get(1)));
                            // Deselect all connection points
                            for (Mission miss : shapeDrawer.getMissionConnection_hopper()) {
                                miss.isConnectionSelected = false;
                            }

                            // Clear the connection hopper
                            shapeDrawer.mission_connection_hopper.clear();
                        }
                    }

                }

                else if (shapeDrawer.getButtonSelected().equals("Data_Mission_Connection")) {
                    // If the left button was clicked
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // If mouse is pressed on a system
                        Mission deepestMission = findDeepestMissionUnderPoint(worldPoint, null);

                        OperationalData deepestData = findDeepestOpDataUnderPoint(worldPoint, null);

                        if (deepestMission != null) {
                            // Add the mission to the missino connection hopper if it's not already in there
                            if (shapeDrawer.mission_data_connection_hopper.connectionEnd == null) {
                                shapeDrawer.mission_data_connection_hopper.connectionEnd = deepestMission;
                                deepestMission.isConnectionSelected = true;
                            }
                        }
                        if (deepestData != null) {
                            // Add the operational data to the missino connection hopper if it's not already
                            // in there
                            if (shapeDrawer.mission_data_connection_hopper.connectionStart == null) {
                                shapeDrawer.mission_data_connection_hopper.connectionStart = deepestData;
                                deepestData.isConnectionSelected = true;
                            }
                        }

                        // If two systems are in the hopper, make a connection
                        if (shapeDrawer.mission_data_connection_hopper.connectionStart != null
                                && shapeDrawer.mission_data_connection_hopper.connectionEnd != null) {

                            // Create a connection between the two systems
                            data_miss_connections.add(
                                    new OpData_Miss_Conn(shapeDrawer.mission_data_connection_hopper.connectionStart,
                                            shapeDrawer.mission_data_connection_hopper.connectionEnd));

                            OperationalData opDataPoint = (OperationalData) shapeDrawer.mission_data_connection_hopper.connectionStart;
                            Mission missPoint = (Mission) shapeDrawer.mission_data_connection_hopper.connectionEnd;

                            opDataPoint.setConnectionSelected(false);
                            missPoint.setConnectionSelected(false);

                            // Clear the connection hopper
                            shapeDrawer.mission_data_connection_hopper.clear();

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

                draggingShape = null;
                resizingShape = null;
                handlePressed = null;
                repaint();
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (isPanning) {
                    Point currentPoint = e.getPoint();
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

                    repaint();
                }
                if (draggingShape != null) {
                    Point newPosition = new Point(worldPoint.x - dragOffset.x, worldPoint.y - dragOffset.y);

                    draggingShape.position.setLocation(newPosition);

                    repaint();
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

    private void updatePhysics() {
        for (Mission_Connection connection : mission_connections) {
            applySpringForce(connection);
        }

        // data_miss_connections
        for (OpData_Miss_Conn connection : data_miss_connections) {
            applySpringForce(connection);
        }

        for (Shape shape : shapeDrawer.operationalDataList) {
            for (Shape shape2 : shapeDrawer.operationalDataList) {
                if (shape != shape2) {
                    applyRepulsion(shape, shape2);
                }
            }
        }

        for (Shape shape : shapeDrawer.missionList) {
            for (Shape shape2 : shapeDrawer.missionList) {
                if (shape != shape2) {
                    applyRepulsion(shape, shape2);
                }
            }
        }

        for (Shape shape : shapeDrawer.operationalDataList) {

            updatePosition(shape);
        }

        for (Shape shape : shapeDrawer.missionList) {
            updatePosition(shape);
        }

    }

    private void updatePosition(Shape node) {
        final double damping = 0.9;
        node.position.x += node.velocity.x;
        node.position.y += node.velocity.y;

        // Apply damping
        node.velocity.x *= damping;
        node.velocity.y *= damping;
    }

    private void applyRepulsion(Shape node1, Shape node2) {
        final double repulsionConstant = 500;
        Point vector = new Point(node2.position.x - node1.position.x,
                node2.position.y - node1.position.y);
        double distance = vector.distance(0, 0);
        double forceMagnitude = repulsionConstant / (distance * distance);

        Point force = new Point((int) (forceMagnitude * vector.x / distance),
                (int) (forceMagnitude * vector.y / distance));

        node1.velocity.x -= force.x;
        node1.velocity.y -= force.y;
        node2.velocity.x += force.x;
        node2.velocity.y += force.y;
    }

    private void applySpringForce(Connection_Object connection) {
        final double springConstant = 0.2; // Adjust for springiness
        final double restLength = 1000; // Resting length of the spring
        final double maxForce = 100; // Maximum force a spring can exert

        Point vector = new Point(connection.connectionEnd.position.x - connection.connectionStart.position.x,
                connection.connectionEnd.position.y - connection.connectionStart.position.y);
        double distance = vector.distance(0, 0) - restLength;
        double forceMagnitude = springConstant * distance;

        // Limit the force to avoid snapping
        forceMagnitude = Math.min(Math.max(forceMagnitude, -maxForce), maxForce);

        // Apply force to end nodes
        Point force = new Point((int) (forceMagnitude * vector.x / vector.distance(0, 0)),
                (int) (forceMagnitude * vector.y / vector.distance(0, 0)));

        connection.connectionStart.velocity.x += force.x;
        connection.connectionStart.velocity.y += force.y;
        connection.connectionEnd.velocity.x -= force.x;
        connection.connectionEnd.velocity.y -= force.y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean showConnectionPoints = shapeDrawer.getButtonSelected().equals("Connection")
                || shapeDrawer.getButtonSelected().equals("Connection Point");

        for (Shape shape : shapeDrawer.operationalDataList) {

            shape.draw(g, zoomFactor, showConnectionPoints, this);
        }

        for (Shape shape : shapeDrawer.missionList) {
            shape.draw(g, zoomFactor, showConnectionPoints, this);
        }

        // Draw system connections
        for (Mission_Connection connection : mission_connections) {
            connection.draw(g, zoomFactor, showConnectionPoints, this);
        }

        // data_miss_connections
        for (OpData_Miss_Conn connection : data_miss_connections) {
            connection.draw(g, zoomFactor, showConnectionPoints, this);
        }

    }

    private Mission findDeepestMissionUnderPoint(Point point, Mission ignoreMission) {
        // Check for Mission
        for (Shape shape : shapeDrawer.missionList) {
            if (shape instanceof Mission) {
                if (shape != ignoreMission) {
                    Mission miss = (Mission) shape;
                    if (miss.contains(point, this)) {
                        return miss;
                    }

                }
            }
        }

        return null; // No shape found

    }

    private OperationalData findDeepestOpDataUnderPoint(Point point, OperationalData ignoreOpData) {
        // Check for Mission
        for (Shape shape : shapeDrawer.operationalDataList) {
            if (shape instanceof OperationalData) {
                if (shape != ignoreOpData) {
                    OperationalData miss = (OperationalData) shape;
                    if (miss.contains(point, this)) {
                        return miss;
                    }

                }
            }
        }

        return null; // No shape found

    }

    // Method to notify ShapeDrawer about shape changes
    public void notifyShapeChanged() {
        shapeDrawer.updateLeftPanel();
    }

    private Shape findDeepestShapeUnderPoint(Point point, MouseEvent e) {
        // Check for OperationalData
        for (Shape shape : shapeDrawer.operationalDataList) {
            if (shape instanceof OperationalData) {
                OperationalData opData = (OperationalData) shape;
                if (opData.contains(point, this)) {
                    return opData;
                }
            }
        }

        // Check for Mission
        for (Shape shape : shapeDrawer.missionList) {
            if (shape instanceof Mission) {
                Mission miss = (Mission) shape;
                if (miss.contains(point, this)) {
                    return miss;
                }
            }
        }

        return null; // No shape found
    }

    // getShapes function
    public List<Mission> getMissionList() {
        // combine shapeDrawer.operationalDataList and shapeDrawer.missionList and
        return shapeDrawer.missionList;
    }

    public List<OperationalData> getOperationalDataList() {
        return shapeDrawer.operationalDataList;
    }

    public List<Mission_Connection> getMissionConnections() {
        return mission_connections;
    }

    public List<OpData_Miss_Conn> getDataMissConnections() {
        return data_miss_connections;
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

        for (Shape shape : shapeDrawer.operationalDataList) {
            OperationalData shapeOpData = (OperationalData) shape;

            shapeOpData.setSelected(false);
            shapeOpData.setConnectionSelected(false);
        }

        for (Shape shape : shapeDrawer.missionList) {

            // Convert shape to a Mission
            Mission shapeMission = (Mission) shape;

            shapeMission.setSelected(false);
            shapeMission.setConnectionSelected(false);

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



    public void updateSelectedMissionOpData(Shape selectedShape) {
        if (selectedShape instanceof OperationalData) {
            shapeDrawer.selectedMissionOpDataShape = selectedShape;

        } else if (selectedShape instanceof Mission) {
            shapeDrawer.selectedMissionOpDataShape = selectedShape;
        }

        else {
            shapeDrawer.selectedMissionOpDataShape = null;
        }
        shapeDrawer.updateMissionDataSelection();
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    @Override
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public void loadMissionList(List<Shape> missionList) {
        shapeDrawer.missionList.clear();
        for (Shape shape : missionList) {
            shapeDrawer.missionList.add((Mission) shape);
        }
    }

    public void loadOperationalDataList(List<Shape> operationalDataList) {
        shapeDrawer.operationalDataList.clear();
        for (Shape shape : operationalDataList) {
            shapeDrawer.operationalDataList.add((OperationalData) shape);
        }
    }

    public void loadMissionHierarchy(List<Mission_Connection> missionHierarchy) {
        mission_connections.clear();
        mission_connections.addAll(missionHierarchy);
    }

    public void loadOpDataMissConns(List<OpData_Miss_Conn> opDataMissConns) {
        data_miss_connections.clear();
        data_miss_connections.addAll(opDataMissConns);
    }

    // Clear the mission list and operational data list and OpData_Miss_Conn list and mission_connections list
    public void clearAllMissionPanelLists() {
        shapeDrawer.missionList.clear();
        shapeDrawer.operationalDataList.clear();
        data_miss_connections.clear();
        mission_connections.clear();
    }
}
