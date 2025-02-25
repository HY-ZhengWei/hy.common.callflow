package org.hy.common.callflow.junit.cflow002;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow002.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎002：两个节点与一个条件逻辑的测试
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-22
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow002
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow002() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow002()
    {
        this.test_CFlow002_Inner();
        System.out.println("\n");
        this.test_CFlow002_Inner();
    }
    
    
    
    private void test_CFlow002_Inner()
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF002_001");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        CallFlow.calcTree(v_FirstNode);
        
        // 传值 9 或 传值 -1 或 不传值
        v_Context.put("NumParam" ,9);
        
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
            System.out.println(StringHelp.rpad(v_NodeResult.getExecuteTreeID() ,9 ," ") 
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
