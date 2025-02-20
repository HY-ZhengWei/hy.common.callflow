package org.hy.common.callflow.common;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 从上下文区、全局区、默认值区三个区中取值
 * 
 * 优先级为：上下文区 > 全局区 > 默认值区
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-13
 * @version     v1.0
 */
public class ValueHelp
{
    
    private static final Logger $Logger = new Logger(ValueHelp.class);
    
    private static final String $Split  = ".";
    
    
    
    /**
     * 从上下文区、全局区、默认值区三个区中取值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_ValueXID 数值、上下文变量、XID标识（支持xxx.yyy.www）
     * @param i_Default  默认值
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public static Object getValue(String i_ValueXID ,Class<?> i_ValueClass ,Object i_Default ,Map<String ,Object> i_Context)
    {
        Object v_Value = i_ValueXID;
        if ( i_ValueXID == null )
        {
            // Nothing.
        }
        else if ( i_ValueXID.startsWith(DBSQL.$Placeholder) )
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
            
            // 尝试从上下文区取值
            if ( !Help.isNull(i_Context) )
            {
                v_Value = i_Context.get(v_ValueID);
                if ( v_Value != null )
                {
                    if ( v_YYYZZZ != null )
                    {
                        return getYYYZZZ(v_Value ,v_YYYZZZ);
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
                    return getYYYZZZ(v_Value ,v_YYYZZZ);
                }
                else
                {
                    return v_Value;
                }
            }
            
            // 尝试从默认值区取值
            if ( v_Value == null )
            {
                v_Value = i_Default;
            }
        }
        else
        {
            v_Value = Help.toObject(i_ValueClass ,i_ValueXID);
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
    public static Object getYYYZZZ(Object i_Object ,String i_YYYZZZ)
    {
        try
        {
            MethodReflect v_MR = new MethodReflect(i_Object ,i_YYYZZZ ,true ,MethodReflect.$NormType_Getter);
            return v_MR.invokeForInstance(i_Object);
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,"getYYYZZZ(" + i_Object.toString() + " ," + i_YYYZZZ + ") is error.");
            return null;
        }
    }
    
}
