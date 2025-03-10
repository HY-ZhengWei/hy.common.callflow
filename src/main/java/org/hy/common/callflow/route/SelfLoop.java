package org.hy.common.callflow.route;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 自引用元素：可实现While循环、for循环的结束点（再次循环点）上
 * 
 * 注：与路由配合，隐性使用
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public class SelfLoop extends ExecuteElement
{
    
    private static final Logger $Logger = new Logger(SelfLoop.class);
    
    
    
    /** 所在编排内其它元素的XID */
    private String refXID;
    
    
    
    public SelfLoop(String i_RefXID)
    {
        super(0L ,0L);
        if ( Help.isNull(i_RefXID) )
        {
            throw new NullPointerException("SelfLoop's refXID is null.");
        }
        
        if ( CallFlow.isSystemXID(i_RefXID) )
        {
            throw new IllegalArgumentException("SelfLoop's refXID[" + i_RefXID + "] is SystemXID.");
        }
        
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.refXID = ValueHelp.standardValueID(i_RefXID);
    }
    
    
    
    /**
     * 执行元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.SelfLoop.getValue();
    }


    
    /**
     * 获取：所在编排内其它元素的XID
     */
    public String getRefXID()
    {
        return DBSQL.$Placeholder + this.refXID;
    }
    
    
    
    /**
     * 获取引用的执行元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
     * @version     v1.0
     *
     * @return
     */
    protected ExecuteElement gatExecuteElement()
    {
        ExecuteElement v_ExecObject = (ExecuteElement) XJava.getObject(this.refXID);
        if ( v_ExecObject == null )
        {
            throw new RuntimeException("SelfLoop.RefXID[" + this.refXID + "] is not find.");
        }
        return v_ExecObject;
    }
    
    
    
    /**
     * 获取：执行链：双向链表：前几个
     * 防止Json无限制循环
     */
    public List<IExecute> gatPrevious()
    {
        return gatExecuteElement().gatPrevious();
    }
    
    
    
    /**
     * 获取：执行链：双向链表：其后多个路由
     */
    public RouteConfig getRoute()
    {
        // 与路由配合，隐性使用，没有其后，防止Json无限制循环
        // 改用 gatRoute() 方法
        return null; 
    }
    
    
    
    /**
     * 获取：执行链：双向链表：其后多个路由
     * 防止Json无限制循环
     */
    public RouteConfig gatRoute()
    {
        return gatExecuteElement().getRoute();
    }
    
    
    
    /**
     * 获取：层级树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return  树ID顺序按先后添加次序返回
     */
    public Collection<String> getTreeIDs()
    {
        return gatExecuteElement().getTreeIDs();
    }
    
    
    
    /**
     * 获取：层级树ID
     * 
     * @param i_SuperTreeID  上级树ID
     */
    public String getTreeID(String i_SuperTreeID)
    {
        ExecuteElement v_ExecObject = gatExecuteElement();
        String         v_TreeID     = v_ExecObject.getTreeID(i_SuperTreeID);
        if ( Help.isNull(v_TreeID) )
        {
            // 定位匹配的树ID
            if ( v_ExecObject.getTreeIDs().size() == 1 )
            {
                v_TreeID = v_ExecObject.getTreeIDs().iterator().next();
            }
            else
            {
                for (String v_TreeIDItem : v_ExecObject.getTreeIDs())
                {
                    if ( i_SuperTreeID.startsWith(v_TreeIDItem) )
                    {
                        v_TreeID = v_TreeIDItem;
                        return v_TreeID;
                    }
                }
                
                v_TreeID = v_ExecObject.getTreeIDs().iterator().next();
            }
        }
        
        return v_TreeID;
    }
    
    
    
    /**
     * 获取：上级树ID
     * 
     * @param i_TreeID  本级树ID
     */
    public String getTreeSuperID(String i_TreeID)
    {
        return gatExecuteElement().getTreeSuperID(i_TreeID);
    }


    
    /**
     * 获取：树层级
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeLevel(String i_TreeID)
    {
        return gatExecuteElement().getTreeLevel(i_TreeID);
    }


    
    /**
     * 获取：树中同层同父的序号编号
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeNo(String i_TreeID)
    {
        return gatExecuteElement().getTreeNo(i_TreeID);
    }
    
    
    
    /**
     * 获取最大的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMaxTreeID()
    {
        return gatExecuteElement().getMaxTreeID();
    }
    
    
    
    /**
     * 获取最小的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMinTreeID()
    {
        return gatExecuteElement().getMinTreeID();
    }

    

    /**
     * 执行
     * 
     * 注：在本类，不做统计、不做计时
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        ExecuteElement v_ExecObject = (ExecuteElement) XJava.getObject(this.refXID);
        if ( v_ExecObject == null )
        {
            ExecuteResult v_Result = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.refXID ,this.toString(io_Context));
            v_Result.setException(new RuntimeException("SelfLoop.RefXID[" + this.refXID + "] is not find."));
            return v_Result;
        }
        
        // 循环元素：当没有下一个循环的元素时，退出循环
        if ( v_ExecObject instanceof ForConfig )
        {
            ForConfig v_For = (ForConfig) v_ExecObject;
            if ( !v_For.hasNext(io_Context) )
            {
                ExecuteResult v_Result = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.refXID ,this.toString(io_Context));
                return v_Result.setResult(false);   // ForConfig 中有设置结果为 true
            }
        }
        
        String v_TreeID      = this.getTreeID(i_SuperTreeID);
        String v_SuperTreeID = this.getTreeSuperID(v_TreeID);
        return v_ExecObject.execute(v_SuperTreeID ,io_Context);
    }

    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    public String toXml(int i_Level ,String i_SuperTreeID)
    {
        // 无须由自己生成，也不允许调用此方法
        throw new RuntimeException("Not allowed to call SelfLoop.toXml().");
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder  v_Builder    = new StringBuilder();
        ExecuteElement v_ExecObject = null;
        
        try
        {
            v_ExecObject = gatExecuteElement();
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        if ( v_ExecObject == null )
        {
            v_Builder.append("SelfLoop ").append(DBSQL.$Placeholder).append(this.refXID);
        }
        else if ( v_ExecObject instanceof ForConfig )
        {
            v_Builder.append("For End ").append(DBSQL.$Placeholder).append(this.refXID);
        }
        else
        {
            v_Builder.append("SelfLoop ").append(DBSQL.$Placeholder).append(this.refXID);
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("SelfLoop ").append(DBSQL.$Placeholder).append(this.refXID);
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 深度克隆编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        // 无须克隆自引用元素，也不允许调用此方法
        throw new RuntimeException("Not allowed to call SelfLoop.clone().");
    }
    
}
