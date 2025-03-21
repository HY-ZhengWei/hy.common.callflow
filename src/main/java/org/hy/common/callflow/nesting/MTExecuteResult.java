package org.hy.common.callflow.nesting;

import java.util.Map;

import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.timeout.TimeoutConfig;





/**
 * 并发元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-21
 * @version     v1.0
 */
public class MTExecuteResult
{
    
    /** 并发项索引号。下标从0开始 */
    private Integer                      indexNo;
    
    /** 并发项 */
    private MTItem                       mtItem;
    
    /** 并发执行对象 */
    private ExecuteElement               callObject;
    
    /** 并发项的独立上下文 */
    private Map<String ,Object>          context;
    
    /** 执行超时时长（单位：毫秒） */
    private Long                         timeout;
    
    /** 超时元素 */
    private TimeoutConfig<ExecuteResult> timeoutConfig;
    
    /** 并发项的执行结果 */
    private ExecuteResult                result;
    
    
    
    public MTExecuteResult(Integer i_IndexNo ,MTItem i_MTItem ,ExecuteElement i_CallObject ,Long i_Timeout)
    {
        this.indexNo    = i_IndexNo;
        this.mtItem     = i_MTItem;
        this.callObject = i_CallObject;
        this.timeout    = i_Timeout == null ? Long.MAX_VALUE : (i_Timeout <= 0L ? Long.MAX_VALUE : i_Timeout);
    }
    
    
    /**
     * 获取：并发项索引号。下标从0开始
     */
    public Integer getIndexNo()
    {
        return indexNo;
    }

    
    /**
     * 获取：并发项
     */
    protected MTItem getMtItem()
    {
        return mtItem;
    }

    
    /**
     * 获取：并发执行对象
     */
    protected ExecuteElement getCallObject()
    {
        return callObject;
    }
    
    
    /**
     * 获取：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套和返回元素的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return this.mtItem.getCallFlowXID();
    }
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     *
     * @return
     */
    public String getComment()
    {
        return this.mtItem.getComment();
    }
    
    
    /**
     * 获取：执行超时时长（单位：毫秒）
     */
    public Long getTimeout()
    {
        return timeout;
    }

    
    /**
     * 获取：超时元素
     */
    protected TimeoutConfig<ExecuteResult> getTimeoutConfig()
    {
        return timeoutConfig;
    }

    
    /**
     * 设置：超时元素
     * 
     * @param i_TimeoutConfig 超时元素
     */
    protected void setTimeoutConfig(TimeoutConfig<ExecuteResult> i_TimeoutConfig)
    {
        this.timeoutConfig = i_TimeoutConfig;
    }


    /**
     * 获取：并发项的独立上下文
     */
    public Map<String ,Object> getContext()
    {
        return context;
    }

    
    /**
     * 设置：并发项的独立上下文
     * 
     * @param i_Context 并发项的独立上下文
     */
    public void setContext(Map<String ,Object> i_Context)
    {
        this.context = i_Context;
    }

    
    /**
     * 获取：并发项的执行结果
     */
    public ExecuteResult getResult()
    {
        return result;
    }

    
    /**
     * 设置：并发项的执行结果
     * 
     * @param i_Result 并发项的执行结果
     */
    public void setResult(ExecuteResult i_Result)
    {
        this.result = i_Result;
    }
    
}
