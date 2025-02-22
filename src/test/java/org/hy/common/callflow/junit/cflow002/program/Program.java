package org.hy.common.callflow.junit.cflow002.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-22
 * @version     v1.0
 */
public class Program
{
    
    public String method_First(int i_Int)
    {
        System.out.println("call method_First: " + i_Int + " >= 0");
        return i_Int >= 0 ? "T" : "F";
    }
    
    
    
    public void method_True()
    {
        System.out.println("call method_True.");
    }
    
    
    
    public void method_False()
    {
        System.out.println("call method_False.");
    }
    
}
