package org.hy.common.callflow.execute;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.callflow.route.SelfLoop;
import org.hy.common.xml.XJava;





/**
 * 执行对象的树ID的相关操作类（注：有向图结构）
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-27
 * @version     v1.0
 */
public class ExecuteTreeHelp
{
    
    private static ExecuteTreeHelp $Instance = new ExecuteTreeHelp();
    
    
    
    /**
     * 获取单例执行对象的树ID的相关操作类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteTreeHelp getInstance()
    {
        return $Instance;
    }
    
    
    
    /**
     * 从对象池中删除自己及子级的元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     */
    public void removeMySelf(ExecuteElement i_ExecObject)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        List<IExecute> v_Childs = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                removeMySelf((ExecuteElement) v_Child);
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                removeMySelf((ExecuteElement) v_Child);
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                removeMySelf((ExecuteElement) v_Child);
            }
        }
        
        if ( !Help.isNull(i_ExecObject.getXJavaID()) )
        {
            XJava.remove(i_ExecObject.getXJavaID());
        }
    }
    
    
    
    /**
     * 清空树ID及寻址相关的信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     */
    public void clearTree(ExecuteElement io_ExecObject)
    {
        if ( io_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        io_ExecObject.setTreeID(null);
        
        List<IExecute> v_Childs = null;
        
        v_Childs = io_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                clearTree((ExecuteElement) v_Child);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                clearTree((ExecuteElement) v_Child);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                clearTree((ExecuteElement) v_Child);
            }
        }
    }
    
    
    
    /**
     * 计算树ID及寻址相关的信息。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     */
    public void calcTree(IExecute io_ExecObject)
    {
        if ( io_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        io_ExecObject.setTreeID(null ,ExecuteElement.$TreeID.getMinIndexNo());
        calcTreeToChilds(io_ExecObject ,io_ExecObject.getTreeID(""));
    }
    
    
    
    /**
     * （递归）计算树ID及寻址相关的信息。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     */
    private void calcTree(IExecute io_ExecObject ,String i_SuperTreeID ,int i_IndexNo)
    {
        String v_TreeID = io_ExecObject.setTreeID(i_SuperTreeID ,i_IndexNo);
        calcTreeToChilds(io_ExecObject ,v_TreeID);
    }
    
    
    
    /**
     * （递归）计算树ID及寻址相关的信息。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     * @param i_TreeID       执行对象的树ID
     */
    private void calcTreeToChilds(IExecute io_ExecObject ,String i_TreeID)
    {
        int            v_IndexNo = ExecuteElement.$TreeID.getMinIndexNo();
        List<IExecute> v_Childs  = null;
        
        v_Childs = io_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                calcTree(v_Child ,i_TreeID ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                calcTree(v_Child ,i_TreeID ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                calcTree(v_Child ,i_TreeID ,v_IndexNo++);
            }
        }
    }
    
    
    
    /**
     * 用树ID定位某个编排中的元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     * @param i_TreeID      树ID
     * @return 
     */
    public IExecute findTreeID(IExecute i_ExecObject ,String i_TreeID)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        if ( i_ExecObject.getTreeLevel(i_TreeID) != null )
        {
            return i_ExecObject;
        }
        
        List<IExecute> v_Childs  = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        v_Childs = i_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                if ( v_Child instanceof SelfLoop )
                {
                    continue;
                }
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 用执行对象的树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult     执行结果
     * @param i_ExecuteTreeID  执行对象的树ID
     * @return 
     */
    public ExecuteResult findExecuteTreeID(ExecuteResult i_ExecResult ,String i_ExecuteTreeID)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        if ( Help.isNull(i_ExecuteTreeID) )
        {
            throw new NullPointerException("ExecuteTreeID is null.");
        }
        
        ExecuteResult v_FirstResult = getFirstResult(i_ExecResult);
        if ( v_FirstResult == null )
        {
            return null;
        }
        else
        {
            return findExecuteTreeID_Inner(v_FirstResult ,i_ExecuteTreeID);
        }
    }
    
    
    
    /**
     * 用执行对象的树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    private ExecuteResult findExecuteTreeID_Inner(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_TreeID.equals(i_ExecResult.getExecuteTreeID()) )
        {
            return i_ExecResult;
        }
        
        List<ExecuteResult> v_Childs = null;
        
        v_Childs = i_ExecResult.getNexts();
        if ( !Help.isNull(v_Childs) )
        {
            for (ExecuteResult v_Child : v_Childs)
            {
                ExecuteResult v_Ret = findExecuteTreeID_Inner(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 用树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    public ExecuteResult findTreeID(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        ExecuteResult v_FirstResult = getFirstResult(i_ExecResult);
        if ( v_FirstResult == null )
        {
            return null;
        }
        else
        {
            return findTreeID_Inner(v_FirstResult ,i_TreeID);
        }
    }
    
    
    
    /**
     * 用树ID定位某个编排实例执行结果中的结果元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @param i_TreeID      树ID
     * @return 
     */
    private ExecuteResult findTreeID_Inner(ExecuteResult i_ExecResult ,String i_TreeID)
    {
        if ( i_TreeID.equals(i_ExecResult.getTreeID()) )
        {
            return i_ExecResult;
        }
        
        List<ExecuteResult> v_Childs = i_ExecResult.getNexts();
        if ( !Help.isNull(v_Childs) )
        {
            for (ExecuteResult v_Child : v_Childs)
            {
                ExecuteResult v_Ret = findTreeID_Inner(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 定位某个编排实例中的首个元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecObject  执行对象（执行元素、条件逻辑元素、等待元素、计算元素、循环元素、嵌套元素）
     * @return               返回元素一定是入参关系中最顶级的首个。但不一定是TreeLevel和TreeNo都是顶级参数。
     */
    public IExecute findFirst(IExecute i_ExecObject)
    {
        if ( i_ExecObject == null )
        {
            throw new NullPointerException("ExecObject is null.");
        }
        
        if ( Help.isNull(i_ExecObject.getTreeIDs()) )
        {
            throw new NullPointerException("ExecObject's TreeIDs is null.");
        }
        else if ( i_ExecObject.getTreeIDs().size() == 1 )
        {
            String v_TreeID = i_ExecObject.getTreeIDs().iterator().next();
            if ( ExecuteElement.$TreeID.getRootLevel()  == i_ExecObject.getTreeLevel(v_TreeID)
              && ExecuteElement.$TreeID.getMinIndexNo() == i_ExecObject.getTreeNo(   v_TreeID) )
            {
                return i_ExecObject;
            }
        }
        
        List<IExecute> v_PreviousList = i_ExecObject.getPrevious();
        if ( Help.isNull(v_PreviousList) )
        {
            return i_ExecObject;
        }
        else
        {
            for (IExecute v_Previous : v_PreviousList)
            {
                if ( v_Previous instanceof SelfLoop )
                {
                    continue;
                }
                IExecute v_SuperRet = findFirst(v_Previous);
                if ( v_SuperRet != null )
                {
                    return v_SuperRet;
                }
            }
            
            return null;
        }
    }
    
    
    
    /**
     * getFirstResult()方法的别名。
     * 定位某个编排实例执行结果中的首个结果元素
     * 
     * 当有嵌套时，此方法将全量编排看做一个整体，返回其中的首个执行对象。
     * 仅要返回顶层流程编排中的首个执行对象应当用：CallFlow.getHelpExecute().getFirstResult() 方法。
     * 
     * 上面的差异仅在A编排的首个元素嵌套着B编排时才能看出区别。参见 JU_CFlow007
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @return              返回结果一定是入参关系中最顶级的首个。但不一定是TreeLevel和TreeNo都是顶级参数。
     */
    public ExecuteResult findFirst(ExecuteResult i_ExecResult)
    {
        return getFirstResult(i_ExecResult);
    }
    
    
    
    /**
     * 定位某个编排实例执行结果中的首个结果元素
     * 
     * 当有嵌套时，此方法将全量编排看做一个整体，返回其中的首个执行对象。
     * 仅要返回顶层流程编排中的首个执行对象应当用：CallFlow.getHelpExecute().getFirstResult() 方法。
     * 
     * 上面的差异仅在A编排的首个元素嵌套着B编排时才能看出区别。参见 JU_CFlow007
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-26
     * @version     v1.0
     *
     * @param i_ExecResult  执行结果
     * @return              返回结果一定是入参关系中最顶级的首个。但不一定是TreeLevel和TreeNo都是顶级参数。
     */
    public ExecuteResult getFirstResult(ExecuteResult i_ExecResult)
    {
        if ( i_ExecResult == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        
        if ( i_ExecResult.getTreeLevel() == null || i_ExecResult.getTreeNo() == null )
        {
            throw new NullPointerException("ExecuteResult's TreeLevel or TreeNo is null.");
        }
        else if ( ExecuteResult.$TreeID.getRootLevel()  == i_ExecResult.getTreeLevel() 
               && ExecuteResult.$TreeID.getMinIndexNo() == i_ExecResult.getTreeNo() )
        {
            return i_ExecResult;
        }
        
        ExecuteResult v_Previous = i_ExecResult.getPrevious();
        if ( v_Previous == null )
        {
            return i_ExecResult;
        }
        else
        {
            return getFirstResult(v_Previous);
        }
    }
    
    
    
    private ExecuteTreeHelp()
    {
        // Nothing.
    }
    
}
