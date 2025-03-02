package org.hy.common.callflow.nesting;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ExecuteStatus;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.ifelse.Condition;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;





/**
 * 嵌套，子编排的配置信息。
 * 
 * 优点：嵌套可以实现递归。
 * 优点：嵌套使编排流程可以共用。嵌套配置元素不建议共用。
 * 
 * 注：不建议嵌套配置共用，即使两个编排调用相同的执行方法也建议配置两个嵌套配置，使嵌套配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享嵌套配置下，无法做到升级时百分百的正确。
 *    原因2是在共享嵌套配置时，统计方面也无法独立区分出来。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-28
 * @version     v1.0
 */
public class NestingConfig extends ExecuteElement
{

    /** 子编排的XID（节点或条件逻辑的XID）。采用弱关联的方式 */
    private String callFlowXID;
    
    /** 执行状态定义的变量ID */
    private String statusID;
    
    
    
    public NestingConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public NestingConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }



    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-28
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime    = this.request();
        ExecuteResult v_NestingBegin = new ExecuteResult(this.getTreeID(i_SuperTreeID)     ,this.xid ,this.toString(io_Context) + " BEGIN ");
        ExecuteResult v_NestingEnd   = new ExecuteResult(v_NestingBegin.getExecuteTreeID() ,this.xid ,this.toString(io_Context) + " END ");
        this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
        
        if ( Help.isNull(this.callFlowXID) )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID is null."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callFlowXID);
        if ( v_CallObject == null )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.callFlowXID + "] is not find."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        // 禁止嵌套直接套嵌套，做无用功
        if ( !(v_CallObject instanceof NodeConfig)
          && !(v_CallObject instanceof Condition) )
        {
            v_NestingBegin.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.callFlowXID + "] is not NodeConfig or Condition."));
            this.refreshStatus(io_Context ,v_NestingBegin.getStatus());
            return v_NestingBegin;
        }
        
        ExecuteResult v_SuperFirstResult = CallFlow.getFirstResult(io_Context);  // 备份父级首个执行结果 
        ExecuteResult v_SuperLastResult  = CallFlow.getLastResult( io_Context);  // 备份父级最后执行结果 
        
        // 将父级的最后结果与子级的首个结果相关联
        if ( v_SuperLastResult != null )
        {
            v_NestingBegin.setPrevious(v_SuperLastResult);
        }
        else
        {
            // 嵌套是主编排的首个执行对象
            v_NestingBegin.setPrevious(null);
        }
        v_NestingBegin.setResult(true);
        
        // 最后执行的嵌套对象（自己），为已编排生成树ID用
        io_Context.put(CallFlow.$LastNestingBeginResult ,v_NestingBegin);
        
        ExecuteElement v_CallFlow = (ExecuteElement) v_CallObject;
        ExecuteResult  v_ExceRet  = CallFlow.execute(v_CallFlow ,io_Context ,CallFlow.getExecuteEvent(io_Context));
        
        this.refreshStatus(io_Context ,v_ExceRet.getStatus());             // 子编排的状态就是我的状态
        
        // 还原：原始父级的首个执行对象的结果
        if ( v_SuperFirstResult != null )
        {
            io_Context.put(CallFlow.$FirstExecuteResult ,v_SuperFirstResult);
        }
        
        if ( !CallFlow.getExecuteIsError(io_Context) )
        {
            ExecuteResult v_LastResult = CallFlow.getLastResult(io_Context);
            v_LastResult.addNext(v_NestingEnd);                            // 子编排完成后的下一步关联到本层编排的嵌套配置
            v_NestingEnd.setPrevious(v_LastResult);                        // 关联最后执行对象的结果
            v_NestingEnd.setResult(v_ExceRet.getResult());                 // 子编排的结果就是我的结果
            this.refreshReturn(io_Context ,v_ExceRet.getResult());         // 这里用 returnID 返回是的执行结果的原始信息，而不是ExecuteResult对象。
        }
        else
        {
            // 不能改写为异常的元素的执行XID和树ID。嵌套就是一个相对独立的元素
            // 如果将子嵌套的执行XID和树ID拿到本编排流程中，是无法定位
            // 子编排错了，就是本编排流程的嵌套对象错了。
            ExecuteResult v_ErrorResult = CallFlow.getErrorResult(io_Context);
            v_ErrorResult.addNext(v_NestingEnd);
            v_NestingEnd.setPrevious( v_ErrorResult);
            v_NestingEnd.setExecuteLogic(v_ErrorResult.getExecuteLogic());
            v_NestingEnd.setException(   v_ErrorResult.getException());
        }
        
        this.success(Date.getTimeNano() - v_BeginTime);
        return v_NestingBegin;
    }
    
    
    
    /**
     * 刷新返回值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Return    返回值
     */
    private void refreshReturn(Map<String ,Object> io_Context ,Object i_Return)
    {
        if ( !Help.isNull(this.returnID) && io_Context != null )
        {
            io_Context.put(this.returnID ,i_Return);
        }
    }
    
    
    /**
     * 刷新执行状态
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Status    执行状态
     */
    private void refreshStatus(Map<String ,Object> io_Context ,ExecuteStatus i_Status)
    {
        if ( !Help.isNull(this.statusID) && io_Context != null )
        {
            io_Context.put(this.statusID ,i_Status.getValue());
        }
    }
    
    
    
    /**
     * 获取：子编排的XID（节点或条件逻辑的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return callFlowXID;
    }


    
    /**
     * 设置：子编排的XID（节点或条件逻辑的XID）。采用弱关联的方式
     * 
     * @param i_CallFlowXID 子编排的XID（节点或条件逻辑的XID）。采用弱关联的方式
     */
    public void setCallFlowXID(String i_CallFlowXID)
    {
        this.callFlowXID = i_CallFlowXID;
    }

    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    @Override
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
        String        v_XName  = "xnesting";
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.callFlowXID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("callFlowXID" ,this.callFlowXID));
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
        }
        if ( !Help.isNull(this.statusID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("statusID" ,this.statusID));
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
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        v_Builder.append(DBSQL.$Placeholder);
        if ( !Help.isNull(this.callFlowXID) )
        {
            v_Builder.append(this.callFlowXID);
            if ( XJava.getObject(this.callFlowXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        v_Builder.append("execute(...)");
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        v_Builder.append(DBSQL.$Placeholder);
        if ( !Help.isNull(this.callFlowXID) )
        {
            v_Builder.append(this.callFlowXID);
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        v_Builder.append("execute(...)");
        
        return v_Builder.toString();
    }
    
}
