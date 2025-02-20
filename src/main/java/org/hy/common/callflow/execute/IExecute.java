package org.hy.common.callflow.execute;

import java.util.Map;

import org.hy.common.callflow.route.RouteConfig;





/**
 * 执行的统一接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public interface IExecute
{
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_IndexNo   本方法要执行的执行序号。下标从1开始
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(int i_IndexNo ,Map<String ,Object> io_Context);
    
    
    
    /**
     * 获取执行之后的路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @return
     */
    public RouteConfig getRoute();
    
}
