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
     * 用执行对象的树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult     执行结果
     * @param i_ExecuteTreeID  执行对象的树ID
     * @return 
     */
    public static ExecuteResult findExecuteTreeID(ExecuteResult i_ExecResult ,String i_ExecuteTreeID)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        if ( Help.isNull(i_ExecuteTreeID) )
        {
            throw new NullPointerException("ExecuteTreeID is null.");
        }
        
        ExecuteResult v_FirstResult = getFirstResult(i_ExecResult);
        if ( v_FirstResult == null )
        {
            return null;
        }
        else
        {
            return findExecuteTreeID_Inner(v_FirstResult ,i_ExecuteTreeID);
        }
    }
    
    
    
    /**
     * 用执行对象的树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    private static ExecuteResult findExecuteTreeID_Inner(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_TreeID.equals(i_ExecResult.getExecuteTreeID()) )
        {
            return i_ExecResult;
        }
        
        List<ExecuteResult> v_Childs = null;
        
        v_Childs = i_ExecResult.getNexts();
        if ( !Help.isNull(v_Childs) )
        {
            for (ExecuteResult v_Child : v_Childs)
            {
                ExecuteResult v_Ret = findExecuteTreeID_Inner(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 用树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    public static ExecuteResult findTreeID(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        ExecuteResult v_FirstResult = getFirstResult(i_ExecResult);
        if ( v_FirstResult == null )
        {
            return null;
        }
        else
        {
            return findTreeID_Inner(v_FirstResult ,i_TreeID);
        }
    }
    
    
    
    /**
     * 用树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    private static ExecuteResult findTreeID_Inner(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_TreeID.equals(i_ExecResult.getTreeID()) )
        {
            return i_ExecResult;
        }
        
        List<ExecuteResult> v_Childs = i_ExecResult.getNexts();
        if ( !Help.isNull(v_Childs) )
        {
            for (ExecuteResult v_Child : v_Childs)
            {
                ExecuteResult v_Ret = findTreeID_Inner(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 定位某个编排实例执行结果中的首个结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @return 
     */
    public static ExecuteResult getFirstResult(ExecuteResult i_ExecResult)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        
        if ( i_ExecResult.getTreeLevel() == null || i_ExecResult.getTreeNo() == null )
        {
            throw new NullPointerException("ExecuteResult's TreeLevel or TreeNo is null.");
        }
        else if ( ExecuteResult.$TreeID.getRootLevel()  == i_ExecResult.getTreeLevel() 
               && ExecuteResult.$TreeID.getMinIndexNo() == i_ExecResult.getTreeNo() )
        {
            return i_ExecResult;
        }
        
        ExecuteResult v_Previous = i_ExecResult.getPrevious();
        if ( v_Previous == null )
        {
            return i_ExecResult;
        }
        else
        {
            return getFirstResult(v_Previous);
        }
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
        
        if ( Help.isNull(i_ExecObject.getTreeID()) )
        {
            CallFlow.calcTree(i_ExecObject);
        }
        
        // 事件：启动前
        if ( i_Event != null && !i_Event.start(i_ExecObject ,io_Context) )
        {
            return CallFlow.putError(io_Context ,(new ExecuteResult(i_ExecObject.getTreeID() ,i_ExecObject.getXJavaID() ,"" ,null)).setCancel());
        }
        
        try
        {
            ExecuteResult v_NodeResult = CallFlow.execute(i_ExecObject ,v_Context ,null ,i_Event);
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
            ExecuteResult v_Result = (new ExecuteResult(i_ExecObject.getTreeID() ,i_ExecObject.getXJavaID() ,"" ,i_Previous)).setCancel();
            if ( i_Previous == null )
            {
                io_Context.put($FirstExecuteResult ,v_Result);
            }
            return CallFlow.putError(io_Context ,v_Result);
        }
        
        ExecuteResult v_Result = i_ExecObject.execute(io_Context);
        v_Result.setPrevious(i_Previous);
        if ( i_Previous == null )
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
                ExecuteResult v_NextResult = CallFlow.execute(v_Next ,io_Context ,v_Result ,i_Event);
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
