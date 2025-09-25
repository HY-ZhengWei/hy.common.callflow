package org.hy.common.callflow.language.shell;





/**
 * 用于脚本元素的上传或下载文件
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-25
 * @version     v1.0
 */
public class ShellFile
{
    
    /** 本地文件或远程文件的全路径 */
    private String file;
    
    /** 远程目录或本地目录的全路径 */
    private String dir;

    
    
    public ShellFile(String i_File ,String i_Dir) 
    {
        this.file = i_File;
        this.dir  = i_Dir;
    }
    
    
    /**
     * 获取：本地文件或远程文件的全路径
     */
    public String getFile()
    {
        return file;
    }

    
    /**
     * 获取：远程目录或本地目录的全路径
     */
    public String getDir()
    {
        return dir;
    }
    
}
