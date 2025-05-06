package org.hy.common.callflow.event.mqtt;

import java.util.List;

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
