package org.hy.common.callflow.milvus;

import java.util.List;
import java.util.Map;

import org.hy.common.Busway;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionBusway;
import org.hy.common.XJavaID;
import org.hy.common.callflow.enums.XMilvusType;
import org.hy.common.db.DBConditions;
import org.hy.common.milvus.MilvusContent;
import org.hy.common.milvus.MilvusData;
import org.hy.common.milvus.MilvusHelp;
import org.hy.common.milvus.MilvusResult;
import org.hy.common.xml.XJava;
import org.hy.common.xml.XSQLTrigger;
import org.hy.common.xml.log.Logger;





/**
 * 向量库操作对象
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-14
 * @version     v1.0
 */
public class XMilvus implements Comparable<XMilvus> ,XJavaID ,Cloneable
{
    
    private static final Logger                                   $Logger              = new Logger(XMilvus.class ,true);
    
    /** 每个XMilvus对象的执行日志。默认每个XMilvus对象只保留1000条日志。按 getObjectID() 分区 */
    public  static final TablePartitionBusway<String ,XMilvusLog> $MilvusBuswayTP      = new TablePartitionBusway<String ,XMilvusLog>();
    
    /** 所有向量库执行日志，有一定的执行顺序。默认只保留1000条执行过的Content语句 */
    public  static final Busway<XMilvusLog>                       $MilvusBusway        = new Busway<XMilvusLog>(1000);
    
    /** 向量库执行异常的日志。默认只保留1000条执行异常的Content语句 */
    public  static final Busway<XMilvusLog>                       $MilvusBuswayError   = new Busway<XMilvusLog>(1000);
    
    
    
    static
    {
        $MilvusBuswayTP.setDefaultWayLength(100);
        XJava.putObject("$MilvusBuswayTP"    ,$MilvusBuswayTP);
        XJava.putObject("$MilvusBusway"      ,$MilvusBusway);
        XJava.putObject("$MilvusBuswayError" ,$MilvusBuswayError);
    }
    
    
    
    /** 唯一标示，主用于对比等操作 */
    private String                         uuid;
    
    /** XJava池中对象的ID标识 */
    private String                         xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String                         comment;
    
    /** 向量库操作辅助类 */
    private MilvusHelp                     milvus;
    
    /** 向量库操作类型 */
    private XMilvusType                    type;
    
    /** 表名称。向量库中Collection的名称。同时支持：库名称.表名称 */
    private String                         collection;
    
    /** 分区名称 */
    private String                         partition;
    
    /** 向量查询时返回的结果集数量 */
    private Integer                        topK;
    
    /** 向量库占位符的信息 */
    private MilvusContent                  content;
    
    /** 解释Xml文件，分析数据库结果集转化为Java实例对象 */
    private MilvusResult                   result;
    
    /**
     * 创建对象。如创建表。
     * 
     * 此属性为动作方法，即this.setCreate(...)时，将尝试创建对象(当对象不存在时)。
     * 也因为是动作方法，所以在设置本属性前milvus、content它两属性应当已设置OK。
     * 
     * 实现服务启动时检查并创建数据库对象(如数据库表)，已存在不创建。
     */
    private String                         create;
    
    /** 请求数据库的次数 */
    private long                           requestCount;
    
    /** 请求成功，并成功返回次数 */
    private long                           successCount;
    
    /**
     * 请求成功，并成功返回的累计用时时长。
     * 用的是Double，而不是long，因为在批量执行时。为了精度，会出现小数
     */
    private double                         successTimeLen;
    
    /**
     * 请求成功，并成功返回的最大用时时长。
     */
    private double                         successTimeLenMax;
    
    /** 读写行数。查询结果的行数或写入数据库的记录数 */
    private long                           ioRowCount;
    
    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     */
    private Date                           executeTime;
    
    
    
    public XMilvus()
    {
        this.uuid               = StringHelp.getUUID9n();
        this.requestCount       = 0L;
        this.successCount       = 0L;
        this.successTimeLen     = 0D;
        this.successTimeLenMax  = 0D;
        this.ioRowCount         = 0L;
        this.executeTime        = null;
        this.comment            = null;
        this.content            = new MilvusContent();
        this.result             = new MilvusResult();
    }
    
    
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        XMilvus v_Clone = new XMilvus();
        
        v_Clone.setType(                 this.getType());
        v_Clone.setCollection(           this.getCollection());
        v_Clone.setPartition(            this.getPartition());
        v_Clone.setTopK(                 this.getTopK());
        v_Clone.setContent(              this.getContent().getContentText());
        v_Clone.setResult((MilvusResult) this.getResult().clone());
        v_Clone.setComment(              this.getComment());
        
        // v_Clone.setXJavaID();                                       // 禁止深度克隆
        // v_Clone.setCreate();                                        // 禁止深度克隆
        
        return v_Clone;
    }
    
    
    
    /**
     * 重置统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     */
    public synchronized void reset()
    {
        this.requestCount      = 0L;
        this.successCount      = 0L;
        this.successTimeLen    = 0D;
        this.successTimeLenMax = 0D;
        this.ioRowCount        = 0L;
        this.executeTime       = null;
    }
    
    
    
    /**
     * 数据请求时的统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @return
     */
    protected synchronized Date request()
    {
        ++this.requestCount;
        this.executeTime = new Date();
        return this.executeTime;
    }
    
    
    
    /**
     * 数据处理成功时的统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @param i_ExecuteTime  执行时间
     * @param i_TimeLen      用时时长（单位：毫秒）
     * @param i_SumCount     成功次数
     * @param i_IORowCount   读写行数
     */
    protected synchronized void success(Date i_ExecuteTime ,double i_TimeLen ,int i_SumCount ,long i_IORowCount)
    {
        this.requestCount     += Help.max(i_SumCount ,1) - 1;
        this.successCount     += Help.max(i_SumCount ,1);
        this.successTimeLen   += i_TimeLen;
        this.successTimeLenMax = Math.max(this.successTimeLenMax ,i_TimeLen);
        this.executeTime       = i_ExecuteTime;
        this.ioRowCount       += i_IORowCount;
    }
    
    
    
    /**
     * 获取：请求数据库的次数
     */
    public long getRequestCount()
    {
        return requestCount;
    }


    
    /**
     * 获取：请求成功，并成功返回次数
     */
    public long getSuccessCount()
    {
        return successCount;
    }



    /**
     * 获取：请求成功，并成功返回的累计用时时长。
     * 用的是Double，而不是long，因为在批量执行时。为了精度，会出现小数
     */
    public double getSuccessTimeLen()
    {
        return successTimeLen;
    }
    
    
    
    /**
     * 获取：请求成功，并成功返回的最大用时时长。
     */
    public double getSuccessTimeLenMax()
    {
        return successTimeLenMax;
    }



    /**
     * 获取：读写行数。查询结果的行数或写入数据库的记录数
     */
    public long getIoRowCount()
    {
        return ioRowCount;
    }



    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @return
     */
    public Date getExecuteTime()
    {
        return this.executeTime;
    }
    
    
    
    /**
     * 检查数据库占位符Content的对象是否为null。同时统计异常数据。
     * 
     * 此方法从各个数据库操作方法中提炼而来。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     */
    protected void checkContent()
    {
        if ( this.content == null )
        {
            NullPointerException v_Exce = new NullPointerException("Content is null of XMilvus[" + Help.NVL(this.getXJavaID() ,this.getObjectID()) + "].");
            
            this.request();
            erroring("" ,v_Exce ,this);
            throw v_Exce;
        }
    }
    
    
    
    /**
     * 是否执行触发器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @param i_IsError  主XMilvus在执行时是否异常？
     * @return
     */
    protected boolean isTriggers(boolean i_IsError)
    {
        // Nothing. 待未来有需要时再实现
        return false;
    }
    
    
    
    /**
     * 执行向量库异常时的统一处理方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @param i_Content  执行语句
     * @param i_Exce     异常信息
     * @param i_XMilvus  向量库操作对象
     */
    protected static void erroring(String i_Content ,Exception i_Exce ,XMilvus i_XMilvus)
    {
        XMilvusLog v_XMilvusLog = new XMilvusLog(i_Content ,i_Exce ,i_XMilvus.getObjectID());

        $MilvusBuswayTP   .putRow(i_XMilvus.getObjectID() ,v_XMilvusLog);
        $MilvusBusway     .put(v_XMilvusLog);
        $MilvusBuswayError.put(v_XMilvusLog);
        
        $Logger.error("\n-- Error time:       " + Date.getNowTime().getFull()
                    + "\n-- Error XMilvus ID: " + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID())
                    + "\n-- Error Content:    " + i_Content ,i_Exce);
        
        i_Exce.printStackTrace();
    }
    
    
    
    /**
     * 获取：可自行定制的XMilvus异常处理机制
     */
    public XMilvusError getError()
    {
        // Nothing. 待未来有需要时再实现
        return null;
    }
    
    
    
    /**
     * 执行之后的日志。（在Content语法成功执行之后，在this.result.getDatas(...)方法之前执行）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-17
     * @version     v1.0
     *
     * @param i_Content
     */
    protected void log(String i_Content)
    {
        XMilvusLog v_XMilvusLog = new XMilvusLog(i_Content ,null ,this.getObjectID());
        
        $MilvusBuswayTP.putRow(this.getObjectID() ,v_XMilvusLog);
        $MilvusBusway  .put(v_XMilvusLog);
        
        StringBuilder v_Buffer = new StringBuilder();
        if ( !Help.isNull(this.xid) )
        {
            v_Buffer.append(this.xid);
            
            if ( !Help.isNull(this.comment) )
            {
                v_Buffer.append(" : ").append(this.comment).append("\n");
            }
            else
            {
                v_Buffer.append("\n");
            }
        }
        else
        {
            if ( !Help.isNull(this.comment) )
            {
                v_Buffer.append(this.comment).append("\n");
            }
        }
        
        v_Buffer.append(i_Content);
        $Logger.debug(v_Buffer.toString());
    }
    
    
    
    /**
     * 触发执行Content前的规则引擎。针对Content参数、占位符的规则引擎
     * 
     * 优先级：触发的优先级高于“XMilvus条件”
     * 
     * 注：无入参的不触发执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_XMilvusParams
     */
    protected void fireBeforeRule(Object i_XMilvusParams)
    {
        // Nothing. 待未来有需要时再实现
    }
    
    
    
    /**
     * 触发执行后的规则引擎
     * 
     * 优先级：触发的优先级高于“XMilvus应用级触发器”
     * 
     * 注1：无入参的不触发执行。
     * 注2：只用于查询返回的XMilvus。
     * 注3：getCount() 等简单数据结构的也不触发执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_MilvusData
     */
    protected void fireAfterRule(MilvusData i_MilvusData)
    {
        // Nothing. 待未来有需要时再实现
    }
    
    
    
    /**
     * 获取：XMilvus的触发器
     */
    public XSQLTrigger getTrigger()
    {
        // Nothing. 待未来有需要时再实现
        return null;
    }
    
    
    
    /**
     * 触发源执行前，生成触发器额外附加参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_ExecuteType  触发源的执行方式
     * @return
     */
    protected Map<String ,Object> executeBeforeForTrigger(String i_ExecuteType ,Object i_XSQLParam)
    {
        // Nothing. 待未来有需要时再实现
        return null;
    }
    
    
    
    /**
     * 触发源执行前，生成触发器额外附加参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_ExecuteType  触发源的执行方式
     * @param i_XSQLParam    触发源的执行参数（禁止修改、添加、删除任务元素）
     * @return
     */
    protected Map<String ,Object> executeBeforeForTrigger(String i_ExecuteType ,final Map<String ,?> i_XSQLParam)
    {
        // Nothing. 待未来有需要时再实现
        return null;
    }
    
    
    
    /**
     * 触发源执行后，生成触发器额外附加参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param io_TriggerParams  触发器额外附加参数
     * @param i_IORowCount      读写行数
     * @param i_ErrorInfo       异常信息。为空和空字符串均表示无异常
     * @return
     */
    protected Map<String ,Object> executeAfterForTrigger(Map<String ,Object> io_TriggerParams ,long i_IORowCount ,String i_ErrorInfo)
    {
        // Nothing. 待未来有需要时再实现
        return null;
    }
    
    
    
    public String getObjectID()
    {
        return this.uuid;
    }
    
    

    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    @Override
    public void setXJavaID(String i_XJavaID)
    {
        this.xid = i_XJavaID;
    }
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    @Override
    public String getXJavaID()
    {
        return this.xid;
    }

    
    
    /**
     * 获取：注释。可用于日志的输出等帮助性的信息
     */
    @Override
    public String getComment()
    {
        return this.comment;
    }
    
    
    
    /**
     * 设置：注释。可用于日志的输出等帮助性的信息
     * 
     * @param comment
     */
    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    
    
    /**
     * 获取：向量库操作辅助类
     */
    public MilvusHelp getMilvus()
    {
        return milvus;
    }


    
    /**
     * 设置：向量库操作辅助类
     * 
     * @param i_Milvus 向量库操作辅助类
     */
    public void setMilvus(MilvusHelp i_Milvus)
    {
        this.milvus = i_Milvus;
    }
    
    
    
    /**
     * 获取：向量库操作类型
     */
    public XMilvusType getType()
    {
        return type;
    }


    
    /**
     * 设置：向量库操作类型
     * 
     * @param i_Type 向量库操作类型
     */
    public void setType(XMilvusType i_Type)
    {
        if ( i_Type == null )
        {
            throw new NullPointerException("XMilvus.type is null");
        }
        else if ( XMilvusType.Auto.equals(i_Type) )
        {
            throw new NullPointerException("XMilvus.type is not allow Auto");
        }
        else
        {
            this.type = i_Type;
        }
    }

    
    
    /**
     * 获取：表名称。向量库中Collection的名称。同时支持：库名称.表名称
     */
    public String getCollection()
    {
        return collection;
    }
    
    
    
    /**
     * 设置：表名称。向量库中Collection的名称。同时支持：库名称.表名称
     * 
     * @param i_Collection 表名称。向量库中Collection的名称。同时支持：库名称.表名称
     */
    public void setCollection(String i_Collection)
    {
        this.collection = i_Collection;
    }
    
    
    
    /**
     * 获取：分区名称
     */
    public String getPartition()
    {
        return partition;
    }


    
    /**
     * 设置：分区名称
     * 
     * @param i_Partition 分区名称
     */
    public void setPartition(String i_Partition)
    {
        this.partition = i_Partition;
    }


    
    /**
     * 获取：向量查询时返回的结果集数量
     */
    public Integer getTopK()
    {
        return topK;
    }


    
    /**
     * 设置：向量查询时返回的结果集数量
     * 
     * @param i_TopK 向量查询时返回的结果集数量
     */
    public void setTopK(Integer i_TopK)
    {
        this.topK = i_TopK;
    }



    /**
     * 获取：向量库占位符的信息
     */
    public MilvusContent getContent()
    {
        return this.content;
    }


    
    /**
     * 设置：向量库占位符的信息
     * 
     * @param i_Content 向量库占位符的信息
     */
    public void setContent(String i_ContentText)
    {
        this.content.setContentText(i_ContentText);
    }
    
    
    
    /**
     * 占位符X有条件的取值。占位符在满足条件时取值A，否则取值B。
     * 取值A、B，可以是占位符X、NULL值，另一个占位符Y或常量字符。
     * 
     * 类似于Mybatis IF条件功能
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-19
     * @version     v1.0
     *
     * @param i_ConditionGroup
     */
    public void setCondition(DBConditions i_ConditionGroup)
    {
        this.content.addCondition(i_ConditionGroup);
    }
    
    
    
    public MilvusContent getContentDB()
    {
        return this.content;
    }
    
    
    
    public void setContentDB(MilvusContent i_MilvusContent)
    {
        this.content = i_MilvusContent;
    }
    
    
    
    /**
     * 获取：创建对象的所属的数据库名称
     * 
     * 此属性为动作方法，即this.setCreate(...)时，将尝试创建对象(当对象不存在时)。
     * 也因为是动作方法，所以在设置本属性前dataSourceGroup、content它两属性应当已设置OK。
     */
    public String getCreateObjectName()
    {
        return create;
    }
    
    
    
    /**
     * 创建对象所属的数据库名称
     * 
     * 此属性为动作方法，即this.setCreate(...)时，将尝试创建对象(当对象不存在时)。
     * 也因为是动作方法，所以在设置本属性前milvus、content它两属性应当已设置OK。
     * 
     * 实现服务启动时检查并创建数据库对象(如数据库表)，已存在不创建。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     *
     * @param i_CreateDBName  被创建对象所属的数据库名称
     */
    public void setCreate(String i_CreateDBName)
    {
        this.create = i_CreateDBName.trim();
        this.type   = XMilvusType.DDLCreate;
        
        this.createObject();
    }
    
    
    
    /**
     * 创建对象。如创建表。
     * 
     * 此属性为动作方法，即this.setCreate(...)时，将尝试创建对象(当对象不存在时)。
     * 也因为是动作方法，所以在设置本属性前milvus、content它两属性应当已设置OK。
     * 
     * 实现服务启动时检查并创建数据库对象(如数据库表)，已存在不创建。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-14
     * @version     v1.0
     */
    public synchronized boolean createObject()
    {
        if ( null == this.getMilvus() )
        {
            throw new NullPointerException("Milvus is null.");
        }
        else if ( Help.isNull(this.content) )
        {
            throw new NullPointerException("Milvus content is null.");
        }
        else if ( Help.isNull(this.collection) )
        {
            throw new NullPointerException("Milvus collection is null.");
        }
        else if ( Help.isNull(this.create) )
        {
            throw new NullPointerException("CreateObjectName is null.");
        }
        else if ( !this.getContent().getContentText().toUpperCase().contains(this.create.toUpperCase()) )
        {
            // 简单的检查创建的对象名称，是否在执行Content语句中存在
            throw new RuntimeException("CreateObjectName[" + this.create + "] is invalid.");
        }
        
        try
        {
            boolean v_IsExists = this.milvus.exists(this.getCollection());
            if ( !v_IsExists )
            {
                boolean v_Ret = this.execute();
                
                if ( v_Ret )
                {
                    System.out.println("Create object[" + this.create + "] OK. " + Help.NVL(this.comment));
                }
                else
                {
                    System.err.println("Create object[" + this.create + "] Error. " + Help.NVL(this.comment));
                    $Logger.error("Create object[" + this.create + "] Error. " + Help.NVL(this.comment));
                }
                
                return v_Ret;
            }
        }
        catch (Exception exce)
        {
            System.err.println("Create object[" + this.create + "] Error. " + Help.NVL(this.comment));
            $Logger.error("Create object[" + this.create + "] Error. " + Help.NVL(this.comment) ,exce);
            exce.printStackTrace();
        }
        
        return false;
    }
    
    
    
    /**
     * 判定对象是否存在。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-17
     * @version     v1.0
     *
     * @param i_ObjectName   向量库对象名称。可以是：表
     * @return
     * @throws Exception
     */
    public boolean isExists(String i_DBObjectName) throws Exception
    {
        return this.milvus.exists(i_DBObjectName);
    }
    
    
    
    /**
     * 占位符Content的查询。-- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @return  查询结果
     */
    public Object query()
    {
        return XMilvusOPQuery.queryMilvusData(this).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。-- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Milvus   外部Milvus的帮助类
     * @return           的查询结果
     */
    public Object query(MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Milvus).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @return           查询结果
     */
    public Object query(Map<String ,?> i_Values)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           查询结果
     */
    public Object query(Map<String ,?> i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_Milvus).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  查询结果
     */
    public Object query(Map<String ,?> i_Values ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNames).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  查询结果
     */
    public Object query(Map<String ,?> i_Values ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNoArr).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @return           查询结果
     */
    public Object query(Object i_Obj)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Obj).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           查询结果
     */
    public Object query(Object i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_Milvus).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  查询结果
     */
    public Object query(Object i_Values ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNames).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  查询结果
     */
    public Object query(Object i_Values ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNoArr).getDatas();
    }
    
    
    
    /**
     * 常规Content的查询。
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @return           查询结果
     */
    public Object query(String i_Content)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content).getDatas();
    }
    
    
    
    /**
     * 常规Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @param i_Milvus   外部Milvus的帮助类
     * @return           查询结果
     */
    public Object query(String i_Content ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_Milvus).getDatas();
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段名称过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  查询结果
     */
    public Object query(String i_Content ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_FilterColNames).getDatas();
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段位置过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public Object query(String i_Content ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_FilterColNoArr).getDatas();
    }
    
    
    
    /**
     * 占位符Content的查询。-- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData()
    {
        return XMilvusOPQuery.queryMilvusData(this);
    }
    
    
    
    /**
     * 占位符Content的查询。-- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的查询。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(Map<String ,?> i_Values)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values);
    }
    
    
    
    /**
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(Map<String ,?> i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(Map<String ,?> i_Values ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNames);
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(Map<String ,?> i_Values ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNoArr);
    }
    
    
    
    /**
     * 占位符Content的查询。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(Object i_Obj)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Obj);
    }
    
    
    
    /**
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(Object i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(Object i_Values ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNames);
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(Object i_Values ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Values ,i_FilterColNoArr);
    }
    
    
    
    /**
     * 常规Content的查询。
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(String i_Content)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content);
    }
    
    
    
    /**
     * 常规Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public MilvusData queryMilvusData(String i_Content ,MilvusHelp i_Milvus)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_Milvus);
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段名称过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(String i_Content ,List<String> i_FilterColNames)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_FilterColNames);
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段位置过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public MilvusData queryMilvusData(String i_Content ,int [] i_FilterColNoArr)
    {
        return XMilvusOPQuery.queryMilvusData(this ,i_Content ,i_FilterColNoArr);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。 -- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @return           返回语句影响的记录数及自增长ID
     */
    public MilvusData executeInsert()
    {
        return XMilvusOPInsert.executeInsert(this);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数及自增长ID
     */
    public MilvusData executeInsert(final Map<String ,?> i_Values)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Values);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @return           返回语句影响的记录数及自增长ID
     */
    public MilvusData executeInsert(final Object i_Obj)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Obj);
    }
    
    
    
    /**
     * 常规Insert语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @return           返回语句影响的记录数及自增长ID。
     */
    public MilvusData executeInsert(final String i_Content)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Content);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。 -- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及自增长ID。
     */
    public MilvusData executeInsert(final MilvusHelp i_Milvus)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及自增长ID。
     */
    public MilvusData executeInsert(final Map<String ,?> i_Values ,final MilvusHelp i_Milvus)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Insert语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及自增长ID。
     */
    public MilvusData executeInsert(final Object i_Obj ,final MilvusHelp i_Milvus)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Obj ,i_Milvus);
    }
    
    
    
    /**
     * 常规Insert语句的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及自增长ID。
     */
    public MilvusData executeInsert(final String i_Content ,final MilvusHelp i_Milvus)
    {
        return XMilvusOPInsert.executeInsert(this ,i_Content ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Values   占位符Content的填充对象。
     * @return           是否执行成功。
     */
    public MilvusData executeInserts(final List<?> i_ObjList)
    {
        return XMilvusOPInsert.executeInserts(this ,i_ObjList);
    }
    
    
    
    /**
     * 批量执行：占位符Content的Insert语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeInserts(final List<?> i_ObjList ,final MilvusHelp i_Milvus)
    {
        return XMilvusOPInsert.executeInserts(this ,i_ObjList ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。 -- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate()
    {
        return XMilvusOPUpdate.executeUpdate(this);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(Map<String ,?> i_Values)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Values);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(Object i_Values)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Values);
    }
    
    
    
    /**
     * 常规Update语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(String i_Content)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Content);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。 -- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(MilvusHelp i_Milvus)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(Map<String ,?> i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(Object i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 常规Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public MilvusData executeUpdate(String i_Content ,MilvusHelp i_Milvus)
    {
        return XMilvusOPUpdate.executeUpdate(this ,i_Content ,i_Milvus);
    }
    
    
    
    /**
     * 批量执行：占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     *   
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeUpdates(List<?> i_ObjList)
    {
        return XMilvusOPUpdate.executeUpdates(this ,i_ObjList);
    }
    
    
    
    /**
     * 批量执行：占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     *   
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeUpdates(List<?> i_ObjList ,MilvusHelp i_Milvus)
    {
        return XMilvusOPUpdate.executeUpdates(this ,i_ObjList ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。 -- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @return  返回语句影响的记录数。
     */
    public MilvusData executeDelete()
    {
        return XMilvusOPDelete.executeDelete(this);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(Map<String ,?> i_Values)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Values);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(Object i_Values)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Values);
    }
    
    
    
    /**
     * 常规Delete语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Content  删除条件的标量过滤条件，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(String i_Content)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Content);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。 -- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(MilvusHelp i_Milvus)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(Map<String ,?> i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(Object i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 常规Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_Content  删除条件的标量过滤条件，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public MilvusData executeDelete(String i_Content ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDelete.executeDelete(this ,i_Content ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的执行。-- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @return           是否执行成功
     */
    public boolean execute()
    {
        return XMilvusOPDDL.execute(this);
    }
    
    
    
    /**
     * 占位符Content的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Values   占位符Content的填充集合。
     * @return           是否执行成功
     */
    public boolean execute(Map<String ,?> i_Values)
    {
        return XMilvusOPDDL.execute(this ,i_Values);
    }
    
    
    
    /**
     * 占位符Content的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Values   占位符Content的填充对象。
     * @return           是否执行成功
     */
    public boolean execute(Object i_Obj)
    {
        return XMilvusOPDDL.execute(this ,i_Obj);
    }
    
    
    
    /**
     * 常规执行内容的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Content  执行内容。
     *                      创建集合对象时，为表的Json结构。与Milvus官网页面中的 “代码” 页面中的Json格式保持一致。
     *                      删除集合对象时，为表的名称，多个表用英文逗号分隔。
     * @return           是否执行成功。
     */
    public boolean execute(String i_Content)
    {
        return XMilvusOPDDL.execute(this ,i_Content);
    }
    
    
    
    /**
     * 占位符Content的执行。-- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public boolean execute(MilvusHelp i_Milvus)
    {
        return XMilvusOPDDL.execute(this ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public boolean execute(Map<String ,?> i_Values ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDDL.execute(this ,i_Values ,i_Milvus);
    }
    
    
    
    /**
     * 占位符Content的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public boolean execute(Object i_Obj ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDDL.execute(this ,i_Obj ,i_Milvus);
    }
    
    
    
    /**
     * 常规Content的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  占位符Content。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public boolean execute(String i_Content ,MilvusHelp i_Milvus)
    {
        return XMilvusOPDDL.execute(this ,i_Content ,i_Milvus);
    }


    
    /**
     * 获取：解释Xml文件，分析数据库结果集转化为Java实例对象
     */
    public MilvusResult getResult()
    {
        return result;
    }


    
    /**
     * 设置：解释Xml文件，分析数据库结果集转化为Java实例对象
     * 
     * @param i_Result 解释Xml文件，分析数据库结果集转化为Java实例对象
     */
    public void setResult(MilvusResult i_Result)
    {
        this.result = i_Result;
    }
    
    
    
    @Override
    public int hashCode()
    {
        return this.getObjectID().hashCode();
    }
    
    
    
    @Override
    public boolean equals(Object i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else if ( this == i_Other )
        {
            return true;
        }
        else if ( i_Other instanceof XMilvus )
        {
            return this.getObjectID().equals(((XMilvus)i_Other).getObjectID());
        }
        else
        {
            return false;
        }
    }



    @Override
    public int compareTo(XMilvus i_XMilvus)
    {
        if ( i_XMilvus == null )
        {
            return 1;
        }
        else if ( this == i_XMilvus )
        {
            return 0;
        }
        else
        {
            return this.getObjectID().compareTo(i_XMilvus.getObjectID());
        }
    }
    
}
