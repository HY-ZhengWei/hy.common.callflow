package org.hy.common.callflow.milvus;





/**
 * XMilvus异常处理机制。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-20
 * @version     v1.0
 */
public interface XMilvusError
{
    
    /**
     * 异常处理
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Error   异常信息
     */
    public void errorLog(XMilvusErrorInfo i_Error);
    
}
