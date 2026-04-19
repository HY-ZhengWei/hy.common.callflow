package org.hy.common.callflow.language.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;

import org.hy.common.Help;





/**
 * 类的动态加载器
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
public class CacheClassLoader extends SecureClassLoader 
{
    
    /** 为取代系统默认类加载器，而定义一个顶级的可公用的加载器单例 */
    private static CacheClassLoader $CacheClassLoader  = null;
    
    /** 运行时环境原本的类加载器 */
    private static ClassLoader      $SystemClassLoader = null;
    
    
    
    /** 类名称 */
    private final String className;
    
    
    
    /**
     * 一个顶级的可公用的加载器单例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @return
     */
    public synchronized static CacheClassLoader getInstanceof()
    {
        if ( $CacheClassLoader == null )
        {
            $CacheClassLoader  = new CacheClassLoader();
            if ( $SystemClassLoader == null )
            {
                $SystemClassLoader = Thread.currentThread().getContextClassLoader();
            }
            
            // 取代系统上下文类加载器
            Thread.currentThread().setContextClassLoader($CacheClassLoader);
        }
        return $CacheClassLoader;
    }
    
    
    
    /**
     * 构建完整 classpath，包含项目所有 jar 和类路径
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-04-18
     * @version     v1.0
     *
     * @return
     */
    public synchronized static String buildClassPath()
    {
        StringBuilder v_CP = new StringBuilder();
        
        if ( $SystemClassLoader == null )
        {
            $SystemClassLoader = Thread.currentThread().getContextClassLoader();
        }
        
        // 1. 兼容 Tomcat 类加载器
        try
        {
            Class<?> v_TomcatClass = Help.forName("org.apache.catalina.webresources.StandardRoot");
            URL []   v_Urls        = (URL []) v_TomcatClass.getMethod("getURLs").invoke($SystemClassLoader);
            for (URL v_Url : v_Urls)
            {
                String v_Path     = v_Url.getPath();
                int    v_LibIndex = v_Path.indexOf("WEB-INF\\lib\\");
                if ( v_LibIndex > 0 )
                {
                    v_Path = v_Path.substring(0 ,v_LibIndex + 12) + "*";
                }
                else
                {
                    v_LibIndex = v_Path.indexOf("WEB-INF/lib/");
                    if ( v_LibIndex > 0 )
                    {
                        v_Path = v_Path.substring(0 ,v_LibIndex + 12) + "*";
                    }
                }
                
                // 防止重复添加
                if ( v_CP.toString().indexOf(v_Path) < 0 )
                {
                    v_CP.append(v_Path).append(System.getProperty("path.separator"));
                }
            }
        }
        catch (Exception ignored)
        {
            
        }
        
        // 2. 降级：URLClassLoader
        try
        {
            if ( $SystemClassLoader instanceof URLClassLoader v_URLClassLoader )
            {
                for (URL v_Url : v_URLClassLoader.getURLs())
                {
                    String v_Path     = v_Url.getPath();
                    int    v_LibIndex = v_Path.indexOf("WEB-INF\\lib\\");
                    if ( v_LibIndex > 0 )
                    {
                        v_Path = v_Path.substring(0 ,v_LibIndex + 12) + "*";
                    }
                    else
                    {
                        v_LibIndex = v_Path.indexOf("WEB-INF/lib/");
                        if ( v_LibIndex > 0 )
                        {
                            v_Path = v_Path.substring(0 ,v_LibIndex + 12) + "*";
                        }
                    }
                    
                    // 防止重复添加
                    if ( v_CP.toString().indexOf(v_Path) < 0 )
                    {
                        v_CP.append(v_Path).append(System.getProperty("path.separator"));
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        
        // 3. 追加系统路径
        v_CP.append(System.getProperty("java.class.path"));
        
        return v_CP.toString();
    }
    
    
    
    private CacheClassLoader()
    {
        this.className = null;
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName  类名称
     */
    public CacheClassLoader(String i_ClassName)
    {
        if ( Help.isNull(i_ClassName) )
        {
            throw new NullPointerException("Classname is null");
        }
        this.className = i_ClassName;
    }
    
    
    
    /**
     * 加载类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass() throws ClassNotFoundException 
    {
        return loadClass(this.className);
    }
    
    
    
    /**
     * 重写findClass，加载自定义字节码
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName  类名
     * @return
     * @throws ClassNotFoundException
     *
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String i_ClassName) throws ClassNotFoundException
    {
        CacheJavaFileObject v_CacheJavaFile = CacheJavaFileManager.getInstanceof().get(i_ClassName);
        if ( v_CacheJavaFile == null )
        {
            Class<?> v_Class = super.findClass(i_ClassName);
            if ( v_Class == null )
            {
                if ( $SystemClassLoader != null )
                {
                    v_Class = $SystemClassLoader.loadClass(i_ClassName);
                }
            }
            return v_Class;
        }
        
        // 将字节码转换为Class对象
        byte [] v_ClassByte = v_CacheJavaFile.getByteCode();
        return super.defineClass(i_ClassName ,v_ClassByte ,0 ,v_ClassByte.length);
    }
    
}
