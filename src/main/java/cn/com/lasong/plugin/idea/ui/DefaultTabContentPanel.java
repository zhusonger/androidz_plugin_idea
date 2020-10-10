package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.util.io.FileUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
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
                content = JDHelper.decompile(node.entryName(), node.path);
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
            rTextArea.setText(content);
            rTextArea.setSyntaxEditingStyle(getStyle());
            rTextArea.setCaretPosition(0);
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
        rTextArea = new RSyntaxTextArea();
        rTextArea.setCodeFoldingEnabled(true);
        rTextArea.setAntiAliasingEnabled(true);
        rTextArea.setCaretPosition(0);
        rTextArea.setEditable(false);
        rTextArea.setDropTarget(null);
        rTextArea.setPopupMenu(null);
        Color color = UIManager.getColor("TextArea.background");
        boolean dark = false;
        if (null != color) {
            // 亮度
            double luminance = PluginHelper.getLuminance(color);
            // 深色系
            dark = luminance < 0.5;
        }
        // 更换主题
        try {
            String themeXml = dark ? "dark.xml" : "eclipse.xml";
            Theme theme = Theme.load(getClass().getClassLoader().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/"+themeXml));
            theme.apply(rTextArea);
        } catch (IOException ignored) {}

        // 替换系统的颜色
        if (UIManager.getFont("TextArea.font") != null) {
            rTextArea.setFont(UIManager.getFont("TextArea.font"));
        }
        if (UIManager.getColor("TextArea.background") != null) {
            rTextArea.setBackground(UIManager.getColor("TextArea.background"));
        }
        if (UIManager.getColor("TextArea.caretForeground") != null) {
            rTextArea.setCaretColor(UIManager.getColor("TextArea.caretForeground"));
        }
        if (UIManager.getColor("TextArea.selectionForeground") != null) {
            rTextArea.setSelectedTextColor(UIManager.getColor("TextArea.selectionForeground"));
        }
        if (UIManager.getColor("TextArea.selectionBackground") != null) {
            rTextArea.setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
        }
        rScrollPane = new RTextScrollPane(rTextArea);
        rScrollPane.setLineNumbersEnabled(true);
        rScrollPane.setFoldIndicatorEnabled(true);
        rScrollPane.setFont(rTextArea.getFont());
    }

    protected Component getContentPanel() {
        return null;
    }
}
