# androidz_plugin_idea
AndroidZ开源库 IDEA插件开发

# AndroidZer

目前有很多优秀的反编译工具, 如JD-GUI、Apktool、Javassist等,

但是在使用的时候互相独立, 使用时需要退出IDEA, 去一个个使用, 觉得比较繁琐,

为了在项目中逆向, 着手来实现一个IDEA插件来集合这些功能。

## 1. JarModify 

### 依赖库

```
// javassist 相关修改字节码
implementation 'org.javassist:javassist:3.27.0-GA'
// 字节码转文本
implementation 'org.jd:jd-core:1.1.3'
// 高亮显示的textarea
implementation 'com.fifesoft:rsyntaxtextarea:3.1.1'
```

### 功能描述

查看jar包的代码, 并支持修改字节码满足需求。

这个修改是基于[Android Studio 三方库修改](https://github.com/zhusonger/androidz_plugin)抽取出来的。

具体支持哪些修改功能, 可以查看这个项目里的README

### 使用

右键jar文件, 会出现AndroidZer选项, 其中的JarModify就是当前功能。

![jar1](https://github.com/zhusonger/androidz_plugin_idea/blob/master/images/jar1.png)

在编辑页右键可以显示菜单, __修改__ 可以修改字节码。

![jar2](https://github.com/zhusonger/androidz_plugin_idea/blob/master/images/jar2.png)





