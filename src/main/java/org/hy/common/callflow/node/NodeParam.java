package org.hy.common.callflow.node;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.XJavaID;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;





/**
 * 节点参数
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeParam implements XJavaID
{
    
    private static final Logger $Logger = new Logger(NodeParam.class);
    
    
    
    /** 全局惟一标识ID */
    private String   xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String   comment;
    
    /** 参数类型。参数为数值类型时生效；或参数有默认值时生效 */
    private Class<?> valueClass;
    
    /** 参数数值。可以是数值、上下文变量、XID标识 */
    private String   value;
    
    /** 参数默认值的字符形式（参数为上下文变量、XID标识时生效） */
    private String   valueDefault;
    
    /** 参数默认值的实例对象 */
    private Object   valueDefaultObject;
    
    
    
    public NodeParam()
    {
        
    }
    
    
    /**
     * （主要用于：上下文变量、XID标识，但没有默认值时）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Value  参数数值。可以是数值、上下文变量、XID标识
     */
    public NodeParam(String i_Value)
    {
        this(i_Value ,null);
    }
    
    
    /**
     * 构造器（主要用于：参数类型是数值时）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Value       参数数值。可以是数值、上下文变量、XID标识
     * @param i_ValueClass  参数类型
     */
    public NodeParam(String i_Value ,Class<?> i_ValueClass)
    {
        if ( Help.isNull(i_Value) )
        {
            throw new NullPointerException("NodeParam's value is null.");
        }
        
        // 是数值时，参数类型必须有
        if ( !i_Value.startsWith(DBSQL.$Placeholder) )
        {
            if ( i_ValueClass == null )
            {
                throw new NullPointerException("NodeParam's valueClass is null, but value is not Placeholder.");
            }
        }
        
        this.setValue(     i_Value);
        this.setValueClass(i_ValueClass);
    }
    
    
    /**
     * 构造器（主要用于：上下文变量、XID标识带默认值时）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Value         参数数值。可以是数值、上下文变量、XID标识
     * @param i_ValueClass    参数类型
     * @param i_ValueDefault  参数默认值的字符形式（参数为上下文变量、XID标识时生效）
     */
    public NodeParam(String i_Value ,Class<?> i_ValueClass ,String i_ValueDefault)
    {
        if ( Help.isNull(i_Value) )
        {
            throw new NullPointerException("NodeParam's value is null.");
        }
        
        // 是数值时，参数类型必须有
        if ( !i_Value.startsWith(DBSQL.$Placeholder) )
        {
            if ( i_ValueClass == null )
            {
                throw new NullPointerException("NodeParam's valueClass is null, but value is not Placeholder.");
            }
        }
        // 是上下文变量、XID标识
        else 
        {
            // 有默认值时，参数类型必须存在
            if ( i_ValueDefault != null && i_ValueClass == null )
            {
                throw new NullPointerException("NodeParam's value is Placeholder and valueClass is null, but valueDefault is not null.");
            }
        }
        
        this.setValue(       i_Value);
        this.setValueClass(  i_ValueClass);
        this.setValueDefault(i_ValueDefault);
    }
    
    
    /**
     * 参数默认值的实例对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @return
     * @throws Exception
     */
    public synchronized Object getValueDefaultObject() throws Exception
    {
        if ( this.valueDefault == null || this.valueClass == null )
        {
            return null;
        }
        
        if ( this.valueDefaultObject == null )
        {
            if ( Help.isBasicDataType(this.valueClass) )
            {
                this.valueDefaultObject = Help.toObject(this.valueClass ,this.valueDefault);
            }
            else
            {
                XJSON v_XJson = new XJSON();
                this.valueDefaultObject = v_XJson.toJava(this.valueDefault ,this.valueClass);
            }
        }
        
        return valueDefaultObject;
    }


    /**
     * 获取：参数类型。参数为数值类型时生效；或参数有默认值时生效
     */
    public Class<?> getValueClass()
    {
        return valueClass;
    }

    
    /**
     * 设置：参数类型。参数为数值类型时生效；或参数有默认值时生效
     * 
     * @param i_ValueClass 参数类型。仅为数值类型时才生效
     */
    public void setValueClass(Class<?> i_ValueClass)
    {
        if ( Void.class.equals(i_ValueClass) )
        {
            this.valueClass = null;
        }
        else
        {
            this.valueClass = i_ValueClass;
        }
        this.valueDefaultObject = null;
    }

    
    /**
     * 获取：参数数值。可以是数值、上下文变量、XID标识
     */
    public String getValue()
    {
        return value;
    }

    
    /**
     * 设置：参数数值。可以是数值、上下文变量、XID标识
     * 
     * @param i_Value 参数数值。可以是数值、上下文变量、XID标识
     */
    public void setValue(String i_Value)
    {
        this.value = i_Value;
    }

    
    /**
     * 获取：参数默认值的字符形式（参数为上下文变量、XID标识时生效）
     */
    public String getValueDefault()
    {
        return valueDefault;
    }

    
    /**
     * 设置：参数默认值的字符形式（参数为上下文变量、XID标识时生效）
     * 
     * @param i_ValueDefault 参数默认值的字符形式（参数为上下文变量、XID标识时生效）
     */
    public void setValueDefault(String i_ValueDefault)
    {
        this.valueDefault       = i_ValueDefault;
        this.valueDefaultObject = null;
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
     * 解析为实时运行时的执行表达式
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
        Object        v_Value   = null;
        
        try
        {
            v_Value = ValueHelp.getValue(this.value ,this.valueClass ,this.getValueDefaultObject() ,i_Context);
        }
        catch (Exception exce)
        {
            $Logger.error("NodeParam[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s value[" + this.value + "] getValue error." ,exce);
        }
        
        v_Builder.append(ValueHelp.getExpression(v_Value));
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.value == null )
        {
            v_Builder.append("NULL");
        }
        else if ( this.value.startsWith(DBSQL.$Placeholder) )
        {
            v_Builder.append(this.value);
            if ( this.valueDefault != null )
            {
                v_Builder.append(" default");
            }
        }
        else if ( this.valueClass != null )
        {
            v_Builder.append(ValueHelp.getExpression(this.value ,this.valueClass));
        }
        else
        {
            v_Builder.append(this.value).append("::?");
        }
        
        return v_Builder.toString();
    }
    
}
