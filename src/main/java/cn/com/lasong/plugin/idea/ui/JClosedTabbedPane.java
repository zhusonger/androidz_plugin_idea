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
        TabContentTextPanel textPanel = new TabContentTextPanel();
        return textPanel.updateContent(node);
    }
}
