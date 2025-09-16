package org.hy.common.callflow.junit.cflow032Safe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.cflow032Safe.program.Program;
import org.hy.common.callflow.safe.EncryptFileConfig;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎032：密文元素、解文元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-15
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow032 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow032() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow032() throws ClassNotFoundException, IOException
    {
        test_CFlow032_Inner();
    }
    
    
    
    private void test_CFlow032_Inner() throws ClassNotFoundException, IOException
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        EncryptFileConfig   v_EncryptFile = (EncryptFileConfig) XJava.getObject("XENF_CF032");
        Map<String ,Object> v_Context     = new HashMap<String ,Object>();
        String              v_FileName    = "C:\\Users\\ZLX\\Desktop\\火车票.xlsx";
        FileHelp            v_FHelp       = new FileHelp();
        InputStream         v_InputStream = new ByteArrayInputStream(v_FHelp.getContentByte(v_FileName));
        
        // v_Context.put("FileName" ,v_FileName);      // 测试：字符路径
        v_Context.put("FileName" ,v_InputStream);   // 测试：数据流
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_EncryptFile);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_EncryptFile ,v_Context);
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
        System.out.println(CallFlow.getHelpExport().export(v_EncryptFile));
    }
    
}
