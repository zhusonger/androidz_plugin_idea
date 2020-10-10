package cn.com.lasong.plugin.idea.ui;

import javax.swing.*;
import java.awt.*;

public class TabContentTextPanel extends DefaultTabContentPanel {
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    @Override
    protected String getStyle() {
        return super.getStyle();
    }

    @Override
    protected void createUIComponents() {
        super.createUIComponents();
        contentPanel = wrappedPanel(this);
        scrollPane = rScrollPane;
        textArea = rTextArea;
    }

    @Override
    public Component getContentPanel() {
        return contentPanel;
    }
}
