package org.hy.common.callflow.junit.cflow003;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow003.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎003：两个节点与两个条件逻辑的测试，并且仅为一个参数的条件，一个为Boolean条件，一个为非空对象
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow003
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow003() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow003()
    {
        this.test_CFlow003_Inner();
        System.out.println("\n");
        this.test_CFlow003_Inner();
    }
    
    
    
    private void test_CFlow003_Inner()
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF003_001");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        // 传值 9 或 传值 -1 或 不传值
        v_Context.put("NumParam"  ,9);
        // 传值 null 或 不为 null
        v_Context.put("NULLValue" ,null);
        
        ExecuteResult v_Result = CallFlow.execute(v_FirstNode ,v_Context);
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
        ExecuteResult v_NodeResult = v_Result;
        do
        {
            System.out.println(StringHelp.lpad(v_NodeResult.getIndexNo() ,3 ," ") 
                             + " " 
                             + Date.toTimeLenNano(v_NodeResult.getBeginTime()) 
                             + " ~ "
                             + Date.toTimeLenNano(v_NodeResult.getEndTime()) 
                             + " "
                             + Date.toTimeLenNano(v_NodeResult.getEndTime() - v_NodeResult.getBeginTime())
                             + " callXID=" + v_NodeResult.getExecuteXID() + " is " + v_NodeResult.isSuccess());
            v_NodeResult = v_NodeResult.getPrevious();
        }
        while ( v_NodeResult != null );
    }
    
}
