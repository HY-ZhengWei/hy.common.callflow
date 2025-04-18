package org.hy.common.callflow.junit.cflow018;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
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
 * 测试单元：编排引擎018：并发元素
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
        
        v_Context.put("typeNameA" ,"A");
        v_Context.put("typeNameB" ,"B");
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_MT);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
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
        
        System.out.println("并发统一返回结果：" + v_Result.getResult());
        System.out.println("返回值：AReturn=" + v_Context.get("AReturn"));
        System.out.println("返回值：BReturn=" + v_Context.get("BReturn"));
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_MT));
        
        toJson(v_MT);
        toJson(v_FirstResult);
        
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
