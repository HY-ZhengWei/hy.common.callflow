package org.hy.common.callflow.junit;





/**
 * 测试单元：从上下文区、全局区、默认值区三个区中取值
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-06-17
 * @version     v1.0
 */
public class JU_ValueHelpUser
{
    
    /** 名称 */
    private String  name;
    
    /** 年龄 */
    private Integer age;
    
    /** 关联 */
    private String  ref;

    
    
    public JU_ValueHelpUser()
    {
        
    }
    
    
    public JU_ValueHelpUser(String i_Name ,Integer i_Age ,String i_Ref)
    {
        this.name = i_Name;
        this.age  = i_Age;
        this.ref  = i_Ref;
    }
    
    
    /**
     * 获取：名称
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * 设置：名称
     * 
     * @param i_Name 名称
     */
    public void setName(String i_Name)
    {
        this.name = i_Name;
    }

    
    /**
     * 获取：年龄
     */
    public Integer getAge()
    {
        return age;
    }

    
    /**
     * 设置：年龄
     * 
     * @param i_Age 年龄
     */
    public void setAge(Integer i_Age)
    {
        this.age = i_Age;
    }


    /**
     * 获取：关联
     */
    public String getRef()
    {
        return ref;
    }

    
    /**
     * 设置：关联
     * 
     * @param i_Ref 关联
     */
    public void setRef(String i_Ref)
    {
        this.ref = i_Ref;
    }
    
}
