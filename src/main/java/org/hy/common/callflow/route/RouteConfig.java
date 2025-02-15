package org.hy.common.callflow.route;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.execute.IExecute;





/**
 * 路由配置 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class RouteConfig
{
    
    
    /** 执行成功后的路由 */
    private List<IExecute> succeeds;
    
    /** 执行失败后的路由（可选） */
    private List<IExecute> faileds;
    
    /** 执行异常后的路由（可选） */
    private List<IExecute> exceptions;

    
    
    /**
     * 添加执行成功后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_Execute  执行对象。节点或判定条件
     */
    public synchronized void addSucceed(IExecute i_Execute)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.succeeds = new ArrayList<IExecute>();
            }
        }
        
        this.succeeds.add(i_Execute);
    }
    
    
    /**
     * 添加执行失败后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_Execute  执行对象。节点或判定条件
     */
    public synchronized void addFailed(IExecute i_Execute)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.faileds = new ArrayList<IExecute>();
            }
        }
        
        this.faileds.add(i_Execute);
    }
    
    
    /**
     * 添加执行异常后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_Execute  执行对象。节点或判定条件
     */
    public synchronized void addException(IExecute i_Execute)
    {
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.exceptions = new ArrayList<IExecute>();
            }
        }
        
        this.exceptions.add(i_Execute);
    }
    
    
    /**
     * 获取：执行成功后的路由
     */
    public List<IExecute> getSucceeds()
    {
        return succeeds;
    }

    
    /**
     * 设置：执行成功后的路由
     * 
     * @param i_Succeeds 执行成功后的路由
     */
    public void setSucceeds(List<IExecute> i_Succeeds)
    {
        this.succeeds = i_Succeeds;
    }

    
    /**
     * 获取：执行失败后的路由（可选）
     */
    public List<IExecute> getFaileds()
    {
        return faileds;
    }

    
    /**
     * 设置：执行失败后的路由（可选）
     * 
     * @param i_Faileds 执行失败后的路由（可选）
     */
    public void setFaileds(List<IExecute> i_Faileds)
    {
        this.faileds = i_Faileds;
    }

    
    /**
     * 获取：执行异常后的路由（可选）
     */
    public List<IExecute> getExceptions()
    {
        return exceptions;
    }

    
    /**
     * 设置：执行异常后的路由（可选）
     * 
     * @param i_Exceptions 执行异常后的路由（可选）
     */
    public void setExceptions(List<IExecute> i_Exceptions)
    {
        this.exceptions = i_Exceptions;
    }
    
}
