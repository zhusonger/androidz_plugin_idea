package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarCellRender;
import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;

public class ClosedTab extends JPanel {
    private JLabel label;
    private JButton button;
    private JarTreeNode dataNode;
    private int index;
    private WeakReference<JTabbedPane> tabbedPaneRef;

    private ClosedTab() {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        label = new JLabel();
        label.setOpaque(false);
        add(label);

        button = new JButton();
        button.setPreferredSize(new Dimension(24, 24));
        button.setOpaque(false);
        //No need to be focusable
        button.setFocusable(false);
        //Making nice rollover effect
        button.setRolloverEnabled(true);
        //Make the button looks the same for all Laf's
        button.setUI(new BasicButtonUI());
        //Make it transparent
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 0));
        button.setIcon(IconsPlugin.CLOSE_ICON);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(null == tabbedPaneRef) {
                    return;
                }
                JTabbedPane tabbedPane = tabbedPaneRef.get();
                if (null == tabbedPane) {
                    return;
                }
                int index = tabbedPane.indexOfTabComponent(ClosedTab.this);
                if (index >= 0) {
                    tabbedPane.remove(index);
                }
            }
        });
        add(button);
    }

    public static ClosedTab newTab(@NotNull JTabbedPane tabbedPane, @NotNull JarTreeNode node) {
        ClosedTab tab = new ClosedTab();
        tab.tabbedPaneRef = new WeakReference<>(tabbedPane);
        tab.label.setText(node.name);
        tab.label.setIcon(JarCellRender.getIcon(node));
        tab.dataNode = node;
        return tab;
    }

    public boolean isEqual(DefaultMutableTreeNode node) {
        if (node == null) {
            return false;
        }
        JarTreeNode jarNode = (JarTreeNode) node.getUserObject();
        if (jarNode.entryName().equals(dataNode.entryName())) {
            return true;
        }
        return false;
    }
}
