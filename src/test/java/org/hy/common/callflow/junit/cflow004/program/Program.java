package org.hy.common.callflow.junit.cflow004.program;

import org.hy.common.Date;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-25
 * @version     v1.0
 */
public class Program
{
    
    /** 浮点数 */
    private Double doubleValue;
    
    /** 时间数值 */
    private Date   timeValue;
    
    
    
    public Program method_First(Date i_Time ,Double i_DoubleValue)
    {
        System.out.println("call method_First: " + i_DoubleValue + " , " + i_Time.getFull());
        
        Program v_Data = new Program();
        v_Data.setDoubleValue(i_DoubleValue);
        v_Data.setTimeValue(i_Time);
        return v_Data;
    }
    
    
    public void method_Default_Null()
    {
        System.out.println("call method_Default_Null.");
    }
    
    
    public void method_Greater_10()
    {
        System.out.println("call method_Greater_10.");
    }
    
    
    public void method_Less_10()
    {
        System.out.println("call method_Less_10.");
    }

    
    /**
     * 获取：浮点数
     */
    public Double getDoubleValue()
    {
        return doubleValue;
    }

    
    /**
     * 设置：浮点数
     * 
     * @param i_DoubleValue 浮点数
     */
    public void setDoubleValue(Double i_DoubleValue)
    {
        this.doubleValue = i_DoubleValue;
    }

    
    /**
     * 获取：时间数值
     */
    public Date getTimeValue()
    {
        return timeValue;
    }

    
    /**
     * 设置：时间数值
     * 
     * @param i_TimeValue 时间数值
     */
    public void setTimeValue(Date i_TimeValue)
    {
        this.timeValue = i_TimeValue;
    }
    
}
