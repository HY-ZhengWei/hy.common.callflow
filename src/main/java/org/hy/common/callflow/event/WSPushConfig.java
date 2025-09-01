package org.hy.common.callflow.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StaticReflect;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.common.FindClass;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.WSContentType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;





/**
 * 点推元素：支持WebSocket协议的服务端推送。衍生于执行元素。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-08-08
 * @version     v1.0
 */
public class WSPushConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger   $Logger   = new Logger(WSPushConfig.class);
    
    /** WebSocketServer类的推送方法 */
    private static       Method   $WSMethod = null;
    
    
    
    /** WS发布的URL路径中推送名称（仅内部使用） */
    private NodeParam                     nameParam;
    
    /** 新消息。可以是常量、上下文变量、XID标识，并且支持多个占位符 */
    private String                        newMessage;
    
    /** 新消息，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> newMessagePlaceholders;
    
    /** 客户端首次连接时的全部消息。可以是常量、上下文变量、XID标识，并且支持多个占位符 */
    private String                        allMessage;
    
    /** 全部消息，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> allMessagePlaceholders;
    
    /** 消息体的格式类型 */
    private String                        contentType;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     */
    public WSPushConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public WSPushConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.nameParam = new NodeParam();
        this.nameParam.setValueClass(String.class.getName());
        this.setCallParam(this.nameParam);
        
        NodeParam v_CallParam = new NodeParam();
        v_CallParam.setValueClass(null);
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
        
        v_CallParam = new NodeParam();
        v_CallParam.setValueClass(null);
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
        
        this.setName(null);
        this.setCallMethod("pushMessages");
        this.setContentType(WSContentType.Json.getValue());
    }
    
    
    
    /**
     * 查找WebSocketServer类的元类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     */
    private synchronized void findWSClass()
    {
        if ( $WSMethod != null )
        {
            return;
        }
        
        Class<?> v_WSClass = FindClass.finds("WebSocketServer");
        if ( v_WSClass != null )
        {
            List<Method> v_Methods = MethodReflect.getMethods(v_WSClass ,this.getCallMethod() ,3);
            if ( !Help.isNull(v_Methods) )
            {
                if ( v_Methods.size() == 1 )
                {
                    $WSMethod = v_Methods.get(0);
                }
                else
                {
                    // 注：不 throw 异常。因为它的父方法会返回是否执行成功标识 
                    $Logger.error(new ClassNotFoundException("Found multiple(" + v_Methods.size() + ") matching classes(WebSocketServer)."));
                }
            }
        }
    }
    
    
    
    /**
     * 获取：WS发布的URL路径中推送名称。可以是常量、上下文变量、XID标识
     */
    public String getName()
    {
        return this.nameParam.getValue();
    }

    
    
    /**
     * 设置：WS发布的URL路径中推送名称。可以是常量、上下文变量、XID标识
     * 
     * @param i_Name  WS发布的URL路径中推送名称。可以是常量、上下文变量、XID标识
     */
    public void setName(String i_Name)
    {
        if ( Help.isNull(i_Name) )
        {
            this.nameParam.setValue("ws");
        }
        else
        {
            this.nameParam.setValue(i_Name.trim());
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：新消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public String getNewMessage()
    {
        return this.newMessage;
    }


    
    /**
     * 设置：新消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     * 
     * @param i_NewMessage 新消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public void setNewMessage(String i_NewMessage)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_NewMessage) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_NewMessage ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.newMessagePlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.newMessagePlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.newMessage = i_NewMessage;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：客户端首次连接时的全部消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public String getAllMessage()
    {
        return this.allMessage;
    }
    
    
    
    /**
     * 设置：客户端首次连接时的全部消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     * 
     * @param i_AllMessage 客户端首次连接时的全部消息。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public void setAllMessage(String i_AllMessage)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_AllMessage) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_AllMessage ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.allMessagePlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.allMessagePlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.allMessage = i_AllMessage;
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
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.WSPush.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.WSPush.getXmlName();
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
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
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        if ( !Help.isNull(this.newMessage) )
        {
            io_Params[1] = ValueHelp.replaceByContext(this.newMessage ,this.newMessagePlaceholders ,io_Context);
        }
        
        if ( !Help.isNull(this.allMessage) )
        {
            io_Params[2] = ValueHelp.replaceByContext(this.allMessage ,this.allMessagePlaceholders ,io_Context);
        }
        
        return io_Params;
    }
    
    
    
    /**
     * 向客户端群发消息。
     * 
     * 首次接入的客户端，将发送全部消息，之后将只发有变化的消息
     * 
     * @param i_Name         WS发布的URL路径中推送名称
     * @param i_NewMessage   仅有变化的消息
     * @param i_AllMessage   全部消息
     */
    public boolean pushMessages(String i_Name ,Object i_NewMessage ,Object i_AllMessage)
    {
        this.findWSClass();
        if ( $WSMethod == null )
        {
            throw new RuntimeException("WebSocketServer is not find.");
        }
        
        if ( i_NewMessage == null )
        {
            throw new NullPointerException("NewMessage is null.");
        }
        
        try
        {
            String v_NewMsg = null;
            String v_AllMsg = null;
            
            if ( i_NewMessage instanceof String )
            {
                v_NewMsg = i_NewMessage.toString();
            }
            else if ( WSContentType.Json.getValue().equalsIgnoreCase(this.getContentType()) )
            {
                XJSON v_XJson = new XJSON();
                v_NewMsg = v_XJson.toJson(i_NewMessage).toJSONString();
            }
            else
            {
                v_NewMsg = i_NewMessage.toString();
            }
            
            // 当“全部消息”没有数据时，用“新消息”代替
            if ( i_AllMessage == null )
            {
                v_AllMsg = v_NewMsg;
            }
            else if ( i_AllMessage instanceof String )
            {
                v_AllMsg = i_AllMessage.toString();
            }
            else if ( WSContentType.Json.getValue().equalsIgnoreCase(this.getContentType()) )
            {
                XJSON v_XJson = new XJSON();
                v_AllMsg = v_XJson.toJson(i_AllMessage).toJSONString();
            }
            else
            {
                v_AllMsg = i_AllMessage.toString();
            }
            
            StaticReflect.invoke($WSMethod ,this.getName() ,v_NewMsg ,Help.NVL(v_AllMsg ,v_NewMsg));
            return true;
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
     * @createDate  2025-08-08
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
        
        if ( !Help.isNull(this.getName()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("name"        ,this.getName()));
        }
        if ( !Help.isNull(this.getNewMessage()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("newMessage"  ,this.getNewMessage() ,v_NewSpace));
        }
        if ( !Help.isNull(this.getAllMessage()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("allMessage"  ,this.getAllMessage() ,v_NewSpace));
        }
        if ( !Help.isNull(this.getContentType()) && !WSContentType.Json.getValue().equalsIgnoreCase(this.getContentType()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("contentType" ,this.getContentType()));
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
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("WS name: ");
        if ( !Help.isNull(this.getName()) )
        {
            try
            {
                String v_Name = (String) ValueHelp.getValue(this.getName() ,String.class ,"" ,i_Context);
                v_Builder.append(v_Name);
            }
            catch (Exception exce)
            {
                v_Builder.append(exce.getMessage());
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" : ");
        if ( !Help.isNull(this.getNewMessage()) )
        {
            try
            {
                String v_NewMessage = ValueHelp.replaceByContext(this.newMessage ,this.newMessagePlaceholders ,i_Context);
                if ( Help.isNull(v_NewMessage) )
                {
                    v_Builder.append("?");
                }
                else
                {
                    v_Builder.append(v_NewMessage);
                }
                
            }
            catch (Exception exce)
            {
                v_Builder.append(exce.getMessage());
            }
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
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("WS name: ");
        if ( !Help.isNull(this.getName()) )
        {
            v_Builder.append(this.getName());
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" : ");
        if ( !Help.isNull(this.getNewMessage()) )
        {
            v_Builder.append(this.getNewMessage());
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
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new WSPushConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        WSPushConfig v_Clone = new WSPushConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.contentType = this.contentType;
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        
        v_Clone.setNewMessage(this.getNewMessage());
        v_Clone.setAllMessage(this.getAllMessage());
        
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
     * @createDate  2025-08-08
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
            throw new NullPointerException("Clone WSPushConfig xid is null.");
        }
        
        WSPushConfig v_Clone = (WSPushConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.contentType = this.contentType;
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        
        v_Clone.setNewMessage(this.getNewMessage());
        v_Clone.setAllMessage(this.getAllMessage());
        
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
     * @createDate  2025-08-08
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
            throw new NullPointerException("Clone WSPushConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        WSPushConfig                v_Clone      = new WSPushConfig();
        
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
