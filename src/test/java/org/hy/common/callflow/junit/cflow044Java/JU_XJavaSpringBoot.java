package org.hy.common.callflow.junit.cflow044Java;

import org.hy.common.callflow.language.java.xml.XJavaSpringBoot;
import org.junit.Test;




/**
 * 测试单元：按运行时中不同的SpringBoot版本，运行创建 AnnotationConfigServletWebServerApplicationContext 的实例 
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-04-18
 * @version     v1.0
 */
public class JU_XJavaSpringBoot
{
    
    @Test
    public void test()
    {
        try
        {
            Object v_ACSWSAC = XJavaSpringBoot.createAnnotationConfigServletWebServerApplicationContext();
            System.out.println(v_ACSWSAC.getClass());
        }
        catch (Throwable exce)
        {
            exce.printStackTrace();
        }
    }
    
}
