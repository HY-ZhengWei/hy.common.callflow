package org.hy.common.callflow.ifelse;

import java.util.Map;





/**
 * 判断条件的统一接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 */
public interface IfElse
{
    
    /**
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default  默认值类型的变量信息
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public boolean allow(Map<String ,Object> i_Default ,Map<String ,Object> i_Context);
    
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Default
     * @param i_Context
     * @return
     */
    public boolean reject(Map<String ,Object> i_Default ,Map<String ,Object> i_Context);
    
}
