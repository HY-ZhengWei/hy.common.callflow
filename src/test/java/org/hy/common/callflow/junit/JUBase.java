package org.hy.common.callflow.junit;

import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.xml.XJSON;





/**
 * 测试单元的基类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-10
 * @version     v1.0
 */
public class JUBase
{
    
    /**
     * 转Json
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param i_ExecElement
     * @throws Exception
     */
    public static void toJson(ExecuteElement i_ExecElement)
    {
        try
        {
            XJSON v_XJson = new XJSON();
            
            v_XJson.setReturnNVL(false);
            
            //System.out.println(XJSON.format(v_XJson.toJson(i_ExecElement).toJSONString()));
            System.out.println(v_XJson.toJson(i_ExecElement).toJSONString());
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 转Json
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-24
     * @version     v1.0
     *
     * @param i_ExecuteResult
     * @throws Exception
     */
    public static void toJson(ExecuteResult i_ExecuteResult)
    {
        try
        {
            XJSON v_XJson = new XJSON();
            
            v_XJson.setReturnNVL(false);
            
            //System.out.println(XJSON.format(v_XJson.toJson(i_ExecElement).toJSONString()));
            System.out.println(v_XJson.toJson(i_ExecuteResult).toJSONString());
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
}
