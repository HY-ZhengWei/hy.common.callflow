package org.hy.common.callflow.event.websocket;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.enums.WSContentType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 点拉元素的执行者接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-30
 * @version     v1.0
 */
public interface WSPullExecuter
{
    
    static final Logger $Logger = new Logger(WSPullExecuter.class);
    
    
    
    /**
     * 初始化点拉元素的执行者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param i_WSPullData  点拉元素的执行数据
     * @return
     */
    public boolean init(WSPullData i_WSPullData);
    
    
    
    /**
     * 执行点拉元素中配置的在收到消息时触发的编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-30
     * @version     v1.0
     *
     * @param i_WSPullData  点拉元素的执行数据
     * @param i_Message     收到消息
     * @return
     */
    default boolean onMessage(WSPullData i_WSPullData ,String i_Message)
    {
        Object v_CallFlowObject = XJava.getObject(i_WSPullData.getCallFlowXID());
        if ( v_CallFlowObject == null )
        {
            $Logger.error("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] is not find.");
            return false;
        }
        if ( !(v_CallFlowObject instanceof ExecuteElement) )
        {
            $Logger.error("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] is not ExecuteElement type.");
            return false;
        }
        
        ExecuteElement v_CallFlow = (ExecuteElement) v_CallFlowObject;
        
        // 克隆运行时上下文，确保每次运行时，都是一样的参数，不受上次执行的影响
        Map<String ,Object> v_Context = i_WSPullData.getExecuteContext();
        
        // 处理收到消息
        if ( !Help.isNull(i_WSPullData.getReturnID()) )
        {
            if ( i_WSPullData.getContentType() == null || WSContentType.Text.equals(i_WSPullData.getContentType()) )
            {
                v_Context.put(i_WSPullData.getReturnID() ,i_Message);
            }
            else if ( WSContentType.Json.equals(i_WSPullData.getContentType()) )
            {
                XJSON  v_XJson     = new XJSON();
                Object v_MsgObject = v_XJson.toJava(i_Message ,i_WSPullData.getReturnClass() == null ? HashMap.class : i_WSPullData.getReturnClass());
                v_Context.put(i_WSPullData.getReturnID() ,v_MsgObject);
            }
        }
        
        ExecuteResult v_Result = CallFlow.execute(v_CallFlow ,v_Context);
        if ( !v_Result.isSuccess() )
        {
            $Logger.error("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] Error XID = " + v_Result.getExecuteXID());
            $Logger.error("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] Error Msg = " + v_Result.getException().getMessage());
            $Logger.error("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] Error." ,v_Result.getException());
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        if ( v_FirstResult != null )
        {
            $Logger.info("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] 日志轨迹\n" + CallFlow.getHelpLog().logs(v_FirstResult));
            $Logger.info("CallFlowXID[:" + i_WSPullData.getCallFlowXID() + "] 整体用时：" + Date.toTimeLenNano(v_Result.getEndTime() - v_Result.getBeginTime()) + "\n");
        }
        
        return v_Result.isSuccess();
    }
    
}
