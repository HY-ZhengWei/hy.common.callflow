package org.hy.common.callflow.junit.cflow009;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow009.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎009：等待N秒、自循环
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow009
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow009() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow009()
    {
        test_CFlow009_Inner();
        System.out.println("\n");
        test_CFlow009_Inner();
    }
    
    
    
    private void test_CFlow009_Inner()
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF009_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        if ( Help.isNull(v_FirstNode.getTreeIDs()) )
        {
            CallFlow.getHelpExecute().calcTree(v_FirstNode);
        }
        
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
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        this.println(v_FirstResult);
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        this.println(v_FirstResult);
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_FirstNode));
    }
    
    
    
    /**
     * 打印执行路径
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Result
     */
    private void println(ExecuteResult i_Result)
    {
        System.out.println(StringHelp.rpad(i_Result.getExecuteTreeID() ,20 ," ") 
                         + " " 
                         + StringHelp.rpad(i_Result.getTreeID() ,20 ," ") 
                         + " " 
                         + Date.toTimeLenNano(i_Result.getEndTime() - i_Result.getBeginTime())
                         + StringHelp.lpad("" ,i_Result.getNestingLevel() * 4 ," ")
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
