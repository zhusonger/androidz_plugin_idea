package cn.com.lasong.plugin.idea;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 分组
 */
public class AndroidZActionsGroup extends DefaultActionGroup {

    public AndroidZActionsGroup() {
    }

    public AndroidZActionsGroup(@NotNull Supplier<String> shortName, boolean popup) {
        super(shortName, popup);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
    }
}
