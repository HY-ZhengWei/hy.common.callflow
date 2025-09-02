package org.hy.common.callflow.event.websocket;





/**
 * 点推元素的执行者接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-02
 * @version     v1.0
 */
public interface WSPushExecuter
{
    
    /**
     * 注册WebSocket接口的模块信息、名称等配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-02
     * @version     v1.0
     *
     * @param i_ServiceType      服务类型
     * @param i_ServiceTypeName  服务类型的名称
     * @param i_ModuleCode       模块编号
     * @param i_ModuleName       模块名称
     */
    public void register(String i_ServiceType ,String i_ServiceTypeName ,String i_ModuleCode ,String i_ModuleName);
    
    
    
    /**
     * 向客户端群发消息。
     * 
     * 首次接入的客户端，将发送全部消息，之后将只发有变化的消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-02
     * @version     v1.0
     * 
     * @param i_ServiceType  服务类型
     * @param i_NewMessage   仅有变化的消息
     * @param i_AllMessage   全部消息
     */
    public void pushMessages(String i_ServiceType ,String i_NewMessage ,String i_AllMessage);
    
}
