package org.hy.common.callflow.junit.cflow009.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public class Program
{
    
    public int method_AddCount(int i_Count)
    {
        System.out.println("call method_AddCount(" + i_Count + ").");
        return i_Count + 1;
    }
    
    
    
    public void method_Finish()
    {
        System.out.println("call method_Finish.");
    }
    
}
