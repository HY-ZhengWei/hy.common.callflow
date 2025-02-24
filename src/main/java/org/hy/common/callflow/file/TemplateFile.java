package org.hy.common.callflow.file;

import java.io.IOException;
import java.io.InputStream;

import org.hy.common.file.FileHelp;
import org.hy.common.xml.plugins.analyse.Analyse;





/**
 * 模板工具类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class TemplateFile extends Analyse
{
    
    /**
     * 获取文件内容（还换行符）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_FileName  文件名称(无须文件路径)。此文件应在同级目录中保存
     * @param i_Package   包路径
     * @return
     * @throws Exception
     */
    protected String getFileTemplate(String i_FileName ,String i_Package) throws Exception
    {
        FileHelp    v_FileHelp    = new FileHelp();
        String      v_PackageName = i_Package.replaceAll("\\." ,"/");
        InputStream v_InputStream = this.getClass().getResourceAsStream("/" + v_PackageName + "/" + i_FileName);
        
        try
        {
            return v_FileHelp.getContent(v_InputStream ,"UTF-8" ,true);
        }
        finally
        {
            if ( v_InputStream != null )
            {
                try
                {
                    v_InputStream.close();
                }
                catch (IOException exce)
                {
                    // Nothing.
                }
                
                v_InputStream = null;
            }
        }
    }
    
}
