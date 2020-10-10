package cn.com.lasong.plugin.idea.base;

import com.intellij.openapi.project.Project;

public interface IAsyncTaskCallback {
    void onResult(Project project, Object... result);
}
