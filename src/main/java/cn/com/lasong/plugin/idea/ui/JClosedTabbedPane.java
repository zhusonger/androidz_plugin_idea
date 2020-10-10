package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class JClosedTabbedPane extends JBTabbedPane {
    public JClosedTabbedPane() {
        super();
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public int addTab(JarTreeNode node) {
        if (null == node || null == node.name) {
            return -1;
        }
        Component component = getContent(node);
        if (null == component) {
            return -1;
        }
        addTab(node.name, component);
        int index = indexOfComponent(component);
        // 重新设置选项卡
        ClosedTab tab = ClosedTab.newTab(this, node);
        setTabComponentAt(index, tab);
        return index;
    }

    /**
     * 获取内容组件
     * @param node
     * @return
     */
    private Component getContent(@NotNull JarTreeNode node) {
        String name = node.name;
        int index = name.lastIndexOf(".");
        String suffix = null;
        if (index > 0) {
            suffix = name.substring(index + 1).toLowerCase();
        }
        Component component = null;
        //  字节码
        if (null != suffix && suffix.equalsIgnoreCase("class")) {

        } else {
            TabContentTextPanel textPanel = new TabContentTextPanel();
            textPanel.updateContent(node);
            component = textPanel.contentPanel;
        }
        return component;
    }
}
