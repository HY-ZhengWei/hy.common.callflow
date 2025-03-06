package org.hy.common.callflow.junit.cflow011.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-06
 * @version     v1.0
 */
public class Program
{
    
    public void method_For1(Integer i_Value)
    {
        System.out.println("call method_For1 Value=" + i_Value);
    }
    
    
    
    public void method_For2(Integer i_Value)
    {
        System.out.println("call method_For2 Value=" + i_Value);
    }
    
    
    
    public void method_Finish()
    {
        System.out.println("call method_Finish");
    }
    
}
