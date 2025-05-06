package org.hy.common.callflow.junit.cflow023.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
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
    
    public void method_Message(Map<String ,Object> i_Context)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Message.");
        Help.print(i_Context);
    }
    
    
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
