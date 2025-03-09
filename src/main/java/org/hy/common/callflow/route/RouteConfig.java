package org.hy.common.callflow.route;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.execute.ExecuteElement;





/**
 * 路由配置 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class RouteConfig
{
    
    /** 归属者（仅对外开放setter方法，为防止死循环）（内部使用） */
    private ExecuteElement  owner;
    
    /** 执行成功后的路由 */
    private List<RouteItem> succeeds;
    
    /** 执行失败后的路由（可选） */
    private List<RouteItem> faileds;
    
    /** 执行异常后的路由（可选） */
    private List<RouteItem> exceptions;
    
    
    
    public RouteConfig(ExecuteElement i_Owner)
    {
        this.setOwner(i_Owner);
    }
    
    
    
    /**
     * 归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @param i_Owner
     */
    public void setOwner(ExecuteElement i_Owner)
    {
        this.owner = i_Owner;
    }
    
    
    
    /**
     * 检查自循环，禁止自循环
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     */
    protected void checkSelfLink(ExecuteElement i_Execute)
    {
        if ( i_Execute == null )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(owner.getXid()) + ":" + Help.NVL(owner.getComment()) + "] add next route Element is null.");
        }
        else if ( this.owner.equals(i_Execute) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(i_Execute.getXid()) + ":" + Help.NVL(i_Execute.getComment()) + "] Not allowed to self link.");
        }
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getIf()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * setSucceed方法的别名，主要用于 "条件逻辑" 判定结果为真时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_RouteItem  路由项
     */
    public void setIf(RouteItem i_RouteItem)
    {
        this.setSucceed(i_RouteItem);
    }
    
    
    /**
     * setFailed方法的别名，主要用于 "条件逻辑" 判定结果为假时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_RouteItem  路由项
     */
    public void setElse(RouteItem i_RouteItem)
    {
        this.setFailed(i_RouteItem);
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getElse()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * 添加执行成功后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param io_RouteItem  路由项
     */
    public void setSucceed(RouteItem io_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.succeeds = new ArrayList<RouteItem>();
            }
        }
        
        this.checkSelfLink(io_RouteItem.getNext());
        io_RouteItem.getNext().setPrevious(this.owner);
        this.succeeds.add(io_RouteItem);
        this.orderBy();
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getSucceed()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * 添加执行失败后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param io_RouteItem  路由项
     */
    public void setFailed(RouteItem io_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.faileds) )
            {
                this.faileds = new ArrayList<RouteItem>();
            }
        }
        
        this.checkSelfLink(io_RouteItem.getNext());
        io_RouteItem.getNext().setPrevious(this.owner);
        this.faileds.add(io_RouteItem);
        this.orderBy();
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getFailed()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * setException方法的别名，添加执行异常后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_RouteItem  路由项
     */
    public void setError(RouteItem i_RouteItem)
    {
        this.setException(i_RouteItem);
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getError()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * 添加执行异常后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param io_RouteItem  路由项
     */
    public void setException(RouteItem io_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.exceptions) )
            {
                this.exceptions = new ArrayList<RouteItem>();
            }
        }
        
        this.checkSelfLink(io_RouteItem.getNext());
        io_RouteItem.getNext().setPrevious(this.owner);
        this.exceptions.add(io_RouteItem);
        this.orderBy();
    }
    
    
    /**
     * 获取一个新的路由项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public RouteItem getException()
    {
        return new RouteItem(this);
    }
    
    
    /**
     * getSucceeds方法的别名，主要用于 "条件逻辑" 判定结果为真时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     * 
     * @return
     */
    public List<RouteItem> getIfs()
    {
        return getSucceeds();
    }
    
    
    /**
     * setSucceeds方法的别名，主要用于 "条件逻辑" 判定结果为真时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_Ifs  真值后的路由
     */
    public void setIfs(List<RouteItem> i_Ifs)
    {
        setSucceeds(i_Ifs);
    }
    
    
    /**
     * 获取：执行成功后的路由
     */
    public List<RouteItem> getSucceeds()
    {
        return succeeds;
    }

    
    /**
     * 设置：执行成功后的路由
     * 
     * @param i_Succeeds 执行成功后的路由
     */
    public void setSucceeds(List<RouteItem> i_Succeeds)
    {
        if ( Help.isNull(i_Succeeds) )
        {
            this.succeeds = null;
        }
        else
        {
            for (RouteItem v_Item : i_Succeeds)
            {
                this.setSucceed(v_Item);
            }
        }
    }
    
    
    /**
     * getFaileds方法的别名，主要用于 "条件逻辑" 判定结果为假时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public List<RouteItem> getElses()
    {
        return getFaileds();
    }
    
    
    /**
     * setFaileds方法的别名，主要用于 "条件逻辑" 判定结果为假时路由配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_Elses  假值后的路由（可选）
     */
    public void setElses(List<RouteItem> i_Elses)
    {
        setFaileds(i_Elses);
    }

    
    /**
     * 获取：执行失败后的路由（可选）
     */
    public List<RouteItem> getFaileds()
    {
        return faileds;
    }

    
    /**
     * 设置：执行失败后的路由（可选）
     * 
     * @param i_Faileds 执行失败后的路由（可选）
     */
    public void setFaileds(List<RouteItem> i_Faileds)
    {
        if ( Help.isNull(i_Faileds) )
        {
            this.faileds = null;
        }
        else
        {
            for (RouteItem v_Item : i_Faileds)
            {
                this.setFailed(v_Item);
            }
        }
    }
    
    
    /**
     * getExceptions方法的别名，
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @return
     */
    public List<RouteItem> getErrors()
    {
        return getExceptions();
    }
    
    
    /**
     * setExceptions方法的别名，
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_Errors  执行异常后的路由（可选）
     */
    public void setErrors(List<RouteItem> i_Errors)
    {
        setExceptions(i_Errors);
    }
    
    
    /**
     * 获取：执行异常后的路由（可选）
     */
    public List<RouteItem> getExceptions()
    {
        return exceptions;
    }

    
    /**
     * 设置：执行异常后的路由（可选）
     * 
     * @param i_Exceptions 执行异常后的路由（可选）
     */
    public void setExceptions(List<RouteItem> i_Exceptions)
    {
        if ( Help.isNull(i_Exceptions) )
        {
            this.exceptions = null;
        }
        else
        {
            for (RouteItem v_Item : i_Exceptions)
            {
                this.setException(v_Item);
            }
        }
    }
    
    
    /**
     * 排序，将自引用排在前面
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     */
    protected synchronized void orderBy()
    {
        this.orderBy(this.succeeds);
        this.orderBy(this.faileds);
        this.orderBy(this.exceptions);
    }
    
    
    /**
     * 排序，将自引用排在前面
     * 
     * 注：应当每添加一个路由时执行此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param io_RouteItems  路由集合
     */
    private void orderBy(List<RouteItem> io_RouteItems) 
    {
        if ( Help.isNull(io_RouteItems) || io_RouteItems.size() == 1 )
        {
            return;
        }
        
        RouteItem v_Last = io_RouteItems.get(io_RouteItems.size() - 1);
        if ( v_Last.getNext() == null || !(v_Last.getNext() instanceof SelfLoop) )
        {
            return;
        }
        
        // 定位最后的自引用
        int v_LastSelfLoopIndex = 0;
        for (int x=0; x<io_RouteItems.size(); x++)
        {
            RouteItem v_RouteItem = io_RouteItems.get(x);
            if ( v_RouteItem.getNext() == null || !(v_RouteItem.getNext() instanceof SelfLoop) )
            {
                v_LastSelfLoopIndex = x;
                break;
            }
        }
        
        // 所有非自引用的向后排
        for (int x=io_RouteItems.size()-2; x>=v_LastSelfLoopIndex; x--)
        {
            io_RouteItems.set(x + 1 ,io_RouteItems.get(x));
        }
        
        // 将最后引用向前移
        io_RouteItems.set(v_LastSelfLoopIndex ,v_Last);
    }
    
}
