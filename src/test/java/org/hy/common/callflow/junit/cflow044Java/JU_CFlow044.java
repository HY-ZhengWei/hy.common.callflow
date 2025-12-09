package org.hy.common.callflow.junit.cflow044Java;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow042ErrorContinue.program.ExecuteEvent;
import org.hy.common.callflow.junit.cflow043XCQL.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎044：爪哇元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow044 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow044() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow044()
    {
        this.test_CFlow044_Inner();
    }
    
    
    
    private void test_CFlow044_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        ForConfig           v_For     = (ForConfig) XJava.getObject("XFor_CF044_循环定义");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        // v_Context.put("Name" ,"ZhengWei");
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_For);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_For ,v_Context ,new ExecuteEvent());
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
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_For));
        
        toJson(v_For);
        
        try
        {
            Help.forName("org.hy.common.callflow.junit.cflow043XCQL.program.Program");
            Help.forName("org.hy.common.callflow.junit.cflow044Java.DynamicInfo");
            
            // Class.forName("org.hy.common.callflow.junit.cflow043XCQL.program.Program");
            // Class.forName("org.hy.common.callflow.junit.cflow044Java.DynamicInfo");      // 它会报错
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
}
