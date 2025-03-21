package org.hy.common.callflow.node;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
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
import org.hy.common.xml.log.Logger;





/**
 * 等待元素：等待配置信息
 * 
 * 注：不建议等待配置共用，即使两个编排调用相同的等待配置也建议配置两个等待配置，使等待配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享等待配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public class WaitConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(WaitConfig.class);
    
    

    /** 等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String  waitTime;
    
    /** 计数器（正整数，下标从1开始，执行等待后++）。可以是上下文变量、XID标识 */
    private String  counter;
    
    /** 计数器的最大值（正整数，允许counter等于最大值）。可以是数值、上下文变量、XID标识 */
    private String  counterMax;
    
    
    
    public WaitConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public WaitConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.waitTime = "0";
        this.counter  = CallFlow.$WaitCounter;
    }
    
    
    
    /**
     * 执行元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Wait.getValue();
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
     * 获取：计数器（正整数，下标从1开始，执行等待后++）。可以是上下文变量、XID标识
     */
    public String getCounter()
    {
        return counter;
    }

    
    
    /**
     * 设置：计数器（正整数，下标从1开始，执行等待后++）。可以是上下文变量、XID标识
     * 
     * @param i_Counter 计数器（正整数，下标从1开始，执行等待后++）。可以是上下文变量、XID标识
     */
    public void setCounter(String i_Counter)
    {
        if ( Help.isNull(i_Counter) )
        {
            this.counter = CallFlow.$WaitCounter;
        }
        else
        {
            this.counter = ValueHelp.standardValueID(i_Counter.trim());
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：计数器的最大值（正整数，允许counter等于最大值）。可以是数值、上下文变量、XID标识
     */
    public String getCounterMax()
    {
        return counterMax;
    }


    
    /**
     * 设置：计数器的最大值（正整数，允许counter等于最大值）。可以是数值、上下文变量、XID标识
     * 
     * @param i_CounterMax 计数器的最大值（正整数，允许counter等于最大值）。可以是数值、上下文变量、XID标识
     */
    public void setCounterMax(String i_CounterMax)
    {
        if ( Help.isNull(i_CounterMax) )
        {
            this.counterMax = null;
        }
        else
        {
            if ( Help.isNumber(i_CounterMax) )
            {
                Integer v_CounterMax = Integer.valueOf(i_CounterMax);
                if ( v_CounterMax <= 0 )
                {
                    IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CounterMax Less than or Equal to zero.");
                    $Logger.error(v_Exce);
                    throw v_Exce;
                }
                this.counterMax = i_CounterMax.trim();
            }
            else
            {
                this.counterMax = ValueHelp.standardRefID(i_CounterMax);
            }
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
            Long v_WaitTime = null;
            if ( Help.isNumber(this.waitTime) )
            {
                v_WaitTime = Long.valueOf(this.waitTime);
            }
            else
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,0L ,io_Context);
            }
            
            if ( v_WaitTime > 0 )
            {
                Thread.sleep(v_WaitTime ,0);
            }
            
            // 计数器
            Integer v_Counter = (Integer) io_Context.get(this.counter);
            if ( v_Counter == null )
            {
                v_Counter = 1;
                io_Context.put(this.counter ,1);
            }
            else
            {
                v_Counter += 1;
                io_Context.put(this.counter ,v_Counter);
            }
            
            // 计数器最大值
            boolean v_Ret = true;
            if ( !Help.isNull(this.counterMax) )
            {
                Integer v_CounterMax = (Integer) ValueHelp.getValue(this.counterMax ,Integer.class ,0 ,io_Context);
                if ( v_CounterMax != null )
                {
                    v_Ret = v_Counter <= v_CounterMax;
                }
            }
            
            v_Result.setResult(v_Ret);
            this.refreshReturn(io_Context ,v_Result.getResult());
            this.refreshStatus(io_Context ,v_Result.getStatus());
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result;
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
    }

    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
        String        v_XName  = ElementType.Wait.getXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.waitTime) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("waitTime" ,this.waitTime));
        }
        if ( !Help.isNull(this.counter) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("counter" ,this.counter));
        }
        if ( !Help.isNull(this.counterMax) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("counterMax" ,this.counterMax));
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
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("Wait：");
        if ( !Help.isNumber(this.waitTime) )
        {
            v_Builder.append(this.waitTime).append("=");
            
            Long v_WaitTime = null;
            try
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,null ,i_Context);
                if ( v_WaitTime == null )
                {
                    v_Builder.append("?");
                }
                else
                {
                    v_Builder.append(v_WaitTime);
                }
            }
            catch (Exception exce)
            {
                v_Builder.append("ERROR");
                $Logger.error(exce);
            }
        }
        else
        {
            v_Builder.append(this.waitTime);
        }
        v_Builder.append(" ms");
        
        // 计数器
        if ( !Help.isNull(this.counter) )
        {
            Integer v_Counter = (Integer) i_Context.get(this.counter);
            if ( v_Counter == null )
            {
                v_Counter = 1;
            }
            else
            {
                v_Counter = v_Counter + 1;
            }
            
            v_Builder.append(" Counter：").append(v_Counter);
        }
        
        // 计数器最大值
        if ( !Help.isNull(this.counterMax) )
        {
            v_Builder.append(" CounterMax：");
            try
            {
                Integer v_CounterMax = (Integer) ValueHelp.getValue(this.counterMax ,Integer.class ,0 ,i_Context);
                v_Builder.append(v_CounterMax);
            }
            catch (Exception exce)
            {
                v_Builder.append("ERROR");
                $Logger.error(exce);
            }
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("Wait：").append(this.waitTime).append(" ms");
        
        if ( !Help.isNull(this.counter) )
        {
            v_Builder.append(" Counter：").append(this.counter);
        }
        
        if ( Help.isNull(this.counterMax) )
        {
            v_Builder.append(" Max：").append(this.counterMax);
        }
        
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
        return new WaitConfig();
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
        WaitConfig v_Clone = new WaitConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.waitTime   = this.waitTime;
        v_Clone.counter    = this.counter;
        v_Clone.counterMax = this.counterMax;
        
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
            throw new NullPointerException("Clone WaitConfig xid is null.");
        }
        
        WaitConfig v_Clone = (WaitConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.waitTime   = this.waitTime;
        v_Clone.counter    = this.counter;
        v_Clone.counterMax = this.counterMax;
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
            throw new NullPointerException("Clone WaitConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        WaitConfig                  v_Clone      = new WaitConfig();
        
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
