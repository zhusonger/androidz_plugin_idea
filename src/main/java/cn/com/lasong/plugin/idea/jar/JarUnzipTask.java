package cn.com.lasong.plugin.idea.jar;

import cn.com.lasong.plugin.idea.base.IAsyncTaskCallback;
import cn.com.lasong.plugin.idea.utils.FileHelper;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class JarUnzipTask extends Task.Backgroundable {

    private final String jarPath;
    private final IAsyncTaskCallback callback;
    private File unzipDir;

    public JarUnzipTask(@Nullable Project project, String jarPath, IAsyncTaskCallback callback) {
        super(project, "Jar unzip...");
        this.jarPath = jarPath;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        Project project = getProject();
        if (null == project || jarPath == null) {
            return;
        }

        String basePath = project.getBasePath();
        unzipDir = FileHelper.unzipJar(jarPath, basePath);

        notifyFinished();
    }

    @Override
    public void onFinished() {
        super.onFinished();
        if (null != unzipDir) {
            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(unzipDir);
        }
        if (null != callback) {
            callback.onResult(getProject(), unzipDir, jarPath);
        }
    }
}
