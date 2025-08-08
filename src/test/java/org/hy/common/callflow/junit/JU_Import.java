package org.hy.common.callflow.junit;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteElement;
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
        String     v_Xml  = CallFlow.getHelpExport().export(v_Node);
        
        System.out.println(v_Xml);
        
        MD5_V4 v_MD5 = new MD5_V4();
        System.out.println(v_MD5.encrypt(v_Xml));
        
        CallFlow.getHelpImport().imports(v_Xml);
    }
    
    
    
    @Test
    public void test_ImportString()
    {
        String v_Xml = """
<?xml version="1.0" encoding="UTF-8"?>

<config>

    <import name="xconfig"    class="java.util.ArrayList" />
    <import name="xmt"        class="org.hy.common.callflow.nesting.MTConfig" />
    <import name="xnesting"   class="org.hy.common.callflow.nesting.NestingConfig" />
    <import name="xfor"       class="org.hy.common.callflow.forloop.ForConfig" />
    <import name="xnode"      class="org.hy.common.callflow.node.NodeConfig" />
    <import name="xwait"      class="org.hy.common.callflow.node.WaitConfig" />
    <import name="xcalculate" class="org.hy.common.callflow.node.CalculateConfig" />
    <import name="xcondition" class="org.hy.common.callflow.ifelse.ConditionConfig" />
    <import name="xreturn"    class="org.hy.common.callflow.returns.ReturnConfig" />
    <import name="xapi"       class="org.hy.common.callflow.node.APIConfig" />
    <import name="xpublish"   class="org.hy.common.callflow.event.PublishConfig" />
    <import name="xsubscribe" class="org.hy.common.callflow.event.SubscribeConfig" />
    <import name="xwspush"    class="org.hy.common.callflow.event.WSPushConfig" />
    <import name="xsql"       class="org.hy.common.callflow.node.XSQLConfig" />
    <import name="xjob"       class="org.hy.common.callflow.event.JOBConfig" />
    
    
    
    <!-- CFlow编排引擎配置 -->
    <xconfig>
    
        <xreturn id="XCFlow_Thermal_Locator_Control_PublishSucceed">
            <comment>消息发布成功时</comment>
            <retValue>
            {
                "retInt": 200,
                "retText": "控制开度成功"
            }
            </retValue>
        </xreturn>
        
        
        <xreturn id="XCFlow_Thermal_Locator_Control_PublishError">
            <comment>消息发布异常时</comment>
            <retValue>
            {
                "retInt": -501,
                "retText": "控制开度异常"
            }
            </retValue>
        </xreturn>
        
        
        <xpublish id="XCFlow_Thermal_Locator_Control_Publish">
            <comment>暖通项目：控制阀门开度</comment>
            <publishURL>https://www.lpslab.cn</publishURL>
            <publishXID>:PublishXID</publishXID>
            <message>:ValveOpeningCmd</message>
            <format>HEX</format>
            <userID>:UserID</userID>
            <route>
                <succeed>
                    <next ref="XCFlow_Thermal_Locator_Control_PublishSucceed" />
                    <comment>成功时</comment>
                </succeed>
                <error>
                    <next ref="XCFlow_Thermal_Locator_Control_PublishError" />
                    <comment>异常时</comment>
                </error>
            </route>
        </xpublish>
        
        
        <xcalculate id="XCFlow_Thermal_Locator_Control_GroupNo">
            <comment>计算表达式</comment>
            <calc>"XPublish_Thermal_" + :owner + :groupNo</calc>
            <returnID>PublishXID</returnID>
            <route>
                <succeed>
                    <next ref="XCFlow_Thermal_Locator_Control_Publish" />
                    <comment>成功时</comment>
                </succeed>
            </route>
        </xcalculate>
        
        
        <xreturn id="XCFlow_Thermal_Locator_Control_CheckError">
            <comment>消息发布异常时</comment>
            <retValue>
            {
                "retInt": -502,
                "retText": "请求参数不完整或不合法"
            }
            </retValue>
        </xreturn>
        
        
        <xnode id="XCFlow_Thermal_Locator_Control_CalcValveOpening">
            <comment>暖通项目：计算阀门开度</comment>
            <callXID>:ValveOpening</callXID>
            <callMethod>calcValveOpening</callMethod>
            <callParam>
                <valueClass>java.lang.String</valueClass>
                <value>:UserID</value>
            </callParam>
            <callParam>
                <valueClass>java.lang.String</valueClass>
                <value>:owner</value>
            </callParam>
            <callParam>
                <valueClass>java.lang.String</valueClass>
                <value>:groupNo</value>
            </callParam>
            <callParam>
                <valueClass>com.lps.microservice.mqtt.thermal.DirectionType</valueClass>
                <value>:DirectionType</value>
            </callParam>
            <callParam>
                <valueClass>java.lang.Double</valueClass>
                <value>:ValveOpening</value>
            </callParam>
            <returnID>ValveOpeningCmd</returnID>
            <route>
                <succeed>
                    <next ref="XCFlow_Thermal_Locator_Control_GroupNo" />
                    <comment>成功时</comment>
                </succeed>
                <error>
                    <next ref="XCFlow_Thermal_Locator_Control_CheckError" />
                    <comment>异常时</comment>
                </error>
            </route>
        </xnode>
        
    </xconfig>
    
</config>
                       """;
        List<ExecuteElement> v_Ret = CallFlow.getHelpImport().imports(v_Xml);
        Help.print(v_Ret);
        System.out.println(CallFlow.getHelpExport().export(v_Ret.get(5)));
    }
    
}
