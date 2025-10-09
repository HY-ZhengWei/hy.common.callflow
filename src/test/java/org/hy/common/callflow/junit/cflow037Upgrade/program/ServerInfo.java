package org.hy.common.callflow.junit.cflow037Upgrade.program;





/**
 * 服务器信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-29
 * @version     v1.0
 */
public class ServerInfo
{
    
    /** 服务器IP地址 */
    private String ip;
    
    /** 服务器用户名称 */
    private String user;
    
    /** 服务器用户密码 */
    private String pwd;
    
    /** 服务器类型 */
    private String type;

    
    
    /**
     * 获取：服务器IP地址
     */
    public String getIp()
    {
        return ip;
    }

    
    /**
     * 设置：服务器IP地址
     * 
     * @param i_Ip 服务器IP地址
     */
    public void setIp(String i_Ip)
    {
        this.ip = i_Ip;
    }

    
    /**
     * 获取：服务器用户名称
     */
    public String getUser()
    {
        return user;
    }

    
    /**
     * 设置：服务器用户名称
     * 
     * @param i_User 服务器用户名称
     */
    public void setUser(String i_User)
    {
        this.user = i_User;
    }

    
    /**
     * 获取：服务器用户密码
     */
    public String getPwd()
    {
        return pwd;
    }

    
    /**
     * 设置：服务器用户密码
     * 
     * @param i_Pwd 服务器用户密码
     */
    public void setPwd(String i_Pwd)
    {
        this.pwd = i_Pwd;
    }

    
    /**
     * 获取：服务器类型
     */
    public String getType()
    {
        return type;
    }

    
    /**
     * 设置：服务器类型
     * 
     * @param i_Type 服务器类型
     */
    public void setType(String i_Type)
    {
        this.type = i_Type;
    }
    
}
