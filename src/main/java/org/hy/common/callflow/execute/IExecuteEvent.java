package org.hy.common.callflow.execute;

import java.util.Map;





/**
 * 执行事件
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-21
 * @version     v1.0
 */
public interface IExecuteEvent
{
    
    /**
     * 编排实例启动前：编排整体执行前触发，对于一个编排执行实例，仅只触发一次。
     * 
     * 注1：触发前编排执行实例ID已生成。
     * 注2：触发前编排树ID已生成。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_FirstExec  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）（编排中的首个执行元素）
     * @param io_Context   上下文类型的变量信息
     * @return             是否允许执行，否则中断
     */
    public boolean start(IExecute i_FirstExec ,Map<String ,Object> io_Context);
    
    
    
    /**
     * 编排实例完成后：编排整体执行后触发（异常时也触发），对于一个编排执行实例，仅只触发一次。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_FirstExec   执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）（编排中的首个执行元素）
     * @param io_Context    上下文类型的变量信息
     * @param i_LastResult  最终的执行结果 
     */
    public void finish(IExecute i_FirstExec ,Map<String ,Object> io_Context ,ExecuteResult i_LastResult);
    
    
    
    /**
     * 执行前：每个编排元素在执行前触发
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_Context    上下文类型的变量信息
     * @return              是否允许执行，否则中断
     */
    public boolean before(IExecute i_ExecObject ,Map<String ,Object> io_Context);
    
    
    
    /**
     * 异常时（包括异常、取消和超时三种情况）：每个编排元素在异常时触发
     * 
     * 注：error() 触在 before() 之后执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_Context    上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean error(IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
    
    
    /**
     * 执行成功：每个编排元素在执行成功时触发
     * 
     * 注：success() 触在 before() 之后执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param i_Context     上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean success(IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
    
    
    /**
     * 执行后：每个编排元素在执行后时触发
     * 
     * 注1：after() 触在 before() 之后执行。
     * 注2：当执行异常时，先触发 error()   方法，在最后再触发 after() 方法。
     * 注3：当执行成功时，先触发 success() 方法，在最后再触发 after() 方法。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_Context    上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean after(IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
}
