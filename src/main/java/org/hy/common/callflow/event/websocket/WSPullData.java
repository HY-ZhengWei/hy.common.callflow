package org.hy.common.callflow.event.websocket;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.WSContentType;
import org.hy.common.callflow.event.WSPullConfig;





/**
 * 点拉元素的执行数据（所有属性只读）
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-30
 * @version     v1.0
 */
public class WSPullData
{
    
    /** 点拉元素的全局惟一标识ID */
    private String              xid;
    
    /** WebSocket连接地址（全路径） */
    private String              wsURL;
    
    /** 编排的XID。采用弱关联的方式 */
    private String              callFlowXID;
    
    /** 运行时的上下文。克隆于点拉元素，与点拉元素相互独立 */
    private Map<String ,Object> executeContext;
    
    /** 消息体的格式类型 */
    private WSContentType       contentType;
    
    /** 为返回值定义的变量ID */          
    private String              returnID;
    
    /** 为返回值定义 */          
    private Class<?>            returnClass;
    
    
    
    /**
     * 点拉元素的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-01
     * @version     v1.0
     *
     * @param i_Config              点拉元素
     * @param i_Context             上下文类型的变量信息
     * @param i_WSURLPlaceholders   WebSocket连接地址，已解释完成的占位符
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public WSPullData(WSPullConfig i_Config ,Map<String ,Object> i_Context ,PartitionMap<String ,Integer> i_WSURLPlaceholders) throws Exception
    {
        if ( i_Config == null )
        {
            throw new NullPointerException("WSPullConfig is null.");
        }
        if ( Help.isNull(i_Context) )
        {
            throw new NullPointerException("Context is null.");
        }
        
        // 克隆运行时上下文，确保每次运行时，都是一样的参数，不受上次执行的影响
        this.setExecuteContext(new HashMap<String ,Object>());
        this.executeContext.putAll(i_Context);
        if ( !Help.isNull(i_Config.getContext()) )
        {
            String v_ContextValue = ValueHelp.replaceByContext(i_Config.getContext() ,this.executeContext);
            Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_ContextValue ,Map.class ,null ,this.executeContext);
            this.executeContext.putAll(v_ContextMap);
            v_ContextMap.clear();
            v_ContextMap = null;
        }
        
        this.setContentType(WSContentType.get(i_Config.getContentType()));
        this.setWsURL(ValueHelp.replaceByContext(i_Config.getWsURL() ,i_WSURLPlaceholders ,this.executeContext));
        this.setXid(        i_Config.getXid());
        this.setCallFlowXID(i_Config.gatCallFlowXID());
        this.setReturnID(   i_Config.getReturnID());
        this.setReturnClass(i_Config.gatReturnClass());
    }

    
    /**
     * 获取：点拉元素的全局惟一标识ID
     */
    public String getXid()
    {
        return xid;
    }

    
    /**
     * 设置：点拉元素的全局惟一标识ID
     * 
     * @param i_Xid 点拉元素的全局惟一标识ID
     */
    private void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }


    /**
     * 获取：WebSocket连接地址（全路径）
     */
    public String getWsURL()
    {
        return wsURL;
    }

    
    /**
     * 设置：WebSocket连接地址（全路径）
     * 
     * @param i_WsURL WebSocket连接地址（全路径）
     */
    private void setWsURL(String i_WsURL)
    {
        this.wsURL = i_WsURL;
    }

    
    /**
     * 获取：编排的XID。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return callFlowXID;
    }

    
    /**
     * 设置：编排的XID。采用弱关联的方式
     * 
     * @param i_CallFlowXID 编排的XID。采用弱关联的方式
     */
    private void setCallFlowXID(String i_CallFlowXID)
    {
        this.callFlowXID = i_CallFlowXID;
    }

    
    /**
     * 获取：运行时的上下文。克隆于点拉元素，与点拉元素相互独立
     * 
     * 注：克隆运行时上下文，确保每次运行时，都是一样的参数，不受上次执行的影响
     *     同时确保外界不会修改到此类的属性
     */
    public Map<String ,Object> getExecuteContext()
    {
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.putAll(this.executeContext);
        return v_Context;
    }

    
    /**
     * 设置：运行时的上下文。克隆于点拉元素，与点拉元素相互独立
     * 
     * @param i_ExecuteContext 运行时的上下文。克隆于点拉元素，与点拉元素相互独立
     */
    private void setExecuteContext(Map<String ,Object> i_ExecuteContext)
    {
        this.executeContext = i_ExecuteContext;
    }

    
    /**
     * 获取：消息体的格式类型
     */
    public WSContentType getContentType()
    {
        return contentType;
    }

    
    /**
     * 设置：消息体的格式类型
     * 
     * @param i_ContentType 消息体的格式类型
     */
    private void setContentType(WSContentType i_ContentType)
    {
        this.contentType = i_ContentType;
    }
    
    
    /**
     * 获取：为返回值定义的变量ID
     */
    public String getReturnID()
    {
        return returnID;
    }

    
    /**
     * 设置：为返回值定义的变量ID
     * 
     * @param i_ReturnID 为返回值定义的变量ID
     */
    private void setReturnID(String i_ReturnID)
    {
        this.returnID = i_ReturnID;
    }


    /**
     * 获取：为返回值定义
     */
    public Class<?> getReturnClass()
    {
        return returnClass;
    }

    
    /**
     * 设置：为返回值定义
     * 
     * @param i_ReturnClass 为返回值定义
     */
    private void setReturnClass(Class<?> i_ReturnClass)
    {
        this.returnClass = i_ReturnClass;
    }
    
}
