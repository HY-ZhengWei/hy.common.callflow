package org.hy.common.callflow.ftp;

import java.util.List;

import org.hy.common.Help;





/**
 * FTP文传元素的执行结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-10-19
 * @version     v1.0
 */
public class FtpResult
{
    
    /** 上传文件信息 */
    private List<FtpFile> upFiles;
    
    /** 上传成功数量 */
    private int           upSucceed;
    
    /** 下载文件信息 */
    private List<FtpFile> downFiles;
    
    /** 下载成功数量 */
    private int           downSucceed;

    
    
    public FtpResult()
    {
        this.upSucceed   = 0;
        this.downSucceed = 0;
    }
    
    
    
    /**
     * 获取：上传文件信息
     */
    public List<FtpFile> getUpFiles()
    {
        return upFiles;
    }

    
    /**
     * 设置：上传文件信息
     * 
     * @param i_UpFiles 上传文件信息
     */
    public void setUpFiles(List<FtpFile> i_UpFiles)
    {
        this.upFiles = i_UpFiles;
    }

    
    /**
     * 获取：下载文件信息
     */
    public List<FtpFile> getDownFiles()
    {
        return downFiles;
    }

    
    /**
     * 设置：下载文件信息
     * 
     * @param i_DownFiles 下载文件信息
     */
    public void setDownFiles(List<FtpFile> i_DownFiles)
    {
        this.downFiles = i_DownFiles;
    }

    
    /**
     * 获取：上传成功数量
     */
    public int getUpSucceed()
    {
        return upSucceed;
    }

    
    /**
     * 设置：上传成功数量
     * 
     * @param i_UpSucceed 上传成功数量
     */
    public void setUpSucceed(int i_UpSucceed)
    {
        this.upSucceed = i_UpSucceed;
    }

    
    /**
     * 获取：下载成功数量
     */
    public int getDownSucceed()
    {
        return downSucceed;
    }

    
    /**
     * 设置：下载成功数量
     * 
     * @param i_DownSucceed 下载成功数量
     */
    public void setDownSucceed(int i_DownSucceed)
    {
        this.downSucceed = i_DownSucceed;
    }


    @Override
    public String toString()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        if ( !Help.isNull(this.upFiles) )
        {
            v_Buffer.append("UpFiles: ");
            v_Buffer.append(this.upSucceed);
            v_Buffer.append("/");
            v_Buffer.append(this.upFiles.size());
            v_Buffer.append(";");
        }
        
        if ( !Help.isNull(this.downFiles) )
        {
            v_Buffer.append("DownFiles: ");
            v_Buffer.append(this.downSucceed);
            v_Buffer.append("/");
            v_Buffer.append(this.downFiles.size());
            v_Buffer.append(";");
        }
        
        return v_Buffer.toString();
    }
    
}
