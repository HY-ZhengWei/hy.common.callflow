package org.hy.common.callflow.ifelse;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.XJavaID;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.Comparer;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.xml.log.Logger;





/**
 * 条件项
 * 
 * 支持两种形式：
 *      if ( A == B ) 的形式
 *      if ( A )      的形式。即NULL值的判定，或逻辑值的判定
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 */
public class ConditionItem implements IfElse ,XJavaID
{
    private static final Logger $Logger = new Logger(ConditionItem.class);
    
    
    
    /** 全局惟一标识ID */
    private String   xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String   comment;
    
    /** 比较器 */
    private Comparer comparer;
    
    /** 参数类型。参数为数值类型时生效 */
    private Class<?> valueClass;
    
    /** 数值A、上下文变量A、XID标识A（支持xxx.yyy.www） */
    private String   valueXIDA;
    
    /** 数值B、上下文变量B、XID标识B（支持xxx.yyy.www） */
    private String   valueXIDB;
    
    
    
    public ConditionItem()
    {
        this(Comparer.Equal ,null ,null ,null);
    }
    
    
    
    /**
     * 构造器（主要用于判定对象是否为NULL；或Boolean类型的真假）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     */
    public ConditionItem(String i_ValueXIDA)
    {
        this(Comparer.Equal ,null ,i_ValueXIDA ,null);
    }
    
    
    /**
     * 构造器（主要用于两个变量、XID标识时）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Comparer    比较器
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     * @param i_ValueXIDB   数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public ConditionItem(Comparer i_Comparer ,String i_ValueXIDA ,String i_ValueXIDB)
    {
        this(i_Comparer ,null ,i_ValueXIDA ,i_ValueXIDB);
    }
    
    
    /**
     * 构造器（主要用于有数值类的）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Comparer    比较器
     * @param i_ValueClass  参数类型。参数为数值类型时生效
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     * @param i_ValueXIDB   数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public ConditionItem(Comparer i_Comparer ,Class<?> i_ValueClass ,String i_ValueXIDA ,String i_ValueXIDB)
    {
        this.comparer   = i_Comparer;
        this.valueClass = i_ValueClass;
        this.valueXIDA  = i_ValueXIDA;
        this.valueXIDB  = i_ValueXIDB;
    }
    
    
    
    /**
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     * @throws Exception 
     */
    public boolean allow(Map<String ,Object> i_Context) throws Exception
    {
        if ( this.comparer == null )
        {
            throw new NullPointerException("ConditionItem comparer [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        if ( this.valueXIDA == null )
        {
            throw new NullPointerException("ConditionItem valueA [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        
        // B可以为空，表示判定A是否为空，或判定A是否为Boolean类型的真假
        if ( this.valueXIDB == null )
        {
            Object v_ValueA = ValueHelp.getValue(this.valueXIDA ,this.valueClass ,null ,i_Context);
            
            if ( Comparer.Equal.equals(this.comparer) )
            {
                if ( v_ValueA == null )
                {
                    // 等于NULL
                    return true;
                }
                else if ( Boolean.class.equals(v_ValueA.getClass()) )
                {
                    return (Boolean) v_ValueA;
                }
                else
                {
                    return false;
                }
            }
            else if ( Comparer.EqualNot.equals(this.comparer) )
            {
                if ( v_ValueA == null )
                {
                    // 不等于NULL
                    return false;
                }
                else if ( Boolean.class.equals(v_ValueA.getClass()) )
                {
                    return !(Boolean) v_ValueA;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return v_ValueA != null;
            }
        }
        else
        {
            Object v_ValueA = ValueHelp.getValue(this.valueXIDA ,this.valueClass ,null ,i_Context);
            Object v_ValueB = ValueHelp.getValue(this.valueXIDB ,this.valueClass ,null ,i_Context);
            
            return this.comparer.compare(v_ValueA ,v_ValueB);
        }
    }
    
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     */
    public boolean reject(Map<String ,Object> i_Context) throws Exception
    {
        return !allow(i_Context);
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
     * 获取：参数类型。参数为数值类型时生效
     */
    public Class<?> getValueClass()
    {
        return valueClass;
    }

    
    /**
     * 设置：参数类型。参数为数值类型时生效
     * 
     * @param i_ValueClass 参数类型。参数为数值类型时生效
     */
    public void setValueClass(Class<?> i_ValueClass)
    {
        this.valueClass = i_ValueClass;
    }


    /**
     * 获取：数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     */
    public String getValueXIDA()
    {
        return valueXIDA;
    }

    
    /**
     * 设置：数值A、变量A、XID标识A（支持xxx.yyy.www）
     * 
     * @param i_ValueXIDA 数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     */
    public void setValueXIDA(String i_ValueXIDA)
    {
        if ( CallFlow.isSystemXID(i_ValueXIDA) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s ValueXIDA[" + i_ValueXIDA + "] is SystemXID.");
        }
        this.valueXIDA = ValueHelp.standardRefID(i_ValueXIDA);
    }

    
    /**
     * 获取：数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public String getValueXIDB()
    {
        return valueXIDB;
    }

    
    /**
     * 设置：数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     * 
     * @param i_ValueXIDB 数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public void setValueXIDB(String i_ValueXIDB)
    {
        if ( CallFlow.isSystemXID(i_ValueXIDB) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s ValueXIDB[" + i_ValueXIDB + "] is SystemXID.");
        }
        this.valueXIDB = ValueHelp.standardRefID(i_ValueXIDB);
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
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    public String toXml(int i_Level ,String i_SuperTreeID)
    {
        StringBuilder v_Xml    = new StringBuilder();
        String        v_Level1 = "    ";
        String        v_LevelN = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName  = "conditionItem";
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        if ( !Help.isNull(this.comment) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("comment" ,this.comment));
        }
        if ( this.valueClass != null )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueClass" ,this.valueClass.getName()));
        }
        if ( !Help.isNull(this.valueXIDA) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueXIDA" ,this.valueXIDA));
        }
        if ( this.comparer != null )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("comparer" ,this.comparer.getValue()));
        }
        if ( !Help.isNull(this.valueXIDB) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueXIDB" ,this.valueXIDB));
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        return v_Xml.toString();
    }
    
    
    /**
     * 解析为实时运行时的逻辑判定表达式
     * 
     * 注：禁止在此真的执行逻辑判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.comparer != null )
        {
            Object v_ValueA = null;
            Object v_ValueB = null;
            
            try
            {
                v_ValueA = ValueHelp.getValue(this.valueXIDA ,this.valueClass ,null ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ConditionItem[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s valueXIDA[" + this.valueXIDA + "] getValue error." ,exce);
                v_ValueA = "ERROR";
            }
            
            // B可以为空，表示判定A是否为空，或判定A是否为Boolean类型的真假
            if ( this.valueXIDB == null )
            {
                v_Builder.append(ValueHelp.getExpression(v_ValueA));
                
                if ( Comparer.Equal.equals(this.comparer) )
                {
                    if ( v_ValueA == null )
                    {
                        // 等于NULL
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" NULL");
                    }
                    else if ( Boolean.class.equals(v_ValueA.getClass()) )
                    {
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" TRUE");
                    }
                    else
                    {
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" NULL");
                    }
                }
                else if ( Comparer.EqualNot.equals(this.comparer) )
                {
                    if ( v_ValueA == null )
                    {
                        // 不等于NULL
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" NULL");
                    }
                    else if ( Boolean.class.equals(v_ValueA.getClass()) )
                    {
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" TRUE");
                    }
                    else
                    {
                        v_Builder.append(" ").append(this.comparer.getValue()).append(" NULL");
                    }
                }
                else
                {
                    v_Builder.append(" != NULL");
                }
            }
            else
            {
                try
                {
                    v_ValueB = ValueHelp.getValue(this.valueXIDB ,this.valueClass ,null ,i_Context);
                }
                catch (Exception exce)
                {
                    $Logger.error("ConditionItem[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s valueXIDB[" + this.valueXIDB + "] getValue error." ,exce);
                    v_ValueB = "ERROR";
                }
                
                v_Builder.append(ValueHelp.getExpression(v_ValueA));
                v_Builder.append(" ").append(this.comparer.getValue()).append(" ");
                v_Builder.append(ValueHelp.getExpression(v_ValueB));
            }
        }
        
        return v_Builder.toString();
    }


    /**
     * 解析为逻辑表达式
     * 
     * 注：禁止在此真的执行逻辑判定
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
            v_Builder.append(this.valueXIDA == null ? "NULL" : ValueHelp.getExpression(this.valueXIDA ,this.valueClass));
            v_Builder.append(" ").append(this.comparer.getValue()).append(" ");
            v_Builder.append(this.valueXIDA == null ? "NULL" : ValueHelp.getExpression(this.valueXIDB ,this.valueClass));
        }
        
        return v_Builder.toString();
    }
    
}
