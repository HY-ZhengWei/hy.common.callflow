package org.hy.common.callflow.junit.cflow022.program;

import org.hy.common.Date;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.node.APIException;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-30
 * @version     v1.0
 */
public class Program
{
    
    public void method_Finish()
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Finish.");
    }
    
    
    public void method_Error(ExecuteResult i_ErrorResult)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Error.");
        System.out.println("请求资源：" + ((APIException)i_ErrorResult.getException()).getUrl());
        System.out.println("响应结果：" + ((APIException)i_ErrorResult.getException()).getMessage());
    }
    
}
