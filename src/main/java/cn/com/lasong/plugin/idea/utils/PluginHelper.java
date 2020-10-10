package cn.com.lasong.plugin.idea.utils;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class PluginHelper {

    private static final NotificationGroup ERROR = NotificationGroup.balloonGroup("AndroidZ Idea (Errors)");
    private static final NotificationGroup INFO = NotificationGroup.logOnlyGroup("AndroidZ Idea (Info)");

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
        INFO.createNotification(message, NotificationType.INFORMATION).notify(null);
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
        ERROR.createNotification(message, NotificationType.ERROR).notify(null);
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
        info(group+": " + s);
    }

    public static void printlnErr(String group, String s) {
        error(group+": " + s);
    }

    /**
     * 获取颜色亮度, 范围0～1
     * @see <a href="https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef">W3C relative luminance definition<a/>
     * @param color
     * @return fff是1 000是0
     */
    public static double getLuminance(@NotNull Color color) {
        return getLinearRGBComponentValue(color.getRed() / 255.0) * 0.2126 +
                getLinearRGBComponentValue(color.getGreen() / 255.0) * 0.7152 +
                getLinearRGBComponentValue(color.getBlue() / 255.0) * 0.0722;
    }

    private static double getLinearRGBComponentValue(double colorValue) {
        if (colorValue <= 0.03928) return colorValue / 12.92;
        return Math.pow(((colorValue + 0.055) / 1.055), 2.4);
    }
}
