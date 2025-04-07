package org.hy.common.callflow.node;





/**
 * 接口元素的业务逻辑异常。
 * 
 * 即用APIConfig.succeedFlag判定出的非成功时。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-07
 * @version     v1.0
 */
public class APIException extends Exception
{

    private static final long serialVersionUID = 6767190988227559208L;
    
    
    
    /** 请求的地址 */
    private String url;
    
    /** 响应消息 */
    private String message;
    
    /** 响应消息转为的对象（可以为空） */
    private Object returnObject;
    
    
    
    public APIException(String i_Url ,String i_Message ,Object i_ReturnObject)
    {
        this.url          = i_Url;
        this.message      = i_Message;
        this.returnObject = i_ReturnObject;
    }


    
    /**
     * 获取：请求的地址
     */
    public String getUrl()
    {
        return url;
    }


    
    /**
     * 获取：响应消息
     */
    public String getMessage()
    {
        return message;
    }


    
    /**
     * 获取：响应消息转为的对象（可以为空）
     */
    public Object getReturnObject()
    {
        return returnObject;
    }
    
}
