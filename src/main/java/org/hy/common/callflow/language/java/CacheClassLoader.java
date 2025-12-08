package org.hy.common.callflow.language.java;

import java.security.SecureClassLoader;





/**
 * 类的动态加载器
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
public class CacheClassLoader extends SecureClassLoader 
{
    
    /** 类名称 */
    private String className;
    
    
    
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
            return super.findClass(i_ClassName);
        }
        
        // 将字节码转换为Class对象
        byte [] v_ClassByte = v_CacheJavaFile.getByteCode();
        return super.defineClass(i_ClassName ,v_ClassByte ,0 ,v_ClassByte.length);
    }
    
}
