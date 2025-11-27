package org.hy.common.callflow.junit.cflow043XCQL.program;





/**
 * 人物信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-26
 * @version     v1.0
 */
public class PeopleInfo
{
    
    /** 人物名称 */
    private String name;
    
    /** 身份、别称 */
    private String nickName;
    
    /** 角色定位 */
    private String roleName;
    
    /** 团队名称 */
    private String groupName;
    
    
    
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
     * 获取：身份、别称
     */
    public String getNickName()
    {
        return nickName;
    }

    
    /**
     * 设置：身份、别称
     * 
     * @param i_NickName 身份、别称
     */
    public void setNickName(String i_NickName)
    {
        this.nickName = i_NickName;
    }

    
    /**
     * 获取：角色定位
     */
    public String getRoleName()
    {
        return roleName;
    }

    
    /**
     * 设置：角色定位
     * 
     * @param i_RoleName 角色定位
     */
    public void setRoleName(String i_RoleName)
    {
        this.roleName = i_RoleName;
    }

    
    /**
     * 获取：团队名称
     */
    public String getGroupName()
    {
        return groupName;
    }

    
    /**
     * 设置：团队名称
     * 
     * @param i_GroupName 团队名称
     */
    public void setGroupName(String i_GroupName)
    {
        this.groupName = i_GroupName;
    }
    
}
