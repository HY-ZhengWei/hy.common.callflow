package org.hy.common.callflow.junit.cflow016;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow016.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎016：自引用、自循环、递归
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-15
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow016 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow016() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow016() throws Exception
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF016_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        // 执行前的静态检查
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_FirstNode);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_FirstNode ,v_Context);
        System.out.println("结果：" + v_Result.getResult());
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Error XID    = " + v_Result.getExecuteXID());
            System.out.println("Error TreeID = " + v_Result.getExecuteTreeID());
            System.out.println("Error Logic  = " + v_Result.getExecuteLogic());
            v_Result.getException().printStackTrace();
        }
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_FirstNode));
        
        toJson(v_FirstNode);
    }
    
}
