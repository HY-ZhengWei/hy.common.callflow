package org.hy.common.callflow.junit;

import org.hy.common.callflow.enums.Comparer;
import org.hy.common.callflow.enums.Logical;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.ifelse.ConditionItem;
import org.junit.Test;





/**
 * 测试单元：条件逻辑
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-18
 * @version     v1.0
 */
public class JU_Condition
{
    
    /**
     * 逻辑与
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     * @throws Exception 
     */
    @Test
    public void test_And() throws Exception
    {
        ConditionConfig v_Condition = new ConditionConfig();
        v_Condition.setLogical(Logical.And);
        
        v_Condition.setComment("123 == 123");
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        System.out.println(v_Condition.getSuccessTimeLen()+ "\t" + v_Condition.toString() + " ? " + v_Condition.allow(null));
        
        v_Condition.setComment("123 == 123 AND 123 == 456");
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"456"));
        System.out.println(v_Condition.getSuccessTimeLen()+ "\t" + v_Condition.toString() + " ? " + v_Condition.allow(null));
    }
    
    
    
    /**
     * 逻辑或
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     * @throws Exception 
     */
    @Test
    public void test_Or() throws Exception
    {
        ConditionConfig v_Condition = new ConditionConfig();
        v_Condition.setLogical(Logical.Or);
        
        v_Condition.setComment("123 == 123");
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        System.out.println(v_Condition.getSuccessTimeLen() + "\t" + v_Condition.toString() + " ? " + v_Condition.allow(null));
        
        v_Condition.setComment("123 == 123 OR 123 == 456");
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        v_Condition.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"456"));
        System.out.println(v_Condition.getSuccessTimeLen()+ "\t" + v_Condition.toString() + " ? " + v_Condition.allow(null));
    }
    
    
    
    /**
     * 逻辑与或的组合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     * @throws Exception 
     */
    @Test
    public void test_AndOr() throws Exception
    {
        ConditionConfig v_CChild = new ConditionConfig();
        v_CChild.setLogical(Logical.Or);
        
        // 123 == 123 OR 123 == 456
        v_CChild.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        v_CChild.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"456"));
        
        
        
        ConditionConfig v_CFather = new ConditionConfig();
        v_CFather.setLogical(Logical.And);
        
        // 123 == 123 AND (123 == 123 OR 123 == 456)
        v_CFather.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"123" ,"123"));
        v_CFather.setItem(v_CChild);
        System.out.println(v_CFather.getSuccessTimeLen() + "\t" + v_CFather.toString() + " ? " + v_CFather.allow(null));
        
        
        
        // (123 == 123 And (123 == 123 Or 123 == 456)) And ABC == ABC
        ConditionConfig v_Super = new ConditionConfig();
        v_Super.setLogical(Logical.And);
        v_Super.setItem(v_CFather);
        v_Super.setItem(new ConditionItem(Comparer.Equal ,String.class.getName() ,"ABC" ,"ABC"));
        System.out.println(v_Super.getSuccessTimeLen() + "\t" + v_Super.toString() + " ? " + v_Super.allow(null));
    }
    
}
