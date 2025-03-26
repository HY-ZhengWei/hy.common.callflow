package org.hy.common.callflow.node;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.timeout.TimeoutConfig;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 执行元素：节点配置信息
 * 
 * 注：不建议节点配置共用，即使两个编排调用相同的执行方法也建议配置两个节点，使节点唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享节点配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeConfig extends ExecuteElement implements NodeConfigBase ,Cloneable
{
    
    private static final Logger $Logger = new Logger(NodeConfig.class);
    
    
    
    /** 执行对象的XID */
    private String              callXID;
    
    /** 执行方法名称 */
    private String              callMethod;
    
    /** 执行方法对象（仅内部使用） */
    private Method              callMethodObject;
    
    /** 是否初始化（仅内部使用） */
    private boolean             isInit;
    
    /** 执行方法的参数 */
    private List<NodeParam>     callParams;
    
    /** 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String              timeout;
    
    /** 向上下文中赋值 */
    private String              context;
    
    
    
    public NodeConfig()
    {
        this(0L ,0L);
    }
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public NodeConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.isInit  = false;
        this.timeout = "0";
    }
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Node.getValue();
    }
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        if ( Help.isNull(this.callXID) )
        {
            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        if ( !Help.isNull(this.context) )
        {
            try
            {
                String v_Context = ValueHelp.replaceByContext(this.context ,io_Context);
                Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_Context ,Map.class ,null ,null);
                io_Context.putAll(v_ContextMap);
                v_ContextMap.clear();
                v_ContextMap = null;
            }
            catch (Exception exce)
            {
                v_Result.setException(exce);
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        v_CallObject = this.generateObject(io_Context ,v_CallObject);
        if ( v_CallObject == null )
        {
            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.callXID + "] is not find."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            try
            {
                for (int x=0; x<v_ParamValues.length; x++)
                {
                    NodeParam v_NodeParam  = this.callParams.get(x);
                    v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.gatValueClass() ,v_NodeParam.gatValueDefaultObject() ,io_Context);
                }
            }
            catch (Exception exce)
            {
                v_Result.setException(exce);
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
        }
        else
        {
            v_ParamValues = new Object[0];
        }
        
        try
        {
            this.init(v_CallObject ,v_ParamValues);
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        v_ParamValues = this.generateParams(io_Context ,v_ParamValues);
        
        try
        {
            Long v_Timeout = this.gatTimeout(io_Context);
            if ( v_Timeout > 0L )
            {
                TimeoutConfig<Object> v_Future = this.executeAsync(v_Timeout ,io_Context ,v_CallObject ,v_ParamValues);
                Object v_ExceRet = v_Future.execute();  // 阻塞等待任务完成
                if ( v_Future.isSucceed() )
                {
                    v_Result.setResult(v_ExceRet);
                }
                else
                {
                    throw v_Future.getException();
                }
            }
            else
            {
                Object v_ExceRet = Void.TYPE;
                if ( Void.TYPE.equals(this.callMethodObject.getReturnType()) )
                {
                    this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
                }
                else
                {
                    v_ExceRet = this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
                }
                
                this.generateReturn(io_Context ,v_ExceRet);
                v_Result.setResult(v_ExceRet);
            }
            
            this.refreshReturn(io_Context ,v_Result.getResult());
            this.refreshStatus(io_Context ,v_Result.getStatus());
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result;
        }
        catch (Exception exce)
        {
            if ( exce instanceof TimeoutException )
            {
                v_Result.setTimeout((TimeoutException) exce);
            }
            else if ( exce.getCause() instanceof TimeoutException )
            {
                v_Result.setTimeout((TimeoutException) exce.getCause());
            }
            else
            {
                v_Result.setException(exce);
            }
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
    }
    
    
    /**
     * 超时异步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     * @version     v2.0  2025-03-16  从 CompletableFuture 升级为 TimeoutConfig
     *
     * @param i_Timeout      执行超时时长（单位：毫秒）
     * @param io_Context     上下文类型的变量信息
     * @param i_CallObject   执行方法的对象实例
     * @param i_ParamValues  执行方法的参数
     * @return
     */
    private TimeoutConfig<Object> executeAsync(Long i_Timeout ,Map<String ,Object> io_Context ,Object i_CallObject ,Object [] i_ParamValues)
    {
        return new TimeoutConfig<Object>(i_Timeout).future(() -> 
        {
            try 
            {
                if ( Void.TYPE.equals(this.callMethodObject.getReturnType()) )
                {
                    this.callMethodObject.invoke(i_CallObject ,i_ParamValues);
                    return this.generateReturn(io_Context ,Void.TYPE);
                }
                else
                {
                    return this.generateReturn(io_Context ,this.callMethodObject.invoke(i_CallObject ,i_ParamValues));
                }
            } 
            catch (Exception exce) 
            {
                throw new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] executeAsync error", exce);
            }
        });
    }
    
    
    /*
    private void call_executeAsync_stop()
    {
        CompletableFuture<Object> v_Future = this.executeAsync_stop(v_Timeout ,io_Context ,v_CallObject ,v_ParamValues);
        
        // 处理任务结果或异常
        v_Future.whenComplete((i_Result ,i_Exce) -> {
            if ( i_Exce != null ) 
            {
                $Logger.error(i_Exce ,i_Exce.getMessage());
            }
            else
            {
                v_Result.setResult(i_Result);
            }
        });
        
       v_Future.get(); // 阻塞等待任务完成
    }
    */
    
    
    /**
     * 超时异步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @param i_Timeout      执行超时时长（单位：毫秒）
     * @param io_Context     上下文类型的变量信息
     * @param i_CallObject   执行方法的对象实例
     * @param i_ParamValues  执行方法的参数
     * @return
     */
    /*
    private CompletableFuture<Object> executeAsync_stop(Long i_Timeout ,Map<String ,Object> io_Context ,Object i_CallObject ,Object [] i_ParamValues) 
    {
        Supplier<Object> v_Task = () -> {
            try 
            {
                if ( Void.TYPE.equals(this.callMethodObject.getReturnType()) )
                {
                    this.callMethodObject.invoke(i_CallObject ,i_ParamValues);
                    return this.generateReturn(io_Context ,Void.TYPE);
                }
                else
                {
                    return this.generateReturn(io_Context ,this.callMethodObject.invoke(i_CallObject ,i_ParamValues));
                }
            } 
            catch (Exception exce) 
            {
                throw new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] executeAsync error", exce);
            }
        };
        
        CompletableFuture<Object> v_Future    = new CompletableFuture<Object>();      // 用于执行任务
        ScheduledExecutorService  v_Scheduler = Executors.newScheduledThreadPool(1);  // 用于超时控制

        // 提交任务到 CompletableFuture
        CompletableFuture.runAsync(() -> {
            try 
            {
                Object v_ExceRet = v_Task.get();   // 执行任务
                v_Future.complete(v_ExceRet);      // 任务完成，设置结果
            } 
            catch (Exception e) 
            {
                v_Future.completeExceptionally(e); // 任务异常，设置异常
            }
        });

        // 设置超时任务
        ScheduledFuture<?> v_TimeoutTask = v_Scheduler.schedule(() -> {
            if ( !v_Future.isDone() ) 
            {
                // 超时后抛出异常
                v_Future.completeExceptionally(new TimeoutException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout[" + this.timeout + "]")); 
                v_Future.cancel(true);
            }
        }, i_Timeout ,TimeUnit.MILLISECONDS);

        // 当任务完成时，取消超时任务
        v_Future.whenComplete((result, ex) -> {
            v_TimeoutTask.cancel(true);   // 取消超时任务
            v_Scheduler.shutdown();       // 关闭调度器
        });

        return v_Future;
    }
    */
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        return io_ExecuteObject;
    }
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        return io_Params;
    }
    
    
    /**
     * 执行成功时，对执行结果的处理
     * 
     * 注：此时执行结果还没有保存到上下文中
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteReturn  执行结果。已用NodeConfig自己的力量获取了执行结果。
     * @return
     */
    public Object generateReturn(Map<String ,Object> io_Context ,Object io_ExecuteReturn)
    {
        return io_ExecuteReturn;
    }
    
    
    /**
     * 手动初始化。
     * 因预先做了初始动作，可加速执行用时，统计数据精准。
     * 
     * 注：初始未成功时，会抛出异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     */
    public synchronized void init(Map<String ,Object> io_Context)
    {
        if ( this.isInit )
        {
            return;
        }
        
        if ( Help.isNull(this.callXID) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.getCallXID() + "] is not find.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            try
            {
                for (int x=0; x<v_ParamValues.length; x++)
                {
                    NodeParam v_NodeParam  = this.callParams.get(x);
                    v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.gatValueClass() ,v_NodeParam.gatValueDefaultObject() ,io_Context);
                }
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
                throw new RuntimeException(exce);
            }
        }
        else
        {
            v_ParamValues = new Object[0];
        }
        
        this.init(v_CallObject ,v_ParamValues);
    }
    
    
    /**
     * 自动初始化。
     * 用于匹配执行方法。仅在首次或关键成员属性改变时才执行。
     * 
     * 注：初始未成功时，会抛出异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_CallObject   执行对象的实例
     * @param i_ParamValues  方法的执行参数
     */
    private synchronized void init(Object i_CallObject ,Object [] i_ParamValues)
    {
        if ( this.isInit )
        {
            return;
        }
        
        if ( Help.isNull(this.callMethod) )
        {
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod is null.");
        }
        
        List<Method> v_CallMethods = MethodReflect.getMethods(i_CallObject.getClass() ,this.callMethod ,i_ParamValues.length);
        if ( Help.isNull(v_CallMethods) )
        {
            throw new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMethod + "(" + i_ParamValues.length + ")] is not find.");
        }
        
        if ( v_CallMethods.size() == 1 )
        {
            this.isInit           = true;
            this.callMethodObject = v_CallMethods.get(0);
        }
        else
        {
            // 参照 MethodReflect.getMethodsBest() 方法配对到最佳方法
            Map<Integer ,Method> v_Bests = new HashMap<Integer ,Method>();
            for (Method v_Method : v_CallMethods)
            {
                int         v_BestValue    = 0;
                Class<?> [] v_MPClassTypes = v_Method.getParameterTypes();
                for (int y=v_MPClassTypes.length - 1; y>=0; y--)
                {
                    Class<?> v_ParamValueClass = null;
                    if ( i_ParamValues[y] == null )
                    {
                        v_ParamValueClass = this.callParams.get(y).gatValueClass();
                    }
                    else
                    {
                        v_ParamValueClass = i_ParamValues[y].getClass();
                    }
                    
                    if ( v_ParamValueClass == null )
                    {
                        // Nothing.
                    }
                    else if ( v_ParamValueClass == v_MPClassTypes[y] )                                  // 3级：完全配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 3;
                    }
                    else if ( v_MPClassTypes[y] == Object.class )                                       // 1级：模糊配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y);
                    }
                    else if ( MethodReflect.isExtendImplement(v_ParamValueClass ,v_MPClassTypes[y]) )   // 2级：继承配对 或 接口实现配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 2;
                    }
                }
                
                if ( v_BestValue > 0 )
                {
                    v_Bests.put(v_BestValue ,v_Method);
                }
            }
            
            if ( !Help.isNull(v_Bests) )
            {
                v_CallMethods.clear();
                v_CallMethods = Help.toList(Help.toReverse(v_Bests));
                v_Bests.clear();
                
                if ( v_CallMethods.size() == 1 )
                {
                    this.isInit           = true;
                    this.callMethodObject = v_CallMethods.get(0);
                }
                else
                {
                    throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMethod + "(" + i_ParamValues.length + ")] is find " + v_CallMethods.size() + " methods.");
                }
            }
            else
            {
                throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMethod + "(" + i_ParamValues.length + ")] is not find.");
            }
        }
    }
    
    
    /**
     * 获取：执行对象的XID
     */
    public String getCallXID()
    {
        if ( Help.isNull(this.callXID) )
        {
            return null;
        }
        else
        {
            return DBSQL.$Placeholder + this.callXID;
        }
    }

    
    /**
     * 设置：执行对象的XID
     * 
     * @param i_CallXID 执行对象的XID
     */
    public void setCallXID(String i_CallXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.callXID = ValueHelp.standardValueID(i_CallXID);
        this.isInit  = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：执行方法名称
     */
    public String getCallMethod()
    {
        return callMethod;
    }

    
    /**
     * 设置：执行方法名称
     * 
     * @param i_CallMethod 执行方法名称
     */
    public void setCallMethod(String i_CallMethod)
    {
        this.callMethod = i_CallMethod;
        this.isInit     = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 添加执行方法的参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     *
     * @param i_Param  执行方法的参数
     */
    public void setCallParam(NodeParam i_Param)
    {
        synchronized (this)
        {
            if ( this.callParams == null )
            {
                this.callParams = new ArrayList<NodeParam>();
            }
        }
        
        this.callParams.add(i_Param);
        this.isInit = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：执行方法的参数
     */
    public List<NodeParam> getCallParams()
    {
        return callParams;
    }

    
    /**
     * 设置：执行方法的参数
     * 
     * @param i_CallParams 执行方法的参数
     */
    public void setCallParams(List<NodeParam> i_CallParams)
    {
        this.callParams = i_CallParams;
        this.isInit     = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 从上下文中获取运行时的超时时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Long gatTimeout(Map<String ,Object> i_Context) throws Exception
    {
        Long v_Timeout = null;
        if ( Help.isNumber(this.timeout) )
        {
            v_Timeout = Long.valueOf(this.timeout);
        }
        else
        {
            v_Timeout = (Long) ValueHelp.getValue(this.timeout ,Long.class ,0L ,i_Context);
        }
        
        return v_Timeout;
    }

    
    /**
     * 获取：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getTimeout()
    {
        return timeout;
    }

    
    /**
     * 设置：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_Timeout 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setTimeout(String i_Timeout)
    {
        if ( Help.isNull(i_Timeout) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_Timeout) )
        {
            Long v_Timeout = Long.valueOf(i_Timeout);
            if ( v_Timeout < 0L )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.timeout = i_Timeout.trim();
        }
        else
        {
            this.timeout = ValueHelp.standardRefID(i_Timeout);
        }
    }
    
    
    /**
     * 获取：向上下文中赋值
     */
    public String getContext()
    {
        return context;
    }

    
    /**
     * 设置：向上下文中赋值
     * 
     * @param i_Context 向上下文中赋值
     */
    public void setContext(String i_Context)
    {
        this.context = i_Context;
    }


    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Node.getXmlName();
    }
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
     * @version     v1.0
     *
     * @param io_Xml         XML内容的缓存区
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_Level1       单级层级的空格间隔
     * @param i_LevelN       N级层级的空格间隔
     * @param i_SuperTreeID  父级树ID
     * @param i_TreeID       当前树ID
     */
    public void toXmlContent(StringBuilder io_Xml ,int i_Level ,String i_Level1 ,String i_LevelN ,String i_SuperTreeID ,String i_TreeID)
    {
        if ( !Help.isNull(this.callXID) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("callXID" ,this.getCallXID()));
        }
        if ( !Help.isNull(this.callMethod) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("callMethod" ,this.callMethod));
        }
        if ( !Help.isNull(this.callParams) )
        {
            for (NodeParam v_Param : this.callParams)
            {
                io_Xml.append(v_Param.toXml(i_Level + 1 ,i_TreeID));
            }
        }
    }


    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    public String toXml(int i_Level ,String i_SuperTreeID)
    {
        String v_TreeID = this.getTreeID(i_SuperTreeID);
        if ( this.getTreeIDs().size() >= 2 )
        {
            String v_MinTreeID = this.getMinTreeID();
            if ( !v_TreeID.equals(v_MinTreeID) )
            {
                // 不等于最小的树ID，不生成Xml内容。防止重复生成
                return "";
            }
        }
        
        StringBuilder v_Xml    = new StringBuilder();
        String        v_Level1 = "    ";
        String        v_LevelN = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName  = toXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        // 生成或写入个性化的XML内容
        toXmlContent(v_Xml ,i_Level ,v_Level1 ,v_LevelN ,i_SuperTreeID ,v_TreeID);
        
        if ( !Help.isNull(this.timeout) && !"0".equals(this.timeout) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("timeout" ,this.timeout));
        }
        if ( !Help.isNull(this.context) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("context" ,this.context));
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
        }
        if ( !Help.isNull(this.statusID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("statusID" ,this.statusID));
        }
        if ( !Help.isNull(this.route.getSucceeds()) 
          || !Help.isNull(this.route.getExceptions()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 成功路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (RouteItem v_RouteItem : this.route.getSucceeds())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Succeed.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Succeed.getXmlName()));
                }
            }
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (RouteItem v_RouteItem : this.route.getExceptions())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Error.getXmlName()));
                }
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toEnd("route"));
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        return v_Xml.toString();
    }
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        v_Builder.append(DBSQL.$Placeholder);
        if ( !Help.isNull(this.callXID) )
        {
            v_Builder.append(this.callXID);
            if ( XJava.getObject(this.callXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( !Help.isNull(this.callMethod) )
        {
            v_Builder.append(this.callMethod);
            this.init(i_Context);
            if ( this.callMethodObject == null )
            {
                v_Builder.append(" not find");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("(");
        
        if ( !Help.isNull(this.callParams) )
        {
            for (int x=0; x<this.callParams.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ,");
                }
                v_Builder.append(this.callParams.get(x).toString(i_Context));
            }
        }
        
        v_Builder.append(")");
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        v_Builder.append(DBSQL.$Placeholder);
        if ( !Help.isNull(this.callXID) )
        {
            v_Builder.append(this.callXID);
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( !Help.isNull(this.callMethod) )
        {
            v_Builder.append(this.callMethod);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("(");
        
        if ( !Help.isNull(this.callParams) )
        {
            for (int x=0; x<this.callParams.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ,");
                }
                v_Builder.append(this.callParams.get(x).toString());
            }
        }
        
        v_Builder.append(")");
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new NodeConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        NodeConfig v_Clone = new NodeConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.callXID    = this.callXID;
        v_Clone.callMethod = this.callMethod; 
        v_Clone.timeout    = this.timeout;
        v_Clone.context    = this.context;
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                v_Clone.callParams.add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone NodeConfig xid is null.");
        }
        
        NodeConfig v_Clone = (NodeConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.callXID    = this.callXID;
        v_Clone.callMethod = this.callMethod; 
        v_Clone.timeout    = this.timeout;
        v_Clone.context    = this.context;
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                NodeParam v_CloneNodeParam = new NodeParam();
                v_NodeParam.clone(v_CloneNodeParam ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.callParams.add(v_CloneNodeParam);
            }
        }
    }


    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone NodeConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        NodeConfig                  v_Clone      = new NodeConfig();
        
        if ( v_Version.booleanValue() )
        {
            this.clone(v_Clone ,v_Version.getParamStr() ,XIDVersion + (v_Version.getParamInt() + 1) ,""         ,v_XIDObjects);
        }
        else
        {
            this.clone(v_Clone ,""                      ,""                                         ,XIDVersion ,v_XIDObjects);
        }
        
        v_XIDObjects.clear();
        v_XIDObjects = null;
        return v_Clone;
    }
    
}
