# YetAnotherSpringBootExmaple

一些使用 Spring Boot 的例子。



强烈推荐一个插件 [lombok](https://github.com/mplushnikov/lombok-intellij-plugin)，能以注解方式添加 Getter、Setter、ToString 等常用功能，有效减少冗余代码。

本项目的代码基本都使用了该插件。

在 IDEA 中使用该插件的方法如下：
    1. 在 Idea 中安装插件
        - MacOs:
            Preferences > Settings > Plugins > Browse repositories... > Search for "lombok" > Install Plugin
        - Windows:
            File > Settings > Plugins > Browse repositories... > Search for "lombok" > Install Plugin
        - Manually:
            Download the latest release and install it manually using Preferences > Plugins > Install plugin from disk...

    1. 在 Idea 中开启`注解处理`
        - Mac
            >  Click `Preferences` -> `Build, Execution, Deployment` -> `Compiler` -> `Annotation Processors`. Click `Enable Annotation Processing`
        - Windows
            >  Click `Preferences` -> `Build, Execution, Deployment` -> `Compiler` -> `Annotation Processors`. Click `Enable Annotation Processing`

