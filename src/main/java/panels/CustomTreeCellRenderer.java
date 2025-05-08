package panels;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import shapes.AuthorizationBoundary;
import shapes.System_Obj;

import java.awt.Component;

class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon systemIcon;
    private Icon boundaryIcon;

    public CustomTreeCellRenderer(Icon systemIcon, Icon boundaryIcon) {
        this.systemIcon = systemIcon;
        this.boundaryIcon = boundaryIcon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof System_Obj) {
            setIcon(systemIcon);
        } else if (node.getUserObject() instanceof AuthorizationBoundary) {
            setIcon(boundaryIcon);
        }

        return this;
    }
}
