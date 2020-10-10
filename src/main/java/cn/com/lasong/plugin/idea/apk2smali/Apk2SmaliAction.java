package cn.com.lasong.plugin.idea.apk2smali;

import cn.com.lasong.plugin.idea.base.DefaultAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * APK反编译
 */
public class Apk2SmaliAction extends DefaultAction {


    @Override
    protected void performedStart(Project project, VirtualFile file) {
        // 添加参数
        VirtualFile baseDir = file.getParent();
        VirtualFile outputDir = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Unzip Dir"), project, baseDir);
        // 表示未选择, 不继续了
        if (null == outputDir) {
            return;
        }
        List<String> list = new ArrayList<>();
        // 解包
        list.add("d");
        // -f 输入文件
        list.add("-f");
        list.add(file.getPath());
        // -o 输出文件
        list.add("-o");
        String output = outputDir.getPath()+"/"+file.getNameWithoutExtension();
        list.add(output);
        String[] params = new String[list.size()];
        list.toArray(params);


        // task
        final Apk2SmaliTask task = new Apk2SmaliTask(project, params);

        // indicator
        final BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(task);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, indicator);
    }

    @Override
    protected String accessType() {
        return "apk";
    }
}
