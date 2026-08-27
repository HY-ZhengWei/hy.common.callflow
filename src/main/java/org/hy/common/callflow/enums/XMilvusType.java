package org.hy.common.callflow.enums;





/**
 * XMilvus向量库操作类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-14
 * @version     v1.0
 */
public enum XMilvusType
{
    
    Auto     ("Auto"    ,"运行时自动识别"),
    
    Create   ("C"       ,"新增数据"),
    
    Read     ("R"       ,"查询数据"),
    
    Update   ("U"       ,"修改数据"),
    
    Delete   ("D"       ,"删除数据"),
    
    DDLCreate("Create"  ,"创建集合"),
    
    DDLDrop  ("Drop"    ,"删除集合"),
    
    Exists   ("Exists"  ,"存在集合"),
    
    Load     ("Load"    ,"加载集合"),
    
    Release  ("Release" ,"释放集合"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static XMilvusType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (XMilvusType v_Enum : XMilvusType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    XMilvusType(String i_Value ,String i_Comment)
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
