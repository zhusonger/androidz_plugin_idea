package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import com.intellij.openapi.util.io.FileUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DefaultTabContentPanel {
    protected RTextScrollPane rScrollPane;
    protected RSyntaxTextArea rTextArea;
    protected JarTreeNode jarNode;
    protected JPopupMenu popupMenu;

    public DefaultTabContentPanel() {
    }

    /**
     * 更新内容
     */
    public Component updateContent(@NotNull JarTreeNode node) {
        if (null == node.path || node.name == null) {
            return null;
        }

        jarNode = node;

        String name = jarNode.name;
        String content = null;
        int index = name.lastIndexOf(".");
        String suffix = null;
        if (index > 0) {
            suffix = name.substring(index + 1).toLowerCase();
        }
        if (null != suffix) {
            if(suffix.equalsIgnoreCase("class")) {
                content = JDHelper.decompile(node.entryName());
            } else if (suffix.equalsIgnoreCase("txt")
                    || suffix.equalsIgnoreCase("mf")
                    || suffix.equalsIgnoreCase("config")
                    || suffix.equalsIgnoreCase("properties")
                    || suffix.equalsIgnoreCase("xml")
                    || suffix.equalsIgnoreCase("vm")) {
                File file = new File(node.path);
                try {
                    content = FileUtil.loadFile(file);
                } catch (IOException ignored) {}
            }
        }

        if (null != content) {
            String style = getStyle();
            rTextArea.setText(content);
            rTextArea.setSyntaxEditingStyle(style);
            rTextArea.setCaretPosition(0);
            if (SyntaxConstants.SYNTAX_STYLE_JAVA.equalsIgnoreCase(style)) {
                rTextArea.setEditable(false);
                popupMenu = rTextArea.getPopupMenu();
                JMenuItem modifyItem = new JMenuItem("修改");
                popupMenu.add(modifyItem, 0);
                modifyItem.addActionListener(e -> {
                    modifyContent(jarNode);
                });
            } else {
                rTextArea.setEditable(true);
                popupMenu = rTextArea.getPopupMenu();
                JMenuItem saveItem = new JMenuItem("保存");
                popupMenu.add(saveItem, 0);
                saveItem.addActionListener(e -> {
                    saveContent(jarNode);
                });
            }
            rTextArea.setPopupMenu(popupMenu);
            return getContentPanel();
        }
        return null;
    }

    /**
     * 文本样式
     */
    protected String getStyle() {
        String style = SyntaxConstants.SYNTAX_STYLE_NONE;
        if (null != jarNode) {
            String name = jarNode.name;
            int index = name.lastIndexOf(".");
            String suffix = null;
            if (index > 0) {
                suffix = name.substring(index + 1).toLowerCase();
            }
            if (null != suffix) {
                if (suffix.equalsIgnoreCase("properties")) {
                    style = SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
                } else if (suffix.equalsIgnoreCase("xml")) {
                    style = SyntaxConstants.SYNTAX_STYLE_XML;
                } else if (suffix.equalsIgnoreCase("class") || suffix.equalsIgnoreCase("java")) {
                    style = SyntaxConstants.SYNTAX_STYLE_JAVA;
                }
            }
        }
        return style;
    }

    protected void createUIComponents() {
        rTextArea = UIHelper.createRSyntaxTextArea();
        rScrollPane = UIHelper.createScrollPane(rTextArea);
    }

    protected Component getContentPanel() {
        return null;
    }

    protected final <T> JPanel wrappedPanel(T data) {
        return new WrappedJPanel<>(data);
    }

    public JarTreeNode getJarNode() {
        return jarNode;
    }

    /**
     * 修改内容
     * @param node
     */
    protected void modifyContent(JarTreeNode node) {

    }

    /**
     * 保存文件
     * @param node
     */
    protected void saveContent(JarTreeNode node) {

    }
}
