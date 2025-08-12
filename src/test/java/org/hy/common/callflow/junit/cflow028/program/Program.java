package org.hy.common.callflow.junit.cflow028.program;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.TablePartitionRID;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-11
 * @version     v1.0
 */
public class Program
{
    
    public void method_Show(Map<String ,String> i_Tables ,TablePartitionRID<String ,String> i_Datas ,Map<String ,String> i_Row ,AppConfig i_AppConfig)
    {
        Help.print(i_Tables);
        Help.print(i_Datas);
        Help.print(i_Row);
    }
    
}
