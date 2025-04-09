package org.hy.common.callflow.enums;





/**
 * XSQL元素类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-09
 * @version     v1.0
 */
public enum XSQLType
{
    
    Auto     ("Auto"      ,"运行时自动识别"),
    
    Create   ("C"         ,"新增数据"),
    
    Read     ("R"         ,"查询数据"),
    
    Update   ("U"         ,"修改数据"),
    
    Delete   ("D"         ,"删除数据"),
    
    DDL      ("DDL"       ,"数据定义语言"),
    
    XSQLGroup("XSQLGroup" ,"XSQL组"),
    
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
    public static XSQLType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (XSQLType v_Enum : XSQLType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    XSQLType(String i_Value ,String i_Comment)
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
