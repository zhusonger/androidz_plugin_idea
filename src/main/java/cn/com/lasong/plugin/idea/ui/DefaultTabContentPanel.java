package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import cn.com.lasong.plugin.idea.jar.jdcore.JDPrinter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DefaultTabContentPanel {
    protected RTextScrollPane rScrollPane;
    protected RSyntaxTextArea rTextArea;
    protected JarTreeNode jarNode;
    protected static final Color DOUBLE_CLICK_HIGHLIGHT_COLOR = new Color(0x66ff66);

    public DefaultTabContentPanel() {
    }

    public void updateContent(@NotNull JarTreeNode node) {
        if (null == node.path || node.name == null) {
            return;
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
            }
        }


        if (null == content) {
            File file = new File(node.path);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder builder = new StringBuilder();
                while ( (line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                content = builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rTextArea.setText(content);
        rTextArea.setSyntaxEditingStyle(getStyle());
        rTextArea.setCaretPosition(0);
    }

    /**
     * 文本样式
     * @return
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
        rTextArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    rTextArea.setMarkAllHighlightColor(DOUBLE_CLICK_HIGHLIGHT_COLOR);
                }
            }
        });
        rScrollPane = new RTextScrollPane();
        rScrollPane.setFoldIndicatorEnabled(true);
        rScrollPane.setFont(rTextArea.getFont());
    }
}
