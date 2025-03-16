package org.hy.common.callflow.junit.cflow017.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-15
 * @version     v1.0
 */
public class Program
{
    
    public void method_Sleep(long i_TimeLen)
    {
        try
        {
            System.out.println("call method_Sleep Start TimeLen=" + i_TimeLen);
            Thread.sleep(1000 * 10);
            System.out.println("call method_Sleep End TimeLen=" + i_TimeLen);
        }
        catch (InterruptedException exce)
        {
            System.out.println("call method_Sleep 发生异常");
            throw new RuntimeException(exce);
        }
    }
    
    
    public void method_OK(long i_TimeLen)
    {
        System.out.println("call method_OK TimeLen=" + i_TimeLen);
    }
    
    
    
    public void method_Timeout()
    {
        System.out.println("call method_Timeout");
    }
    
    
    
    public void method_Timeout_Finish()
    {
        System.out.println("call method_Timeout_Finish");
    }
    
    
    
    public void method_Child_Finish()
    {
        System.out.println("call method_Child_Finish。 嵌套超时后，此元素不应当被执行");
    }
    
}
