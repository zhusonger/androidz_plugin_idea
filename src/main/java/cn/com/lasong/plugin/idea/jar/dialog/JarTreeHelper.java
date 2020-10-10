package cn.com.lasong.plugin.idea.jar.dialog;

import cn.com.lasong.plugin.idea.utils.TreeComparator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.util.Arrays;

public class JarTreeHelper {

    /**
     * 更新树节点
     * @param tree
     * @param dir
     */
    public static void updateTree(JTree tree, File dir) {
        if (null == tree || null == dir) {
            return;
        }

        DefaultMutableTreeNode root = JarTreeNode.createRootNode(dir);
        TreeModel treeModel = new DefaultTreeModel(root);
        File[] files = dir.listFiles();
        if (null != files) {
            Arrays.sort(files, new TreeComparator());
            for (File childFile : files) {
                addTreeNodes(root, childFile);
            }
        }
        tree.setModel(treeModel);
    }

    /**
     * 添加节点
     * @param parent
     * @param file
     */
    private static void addTreeNodes(DefaultMutableTreeNode parent, File file) {
        if (null == parent || file == null || !file.exists()) {
            return;
        }

        // 目录节点
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                Arrays.sort(files, new TreeComparator());
                DefaultMutableTreeNode dirNode = JarTreeNode.createDirNode(parent, file);
                parent.add(dirNode);
                for (File childFile : files) {
                    addTreeNodes(dirNode, childFile);
                }
            }
            return;
        }

        // 文件节点
        DefaultMutableTreeNode clzNode = JarTreeNode.createClassNode(parent, file);
        parent.add(clzNode);
    }
}
