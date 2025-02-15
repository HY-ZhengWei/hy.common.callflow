package org.hy.common.callflow.node;

import java.util.List;
import java.util.Map;

import org.hy.common.XJavaID;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.route.RouteConfig;





/**
 * 节点配置信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeConfig implements IExecute ,XJavaID
{
    
    /** 全局惟一标识ID */
    private String          xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String          comment;
    
    /** 执行对象的XID */
    private String          callXID;
    
    /** 执行方法名称 */
    private String          callMehod;
    
    /** 执行方法的参数 */
    private List<NodeParam> callParams;
    
    /** 为返回值定义的变量ID */
    private String          returnID;
    
    /** 路由 */
    private RouteConfig     route;
    
    
    
    public NodeConfig()
    {
        this.route = new RouteConfig();
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_IndexNo   本方法要执行的执行序号。下标从1开始
     * @param io_Default  默认值类型的变量信息
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(int i_IndexNo ,Map<String ,Object> io_Default ,Map<String ,Object> io_Context)
    {
        return null;
    }
    
    
    /**
     * 获取：执行对象的XID
     */
    public String getCallXID()
    {
        return callXID;
    }

    
    /**
     * 设置：执行对象的XID
     * 
     * @param i_CallXID 执行对象的XID
     */
    public void setCallXID(String i_CallXID)
    {
        this.callXID = i_CallXID;
    }

    
    /**
     * 获取：执行方法名称
     */
    public String getCallMehod()
    {
        return callMehod;
    }

    
    /**
     * 设置：执行方法名称
     * 
     * @param i_CallMehod 执行方法名称
     */
    public void setCallMehod(String i_CallMehod)
    {
        this.callMehod = i_CallMehod;
    }

    
    /**
     * 获取：执行方法的参数
     */
    public List<NodeParam> getCallParams()
    {
        return callParams;
    }

    
    /**
     * 设置：执行方法的参数
     * 
     * @param i_CallParams 执行方法的参数
     */
    public void setCallParams(List<NodeParam> i_CallParams)
    {
        this.callParams = i_CallParams;
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
    public void setReturnID(String i_ReturnID)
    {
        this.returnID = i_ReturnID;
    }
    
    
    /**
     * 获取：路由
     */
    public RouteConfig getRoute()
    {
        return route;
    }

    
    /**
     * 设置：路由
     * 
     * @param i_Route 路由
     */
    public void setRoute(RouteConfig i_Route)
    {
        this.route = i_Route;
    }


    /**
     * 获取：全局惟一标识ID
     */
    public String getXid()
    {
        return xid;
    }

    
    /**
     * 设置：全局惟一标识ID
     * 
     * @param i_Xid 全局惟一标识ID
     */
    public void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }


    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_XJavaID)
    {
        this.xid = i_XJavaID;
    }
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    public String getXJavaID()
    {
        return this.xid;
    }
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     *
     * @return
     */
    public String getComment()
    {
        return this.comment;
    }
    
}
