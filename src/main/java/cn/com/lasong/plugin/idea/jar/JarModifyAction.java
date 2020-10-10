package cn.com.lasong.plugin.idea.jar;

import cn.com.lasong.plugin.idea.base.DefaultAction;
import cn.com.lasong.plugin.idea.jar.dialog.JarModifyDialog;
import cn.com.lasong.plugin.idea.jar.jdcore.JDHelper;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * 修改Jar包
 */
public class JarModifyAction extends DefaultAction {

    @Override
    protected void performedStart(Project project, VirtualFile file) {
        // task
        final JarUnzipTask task = new JarUnzipTask(project, file.getPath(), this);

        // indicator
        final BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(task);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, indicator);
    }

    @Override
    protected String accessType() {
        return "jar";
    }

    @Override
    public void onResult(Project project, Object... result) {
        super.onResult(project);
        File jarDir = (File) result[0];
        String jarPath = (String) result[1];
        JarModifyDialog dialog = new JarModifyDialog(project, jarDir, jarPath);
        dialog.show();
    }
}
