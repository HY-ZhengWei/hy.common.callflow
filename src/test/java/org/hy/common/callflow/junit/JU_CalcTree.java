package org.hy.common.callflow.junit;

import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.junit.cflow003.JU_CFlow003;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.junit.Test;





/**
 * 测试单元：计算树结构
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-25
 * @version     v1.0
 */
public class JU_CalcTree
{
    
    @Test
    public void test_calcTree() throws Exception
    {
        new JU_CFlow003();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF003_001");
        CallFlow.calcTree(v_Node);
        System.out.println(v_Node.toXml(1));
        
        NodeConfig v_v_Node2 = (NodeConfig) XJava.getObject("XNode_CF003_005");
        System.out.println(v_v_Node2.toXml(1));
    }
    
}
