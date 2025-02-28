package org.hy.common.callflow.file;

import java.io.IOException;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteElement;
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
    
    private static final Logger    $Logger   = new Logger(ImportXML.class);
    
    private static final ImportXML $Instance = new ImportXML();
    
    
    
    /**
     * 获取单例导入XML格式的编排配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @return
     */
    public static ImportXML getInstance()
    {
        return $Instance;
    }
    
    
    
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
    public List<ExecuteElement> imports(String i_Xml)
    {
        if ( Help.isNull(i_Xml) )
        {
            throw new NullPointerException("Xml is null.");
        }
        
        try
        {
            Object v_Ret = XJava.parserXml(i_Xml ,"CallFlow");
            return (List<ExecuteElement>) v_Ret;
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        return null;
    }
    
    
    
    /**
     * 升级编排配置（备份、删除、导入）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）（允许为NULL）
     * @param i_Xml         XML格式的编排配置
     * @return
     * @throws IOException
     */
    public List<ExecuteElement> upgrade(ExecuteElement i_ExecObject ,String i_Xml) throws IOException
    {
        if ( Help.isNull(i_Xml) )
        {
            throw new NullPointerException("Xml is null.");
        }
        
        if ( i_ExecObject != null )
        {
            CallFlow.getHelpExport().save(i_ExecObject);           // 先备份
            CallFlow.getHelpExecute().removeMySelf(i_ExecObject);  // 后删除对象池关系
        }
        
        return imports(i_Xml);                                     // 再导入
    }
    
    
    
    private ImportXML()
    {
        // Nothing.
    }
    
}
