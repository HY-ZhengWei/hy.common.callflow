package org.hy.common.callflow.enums;





/**
 * 循环类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-09
 * @version     v1.0
 */
public enum SelfLoopType
{
    
    Normal("NORMAL" ,"非循环的普通类型"),
    
    While( "WHILE"  ,"While循环"),

    For(   "FOR"    ,"For循环"),
    
    MySelf("MYSELF" ,"自循环"),
    
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
    public static SelfLoopType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (SelfLoopType v_Enum : SelfLoopType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    SelfLoopType(String i_Value ,String i_Comment)
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
