package org.hy.common.callflow.junit;

import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.file.ExportXml;
import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.junit.cflow003.JU_CFlow003;
import org.hy.common.callflow.junit.cflow004.JU_CFlow004;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.junit.Test;





/**
 * 测试单元：转为XML文件内容
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class JU_ToXml
{
    
    @Test
    public void test_Xml() throws Exception
    {
        new JU_CFlow003();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF003_001");
        System.out.println(v_Node.toXml(1));
        
        Condition v_Condition = (Condition) XJava.getObject("XCondition_CF003_003");
        System.out.println(v_Condition.toXml(1));
    }
    
    
    
    @Test
    public void test_ExportXml() throws Exception
    {
        new JU_CFlow004();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF004_001");
        
        CallFlow.calcTree(v_Node);
        
        System.out.println(ExportXml.export(v_Node));
    }
    
}
