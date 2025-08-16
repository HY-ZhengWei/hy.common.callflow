package org.hy.common.callflow.junit;

import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.junit.cflow003.JU_CFlow003;
import org.hy.common.callflow.junit.cflow004.JU_CFlow004;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.xml.XJava;
import org.junit.Test;





/**
 * 测试单元：导出
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class JU_Export
{
    
    @Test
    public void test_Xml() throws Exception
    {
        new JU_CFlow003();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF003_1");
        System.out.println(v_Node.toXml(1 ,ExportType.All));
        
        ConditionConfig v_Condition = (ConditionConfig) XJava.getObject("XCondition_CF003_1_1_1");
        System.out.println(v_Condition.toXml(1 ,ExportType.All));
        
        System.out.println("\n\n\n" + CallFlow.getHelpExport().export(v_Node ,"导出：编排逻辑与流图" ,ExportType.All));
        System.out.println("\n\n\n" + CallFlow.getHelpExport().export(v_Node ,"导出：编排逻辑"      ,ExportType.Logic));
        System.out.println("\n\n\n" + CallFlow.getHelpExport().export(v_Node ,"导出：编排流图"      ,ExportType.UI));
    }
    
    
    
    @Test
    public void test_ExportXml() throws Exception
    {
        new JU_CFlow004();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF004_1");
        
        System.out.println(CallFlow.getHelpExport().export(v_Node));
    }
    
    
    
    @Test
    public void test_SaveXml() throws Exception
    {
        new JU_CFlow004();
        NodeConfig v_Node = (NodeConfig) XJava.getObject("XNode_CF004_1");
        String v_SaveName = CallFlow.getHelpExport().save(v_Node);
        
        System.out.println(v_SaveName);
    }
    
}
