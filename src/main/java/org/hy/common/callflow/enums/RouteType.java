package org.hy.common.callflow.enums;





/**
 * 路由类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-09
 * @version     v1.0
 */
public enum RouteType
{
    
    Succeed("SUCCEED" ,"succeed" ,"成功路由"),
    
    Error(  "ERROR"   ,"error"   ,"异常路由"),
    
    If(     "IF"      ,"if"      ,"真值路由"),
    
    Else(   "ELSE"    ,"else"    ,"假值路由"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** XML名称 */
    private String  xmlName;
    
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
    public static RouteType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (RouteType v_Enum : RouteType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    RouteType(String i_Value ,String i_XmlName ,String i_Comment)
    {
        this.value   = i_Value;
        this.xmlName = i_XmlName;
        this.comment = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public String getXmlName()
    {
        return xmlName;
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
