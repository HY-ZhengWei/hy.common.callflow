package org.hy.common.callflow.enums;





/**
 * 数据来源的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public enum ParamFrom
{
    
    /** 当变量ID相同时，优先级最高 */
    Default("Default" ,"默认值类型"),
    
    /** 当变量ID相同时，优先级次之 */
    Context("Context" ,"上下文数据类型"),
    
    /** 当变量ID相同时，优先级最低 */
    XID    ("XID"     ,"全局数据类型"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-11
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static ParamFrom get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (ParamFrom v_Enum : ParamFrom.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ParamFrom(String i_Value ,String i_Comment)
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
