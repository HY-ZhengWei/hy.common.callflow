package org.hy.common.callflow.nesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 并发元素：并发配置信息
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-03-20
 * @version     v1.0
 *              v2.0  2025-06-09  添加：上下文已解释完成的占位符，使其支持面向对象的占位符。
 *              v3.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v4.0  2025-09-26  迁移：静态检查
 */
public class MTConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(MTConfig.class);
    
    

    /** 并发项的集合 */
    private List<MTItem>                  mtitems;
    
    /** 非并发同时执行，而是一个一个的执行 */
    private Boolean                       oneByOne;
    
    /** 每并发项的间隔多少时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String                        waitTime;
    
    
    
    public MTConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public MTConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.mtitems  = new ArrayList<MTItem>();
        this.oneByOne = false;
        this.waitTime = "0";
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        if ( Help.isNull(this.getMtitems()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].Mtitems is null.");
            return false;
        }
        
        int x = 0;
        for (MTItem v_MTItem : this.getMtitems())
        {
            // 并发元素必须有子编排的XID
            if ( Help.isNull(v_MTItem.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].callFlowXID is null.");
                return false;
            }
            
            // 并发元素的不应自己并发自己，递归应采用自引用方式实现
            if ( v_MTItem.getCallFlowXID().equals(this.getXJavaID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].callFlowXID[" + v_MTItem.getCallFlowXID() + "] cannot nest itself.");
                return false;
            }
            
            if ( !Help.isNull(v_MTItem.getValueXIDA()) )
            {
                // 当有比较值A时，比较器不应为空
                if ( v_MTItem.getComparer() == null )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].comparer is null.");
                    return false;
                }
                
                if ( !ValueHelp.isRefID(v_MTItem.getValueXIDA()) )
                {
                    if ( Help.isNull(v_MTItem.getValueClass()) )
                    {
                        // 条件项的比值为数值类型时，其类型应不会空
                        io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].valueXIDA is Normal type ,but valueClass is null.");
                        return false;
                    }
                }
            }
            
            if ( !Help.isNull(v_MTItem.getValueXIDB()) )
            {
                // 当有比较值B时，比较值A不应为空
                if ( Help.isNull(v_MTItem.getValueXIDA()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].valueXIDA is null.");
                    return false;
                }
                
                // 当有比较值B时，比较器不应为空
                if ( v_MTItem.getComparer() == null )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].comparer is null.");
                    return false;
                }
                
                if ( !ValueHelp.isRefID(v_MTItem.getValueXIDB()) )
                {
                    if ( Help.isNull(v_MTItem.getValueClass()) )
                    {
                        // 条件项的比值为数值类型时，其类型应不会空
                        io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].[" + x + "].valueXIDB is Normal type ,but valueClass is null.");
                        return false;
                    }
                }
            }
            
            x++;
        }
        
        return true;
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.MT.getValue();
    }
    
    
    
    /**
     * 获取一个新的并发项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @return
     */
    public MTItem getItem()
    {
        return new MTItem();
    }
    
    
    
    /**
     * 添加一个并发项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     */
    public void setItem(MTItem i_MTItem) 
    {
        this.mtitems.add(i_MTItem);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：并发项的集合
     */
    public List<MTItem> getMtitems()
    {
        return mtitems;
    }

    
    
    /**
     * 获取：非并发同时执行，而是一个一个的执行
     */
    public Boolean getOneByOne()
    {
        return oneByOne;
    }


    
    /**
     * 设置：非并发同时执行，而是一个一个的执行
     * 
     * @param i_OneByOne 非并发同时执行，而是一个一个的执行
     */
    public void setOneByOne(Boolean i_OneByOne)
    {
        if ( i_OneByOne == null )
        {
            this.oneByOne = false;
        }
        else
        {
            this.oneByOne = i_OneByOne;
        }
    }
    
    
    
    /**
     * 获取：等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getWaitTime()
    {
        return waitTime;
    }


    
    /**
     * 设置：等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_WaitTime 等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setWaitTime(String i_WaitTime)
    {
        if ( Help.isNull(i_WaitTime) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s WaitTime is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_WaitTime) )
        {
            Long v_WaitTime = Long.valueOf(i_WaitTime);
            if ( v_WaitTime < 0L )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s WaitTime Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.waitTime = i_WaitTime.trim();
        }
        else
        {
            this.waitTime = ValueHelp.standardRefID(i_WaitTime);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 设置：并发项的集合
     * 
     * @param i_Mtitems 并发项的集合
     */
    public void setMtitems(List<MTItem> i_Mtitems)
    {
        if ( i_Mtitems == null )
        {
            this.mtitems.clear();
        }
        else
        {
            this.mtitems = i_Mtitems;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        try
        {
            List<MTExecuteResult> v_MTItemResults = new ArrayList<MTExecuteResult>();
            boolean               v_IsOrderBy     = false;
            Exception             v_Exception     = null;
            
            if ( !this.handleContext(io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            // 先判定允许执行的并发项
            if ( !Help.isNull(this.mtitems) )
            {
                for (int x=0; x<this.mtitems.size(); x++)
                {
                    MTItem v_Item = this.mtitems.get(x);
                    
                    if ( Help.isNull(v_Item.getValueXIDA()) 
                      || v_Item.getComparer() == null
                      || v_Item.allow(io_Context) )
                    {
                        if ( Help.isNull(v_Item.gatCallFlowXID()) )
                        {
                            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID is null."));
                            this.refreshStatus(io_Context ,v_Result.getStatus());
                            return v_Result;
                        }
                        
                        // 获取执行对象
                        Object v_CallObject = XJava.getObject(v_Item.gatCallFlowXID());
                        if ( v_CallObject == null )
                        {
                            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + v_Item.gatCallFlowXID() + "] is not find."));
                            this.refreshStatus(io_Context ,v_Result.getStatus());
                            return v_Result;
                        }
                        // 执行对象不是编排元素
                        if ( !MethodReflect.isExtendImplement(v_CallObject ,ExecuteElement.class) )
                        {
                            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + v_Item.gatCallFlowXID() + "] is not ExecuteElement."));
                            this.refreshStatus(io_Context ,v_Result.getStatus());
                            return v_Result;
                        }
                        // 超时时长
                        MTExecuteResult v_MTExecuteResult = new MTExecuteResult(x ,v_Item ,(ExecuteElement) v_CallObject ,v_Item.gatTimeout(io_Context));
                        v_MTItemResults.add(v_MTExecuteResult);
                        if ( !this.oneByOne && v_MTExecuteResult.getTimeout() < Long.MAX_VALUE )
                        {
                            v_IsOrderBy = true;
                        }
                    }
                }
            }
            
            if ( !Help.isNull(v_MTItemResults) )
            {
                // 优化执行顺序
                if ( v_IsOrderBy )
                {
                    Help.toSort(v_MTItemResults ,"timeout NumAsc");
                }
                
                // 生成并发项的独立上下文
                for (MTExecuteResult v_MTItemResult : v_MTItemResults)
                {
                    Map<String ,Object> v_MTItemContext = new HashMap<String ,Object>();
                    v_MTItemContext.putAll(io_Context);
                    
                    if ( !Help.isNull(v_MTItemResult.getMtItem().getContext()) )
                    {
                        String v_Context = ValueHelp.replaceByContext(v_MTItemResult.getMtItem().getContext() ,v_MTItemResult.getMtItem().getContextPlaceholders() ,v_MTItemContext);
                        Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_Context ,Map.class ,null ,null);
                        v_MTItemContext.putAll(v_ContextMap);
                        v_ContextMap.clear();
                        v_ContextMap = null;
                    }
                    
                    v_MTItemResult.setContext(v_MTItemContext);
                }
                
                // 计算并发项间的间隔等待时长
                Long v_WaitTime = null;
                if ( Help.isNumber(this.waitTime) )
                {
                    v_WaitTime = Long.valueOf(this.waitTime);
                }
                else
                {
                    v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,0L ,io_Context);
                }
                
                // 执行并发项
                long v_TimeoutTotal = 0;
                for (int x=0; x<v_MTItemResults.size(); x++)
                {
                    MTExecuteResult v_MTItemResult = v_MTItemResults.get(x);
                    long v_Timeout = v_MTItemResult.getTimeout();
                    if ( v_Timeout == Long.MAX_VALUE )
                    {
                        v_Timeout = 0L;
                    }
                    else if ( !this.oneByOne )
                    {
                        if ( v_Timeout > v_TimeoutTotal )
                        {
                            v_Timeout -= v_TimeoutTotal;
                            v_TimeoutTotal += v_Timeout;
                        }
                        else
                        {
                            v_Timeout = 1L;
                        }
                    }
                    
                    TimeoutConfig<ExecuteResult> v_Future = this.executeAsync(v_Timeout ,v_MTItemResult.getContext() ,v_MTItemResult.getCallObject());
                    v_Future.executeAsync();
                    v_MTItemResult.setTimeoutConfig(v_Future);
                    
                    if ( this.oneByOne )
                    {
                        ExecuteResult v_ExecResult = v_Future.get();
                        v_MTItemResult.setResult(v_ExecResult);
                        if ( !v_ExecResult.isSuccess() )
                        {
                            if ( v_Exception == null )
                            {
                                v_Exception = v_ExecResult.getException();
                                break;
                            }
                        }
                        else 
                        {
                            this.refreshReturn(io_Context ,v_MTItemResult.getContext() ,v_MTItemResult.getMtItem().getReturnID());
                        }
                    }
                    
                    // 并发项间的间隔等待时长
                    if ( v_WaitTime > 0 )
                    {
                        if ( x < v_MTItemResults.size() - 1 )
                        {
                            Thread.sleep(v_WaitTime ,0);
                        }
                    }
                }
                
                if ( !this.oneByOne )
                {
                    // 并发项等待
                    for (MTExecuteResult v_MTItemResult : v_MTItemResults)
                    {
                        ExecuteResult v_ExecResult = v_MTItemResult.getTimeoutConfig().get();
                        v_MTItemResult.setResult(v_ExecResult);
                        if ( !v_ExecResult.isSuccess() )
                        {
                            if ( v_Exception == null )
                            {
                                v_Exception = v_ExecResult.getException();
                            }
                        }
                        else
                        {
                            this.refreshReturn(io_Context ,v_MTItemResult.getContext() ,v_MTItemResult.getMtItem().getReturnID());
                        }
                    }
                }
            }
            
            if ( v_Exception != null )
            {
                v_Result.setException(v_Exception ,v_MTItemResults);
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            else
            {
                v_Result.setResult(v_MTItemResults);
                this.refreshReturn(io_Context ,v_Result.getResult());
                this.refreshStatus(io_Context ,v_Result.getStatus());
                this.success(Date.getTimeNano() - v_BeginTime);
                return v_Result;
            }
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
    }
    
    
    /**
     * 超时异步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_Timeout      执行超时时长（单位：毫秒）
     * @param io_Context     上下文类型的变量信息
     * @param i_CallObject   执行方法的对象实例
     * @return
     */
    private TimeoutConfig<ExecuteResult> executeAsync(Long i_Timeout ,Map<String ,Object> io_Context ,ExecuteElement i_CallObject)
    {
        return new TimeoutConfig<ExecuteResult>(i_Timeout).future(() -> 
        {
            try 
            {
                return CallFlow.execute(i_CallObject ,io_Context);
            } 
            catch (Exception exce) 
            {
                throw new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] executeAsync error", exce);
            }
        });
    }
    
    
    /**
     * 刷新返回值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-25
     * @version     v1.0
     *
     * @param io_Context       上下文类型的变量信息
     * @param i_MTItemContext  并发项的独立上下文
     * @param i_ReturnID       返回值变量名称ID
     */
    private void refreshReturn(Map<String ,Object> io_Context ,Map<String ,Object> i_MTItemContext ,String i_ReturnID)
    {
        if ( !Help.isNull(i_ReturnID) && io_Context != null )
        {
            io_Context.put(i_ReturnID ,CallFlow.getLastResult(i_MTItemContext).getResult());
        }
    }
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
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
        
        StringBuilder v_Xml      = new StringBuilder();
        String        v_Level1   = "    ";
        String        v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName    = ElementType.MT.getXmlName();
        String        v_NewSpace = "\n" + v_LevelN + v_Level1;
        
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
            if ( this.oneByOne != null && this.oneByOne )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("oneByOne" ,this.oneByOne));
            }
            if ( !Help.isNull(this.waitTime) && !"0".equals(this.waitTime) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("waitTime" ,this.waitTime));
            }
            
            if ( !Help.isNull(this.mtitems) )
            {
                for (MTItem v_Item : this.mtitems)
                {
                    v_Xml.append(v_Item.toXml(i_Level + 1 ,v_TreeID));
                }
            }
            if ( !Help.isNull(this.returnID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("returnID" ,this.returnID));
            }
            if ( !Help.isNull(this.statusID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("statusID" ,this.statusID));
            }
            
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toBegin("route"));
                
                // 成功路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,RouteType.Error.getXmlName()   ,i_Level ,v_TreeID ,i_ExportType);
                
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("route"));
            }
            
            this.toXmlExecute(v_Xml ,v_NewSpace);
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
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.mtitems) )
        {
            v_Builder.append("MT count：").append(this.mtitems.size()).append("(");
            
            for (int x=0; x<this.mtitems.size(); x++)
            {
                MTItem v_Item = this.mtitems.get(x);
                if ( x >= 1 )
                {
                    v_Builder.append("、");
                }
                v_Builder.append(v_Item.getCallFlowXID());
            }
            
            v_Builder.append(")");
        }
        else
        {
            v_Builder.append("MT count：0");
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.mtitems) )
        {
            v_Builder.append("MT count：").append(this.mtitems.size()).append("(");
            
            for (int x=0; x<this.mtitems.size(); x++)
            {
                MTItem v_Item = this.mtitems.get(x);
                if ( x >= 1 )
                {
                    v_Builder.append("、");
                }
                v_Builder.append(v_Item.getCallFlowXID());
            }
            
            v_Builder.append(")");
        }
        else
        {
            v_Builder.append("MT count：0");
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new MTConfig();
    }
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        MTConfig v_Clone = new MTConfig();
        
        this.cloneMyOnly(v_Clone);
        if ( !Help.isNull(this.mtitems) )
        {
            for (MTItem v_Item : this.mtitems)
            {
                v_Clone.setItem((MTItem) v_Item.cloneMyOnly());
            }
        }
        
        v_Clone.oneByOne = this.oneByOne;
        v_Clone.waitTime = this.waitTime;
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
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
            throw new NullPointerException("Clone MTConfig xid is null.");
        }
        
        MTConfig v_Clone = (MTConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        if ( !Help.isNull(this.mtitems) )
        {
            for (MTItem v_Item : this.mtitems)
            {
                MTItem v_CloneItem = new MTItem();
                v_Item.clone(v_CloneItem ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.setItem(v_CloneItem);
            }
        }
        
        v_Clone.oneByOne = this.oneByOne;
        v_Clone.waitTime = this.waitTime;
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
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
            throw new NullPointerException("Clone MTConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        MTConfig                    v_Clone      = new MTConfig();
        
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
