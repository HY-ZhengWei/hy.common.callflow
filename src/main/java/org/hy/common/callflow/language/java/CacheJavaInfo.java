package org.hy.common.callflow.language.java;

import org.hy.common.Help;





/**
 * 内存编译源码的信息类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-08
 * @version     v1.0
 */
public class CacheJavaInfo
{
    
    /** 包路径 */
    private String packageName;
    
    /** 类名称（仅名类名称，不包括包路径） */
    private String className;
    
    /** Java源码 */
    private String sourceCode;
    
    
    
    /**
     * 获取类名称的全路径
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @return
     */
    public String getClassNameFull()
    {
        if ( Help.isNull(this.packageName) || Help.isNull(this.className) )
        {
            return null;
        }
        else
        {
            return this.packageName + "." + this.className;
        }
    }
    
    
    
    /**
     * 获取：包路径
     */
    public String getPackageName()
    {
        return packageName;
    }


    
    /**
     * 设置：包路径
     * 
     * @param i_PackageName 包路径
     */
    public void setPackageName(String i_PackageName)
    {
        this.packageName = i_PackageName;
    }



    /**
     * 获取：类名称（仅名类名称，不包括包路径）
     */
    public String getClassName()
    {
        return className;
    }


    
    /**
     * 设置：类名称（仅名类名称，不包括包路径）
     * 
     * @param i_ClassName 类名称（仅名类名称，不包括包路径）
     */
    public void setClassName(String i_ClassName)
    {
        this.className = i_ClassName;
    }


    
    /**
     * 获取：Java源码
     */
    public String getSourceCode()
    {
        return sourceCode;
    }


    
    /**
     * 设置：Java源码
     * 
     * @param i_SourceCode Java源码
     */
    public void setSourceCode(String i_SourceCode)
    {
        this.sourceCode = i_SourceCode;
    }

}
