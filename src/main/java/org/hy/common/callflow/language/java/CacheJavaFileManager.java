package org.hy.common.callflow.language.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import org.hy.common.Help;





/**
 * 内存编译源码的文件管理者
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
public class CacheJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> 
{
    
    /** 缓存所有编译码对象。Map.key为类名称 */
    private static final Map<String ,CacheJavaFileObject> $ClassJavaFileObjects = new HashMap<String ,CacheJavaFileObject>();
    
    /** 缓存所有类加载器。Map.key为类名称 */
    private static final Map<String ,CacheClassLoader>    $ClassLoaders         = new HashMap<String ,CacheClassLoader>();
    
    /** 管理者的采用单例模式 */
    private static CacheJavaFileManager                   $CacheJavaFileManager = null;
    
    /** 编译器 */
    private static JavaCompiler                           $JavaCompiler;
    
    
    
    /**
     * 获取实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @return
     */
    public static CacheJavaFileManager getInstanceof()
    {
        synchronized ( $ClassJavaFileObjects )
        {
            if ( $CacheJavaFileManager == null )
            {
                // 1. 获取Java编译器实例
                // JavaCompiler是 JDK 内置的编译器 API，用于编译 Java 源码（需使用 JDK 运行，JRE 无此 API）
                $JavaCompiler = ToolProvider.getSystemJavaCompiler();
                if ( $JavaCompiler == null ) 
                {
                    return null;
                }
                
                // 2. 自定义文件管理器（管理内存中的源码/字节码）
                StandardJavaFileManager v_StandardJavaManager = $JavaCompiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
                $CacheJavaFileManager = new CacheJavaFileManager(v_StandardJavaManager);
            }
        }
        
        return $CacheJavaFileManager;
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_JavaFileManager  JDK的Java文件管理器
     */
    private CacheJavaFileManager(JavaFileManager i_JavaFileManager)
    {
        super(i_JavaFileManager);
    }
    
    
    
    /**
     * 编译源码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName   类名称
     * @param i_SourceCode  源码
     * @return
     */
    public boolean compiler(String i_ClassName ,String i_SourceCode)
    {
        if ( $CacheJavaFileManager == null )
        {
            return false;
        }
        
        if ( Help.isNull(i_ClassName) || Help.isNull(i_SourceCode) )
        {
            return false;
        }
                
        // 3. 创建编译任务（可选：如编码、类路径）
        //   参数01：输出流（null使用系统默认）
        //   参数02：文件管理器（自定义内存文件管理器）
        //   参数03：诊断监听器
        //   参数04：编译参数
        //   参数05：注解处理器
        //   参数06：待编译的源码
        CompilationTask v_CompilationTask = $JavaCompiler.getTask(null                                                                  
                                                              ,$CacheJavaFileManager
                                                              ,null
                                                              ,Arrays.asList("-encoding", "UTF-8")
                                                              ,null
                                                              ,$CacheJavaFileManager.getJavaFileObjects(i_ClassName ,i_SourceCode));
        
        // 4. 执行编译
        boolean v_CompileSuccess = v_CompilationTask.call();
        return v_CompileSuccess;
    }
    
    
    
    /**
     * 类加载
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String i_ClassName) throws ClassNotFoundException
    {
        if ( $CacheJavaFileManager == null )
        {
            return null;
        }
        
        if ( Help.isNull(i_ClassName) )
        {
            return null;
        }
        
        CacheClassLoader v_ClassLoader = null;
        synchronized ($ClassLoaders)
        {
            v_ClassLoader = $ClassLoaders.get(i_ClassName);
            if ( v_ClassLoader != null )
            {
                $ClassLoaders.remove(i_ClassName);
                v_ClassLoader = null;
            }
            
            v_ClassLoader = new CacheClassLoader(i_ClassName);
            $ClassLoaders.put(i_ClassName ,v_ClassLoader);
            
            CacheClassLoader.getInstanceof();
        }
        
        // 5. 获取编译后的字节码，通过自定义类加载器加载
        return v_ClassLoader.loadClass();
    }
    
    
    
    /**
     * 获取某个内存编译源码对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName  类名称
     * @return
     */
    public CacheJavaFileObject get(String i_ClassName)
    {
        if ( $CacheJavaFileManager == null )
        {
            return null;
        }
        else
        {
            return $ClassJavaFileObjects.get(i_ClassName);
        }
    }
    
    
    
    /**
     * 获取待编译的源码文件对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName   类名称
     * @param i_SourceCode  源码
     * @return
     */
    public Iterable<JavaFileObject> getJavaFileObjects(String i_ClassName ,String i_SourceCode) 
    {
        return Collections.singletonList(new CacheJavaFileObject(i_ClassName, i_SourceCode));
    }

    
    
    /**
     * 为编译输出创建内存文件对象
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_Location
     * @param i_ClassName   类名称
     * @param i_Kind        种类（源码、编译后的字节码）。实现值只为编译后的字节码
     * @param i_Sibling     编译字节码对应的源码。即：CacheJavaFileObject类
     * @return
     * @throws IOException
     *
     * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location, java.lang.String, javax.tools.JavaFileObject.Kind, javax.tools.FileObject)
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location i_Location ,String i_ClassName ,Kind i_Kind ,FileObject i_Sibling) throws IOException
    {
        synchronized ($ClassJavaFileObjects)
        {
            String v_SourceCode = null;
            if ( i_Sibling instanceof CacheJavaFileObject )
            {
                v_SourceCode = ((CacheJavaFileObject) i_Sibling).getSourceCode();
            }
            
            CacheJavaFileObject v_JavaFileObject = new CacheJavaFileObject(i_ClassName ,v_SourceCode ,i_Kind);
            $ClassJavaFileObjects.put(i_ClassName ,v_JavaFileObject);
            
            return v_JavaFileObject;
        }
    }

    
    
    /**
     * 获取类的字节码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName  类名称
     * @return
     */
    public byte [] getClassBytes(String i_ClassName)
    {
        return $ClassJavaFileObjects.get(i_ClassName).getByteCode();
    }
    
}
