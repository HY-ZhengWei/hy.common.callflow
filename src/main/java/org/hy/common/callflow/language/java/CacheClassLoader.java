package org.hy.common.callflow.language.java;

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
            $SystemClassLoader = Thread.currentThread().getContextClassLoader();
            
            // 取代系统上下文类加载器
            Thread.currentThread().setContextClassLoader($CacheClassLoader);
        }
        return $CacheClassLoader;
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
