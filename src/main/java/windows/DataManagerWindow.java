package windows;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import non_shape_objects.OperationalData;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.adkin.cyberdiagramer.CyberDiagramer;

public class DataManagerWindow extends JDialog {
    private List<OperationalData> dataList;
    private Map<Integer, UUID> rowIndexToUuidMap; // Map to store row index to UUID
    private JTable dataTable;
    private DataModel dataModel;


    public DataManagerWindow(Frame owner, List<OperationalData> operationalDataList) {
        super(owner, "Data Manager", true);
        this.dataList = operationalDataList;
        this.rowIndexToUuidMap = new HashMap<>();
        setSize(600, 400);
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        

        dataModel = new DataModel();
        dataTable = new JTable(dataModel);

        // Hide UUID column from the user
        // dataTable.removeColumn(dataTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(dataTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addData());
        editButton.addActionListener(e -> editData());
        deleteButton.addActionListener(e -> deleteData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addData() {
        OperationalDataDialog dialog = new OperationalDataDialog(this, null);
        dialog.setVisible(true);
        OperationalData newData = dialog.getOperationalData();
        if (newData != null) {
            dataList.add(newData);
            dataModel.fireTableDataChanged();
        }
    }

    private void editData() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow >= 0) {
            UUID uuid = rowIndexToUuidMap.get(selectedRow); 
            

            OperationalData existingData = dataList.stream()
                    .filter(data -> data.getUuid().equals(uuid))
                    .findFirst()
                    .orElse(null);

            if (existingData != null) {

                OperationalDataDialog dialog = new OperationalDataDialog(this, existingData);
                dialog.setVisible(true);
                OperationalData updatedData = dialog.getOperationalData();
                if (updatedData != null) {
                    dataList.set(selectedRow, updatedData);
                    dataModel.fireTableRowsUpdated(selectedRow, selectedRow);
                }
            }
        }
    }

    private void deleteData() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow >= 0) {
            dataList.remove(selectedRow);
            dataModel.fireTableRowsDeleted(selectedRow, selectedRow);
        }
    }

    class DataModel extends AbstractTableModel {
        String[] columnNames = { "Name", "Description", "Confidentiality", "Integrity", "Availability", "Classif" };

        @Override
        public int getRowCount() {
            return dataList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OperationalData data = dataList.get(rowIndex);
            // Ensure UUID is stored in the map for each row
            rowIndexToUuidMap.put(rowIndex, data.getUuid());

            switch (columnIndex) {
                case 0:
                    return data.getName();
                case 1:
                    return data.getDescription();
                case 2:
                    return data.getConfidentialityValue();
                case 3:
                    return data.getIntegrityValue();
                case 4:
                    return data.getAvailabilityValue();
                case 5:
                    return data.getClassif();
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

}
