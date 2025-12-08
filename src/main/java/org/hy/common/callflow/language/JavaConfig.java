package org.hy.common.callflow.language;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.hy.common.callflow.language.java.CacheJavaFileManager;
import org.hy.common.callflow.language.java.CacheJavaInfo;
import org.hy.common.db.DBSQL;





/**
 * 爪哇元素：编译Java源代码动态构建运行时的类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-03
 * @version     v1.0
 */
public class JavaConfig extends ExecuteElement implements Cloneable
{
    
    /** 包路径的正则表达式 */
    private static final String $RegPackageName = "^\\s*package\\s+([a-zA-Z_$][a-zA-Z0-9_$]*(\\.[a-zA-Z_$][a-zA-Z0-9_$]*)*)\\s*;";
    
    /** 类名称的正则表达式 */
    private static final String $RegCalssname   = "\\b(?:public\\s+)?class\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\s*(?:extends|implements|\\{|;)";
    
    
    
    /** Java代码 */
    private String                        java;
    
    /** Java代码，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> javaPlaceholders;
    
    /** 是否允许重复解析源码加载类。默认值：不允许重复解析加载 */
    private boolean                       reload;
    
    
    
    public JavaConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public JavaConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.reload = false;
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        if ( Help.isNull(this.getJava()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].java is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        return "XJava_" + StringHelp.getUUID9n();
    }


    
    /**
     * 获取：Java代码
     */
    public String getJava()
    {
        return java;
    }
    
    
    
    /**
     * 运行时环境中获取Java代码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_Context   上下文类型的变量信息
     * @throws Exception 
     */
    private String gatJava(Map<String ,Object> i_Context) throws Exception
    {
        return (String) ValueHelp.getValueReplace(this.java ,this.javaPlaceholders ,String.class ,"" ,i_Context);
    }


    
    /**
     * 设置：Java代码
     * 
     * @param i_Java Java代码
     */
    public void setJava(String i_Java)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Java) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Java ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.javaPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.javaPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.java = i_Java;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：是否允许重复解析源码加载类。默认值：不允许重复解析加载
     */
    public boolean isReload()
    {
        return reload;
    }


    
    /**
     * 设置：是否允许重复解析源码加载类。默认值：不允许重复解析加载
     * 
     * @param i_Reload 是否允许重复解析源码加载类。默认值：不允许重复解析加载
     */
    public void setReload(boolean i_Reload)
    {
        this.reload = i_Reload;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Java.getValue();
    }
    
    
    
    /**
     * 解释Java代码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param io_Context     上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private synchronized CacheJavaInfo parserJava(Map<String ,Object> io_Context) throws Exception
    {
        CacheJavaInfo v_Java = null;
        if ( Help.isNull(this.java) )
        {
            return v_Java;
        }
        
        v_Java = new CacheJavaInfo();
        v_Java.setSourceCode(this.gatJava(io_Context));
        
        Pattern v_PackagePattern = Pattern.compile($RegPackageName ,Pattern.MULTILINE);
        Matcher v_PackageMatcher = v_PackagePattern.matcher(v_Java.getSourceCode());
        if ( v_PackageMatcher.find() ) 
        {
            v_Java.setPackageName(v_PackageMatcher.group(1));
        }
        
        // 忽略大小写（防止出现Class等写法，实际Java规范要求小写）
        Pattern v_ClassPattern = Pattern.compile($RegCalssname,Pattern.CASE_INSENSITIVE); 
        Matcher v_ClassMatcher = v_ClassPattern.matcher(v_Java.getSourceCode());
        
        while ( v_ClassMatcher.find() )
        {
            String v_CurrentClassName = v_ClassMatcher.group(1);
            // 优先取public修饰的类（Java规范中一个文件仅一个public类）
            String v_Modifier = v_ClassMatcher.group(0).substring(0 ,v_ClassMatcher.group(0).indexOf("class")).trim();
            
            if ( "public".equals(v_Modifier) )
            {
                v_Java.setClassName(v_CurrentClassName);
                break; // 找到public类后直接退出，无需继续匹配
            }
            
            // 无public类时，取第一个顶级类
            if ( Help.isNull(v_Java.getClassName()) )
            {
                v_Java.setClassName(v_CurrentClassName);
            }
        }
        
        return v_Java;
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
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
        
        // 编排异常后续跑
        if ( !this.redo(io_Context ,v_BeginTime ,v_Result) )
        {
            return v_Result;
        }
        
        // Mock模拟
        if ( super.mock(io_Context ,v_BeginTime ,v_Result ,null ,HashMap.class.getName()) )
        {
            return v_Result;
        }
        
        try
        {
            CacheJavaInfo v_Java = this.parserJava(io_Context);
            if ( v_Java == null || Help.isNull(v_Java.getPackageName()) || Help.isNull(v_Java.getClassName()) )
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s 解析Java源码失败."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            String   v_ClassName = v_Java.getClassNameFull();
            Class<?> v_Class     = null;
            if ( !this.reload )
            {
                v_Class = Help.forName(v_ClassName);
                if ( v_Class != null )
                {
                    // 不再重复解析源码、不再重复加载类
                    v_Result.setResult(v_Java);
                    this.refreshReturn(io_Context ,v_Result.getResult());
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    this.success(Date.getTimeNano() - v_BeginTime);
                    return v_Result;
                }
            }
            
            CacheJavaFileManager v_CacheJavaManager = CacheJavaFileManager.getInstanceof();
            if ( v_CacheJavaManager == null ) 
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s 未找到编译器（请使用JDK而非JRE）."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            boolean v_CompileSuccess = v_CacheJavaManager.compiler(v_Java.getClassNameFull() ,v_Java.getSourceCode());
            if ( !v_CompileSuccess )
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] 类[" + v_ClassName + "]编译失败."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            v_Class = v_CacheJavaManager.loadClass(v_ClassName);
            if ( v_Class == null )
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] 类[" + v_ClassName + "]加载失败."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            v_Result.setResult(v_Java);
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
     * @createDate  2025-12-03
     * @version     v1.0
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
        String        v_XName    = ElementType.Java.getXmlName();
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
            if ( !Help.isNull(this.java) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("java" ,this.java));
            }
            if ( !Help.isNull(this.reload) && this.reload )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("reload" ,this.reload));
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
                
                // 真时的路由
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
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        // 没有入参，因而不做特征显示
        return "";
    }
    
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        // 没有入参，因而不做特征显示
        return "";
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new JavaConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        JavaConfig v_Clone = new JavaConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.java   = this.java;
        v_Clone.reload = this.reload;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
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
            throw new NullPointerException("Clone JavaConfig xid is null.");
        }
        
        JavaConfig v_Clone = (JavaConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.java   = this.java;
        v_Clone.reload = this.reload;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-03
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
            throw new NullPointerException("Clone JavaConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        JavaConfig                  v_Clone      = new JavaConfig();
        
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
