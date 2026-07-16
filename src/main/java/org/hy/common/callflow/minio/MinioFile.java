package org.hy.common.callflow.minio;

import java.util.concurrent.TimeUnit;

import org.hy.common.Help;
import org.hy.common.StringHelp;





/**
 * 用于Minio存对元素的上传或下载文件
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-07-14
 * @version     v1.0
 */
public class MinioFile
{
    
    /** 本地文件（上传时）或远程文件（下载时）的全路径 */
    private String   source;
    
    /** 远程文件（上传时）或本地文件（下载时）的全路径 或 分享文件的URL */
    private String   target;
    
    /** 本地文件（下载时）的路径 */
    private String   targetDir;
    
    /** 本地文件（下载时）的文件名称 */
    private String   targetName;
    
    /** 过期时长 */
    private Integer  expiry;
    
    /** 过期时间类型 */
    private TimeUnit expiryUnit;
    
    /** 异常信息。无或空表示成功 */
    private String  errorInfo;

    
    
    public MinioFile(String i_Source ,String i_Target) 
    {
        this.source = StringHelp.replaceAll(i_Source.trim() ,"\\" ,"/");
        this.target = StringHelp.replaceAll(i_Target.trim() ,"\\" ,"/");
    }
    
    
    public MinioFile(String i_Source ,Integer i_Expiry ,TimeUnit i_ExpiryUnit) 
    {
        this.source     = StringHelp.replaceAll(i_Source.trim() ,"\\" ,"/");
        this.expiry     = Help.max(i_Expiry ,1);
        this.expiryUnit = i_ExpiryUnit;
    }
    
    
    /**
     * 获取：本地文件（上传时）或远程文件（下载时）的全路径
     */
    public String getSource()
    {
        return source;
    }

    
    /**
     * 获取：远程文件（上传时）或本地文件（下载时）的全路径 或 分享文件的URL
     */
    public String getTarget()
    {
        return target;
    }
    
    
    /**
     * 设置：远程文件（上传时）或本地文件（下载时）的全路径 或 分享文件的URL
     * 
     * @param i_Target 远程文件（上传时）或本地文件（下载时）的全路径 或 分享文件的URL
     */
    public void setTarget(String i_Target)
    {
        this.target = i_Target;
    }

    
    /**
     * 获取：本地文件（下载时）的路径
     */
    public String getTargetDir()
    {
        return targetDir;
    }

    
    /**
     * 设置：本地文件（下载时）的路径
     * 
     * @param i_TargetDir 本地文件（下载时）的路径
     */
    public void setTargetDir(String i_TargetDir)
    {
        this.targetDir = i_TargetDir;
    }

    
    /**
     * 获取：本地文件（下载时）的文件名称
     */
    public String getTargetName()
    {
        return targetName;
    }

    
    /**
     * 设置：本地文件（下载时）的文件名称
     * 
     * @param i_TargetName 本地文件（下载时）的文件名称
     */
    public void setTargetName(String i_TargetName)
    {
        this.targetName = i_TargetName;
    }


    /**
     * 获取：异常信息。无或空表示成功
     */
    public String getErrorInfo()
    {
        return errorInfo;
    }
    
    
    /**
     * 获取：过期天数
     */
    public Integer getExpiry()
    {
        return expiry;
    }

    
    /**
     * 获取：过期时间类型
     */
    public TimeUnit getExpiryUnit()
    {
        return expiryUnit;
    }


    /**
     * 设置：异常信息。无或空表示成功
     * 
     * @param i_ErrorInfo 异常信息。无或空表示成功
     */
    public void setErrorInfo(String i_ErrorInfo)
    {
        this.errorInfo = i_ErrorInfo;
    }
    
}
