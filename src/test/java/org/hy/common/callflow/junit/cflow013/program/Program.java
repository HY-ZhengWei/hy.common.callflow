package org.hy.common.callflow.junit.cflow013.program;

import org.hy.common.Date;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-07
 * @version     v1.0
 */
public class Program
{
    
    public long method_Timeout(long i_Sleep)
    {
        System.out.println("call method_Timeout Sleep=" + i_Sleep + "\tStartTime：" + Date.getNowTime().getFullMilli());
        
        try
        {
            Thread.sleep(i_Sleep);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        System.out.println("call method_Timeout Sleep=" + i_Sleep + "\t  endTime：" + Date.getNowTime().getFullMilli());
        
        return i_Sleep;
    }
    
    
    
    public void method_OK(long i_TimeLen)
    {
        System.out.println("call method_OK TimeLen=" + i_TimeLen);
    }
    
    
    
    public void method_Error()
    {
        System.out.println("call method_Error");
    }
    
}
