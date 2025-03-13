package org.hy.common.callflow.junit;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.app.Param;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.xml.XJava;
import org.junit.Test;





/**
 * 测试单元：执行方法节点
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-19
 * @version     v1.0
 */
public class JU_Node
{
    
    /**
     * 方法多态测试
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     */
    @Test
    public void test_Node()
    {
        XJava.putObject("N001" ,new JU_Node());
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.put("Param" ,new Param("N1" ,"V1" ,"C1"));
        
        
        NodeConfig v_Node = new NodeConfig();
        v_Node.setReturnID("Ret");
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        ExecuteResult v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam("HY" ,String.class.getName()));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam("HY"  ,String.class.getName()));
        v_Node.setCallParam(new NodeParam("123" ,Integer.class.getName()));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam("HY"  ,String.class.getName()));
        v_Node.setCallParam(new NodeParam("123" ,Integer.class.getName()));
        v_Node.setCallParam(new NodeParam("2025-02-19 22:15:00" ,Date.class.getName()));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam(":Param" ,Param.class.getName()));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        String v_Default = """
                           {
                               "name":    "Def N1",
                               "value":   "Def V1",
                               "comment": "Def C1"
                           }
                           """;
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam(":Param" ,Param.class.getName() ,v_Default));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(null));
        v_Result = v_Node.execute(null ,new HashMap<String ,Object>());
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMethod("m001");
        v_Node.setCallParam(new NodeParam(":Param" ,Param.class.getName()));
        System.out.println("\n\n" + v_Node.toString());
        System.out.println(v_Node.toString(v_Context));
        v_Result = v_Node.execute(null ,v_Context);
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
    }
    
    
    
    public void m001()
    {
        System.out.println("m001 executed.");
    }
    
    public void m001(String i_Str)
    {
        System.out.println("m002 executed. " + i_Str);
    }
    
    public void m001(String i_Str ,Integer i_Int)
    {
        System.out.println("m003 executed. " + i_Str + " ," + i_Int);
    }
    
    public void m001(String i_Str ,Integer i_Int ,Date i_Date)
    {
        System.out.println("m004 executed. " + i_Str + " ," + i_Int + " ," + i_Date.getFullMilli());
    }
    
    public void m001(Param i_Param)
    {
        if ( i_Param == null )
        {
            System.out.println("m005 executed. Param is null.");
        }
        else
        {
            System.out.println("m005 executed. " + i_Param.getName() + " ," + i_Param.getValue() + " ," + i_Param.getComment());
        }
    }
    
}
