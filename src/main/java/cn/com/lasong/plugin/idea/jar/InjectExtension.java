package cn.com.lasong.plugin.idea.jar;
import java.util.Arrays;
import java.util.List;

/**
 * Author: zhusong
 * Email: song.zhu@lasong.com.cn
 * Date: 2020/8/17
 * Description:
 */

public class InjectExtension {

    //定义一个 NamedDomainObjectContainer 属性
    // 定义需要注入的各个节点
    public List<InjectDomain> injectDomains;
    // 是否debug模式
    public boolean injectDebug;


    // 如果想不用=给属性赋值, 需要添加同名的方法
    public void injectDebug(boolean injectDebug) {
        this.injectDebug = injectDebug;
    }

    public boolean getInjectDebug() {
        return injectDebug;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"injectDomains\":")
                .append(Arrays.deepToString(injectDomains.toArray()));
        sb.append(",\"injectDebug\":")
                .append(injectDebug);
        sb.append('}');
        return sb.toString();
    }
}
