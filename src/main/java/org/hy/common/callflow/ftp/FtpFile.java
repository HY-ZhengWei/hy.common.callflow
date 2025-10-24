package org.hy.common.callflow.ftp;

import org.hy.common.StringHelp;





/**
 * 用于FTP文传元素的上传或下载文件
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-10-19
 * @version     v1.0
 */
public class FtpFile
{
    
    /** 本地文件（上传时）或远程文件（下载时）的全路径 */
    private String source;
    
    /** 远程文件（上传时）或本地文件（下载时）的全路径 */
    private String target;
    
    /** 异常信息。无或空表示成功 */
    private String errorInfo;

    
    
    public FtpFile(String i_Source ,String i_Target) 
    {
        this.source = StringHelp.replaceAll(i_Source.trim() ,"\\" ,"/");
        this.target = StringHelp.replaceAll(i_Target.trim() ,"\\" ,"/");
    }
    
    
    /**
     * 获取：本地文件（上传时）或远程文件（下载时）的全路径
     */
    public String getSource()
    {
        return source;
    }

    
    /**
     * 获取：远程文件（上传时）或本地文件（下载时）的全路径
     */
    public String getTarget()
    {
        return target;
    }
    
    
    /**
     * 获取：异常信息。无或空表示成功
     */
    public String getErrorInfo()
    {
        return errorInfo;
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
