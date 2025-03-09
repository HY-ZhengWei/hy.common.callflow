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
     * 获取：归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     */
    protected ExecuteElement gatOwner()
    {
        return owner;
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
     * @param i_RouteItem  路由项
     */
    public void setSucceed(RouteItem i_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.succeeds = new ArrayList<RouteItem>();
            }
        }
        
        this.succeeds.add(i_RouteItem);
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
     * @param i_RouteItem  路由项
     */
    public void setFailed(RouteItem i_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.faileds) )
            {
                this.faileds = new ArrayList<RouteItem>();
            }
        }
        
        this.faileds.add(i_RouteItem);
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
     * @param i_RouteItem  路由项
     */
    public void setException(RouteItem i_RouteItem)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.exceptions) )
            {
                this.exceptions = new ArrayList<RouteItem>();
            }
        }
        
        this.exceptions.add(i_RouteItem);
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
    
}
