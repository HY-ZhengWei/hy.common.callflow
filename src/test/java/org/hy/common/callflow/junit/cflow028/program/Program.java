package org.hy.common.callflow.junit.cflow028.program;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-11
 * @version     v1.0
 */
public class Program
{
    
    public void method_Show(Map<String ,String> i_Tables ,Map<String ,String> i_Datas ,Map<String ,String> i_Row ,AppConfig i_AppConfig) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println("\n\n所有的表");
        Help.print(i_Tables);
        
        System.out.println("\n\n表中所有行");
        Help.print(i_Datas);
        
        System.out.println("\n\n一行数据");
        Help.print(i_Row);
        
        System.out.println("\n\n一行对象");
        Help.print(Help.toMap(i_AppConfig));
    }
    
}
