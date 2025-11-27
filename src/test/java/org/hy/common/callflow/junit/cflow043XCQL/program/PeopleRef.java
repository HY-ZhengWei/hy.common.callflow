package org.hy.common.callflow.junit.cflow043XCQL.program;





/**
 * 人物关系
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-26
 * @version     v1.0
 */
public class PeopleRef
{
    
    /** 人物名称 */
    private String name;
    
    /** 关联关系人物名称 */
    private String refName;
    
    /** 关系说明 */
    private String comment;

    
    
    /**
     * 获取：人物名称
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * 设置：人物名称
     * 
     * @param i_Name 人物名称
     */
    public void setName(String i_Name)
    {
        this.name = i_Name;
    }

    
    /**
     * 获取：关联关系人物名称
     */
    public String getRefName()
    {
        return refName;
    }

    
    /**
     * 设置：关联关系人物名称
     * 
     * @param i_RefName 关联关系人物名称
     */
    public void setRefName(String i_RefName)
    {
        this.refName = i_RefName;
    }

    
    /**
     * 获取：关系说明
     */
    public String getComment()
    {
        return comment;
    }

    
    /**
     * 设置：关系说明
     * 
     * @param i_Comment 关系说明
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
}
