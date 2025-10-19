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
    
    public void method_Begin(int i_IndexNo ,String i_IP ,String i_MSType)
    {
        System.out.println("\n\n" + i_IndexNo + "\t" + i_IP + " " + i_MSType + " 准备开始 " + Date.getNowTime().getFull());
    }
    
    
    
    public void method_Show(ShellResult i_ShellResult)
    {
        if ( i_ShellResult != null )
        {
            System.out.println(i_ShellResult.getResult());
        }
        else
        {
            System.out.println("无返回任何信息");
        }
    }
    
    
    
    public void method_ShowError(ShellResult i_ShellResult)
    {
        if ( i_ShellResult != null )
        {
            System.out.println("发生异常：" + i_ShellResult.getResult());
        }
        else
        {
            System.out.println("发生异常：无返回任何信息");
        }
    }
    
    
    
    public void method_Finish()
    {
        System.out.println("全部完成");
    }
    
}
