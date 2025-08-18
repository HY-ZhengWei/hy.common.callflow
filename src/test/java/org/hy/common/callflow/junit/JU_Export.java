package org.hy.common.callflow.junit;

import org.hy.common.StringHelp;
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
        NodeConfig v_Node1 = (NodeConfig) XJava.getObject("XNode_CF003_1");
        NodeConfig v_Node5 = null;
        System.out.println(v_Node1.toXml(1 ,ExportType.All));
        
        ConditionConfig v_Condition = (ConditionConfig) XJava.getObject("XCondition_CF003_1_1_1");
        System.out.println(v_Condition.toXml(1 ,ExportType.All));
        
        String v_Logic = CallFlow.getHelpExport().export(v_Node1 ,"导出：编排逻辑" ,ExportType.Logic);
        String v_UI    = CallFlow.getHelpExport().export(v_Node1 ,"导出：编排流图" ,ExportType.UI);
        
        System.out.println("\n\n\n" + CallFlow.getHelpExport().export(v_Node1 ,"导出：编排逻辑与流图" ,ExportType.All));
        System.out.println("\n\n\n" + v_Logic);
        System.out.println("\n\n\n" + v_UI);
        
        
        
        // 模拟编排逻辑被修改
        v_Logic = StringHelp.replaceAll(v_Logic ,"五个环节，为假时的走向" ,"第5个环节，为假时的走向路径");
        
        // 先导入编排逻辑，后导入编排流图
        CallFlow.getHelpImport().imports(v_Logic);
        CallFlow.getHelpImport().imports(v_UI);
        
        v_Node1 = (NodeConfig) XJava.getObject("XNode_CF003_1");         // 导入编排逻辑时，需重新获取实例
        v_Node5 = (NodeConfig) XJava.getObject("XNode_CF003_1_1_2及1_1_1_2");
        System.out.println("新的注释说明：" + v_Node5.getComment());

        
        
        // 模拟编排流图被修改
        v_UI = StringHelp.replaceAll(v_UI ,"<x>0.0</x>" ,"<x>3.14</x>");
        CallFlow.getHelpImport().imports(v_UI);                          // 导入流图时，不用重新XJava.getObject()获取实例
        System.out.println("新的坐标值：" + v_Node5.getX());
        System.out.println("\n\n\n" + CallFlow.getHelpExport().export(v_Node1 ,"导出：编排逻辑与流图" ,ExportType.All));
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
