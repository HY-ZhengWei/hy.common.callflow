package org.hy.common.callflow.event;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.MessageType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.APIConfig;





/**
 * 发布元素：支持MQTT协议的发布。衍生于接口元素。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-04-28
 * @version     v1.0
 */
public class PublishConfig extends APIConfig
{
    
    public static String $PublishURL = "http://127.0.0.1";
    
    
    
    /** 发布类型 */
    private MessageType publishType;
    
    /** 发布微服务地址。默认为：http://127.0.0.1 */
    private String      publishURL;
    
    /** 数据发布XID。可以是数值、上下文变量、XID标识 */
    private String      publishXID;
    
    /** 发布的消息。可以是数值、上下文变量、XID标识 */
    private String      message;
    
    /** MQTT服务质量等级。默认为NULL，表示创建数据发布时定义的值 */
    private Integer     qoS;
    
    /** MQTT是否保留。默认为NULL，表示创建数据发布时定义的值 */
    private Boolean     retain;
    
    /** 用户ID。可以是数值、上下文变量、XID标识 */
    private String      userID;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-28
     * @version     v1.0
     *
     */
    public PublishConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-28
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public PublishConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.publishType = MessageType.MQTT;
        this.setPublishURL($PublishURL);
        this.setRequestType("POST");
        this.setSucceedFlag("200");
    }


    
    /**
     * 获取：发布类型
     */
    public MessageType getPublishType()
    {
        return publishType;
    }


    
    /**
     * 设置：发布类型
     * 
     * @param i_PublishType 发布类型
     */
    public void setPublishType(MessageType i_PublishType)
    {
        if ( i_PublishType == null )
        {
            this.publishType = MessageType.MQTT;
        }
        else
        {
            this.publishType = i_PublishType;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：发布微服务地址。默认为：http://127.0.0.1
     */
    public String getPublishURL()
    {
        return publishURL;
    }


    
    /**
     * 设置：发布微服务地址。默认为：http://127.0.0.1
     * 
     * @param i_PublishURL 发布微服务地址。默认为：http://127.0.0.1
     */
    public void setPublishURL(String i_PublishURL)
    {
        if ( Help.isNull(i_PublishURL) )
        {
            this.publishURL = $PublishURL;
        }
        else
        {
            this.publishURL = i_PublishURL;
        }
        this.setUrl(this.publishURL + "/msMQTT/publish/executePublish");
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 获取：数据发布XID。可以是数值、上下文变量、XID标识
     */
    public String getPublishXID()
    {
        return publishXID;
    }


    
    /**
     * 设置：数据发布XID。可以是数值、上下文变量、XID标识
     * 
     * @param i_PublishXID 数据发布XID。可以是数值、上下文变量、XID标识
     */
    public void setPublishXID(String i_PublishXID)
    {
        this.publishXID = i_PublishXID;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：发布的消息。可以是数值、上下文变量、XID标识
     */
    public String getMessage()
    {
        return message;
    }


    
    /**
     * 设置：发布的消息。可以是数值、上下文变量、XID标识
     * 
     * @param i_Message 发布的消息。可以是数值、上下文变量、XID标识
     */
    public void setMessage(String i_Message)
    {
        this.message = i_Message;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：服务质量等级。默认为NULL，表示创建数据发布时定义的值
     */
    public Integer getQoS()
    {
        return qoS;
    }


    
    /**
     * 设置：服务质量等级。默认为NULL，表示创建数据发布时定义的值
     * 
     * @param i_QoS 服务质量等级。默认为NULL，表示创建数据发布时定义的值
     */
    public void setQoS(Integer i_QoS)
    {
        this.qoS = i_QoS;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：是否保留。默认为NULL，表示创建数据发布时定义的值
     */
    public Boolean getRetain()
    {
        return retain;
    }


    
    /**
     * 设置：是否保留。默认为NULL，表示创建数据发布时定义的值
     * 
     * @param i_Retain 是否保留。默认为NULL，表示创建数据发布时定义的值
     */
    public void setRetain(Boolean i_Retain)
    {
        this.retain = i_Retain;
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
    }



    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-28
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Publish.getValue();
    }
   
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-28
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Publish.getXmlName();
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-30
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
        
        String v_PublishXID = null;
        if ( !Help.isNull(this.publishXID) )
        {
            v_PublishXID = ValueHelp.replaceByContext(this.publishXID ,io_Context);
        }
        else
        {
            v_PublishXID = "";
        }
        
        String v_Message = null;
        if ( !Help.isNull(this.message) )
        {
            v_Message = ValueHelp.replaceByContext(this.message ,io_Context);
        }
        else
        {
            v_Message = "";
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
        if ( MessageType.MQTT.equals(this.publishType) )
        {
            v_Body.append("{");
            v_Body.append("  \"xid\": \"").append(v_PublishXID).append("\",");
            v_Body.append("  \"message\": \"").append(v_Message).append("\",");
            
            if ( !Help.isNull(this.qoS) )
            {
                v_Body.append("  \"qoS\": \"").append(this.qoS).append("\",");
            }
            if ( !Help.isNull(this.retain) )
            {
                v_Body.append("  \"retain\": \"").append(this.retain ? 1 : 0).append("\",");
            }
            
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
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-28
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
        
        if ( this.publishType != null && !MessageType.MQTT.equals(this.publishType) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("publishType" ,this.publishType.getValue()));
        }
        if ( !Help.isNull(this.publishURL) && !$PublishURL.equals(this.publishURL) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("publishURL" ,this.publishURL));
        }
        if ( !Help.isNull(this.publishXID) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("publishXID" ,this.publishXID));
        }
        if ( !Help.isNull(this.message) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("message" ,this.message ,v_NewSpace));
        }
        if ( !Help.isNull(this.qoS) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("qoS" ,this.qoS));
        }
        if ( !Help.isNull(this.retain) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("retain" ,this.retain));
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
     * @createDate  2025-04-28
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append(this.publishType.getValue());
        v_Builder.append(":");
        v_Builder.append(this.publishURL);
        v_Builder.append(":");
        
        if ( Help.isNull(this.getPublishXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            try
            {
                v_Builder.append(ValueHelp.getValue(this.getPublishXID() ,String.class ,this.getPublishXID() ,i_Context));
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
        
        v_Builder.append(":");
        if ( Help.isNull(this.getMessage()) )
        {
            v_Builder.append("?");
        }
        else
        {
            try
            {
                v_Builder.append(ValueHelp.getValue(this.getMessage() ,String.class ,this.getMessage() ,i_Context));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-29
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append(this.publishType.getValue());
        v_Builder.append(":");
        v_Builder.append(this.publishURL);
        v_Builder.append(":");
        v_Builder.append(Help.NVL(this.getPublishXID() ,"?"));
        v_Builder.append(":");
        v_Builder.append(Help.NVL(this.getUserID() ,"?"));
        v_Builder.append(":");
        v_Builder.append(Help.NVL(this.getMessage() ,"?"));
        
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-29
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new PublishConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-29
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        PublishConfig v_Clone = new PublishConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.setPublishType(   this.getPublishType());
        v_Clone.setPublishURL(    this.getPublishURL());
        v_Clone.setPublishXID(    this.getPublishXID());
        v_Clone.setMessage(       this.getMessage());
        v_Clone.setQoS(           this.getQoS());
        v_Clone.setRetain(        this.getRetain());
        v_Clone.setParam(         this.getParam());
        v_Clone.setHead(          this.getHead());
        v_Clone.setContext(       this.getContext());
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(   this.getReadTimeout());
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-29
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
            throw new NullPointerException("Clone PublishConfig xid is null.");
        }
        
        PublishConfig v_Clone = (PublishConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.setPublishType(   this.getPublishType());
        v_Clone.setPublishURL(    this.getPublishURL());
        v_Clone.setPublishXID(    this.getPublishXID());
        v_Clone.setMessage(       this.getMessage());
        v_Clone.setQoS(           this.getQoS());
        v_Clone.setRetain(        this.getRetain());
        v_Clone.setParam(         this.getParam());
        v_Clone.setHead(          this.getHead());
        v_Clone.setContext(       this.getContext());
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(   this.getReadTimeout());
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-29
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
            throw new NullPointerException("Clone PublishConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        PublishConfig               v_Clone      = new PublishConfig();
        
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
