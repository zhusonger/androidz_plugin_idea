package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.jar.inject.InjectHelper;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import cn.com.lasong.plugin.idea.ui.ClosedTab;
import cn.com.lasong.plugin.idea.ui.JClosedTabbedPane;
import cn.com.lasong.plugin.idea.utils.FileHelper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.util.ui.JBUI;
import org.jdesktop.swingx.JXTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class JarModifyDialog extends DialogWrapper {

    private File jarUnzipDir;
    private String jarPath;

    private boolean update;

    public JarModifyDialog(Project project) {
        this(project, null, null);
    }

    public JarModifyDialog(Project project, File jarDir, String path) {
        super(project);
        init();
        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                InjectHelper.release();
                if (null != project && null != project.getBasePath()) {
                    String basePath = project.getBasePath();
                    FileHelper.cleanTmpDir(basePath);
                }

                JDHelper.release();
                InjectHelper.release();
            }
        });
        this.jarUnzipDir = jarDir;
        this.jarPath = path;
        
        // 添加双击打开字节码内容
        // 新开Tab页
        codeTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Object source = e.getSource();
                int clickCount = e.getClickCount();
                if (source != codeTree || clickCount < 2) {
                    return;
                }
                TreePath selPath = codeTree.getPathForLocation(e.getX(), e.getY());
                // 谨防空指针异常!双击空白处是会这样
                if (selPath == null) {
                    return;
                }
                // 获取这个路径上的最后一个组件,也就是双击的地方
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                editLeafNode(node);
            }
        });

        // 更新树
        if (null != jarDir) {
            String name = jarDir.getName();
            setTitle(name + ".jar");
            JarTreeHelper.updateTree(codeTree, jarDir);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        JarModifyDialog test = new JarModifyDialog(null);
        frame.setContentPane(test.contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public JPanel contentPanel;
    private JTree codeTree;
    private JTabbedPane tabbedPane;

    private void createUIComponents() {
        // 创建自己的jar包树路径
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
        codeTree = new JXTree(root);
        codeTree.setBorder(JBUI.Borders.empty(2, 6, 0, 0));
        codeTree.setCellRenderer(new JarCellRender());
        tabbedPane = new JClosedTabbedPane();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action cancel = getCancelAction();
        Action ok = getOKAction();
        ok.putValue(Action.NAME, "Done");
        return new Action[]{cancel, ok};
    }

    @Override
    protected void doOKAction() {
        if (null != jarPath && null != jarUnzipDir) {
            File dir = FileHelper.zipJar(jarPath, jarUnzipDir);
            if (null != dir) {
                LocalFileSystem.getInstance().refreshAndFindFileByIoFile(dir);
            }
        }
        super.doOKAction();
    }

    /**
     * 编辑叶子节点
     * @param node
     */
    private void editLeafNode(DefaultMutableTreeNode node) {
        if (null == node || !node.isLeaf() || null == node.getUserObject()) {
            return;
        }
        JarTreeNode jarNode = (JarTreeNode) node.getUserObject();
        String name = jarNode.name;
        if (null == name || !name.contains(".")) {
            return;
        }
        int count = tabbedPane.getTabCount();
        int index = -1;
        for (int i = 0; i < count; i++) {
            Component component = tabbedPane.getTabComponentAt(i);
            // 已经存在的, 直接选中
            if (component instanceof ClosedTab && ((ClosedTab) component).isEqual(node)) {
                index = i;
                break;
            }
        }

        // 不存在已经存在的, 新建并选中
        if (index < 0) {
            index = ((JClosedTabbedPane)tabbedPane).addTab(jarNode);
        }
        if (index >= 0) {
            tabbedPane.setSelectedIndex(index);
        }
    }
}
