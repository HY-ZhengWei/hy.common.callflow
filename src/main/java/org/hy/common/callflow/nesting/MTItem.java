package org.hy.common.callflow.nesting;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.Comparer;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.ifelse.ConditionItem;
import org.hy.common.xml.log.Logger;





/**
 * 并发项
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-20
 * @version     v1.0
 */
public class MTItem extends ConditionItem
{
    
    private static final Logger $Logger = new Logger(MTItem.class);
    
    
    
    /** 子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素的XID）。采用弱关联的方式 */
    private String callFlowXID;
    
    /** 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String timeout;
    
    /** 向上下文中赋值（仅向并发项的独立上下文中赋值） */
    private String context;
    
    /** 为返回值定义的变量ID（返回最后的结果） */          
    private String returnID;
    
    
    
    public MTItem()
    {
        super();
        this.timeout = "0";
    }
    
    
    /**
     * 构造器（主要用于判定对象是否为NULL；或Boolean类型的真假）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     */
    public MTItem(String i_ValueXIDA)
    {
        super(i_ValueXIDA);
        this.timeout = "0";
    }
    
    
    /**
     * 构造器（主要用于两个变量、XID标识时）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_Comparer    比较器
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     * @param i_ValueXIDB   数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public MTItem(Comparer i_Comparer ,String i_ValueXIDA ,String i_ValueXIDB)
    {
        super(i_Comparer ,null ,i_ValueXIDA ,i_ValueXIDB);
        this.timeout = "0";
    }
    
    
    /**
     * 构造器（主要用于有数值类的）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_Comparer    比较器
     * @param i_ValueClass  参数类型。参数为数值类型时生效
     * @param i_ValueXIDA   数值A、上下文变量A、XID标识A（支持xxx.yyy.www）
     * @param i_ValueXIDB   数值B、上下文变量B、XID标识B（支持xxx.yyy.www）
     */
    public MTItem(Comparer i_Comparer ,String i_ValueClass ,String i_ValueXIDA ,String i_ValueXIDB)
    {
        super(i_Comparer ,i_ValueClass ,i_ValueXIDA ,i_ValueXIDB);
        this.timeout = "0";
    }
    
    
    /**
     * 获取：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return ValueHelp.standardRefID(this.callFlowXID);
    }
    
    
    /**
     * 获取：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素的XID）。采用弱关联的方式
     */
    protected String gatCallFlowXID()
    {
        return this.callFlowXID;
    }

    
    /**
     * 设置：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素的XID）。采用弱关联的方式
     * 
     * @param i_CallFlowXID 子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素的XID）。采用弱关联的方式
     */
    public void setCallFlowXID(String i_CallFlowXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.callFlowXID = ValueHelp.standardValueID(i_CallFlowXID);
    }
    
    
    /**
     * 从上下文中获取运行时的超时时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    protected Long gatTimeout(Map<String ,Object> i_Context) throws Exception
    {
        Long v_Timeout = null;
        if ( Help.isNumber(this.timeout) )
        {
            v_Timeout = Long.valueOf(this.timeout);
        }
        else
        {
            v_Timeout = (Long) ValueHelp.getValue(this.timeout ,Long.class ,0L ,i_Context);
        }
        
        return v_Timeout;
    }
    
    
    /**
     * 获取：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getTimeout()
    {
        return timeout;
    }

    
    /**
     * 设置：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_Timeout 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setTimeout(String i_Timeout)
    {
        if ( Help.isNull(i_Timeout) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_Timeout) )
        {
            Long v_Timeout = Long.valueOf(i_Timeout);
            if ( v_Timeout < 0L )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.timeout = i_Timeout.trim();
        }
        else
        {
            this.timeout = ValueHelp.standardRefID(i_Timeout);
        }
    }
    
    
    /**
     * 获取：向上下文中赋值（仅向并发项的独立上下文中赋值）
     */
    public String getContext()
    {
        return context;
    }

    
    /**
     * 设置：向上下文中赋值（仅向并发项的独立上下文中赋值）
     * 
     * @param i_Context 向上下文中赋值（仅向并发项的独立上下文中赋值）
     */
    public void setContext(String i_Context)
    {
        this.context = i_Context;
    }
    
    
    /**
     * 获取：为返回值定义的变量ID（返回最后的结果）
     */
    public String getReturnID()
    {
        return returnID;
    }
    
    
    /**
     * 设置：为返回值定义的变量ID（返回最后的结果）
     * 
     * @param i_ReturnID 为返回值定义的变量ID（返回最后的结果）
     */
    public void setReturnID(String i_ReturnID)
    {
        if ( CallFlow.isSystemXID(i_ReturnID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s returnID[" + i_ReturnID + "] is SystemXID.");
        }
        
        this.returnID = ValueHelp.standardValueID(i_ReturnID);
    }

    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
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
        String        v_XName  = "item";
        
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
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueClass" ,this.gatValueClass().getName()));
        }
        if ( !Help.isNull(this.valueXIDA) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueXIDA" ,this.valueXIDA));
        }
        if ( this.comparer != null && Comparer.Equal != this.comparer )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("comparer" ,this.comparer.getValue()));
        }
        if ( !Help.isNull(this.valueXIDB) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("valueXIDB" ,this.valueXIDB));
        }
        if ( !Help.isNull(this.gatCallFlowXID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("callFlowXID" ,this.getCallFlowXID()));
        }
        if ( !Help.isNull(this.timeout) && !"0".equals(this.timeout) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("timeout" ,this.timeout));
        }
        if ( !Help.isNull(this.context) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("context" ,this.context));
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
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
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.comparer != null && !Help.isNull(this.valueXIDA) )
        {
            Object v_ValueA = null;
            Object v_ValueB = null;
            
            try
            {
                v_ValueA = ValueHelp.getValue(this.valueXIDA ,this.gatValueClass() ,null ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ConditionItem[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s valueXIDA[" + this.valueXIDA + "] getValue error." ,exce);
                v_ValueA = "ERROR";
            }
            
            // B可以为空，表示判定A是否为空，或判定A是否为Boolean类型的真假
            if ( this.valueXIDB == null )
            {
                v_Builder.append(this.valueXIDA).append("[");
                v_Builder.append(ValueHelp.getExpression(v_ValueA));
                v_Builder.append("]");
                
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
                    v_ValueB = ValueHelp.getValue(this.valueXIDB ,this.gatValueClass() ,null ,i_Context);
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
            
            v_Builder.append(" ");
        }
        
        v_Builder.append("TO ").append(Help.NVL(this.callFlowXID));
        
        return v_Builder.toString();
    }


    /**
     * 解析为逻辑表达式
     * 
     * 注：禁止在此真的执行逻辑判定
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.comparer != null && !Help.isNull(this.valueXIDA) )
        {
            v_Builder.append(this.valueXIDA == null ? "NULL" : ValueHelp.getExpression(this.valueXIDA ,this.gatValueClass()));
            v_Builder.append(" ").append(this.comparer.getValue()).append(" ");
            v_Builder.append(this.valueXIDA == null ? "NULL" : ValueHelp.getExpression(this.valueXIDB ,this.gatValueClass()));
            v_Builder.append(" ");
        }
        
        v_Builder.append("TO ").append(Help.NVL(this.callFlowXID));
        
        return v_Builder.toString();
    }
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new MTItem();
    }
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        MTItem v_Clone = new MTItem();
        
        v_Clone.id          = this.id;
        v_Clone.comment     = this.comment;
        v_Clone.comparer    = this.comparer;
        v_Clone.valueClass  = this.valueClass;
        v_Clone.valueXIDA   = this.valueXIDA;
        v_Clone.valueXIDB   = this.valueXIDB; 
        v_Clone.callFlowXID = this.callFlowXID; 
        v_Clone.timeout     = this.timeout; 
        v_Clone.context     = this.context;
        v_Clone.returnID    = this.returnID;
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        MTItem v_Clone = (MTItem) io_Clone;
        
        v_Clone.xid         = this.cloneXID(this.xid ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID);
        v_Clone.id          = this.id;
        v_Clone.comment     = this.comment;
        v_Clone.comparer    = this.comparer;
        v_Clone.valueClass  = this.valueClass;
        v_Clone.valueXIDA   = this.valueXIDA;
        v_Clone.valueXIDB   = this.valueXIDB;
        v_Clone.callFlowXID = this.callFlowXID;
        v_Clone.timeout     = this.timeout; 
        v_Clone.context     = this.context;
        v_Clone.returnID    = this.returnID;
    }
    
}
