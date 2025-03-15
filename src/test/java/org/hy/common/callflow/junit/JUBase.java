package org.hy.common.callflow.junit;

import org.hy.common.callflow.CallFlow;
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
     * 打印执行路径
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-06
     * @version     v1.0
     *
     * @param i_Result
     */
    protected void println(ExecuteResult i_Result)
    {
        System.out.println(CallFlow.getHelpLog().logs(i_Result));
    }
    
    
    
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
    protected void toJson(ExecuteElement i_ExecElement)
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
    
}
