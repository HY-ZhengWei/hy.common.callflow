package org.hy.common.callflow.junit;

import org.hy.common.Date;
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
    
    @Test
    public void test_Node()
    {
        XJava.putObject("N001" ,new JU_Node());
        
        NodeConfig v_Node = new NodeConfig();
        v_Node.setCallXID("N001");
        v_Node.setCallMehod("m001");
        ExecuteResult v_Result = v_Node.execute(1 ,null ,null);
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.setCallXID("N001");
        v_Node.setCallMehod("m002");
        v_Node.setCallParam(new NodeParam("HY" ,String.class));
        v_Result = v_Node.execute(1 ,null ,null);
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMehod("m003");
        v_Node.setCallParam(new NodeParam("HY"  ,String.class));
        v_Node.setCallParam(new NodeParam("123" ,Integer.class));
        v_Result = v_Node.execute(1 ,null ,null);
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
        
        
        v_Node.getCallParams().clear();
        v_Node.setCallXID("N001");
        v_Node.setCallMehod("m004");
        v_Node.setCallParam(new NodeParam("HY"  ,String.class));
        v_Node.setCallParam(new NodeParam("123" ,Integer.class));
        v_Node.setCallParam(new NodeParam("2025-02-19 22:15:00" ,Date.class));
        v_Result = v_Node.execute(1 ,null ,null);
        if ( !v_Result.isSuccess() )
        {
            v_Result.getException().printStackTrace();
        }
    }
    
    
    
    public void m001()
    {
        System.out.println("m001 executed.");
    }
    
    public void m002(String i_Str)
    {
        System.out.println("m002 executed. " + i_Str);
    }
    
    public void m003(String i_Str ,Integer i_Int)
    {
        System.out.println("m003 executed. " + i_Str + " ," + i_Int);
    }
    
    public void m004(String i_Str ,Integer i_Int ,Date i_Date)
    {
        System.out.println("m004 executed. " + i_Str + " ," + i_Int + " ," + i_Date.getFullMilli());
    }
    
}
