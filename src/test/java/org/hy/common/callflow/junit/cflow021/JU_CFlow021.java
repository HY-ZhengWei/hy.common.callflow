package org.hy.common.callflow.junit.cflow021;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.event.JOBConfig;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow019.JU_CFlow019;
import org.hy.common.callflow.junit.cflow019.program.Program;
import org.hy.common.thread.Jobs;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎021：定时元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-24
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow021 extends JUBase
{
    
    private static boolean $isInit = false;
    private static Jobs    $Jobs   = new Jobs();
    
    
    
    public JU_CFlow021() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
        
        new JU_CFlow019();
        
        XJava.putObject("XJOBS" ,$Jobs);
    }
    
    
    
    @Test
    public void test_CFlow021() throws InterruptedException
    {
        test_CFlow021_Inner();
        
    }
    
    
    
    private void test_CFlow021_Inner() throws InterruptedException
    {
        // 初始化被编排的执行对象方法（注：这里是019的对象）
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        JOBConfig           v_JOB     = (JOBConfig) XJava.getObject("XJOB_CF021_1");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        v_Context.put("IP" ,"114.114.114.114"); // 它是给019执行用的上下文参数
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_JOB);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_JOB ,v_Context);
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
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_JOB));
        
        toJson(v_JOB);
        toJson(v_FirstResult);
        
        $Jobs.startup();
        Thread.sleep(5 * 60 * 1000);
    }
    
}
