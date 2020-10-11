package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.ui.IconsPlugin;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ModifyDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel clzNameLabel;

    private JarTreeNode jarNode;

    public ModifyDialog(JarTreeNode node) {
        super(PluginHelper.getProject());
        init();
        jarNode = node;
        clzNameLabel.setText(jarNode.entryName());
        clzNameLabel.setIcon(IconsPlugin.CLASS_OBJ_ICON);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }
}
