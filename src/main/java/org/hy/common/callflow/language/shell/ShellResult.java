package org.hy.common.callflow.language.shell;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;





/**
 * SH脚本元素的执行结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-25
 * @version     v1.0
 */
public class ShellResult
{
    
    /** Shell代码执行结果 */
    private String  result;
    
    /** Shell代码执行退出状态 */
    private Integer exitStatus;
    
    /** 上传文件信息 */
    private List<ShellFile> upFiles;
    
    /** 下载文件信息 */
    private List<ShellFile> downFiles;

    
    /**
     * 获取：Shell代码执行结果
     */
    public String getResult()
    {
        return result;
    }

    
    /**
     * 设置：Shell代码执行结果
     * 
     * @param i_Result Shell代码执行结果
     */
    public void setResult(String i_Result)
    {
        this.result = i_Result;
    }

    
    /**
     * 获取：Shell代码执行退出状态
     */
    public Integer getExitStatus()
    {
        return exitStatus;
    }

    
    /**
     * 设置：Shell代码执行退出状态
     * 
     * @param i_ExitStatus Shell代码执行退出状态
     */
    public void setExitStatus(Integer i_ExitStatus)
    {
        this.exitStatus = i_ExitStatus;
    }

    
    /**
     * 获取：上传文件信息
     */
    public List<ShellFile> getUpFiles()
    {
        return upFiles;
    }

    
    /**
     * 设置：上传文件信息
     * 
     * @param i_UpFiles 上传文件信息
     */
    public void setUpFiles(List<ShellFile> i_UpFiles)
    {
        this.upFiles = i_UpFiles;
    }

    
    /**
     * 获取：下载文件信息
     */
    public List<ShellFile> getDownFiles()
    {
        return downFiles;
    }

    
    /**
     * 设置：下载文件信息
     * 
     * @param i_DownFiles 下载文件信息
     */
    public void setDownFiles(List<ShellFile> i_DownFiles)
    {
        this.downFiles = i_DownFiles;
    }


    @Override
    public String toString()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        if ( !Help.isNull(this.upFiles) )
        {
            v_Buffer.append("UpFiles: ");
            for (ShellFile v_File : this.upFiles)
            {
                v_Buffer.append(v_File.getDir());
                if ( !v_File.getDir().endsWith("/") )
                {
                    v_Buffer.append("/");
                }
                v_Buffer.append(StringHelp.getFileName(v_File.getFile())).append(" ");
            }
            v_Buffer.append(";");
        }
        
        if ( this.exitStatus != null )
        {
            v_Buffer.append(this.exitStatus).append(":").append(StringHelp.replaceAll(this.result ,new String[]{"\r" ,"\n"} ,StringHelp.$ReplaceSpace)).append(";");
        }
        
        if ( !Help.isNull(this.downFiles) )
        {
            v_Buffer.append("DownFiles: ");
            for (ShellFile v_File : this.downFiles)
            {
                v_Buffer.append(v_File.getDir());
                if ( !v_File.getDir().endsWith("/") )
                {
                    v_Buffer.append("/");
                }
                v_Buffer.append(StringHelp.getFileName(v_File.getFile())).append(" ");
            }
            v_Buffer.append(";");
        }
        
        return v_Buffer.toString();
    }
    
}
