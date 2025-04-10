package org.hy.common.callflow.junit.cflow020;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow020.program.Program;
import org.hy.common.callflow.node.XSQLConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.Test;





/**
 *测试单元：编排引擎020：XSQL元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-10
 * @version     v1.0
 */
public class JU_CFlow020 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    @SuppressWarnings("unchecked")
    public JU_CFlow020() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            
            String v_XmlRoot = this.getClass().getResource("").getFile();
            
            this.loadXML("config/startup.Config.xml"                   ,v_XmlRoot);
            this.loadXML((List<Param>)XJava.getObject("StartupConfig") ,v_XmlRoot);
        }
    }
    
    
    
    @Test
    public void test_CFlow020() throws InterruptedException
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        XSQLConfig           v_XSQLC  = (XSQLConfig) XJava.getObject("XSQLC_CF020_1");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        v_Context.put("beginTime" ,"2025-04-09 09:00:00");
        v_Context.put("endTime"   ,"2025-04-09 10:00:00");
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_XSQLC);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_XSQLC ,v_Context);
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Error XID = " + v_Result.getExecuteXID());
            System.out.println("Error Msg = " + v_Result.getException().getMessage());
            if ( v_Result.getException() instanceof TimeoutException )
            {
                System.out.println("is TimeoutException");
            }
            v_Result.getException().printStackTrace();
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_XSQLC));
    }
    
}
