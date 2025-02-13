package org.hy.common.callflow.ifelse;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.XJavaID;
import org.hy.common.callflow.enums.Comparer;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





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
    
    private static final Logger $Logger = new Logger(ConditionItem.class);
    
    private static final String $Split  = ".";
    
    
    
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
     * @return
     */
    public boolean allow(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        if ( this.comparer == null )
        {
            $Logger.warn("ConditionItem comparer [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
            return false;
        }
        if ( this.valueXIDA == null )
        {
            $Logger.warn("ConditionItem valueA [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
            return false;
        }
        if ( this.valueXIDB == null )
        {
            $Logger.warn("ConditionItem valueB [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
            return false;
        }
        
        Object v_ValueA = this.getValue(this.valueXIDA ,i_Default ,i_Context);
        Object v_ValueB = this.getValue(this.valueXIDB ,i_Default ,i_Context);
        
        return this.comparer.compare(v_ValueA ,v_ValueB);
    }
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default
     * @param i_Context
     * @return
     */
    public boolean reject(Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        return !allow(i_Default ,i_Context);
    }
    
    
    /**
     * 从默认值区、上下文区、全局区三个区中取值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_ValueXID 数值、变量、XID标识（支持xxx.yyy.www）
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    private Object getValue(String i_ValueXID ,Map<String ,Object> i_Default ,Map<String ,Object> i_Context)
    {
        Object v_Value = i_ValueXID;
        if ( i_ValueXID.startsWith(DBSQL.$Placeholder) )
        {
            String v_ValueID = i_ValueXID.trim().substring(DBSQL.$Placeholder.length());
            String v_YYYZZZ  = null;
            int    v_Index   = v_ValueID.indexOf("\\" + $Split);
            if ( v_Index > 0 )
            {
                if ( v_Index + 1 < v_ValueID.length() )
                {
                    v_YYYZZZ = v_ValueID.substring(v_Index + 1);
                }
                v_ValueID = v_ValueID.substring(0 ,v_Index);
            }
            
            // 尝试从默认值区取值
            if ( !Help.isNull(i_Default) )
            {
                v_Value = i_Default.get(v_ValueID);
                if ( v_Value != null )
                {
                    if ( v_YYYZZZ != null )
                    {
                        return this.getYYYZZZ(v_Value ,v_YYYZZZ);
                    }
                    else
                    {
                        return v_Value;
                    }
                }
            }
            
            // 尝试从上下文区取值
            if ( !Help.isNull(i_Context) )
            {
                v_Value = i_Context.get(v_ValueID);
                if ( v_Value != null )
                {
                    if ( v_YYYZZZ != null )
                    {
                        return this.getYYYZZZ(v_Value ,v_YYYZZZ);
                    }
                    else
                    {
                        return v_Value;
                    }
                }
            }
            
            // 尝试从全局区取值
            v_Value = XJava.getObject(v_ValueID);
            if ( v_Value != null )
            {
                if ( v_YYYZZZ != null )
                {
                    return this.getYYYZZZ(v_Value ,v_YYYZZZ);
                }
                else
                {
                    return v_Value;
                }
            }
        }
        
        return v_Value;
    }
    
    
    /**
     * 从面向对象的方法路径中 xxx.yyy.www 获取数值 
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Object  顶级实例
     * @param i_YYYZZZ  方法路径
     * @return
     */
    private Object getYYYZZZ(Object i_Object ,String i_YYYZZZ)
    {
        try
        {
            MethodReflect v_MR = new MethodReflect(i_Object ,i_YYYZZZ ,true ,MethodReflect.$NormType_Getter);
            return v_MR.invokeForInstance(i_Object);
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,"ConditionItem get[" + i_YYYZZZ + "] for [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is error.");
            return null;
        }
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
    
}
