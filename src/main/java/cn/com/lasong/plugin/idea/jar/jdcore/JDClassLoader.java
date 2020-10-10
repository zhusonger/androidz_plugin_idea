package cn.com.lasong.plugin.idea.jar.jdcore;

import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JDClassLoader implements Loader {
    private Map<String, File> clzMap = new HashMap<>();

    public JDClassLoader() {
    }

    @Override
    public boolean canLoad(String internalName) {
        if (!internalName.endsWith(".class")) {
            internalName += ".class";
        }
        return clzMap.containsKey(internalName);
    }

    @Override
    public byte[] load(String internalName) {
        if (!internalName.endsWith(".class")) {
            internalName += ".class";
        }
        if (!clzMap.containsKey(internalName)) {
            return new byte[0];
        }
        File file = clzMap.get(internalName);
        try {
            return FileUtil.loadFileBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 添加支持的字节码文件
     * @param entryName
     * @param path
     */
    public void appendClz(String entryName, String path) {
        if (null == path) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        clzMap.put(entryName, file);
    }

    /**
     * 清空字节码文件
     */
    public void clear() {
        clzMap.clear();
    }
}
