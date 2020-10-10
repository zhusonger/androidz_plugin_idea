package cn.com.lasong.plugin.idea.jar;

import cn.com.lasong.plugin.idea.utils.FileHelper;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JarZipTask extends Task.Backgroundable {

    private String jarPath;
    public JarZipTask(@Nullable Project project, String jarPath) {
        super(project, "Jar zip...");
        this.jarPath = jarPath;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        Project project = getProject();
        if (null == project || jarPath == null) {
            return;
        }

        String basePath = project.getBasePath();
        FileHelper.zipJar(jarPath, basePath);

        notifyFinished();
    }

    @Override
    public void onFinished() {
        super.onFinished();

    }
}
