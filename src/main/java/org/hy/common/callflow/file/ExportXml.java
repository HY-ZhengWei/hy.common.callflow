package org.hy.common.callflow.file;

import org.hy.common.callflow.execute.IExecute;





/**
 * 编排配置导出为XML文件
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class ExportXml
{
    
    public static void export(IExecute i_ExecObject)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        
    }
    
    
    
    /**
     * 获取XML文件的模板内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @return
     */
    public static String getTemplateXml() 
    {
        TemplateFile Template = new TemplateFile();
        return Template.getTemplateContent("ExportTemplate.xml" ,ExportXml.class.getPackageName());
    }
    
}
