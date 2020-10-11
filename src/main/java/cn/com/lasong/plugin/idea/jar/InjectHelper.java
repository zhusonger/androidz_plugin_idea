package cn.com.lasong.plugin.idea.jar;


import cn.com.lasong.plugin.idea.utils.PluginHelper;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LineNumberAttribute;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InjectHelper {

    //初始化类池
    protected static ClassPool pool;
    // 加入类路径的记录, 方便修改
    protected final static Map<String, ClassPath> clzPaths = new HashMap<>();
    // 记录加入的包名
    protected final static Set<String> pkgNames = new HashSet<>();

    /**
     * 添加javassist类搜索路径
     *
     * @param path
     * @return
     */
    public synchronized static void appendClassPath(String tag, String path) throws RuntimeException {
        if (null == pool) {
            pool = new ClassPool(true);
        }
        removeClassPath(tag);
        try {
            ClassPath classPath = pool.appendClassPath(path);
            clzPaths.put(tag, classPath);
            String lower = path.toLowerCase();
            // jar包
            if (lower.endsWith(".jar") || lower.endsWith(".zip")) {
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry srcEntry = entries.nextElement();
                    String entryName = srcEntry.getName();
                    importPackageNameByEntry(entryName);
                }
                jarFile.close();
            } else {
                File clzDir = new File(path);
                if (clzDir.isDirectory()) {

                }
                String clzDirPath = clzDir.getAbsolutePath() + File.separator;
                List<File> clzFile = listClasses(clzDir);
                if (!clzFile.isEmpty()) {
                    File file = clzFile.get(0);
                    String entryName = file.getAbsolutePath().replace(clzDirPath, "");
                    importPackageNameByEntry(entryName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除上一次的classpath
     */
    public synchronized static void release() {
        clearJarFactoryCache();
        // 结束后移除这个classpool, 用同一个会有问题
        pool = null;
        clzPaths.clear();
        pkgNames.clear();
    }


    /**
     * 导入包名
     */
    private static void importPackageNameByEntry(String entryName) {
        if (entryName == null || !entryName.endsWith(".class")) {
            return;
        }
        int end = entryName.lastIndexOf("/");
        if (end <= 0) {
            return;
        }
        String packageName = entryName.substring(0, end).replace("/", ".");
        importPackageName(packageName);
    }
    private static void importPackageName(String packageName) {
        if (!pkgNames.contains(packageName)) {
            pool.importPackage(packageName);
            pkgNames.add(packageName);
        }
    }

    /**
     * 移除tag对应的classpath
     *
     * @param tag
     */
    public static void removeClassPath(String tag) {
        ClassPath cachePath = clzPaths.get(tag);
        if (null != cachePath) {
            pool.removeClassPath(cachePath);
        }
    }

    /**
     * 注入类
     */
    public static byte[] injectClass(@NotNull InjectClzModify clzModify) {
        boolean injectDebug = true;
        byte[] buffer = null;
        String className = clzModify.className;
        CtClass ctClass = null;

        try {
            ctClass = pool.get(className);
            // 解冻
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            // 修改方法修饰符
            if (null != clzModify.modifiers && clzModify.modifiers.trim().length() > 0) {
                int modifiers;
                if (clzModify.modifiers.contains("public")) {
                    modifiers = AccessFlag.PUBLIC;
                } else if (clzModify.modifiers.contains("private")) {
                    modifiers = AccessFlag.PRIVATE;
                } else {
                    modifiers = AccessFlag.PROTECTED;
                }
                if (clzModify.modifiers.contains("final")) {
                    modifiers |= AccessFlag.FINAL;
                }
                if (clzModify.modifiers.contains("static")) {
                    modifiers |= AccessFlag.STATIC;
                }
                if (clzModify.modifiers.contains("synchronized")) {
                    modifiers |= AccessFlag.SYNCHRONIZED;
                }
                ctClass.setModifiers(modifiers);

                if (injectDebug)
                    PluginHelper.info("modifyClass modifiers [" + clzModify.modifiers + "]");
            }

            // 导入关联类
            List<String> importPackages = clzModify.importPackages;
            if (null != importPackages && !importPackages.isEmpty()) {
                for (String packageName : importPackages) {
                    importPackageName(packageName);
                    if (injectDebug)
                        PluginHelper.info("importPackage [" + packageName + "]");
                }
            }

            // 修改方法
            List<InjectModifyMethod> modifyMethods = clzModify.modifyMethods;
            if (null != modifyMethods && !modifyMethods.isEmpty()) {
                for (InjectModifyMethod method : modifyMethods) {
                    String action = method.action;
                    // 修改方法
                    if (InjectModifyMethod.ACTION_MODIFY.equalsIgnoreCase(action)) {
                        modifyMethod(injectDebug, ctClass, method);
                    }
                    // 新增属性
                    else if (InjectModifyMethod.ACTION_ADD_FIELD.equalsIgnoreCase(action)) {
                        addField(injectDebug, ctClass, method);
                    }
                    // 新增方法
                    else if (InjectModifyMethod.ACTION_ADD_METHOD.equalsIgnoreCase(action)) {
                        addMethod(injectDebug, ctClass, method);
                    }
                }
            }
            try {
                boolean p = ctClass.stopPruning(true);
                buffer = ctClass.toBytecode();
                ctClass.defrost();
                ctClass.stopPruning(p);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (injectDebug)
                PluginHelper.info("modify [" + className + "] Done!");
        } catch (Exception e){
            PluginHelper.error(e);
        } finally {
            if (null != ctClass) {
                ctClass.detach();
            }

            // 移除导入的包
            List<String> importPackages = clzModify.importPackages;
            if (null != importPackages && !importPackages.isEmpty()) {
                Set<String> pkgSet = new HashSet<>(importPackages);
                Iterator<String> iterator = pool.getImportedPackages();
                while (iterator.hasNext()) {
                    String pkg = iterator.next();
                    if (pkgSet.contains(pkg)) {
                        iterator.remove();
                        if (injectDebug)
                            PluginHelper.info("removePackage [" + pkg + "]");
                    }
                }
            }
        }

        return buffer;
    }

    /**
     * 解析方法签名到CtClass参数
     *
     * @param params
     * @return
     */
    private static CtClass[] parseCtClass(String params) {
        CtClass[] ret = null;
        if (null != params && params.length() > 0) {
            try {
                ret = Descriptor.getParameterTypes(params, pool);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 获取所有class文件
     *
     * @param dir
     * @return
     */
    private static List<File> listClasses(File dir) {
        File[] files = dir.listFiles();
        List<File> clzList = new ArrayList<>();
        if (null == files || files.length == 0) {
            return clzList;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                clzList.addAll(listClasses(file));
            } else {
                clzList.add(file);
            }
        }
        return clzList;
    }

    /**
     * 清理缓存, 否则容易出异常
     * Caused by: javassist.NotFoundException: broken jar file?
     */
    private static void clearJarFactoryCache() {
        try {
            Class<?> clazz = Class.forName("sun.net.www.protocol.jar.JarFileFactory");
            Field fileCacheField = clazz.getDeclaredField("fileCache");
            Field urlCacheField = clazz.getDeclaredField("urlCache");
            fileCacheField.setAccessible(true);
            urlCacheField.setAccessible(true);
            Map<?, ?> fileCache = (Map<?, ?>) fileCacheField.get(null);
            Map<?, ?> urlCache = (Map<?, ?>) urlCacheField.get(null);
            fileCache.clear();
            urlCache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改方法
     */
    private static void modifyMethod(boolean injectDebug, CtClass ctClass, InjectModifyMethod method) throws Exception {
        String name = method.name;
        if (null == name || name.length() == 0) {
            return;
        }
        CtClass[] params = parseCtClass(method.params);

        CtMethod ctMethod;
        if (null != params) {
            ctMethod = ctClass.getDeclaredMethod(name, params);
        } else {
            ctMethod = ctClass.getDeclaredMethod(name);
        }

        if (null == ctMethod) {
            throw new IOException("modifyMethod [" + name + "] name is null !");
        }

        // 修改方法名
        if (method.newName != null && method.newName.trim().length() > 0) {
            ctMethod.setName(method.newName);
            if (injectDebug) {
                PluginHelper.info("modifyMethod from [" + name + "] to [" + method.newName+"]");
            }
        }
        // 修改方法修饰符
        if (null != method.modifiers && method.modifiers.trim().length() > 0) {
            int modifiers;
            if (method.modifiers.contains("public")) {
                modifiers = AccessFlag.PUBLIC;
            } else if (method.modifiers.contains("private")) {
                modifiers = AccessFlag.PRIVATE;
            } else {
                modifiers = AccessFlag.PROTECTED;
            }
            if (method.modifiers.contains("final")) {
                modifiers |= AccessFlag.FINAL;
            }
            if (method.modifiers.contains("static")) {
                modifiers |= AccessFlag.STATIC;
            }
            if (method.modifiers.contains("synchronized")) {
                modifiers |= AccessFlag.SYNCHRONIZED;
            }
            ctMethod.setModifiers(modifiers);

            if (injectDebug) {
                PluginHelper.info("modifyMethod modifiers [" + method.modifiers + "]");
            }
        }

        String type = method.type;
        if (null == type || type.length() == 0) {
            return;
        }
        String content = method.content;
        if (!type.equalsIgnoreCase("deleteAt") && (null == content || content.length() == 0)) {
            return;
        }

        if (type.equalsIgnoreCase("insertAt") && method.lineNum < 0) {
            throw new IOException("modifyMethod [" + name + "] lineNum[for insertAt] can't empty !");
        }
        if (type.equalsIgnoreCase("deleteAt") && (method.lineRange == null || method.lineRange.length() == 0)) {
            throw new IOException("modifyMethod [" + name + "] lineRange[for deleteAt] can't empty!");
        }

        if (type.equalsIgnoreCase("insertBefore")) {
            ctMethod.insertBefore(content);
        } else if (type.equalsIgnoreCase("insertAfter")) {
            ctMethod.insertAfter(content);
        } else if (type.equalsIgnoreCase("insertAt")) {
            int lineStart = ctMethod.getMethodInfo().getLineNumber(0);
            ctMethod.insertAt(lineStart + method.lineNum, content);
        } else if (type.equalsIgnoreCase("setBody")) {
            ctMethod.setBody(content);
        } else if (type.equalsIgnoreCase("deleteAt")) {
            int lineStart = ctMethod.getMethodInfo().getLineNumber(0);
            // Access the code attribute
            CodeAttribute codeAttribute = ctMethod.getMethodInfo().getCodeAttribute();

            // Access the LineNumberAttribute
            LineNumberAttribute lineNumberAttribute = (LineNumberAttribute) codeAttribute.getAttribute(LineNumberAttribute.tag);

            if (null == lineNumberAttribute) {
                if (injectDebug)
                    PluginHelper.info("lineNumberAttribute is null");
                return;
            }


            String lineRange = method.lineRange;
            String[] rangeArr = lineRange.split(",");
            for (String item : rangeArr) {
                if (null == item) {
                    continue;
                }
                String[] range = item.split("#");
                int start = Integer.parseInt(range[0]);
                int len = 1;
                if (range.length > 1) {
                    len = Integer.parseInt(range[1]);
                }

                // Index in bytecode array where the instruction starts
                int startPc = lineNumberAttribute.toStartPc(lineStart + start);

                // Index in the bytecode array where the following instruction starts
                int endPc = lineNumberAttribute.toStartPc(lineStart + start + len);

                // Let's now get the bytecode array
                byte[] code = codeAttribute.getCode();
                for (int i = startPc; i < endPc; i++) {
                    // change byte to a no operation code
                    code[i] = CodeAttribute.NOP;
                }
            }
        }

        if (injectDebug) {
            PluginHelper.info("modifyMethod [" + name + "] " + type);
            if (null != content && content.length() > 0) {
                PluginHelper.info(content);
            }
        }
    }

    /**
     * 添加属性
     */
    private static void addField(boolean injectDebug, CtClass ctClass, InjectModifyMethod method) throws Exception {
        CtField ctField = CtField.make(method.content, ctClass);
        ctClass.addField(ctField);
        if (injectDebug)
            PluginHelper.info("addField [" + method.content + "]");
    }

    /**
     * 添加方法
     */
    private static void addMethod(boolean injectDebug, CtClass ctClass, InjectModifyMethod method) throws Exception {
        CtMethod ctMethod = CtMethod.make(method.content, ctClass);
        ctClass.addMethod(ctMethod);
        if (injectDebug)
            PluginHelper.info("addMethod [" + method.content + "]");
    }
}
