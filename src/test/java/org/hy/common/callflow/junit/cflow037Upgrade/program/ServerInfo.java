package org.hy.common.callflow.junit.cflow037Upgrade.program;

import org.hy.common.Help;





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
    
    /** 服务器的设备直联IP地址 */
    private String ipDevice1;
    
    /** 服务器的设备直联IP地址 */
    private String ipDevice2;
    
    /** 服务器的设备直联IP地址 */
    private String ipDevice3;
    
    /** 服务器用户名称 */
    private String user;
    
    /** 服务器用户密码 */
    private String pwd;
    
    /** 服务器类型 */
    private String type;
    
    /** 微服务类型 */
    private String msType;

    
    
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
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice1Help()
    {
        return Help.NVL(ipDevice1 ,"192.168.99.101");
    }
    
    
    /**
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice1()
    {
        return ipDevice1;
    }

    
    /**
     * 设置：服务器的设备直联IP地址
     * 
     * @param i_IpDevice1 服务器的设备直联IP地址
     */
    public void setIpDevice1(String i_IpDevice1)
    {
        this.ipDevice1 = i_IpDevice1;
    }

    
    /**
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice2Help()
    {
        return Help.NVL(ipDevice2 ,"192.168.99.102");
    }
    
    
    /**
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice2()
    {
        return ipDevice2;
    }

    
    /**
     * 设置：服务器的设备直联IP地址
     * 
     * @param i_IpDevice2 服务器的设备直联IP地址
     */
    public void setIpDevice2(String i_IpDevice2)
    {
        this.ipDevice2 = i_IpDevice2;
    }

    
    /**
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice3Help()
    {
        return Help.NVL(ipDevice3 ,"192.168.99.103");
    }
    
    
    /**
     * 获取：服务器的设备直联IP地址
     */
    public String getIpDevice3()
    {
        return ipDevice3;
    }

    
    /**
     * 设置：服务器的设备直联IP地址
     * 
     * @param i_IpDevice3 服务器的设备直联IP地址
     */
    public void setIpDevice3(String i_IpDevice3)
    {
        this.ipDevice3 = i_IpDevice3;
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

    
    /**
     * 获取：微服务类型
     */
    public String getMsType()
    {
        return msType;
    }

    
    /**
     * 设置：微服务类型
     * 
     * @param i_MsType 微服务类型
     */
    public void setMsType(String i_MsType)
    {
        this.msType = i_MsType;
    }
    
}
