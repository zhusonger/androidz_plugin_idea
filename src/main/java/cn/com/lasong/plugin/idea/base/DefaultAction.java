package cn.com.lasong.plugin.idea.base;

import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * 默认action
 */
public abstract class DefaultAction extends AnAction implements IAsyncTaskCallback {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (null == project) {
            return;
        }
        // hook logger
        PluginHelper.hookLogger();

        VirtualFile virtualFile = PluginHelper.getVirtualFile(event);

        performedStart(project, virtualFile);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        String type = accessType();
        if (null == type || type.isEmpty()) {
            return;
        }
        // 只有指定类型显示这个action
        VirtualFile virtualFile = PluginHelper.getVirtualFile(event);
        String extension = null;
        if (null != virtualFile) {
            extension = virtualFile.getExtension();
        }
        Presentation presentation = event.getPresentation();
        presentation.setEnabledAndVisible(type.equalsIgnoreCase(extension));
    }

    protected abstract void performedStart(Project project, VirtualFile file);

    protected String accessType() {
        return null;
    }

    @Override
    public void onResult(Project project, Object... result) {

    }
}
