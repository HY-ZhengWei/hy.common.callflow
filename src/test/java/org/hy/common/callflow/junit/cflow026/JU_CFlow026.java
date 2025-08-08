package org.hy.common.callflow.junit.cflow026;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Return;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.event.WSPushConfig;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow026.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎026：点推元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-08
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow026 extends JUBase
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow026() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow026()
    {
        test_CFlow026_Inner();
    }
    
    
    
    private void test_CFlow026_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        WSPushConfig        v_WSPush  = (WSPushConfig) XJava.getObject("XWSPush");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        v_Context.put("Message" ,"文本消息");
        v_Context.put("Message" ,new Param("对象信息" ,"对象数据"));
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_WSPush);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_WSPush));
        
        toJson(v_WSPush);
    }
    
}
