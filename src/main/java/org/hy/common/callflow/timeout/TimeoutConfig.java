package org.hy.common.callflow.timeout;

import java.util.concurrent.TimeoutException;





/**
 * 超时元素
 * 
 * 注：与执行元素和嵌套元素配合，隐性使用
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
 * @param <R>
 */
public class TimeoutConfig<R>
{
    
    /** 等待时长（单位：毫秒） */
    private long               timeout;
                                        
    /** 执行线程 */                     
    private Thread             executeThread;
                                        
    /** 是否为执行方法内部的异常 */
    private Exception          executeException;
                                        
    /** 执行结果 */                     
    private R                  executeResult;
    
    /** 未来要执行的方法 */
    private TimeoutRunnable<R> runnable;
    
    
    
    public TimeoutConfig(long i_Timeout)
    {
        this.timeout = i_Timeout;
    }
    
    
    /**
     * 定义未来要执行的方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param i_Runnable
     * @return
     */
    public TimeoutConfig<R> future(TimeoutRunnable<R> i_Runnable)
    {
        this.runnable = i_Runnable;
        return this;
    }
    
    
    /**
     * 执行的方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @return
     */
    public R execute()
    {
        if ( this.executeAsync() )
        {
            return this.get(); 
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * 异步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @return  是否执行
     */
    public boolean executeAsync()
    {
        if ( this.runnable == null )
        {
            this.executeException = new NullPointerException("TimeoutRunnable is null.");
            this.executeResult    = null;
            return false;
        }
        
        try
        {
            this.executeException = null;
            this.executeResult    = null;
            this.executeThread    = new Thread(() -> 
            {
                try
                {
                    this.executeResult = this.runnable.run();
                }
                catch (InterruptedException exce)
                {
                    this.executeException = new TimeoutException("Timeout " + this.timeout + " millisecond.");
                }
                catch (Exception exce)
                {
                    this.executeException = exce;
                }
            });
            this.executeThread.start();
        }
        catch (Exception exce)
        {
            this.executeException = exce;
        }
        
        return true;
    }
    
    
    /**
     * 获取异步执行的结果（阻塞等待任务完成）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-21
     * @version     v1.0
     *
     * @return
     */
    public R get()
    {
        try
        {
            if ( this.timeout > 0L )
            {
                this.executeThread.join(this.timeout ,0);
            }
            else
            {
                this.executeThread.join();
            }
            
            this.executeThread.interrupt();   // 必须要有它，它十分重要
            Thread.interrupted();
        }
        catch (InterruptedException exce)
        {
            this.executeException = exce;
        }
        
        // 执行成功
        if ( this.executeResult != null )
        {
            this.executeException = null;
            this.executeThread    = null;
            return this.executeResult;
        }
        // 执行对象内部的异常
        else if ( this.executeException != null )
        {
            if ( this.executeException instanceof TimeoutException )
            {
                // Nothing
            }
            else if ( this.executeException.getCause() instanceof TimeoutException )
            {
                this.executeException = (TimeoutException) this.executeException.getCause();
            }
            else if ( this.executeException instanceof InterruptedException )
            {
                this.executeException = new TimeoutException("Timeout " + this.timeout + " millisecond.");
            }
            else if ( this.executeException.getCause() instanceof InterruptedException )
            {
                this.executeException = new TimeoutException("Timeout " + this.timeout + " millisecond.");
            }
            this.executeThread = null;
            return null;
        }
        // 执行超时
        else 
        {
            this.executeException = new TimeoutException("Timeout " + this.timeout + " millisecond.");
            this.executeThread    = null;
            return null;
        }
    }
    
    
    /**
     * 是否发生超时异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @return
     */
    public boolean isTimeout()
    {
        if ( this.executeException == null )
        {
            return false;
        }
        else if ( this.executeException instanceof TimeoutException )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 是否发生异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @return
     */
    public boolean isError()
    {
        return this.executeException != null;
    }
    
    
    /**
     * 获取：是否执行成功
     */
    public boolean isSucceed()
    {
        return this.executeResult != null;
    }

    
    /**
     * 获取：是否为执行方法内部的异常
     */
    public Exception getException()
    {
        return executeException;
    }

    
    /**
     * 获取：执行结果
     */
    public R getResult()
    {
        return executeResult;
    }


    /**
     * 取消执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @return
     */
    public boolean cancel()
    {
        if ( this.executeThread != null )
        {
            this.executeThread.interrupt();
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
