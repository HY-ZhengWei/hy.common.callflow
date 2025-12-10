package org.hy.common.callflow.junit.cflow019API.program;

import java.util.Map;

import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-09
 * @version     v1.0
 */
public class Program
{
    
    public void show(Object i_Data)
    {
        System.out.println(i_Data);
    }
    
    
    
    public void showMap(Map<String ,Object> i_Data)
    {
        Help.print(i_Data);
    }
    
}
