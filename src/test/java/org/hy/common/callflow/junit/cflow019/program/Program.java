package org.hy.common.callflow.junit.cflow019.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-21
 * @version     v1.0
 */
public class Program
{
    
    public void method_Finish(String i_Datas ,Map<String ,Object> i_DataMap)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Finish.");
        System.out.println(i_Datas);
        Help.print(i_DataMap);
    }
    
}
