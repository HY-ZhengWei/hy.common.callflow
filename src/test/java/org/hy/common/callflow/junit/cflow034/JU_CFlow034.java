package org.hy.common.callflow.junit.cflow034;

import java.io.IOException;

import org.hy.common.Return;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.junit.cflow033Python.program.Program;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎034：复杂业务测试
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-09-23
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow034 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    public JU_CFlow034() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            XJava.parserAnnotation(this.getClass().getName());
        }
    }
    
    
    
    @Test
    public void test_CFlow034() throws ClassNotFoundException, IOException
    {
        test_CFlow034_Inner();
    }
    
    
    
    private void test_CFlow034_Inner() throws ClassNotFoundException, IOException
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        NodeConfig  v_Node = (NodeConfig) XJava.getObject("XCFlow_Stripping_Start数据确认");
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_Node);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
    }
    
}
