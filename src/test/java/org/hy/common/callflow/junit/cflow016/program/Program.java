package org.hy.common.callflow.junit.cflow016.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-15
 * @version     v1.0
 */
public class Program
{
    
    private int counter = 0;
    
    
    
    public void method_1()
    {
        counter++;
        System.out.println("call method_1：" + this.counter);
        if ( counter >= 10 )
        {
            throw new RuntimeException("发生异常，退出递归");
        }
    }
    
    public void method_Finish()
    {
        System.out.println("call method_Finish");
    }
    
}
