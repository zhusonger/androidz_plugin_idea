package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.jar.inject.InjectClzModify;
import cn.com.lasong.plugin.idea.jar.inject.InjectHelper;
import cn.com.lasong.plugin.idea.jar.inject.InjectModifyMethod;
import cn.com.lasong.plugin.idea.jar.inject.JMethodModel;
import cn.com.lasong.plugin.idea.ui.IResultListener;
import cn.com.lasong.plugin.idea.ui.IconsPlugin;
import cn.com.lasong.plugin.idea.ui.UIHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ModifyDialog extends DialogWrapper {
    private JPanel contentPane;
    // class
    private JLabel clzNameLabel;
    private JTextField clzModifyTextField;
    private JCheckBox clzModifyCheckBox;

    // method
    private JComboBox<String> actionComboBox;
    private JComboBox<JMethodModel> methodComboBox;

    // content
    private JTextArea contentTextArea;
    private JScrollPane contentScrollPane;
    private JTextField methodTextField;
    private JSpinner lineNumSpinner;
    private JComboBox<String> typeComboBox;
    private JPanel modifyPanel;
    private JPanel optionPanel;
    private JTextField rangeTextField;
    private JPanel deletePanel;
    private JPanel methodPanel;
    private JPanel insertPanel;
    private JTextField modifiersTextField;
    private JTextArea codeTextArea;
    private JScrollPane codeScrollPane;
    private final CardLayout optionLayout;

    private final JarTreeNode jarNode;

    private IResultListener listener;

    public ModifyDialog(JarTreeNode node) {
        super(PluginHelper.getProject());
        init();
        jarNode = node;
        clzNameLabel.setText("Class["+jarNode.className()+"]");
        clzNameLabel.setIcon(IconsPlugin.CLASS_OBJ_ICON);
        clzModifyCheckBox.addActionListener(e -> {
            boolean editable = clzModifyCheckBox.isSelected();
            clzModifyTextField.setEnabled(editable);
            if (editable) {
                clzModifyTextField.requestFocus();
            }
        });
        optionLayout = (CardLayout) optionPanel.getLayout();
        methodPanel.setVisible(true);
        optionPanel.setVisible(true);
        optionLayout.show(optionPanel, "modify");
        String modifiers = InjectHelper.getClassModifiers(jarNode);
        clzModifyTextField.setText(modifiers);
        List<JMethodModel> methods = InjectHelper.getMethods(jarNode);
        for (JMethodModel model : methods) {
            methodComboBox.addItem(model);
        }

        actionComboBox.addItemListener(e -> {
            String action = (String) actionComboBox.getSelectedItem();
            if (null == action) {
                return;
            }
            toggleModifyUI(action.startsWith("modify"));
        });

        typeComboBox.addItemListener(e -> updateOptionUI());
    }


    public void setListener(IResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    private String ofAction() {
        String action = (String) actionComboBox.getSelectedItem();
        if (null != action) {
            if (action.equalsIgnoreCase("modify")) {
                return InjectModifyMethod.ACTION_MODIFY;
            } else if (action.equalsIgnoreCase("addField")) {
                return InjectModifyMethod.ACTION_ADD_FIELD;
            } else if (action.equalsIgnoreCase("addMethod")) {
                return InjectModifyMethod.ACTION_ADD_METHOD;
            }
        }
        return null;
    }

    @Override
    protected void doOKAction() {

        JMethodModel model = (JMethodModel) methodComboBox.getSelectedItem();
        if (null == model) {
            return;
        }
        String action = ofAction();
        if (null != action && null != jarNode) {
            InjectClzModify clzModify = new InjectClzModify();
            clzModify.className = jarNode.className();
            if (clzModifyCheckBox.isSelected()) {
                clzModify.modifiers = clzModifyTextField.getText();
            }
            model.action = action;
            model.type = (String) typeComboBox.getSelectedItem();
            model.content = contentTextArea.getText();
            model.lineNum = (int) lineNumSpinner.getValue();
            model.lineRange = rangeTextField.getText();
            model.newName = methodTextField.getText();
            model.modifiers = modifiersTextField.getText();
            clzModify.setModifyMethods(new InjectModifyMethod[]{model});
            byte[] buffer = InjectHelper.injectClass(clzModify);
            if (null != buffer) {
                File file = new File(jarNode.path);
                try {
                    FileUtil.writeToFile(file, buffer);
                    if (null != listener) {
                        listener.onResult(true);
                    }
                } catch (IOException e) {
                    PluginHelper.error(e);
                    if (null != listener) {
                        listener.onResult(false, e);
                    }
                }
            }
        }
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

        rTextArea = UIHelper.createRSyntaxTextArea();
        rTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
        codeTextArea = rTextArea;
        codeScrollPane = UIHelper.createScrollPane(rTextArea);
    }

    private void toggleModifyUI(boolean modify) {
        methodPanel.setVisible(modify);

        if (modify) {
            updateOptionUI();
        } else {
            optionPanel.setVisible(false);
        }
        repaint();
    }

    private void updateOptionUI() {
        String type = (String) typeComboBox.getSelectedItem();
        if (null == type) {
            return;
        }
        if (type.startsWith("deleteAt")) {
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "delete");
        } else if (type.startsWith("insertAt")){
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "insert");
        } else if (type.startsWith("setBody")){
            optionPanel.setVisible(false);
            optionLayout.show(optionPanel, "insert");
        } else {
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "modify");
        }
    }
}
