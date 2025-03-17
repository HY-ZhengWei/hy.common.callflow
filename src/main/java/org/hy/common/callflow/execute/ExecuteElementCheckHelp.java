package org.hy.common.callflow.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.nesting.NestingConfig;
import org.hy.common.callflow.node.CalculateConfig;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.WaitConfig;
import org.hy.common.callflow.returns.ReturnConfig;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.route.SelfLoop;





/**
 * 编排及元素的检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
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
     * @param i_ExecObject           执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @return  Return.get()         表示检测是否通过。
     *          Return.getParamStr() 检测不合格时，表示不合格原因。
     */
    public Return<Object> check(IExecute i_ExecObject)
    {
        Return<Object> v_Ret = new Return<Object>(true);
        
        if ( i_ExecObject == null )
        {
            return v_Ret.set(false).setParamStr("ExecObject is null.");
        }
        
        Map<String ,Integer> v_XIDs    = new HashMap<String ,Integer>();
        Map<String ,Integer> v_ForXIDs = new HashMap<String ,Integer>();
        
        if ( this.check(v_Ret ,i_ExecObject ,v_XIDs ,v_ForXIDs) )
        {
            if ( !Help.isNull(v_ForXIDs) )
            {
                // 检测所有Fort元素是否都闭环
                for (Map.Entry<String ,Integer> v_Item : v_ForXIDs.entrySet())
                {
                    if ( v_Item.getValue() <= 0 )
                    {
                        return v_Ret.set(false).setParamStr("For.xid[" + v_Item.getKey() + "] refXID count is 0.");
                    }
                }
            }
        }
        
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
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @param io_XIDs       所有元素的XID，及被引用的数量
     * @param io_ForXIDs    所有For循环的XID，及被引用的数量
     * @return              是否检测合格
     */
    private boolean check(Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs)
    {
        if ( !Help.isNull(i_ExecObject.getXJavaID()) )
        {
            if ( io_XIDs.get(i_ExecObject.getXJavaID()) != null )
            {
                io_Result.set(false).setParamStr("XID[" + i_ExecObject.getXJavaID() + "] cannot be repeated.");
                return false;
            }
            
            io_XIDs.put(i_ExecObject.getXJavaID() ,0);
        }
        
        if ( i_ExecObject instanceof ForConfig )
        {
            ForConfig v_For = (ForConfig) i_ExecObject;
            
            // For循环元素必须有XID
            if ( Help.isNull(v_For.getXid()) )
            {
                io_Result.set(false).setParamStr("ForConfig.xid is null.");
                return false;
            }
            
            // For循环元素的结束值不能为空
            if ( Help.isNull(v_For.getEnd()) )
            {
                io_Result.set(false).setParamStr("ForConfig[" + Help.NVL(v_For.getXid()) + "].end is null.");
                return false;
            }
            
            io_ForXIDs.put(i_ExecObject.getXJavaID() ,0);
        }
        else if ( i_ExecObject instanceof NestingConfig )
        {
            NestingConfig v_Nesting = (NestingConfig) i_ExecObject;
            
            // 嵌套元素必须有子编排的XID
            if ( Help.isNull(v_Nesting.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("NestingConfig[" + Help.NVL(v_Nesting.getXid()) + "].callFlowXID is null.");
                return false;
            }
            
            // 嵌套元素的不应自己嵌套自己，递归应采用自引用方式实现
            if ( v_Nesting.getCallFlowXID().equals(i_ExecObject.getXJavaID()) )
            {
                io_Result.set(false).setParamStr("NestingConfig.callFlowXID[" + v_Nesting.getCallFlowXID() + "] cannot nest itself.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof ConditionConfig )
        {
            ConditionConfig v_Condition = (ConditionConfig) i_ExecObject;
            
            // 条件元素的逻辑不能为空
            if ( v_Condition.getLogical() == null )
            {
                io_Result.set(false).setParamStr("ConditionConfig[" + Help.NVL(v_Condition.getXid()) + "].logical is null.");
                return false;
            }
            // 条件元素必须要用至少一个条件项
            if ( Help.isNull(v_Condition.getItems()) )
            {
                io_Result.set(false).setParamStr("ConditionConfig[" + Help.NVL(v_Condition.getXid()) + "] has no condition items.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof CalculateConfig )
        {
            CalculateConfig v_Calculate = (CalculateConfig) i_ExecObject;
            
            // 计算元素的表达式不能为空
            if ( Help.isNull(v_Calculate.getCalc()) )
            {
                io_Result.set(false).setParamStr("CalculateConfig[" + Help.NVL(v_Calculate.getXid()) + "].calc is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof NodeConfig )
        {
            NodeConfig v_Node = (NodeConfig) i_ExecObject;
            
            // 执行元素的执行对象不能为空
            if ( Help.isNull(v_Node.getCallXID()) )
            {
                io_Result.set(false).setParamStr("NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callXID is null.");
                return false;
            }
            // 执行元素的执行方法不能为空
            if ( Help.isNull(v_Node.getCallMethod()) )
            {
                io_Result.set(false).setParamStr("NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callMethod is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof ReturnConfig )
        {
            ReturnConfig v_Return = (ReturnConfig) i_ExecObject;
            
            // 返回元素的返回结果数据不能为空
            if ( Help.isNull(v_Return.getRetValue()) )
            {
                io_Result.set(false).setParamStr("ReturnConfig[" + Help.NVL(v_Return.getXid()) + "].retValue is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof WaitConfig )
        {
            WaitConfig v_Wait = (WaitConfig) i_ExecObject;
            
            // 返回元素的返回结果数据不能为空
            if ( "0".equals(v_Wait.getWaitTime()) )
            {
                io_Result.set(false).setParamStr("WaitConfig[" + Help.NVL(v_Wait.getXid()) + "].waitTime is 0.");
                return false;
            }
        }
        
        if ( check_SelfLoop(i_ExecObject.getRoute().getSucceeds() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs) )
        {
            if ( check_SelfLoop(i_ExecObject.getRoute().getFaileds() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs) )
            {
                return check_SelfLoop(i_ExecObject.getRoute().getExceptions() ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs);
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
     * @param i_ExecObject  执行对象（执行、条件逻辑、等待、计算、循环、嵌套和返回元素）
     * @param io_XIDs       所有元素的XID，及被引用的数量
     * @param io_ForXIDs    所有For循环的XID，及被引用的数量
     * @return              是否检测合格
     */
    private boolean check_SelfLoop(List<RouteItem> i_Childs ,Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs)
    {
        if ( !Help.isNull(i_Childs) )
        {
            for (RouteItem v_RouteItem : i_Childs)
            {
                IExecute v_Child = v_RouteItem.gatNext();
                
                // 路由项的下一个元素不能为空
                if ( v_Child == null )
                {
                    io_Result.set(false).setParamStr("RouteItem.next is null.");
                    return false;
                }
                
                if ( v_Child instanceof SelfLoop )
                {
                    SelfLoop v_SelfLoop = (SelfLoop) v_Child;
                    Integer  v_RefCount = io_XIDs.get(v_SelfLoop.getRefXID());
                    if ( v_RefCount == null )
                    {
                        // 自引用必须是本编排内的
                        // 自引用必须是向上的引用，也不能是向后的引用，向后应采用正常路由
                        io_Result.set(false).setParamStr("SelfLoop.RefXID[" + v_SelfLoop.getRefXID() + "] is not exists.");
                        return false;
                    }
                    else
                    {
                        io_XIDs.put(v_SelfLoop.getRefXID() ,v_RefCount + 1);
                        
                        v_RefCount = io_ForXIDs.get(v_SelfLoop.getRefXID());
                        if ( v_RefCount != null )
                        {
                            io_ForXIDs.put(v_SelfLoop.getRefXID() ,v_RefCount + 1);
                        }
                    }
                    continue;
                }
                // 路由的下一个元素不能是再是自己。如果要递归，应采用自引用
                else if ( v_Child == v_RouteItem.gatOwner().gatOwner() )
                {
                    io_Result.set(false).setParamStr("RouteItem.next cannot be itself[" + Help.NVL(v_RouteItem.gatOwner().gatOwner().getXid()) + "].");
                    return false;
                }
                
                if ( !this.check(io_Result ,v_Child ,io_XIDs ,io_ForXIDs) )
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
