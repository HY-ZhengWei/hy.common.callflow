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
     * 执行前
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_IndexNo     本方法要执行的执行序号。下标从1开始
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @return              是否允许执行，否则中断
     */
    public boolean before(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Context);
    
    
    
    /**
     * 异常时（包括异常、取消和超时三种情况）
     * 
     * 注：error() 触在 before() 之后执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_IndexNo     本方法要执行的执行序号。下标从1开始
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean error(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
    
    
    /**
     * 执行成功
     * 
     * 注：success() 触在 before() 之后执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_IndexNo     本方法要执行的执行序号。下标从1开始
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param i_Context     上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean success(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
    
    
    /**
     * 执行后
     * 
     * 注1：after() 触在 before() 之后执行。
     * 注2：当执行异常时，先触发 error()   方法，在最后再触发 after() 方法。
     * 注3：当执行成功时，先触发 success() 方法，在最后再触发 after() 方法。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_IndexNo     本方法要执行的执行序号。下标从1开始
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @param i_Result      执行结果 
     * @return              是否允许后续流程的执行，否则中断
     */
    public boolean after(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Context ,ExecuteResult i_Result);
    
}
