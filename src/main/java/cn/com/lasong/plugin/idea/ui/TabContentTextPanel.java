package cn.com.lasong.plugin.idea.ui;

import cn.com.lasong.plugin.idea.jar.InjectClzModify;
import cn.com.lasong.plugin.idea.jar.InjectHelper;
import cn.com.lasong.plugin.idea.jar.InjectModifyMethod;
import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.util.io.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TabContentTextPanel extends DefaultTabContentPanel {
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
        String entryName = node.entryName();
        if (entryName.endsWith(".class")) {
            InjectClzModify clzModify = new InjectClzModify();
            clzModify.className = node.className();
            InjectModifyMethod[] methods = new InjectModifyMethod[1];
            InjectModifyMethod method = new InjectModifyMethod();
            method.action = InjectModifyMethod.ACTION_ADD_FIELD;
            Random random = new Random(System.currentTimeMillis());
            method.content = "public int testValue_"+random.nextInt(10)+";";
            methods[0] = method;
            clzModify.setModifyMethods(methods);
            // brut/apktool/Main.class
            // {"className":"null","importPackages":null,"modifyMethods":[{"action":"MODIFY","modifiers":"null","name":"null","params":"null","content":"public int testValue_-1741296790;","newName":"null","type":"ADD_FIELD","lineNum":-1,"lineRange":null}],"isInject":true}
            byte[] buffer = InjectHelper.injectClass(clzModify);
            if (null != buffer) {
                File file = new File(node.path);
                try {
                    FileUtil.writeToFile(file, buffer);
                } catch (IOException e) {
                    PluginHelper.error(e);
                }
            }
        }
    }
}
