package panels;

import javax.swing.*;
import javax.swing.RowFilter.Entry;
import javax.swing.tree.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;

import shapes.Shape;
import shapes.SystemConnection;
import shapes.System_Obj;
import windows.AuthorizationBoundaryEditDialog;
import windows.ConnectionEditDialog;
import windows.EntryPointEditDialog;
import windows.ShapeEditDialog;
import shapes.AuthorizationBoundary;
import shapes.Connection_Point_Obj;
import shapes.Entry_Point;
import shapes.Interface_Obj;

import com.adkin.cyberdiagramer.CyberDiagramer;

public class LeftPanel extends JPanel {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private CyberDiagramer shapeDrawer;

    // Icons
    private Icon systemIcon;
    private Icon systemCollectionIcon;
    private Icon boundaryIcon;
    private Icon boundaryCollectionIcon;
    private Icon entryPointIcon;
    private Icon connectionIcon;
    private Icon interfaceIcon;

    public LeftPanel(CyberDiagramer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 800));

        // Load custom icons
        systemIcon = new ImageIcon(getClass().getResource("/icons/system_icon.png"));
        systemCollectionIcon = new ImageIcon(getClass().getResource("/icons/system_collection_icon.png"));
        boundaryIcon = new ImageIcon(getClass().getResource("/icons/boundary_icon.png"));
        boundaryCollectionIcon = new ImageIcon(getClass().getResource("/icons/boundary_collection_icon.png"));
        entryPointIcon = new ImageIcon(getClass().getResource("/icons/entry_point_icon.png"));
        connectionIcon = new ImageIcon(getClass().getResource("/icons/connection_icon.png"));
        interfaceIcon = new ImageIcon(getClass().getResource("/icons/interface_icon.png"));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Shapes");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);

        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                updateNodeIcon(node);

                return c;
            }

            private void updateNodeIcon(DefaultMutableTreeNode node) {
                if (node.getUserObject() instanceof System_Obj) {
                    setIcon(node.getChildCount() > 0 ? systemCollectionIcon : systemIcon);
                } else if (node.getUserObject() instanceof AuthorizationBoundary) {
                    setIcon(node.getChildCount() > 0 ? boundaryCollectionIcon : boundaryIcon);
                } else if (node.getUserObject() instanceof Entry_Point) {
                    setIcon(entryPointIcon);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);

        setupTreeDoubleClick();
    }

    public void updateList(List<Shape> items, List<SystemConnection> connections) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        Map<AuthorizationBoundary, DefaultMutableTreeNode> boundaryNodes = new HashMap<>();
        Map<System_Obj, DefaultMutableTreeNode> systemNodes = new HashMap<>();
        // Map<SystemConnection, DefaultMutableTreeNode> connectionNodes = new HashMap<>();

        // Create nodes for all boundaries
        for (Shape item : items) {
            if (item instanceof AuthorizationBoundary) {
                AuthorizationBoundary boundary = (AuthorizationBoundary) item;
                DefaultMutableTreeNode boundaryNode = new DefaultMutableTreeNode(boundary);
                boundaryNodes.put(boundary, boundaryNode);
                if (boundary.parent == null) {
                    root.add(boundaryNode);
                }
            }
        }

        // Build hierarchy for boundaries
        for (AuthorizationBoundary boundary : boundaryNodes.keySet()) {
            if (boundary.parent != null) {
                DefaultMutableTreeNode parentBoundaryNode = boundaryNodes.get(boundary.parent);
                if (parentBoundaryNode != null) {
                    parentBoundaryNode.add(boundaryNodes.get(boundary));
                }
            }
        }

        // Create nodes for all systems
        for (Shape item : items) {
            if (item instanceof System_Obj) {
                System_Obj system = (System_Obj) item;
                DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode(system);
                systemNodes.put(system, systemNode);
                if (system.parent == null && system.boundary == null) {
                    root.add(systemNode);
                }
            }
        }

        // Build hierarchy for systems
        for (System_Obj system : systemNodes.keySet()) {
            if (system.parent != null) {
                DefaultMutableTreeNode parentSystemNode = systemNodes.get(system.parent);
                if (parentSystemNode != null) {
                    parentSystemNode.add(systemNodes.get(system));
                }
            }
            if (system.boundary != null) {
                DefaultMutableTreeNode boundaryNode = boundaryNodes.get(system.boundary);
                if (boundaryNode != null) {
                    boundaryNode.add(systemNodes.get(system));
                }
            }
        }

        // Add Entry Points under their corresponding Authorization Boundary
        for (Shape item : items) {
            if (item instanceof Entry_Point) {
                Entry_Point entryPoint = (Entry_Point) item;
                DefaultMutableTreeNode entryPointNode = new DefaultMutableTreeNode(entryPoint);
                AuthorizationBoundary boundary = entryPoint.getAuthorizationBoundaryOwner();
                if (boundary != null) {
                    DefaultMutableTreeNode boundaryNode = boundaryNodes.get(boundary);
                    if (boundaryNode != null) {
                        boundaryNode.add(entryPointNode);
                    }
                } else {
                    root.add(entryPointNode); // If no boundary owner, add to root
                }
            }
        }

        // Add Interfaces under their corresponding System
        for (Shape item : items) {
            if (item instanceof Interface_Obj) {
                Interface_Obj interfaceObj = (Interface_Obj) item;
                DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(interfaceObj);
                System_Obj system = interfaceObj.getSystem();
                if (system != null) {
                    DefaultMutableTreeNode systemNode = systemNodes.get(system);
                    if (systemNode != null) {
                        systemNode.add(interfaceNode);
                    }
                } else {
                    root.add(interfaceNode); // If no system owner, add to root
                }
            }
        }

        // Create and organize nodes for all shapes
        for (SystemConnection connection : connections) {
        
            Shape owner1 = connection.getOwner(0);
            Shape owner2 = connection.getOwner(1);
                
            if (owner1 != null) {
                DefaultMutableTreeNode systemNode1 = systemNodes.get(owner1);
                if (systemNode1 != null) {
                    systemNode1.add(new DefaultMutableTreeNode(connection));
                }
            }
        
            if (owner2 != null && owner2 != owner1) {
                DefaultMutableTreeNode systemNode2 = systemNodes.get(owner2);
                if (systemNode2 != null) {
                    systemNode2.add(new DefaultMutableTreeNode(connection));
                }
            }
        }

        treeModel.reload();
    }

    // private void buildSystemHierarchy(DefaultMutableTreeNode parent, System_Obj
    // system, Set<System_Obj> addedSystems) {
    // for (System_Obj child : system.getChildren()) {
    // if (!addedSystems.contains(child)) {
    // DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
    // parent.add(childNode);
    // addedSystems.add(child);
    // buildSystemHierarchy(childNode, child, addedSystems);
    // }
    // }
    // }

    private void setupTreeDoubleClick() {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null)
                        return;

                    Object nodeInfo = node.getUserObject();
                    if (nodeInfo instanceof System_Obj) {
                        showSystemEditDialog((System_Obj) nodeInfo);
                    } else if (nodeInfo instanceof AuthorizationBoundary) {
                        showAuthorizationBoundaryEditDialog((AuthorizationBoundary) nodeInfo);
                    } else if (nodeInfo instanceof Entry_Point) {
                        showEntryPointEditDialog((Entry_Point) nodeInfo);
                    } else if (nodeInfo instanceof SystemConnection) {
                        showConnectionEditDialog((SystemConnection) nodeInfo);
                    }
                }
            }
        });
    }

    private void showSystemEditDialog(System_Obj system) {
        // Open a dialog to edit the system
        ShapeEditDialog dialog = new ShapeEditDialog(shapeDrawer, system);
        dialog.setLocationRelativeTo(shapeDrawer);
        dialog.setVisible(true);

        Color newColor = dialog.getSelectedColor();
        String newText = dialog.getEnteredText();

        if (newColor != null) {
            system.setColor(newColor);
        }
        system.setText(newText);
        repaint();
        return;
    }

    private void showAuthorizationBoundaryEditDialog(AuthorizationBoundary boundary) {
        // Open a dialog to edit the authorization boundary
        AuthorizationBoundaryEditDialog dialog = new AuthorizationBoundaryEditDialog(shapeDrawer, boundary);
        dialog.setLocationRelativeTo(shapeDrawer);
        dialog.setVisible(true);

        // Repaint to reflect any changes made in the dialog
        repaint();
    }

    private void showEntryPointEditDialog(Entry_Point entryPoint) {
        // Open a dialog to edit the entry point
        EntryPointEditDialog dialog = new EntryPointEditDialog(shapeDrawer, entryPoint);
        dialog.setLocationRelativeTo(shapeDrawer);
        dialog.setVisible(true);

        // Repaint to reflect any changes made in the dialog
        repaint();
    }

    private void showConnectionEditDialog(SystemConnection connection) {
        // Open a dialog to edit the connection
        ConnectionEditDialog dialog = new ConnectionEditDialog(shapeDrawer, connection, shapeDrawer.operationalDataList);
        dialog.setLocationRelativeTo(shapeDrawer);
        dialog.setVisible(true);

        // Repaint to reflect any changes made in the dialog
        repaint();
    }

}
