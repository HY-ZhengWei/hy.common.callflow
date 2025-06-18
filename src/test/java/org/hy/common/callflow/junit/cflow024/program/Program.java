package org.hy.common.callflow.junit.cflow024.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-06-18
 * @version     v1.0
 */
public class Program
{
    
    public void method_Info(Map<String ,Object> i_Context)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Info.");
        Help.print(i_Context);
    }
    
}
