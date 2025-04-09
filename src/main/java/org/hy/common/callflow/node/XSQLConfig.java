package org.hy.common.callflow.node;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Return;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.XSQLType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XSQL;
import org.hy.common.xml.plugins.XSQLGroup;





/**
 * XSQL元素。衍生于执行元素， 数据库的查询、插入、更新、删除、DDL、DML、XSQL组等操作。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-04-09
 * @version     v1.0
 */
public class XSQLConfig extends NodeConfig implements NodeConfigBase
{
    
    /** XSQL元素类型 */
    private XSQLType type;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     */
    public XSQLConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public XSQLConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.type = XSQLType.Auto;
    }


    
    /**
     * 获取：XSQL元素类型
     */
    public XSQLType getType()
    {
        return type;
    }


    
    /**
     * 设置：XSQL元素类型
     * 
     * @param i_Type XSQL元素类型
     */
    public void setType(XSQLType i_Type)
    {
        if ( i_Type == null )
        {
            this.type = XSQLType.Auto;
        }
        else
        {
            this.type = i_Type;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 按XSQL元素类型初始化执行方法名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @param i_ExecuteObject  执行对象
     */
    private void initCallMethod(Object i_ExecuteObject)
    {
        XSQLType v_XSQLType = this.type;
        
        if ( XSQLType.Auto.equals(v_XSQLType) )
        {
            if ( Help.isNull(this.getCallXID()) )
            {
                throw new RuntimeException("CallXID is null");
            }
            
            if ( i_ExecuteObject == null )
            {
                throw new RuntimeException("CallXID is not find");
            }
            else if ( i_ExecuteObject instanceof XSQL )
            {
                v_XSQLType = this.parserXSQLType((XSQL) i_ExecuteObject);
            }
            else if ( i_ExecuteObject instanceof XSQLGroup )
            {
                v_XSQLType = XSQLType.XSQLGroup;
            }
            else
            {
                throw new RuntimeException("Unknown type：" + i_ExecuteObject.getClass().getName());
            }
        }
        
        if ( XSQLType.XSQLGroup.equals(v_XSQLType) )
        {
            this.setCallMethod("executes");
        }
        else if ( XSQLType.Create.equals(v_XSQLType) )
        {
            this.setCallMethod("executeInsertPrepared");
            
            if ( !Help.isNull(this.getCallParams()) )
            {
                String v_ValueClass = this.getCallParams().get(0).getValueClass();
                try
                {
                    Class<?> v_VClass = Help.forName(v_ValueClass);
                    if ( MethodReflect.isExtendImplement(v_VClass ,List.class) )
                    {
                        // 批量写
                        this.setCallMethod("executeInsertsPrepared");
                    }
                }
                catch (Exception exce)
                {
                    throw new RuntimeException(exce);
                }
            }
        }
        else if ( XSQLType.Read.equals(v_XSQLType) )
        {
            this.setCallMethod("queryXSQLData");
        }
        else if ( XSQLType.Update.equals(v_XSQLType) 
               || XSQLType.Delete.equals(v_XSQLType) )
        {
            this.setCallMethod("executeUpdatePrepared");
            
            if ( !Help.isNull(this.getCallParams()) )
            {
                String v_ValueClass = this.getCallParams().get(0).getValueClass();
                try
                {
                    Class<?> v_VClass = Help.forName(v_ValueClass);
                    if ( MethodReflect.isExtendImplement(v_VClass ,List.class) )
                    {
                        // 批量更新
                        this.setCallMethod("executeUpdatesPrepared");
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
     * 解释XSQL类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @param i_XSQL  XSQL对象
     * @return
     */
    private XSQLType parserXSQLType(XSQL i_XSQL)
    {
        int v_SQLType = i_XSQL.getContent().getSQLType();
        
        if ( DBSQL.$DBSQL_TYPE_INSERT == v_SQLType )
        {
            return XSQLType.Create;
        }
        else if ( DBSQL.$DBSQL_TYPE_SELECT == v_SQLType )
        {
            return XSQLType.Read;
        }
        else if ( DBSQL.$DBSQL_TYPE_UPDATE == v_SQLType )
        {
            return XSQLType.Update;
        }
        else if ( DBSQL.$DBSQL_TYPE_DELETE == v_SQLType )
        {
            return XSQLType.Delete;
        }
        else if ( DBSQL.$DBSQL_TYPE_DDL == v_SQLType )
        {
            return XSQLType.DDL;
        }
        else
        {
            throw new RuntimeException("Unknown SQL type：" + v_SQLType);
        }
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.XSQL.getValue();
    }
   
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.XSQL.getXmlName();
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        this.initCallMethod(io_ExecuteObject);
        return io_ExecuteObject;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
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
        if ( this.type != null )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("type" ,this.type.getValue()));
        }
        if ( !Help.isNull(this.getCallXID()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("callXID" ,this.getCallXID()));
        }
        if ( !Help.isNull(this.getCallParams()) )
        {
            for (NodeParam v_Param : this.getCallParams())
            {
                io_Xml.append(v_Param.toXml(i_Level + 1 ,i_TreeID));
            }
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
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        return super.toString(i_Context);
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        return super.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new XSQLConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        XSQLConfig v_Clone = new XSQLConfig();
        
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
        v_Clone.type = this.type;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
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
            throw new NullPointerException("Clone XSQLConfig xid is null.");
        }
        
        XSQLConfig v_Clone = (XSQLConfig) io_Clone;
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
        v_Clone.type = this.type;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-09
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
            throw new NullPointerException("Clone XSQLConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        XSQLConfig                  v_Clone      = new XSQLConfig();
        
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
