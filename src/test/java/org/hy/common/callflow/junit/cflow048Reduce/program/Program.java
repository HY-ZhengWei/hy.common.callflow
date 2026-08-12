package org.hy.common.callflow.junit.cflow048Reduce.program;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.StringHelp;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-11
 * @version     v1.0
 */
public class Program
{
    
    public Map<String ,Object> datasMapA()
    {
        Map<String ,Object> v_Datas = new LinkedHashMap<String ,Object>();
        for (int x=1; x<=3; x++)
        {
            v_Datas.put("MA" + x ,StringHelp.getUUID9n());
        }
        return v_Datas;
    }
    
    
    
    public Map<String ,Object> datasMapB()
    {
        Map<String ,Object> v_Datas = new LinkedHashMap<String ,Object>();
        for (int x=1; x<=5; x++)
        {
            v_Datas.put("MB" + x ,StringHelp.getUUID9n());
        }
        return v_Datas;
    }
    
    
    
    public List<Object> datasListA()
    {
        List<Object> v_Datas = new ArrayList<Object>();
        for (int x=1; x<=3; x++)
        {
            v_Datas.add("LA" + x);
        }
        return v_Datas;
    }
    
    
    
    public List<Object> datasListB()
    {
        List<Object> v_Datas = new ArrayList<Object>();
        for (int x=1; x<=5; x++)
        {
            v_Datas.add("LB" + x);
        }
        return v_Datas;
    }
    
    
    
    public Set<Object> datasSetA()
    {
        Set<Object> v_Datas = new LinkedHashSet<Object>();
        for (int x=1; x<=3; x++)
        {
            v_Datas.add("SA" + x);
        }
        return v_Datas;
    }
    
    
    
    public Set<Object> datasSetB()
    {
        Set<Object> v_Datas = new LinkedHashSet<Object>();
        for (int x=1; x<=5; x++)
        {
            v_Datas.add("SB" + x);
        }
        return v_Datas;
    }
    
    
    
    public Object [] datasArrayA()
    {
        List<Object> v_Datas = new ArrayList<Object>();
        for (int x=1; x<=3; x++)
        {
            v_Datas.add("AA" + x);
        }
        return v_Datas.toArray();
    }
    
    
    
    public Object [] datasArrayB()
    {
        List<Object> v_Datas = new ArrayList<Object>();
        for (int x=1; x<=3; x++)
        {
            v_Datas.add("AB" + x);
        }
        return v_Datas.toArray();
    }
    
    
    
    public Object dataObject()
    {
        return "Object";
    }
    
}
