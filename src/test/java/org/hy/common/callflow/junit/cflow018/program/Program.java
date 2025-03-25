package org.hy.common.callflow.junit.cflow018.program;

import org.hy.common.Date;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-21
 * @version     v1.0
 */
public class Program
{
    
    public String method_ShowWaitTime(long i_TimeLen ,String i_TypeName)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_ShowWaitTime：" + i_TimeLen + " ,typeName=" + i_TypeName);
        return i_TypeName + ":" + i_TimeLen;
    }
    
    
    
    public void method_Finish()
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Finish.");
    }
    
}
