package org.hy.common.callflow.junit.cflow010;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow010.program.Program;
import org.hy.common.callflow.node.CalculateConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎010：计算表达式，及当它是条件逻辑的使用
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-04
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow010 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow010() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow010()
    {
        test_CFlow010_Inner();
        System.out.println("\n");
        test_CFlow010_Inner();
    }
    
    
    
    private void test_CFlow010_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        CalculateConfig     v_Calculate = (CalculateConfig) XJava.getObject("XCalculate_CF010_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        // 传数字 或 字符类的数字
        v_Context.put("Value" ,99);
        
        ExecuteResult v_Result = CallFlow.execute(v_Calculate ,v_Context);
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
        this.println(v_FirstResult);
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        this.println(v_FirstResult);
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_Calculate));
        
        toJson(v_Calculate);
    }
    
}
