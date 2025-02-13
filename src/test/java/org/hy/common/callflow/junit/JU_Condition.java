package org.hy.common.callflow.junit;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.callflow.enums.Comparer;
import org.hy.common.callflow.ifelse.ConditionItem;
import org.junit.Test;





public class JU_Condition
{
    
    @Test
    public void test_ConditionItem()
    {
        Map<String ,Object> v_Default = new HashMap<String ,Object>();
        v_Default.put("VNumber" ,123);
        v_Default.put("V100"    ,100);
        v_Default.put("VNumStr" ,"123");
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.put("C20250213" ,new Date("2025-02-13 00:00:00"));
        v_Context.put("C20250214" ,new Date("2025-02-14 00:00:00"));
        
        String        v_A        = "";
        String        v_B        = "";
        Comparer      v_Comparer = null;
        ConditionItem v_CItem    = null;
        
        v_A        = "123";
        v_B        = "123";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = "123";
        v_B        = "123";
        v_Comparer = Comparer.EqualNot;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = "123";
        v_B        = "456";
        v_Comparer = Comparer.EqualNot;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumber";
        v_B        = ":VNumStr";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumStr";
        v_B        = ":VNumStr";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumber";
        v_B        = ":VNumber";
        v_Comparer = Comparer.Greater;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumber";
        v_B        = ":V100";
        v_Comparer = Comparer.GreaterEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumber";
        v_B        = ":VNumber";
        v_Comparer = Comparer.Less;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":VNumber";
        v_B        = ":V100";
        v_Comparer = Comparer.LessEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
        
        v_A        = ":C20250213";
        v_B        = ":C20250214";
        v_Comparer = Comparer.LessEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_A + " " + v_Comparer.getValue() + " " + v_B + " ? " + v_CItem.allow(v_Default ,v_Context));
    }
    
}
