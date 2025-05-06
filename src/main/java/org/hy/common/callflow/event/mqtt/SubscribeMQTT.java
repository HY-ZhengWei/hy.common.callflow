package org.hy.common.callflow.event.mqtt;

import org.hy.common.mqtt.broker.BrokerConfig;





/**
 * MQTT订阅消息的配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-05-06
 * @version     v1.0
 */
public class SubscribeMQTT
{
    
    /** 主键 */
    private String       id;
    
    /** 逻辑ID */
    private String       xid;
    
    /** 主题 */
    private String       topic;
    
    /** 服务质量等级 */
    private Integer      qoS;
    
    /** 注解说明 */
    private String       comment;
    
    /** MQTT服务 */
    private BrokerConfig broker;

    
    /**
     * 获取：主键
     */
    public String getId()
    {
        return id;
    }

    
    /**
     * 设置：主键
     * 
     * @param i_Id 主键
     */
    public void setId(String i_Id)
    {
        this.id = i_Id;
    }

    
    /**
     * 获取：逻辑ID
     */
    public String getXid()
    {
        return xid;
    }

    
    /**
     * 设置：逻辑ID
     * 
     * @param i_Xid 逻辑ID
     */
    public void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }

    
    /**
     * 获取：主题
     */
    public String getTopic()
    {
        return topic;
    }

    
    /**
     * 设置：主题
     * 
     * @param i_Topic 主题
     */
    public void setTopic(String i_Topic)
    {
        this.topic = i_Topic;
    }

    
    /**
     * 获取：服务质量等级
     */
    public Integer getQoS()
    {
        return qoS;
    }

    
    /**
     * 设置：服务质量等级
     * 
     * @param i_QoS 服务质量等级
     */
    public void setQoS(Integer i_QoS)
    {
        this.qoS = i_QoS;
    }

    
    /**
     * 获取：注解说明
     */
    public String getComment()
    {
        return comment;
    }

    
    /**
     * 设置：注解说明
     * 
     * @param i_Comment 注解说明
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }

    
    /**
     * 获取：MQTT服务
     */
    public BrokerConfig getBroker()
    {
        return broker;
    }

    
    /**
     * 设置：MQTT服务
     * 
     * @param i_Broker MQTT服务
     */
    public void setBroker(BrokerConfig i_Broker)
    {
        this.broker = i_Broker;
    }
    
}
