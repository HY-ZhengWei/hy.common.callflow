package org.hy.common.callflow.junit.cflow049Milvus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.junit.JUBase;
import org.hy.common.callflow.junit.cflow043XCQL.program.Program;
import org.hy.common.callflow.junit.cflow049Milvus.program.BookInfo;
import org.hy.common.callflow.milvus.XMilvusConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.AppInitConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：编排引擎049：Milvus向量元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-24
 * @version     v1.0
 */
@Xjava(value=XType.XML)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_CFlow049 extends AppInitConfig
{
    
    private static boolean $isInit = false;
    
    
    
    @SuppressWarnings("unchecked")
    public JU_CFlow049() throws Exception
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
    public void test_CFlow049()
    {
        this.test_CFlow049_Inner();
    }
    
    
    
    private void test_CFlow049_Inner()
    {
        // 初始化被编排的执行对象方法
        XJava.putObject("XProgram" ,new Program());
        
        // 获取编排中的首个元素
        XMilvusConfig       v_XMilvus = (XMilvusConfig) XJava.getObject("XMilvus_CF049_判断表是否存在");
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        
        List<BookInfo> v_Books = new ArrayList<BookInfo>();
        for (int x=1; x<=10; x++)
        {
            BookInfo v_Book = new BookInfo();
            v_Book.setId(x + 1000);
            v_Book.setBookName("爱丽丝漫游奇境记" + StringHelp.random(10 ,true ,true));
            v_Book.setVectorA(x *  1.0F);
            v_Book.setVectorB(x * -1.0F);
            v_Book.setTitleVector(Help.toList(new Float[] {x * 1.0F ,x * 2.0F ,x * 3.0F ,x * 4.0F}));
            v_Books.add(v_Book);
        }
        
        v_Context.put("ID"          ,1);
        v_Context.put("BookName"    ,"爱丽丝漫游奇境记");
        v_Context.put("VectorA"     ,1.0);
        v_Context.put("VectorB"     ,-1.0);
        v_Context.put("Books"       ,v_Books);
        v_Context.put("BookVector"  ,new Float[] {5.0F ,-5.0F});
        v_Context.put("TitleVector" ,new Float[] {1.0F ,2.0F ,3.0F ,4.0F});
        
        // 执行前的静态检查（关键属性未变时，check方法内部为快速检查）
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(v_XMilvus);
        if ( !v_CheckRet.get() )
        {
            System.out.println(v_CheckRet.getParamStr());  // 打印不合格的原因
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_XMilvus ,v_Context);
        if ( v_Result.isSuccess() )
        {
            System.out.println("Success");
            System.out.println(v_Context.get("RetID"));
            System.out.println(v_Context.get("RetBooks"));
            System.out.println(v_Context.get("RetVectors"));
            System.out.println(v_Context.get("RetVectorsMmultiple"));
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
        System.out.println(CallFlow.getHelpExport().export(v_XMilvus));
        
        JUBase.toJson(v_XMilvus);
    }
    
}
