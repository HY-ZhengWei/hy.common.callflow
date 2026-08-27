package org.hy.common.callflow.junit.cflow049Milvus.program;

import org.hy.common.XJavaID;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-24
 * @version     v1.0
 */
public class Program implements XJavaID
{
    
    /** 主键 */
    private String  id;
    
    /** 逻辑ID */
    private String  xid;
           
    /** 备注说明 */
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
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    @Override
    public void setXJavaID(String i_XJavaID)
    {
        this.xid = i_XJavaID;
    }
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    @Override
    public String getXJavaID()
    {
        return this.xid;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment
     */
    @Override
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     *
     * @return
     */
    @Override
    public String getComment()
    {
        return this.comment;
    }
    
}
