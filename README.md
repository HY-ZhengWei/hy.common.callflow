# 编排引擎 CallFlow



目录
------
* [主导思想](#主导思想)
* [概要说明](#概要说明)




主导思想
------

    1. 将基础的 Java 方法灵活组合，构建特定业务逻辑。

    2. 柔性配置：组合过程无需开发，可配置、即改即用，服务无须重启。
    
    3. 可移植性：编排配置支持导出导入，便于保存、复制和升级。
    
    4. 自动化执行：只需输入数据，引擎自动执行流程并返回结果，实现“输入->执行->输出”的闭环。
    
    5. 嵌入式：编排引擎将直接嵌入业务服务中运行。
    


概要说明
------

    1. 6种编排元素。
    
        1.1. 执行元素，配置及执行Java方法。可轻松扩展它，衍生成特定的业务元素。
    
        1.2. 条件逻辑元素，与或逻辑判定。
    
        1.3. 计算元素，计算数据、加工数据、创建新数据。
        
        1.4. 等待元素，等待一段时间。
    
        1.5. 循环元素，按数组、集合或数列，循环执行一个或多个其它元素。
    
        1.6. 嵌套元素，嵌套其它编排，复用与共享编排，构建更复杂的业务。
        
    2. 4种编排路由。
        
        2.1 成功路由，元素执行成功时走的路。
        
        2.2 异常路由，元素执行出时异常时走的路。
        
        2.3 真值路由，条件逻辑元素与计算元素（逻辑类型的），结果为真时走的路。
        
        2.4 假值路由，条件逻辑元素与计算元素（逻辑类型的），结果为假时走的路。
        
    3. 2种编排循环。
        
        3.1 While循环路由，下层元素回流到上层元素时走的路。
        
        3.2 For循环路由，与循环元素配合（至少有一个），回流到循环元素的路。
        
        3.3 路由与循环可以组合后共存，即：成功路由+For循环路由组成一条成功时For循环的路由。



简单编排：两个元素举例
------

[查看代码](src/test/java/org/hy/common/callflow/junit/cflow001)
  
![image](src/test/java/org/hy/common/callflow/junit/cflow001/JU_CFlow001.png)

__编排XML配置__

```xml
<?xml version="1.0" encoding="UTF-8"?>

<config>

    <import name="xconfig"    class="java.util.ArrayList" />
    <import name="xnesting"   class="org.hy.common.callflow.nesting.NestingConfig" />
    <import name="xfor"       class="org.hy.common.callflow.forloop.ForConfig" />
    <import name="xnode"      class="org.hy.common.callflow.node.NodeConfig" />
    <import name="xwait"      class="org.hy.common.callflow.node.WaitConfig" />
    <import name="xcalculate" class="org.hy.common.callflow.node.CalculateConfig" />
    <import name="xcondition" class="org.hy.common.callflow.ifelse.ConditionConfig" />
    
    
    
    <!-- CFlow编排引擎配置 -->
    <xconfig>
        
        <xnode id="XNode_CF001_002">
            <comment>节点002，后面节点配置在XML前</comment>
            <callXID>:XProgram</callXID>
            <callMehod>method002</callMehod>
        </xnode>
        
        
        <xnode id="XNode_CF001_001">
            <comment>节点001</comment>
            <callXID>:XProgram</callXID>                    <!-- 定义执行对象 -->
            <callMehod>method001</callMehod>                <!-- 定义执行方法 -->
            <route>
                <succeed>                                   <!-- 成功时，关联后置节点 -->
                    <next ref="XNode_CF001_002" />
                    <comment>成功时</comment>
                </succeed>
            </route>
        </xnode>
        
    </xconfig>
    
</config>
```

__执行编排__

```java
// 初始化被编排的执行对象方法（按业务需要）
XJava.putObject("XProgram" ,new Program());
        
// 获取编排中的首个元素
NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF001_001");

// 初始化上下文（可方便的获取中间运算结果，也可传NULL）
Map<String ,Object> v_Context   = new HashMap<String ,Object>();

// 执行编排。返回执行结果       
ExecuteResult       v_Result    = CallFlow.execute(v_FirstNode ,v_Context);
```


---
#### 本项目引用Jar包，其源码链接如下
引用 https://github.com/HY-ZhengWei/hy.common.base 类库

引用 https://github.com/HY-ZhengWei/hy.common.file 类库

引用 https://github.com/HY-ZhengWei/XJava 类库