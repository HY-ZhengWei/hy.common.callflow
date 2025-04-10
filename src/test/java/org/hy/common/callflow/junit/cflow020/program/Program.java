package org.hy.common.callflow.junit.cflow020.program;


import java.util.List;

import org.hy.common.Date;
import org.hy.common.MapJson;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-10
 * @version     v1.0
 */
public class Program
{
    
    public void method_ForElement(MapJson i_Data ,String i_TotalTime)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_ForElement. " 
                         + i_TotalTime + " " + i_Data.get("totals[0].waterValue") + "~" + i_Data.get("totals.waterValue") + " " + ((List<?>)i_Data.get("totals")).size() + "个数据");
    }
    
    
    
    public void method_Finish()
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Finish.");
    }
    
}
