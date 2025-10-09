package org.hy.common.callflow.junit.cflow037Upgrade.program;

import org.hy.common.Date;
import org.hy.common.callflow.language.shell.ShellResult;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-29
 * @version     v1.0
 */
public class Program
{
    
    public void method_Begin(int i_IndexNo ,String i_IP)
    {
        System.out.println("\n\n" + i_IndexNo + "\t" + i_IP + "准备开始 " + Date.getNowTime().getFull());
    }
    
    
    
    public void method_Show(ShellResult i_ShellResult)
    {
        System.out.println(i_ShellResult.getResult());
    }
    
    
    
    public void method_Finish()
    {
        System.out.println("全部完成");
    }
    
}
