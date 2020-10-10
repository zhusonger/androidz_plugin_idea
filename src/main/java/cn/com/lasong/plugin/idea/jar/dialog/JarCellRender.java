package cn.com.lasong.plugin.idea.jar.dialog;

import icons.IconsPlugin;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class JarCellRender extends DefaultXTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value != null && tree.isEnabled() && value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
            if (userObject instanceof JarTreeNode) {
                JarTreeNode node = (JarTreeNode) userObject;
                Icon icon = getIcon(node, sel, expanded, leaf);
                if (null != icon) {
                    setIcon(icon);
                }
            }
        }
        return this;
    }

    /**
     * 获取默认图标
     * @param node
     * @param sel
     * @param expanded
     * @param leaf
     * @return
     */
    private static Icon getIcon(JarTreeNode node, boolean sel, boolean expanded, boolean leaf) {
        if (node == null || node.name == null) {
            return IconsPlugin.NONE_ICON;
        }
        String name = node.name;
        int index = name.lastIndexOf(".");
        String suffix = null;
        if (index > 0) {
            suffix = name.substring(index + 1).toLowerCase();
        }

        Icon icon = IconsPlugin.NONE_ICON;
        if (leaf && null != suffix) {
            if (suffix.equalsIgnoreCase("jar")) {
                icon = IconsPlugin.JAR_OBJ_ICON;
            } else if (suffix.equalsIgnoreCase("class")) {
                icon = IconsPlugin.CLASS_OBJ_ICON;
                // INTERFACE_OBJ_ICON
            } else if (suffix.equalsIgnoreCase("mf")){
                icon = IconsPlugin.MANIFEST_OBJ_ICON;
            } else if (suffix.equalsIgnoreCase("properties")) {
                icon = IconsPlugin.PROPERTIES_ICON;
            } else if (suffix.equalsIgnoreCase("txt") ) {
                icon = IconsPlugin.TXT_ICON;
            } else if (suffix.equalsIgnoreCase("xml")) {
                icon = IconsPlugin.XML_OBJ_ICON;
            } else if (suffix.equalsIgnoreCase("config")) {
                icon = IconsPlugin.CONFIG_OBJ_ICON;
            }
        } else if (!leaf) {
            icon = IconsPlugin.FOLDER_ICON;
        }
        return icon;
    }

    /**
     * 获取Icon
     * @param node
     * @return
     */
    public static Icon getIcon(JarTreeNode node) {
        return getIcon(node, false, false, true);
    }

}
