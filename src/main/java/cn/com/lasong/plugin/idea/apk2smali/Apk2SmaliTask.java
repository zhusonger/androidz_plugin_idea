package cn.com.lasong.plugin.idea.apk2smali;

import cn.com.lasong.plugin.idea.utils.ILoggerHandler;
import cn.com.lasong.plugin.idea.utils.LoggerPrintStream;
import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Apk2SmaliTask extends Task.Backgroundable implements ILoggerHandler {
    private String[] params = null;

    public Apk2SmaliTask(@Nullable Project project, String[] params) {
        super(project, "Apk2Smali decompiling");
        this.params = params;
    }

    public Apk2SmaliTask(@Nullable Project project, @NotNull String title) {
        super(project, title);
    }

    public Apk2SmaliTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled) {
        super(project, title, canBeCancelled);
    }

    public Apk2SmaliTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, @Nullable PerformInBackgroundOption backgroundOption) {
        super(project, title, canBeCancelled, backgroundOption);
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        if (null == params || params.length <= 0) {
            return;
        }
        LoggerPrintStream.registerHandler(this);
        try {
            brut.apktool.Main.main(params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            notifyFinished();
            LoggerPrintStream.unregisterHandler(this);
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
        PluginHelper.error(error);
    }

    @Override
    public void onFinished() {
        super.onFinished();
    }

    @Override
    public void onMessage(boolean error, String message) {
        // 忽略换行
        if (null == message || message.equals("\n") || message.contains("W:")) {
            return;
        }
        if (error) {
            PluginHelper.error(message);
        } else {
            PluginHelper.info(message);
        }
        //  只输出apktool的内容
        if (!error && message.startsWith("I:")){
            String title = message.replace("I: ", "").replace("...", "").replace("\n", "");
            setTitle(title);
        }
    }
}
