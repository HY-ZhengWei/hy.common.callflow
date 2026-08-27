package org.hy.common.callflow.milvus;

import java.io.Serializable;

import org.hy.common.Date;
import org.hy.common.xml.plugins.XSQLFilter;





/**
 * XMilvus 执行日志
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-14
 * @version     v1.0
 */
public class XMilvusLog implements Serializable
{
    
    private static final long serialVersionUID = -2257925651499128926L;
    
    

    /** XMilvus 的唯一标识ID */
    private String oid;
    
    /** 执行语句 */
    private String content;
    
    /** 执行时间。一般执行完成时的时间，或出现异常时的时间 */
    private String time;
    
    /** 执行异常信息 */
    private String e;
    
    
    
    public XMilvusLog(String i_Content)
    {
        this.time    = Date.getNowTime().getFullMilli();
        this.content = i_Content;
        this.e       = "";
        
        this.logXMilvus();
    }
    
    
    
    public XMilvusLog(String i_Content ,Exception i_Exce ,String i_XSQLObjectID)
    {
        this.time    = Date.getNowTime().getFullMilli();
        this.content = i_Content;
        this.oid     = i_XSQLObjectID;
        
        if ( i_Exce != null )
        {
            this.e = i_Exce.getMessage();
        }
        
        this.logXMilvus();
    }
    
    
    
    private void logXMilvus()
    {
        XSQLFilter.logXSQL(Thread.currentThread().getId() ,this.content);
    }
    
    
    
    /**
     * 获取：执行S语句
     */
    public String getContent()
    {
        return content;
    }

    
    /**
     * 设置：执行语句
     * 
     * @param i_Content
     */
    public void setContent(String i_Content)
    {
        this.content = i_Content;
    }

    
    /**
     * 获取：执行时间
     */
    public String getTime()
    {
        return time;
    }

    
    /**
     * 设置：执行时间
     * 
     * @param time
     */
    public void setTime(String time)
    {
        this.time = time;
    }

    
    /**
     * 获取：执行异常信息
     */
    public String getE()
    {
        return e;
    }

    
    /**
     * 设置：执行异常信息
     * 
     * @param error
     */
    public void setE(String e)
    {
        this.e = e;
    }

    
    /**
     * 获取：XMilvus 的唯一标识ID
     */
    public String getOid()
    {
        return oid;
    }

    
    /**
     * 设置：XMilvus 的唯一标识ID
     * 
     * @param oid
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }
    
}
