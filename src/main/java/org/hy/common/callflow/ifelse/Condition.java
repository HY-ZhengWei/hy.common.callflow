package org.hy.common.callflow.ifelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.XJavaID;
import org.hy.common.callflow.enums.Logical;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.route.RouteConfig;
import org.hy.common.xml.log.Logger;





/**
 * 条件逻辑
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 * @param <V>
 */
public class Condition implements IExecute ,IfElse ,XJavaID
{
    
    private static final Logger $Logger = new Logger(Condition.class);
    
    
    
    /** 全局惟一标识ID */
    private String       xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String       comment;
    
    /** 逻辑 */
    private Logical      logical;
    
    /** 条件项或嵌套条件逻辑 */
    private List<IfElse> items;
    
    /** 路由 */
    private RouteConfig  route;
    
    
    
    public Condition()
    {
        this.items = new ArrayList<>();
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
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public boolean allow(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        if ( this.logical == null )
        {
            $Logger.warn("Condition logical [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
            return false;
        }
        if ( Help.isNull(this.items) )
        {
            $Logger.warn("Condition items [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
            return false;
        }
        
        // 与
        if ( Logical.And.equals(this.logical) )
        {
            for (IfElse v_Item : this.items)
            {
                if ( v_Item == null )
                {
                    $Logger.warn("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
                }
                else
                {
                    boolean v_ChildRet = v_Item.allow(i_Default ,i_Context);
                    if ( !v_ChildRet )
                    {
                        return false;
                    }
                }
            }
        }
        // 或
        else if ( Logical.Or.equals(this.logical) )
        {
            for (IfElse v_Item : this.items)
            {
                if ( v_Item == null )
                {
                    $Logger.warn("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
                }
                else
                {
                    boolean v_ChildRet = v_Item.allow(i_Default ,i_Context);
                    if ( v_ChildRet )
                    {
                        return true;
                    }
                }
            }
        }
        
        return true;
    }
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default
     * @param i_Context
     * @return
     */
    public boolean reject(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        return !allow(i_Default ,i_Context);
    }
    
    
    /**
     * 添加条件项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param <V>
     * @param i_Item
     */
    public boolean add(ConditionItem i_Item)
    {
        if ( i_Item != null )
        {
            this.items.add(i_Item);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 添加嵌套条件逻辑
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Item
     */
    public boolean add(Condition i_Item)
    {
        if ( i_Item != null  )
        {
            this.items.add(i_Item);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 获取：逻辑
     */
    public Logical getLogical()
    {
        return logical;
    }

    
    /**
     * 设置：逻辑
     * 
     * @param i_Logical 逻辑
     */
    public void setLogical(Logical i_Logical)
    {
        this.logical = i_Logical;
    }
    
    
    /**
     * 获取：条件项或嵌套条件逻辑
     */
    public List<IfElse> getItems()
    {
        return items;
    }
    
    
    /**
     * 设置：条件项或嵌套条件逻辑
     * 
     * @param i_Items 条件项或嵌套条件逻辑
     */
    public void setItems(List<IfElse> i_Items)
    {
        this.items = i_Items;
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
