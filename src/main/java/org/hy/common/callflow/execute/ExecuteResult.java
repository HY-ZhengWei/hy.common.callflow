package org.hy.common.callflow.execute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.callflow.common.ITreeID;
import org.hy.common.callflow.common.TreeIDHelp;
import org.hy.common.callflow.enums.ExecuteStatus;
import org.hy.common.xml.log.Logger;





/**
 * 执行结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-15
 * @version     v1.0
 */
public class ExecuteResult implements ITreeID
{
    
    private static final Logger     $Logger = new Logger(ExecuteResult.class);
    
    public  static final TreeIDHelp $TreeID = new TreeIDHelp("." ,1 ,1);
    
    
    
    /** 结果树ID */
    private String              treeID;
    
    /** 结果树层级 */
    private Integer             treeLevel;
    
    /** 结果树中同层同父的序号编号 */
    private Integer             treeNo;
    
    /** 执行对象的树ID */
    private String              executeTreeID;
    
    /** 执行对象的全局惟一标识ID */
    private String              executeXID;
    
    /** 执行逻辑的表达式 */
    private String              executeLogic;
                                
    /** 执行状态 */             
    private ExecuteStatus       status;
    
    /** 执行结果（方法编排中的最后一个结果数据） */
    private Object              result;
                                
    /** 执行结果是否成功 */
    private boolean             success;
                                
    /** 为异常对象 */           
    private Exception           exception;
                                
    /** 执行开始时间（精度：纳秒） */         
    private Long                beginTime;
                                
    /** 执行结束时间（精度：纳秒） */         
    private Long                endTime;
                                
    /** 执行链：双向链表：前一个 */       
    private ExecuteResult       previous;
    
    /** 执行链：双向链表：其后多个 */
    private List<ExecuteResult> nexts;
    
    
    
    public ExecuteResult()
    {
        this("" ,"" ,"");
    }
    
    
    public ExecuteResult(String i_ExecuteTreeID ,String i_ExecuteXID ,String i_ExecuteLogic)
    {
        this(i_ExecuteTreeID ,i_ExecuteXID ,i_ExecuteLogic ,null);
    }
    
    
    public ExecuteResult(String i_ExecuteTreeID ,String i_ExecuteXID ,String i_ExecuteLogic ,ExecuteResult i_Previous)
    {
        this.beginTime     = Date.getTimeNano();
        this.success       = false;
        this.executeTreeID = i_ExecuteTreeID;
        this.executeXID    = i_ExecuteXID;
        this.executeLogic  = i_ExecuteLogic;
        this.status        = ExecuteStatus.Started;
        this.previous      = i_Previous;
    }
    
    
    /**
     * 执行超时
     * 
     * @param i_Exception 为异常对象
     */
    public ExecuteResult setTimeout()
    {
        // 所有状态类的setter方法仅允许执行一次
        synchronized ( this )
        {
            if ( this.success || this.exception != null )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("All state setter methods are only allowed to be executed once.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
        }
        
        this.endTime   = Date.getTimeNano();
        this.exception = new TimeoutException();
        this.success   = false;
        this.status    = ExecuteStatus.Timeout;
        
        return this;
    }
    
    
    /**
     * 取消执行
     * 
     * @param i_Exception 为异常对象
     */
    public ExecuteResult setCancel()
    {
        // 所有状态类的setter方法仅允许执行一次
        synchronized ( this )
        {
            // 可以在执行完成后，再取消，此时的取消是中断后续操作的取消
            // 所以成功标记与取消标记可以同时存在
            if ( this.exception != null )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("All state setter methods are only allowed to be executed once.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
        }
        
        this.endTime   = Date.getTimeNano();
        this.exception = new CancellationException();
        this.status    = ExecuteStatus.Canceled;
        
        return this;
    }
    
    
    /**
     * 获取：执行结果（方法编排中的最后一个结果数据）
     */
    public Object getResult()
    {
        return result;
    }

    
    /**
     * 设置：执行结果（方法编排中的最后一个结果数据）
     * 
     * @param i_Result 执行结果（方法编排中的最后一个结果数据）
     */
    public ExecuteResult setResult(Object i_Result)
    {
        // 所有状态类的setter方法仅允许执行一次
        synchronized ( this )
        {
            if ( this.success || this.exception != null )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("All state setter methods are only allowed to be executed once.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
        }
        
        this.endTime = Date.getTimeNano();
        this.success = true;
        this.result  = i_Result;
        this.status  = ExecuteStatus.Finished;
        
        return this;
    }


    /**
     * 获取：为异常对象
     */
    public Exception getException()
    {
        return exception;
    }

    
    /**
     * 设置：为异常对象
     * 
     * @param i_Exception 为异常对象
     */
    public ExecuteResult setException(Exception i_Exception)
    {
        // 所有状态类的setter方法仅允许执行一次
        synchronized ( this )
        {
            if ( this.success || this.exception != null )
            {
                if ( this.success || this.exception != null )
                {
                    IllegalArgumentException v_Exce = new IllegalArgumentException("All state setter methods are only allowed to be executed once.");
                    $Logger.error(v_Exce);
                    throw v_Exce;
                }
            }
        }
        
        this.endTime   = Date.getTimeNano();
        this.exception = i_Exception;
        this.success   = false;
        this.status    = ExecuteStatus.Exception;
        
        return this;
    }
    
    
    /**
     * 获取：执行链：前一个
     */
    public ExecuteResult getPrevious()
    {
        return previous;
    }

    
    /**
     * 设置：执行链：前一个
     * 
     * @param i_Previous 执行链：前一个
     */
    public ExecuteResult setPrevious(ExecuteResult i_Previous)
    {
        // 前一个仅允许被设置一次
        synchronized ( this )
        {
            if ( this.previous != null )
            {
                if ( this.success || this.exception != null )
                {
                    IllegalArgumentException v_Exce = new IllegalArgumentException("The setPrevious method of A can only be called once.");
                    $Logger.error(v_Exce);
                    throw v_Exce;
                }
            }
        }
        
        this.previous = i_Previous;
        if ( this.previous == null )
        {
            this.setTreeID(null ,$TreeID.getMinIndexNo());
        }
        else
        {
            int v_IndexNo = 1;
            if ( this.previous.getNexts() != null )
            {
                v_IndexNo = this.previous.getNexts().size() + 1;
            }
            this.setTreeID(this.previous.getTreeID() ,v_IndexNo);
        }
        return this;
    }
    
    
    /**
     * 获取：执行链：双向链表：其后多个
     */
    public List<ExecuteResult> getNexts()
    {
        return nexts;
    }
    
    
    /**
     * 添加执行链：双向链表中的其后一个
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param i_Next  其后一个执行结果
     * @return
     */
    public synchronized ExecuteResult addNext(ExecuteResult i_Next)
    {
        if ( this.nexts == null )
        {
            this.nexts = new ArrayList<ExecuteResult>();
        }
        
        this.nexts.add(i_Next);
        return this;
    }
    
    
    /**
     * 获取：层级树ID
     */
    public String getTreeID()
    {
        return treeID;
    }
    
    
    /**
     * 生成本次树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     */
    private ExecuteResult setTreeID(String i_SuperTreeID ,int i_IndexNo)
    {
        return this.setTreeID($TreeID.getTreeID(i_SuperTreeID ,i_IndexNo));
    }

    
    /**
     * 设置：层级树ID
     * 
     * @param i_TreeID 层级树ID
     */
    private ExecuteResult setTreeID(String i_TreeID)
    {
        if ( Help.isNull(i_TreeID) )
        {
            this.treeID    = null;
            this.treeLevel = null;
            this.treeNo    = null;
        }
        else
        {
            this.treeLevel = $TreeID.getLevel(  i_TreeID);
            this.treeNo    = $TreeID.getIndexNo(i_TreeID);
            this.treeID    = i_TreeID;
        }
        return this;
    }

    
    /**
     * 获取：树层级
     */
    public Integer getTreeLevel()
    {
        return treeLevel;
    }

    
    /**
     * 获取：树中同层同父的序号编号
     */
    public Integer getTreeNo()
    {
        return treeNo;
    }



    /**
     * 获取：执行对象的树ID
     */
    public String getExecuteTreeID()
    {
        return executeTreeID;
    }
    
    
    /**
     * 设置：执行对象的树ID
     * 
     * @param i_ExecuteTreeID 执行对象的树ID
     */
    public ExecuteResult setExecuteTreeID(String i_ExecuteTreeID)
    {
        this.executeTreeID = i_ExecuteTreeID;
        return this;
    }


    /**
     * 获取：执行对象的全局惟一标识ID
     */
    public String getExecuteXID()
    {
        return executeXID;
    }
    
    
    /**
     * 设置：执行对象的全局惟一标识ID
     * 
     * @param i_ExecuteXID 执行对象的全局惟一标识ID
     */
    public ExecuteResult setExecuteXID(String i_ExecuteXID)
    {
        this.executeXID = i_ExecuteXID;
        return this;
    }


    /**
     * 获取：执行逻辑的表达式
     */
    public String getExecuteLogic()
    {
        return executeLogic;
    }

    
    /**
     * 设置：执行逻辑的表达式
     * 
     * @param i_ExecuteLogic 执行逻辑的表达式
     */
    public ExecuteResult setExecuteLogic(String i_ExecuteLogic)
    {
        this.executeLogic = i_ExecuteLogic;
        return this;
    }


    /**
     * 获取：执行结果是否成功
     */
    public boolean isSuccess()
    {
        return success;
    }

    
    /**
     * 获取：执行开始时间（精度：纳秒）
     */
    public Long getBeginTime()
    {
        return beginTime;
    }
    
    
    /**
     * 获取：执行开始时间（精度：毫秒）
     */
    public Date getBeginDate()
    {
        return Date.nanoToDate(this.beginTime);
    }

    
    /**
     * 获取：执行结束时间
     */
    public Long getEndTime()
    {
        return endTime;
    }
    
    
    /**
     * 获取：执行结束时间（精度：毫秒）
     */
    public Date getEndDate()
    {
        return Date.nanoToDate(this.endTime);
    }

    
    /**
     * 获取：执行状态
     */
    public ExecuteStatus getStatus()
    {
        return status;
    }
    
}
