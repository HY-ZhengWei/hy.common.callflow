package org.hy.common.callflow.mock;





/**
 * 模拟异常
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-06
 * @version     v1.0
 */
public class MockException extends RuntimeException
{
    
    private static final long serialVersionUID = 6577103309260599454L;



    public MockException(String i_Message)
    {
        super(i_Message);
    }



    public MockException(String i_Message, Throwable i_Cause)
    {
        super(i_Message ,i_Cause);
    }
    
}
