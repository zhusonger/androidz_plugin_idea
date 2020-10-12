package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.jar.inject.InjectHelper;
import cn.com.lasong.plugin.idea.ui.IconsPlugin;
import cn.com.lasong.plugin.idea.ui.UIHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.ui.DialogWrapper;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JTextField methodTextField;
    private JSpinner lineNumSpinner;
    private JComboBox typeComboBox;
    private JPanel modifyPanel;
    private JPanel optionPanel;
    private JTextField rangeTextField;
    private JPanel rangePanel;
    private JPanel methodPanel;
    private CardLayout optionLayout;

    private JarTreeNode jarNode;

    public ModifyDialog(JarTreeNode node) {
        super(PluginHelper.getProject());
        init();
        jarNode = node;
        clzNameLabel.setText("Class["+jarNode.className()+"]");
        clzNameLabel.setIcon(IconsPlugin.CLASS_OBJ_ICON);
        clzModifyCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean editable = clzModifyCheckBox.isSelected();
                clzModifyTextField.setEnabled(editable);
                if (editable) {
                    clzModifyTextField.requestFocus();
                }
            }
        });
        optionLayout = (CardLayout) optionPanel.getLayout();
        methodPanel.setVisible(true);
        optionPanel.setVisible(true);
        optionLayout.show(optionPanel, "modify");
        String modifiers = InjectHelper.getClassModifiers(jarNode);
        clzModifyTextField.setText(modifiers);
        InjectHelper.getMethods(jarNode);
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
        RSyntaxTextArea rTextArea = UIHelper.createRSyntaxTextArea();
        rTextArea.setEditable(true);
        rTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        contentTextArea = rTextArea;
        contentScrollPane = UIHelper.createScrollPane(rTextArea);
    }
}
