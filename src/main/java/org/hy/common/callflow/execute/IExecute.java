package org.hy.common.callflow.execute;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.XJavaID;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteConfig;





/**
 * 执行的统一接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public interface IExecute extends IToXml ,XJavaID
{
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(Map<String ,Object> io_Context);
    
    
    
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
    
    
    
    /**
     * 生成本次树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     */
    public void setTreeID(String i_SuperTreeID ,int i_IndexNo);
    
    
    
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
    
    
    
    /**
     * 获取：子节点的数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @return
     */
    default int getChildSize()
    {
        int v_Size = 0;
        
        if ( !Help.isNull(this.getRoute().getSucceeds()) )
        {
            v_Size += this.getRoute().getSucceeds().size();
        }
        if ( !Help.isNull(this.getRoute().getFaileds()) )
        {
            v_Size += this.getRoute().getFaileds().size();
        }
        if ( !Help.isNull(this.getRoute().getExceptions()) )
        {
            v_Size += this.getRoute().getExceptions().size();
        }
        
        return v_Size;
    }
    
}
