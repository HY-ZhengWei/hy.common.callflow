package org.hy.common.callflow.language.java.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.language.JavaConfig;
import org.hy.common.callflow.language.java.CacheJavaInfo;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;
import org.hy.common.xml.plugins.XJavaSpringBootLoadingText;





/**
 * 动态创建 AnnotationConfigServletWebServerApplicationContext 的实例
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-04-18
 * @version     v1.0
 */
public class XJavaSpringBoot
{
    
    private static final Logger $Logger = new Logger(XJavaSpringBoot.class ,true);
    
    
    
    private XJavaSpringBoot()
    {
        // 不允许构造对象
    }
    
    
    
    /**
     * 按运行时中不同的SpringBoot版本，运行创建 AnnotationConfigServletWebServerApplicationContext 的实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-04-18
     * @version     v1.0
     *
     * @return
     */
    public static Class<?> getAnnotationConfigServletWebServerApplicationContext()
    {
        Class<?>   v_Ret               = null;
        JavaConfig v_JConfig           = new JavaConfig();
        int        v_SpringBootVersion = XJavaSpringBootLoadingText.getVersion();
        v_JConfig.setXid("XJavaSpringBoot");
        v_JConfig.setJava(XJavaSpringBootLoadingText.getJava(v_SpringBootVersion));
        v_JConfig.setReturnID("XAnnotationConfigServletWebServerApplicationContext");
        
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_JConfig);
        if ( !v_CheckRet.get() )
        {
            $Logger.error(v_CheckRet.getParamStr());  // 打印静态检查不合格的原因
            return null;
        }
        
        // 没有此步下面的执行编排无法成功，因为XJava在初始本对象的过程还没有完成呢
        XJava.putObject(v_JConfig.getXid() ,v_JConfig);
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        ExecuteResult       v_Result  = CallFlow.execute(v_JConfig ,v_Context);
        if ( !v_Result.isSuccess() )
        {
            StringBuilder v_ErrorLog = new StringBuilder();
            v_ErrorLog.append("Error XID = " + v_Result.getExecuteXID()).append("\n");
            v_ErrorLog.append("Error Msg = " + v_Result.getException().getMessage());
            if ( v_Result.getException() instanceof TimeoutException )
            {
                v_ErrorLog.append("is TimeoutException");
            }
            $Logger.error(v_ErrorLog.toString() ,v_Result.getException());
        }
        else
        {
            CacheJavaInfo v_CacheJavaInfo = (CacheJavaInfo) v_Context.get("XAnnotationConfigServletWebServerApplicationContext");
            if ( v_CacheJavaInfo != null )
            {
                v_Ret = v_CacheJavaInfo.getClazz();
            }
        }
        
        v_Context.clear();
        v_Context = null;
        return v_Ret;
    }
    
}
