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
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.timeout.TimeoutConfig;
import org.hy.common.xml.XJava;





/**
 * 并发元素：并发配置信息
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-03-20
 * @version     v1.0
 */
public class MTConfig extends ExecuteElement implements Cloneable
{

    /** 并发项的集合 */
    private List<MTItem> mtitems;
    
    
    
    public MTConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public MTConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.mtitems = new ArrayList<MTItem>();
    }
    
    
    
    /**
     * 执行元素的类型
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
                        if ( v_MTExecuteResult.getTimeout() < Long.MAX_VALUE )
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
                    v_MTItemResult.setContext(v_MTItemContext);
                }
                
                // 执行并发项
                long v_TimeoutTotal = 0;
                for (MTExecuteResult v_MTItemResult : v_MTItemResults)
                {
                    long v_Timeout = v_MTItemResult.getTimeout();
                    if ( v_Timeout == Long.MAX_VALUE )
                    {
                        v_Timeout = 0L;
                    }
                    else
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
                }
                
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
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-20
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
        String        v_XName  = ElementType.MT.getXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.mtitems) )
        {
            for (MTItem v_Item : this.mtitems)
            {
                v_Xml.append(v_Item.toXml(i_Level + 1 ,v_TreeID));
            }
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
        }
        
        if ( !Help.isNull(this.route.getSucceeds()) 
          || !Help.isNull(this.route.getFaileds())
          || !Help.isNull(this.route.getExceptions()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 真时的路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (RouteItem v_RouteItem : this.route.getSucceeds())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.If.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.If.getXmlName()));
                }
            }
            // 假时的路由
            if ( !Help.isNull(this.route.getFaileds()) )
            {
                for (RouteItem v_RouteItem : this.route.getFaileds())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Else.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Else.getXmlName()));
                }
            }
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (RouteItem v_RouteItem : this.route.getExceptions())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
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
