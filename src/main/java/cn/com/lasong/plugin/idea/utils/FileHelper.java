package cn.com.lasong.plugin.idea.utils;

import cn.com.lasong.plugin.idea.wrap.Compressor;
import cn.com.lasong.plugin.idea.wrap.Decompressor;
import com.intellij.openapi.util.io.FileUtil;
import javassist.CtClass;

import java.io.File;
import java.io.IOException;

public class FileHelper {

    private static final String BASE_DIR = ".androidz";

    private static synchronized File getBaseDir(String basePath) {
        File basePathDir = new File(basePath);

        // 基础文件夹
        File baseDir = new File(basePathDir.getParent(), BASE_DIR);
        return baseDir;
    }

    /**
     * 解压jar包
     * @param path
     * @param basePath
     * @return
     */
    public static File unzipJar(String path, String basePath) {
        if (null == path || basePath == null) {
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        // 输出的文件名
        String name = file.getName();
        System.out.println("unzip " + name);
        int end = name.lastIndexOf(".");
        if (end > 0) {
            name = name.substring(0, end);
        }

        File baseDir = getBaseDir(basePath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        // 临时文件夹解压jar包内容
        String tmpName = "tmp/" + name;
        File tmpDir = new File(baseDir, tmpName);
        if (tmpDir.exists()) {
            tmpDir.delete();
        }
        tmpDir.mkdirs();
        // dump文件夹现实修改的类
        String dumpName = "dump/" + name;
        File dumpDir = new File(baseDir, dumpName);
        if (dumpDir.exists()) {
            dumpDir.delete();
        }
        dumpDir.mkdirs();
        CtClass.debugDump = dumpDir.getAbsolutePath();

        Decompressor.Zip zip = new Decompressor.Zip(file);
        File result = null;
        try {
            zip.extract(tmpDir);
            result = tmpDir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 压缩成jar包
     * @param path
     * @param basePath
     * @return
     */
    public static File zipJar(String path, String basePath) {
        if (null == path || basePath == null) {
            return null;
        }

        File file = new File(path);
        // 输出的文件名
        String name = file.getName();
        // 基础文件夹
        File baseDir = getBaseDir(basePath);
        if (!baseDir.exists()) {
            return null;
        }
        // 临时文件夹解压jar包内容
        String tmpName = "tmp/" + name;
        File tmpDir = new File(baseDir, tmpName);
        return zipJar(path, tmpDir);
    }

    /**
     * 压缩jar包
     * @param path
     * @param jarUnzipDir
     * @return
     */
    public static File zipJar(String path, File jarUnzipDir) {
        if (null == path || jarUnzipDir == null) {
            return null;
        }

        File file = new File(path);
        // 解压缩的文件夹
        if (!jarUnzipDir.exists()) {
            return null;
        }
        try {
            Compressor.Jar jar = new Compressor.Jar(file);
            jar.addDirectory(jarUnzipDir);
            jar.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除缓存文件夹
     */
    public static File cleanTmpDir(String basePath) {
        if (null == basePath) {
            return null;
        }
        File baseDir = getBaseDir(basePath);
        if(FileUtil.delete(baseDir)) {
            return baseDir;
        }
        return null;
    }
}
