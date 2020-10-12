package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.ui.UIHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModifyDialog extends DialogWrapper {
    private JPanel contentPane;
    // class
    private JLabel clzNameLabel;
    private JTextField clzModifyTextField;
    private JCheckBox clzModifyCheckBox;

    // method
    private JComboBox actionComboBox;
    private JComboBox methodComboBox;

    // content
    private JTextArea contentTextArea;
    private JScrollPane contentScrollPane;
    private JTextField textField1;
    private JSpinner spinner1;

    private JarTreeNode jarNode;

    public ModifyDialog(JarTreeNode node) {
        super(PluginHelper.getProject());
        init();
        jarNode = node;
//        clzNameLabel.setText(jarNode.entryName());
//        clzNameLabel.setIcon(IconsPlugin.CLASS_OBJ_ICON);
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

    private void createUIComponents() {
        contentTextArea = UIHelper.createRSyntaxTextArea();
        contentScrollPane = UIHelper.createScrollPane(contentTextArea);
    }
}
