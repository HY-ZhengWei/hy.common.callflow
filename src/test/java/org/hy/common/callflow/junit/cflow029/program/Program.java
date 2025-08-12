package org.hy.common.callflow.junit.cflow029.program;

import org.hy.common.callflow.junit.cflow028.program.AppConfig;
import org.hy.common.xml.XJSON;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-12
 * @version     v1.0
 */
public class Program
{
    
    public void method_Show(AppConfig i_AppConfig)
    {
        XJSON v_XJson = new XJSON();
        v_XJson.setReturnNVL(false);
        
        System.out.println(XJSON.format(v_XJson.toJson(i_AppConfig).toJSONString()));
    }
    
}
