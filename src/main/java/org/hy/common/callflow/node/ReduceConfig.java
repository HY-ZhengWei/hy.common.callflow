package org.hy.common.callflow.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;





/**
 * 归纳元素：将多个Map集合归纳成一个大Map集合；或将多个List集合归纳成一个大List集合；或将多个Set集合归纳成一个大Set集合；或将多个数组归纳成一个大数组；
 * 
 * 归纳原因：当为指定归纳类型时（bigType<=0），首个非空数据的类型，决定归纳成一个大集合的类型。
 *         如，首个非空数据类型是List，将按List归纳成一个大集合；
 *         如，首个非空数据类型是Set，将按Set归纳成一个大集合；
 *         如，首个非空数据类型是Array，将按Array归纳成一个大集合；
 *         如，首个非空数据类型是Map，将按Map归纳成一个大集合；
 *         如，首个非空数据类型是Object，将按Map归纳成一个大集合；并且，Map.key为数据的变量ID
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-11
 * @version     v1.0
 */
public class ReduceConfig extends NodeConfig implements NodeConfigBase
{
    
    /** 要归纳的集合变量ID。多个ID间用英文逗号分隔，将每变量ID前要用占位符:冒号。区分大小写 */
    private String  ids;
    
    /** 要归纳的集合变量ID的前缀符合条件的要求。不要写占位符:冒号。区分大小写 */
    private String  idPrefix;
    
    /** 要归纳的集合变量ID的后缀符合条件的要求。不要写占位符:冒号。区分大小写 */
    private String  idSuffix;
    
    /** 归纳类型（1:Map；2:List；3:Set；4:Array；-1:自动识别）。当未指定时，将在运行时参考归纳原因自动识别 */
    private Integer bigType;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     */
    public ReduceConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public ReduceConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.setCallMethod("reduce");
        this.setRetFalseIsError(true);
        
        NodeParam v_ContextParam = new NodeParam();
        v_ContextParam.setValueClass(Map.class.getName());
        v_ContextParam.setValue(DBSQL.$Placeholder + CallFlow.$Context);
        this.setCallParam(v_ContextParam);
        this.bigType = -1;
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        if ( !super.check(io_Result) )
        {
            return false;
        }
        
        if ( Help.isNull(this.getIds()) && Help.isNull(this.getIdPrefix()) && Help.isNull(this.getIdSuffix()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].ids idPrefix and idSuffix is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param io_Context   上下文类型的变量信息
     * @param i_BeginTime  编排元素的开始时间
     * @param io_Result    编排元素的执行结果
     * @return             表示是否有模拟数据
     */
    public boolean mock(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result) 
    {
        return super.mock(io_Context ,i_BeginTime ,io_Result ,null ,HashMap.class.getName());
    }
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        return "XReduce_" + StringHelp.getUUID9n();
    }

    
    
    /**
     * 获取：要归纳的集合变量ID。多个ID间用英文逗号分隔，将每变量ID前要用占位符:冒号
     */
    public String getIds()
    {
        return ids;
    }


    
    /**
     * 设置：要归纳的集合变量ID。多个ID间用英文逗号分隔，将每变量ID前要用占位符:冒号。区分大小写
     * 
     * @param i_Ids 要归纳的集合变量ID。多个ID间用英文逗号分隔，将每变量ID前要用占位符:冒号。区分大小写
     */
    public void setIds(String i_Ids)
    {
        this.ids = i_Ids;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：要归纳的集合变量ID的前缀符合条件的要求。不要写占位符:冒号。区分大小写
     */
    public String getIdPrefix()
    {
        return idPrefix;
    }


    
    /**
     * 设置：要归纳的集合变量ID的前缀符合条件的要求。不要写占位符:冒号。区分大小写
     * 
     * @param i_IdPrefix 要归纳的集合变量ID的前缀符合条件的要求。不要写占位符:冒号。区分大小写
     */
    public void setIdPrefix(String i_IdPrefix)
    {
        this.idPrefix = i_IdPrefix;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：要归纳的集合变量ID的后缀符合条件的要求。不要写占位符:冒号。区分大小写
     */
    public String getIdSuffix()
    {
        return idSuffix;
    }


    
    /**
     * 设置：要归纳的集合变量ID的后缀符合条件的要求。不要写占位符:冒号。区分大小写
     * 
     * @param i_IdSuffix 要归纳的集合变量ID的后缀符合条件的要求。不要写占位符:冒号。区分大小写
     */
    public void setIdSuffix(String i_IdSuffix)
    {
        this.idSuffix = i_IdSuffix;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：归纳类型（1:Map；2:List；3:Set；4:Array；-1:自动识别）。当未指定时，将在运行时参考归纳原因自动识别
     */
    public Integer getBigType()
    {
        return bigType;
    }


    
    /**
     * 设置：归纳类型（1:Map；2:List；3:Set；4:Array；-1:自动识别）。当未指定时，将在运行时参考归纳原因自动识别
     * 
     * @param i_BigType 归纳类型（1:Map；2:List；3:Set；4:Array；-1:自动识别）。当未指定时，将在运行时参考归纳原因自动识别
     */
    public void setBigType(Integer i_BigType)
    {
        if ( i_BigType == null || i_BigType <= 0 )
        {
            this.bigType = -1;
        }
        else
        {
            this.bigType = Help.min(i_BigType ,4);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * 自己反射调用自己的实例中的方法
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_Xid)
    {
        super.setXJavaID(i_Xid);
        this.setCallXID(this.getXid());
    }
    
    
    
    /**
     * 设置：全局惟一标识ID
     * 
     * 自己反射调用自己的实例中的方法
     * 
     * @param i_Xid 全局惟一标识ID
     */
    public void setXid(String i_Xid)
    {
        super.setXid(i_Xid);
        this.setCallXID(this.getXid());
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Reduce.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Reduce.getXmlName();
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
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        // 其实就是返回自己。io_ExecuteObject 获取正确时，也是this自己
        return io_ExecuteObject == null ? this : io_ExecuteObject;
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        return io_Params;
    }
    
    
    
    /**
     * 创建压缩文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public Object reduce(Map<String ,Object> i_Context)
    {
        int                 v_Count   = 0;
        int                 v_BigType = this.getBigType();
        Map<String ,Object> v_BigMap  = new LinkedHashMap<String ,Object>();
        List<Object>        v_BigList = new ArrayList<Object>();
        Set<Object>         v_BigSet  = new LinkedHashSet<Object>();
        
        if ( !Help.isNull(this.getIds()) )
        {
            String [] v_IDs = this.getIds().split(",");
            for (String v_Item : v_IDs)
            {
                String v_ID = ValueHelp.standardValueID(v_Item);
                if ( Help.isNull(v_ID) )
                {
                    continue;
                }
                
                Object v_Datas = i_Context.get(v_ID);
                if ( v_Datas == null )
                {
                    continue;
                }
                
                int v_AddRet = this.addBig(v_BigType ,v_BigMap ,v_BigList ,v_BigSet ,v_Datas ,v_ID);
                if ( v_AddRet >= 1 )
                {
                    v_BigType = v_AddRet;
                    v_Count++;
                }
            }
        }
        
        if ( !Help.isNull(this.getIdPrefix()) && !Help.isNull(this.getIdSuffix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                Object v_Datas = v_Item.getValue();
                if ( v_Datas == null )
                {
                    continue;
                }
                
                String v_ID = v_Item.getKey();
                if ( v_ID.startsWith(this.getIdPrefix())
                  && v_ID.endsWith(  this.getIdSuffix()) )
                {
                    int v_AddRet = this.addBig(v_BigType ,v_BigMap ,v_BigList ,v_BigSet ,v_Datas ,v_ID);
                    if ( v_AddRet >= 1 )
                    {
                        v_BigType = v_AddRet;
                        v_Count++;
                    }
                }
            }
        }
        else if ( !Help.isNull(this.getIdPrefix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                Object v_Datas = v_Item.getValue();
                if ( v_Datas == null )
                {
                    continue;
                }
                
                String v_ID = v_Item.getKey();
                if ( v_ID.startsWith(this.getIdPrefix()) )
                {
                    int v_AddRet = this.addBig(v_BigType ,v_BigMap ,v_BigList ,v_BigSet ,v_Datas ,v_ID);
                    if ( v_AddRet >= 1 )
                    {
                        v_BigType = v_AddRet;
                        v_Count++;
                    }
                }
            }
        }
        else if ( !Help.isNull(this.getIdSuffix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                Object v_Datas = v_Item.getValue();
                if ( v_Datas == null )
                {
                    continue;
                }
                
                String v_ID = v_Item.getKey();
                if ( v_ID.endsWith(this.getIdSuffix()) )
                {
                    int v_AddRet = this.addBig(v_BigType ,v_BigMap ,v_BigList ,v_BigSet ,v_Datas ,v_ID);
                    if ( v_AddRet >= 1 )
                    {
                        v_BigType = v_AddRet;
                        v_Count++;
                    }
                }
            }
        }
        
        if ( v_Count <= 0 )
        {
            v_BigMap .clear();
            v_BigList.clear();
            v_BigSet .clear();
            v_BigMap  = null;
            v_BigList = null;
            v_BigSet  = null;
            return null;
        }
        else if ( v_BigType == 1 )
        {
            v_BigList.clear();
            v_BigSet .clear();
            v_BigList = null;
            v_BigSet  = null;
            return v_BigMap;
        }
        else if ( v_BigType == 2 )
        {
            v_BigMap .clear();
            v_BigSet .clear();
            v_BigMap  = null;
            v_BigSet  = null;
            return v_BigList;
        }
        else if ( v_BigType == 3 )
        {
            v_BigMap .clear();
            v_BigList.clear();
            v_BigMap  = null;
            v_BigList = null;
            return v_BigSet;
        }
        else if ( v_BigType == 4 )
        {
            Object[] v_BigArr = v_BigList.toArray();
            v_BigMap .clear();
            v_BigList.clear();
            v_BigSet .clear();
            v_BigMap  = null;
            v_BigList = null;
            v_BigSet  = null;
            return v_BigArr;
        }
        else
        {
            v_BigMap .clear();
            v_BigList.clear();
            v_BigSet .clear();
            v_BigMap  = null;
            v_BigList = null;
            v_BigSet  = null;
            return null;
        }
    }
    
    
    
    /**
     * 每个小集合向统一的大集合中归纳
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param i_BigType   大集合的类型。1:Map；2:List；3:Set；4:Array；其它无效
     * @param io_BigMap   大集合的Map类型
     * @param io_BigList  大集合的List类型，同时兼容数组类型
     * @param io_BigSet   大集合的Set类型
     * @param i_Datas     小集合数据
     * @param i_DatasID   小集合数据的变量ID
     * @return            正确归纳时，返回大集合的类型。
     *                    异常时，返回-1；
     *                      异常情况1：首个小集合i_Datas，不是Map、List、Set、Array类型时
     *                      异常情况2：小集合类型与大集合类型不一致时
     */
    @SuppressWarnings("unchecked")
    private int addBig(int i_BigType ,Map<String ,Object> io_BigMap ,List<Object> io_BigList ,Set<Object> io_BigSet ,Object i_Datas ,String i_DatasID)
    {
        int v_BigType = i_BigType;
        if ( v_BigType <= 0 )
        {
            if ( MethodReflect.isExtendImplement(i_Datas ,Map.class) )
            {
                v_BigType = 1;
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,List.class) )
            {
                v_BigType = 2;
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,Set.class) )
            {
                v_BigType = 3;
            }
            else if ( i_Datas.getClass().isArray() )
            {
                v_BigType = 4;
            }
            else
            {
                // 首个非空数据类型是Object，将按Map归纳成一个大集合；
                return 1;
            }
        }
        
        if ( v_BigType == 1 )
        {
            if ( MethodReflect.isExtendImplement(i_Datas ,Map.class) )
            {
                io_BigMap.putAll((Map<String ,Object>) i_Datas);
            }
            else
            {
                io_BigMap.put(i_DatasID ,i_Datas);
            }
        }
        else if ( v_BigType == 2 )
        {
            if ( MethodReflect.isExtendImplement(i_Datas ,Map.class) )
            {
                io_BigList.addAll(((Map<String ,Object>) i_Datas).values());
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,List.class) )
            {
                io_BigList.addAll((List<Object>) i_Datas);
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,Set.class) )
            {
                io_BigList.addAll((Set<Object>) i_Datas);
            }
            else if ( i_Datas.getClass().isArray() )
            {
                Object [] v_DatasArray = (Object []) i_Datas;
                io_BigList.addAll(Arrays.asList(v_DatasArray));
            }
            else
            {
                io_BigList.add(i_Datas);
            }
        }
        else if ( v_BigType == 3 )
        {
            if ( MethodReflect.isExtendImplement(i_Datas ,Map.class) )
            {
                io_BigSet.addAll(((Map<String ,Object>) i_Datas).values());
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,List.class) )
            {
                io_BigSet.addAll((List<Object>) i_Datas);
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,Set.class) )
            {
                io_BigSet.addAll((Set<Object>) i_Datas);
            }
            else if ( i_Datas.getClass().isArray() )
            {
                Object [] v_DatasArray = (Object []) i_Datas;
                io_BigSet.addAll(Arrays.asList(v_DatasArray));
            }
            else
            {
                io_BigSet.add(i_Datas);
            }
        }
        else if ( v_BigType == 4 )
        {
            if ( MethodReflect.isExtendImplement(i_Datas ,Map.class) )
            {
                io_BigList.addAll(((Map<String ,Object>) i_Datas).values());
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,List.class) )
            {
                io_BigList.addAll((List<Object>) i_Datas);
            }
            else if ( MethodReflect.isExtendImplement(i_Datas ,Set.class) )
            {
                io_BigList.addAll((Set<Object>) i_Datas);
            }
            else if ( i_Datas.getClass().isArray() )
            {
                Object [] v_DatasArray = (Object []) i_Datas;
                io_BigList.addAll(Arrays.asList(v_DatasArray));
            }
            else
            {
                io_BigList.add(i_Datas);
            }
        }
        
        return v_BigType;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
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
        String v_NewSpace = "\n" + i_LevelN + i_Level1;
        
        if ( !Help.isNull(this.getIds()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("ids"      ,this.getIds()));
        }
        if ( !Help.isNull(this.getIdPrefix()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("idPrefix" ,this.getIdPrefix()));
        }
        if ( !Help.isNull(this.getIdSuffix()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("idSuffix" ,this.getIdSuffix()));
        }
        if ( !Help.isNull(this.getBigType()) && this.getBigType() >= 1 )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("bigType"  ,this.getBigType()));
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
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        int           v_Count   = 0;
        
        if ( this.bigType == 1 )
        {
            v_Builder.append("归纳为Map ");
        }
        else if ( this.bigType == 2 )
        {
            v_Builder.append("归纳为List ");
        }
        else if ( this.bigType == 3 )
        {
            v_Builder.append("归纳为Set ");
        }
        else if ( this.bigType == 4 )
        {
            v_Builder.append("归纳为Array ");
        }
        else
        {
            v_Builder.append("自动识别归纳类型 ");
        }
        
        if ( !Help.isNull(this.getIds()) )
        {
            String [] v_IDs = this.getIds().split(",");
            for (String v_Item : v_IDs)
            {
                String v_ID = ValueHelp.standardValueID(v_Item);
                if ( Help.isNull(v_ID) )
                {
                    continue;
                }
                
                Object v_Value = i_Context.get(v_ID);
                if ( v_Value == null )
                {
                    continue;
                }
                
                if ( v_Count >= 1 )
                {
                    v_Builder.append(",");
                }
                v_Builder.append(DBSQL.$Placeholder + v_ID);
                v_Count++;
            }
        }
        
        if ( !Help.isNull(this.getIdPrefix()) && !Help.isNull(this.getIdSuffix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                if ( v_Item.getValue() == null )
                {
                    continue;
                }
                
                if ( v_Item.getKey().startsWith(this.getIdPrefix())
                  && v_Item.getKey().endsWith(  this.getIdSuffix()) )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Builder.append(",");
                    }
                    v_Builder.append(DBSQL.$Placeholder + v_Item.getKey());
                    v_Count++;
                }
            }
        }
        else if ( !Help.isNull(this.getIdPrefix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                if ( v_Item.getValue() == null )
                {
                    continue;
                }
                
                if ( v_Item.getKey().startsWith(this.getIdPrefix()) )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Builder.append(",");
                    }
                    v_Builder.append(DBSQL.$Placeholder + v_Item.getKey());
                    v_Count++;
                }
            }
        }
        else if ( !Help.isNull(this.getIdSuffix()) )
        {
            for (Map.Entry<String ,Object> v_Item : i_Context.entrySet())
            {
                if ( v_Item.getValue() == null )
                {
                    continue;
                }
                
                if ( v_Item.getKey().endsWith(this.getIdSuffix()) )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Builder.append(",");
                    }
                    v_Builder.append(DBSQL.$Placeholder + v_Item.getKey());
                    v_Count++;
                }
            }
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        int           v_Count   = 0;
        
        if ( !Help.isNull(this.getIds()) )
        {
            v_Builder.append(this.getIds());
            v_Count++;
        }
        
        if ( !Help.isNull(this.getIdPrefix()) && !Help.isNull(this.getIdSuffix()) )
        {
            if ( v_Count >= 1 )
            {
                v_Builder.append(",");
            }
            v_Builder.append(DBSQL.$Placeholder).append(this.getIdPrefix()).append("*").append(this.getIdSuffix());
            v_Count++;
        }
        else if ( !Help.isNull(this.getIdPrefix()) )
        {
            if ( v_Count >= 1 )
            {
                v_Builder.append(",");
            }
            v_Builder.append(DBSQL.$Placeholder).append(this.getIdPrefix()).append("*");
            v_Count++;
        }
        else if ( !Help.isNull(this.getIdSuffix()) )
        {
            if ( v_Count >= 1 )
            {
                v_Builder.append(",");
            }
            v_Builder.append(DBSQL.$Placeholder).append("*").append(this.getIdSuffix());
            v_Count++;
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0 
     *
     * @return
     */
    public Object newMy()
    {
        return new ReduceConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        ReduceConfig v_Clone = new ReduceConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.ids         = this.ids;
        v_Clone.idPrefix    = this.idPrefix;
        v_Clone.idSuffix    = this.idSuffix;
        v_Clone.bigType     = this.bigType;
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                v_Clone.callParams.add((NodeParam) v_NodeParam.cloneMyOnly());
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
     * @createDate  2026-08-11
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
            throw new NullPointerException("Clone ReduceConfig xid is null.");
        }
        
        ReduceConfig v_Clone = (ReduceConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.ids         = this.ids;
        v_Clone.idPrefix    = this.idPrefix;
        v_Clone.idSuffix    = this.idSuffix;
        v_Clone.bigType     = this.bigType;
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                v_Clone.callParams.add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-08-11
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
            throw new NullPointerException("Clone ReduceConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ReduceConfig                v_Clone      = new ReduceConfig();
        
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
