package org.hy.common.callflow.event;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.common.FindClass;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.WSContentType;
import org.hy.common.callflow.event.websocket.WSPullData;
import org.hy.common.callflow.event.websocket.WSPullExecuter;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.log.Logger;





/**
 * 点拉元素：支持WebSocket协议的客户端拉取。衍生于执行元素。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-08-30
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class WSPullConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger   $Logger   = new Logger(WSPullConfig.class);
    
    
    
    /** WebSocket连接地址（全路径）。格式为：ws://IP:Port/服务名/接口名 或 wss://IP:Port/服务名/接口名。可以是常量、上下文变量、XID标识，并且支持多个占位符 */
    private String                        wsURL;
    
    /** WebSocket连接地址，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> wsURLPlaceholders;
    
    /** 编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式 */
    private String                        callFlowXID;
    
    /** 消息体的格式类型 */
    private String                        contentType;
    
    /** 
     * 返回结果的元类型。返回结果的数据为数值类型时生效；或返回结果有默认值时生效 
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     */
    private String                        returnClass;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     */
    public WSPullConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public WSPullConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setCallMethod("pullMessages");
        NodeParam v_CallParam = new NodeParam();
        v_CallParam.setValueClass(Map.class.getName());
        v_CallParam.setValue(":CallFlowContext");
        this.setCallParam(v_CallParam);
        
        this.setContentType(WSContentType.Json.getValue());
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
        if ( !super.check(io_Result) )
        {
            return false;
        }
        
        if ( Help.isNull(this.getWsURL()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].wsURL is null.");
            return false;
        }
        if ( Help.isNull(this.getCallFlowXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callFlowXID is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 查找WSPullExecuter接口实现并创建它的实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     */
    private WSPullExecuter newWSPullExecuter()
    {
        WSPullExecuter v_Executer = null;
        Class<?>       v_WSClass  = FindClass.finds("WebSocketPullExecuter");
        
        if ( v_WSClass != null )
        {
            if ( !MethodReflect.isExtendImplement(v_WSClass ,WSPullExecuter.class) )
            {
                // 注：不 throw 异常。因为它的父方法会返回是否执行成功标识 
                ClassNotFoundException v_Error = new ClassNotFoundException("Class[" + v_WSClass.getName() + "] does not implement interface(WSPullExecuter)");
                $Logger.error(v_Error);
            }
            
            try
            {
                v_Executer = (WSPullExecuter) v_WSClass.getDeclaredConstructor().newInstance();
            }
            catch (Exception exce)
            {
                // 注：不 throw 异常。因为它的父方法会返回是否执行成功标识 
                InstantiationException v_Error = new InstantiationException("Class[" + v_WSClass.getName() + "] newInstance error");
                $Logger.error(v_Error);
            }
        }
        
        return v_Executer;
    }
    
    
    
    /**
     * 获取：WebSocket连接地址（全路径）。格式为：ws://IP:Port/服务名/接口名 或 wss://IP:Port/服务名/接口名。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public String getWsURL()
    {
        return this.wsURL;
    }


    
    /**
     * 设置：WebSocket连接地址（全路径）。格式为：ws://IP:Port/服务名/接口名 或 wss://IP:Port/服务名/接口名。可以是常量、上下文变量、XID标识，并且支持多个占位符
     * 
     * @param i_WsURL  WebSocket连接地址（全路径）。格式为：ws://IP:Port/服务名/接口名 或 wss://IP:Port/服务名/接口名。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public void setWsURL(String i_WsURL)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_WsURL) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_WsURL ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.wsURLPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.wsURLPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.wsURL = i_WsURL;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：消息体的格式类型
     */
    public String getContentType()
    {
        return contentType;
    }


    
    /**
     * 设置：消息体的格式类型
     * 
     * @param i_ContentType 消息体的格式类型
     */
    public void setContentType(String i_ContentType)
    {
        this.contentType = Help.isNull(i_ContentType) ? WSContentType.Json.getValue() : i_ContentType;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return ValueHelp.standardRefID(this.callFlowXID);
    }
    
    
    
    /**
     * 获取：编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public String gatCallFlowXID()
    {
        return this.callFlowXID;
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
     * 获取返回结果的元类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-03
     * @version     v1.0
     *
     * @return
     */
    public Class<?> gatReturnClass()
    {
        if ( !Help.isNull(this.returnClass) )
        {
            try
            {
                return Help.forName(this.returnClass);
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
    public String getReturnClass()
    {
        return returnClass;
    }
    
    
    
    /**
     * 设置：返回结果的元类型。返回结果的数据为数值类型时生效
     * 
     * @param i_ReturnClass 返回结果的元类型。返回结果的数据为数值类型时生效
     */
    public void setReturnClass(String i_ReturnClass)
    {
        if ( Void.class.getName().equals(i_ReturnClass) )
        {
            this.returnClass = null;
        }
        else
        {
            this.returnClass = i_ReturnClass;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * 自己反射调用自己的实例中的方法
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_Xid)
    {
        super.setXJavaID(i_Xid);
        this.setCallXID(this.getXid());
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.WSPull.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.WSPull.getXmlName();
    }
    
    
    
    /**
     * 转XML时是否显示retFalseIsError属性
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public boolean xmlShowRetFalseIsError()
    {
        return false;
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        // 其实就是返回自己。io_ExecuteObject 获取正确时，也是this自己
        return io_ExecuteObject == null ? this : io_ExecuteObject;
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        return io_Params;
    }
    
    
    
    /**
     * 创建点拉元素的执行者用于接收WebSocket消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public boolean pullMessages(Map<String ,Object> io_Context)
    {
        WSPullExecuter v_Executer = this.newWSPullExecuter();
        if ( v_Executer == null )
        {
            throw new RuntimeException("WSPullExecuter is not find.");
        }
        if ( Help.isNull(this.gatCallFlowXID()) )
        {
            throw new NullPointerException("CallFlowXID is null.");
        }
        
        try
        {
            return v_Executer.init(new WSPullData(this ,io_Context ,this.wsURLPlaceholders));
        }
        catch (Exception exce)
        {
            throw new RuntimeException(exce);
        }
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
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
        String v_NewSpace = "\n" + i_LevelN + i_Level1;
        
        if ( !Help.isNull(this.getWsURL()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("wsURL"       ,this.getWsURL()));
        }
        if ( !Help.isNull(this.gatCallFlowXID()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("callFlowXID" ,this.getCallFlowXID()));
        }
        if ( !Help.isNull(this.getContentType()) && !WSContentType.Json.getValue().equalsIgnoreCase(this.getContentType()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("contentType" ,this.getContentType()));
        }
        if ( !Help.isNull(this.returnClass) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("returnClass" ,this.returnClass));
        }
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("WSPull : ");
        if ( !Help.isNull(this.getWsURL()) )
        {
            v_Builder.append(ValueHelp.replaceByContext(this.wsURL ,this.wsURLPlaceholders ,i_Context));
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" : ");
        if ( !Help.isNull(this.gatCallFlowXID()) )
        {
            v_Builder.append(this.getCallFlowXID());
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("WSPull : ");
        if ( !Help.isNull(this.getWsURL()) )
        {
            v_Builder.append(this.getWsURL());
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" : ");
        if ( !Help.isNull(this.gatCallFlowXID()) )
        {
            v_Builder.append(this.getCallFlowXID());
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new WSPullConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        WSPullConfig v_Clone = new WSPullConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.wsURL       = this.wsURL;
        v_Clone.callFlowXID = this.callFlowXID;
        v_Clone.contentType = this.contentType;
        v_Clone.returnClass = this.returnClass;
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        
        // 注：不用克隆执行元素的方法参数配置。因为都是统一默认的
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
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
            throw new NullPointerException("Clone WSPullConfig xid is null.");
        }
        
        WSPullConfig v_Clone = (WSPullConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.wsURL       = this.wsURL;
        v_Clone.callFlowXID = this.callFlowXID;
        v_Clone.contentType = this.contentType;
        v_Clone.returnClass = this.returnClass;
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        
        // 注：不用克隆执行元素的方法参数配置。因为都是统一默认的
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
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
            throw new NullPointerException("Clone WSPullConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        WSPullConfig                v_Clone      = new WSPullConfig();
        
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
