package org.hy.common.callflow.common;

import java.util.Collection;





/**
 * 有向图结构的接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-27
 * @version     v1.0
 */
public interface IDirectedGraphID
{
    
    /**
     * 获取：层级树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return  树ID顺序按先后添加次序返回
     */
    public Collection<String> getTreeIDs();
    
    
    
    /**
     * 获取：层级树ID
     * 
     * @param i_SuperTreeID  上级树ID
     */
    public String getTreeID(String i_SuperTreeID);


    
    /**
     * 获取：树层级
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeLevel(String i_TreeID);


    
    /**
     * 获取：树中同层同父的序号编号
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeNo(String i_TreeID);
    
    
    
    /**
     * 获取最大的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMaxTreeID();
    
    
    
    /**
     * 获取最小的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMinTreeID();
    
}
