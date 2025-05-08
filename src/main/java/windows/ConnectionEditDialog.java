package windows;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import non_shape_objects.OperationalData;
import shapes.SystemConnection;



class OperationalDataRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (value instanceof OperationalData) {
            OperationalData data = (OperationalData) value;
            String displayText = String.format("%s (C: %d, I: %d, A: %d)",
                    data.getName(),
                    data.getConfidentialityValue(),
                    data.getIntegrityValue(),
                    data.getAvailabilityValue());
            return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}

public class ConnectionEditDialog extends JDialog {
    private final SystemConnection connection;
    private final JColorChooser colorChooser;
    private final JCheckBox rightAngleCheckBox, dashedLineCheckBox;
    private final DefaultListModel<OperationalData> sys1ToSys2DataModel, sys2ToSys1DataModel;
    private final List<OperationalData> availableData;

    public ConnectionEditDialog(JFrame owner, SystemConnection connection, List<OperationalData> availableData) {
        super(owner, "Edit Connection", true);
        this.connection = connection;
        this.availableData = new ArrayList<>(availableData);

        setSize(800, 600);
        setLayout(new BorderLayout());
        colorChooser = new JColorChooser(connection.getColor());
        rightAngleCheckBox = new JCheckBox("Right-Angled Connection", connection.isRightAngle());
        dashedLineCheckBox = new JCheckBox("Dashed Line", !connection.isSolidLine());

        sys1ToSys2DataModel = new DefaultListModel<>();
        sys1ToSys2DataModel.addAll(connection.getSys1ToSys2Data());

        sys2ToSys1DataModel = new DefaultListModel<>();
        sys2ToSys1DataModel.addAll(connection.getSys2ToSys1Data());

        setupOptionsPanel();
        setupDataManagementPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        add(saveButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void setupOptionsPanel() {
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1));
        optionsPanel.setBorder(new TitledBorder("Options"));
        optionsPanel.add(colorChooser); // Adding color chooser to the options panel
        optionsPanel.add(rightAngleCheckBox);
        optionsPanel.add(dashedLineCheckBox);
        add(optionsPanel, BorderLayout.NORTH);
    }

    private void setupDataManagementPanel() {

        shapes.Shape connectionOwner1 = connection.getOwner(0);
        shapes.Shape connectionOwner2 = connection.getOwner(1);

        String connOwner1Name = null;
        String connOwner2Name = null;

        if (connectionOwner1 instanceof shapes.System_Obj) {
            connOwner1Name = ((shapes.System_Obj) connectionOwner1).getName();
        } else {
            connOwner1Name = ((shapes.Interface_Obj) connectionOwner1).getSystem().getName();
        }

        if (connectionOwner2 instanceof shapes.System_Obj) {
            connOwner2Name = ((shapes.System_Obj) connectionOwner2).getName();
        } else {
            connOwner2Name = ((shapes.Interface_Obj) connectionOwner2).getSystem().getName();
        }



        // Create panels for both directions
        JPanel sys1ToSys2Panel = createDirectionPanel(
                connOwner1Name + " to " + connOwner2Name + " Data",
                sys1ToSys2DataModel);


                
        JPanel sys2ToSys1Panel = createDirectionPanel(
                connOwner2Name + " to " + connOwner1Name + " Data",
                sys2ToSys1DataModel);
    
        // Create a split pane to hold the two panels side by side
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, 
            sys1ToSys2Panel, 
            sys2ToSys1Panel
        );
    
        // Configure the split pane (optional)
        splitPane.setDividerLocation(400); // You can adjust this based on your preference
        splitPane.setResizeWeight(0.5); // This sets the resize behavior
    
        // Add the split pane to the dialog
        add(splitPane, BorderLayout.CENTER);
    }
    

    private JPanel createDirectionPanel(String title, DefaultListModel<OperationalData> model) {
        JPanel panel = new JPanel(new BorderLayout());
        JList<OperationalData> dataList = new JList<>(model);
        dataList.setCellRenderer(new OperationalDataRenderer()); // Set the custom renderer
        JScrollPane scrollPane = new JScrollPane(dataList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Data");
        addButton.addActionListener(e -> addData(model));
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelectedData(dataList, model));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createTitledBorder(title));

        return panel;
    }

    private void addData(DefaultListModel<OperationalData> model) {
        OperationalData selectedData = selectOperationalData();
        if (selectedData != null && !model.contains(selectedData)) {
            model.addElement(selectedData);
        }
    }

    private OperationalData selectOperationalData() {
        JComboBox<OperationalData> dataComboBox = new JComboBox<>();
        for (OperationalData data : availableData) {
            dataComboBox.addItem(data);
        }

        int result = JOptionPane.showConfirmDialog(this, dataComboBox, "Select Data", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return (OperationalData) dataComboBox.getSelectedItem();
        }
        return null;
    }

    private void removeSelectedData(JList<OperationalData> dataList, DefaultListModel<OperationalData> model) {
        int selectedIndex = dataList.getSelectedIndex();
        if (selectedIndex != -1) {
            model.remove(selectedIndex);
        }
    }

    private void onSave() {
        connection.setColor(colorChooser.getColor());
        connection.setRightAngle(rightAngleCheckBox.isSelected());
        connection.setSolidLine(!dashedLineCheckBox.isSelected());

        // Collect sys1ToSys2 data
        ArrayList<OperationalData> sys1ToSys2Data = new ArrayList<>();
        for (Enumeration<OperationalData> e = sys1ToSys2DataModel.elements(); e.hasMoreElements();) {
            sys1ToSys2Data.add(e.nextElement());
        }
        connection.setSys1ToSys2Data(sys1ToSys2Data);

        // Collect sys2ToSys1 data
        ArrayList<OperationalData> sys2ToSys1Data = new ArrayList<>();
        for (Enumeration<OperationalData> e = sys2ToSys1DataModel.elements(); e.hasMoreElements();) {
            sys2ToSys1Data.add(e.nextElement());
        }
        connection.setSys2ToSys1Data(sys2ToSys1Data);

        setVisible(false);
    }

}
