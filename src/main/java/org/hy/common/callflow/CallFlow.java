package org.hy.common.callflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.ExecuteTreeHelp;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.execute.IExecuteEvent;
import org.hy.common.callflow.file.ExportXml;
import org.hy.common.callflow.ifelse.Condition;





/**
 * 方法编排引擎
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class CallFlow
{
    
    /** 编排XML配置文件的保存路径 */
    public static       String $SavePath           = Help.getSysTempPath();
    
    /** 变量ID名称：编排执行实例ID */
    public static final String $WorkID             = "CallFlowWorkID";
    
    /** 变量ID名称：编排执行实例的首个执行对象的执行结果 */
    public static final String $FirstExecuteResult = "CallFlowFirstExecuteResult";
    
    /** 变量ID名称：编排执行实例是否异常 */
    public static final String $ExecuteIsError     = "CallFlowExecuteIsError";
    
    /** 变量ID名称：编排执行实例异常的结果 */
    public static final String $ErrorResult        = "CallFlowErrorResult";
    
    
    
    /**
     * 从执行上下文中，获取编排执行实例ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static String getWorkID(Map<String ,Object> i_Context)
    {
        return (String) i_Context.get($WorkID);
    }
    
    
    
    /**
     * 从执行上下文中，获取首个执行对象的执行结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static ExecuteResult getFirstResult(Map<String ,Object> i_Context)
    {
        return (ExecuteResult) i_Context.get($FirstExecuteResult);
    }
    
    
    
    /**
     * 从执行上下文中，获取执行是否异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static Boolean getExecuteIsError(Map<String ,Object> i_Context)
    {
        return (Boolean) i_Context.get($ExecuteIsError);
    }
    
    
    
    /**
     * 从执行上下文中，获取执行异常的结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static ExecuteResult getErrorResult(Map<String ,Object> i_Context)
    {
        return (ExecuteResult) i_Context.get($ErrorResult);
    }
    
    
    
    /**
     * 向执行上下文中，记录执行异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_Context     上下文类型的变量信息
     * @param i_ErrorResult  异常结果 
     */
    private static ExecuteResult putError(Map<String ,Object> io_Context ,ExecuteResult i_ErrorResult)
    {
        io_Context.put($ExecuteIsError ,true);
        io_Context.put($ErrorResult    ,i_ErrorResult);
        return i_ErrorResult;
    }
    
    
    
    /**
     * 集成：执行对象的树ID的相关操作类（注：有向图结构）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteTreeHelp getExecuteHelp()
    {
        return ExecuteTreeHelp.getInstance();
    }
    
    
    
    /**
     * 集成：编排配置导出为XML
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public static ExportXml getExportHelp()
    {
        return ExportXml.getInstance();
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
     * @return              返回编排执行链中的最后的执行结果
     *                          1.最后执行结果的开始时间beginTime，也是整个编排的最早起始时间
     *                          2.最后执行结果的结束时间endTime  ，也是整个编排的最晚结束时间
     *                          3.异常时，最后执行结果重复描述一次异常信息
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
     * @return              返回编排执行链中的最后的执行结果
     *                          1.最后执行结果的开始时间beginTime，也是整个编排的最早起始时间
     *                          2.最后执行结果的结束时间endTime  ，也是整个编排的最晚结束时间
     *                          3.异常时，最后执行结果重复描述一次异常信息
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        ExecuteResult v_LastResult = new ExecuteResult();
        if ( i_ExecObject == null )
        {
            return v_LastResult.setException(new NullPointerException("ExecObject is null."));
        }
        
        Map<String ,Object> v_Context = io_Context == null ? new HashMap<String ,Object>() : io_Context;
        io_Context.put($ExecuteIsError ,false);
        
        // 外界未定义编排执行实例ID时，自动生成
        if ( v_Context.get($WorkID) == null )
        {
            v_Context.put($WorkID ,"CFW" + StringHelp.getUUID9n());
        }
        
        if ( Help.isNull(i_ExecObject.getTreeIDs()) )
        {
            CallFlow.getExecuteHelp().calcTree(i_ExecObject);
        }
        
        // 事件：启动前
        String v_TreeID = i_ExecObject.getTreeIDs().iterator().next();
        if ( i_Event != null && !i_Event.start(i_ExecObject ,io_Context) )
        {
            return CallFlow.putError(io_Context ,(new ExecuteResult(i_ExecObject.getTreeIDs().iterator().next() ,i_ExecObject.getXJavaID() ,"" ,null)).setCancel());
        }
        
        try
        {
            ExecuteResult v_NodeResult = CallFlow.execute(i_ExecObject ,v_Context ,i_ExecObject.getTreeSuperID(v_TreeID) ,null ,i_Event);
            v_LastResult.setPrevious(v_NodeResult);
            
            if ( !CallFlow.getExecuteIsError(io_Context) )
            {
                return v_LastResult.setResult(v_NodeResult.getResult());
            }
            else
            {
                ExecuteResult v_ErrorResult = CallFlow.getErrorResult(v_Context);
                v_LastResult.setExecuteTreeID(   v_ErrorResult.getExecuteTreeID());
                v_LastResult.setExecuteXID(      v_ErrorResult.getExecuteXID());
                v_LastResult.setExecuteLogic(    v_ErrorResult.getExecuteLogic());
                return v_LastResult.setException(v_ErrorResult.getException());
            }
        }
        finally
        {
            // 事件：完成后
            if ( i_Event != null )
            {
                i_Event.finish(i_ExecObject ,io_Context ,v_LastResult);
            }
        }
    }
    
    
    
    /**
     * 递归执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject      执行对象（节点或条件逻辑）
     * @param io_Context        上下文类型的变量信息
     * @param i_SuperTreeID     执行链：前一个执行对象的树ID
     * @param i_PreviousResult  执行链：前一个执行结果
     * @param i_Event           执行监听事件
     * @return
     */
    private static ExecuteResult execute(IExecute            i_ExecObject 
                                        ,Map<String ,Object> io_Context 
                                        ,String              i_SuperTreeID
                                        ,ExecuteResult       i_PreviousResult 
                                        ,IExecuteEvent       i_Event)
    {
        // 事件：执行前
        if ( i_Event != null && !i_Event.before(i_ExecObject ,io_Context) )
        {
            ExecuteResult v_Result = (new ExecuteResult(i_ExecObject.getTreeID(i_SuperTreeID) ,i_ExecObject.getXJavaID() ,"" ,i_PreviousResult)).setCancel();
            if ( i_PreviousResult == null )
            {
                io_Context.put($FirstExecuteResult ,v_Result);
            }
            return CallFlow.putError(io_Context ,v_Result);
        }
        
        ExecuteResult v_Result = i_ExecObject.execute(i_SuperTreeID ,io_Context);
        v_Result.setPrevious(i_PreviousResult);
        if ( i_PreviousResult == null )
        {
            io_Context.put($FirstExecuteResult ,v_Result);
        }
        
        List<IExecute> v_Nexts  = null;
        if ( v_Result.isSuccess() )
        {
            // 事件：执行成功
            if ( i_Event != null && !i_Event.success(i_ExecObject ,io_Context ,v_Result) )
            {
                return CallFlow.putError(io_Context ,v_Result.setCancel());
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
            if ( i_Event != null && !i_Event.error(i_ExecObject ,io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            v_Nexts = i_ExecObject.getRoute().getExceptions();
        }
        
        // 事件：执行后
        if ( i_Event != null && !i_Event.after(i_ExecObject ,io_Context ,v_Result) )
        {
            return CallFlow.putError(io_Context ,v_Result.setCancel());
        }
        
        if ( !Help.isNull(v_Nexts) )
        {
            for (IExecute v_Next : v_Nexts)
            {
                ExecuteResult v_NextResult = CallFlow.execute(v_Next ,io_Context ,i_ExecObject.getTreeID(i_SuperTreeID) ,v_Result ,i_Event);
                v_Result.addNext(v_NextResult);
                if ( !v_NextResult.isSuccess() )
                {
                    CallFlow.putError(io_Context ,v_NextResult);
                    return v_Result;
                }
                else if ( CallFlow.getExecuteIsError(io_Context) )
                {
                    // 子级或子子级执行异常
                    return v_Result;
                }
            }
            return v_Result;
        }
        
        return v_Result;
    }
    
}
