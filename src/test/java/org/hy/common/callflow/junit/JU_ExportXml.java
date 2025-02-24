package org.hy.common.callflow.junit;

import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.junit.cflow003.JU_CFlow003;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.junit.Test;





public class JU_ExportXml
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
    
}
