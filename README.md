# 游戏服务器热修复的两种方式

## 方式一:Java探针
JDK6开始为Instrument增加很多强大的功能，其中要指出的就是在JDK5中如果想要完成增强处理，必须是在目标JVM程序启动前通过命令行指定Instrument,然后在实际应用中，目标程序可能是已经运行中，针对这种场景下如果要保证 JVM不重启得以完成我们工作，这不是我们想要的，于是JDK6中Instrument提供了在JVM启动之后指定设置java agent达到Instrument的目的。

#### 使用前提：
<li>agent jar中manifest必须包含属性Agent-Class，其值为agent类名。
<li>agent类中必须包含公有静态方法agentmain。
<li>system classload必须支持可以将agent jar添加到system class path。

#### 使用限制：
<li>新类和老类的父类必须相同。
<li>新类和老类实现的接口数也要相同，并且是相同的接口。
<li>新类和老类访问符必须一致。 新类和老类字段数和字段名要一致。
<li>新类和老类新增或删除的方法必须是private static/final修饰的。
<li>可以修改方法体。

总结:此方式局限性大,但是非常方便,对于游戏服务器进行紧急hotfix已经足够.
## 方式二：自定义classloader

### 关键步骤
<li>核心游戏jar包代码使用appclassloader加载
<li>游戏功能逻辑部分使用自定义classloader加载
<li>脚本类必须实现对应方法签名接口,因为在appclassloader中是找不到脚本实例的,不能进行强转,只有使用接口调用
<li>脚本类中在重载后其缓存数据都会被重置,所以脚本不能缓存数据。主要实现逻辑,变量值一般都是通过方法传递进去。

这种方式需要对项目做出整体的架构规划整理,比较麻烦和复杂,并且需要清楚的知道类加载的相关知识.
