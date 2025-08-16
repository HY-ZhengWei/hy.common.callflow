package org.hy.common.callflow.enums;





/**
 * 编排导出类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-15
 * @version     v1.0
 */
public enum ExportType
{
    
    Logic("LOGIC" ,"编排逻辑"),
    
    UI   ("UI"    ,"编排流图"),
    
    All  ("ALL"   ,"编排逻辑与流图"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
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
    public static ExportType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (ExportType v_Enum : ExportType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ExportType(String i_Value ,String i_Comment)
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
