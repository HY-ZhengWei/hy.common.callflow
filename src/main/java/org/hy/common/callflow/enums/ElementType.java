package org.hy.common.callflow.enums;





/**
 * 执行元素类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public enum ElementType
{
    
    Node(     "NODE"      ,"执行节点"),
    
    Condition("CONDITION" ,"条件逻辑"),
    
    Wait(     "WAIT"      ,"等待元素"),
    
    Calculate("CALCULATE" ,"计算元素"),
    
    SelfLoop( "SELFLOOP"  ,"自循环"),
    
    Nesting(  "NESTING"   ,"嵌套子编排"),
    
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
    public static ElementType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (ElementType v_Enum : ElementType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ElementType(String i_Value ,String i_Comment)
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
