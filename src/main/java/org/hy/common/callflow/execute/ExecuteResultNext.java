package org.hy.common.callflow.execute;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.Queue;
import org.hy.common.Queue.QueueType;





/**
 * 一步一步的获取执行结果 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-17
 * @version     v1.0
 */
public class ExecuteResultNext
{
    
    /** 整个编排的首个元素的执行结果 */
    private ExecuteResult    firstResult;
    
    /** 前一个。即上次next()方法返回的值 */
    private ExecuteResult    previous;
    
    /** 回溯栈（仅内部使用） */
    private Queue<QueueItem> queue;
    
    
    
    public ExecuteResultNext(ExecuteResult i_FirstResult)
    {
        this.firstResult = i_FirstResult;
        this.queue       = new Queue<QueueItem>(QueueType.$First_IN_Last_OUT);
    }
    
    
    
    /**
     * 获取下一个执行结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-17
     * @version     v1.0
     *
     * @return
     */
    public synchronized ExecuteResult next()
    {
        if ( this.previous == null )
        {
            this.previous = this.firstResult;
            return this.previous;
        }
        
        List<ExecuteResult> v_Nexts = this.previous.getNexts();
        // 当没有其后的执行结果时，尝试从栈里取。即回溯
        if ( Help.isNull(v_Nexts) )
        {
            while ( this.queue.size() >= 1 )
            {
                QueueItem v_QueueItem = this.queue.get();
                this.previous = v_QueueItem.next();
                if ( this.previous != null )
                {
                    // “我”后面，还有父项执行结果的多个分支时，将“我”的父项执行结果压入到栈里去
                    if ( v_QueueItem.getFather().getNexts().size() > v_QueueItem.getFatherIndex() + 1 + 1 )
                    {
                        this.queue.put(new QueueItem(v_QueueItem.getFather() ,v_QueueItem.getFatherIndex() + 1));
                    }
                    return this.previous;
                }
            }
            
            return null;
        }
        // 当其后仅只有一个时，直接下穿，并不压入到栈里去
        else if ( v_Nexts.size() == 1 )
        {
            this.previous = v_Nexts.get(0);
            return this.previous;
        }
        // 当其后有多个时，须将“我”的父项执行结果先压入到栈里去
        else
        {
            this.queue.put(new QueueItem(this.previous ,0));
            this.previous = this.previous.getNexts().get(0);
            return this.previous;
        }
    }
    
    
    
    
    
    /**
     * 栈中的对象信息
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-17
     * @version     v1.0
     */
    class QueueItem
    {
        
        /** 父项执行结果 */
        private ExecuteResult father;
        
        /** “我”在父项执行结果中的位置。下标从0开始 */
        private int           fatherIndex;
        
        
        
        public QueueItem(ExecuteResult i_Father ,int i_FatherIndex)
        {
            this.father      = i_Father;
            this.fatherIndex = i_FatherIndex;
        }
        
        
        public ExecuteResult next()
        {
            if ( this.father.getNexts().size() > this.fatherIndex + 1 )
            {
                return this.father.getNexts().get(this.fatherIndex + 1);
            }
            else
            {
                return null;
            }
        }

        
        /**
         * 获取：父项执行结果
         */
        public ExecuteResult getFather()
        {
            return father;
        }

        
        /**
         * 获取：“我”在父项执行结果中的位置。下标从0开始
         */
        public int getFatherIndex()
        {
            return fatherIndex;
        }
        
    }
    
}
