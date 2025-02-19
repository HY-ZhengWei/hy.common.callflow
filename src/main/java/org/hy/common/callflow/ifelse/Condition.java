package org.hy.common.callflow.ifelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Total;
import org.hy.common.XJavaID;
import org.hy.common.callflow.enums.Logical;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.route.RouteConfig;





/**
 * 条件逻辑
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 * @param <V>
 */
public class Condition extends Total implements IExecute ,IfElse ,XJavaID
{
    
    /** 全局惟一标识ID */
    private String       xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String       comment;
    
    /** 逻辑 */
    private Logical      logical;
    
    /** 条件项或嵌套条件逻辑 */
    private List<IfElse> items;
    
    /** 为返回值定义的变量ID */
    private String       returnID;
    
    /** 路由 */
    private RouteConfig  route;
    
    
    
    public Condition()
    {
        this(0L ,0L);
    }
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public Condition(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
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
        ExecuteResult v_Result = new ExecuteResult(i_IndexNo ,this.xid);
        
        try
        {
            long    v_BeginTime = this.request().getTime();
            boolean v_ExceRet   = this.allow(io_Default ,io_Context);
            
            if ( !Help.isNull(this.returnID) )
            {
                io_Context.put(this.returnID ,v_ExceRet);
            }
            
            this.success(Date.getNowTime().getTime() - v_BeginTime);
            v_Result.setResult(v_ExceRet);
            return v_Result;
        }
        catch (Exception exce)
        {
            return v_Result.setException(exce);
        }
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
     * @return           返回判定结果或抛出异常
     */
    public boolean allow(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        if ( this.logical == null )
        {
            throw new NullPointerException("Condition logical [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        if ( Help.isNull(this.items) )
        {
            throw new NullPointerException("Condition items [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        
        // 与
        if ( Logical.And.equals(this.logical) )
        {
            for (IfElse v_Item : this.items)
            {
                if ( v_Item == null )
                {
                    throw new NullPointerException("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
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
                    throw new NullPointerException("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
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
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
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
    public boolean setItem(ConditionItem i_Item)
    {
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        
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
    public boolean setItem(Condition i_Item)
    {
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        
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
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
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
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
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
    
    
    /**
     * 转为表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.logical != null && !Help.isNull(this.items) )
        {
            for (int x=0; x<this.items.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ").append(this.logical.getValue()).append(" ");
                }
                
                IfElse v_Item = this.items.get(x);
                if ( v_Item instanceof Condition )
                {
                    v_Builder.append("(").append(v_Item.toString()).append(")");
                }
                else
                {
                    v_Builder.append(v_Item.toString());
                }
            }
        }
        
        return v_Builder.toString();
    }
    
}
