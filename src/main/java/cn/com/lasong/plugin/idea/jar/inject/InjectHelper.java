package cn.com.lasong.plugin.idea.jar.inject;


import cn.com.lasong.plugin.idea.jar.dialog.JarTreeNode;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import javassist.*;
import javassist.bytecode.*;
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
     * int转换成string
     * @param modifiers
     * @return
     */
    public static String ofModifiers(int modifiers) {
        List<String> list = new ArrayList<>();
        if (AccessFlag.isPublic(modifiers)) {
            list.add("public");
        } else if (AccessFlag.isProtected(modifiers)) {
            list.add("protected");
        } else if (AccessFlag.isPrivate(modifiers)) {
            list.add("private");
        }

        if((modifiers & AccessFlag.STATIC) != 0) {
            list.add("static");
        }

        if((modifiers & AccessFlag.ABSTRACT) != 0) {
            list.add("abstract");
        }

        if((modifiers & AccessFlag.FINAL) != 0) {
            list.add("final");
        }

        if((modifiers & AccessFlag.VOLATILE) != 0) {
            list.add("volatile");
        }

        if((modifiers & AccessFlag.SYNCHRONIZED) != 0) {
            list.add("synchronized");
        }

        if((modifiers & AccessFlag.NATIVE) != 0) {
            list.add("native");
        }

        if((modifiers & AccessFlag.ENUM) != 0) {
            list.add("enum");
        }

        StringBuilder builder = new StringBuilder();
        for (String item : list) {
            builder.append(item).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    /**
     * string解析成int
     * @param modifiersValue
     * @return
     */
    public static int ofModifiers(String modifiersValue) {
        int modifiers = 0;
        if (modifiersValue.contains("public")) {
            modifiers = AccessFlag.PUBLIC;
        } else if (modifiersValue.contains("private")) {
            modifiers = AccessFlag.PRIVATE;
        } else if (modifiersValue.contains("protected")) {
            modifiers = AccessFlag.PROTECTED;
        }

        if (modifiersValue.contains("final")) {
            modifiers |= AccessFlag.FINAL;
        }
        if (modifiersValue.contains("static")) {
            modifiers |= AccessFlag.STATIC;
        }
        if (modifiersValue.contains("synchronized")) {
            modifiers |= AccessFlag.SYNCHRONIZED;
        }
        if (modifiersValue.contains("volatile")) {
            modifiers |= AccessFlag.VOLATILE;
        }
        if (modifiersValue.contains("abstract")) {
            modifiers |= AccessFlag.ABSTRACT;
        }
        if (modifiersValue.contains("interface")) {
            modifiers |= AccessFlag.INTERFACE;
        }
        if (modifiersValue.contains("native")) {
            modifiers |= AccessFlag.NATIVE;
        }
        return modifiers;
    }

    /**
     * 获取类的修饰符
     * @param node
     * @return
     */
    public static String getClassModifiers(JarTreeNode node) {
        String className = node.className();
        CtClass ctClass = null;
        StringBuilder builder = new StringBuilder();
        try {
            ctClass = pool.get(className);
            String modifiers = ofModifiers(ctClass.getModifiers());
            builder.append(modifiers);
        }catch (Exception e){
            PluginHelper.error(e);
        } finally {
            if (null != ctClass) {
                ctClass.detach();
            }
        }

        return builder.toString();
    }

    /**
     * 获取所有方法
     * @param node
     * @return
     */
    public static List<JMethodModel> getAllCts(JarTreeNode node) {
        String className = node.className();
        CtClass ctClass = null;
        List<JMethodModel> methods = new ArrayList<>();
        try {
            ctClass = pool.get(className);
            List<CtBehavior> list = new ArrayList<>();
            CtConstructor[] ctConstructors = ctClass.getDeclaredConstructors();
            if (null != ctConstructors && ctConstructors.length > 0) {
                list.addAll(Arrays.asList(ctConstructors));
            }
            CtMethod[] ctMethods = ctClass.getDeclaredMethods();
            if (null != ctMethods && ctMethods.length > 0) {
                list.addAll(Arrays.asList(ctMethods));
            }

            for (CtBehavior method : list) {
                JMethodModel m = new JMethodModel();
                m.name = method.getName();
                String params = method.getSignature();
                MethodInfo methodInfo = method.getMethodInfo();
                if (null != methodInfo) {
                    params = Descriptor.getParamDescriptor(methodInfo.getDescriptor());
                } else if (null != params){
                    params = params.substring(0, params.indexOf(')') + 1);
                }
                m.isConstructor = method instanceof CtConstructor;
                m.params = params;
                methods.add(m);
            }
        }catch (Exception e){
            PluginHelper.error(e);
        } finally {
            if (null != ctClass) {
                ctClass.detach();
            }
        }

        return methods;
    }

    public static byte[] injectClass(@NotNull InjectClzModify clzModify) {
        return injectClass(true, clzModify);
    }

    /**
     * 注入类
     */
    public static byte[] injectClass(boolean injectDebug, @NotNull InjectClzModify clzModify) {
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
                int modifiers = 0;
                if (clzModify.modifiers.contains("public")) {
                    modifiers = AccessFlag.PUBLIC;
                } else if (clzModify.modifiers.contains("private")) {
                    modifiers = AccessFlag.PRIVATE;
                } else if (clzModify.modifiers.contains("protected")){
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
            List<InjectCtModify> modifyMethods = clzModify.modifyMethods;
            if (null != modifyMethods && !modifyMethods.isEmpty()) {
                for (InjectCtModify method : modifyMethods) {
                    String action = method.action;
                    // 修改方法
                    if (InjectCtModify.ACTION_MODIFY.equalsIgnoreCase(action)) {
                        if (null != method.name || method.isConstructor) {
                            modifyCt(injectDebug, ctClass, method);
                        } else if (null != method.fieldName) {
                            modifyField(injectDebug, ctClass, method);
                        }
                    }
                    // 新增属性
                    else if (InjectCtModify.ACTION_ADD_FIELD.equalsIgnoreCase(action)) {
                        addField(injectDebug, ctClass, method);
                    }
                    // 新增方法
                    else if (InjectCtModify.ACTION_ADD_METHOD.equalsIgnoreCase(action)) {
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
     * 修改内容
     */
    private static void modifyCt(boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        modifyCt(null, injectDebug, ctClass, method);
    }
    private static void modifyCt(String group, boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        CtClass[] params = parseCtClass(method.params);

        CtBehavior ctBehavior;
        String name = method.name;
        if (null == name) {
            name = ctClass.getSimpleName();
        }
        if (method.isConstructor) {
            ctBehavior = ctClass.getDeclaredConstructor(params);
            if (injectDebug) {
                PluginHelper.println(group, "modifyCt getDeclaredConstructor [" + name + "] " + ctBehavior);
            }
        } else {
            if (PluginHelper.isEmpty(name)) {
                throw new IOException("modifyCt [" + name + "] name is null !");
            }
            if (null != params) {
                ctBehavior = ctClass.getDeclaredMethod(name, params);
            } else {
                ctBehavior = ctClass.getDeclaredMethod(name);
            }
            if (injectDebug) {
                PluginHelper.println(group, "modifyCt getDeclaredMethod [" + name + "] " + ctBehavior);
            }
        }

        if (null == ctBehavior) {
            throw new IOException("modifyCt [" + name + "] is not found!");
        }

        // 修改方法名
        if (method.newName != null && method.newName.trim().length() > 0) {
            if (ctBehavior instanceof CtMethod) {
                ((CtMethod)ctBehavior).setName(method.newName);
            }
            if (injectDebug) {
                PluginHelper.println(group, "modifyCt from [" + name + "] to [" + method.newName+"]");
            }
        }
        // 修改方法修饰符
        if (null != method.modifiers && method.modifiers.trim().length() > 0) {
            int modifiers = ofModifiers(method.modifiers);
            ctBehavior.setModifiers(modifiers);

            if (injectDebug) {
                PluginHelper.println(group, "modifyCt modifiers [" + method.modifiers + "]");
            }
        }

        String type = method.type;
        if (PluginHelper.isEmpty(type)) {
            return;
        }
        String content = method.content;
        if (!type.equalsIgnoreCase("deleteAt") && PluginHelper.isEmpty(content)) {
            throw new IOException("modifyCt [" + name + "] type ["+type+"] content can't be empty!");
        }

        if (type.equalsIgnoreCase("insertAt") && method.lineNum < 0) {
            throw new IOException("modifyCt [" + name + "] lineNum[for insertAt] can't empty !");
        }
        if (type.equalsIgnoreCase("deleteAt") && (method.lineRange == null || method.lineRange.length() == 0)) {
            throw new IOException("modifyCt [" + name + "] lineRange[for deleteAt] can't empty!");
        }

        if (type.equalsIgnoreCase("insertBefore")) {
            ctBehavior.insertBefore(content);
        } else if (type.equalsIgnoreCase("insertAfter")) {
            ctBehavior.insertAfter(content);
        } else if (type.equalsIgnoreCase("insertAt")) {
            int lineStart = ctBehavior.getMethodInfo().getLineNumber(0);
            ctBehavior.insertAt(lineStart + method.lineNum, content);
        } else if (type.equalsIgnoreCase("setBody")) {
            ctBehavior.setBody(content);
        } else if (type.equalsIgnoreCase("deleteAt")) {
            int lineStart = ctBehavior.getMethodInfo().getLineNumber(0);
            // Access the code attribute
            CodeAttribute codeAttribute = ctBehavior.getMethodInfo().getCodeAttribute();

            // Access the LineNumberAttribute
            LineNumberAttribute lineNumberAttribute = (LineNumberAttribute) codeAttribute.getAttribute(LineNumberAttribute.tag);

            if (null == lineNumberAttribute) {
                if (injectDebug)
                    PluginHelper.println(group, "lineNumberAttribute is null");
                return;
            }


            String lineRange = method.lineRange;
            String[] rangeArr = lineRange.split(",");
            for (String item : rangeArr) {
                if (null == item) {
                    continue;
                }
                String[] range = item.split("#");
                if (range.length == 0) {
                    throw new IOException("modifyCt [" + name + "] lineRange [for deleteAt] can't empty!");
                }
                int start = Integer.parseInt(range[0].trim());
                int len = 1;
                if (range.length > 1) {
                    len = Integer.parseInt(range[1].trim());
                }

                if (injectDebug)
                    PluginHelper.println(group, "deleteAt range " + item);

                // Index in bytecode array where the instruction starts
                LineNumberAttribute.Pc startPc = lineNumberAttribute.toNearPc(lineStart + start);

                if (null == startPc) {
                    if (injectDebug)
                        PluginHelper.println(group, "deleteAt start null");
                    continue;
                }

                if (injectDebug)
                    PluginHelper.println(group, "deleteAt start : index = " + startPc.index+", line = " + startPc.line);

                // Index in the bytecode array where the following instruction starts
                LineNumberAttribute.Pc endPc = lineNumberAttribute.toNearPc(lineStart + start + len);
                if (null == endPc) {
                    if (injectDebug)
                        PluginHelper.println(group, "deleteAt end null");
                    continue;
                }

                if (injectDebug)
                    PluginHelper.println(group, "deleteAt end : index = " + endPc.index+", line = " + endPc.line);

                // Let's now get the bytecode array
                byte[] code = codeAttribute.getCode();
                for (int i = startPc.index; i < endPc.index; i++) {
                    // change byte to a no operation code
                    code[i] = CodeAttribute.NOP;
                }
            }
        }

        if (injectDebug) {
            PluginHelper.println(group, "modifyCt [" + name + "] " + type);
            if (null != content && content.length() > 0) {
                PluginHelper.println(group, content);
            }
        }
    }

    /**
     * 添加属性
     */
    private static void addField(boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        CtField ctField = CtField.make(method.content, ctClass);
        ctClass.addField(ctField);
        if (injectDebug)
            PluginHelper.info("addField [" + method.content + "]");
    }

    /**
     * 添加方法
     */
    private static void addMethod(boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        CtMethod ctMethod = CtMethod.make(method.content, ctClass);
        ctClass.addMethod(ctMethod);
        if (injectDebug)
            PluginHelper.info("addMethod [" + method.content + "]");
    }

    /**
     * 修改属性
     * @param injectDebug
     * @param ctClass
     * @param method
     * @throws Exception
     */
    private static void modifyField(boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        modifyField(null, injectDebug, ctClass, method);
    }
    private static void modifyField(String group, boolean injectDebug, CtClass ctClass, InjectCtModify method) throws Exception {
        String fieldName = method.fieldName;
        CtField ctField;
        try {
            ctField = ctClass.getDeclaredField(fieldName);
        } catch (NotFoundException e) {
            throw new IOException("modifyField [" + fieldName + "] fieldName is not found !");
        }

        String newFieldName = method.newFieldName;
        if (!PluginHelper.isEmpty(newFieldName)) {
            ctField.setName(newFieldName);
            if (injectDebug) {
                PluginHelper.println(group, "modifyField newFieldName [" + newFieldName+"] Successfully!");
            }
        }


        if (!PluginHelper.isEmpty(method.fieldModifiers)) {
            int modifiers = ofModifiers(method.fieldModifiers);
            ctField.setModifiers(modifiers);

            if (injectDebug) {
                PluginHelper.println(group, "modifyField fieldModifiers [" + method.fieldModifiers+"] Successfully!");
            }
        }
    }
}
