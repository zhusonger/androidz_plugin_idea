package cn.com.lasong.plugin.idea;

import cn.com.lasong.plugin.idea.utils.PluginHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

/**
 * 分组
 */
public class AndroidZActionsGroup extends DefaultActionGroup {

    public AndroidZActionsGroup() {
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        PluginHelper.setProject(event.getProject());
    }
}
