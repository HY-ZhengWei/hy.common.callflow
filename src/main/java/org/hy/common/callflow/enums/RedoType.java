package org.hy.common.callflow.enums;





/**
 * 编排整体重做的类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-17
 * @version     v1.0
 */
public enum RedoType
{
    
    Disable(-1 ,"禁止重做"),
    
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
    public static RedoType get(Integer i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (RedoType v_Enum : RedoType.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    RedoType(Integer i_Value ,String i_Comment)
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
