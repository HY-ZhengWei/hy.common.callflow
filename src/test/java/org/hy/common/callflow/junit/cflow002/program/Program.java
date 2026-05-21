package org.hy.common.callflow.junit.cflow002.program;

import java.util.HashMap;
import java.util.Map;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-22
 * @version     v1.0
 */
public class Program
{
    
    private String name;
    
    private String value;
    
    
    
    public String method_First(int i_Int)
    {
        System.out.println("call method_First: " + i_Int + " >= 0");
        return i_Int >= 0 ? "T" : "F";
    }
    
    
    
    public Map<String ,Object> method_RetaMap(int i_Int)
    {
        return new HashMap<String ,Object>();
    }
    
    
    
    public void method_True()
    {
        System.out.println("call method_True.");
    }
    
    
    
    public void method_False()
    {
        System.out.println("call method_False.");
    }

    
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String i_Name)
    {
        this.name = i_Name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String i_Value)
    {
        this.value = i_Value;
    }
    
}
