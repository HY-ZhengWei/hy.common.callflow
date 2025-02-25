package org.hy.common.callflow.file;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.node.NodeConfig;





/**
 * 编排配置导出为XML文件
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class ExportXml
{
    
    /**
     * 导出为XML格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @return
     */
    public static String export(IExecute i_ExecObject)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        String v_Content  = exportToChild(i_ExecObject);
        String v_Template = getTemplateXml();
        
        while ( v_Content.startsWith("\n") )
        {
            v_Content = v_Content.substring(1);
        }
        
        return StringHelp.replaceAll(v_Template ,":Content" ,v_Content);
    }
    
    
    
    /**
     * 递归：导出为XML格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @return
     */
    private static String exportToChild(IExecute i_ExecObject)
    {
        StringBuilder  v_Xml    = new StringBuilder();
        List<IExecute> v_Childs = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child));
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child));
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child));
            }
        }
        
        // 没有XID时，自动生成
        if ( Help.isNull(i_ExecObject.getXJavaID()) )
        {
            if ( i_ExecObject instanceof NodeConfig )
            {
                i_ExecObject.setXJavaID("XNode_" + StringHelp.getUUID9n());
            }
            else if ( i_ExecObject instanceof Condition )
            {
                i_ExecObject.setXJavaID("XCondition_" + StringHelp.getUUID9n());
            }
        }
        
        v_Xml.append("\n\n").append(i_ExecObject.toXml(2));
        
        return v_Xml.toString();
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
    private static String getTemplateXml() 
    {
        TemplateFile Template = new TemplateFile();
        return Template.getTemplateContent("ExportTemplate.xml" ,ExportXml.class.getPackageName());
    }
    
}
