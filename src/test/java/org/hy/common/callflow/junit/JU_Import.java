package org.hy.common.callflow.junit;

import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.file.ImportXML;
import org.hy.common.callflow.junit.cflow004.JU_CFlow004;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.license.md5.MD5_V4;
import org.hy.common.xml.XJava;
import org.junit.Test;





/**
 * 测试单元：导入
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-26
 * @version     v1.0
 */
public class JU_Import
{
    
    @Test
    public void test_Import() throws Exception
    {
        new JU_CFlow004();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF004_001");
        String     v_Xml  = CallFlow.getExportHelp().export(v_Node);
        
        System.out.println(v_Xml);
        
        MD5_V4 v_MD5 = new MD5_V4();
        System.out.println(v_MD5.encrypt(v_Xml));
        
        ImportXML.imports(v_Xml);
    }
    
}
