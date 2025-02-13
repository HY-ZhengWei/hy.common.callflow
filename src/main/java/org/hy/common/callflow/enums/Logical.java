package org.hy.common.callflow.enums;





/**
 * 逻辑的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 */
public enum Logical
{
    
    And("And" ,"与"),
    
    Or ("Or"  ,"或"),
    
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
    public static Logical get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (Logical v_Enum : Logical.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    Logical(String i_Value ,String i_Comment)
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
