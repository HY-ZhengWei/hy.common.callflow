package org.hy.common.callflow.common;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.PartitionMap;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 从上下文区、全局区、默认值区三个区中取值
 * 
 * 优先级为：上下文区 > 全局区 > 默认值区
 * 
 *   :xxx                     支持单个占位符。
 *   :xxx.yyy.zzz             支持单个占位符的面向对象。
 *   :uuu.{:xxx}              支持多层的占位符。如内层占位符当作外层占位符的参数。内层占位符可以是对象，但内层占位符的最终值只能是简单类型的。
 *   :uuu.{:xxx.yyy.zzz}.www  支持多层的占位符。
 *   :LIST.1                  支持单个占位符的集合取一，即从集合中取某个元素（1为集合中的第二个元素）。
 *   :LIST.1.xxx              支持单个占位符的集合取一。
 *   :LIST.{:xxx.yyy}.uuu     支持单个占位符的集合取一组合多层的占位符。
 *   :Array.1                 支持单个占位符的数组取一。即从数组中取某个元素（1为数组中的第二个元素）。
 *   :Array.1.xxx             支持单个占位符的数组取一。
 *   :Array.{xxx.yyy}.uuu     支持单个占位符的数组取一组合多层的占位符。
 *   :Map.key                 支持单个占位符的Map取一。即从Map取某个元素的Value（关键字为字符串key，key是固定值）。
 *   :Map.key.xxx             支持单个占位符的Map取一。
 *   :Map.{xxx.yyy}.uuu       支持单个占位符的Map取一组合多层的占位符。使Map的关键字也变成动态取值的。
 *   :{:xxx}                  支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符。
 *   :{:xxx}.uuu              支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符，并取对象的属性。
 *   :{:{:xxx}}.uuu           支持占位符的嵌套获取对象。嵌套三层。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-13
 * @version     v1.0
 *              v2.0  2025-06-05  添加：上下文中支持多层组的占位符。如内层占位符当作外层占位符的参数。如：Factory.users.$get({:School.users.$get(A).ref}).name
 *              v3.0  2025-10-17  添加：占位符的嵌套获取对象，如：:{:xxx}.uuu，:xxx是一个占位符，它的值是另一个占位符。
 *              v3.1  2025-10-18  添加：整合replaceByContext与getValue两方法。即支持单个占位符的解析，也支持多个占位符组合的解析
 */
public class ValueHelp
{
    
    private static final Logger $Logger = new Logger(ValueHelp.class);
    
    public  static final String $Split  = ".";
    
    
    
    /**
     * 判定是否符合XID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-18
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static boolean isXID(String i_Value)
    {
        if ( Help.isNull(i_Value) )
        {
            return false;
        }
        else if ( i_Value.startsWith(DBSQL.$Placeholder) )
        {
            return !StringHelp.isContains(i_Value ," " ,"+" ,"=" ,"(" ,")" ,"{" ,"}" ,"[" ,"]" ,"%" ,"#" ,"!" ,"&" ,"`" ,"~" ,"@" ,"^" ,"*" ,";" ,"?" ,"," ,"/" ,"\\" ,"|");
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 检查是否为合法的变量名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-21
     * @version     v1.0
     *
     * @param i_VarName  定义的变量名称
     * @return
     */
    public static boolean isVarName(String i_VarName)
    {
        if ( Help.isNull(i_VarName) )
        {
            return false;
        }
        else
        {
            return !StringHelp.isContains(i_VarName.trim() ,"." ,":" ," " ,"+" ,"=" ,"(" ,")" ,"{" ,"}" ,"[" ,"]" ,"%" ,"#" ,"!" ,"&" ,"`" ,"~" ,"@" ,"^" ,"*" ,";" ,"?" ,"," ,"/" ,"\\" ,"|");
        }
    }
    
    
    
    /**
     * 是否为引用ID的格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @param i_RefID  引用的XID
     * @return
     */
    public static boolean isRefID(String i_RefID)
    {
        return i_RefID.startsWith(DBSQL.$Placeholder);
    }
    
    
    
    /**
     * 定义变量ID时的标准化。
     * 
     *   定义变量ID前缘有占位符时，去除占位符。
     *   定义变量ID为空字符时，返回NULL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-05
     * @version     v1.0
     *
     * @param i_ValueID  定义变量ID
     * @return
     */
    public static String standardValueID(String i_ValueID)
    {
        if ( !Help.isNull(i_ValueID) )
        {
            String v_ValueID = i_ValueID.trim();
            if ( v_ValueID.equals(DBSQL.$Placeholder) )
            {
                throw new IllegalArgumentException("ValueID[" + i_ValueID + "] is error.");
            }
            
            if ( v_ValueID.startsWith(DBSQL.$Placeholder) )
            {
                v_ValueID = v_ValueID.substring(1);
            }
            
            return v_ValueID;
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 引用变量ID时的标准化。
     * 
     *   引用变量ID时，没有占位符时，追加占位符后返回。
     *   引用变量ID为空字符时，返回NULL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-05
     * @version     v1.0
     *
     * @param i_RefID
     * @return
     */
    public static String standardRefID(String i_RefID)
    {
        if ( Help.isNull(i_RefID) )
        {
            return null;
        }
        else
        {
            String v_RefID = i_RefID.trim();
            if ( v_RefID.equals(DBSQL.$Placeholder) )
            {
                throw new IllegalArgumentException("RefID[" + v_RefID + "] is error.");
            }
            else if ( !v_RefID.startsWith(DBSQL.$Placeholder) )
            {
                return DBSQL.$Placeholder + v_RefID;
            }
            else
            {
                return v_RefID;
            }
        }
    }
    
    
    
    /**
     * 从上下文区、全局区、默认值区三个区中取值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *              v2.0  2025-10-17  添加：占位符的嵌套获取对象，如：:{:xxx}.uuu，:xxx是一个占位符，它的值是另一个占位符。
     *
     * @param i_ValueXID    数值、上下文变量、XID标识（支持xxx.yyy.www）
     * @param i_ValueClass  数值时的元类型
     * @param i_Default     默认值
     * @param i_Context     上下文类型的变量信息
     * @return
     */
    public static Object getValue(String i_ValueXID ,Class<?> i_ValueClass ,Object i_Default ,Map<String ,Object> i_Context) throws Exception
    {
        Object v_Value = i_ValueXID;
        if ( i_ValueXID == null )
        {
            if ( i_Default != null )
            {
                return toObject(i_Default ,i_ValueClass);
            }
        }
        else if ( i_ValueXID.startsWith(DBSQL.$Placeholder) )
        {
            String v_ValueID = i_ValueXID.trim().substring(DBSQL.$Placeholder.length());
            if ( CallFlow.$Context.equals(v_ValueID) )
            {
                // 上下文当作参数值
                return i_Context;
            }
            else if ( CallFlow.$ErrorResult.equals(v_ValueID) )
            {
                // 实例异常的结果
                return CallFlow.getErrorResult(i_Context);
            }
            else if ( CallFlow.$WorkID.equals(v_ValueID) )
            {
                // 编排执行实例ID
                return CallFlow.getWorkID(i_Context);
            }
            else if ( CallFlow.$TimeLen.equals(v_ValueID) )
            {
                // 编排执行时长（单位：毫秒）
                return CallFlow.getTimeLen(i_Context);
            }
            else if ( CallFlow.$BeginTime.equals(v_ValueID) )
            {
                // 编排执行开始时间
                return CallFlow.getBeginTime(i_Context);
            }
            
            String v_YYYZZZ = null;
            int    v_Index  = v_ValueID.indexOf($Split);
            if ( v_Index > 0 )
            {
                if ( v_Index + 1 < v_ValueID.length() )
                {
                    v_YYYZZZ = v_ValueID.substring(v_Index + 1);
                }
                v_ValueID = v_ValueID.substring(0 ,v_Index);
            }
            
            // 双冒号：取到首个占位符的值，它还是一个嵌套的占位符
            /*
            Object v_BothValue = null;
            if ( v_ValueID.startsWith(DBSQL.$Placeholder) )
            {
                v_BothValue = getValue(v_ValueID ,null ,null ,i_Context);
                if ( v_BothValue != null && v_BothValue instanceof String )
                {
                    v_BothValue = getValue(DBSQL.$Placeholder + (String) v_BothValue ,null ,null ,i_Context);
                    if ( v_BothValue == null )
                    {
                        return i_Default;
                    }
                }
                else
                {
                    return i_Default;
                }
            }
            */
            
            // 尝试从上下文区取值
            if ( !Help.isNull(i_Context) )
            {
                /*
                if ( v_BothValue == null )
                {
                    v_Value = i_Context.get(v_ValueID);
                }
                else
                {
                    v_Value = v_BothValue;
                }
                */
                v_Value = i_Context.get(v_ValueID);
                if ( v_Value != null )
                {
                    if ( v_YYYZZZ != null )
                    {
                        return toObject(getYYYZZZ(v_Value ,v_YYYZZZ) ,i_ValueClass);
                    }
                    else
                    {
                        return toObject(v_Value ,i_ValueClass);
                    }
                }
            }
            
            // 尝试从全局区取值
            /*
            if ( v_BothValue == null )
            {
                v_Value = XJava.getObject(v_ValueID);
            }
            else
            {
                v_Value = v_BothValue;
            }
            */
            v_Value = XJava.getObject(v_ValueID);
            if ( v_Value != null )
            {
                if ( v_YYYZZZ != null )
                {
                    return toObject(getYYYZZZ(v_Value ,v_YYYZZZ) ,i_ValueClass);
                }
                else
                {
                    return toObject(v_Value ,i_ValueClass);
                }
            }
            
            // 尝试从默认值区取值
            if ( v_Value == null )
            {
                v_Value = toObject(i_Default ,i_ValueClass);
            }
        }
        else if ( Object.class.equals(i_ValueClass) )
        {
            // Nothing.
        }
        else if ( Help.isBasicDataType(i_ValueClass) )
        {
            v_Value = Help.toObject(i_ValueClass ,i_ValueXID);
        }
        else if ( i_ValueClass != null )
        {
            XJSON v_XJson = new XJSON();
            v_Value = v_XJson.toJava(i_ValueXID ,i_ValueClass);
        }
        
        return v_Value;
    }
    
    
    
    /**
     * 从上下文区、全局区、默认值区三个区中取值
     * 
     * 整合replaceByContext与getValue两方法。即支持单个占位符的解析，也支持多个占位符组合的解析
     *   :xxx                     支持单个占位符。
     *   :xxx.yyy.zzz             支持单个占位符的面向对象。
     *   :uuu.{:xxx}              支持多层的占位符。如内层占位符当作外层占位符的参数。内层占位符可以是对象，但内层占位符的最终值只能是简单类型的。
     *   :uuu.{:xxx.yyy.zzz}.www  支持多层的占位符。
     *   :LIST.1                  支持单个占位符的集合取一，即从集合中取某个元素（1为集合中的第二个元素）。
     *   :LIST.1.xxx              支持单个占位符的集合取一。
     *   :LIST.{:xxx.yyy}.uuu     支持单个占位符的集合取一组合多层的占位符。
     *   :Array.1                 支持单个占位符的数组取一。即从数组中取某个元素（1为数组中的第二个元素）。
     *   :Array.1.xxx             支持单个占位符的数组取一。
     *   :Array.{xxx.yyy}.uuu     支持单个占位符的数组取一组合多层的占位符。
     *   :Map.key                 支持单个占位符的Map取一。即从Map取某个元素的Value（关键字为字符串key，key是固定值）。
     *   :Map.key.xxx             支持单个占位符的Map取一。
     *   :Map.{xxx.yyy}.uuu       支持单个占位符的Map取一组合多层的占位符。使Map的关键字也变成动态取值的。
     *   :{:xxx}                  支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符。
     *   :{:xxx}.uuu              支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符，并取对象的属性。
     *   :{:{:xxx}}.uuu           支持占位符的嵌套获取对象。嵌套三层。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-18
     * @version     v1.0
     *
     * @param i_ValueXID      数值、上下文变量、XID标识（支持xxx.yyy.www）
     * @param i_Placeholders  预解析过的占位符信息
     * @param i_ValueClass    数值时的元类型
     * @param i_Default       默认值
     * @param i_Context       上下文类型的变量信息
     * @return
     */
    public static Object getValueReplace(String i_ValueXID ,PartitionMap<String ,Integer> i_Placeholders ,Class<?> i_ValueClass ,Object i_Default ,Map<String ,Object> i_Context) throws Exception
    {
        return getValueReplace(i_ValueXID ,i_Placeholders ,1 ,i_ValueClass ,i_Default ,i_Context);
    }
    
    
    
    /**
     * 从上下文区、全局区、默认值区三个区中取值
     * 
     * 整合replaceByContext与getValue两方法。即支持单个占位符的解析，也支持多个占位符组合的解析
     *   :xxx                     支持单个占位符。
     *   :xxx.yyy.zzz             支持单个占位符的面向对象。
     *   :uuu.{:xxx}              支持多层的占位符。如内层占位符当作外层占位符的参数。内层占位符可以是对象，但内层占位符的最终值只能是简单类型的。
     *   :uuu.{:xxx.yyy.zzz}.www  支持多层的占位符。
     *   :LIST.1                  支持单个占位符的集合取一，即从集合中取某个元素（1为集合中的第二个元素）。
     *   :LIST.1.xxx              支持单个占位符的集合取一。
     *   :LIST.{:xxx.yyy}.uuu     支持单个占位符的集合取一组合多层的占位符。
     *   :Array.1                 支持单个占位符的数组取一。即从数组中取某个元素（1为数组中的第二个元素）。
     *   :Array.1.xxx             支持单个占位符的数组取一。
     *   :Array.{xxx.yyy}.uuu     支持单个占位符的数组取一组合多层的占位符。
     *   :Map.key                 支持单个占位符的Map取一。即从Map取某个元素的Value（关键字为字符串key，key是固定值）。
     *   :Map.key.xxx             支持单个占位符的Map取一。
     *   :Map.{xxx.yyy}.uuu       支持单个占位符的Map取一组合多层的占位符。使Map的关键字也变成动态取值的。
     *   :{:xxx}                  支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符。
     *   :{:xxx}.uuu              支持占位符的嵌套获取对象。:xxx是一个占位符，它的值是另一个占位符，并取对象的属性。
     *   :{:{:xxx}}.uuu           支持占位符的嵌套获取对象。嵌套三层。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-18
     * @version     v1.0
     *
     * @param i_ValueXID      数值、上下文变量、XID标识（支持xxx.yyy.www）
     * @param i_Placeholders  预解析过的占位符信息
     * @param i_Level         递归层级。从1开始
     * @param i_ValueClass    数值时的元类型
     * @param i_Default       默认值
     * @param i_Context       上下文类型的变量信息
     * @return
     */
    private static Object getValueReplace(String i_ValueXID ,PartitionMap<String ,Integer> i_Placeholders ,int i_Level ,Class<?> i_ValueClass ,Object i_Default ,Map<String ,Object> i_Context) throws Exception
    {
        Object v_Value = i_ValueXID;
        if ( Help.isNull(i_ValueXID) )
        {
            if ( i_Default != null )
            {
                return toObject(i_Default ,i_ValueClass);
            }
        }
        else 
        {
            String v_ValueID = i_ValueXID.trim();
            if ( v_ValueID.startsWith(DBSQL.$Placeholder) )
            {
                if ( v_ValueID.indexOf(DBSQL.$Placeholder ,DBSQL.$Placeholder.length()) < 0 )
                {
                    // 仅单个占位符时
                    return getValue(i_ValueXID ,i_ValueClass ,i_Default ,i_Context);
                }
            }
            else
            {
                // 无占位符时
                if ( (i_Placeholders != null && i_Placeholders.isEmpty()) || v_ValueID.indexOf(DBSQL.$Placeholder) < 0 )
                {
                    if ( Object.class.equals(i_ValueClass) )
                    {
                        // Nothing.
                    }
                    else if ( Help.isBasicDataType(i_ValueClass) )
                    {
                        v_Value = Help.toObject(i_ValueClass ,i_ValueXID);
                    }
                    else if ( i_ValueClass != null )
                    {
                        XJSON v_XJson = new XJSON();
                        v_Value = v_XJson.toJava(i_ValueXID ,i_ValueClass);
                    }
                    return v_Value;
                }
            }
            
            PartitionMap<String ,Integer> v_Placeholders = i_Placeholders;
            if ( Help.isNull(v_Placeholders) )
            {
                PartitionMap<String ,Integer> v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,v_ValueID ,true);
                if ( Help.isNull(v_PlaceholdersOrg) )
                {
                    if ( Object.class.equals(i_ValueClass) )
                    {
                        // Nothing.
                    }
                    else if ( Help.isBasicDataType(i_ValueClass) )
                    {
                        v_Value = Help.toObject(i_ValueClass ,i_ValueXID);
                    }
                    else if ( i_ValueClass != null )
                    {
                        XJSON v_XJson = new XJSON();
                        v_Value = v_XJson.toJava(i_ValueXID ,i_ValueClass);
                    }
                    return v_Value;
                }
                
                v_Placeholders = Help.toReverse(v_PlaceholdersOrg);
            }
            
            if ( !Help.isNull(v_Placeholders) )
            {
                Map<String ,Object> v_Context = new HashMap<String ,Object>();
                for (String v_Placeholder : v_Placeholders.keySet())
                {
                    // 格式为  {:占位符}
                    if ( v_Placeholder.startsWith("{" + DBSQL.$Placeholder) )
                    {
                        String v_PlaceholderTrue = v_Placeholder.substring(1 + DBSQL.$Placeholder.length() ,v_Placeholder.length() - 1);
                        // 此处不能确定 “元类型” 和 “默认值”
                        v_Context.put(v_Placeholder ,getValue(DBSQL.$Placeholder + v_PlaceholderTrue ,null ,null ,i_Context));
                    }
                    // 格式为  :占位符
                    else
                    {
                        // 此处不能确定 “元类型” 和 “默认值”
                        v_Context.put(DBSQL.$Placeholder + v_Placeholder ,getValue(DBSQL.$Placeholder + v_Placeholder ,null ,null ,i_Context));
                    }
                }
                
                // 格式01
                //   解析第一层的占位符形式  :{:占位符的名称}.data.data.Automatic
                //   解析第二层的占位符形式  :AIPRets.data.data.Automatic
                // 格式02
                //   解析第一层的占位符形式  :StationStatus.{:{:占位符的名称}.data.data.Automatic}
                //   解析第二层的占位符形式  :StationStatus.{:AIPRets.data.data.Automatic}
                //   解析第三层的占位符形式  :StationStatus.2
                String v_NewValue = StringHelp.replaceAll(v_ValueID ,v_Context);
                v_Context.clear();
                v_Context = null;
                if ( i_Placeholders == null )
                {
                    v_Placeholders.clear();
                    v_Placeholders = null;
                }
                
                String v_OldValue = null;
                do 
                {
                    v_OldValue = v_NewValue;
                    if ( v_NewValue.indexOf(":" ,DBSQL.$Placeholder.length()) >= 0 )
                    {
                        // 逐层级解析，并且应当 “元类型” 和 “默认值” 向下传
                        Object v_GetValue = getValueReplace(v_NewValue ,null ,i_Level + 1 ,i_ValueClass ,i_Default ,i_Context);
                        if ( v_GetValue == null )
                        {
                            return i_Default;
                        }
                        if ( v_GetValue instanceof String )
                        {
                            if ( i_Level == 1 )
                            {
                                // 已在递归中解析所有层级
                                return v_GetValue;
                            }
                            else
                            {
                                v_NewValue = (String) v_GetValue;
                            }
                        }
                        else
                        {
                            return v_GetValue;
                        }
                    }
                }
                while ( !v_OldValue.equals(v_NewValue) );
                
                // 此步最后一步解析所有层级
                // 格式01
                //   解析第二层的占位符形式  :AIPRets.data.data.Automatic
                // 格式02
                //   解析第三层的占位符形式  :StationStatus.2
                return getValue(v_NewValue ,i_ValueClass ,i_Default ,i_Context);
            }
            else
            {
                if ( Object.class.equals(i_ValueClass) )
                {
                    // Nothing.
                }
                else if ( Help.isBasicDataType(i_ValueClass) )
                {
                    v_Value = Help.toObject(i_ValueClass ,i_ValueXID);
                }
                else if ( i_ValueClass != null )
                {
                    XJSON v_XJson = new XJSON();
                    v_Value = v_XJson.toJava(i_ValueXID ,i_ValueClass);
                }
            }
        }
        
        return v_Value;
    }
    
    
    /**
     * 对上下文变量、XID标识取到的数值尝试二次进行转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
     * @version     v1.0
     *
     * @param i_Value
     * @param i_ValueClass
     * @return
     * @throws Exception 
     */
    private static Object toObject(Object i_Value ,Class<?> i_ValueClass) throws Exception
    {
        if ( i_Value != null && i_ValueClass != null )
        {
            if ( Help.isBasicDataType(i_ValueClass) )
            {
                return Help.toObject(i_ValueClass ,i_Value.toString());
            }
            else if ( String.class.equals(i_Value.getClass()) && MethodReflect.isExtendImplement(i_ValueClass ,Map.class) )
            {
                XJSON v_XJson = new XJSON();
                return v_XJson.toJava(i_Value.toString() ,Map.class);
            }
        }
        
        return i_Value;
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
    @SuppressWarnings("unchecked")
    public static Object getYYYZZZ(Object i_Object ,String i_YYYZZZ)
    {
        try
        {
            if ( MethodReflect.isExtendImplement(i_Object ,Map.class) )
            {
                return MethodReflect.getMapValue((Map<String ,?>) i_Object ,i_YYYZZZ);
            }
            else
            {
                MethodReflect v_MR = new MethodReflect(i_Object ,i_YYYZZZ ,true ,MethodReflect.$NormType_Getter);
                return v_MR.invokeForInstance(i_Object);
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,"getYYYZZZ(" + i_Object.toString() + " ," + i_YYYZZZ + ") is error.");
            return null;
        }
    }
    
    
    /**
     * 生成数值的表达式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Value  数值
     * @return
     */
    public static String getExpression(Object i_Value)
    {
        if ( i_Value == null )
        {
            return getExpression(null ,null);
        }
        else
        {
            return getExpression(i_Value.toString() ,i_Value.getClass());
        }
    }
    
    
    
    /**
     * 生成数值的表达式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Value       数值的字符形式
     * @param i_ValueClass  数值的类型
     * @return
     */
    public static String getExpression(String i_Value ,Class<?> i_ValueClass)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( i_Value == null )
        {
            v_Builder.append("NULL");
        }
        else if ( i_ValueClass == null )
        {
            v_Builder.append(i_Value);
        }
        else if ( i_ValueClass.equals(String.class) )
        {
            v_Builder.append("\"").append(i_Value).append("\"");
        }
        else if ( i_ValueClass.equals(Character.class) )
        {
            v_Builder.append("'").append(i_Value).append("'");
        }
        else if ( i_ValueClass.equals(Long.class) )
        {
            v_Builder.append(i_Value).append("L");
        }
        else if ( i_ValueClass.equals(Float.class) )
        {
            v_Builder.append(i_Value).append("F");
        }
        else if ( i_ValueClass.equals(Double.class) )
        {
            v_Builder.append(i_Value).append("D");
        }
        else
        {
            v_Builder.append(i_Value);
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 用上下文中的内容替换文本中的占位符
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-25
     * @version     v1.0
     *              v2.0  2025-06-17  添加：支持多层的占位符。如内层占位符当作外层占位符的参数
     *                                     举例：Factory.users.$get({:School.users.$get(A).ref}).name
     *                                     详见：JU_ValueHelp
     *
     * @param i_Value    文本字符
     * @param i_Context  上下文内容
     * @return
     */
    public static String replaceByContext(String i_Value ,Map<String ,Object> i_Context)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Value) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Value ,true);
        }
        
        if ( Help.isNull(v_PlaceholdersOrg) )
        {
            return i_Value;
        }
        
        PartitionMap<String ,Integer> v_Placeholders = Help.toReverse(v_PlaceholdersOrg);
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        for (String v_Placeholder : v_Placeholders.keySet())
        {
            // 格式为  {:占位符}
            if ( v_Placeholder.startsWith("{") )
            {
                v_Context.put(v_Placeholder ,MethodReflect.getMapValue(i_Context ,v_Placeholder.substring(2 ,v_Placeholder.length() - 1)));
            }
            // 格式为  :占位符
            else
            {
                v_Context.put(DBSQL.$Placeholder + v_Placeholder ,MethodReflect.getMapValue(i_Context ,v_Placeholder));
            }
        }
        
        String v_NewValue = StringHelp.replaceAll(i_Value ,v_Context);
        v_Context.clear();
        v_Context = null;
        v_Placeholders.clear();
        v_Placeholders = null;
        v_PlaceholdersOrg.clear();
        v_PlaceholdersOrg = null;
        
        String v_OldValue = "";
        do 
        {
            v_OldValue = v_NewValue;
            if ( v_NewValue.indexOf(":") >= 0 )
            {
                // 逐层级解析
                v_NewValue = replaceByContext(v_NewValue ,i_Context);
            }
        }
        while ( !v_OldValue.equals(v_NewValue) );
        return v_NewValue;
    }
    
    
    
    /**
     * 用上下文中的内容替换文本中的占位符
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-06-05
     * @version     v1.0
     *              v2.0  2025-06-17  添加：支持多层的占位符。如内层占位符当作外层占位符的参数
     *                                     举例：Factory.users.$get({:School.users.$get(A).ref}).name
     *                                     详见：JU_ValueHelp
     *
     * @param i_Value         文本字符
     * @param i_Placeholders  占位符
     * @param i_Context       上下文内容
     * @return
     */
    public static String replaceByContext(String i_Value ,PartitionMap<String ,Integer> i_Placeholders ,Map<String ,Object> i_Context)
    {
        if ( !Help.isNull(i_Placeholders) )
        {
            Map<String ,Object> v_Context = new HashMap<String ,Object>();
            for (String v_Placeholder : i_Placeholders.keySet())
            {
                // 格式为  {:占位符}
                if ( v_Placeholder.startsWith("{") )
                {
                    v_Context.put(v_Placeholder ,MethodReflect.getMapValue(i_Context ,v_Placeholder.substring(2 ,v_Placeholder.length() - 1)));
                }
                // 格式为  :占位符
                else
                {
                    v_Context.put(DBSQL.$Placeholder + v_Placeholder ,MethodReflect.getMapValue(i_Context ,v_Placeholder));
                }
            }
            
            String v_NewValue = StringHelp.replaceAll(i_Value ,v_Context);
            v_Context.clear();
            v_Context = null;
            
            String v_OldValue = "";
            do 
            {
                v_OldValue = v_NewValue;
                if ( v_NewValue.indexOf(":") >= 0 )
                {
                    // 逐层级解析
                    v_NewValue = replaceByContext(v_NewValue ,i_Context);
                }
            }
            while ( !v_OldValue.equals(v_NewValue) );
            
            return v_NewValue;
        }
        else
        {
            return i_Value;
        }
    }
    
}
