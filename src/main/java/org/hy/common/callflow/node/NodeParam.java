package org.hy.common.callflow.node;

import org.hy.common.XJavaID;




/**
 * 节点参数
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeParam implements XJavaID
{
    
    /** 全局惟一标识ID */
    private String   xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String   comment;
    
    /** 参数类型。仅为数值类型时才生效 */
    private Class<?> valueClass;
    
    /** 参数数值。可以是数值、变量、XID标识 */
    private String   value;
    
    
    
    /**
     * 获取：参数类型。仅为数值类型时才生效
     */
    public Class<?> getValueClass()
    {
        return valueClass;
    }

    
    /**
     * 设置：参数类型。仅为数值类型时才生效
     * 
     * @param i_ValueClass 参数类型。仅为数值类型时才生效
     */
    public void setValueClass(Class<?> i_ValueClass)
    {
        this.valueClass = i_ValueClass;
    }

    
    /**
     * 获取：参数数值。可以是数值、变量、XID标识
     */
    public String getValue()
    {
        return value;
    }

    
    /**
     * 设置：参数数值。可以是数值、变量、XID标识
     * 
     * @param i_Value 参数数值。可以是数值、变量、XID标识
     */
    public void setValue(String i_Value)
    {
        this.value = i_Value;
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
    
}
