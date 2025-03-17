package org.hy.common.callflow.returns;

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
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;





/**
 * 返回元素：返回特定的数据，并将它定义为结果。
 * 
 * 什么是 “真返回” ，即不再执行其它分支、其它未执行的元素而返回。
 *     如编程语言中的 return 一样。
 *     当是嵌套的子编排时，在嵌套内的 “真返回” 仅退出子编排，返回到主编排向后继续执行。
 *     当不是 “真返回” 时，表示要向外返回多个值 或 仅当此元素为普通元素用。
 * 
 * 注：不建议等待配置共用，即使两个编排调用相同的等待配置也建议配置两个等待配置，使等待配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享等待配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-11
 * @version     v1.0
 */
public class ReturnConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(ReturnConfig.class);
    
    
    
    /** 
     * 返回结果的元类型。返回结果的数据为数值类型时生效；或返回结果有默认值时生效 
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     */
    private String              retClass;
    
    /** 返回结果的数据。可以是数值、上下文变量、XID标识 */
    private String              retValue;
    
    /** 返回结果的默认值的字符形式（参数为上下文变量、XID标识时生效） */
    private String              retDefault;
    
    /** 返回结果默认值的实例对象(内部使用) */
    private Object              retDefaultObject;
    
    
    
    public ReturnConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public ReturnConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.retClass = ReturnData.class.getName();
    }
    
    
    
    /**
     * 执行元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Return.getValue();
    }
    
    
    
    /**
     * 参数默认值的实例对象
     * 禁止转Json
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     * @throws Exception
     */
    public synchronized Object gatRetDefaultObject() throws Exception
    {
        if ( this.retDefault == null || this.retClass == null )
        {
            return null;
        }
        
        if ( this.retDefaultObject == null )
        {
            Class<?> v_RetClass = this.gatRetClass();
            if ( Help.isBasicDataType(v_RetClass) )
            {
                this.retDefaultObject = Help.toObject(v_RetClass ,this.retDefault);
            }
            else
            {
                XJSON v_XJson = new XJSON();
                this.retDefaultObject = v_XJson.toJava(this.retDefault ,v_RetClass);
            }
        }
        
        return retDefaultObject;
    }
    
    
    
    /**
     * 获取返回结果的元类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-13
     * @version     v1.0
     *
     * @return
     */
    public Class<?> gatRetClass()
    {
        if ( !Help.isNull(this.retClass) )
        {
            try
            {
                return Help.forName(this.retClass);
            }
            catch (Exception exce)
            {
                throw new RuntimeException(exce);
            }
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取：返回结果的元类型。返回结果的数据为数值类型时生效
     */
    public String getRetClass()
    {
        return retClass;
    }

    
    
    /**
     * 设置：返回结果的元类型。返回结果的数据为数值类型时生效
     * 
     * @param i_RetClass 返回结果的元类型。返回结果的数据为数值类型时生效
     */
    public void setRetClass(String i_RetClass)
    {
        if ( Void.class.getName().equals(i_RetClass) )
        {
            this.retClass = null;
        }
        else
        {
            this.retClass = i_RetClass;
        }
        this.retDefaultObject = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：返回结果的数据。可以是数值、上下文变量、XID标识
     */
    public String getRetValue()
    {
        return retValue;
    }

    
    
    /**
     * 设置：返回结果的数据。可以是数值、上下文变量、XID标识
     * 
     * @param i_RetValue 返回结果的数据。可以是数值、上下文变量、XID标识
     */
    public void setRetValue(String i_RetValue)
    {
        this.retValue = i_RetValue;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    
    /**
     * 获取：返回结果的默认值的字符形式（参数为上下文变量、XID标识时生效）
     */
    public String getRetDefault()
    {
        return retDefault;
    }

    
    
    /**
     * 设置：返回结果的默认值的字符形式（参数为上下文变量、XID标识时生效）
     * 
     * @param i_RetDefault 返回结果的默认值的字符形式（参数为上下文变量、XID标识时生效）
     */
    public void setRetDefault(String i_RetDefault)
    {
        this.retDefault       = i_RetDefault;
        this.retDefaultObject = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 是否 “真返回”。 什么是真返回，即不在执行其它分支、其它未执行的元素而返回。
     * 
     * 如编程语言中的 return 一样。
     * 当是嵌套的子编排时，在嵌套内的 “真返回” 仅退出子编排，返回到主编排向后继续执行。
     * 
     * 当不是 “真返回” 时，表示要向外返回多个值 或 仅当此元素为普通元素用。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     */
    public boolean isReturn()
    {
        return Help.isNull(this.route.getSucceeds())
            && Help.isNull(this.route.getErrors());
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
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
            Object v_Value = ValueHelp.getValue(this.retValue ,this.gatRetClass() ,this.gatRetDefaultObject() ,io_Context);
            if ( this.isReturn() )
            {
                // 不要在此 put(v_Value) ，容易产生幻觉，一种情况下能取到值，一种情况下取不值。
                // 原因是：$CallFlowReturn 只是一种标记，并且在嵌套情况下，它还会被清除
                io_Context.put(CallFlow.$CallFlowReturn      ,true);
                io_Context.put(CallFlow.$CallFlowReturnValue ,v_Value);
            }
            
            this.refreshStatus(io_Context ,v_Result.getStatus());
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result.setResult(v_Value);
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
     * @createDate  2025-03-11
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
        String        v_XName  = ElementType.Return.getXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        
        if ( this.retClass != null )
        {
            if ( !this.retClass.equals(ReturnData.class.getName()) )
            {
                v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("retClass" ,this.gatRetClass().getName()));
            }
        }
        if ( !Help.isNull(this.retValue) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("retValue" ,this.retValue));
        }
        if ( !Help.isNull(this.retDefault) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("retDefault" ,this.retDefault));
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
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        Object        v_Value   = null;
        
        v_Builder.append("Return：");
        
        try
        {
            v_Value = ValueHelp.getValue(this.retValue ,this.gatRetClass() ,this.gatRetDefaultObject() ,i_Context);
        }
        catch (Exception exce)
        {
            $Logger.error("ReturnConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s retValue[" + this.retValue + "] getValue error." ,exce);
        }
        
        v_Builder.append(v_Value);
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("Return：").append(this.retValue);
        
        return v_Builder.toString();
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
        ReturnConfig v_Clone = new ReturnConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.retClass   = this.retClass;
        v_Clone.retValue   = this.retValue;
        v_Clone.retDefault = this.retDefault; 
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
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
            throw new NullPointerException("Clone ReturnConfig xid is null.");
        }
        
        ReturnConfig v_Clone = (ReturnConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.retClass   = this.retClass;
        v_Clone.retValue   = this.retValue;
        v_Clone.retDefault = this.retDefault; 
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
    protected Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone ReturnConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ReturnConfig                v_Clone      = new ReturnConfig();
        
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
