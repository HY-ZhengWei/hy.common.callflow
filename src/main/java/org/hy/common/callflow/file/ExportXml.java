package org.hy.common.callflow.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.file.FileHelp;
import org.hy.common.license.Hash;
import org.hy.common.license.IHash;





/**
 * 编排配置导出为XML
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class ExportXml
{
    
    private static final ExportXml $Instance = new ExportXml();
    
    /** 文件数字ID的加密算法 */
    public  static final IHash     $Hash = new Hash();
    
    
    
    /**
     * 获取单例编排配置导出为XML
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public static ExportXml getInstance()
    {
        return $Instance;
    }
    
    
    
    /**
     * 保存编排为文件
     * 
     * 注1：同一天保存多次，如果编排配置没有发生改变时，只生成一份保存文件。
     * 注2：当执行对象没有XID时，会自动生成
     * 注3：没有XID时会自动生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     * @return               返回保存文件的全路径
     * @throws IOException 
     */
    public String save(IExecute io_ExecObject) throws IOException
    {
        return save(io_ExecObject ,CallFlow.$SavePath);
    }
    
    
    
    /**
     * 保存编排为文件
     * 
     * 注1：同一天保存多次，如果编排配置没有发生改变时，只生成一份保存文件。
     * 注2：当执行对象没有XID时，会自动生成
     * 注3：没有XID时会自动生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     * @param i_SavePath     保存目录
     * @return               返回保存文件的全路径
     * @throws IOException 
     */
    public String save(IExecute io_ExecObject ,String i_SavePath) throws IOException
    {
        if ( Help.isNull(io_ExecObject.getXJavaID()) )
        {
            throw new NullPointerException("ExecObject's xid is null.");
        }
        
        File v_SavePath = new File(i_SavePath);
        if ( !v_SavePath.exists() )
        {
            throw new IllegalArgumentException("SavePath[" + i_SavePath + "] is not exists.");
        }
        if ( !v_SavePath.isDirectory() )
        {
            throw new IllegalArgumentException("SavePath[" + i_SavePath + "] is not Directory.");
        }
        
        if ( Help.isNull(io_ExecObject.getTreeIDs()) )
        {
            CallFlow.getHelpExecute().calcTree(io_ExecObject);
        }
        
        FileHelp v_FileHelp   = new FileHelp();
        String   v_XmlContent = export(io_ExecObject);
        String   v_Signature  = $Hash.encrypt(v_XmlContent);
        String   v_SaveName   = v_SavePath.getPath() 
                              + Help.getSysPathSeparator()
                              + io_ExecObject.getXJavaID() 
                              + "_"
                              + Date.getNowTime().getYMD_ID()
                              + "_" 
                              + v_Signature
                              + ".xml";
        
        v_FileHelp.setAppend(false);
        v_FileHelp.setOverWrite(true);
        
        v_FileHelp.create(v_SaveName ,v_XmlContent);
        return v_SaveName;
    }
    
    
    
    /**
     * 导出为XML格式
     * 
     * 注1：当没有树ID时，会自动生成
     * 注2：当执行对象没有XID时，会自动生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（节点或条件逻辑）
     * @return
     */
    public String export(IExecute i_ExecObject)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        if ( Help.isNull(i_ExecObject.getTreeIDs()) )
        {
            CallFlow.getHelpExecute().calcTree(i_ExecObject);
        }
        
        String v_Content  = exportToChild(i_ExecObject ,i_ExecObject.getTreeIDs().iterator().next());
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
     * @param i_TreeID      执行对象的树ID
     * @return
     */
    private String exportToChild(IExecute i_ExecObject ,String i_TreeID)
    {
        StringBuilder  v_Xml    = new StringBuilder();
        List<IExecute> v_Childs = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child ,v_Child.getTreeID(i_TreeID)));
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child ,v_Child.getTreeID(i_TreeID)));
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                v_Xml.append(exportToChild(v_Child ,v_Child.getTreeID(i_TreeID)));
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
        
        String v_ExecXml = i_ExecObject.toXml(2 ,i_ExecObject.getTreeSuperID(i_TreeID));
        if ( !Help.isNull(v_ExecXml) )
        {
            v_Xml.append("\n\n").append(v_ExecXml);
        }
        
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
    private String getTemplateXml() 
    {
        TemplateFile Template = new TemplateFile();
        return Template.getTemplateContent("ExportTemplate.xml" ,ExportXml.class.getPackageName());
    }
    
    
    
    private ExportXml()
    {
        // Nothing.
    }
    
}
