package cn.com.lasong.plugin.idea.utils;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class PluginHelper {

    private static final NotificationGroup ERROR = new NotificationGroup("AndroidZer", NotificationDisplayType.BALLOON, true);
    private static final NotificationGroup INFO = new NotificationGroup("AndroidZer", NotificationDisplayType.NONE, true);

    private static Project PROJECT;

    /**
     * 设置项目
     * @param project
     */
    public static void setProject(Project project) {
        if (null == PluginHelper.PROJECT || PluginHelper.PROJECT == project) {
            return;
        }
        PluginHelper.PROJECT = project;
    }

    /**
     * 获取当前项目的Project
     * @return
     */
    public static Project getProject() {
        return PROJECT;
    }

    /**
     * 获取右键action点击的文件
     *
     * @param event
     * @return
     */
    public static VirtualFile getVirtualFile(AnActionEvent event) {
        VirtualFile virtualFile = null;
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (null != psiFile) {
            virtualFile = psiFile.getVirtualFile();
        }
        return virtualFile;
    }

    /**
     * 静默消息
     *
     * @param message
     */
    public static void info(String message) {
        if (null == message || message.length() <= 0 || message.trim().length() <= 0) {
            return;
        }
        Notification notification = INFO.createNotification(message, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }

    /**
     * 弹出的错误
     *
     * @param message
     */
    public static void error(String message) {
        if (null == message || message.length() <= 0 || message.trim().length() <= 0) {
            return;
        }
        Notification notification = ERROR.createNotification(message, NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }

    public static void error(Throwable throwable) {
        String rtn = FormatStackTrace(throwable);
        error(rtn);
    }

    private volatile static boolean isHook = false;

    public synchronized static void hookLogger() {
        if (isHook) {
            return;
        }
        System.setErr(LoggerPrintStream.err);
        System.setOut(LoggerPrintStream.out);
        isHook = true;
    }

    /**
     * 格式化异常
     * @param throwable
     * @return
     */
    public static String FormatStackTrace(Throwable throwable) {
        if (throwable == null) return "";
        try {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            printWriter.flush();
            writer.flush();
            String rtn = writer.toString();
            printWriter.close();
            writer.close();
            return rtn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void println(String group, String s) {
        info((null != group ? (group+": ") : "") + s);
    }

    public static void printlnErr(String group, String s) {
        error((null != group ? (group+": ") : "") + s);
    }

    public static boolean isEmpty(String text) {
        return null == text || text.length() == 0 || text.trim().length() == 0;
    }
}
