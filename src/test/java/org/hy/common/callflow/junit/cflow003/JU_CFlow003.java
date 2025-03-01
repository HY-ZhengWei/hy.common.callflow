package org.hy.common.callflow.junit.cflow003;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.junit.cflow003.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJSON;
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
    public void test_CFlow003() throws Exception
    {
        this.test_CFlow003_Inner();
        System.out.println("\n");
        this.test_CFlow003_Inner();
    }
    
    
    
    private void test_CFlow003_Inner() throws Exception
    {
        // 初始化被编排的执行程序
        XJava.putObject("XProgram" ,new Program());
        
        // 启动编排
        NodeConfig          v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF003_001");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        ExecuteResult       v_ER        = null;
        IExecute            v_E         = null;
        
        if ( Help.isNull(v_FirstNode.getTreeIDs()) )
        {
            CallFlow.getHelpExecute().calcTree(v_FirstNode);
        }
        
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
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        this.println(v_FirstResult);
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        this.println(v_FirstResult);
        
        System.out.println();
        // 在走第一个判定为真，第二个判定为假时，否则不存此路径
        v_ER = CallFlow.getHelpExecute().findExecuteTreeID(v_Result ,"1-1-1-2");
        if ( v_ER != null )
        {
            System.out.println("1-1-1-2 的执行逻辑 " + v_ER.getExecuteLogic());
            v_E = CallFlow.getHelpExecute().findTreeID(v_FirstNode ,"1-1-2");
            System.out.println("1-1-2   的执行逻辑 " + v_E.toString());
        }
        
        // 在走第一个判定假时，否则不存此路径
        v_ER = CallFlow.getHelpExecute().findExecuteTreeID(v_Result ,"1-1-2");
        if ( v_ER != null )
        {
            System.out.println("1-1-2   的执行逻辑 " + CallFlow.getHelpExecute().findExecuteTreeID(v_Result ,"1-1-2")  .getExecuteLogic());
            v_E = CallFlow.getHelpExecute().findTreeID(v_FirstNode ,"1-1-1-2");
            System.out.println("1-1-1-2 的执行逻辑 " + v_E.toString());
        }
        System.out.println();
        
        test_ToJson();
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
    
    
    
    @Test
    public void test_ToJson() throws Exception
    {
        NodeConfig v_FirstNode = (NodeConfig) XJava.getObject("XNode_CF003_001");
        XJSON      v_XJson     = new XJSON();
        
        v_XJson.setReturnNVL(false);
        
        System.out.println(v_XJson.toJson(v_FirstNode).toJSONString());
    }
    
}
