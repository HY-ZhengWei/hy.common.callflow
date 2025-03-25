package org.hy.common.callflow.junit.cflow012;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Date;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow012.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎012：For循环元素。循环List、Set、Collection、数组、Map
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-06
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow012 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow012() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow012()
    {
        test_CFlow012_Inner();
        System.out.println("\n");
        test_CFlow012_Inner();
    }
    
    
    
    private void test_CFlow012_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        ForConfig           v_ForConfig = (ForConfig) XJava.getObject("XFor_CF012_1");
        Map<String ,Object> v_Context   = new HashMap<String ,Object>();
        
        // 测试数据：List
        List<String> v_ListDatas = new ArrayList<String>();
        v_ListDatas.add("123");
        v_ListDatas.add("456");
        v_ListDatas.add("789");
        // v_Context.put("Datas" ,v_ListDatas);
        
        // 测试数据：Set
        Set<String> v_SetDatas = new HashSet<String>();
        v_SetDatas.add("123");
        v_SetDatas.add("456");
        v_SetDatas.add("789");
        // v_Context.put("Datas" ,v_SetDatas);
        
        // 测试数据：Collection
        // v_Context.put("Datas" ,(Collection<?>)v_ListDatas);
        
        // 测试数据：数组
        // v_Context.put("Datas" ,v_ListDatas.toArray());
        
        // 测试数据：Map
        Map<String ,Integer> v_MapDatas = new HashMap<String ,Integer>();
        v_MapDatas.put("K123" ,123);
        v_MapDatas.put("K456" ,456);
        v_MapDatas.put("K789" ,789);
        v_Context.put("Datas" ,v_MapDatas);
        
        ExecuteResult v_Result = CallFlow.execute(v_ForConfig ,v_Context);
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
        
        System.out.println();
        
        // 第二种方法获取首个执行结果
        v_FirstResult = CallFlow.getHelpExecute().getFirstResult(v_Result);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_ForConfig));
        
        toJson(v_ForConfig);
    }
    
}
