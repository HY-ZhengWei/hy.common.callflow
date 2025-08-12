package org.hy.common.callflow.junit.cflow028.program;

import org.hy.common.Date;





/**
 * 应用租户
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-06-25
 * @version     v1.0
 */
public class AppConfig
{
    
    /** 主键 */
    private String  id;
    
    /** 逻辑ID */
    private String  xid;
    
    /** 应用租户名称 */
    private String  appName;
    
    /** 创建时间 */
    private Date    createTime;
    
    /** 修改时间 */
    private Date    updateTime;
           
    
    
    /**
     * 获取：主键
     */
    public String getId()
    {
        return id;
    }

    
    
    /**
     * 设置：主键
     * 
     * @param i_Id 主键
     */
    public void setId(String i_Id)
    {
        this.id = i_Id;
    }

    
    
    /**
     * 获取：逻辑ID
     */
    public String getXid()
    {
        return xid;
    }

    
    
    /**
     * 设置：逻辑ID
     * 
     * @param i_Xid 逻辑ID
     */
    public void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }

    
    
    /**
     * 获取：应用租户名称
     */
    public String getAppName()
    {
        return appName;
    }

    
    
    /**
     * 设置：应用租户名称
     * 
     * @param i_AppName 应用租户名称
     */
    public void setAppName(String i_AppName)
    {
        this.appName = i_AppName;
    }
    
    
    
    /**
     * 获取：创建时间
     */
    public Date getCreateTime()
    {
        return createTime;
    }
    
    
    
    /**
     * 设置：创建时间
     * 
     * @param createTime
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
    
    
    
    /**
     * 获取：修改时间
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    
    
    /**
     * 设置：修改时间
     * 
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
    
}
