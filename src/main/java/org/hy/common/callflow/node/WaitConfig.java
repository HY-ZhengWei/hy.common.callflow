package org.hy.common.callflow.node;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.log.Logger;





/**
 * 等待配置信息
 * 
 * 注：不建议等待配置共用，即使两个编排调用相同的等待配置也建议配置两个等待配置，使等待配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享等待配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public class WaitConfig extends ExecuteElement
{
    
    private static final Logger $Logger = new Logger(WaitConfig.class);
    
    

    /** 等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String waitTime;
    
    
    
    public WaitConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public WaitConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.waitTime = "0";
    }

    
    
    /**
     * 获取：等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getWaitTime()
    {
        return waitTime;
    }


    
    /**
     * 设置：等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_WaitTime 等待时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setWaitTime(String i_WaitTime)
    {
        if ( Help.isNull(i_WaitTime) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s WaitTime is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        if ( Help.isNumber(i_WaitTime) )
        {
            Long v_WaitTime = Long.valueOf(i_WaitTime);
            if ( v_WaitTime < 0L )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s WaitTime Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.waitTime = i_WaitTime.trim();
        }
        else
        {
            this.waitTime = i_WaitTime.trim();
            if ( !this.waitTime.startsWith(DBSQL.$Placeholder) )
            {
                this.waitTime = DBSQL.$Placeholder + this.waitTime;
            }
        }
    }



    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        
        try
        {
            Long v_WaitTime = null;
            if ( Help.isNumber(this.waitTime) )
            {
                v_WaitTime = Long.valueOf(this.waitTime);
            }
            else
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,0L ,io_Context);
            }
            
            Thread.sleep(v_WaitTime);
            
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result.setResult(true);
        }
        catch (Exception exce)
        {
            return v_Result.setException(exce);
        }
    }

    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    public String toXml(int i_Level ,String i_SuperTreeID)
    {
        String v_TreeID = this.getTreeID(i_SuperTreeID);
        if ( this.getTreeIDs().size() >= 2 )
        {
            String v_MinTreeID = this.getMinTreeID();
            if ( !v_TreeID.equals(v_MinTreeID) )
            {
                // 不等于最小的树ID，不生成Xml内容。防止重复生成
                return "";
            }
        }
        
        StringBuilder v_Xml    = new StringBuilder();
        String        v_Level1 = "    ";
        String        v_LevelN = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName  = "xwait";
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.waitTime) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("waitTime" ,this.waitTime));
        }
        if ( !Help.isNull(this.route.getSucceeds()) 
          || !Help.isNull(this.route.getExceptions()) )
        {
            int v_MaxLpad = 0;
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                v_MaxLpad = 7;
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 成功路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (IExecute v_Item : this.route.getSucceeds())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("succeed" ,v_Item.getXJavaID()));
                    }
                    else
                    {
                        v_Xml.append(v_Item.toXml(i_Level + 1 ,v_TreeID));
                    }
                }
            }
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (IExecute v_Item : this.route.getExceptions())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("error" ,v_Item.getXJavaID() ,v_MaxLpad - 5));
                    }
                    else
                    {
                        v_Xml.append(v_Item.toXml(i_Level + 1 ,v_TreeID));
                    }
                }
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toEnd("route"));
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        return v_Xml.toString();
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("Wait ");
        if ( !Help.isNumber(this.waitTime) )
        {
            v_Builder.append(this.waitTime).append("=");
            
            Long v_WaitTime = null;
            try
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,null ,i_Context);
                if ( v_WaitTime == null )
                {
                    v_Builder.append("?");
                }
                else
                {
                    v_Builder.append(v_WaitTime);
                }
            }
            catch (Exception exce)
            {
                v_Builder.append("ERROR");
                $Logger.error(exce);
            }
        }
        else
        {
            v_Builder.append(this.waitTime);
        }
        v_Builder.append(" ms");
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("Wait ").append(this.waitTime).append(" ms");
        
        return v_Builder.toString();
    }
    
}
