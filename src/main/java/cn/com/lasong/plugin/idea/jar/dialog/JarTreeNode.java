package cn.com.lasong.plugin.idea.jar.dialog;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点
 */
public class JarTreeNode {
    public JarTreeNode parent;
    public String name;
    public List<JarTreeNode> children;
    public String path;

    private JarTreeNode() {
    }

    /**
     * 创建字节码文件节点
     * @param parent
     * @param clz
     * @return
     */
    public static DefaultMutableTreeNode createClassNode(DefaultMutableTreeNode parent, File clz) {
        JarTreeNode jarNode = new JarTreeNode();
        jarNode.name = clz.getName();
        jarNode.parent = null != parent ? (JarTreeNode) parent.getUserObject() : null;
        jarNode.path = clz.getAbsolutePath();
        if(jarNode.parent != null) {
            jarNode.parent.children.add(jarNode);
        }
        return new DefaultMutableTreeNode(jarNode);
    }

    /**
     * 创建文件夹节点
     * @param parent
     * @param dir
     * @return
     */
    public static DefaultMutableTreeNode createRootNode(DefaultMutableTreeNode parent, File dir) {
        JarTreeNode jarNode = new JarTreeNode();
        jarNode.name = dir.getName();
        jarNode.parent = null != parent ? (JarTreeNode) parent.getUserObject() : null;
        jarNode.path = dir.getAbsolutePath();
        jarNode.children = new ArrayList<>();
        return new DefaultMutableTreeNode(jarNode);
    }

    /**
     * 创建根节点
     * @param rootDir
     * @return
     */
    public static DefaultMutableTreeNode createRootNode(File rootDir) {
        File file = new File(rootDir.getParent(), rootDir.getName() + ".jar");
        return createRootNode(null, file);
    }

    /**
     * 获取entryName
     * @return
     */
    public String entryName() {

        // 根节点, 不需要
        if (null == parent) {
            return "";
        }
        String parentName = parent.entryName();
        if (null != parentName && parentName.length() > 0) {
            parentName += "/";
        }
        return parentName + name;
    }

    @Override
    public String toString() {
        return name;
    }
}
