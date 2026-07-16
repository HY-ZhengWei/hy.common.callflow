package org.hy.common.callflow.minio;

import java.util.List;

import org.hy.common.Help;





/**
 * Minio存对元素的执行结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-07-14
 * @version     v1.0
 */
public class MinioResult
{
    
    /** 上传文件信息 */
    private List<MinioFile> upFiles;
    
    /** 上传成功数量 */
    private int             upSucceed;
    
    /** 下载文件信息 */
    private List<MinioFile> downFiles;
    
    /** 下载成功数量 */
    private int             downSucceed;
    
    /** 分享文件信息 */
    private List<MinioFile> shareFiles;
    
    /** 分享成功数量 */
    private int             shareSucceed;

    
    
    public MinioResult()
    {
        this.upSucceed    = 0;
        this.downSucceed  = 0;
        this.shareSucceed = 0;
    }
    
    
    
    /**
     * 获取：上传文件信息
     */
    public List<MinioFile> getUpFiles()
    {
        return upFiles;
    }

    
    /**
     * 设置：上传文件信息
     * 
     * @param i_UpFiles 上传文件信息
     */
    public void setUpFiles(List<MinioFile> i_UpFiles)
    {
        this.upFiles = i_UpFiles;
    }

    
    /**
     * 获取：下载文件信息
     */
    public List<MinioFile> getDownFiles()
    {
        return downFiles;
    }

    
    /**
     * 设置：下载文件信息
     * 
     * @param i_DownFiles 下载文件信息
     */
    public void setDownFiles(List<MinioFile> i_DownFiles)
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

    
    /**
     * 获取：分享文件信息
     */
    public List<MinioFile> getShareFiles()
    {
        return shareFiles;
    }

    
    /**
     * 设置：分享文件信息
     * 
     * @param i_ShareFiles 分享文件信息
     */
    public void setShareFiles(List<MinioFile> i_ShareFiles)
    {
        this.shareFiles = i_ShareFiles;
    }


    
    /**
     * 获取：分享成功数量
     */
    public int getShareSucceed()
    {
        return shareSucceed;
    }

    
    /**
     * 设置：分享成功数量
     * 
     * @param i_ShareSucceed 分享成功数量
     */
    public void setShareSucceed(int i_ShareSucceed)
    {
        this.shareSucceed = i_ShareSucceed;
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
        
        if ( !Help.isNull(this.shareFiles) )
        {
            v_Buffer.append("ShareFiles: ");
            v_Buffer.append(this.shareSucceed);
            v_Buffer.append("/");
            v_Buffer.append(this.shareFiles.size());
            v_Buffer.append(";");
        }
        
        return v_Buffer.toString();
    }
    
}
