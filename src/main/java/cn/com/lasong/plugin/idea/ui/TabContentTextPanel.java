package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TabContentTextPanel extends DefaultTabContentPanel {
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    @Override
    protected String getStyle() {
        return super.getStyle();
    }

    protected void createUIComponents() {
        super.createUIComponents();
        scrollPane = rScrollPane;
        textArea = rTextArea;
    }

    public Component getContentPanel() {
        return contentPanel;
    }
}
