package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.jar.inject.InjectClzModify;
import cn.com.lasong.plugin.idea.jar.inject.InjectHelper;
import cn.com.lasong.plugin.idea.jar.inject.InjectCtModify;
import cn.com.lasong.plugin.idea.jar.inject.JMethodModel;
import cn.com.lasong.plugin.idea.ui.IResultListener;
import cn.com.lasong.plugin.idea.ui.IconsPlugin;
import cn.com.lasong.plugin.idea.ui.UIHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 显示修改字节码弹窗
 */
public class ModifyDialog extends DialogWrapper {
    private JPanel contentPane;
    // class
    private JLabel clzNameLabel;
    private JTextField clzModifyTextField;

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
    private final CardLayout optionLayout;

    private final JarTreeNode jarNode;

    private IResultListener listener;

    public ModifyDialog(JarTreeNode node) {
        super(PluginHelper.getProject());
        init();
        jarNode = node;
        clzNameLabel.setText("Class[" + jarNode.className() + "]");
        clzNameLabel.setIcon(IconsPlugin.CLASS_OBJ_ICON);
        optionLayout = (CardLayout) optionPanel.getLayout();
        methodPanel.setVisible(true);
        optionPanel.setVisible(true);
        optionLayout.show(optionPanel, "modify");
        List<JMethodModel> methods = InjectHelper.getAllCts(jarNode);
        for (JMethodModel model : methods) {
            methodComboBox.addItem(model);
        }

        actionComboBox.addItemListener(e -> {
            updateActionUI(ofAction());
        });

        typeComboBox.addItemListener(e -> updateTypeUI());
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        lineNumSpinner.setModel(model);

        methodComboBox.setEditable(true);
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
                return InjectCtModify.ACTION_MODIFY;
            } else if (action.equalsIgnoreCase("addField")) {
                return InjectCtModify.ACTION_ADD_FIELD;
            } else if (action.equalsIgnoreCase("addMethod")) {
                return InjectCtModify.ACTION_ADD_METHOD;
            }
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        String action = ofAction();
        if (null != action && null != jarNode) {
            InjectClzModify clzModify = new InjectClzModify();
            clzModify.className = jarNode.className();
            String clzModifiers = clzModifyTextField.getText();
            if (null != clzModifiers && clzModifiers.trim().length() > 0) {
                clzModify.modifiers = clzModifiers;
            }

            InjectCtModify method = new InjectCtModify();
            method.action = action;
            if (InjectCtModify.ACTION_ADD_FIELD.equals(action)
                    || InjectCtModify.ACTION_ADD_METHOD.equals(action)) {
                method.content = contentTextArea.getText();
            } else if (InjectCtModify.ACTION_MODIFY.equals(action)) {
                JMethodModel model = (JMethodModel) methodComboBox.getSelectedItem();
                String type = (String) typeComboBox.getSelectedItem();
                if (null != model) {
                    method.params = model.params;
                    method.name = model.name;
                    method.isConstructor = model.isConstructor;
                }
                if (!PluginHelper.isEmpty(contentTextArea.getText()) && null != type
                        && (type.startsWith("insert") || type.equalsIgnoreCase("setBody"))) {
                    method.content = contentTextArea.getText();
                }
                method.type = type;
                if ("insertAt".equals(type)) {
                    method.lineNum = (int) lineNumSpinner.getValue();
                } else if ("deleteAt".equals(type)) {
                    method.lineRange = rangeTextField.getText();
                }
                if (!PluginHelper.isEmpty(methodTextField.getText())) {
                    method.newName = methodTextField.getText();
                }
                if (!PluginHelper.isEmpty(modifiersTextField.getText())) {
                    method.modifiers = modifiersTextField.getText();
                }
            }

            clzModify.setModifyMethods(new InjectCtModify[]{method});
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
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String action = ofAction();
        ValidationInfo validationInfo = null;
        // 只要是修改类的, 无所谓下面的内容
        String clzModifiers = clzModifyTextField.getText();
        if (!PluginHelper.isEmpty(clzModifiers)) {
            return null;
        }

        if (null != action) {
            String content = contentTextArea.getText();
            if ((InjectCtModify.ACTION_ADD_FIELD.equals(action)
                    || InjectCtModify.ACTION_ADD_METHOD.equals(action)) && PluginHelper.isEmpty(content)) {
                validationInfo = new ValidationInfo("Content can't empty.", contentTextArea);
            } else if (InjectCtModify.ACTION_MODIFY.equals(action)) {
                JMethodModel model = (JMethodModel) methodComboBox.getSelectedItem();
                String type = (String) typeComboBox.getSelectedItem();
                if (null == model) {
                    validationInfo = new ValidationInfo("Method can't empty.", methodComboBox);
                } else if (("insertBefore".equalsIgnoreCase(type)
                            || "insertAfter".equalsIgnoreCase(type)
                            || "insertAt".equalsIgnoreCase(type)
                            || "setBody".equalsIgnoreCase(type)) && PluginHelper.isEmpty(content)) {
                    validationInfo = new ValidationInfo("Content can't empty.", contentTextArea);
                } else if ("deleteAt".equalsIgnoreCase(type) && PluginHelper.isEmpty(rangeTextField.getText())) {
                    validationInfo = new ValidationInfo("LineRange can't empty.", rangeTextField);
                }
            }
        }

        return validationInfo;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return contentTextArea;
    }

    /**
     * 根据action更新显示组件
     * @param action
     */
    private void updateActionUI(String action) {
        if (InjectCtModify.ACTION_MODIFY.equals(action)) {
            methodPanel.setVisible(true);
            updateTypeUI();
        } else if (InjectCtModify.ACTION_ADD_FIELD.equals(action)
            || InjectCtModify.ACTION_ADD_METHOD.equals(action)){
            methodPanel.setVisible(false);
            optionPanel.setVisible(false);
        }
        repaint();
    }

    /**
     * 根据type展示UI
     */
    private void updateTypeUI() {
        String type = (String) typeComboBox.getSelectedItem();
        if (null == type) {
            return;
        }
        if (type.startsWith("deleteAt")) {
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "delete");
            contentScrollPane.setEnabled(false);
        } else if (type.startsWith("insertAt")) {
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "insert");
            contentScrollPane.setEnabled(true);
        } else if (type.startsWith("setBody")) {
            optionPanel.setVisible(false);
            optionLayout.show(optionPanel, "insert");
            contentScrollPane.setEnabled(true);
        } else {
            optionPanel.setVisible(true);
            optionLayout.show(optionPanel, "modify");
            contentScrollPane.setEnabled(true);
        }
    }
}
