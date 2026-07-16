package org.hy.common.callflow.common;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.callflow.file.ExportXml;
import org.hy.common.xml.XJavaImport;





/**
 * 编排众包引用
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-05-21
 * @version     v1.0
 */
public class CallFlowImports implements XJavaImport
{
    
    public static final List<Import> $Imports = new ArrayList<Import>();
    
    
    
    static 
    {
        ExportXml.getInstance();
    }
    
    
    
    /**
     * 获取可引用的类信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-05-21
     * @version     v1.0
     *
     * @return
     */
    public List<Import> getImports()
    {
        return $Imports;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-05-21
     * @version     v1.0
     *
     * @return
     */
    public String getComment()
    {
        return "编排众包的引用";
    }
    
}
