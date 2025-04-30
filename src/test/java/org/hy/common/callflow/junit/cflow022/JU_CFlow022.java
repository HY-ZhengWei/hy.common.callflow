package org.hy.common.callflow.junit.cflow022;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.event.PublishConfig;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow022.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎022：发布元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-29
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow022 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow022() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow022() throws InterruptedException
    {
        test_CFlow022_Inner();
        
    }
    
    
    
    private void test_CFlow022_Inner() throws InterruptedException
    {
        // 初始化被编排的执行对象方法（注：这里是019的对象）
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        PublishConfig       v_Publish = (PublishConfig) XJava.getObject("XPublish_CF022_1");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        v_Context.put("Message" ,"Who am I?"); // 发布的消息内容
        v_Context.put("UserID"  ,"ZhengWei");  // 发布者
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_Publish);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_Publish ,v_Context);
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
        System.out.println(CallFlow.getHelpExport().export(v_Publish));
        
        toJson(v_Publish);
        toJson(v_FirstResult);
    }
    
}
