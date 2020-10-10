package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TabContentTextPanel {
    protected JPanel contentPanel;
    private JTextArea textArea;

    public TabContentTextPanel() {
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
    }

    public void updateContent(JarTreeNode node) {
        String path = node.path;
        if (null == path) {
            return;
        }

        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ( (line = reader.readLine()) != null) {
                textArea.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
