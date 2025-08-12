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
    
    /** 创建人编号 */
    private String  createUserID;
    
    /** 修改者编号 */
    private String  updateUserID;
    
    /** 删除标记。1删除；0未删除 */
    private Integer isDel;
    
    /** 注解说明 */
    private String  comment;
           
    
    
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


    
    /**
     * 获取：创建人编号
     */
    public String getCreateUserID()
    {
        return createUserID;
    }


    
    /**
     * 设置：创建人编号
     * 
     * @param i_CreateUserID 创建人编号
     */
    public void setCreateUserID(String i_CreateUserID)
    {
        this.createUserID = i_CreateUserID;
    }


    
    /**
     * 获取：修改者编号
     */
    public String getUpdateUserID()
    {
        return updateUserID;
    }


    
    /**
     * 设置：修改者编号
     * 
     * @param i_UpdateUserID 修改者编号
     */
    public void setUpdateUserID(String i_UpdateUserID)
    {
        this.updateUserID = i_UpdateUserID;
    }


    
    /**
     * 获取：删除标记。1删除；0未删除
     */
    public Integer getIsDel()
    {
        return isDel;
    }


    
    /**
     * 设置：删除标记。1删除；0未删除
     * 
     * @param i_IsDel 删除标记。1删除；0未删除
     */
    public void setIsDel(Integer i_IsDel)
    {
        this.isDel = i_IsDel;
    }


    
    /**
     * 获取：注解说明
     */
    public String getComment()
    {
        return comment;
    }


    
    /**
     * 设置：注解说明
     * 
     * @param i_Comment 注解说明
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
}
