package org.hy.common.callflow.nesting;

import java.util.HashMap;
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
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.timeout.TimeoutConfig;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 嵌套元素：子编排的配置信息。
 * 
 * 优点：嵌套可以实现递归。
 * 优点：嵌套使编排流程可以共用。嵌套配置元素不建议共用。
 * 
 * 注：不建议嵌套配置共用，即使两个编排调用相同的执行方法也建议配置两个嵌套配置，使嵌套配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享嵌套配置下，无法做到升级时百分百的正确。
 *    原因2是在共享嵌套配置时，统计方面也无法独立区分出来。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-28
 * @version     v1.0
 *              v2.0  2025-06-10  添加：向上下文中赋值及占位符支持面向对象
 *              v3.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v3.1  2025-08-27  修正：三层以上的多组嵌套组成的复合编排，在执行顺序上混乱。发现人：王雨墨
 */
public class NestingConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(NestingConfig.class);
    
    

    /** 子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式 */
    private String callFlowXID;
    
    /** 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String timeout;
    
    
    
    public NestingConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public NestingConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
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
        return ElementType.Nesting.getValue();
    }
    


    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime    = this.request();
        Integer       v_NestingLevel = CallFlow.getNestingLevel(io_Context);
        ExecuteResult v_NestingBegin = new ExecuteResult(v_NestingLevel ,this.getTreeID(i_SuperTreeID)     ,this.xid ,this.toString(io_Context) + " BEGIN ");
        ExecuteResult v_NestingEnd   = new ExecuteResult(v_NestingLevel ,v_NestingBegin.getExecuteTreeID() ,this.xid ,this.toString(io_Context) + " END ");
        this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
        
        if ( Help.isNull(this.callFlowXID) )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID is null."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        
        if ( !this.handleContext(io_Context ,v_NestingBegin) )
        {
            return v_NestingBegin;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callFlowXID);
        if ( v_CallObject == null )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.callFlowXID + "] is not find."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        // 执行对象不是编排元素
        if ( !MethodReflect.isExtendImplement(v_CallObject ,ExecuteElement.class) )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.callFlowXID + "] is not ExecuteElement."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        // 禁止嵌套直接套嵌套，做无用功
        /*
        if ( v_CallObject instanceof NestingConfig )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.callFlowXID + "] is not NodeConfig or Condition."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        */
        
        Long v_Timeout = null;
        try
        {
            v_Timeout = this.gatTimeout(io_Context);
        }
        catch (Exception exce)
        {
            v_NestingBegin.setException(exce);
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        
        ExecuteResult v_SuperFirstResult = CallFlow.getFirstResult(io_Context);  // 备份父级首个执行结果 
        ExecuteResult v_SuperLastResult  = CallFlow.getLastResult( io_Context);  // 备份父级最后执行结果 
        
        // 将父级的最后结果与子级的首个结果相关联
        if ( v_SuperLastResult != null )
        {
            v_NestingBegin.setPrevious(v_SuperLastResult);
        }
        else
        {
            // 嵌套是主编排的首个执行对象
            v_NestingBegin.setPrevious(null);
        }
        
        synchronized ( this )
        {
            // 最后执行的嵌套对象（自己），为已编排生成树ID用
            io_Context.put(CallFlow.$NestingLevel ,v_NestingLevel + 1);     // 嵌套层级++，并且它必须在前
            CallFlow.putLastNestingBeginResult(io_Context ,v_NestingBegin);
        }
        
        ExecuteElement   v_CallFlow    = (ExecuteElement) v_CallObject;
        ExecuteResult    v_ExceRet     = null;
        TimeoutException v_TimeoutExce = null;
        if ( v_Timeout > 0L )
        {
            TimeoutConfig<ExecuteResult> v_Future = this.executeAsync(v_Timeout ,io_Context ,v_CallFlow);
            v_ExceRet = v_Future.execute();  // 阻塞等待任务完成
            if ( v_Future.isError() )
            {
                if ( v_Future.getException() instanceof TimeoutException )
                {
                    v_TimeoutExce = (TimeoutException) v_Future.getException();
                }
                else
                {
                    if ( !CallFlow.getExecuteIsError(io_Context) )
                    {
                        $Logger.error("应当永不会被执行，如果执行了，程序就有Bug" ,v_Future.getException());
                    }
                }
            }
        }
        else
        {
            v_ExceRet = CallFlow.execute(v_CallFlow ,io_Context ,CallFlow.getExecuteEvent(io_Context));
        }
        
        // 清除子编排中的 “真返回” 标记
        CallFlow.clearTrueReturn(io_Context);
        
        synchronized ( this )
        {
            // 还原：原始父级的首个执行对象的结果
            if ( v_SuperFirstResult != null )
            {
                io_Context.put(CallFlow.$FirstExecuteResult ,v_SuperFirstResult);
            }
            io_Context.put(CallFlow.$NestingLevel ,v_NestingLevel);
        }
        
        // 超时异常
        if ( v_TimeoutExce != null )
        {
            ExecuteResult v_ErrorResult = null;
            do
            {
                v_ErrorResult = CallFlow.getErrorResult(io_Context);
                if ( v_ErrorResult == null )
                {
                    v_ErrorResult = CallFlow.getLastResult(io_Context);
                }
                
                if ( v_ErrorResult == null )
                {
                    try
                    {
                        // 子编排中的元素有可能会慢于主编排的嵌套返回，所以要等一下
                        Thread.sleep(10L);
                    }
                    catch (InterruptedException e)
                    {
                        // Nothing.
                    }
                }
            }
            while ( v_ErrorResult == null );
            
            // 必须清除子编排元素引发的异常。如Thread.sleep()方法的InterruptedException异常
            // 使嵌套元素来主导异常的走向，拥有异常的发表权和控制权
            CallFlow.clearError(io_Context);   
            
            v_ErrorResult.addNext(v_NestingEnd);
            v_NestingEnd.setPrevious(v_ErrorResult);
            v_NestingEnd.setTimeout(v_TimeoutExce);
            
            v_NestingBegin.setTimeout(v_TimeoutExce);
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
        }
        // 运行时异常
        else if ( CallFlow.getExecuteIsError(io_Context) )
        {
            // 不能改写为异常的元素的执行XID和树ID。嵌套就是一个相对独立的元素
            // 如果将子嵌套的执行XID和树ID拿到本编排流程中，是无法定位
            // 子编排错了，就是本编排流程的嵌套对象错了。
            ExecuteResult v_ErrorResult = CallFlow.getErrorResult(io_Context);
            v_ErrorResult.addNext(v_NestingEnd);
            v_NestingEnd.setPrevious( v_ErrorResult);
            v_NestingEnd.setException(v_ErrorResult.getException());
            
            v_NestingBegin.setException(v_ErrorResult.getException());
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
        }
        // 执行成功
        else
        {
            ExecuteResult v_LastResult = CallFlow.removeLastResult(io_Context ,v_NestingLevel + 1);
            v_LastResult.addNext(v_NestingEnd);                        // 子编排完成后的下一步关联到本层编排的嵌套配置
            v_NestingEnd.setPrevious(v_LastResult);                    // 关联最后执行对象的结果
            v_NestingEnd.setResult(v_ExceRet.getResult());             // 子编排的结果就是我的结果
            this.refreshReturn(io_Context ,v_ExceRet.getResult());     // 这里用 returnID 返回是的执行结果的原始信息，而不是ExecuteResult对象。
            this.refreshStatus(io_Context ,v_ExceRet.getStatus());     // 子编排的状态就是我的状态
            
            v_NestingBegin.setResult(v_ExceRet.getResult());
            this.success(Date.getTimeNano() - v_BeginTime);
        }
        
        return v_NestingBegin;
    }
    
    
    
    /**
     * 超时异步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param i_Timeout      执行超时时长（单位：毫秒）
     * @param io_Context     上下文类型的变量信息
     * @param i_CallObject   执行方法的对象实例
     * @param i_ParamValues  执行方法的参数
     * @return
     */
    private TimeoutConfig<ExecuteResult> executeAsync(Long i_Timeout ,Map<String ,Object> io_Context ,ExecuteElement i_CallFlow)
    {
        return new TimeoutConfig<ExecuteResult>(i_Timeout).future(() -> 
        {
            return CallFlow.execute(i_CallFlow ,io_Context ,CallFlow.getExecuteEvent(io_Context));
        });
    }
    

    
    /**
     * 获取：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return ValueHelp.standardRefID(this.callFlowXID);
    }


    
    /**
     * 设置：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     * 
     * @param i_CallFlowXID 子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public void setCallFlowXID(String i_CallFlowXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.callFlowXID = ValueHelp.standardValueID(i_CallFlowXID);
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
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *              v2.0  2025-08-15  添加：导出类型
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @param i_ExportType   导出类型
     * @return
     */
    @Override
    public String toXml(int i_Level ,String i_SuperTreeID ,ExportType i_ExportType)
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
        String        v_XName  = ElementType.Nesting.getXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            if ( ExportType.UI.equals(i_ExportType) )
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginThis(v_XName ,this.getXJavaID()));
            }
            else
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(  v_XName ,this.getXJavaID()));
            }
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level ,i_ExportType));
        
        if ( !ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.getCallFlowXID()) )
            {
                v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("callFlowXID" ,this.getCallFlowXID()));
            }
            if ( !Help.isNull(this.timeout) && !"0".equals(this.timeout) )
            {
                v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("timeout" ,this.timeout));
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
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,RouteType.Error.getXmlName()   ,i_Level ,v_TreeID ,i_ExportType);
                
                v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toEnd("route"));
            }
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        // 编排流图时，提升路由项的层次，同时独立输出每个路由项
        if ( ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                // 成功路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
            }
        }
        
        return v_Xml.toString();
    }


    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
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
        if ( !Help.isNull(this.callFlowXID) )
        {
            v_Builder.append(this.callFlowXID);
            if ( XJava.getObject(this.callFlowXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        v_Builder.append("execute(...)");
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
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
        if ( !Help.isNull(this.callFlowXID) )
        {
            v_Builder.append(this.callFlowXID);
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        v_Builder.append("execute(...)");
        
        return v_Builder.toString();
    }
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new NestingConfig();
    }
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        NestingConfig v_Clone = new NestingConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.callFlowXID = this.callFlowXID;
        v_Clone.timeout     = this.timeout;
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
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
            throw new NullPointerException("Clone NestingConfig xid is null.");
        }
        
        NestingConfig v_Clone = (NestingConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.callFlowXID = this.callFlowXID;
        v_Clone.timeout     = this.timeout;
    }
    
    
    /**
     * 深度克隆编排元素
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
            throw new NullPointerException("Clone NestingConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        NestingConfig               v_Clone      = new NestingConfig();
        
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
