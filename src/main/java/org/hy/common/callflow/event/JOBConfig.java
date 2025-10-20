package org.hy.common.callflow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.JobIntervalType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;
import org.hy.common.thread.Job;
import org.hy.common.thread.Jobs;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 定时元素：定时器的配置信息
 * 
 * 注：不建议节点配置共用，即使两个编排调用相同的执行方法也建议配置两个节点，使节点唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享节点配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-21
 * @version     v1.0
 *              v2.0  2025-06-09  添加：上下文已解释完成的占位符，使其支持面向对象的占位符。
 *              v3.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v4.0  2025-09-26  迁移：静态检查
 *              v5.0  2025-10-20  修正：先handleContext()解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
 */
public class JOBConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(JOBConfig.class);
    
    

    /** 任务组的XID。为空时从XJava对象池中获取首个 */
    private String                        jobsXID;
    
    /** 编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式 */
    private String                        callFlowXID;
    
    /** 间隔类型。可以是数值、上下文变量、XID标识 */
    private String                        intervalType;
    
    /** 间隔长度。可以是数值、上下文变量、XID标识 */
    private String                        intervalLen;
    
    /**
     * 允许执行的条件。
     * 
     *  表达式中，预定义占位符有（占位符不区分大小写）：
     *    :Y    表示年份
     *    :M    表示月份
     *    :D    表示天
     *    :H    表示小时(24小时制)
     *    :MI   表示分钟
     *    :S    表示秒
     *    :YMD  表示年月日，格式为YYYYMMDD 样式的整数类型。整数类型是为了方便比较
     */
    private String                        condition;
    
    /** 开始时间组。多个开始时间用分号分隔。多个开始时间对 "间隔类型:秒、分" 是无效的（只取最小时间为开始时间） */
    private List<Date>                    startTimes;
    
    /** 任务对象（仅内部使用） */
    private Job                           job;
    
    /** 执行定时元素时的运行时的向上下文（仅内部使用） */
    private Map<String ,Object>           executeContext;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-21
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public JOBConfig()
    {
        this(0L ,0L);
    }
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-21
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public JOBConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        // 子编排的XID不能为空
        if ( Help.isNull(this.getCallFlowXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callFlowXID is null.");
            return false;
        }
        
        // 间隔类型
        if ( Help.isNull(this.getIntervalType()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].intervalType is null.");
            return false;
        }
        
        if ( !this.getIntervalType().startsWith(DBSQL.$Placeholder) )
        {
            if ( JobIntervalType.get(this.getIntervalType()) == null )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].intervalType is invalid.");
                return false;
            }
        }
        
        // 间隔时长
        if ( Help.isNull(this.getIntervalLen()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].intervalLen is null.");
            return false;
        }
        
        if ( !this.getIntervalLen().startsWith(DBSQL.$Placeholder) )
        {
            if ( !Help.isNumber(this.getIntervalLen()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].intervalLen is invalid.");
                return false;
            }
            
            int v_IntervalLen = Integer.parseInt(this.getIntervalLen());
            if ( v_IntervalLen <= 0 )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].intervalLen <= 0.");
                return false;
            }
        }
        
        return true;
    }
    

    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-21
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Job.getValue();
    }
    
    
    /**
     * 获取：任务组的XID。为空时从XJava对象池中获取首个
     */
    public String getJobsXID()
    {
        return ValueHelp.standardRefID(this.jobsXID);
    }
    
    
    /**
     * 获取：编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    private String gatJobsXID()
    {
        return this.jobsXID;
    }

    
    /**
     * 设置：任务组的XID。为空时从XJava对象池中获取首个
     * 
     * @param i_JobsXID 任务组的XID。为空时从XJava对象池中获取首个
     */
    public void setJobsXID(String i_JobsXID)
    {
        this.jobsXID = ValueHelp.standardValueID(i_JobsXID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public String getCallFlowXID()
    {
        return ValueHelp.standardRefID(this.callFlowXID);
    }
    
    
    /**
     * 获取：编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    private String gatCallFlowXID()
    {
        return this.callFlowXID;
    }

    
    /**
     * 设置：子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     * 
     * @param i_CallFlowXID 子编排的XID（执行、条件逻辑、等待、计算、循环、嵌套、返回和并发元素等等的XID）。采用弱关联的方式
     */
    public void setCallFlowXID(String i_CallFlowXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.callFlowXID = ValueHelp.standardValueID(i_CallFlowXID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    /**
     * 获取：间隔类型。可以是数值、上下文变量、XID标识
     */
    public String getIntervalType()
    {
        return intervalType;
    }

    
    /**
     * 设置：间隔类型。可以是数值、上下文变量、XID标识
     * 
     * @param i_IntervalType 间隔类型。可以是数值、上下文变量、XID标识
     */
    public void setIntervalType(String i_IntervalType)
    {
        this.intervalType = i_IntervalType;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：间隔长度。可以是数值、上下文变量、XID标识
     */
    public String getIntervalLen()
    {
        return intervalLen;
    }

    
    /**
     * 设置：间隔长度。可以是数值、上下文变量、XID标识
     * 
     * @param i_IntervalLen 间隔长度。可以是数值、上下文变量、XID标识
     */
    public void setIntervalLen(String i_IntervalLen)
    {
        this.intervalLen = i_IntervalLen;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：允许执行的条件。
     * 
     *  表达式中，预定义占位符有（占位符不区分大小写）：
     *    :Y    表示年份
     *    :M    表示月份
     *    :D    表示天
     *    :H    表示小时(24小时制)
     *    :MI   表示分钟
     *    :S    表示秒
     *    :YMD  表示年月日，格式为YYYYMMDD 样式的整数类型。整数类型是为了方便比较
     */
    public String getCondition()
    {
        return condition;
    }

    
    /**
     * 设置：允许执行的条件。
     * 
     *  表达式中，预定义占位符有（占位符不区分大小写）：
     *    :Y    表示年份
     *    :M    表示月份
     *    :D    表示天
     *    :H    表示小时(24小时制)
     *    :MI   表示分钟
     *    :S    表示秒
     *    :YMD  表示年月日，格式为YYYYMMDD 样式的整数类型。整数类型是为了方便比较
     * 
     * @param i_Condition 允许执行的条件。
     */
    public void setCondition(String i_Condition)
    {
        this.condition = i_Condition;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：开始时间组。多个开始时间用分号分隔。多个开始时间对 "间隔类型:秒、分" 是无效的（只取最小时间为开始时间）
     */
    public List<Date> getStartTimes()
    {
        return startTimes;
    }

    
    /**
     * 设置：开始时间组。多个开始时间用分号分隔。多个开始时间对 "间隔类型:秒、分" 是无效的（只取最小时间为开始时间）
     * 
     * @param i_StartTimes 开始时间组。多个开始时间用分号分隔。多个开始时间对 "间隔类型:秒、分" 是无效的（只取最小时间为开始时间）
     */
    public void setStartTimes(List<Date> i_StartTimes)
    {
        this.startTimes = i_StartTimes;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 设置：开始时间组。多个开始时间用分号分隔。多个开始时间对 "间隔类型:秒、分" 是无效的（只取最小时间为开始时间）
     * 
     * @param i_StartTimesStr
     */
    public void setStartTime(String i_StartTimesStr)
    {
        if ( Help.isNull(i_StartTimesStr) )
        {
            return;
        }
        
        this.startTimes = new ArrayList<Date>();
        String [] v_STimeArr = StringHelp.replaceAll(i_StartTimesStr ,new String[]{"\t" ,"\n" ,"\r"} ,new String[]{""}).split(",");
        for (String v_STime : v_STimeArr)
        {
            this.startTimes.add(new Date(v_STime.trim()));
        }
        
        Help.toSort(this.startTimes);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 定时执行编排（定时元素的执行核心）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    public void executeJobForCallFlow()
    {
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        if ( !Help.isNull(this.executeContext) )
        {
            // 克隆运行时上下文，确保每次运行时，都是一样的参数，不受上次执行的影响
            v_Context.putAll(this.executeContext);
        }
        
        if ( !Help.isNull(this.context) )
        {
            try
            {
                String v_ContextValue = ValueHelp.replaceByContext(this.context ,this.contextPlaceholders ,v_Context);
                Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_ContextValue ,Map.class ,null ,v_Context);
                v_Context.putAll(v_ContextMap);
                v_ContextMap.clear();
                v_ContextMap = null;
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
                return;
            }
        }
        
        if ( Help.isNull(this.gatCallFlowXID()) )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s callFlowXID[" + Help.NVL(this.callFlowXID ,"?") + "] is null."));
            return;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.gatCallFlowXID());
        if ( v_CallObject == null )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.gatCallFlowXID() + "] is not find."));
            return;
        }
        
        // 执行对象不是编排元素
        if ( !MethodReflect.isExtendImplement(v_CallObject ,ExecuteElement.class) )
        {
            $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.gatCallFlowXID() + "] is not ExecuteElement."));
            return;
        }
        
        ExecuteResult v_Result = CallFlow.execute((ExecuteElement) v_CallObject ,v_Context);
        if ( v_Result.isSuccess() )
        {
            $Logger.info("Success：" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment));
        }
        else
        {
            $Logger.error("Failed：" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "。Error XID = " + v_Result.getExecuteXID() ,v_Result.getException());
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        $Logger.info("\n" + CallFlow.getHelpLog().logs(v_FirstResult));
        
        v_Context.clear();
        v_Context = null;
    }


    /**
     * 定时元素的启动或开启。将原先 “执行” 的含义改变了
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-21
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        Exception     v_ContextEr = this.handleContext(io_Context);  // 先解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        if ( v_ContextEr != null )
        {
            v_Result.setException(v_ContextEr);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        try
        {
            if ( Help.isNull(this.xid) )
            {
                v_Result.setException(new NullPointerException("JOBConfig[" + Help.NVL(this.comment) + "]'s XID is null."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            String v_IntervalType = (String) ValueHelp.getValue(this.getIntervalType() ,String.class ,null ,io_Context);
            JobIntervalType v_JobIntervalType = JobIntervalType.get(v_IntervalType);
            if ( v_JobIntervalType == null )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s intervalType[" + Help.NVL(this.intervalType ,"?") + "] is null."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            Integer v_IntervalLen = (Integer) ValueHelp.getValue(this.getIntervalLen() ,Integer.class ,null ,io_Context);
            if ( v_IntervalLen == null )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s intervalLen[" + Help.NVL(this.intervalLen ,"?") + "] is null."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            if ( Help.isNull(this.gatCallFlowXID()) )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s callFlowXID[" + Help.NVL(this.callFlowXID ,"?") + "] is null."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            // 获取执行对象
            Object v_CallObject = XJava.getObject(this.gatCallFlowXID());
            if ( v_CallObject == null )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.gatCallFlowXID() + "] is not find."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            // 执行对象不是编排元素
            if ( !MethodReflect.isExtendImplement(v_CallObject ,ExecuteElement.class) )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallFlowXID[" + this.gatCallFlowXID() + "] is not ExecuteElement."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            List<Date> v_StartTimes = this.getStartTimes();
            if ( Help.isNull(v_StartTimes) )
            {
                v_StartTimes = new ArrayList<Date>();
                v_StartTimes.add(Date.getNowTime().getFirstTimeOfDay());
            }
            
            Jobs v_Jobs = null;
            if ( Help.isNull(this.gatJobsXID()) )
            {
                v_Jobs = XJava.getObject(Jobs.class ,false);
            }
            else
            {
                Object v_JobsObject = XJava.getObject(this.gatJobsXID());
                if ( !(v_JobsObject instanceof Jobs) )
                {
                    v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s Jobs[" + this.gatCallFlowXID() + "] is not Jobs."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                v_Jobs = (Jobs) v_JobsObject;
            }
            
            if ( v_Jobs == null )
            {
                v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s Jobs[" + Help.NVL(this.gatJobsXID() ,"?") + "] is null."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            this.executeContext = new HashMap<String ,Object>();
            if ( !Help.isNull(io_Context) )
            {
                for (Map.Entry<String ,Object> v_Item : io_Context.entrySet())
                {
                    if ( CallFlow.isSystemXID(v_Item.getKey()) )
                    {
                        continue;
                    }
                    this.executeContext.put(v_Item.getKey() ,v_Item.getValue());
                }
            }
            
            if ( this.job != null )
            {
                this.delJobByJobs(v_Jobs ,this.job.getCode());
            }
            
            this.job = new Job();
            this.job.setXJavaID(     "JOB_" + this.getXid());
            this.job.setCode   (     "JOB_" + this.getXid());
            this.job.setName   (     this.getComment());
            this.job.setComment(     this.getComment());
            this.job.setIntervalType(v_JobIntervalType.getInterval());
            this.job.setIntervalLen( v_IntervalLen);
            this.job.setStartTimes(  v_StartTimes);
            this.job.setCondition(   this.getCondition());
            this.job.setXid(         this.getXid());
            this.job.setMethodName(  "executeJobForCallFlow");
            
            XJava.putObject(this.getXid() ,this);
            XJava.putObject("JOB_" + this.getXid() ,this.job);
            v_Jobs.addJob(this.job);
            
            v_Result.setResult(true);
            this.refreshReturn(io_Context ,v_Result.getResult());
            this.refreshStatus(io_Context ,v_Result.getStatus());
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result;
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
    }
    
    
    /**
     * 删除定时任务池中的任务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-09-13
     * @version     v1.0
     *
     * @param i_Jobs   定时任务池
     * @param i_Code   要删除任务编号
     */
    private void delJobByJobs(Jobs i_Jobs ,String i_Code)
    {
        Iterator<Job> v_JobList = i_Jobs.getJobs();
        while ( v_JobList.hasNext() )
        {
            Job v_Item = v_JobList.next();
            if ( v_Item.getCode().equals(i_Code) )
            {
                i_Jobs.delJob(v_Item);
                break;
            }
        }
    }
    

    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *              v2.0  2025-08-15  添加：导出类型
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @param i_ExportType   导出类型
     * @return
     */
    @Override
    public String toXml(int i_Level ,String i_SuperTreeID ,ExportType i_ExportType)
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
        
        StringBuilder v_Xml      = new StringBuilder();
        String        v_Level1   = "    ";
        String        v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName    = ElementType.Job.getXmlName();
        String        v_NewSpace = "\n" + v_LevelN + v_Level1;
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            if ( ExportType.UI.equals(i_ExportType) )
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginThis(v_XName ,this.getXJavaID()));
            }
            else
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(  v_XName ,this.getXJavaID()));
            }
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level ,i_ExportType));
        
        if ( !ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.gatJobsXID()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("jobsXID" ,this.getJobsXID()));
            }
            if ( !Help.isNull(this.gatCallFlowXID()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("callFlowXID" ,this.getCallFlowXID()));
            }
            if ( !Help.isNull(this.intervalType) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("intervalType" ,this.intervalType));
            }
            if ( !Help.isNull(this.intervalLen) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("intervalLen" ,this.intervalLen));
            }
            if ( !Help.isNull(this.condition) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("condition" ,this.condition));
            }
            if ( !Help.isNull(this.startTimes) )
            {
                for (Date v_StartTime : this.startTimes)
                {
                    v_Xml.append(v_NewSpace).append(IToXml.toValue("startTime" ,v_StartTime));
                }
            }
            
            if ( !Help.isNull(this.returnID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("returnID" ,this.returnID));
            }
            if ( !Help.isNull(this.statusID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("statusID" ,this.statusID));
            }
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toBegin("route"));
                
                // 成功路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,RouteType.Error.getXmlName()   ,i_Level ,v_TreeID ,i_ExportType);
                
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("route"));
            }
            
            this.toXmlExecute(v_Xml ,v_NewSpace);
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        // 编排流图时，提升路由项的层次，同时独立输出每个路由项
        if ( ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                // 成功路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
            }
        }
        
        return v_Xml.toString();
    }
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        JobIntervalType v_JobIntervalType = null;
        Integer         v_IntervalLen     = null;
        
        try
        {
            String v_IntervalType = (String) ValueHelp.getValue(this.getIntervalType() ,String.class ,null ,i_Context);
            v_JobIntervalType = JobIntervalType.get(v_IntervalType);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        try
        {
            v_IntervalLen = (Integer) ValueHelp.getValue(this.getIntervalLen() ,Integer.class ,null ,i_Context);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        if ( v_JobIntervalType == null && v_IntervalLen == null )
        {
            v_Builder.append("未知间隔，未知周期");
        }
        else if ( v_JobIntervalType == null )
        {
            v_Builder.append("间隔").append(v_IntervalLen).append("，未知周期");
        }
        else if ( v_IntervalLen == null )
        {
            v_Builder.append("未知间隔，").append(v_JobIntervalType.getComment());
        }
        else if ( v_JobIntervalType.equals(JobIntervalType.Manual) )
        {
            v_Builder.append(v_JobIntervalType.getComment());
        }
        else
        {
            v_Builder.append("间隔 ").append(v_IntervalLen).append(" ").append(v_JobIntervalType.getComment()).append(" 周期执行");
        }
        
        if ( Help.isNull(this.gatCallFlowXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getCallFlowXID());
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( Help.isNull(this.intervalType) && Help.isNull(this.intervalLen) )
        {
            v_Builder.append("未知间隔，未知周期");
        }
        else if ( Help.isNull(this.intervalType) )
        {
            v_Builder.append("间隔").append(this.intervalLen).append("，未知周期");
        }
        else if ( Help.isNull(this.intervalLen) )
        {
            v_Builder.append("未知间隔，").append(this.intervalType);
        }
        else
        {
            v_Builder.append("间隔 ").append(this.intervalLen).append(" ").append(this.intervalType).append(" 周期执行");
        }
        
        if ( Help.isNull(this.gatCallFlowXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getCallFlowXID());
        }
        
        return v_Builder.toString();
    }
    

    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new JOBConfig();
    }
    

    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        JOBConfig v_Clone = new JOBConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.jobsXID      = this.jobsXID;
        v_Clone.callFlowXID  = this.callFlowXID; 
        v_Clone.intervalType = this.intervalType;
        v_Clone.intervalLen  = this.intervalLen;
        v_Clone.condition    = this.condition;
        
        if ( !Help.isNull(this.startTimes) )
        {
            for (Date v_Time : this.startTimes)
            {
                v_Clone.setStartTime(v_Time.getFull());
            }
        }
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-05-06
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone JOBConfig xid is null.");
        }
        
        JOBConfig v_Clone = (JOBConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.jobsXID      = this.jobsXID;
        v_Clone.callFlowXID  = this.callFlowXID; 
        v_Clone.intervalType = this.intervalType;
        v_Clone.intervalLen  = this.intervalLen;
        v_Clone.condition    = this.condition;
        
        if ( !Help.isNull(this.startTimes) )
        {
            for (Date v_Time : this.startTimes)
            {
                v_Clone.setStartTime(v_Time.getFull());
            }
        }
    }
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-22
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone JOBConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        JOBConfig                   v_Clone      = new JOBConfig();
        
        if ( v_Version.booleanValue() )
        {
            this.clone(v_Clone ,v_Version.getParamStr() ,XIDVersion + (v_Version.getParamInt() + 1) ,""         ,v_XIDObjects);
        }
        else
        {
            this.clone(v_Clone ,""                      ,""                                         ,XIDVersion ,v_XIDObjects);
        }
        
        v_XIDObjects.clear();
        v_XIDObjects = null;
        return v_Clone;
    }
    
}
