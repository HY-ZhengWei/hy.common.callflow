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
    
        1.1. __执行元素__，配置及执行Java方法。可轻松扩展它，衍生成特定的业务元素。
    
        1.2. __条件逻辑元素__，与或逻辑判定。
    
        1.3. __计算元素__，计算数据、加工数据、创建新数据。
        
        1.4. __等待元素__，等待一段时间。
    
        1.5. __循环元素__，按数组、集合或数列，循环执行一个或多个其它元素。
    
        1.6. __嵌套元素__，嵌套其它编排，复用与共享编排，构建更复杂的业务。
        
    2. 4种编排路由。
        
        2.1 __成功路由__，元素执行成功时走的路。
        
        2.2 __异常路由__，元素执行出时异常时走的路。
        
        2.3 __真值路由__，条件逻辑元素与计算元素（逻辑类型的），结果为真时走的路。
        
        2.4 __假值路由__，条件逻辑元素与计算元素（逻辑类型的），结果为假时走的路。
        
    3. 2种编排循环。
        
        3.1 __While循环路由__，下层元素回流到上层元素时走的路。
        
        3.2 __For循环路由__，与循环元素配合（至少有一个），回流到循环元素的路。
        
        3.3 路由与循环可以组合后共存，即：成功路由+For循环路由组成一条成功时For循环的路由。



---
#### 本项目引用Jar包，其源码链接如下
引用 https://github.com/HY-ZhengWei/hy.common.base 类库

引用 https://github.com/HY-ZhengWei/hy.common.file 类库

引用 https://github.com/HY-ZhengWei/XJava 类库