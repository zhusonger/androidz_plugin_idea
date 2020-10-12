package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.wrap.ColorUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UIHelper {
    /**
     * 创建语法 text area
     * @return
     */
    public static RSyntaxTextArea createRSyntaxTextArea() {
        RSyntaxTextArea rTextArea = new RSyntaxTextArea();
        rTextArea.setCodeFoldingEnabled(true);
        rTextArea.setAntiAliasingEnabled(true);
        rTextArea.setCaretPosition(0);
        rTextArea.setEditable(false);
        rTextArea.setDropTarget(null);

        Color color = UIManager.getColor("TextArea.background");
        boolean dark = false;
        if (null != color) {
            // 亮度
            double luminance = ColorUtil.getLuminance(color);
            // 深色系
            dark = luminance < 0.5;
        }
        // 更换主题
        try {
            String themeXml = dark ? "dark.xml" : "eclipse.xml";
            Theme theme = Theme.load(UIHelper.class.getClassLoader().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/"+themeXml));
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
        return rTextArea;
    }

    /**
     * 创建语法 scroll pane
     * @param rTextArea
     * @return
     */
    public static RTextScrollPane createScrollPane(JTextArea rTextArea) {
        RTextScrollPane rScrollPane = new RTextScrollPane(rTextArea);
        rScrollPane.setLineNumbersEnabled(true);
        rScrollPane.setFoldIndicatorEnabled(true);
        if (null != rTextArea) {
            rScrollPane.setFont(rTextArea.getFont());
        }
        return rScrollPane;
    }
}
