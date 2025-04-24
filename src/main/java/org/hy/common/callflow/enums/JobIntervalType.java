package org.hy.common.callflow.enums;





/**
 * 定时元素的时间间隔的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-21
 * @version     v1.0
 */
public enum JobIntervalType
{
    
    Second("S"  ,-2                         ,"秒"),
    
    Minute("MI" ,60                         ,"分钟"),
    
    Hour  ("H"  ,60 * Minute.getInterval()  ,"小时"),
    
    Day   ("D"  ,24 * Hour  .getInterval()  ,"天"),
    
    Week  ("W"  ,7  * Day   .getInterval()  ,"周"),
    
    Month ("M"  ,1                          ,"月"),
    
    Year  ("Y"  ,2                          ,"年"),
    
    Manual("MA" ,-1                         ,"手工执行"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** 间隔 */
    private Integer interval;
    
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
    public static JobIntervalType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (JobIntervalType v_Enum : JobIntervalType.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    JobIntervalType(String i_Value ,Integer i_Interval ,String i_Comment)
    {
        this.value    = i_Value;
        this.interval = i_Interval;
        this.comment  = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public Integer getInterval()
    {
        return interval;
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
