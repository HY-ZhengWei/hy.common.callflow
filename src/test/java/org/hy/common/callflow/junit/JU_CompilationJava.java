package org.hy.common.callflow.junit;

import java.lang.reflect.InvocationTargetException;

import org.hy.common.StringHelp;
import org.hy.common.callflow.language.java.CacheJavaFileManager;
import org.junit.Test;





/**
 * 测试单元：动态编译Java源码
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
public class JU_CompilationJava
{
 
    @Test
    public void test() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
    {
        String v_ClassName  = "org.hy.common.callflow.junit.CompilationJava";
        String v_SourceCode = """
                                package org.hy.common.callflow.junit;
                
                                import org.hy.common.Date;
                                
                                public class CompilationJava
                                {
                                
                                    @Override
                                    public String toString()
                                    {
                                        return Date.getNowTime().getFull();
                                    }
                                    
                                }
                              """;
        
        String v_SourceCod2 = """
                                package org.hy.common.callflow.junit;
                
                                import org.hy.common.Date;
                                
                                public class CompilationJava
                                {
                                
                                    @Override
                                    public String toString()
                                    {
                                        return "我是另一个";
                                    }
                                    
                                }
                              """;
        
        CacheJavaFileManager v_CacheJavaManager = CacheJavaFileManager.getInstanceof();
        boolean              v_CompileSuccess   = v_CacheJavaManager.compiler(v_ClassName ,v_SourceCode);
        if ( !v_CompileSuccess )
        {
            throw new IllegalStateException("类编译失败：" + v_ClassName);
        }
        
        Class<?> v_Class = v_CacheJavaManager.loadClass(v_ClassName);
        Object   v_Object = v_Class.getDeclaredConstructor().newInstance();
        System.out.println(v_Object);
        
        for (int x=1; x<=10000; x++)
        {
            // 尝试第二次编译相同的类
            v_CompileSuccess = v_CacheJavaManager.compiler(v_ClassName ,StringHelp.replaceAll(v_SourceCod2 ,"我是另一个" ,"我是" + x + "号"));
            
            // 尝试第二次加载相同的类
            v_Class  = v_CacheJavaManager.loadClass(v_ClassName);
            v_Object = v_Class.getDeclaredConstructor().newInstance();
            System.out.println(v_Object);
        }
    }
    
}
