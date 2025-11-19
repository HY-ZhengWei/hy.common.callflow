package org.hy.common.callflow.junit.cflow042ErrorContinue;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.ExecuteResultNext;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow042ErrorContinue.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎042：整个编排异常后尝试重做
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-17
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow042 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow042() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow042()
    {
        this.test_CFlow042_Inner();
    }
    
    
    
    private void test_CFlow042_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        NodeConfig          v_Node    = (NodeConfig) XJava.getObject("XNode_CF041_ThreeInOne_执行");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        v_Context.put("CounterMax" ,5);     // 超时完成
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_Node);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_Node ,v_Context);
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
        
        
        
        // 适用场景：异常后，异常点已恢复，延续编排上下文，重新执行异常点。
        //        精准续跑、编排不中断，如果异常点二次执行成功，继续执行后面编排元素。
        //
        // 第一次异常后，从异常点的地方续跑。
        // 已成功执行的默认不再重新执行（除用户标记的和主动重做的元素外），仅对异常点重新执行
        Map<String ,Object> v_OldContext = v_Context;                      // 第一次执行后的编排上下文
        Map<String ,Object> v_NewContext = new HashMap<String ,Object>();  // 第二次执行时创建新的上下文
        
        v_NewContext.put("CounterMax" ,5);                                 // 第二次时的执行参数（与第一次一样）
        
        CallFlow.putContinue(v_NewContext ,v_OldContext);                  // 分析异常后的续跑信息
        v_Result = CallFlow.execute(v_Node ,v_NewContext);                 // 第二次执行，请用新的上下文
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
        v_FirstResult = CallFlow.getFirstResult(v_NewContext);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_Node));
        
        toJson(v_Node);
        
        ExecuteResultNext v_ERNext = new ExecuteResultNext(v_FirstResult);
        ExecuteResult     v_Next   = null;
        while ( (v_Next = v_ERNext.next()) != null )
        {
            System.out.println(v_Next.getExecuteXID());
        }
    }
    
}
