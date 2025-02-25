package org.hy.common.callflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.execute.IExecuteEvent;
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
    
    /** 编排实例的变量ID名称 */
    public static final String $WorkID = "CallFlowWorkID";
    
    
    
    /**
     * 计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     */
    public static void calcTree(IExecute io_ExecObject)
    {
        if ( io_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        io_ExecObject.setTreeID(null ,ExecuteElement.$TreeID.getMinIndexNo());
        calcTreeToChilds(io_ExecObject);
    }
    
    
    
    /**
     * （递归）计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     */
    private static void calcTree(IExecute io_ExecObject ,String i_SuperTreeID ,int i_IndexNo)
    {
        io_ExecObject.setTreeID(i_SuperTreeID ,i_IndexNo);
        calcTreeToChilds(io_ExecObject);
    }
    
    
    
    /**
     * （递归）计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     */
    private static void calcTreeToChilds(IExecute io_ExecObject)
    {
        int            v_IndexNo = ExecuteElement.$TreeID.getMinIndexNo();
        List<IExecute> v_Childs  = null;
        
        v_Childs = io_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                calcTree(v_Child ,io_ExecObject.getTreeID() ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                calcTree(v_Child ,io_ExecObject.getTreeID() ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                calcTree(v_Child ,io_ExecObject.getTreeID() ,v_IndexNo++);
            }
        }
    }
    
    
    
    /**
     * 用树ID定位某个编排中的元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param i_TreeID      树ID
     * @return 
     */
    public static IExecute findTreeID(IExecute i_ExecObject ,String i_TreeID)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        if ( i_TreeID.equals(i_ExecObject.getTreeID()) )
        {
            return i_ExecObject;
        }
        
        List<IExecute> v_Childs  = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
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
     *                          最后执行结果的开始时间beginTime，也是整个编排的最早起始时间
     *                          最后执行结果的结束时间endTime  ，也是整个编排的最晚结束时间
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
     *                          最后执行结果的开始时间beginTime，也是整个编排的最早起始时间
     *                          最后执行结果的结束时间endTime  ，也是整个编排的最晚结束时间
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        ExecuteResult v_LastResult = new ExecuteResult();
        if ( i_ExecObject == null )
        {
            return v_LastResult.setException(new NullPointerException("ExecObject is null."));
        }
        
        Map<String ,Object> v_Context = io_Context == null ? new HashMap<String ,Object>() : io_Context;
        
        // 外界未定义编排实例ID时，自动生成
        if ( v_Context.get($WorkID) == null )
        {
            v_Context.put($WorkID ,"CFW" + StringHelp.getUUID9n());
        }
        
        // 事件：启动前
        if ( i_Event != null && !i_Event.start(i_ExecObject ,io_Context) )
        {
            return (new ExecuteResult(i_ExecObject.getTreeID() ,i_ExecObject.getXJavaID() ,"" ,null)).setCancel();
        }
        
        try
        {
            ExecuteResult v_NodeResult = CallFlow.execute(i_ExecObject ,v_Context ,null ,i_Event);
            v_LastResult.setPrevious(v_NodeResult);
            
            if ( v_NodeResult.isSuccess() )
            {
                return v_LastResult.setResult(v_NodeResult.getResult());
            }
            else
            {
                return v_LastResult.setException(v_NodeResult.getException());
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
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @param io_Context    上下文类型的变量信息
     * @param i_Previous    执行链：前一个
     * @param i_Event       执行监听事件
     * @return
     */
    private static ExecuteResult execute(IExecute            i_ExecObject 
                                        ,Map<String ,Object> io_Context 
                                        ,ExecuteResult       i_Previous 
                                        ,IExecuteEvent       i_Event)
    {
        // 事件：执行前
        if ( i_Event != null && !i_Event.before(i_ExecObject ,io_Context) )
        {
            return (new ExecuteResult(i_ExecObject.getTreeID() ,i_ExecObject.getXJavaID() ,"" ,i_Previous)).setCancel();
        }
        
        ExecuteResult v_Result = i_ExecObject.execute(io_Context);
        v_Result.setPrevious(i_Previous);
        
        List<IExecute> v_Nexts  = null;
        if ( v_Result.isSuccess() )
        {
            // 事件：执行成功
            if ( i_Event != null && !i_Event.success(i_ExecObject ,io_Context ,v_Result) )
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
            if ( i_Event != null && !i_Event.error(i_ExecObject ,io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            v_Nexts = i_ExecObject.getRoute().getExceptions();
        }
        
        // 事件：执行后
        if ( i_Event != null && !i_Event.after(i_ExecObject ,io_Context ,v_Result) )
        {
            return v_Result.setCancel();
        }
        
        if ( !Help.isNull(v_Nexts) )
        {
            ExecuteResult v_NextResult = null;
            for (IExecute v_Next : v_Nexts)
            {
                v_NextResult = CallFlow.execute(v_Next ,io_Context ,v_Result ,i_Event);
                v_Result.addNext(v_NextResult);
                if ( !v_NextResult.isSuccess() )
                {
                    return v_NextResult;
                }
            }
            return v_NextResult;
        }
        
        return v_Result;
    }
    
}
