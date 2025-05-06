package org.hy.common.callflow.event.mqtt;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.event.SubscribeConfig;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.mqtt.broker.BrokerConfig;
import org.hy.common.mqtt.broker.XBroker;
import org.hy.common.mqtt.client.subscribe.IMqttMessage;
import org.hy.common.mqtt.client.subscribe.IMqttMessageListener;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * MQTT订阅消息的监听器 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-05-06
 * @version     v1.0
 */
public class SubscribeMQTTListener implements IMqttMessageListener
{
    
    private static Logger $Logger = new Logger(SubscribeMQTTListener.class);
    
    
    
    /** MQTT订阅消息配置 */
    private SubscribeMQTT   subscribe;
    
    /** MQTT服务 */
    private XBroker         broker;
    
    /** 订阅元素 */
    private SubscribeConfig config;
    
    
    
    public SubscribeMQTTListener(SubscribeMQTT i_Subscribe ,SubscribeConfig i_Config)
    {
        this.subscribe = i_Subscribe;
        
        if ( this.subscribe.getBroker() == null )
        {
            NullPointerException v_Exce = new NullPointerException(this.subscribe.getXid() + "：MQTT Broker is null");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        BrokerConfig v_Broker = this.subscribe.getBroker();
        if ( Help.isNull(v_Broker.getMqttVersion()) )
        {
            NullPointerException v_Exce = new NullPointerException(v_Broker.getXid() + "：MQTT协议版本为空");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNull(v_Broker.getProtocol()) )
        {
            NullPointerException v_Exce = new NullPointerException(v_Broker.getXid() + "：MQTT协议类型为空");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNull(v_Broker.getHost()) )
        {
            NullPointerException v_Exce = new NullPointerException(v_Broker.getXid() + "：MQTT服务配置主机为空");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNull(v_Broker.getPort()) )
        {
            NullPointerException v_Exce = new NullPointerException(v_Broker.getXid() + "：MQTT服务配置端口为空");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        try
        {
            if ( !Help.isNull(i_Config.getBrokerPassword()) )
            {
                v_Broker.setUserPassword(i_Config.getBrokerPassword());
            }
            
            this.config = i_Config;
            this.broker = new XBroker(v_Broker);
            this.broker.getMqttClient().connect();
            this.subscribeMessage();
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            throw new RuntimeException(exce);
        }
    }
    
    
    
    /**
     * 订阅消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    public boolean subscribeMessage()
    {
        return this.broker.getMqttClient().subscribe(this.subscribe.getTopic()
                                                    ,this.subscribe.getQoS()
                                                    ,this);
    }
    
    
    
    /**
     * 取消订阅并关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @return
     */
    public boolean unsubscribeClose()
    {
        this.broker.getMqttClient().unsubscribe(this.subscribe.getTopic());
        return this.broker.getMqttClient().close();
    }
    
    
    
    /**
     * 收到消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-02-26
     * @version     v1.0
     *
     * @param i_Topic     主题
     * @param i_Message   消息
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void messageArrived(String i_Topic, IMqttMessage i_Message) throws Exception
    {
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        if ( !Help.isNull(this.config.gatExecuteContext()) )
        {
            // 克隆运行时上下文，确保每次运行时，都是一样的参数，不受上次执行的影响
            v_Context.putAll(this.config.gatExecuteContext());
        }
        
        if ( !Help.isNull(this.config.getContext()) )
        {
            try
            {
                String v_ContextValue = ValueHelp.replaceByContext(this.config.getContext() ,v_Context);
                Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_ContextValue ,Map.class ,null ,v_Context);
                v_Context.putAll(v_ContextMap);
                v_ContextMap.clear();
                v_ContextMap = null;
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
                return;
            }
        }
        
        // 返回ID在收到消息后，可裂变成两个上下文变量
        if ( !Help.isNull(this.config.getReturnID()) )
        {
            v_Context.put(this.config.getReturnIDTopic()   ,i_Topic);
            v_Context.put(this.config.getReturnIDMessage() ,new String(i_Message.getPayload()));
        }
        
        if ( Help.isNull(this.config.gatCallFlowXID()) )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.config.getXid()) + ":" + Help.NVL(this.config.getComment()) + "]'s callFlowXID[" + Help.NVL(this.config.gatCallFlowXID() ,"?") + "] is null."));
            return;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.config.gatCallFlowXID());
        if ( v_CallObject == null )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.config.getXid()) + ":" + Help.NVL(this.config.getComment()) + "]'s CallFlowXID[" + this.config.gatCallFlowXID() + "] is not find."));
            return;
        }
        
        // 执行对象不是编排元素
        if ( !MethodReflect.isExtendImplement(v_CallObject ,ExecuteElement.class) )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.config.getXid()) + ":" + Help.NVL(this.config.getComment()) + "]'s CallFlowXID[" + this.config.gatCallFlowXID() + "] is not ExecuteElement."));
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute((ExecuteElement) v_CallObject ,v_Context);
        if ( v_Result.isSuccess() )
        {
            $Logger.info("Success：" + Help.NVL(this.config.getXid()) + ":" + Help.NVL(this.config.getComment()));
        }
        else
        {
            $Logger.error("Failed：" + Help.NVL(this.config.getXid()) + ":" + Help.NVL(this.config.getComment()) + "。Error XID = " + v_Result.getExecuteXID() ,v_Result.getException());
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        $Logger.info("\n" + CallFlow.getHelpLog().logs(v_FirstResult));
        
        v_Context.clear();
        v_Context = null;
    }
    
}
