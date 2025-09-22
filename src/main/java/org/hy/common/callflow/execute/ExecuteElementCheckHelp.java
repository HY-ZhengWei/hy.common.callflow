package org.hy.common.callflow.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.cache.CacheGetConfig;
import org.hy.common.callflow.cache.CacheSetConfig;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.JobIntervalType;
import org.hy.common.callflow.event.JOBConfig;
import org.hy.common.callflow.event.PublishConfig;
import org.hy.common.callflow.event.SubscribeConfig;
import org.hy.common.callflow.event.WSPullConfig;
import org.hy.common.callflow.event.WSPushConfig;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.ifelse.ConditionItem;
import org.hy.common.callflow.ifelse.IfElse;
import org.hy.common.callflow.nesting.MTConfig;
import org.hy.common.callflow.nesting.MTItem;
import org.hy.common.callflow.nesting.NestingConfig;
import org.hy.common.callflow.node.APIConfig;
import org.hy.common.callflow.node.CalculateConfig;
import org.hy.common.callflow.node.CommandConfig;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.callflow.node.UnzipConfig;
import org.hy.common.callflow.node.WaitConfig;
import org.hy.common.callflow.node.XSQLConfig;
import org.hy.common.callflow.node.ZipConfig;
import org.hy.common.callflow.python.PythonConfig;
import org.hy.common.callflow.returns.ReturnConfig;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.callflow.route.SelfLoop;
import org.hy.common.callflow.safe.DecryptFileConfig;
import org.hy.common.callflow.safe.EncryptFileConfig;
import org.hy.common.db.DBSQL;





/**
 * 编排及元素的检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
 *              v2.0  2025-08-27  添加：For循环元素的必须有下一个成功路由
 *                                添加：For元素禁止自已引用自己，即：禁止递归
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
                        v_Ret.set(false).setParamStr("CFlowCheck：For.xid[" + v_Item.getKey() + "] refXID count is 0.");
                        break;
                    }
                }
            }
        }
        
        if ( v_Ret.get() )
        {
            // 仅标记首个元素，预防从编排中间的某个元素开始执行
            ((ExecuteElement) i_ExecObject).checkOK();
        }
        
        v_XIDs   .clear();
        v_ForXIDs.clear();
        
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
     * @return              是否检测合格
     */
    private boolean check(Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs)
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
            ForConfig v_For = (ForConfig) i_ExecObject;
            
            // For循环元素必须有XID
            if ( Help.isNull(v_For.getXid()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ForConfig.xid is null.");
                return false;
            }
            
            // For循环元素的结束值不能为空
            if ( Help.isNull(v_For.getEnd()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ForConfig[" + Help.NVL(v_For.getXid()) + "].end is null.");
                return false;
            }
            
            // For循环元素的必须有下一个成功路由
            if ( Help.isNull(v_For.getRoute().getSucceeds()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ForConfig[" + Help.NVL(v_For.getXid()) + "].Succeed route is null.");
                return false;
            }
            
            io_ForXIDs.put(i_ExecObject.getXJavaID() ,0);
        }
        else if ( i_ExecObject instanceof MTConfig )
        {
            MTConfig v_MT = (MTConfig) i_ExecObject;
            
            if ( Help.isNull(v_MT.getMtitems()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].Mtitems is null.");
                return false;
            }
            
            int x = 0;
            for (MTItem v_MTItem : v_MT.getMtitems())
            {
                // 并发元素必须有子编排的XID
                if ( Help.isNull(v_MTItem.getCallFlowXID()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].callFlowXID is null.");
                    return false;
                }
                
                // 并发元素的不应自己并发自己，递归应采用自引用方式实现
                if ( v_MTItem.getCallFlowXID().equals(v_MT.getXJavaID()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].callFlowXID[" + v_MTItem.getCallFlowXID() + "] cannot nest itself.");
                    return false;
                }
                
                if ( !Help.isNull(v_MTItem.getValueXIDA()) )
                {
                    // 当有比较值A时，比较器不应为空
                    if ( v_MTItem.getComparer() == null )
                    {
                        io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].comparer is null.");
                        return false;
                    }
                    
                    if ( !ValueHelp.isRefID(v_MTItem.getValueXIDA()) )
                    {
                        if ( Help.isNull(v_MTItem.getValueClass()) )
                        {
                            // 条件项的比值为数值类型时，其类型应不会空
                            io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].valueXIDA is Normal type ,but valueClass is null.");
                            return false;
                        }
                    }
                }
                
                if ( !Help.isNull(v_MTItem.getValueXIDB()) )
                {
                    // 当有比较值B时，比较值A不应为空
                    if ( Help.isNull(v_MTItem.getValueXIDA()) )
                    {
                        io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].valueXIDA is null.");
                        return false;
                    }
                    
                    // 当有比较值B时，比较器不应为空
                    if ( v_MTItem.getComparer() == null )
                    {
                        io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].comparer is null.");
                        return false;
                    }
                    
                    if ( !ValueHelp.isRefID(v_MTItem.getValueXIDB()) )
                    {
                        if ( Help.isNull(v_MTItem.getValueClass()) )
                        {
                            // 条件项的比值为数值类型时，其类型应不会空
                            io_Result.set(false).setParamStr("CFlowCheck：MTConfig[" + Help.NVL(v_MT.getXid()) + "].[" + x + "].valueXIDB is Normal type ,but valueClass is null.");
                            return false;
                        }
                    }
                }
                
                x++;
            }
        }
        else if ( i_ExecObject instanceof NestingConfig )
        {
            NestingConfig v_Nesting = (NestingConfig) i_ExecObject;
            
            // 嵌套元素必须有子编排的XID
            if ( Help.isNull(v_Nesting.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：NestingConfig[" + Help.NVL(v_Nesting.getXid()) + "].callFlowXID is null.");
                return false;
            }
            
            // 嵌套元素的不应自己嵌套自己，递归应采用自引用方式实现
            if ( v_Nesting.getCallFlowXID().equals(i_ExecObject.getXJavaID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：NestingConfig.callFlowXID[" + v_Nesting.getCallFlowXID() + "] cannot nest itself.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof CalculateConfig )
        {
            CalculateConfig v_Calculate = (CalculateConfig) i_ExecObject;
            
            // 计算元素的表达式不能为空
            if ( Help.isNull(v_Calculate.getCalc()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：CalculateConfig[" + Help.NVL(v_Calculate.getXid()) + "].calc is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof WaitConfig )
        {
            WaitConfig v_Wait = (WaitConfig) i_ExecObject;
            
            // 返回元素的返回结果数据不能为空
            if ( "0".equals(v_Wait.getWaitTime()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：WaitConfig[" + Help.NVL(v_Wait.getXid()) + "].waitTime is 0.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof ReturnConfig )
        {
            ReturnConfig v_Return = (ReturnConfig) i_ExecObject;
            
            // 返回元素的返回结果数据不能为空
            if ( Help.isNull(v_Return.getRetValue()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ReturnConfig[" + Help.NVL(v_Return.getXid()) + "].retValue is null.");
                return false;
            }
            
            if ( !ValueHelp.isRefID(v_Return.getRetValue()) )
            {
                if ( Help.isNull(v_Return.getRetClass()) )
                {
                    // 返回结果为数值类型时，及类型应不会空
                    io_Result.set(false).setParamStr("CFlowCheck：ReturnConfig[" + Help.NVL(v_Return.getXid()) + "] retValue is Normal type ,but retClass is null.");
                    return false;
                }
            }
            
            if ( !Help.isNull(v_Return.getRetDefault()) )
            {
                if ( !ValueHelp.isRefID(v_Return.getRetDefault()) )
                {
                    if ( Help.isNull(v_Return.getRetClass()) )
                    {
                        // 返回结果的默认值为数值类型时，及类型应不会空
                        io_Result.set(false).setParamStr("CFlowCheck：ReturnConfig[" + Help.NVL(v_Return.getXid()) + "] retDefault is Normal type ,but retClass is null.");
                        return false;
                    }
                }
            }
        }
        else if ( i_ExecObject instanceof ConditionConfig )
        {
            ConditionConfig v_Condition = (ConditionConfig) i_ExecObject;
            
            // 条件元素的逻辑不能为空
            if ( v_Condition.getLogical() == null )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(v_Condition.getXid()) + "].logical is null.");
                return false;
            }
            // 条件元素必须要用至少一个条件项
            if ( Help.isNull(v_Condition.getItems()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(v_Condition.getXid()) + "] has no condition items.");
                return false;
            }
            
            this.check_Condition(v_Condition ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs);
        }
        else if ( i_ExecObject instanceof CacheGetConfig )
        {
            CacheGetConfig v_CacheGet = (CacheGetConfig) i_ExecObject;
            
            // 表不空，库不能为空
            if ( !Help.isNull(v_CacheGet.getTable()) )
            {
                if ( Help.isNull(v_CacheGet.getDataBase()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：CacheGetConfig[" + Help.NVL(v_CacheGet.getDataBase()) + "].database is null ,but table is not null.");
                    return false;
                }
            }
            // 主键不空，库不空，表不能为空
            if ( !Help.isNull(v_CacheGet.getPkID()) )
            {
                if ( !Help.isNull(v_CacheGet.getDataBase()) && Help.isNull(v_CacheGet.getTable()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：CacheGetConfig[" + Help.NVL(v_CacheGet.getTable()) + "].table is null ,but database and pkID are not null.");
                    return false;
                }
            }
        }
        else if ( i_ExecObject instanceof CacheSetConfig )
        {
            CacheSetConfig v_CacheSet = (CacheSetConfig) i_ExecObject;
            
            // 表不空，库不能为空
            if ( !Help.isNull(v_CacheSet.getTable()) )
            {
                if ( Help.isNull(v_CacheSet.getDataBase()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：CacheSetConfig[" + Help.NVL(v_CacheSet.getDataBase()) + "].database is null ,but table is not null.");
                    return false;
                }
            }
            // 主键不空，库不空，表不能为空
            if ( !Help.isNull(v_CacheSet.getPkID()) )
            {
                if ( !Help.isNull(v_CacheSet.getDataBase()) && Help.isNull(v_CacheSet.getTable()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：CacheSetConfig[" + Help.NVL(v_CacheSet.getTable()) + "].table is null ,but database and pkID are not null.");
                    return false;
                }
            }
        }
        else if ( i_ExecObject instanceof WSPushConfig )
        {
            WSPushConfig v_WSPush = (WSPushConfig) i_ExecObject;
            
            if ( Help.isNull(v_WSPush.getName()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：WSPushConfig[" + Help.NVL(v_WSPush.getXid()) + "].name is null.");
                return false;
            }
            if ( Help.isNull(v_WSPush.getNewMessage()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：WSPushConfig[" + Help.NVL(v_WSPush.getXid()) + "].newMessage is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof WSPullConfig )
        {
            WSPullConfig v_WSPull = (WSPullConfig) i_ExecObject;
            
            if ( Help.isNull(v_WSPull.getWsURL()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：WSPullConfig[" + Help.NVL(v_WSPull.getXid()) + "].wsURL is null.");
                return false;
            }
            if ( Help.isNull(v_WSPull.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：WSPullConfig[" + Help.NVL(v_WSPull.getXid()) + "].callFlowXID is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof PublishConfig )
        {
            PublishConfig v_Publish = (PublishConfig) i_ExecObject;
            
            if ( Help.isNull(v_Publish.getPublishXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：PublishConfig[" + Help.NVL(v_Publish.getXid()) + "].publishXID is null.");
                return false;
            }
            if ( Help.isNull(v_Publish.getMessage()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：PublishConfig[" + Help.NVL(v_Publish.getXid()) + "].message is null.");
                return false;
            }
            if ( Help.isNull(v_Publish.getUserID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：PublishConfig[" + Help.NVL(v_Publish.getXid()) + "].userID is null.");
                return false;
            }
            if ( !Help.isNull(v_Publish.getQoS()) )
            {
                if ( v_Publish.getQoS() < 0 || v_Publish.getQoS() > 2 )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：PublishConfig[" + Help.NVL(v_Publish.getXid()) + "].qoS is invalid.");
                    return false;
                }
            }
        }
        else if ( i_ExecObject instanceof SubscribeConfig )
        {
            SubscribeConfig v_Subscribe = (SubscribeConfig) i_ExecObject;
            
            if ( Help.isNull(v_Subscribe.getSubscribeXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(v_Subscribe.getXid()) + "].subscribeXID is null.");
                return false;
            }
            if ( Help.isNull(v_Subscribe.getUserID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(v_Subscribe.getXid()) + "].userID is null.");
                return false;
            }
            if ( Help.isNull(v_Subscribe.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：SubscribeConfig[" + Help.NVL(v_Subscribe.getXid()) + "].callFlowXID is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof APIConfig )
        {
            APIConfig v_API = (APIConfig) i_ExecObject;
            
            if ( Help.isNull(v_API.getUrl()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：APIConfig[" + Help.NVL(v_API.getXid()) + "].url is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof XSQLConfig )
        {
            XSQLConfig v_API = (XSQLConfig) i_ExecObject;
            
            // 执行对象不能为空
            if ( Help.isNull(v_API.getCallXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：XSQLConfig[" + Help.NVL(v_API.getXid()) + "].callXID is null.");
                return false;
            }
            
            if ( !Help.isNull(v_API.getCallParams()) )
            {
                int x = 0;
                for (NodeParam v_NodeParam : v_API.getCallParams())
                {
                    x++;
                    
                    if ( v_NodeParam.getValue() == null && v_NodeParam.getValueDefault() == null )
                    {
                        // 方法参数及默认值均会空时异常
                        io_Result.set(false).setParamStr("CFlowCheck：XSQLConfig[" + Help.NVL(v_API.getXid()) + "].callParams[" + x + "] value and valueDefault is null.");
                        return false;
                    }
                    
                    if ( !Help.isNull(v_NodeParam.getValue()) )
                    {
                        if ( !ValueHelp.isRefID(v_NodeParam.getValue()) )
                        {
                            if ( Help.isNull(v_NodeParam.getValueClass()) )
                            {
                                // 方法参数为数值类型时，参数类型应不会空
                                io_Result.set(false).setParamStr("CFlowCheck：XSQLConfig[" + Help.NVL(v_API.getXid()) + "].callParams[" + x + "] value is Normal type ,but valueClass is null.");
                                return false;
                            }
                        }
                    }
                    
                    if ( !Help.isNull(v_NodeParam.getValueDefault()) )
                    {
                        if ( !ValueHelp.isRefID(v_NodeParam.getValueDefault()) )
                        {
                            if ( Help.isNull(v_NodeParam.getValueClass()) )
                            {
                                // 方法参数的默认值为数值类型时，参数类型应不会空
                                io_Result.set(false).setParamStr("CFlowCheck：XSQLConfig[" + Help.NVL(v_API.getXid()) + "].callParams[" + x + "] valueDefault is Normal type ,but valueClass is null.");
                                return false;
                            }
                        }
                    }
                }
            }
        }
        else if ( i_ExecObject instanceof JOBConfig )
        {
            JOBConfig v_API = (JOBConfig) i_ExecObject;
            
            // 子编排的XID不能为空
            if ( Help.isNull(v_API.getCallFlowXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].callFlowXID is null.");
                return false;
            }
            
            // 间隔类型
            if ( Help.isNull(v_API.getIntervalType()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].intervalType is null.");
                return false;
            }
            
            if ( !v_API.getIntervalType().startsWith(DBSQL.$Placeholder) )
            {
                if ( JobIntervalType.get(v_API.getIntervalType()) == null )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].intervalType is invalid.");
                    return false;
                }
            }
            
            // 间隔时长
            if ( Help.isNull(v_API.getIntervalLen()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].intervalLen is null.");
                return false;
            }
            
            if ( !v_API.getIntervalLen().startsWith(DBSQL.$Placeholder) )
            {
                if ( !Help.isNumber(v_API.getIntervalLen()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].intervalLen is invalid.");
                    return false;
                }
                
                int v_IntervalLen = Integer.parseInt(v_API.getIntervalLen());
                if ( v_IntervalLen <= 0 )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：JOBConfig[" + Help.NVL(v_API.getXid()) + "].intervalLen <= 0.");
                    return false;
                }
            }
        }
        else if ( i_ExecObject instanceof CommandConfig )
        {
            CommandConfig v_Command = (CommandConfig) i_ExecObject;
            
            if ( Help.isNull(v_Command.getCommand()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：CommandConfig[" + Help.NVL(v_Command.getXid()) + "].command is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof PythonConfig )
        {
            PythonConfig v_Python = (PythonConfig) i_ExecObject;
            
            if ( Help.isNull(v_Python.getPython()) && Help.isNull(v_Python.getScript()) )
            {
                io_Result.set(false).setParamStr("PythonConfig：PythonConfig[" + Help.NVL(v_Python.getXid()) + "].script and python is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof ZipConfig )
        {
            ZipConfig v_Zip = (ZipConfig) i_ExecObject;
            
            if ( Help.isNull(v_Zip.getFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ZipConfig[" + Help.NVL(v_Zip.getXid()) + "].file is null.");
                return false;
            }
            if ( Help.isNull(v_Zip.getDir()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：ZipConfig[" + Help.NVL(v_Zip.getXid()) + "].dir is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof UnzipConfig )
        {
            UnzipConfig v_Unzip = (UnzipConfig) i_ExecObject;
            if ( Help.isNull(v_Unzip.getFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：UnzipConfig[" + Help.NVL(v_Unzip.getXid()) + "].file is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof EncryptFileConfig )
        {
            EncryptFileConfig v_EncryptFile = (EncryptFileConfig) i_ExecObject;
            if ( Help.isNull(v_EncryptFile.getFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：EncryptFileConfig[" + Help.NVL(v_EncryptFile.getXid()) + "].file is null.");
                return false;
            }
            if ( Help.isNull(v_EncryptFile.getDir()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：EncryptFileConfig[" + Help.NVL(v_EncryptFile.getXid()) + "].dir is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof DecryptFileConfig )
        {
            DecryptFileConfig v_EncryptFile = (DecryptFileConfig) i_ExecObject;
            if ( Help.isNull(v_EncryptFile.getFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：DecryptFileConfig[" + Help.NVL(v_EncryptFile.getXid()) + "].file is null.");
                return false;
            }
            if ( Help.isNull(v_EncryptFile.getPassword()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：DecryptFileConfig[" + Help.NVL(v_EncryptFile.getXid()) + "].password is null.");
                return false;
            }
        }
        else if ( i_ExecObject instanceof NodeConfig )
        {
            NodeConfig v_Node = (NodeConfig) i_ExecObject;
            
            // 执行元素的执行对象不能为空
            if ( Help.isNull(v_Node.getCallXID()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callXID is null.");
                return false;
            }
            // 执行元素的执行方法不能为空
            if ( Help.isNull(v_Node.getCallMethod()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callMethod is null.");
                return false;
            }
            
            if ( !Help.isNull(v_Node.getCallParams()) )
            {
                int x = 0;
                for (NodeParam v_NodeParam : v_Node.getCallParams())
                {
                    x++;
                    
                    if ( v_NodeParam.getValue() == null && v_NodeParam.getValueDefault() == null )
                    {
                        // 方法参数及默认值均会空时异常
                        io_Result.set(false).setParamStr("CFlowCheck：NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callParams[" + x + "] value and valueDefault is null.");
                        return false;
                    }
                    
                    if ( !Help.isNull(v_NodeParam.getValue()) )
                    {
                        if ( !ValueHelp.isRefID(v_NodeParam.getValue()) )
                        {
                            if ( Help.isNull(v_NodeParam.getValueClass()) )
                            {
                                // 方法参数为数值类型时，参数类型应不会空
                                io_Result.set(false).setParamStr("CFlowCheck：NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callParams[" + x + "] value is Normal type ,but valueClass is null.");
                                return false;
                            }
                        }
                    }
                    
                    if ( !Help.isNull(v_NodeParam.getValueDefault()) )
                    {
                        if ( !ValueHelp.isRefID(v_NodeParam.getValueDefault()) )
                        {
                            if ( Help.isNull(v_NodeParam.getValueClass()) )
                            {
                                // 方法参数的默认值为数值类型时，参数类型应不会空
                                io_Result.set(false).setParamStr("CFlowCheck：NodeConfig[" + Help.NVL(v_Node.getXid()) + "].callParams[" + x + "] valueDefault is Normal type ,but valueClass is null.");
                                return false;
                            }
                        }
                    }
                }
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
     * （公共方法的递归）条件逻辑元素的检测
     * 
     * 仅做离线环境下的检测。
     * 不做在线环境下的检测。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @param i_Condition   条件逻辑
     * @param io_Result     表示检测结果
     * @param i_ExecObject  （顶级）执行对象（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等）
     * @param io_XIDs       所有元素的XID，及被引用的数量
     * @param io_ForXIDs    所有For循环的XID，及被引用的数量
     * @return              是否检测合格
     */
    private boolean check_Condition(ConditionConfig i_Condition ,Return<Object> io_Result ,IExecute i_ExecObject ,Map<String ,Integer> io_XIDs ,Map<String ,Integer> io_ForXIDs)
    {
        // 条件元素的逻辑不能为空
        if ( i_Condition.getLogical() == null )
        {
            io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "].logical is null.");
            return false;
        }
        // 条件元素必须要用至少一个条件项
        if ( Help.isNull(i_Condition.getItems()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "] has no condition items.");
            return false;
        }
        
        for (IfElse v_Item : i_Condition.getItems())
        {
            if ( v_Item instanceof ConditionConfig )
            {
                if ( !this.check_Condition((ConditionConfig) v_Item ,io_Result ,i_ExecObject ,io_XIDs ,io_ForXIDs) )
                {
                    return false;
                }
            }
            else if ( v_Item instanceof ConditionItem )
            {
                ConditionItem v_CItem = (ConditionItem) v_Item;
                if ( v_CItem.getComparer() == null )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "].comparer is null.");
                    return false;
                }
                
                if ( Help.isNull(v_CItem.getValueXIDA()) )
                {
                    io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "].valueXIDA is null.");
                    return false;
                }
                
                if ( !ValueHelp.isRefID(v_CItem.getValueXIDA()) )
                {
                    if ( Help.isNull(v_CItem.getValueClass()) )
                    {
                        // 条件项的比值为数值类型时，其类型应不会空
                        io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "] valueXIDA is Normal type ,but valueClass is null.");
                        return false;
                    }
                }
                
                if ( !Help.isNull(v_CItem.getValueXIDB()) )
                {
                    if ( !ValueHelp.isRefID(v_CItem.getValueXIDB()) )
                    {
                        if ( Help.isNull(v_CItem.getValueClass()) )
                        {
                            // 条件项的比值为数值类型时，其类型应不会空
                            io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "] valueXIDB is Normal type ,but valueClass is null.");
                            return false;
                        }
                    }
                }
            }
            else
            {
                io_Result.set(false).setParamStr("CFlowCheck：ConditionConfig[" + Help.NVL(i_ExecObject.getXJavaID()) + "].item is unknown type.");
                return false;
            }
        }
        
        return true;
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
                        io_Result.set(false).setParamStr("CFlowCheck：SelfLoop.RefXID[" + v_SelfLoop.getRefXID() + "] is not exists.");
                        return false;
                    }
                    else
                    {
                        io_XIDs.put(v_RefXID ,v_RefCount + 1);
                        
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
