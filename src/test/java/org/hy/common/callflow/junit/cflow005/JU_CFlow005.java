package org.hy.common.callflow.junit.cflow005;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow005.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎005：多个分支执行
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-25
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow005
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow005() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow005() throws Exception
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF005_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        ExecuteResult v_Result = CallFlow.execute(v_FirstNode ,v_Context);
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        this.println(v_FirstResult);
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        this.println(v_FirstResult);
        
        System.out.println();
        System.out.println("1.1.1 的执行逻辑 " + CallFlow.getHelpExecute().findTreeID       (v_Result ,"1.1.1").getExecuteLogic());
        System.out.println("1-1-1 的执行逻辑 " + CallFlow.getHelpExecute().findExecuteTreeID(v_Result ,"1-1-1").getExecuteLogic());
        System.out.println();
        
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
    }
    
    
    
    /**
     * 打印执行路径
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_Result
     */
    private void println(ExecuteResult i_Result)
    {
        System.out.println(StringHelp.rpad(i_Result.getExecuteTreeID() ,9 ," ") 
                         + " " 
                         + StringHelp.rpad(i_Result.getTreeID() ,9 ," ") 
                         + " " 
                         + Date.toTimeLenNano(i_Result.getEndTime() - i_Result.getBeginTime())
                         + " " + i_Result.getExecuteLogic()
                         + " " + Help.NVL(i_Result.getResult())
                         + " " + i_Result.isSuccess());
        
        if ( !Help.isNull(i_Result.getNexts()) )
        {
            for (ExecuteResult v_Item : i_Result.getNexts())
            {
                this.println(v_Item);
            }
        }
    }
    
}
