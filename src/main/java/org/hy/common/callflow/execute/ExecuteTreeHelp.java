package org.hy.common.callflow.execute;

import java.util.List;

import org.hy.common.Help;





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
     * 计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
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
     * （递归）计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     */
    private void calcTree(IExecute io_ExecObject ,String i_SuperTreeID ,int i_IndexNo)
    {
        String v_TreeID = io_ExecObject.setTreeID(i_SuperTreeID ,i_IndexNo);
        calcTreeToChilds(io_ExecObject ,v_TreeID);
    }
    
    
    
    /**
     * （递归）计算树结构。包括树层级、同级同父序列编号、树ID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param io_ExecObject  执行对象（节点或条件逻辑）
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
                calcTree(v_Child ,i_TreeID ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getFaileds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
                calcTree(v_Child ,i_TreeID ,v_IndexNo++);
            }
        }
        
        v_Childs = io_ExecObject.getRoute().getExceptions();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
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
     * @param i_ExecObject  执行对象（节点或条件逻辑）
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
        
        if ( i_TreeID.equals(i_ExecObject.getTreeIDs()) )
        {
            return i_ExecObject;
        }
        
        List<IExecute> v_Childs  = null;
        
        v_Childs = i_ExecObject.getRoute().getSucceeds();
        if ( !Help.isNull(v_Childs) )
        {
            for (IExecute v_Child : v_Childs)
            {
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
                IExecute v_Ret = findTreeID(v_Child ,i_TreeID);
                if ( v_Ret != null )
                {
                    return v_Ret;
                }
            }
        }
        
        return null;
    }
    
    
    
    private ExecuteTreeHelp()
    {
        // Nothing.
    }
    
}
