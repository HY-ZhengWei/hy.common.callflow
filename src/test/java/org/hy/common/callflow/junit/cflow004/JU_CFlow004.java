package org.hy.common.callflow.junit.cflow004;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.junit.cflow004.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎003：组合条件逻辑，方法返回结果为对象
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-25
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow004
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow004() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow004() throws Exception
    {
        Condition v_Condition = (Condition) XJava.getObject("XCondition_CF004_002");
        System.out.println(v_Condition.toString());
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        // 传值大于PI 或 等于PI 或 小于PI 或 NULL
        v_Context.put("DoubleParam" ,null);
        // 传值 NULL 或 2025-02-25 或 其它时间
        v_Context.put("TimeParam"   ,null);
        
        this.test_CFlow004_Inner(v_Context);
        System.out.println("\n");
        
        
        v_Context.put("DoubleParam" ,3.1415926);
        v_Context.put("TimeParam"   ,new Date("2025-02-25"));
        this.test_CFlow004_Inner(v_Context);
        System.out.println("\n");
        
        
        v_Context.put("DoubleParam" ,10D);
        v_Context.put("TimeParam"   ,null);
        this.test_CFlow004_Inner(v_Context);
        System.out.println("\n");
        
        
        v_Context.put("DoubleParam" ,2D);
        v_Context.put("TimeParam"   ,null);
        this.test_CFlow004_Inner(v_Context);
        System.out.println("\n");
    }
    
    
    
    private void test_CFlow004_Inner(Map<String ,Object> i_Context) throws Exception
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF004_001");
        
        CallFlow.calcTree(v_FirstNode);
        
        ExecuteResult v_Result = CallFlow.execute(v_FirstNode ,i_Context);
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
                             + " " + v_NodeResult.getExecuteLogic()
                             + " " + Help.NVL(v_NodeResult.getResult())
                             + " " + v_NodeResult.isSuccess());
            v_NodeResult = v_NodeResult.getPrevious();
        }
        while ( v_NodeResult != null );
    }
    
}
