package org.hy.common.callflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.clone.CloneableHelp;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteElementCheckHelp;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.ExecuteResultLogHelp;
import org.hy.common.callflow.execute.ExecuteTreeHelp;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.execute.IExecuteEvent;
import org.hy.common.callflow.file.ExportXml;
import org.hy.common.callflow.file.ImportXML;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.nesting.NestingConfig;
import org.hy.common.callflow.node.CalculateConfig;
import org.hy.common.callflow.node.WaitConfig;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.route.SelfLoop;
import org.hy.common.xml.XJava;





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
    public static       String $SavePath                = Help.getSysTempPath();
    
    /** 变量ID名称：编排执行实例ID */
    public static final String $WorkID                  = "CallFlowWorkID";
    
    /** 变量ID名称：编排执行实例的首个执行对象的执行结果 */
    public static final String $FirstExecuteResult      = "CallFlowFirstExecuteResult";
    
    /** 变量ID名称：编排执行实例的最后执行对象的执行结果（但不包括异常的） */
    public static final String $LastExecuteResult       = "CallFlowLastExecuteResult";
    
    /** 变量ID名称：编排执行实例的最后执行嵌套的开始部分的结果（瞬息间的值，用完就即时释放了） */
    public static final String $LastNestingBeginResult  = "CallFlowLastNestingBeginResult";
    
    /** 变量ID名称：编排执行实例的嵌套层次 */
    public static final String $NestingLevel            = "CallFlowNestingLevel";
    
    /** 变量ID名称：编排执行实例是否异常 */
    public static final String $ExecuteIsError          = "CallFlowExecuteIsError";
    
    /** 变量ID名称：编排执行实例的返回元素执行真返回时的标记 */
    public static final String $CallFlowReturn          = "CallFlowReturn";
    
    /** 变量ID名称：编排执行实例的返回元素执行真返回时的返回值 */
    public static final String $CallFlowReturnValue     = "CallFlowReturnValue";
    
    /** 变量ID名称：编排执行实例异常的结果 */
    public static final String $ErrorResult             = "CallFlowErrorResult";
    
    /** 变量ID名称：编排执行实例的监听事件（事件可以传递到嵌套子编排中去） */
    public static final String $ExecuteEvent            = "CallFlowExecuteEvent";
    
    /** 变量ID名称：编排执行实例的上下文当参数传输到方法中时的系统预设的变量名 */
    public static final String $Context                 = "CallFlowContext";
    
    /** 变量ID名称：编排执行实例的等待元素的计数器的系统预设的变量名 */
    public static final String $WaitCounter             = "CallFlowWaitCounter";
    
    
    
    /**
     * 是否为系统预定义的XID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
     * @version     v1.0
     *
     * @param i_XID
     * @return
     */
    public static boolean isSystemXID(String i_XID)
    {
        if ( Help.isNull(i_XID) )
        {
            return false;
        }
        
        String v_XID = ValueHelp.standardValueID(i_XID);
        
        if ( CallFlow.$WorkID                .equals(v_XID) 
          || CallFlow.$FirstExecuteResult    .equals(v_XID) 
          || CallFlow.$LastExecuteResult     .equals(v_XID) 
          || CallFlow.$LastNestingBeginResult.equals(v_XID) 
          || CallFlow.$NestingLevel          .equals(v_XID) 
          || CallFlow.$ExecuteIsError        .equals(v_XID) 
          || CallFlow.$ErrorResult           .equals(v_XID) 
          || CallFlow.$ExecuteEvent          .equals(v_XID)
          || CallFlow.$Context               .equals(v_XID)
          || CallFlow.$WaitCounter           .equals(v_XID)
          || CallFlow.$CallFlowReturn        .equals(v_XID)
          || CallFlow.$CallFlowReturnValue   .equals(v_XID) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 在执行上下文中，设置编排执行实例ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public static Map<String ,Object> setWorkID(Map<String ,Object> io_Context ,String i_WorkID)
    {
        io_Context.put($WorkID ,i_WorkID);
        return io_Context;
    }
    
    
    
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
     * 从执行上下文中，获取 “真返回” 的返回数值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-13
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static Object getReturnValue(Map<String ,Object> i_Context)
    {
        return i_Context.get($CallFlowReturnValue);
    }
    
    
    
    /**
     * 从执行上下文中，是否 “真返回”
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static boolean isTrueReturn(Map<String ,Object> i_Context)
    {
        return i_Context.get($CallFlowReturn) != null;
    }
    
    
    
    /**
     * 从执行上下文中，清除 “真返回”
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     */
    public static void clearTrueReturn(Map<String ,Object> io_Context)
    {
        // 注：但不会清除的返回数值 $CallFlowReturnValue
        io_Context.remove($CallFlowReturn);
    }
    
    
    
    /**
     * 从执行上下文中，获取首个执行对象的执行结果
     * 
     * 当有嵌套时，此方法仅能返回顶层流程编排中的首个执行对象。
     * 要返回全量首个执行对象应当用：CallFlow.getHelpExecute().getFirstResult() 方法。
     * 
     * 上面的差异仅在A编排的首个元素嵌套着B编排时才能看出区别。参见 JU_CFlow007
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
     * 从执行上下文中，获取最后执行对象的执行结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static ExecuteResult getLastResult(Map<String ,Object> i_Context)
    {
        return (ExecuteResult) i_Context.get($LastExecuteResult);
    }
    
    
    
    /**
     * 从执行上下文中，获取嵌套层次
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-02
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static Integer getNestingLevel(Map<String ,Object> i_Context)
    {
        return (Integer) i_Context.get($NestingLevel);
    }
    
    
    
    /**
     * 从执行上下文中，获取执行事件监听器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static IExecuteEvent getExecuteEvent(Map<String ,Object> i_Context)
    {
        return (IExecuteEvent) i_Context.get($ExecuteEvent);
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
     * 从上下文中清除之前记录的异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param io_Context
     */
    public static void clearError(Map<String ,Object> io_Context)
    {
        io_Context.put($ExecuteIsError ,false);
        io_Context.remove($ErrorResult);
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
    public static ExecuteTreeHelp getHelpExecute()
    {
        return ExecuteTreeHelp.getInstance();
    }
    
    
    
    /**
     * 集成：编排及元素的检测
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-17
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteElementCheckHelp getHelpCheck()
    {
        return ExecuteElementCheckHelp.getInstance();
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
    public static ExportXml getHelpExport()
    {
        return ExportXml.getInstance();
    }
    
    
    
    /**
     * 集成：导入XML格式的编排配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @return
     */
    public static ImportXML getHelpImport()
    {
        return ImportXML.getInstance();
    }
    
    
    
    /**
     * 集成：执行结果的日志辅助类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteResultLogHelp getHelpLog()
    {
        return ExecuteResultLogHelp.getInstance();
    }
    
    
    
    /**
     * 集成：克隆编排的辅助类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @return
     */
    public static CloneableHelp getHelpClone()
    {
        return CloneableHelp.getInstance();
    }
    
    
    
    /**
     * 首个执行、条件逻辑、等待、计算、循环、嵌套和返回元素的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param i_ExecXID     执行对象的XID（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @param io_Context    上下文类型的变量信息
     * @return              返回编排执行链中的最后的执行结果
     *                          1.最后执行结果的开始时间beginTime，也是整个编排的最早起始时间
     *                          2.最后执行结果的结束时间endTime  ，也是整个编排的最晚结束时间
     *                          3.异常时，最后执行结果重复描述一次异常信息
     */
    public static ExecuteResult execute(String i_ExecXID ,Map<String ,Object> io_Context)
    {
        return execute(i_ExecXID ,io_Context ,null);
    }
    
    
    
    /**
     * 首个执行、条件逻辑、等待、计算、循环、嵌套和返回元素的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param i_ExecXID     执行对象的XID（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @param io_Context    上下文类型的变量信息
     * @param i_Event       执行监听事件
     * @return              返回编排执行链中的首个执行对象的执行结果
     *                          1.返回值的beginTime，是整个编排的最早起始时间
     *                          2.返回值的endTime  ，是整个编排的最晚结束时间
     *                          3.异常时，最后执行结果重复描述一次异常信息
     */
    public static ExecuteResult execute(String i_ExecXID ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        IExecute v_ExecObject = (IExecute) XJava.getObject(i_ExecXID);
        return execute(v_ExecObject ,io_Context ,i_Event);
    }
    
    
    
    /**
     * 首个执行、条件逻辑、等待、计算、循环、嵌套和返回元素的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
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
     * 首个执行、条件逻辑、等待、计算、循环、嵌套和返回元素的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @param io_Context    上下文类型的变量信息
     * @param i_Event       执行监听事件
     * @return              返回编排执行链中的首个执行对象的执行结果
     *                          1.返回值的beginTime，是整个编排的最早起始时间
     *                          2.返回值的endTime  ，是整个编排的最晚结束时间
     *                          3.异常时，最后执行结果重复描述一次异常信息
     */
    public static ExecuteResult execute(IExecute i_ExecObject ,Map<String ,Object> io_Context ,IExecuteEvent i_Event)
    {
        ExecuteResult v_LastResult = new ExecuteResult();
        if ( i_ExecObject == null )
        {
            return v_LastResult.setException(new NullPointerException("ExecObject is null."));
        }
        if ( ((ExecuteElement) i_ExecObject).isKeyChange() )
        {
            Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(i_ExecObject);
            if ( !v_CheckRet.get() )
            {
                return v_LastResult.setException(new RuntimeException(v_CheckRet.getParamStr()));
            }
        }
        
        Map<String ,Object> v_Context = io_Context == null ? new HashMap<String ,Object>() : io_Context;
        CallFlow.clearError(v_Context);
        
        // 外界未定义编排执行实例ID时，自动生成
        if ( v_Context.get($WorkID) == null )
        {
            v_Context.put($WorkID ,"CFW" + StringHelp.getUUID9n());
        }
        
        // 嵌套层次一般不用外界定义
        // 在主编排中初始化为0，在每进一级嵌套，此值累加
        if ( v_Context.get($NestingLevel) == null )
        {
            v_Context.put($NestingLevel ,0);
        }
        
        if ( Help.isNull(i_ExecObject.getTreeIDs()) )
        {
            CallFlow.getHelpExecute().calcTree(i_ExecObject);
        }
        
        // 上下文中压入事件监听器
        if ( i_Event != null )
        {
            v_Context.put($ExecuteEvent ,i_Event);
        }
        
        // 事件：启动前
        String v_TreeID = i_ExecObject.getTreeIDs().iterator().next();
        if ( i_Event != null && !i_Event.start(i_ExecObject ,io_Context) )
        {
            return CallFlow.putError(io_Context ,(new ExecuteResult(CallFlow.getNestingLevel(v_Context) ,v_TreeID ,i_ExecObject.getXJavaID() ,"" ,null)).setCancel());
        }
        
        try
        {
            ExecuteResult v_NodeResult = CallFlow.execute(i_ExecObject ,v_Context ,i_ExecObject.getTreeSuperID(v_TreeID) ,null ,i_Event);
            v_LastResult.setPrevious(v_NodeResult);
            
            if ( !CallFlow.getExecuteIsError(io_Context) )
            {
                // 真返回时的返回数值
                if ( CallFlow.isTrueReturn(v_Context) )
                {
                    return v_LastResult.setResult(CallFlow.getReturnValue(v_Context));
                }
                else
                {
                    return v_LastResult.setResult(v_NodeResult.getResult());
                }
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
     * @param i_ExecObject      执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
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
            ExecuteResult v_Result = (new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,i_ExecObject.getTreeID(i_SuperTreeID) ,i_ExecObject.getXJavaID() ,"" ,i_PreviousResult)).setCancel();
            if ( i_PreviousResult == null )
            {
                io_Context.put($FirstExecuteResult ,v_Result);
            }
            return CallFlow.putError(io_Context ,v_Result);
        }
        
        ExecuteResult v_Result = i_ExecObject.execute(i_SuperTreeID ,io_Context);
        v_Result.setElementType(i_ExecObject.getElementType());
        v_Result.setComment(i_ExecObject.getComment());
        if ( i_PreviousResult == null )
        {
            // 这里须明白，当有嵌套时，子级的编排也有它自己的首个执行对象的结果
            // 子级的编排会覆盖父级的编排，所以因为嵌套中处理，如备份处理。
            io_Context.put($FirstExecuteResult ,v_Result);
            
            // 有两种可能造成上个结果为NULL
            //     原因1：主编排中首个执行结果
            //     原因2：子编排中首个执行结果
            ExecuteResult v_NestingBegin = (ExecuteResult) io_Context.remove($LastNestingBeginResult);
            if ( v_NestingBegin != null )
            {
                // 原因2：子编排中首个执行结果。如果嵌套的情况，也只用一次
                v_Result.setPrevious(v_NestingBegin);
                v_NestingBegin.addNext(v_Result);
            }
            else
            {
                // 原因1：主编排中首个执行结果
                v_Result.setPrevious(null);
            }
        }
        
        if ( i_ExecObject instanceof NestingConfig )
        {
            // 嵌套配置时，因为嵌套配置已在它的内部设置了
            // 并且嵌套的Previous前一个关联在了子编排流程中的最后执行元素上
        }
        else if ( i_PreviousResult != null )
        {
            v_Result.setPrevious(i_PreviousResult);
        }
        io_Context.put($LastExecuteResult ,v_Result);
        
        List<RouteItem> v_Nexts  = null;
        if ( v_Result.isSuccess() )
        {
            // 事件：执行成功
            if ( i_Event != null && !i_Event.success(i_ExecObject ,io_Context ,v_Result) )
            {
                return CallFlow.putError(io_Context ,v_Result.setCancel());
            }
            
            // 条件逻辑元素
            if ( i_ExecObject instanceof ConditionConfig )
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
            // 等待元素
            else if ( i_ExecObject instanceof WaitConfig )
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
            // 计算元素
            else if ( i_ExecObject instanceof CalculateConfig )
            {
                // 计算元素按条件逻辑元素行事
                if ( Boolean.class.equals(v_Result.getResult().getClass()) )
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
                // 计算元素按计算运算行事
                else
                {
                    v_Nexts = i_ExecObject.getRoute().getSucceeds();
                }
            }
            // 循环元素
            else if ( i_ExecObject instanceof SelfLoop )
            {
                Object v_RetValue = v_Result.getResult();
                if ( v_RetValue instanceof Boolean )
                {
                    if ( (Boolean) v_RetValue )
                    {
                        v_Nexts = ((SelfLoop) i_ExecObject).gatRoute().getSucceeds();
                    }
                    else
                    {
                        // 当循环结束时
                        // Nothing.
                    }
                }
                else
                {
                    v_Nexts = ((SelfLoop) i_ExecObject).gatRoute().getSucceeds();
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
            
            if ( i_ExecObject instanceof SelfLoop )
            {
                v_Nexts = ((SelfLoop) i_ExecObject).gatRoute().getExceptions();
            }
            else
            {
                v_Nexts = i_ExecObject.getRoute().getExceptions();
            }
            
            if ( Help.isNull(v_Nexts) )
            {
                // 当没有异常处理时，应结束整个编排，退出结束整个编排
                CallFlow.putError(io_Context ,v_Result);
            }
        }
        
        // 事件：执行后
        if ( i_Event != null && !i_Event.after(i_ExecObject ,io_Context ,v_Result) )
        {
            return CallFlow.putError(io_Context ,v_Result.setCancel());
        }
        
        if ( !Help.isNull(v_Nexts) )
        {
            for (RouteItem v_Next : v_Nexts)
            {
                ExecuteResult v_NextResult = CallFlow.execute(v_Next.gatNext() ,io_Context ,i_ExecObject.getTreeID(i_SuperTreeID) ,v_Result ,i_Event);
                v_Result.addNext(v_NextResult);
                if ( CallFlow.getExecuteIsError(io_Context) )
                {
                    // 子级或子子级执行异常
                    return v_Result;
                }
                else if ( CallFlow.isTrueReturn(io_Context) )
                {
                    // 真返回
                    return v_Result;
                }
                
                // 循环元素
                if ( v_Next.gatNext() instanceof SelfLoop )
                {
                    Object v_RetValue = v_NextResult.getResult();
                    if ( v_RetValue instanceof Boolean )
                    {
                        if ( (Boolean) v_RetValue )
                        {
                            // 当循环下一个时
                            break;
                        }
                    }
                }
            }
            return v_Result;
        }
        
        return v_Result;
    }
    
}
