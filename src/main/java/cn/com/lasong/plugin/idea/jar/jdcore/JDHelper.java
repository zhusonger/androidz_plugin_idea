package cn.com.lasong.plugin.idea.jar.jdcore;

import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;

/**
 * JDCore工具类
 */
public class JDHelper {
    private static final ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
    private static final JDPrinter printer = new JDPrinter();
    private static JDClassLoader loader = new JDClassLoader();

    /**
     * 添加字节码文件
     * @param node
     */
    public static void appendClass(JarTreeNode node) {
        if (null == node) {
            return;
        }
        loader.appendClz(node.entryName(), node.path);
    }

    /**
     * 反编译class
     * @param entryName
     * @return
     */
    public static String decompile(String entryName) {
        printer.clear();
        try {
            decompiler.decompile(loader, printer, entryName);
            return printer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 释放资源和缓存
     */
    public static void release() {
        loader.clear();
    }
}
