package org.hy.common.callflow.common;





/**
 * 树结构的接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-26
 * @version     v1.0
 */
public interface ITreeID
{
    
    /**
     * 获取：层级树ID
     */
    public String getTreeID();
    
    
    
    /**
     * 获取：树层级
     */
    public Integer getTreeLevel();
    
    
    
    /**
     * 获取：树中同层同父的序号编号
     */
    public Integer getTreeNo();
    
}

