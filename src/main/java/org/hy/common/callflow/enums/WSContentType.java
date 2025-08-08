package org.hy.common.callflow.enums;





/**
 * 点推元素的消息内容类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-08
 * @version     v1.0
 */
public enum WSContentType
{
    
    Text("TEXT" ,"文本消息"),
    
    Json("JSON" ,"Json格式消息"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static WSContentType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (WSContentType v_Enum : WSContentType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    WSContentType(String i_Value ,String i_Comment)
    {
        this.value   = i_Value;
        this.comment = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public String getComment()
    {
        return this.comment;
    }
    
    

    public String toString()
    {
        return this.value + "";
    }
    
}
