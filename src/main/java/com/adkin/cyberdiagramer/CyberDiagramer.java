package com.adkin.cyberdiagramer;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import shapes.Shape;

import windows.*;
import panels.*;
import panels.MissionHierarchy.DataDetailPanel;
import shapes.*;
import menu.*;
import menu.MenuBar;
import non_shape_objects.Mission;
import non_shape_objects.OpData_Miss_Conn;
import non_shape_objects.OperationalData;
import non_shape_objects.Mission_Connection;

public class CyberDiagramer extends JFrame implements RightPanelListener {
    private final DrawingPanel drawingPanel;
    private final RightPanel rightPanel;
    private final LeftPanel leftPanel;
    private final MenuBar menuBar;
    public final List<OperationalData> operationalDataList;
    public final List<Mission> missionList;
    private final MissionHierarchyPanel missionHierarchyPanel;
    private final MissionHierarchy_RightPanel missionHierarchy_RightPanel;

    public Shape selectedMissionOpDataShape = null;
    public boolean PhysicsOn = false;

    // SelectedButton variable that will be passed to the Drawingpanel
    public String buttonSelected;
    public ArrayList<Connection_Point_Obj> connection_hopper = new ArrayList<Connection_Point_Obj>();
    public ArrayList<Mission> mission_connection_hopper = new ArrayList<Mission>();

    // mission data connection hopper that will hold Mission and OperationalData objects
    public OpData_Miss_Conn mission_data_connection_hopper = new OpData_Miss_Conn(null, null);

    private CardLayout cardLayout, rightPaneLayout;
    private JPanel cardPanel, rightCardPanel;

    public CyberDiagramer() {
        setTitle("ModelForger");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        
        // Add a new panel, MissionHierarchyPanel
        // JPanel missionHierarchyPanel = new MissionHierarchyPanel();

 
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        buttonSelected = "";


        drawingPanel = new DrawingPanel(this);
        rightPanel = new RightPanel(this);
        leftPanel = new LeftPanel(this);

        missionHierarchyPanel = new MissionHierarchyPanel(this);
        missionHierarchy_RightPanel = new MissionHierarchy_RightPanel(this);


        // Add drawing panel to card panel
        cardPanel.add(drawingPanel, "DrawingPanel");
        cardPanel.add(missionHierarchyPanel, "MissionHierarchyPanel");


        

        
        rightPaneLayout = new CardLayout();
        rightCardPanel = new JPanel(rightPaneLayout);
        rightCardPanel.add(rightPanel, "DefaultRightPanel");
        rightCardPanel.add(missionHierarchy_RightPanel, "MissionHierarchy_RightPanel");


        // add(drawingPanel, BorderLayout.CENTER);

        add(cardPanel, BorderLayout.CENTER);
        add(rightCardPanel, BorderLayout.EAST);
        // add(rightPanel, BorderLayout.EAST);
        add(leftPanel, BorderLayout.WEST);


        operationalDataList = new ArrayList<>();
        missionList = new ArrayList<>();
       
        
        // Create menu bar
        menuBar = new MenuBar(drawingPanel, this, missionHierarchyPanel);

        // Set menu bar
        setJMenuBar(menuBar);



        // // Add two missions
        // Mission mission1 = new Mission("Mission 1", "Mission 1 Description", new
        // Point(100, 300));
        // missionHierarchyPanel.shapeDrawer.missionList.add(mission1);
        // Mission mission2 = new Mission("Mission 2", "Mission 2 Description", new
        // Point(200, 300));
        // missionHierarchyPanel.shapeDrawer.missionList.add(mission2);

        // // Add Mission 1a and 1b
        // Mission mission1a = new Mission("Mission 1a", "Mission 1a Description", new Point(100, 400));
        // missionHierarchyPanel.shapeDrawer.missionList.add(mission1a);
        // Mission mission1b = new Mission("Mission 1b", "Mission 1b Description", new Point(200, 400));
        // missionHierarchyPanel.shapeDrawer.missionList.add(mission1b);

        // // Add three operational data
        // OperationalData opData1 = new OperationalData("OpData 1", "OpData 1 Description", 1, 1, 1, new Point(100, 100), "Classif 1");
        // missionHierarchyPanel.shapeDrawer.operationalDataList.add(opData1);
        // OperationalData opData2 = new OperationalData("OpData 2", "OpData 2 Description", 1, 1, 1, new Point(200, 100), "Classif 2");
        // missionHierarchyPanel.shapeDrawer.operationalDataList.add(opData2);
        // OperationalData opData3 = new OperationalData("OpData 3", "OpData 3 Description", 1, 1, 1, new Point(300, 100), "Classif 3");
        // missionHierarchyPanel.shapeDrawer.operationalDataList.add(opData3);


        // // Add Connection between Mission 1 and Mission 1a and 1b
        // Mission_Connection missionConnection1 = new Mission_Connection(mission1a, mission1);
        // missionHierarchyPanel.mission_connections.add(missionConnection1);
        // Mission_Connection missionConnection2 = new Mission_Connection(mission1b, mission1);
        // missionHierarchyPanel.mission_connections.add(missionConnection2);


        // // Add Connection between Mission 1a and OpData 1 and 2, Mission 1b and Op Data 2, and Mission 2 and Op Data 3
        // OpData_Miss_Conn missionDataConnection1 = new OpData_Miss_Conn(opData1, mission1a);
        // missionHierarchyPanel.data_miss_connections.add(missionDataConnection1);
        // OpData_Miss_Conn missionDataConnection2 = new OpData_Miss_Conn(opData2, mission1a);
        // missionHierarchyPanel.data_miss_connections.add(missionDataConnection2);
        // OpData_Miss_Conn missionDataConnection3 = new OpData_Miss_Conn(opData2, mission1b);
        // missionHierarchyPanel.data_miss_connections.add(missionDataConnection3);
        // OpData_Miss_Conn missionDataConnection4 = new OpData_Miss_Conn(opData3, mission2);
        // missionHierarchyPanel.data_miss_connections.add(missionDataConnection4);


        // // Get the System Connection list
        // List<SystemConnection> connections = drawingPanel.getConnections();
        // ArrayList<OperationalData> sys1ToSys2Data = new ArrayList<>();
        // ArrayList<OperationalData> sys2ToSys1Data = new ArrayList<>();
        // SystemConnection sysConnection = connections.get(0);

        // // // Add Operational Data to the System Connection
        // // sys1ToSys2Data.add(opData1);
        // // sys1ToSys2Data.add(opData2);
        // // sys2ToSys1Data.add(opData3);
        // // sysConnection.setSys1ToSys2Data(sys1ToSys2Data);
        // // sysConnection.setSys2ToSys1Data(sys2ToSys1Data);




    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CyberDiagramer frame = new CyberDiagramer();
            frame.setVisible(true);
            
        });
    }

    // Method to switch panels
    public void switchToPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    public void switchToRightPanel(String panelName) {
        rightPaneLayout.show(rightCardPanel, panelName);
    }

    @Override
    public void onButtonSelected(String buttonType) {
        // Implement your logic based on the selected button type
        buttonSelected = buttonType;

        // deselect all shapes in the drawing panel
        drawingPanel.deselectAll();
    }


    public void updateMissionDataSelection(){
        if(selectedMissionOpDataShape != null){
            if(selectedMissionOpDataShape instanceof OperationalData){
                DataDetailPanel.setOperationalData((OperationalData) selectedMissionOpDataShape);
            } else if (selectedMissionOpDataShape instanceof Mission) {
                DataDetailPanel.setMissionData((Mission) selectedMissionOpDataShape);
            }
        }
    }
    

    public void updateLeftPanel() {
        // List<String> items = getSystemAndCircleNames();
        List<Shape> items = drawingPanel.getShapes();
        List<SystemConnection> connections = drawingPanel.getConnections();
        leftPanel.updateList(items, connections);
    }

    public String getButtonSelected() {
        return buttonSelected;
    }

    public List<Connection_Point_Obj> getConnection_hopper() {
        return connection_hopper;
    }

    public List<Mission> getMissionConnection_hopper(){
        return mission_connection_hopper;
    }

    // Add system to the connection hopper
    public void addConnectionPointToConnectionHopper(Connection_Point_Obj deepestConnectionPoint) {
        connection_hopper.add(deepestConnectionPoint);
    }

    // Add mission to the missionconnection hopper
    public void addMissionToMissionConnectionHopper(Mission deepestMission) {
        mission_connection_hopper.add(deepestMission);
    }

    // Remove mission from the missionconnection hopper
    public void removeMissionFromMissionConnectionHopper(Mission mission) {
        mission_connection_hopper.remove(mission);
    }

    // Clear the connection hopper
    public void clearConnectionHopper() {
        connection_hopper.clear();
    }

    public void togglePhysics() {
        PhysicsOn = !PhysicsOn;
    }

    public boolean isPhysicsOn() {
        return PhysicsOn;
    }

    // Get Mission List
    public List<Mission> getMissionList() {
        return missionList;
    }

    // Get Operational Data List
    public List<OperationalData> getOperationalDataList() {
        return operationalDataList;
    }

}
