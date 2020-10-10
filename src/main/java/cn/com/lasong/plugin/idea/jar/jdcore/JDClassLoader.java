package cn.com.lasong.plugin.idea.jar.jdcore;

import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JDClassLoader implements Loader {
    private JarFile jarFile;

    public JDClassLoader(String path) {
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JDClassLoader(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public boolean canLoad(String internalName) {
        if (null != jarFile) {
            JarEntry entry = jarFile.getJarEntry(internalName);
            return entry != null;
        }
        return false;
    }

    @Override
    public byte[] load(String internalName) throws LoaderException {
        if (null != jarFile) {
            ZipEntry entry = jarFile.getEntry(internalName);
            try {
                return IOUtils.toByteArray(jarFile.getInputStream(entry));
            } catch (IOException e) {
                throw new LoaderException(e);
            }
        }
        return null;
    }
}
