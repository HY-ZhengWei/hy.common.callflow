package org.hy.common.callflow.junit.cflow033Python;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow033Python.program.Program;
import org.hy.common.callflow.python.PythonConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎033：蟒蛇元素
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-09-19
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow033 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    public static void main(String [] args) throws Exception
    {
        new JU_CFlow033().test_CFlow033();
    }
    
    
    
    public JU_CFlow033() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow033() throws ClassNotFoundException, IOException
    {
        test_CFlow033_Inner();
    }
    
    
    
    private void test_CFlow033_Inner() throws ClassNotFoundException, IOException
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        PythonConfig        v_Python  = (PythonConfig) XJava.getObject("XPython_CF033_蟒蛇元素");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        List<String> v_JavaVarList = new ArrayList<String>();
        v_JavaVarList.add("L 1");
        v_JavaVarList.add("L 2");
        v_JavaVarList.add("L 3");
        
        v_Context.put("JavaVarString" ,"Python from Java");
        v_Context.put("JavaVarDouble" ,3.14D);
        v_Context.put("JavaVarList"   ,v_JavaVarList);
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_Python);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_Python ,v_Context);
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
        
        System.out.println("返回结果：" + v_Result.getResult());
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_Python));
    }
    
}
