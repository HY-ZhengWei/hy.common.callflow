package org.hy.common.callflow.event;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.MessageType;
import org.hy.common.callflow.event.mqtt.SubscribeMQTTListener;
import org.hy.common.callflow.event.mqtt.SubscribeMQTTs;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.APIConfig;
import org.hy.common.callflow.node.APIException;
import org.hy.common.mqtt.client.enums.MessageFormat;
import org.hy.common.xml.XJava;





/**
 * 订阅元素：支持MQTT协议的订阅。衍生于接口元素。
 * 
 * 返回ID（returnID），一共会生成三种上下文中的变量。
 *    第一种：在执行订阅元素时，按returnID命名生成变量，返回值在MQTT类型时为SubscribeMQTT。
 *    第二种：在执行订阅元素后，收到订阅消息时，按returnID + "_Topic"   命名生成变量，返回值为订阅消息的主题。
 *    第三种：在执行订阅元素后，收到订阅消息时，按returnID + "_Message" 命名生成变量，返回值为订阅消息的内容。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-05-06
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class SubscribeConfig extends APIConfig
{
    
    /** 动态创建的MQTT订阅的监听器实例对象的XID命名后缀 */
    private static final String $ListenerXID   = "_SubscribeMQTTListener";
    
    /** 收到订阅消息时，按返回ID生成的主题变量命名后缀 */
    private static final String $ReturnTopic   = "_Topic";
    
    /** 收到订阅消息时，按返回ID生成的消息变量命名后缀 */
    private static final String $ReturnMessage = "_Message";
    
    
    
    /** 订阅类型 */
    private MessageType         subscribeType;
    
    /** 订阅微服务地址。默认为：http://127.0.0.1 */
    private String              subscribeURL;
    
    /** 数据订阅XID。可以是数值、上下文变量、XID标识 */
    private String              subscribeXID;
    
    /** 物联设备的连接密码（即，MQTT服务器的密码） */
    private String              brokerPassword;
    
    /** 用户ID。可以是数值、上下文变量、XID标识 */
    private String              userID;
    
    /** 编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式 */
    private String              callFlowXID;
    
    /** 订阅消息的格式。可以是数值、上下文变量、XID标识 */
    private String              format;
    
    /** 执行定时元素时的运行时的上下文（仅内部使用） */
    private Map<String ,Object> executeContext;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     */
    public SubscribeConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public SubscribeConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setSubscribeType(MessageType.MQTT);
        this.setSubscribeURL(PublishConfig.$PublishURL);
        this.setFormat(MessageFormat.Text.getValue());
        this.setRequestType("POST");
        this.setSucceedFlag("200");
        this.setConnectTimeout(10 * 1000);
        this.setReadTimeout(   15 * 1000);
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
        if ( Help.isNull(this.getSubscribeXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(this.getXid()) + "].subscribeXID is null.");
            return false;
        }
        if ( Help.isNull(this.getUserID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(this.getXid()) + "].userID is null.");
            return false;
        }
        if ( Help.isNull(this.getCallFlowXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(this.getXid()) + "].callFlowXID is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 获取：订阅类型
     */
    public MessageType getSubscribeType()
    {
        return subscribeType;
    }


    
    /**
     * 设置：订阅类型
     * 
     * @param i_SubscribeType 订阅类型
     */
    public void setSubscribeType(MessageType i_SubscribeType)
    {
        if ( i_SubscribeType == null )
        {
            this.subscribeType = MessageType.MQTT;
        }
        else
        {
            this.subscribeType = i_SubscribeType;
        }
        
        if ( this.subscribeType.equals(MessageType.MQTT) )
        {
            this.setReturnClass(SubscribeMQTTs.class.getName());
            this.setReturnClassKey("data");
        }
        
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：订阅微服务地址。默认为：http://127.0.0.1
     */
    public String getSubscribeURL()
    {
        return subscribeURL;
    }


    
    /**
     * 设置：订阅微服务地址。默认为：http://127.0.0.1
     * 
     * @param i_SubscribeURL 订阅微服务地址。默认为：http://127.0.0.1
     */
    public void setSubscribeURL(String i_SubscribeURL)
    {
        if ( Help.isNull(i_SubscribeURL) )
        {
            this.subscribeURL = PublishConfig.$PublishURL;
        }
        else
        {
            this.subscribeURL = i_SubscribeURL;
        }
        this.setUrl(this.subscribeURL + "/msMQTT/subscribe/querySubscribe");
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：数据订阅XID。可以是数值、上下文变量、XID标识
     */
    public String getSubscribeXID()
    {
        return subscribeXID;
    }


    
    /**
     * 设置：数据订阅XID。可以是数值、上下文变量、XID标识
     * 
     * @param i_SubscribeXID 数据订阅XID。可以是数值、上下文变量、XID标识
     */
    public void setSubscribeXID(String i_SubscribeXID)
    {
        this.subscribeXID = i_SubscribeXID;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：物联设备的连接密码（即，MQTT服务器的密码）
     */
    public String getBrokerPassword()
    {
        return brokerPassword;
    }


    
    /**
     * 设置：物联设备的连接密码（即，MQTT服务器的密码）
     * 
     * @param i_BrokerPassword 物联设备的连接密码（即，MQTT服务器的密码）
     */
    public void setBrokerPassword(String i_BrokerPassword)
    {
        this.brokerPassword = i_BrokerPassword;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：用户ID。可以是数值、上下文变量、XID标识
     */
    public String getUserID()
    {
        return userID;
    }


    
    /**
     * 设置：用户ID。可以是数值、上下文变量、XID标识
     * 
     * @param i_UserID 用户ID。可以是数值、上下文变量、XID标识
     */
    public void setUserID(String i_UserID)
    {
        this.userID = i_UserID;
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
     * 获取：订阅消息的格式。可以是数值、上下文变量、XID标识
     */
    public String getFormat()
    {
        return format;
    }


    
    /**
     * 设置：订阅消息的格式。可以是数值、上下文变量、XID标识
     * 
     * @param i_Format 订阅消息的格式。可以是数值、上下文变量、XID标识
     */
    public void setFormat(String i_Format)
    {
        this.format = i_Format;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 获取：执行定时元素时的运行时的上下文（仅内部使用）
     */
    public Map<String ,Object> gatExecuteContext()
    {
        return executeContext;
    }
    
    
    
    /**
     * 收到订阅消息时，按返回ID生成的主题变量命名
     */
    public String getReturnIDTopic()
    {
        if ( Help.isNull(this.returnID) )
        {
            return null;
        }
        else
        {
            return this.returnID + $ReturnTopic;
        }
    }
    
    
    
    /**
     * 收到订阅消息时，按返回ID生成的消息变量命名
     */
    public String getReturnIDMessage()
    {
        if ( Help.isNull(this.returnID) )
        {
            return null;
        }
        else
        {
            return this.returnID + $ReturnMessage;
        }
    }



    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Subscribe.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Subscribe.getXmlName();
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        if ( !Help.isNull(this.param) )
        {
            io_Params[0] = ValueHelp.replaceByContext(this.param ,io_Context);
        }
        
        String v_SubscribeXID = null;
        if ( !Help.isNull(this.subscribeXID) )
        {
            v_SubscribeXID = ValueHelp.replaceByContext(this.subscribeXID ,io_Context);
        }
        else
        {
            v_SubscribeXID = "";
        }
        
        String v_UserID = null;
        if ( !Help.isNull(this.userID) )
        {
            v_UserID = ValueHelp.replaceByContext(this.userID ,io_Context);
        }
        else
        {
            v_UserID = "";
        }
        
        StringBuilder v_Body = new StringBuilder();
        if ( MessageType.MQTT.equals(this.subscribeType) )
        {
            v_Body.append("{");
            v_Body.append("  \"xid\": \"").append(v_SubscribeXID).append("\",");
            v_Body.append("  \"userID\": \"").append(v_UserID).append("\"");
            v_Body.append("}");
        }
        io_Params[1] = v_Body.toString();
        
        if ( !Help.isNull(this.head) )
        {
            String v_Head = ValueHelp.replaceByContext(this.head ,io_Context);
            try
            {
                io_Params[2] = ValueHelp.getValue(v_Head ,Map.class ,null ,io_Context);
            }
            catch (Exception exce)
            {
                throw new RuntimeException(exce);
            }
        }
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
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteReturn  执行结果。已用NodeConfig自己的力量获取了执行结果。
     * @return                  Return.get()          是否执行成功
     *                          Return.getParamObj()  执行结果
     *                          Return.getException() 执行异常
     * @throws Exception 
     */
    public Return<Object> generateReturn(Map<String ,Object> io_Context ,Object io_ExecuteReturn)
    {
        Return<Object> v_Ret = super.generateReturn(io_Context ,io_ExecuteReturn);
        if ( !v_Ret.booleanValue() )
        {
            return v_Ret;
        }
        
        if ( this.subscribeType.equals(MessageType.MQTT) )
        {
            SubscribeMQTTs v_Subscribes = (SubscribeMQTTs) v_Ret.paramObj;
            if ( Help.isNull(v_Subscribes) )
            {
                APIException v_Exce = new APIException(v_Ret.getParamStr() ,"SubscribeMQTTs size is 0." ,v_Ret.getParamObj());
                return new Return<Object>(false).setException(v_Exce);
            }
            
            SubscribeMQTTListener v_Listener = (SubscribeMQTTListener) XJava.getObject(this.getXid() + $ListenerXID);
            if ( v_Listener != null )
            {
                v_Listener.unsubscribeClose();
            }
            
            this.executeContext = new HashMap<String ,Object>();
            if ( !Help.isNull(io_Context) )
            {
                for (Map.Entry<String ,Object> v_Item : io_Context.entrySet())
                {
                    if ( CallFlow.isSystemXID(v_Item.getKey()) )
                    {
                        continue;
                    }
                    this.executeContext.put(v_Item.getKey() ,v_Item.getValue());
                }
            }
            
            v_Listener = new SubscribeMQTTListener(v_Subscribes.getDatas().get(0) ,this);
            XJava.putObject(this.getXid() + $ListenerXID ,v_Listener);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
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
        
        if ( this.subscribeType != null && !MessageType.MQTT.equals(this.subscribeType) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("subscribeType" ,this.subscribeType.getValue()));
        }
        if ( !Help.isNull(this.subscribeURL) && !PublishConfig.$PublishURL.equals(this.subscribeURL) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("subscribeURL" ,this.subscribeURL));
        }
        if ( !Help.isNull(this.subscribeXID) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("subscribeXID" ,this.subscribeXID));
        }
        if ( !Help.isNull(this.userID) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("userID" ,this.userID));
        }
        if ( !Help.isNull(this.brokerPassword) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("brokerPassword" ,this.brokerPassword));
        }
        if ( this.getConnectTimeout() != 10 * 1000 )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("connectTimeout" ,this.getConnectTimeout()));
        }
        if ( this.getReadTimeout() != 15 * 1000 )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("readTimeout" ,this.getReadTimeout()));
        }
        if ( !Help.isNull(this.param) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("param" ,this.param));
        }
        if ( !Help.isNull(this.head) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("head" ,this.head));
        }
        if ( !Help.isNull(this.gatCallFlowXID()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("callFlowXID" ,this.getCallFlowXID()));
        }
        if ( !Help.isNull(this.format) && !MessageFormat.Text.equals(MessageFormat.get(this.format)) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("format" ,this.format ,v_NewSpace));
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
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("订阅");
        v_Builder.append(this.subscribeType.getValue());
        v_Builder.append(":");
        v_Builder.append(this.subscribeURL);
        v_Builder.append(":");
        
        if ( Help.isNull(this.getSubscribeXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            try
            {
                v_Builder.append(ValueHelp.getValue(this.getSubscribeXID() ,String.class ,this.getSubscribeXID() ,i_Context));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        v_Builder.append(":");
        if ( Help.isNull(this.getUserID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            try
            {
                v_Builder.append(ValueHelp.getValue(this.getUserID() ,String.class ,this.getUserID() ,i_Context));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        v_Builder.append(" TO ");
        if ( Help.isNull(this.gatCallFlowXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getCallFlowXID());
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("订阅");
        v_Builder.append(this.subscribeType.getValue());
        v_Builder.append(":");
        v_Builder.append(this.subscribeURL);
        v_Builder.append(":");
        v_Builder.append(Help.NVL(this.getSubscribeXID() ,"?"));
        v_Builder.append(":");
        v_Builder.append(Help.NVL(this.getUserID() ,"?"));
        v_Builder.append(" TO ");
        v_Builder.append(Help.NVL(this.getCallFlowXID() ,"?"));
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new SubscribeConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        SubscribeConfig v_Clone = new SubscribeConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.setSubscribeType( this.getSubscribeType());
        v_Clone.setSubscribeURL(  this.getSubscribeURL());
        v_Clone.setSubscribeXID(  this.getSubscribeXID());
        v_Clone.setBrokerPassword(this.getBrokerPassword());
        v_Clone.setUserID(        this.getUserID());
        v_Clone.setParam(         this.getParam());
        v_Clone.setHead(          this.getHead());
        v_Clone.setContext(       this.getContext());
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(   this.getReadTimeout());
        v_Clone.setTimeout(       this.getTimeout());
        v_Clone.setCallFlowXID(   this.getCallFlowXID());
        v_Clone.setFormat(        this.getFormat());
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
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
            throw new NullPointerException("Clone SubscribeConfig xid is null.");
        }
        
        SubscribeConfig v_Clone = (SubscribeConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.setSubscribeType( this.getSubscribeType());
        v_Clone.setSubscribeURL(  this.getSubscribeURL());
        v_Clone.setSubscribeXID(  this.getSubscribeXID());
        v_Clone.setBrokerPassword(this.getBrokerPassword());
        v_Clone.setUserID(        this.getUserID());
        v_Clone.setParam(         this.getParam());
        v_Clone.setHead(          this.getHead());
        v_Clone.setContext(       this.getContext());
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(   this.getReadTimeout());
        v_Clone.setTimeout(       this.getTimeout());
        v_Clone.setCallFlowXID(   this.getCallFlowXID());
        v_Clone.setFormat(        this.getFormat());
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone SubscribeConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        SubscribeConfig             v_Clone      = new SubscribeConfig();
        
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
