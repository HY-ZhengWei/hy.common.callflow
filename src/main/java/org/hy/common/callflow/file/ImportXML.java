package org.hy.common.callflow.file;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 导入XML格式的编排配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-26
 * @version     v1.0
 */
public class ImportXML
{
    
    private static final Logger $Logger = new Logger(ImportXML.class);
    
    
    
    /**
     * 导入XML格式的编排配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_Xml  XML格式的编排配置
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<IExecute> imports(String i_Xml)
    {
        if ( Help.isNull(i_Xml) )
        {
            throw new NullPointerException("Xml is null.");
        }
        
        try
        {
            Object v_Ret = XJava.parserXml(i_Xml ,"CallFlow");
            return (List<IExecute>) v_Ret;
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        return null;
    }
    
    
    
    private ImportXML()
    {
        // Nothing.
    }
    
}
