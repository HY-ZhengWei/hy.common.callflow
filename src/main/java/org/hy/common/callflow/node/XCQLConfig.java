package org.hy.common.callflow.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Busway;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.XCQLType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;
import org.hy.common.xcql.DBCQL;
import org.hy.common.xcql.XCQL;
import org.hy.common.xcql.XCQLData;
import org.hy.common.xml.XJava;





/**
 * XCQL图谱元素。衍生于执行元素， 图数据库的查询、插入、更新、删除、DDL、DML等操作。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-20
 * @version     v1.0
 */
public class XCQLConfig extends NodeConfig implements NodeConfigBase
{
    
    /** XCQL元素类型 */
    private XCQLType type;
    
    /**
     * 表示只取查询结果集中的首行记录。即只返回一个对象。
     * 
     * 只用于方法。
     * 只用于查询CQL，并且结果集的类型为List集合。
     */
    private boolean  returnOne;
    
    /** 是否支持分页查询 */
    private boolean  paging;
    
    /** 分页查询的对象 */
    private XCQL     xcqlPaging;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     */
    public XCQLConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public XCQLConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.type      = XCQLType.Auto;
        this.returnOne = false;
        this.paging    = false;
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
        // 它不能执行父类的静态检查，因为此时它的执行方法还没有确认，执行方法是在运行时确定的。
        /*
        if ( !super.check(io_Result) )
        {
            return false;
        }
        */
        
        // 执行对象不能为空
        if ( Help.isNull(this.getCallXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callXID is null.");
            return false;
        }
        
        if ( !Help.isNull(this.getCallParams()) )
        {
            int x = 0;
            for (NodeParam v_NodeParam : this.getCallParams())
            {
                x++;
                
                if ( v_NodeParam.getValue() == null && v_NodeParam.getValueDefault() == null )
                {
                    // 方法参数及默认值均会空时异常
                    io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callParams[" + x + "] value and valueDefault is null.");
                    return false;
                }
                
                if ( !Help.isNull(v_NodeParam.getValue()) )
                {
                    if ( !ValueHelp.isRefID(v_NodeParam.getValue()) )
                    {
                        if ( Help.isNull(v_NodeParam.getValueClass()) )
                        {
                            // 方法参数为数值类型时，参数类型应不会空
                            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callParams[" + x + "] value is Normal type ,but valueClass is null.");
                            return false;
                        }
                    }
                }
                
                if ( !Help.isNull(v_NodeParam.getValueDefault()) )
                {
                    if ( !ValueHelp.isRefID(v_NodeParam.getValueDefault()) )
                    {
                        if ( Help.isNull(v_NodeParam.getValueClass()) )
                        {
                            // 方法参数的默认值为数值类型时，参数类型应不会空
                            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].callParams[" + x + "] valueDefault is Normal type ,but valueClass is null.");
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        // 重写NodeConfig中方法的原因是：ElementType.XCQL.getValue() 中的首字母已有一个X了， 就没有必要重复出现
        return this.getElementType() + "_" + StringHelp.getUUID9n();
    }
    
    
    
    /**
     * 设置：执行对象的XID
     * 
     * @param i_CallXID 执行对象的XID
     */
    public void setCallXID(String i_CallXID)
    {
        synchronized (this)
        {
            if ( this.xcqlPaging != null && !Help.isNull(this.xcqlPaging.getXJavaID()) )
            {
                XCQL.removePaging(this.xcqlPaging.getXJavaID()); 
            }
            
            super.setCallXID(i_CallXID);
            this.xcqlPaging = null;
        }
    }


    
    /**
     * 获取：XCQL元素类型
     */
    public XCQLType getType()
    {
        return type;
    }
    
    
    
    /**
     * 设置：XCQL元素类型
     * 
     * @param i_Type XCQL元素类型
     */
    public void setType(XCQLType i_Type)
    {
        if ( i_Type == null )
        {
            this.type = XCQLType.Auto;
        }
        else
        {
            this.type = i_Type;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：表示只取查询结果集中的首行记录。即只返回一个对象。
     * 
     * 只用于方法。
     * 只用于查询CQL，并且结果集的类型为List集合。
     */
    public boolean isReturnOne()
    {
        return returnOne;
    }


    
    /**
     * 设置：表示只取查询结果集中的首行记录。即只返回一个对象。
     * 
     * 只用于方法。
     * 只用于查询CQL，并且结果集的类型为List集合。
     * 
     * @param i_ReturnOne  表示只取查询结果集中的首行记录。即只返回一个对象。
     */
    public void setReturnOne(boolean i_ReturnOne)
    {
        this.returnOne = i_ReturnOne;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 按XCQL元素类型初始化执行方法名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param i_ExecuteObject  执行对象
     */
    private void initCallMethod(Object i_ExecuteObject)
    {
        XCQLType v_XCQLType = this.type;
        
        if ( XCQLType.Auto.equals(v_XCQLType) )
        {
            if ( Help.isNull(this.getCallXID()) )
            {
                throw new RuntimeException("CallXID is null");
            }
            
            if ( i_ExecuteObject == null )
            {
                throw new RuntimeException("CallXID is not find");
            }
            else if ( i_ExecuteObject instanceof XCQL )
            {
                v_XCQLType = this.parserXCQLType((XCQL) i_ExecuteObject);
            }
            else
            {
                throw new RuntimeException("Unknown type：" + i_ExecuteObject.getClass().getName());
            }
        }
        
        if ( XCQLType.Create.equals(v_XCQLType) )
        {
            this.setCallMethod("executeInsert");
            
            if ( !Help.isNull(this.getCallParams()) )
            {
                String v_ValueClass = this.getCallParams().get(0).getValueClass();
                try
                {
                    // 批量数据时，需要用户显示说明类型为List，否则按单行数据处理
                    if ( !Help.isNull(v_ValueClass) )
                    {
                        Class<?> v_VClass = Help.forName(v_ValueClass);
                        if ( MethodReflect.isExtendImplement(v_VClass ,List.class) )
                        {
                            // 批量写
                            this.setCallMethod("executeInserts");
                        }
                    }
                }
                catch (Exception exce)
                {
                    throw new RuntimeException(exce);
                }
            }
        }
        else if ( XCQLType.Read.equals(v_XCQLType) )
        {
            this.setCallMethod("queryXCQLData");
        }
        else if ( XCQLType.Update.equals(v_XCQLType) 
               || XCQLType.Delete.equals(v_XCQLType) )
        {
            this.setCallMethod("executeUpdate");
            
            if ( !Help.isNull(this.getCallParams()) )
            {
                String v_ValueClass = this.getCallParams().get(0).getValueClass();
                try
                {
                    // 批量数据时，需要用户显示说明类型为List，否则按单行数据处理
                    if ( !Help.isNull(v_ValueClass) )
                    {
                        Class<?> v_VClass = Help.forName(v_ValueClass);
                        if ( MethodReflect.isExtendImplement(v_VClass ,List.class) )
                        {
                            // 批量更新
                            this.setCallMethod("executeUpdates");
                        }
                    }
                }
                catch (Exception exce)
                {
                    throw new RuntimeException(exce);
                }
            }
        }
        else
        {
            this.setCallMethod("execute");
        }
    }
    
    
    
    /**
     * 解释XCQL类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param i_XCQL  XCQL对象
     * @return
     */
    private XCQLType parserXCQLType(XCQL i_XCQL)
    {
        int v_CQLType = i_XCQL.getContent().getCQLType();
        
        if ( DBCQL.$DBCQL_TYPE_CREATE == v_CQLType )
        {
            return XCQLType.Create;
        }
        else if ( DBCQL.$DBCQL_TYPE_MATCH == v_CQLType )
        {
            return XCQLType.Read;
        }
        else if ( DBCQL.$DBCQL_TYPE_SET == v_CQLType )
        {
            return XCQLType.Update;
        }
        else if ( DBCQL.$DBCQL_TYPE_DELETE == v_CQLType )
        {
            return XCQLType.Delete;
        }
        else if ( DBCQL.$DBCQL_TYPE_DDL == v_CQLType )
        {
            return XCQLType.DDL;
        }
        else
        {
            throw new RuntimeException("Unknown CQL type：" + v_CQLType);
        }
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.XCQL.getValue();
    }
   
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.XCQL.getXmlName();
    }
    
    
    
    /**
     * 转XML时是否显示retFalseIsError属性
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public boolean xmlShowRetFalseIsError()
    {
        return false;
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        synchronized (this) 
        {
            this.initCallMethod(io_ExecuteObject);
            
            if ( this.paging && this.xcqlPaging == null )
            {
                XCQL v_XCQL     = (XCQL) io_ExecuteObject;
                this.xcqlPaging = XCQL.queryPaging(v_XCQL ,true);
                return this.xcqlPaging;
            }
        }
        return io_ExecuteObject;
    }
    
    
    
    /**
     * 执行成功时，对执行结果的处理
     * 
     * 注：此时执行结果还没有保存到上下文中
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteReturn  执行结果。已用NodeConfig自己的力量获取了执行结果。
     * @return                  Return.get()          是否执行成功
     *                          Return.getParamObj()  执行结果
     *                          Return.getException() 执行异常
     * @throws Exception 
     */
    public Return<Object> generateReturn(Map<String ,Object> io_Context ,Object io_ExecuteReturn)
    {
        if ( io_ExecuteReturn instanceof XCQLData )
        {
            if ( this.returnOne )
            {
                Object v_Datas = ((XCQLData) io_ExecuteReturn).getDatas();
                return new Return<Object>(true).setParamObj(this.generateReturnOne(v_Datas));
            }
            else
            {
                return new Return<Object>(true).setParamObj(((XCQLData) io_ExecuteReturn).getDatas());
            }
        }
        else
        {
            return new Return<Object>(true).setParamObj(io_ExecuteReturn);
        }
    }
    
    
    
    /**
     * 只返回一个对象。即万里挑一
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object generateReturnOne(Object i_Datas)
    {
        Object v_Datas = i_Datas;
        
        if ( MethodReflect.isExtendImplement(v_Datas ,List.class) )
        {
            List<?> v_List = (List<?>)v_Datas;
            
            if ( v_List.size() >= 1 )
            {
                v_Datas = v_List.get(0);
                v_List.clear();
                v_List = null;
            }
            else
            {
                v_Datas = null;
            }
        }
        // 支持Set集合随机获取一个元素的功能
        else if ( MethodReflect.isExtendImplement(v_Datas ,Set.class) )
        {
            Set<?> v_Set = (Set<?>)v_Datas;
            
            if ( v_Set.size() >= 1 )
            {
                v_Datas = v_Set.iterator().next();
                v_Set.clear();
                v_Set = null;
            }
            else
            {
                v_Datas = null;
            }
        }
        // 支持Map集合随机获取一个元素的功能
        else if ( MethodReflect.isExtendImplement(v_Datas ,Map.class) )
        {
            Map<? ,?> v_Map = (Map<? ,?>)v_Datas;
            
            if ( v_Map.size() >= 1 )
            {
                v_Datas = v_Map.values().iterator().next();
                
                if ( v_Datas != null )
                {
                    // 2024-01-03 Add 预防类似 TablePartitionBusway 结构的数据的 clear() 方法有逐级删除的能力
                    if ( v_Datas instanceof Busway )
                    {
                        v_Datas = new Busway<Object>((Busway<Object>)v_Datas);
                    }
                    // 2024-01-03 Add 预防类似 TablePartitionRID 结构的数据的 clear() 方法有逐级删除的能力
                    if ( MethodReflect.isExtendImplement(v_Datas ,Map.class) )
                    {
                        v_Datas = new LinkedHashMap<Object ,Object>((Map<? ,?>)v_Datas);
                    }
                    // 2024-01-03 Add 预防类似 TablePartitionSet 结构的数据的 clear() 方法有逐级删除的能力
                    else if ( MethodReflect.isExtendImplement(v_Datas ,Set.class) )
                    {
                        v_Datas = new HashSet<Object>((Set<?>)v_Datas);
                    }
                    // 2024-01-03 Add 预防类似 TablePartition \ TablePartitionLink 结构的数据的 clear() 方法有逐级删除的能力
                    else if ( MethodReflect.isExtendImplement(v_Datas ,List.class) )
                    {
                        v_Datas = new ArrayList<Object>((List<?>)v_Datas);
                    }
                }
                
                v_Map.clear();
                v_Map = null;
            }
            else
            {
                v_Datas = null;
            }
        }
        // 支持Collection集合随机获取一个元素的功能
        else if ( MethodReflect.isExtendImplement(v_Datas ,Collection.class) )
        {
            Collection<?> v_Collection = (Collection<?>)v_Datas;
            
            if ( v_Collection.size() >= 1 )
            {
                v_Datas = v_Collection.iterator().next();
                v_Collection.clear();
                v_Collection = null;
            }
            else
            {
                v_Datas = null;
            }
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @param io_Xml         XML内容的缓存区
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_Level1       单级层级的空格间隔
     * @param i_LevelN       N级层级的空格间隔
     * @param i_SuperTreeID  父级树ID
     * @param i_TreeID       当前树ID
     */
    public void toXmlContent(StringBuilder io_Xml ,int i_Level ,String i_Level1 ,String i_LevelN ,String i_SuperTreeID ,String i_TreeID)
    {
        if ( this.type != null && !XCQLType.Auto.equals(this.type) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("type"    ,this.type.getValue()));
        }
        if ( !Help.isNull(this.getCallXID()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("callXID" ,this.getCallXID()));
        }
        if ( this.paging )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("paging"  ,this.paging));
        }
        if ( !Help.isNull(this.getCallParams()) )
        {
            for (NodeParam v_Param : this.getCallParams())
            {
                io_Xml.append(v_Param.toXml(i_Level + 1 ,i_TreeID));
            }
        }
        if ( this.returnOne )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("returnOne" ,this.returnOne));
        }
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
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
        if ( !Help.isNull(this.callXID) )
        {
            v_Builder.append(this.callXID);
            if ( XJava.getObject(this.callXID) == null )
            {
                v_Builder.append(" is NULL");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( Help.isNull(this.callMethod) )
        {
            initCallMethod(XJava.getObject(this.callXID));
        }
        if ( !Help.isNull(this.callMethod) )
        {
            v_Builder.append(this.callMethod);
            this.init(i_Context);
            if ( this.callMethodObject == null )
            {
                v_Builder.append(" not find");
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("(");
        
        if ( !Help.isNull(this.callParams) )
        {
            for (int x=0; x<this.callParams.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ,");
                }
                v_Builder.append(this.callParams.get(x).toString(i_Context));
            }
        }
        
        v_Builder.append(")");
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
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
        if ( !Help.isNull(this.callXID) )
        {
            v_Builder.append(this.callXID);
        }
        else
        {
            v_Builder.append("?");
        }
        v_Builder.append(ValueHelp.$Split);
        
        if ( Help.isNull(this.callMethod) )
        {
            try
            {
                initCallMethod(XJava.getObject(this.callXID));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        if ( !Help.isNull(this.callMethod) )
        {
            v_Builder.append(this.callMethod);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("(");
        
        if ( !Help.isNull(this.callParams) )
        {
            for (int x=0; x<this.callParams.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ,");
                }
                v_Builder.append(this.callParams.get(x).toString());
            }
        }
        
        v_Builder.append(")");
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new XCQLConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        XCQLConfig v_Clone = new XCQLConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.setCallXID(this.getCallXID());
        if ( !Help.isNull(this.getCallParams()) )
        {
            v_Clone.setCallParams(new ArrayList<NodeParam>());
            for (NodeParam v_NodeParam : this.getCallParams())
            {
                v_Clone.getCallParams().add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
        v_Clone.setTimeout(this.getTimeout());
        v_Clone.setContext(this.getContext());
        v_Clone.type      = this.type;
        v_Clone.returnOne = this.returnOne;
        v_Clone.paging    = this.paging;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
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
            throw new NullPointerException("Clone XCQLConfig xid is null.");
        }
        
        XCQLConfig v_Clone = (XCQLConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.setCallXID(this.getCallXID());
        if ( !Help.isNull(this.getCallParams()) )
        {
            v_Clone.setCallParams(new ArrayList<NodeParam>());
            for (NodeParam v_NodeParam : this.getCallParams())
            {
                v_Clone.getCallParams().add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
        v_Clone.setTimeout(this.getTimeout());
        v_Clone.setContext(this.getContext());
        v_Clone.type      = this.type;
        v_Clone.returnOne = this.returnOne;
        v_Clone.paging    = this.paging;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-20
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone XCQLConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        XCQLConfig                  v_Clone      = new XCQLConfig();
        
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
