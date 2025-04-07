package org.hy.common.callflow.junit.cflow019.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.node.APIException;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-21
 * @version     v1.0
 */
public class Program
{
    
    public void method_Succeed(String i_Datas ,Map<String ,Object> i_DataMap)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Succeed.");
        System.out.println(i_Datas);
        Help.print(i_DataMap);
    }
    
    
    
    public void method_Error(ExecuteResult i_ErrorResult)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Error.");
        System.out.println("请求资源：" + ((APIException)i_ErrorResult.getException()).getUrl());
        System.out.println("响应结果：" + ((APIException)i_ErrorResult.getException()).getMessage());
    }
    
}
