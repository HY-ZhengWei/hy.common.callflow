package org.hy.common.callflow.event.mqtt;

import java.util.List;





/**
 * MQTT订阅消息的配置集合
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-05-06
 * @version     v1.0
 */
public class SubscribeMQTTs
{
    
    /** 数据集 */
    private List<SubscribeMQTT> datas;

    
    
    /**
     * 获取：数据集
     */
    public List<SubscribeMQTT> getDatas()
    {
        return datas;
    }

    
    /**
     * 设置：数据集
     * 
     * @param i_Datas 数据集
     */
    public void setDatas(List<SubscribeMQTT> i_Datas)
    {
        this.datas = i_Datas;
    }
    
}
