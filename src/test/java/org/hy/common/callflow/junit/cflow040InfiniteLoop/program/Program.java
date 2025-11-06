package org.hy.common.callflow.junit.cflow040InfiniteLoop.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-10-30
 * @version     v1.0
 */
public class Program
{
    
    public void start()
    {
        System.out.println(Date.getNowTime().getFull() + " 开始");
    }
    
    
    
    public void loop(Map<String ,Object> io_Context)
    {
        Help.print(io_Context);
    }
    
    
    public void finish()
    {
        System.out.println(Date.getNowTime().getFull() + " 完成");
    }
    
}
