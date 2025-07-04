package org.hy.common.callflow.junit.cflow024.program;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-06-18
 * @version     v1.0
 */
public class Program
{
    
    /** 字符类型 */
    private String  valueString;
    
    /** 整数类型 */
    private Integer valueInteger;
    
    /** 复合对象 */
    private Program myself;
    
    
    
    public Program method_Info(Map<String ,Object> i_Context)
    {
        System.out.println(Date.getNowTime().getFullMilli() + " call method_Info.");
        Help.print(i_Context);
        
        Program v_Ret = new Program();
        v_Ret.setValueString("XCFlow_MouldingLine_1_GetState");
        v_Ret.setValueInteger(123456);
        
        v_Ret.setMyself(new Program());
        v_Ret.getMyself().setValueString("XPLC_MouldingLine");
        
        return v_Ret;
    }
    
    
    /**
     * 获取：字符类型
     */
    public String getValueString()
    {
        return valueString;
    }

    
    /**
     * 设置：字符类型
     * 
     * @param i_ValueString 字符类型
     */
    public void setValueString(String i_ValueString)
    {
        this.valueString = i_ValueString;
    }

    
    /**
     * 获取：整数类型
     */
    public Integer getValueInteger()
    {
        return valueInteger;
    }

    
    /**
     * 设置：整数类型
     * 
     * @param i_ValueInteger 整数类型
     */
    public void setValueInteger(Integer i_ValueInteger)
    {
        this.valueInteger = i_ValueInteger;
    }

    
    /**
     * 获取：复合对象
     */
    public Program getMyself()
    {
        return myself;
    }

    
    /**
     * 设置：复合对象
     * 
     * @param i_Myself 复合对象
     */
    public void setMyself(Program i_Myself)
    {
        this.myself = i_Myself;
    }
    
}
