package org.hy.common.callflow.junit.cflow029;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.cache.CacheSetConfig;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow028.program.AppConfig;
import org.hy.common.callflow.junit.cflow029.program.Program;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎029：缓存写元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-12
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow029 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    @SuppressWarnings("unchecked")
    public JU_CFlow029() throws Exception
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
    public void test_CFlow029()
    {
        test_CFlow029_Inner();
    }
    
    
    
    private void test_CFlow029_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        CacheSetConfig      v_CS      = (CacheSetConfig) XJava.getObject("XCS_Create_ByJson");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        AppConfig           v_RowData = new AppConfig();
        
        v_RowData.setId(StringHelp.getUUID9n());
        v_RowData.setUpdateTime(new Date());
        v_RowData.setUpdateUserID("HY");
        v_RowData.setIsDel(1);
        
        // 删除Redis实例，模拟本地缓存模式
        // XJava.remove("RedisOperation_MS_Common");
        
        v_Context.put("RowDataObject" ,v_RowData);
        v_Context.put("PKID"          ,v_RowData.getId());
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_CS);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_CS ,v_Context);
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
        
        System.out.println("返回结果：" + v_Result.getResult());
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_CS));
    }
    
}
