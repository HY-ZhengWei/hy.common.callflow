package org.hy.common.callflow.junit.cflow027.program;

import java.util.List;

import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-08
 * @version     v1.0
 */
public class Program
{
    
    public void method_Show(List<String> i_CMDRet)
    {
        Help.print(i_CMDRet);
    }
    
}
