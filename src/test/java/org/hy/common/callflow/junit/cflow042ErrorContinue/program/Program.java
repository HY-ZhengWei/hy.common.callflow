package org.hy.common.callflow.junit.cflow042ErrorContinue.program;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-17
 * @version     v1.0
 */
public class Program
{
    
    /**
     * 累加
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-17
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public Integer addUp(Integer i_Value)
    {
        System.out.println(i_Value);
        return i_Value + 1;
    }
    
    
    
    /**
     * 异常发生器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param i_IsError
     */
    public void errorContinue(Boolean i_IsError)
    {
        if ( i_IsError )
        {
            System.out.println("模拟发生异常");
            throw new RuntimeException("发生运行时异常");
        }
        else
        {
            System.out.println("没有发生异常");
        }
    }
    
}
