package org.hy.common.callflow.junit.cflow035Groovy.program;

import java.util.Map;

import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-24
 * @version     v1.0
 */
public class Program
{
    
    public void method_Show(Map<String ,Object> i_Datas)
    {
        Help.print(i_Datas);
    }
    
}
