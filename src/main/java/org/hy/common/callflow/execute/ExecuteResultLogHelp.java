package org.hy.common.callflow.execute;

import java.util.List;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.nesting.MTExecuteResult;





/**
 * 执行结果的日志辅助类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-15
 * @version     v1.0
 */
public class ExecuteResultLogHelp
{
    
    private static ExecuteResultLogHelp $Instance = new ExecuteResultLogHelp();
    
    
    
    /**
     * 获取单例执行结果的日志的相关操作类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @return
     */
    public static ExecuteResultLogHelp getInstance()
    {
        return $Instance;
    }
    
    
    
    /**
     * 生成日志内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @param i_Result  执行结果
     * @return
     */
    public String logs(ExecuteResult i_Result)
    {
        if ( i_Result == null )
        {
            throw new NullPointerException("ExecuteResult is null.");
        }
        
        StringBuilder v_Buffer = new StringBuilder();
        
        int[] v_Lens = calcMaxLen(i_Result);
        this.logs_Inner(i_Result ,v_Buffer ,v_Lens[0] + 1 ,v_Lens[1] + 1 ,v_Lens[2]);
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 计算最大长度
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @param i_Result  执行结果
     * @return          第1个元素表示：执行对象的树ID的最大长度
     *                  第2个元素表示：执行结果的树ID的最大长度
     *                  第3个元素表示：执行结果的XID的最大长度
     */
    private int[] calcMaxLen(ExecuteResult i_Result)
    {
        int v_MaxExecuteTreeIDLen = 0;
        int v_MaxResultTreeIDLen  = 0;
        int v_MaxExecuteXIDLen    = 0;
        
        v_MaxExecuteTreeIDLen = Help.max(i_Result.getExecuteTreeID().length() ,v_MaxExecuteTreeIDLen);
        v_MaxResultTreeIDLen  = Help.max(i_Result.getTreeID().length()        ,v_MaxResultTreeIDLen);
        v_MaxExecuteXIDLen    = Help.max(i_Result.getExecuteXID().length()    ,v_MaxExecuteXIDLen);
        
        if ( !Help.isNull(i_Result.getNexts()) )
        {
           for (ExecuteResult v_Item : i_Result.getNexts())
           {
               int [] v_ItemRet = this.calcMaxLen(v_Item);
               
               v_MaxExecuteTreeIDLen = Help.max(v_ItemRet[0] ,v_MaxExecuteTreeIDLen);
               v_MaxResultTreeIDLen  = Help.max(v_ItemRet[1] ,v_MaxResultTreeIDLen);
               v_MaxExecuteXIDLen    = Help.max(v_ItemRet[2] ,v_MaxExecuteXIDLen);
           }
        }
        
        return new int [] {v_MaxExecuteTreeIDLen ,v_MaxResultTreeIDLen ,v_MaxExecuteXIDLen};
    }
    
    
    
    /**
     * 递归生成日志
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-15
     * @version     v1.0
     *
     * @param i_Result               执行结果
     * @param io_Logs                日志缓存区
     * @param i_MaxExecuteTreeIDLen  执行对象的树ID的最大长度
     * @param i_MaxResultTreeIDLen   执行结果的树ID的最大长度
     * @param i_MaxExecuteXIDLen     执行结果的XID的最大长度
     */
    @SuppressWarnings("unchecked")
    private void logs_Inner(ExecuteResult i_Result ,StringBuilder io_Logs ,int i_MaxExecuteTreeIDLen ,int i_MaxResultTreeIDLen ,int i_MaxExecuteXIDLen)
    {
        io_Logs.append(StringHelp.rpad(i_Result.getExecuteTreeID() ,i_MaxExecuteTreeIDLen ," "));
        io_Logs.append(StringHelp.rpad(i_Result.getTreeID()        ,i_MaxResultTreeIDLen  ," "));
        io_Logs.append(Date.toTimeLenNano(i_Result.getEndTime() - i_Result.getBeginTime()));
        io_Logs.append(i_Result.isSuccess() ? " 成功 " : " 异常 ");
        io_Logs.append(i_Result.getStatus().getComment()).append(" ");
        io_Logs.append(StringHelp.rpad(i_Result.getExecuteXID() ,i_MaxExecuteXIDLen ," "));
        io_Logs.append(StringHelp.lpad("" ,i_Result.getNestingLevel() * 4 ," ")).append(" ");
        io_Logs.append(i_Result.getExecuteLogic()).append(" ");
        
        if ( ElementType.MT.getValue().equals(i_Result.getElementType()) )
        {
            io_Logs.append(Help.NVL(i_Result.getComment())).append("\n\n");
            
            List<MTExecuteResult> v_MTResults = (List<MTExecuteResult>) i_Result.getResult();
            if ( Help.isNull(v_MTResults) )
            {
                io_Logs.append("并发结果为空\n");
            }
            else
            {
                for (MTExecuteResult v_MTResult : v_MTResults)
                {
                    io_Logs.append("并发：").append(v_MTResult.getIndexNo()).append(" ")
                           .append(v_MTResult.getCallFlowXID()).append(" ")
                           .append(Help.NVL(v_MTResult.getComment())).append("\n");
                    
                    ExecuteResult v_MTFirstResult = CallFlow.getFirstResult(v_MTResult.getContext());
                    io_Logs.append(this.logs(v_MTFirstResult)).append("\n");
                }
            }
        }
        else if ( ElementType.XSQL.getValue().equals(i_Result.getElementType()) )
        {
            io_Logs.append(Help.NVL(i_Result.getComment())).append("\n");
        }
        else
        {
            io_Logs.append(Help.NVL(i_Result.getResult())).append(" ");
            io_Logs.append(Help.NVL(i_Result.getComment())).append("\n");
        }
        
        if ( !Help.isNull(i_Result.getNexts()) )
        {
           for (ExecuteResult v_Item : i_Result.getNexts())
           {
               this.logs_Inner(v_Item ,io_Logs ,i_MaxExecuteTreeIDLen ,i_MaxResultTreeIDLen ,i_MaxExecuteXIDLen);
           }
        }
    }
    
    
    
    private ExecuteResultLogHelp()
    {
        // Nothing.
    }
    
}
