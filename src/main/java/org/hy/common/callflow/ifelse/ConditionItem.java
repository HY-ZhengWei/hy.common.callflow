package org.hy.common.callflow.ifelse;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.XJavaID;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.Comparer;





/**
 * 条件项
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 * @param <V>
 */
public class ConditionItem implements IfElse ,XJavaID
{
    
    /** 全局惟一标识ID */
    private String   xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String   comment;
    
    /** 比较器 */
    private Comparer comparer;
    
    /** 数值A、变量A、XID标识A（支持xxx.yyy.www） */
    private String   valueXIDA;
    
    /** 数值B、变量B、XID标识B（支持xxx.yyy.www） */
    private String   valueXIDB;
    
    
    
    public ConditionItem()
    {
        
    }
    
    
    
    public ConditionItem(Comparer i_Comparer ,String i_ValueXIDA ,String i_ValueXIDB)
    {
        this.comparer  = i_Comparer;
        this.valueXIDA = i_ValueXIDA;
        this.valueXIDB = i_ValueXIDB;
    }
    
    
    
    /**
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     */
    public boolean allow(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        if ( this.comparer == null )
        {
            throw new NullPointerException("ConditionItem comparer [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        if ( this.valueXIDA == null )
        {
            throw new NullPointerException("ConditionItem valueA [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        if ( this.valueXIDB == null )
        {
            throw new NullPointerException("ConditionItem valueB [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        
        Object v_ValueA = ValueHelp.getValue(this.valueXIDA ,String.class ,i_Default ,i_Context);
        Object v_ValueB = ValueHelp.getValue(this.valueXIDB ,String.class ,i_Default ,i_Context);
        
        return this.comparer.compare(v_ValueA ,v_ValueB);
    }
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     */
    public boolean reject(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        return !allow(i_Default ,i_Context);
    }
    
    
    
    /**
     * 获取：比较器
     */
    public Comparer getComparer()
    {
        return comparer;
    }

    
    /**
     * 设置：比较器
     * 
     * @param i_Comparer 比较器
     */
    public void setComparer(Comparer i_Comparer)
    {
        this.comparer = i_Comparer;
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
     * 获取：数值A、变量A、XID标识A（支持xxx.yyy.www）
     */
    public String getValueXIDA()
    {
        return valueXIDA;
    }

    
    /**
     * 设置：数值A、变量A、XID标识A（支持xxx.yyy.www）
     * 
     * @param i_ValueXIDA 数值A、变量A、XID标识A（支持xxx.yyy.www）
     */
    public void setValueXIDA(String i_ValueXIDA)
    {
        this.valueXIDA = i_ValueXIDA;
    }

    
    /**
     * 获取：数值B、变量B、XID标识B（支持xxx.yyy.www）
     */
    public String getValueXIDB()
    {
        return valueXIDB;
    }

    
    /**
     * 设置：数值B、变量B、XID标识B（支持xxx.yyy.www）
     * 
     * @param i_ValueXIDB 数值B、变量B、XID标识B（支持xxx.yyy.www）
     */
    public void setValueXIDB(String i_ValueXIDB)
    {
        this.valueXIDB = i_ValueXIDB;
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


    /**
     * 转为表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.comparer != null )
        {
            v_Builder.append(this.valueXIDA == null ? "NULL" : this.valueXIDA);
            v_Builder.append(" ").append(this.comparer.getValue()).append(" ");
            v_Builder.append(this.valueXIDA == null ? "NULL" : this.valueXIDB);
        }
        
        return v_Builder.toString();
    }
    
}
