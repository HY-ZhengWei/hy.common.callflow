package org.hy.common.callflow.junit.cflow011;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow011.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎011：For循环元素。1到n的数值循环
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-06
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow011 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow011() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow011()
    {
        test_CFlow011_Inner();
        System.out.println("\n");
        test_CFlow011_Inner();
    }
    
    
    
    private void test_CFlow011_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        ForConfig           v_ForConfig = (ForConfig) XJava.getObject("XFor_CF011_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        ExecuteResult v_Result = CallFlow.execute(v_ForConfig ,v_Context);
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Error XID = " + v_Result.getExecuteXID());
            v_Result.getException().printStackTrace();
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        CallFlow.getHelpLog().logs(v_FirstResult);
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        CallFlow.getHelpLog().logs(v_FirstResult);
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_ForConfig));
        
        toJson(v_ForConfig);
    }
    
}
