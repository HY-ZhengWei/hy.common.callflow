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
    
}
