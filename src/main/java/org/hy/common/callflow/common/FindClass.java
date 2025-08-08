package org.hy.common.callflow.common;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.hy.common.Help;





/**
 * 不知道类的全路径，仅知道类名称的情况下查找此类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-08
 * @version     v1.0
 */
public class FindClass
{
    
    private FindClass()
    {
        // Nothing.
    }
    
    
    
    /**
     * 查找类名称（仅类名称，不是全路径）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param i_ClassName  查找的类名称（仅类名称，不是全路径）
     * @return             查找到时返回类元素，否则返回null
     */
    public static Class<?> finds(String i_ClassName)
    {
        ClassLoader v_ClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Enumeration<URL> v_Resources = v_ClassLoader.getResources("");
            while ( v_Resources.hasMoreElements() )
            {
                URL v_Resource = v_Resources.nextElement();
                File v_File    = new File(v_Resource.toURI());
                if ( v_File.isDirectory() )
                {
                    Class<?> v_Found = scanDirectory(v_File ,"" ,i_ClassName);
                    if ( v_Found != null )
                    {
                        return v_Found;
                    }
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return null;
    }
    
    
    
    /**
     * 在目标目录中的查找类名称（仅类名称，不是全路径）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-08
     * @version     v1.0
     *
     * @param i_Directory    扫描的目录
     * @param i_PackageName  扫描的包路径
     * @param i_ClassName    查找的类名称（仅类名称，不是全路径）
     * @return
     */
    private static Class<?> scanDirectory(File i_Directory ,String i_PackageName ,String i_ClassName)
    {
        File [] v_Files = i_Directory.listFiles();
        if ( v_Files == null )
        {
            return null;
        }
        
        for (File v_File : v_Files)
        {
            if ( v_File.isDirectory() )
            {
                String   v_NewPackage = i_PackageName.isEmpty() ? v_File.getName() : i_PackageName + "." + v_File.getName();
                Class<?> v_Found      = scanDirectory(v_File ,v_NewPackage ,i_ClassName);
                if ( v_Found != null )
                {
                    return v_Found;
                }
            }
            else if ( v_File.getName().endsWith(".class") )
            {
                String v_ClassName = v_File.getName().substring(0 ,v_File.getName().length() - 6);
                if ( v_ClassName.equals(i_ClassName) )
                {
                    try
                    {
                        String v_FullClassName = i_PackageName.isEmpty() ? v_ClassName : i_PackageName + "." + v_ClassName;
                        return Help.forName(v_FullClassName);
                    }
                    catch (ClassNotFoundException e)
                    {
                        // Nothing. 继续扫描
                    }
                }
            }
        }
        
        return null;
    }
    
}
