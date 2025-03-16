package org.hy.common.callflow.timeout;





/**
 * 超时元素的运行方法
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
 * @param <R>
 */
@FunctionalInterface
public interface TimeoutRunnable<R>
{
    
    /**
     * 运行方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @return  成功时的返回数据
     */
    public abstract R run() throws InterruptedException;
    
}

