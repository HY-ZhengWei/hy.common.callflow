package org.hy.common.callflow.junit.cflow030Nesting.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-27
 * @version     v1.0
 */
public class Program
{
    
    public void method_Father()
    {
        System.out.println("Call method_Father");
    }
    
    
    
    public void method_Daughter()
    {
        System.out.println("Call method_Daughter");
    }
    
}
