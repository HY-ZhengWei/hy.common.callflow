package org.hy.common.callflow.execute;

import org.hy.common.Date;





/**
 * 执行结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class ExecuteResult
{
    
    /** 执行序号。下标从1开始 */
    private Integer   indexNo;
    
    /** 执行对象的全局惟一标识ID */
    private String    executeXID;
    
    /** 执行结果（方法编排中的最后一个结果数据） */
    private Object    result;
    
    /** 执行结果是否成功 */
    private boolean   success;
    
    /** 为异常对象 */
    private Exception exception;
    
    /** 执行开始时间 */
    private Date      beginTime;
    
    /** 执行结束时间 */
    private Date      endTime;
    
    
    
    public ExecuteResult()
    {
        this(0 ,null);
    }
    
    
    public ExecuteResult(Integer i_IndexNo ,String i_ExecuteXID)
    {
        this.indexNo    = i_IndexNo;
        this.executeXID = i_ExecuteXID;
        this.beginTime  = new Date();
    }
    
    
    /**
     * 获取：执行结果（方法编排中的最后一个结果数据）
     */
    public Object getResult()
    {
        return result;
    }

    
    /**
     * 设置：执行结果（方法编排中的最后一个结果数据）
     * 
     * @param i_Result 执行结果（方法编排中的最后一个结果数据）
     */
    public ExecuteResult setResult(Object i_Result)
    {
        this.endTime = new Date();
        this.success = true;
        this.result  = i_Result;
        
        return this;
    }


    /**
     * 获取：为异常对象
     */
    public Exception getException()
    {
        return exception;
    }

    
    /**
     * 设置：为异常对象
     * 
     * @param i_Exception 为异常对象
     */
    public ExecuteResult setException(Exception i_Exception)
    {
        this.exception = i_Exception;
        this.success   = false;
        this.endTime   = new Date();
        
        return this;
    }

    
    /**
     * 获取：执行序号。下标从1开始
     */
    public Integer getIndexNo()
    {
        return indexNo;
    }

    
    /**
     * 设置：执行序号。下标从1开始
     * 
     * @param i_IndexNo 执行序号。下标从1开始
     */
    public ExecuteResult setIndexNo(Integer i_IndexNo)
    {
        this.indexNo = i_IndexNo;
        return this;
    }

    
    /**
     * 获取：执行对象的全局惟一标识ID
     */
    public String getExecuteXID()
    {
        return executeXID;
    }

    
    /**
     * 设置：执行对象的全局惟一标识ID
     * 
     * @param i_ExecuteXID 执行对象的全局惟一标识ID
     */
    public ExecuteResult setExecuteXID(String i_ExecuteXID)
    {
        this.executeXID = i_ExecuteXID;
        return this;
    }


    /**
     * 获取：执行结果是否成功
     */
    public boolean isSuccess()
    {
        return success;
    }

    
    /**
     * 获取：执行开始时间
     */
    public Date getBeginTime()
    {
        return beginTime;
    }

    
    /**
     * 获取：执行结束时间
     */
    public Date getEndTime()
    {
        return endTime;
    }
    
}
