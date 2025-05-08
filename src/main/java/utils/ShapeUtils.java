package utils;

import java.awt.Color;
import java.awt.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import non_shape_objects.Mission;
import non_shape_objects.Mission_Connection;
import non_shape_objects.OpData_Miss_Conn;
import non_shape_objects.OperationalData;
import panels.DrawingPanel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import shapes.AuthorizationBoundary;
import shapes.Connection_Point_Obj;
import shapes.Entry_Point;
import shapes.Shape;
import shapes.System_Obj;
import shapes.Interface_Obj;
import shapes.SystemConnection;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ShapeUtils {

    private static Map<Class<? extends Shape>, Function<Shape, JSONObject>> exportFunctions = new HashMap<>();

    private static Map<String, String> jsonKeyMappings = new HashMap<>();

    static {
        // Directly populate the key mappings
        jsonKeyMappings.put("System_Obj", "System");
        jsonKeyMappings.put("Entry_Point", "Entry_Point");
        jsonKeyMappings.put("AuthorizationBoundary", "AuthorizationBoundary");
        jsonKeyMappings.put("Interface_Obj", "Interface");
        jsonKeyMappings.put("SystemConnection", "SystemConnection");
        jsonKeyMappings.put("Mission_Connection", "MissionHierarchy");
        jsonKeyMappings.put("OpData_Miss_Conn", "MissionData");


        // Populate export functions
        exportFunctions.put(Entry_Point.class, shape -> ((Entry_Point) shape).export_object());
        exportFunctions.put(System_Obj.class, shape -> ((System_Obj) shape).export_object());
        exportFunctions.put(AuthorizationBoundary.class, shape -> ((AuthorizationBoundary) shape).export_object());
        exportFunctions.put(Interface_Obj.class, shape -> ((Interface_Obj) shape).export_object());
        exportFunctions.put(SystemConnection.class, shape -> ((SystemConnection) shape).export_object());

        // Add other shape types here...
    }

    // Method to get the JSON key for a given shape object
    private static String getJsonKeyForShape(Shape shape) {
        return jsonKeyMappings.getOrDefault(shape.getClass().getSimpleName(), shape.getClass().getSimpleName());
    }

    private static JSONObject exportShape(Shape shape) {
        Function<Shape, JSONObject> exportFunction = exportFunctions.get(shape.getClass());
        if (exportFunction != null) {
            return exportFunction.apply(shape);
        } else {
            return null;  // Consider throwing an exception or using Optional to handle this case more gracefully.
        }
    }
    

    public static void saveShapesToFile(List<Shape> shapes, List<SystemConnection> systemConnections, List<Mission> missionList, List<OperationalData> operationalDataList, List<Mission_Connection> missionConnections, List<OpData_Miss_Conn> opDataMissConns) {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        // Set default location to current directory
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Save File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Show the save dialog
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Ensure the file has the correct extension
            if (!filePath.toLowerCase().endsWith(".json")) {
                filePath += ".json";
            }

            // Initialize a map to hold JSON arrays for each shape type
            Map<String, JSONArray> shapeTypeJsonArrays = new HashMap<>();


            // Save Shape objects
            for (Shape shape : shapes) {
                JSONObject shapeJson = exportShape(shape);
                if (shapeJson != null) {
                    String shapeType = getJsonKeyForShape(shape); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(shapeJson);
                }
            }


            // Save Connection objects
            for (SystemConnection connection : systemConnections) {
                JSONObject connectionJson = connection.export_object();
                if (connectionJson != null) {
                    String shapeType = getJsonKeyForShape(connection); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(connectionJson);
                }
            }

            // Save Mission objects
            for (Mission mission : missionList) {
                JSONObject missionJson = mission.export_object();
                if (missionJson != null) {
                    String shapeType = getJsonKeyForShape(mission); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(missionJson);
                }
            }

            // Save OperationalData objects
            for (OperationalData operationalData : operationalDataList) {
                JSONObject operationalDataJson = operationalData.export_object();
                if (operationalDataJson != null) {
                    String shapeType = getJsonKeyForShape(operationalData); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(operationalDataJson);
                }
            }

            // Save Mission_Connection objects
            for (Mission_Connection missionConnection : missionConnections) {
                JSONObject missionConnectionJson = missionConnection.export_object();
                if (missionConnectionJson != null) {
                    String shapeType = getJsonKeyForShape(missionConnection); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(missionConnectionJson);
                }
            }

            // Save OpData_Miss_Conn objects
            for (OpData_Miss_Conn opDataMissConn : opDataMissConns) {
                JSONObject opDataMissConnJson = opDataMissConn.export_object();
                if (opDataMissConnJson != null) {
                    String shapeType = getJsonKeyForShape(opDataMissConn); // Get the simple name of the class
                    shapeTypeJsonArrays.putIfAbsent(shapeType, new JSONArray());
                    shapeTypeJsonArrays.get(shapeType).put(opDataMissConnJson);
                }
            }



            // Save SystemConnectionData
            // Create temporary map to hold system connection data
            // @ TODO: THIS IS A FUCKING STUIPID WAY TO DO THIS BUT Schema 2.0 WILL SOLVE THIS
            Map<String, JSONObject> systemConnectionData = new HashMap<>();
            // iterate through system connections
            for (SystemConnection systemConnection : systemConnections) {

                // Get SystemConnectionUUID
                String systemConnectionUUID = systemConnection.getIdentifier();

                // Get the first system's UUID and the second system's UUID
                String sys1UUID = systemConnection.getFirstSystem().getIdentifier();
                String sys2UUID = systemConnection.getSecondSystem().getIdentifier();

                // Iterate through sys1ToSys2Data and sys2ToSys1Data
                for (OperationalData operationalData : systemConnection.getSys1ToSys2Data()) {

                    String operationalDataUUID = operationalData.getIdentifier();

                    // Create a JSON object for the connection
                    JSONObject json = new JSONObject();
    
                    // Schema compliant
                    ShapeUtils.addAttribute(json, java.util.UUID.randomUUID().toString(), false, "UUID");
                    ShapeUtils.addAttribute(json, systemConnectionUUID, false, "System_Connection_ID");
                    ShapeUtils.addAttribute(json, operationalDataUUID, false, "Operational_Data_ID");
                    ShapeUtils.addAttribute(json, sys1UUID, false, "Source_System_ID");
                    ShapeUtils.addAttribute(json, sys2UUID, false, "Target_System_ID");


                    // Add the JSON object to the map
                    systemConnectionData.put(systemConnectionUUID + "_" + operationalDataUUID, json);
                }

                for (OperationalData operationalData : systemConnection.getSys2ToSys1Data()) {

                    String operationalDataUUID = operationalData.getIdentifier();

                    // Create a JSON object for the connection
                    JSONObject json = new JSONObject();
    
                    // Schema compliant
                    ShapeUtils.addAttribute(json, java.util.UUID.randomUUID().toString(), false, "UUID");
                    ShapeUtils.addAttribute(json, systemConnectionUUID, false, "System_Connection_ID");
                    ShapeUtils.addAttribute(json, operationalDataUUID, false, "Operational_Data_ID");
                    ShapeUtils.addAttribute(json, sys2UUID, false, "Source_System_ID");
                    ShapeUtils.addAttribute(json, sys1UUID, false, "Target_System_ID");

                    // Add the JSON object to the map
                    systemConnectionData.put(systemConnectionUUID + "_" + operationalDataUUID, json);
                }
            }

            // Add the system connection data to the shape type JSON arrays
            for (Map.Entry<String, JSONObject> entry : systemConnectionData.entrySet()) {
                shapeTypeJsonArrays.putIfAbsent("SystemConnectionData", new JSONArray());
                shapeTypeJsonArrays.get("SystemConnectionData").put(entry.getValue());
            }







            // Build the final JSON object
            JSONObject finalJson = new JSONObject();
            for (Map.Entry<String, JSONArray> entry : shapeTypeJsonArrays.entrySet()) {
                finalJson.put(entry.getKey(), entry.getValue());
            }

            // Save the final JSON object to a file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(finalJson.toString());
            } catch (IOException e) {
                // e.printStackTrace();
                System.out.println("Error saving file: ");
            }
        }
    }

    public static List<Shape> loadShapesFromFile(DrawingPanel drawingPanel) {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Set the default location to the current directory
        fileChooser.setCurrentDirectory(new File("."));

        // Show the open dialog
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            List<Shape> loadedShapes = new ArrayList<>();
    
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                JSONObject json = new JSONObject(content);
    
                // SYSTEMS
                if (json.has("System")) {
                    JSONArray systems = json.getJSONArray("System");
                    for (int i = 0; i < systems.length(); i++) {
                        JSONObject systemJson = systems.getJSONObject(i);
                        System_Obj system = parseSystemObj(systemJson, drawingPanel);

                        // If jea::color is not present, set it to the default color
                        if (!systemJson.has("jea::color")) {
                            system.setColor(new Color(0, 0, 0));
                        }
    
                        ArrayList<Connection_Point_Obj> connectionPoints = new ArrayList<>();
    
                        if (systemJson.has("jea::connectionPoints")) {
                            JSONArray connectionPointsArray = systemJson.getJSONArray("jea::connectionPoints");
                            for (int j = 0; j < connectionPointsArray.length(); j++) {
                                Connection_Point_Obj connectionPoint = parseConnection_Point_Obj(
                                        connectionPointsArray.getJSONObject(j), system
                                );
                                connectionPoints.add(connectionPoint);
                            }
                        }
    
                        system.setConnectionPoints(connectionPoints);
                        loadedShapes.add(system);
                    }
    
                    // Update System Parents
                    for (int i = 0; i < systems.length(); i++) {
                        JSONObject systemJson = systems.getJSONObject(i);
    
                        // Get the system's UUID
                        String systemUUID = systemJson.optString("UUID");
                        // Get the parent's UUID
                        String parentUUID = systemJson.optString("Parent_ID");
    
                        // Find the system with the UUID
                        System_Obj system = (System_Obj) loadedShapes.stream().filter(shape -> shape instanceof System_Obj)
                                .filter(shape -> ((System_Obj) shape).getIdentifier().equals(systemUUID)).findFirst()
                                .orElse(null);
    
                        // Find the parent with the UUID
                        System_Obj parent = (System_Obj) loadedShapes.stream().filter(shape -> shape instanceof System_Obj)
                                .filter(shape -> ((System_Obj) shape).getIdentifier().equals(parentUUID)).findFirst()
                                .orElse(null);
    
                        // Set the parent
                        system.setParent(parent);
    
                        if (parent != null) {
                            parent.addChild(system, false);
                        }
                    }
                }
    
                // AUTHORIZATION BOUNDARIES
                if (json.has("AuthorizationBoundary")) {
                    JSONArray authorizationBoundaries = json.getJSONArray("AuthorizationBoundary");
                    for (int i = 0; i < authorizationBoundaries.length(); i++) {
                        JSONObject authorizationBoundaryJson = authorizationBoundaries.getJSONObject(i);
                        AuthorizationBoundary authorizationBoundary = parseAuthorizationBoundary(authorizationBoundaryJson,
                                drawingPanel);
                        loadedShapes.add(authorizationBoundary);
                    }
                }


                // // OPERATIONAL DATA
                // if (json.has("OperationalData")) {
                //     JSONArray operationalData = json.getJSONArray("OperationalData");
                //     for (int i = 0; i < operationalData.length(); i++) {
                //         JSONObject operationalDataJson = operationalData.getJSONObject(i);
                //         OperationalData operationalDataObj = parseOperationalData(operationalDataJson, drawingPanel);
                //         loadedShapes.add(operationalDataObj);
                //     }
                // }

                // MISSIONS
                if (json.has("Mission")) {
                    JSONArray missions = json.getJSONArray("Mission");
                    for (int i = 0; i < missions.length(); i++) {
                        JSONObject missionJson = missions.getJSONObject(i);
                        Mission mission = parseMissionObj(missionJson, drawingPanel);
                        loadedShapes.add(mission);
                    }
                }

                // MISSION HIERARCHY
                if (json.has("MissionHierarchy")) {
                    JSONArray missionHierarchyArray = json.getJSONArray("MissionHierarchy");
                    for (int i = 0; i < missionHierarchyArray.length(); i++) {
                        JSONObject missionHierarchyJson = missionHierarchyArray.getJSONObject(i);
                        Mission_Connection missionConnection = parseMissionHierarchy(missionHierarchyJson, loadedShapes);
                        loadedShapes.add(missionConnection);
                    }
                }

                // // MISSION DATA
                // if (json.has("MissionData")) {
                //     JSONArray missionDataArray = json.getJSONArray("MissionData");
                //     for (int i = 0; i < missionDataArray.length(); i++) {
                //         JSONObject missionDataJson = missionDataArray.getJSONObject(i);
                //         OpData_Miss_Conn opDataMissConn = parseOpDataMissConn(missionDataJson, loadedShapes);
                //         loadedShapes.add(opDataMissConn);
                //     }
                // }
                                
                
                return loadedShapes;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static AuthorizationBoundary parseAuthorizationBoundary(JSONObject json, DrawingPanel drawingPanel) {
        // Extract fields from JSON object
        String UUID = json.optString("UUID");
        String name = json.optString("Name");
        String acronym = json.optString("Acronym");
        int width = json.optInt("jea::width");
        int height = json.optInt("jea::height");
        int x = json.optInt("jea::x");
        int y = json.optInt("jea::y");
        String given_color = json.optString("jea::color"); // (r,g,b)
        // String parent_id = json.optString("jea::Parent_ID");

        // Create Point and Color objects from string representations
        Point position = new Point(x, y);
        // You might need to implement a method to parse the color string
        Color color = parseColor(given_color);
        Point text_position = new Point(json.optInt("jea::text_x_position"), json.optInt("jea::text_y_position"));

        // Create and configure the System_Obj instance
        AuthorizationBoundary authorizationBoundary = new AuthorizationBoundary(position);
        authorizationBoundary.setUUID(UUID);
        authorizationBoundary.setName(name);
        authorizationBoundary.setAcronym(acronym);
        authorizationBoundary.setWidth(width);
        authorizationBoundary.setHeight(height);
        authorizationBoundary.setColor(color);
        authorizationBoundary.setSelectedTextPosition(text_position);
        authorizationBoundary.setDisplayAcronym(json.optBoolean("jea::displayAcronym"));
        authorizationBoundary.setDashed(json.optBoolean("jea::dashed"));
        authorizationBoundary.setThickness(json.optInt("jea::thickness"));


        return authorizationBoundary;
    }

    private static Connection_Point_Obj parseConnection_Point_Obj(JSONObject json, Shape owner) {
        int x = json.optInt("jea::x");
        int y = json.optInt("jea::y");
        Point position = new Point(x, y);
        Connection_Point_Obj connectionPoint = new Connection_Point_Obj(position, owner);
    
        // Handle archivedPositions
        JSONArray archivedPositionsJsonArray = json.optJSONArray("jea::archivedPositions");
        List<Point> archivedPositions = new ArrayList<>();
        if (archivedPositionsJsonArray != null) {
            for (int i = 0; i < archivedPositionsJsonArray.length(); i++) {
                String pointString = archivedPositionsJsonArray.optString(i);
                if (!pointString.isEmpty()) {
                    String[] parts = pointString.replaceAll("[()]", "").split(",");
                    if (parts.length == 2) {
                        try {
                            int x1 = Integer.parseInt(parts[0].trim());
                            int y1 = Integer.parseInt(parts[1].trim());
                            archivedPositions.add(new Point(x1, y1));
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing point: " + pointString);
                        }
                    }
                }
            }
        }
        connectionPoint.setArchivedPositions(archivedPositions);
    
        // Handle archivedPositionPercentage
        JSONArray archivedPositionPercentageArray = json.optJSONArray("jea::archivedPositionPercentage");
        List<Double> archivedPositionPercentage = new ArrayList<>();
        if (archivedPositionPercentageArray != null) {
            for (int i = 0; i < archivedPositionPercentageArray.length(); i++) {
                double percentage = archivedPositionPercentageArray.optDouble(i, -1);
                if (percentage != -1) {
                    archivedPositionPercentage.add(percentage);
                }
            }
        }
        connectionPoint.setArchivedPositionPercentage(archivedPositionPercentage);
    
        return connectionPoint;
    }
    

    private static System_Obj parseSystemObj(JSONObject json, DrawingPanel drawingPanel) {

        // Extract fields from JSON object
        String UUID = json.optString("UUID");
        String name = json.optString("Name");
        String acronym = json.optString("Acronym");
        int width = json.optInt("jea::width");
        int height = json.optInt("jea::height");
        int x = json.optInt("jea::x");
        int y = json.optInt("jea::y");
        String given_color = json.optString("jea::color"); // (r,g,b)
        // String parent_id = json.optString("jea::Parent_ID");

        // Create Point and Color objects from string representations
        Point position = new Point(x, y);
        // You might need to implement a method to parse the color string
        Color color = parseColor(given_color);
        Point text_position = new Point(json.optInt("jea::text_x_position"), json.optInt("jea::text_y_position"));

        // Create and configure the System_Obj instance
        System_Obj system = new System_Obj(position, drawingPanel, new ArrayList<>());
        system.setUUID(UUID);
        system.setName(name);
        system.setAcronym(acronym);
        system.setWidth(width);
        system.setHeight(height);
        system.setColor(color);
        system.setDisplayAcronym(json.optBoolean("jea::displayAcronym"));
        system.setSelectedTextPosition(text_position);
        system.setHiddenStatus(json.optBoolean("jea::hidden"));
        system.setCollapsed(json.optBoolean("jea::collapsed"), false);
        system.setPreCollapsedHeight(json.optInt("jea::preCollapsedHeight"));
        system.setPreCollapsedWidth(json.optInt("jea::preCollapsedWidth"));

        return system;
    }

    private static Color parseColor(String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            return null;
        }

        // Remove the parentheses and split the string by comma
        String[] parts = colorString.replaceAll("[()]", "").split(",");

        if (parts.length == 3) {
            try {
                // Parse the individual color components
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return new Color(r, g, b);
            } catch (NumberFormatException e) {
                // Handle the exception if parsing fails
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    
    public static Mission parseMissionObj(JSONObject json, DrawingPanel drawingPanel) {
        // Extract values from the JSON object
        String UUID = json.optString("UUID");
        String name = json.optString("Name");
        String description = json.optString("Description");
        String given_color = json.optString("jea::color");
        int x = json.optInt("jea::x");
        int y = json.optInt("jea::y");

        // Create Point and Color objects from string representations
        Point position = new Point(x, y);
        Color color = parseColor(given_color);

        // Create and configure the Mission instance
        // public Mission(String name, String description, Point position) {
        Mission mission = new Mission(name, description, position);
        mission.setUuid(UUID);
        mission.setName(name);
        mission.setDescription(description);
        mission.setColor(color);

        return mission;
    }

    // parseOperationalData
    public static OperationalData parseOperationalData(JSONObject json, DrawingPanel drawingPanel) {
        // Extract values from the JSON object
        String UUID = json.optString("UUID");
        String name = json.optString("Name");
        String description = json.optString("Description");
        String given_color = json.optString("jea::color");
        int x = json.optInt("jea::x");
        int y = json.optInt("jea::y");

        // If jea::color is not present, set it to the default color
        if (!json.has("jea::color")) {
            given_color = "125,125,125";
        }

        // If jea::x is not present, set it to the default x
        if (!json.has("jea::x")) {
            x = 0;
        }

        // If jea::y is not present, set it to the default y
        if (!json.has("jea::y")) {
            y = 0;
        }

        // Create Point and Color objects from string representations
        Point position = new Point(x, y);
        Color color = parseColor(given_color);

        // Create and configure the OperationalData instance
        // OperationalData opData3 = new OperationalData("OpData 3", "OpData 3 Description", 1, 1, 1, new Point(300, 100), "Classif 3");
        // public OperationalData(String name, String description, int confidentialityValue, int integrityValue, int availabilityValue, Point position, String classif) {

        OperationalData operationalData = new OperationalData(name, description, 0, 0, 0, position, "UNCLASSIFIED");
        operationalData.setUuid(UUID);
        operationalData.setColor(color);

        return operationalData;
    }
    

    private static Mission_Connection parseMissionHierarchy(JSONObject json, List<Shape> loadedShapes) {
        String parentMissionUUID = json.optString("Parent_Mission");
        String childMissionUUID = json.optString("Child_Mission");
    
        Mission parentMission = (Mission) loadedShapes.stream()
            .filter(shape -> shape instanceof Mission && ((Mission) shape).getUuid().equals(parentMissionUUID))
            .findFirst()
            .orElse(null);
    
        Mission childMission = (Mission) loadedShapes.stream()
            .filter(shape -> shape instanceof Mission && ((Mission) shape).getUuid().equals(childMissionUUID))
            .findFirst()
            .orElse(null);
    
        if (parentMission != null && childMission != null) {
            return new Mission_Connection(childMission, parentMission);
        }
        return null;
    }


    private static OpData_Miss_Conn parseOpDataMissConn(JSONObject json, List<Shape> loadedShapes) {
        String missionUUID = json.optString("Mission_ID");
        String operationalDataUUID = json.optString("Operational_Data_ID");

        Mission mission = (Mission) loadedShapes.stream()
            .filter(shape -> shape instanceof Mission && ((Mission) shape).getUuid().equals(missionUUID))
            .findFirst()
            .orElse(null);

            OperationalData operationalData = (OperationalData) loadedShapes.stream()
            .filter(shape -> shape instanceof OperationalData && ((OperationalData) shape).getUuid().toString().equals(operationalDataUUID))
            .findFirst()
            .orElse(null);

        // System.out.println("mission: " + mission);
        // System.out.println("operationalData: " + operationalData);

        if (mission != null && operationalData != null) {
            return new OpData_Miss_Conn(operationalData, mission);
        }

        // System.out.println("OpData_Miss_Conn not found");
        return null;
    }

    
    // Inner classes to represent the JSON structure
    class DataSet {
        List<ObjLoad_System> System;
    }

    class ObjLoad_System {
        String UUID;
        String Name;
        String Acronym;
    }

    public static void addAttribute(JSONObject json, Object value, boolean useJeaTag, String attributeName) {
        String key = useJeaTag ? "jea::" + attributeName : attributeName;
        json.put(key, value != null ? value : JSONObject.NULL);
    }

}
