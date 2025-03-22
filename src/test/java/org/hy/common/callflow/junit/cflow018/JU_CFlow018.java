package org.hy.common.callflow.junit.cflow018;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow018.program.Program;
import org.hy.common.callflow.nesting.MTConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎018：并发元素发
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-21
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow018 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow018() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow018()
    {
        test_CFlow018_Inner();
        System.out.println("\n");
        test_CFlow018_Inner();
    }
    
    
    
    private void test_CFlow018_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        MTConfig            v_MT      = (MTConfig) XJava.getObject("XMT_CF018_1");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        ExecuteResult v_Result = CallFlow.execute(v_MT ,v_Context);
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Error XID = " + v_Result.getExecuteXID());
            System.out.println("Error Msg = " + v_Result.getException().getMessage());
            if ( v_Result.getException() instanceof TimeoutException )
            {
                System.out.println("is TimeoutException");
            }
            v_Result.getException().printStackTrace();
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        this.println(v_FirstResult);
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_MT));
        
        toJson(v_MT);
        
        try
        {
            Thread.sleep(10 * 1000);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
}
