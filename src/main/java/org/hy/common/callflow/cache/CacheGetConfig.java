package org.hy.common.callflow.cache;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
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
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 缓存读元素：底层为Redis 
 * 
 * 约定1：仅有库名称时，查库中所有表信息。
 *           返回类型为：Map<String ,String>，Map.key行主键，Map.key表名称，Map.value表的创建时间
 *           
 * 约定2：仅有库、表名称时，查表中所有行数据。
 *           按Map 返回，未定义rowClass类型时，返回类型为：TablePartitionRID<String ,String>，Map.key行主键，Map.Map.key字段名，Map.Map.value字段值
 *           按Map 返回，有定义rowClass类型时，返回类型为：Map<String ,E>，Map.key行主键，Map.value为行数据
 *           按List返回，未定义rowClass类型时，返回类型为：List<Map<String ,String>>，Map.key字段名，Map.value字段值
 *           按List返回，有定义rowClass类型时，返回类型为：List<E>
 *           
 * 约定3：有库、表、主键ID时，查一行数据。
 *           未定义rowClass类型时，返回类型为：Map<String ,String>，Map.key字段名，Map.value字段值
 *           有定义rowClass类型时，返回类型为：rowClass指定的对象实例
 *           
 * 约定4：仅有主键ID时，按Key、Value查一个普通字符串。
 *           返回类型为：String
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-10
 * @version     v1.0
 *              v2.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v3.0  2025-09-26  迁移：静态检查
 *              v4.0  2025-10-20  修正：先handleContext()解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
 */
public class CacheGetConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(CacheGetConfig.class);
    
    
    
    /** 缓存实例的XID。可以是数值、上下文变量、XID标识 */
    private String    cacheXID;
    
    /** 数据库名称。可以是数值、上下文变量、XID标识 */
    private String    dataBase;
    
    /** 表名称。可以是数值、上下文变量、XID标识 */
    private String    table;
    
    /** 主键ID（要求：全域、全库、全表均是惟一的）。可以是数值、上下文变量、XID标识 */
    private String    pkID;
    
    /** 获取数据并删除（仅对只通Key获取Value的情况有效，对库表行无效）。默认为：false */
    private Boolean   allowDelValue;
    
    /** 行数据的元类型。未直接使用Class<?>原因是：允许类不存在，仅在要执行时存在即可。优点：提高可移植性。 */
    private String    rowClass;
    
    /** 如果获取多行数据时，按List（false时）或Map（true时）结构返回的。默认为：true */
    private Boolean   returnListOrMap;
    
    /** 统一缓存接口（仅内部使用） */
    private ICache<?> cache;
    
    /** 是否初始化（仅内部使用） */
    private boolean   isInit;
    
    
    
    public CacheGetConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public CacheGetConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.isInit          = false;
        this.allowDelValue   = false;
        this.returnListOrMap = true;
        this.rowClass        = null;
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
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-21
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        return "XCG_" + StringHelp.getUUID9n();
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
     * @createDate  2025-08-10
     * @version     v1.0
     *
     * @return
     */
    private synchronized ICache<?> gatCache()
    {
        if ( !this.isInit )
        {
            Class<?> v_RowClass = this.gatRowClass();
            if ( v_RowClass == null )
            {
                v_RowClass = HashMap.class;
            }
            
            if ( !Help.isNull(this.cacheXID) )
            {
                Object v_Cache = XJava.getObject(this.cacheXID);
                if ( v_Cache == null )
                {
                    this.cache = CacheFactory.newInstanceOf(null ,v_RowClass);
                }
                else if ( v_Cache instanceof IRedis )
                {
                    this.cache = CacheFactory.newInstanceOf((IRedis) v_Cache ,v_RowClass);
                }
                else
                {
                    this.cache = CacheFactory.newInstanceOf(null ,v_RowClass);
                }
            }
            else
            {
                this.cache = CacheFactory.newInstanceOf(null ,v_RowClass);
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
     * 获取：获取数据并删除（仅对只通Key获取Value的情况有效，对库表行无效）。默认为：false
     */
    public Boolean getAllowDelValue()
    {
        return allowDelValue;
    }


    
    /**
     * 设置：获取数据并删除（仅对只通Key获取Value的情况有效，对库表行无效）。默认为：false
     * 
     * @param i_AllowDelValue 获取数据并删除（仅对只通Key获取Value的情况有效，对库表行无效）。默认为：false
     */
    public void setAllowDelValue(Boolean i_AllowDelValue)
    {
        this.allowDelValue = i_AllowDelValue;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：如果获取多行数据时，按List结构返回的
     */
    public Boolean getReturnList()
    {
        return !this.returnListOrMap;
    }


    
    /**
     * 设置：如果获取多行数据时，按List结构返回的
     * 
     * @param i_ReturnList 如果获取多行数据时，按List结构返回的
     */
    public void setReturnList(Boolean i_ReturnList)
    {
        this.returnListOrMap = !i_ReturnList;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：如果获取多行数据时，按Map结构返回的。Map.key行主键，Map.value行数据
     */
    public Boolean getReturnMap()
    {
        return this.returnListOrMap;
    }


    
    /**
     * 设置：如果获取多行数据时，按Map结构返回的。Map.key行主键，Map.value行数据
     * 
     * @param i_ReturnMap 如果获取多行数据时，按Map结构返回的。Map.key行主键，Map.value行数据
     */
    public void setReturnMap(Boolean i_ReturnMap)
    {
        this.returnListOrMap = i_ReturnMap;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取行数据的元类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
     * @version     v1.0
     *
     * @return
     */
    public Class<?> gatRowClass()
    {
        if ( !Help.isNull(this.rowClass) )
        {
            try
            {
                return Help.forName(this.rowClass);
            }
            catch (Exception exce)
            {
                throw new RuntimeException(exce);
            }
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取：行数据的元类型
     */
    public String getRowClass()
    {
        return rowClass;
    }


    
    /**
     * 设置：行数据的元类型
     * 
     * @param i_RowClass 行数据的元类型
     */
    public void setRowClass(String i_RowClass)
    {
        this.rowClass = i_RowClass;
        this.isInit   = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    

    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.CacheGet.getValue();
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
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
        
        // 编排整体二次重做
        if ( !this.redo(io_Context ,v_BeginTime ,v_Result) )
        {
            return v_Result;
        }
        
        // Mock模拟
        Class<?> v_RowClas  = this.gatRowClass();
        String   v_RowClass = Help.NVL(v_RowClas == null ? null : v_RowClas.getName() ,HashMap.class.getName());
        if ( super.mock(io_Context ,v_BeginTime ,v_Result ,null ,v_RowClass) )
        {
            return v_Result;
        }
        
        try
        {
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
                        
                        // 约定3：有库、表、主键ID时，查一行数据。
                        v_CacheRetDatas = this.cache.getRow(v_DataBase ,v_Table ,v_PKID);
                    }
                    else
                    {
                        // 约定2：仅有库、表名称时，查表中所有行数据。
                        if ( this.returnListOrMap )
                        {
                            v_CacheRetDatas = this.cache.getRowsMap(v_DataBase ,v_Table);
                        }
                        else
                        {
                            v_CacheRetDatas = this.cache.getRowsList(v_DataBase ,v_Table);
                        }
                    }
                }
                else
                {
                    // 约定1：仅有库名称时，查库中所有表信息。
                    v_CacheRetDatas = this.cache.getRows(v_DataBase);
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
                
                // 约定4：仅有主键ID时，按Key、Value查一个普通字符串。
                if ( this.allowDelValue )
                {
                    v_CacheRetDatas = this.cache.getdel(v_PKID);
                }
                else
                {
                    v_CacheRetDatas = this.cache.get(v_PKID);
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
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
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
        String        v_XName    = ElementType.CacheGet.getXmlName();
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
                v_Xml.append(v_NewSpace).append(IToXml.toValue("cacheXID"      ,this.getCacheXID()));
            }
            if ( !Help.isNull(this.dataBase) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("dataBase"      ,this.dataBase));
            }
            if ( !Help.isNull(this.table) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("table"         ,this.table));
            }
            if ( !Help.isNull(this.pkID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("pkID"          ,this.pkID));
            }
            if ( !Help.isNull(this.allowDelValue) && this.allowDelValue )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("allowDelValue" ,this.allowDelValue));
            }
            if ( !Help.isNull(this.rowClass) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("rowClass"      ,this.rowClass));
            }
            if ( !Help.isNull(this.returnListOrMap) && !this.returnListOrMap )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("returnList"    ,!this.returnListOrMap));
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
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,RouteType.Error  .getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("route"));
            }
            
            // 模拟数据
            if ( !Help.isNull(this.mock.getSucceeds()) 
              || !Help.isNull(this.mock.getExceptions()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toBegin("mock"));
                if ( this.mock.isValid() )
                {
                    v_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toValue("valid" ,"true"));
                }
                if ( !Help.isNull(this.mock.getDataClass()) )
                {
                    v_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toValue("dataClass" ,this.mock.getDataClass()));
                }
                this.toXmlMockItems(v_Xml ,this.mock.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                this.toXmlMockItems(v_Xml ,this.mock.getExceptions() ,RouteType.Error  .getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("mock"));
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
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
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
                    }
                    else
                    {
                        v_Builder.append("[").append(this.pkID).append("=?]");
                    }
                }
                else
                {
                    v_Builder.append("表中全部数据");
                }
            }
            else
            {
                v_Builder.append("库中全部表");
            }
        }
        else if ( !Help.isNull(this.pkID) )
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
            
            if ( this.allowDelValue )
            {
                v_Builder.append("getdel(").append(Help.NVL(v_PKID ,this.pkID + "=?")).append(")");
            }
            else
            {
                v_Builder.append(Help.NVL(v_PKID ,this.pkID + "=?"));
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
     * @createDate  2025-08-10
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
                }
                else
                {
                    v_Builder.append("表中全部数据");
                }
            }
            else
            {
                v_Builder.append("库中全部表");
            }
        }
        else if ( !Help.isNull(this.pkID) )
        {
            if ( this.allowDelValue )
            {
                v_Builder.append("getdel(").append(this.pkID).append(")");
            }
            else
            {
                v_Builder.append(this.pkID);
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
     * @createDate  2025-08-10
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new CacheGetConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        CacheGetConfig v_Clone = new CacheGetConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.cacheXID        = this.cacheXID;
        v_Clone.dataBase        = this.dataBase;
        v_Clone.table           = this.table;
        v_Clone.pkID            = this.pkID;
        v_Clone.allowDelValue   = this.allowDelValue;
        v_Clone.rowClass        = this.rowClass;
        v_Clone.returnListOrMap = this.returnListOrMap;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
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
            throw new NullPointerException("Clone CacheGetConfig xid is null.");
        }
        
        CacheGetConfig v_Clone = (CacheGetConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.cacheXID        = this.cacheXID;
        v_Clone.dataBase        = this.dataBase;
        v_Clone.table           = this.table;
        v_Clone.pkID            = this.pkID;
        v_Clone.allowDelValue   = this.allowDelValue;
        v_Clone.rowClass        = this.rowClass;
        v_Clone.returnListOrMap = this.returnListOrMap;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-10
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
            throw new NullPointerException("Clone CacheGetConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        CacheGetConfig              v_Clone      = new CacheGetConfig();
        
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
