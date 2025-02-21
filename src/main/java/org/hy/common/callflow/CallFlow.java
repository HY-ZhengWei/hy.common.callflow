package org.hy.common.callflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.execute.IExecuteEvent;
import org.hy.common.callflow.ifelse.Condition;





/**
 * 方法编排
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class CallFlow
{
    
    /** 编排实例的变量ID名称 */
    public static final String $WorkID = "CallFlowWorkID";
    
    
    
    /**
     * 首个节点或条件逻辑的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @return
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Context)
    {
        return execute(i_ExecObject ,io_Context ,null);
    }
    
    
    
    /**
     * 首个节点或条件逻辑的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @param i_Event       执行监听事件
     * @return
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        ExecuteResult v_Result = new ExecuteResult();
        if ( i_ExecObject == null )
        {
            return v_Result.setException(new NullPointerException("ExecObject is null."));
        }
        
        Map<String ,Object> v_Context = io_Context == null ? new HashMap<String ,Object>() : io_Context;
        int                 v_IndexNo = 0;
        
        // 外界未定义编排实例ID时，自动生成
        if ( v_Context.get($WorkID) == null )
        {
            v_Context.put($WorkID ,"CFW" + StringHelp.getUUID9n());
        }
        
        ExecuteResult v_NodeResult = CallFlow.execute(v_IndexNo + 1 ,i_ExecObject ,v_Context ,i_Event);
        v_Result.setIndexNo(v_NodeResult.getIndexNo());
        v_Result.setExecuteXID(v_NodeResult.getExecuteXID());
        
        if ( v_NodeResult.isSuccess() )
        {
            return v_Result.setResult(v_NodeResult.getResult());
        }
        else
        {
            return v_Result.setException(v_NodeResult.getException());
        }
    }
    
    
    
    /**
     * 递归执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_IndexNo     本方法要执行的执行序号。下标从1开始
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @param i_Event       执行监听事件
     * @return
     */
    private static ExecuteResult execute(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        // 事件：执行前
        if ( i_Event != null && !i_Event.before(i_IndexNo ,i_ExecObject ,io_Context) )
        {
            return (new ExecuteResult(i_IndexNo ,i_ExecObject.getXJavaID())).setCancel();
        }
        
        ExecuteResult v_Result = i_ExecObject.execute(i_IndexNo ,io_Context);
        
        List<IExecute> v_Nexts  = null;
        if ( v_Result.isSuccess() )
        {
            // 事件：执行成功
            if ( i_Event != null && !i_Event.success(i_IndexNo ,i_ExecObject ,io_Context ,v_Result) )
            {
                return v_Result.setCancel();
            }
            
            if ( i_ExecObject instanceof Condition )
            {
                if ( (Boolean) v_Result.getResult() )
                {
                    v_Nexts = i_ExecObject.getRoute().getSucceeds();
                }
                else
                {
                    v_Nexts = i_ExecObject.getRoute().getFaileds();
                }
            }
            else
            {
                v_Nexts = i_ExecObject.getRoute().getSucceeds();
            }
        }
        else
        {
            // 事件：执行异常（包括异常、取消和超时三种情况）
            if ( i_Event != null && !i_Event.error(i_IndexNo ,i_ExecObject ,io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            v_Nexts = i_ExecObject.getRoute().getExceptions();
        }
        
        // 事件：执行后
        if ( i_Event != null && !i_Event.after(i_IndexNo ,i_ExecObject ,io_Context ,v_Result) )
        {
            return v_Result.setCancel();
        }
        
        if ( !Help.isNull(v_Nexts) )
        {
            for (IExecute v_Next : v_Nexts)
            {
                v_Result = CallFlow.execute(i_IndexNo + 1 ,v_Next ,io_Context ,i_Event);
                if ( !v_Result.isSuccess() )
                {
                    return v_Result;
                }
            }
        }
        
        return v_Result;
    }
    
}
