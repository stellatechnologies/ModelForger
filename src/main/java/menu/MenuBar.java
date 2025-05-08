package menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import panels.DrawingPanel;
import panels.MissionHierarchyPanel;
import shapes.Shape;
import utils.CommonUtils;
import utils.ShapeUtils;
import windows.DataManagerWindow;

import com.adkin.cyberdiagramer.CyberDiagramer;

import non_shape_objects.Mission;
import non_shape_objects.Mission_Connection;
import non_shape_objects.OperationalData;
import non_shape_objects.OpData_Miss_Conn;


public class MenuBar extends JMenuBar {
    private DrawingPanel drawingPanel;
    private final CyberDiagramer shapeDrawer;
    private MissionHierarchyPanel missionHierarchyPanel;

    // Constructor accepting a DrawingPanel reference
    public MenuBar(DrawingPanel drawingPanel, CyberDiagramer shapeDrawer, MissionHierarchyPanel missionHierarchyPanel) {
        this.drawingPanel = drawingPanel;
        this.shapeDrawer = shapeDrawer;
        this.missionHierarchyPanel = missionHierarchyPanel;

        // Create "File" menu and its items
        JMenu fileMenu = createMenu("File", new String[]{"New", "Open", "Save", "Load", "Exit"});

        // Create "Edit" menu and its items
        JMenu editMenu = createMenu("Edit", new String[]{"Toggle Physics"});

        // Create "View" menu and its items
        JMenu viewMenu = createMenu("View", new String[]{"Data Manager", "Mission Hierarchy", "Shape Drawer"});

        // Create "Help" menu and its items
        JMenu helpMenu = createMenu("Help", new String[]{"About", "Documentation"});

        // Add menus to the menu bar
        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(helpMenu);
    }

    private JMenu createMenu(String menuName, String[] itemNames) {
        JMenu menu = new JMenu(menuName);
        for (String itemName : itemNames) {
            JMenuItem menuItem = new JMenuItem(itemName);

            if ("Save".equals(itemName)) {
                menuItem.addActionListener(e -> save());
            } else if ("Load".equals(itemName)) {
                menuItem.addActionListener(e -> load());
            } else if ("Data Manager".equals(itemName)) {
                menuItem.addActionListener(e -> {
                    DataManagerWindow dataManagerWindow = new DataManagerWindow(shapeDrawer, shapeDrawer.operationalDataList);
                    dataManagerWindow.setVisible(true);
                });
            } else if ("Mission Hierarchy".equals(itemName)) {
                menuItem.addActionListener(e -> {
                    // shapeDrawer.cardLayout.show(shapeDrawer.cardPanel, "MissionHierarchyPanel");
                    shapeDrawer.switchToPanel("MissionHierarchyPanel");
                    shapeDrawer.switchToRightPanel("MissionHierarchy_RightPanel");

                });
            } else if ("Shape Drawer".equals(itemName)) {
                menuItem.addActionListener(e -> {
                    // shapeDrawer.cardLayout.show(shapeDrawer.cardPanel, "DrawingPanel");
                    shapeDrawer.switchToPanel("DrawingPanel");
                    shapeDrawer.switchToRightPanel("DefaultRightPanel");
                });
            } else if ("Toggle Physics".equals(itemName)) {
                menuItem.addActionListener(e -> {
                    if (drawingPanel != null) {
                        shapeDrawer.togglePhysics();
                    }
                });
            }

            menu.add(menuItem);
        }
        return menu;
    }

    private void save() {
        if (drawingPanel != null) {
            ShapeUtils.saveShapesToFile(
                drawingPanel.getShapes(),
                drawingPanel.getConnections(),
                missionHierarchyPanel.getMissionList(),
                missionHierarchyPanel.getOperationalDataList(),
                missionHierarchyPanel.getMissionConnections(),
                missionHierarchyPanel.getDataMissConnections()
            );
        }
    }

    private void load() {
        if (drawingPanel != null) {
            List<Shape> shapes = ShapeUtils.loadShapesFromFile(drawingPanel);


            List<Shape> missionList = new ArrayList<>();
            for (Shape shape : shapes) {
                if (shape instanceof Mission) {
                    missionList.add(shape);
                }
            }
            shapes.removeAll(missionList);

            // Remove all OperationalData
            List<Shape> operationalDataList = new ArrayList<>();
            for (Shape shape : shapes) {
                if (shape instanceof OperationalData) {
                    operationalDataList.add(shape);
                }
            }
            shapes.removeAll(operationalDataList);

            List<Mission_Connection> missionHierarchy = new ArrayList<>();
            for (Shape shape : shapes) {
                if (shape instanceof Mission_Connection) {
                    missionHierarchy.add((Mission_Connection) shape);
                }
            }
            shapes.removeAll(missionHierarchy);

            List<OpData_Miss_Conn> opDataMissConns = new ArrayList<>();
            for (Shape shape : shapes) {
                if (shape instanceof OpData_Miss_Conn) {
                    opDataMissConns.add((OpData_Miss_Conn) shape);
                }
            }
            shapes.removeAll(opDataMissConns);


            missionHierarchyPanel.clearAllMissionPanelLists();
    
            drawingPanel.loadShapes(shapes);
            missionHierarchyPanel.loadMissionList(missionList);
            missionHierarchyPanel.loadOperationalDataList(operationalDataList);
            missionHierarchyPanel.loadMissionHierarchy(missionHierarchy);
            missionHierarchyPanel.loadOpDataMissConns(opDataMissConns);
        }
    }
}
