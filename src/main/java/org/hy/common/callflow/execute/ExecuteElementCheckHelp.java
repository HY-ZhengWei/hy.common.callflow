package org.hy.common.callflow.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.route.SelfLoop;
import org.hy.common.xml.XJava;





/**
 * 编排及元素的检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
 *              v2.0  2025-08-27  添加：For循环元素的必须有下一个成功路由
 *                                添加：For元素禁止自已引用自己，即：禁止递归
 *              v3.0  2025-09-23  修正：多路径情况下，首先检测的路径中发现一个不存在自引用，但它其实存在于另一条路径中的问题。发现人：韩万里
 *                                修正：对于自引用元素的这样的弱引用，不应计算引用数量。发现人：韩万里
 *              v4.0  2025-09-26  迁移：静态检查
 */
public class ExecuteElementCheckHelp
{
    
    private static ExecuteElementCheckHelp $Instance = new ExecuteElementCheckHelp();
    
    
    
    /**
     * 获取单例执行结果的日志的相关操作类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteElementCheckHelp getInstance()
    {
        return $Instance;
    }
    
    
    
    /**
     * 编排及元素的检测
     * 
     * 仅做离线环境下的检测。
     * 不做在线环境下的检测。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param i_ExecObject           执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @return  Return.get()         表示检测是否通过。
     *          Return.getParamStr() 检测不合格时，表示不合格原因。
     */
    public Return<Object> check(IExecute i_ExecObject)
    {
        Return<Object> v_Ret = new Return<Object>(true);
        
        if ( i_ExecObject == null )
        {
            return v_Ret.set(false).setParamStr("CFlowCheck：ExecObject is null.");
        }
        
        Map<String ,Integer> v_XIDs     = new HashMap<String ,Integer>();
        Map<String ,Integer> v_ForXIDs  = new HashMap<String ,Integer>();
        Map<String ,String>  v_RefNulls = new HashMap<String ,String>();  // Map.key为引对象的XID、一个空格和所有者的XID，Map.value为所有者的XID
        
        if ( this.check(v_Ret ,i_ExecObject ,v_XIDs ,v_ForXIDs ,v_RefNulls) )
        {
            if ( !Help.isNull(v_ForXIDs) )
            {
                // 检测所有Fort元素是否都闭环
                for (Map.Entry<String ,Integer> v_Item : v_ForXIDs.entrySet())
                {
                    if ( v_Item.getValue() <= 0 )
                    {
                        // 没有被其它元素引用
                        v_Ret.set(false).setParamStr("CFlowCheck：For.xid[" + v_Item.getKey() + "] refXID count is 0 , Not referenced by other Elements.");
                        break;
                    }
                }
            }
        }
        
        // 在遍历完成后，再检查 Add 2025-09-23
        // 自引用必须是本编排内的
        // 自引用必须是向上的引用，也不能是向后的引用，向后应采用正常路由
        if ( v_Ret.get() && !Help.isNull(v_RefNulls) )
        {
            for (Map.Entry<String ,String> v_Item : v_RefNulls.entrySet())
            {
                String  v_RefXID      = v_Item.getKey().split(" ")[0];
                String  v_RefOwnerXID = v_Item.getValue();
                Integer v_RefCount    = v_XIDs.get(v_RefXID);
                
                if ( v_RefCount == null )
                {
                    v_Ret.set(false).setParamStr("CFlowCheck：XID[" + v_RefOwnerXID + "]'s SelfLoop.RefXID[" + v_RefXID + "] is not exists or not valid.");
                    break;
                }
                
                ExecuteElement v_Ref      = (ExecuteElement) XJava.getObject(v_RefXID);
                ExecuteElement v_RefOwner = (ExecuteElement) XJava.getObject(v_RefOwnerXID);
                boolean        v_IsOK     = false;
                
                for (String v_RefTreeID : v_Ref.getTreeIDs())
                {
                    for (String v_OwnerTreeID : v_RefOwner.getTreeIDs())
                    {
                        if ( v_OwnerTreeID.startsWith(v_RefTreeID) )
                        {
                            v_IsOK = true;
                            break;
                        }
                    }
                    
                    if ( v_IsOK )
                    {
                        break;
                    }
                }
                
                // 自引用必须是向上的引用，也不能是向后的引用，向后应采用正常路由
                if ( !v_IsOK )
                {
                    v_Ret.set(false).setParamStr("CFlowCheck：XID[" + v_RefOwnerXID + "]'s SelfLoop.RefXID[" + v_RefXID + "], it must be a forward reference, not a backward reference.");
                    break;
                }
            }
        }
        
        if ( v_Ret.get() )
        {
            // 仅标记首个元素，预防从编排中间的某个元素开始执行
            ((ExecuteElement) i_ExecObject).checkOK();
        }
        
        v_XIDs    .clear();
        v_ForXIDs .clear();
        v_RefNulls.clear();
        
        return v_Ret;
    }
    
    
    
    /**
     * 编排及元素的检测
     * 
     * 仅做离线环境下的检测。
     * 不做在线环境下的检测。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_XIDs       所有元素的XID，及被引用的数量
     * @param io_ForXIDs    所有For循环的XID，及被引用的数量
     * @param io_RefNulls   所有引用找不到的XID
     * @return              是否检测合格
     */
    private boolean check(Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs ,Map<String ,String> io_RefNulls)
    {
        if ( !Help.isNull(i_ExecObject.getXJavaID()) )
        {
            Integer v_RefCount = io_XIDs.get(i_ExecObject.getXJavaID());
            if ( v_RefCount != null )
            {
                if ( i_ExecObject.gatPrevious() != null && v_RefCount < i_ExecObject.gatPrevious().size() - 1 )
                {
                    // 已经不是第一次检测了，直接跳出
                    io_XIDs.put(i_ExecObject.getXJavaID() ,v_RefCount + 1);
                    return true;
                }
                else
                {
                    io_Result.set(false).setParamStr("CFlowCheck：XID[" + i_ExecObject.getXJavaID() + "] cannot be repeated.");
                    return false;
                }
            }
            else
            {
                io_XIDs.put(i_ExecObject.getXJavaID() ,0);
            }
        }
        
        if ( i_ExecObject instanceof ForConfig )
        {
            if ( i_ExecObject.check(io_Result) )
            {
                io_ForXIDs.put(i_ExecObject.getXJavaID() ,0);
            }
            else
            {
                return false;
            }
        }
        else if ( !i_ExecObject.check(io_Result) )
        {
            return false;
        }
        
        if ( check_SelfLoop(i_ExecObject.getRoute().getSucceeds() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs ,io_RefNulls) )
        {
            if ( check_SelfLoop(i_ExecObject.getRoute().getFaileds() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs ,io_RefNulls) )
            {
                return check_SelfLoop(i_ExecObject.getRoute().getExceptions() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs ,io_RefNulls);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * （公共方法的递归）编排及元素的检测
     * 
     * 仅做离线环境下的检测。
     * 不做在线环境下的检测。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     * @param i_Childs      子级路由
     * @param io_Result     表示检测结果
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_XIDs       所有元素的XID，及被引用的数量
     * @param io_ForXIDs    所有For循环的XID，及被引用的数量
     * @param io_RefNulls   所有引用找不到的XID
     * @return              是否检测合格
     */
    private boolean check_SelfLoop(List<RouteItem> i_Childs ,Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs ,Map<String ,String> io_RefNulls)
    {
        if ( !Help.isNull(i_Childs) )
        {
            for (RouteItem v_RouteItem : i_Childs)
            {
                IExecute v_Child = v_RouteItem.gatNext();
                
                // 路由项的下一个元素不能为空
                if ( v_Child == null )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：RouteItem.next is null.");
                    return false;
                }
                
                if ( v_Child instanceof SelfLoop )
                {
                    SelfLoop       v_SelfLoop = (SelfLoop) v_Child;
                    ExecuteElement v_Owner    = v_SelfLoop.gatOwner().gatOwner().gatOwner();  // 自引用元素归属的元素
                    String         v_RefXID   = v_SelfLoop.gatRefXID();
                    Integer        v_RefCount = io_XIDs.get(v_RefXID);
                    
                    if ( v_Owner instanceof ForConfig )
                    {
                        if ( v_Owner.getXid().equals(v_SelfLoop.getRefXID()) )
                        {
                            // For元素禁止自已引用自己，即：禁止递归
                            io_Result.set(false).setParamStr("CFlowCheck：SelfLoop.RefXID[" + v_SelfLoop.getRefXID() + "] ref ForConfig[" + v_Owner.getXid() + "] myself.");
                            return false;
                        }
                    }
                    
                    if ( v_RefCount == null )
                    {
                        // 自引用必须是本编排内的
                        // 自引用必须是向上的引用，也不能是向后的引用，向后应采用正常路由
                        // 这里先记录下，最后再核算
                        io_RefNulls.put(v_RefXID + " " + v_Owner.getXJavaID() ,v_Owner.getXJavaID());
                    }
                    else
                    {
                        // 对于自引用元素的这样的弱引用，不应计算引用数量 
                        // io_XIDs.put(v_RefXID ,v_RefCount + 1);   Del 2025-09-23
                        
                        v_RefCount = io_ForXIDs.get(v_RefXID);
                        if ( v_RefCount != null )
                        {
                            io_ForXIDs.put(v_RefXID ,v_RefCount + 1);
                        }
                    }
                    continue;
                }
                // 路由的下一个元素不能是再是自己。如果要递归，应采用自引用
                else if ( v_Child == v_RouteItem.gatOwner().gatOwner() )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：RouteItem.next cannot be itself[" + Help.NVL(v_RouteItem.gatOwner().gatOwner().getXid()) + "].");
                    return false;
                }
                
                if ( !this.check(io_Result ,v_Child ,io_XIDs ,io_ForXIDs ,io_RefNulls) )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    
    private ExecuteElementCheckHelp()
    {
        // Nothing.
    }
    
}
