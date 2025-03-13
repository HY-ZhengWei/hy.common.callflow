package org.hy.common.callflow.junit;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.callflow.enums.Comparer;
import org.hy.common.callflow.ifelse.ConditionItem;
import org.junit.Test;





/**
 * 测试单元：条件项
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-18
 * @version     v1.0
 */
public class JU_ConditionItem
{
    
    @Test
    public void test_ConditionItem() throws Exception
    {
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.put("VNumber" ,123);
        v_Context.put("V100"    ,100);
        v_Context.put("VNumStr" ,"123");
        v_Context.put("S123"    ,(short)123);
        v_Context.put("I123"    ,123);
        v_Context.put("L123"    ,123L);
        v_Context.put("F123"    ,123F);
        v_Context.put("D123"    ,123D);
        v_Context.put("CA"      ,'A');
        v_Context.put("SA"      ,"A");
        v_Context.put("C20250213" ,new Date("2025-02-13 00:00:00"));
        v_Context.put("C20250214" ,new Date("2025-02-14 00:00:00"));
        v_Context.put("CDate"     ,new Date("2025-02-20 00:00:00"));
        v_Context.put("CDateJava" ,new Date("2025-02-20 00:00:00").getDateObject());
        
        String        v_A        = "";
        String        v_B        = "";
        Comparer      v_Comparer = null;
        ConditionItem v_CItem    = null;
        
        v_A        = "123";
        v_B        = "123";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,String.class.getName() ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = "123";
        v_B        = "123";
        v_Comparer = Comparer.EqualNot;
        v_CItem    = new ConditionItem(v_Comparer ,String.class.getName() ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = "123";
        v_B        = "456";
        v_Comparer = Comparer.EqualNot;
        v_CItem    = new ConditionItem(v_Comparer ,String.class.getName() ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":VNumber";
        v_B        = ":VNumStr";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":VNumStr";
        v_B        = ":VNumStr";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":CA";
        v_B        = ":SA";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":I123";
        v_B        = ":S123";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":L123";
        v_B        = ":D123";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":F123";
        v_B        = ":D123";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        Float  v_F = 12F;
        Double v_D = 12D;
        System.out.println(v_F.equals(v_D) + "\n\n");
        
        v_A        = ":VNumber";
        v_B        = ":VNumber";
        v_Comparer = Comparer.Greater;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":VNumber";
        v_B        = ":V100";
        v_Comparer = Comparer.GreaterEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":VNumber";
        v_B        = ":VNumber";
        v_Comparer = Comparer.Less;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":VNumber";
        v_B        = ":V100";
        v_Comparer = Comparer.LessEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":C20250213";
        v_B        = ":C20250214";
        v_Comparer = Comparer.LessEqual;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
        
        v_A        = ":CDate";
        v_B        = ":CDateJava";
        v_Comparer = Comparer.Equal;
        v_CItem    = new ConditionItem(v_Comparer ,v_A ,v_B);
        System.out.println(v_CItem.toString()          + " ? " + v_CItem.allow(v_Context));
        System.out.println(v_CItem.toString(v_Context) + " ? " + v_CItem.allow(v_Context) + "\n\n");
    }
    
}
