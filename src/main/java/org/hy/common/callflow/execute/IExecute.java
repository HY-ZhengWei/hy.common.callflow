package org.hy.common.callflow.execute;

import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.XJavaID;
import org.hy.common.callflow.common.IDirectedGraphID;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteConfig;





/**
 * 执行的统一接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public interface IExecute extends IDirectedGraphID ,IToXml ,XJavaID
{
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context);
    
    
    
    /**
     * 获取：执行链：双向链表：前几个
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public List<IExecute> getPrevious();
    
    
    
    /**
     * 获取：执行链：双向链表：其后多个路由
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
     * @return               返回生成的树ID
     */
    public String setTreeID(String i_SuperTreeID ,int i_IndexNo);
    
    
    
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
