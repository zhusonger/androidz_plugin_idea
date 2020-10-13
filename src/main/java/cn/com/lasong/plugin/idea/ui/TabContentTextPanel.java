package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.jar.dialog.ModifyDialog;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.util.io.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TabContentTextPanel extends DefaultTabContentPanel implements IResultListener{
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    public TabContentTextPanel() {
        super();
    }

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

    @Override
    protected void modifyContent(JarTreeNode node) {
        super.modifyContent(node);
        ModifyDialog dialog = new ModifyDialog(node);
        dialog.setListener(this);
        dialog.show();
    }

    @Override
    protected void saveContent(JarTreeNode node) {
        File file = new File(jarNode.path);
        try {
            FileUtil.writeToFile(file, rTextArea.getText());
        } catch (IOException e) {
            PluginHelper.error(e);
        }
    }

    @Override
    public void onResult(Object... result) {
        boolean ret = (boolean) result[0];
        if (ret) {
            String content = JDHelper.decompile(jarNode.entryName());
            textArea.setText(content);
        }
    }
}
