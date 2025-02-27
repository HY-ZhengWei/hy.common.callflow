package org.hy.common.callflow.file;

import org.hy.common.StringHelp;





/**
 * 转为Xml格式内容的接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public interface IToXml
{
    
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
    public String toXml(int i_Level ,String i_SuperTreeID);
    
    
    
    /**
     * 生成普通值标记
     * 
     * 生成形式如：<标记名称>值</标记名称>
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName  标记名称
     * @param i_Value    值
     * @return
     */
    static String toValue(String i_TagName ,Object i_Value)
    {
        if ( StringHelp.isContains(i_Value.toString() ,"<" ,">") )
        {
            return toValueCDATA(i_TagName ,i_Value);
        }
        else
        {
            return toBegin(i_TagName) + i_Value + toEnd(i_TagName);
        }
    }
    
    
    
    /**
     * 生成普通值标记
     * 
     * 生成形式如：<标记名称>值</标记名称>
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName  标记名称
     * @param i_Value    值
     * @return
     */
    static String toValueCDATA(String i_TagName ,Object i_Value)
    {
        return toBegin(i_TagName) + "<![CDATA[" + i_Value + "]]>" + toEnd(i_TagName);
    }
    
    
    
    /**
     * 生成引用类型的标记
     * 
     * 生成形式如：<标记名称 ref="引用值" />
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName   标记名称
     * @param i_RefValue  引用值
     * @return
     */
    static String toRef(String i_TagName ,String i_RefValue)
    {
        return toRef(i_TagName ,i_RefValue ,0);
    }
    
    
    
    /**
     * 生成引用类型的标记
     * 
     * 生成形式如：<标记名称 ref="引用值" />
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName   标记名称
     * @param i_RefValue  引用值
     * @param i_LpadSize  ref关键字左侧空格多少个
     * @return
     */
    static String toRef(String i_TagName ,String i_RefValue ,int i_LpadSize)
    {
        if ( i_LpadSize <= 0 )
        {
            return "<" + i_TagName + " ref=\"" + i_RefValue + "\" />";
        }
        else
        {
            return "<" + i_TagName + StringHelp.lpad("" ,i_LpadSize ," ") + " ref=\"" + i_RefValue + "\" />";
        }
    }
    
    
    
    /**
     * 按标记名称生成 xml 格式的标记开始
     * 
     * 生成形式如：<标记名称>
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName  标记名称
     * @return
     */
    static String toBegin(String i_TagName)
    {
        return "<" + i_TagName + ">";
    }
    
    
    
    /**
     * 按标记名称生成 xml 格式的标记开始（带有ID值）
     * 
     * 生成形式如：<标记名称 id="ID值">
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName  标记名称
     * @param i_ID       ID值
     * @return
     */
    static String toBeginID(String i_TagName ,String i_ID)
    {
        return "<" + i_TagName + " id=\"" + i_ID + "\">";
    }
    
    
    
    /**
     * 按标记名称生成 xml 格式的标记结束
     * 
     * 生成形式如：</标记名称>
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TagName  标记名称
     * @return
     */
    static String toEnd(String i_TagName)
    {
        return "</" + i_TagName + ">";
    }
    
}
