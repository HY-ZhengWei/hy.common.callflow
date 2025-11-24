package org.hy.common.callflow.junit.cflow043XCQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow043XCQL.program.Program;
import org.hy.common.callflow.node.XCQLConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎043：XCQL图谱元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-20
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow043 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    @SuppressWarnings("unchecked")
    public JU_CFlow043() throws Exception
    {
        if ( !$isInit )
        {
            $isInit = true;
            String v_XmlRoot = this.getClass().getResource("").getFile();
            
            this.loadXML("config/startup.Config.xml"                   ,v_XmlRoot);
            this.loadXML((List<Param>)XJava.getObject("StartupConfig") ,v_XmlRoot);
        }
    }
    
    
    
    @Test
    public void test_CFlow043()
    {
        this.test_CFlow043_Inner();
    }
    
    
    
    private void test_CFlow043_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        XCQLConfig          v_XCQL    = (XCQLConfig) XJava.getObject("XCQL_CF043_添加节点_对象参数");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        Program v_DataObject = new Program();
        v_DataObject.setId("111");
        v_DataObject.setDatabaseName("dataCenter");
        v_DataObject.setPort(3306);
        v_DataObject.setCreateTime(new Date());
        
        Map<String ,Object> v_DataMap = new HashMap<String ,Object>();
        v_DataMap.put("id"           ,"333");
        v_DataMap.put("databaseName" ,"dataCenter");
        v_DataMap.put("port"         ,3306);
        v_DataMap.put("createTime"   ,new Date());
        
        List<Program> v_Datas = new ArrayList<Program>();
        for (int x=1; x<=10; x++)
        {
            Program v_Data = new Program();
            v_Data.setId("100" + x);
            v_Data.setDatabaseName("dataCenter_" + v_Data.getId());
            v_Data.setPort(3306 + x);
            v_Data.setCreateTime(new Date());
            
            v_Datas.add(v_Data);
        }
        
        Program v_WheresObject = new Program();
        v_WheresObject.setDatabaseName("dataCenter");
        
        Map<String ,Object> v_WheresMap = new HashMap<String ,Object>();
        v_WheresMap.put("databaseName" ,"dataCenter");
        
        Program v_SetObject = new Program();
        v_SetObject.setDatabaseName("dataCenter");
        v_SetObject.setPort(9999);
        v_SetObject.setUserName("ZhengWei");
        
        Program v_WheresPaging = new Program();
        v_WheresPaging.setPageIndex(1L);
        v_WheresPaging.setPagePerCount(3L);
        
        Program v_DelObject = new Program();
        v_DelObject.setDatabaseName("dataCenter");
        
        Program v_DelWhere = new Program();
        v_DelWhere.setDatabaseName("dataCenter_3");
        
        v_Context.put("DataMap"      ,v_DataMap);
        v_Context.put("DataObject"   ,v_DataObject);
        v_Context.put("DataList"     ,v_Datas);
        v_Context.put("DataList"     ,v_Datas);
        v_Context.put("WheresObject" ,v_WheresObject);
        v_Context.put("WheresMap"    ,v_WheresMap);
        v_Context.put("SetObject"    ,v_SetObject);
        v_Context.put("WheresPaging" ,v_WheresPaging);
        v_Context.put("DelObject"    ,v_DelObject);
        v_Context.put("DelWhere"     ,v_DelWhere);
        
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_XCQL);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_XCQL ,v_Context);
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Error XID = " + v_Result.getExecuteXID());
            v_Result.getException().printStackTrace();
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        System.out.println(CallFlow.getHelpLog().logs(v_FirstResult));
        System.out.println("整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        
        // 导出
        System.out.println(CallFlow.getHelpExport().export(v_XCQL));
        
        JUBase.toJson(v_XCQL);
    }
    
}
