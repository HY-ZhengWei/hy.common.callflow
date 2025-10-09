package org.hy.common.callflow.cache;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;
import org.hy.common.redis.IRedis;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 缓存写元素：底层为Redis 
 * 
 * 约定1：仅有库名称时，删除库。
 *           仅在 allowDelTable 为真时才删除
 *           返回类型为：Boolean ，表示是否删除成功。
 *           
 * 约定2：仅有库、表名称时，删除表中所有行数据。
 *           仅在 allowDelTable 为真时才删除
 *           返回类型为：Boolean ，表示是否删除成功。
 *           
 * 约定3：有库、表、主键ID时，创建、修改或删除一行数据。
 *           当行数据为空或空字符串时，删除一行数据
 *           返回类型为：Long ，返回影响的行数。负数表示异常
 *           
 * 约定4：仅有主键ID时，按Key、Value保存一个普通字符串。
 *           当Value为空或空字符串时，删除Key
 *           返回类型为：Long ，返回影响的行数。负数表示异常
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-11
 * @version     v1.0
 *              v2.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v3.0  2025-09-26  迁移：静态检查
 */
public class CacheSetConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(CacheSetConfig.class);
    
    
    
    /** 缓存实例的XID。可以是数值、上下文变量、XID标识 */
    private String                        cacheXID;
    
    /** 数据库名称。可以是数值、上下文变量、XID标识 */
    private String                        dataBase;
    
    /** 表名称。可以是数值、上下文变量、XID标识 */
    private String                        table;
    
    /** 主键ID（要求：全域、全库、全表均是惟一的）。可以是数值、上下文变量、XID标识 */
    private String                        pkID;
    
    /** 行数据。可以是常量、上下文变量、XID标识，并且支持多个占位符 */
    private String                        rowData;
    
    /** 行数据，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> rowDataPlaceholders;
    
    /** 行对象成员属性为 null 时，nullDel=false时，成员属性不参与更新；nullDel=true时，成员属性将从Redis中删除。默认为：false */
    private Boolean                       nullDel;
    
    /** 过期时间（单位：秒）。可以是数值、上下文变量、XID标识 */
    private String                        expireTime;
    
    /** 是否允许删除库或表。默认为：false */
    private Boolean                       allowDelTable;
    
    /** 统一缓存接口（仅内部使用） */
    private ICache<?>                     cache;
    
    /** 是否初始化（仅内部使用） */
    private boolean                       isInit;
    
    
    
    public CacheSetConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public CacheSetConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.isInit        = false;
        this.nullDel       = false;
        this.expireTime    = "0";
        this.allowDelTable = false;
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
        // 表不空，库不能为空
        if ( !Help.isNull(this.getTable()) )
        {
            if ( Help.isNull(this.getDataBase()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getDataBase()) + "].database is null ,but table is not null.");
                return false;
            }
        }
        // 主键不空，库不空，表不能为空
        if ( !Help.isNull(this.getPkID()) )
        {
            if ( !Help.isNull(this.getDataBase()) && Help.isNull(this.getTable()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getTable()) + "].table is null ,but database and pkID are not null.");
                return false;
            }
        }
        
        return true;
    }

    
    
    /**
     * 获取：缓存实例的XID。可以是数值、上下文变量、XID标识
     */
    public String getCacheXID()
    {
        if ( Help.isNull(this.cacheXID) )
        {
            return null;
        }
        else
        {
            return DBSQL.$Placeholder + this.cacheXID;
        }
    }


    
    /**
     * 设置：缓存实例的XID。可以是数值、上下文变量、XID标识
     * 
     * @param i_CacheXID 缓存实例的XID。可以是数值、上下文变量、XID标识
     */
    public void setCacheXID(String i_CacheXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.cacheXID = ValueHelp.standardValueID(i_CacheXID);
        this.isInit   = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取缓存实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-11
     * @version     v1.0
     *
     * @return
     */
    private synchronized ICache<?> gatCache()
    {
        if ( !this.isInit )
        {
            if ( !Help.isNull(this.cacheXID) )
            {
                Object v_Cache = XJava.getObject(this.cacheXID);
                if ( v_Cache == null )
                {
                    this.cache = CacheFactory.newInstanceOf(null ,Object.class);
                }
                else if ( v_Cache instanceof IRedis )
                {
                    this.cache = CacheFactory.newInstanceOf((IRedis) v_Cache ,Object.class);
                }
                else
                {
                    this.cache = CacheFactory.newInstanceOf(null ,Object.class);
                }
            }
            else
            {
                this.cache = CacheFactory.newInstanceOf(null ,Object.class);
            }
            
            this.isInit = true;
        }
        
        return this.cache;
    }



    /**
     * 获取：数据库名称。可以是数值、上下文变量、XID标识
     */
    public String getDataBase()
    {
        return dataBase;
    }


    
    /**
     * 设置：数据库名称。可以是数值、上下文变量、XID标识
     * 
     * @param i_DataBase 数据库名称。可以是数值、上下文变量、XID标识
     */
    public void setDataBase(String i_DataBase)
    {
        this.dataBase = i_DataBase;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：表名称。可以是数值、上下文变量、XID标识
     */
    public String getTable()
    {
        return table;
    }


    
    /**
     * 设置：表名称。可以是数值、上下文变量、XID标识
     * 
     * @param i_Table 表名称。可以是数值、上下文变量、XID标识
     */
    public void setTable(String i_Table)
    {
        this.table = i_Table;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：主键ID（要求：全域、全库、全表均是惟一的）。可以是数值、上下文变量、XID标识
     */
    public String getPkID()
    {
        return pkID;
    }


    
    /**
     * 设置：主键ID（要求：全域、全库、全表均是惟一的）。可以是数值、上下文变量、XID标识
     * 
     * @param i_PkID 主键ID（要求：全域、全库、全表均是惟一的）。可以是数值、上下文变量、XID标识
     */
    public void setPkID(String i_PkID)
    {
        this.pkID = i_PkID;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：行数据。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public String getRowData()
    {
        return rowData;
    }


    
    /**
     * 设置：行数据。可以是常量、上下文变量、XID标识，并且支持多个占位符
     * 
     * @param i_RowData 行数据。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public void setRowData(String i_RowData)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_RowData) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_RowData ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.rowDataPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.rowDataPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.rowData = i_RowData;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：行对象成员属性为 null 时，nullDel=false时，成员属性不参与更新；nullDel=true时，成员属性将从Redis中删除。默认为：false
     */
    public Boolean getNullDel()
    {
        return nullDel;
    }


    
    /**
     * 设置：行对象成员属性为 null 时，nullDel=false时，成员属性不参与更新；nullDel=true时，成员属性将从Redis中删除。默认为：false
     * 
     * @param i_NullDel 行对象成员属性为 null 时，nullDel=false时，成员属性不参与更新；nullDel=true时，成员属性将从Redis中删除。默认为：false
     */
    public void setNullDel(Boolean i_NullDel)
    {
        this.nullDel = i_NullDel;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：过期时间（单位：秒）。可以是数值、上下文变量、XID标识
     */
    public String getExpireTime()
    {
        return expireTime;
    }


    
    /**
     * 设置：过期时间（单位：秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_ExpireTime 过期时间（单位：秒）。可以是数值、上下文变量、XID标识
     */
    public void setExpireTime(String i_ExpireTime)
    {
        this.expireTime = i_ExpireTime;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：是否允许删除库或表。默认为：false
     */
    public Boolean getAllowDelTable()
    {
        return allowDelTable;
    }


    
    /**
     * 设置：是否允许删除库或表。默认为：false
     * 
     * @param i_AllowDelTable 是否允许删除库或表。默认为：false
     */
    public void setAllowDelTable(Boolean i_AllowDelTable)
    {
        this.allowDelTable = i_AllowDelTable;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-11
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.CacheSet.getValue();
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-11
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        try
        {
            if ( !this.handleContext(io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            this.gatCache();
            
            Object v_CacheRetDatas = null;
            if ( !Help.isNull(this.dataBase) )
            {
                String v_DataBase = (String) ValueHelp.getValue(this.dataBase ,String.class ,"" ,io_Context);
                if ( Help.isNull(v_DataBase) )
                {
                    v_Result.setException(new RuntimeException(this.getXid() + " database[" + this.dataBase + "] is not exists."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                if ( !Help.isNull(this.table) )
                {
                    String v_Table = (String) ValueHelp.getValue(this.table ,String.class ,"" ,io_Context);
                    if ( Help.isNull(v_Table) )
                    {
                        v_Result.setException(new RuntimeException(this.getXid() + " table[" + this.table + "] is not exists."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    if ( !Help.isNull(this.pkID) )
                    {
                        String v_PKID = (String) ValueHelp.getValue(this.pkID ,String.class ,"" ,io_Context);
                        if ( Help.isNull(v_PKID) )
                        {
                            v_Result.setException(new RuntimeException(this.getXid() + " pkID[" + this.pkID + "] is not exists."));
                            this.refreshStatus(io_Context ,v_Result.getStatus());
                            return v_Result;
                        }
                        
                        // 约定3：有库、表、主键ID时，创建、修改或删除一行数据。
                        Long v_ExpireTime = (Long) ValueHelp.getValue(this.expireTime ,Long.class ,0L ,io_Context);
                        if ( v_ExpireTime == null || v_ExpireTime < 0L )
                        {
                            v_ExpireTime = 0L;
                        }
                        
                        Object v_RowData = this.gatRowData(io_Context);
                        if ( v_RowData == null )
                        {
                            v_CacheRetDatas = this.cache.delete(v_DataBase ,v_Table ,v_PKID);
                        }
                        else if ( v_RowData instanceof String )
                        {
                            String v_RowText = (String) v_RowData;
                            if ( Help.isNull(v_RowText) )
                            {
                                v_CacheRetDatas = this.cache.delete(v_DataBase ,v_Table ,v_PKID);
                            }
                            else if ( XJSON.isJson(v_RowText) )
                            {
                                XJSON               v_XJson  = new XJSON();
                                Map<String ,Object> v_RowMap = (Map<String ,Object>) v_XJson.toJava(v_RowText ,HashMap.class);
                                if ( Help.isNull(v_RowMap) )
                                {
                                    v_CacheRetDatas = this.cache.delete(v_DataBase ,v_Table ,v_PKID);
                                }
                                else
                                {
                                    v_CacheRetDatas = this.cache.save(v_DataBase ,v_Table ,v_PKID ,v_RowMap ,v_ExpireTime);
                                }
                            }
                            else
                            {
                                v_Result.setException(new RuntimeException(this.getXid() + " rowData[" + v_RowText + "] is a text ,but it is not in JSON format."));
                                this.refreshStatus(io_Context ,v_Result.getStatus());
                                return v_Result;
                            }
                        }
                        else 
                        {
                            v_CacheRetDatas = this.cache.save(v_DataBase ,v_Table ,v_PKID ,v_RowData ,this.nullDel ,v_ExpireTime);
                        }
                    }
                    else
                    {
                        // 约定2：仅有库、表名称时，删除表中所有行数据。
                        if ( this.allowDelTable )
                        {
                            v_CacheRetDatas = this.cache.dropTable(v_DataBase ,v_Table);
                        }
                        else
                        {
                            // 防止误操作
                            v_Result.setException(new RuntimeException(this.getXid() + " drop table[" + v_DataBase + "->" + v_Table + "] is not allowed."));
                            this.refreshStatus(io_Context ,v_Result.getStatus());
                            return v_Result;
                        }
                    }
                }
                else
                {
                    // 约定1：仅有库名称时，删除库。
                    if ( this.allowDelTable )
                    {
                        v_CacheRetDatas = this.cache.dropDatabase(v_DataBase);
                    }
                    else
                    {
                        // 防止误操作
                        v_Result.setException(new RuntimeException(this.getXid() + " drop database[" + v_DataBase + "] is not allowed."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                }
            }
            else if ( !Help.isNull(this.pkID) )
            {
                String v_PKID = (String) ValueHelp.getValue(this.pkID ,String.class ,"" ,io_Context);
                if ( Help.isNull(v_PKID) )
                {
                    v_Result.setException(new RuntimeException(this.getXid() + " pkID[" + this.pkID + "] is not exists."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                // 约定4：仅有主键ID时，按Key、Value保存一个普通字符串。
                Long v_ExpireTime = (Long) ValueHelp.getValue(this.expireTime ,Long.class ,0L ,io_Context);
                if ( v_ExpireTime == null || v_ExpireTime < 0L )
                {
                    v_ExpireTime = 0L;
                }
                
                Object v_RowData = this.gatRowData(io_Context);
                if ( v_RowData == null )
                {
                    v_CacheRetDatas = this.cache.del(v_PKID);
                }
                else if ( v_RowData instanceof String )
                {
                    String v_RowText = (String) v_RowData;
                    if ( Help.isNull(v_RowText) )
                    {
                        v_CacheRetDatas = this.cache.del(v_PKID);
                    }
                    else
                    {
                        if ( v_ExpireTime > 0L )
                        {
                            if ( this.cache.setex(v_PKID ,v_RowText ,v_ExpireTime) ) 
                            {
                                v_CacheRetDatas = 1L;
                            }
                            else
                            {
                                v_CacheRetDatas = 0L;
                            }
                        }
                        else
                        {
                            if ( this.cache.set(v_PKID ,v_RowText) )
                            {
                                v_CacheRetDatas = 1L;
                            }
                            else
                            {
                                v_CacheRetDatas = 0L;
                            }
                        }
                    }
                }
                else 
                {
                    String v_RowText = v_RowData.toString();
                    if ( v_ExpireTime > 0L )
                    {
                        if ( this.cache.setex(v_PKID ,v_RowText ,v_ExpireTime) )
                        {
                            v_CacheRetDatas = 1L;
                        }
                        else
                        {
                            v_CacheRetDatas = 0L;
                        }
                    }
                    else
                    {
                        if (this.cache.set(v_PKID ,v_RowText) )
                        {
                            v_CacheRetDatas = 1L;
                        }
                        else
                        {
                            v_CacheRetDatas = 0L;
                        }
                    }
                }
            }
            
            v_Result.setResult(v_CacheRetDatas);
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
     * 将 rowData 从运行时环境中转为数值或对象实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-11
     * @version     v1.0
     *
     * @param io_Context
     * @return
     * @throws Exception
     */
    private Object gatRowData(Map<String ,Object> io_Context) throws Exception
    {
        // 常量数值时
        if ( Help.isNull(this.rowDataPlaceholders) )
        {
            return this.rowData;
        }
        // 仅有一个占位符的
        else if ( this.rowDataPlaceholders.size() == 1 )
        {
            String v_Key = DBSQL.$Placeholder + this.rowDataPlaceholders.keySet().iterator().next();
            
            // 这一个占位符就是全部时，说明可能是一个对象
            if ( this.rowData.equals(v_Key) )
            {
                return ValueHelp.getValue(this.rowData ,null ,null ,io_Context);
            }
            // 拼接一个占位符的字符串
            else
            {
                return ValueHelp.replaceByContext(this.rowData ,this.rowDataPlaceholders ,io_Context);
            }
        }
        // 拼接多个占位符的字符串
        else
        {
            return ValueHelp.replaceByContext(this.rowData ,this.rowDataPlaceholders ,io_Context);
        }
    }
    
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-11
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
        String        v_XName    = ElementType.CacheSet.getXmlName();
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
            if ( !Help.isNull(this.cacheXID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("cacheXID"     ,this.getCacheXID()));
            }
            if ( !Help.isNull(this.dataBase) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("dataBase"     ,this.dataBase));
            }
            if ( !Help.isNull(this.table) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("table"        ,this.table));
            }
            if ( !Help.isNull(this.pkID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("pkID"         ,this.pkID));
            }
            if ( !Help.isNull(this.rowData) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("rowData"      ,this.rowData ,v_NewSpace));
            }
            if ( this.nullDel != null && this.nullDel )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("nullDel"       ,this.nullDel));
            }
            if ( !Help.isNull(this.expireTime) && !"0".equals(this.expireTime) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("expireTime"    ,this.expireTime));
            }
            if ( this.allowDelTable != null && this.allowDelTable )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("allowDelTable" ,this.allowDelTable));
            }
            if ( !Help.isNull(this.returnID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("returnID"      ,this.returnID));
            }
            if ( !Help.isNull(this.statusID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("statusID"      ,this.statusID));
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
            
            this.toXmlInitExecute(v_Xml ,v_NewSpace);
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
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
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
        
        if ( !Help.isNull(this.cacheXID) )
        {
            v_Builder.append(this.getCacheXID());
            if ( XJava.getObject(this.cacheXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( !Help.isNull(this.dataBase) )
        {
            String v_Database = null;
            try
            {
                v_Database = (String) ValueHelp.getValue(this.dataBase ,String.class ,"" ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error(this.getXid() + ".Database[" + this.dataBase + "] is error" ,exce);
            }
            
            if ( !Help.isNull(v_Database) )
            {
                v_Builder.append(v_Database);
            }
            else
            {
                v_Builder.append("[").append(this.dataBase).append("=?]");
            }
            v_Builder.append(ValueHelp.$Split);
            
            if ( !Help.isNull(this.table) )
            {
                String v_Table = null;
                try
                {
                    v_Table = (String) ValueHelp.getValue(this.table ,String.class ,"" ,i_Context);
                }
                catch (Exception exce)
                {
                    $Logger.error(this.getXid() + ".Table[" + this.table + "] is error" ,exce);
                }
                
                if ( !Help.isNull(v_Table) )
                {
                    v_Builder.append(v_Table);
                }
                else
                {
                    v_Builder.append("[").append(this.table).append("=?]");
                }
                v_Builder.append(ValueHelp.$Split);
                
                if ( !Help.isNull(this.pkID) )
                {
                    String v_PKID = null;
                    try
                    {
                        v_PKID = (String) ValueHelp.getValue(this.pkID ,String.class ,"" ,i_Context);
                    }
                    catch (Exception exce)
                    {
                        $Logger.error(this.getXid() + ".PKID[" + this.pkID + "] is error" ,exce);
                    }
                    
                    if ( !Help.isNull(v_PKID) )
                    {
                        v_Builder.append(v_PKID);
                        
                        try
                        {
                            Object v_RowData = this.gatRowData(i_Context);
                            if ( v_RowData == null )
                            {
                                v_Builder.append("删除一行数据");
                            }
                            else if ( v_RowData instanceof String )
                            {
                                String v_RowText = (String) v_RowData;
                                if ( Help.isNull(v_RowText) )
                                {
                                    v_Builder.append("删除一行数据");
                                }
                                else
                                {
                                    v_Builder.append("=").append(v_RowText);
                                }
                            }
                            else
                            {
                                v_Builder.append("=").append(v_RowData.toString());
                            }
                        }
                        catch (Exception exce)
                        {
                            $Logger.error(this.getXid() + ".PKID[" + this.pkID + "] is error" ,exce);
                        }
                    }
                    else
                    {
                        v_Builder.append("[").append(this.pkID).append("=?]");
                    }
                }
                else
                {
                    if ( this.allowDelTable )
                    {
                        v_Builder.append("删除表中全部数据");
                    }
                    else
                    {
                        v_Builder.append("Error");
                    }
                }
            }
            else
            {
                if ( this.allowDelTable )
                {
                    v_Builder.append("删除库中全部表");
                }
                else
                {
                    v_Builder.append("Error");
                }
            }
        }
        else if ( !Help.isNull(this.pkID) )
        {
            String v_PKID = null;
            
            try
            {
                v_PKID = (String) ValueHelp.getValue(this.pkID ,String.class ,"" ,i_Context);
                if ( !Help.isNull(v_PKID) )
                {
                    v_Builder.append(this.pkID);
                    
                    Object v_RowData = this.gatRowData(i_Context);
                    if ( v_RowData == null )
                    {
                        v_Builder.append("删除Key");
                    }
                    else if ( v_RowData instanceof String )
                    {
                        String v_RowText = (String) v_RowData;
                        if ( Help.isNull(v_RowText) )
                        {
                            v_Builder.append("删除Key");
                        }
                        else
                        {
                            v_Builder.append("=").append(v_RowText);
                        }
                    }
                    else 
                    {
                        v_Builder.append("=").append(v_RowData.toString());
                    }
                }
                else
                {
                    v_Builder.append("[").append(this.pkID).append("=?]");
                }
            }
            catch (Exception exce)
            {
                $Logger.error(this.getXid() + ".PKID[" + this.pkID + "] is error" ,exce);
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        if ( !Help.isNull(this.cacheXID) )
        {
            v_Builder.append(this.getCacheXID());
            if ( XJava.getObject(this.cacheXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( !Help.isNull(this.dataBase) )
        {
            v_Builder.append(this.dataBase);
            v_Builder.append(ValueHelp.$Split);
            
            if ( !Help.isNull(this.table) )
            {
                v_Builder.append(this.table);
                v_Builder.append(ValueHelp.$Split);
                
                if ( !Help.isNull(this.pkID) )
                {
                    v_Builder.append(this.pkID);
                    
                    if ( Help.isNull(this.rowData) )
                    {
                        v_Builder.append("删除一行数据");
                    }
                    else
                    {
                        v_Builder.append("=").append(this.rowData);
                    }
                }
                else
                {
                    if ( this.allowDelTable )
                    {
                        v_Builder.append("删除表中全部数据");
                    }
                    else
                    {
                        v_Builder.append("Error");
                    }
                }
            }
            else
            {
                if ( this.allowDelTable )
                {
                    v_Builder.append("删除库中全部表");
                }
                else
                {
                    v_Builder.append("Error");
                }
            }
        }
        else if ( !Help.isNull(this.pkID) )
        {
            v_Builder.append(this.pkID);
            
            if ( Help.isNull(this.rowData) )
            {
                v_Builder.append("删除Key");
            }
            else
            {
                v_Builder.append("=").append(this.rowData);
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new CacheSetConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        CacheSetConfig v_Clone = new CacheSetConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.cacheXID      = this.cacheXID;
        v_Clone.dataBase      = this.dataBase;
        v_Clone.table         = this.table;
        v_Clone.pkID          = this.pkID;
        v_Clone.nullDel       = this.nullDel;
        v_Clone.expireTime    = this.expireTime;
        v_Clone.allowDelTable = this.allowDelTable;
        
        v_Clone.setRowData(this.rowData);
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
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
            throw new NullPointerException("Clone CacheSetConfig xid is null.");
        }
        
        CacheSetConfig v_Clone = (CacheSetConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.cacheXID      = this.cacheXID;
        v_Clone.dataBase      = this.dataBase;
        v_Clone.table         = this.table;
        v_Clone.pkID          = this.pkID;
        v_Clone.nullDel       = this.nullDel;
        v_Clone.expireTime    = this.expireTime;
        v_Clone.allowDelTable = this.allowDelTable;
        
        v_Clone.setRowData(this.rowData);
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-12
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
            throw new NullPointerException("Clone CacheSetConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        CacheSetConfig              v_Clone      = new CacheSetConfig();
        
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
