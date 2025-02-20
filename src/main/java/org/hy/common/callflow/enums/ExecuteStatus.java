package org.hy.common.callflow.enums;





/**
 * 编排流程上下文的执行状态的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-20
 * @version     v1.0
 */
public enum ExecuteStatus
{
    
    NotStart ("NotStart"  ,"未开始"),
    
    Started  ("Started"   ,"已开始，即执行中"),
    
    Finished ("Finished"  ,"已完成"),
    
    Exception("Exception" ,"发生异常"),
    
    Timeout  ("Timeout"   ,"执行超时"),
    
    Canceled ("Canceled"  ,"取消执行"),
    
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
    public static ExecuteStatus get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (ExecuteStatus v_Enum : ExecuteStatus.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ExecuteStatus(String i_Value ,String i_Comment)
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
