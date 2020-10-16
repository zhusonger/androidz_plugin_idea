package cn.com.lasong.plugin.idea.jar.inject;

import java.util.Arrays;
import java.util.List;

/**
 * Author: zhusong
 * Email: song.zhu@lasong.com.cn
 * Date: 2020/8/14
 * Description: 需要注入的方法
 */
public class InjectClzModify {

    // 需要注入的类名
    // 普通类 cn.com.lasong.base.AppManager
    // 内部类 cn.com.lasong.base.AppManager$Inner
    // 匿名类 cn.com.lasong.base.AppManager$1
    public String className;

    // 修饰符
    public String modifiers;

    // 导入包
    public List<String> importPackages;

    // 方法修改
    public List<InjectCtModify> modifyMethods;
    // 是否注入
    public boolean isInject = true;

    public String getEntryName() {
        if (null != className) {
            return className.replace(".", "/") +".class";
        }
        return null;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public List<String> getImportPackages() {
        return importPackages;
    }

    public void setImportPackages(List<String> importPackages) {
        this.importPackages = importPackages;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isInject() {
        return isInject;
    }

    public void setInject(boolean inject) {
        isInject = inject;
    }

    public List<InjectCtModify> getModifyMethods() {
        return modifyMethods;
    }

    public void setModifyMethods(InjectCtModify[] modifyMethods) {
        this.modifyMethods = Arrays.asList(modifyMethods);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"className\":\"")
                .append(className).append('\"');
        sb.append(",\"importPackages\":")
                .append(importPackages);
        sb.append(",\"modifyMethods\":")
                .append(modifyMethods);
        sb.append(",\"isInject\":")
                .append(isInject);
        sb.append('}');
        return sb.toString();
    }
}
