package org.hy.common.callflow.node;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.xml.XHttp;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;





/**
 * 接口元素。衍生于执行元素， 用于HTTP\HTTPS请求、API接口调用。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-02
 * @version     v1.0
 */
public class APIConfig extends NodeConfig implements NodeConfigBase
{

    /** 接口请求地址。格式为 http://IP:Port/服务名/xx/yy */
    protected String url;
    
    /** 接口请求地址中的参数。URL参数结构，支持占位符。占位符可以是上下文变量、XID标识 */
    protected String param;
    
    /** 接口请求体Body中的参数。文本字符结构，支持占位符。占位符可以是上下文变量、XID标识 */
    protected String body;
    
    /** 接口请求头Head中的参数。Map结构的Json字符串，支持占位符。占位符可以是上下文变量、XID标识 */
    protected String head;
    
    /** 
     * 返回结果的元类型。返回结果的数据为数值类型时生效；或返回结果有默认值时生效 
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     */
    protected String returnClass;
    
    /** 请求成功时的成功标记。无此标记即表示请求异常（抛APIException异常）。为空时，表示不用判定 */
    protected String succeedFlag;
    
    /** 接口请求对象(内部使用) */
    private   XHttp  callObject;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     */
    public APIConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public APIConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        String v_XHttpXID = "XAPI" + StringHelp.getUUID9n();
        this.callObject = new XHttp();
        this.callObject.setXJavaID(v_XHttpXID);
        this.callObject.setContentType("application/json");
        this.callObject.setEncode(false);
        this.callObject.setToUnicode(false);
        this.callObject.setRequestType(XHttp.$Request_Type_Get);
        this.callObject.setHaveQuestionMark(false);
        
        XJava.putObject(v_XHttpXID ,this.callObject);
        this.setCallXID(v_XHttpXID);
        this.setCallMethod("request");
        
        NodeParam v_CallParam = new NodeParam();
        v_CallParam.setValueClass(String.class.getName());
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
        
        v_CallParam = new NodeParam();
        v_CallParam.setValueClass(String.class.getName());
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
        
        v_CallParam = new NodeParam();
        v_CallParam.setValueClass(Map.class.getName());
        v_CallParam.setValue("{}");
        this.setCallParam(v_CallParam);
    }
    
    
    
    /**
     * 获取：接口请求地址。格式为 http://IP:Port/服务名/xx/yy
     */
    public String getUrl()
    {
        return url;
    }

    
    /**
     * 设置：接口请求地址。格式为 http://IP:Port/服务名/xx/yy
     * 
     * @param i_Url 接口请求地址。格式为 http://IP:Port/服务名/xx/yy
     */
    public void setUrl(String i_Url)
    {
        if ( Help.isNull(i_Url) )
        {
            this.callObject.setProtocol("http");
            this.callObject.setIp("127.0.0.1");
            this.callObject.setPort(80);
            this.callObject.setUrl(null);
        }
        
        String [] v_HttpArr = i_Url.split("://");
        if ( Help.isNull(v_HttpArr) || v_HttpArr.length < 2 )
        {
            throw new IllegalArgumentException("Url[" + i_Url + "] not find http:// or https://");
        }
        
        this.callObject.setProtocol(v_HttpArr[0].toLowerCase());
        
        String [] v_ServiceNameArr = v_HttpArr[1].split("/");
        if ( Help.isNull(v_ServiceNameArr) || v_ServiceNameArr.length < 2 )
        {
            this.callObject.setUrl("/");
        }
        else
        {
            this.callObject.setUrl(v_HttpArr[1].substring(v_ServiceNameArr[0].length()));
        }
        
        String [] v_IPPortArr = v_ServiceNameArr[0].split(":");
        if ( Help.isNull(v_IPPortArr) )
        {
            if ( "https".equals(this.callObject.getProtocol()) )
            {
                this.callObject.setPort(443);
            }
            else if ( "http".equals(this.callObject.getProtocol()) )
            {
                this.callObject.setPort(80);
            }
            else
            {
                throw new IllegalArgumentException("Url[" + i_Url + "] not find Port");
            }
        }
        else if ( v_IPPortArr.length < 2 )
        {
            this.callObject.setIp(v_IPPortArr[0]);
        }
        else if ( v_IPPortArr.length == 2 )
        {
            if ( Help.isNumber(v_IPPortArr[1]) )
            {
                this.callObject.setIp(v_IPPortArr[0]);
                this.callObject.setPort(Integer.parseInt(v_IPPortArr[1]));
                
                if ( this.callObject.getPort() <=0 )
                {
                    throw new IllegalArgumentException("Url[" + i_Url + "] port is not 1~65535");
                }
            }
            else
            {
                throw new IllegalArgumentException("Url[" + i_Url + "] port is not number");
            }
        }
        else
        {
            throw new IllegalArgumentException("Url[" + i_Url + "] is error, more : in Url");
        }
        
        this.url = i_Url;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    
    /**
     * 获取：请求类型。1:get方式(默认值)  2:post方式
     */
    public String getRequestType()
    {
        if ( this.callObject.getRequestType() == XHttp.$Request_Type_Get )
        {
            return "GET";
        }
        else if ( this.callObject.getRequestType() == XHttp.$Request_Type_Post )
        {
            return "POST";
        }
        else
        {
            return "" + this.callObject.getRequestType();
        }
    }

    
    
    /**
     * 设置：请求类型。1:get方式(默认值)  2:post方式
     * 
     * @param i_RequestType 请求类型。1:get方式(默认值)  2:post方式
     */
    public void setRequestType(String i_RequestType)
    {
        if ( Help.isNull(i_RequestType) )
        {
            this.callObject.setRequestType(XHttp.$Request_Type_Get);
        }
        else
        {
            if ( i_RequestType.trim().equalsIgnoreCase("POST") )
            {
                this.callObject.setRequestType(XHttp.$Request_Type_Post);
            }
            else
            {
                this.callObject.setRequestType(XHttp.$Request_Type_Get);
            }
        }
        
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    
    /**
     * 获取：请求内容类型(默认:application/json)
     */
    public String getContentType()
    {
        return this.callObject.getContentType();
    }


    
    /**
     * 设置：请求内容类型(默认:application/json)
     * 
     * @param i_ContentType 请求内容类型(默认:application/json)
     */
    public void setContentType(String i_ContentType)
    {
        this.callObject.setContentType(i_ContentType);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 获取：连接超时（单位：毫秒）。零值：表示永远不超时
     */
    public int getConnectTimeout()
    {
        return this.callObject.getConnectTimeout();
    }

    

    /**
     * 设置：连接超时（单位：毫秒）。零值：表示永远不超时
     * 
     * @param i_ConnectTimeout 连接超时（单位：毫秒）。零值：表示永远不超时
     */
    public void setConnectTimeout(int i_ConnectTimeout)
    {
        this.callObject.setConnectTimeout(i_ConnectTimeout);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：读取数据超时时长（单位：毫秒）。零值：表示永远不超时
     */
    public int getReadTimeout()
    {
        return this.callObject.getReadTimeout();
    }


    
    /**
     * 设置：读取数据超时时长（单位：毫秒）。零值：表示永远不超时
     * 
     * @param i_ReadTimeout 读取数据超时时长（单位：毫秒）。零值：表示永远不超时
     */
    public void setReadTimeout(int i_ReadTimeout)
    {
        this.callObject.setReadTimeout(i_ReadTimeout);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：接口请求地址中的参数。URL参数结构，支持占位符。占位符可以是上下文变量、XID标识
     */
    public String getParam()
    {
        return param;
    }


    
    /**
     * 设置：接口请求地址中的参数。URL参数结构，支持占位符。占位符可以是上下文变量、XID标识
     * 
     * @param i_Param 接口请求地址中的参数。URL参数结构，支持占位符。占位符可以是上下文变量、XID标识
     */
    public void setParam(String i_Param)
    {
        this.param = i_Param;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：接口请求体Body中的参数。文本字符结构，支持占位符。占位符可以是上下文变量、XID标识
     */
    public String getBody()
    {
        return body;
    }


    
    /**
     * 设置：接口请求体Body中的参数。文本字符结构，支持占位符。占位符可以是上下文变量、XID标识
     * 
     * @param i_Body 接口请求体Body中的参数。文本字符结构，支持占位符。占位符可以是上下文变量、XID标识
     */
    public void setBody(String i_Body)
    {
        this.body = i_Body;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：接口请求头Head中的参数。Map结构的Json字符串，支持占位符。占位符可以是上下文变量、XID标识
     */
    public String getHead()
    {
        return head;
    }


    
    /**
     * 设置：接口请求头Head中的参数。Map结构的Json字符串，支持占位符。占位符可以是上下文变量、XID标识
     * 
     * @param i_Head 接口请求头Head中的参数。Map结构的Json字符串，支持占位符。占位符可以是上下文变量、XID标识
     */
    public void setHead(String i_Head)
    {
        this.head = i_Head;
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
     * 获取：请求成功时的成功标记。无此标记即表示请求异常（抛APIException异常）。为空时，表示不用判定
     */
    public String getSucceedFlag()
    {
        return succeedFlag;
    }


    
    /**
     * 设置：请求成功时的成功标记。无此标记即表示请求异常（抛APIException异常）。为空时，表示不用判定
     * 
     * @param i_SucceedFlag 请求成功时的成功标记。无此标记即表示请求异常。为空时，表示不用判定
     */
    public void setSucceedFlag(String i_SucceedFlag)
    {
        if ( Help.isNull(i_SucceedFlag) )
        {
            this.succeedFlag = null;
        }
        else
        {
            this.succeedFlag = i_SucceedFlag;
        }
    }



    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Api.getValue();
    }
   
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Api.getXmlName();
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-03
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
        if ( !Help.isNull(this.body) )
        {
            io_Params[1] = ValueHelp.replaceByContext(this.body ,io_Context);
        }
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
     * @createDate  2025-04-03
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
        Return<?> v_XHttpRet = (Return<?>) io_ExecuteReturn;
        
        if ( v_XHttpRet.get() )
        {
            Object v_ReturnValue = v_XHttpRet.getParamStr();
            if ( !Help.isNull(this.returnClass) )
            {
                Class<?> v_ReturnClass = this.gatReturnClass();
                if ( Help.isBasicDataType(v_ReturnClass) )
                {
                    v_ReturnValue = Help.toObject(v_ReturnClass ,v_ReturnValue.toString());
                }
                else
                {
                    XJSON v_XJson = new XJSON();
                    try
                    {
                        v_ReturnValue = v_XJson.toJava(v_ReturnValue.toString() ,v_ReturnClass);
                    }
                    catch (Exception exce)
                    {
                        throw new RuntimeException(exce);
                    }
                }
            }
            
            if ( !Help.isNull(this.succeedFlag) )
            {
                if ( v_XHttpRet.getParamStr().indexOf(this.succeedFlag) >= 0 )
                {
                    return new Return<Object>(true).setParamObj(v_ReturnValue);
                }
                else
                {
                    return new Return<Object>(false).setException(new APIException(v_XHttpRet.getParamObj() == null ? "" : v_XHttpRet.getParamObj().toString() ,v_XHttpRet.getParamStr() ,v_ReturnValue));
                }
            }
            else
            {
                return new Return<Object>(true).setParamObj(v_ReturnValue);
            }
        }
        else
        {
            return new Return<Object>(false).setException(v_XHttpRet.getException());
        }
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
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
        
        if ( !Help.isNull(this.url) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("url" ,this.url));
        }
        if ( !Help.isNull(this.param) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("param" ,this.param));
        }
        if ( !Help.isNull(this.body) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("body" ,this.body ,v_NewSpace));
        }
        if ( !Help.isNull(this.head) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("head" ,this.head));
        }
        if ( !Help.isNull(this.getRequestType()) && this.callObject.getRequestType() != XHttp.$Request_Type_Get )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("requestType" ,this.getRequestType()));
        }
        if ( !Help.isNull(this.getContentType()) && !"application/json".equalsIgnoreCase(this.getContentType()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("contentType" ,this.getContentType()));
        }
        if ( this.getConnectTimeout() != 30 * 1000 )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("connectTimeout" ,this.getConnectTimeout()));
        }
        if ( this.getReadTimeout() != 300 * 1000 )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("readTimeout" ,this.getReadTimeout()));
        }
        if ( !Help.isNull(this.returnClass) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("returnClass" ,this.returnClass));
        }
        if ( !Help.isNull(this.succeedFlag) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("succeedFlag" ,this.succeedFlag));
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
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        return this.callObject.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        return this.callObject.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new APIConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        APIConfig v_Clone = new APIConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.setContext(this.getContext());
        v_Clone.url = this.url;
        v_Clone.setRequestType(this.getRequestType()); 
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(this.getReadTimeout());
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
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
            throw new NullPointerException("Clone APIConfig xid is null.");
        }
        
        APIConfig v_Clone = (APIConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.setContext(this.getContext());
        v_Clone.url = this.url;
        v_Clone.setRequestType(this.getRequestType()); 
        v_Clone.setConnectTimeout(this.getConnectTimeout());
        v_Clone.setReadTimeout(this.getReadTimeout());
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
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
            throw new NullPointerException("Clone APIConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        APIConfig                   v_Clone      = new APIConfig();
        
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
