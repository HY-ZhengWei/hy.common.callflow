package org.hy.common.callflow.enums;





/**
 * 编排续跑的类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-17
 * @version     v1.0
 */
public enum ContinueType
{
    
    Default( 0 ,"默认重做：异常时或用户标记时重做"),
    
    Active ( 1 ,"主动重做：必须重做"),
    
    ;
    
    
    
    /** 值 */
    private Integer value;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-15
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static ContinueType get(Integer i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (ContinueType v_Enum : ContinueType.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ContinueType(Integer i_Value ,String i_Comment)
    {
        this.value   = i_Value;
        this.comment = i_Comment;
    }

    
    
    public Integer getValue()
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
