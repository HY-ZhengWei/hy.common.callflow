package org.hy.common.callflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
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
    
    /**
     * 首个节点或条件逻辑的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Default    默认值类型的变量信息
     * @param io_Context    上下文类型的变量信息
     * @return
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Default ,Map<String ,Object> io_Context)
    {
        ExecuteResult v_Result = new ExecuteResult();
        if ( i_ExecObject == null )
        {
            return v_Result.setException(new NullPointerException("ExecObject is null."));
        }
        
        Map<String ,Object> v_Default = io_Default == null ? new HashMap<String ,Object>() : io_Default;
        Map<String ,Object> v_Context = io_Context == null ? new HashMap<String ,Object>() : io_Context;
        int                 v_IndexNo = 0;
        
        ExecuteResult v_NodeResult = CallFlow.execute(v_IndexNo + 1 ,i_ExecObject ,v_Default ,v_Context);
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
     * @param io_Default    默认值类型的变量信息
     * @param io_Context    上下文类型的变量信息
     * @return
     */
    private static ExecuteResult execute(int i_IndexNo ,IExecute i_ExecObject ,Map<String ,Object> io_Default ,Map<String ,Object> io_Context)
    {
        ExecuteResult  v_Result = i_ExecObject.execute(i_IndexNo ,io_Default ,io_Context);
        List<IExecute> v_Nexts  = null;
        
        if ( v_Result.isSuccess() )
        {
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
        }
        else
        {
            v_Nexts = i_ExecObject.getRoute().getExceptions();
        }
        
        
        if ( !Help.isNull(v_Nexts) )
        {
            for (IExecute v_Next : v_Nexts)
            {
                v_Result = CallFlow.execute(i_IndexNo + 1 ,v_Next ,io_Default ,io_Context);
                if ( !v_Result.isSuccess() )
                {
                    return v_Result;
                }
            }
        }
        
        return v_Result;
    }
    
}
