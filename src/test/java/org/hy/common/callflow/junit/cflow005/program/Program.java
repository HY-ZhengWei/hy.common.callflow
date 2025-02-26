package org.hy.common.callflow.junit.cflow005.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-25
 * @version     v1.0
 */
public class Program
{
    
    public void method_1()
    {
        System.out.println("call method_1");
    }
    
    public void method_1_1()
    {
        System.out.println("call method_1_1");
    }
    
    public void method_1_1_1()
    {
        System.out.println("call method_1_1_1");
    }
    
    public void method_1_1_2()
    {
        System.out.println("call method_1_1_2");
        throw new NullPointerException("抛出异常");
    }
    
    public void method_1_2()
    {
        System.out.println("call method_1_2");
    }
    
}
