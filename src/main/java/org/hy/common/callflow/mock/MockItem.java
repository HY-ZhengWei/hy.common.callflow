package org.hy.common.callflow.mock;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.XJavaID;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.clone.CloneableCallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;





/**
 * 模拟项
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-06
 * @version     v1.0
 */
public class MockItem implements IToXml ,CloneableCallFlow ,XJavaID
{
    
    /** 主键标识 */
    private String                        id;
    
    /** 全局惟一标识ID */                
    private String                        xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String                        comment;
    
    /** 是否启用。可以是数值、上下文变量、XID标识 */
    private String                        enable;
    
    /** 是否启用，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> enablePlaceholders;
    
    /** 模拟数据。可以是数值、上下文变量、XID标识 */
    private String                        data;
    
    /** 是否启用，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> dataPlaceholders;
    
    /** 创建人编号 */        
    private String                        createUserID;
                             
    /** 修改者编号 */        
    private String                        updateUserID;
                             
    /** 创建时间 */          
    private Date                          createTime;
                             
    /** 最后修改时间 */   
    private Date                          updateTime;

    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           没有符合要求的模拟数据时，返回NULL
     * @throws Exception 
     */
    public Object mock(Map<String ,Object> i_Context) throws Exception
    {
        boolean v_Enable = (Boolean) ValueHelp.getValueReplace(this.enable ,this.enablePlaceholders ,Boolean.class ,Boolean.FALSE ,i_Context);
        if ( !v_Enable )
        {
            return null;
        }
        
        // 这里的默认值只能是空字符串，不能是NULL
        // 好处时，当条件逻辑元素在使用Mock时，就不用设置this.data属性
        return ValueHelp.getValueReplace(this.data ,this.dataPlaceholders ,null ,null ,i_Context);
    }
    
    
    
    /**
     * 获取：是否启用。可以是数值、上下文变量、XID标识
     */
    public String getEnable()
    {
        return enable;
    }

    
    /**
     * 设置：是否启用。可以是数值、上下文变量、XID标识
     * 
     * @param i_Enable 是否启用。可以是数值、上下文变量、XID标识
     */
    public void setEnable(String i_Enable)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Enable) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Enable ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.enablePlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.enablePlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.enable = i_Enable;
    }

    
    /**
     * 获取：模拟数据。可以是数值、上下文变量、XID标识
     */
    public String getData()
    {
        return data;
    }

    
    /**
     * 设置：模拟数据。可以是数值、上下文变量、XID标识
     * 
     * @param i_Data 模拟数据。可以是数值、上下文变量、XID标识
     */
    public void setData(String i_Data)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Data) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Data ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.dataPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.dataPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.data = i_Data;
    }
    
    
    
    /**
     * 获取：主键标识
     */
    public String getId()
    {
        return id;
    }


    
    /**
     * 设置：主键标识
     * 
     * @param i_Id 主键标识
     */
    public void setId(String i_Id)
    {
        this.id = i_Id;
    }



    /**
     * 获取：注释。可用于日志的输出等帮助性的信息
     */
    public String getComment()
    {
        return comment;
    }


    
    /**
     * 设置：注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment 注释。可用于日志的输出等帮助性的信息
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
    
    
    /**
     * 获取：创建人编号
     */
    public String getCreateUserID()
    {
        return createUserID;
    }


    
    /**
     * 设置：创建人编号
     * 
     * @param i_CreateUserID 创建人编号
     */
    public void setCreateUserID(String i_CreateUserID)
    {
        this.createUserID = i_CreateUserID;
    }


    
    /**
     * 获取：修改者编号
     */
    public String getUpdateUserID()
    {
        return updateUserID;
    }


    
    /**
     * 设置：修改者编号
     * 
     * @param i_UpdateUserID 修改者编号
     */
    public void setUpdateUserID(String i_UpdateUserID)
    {
        this.updateUserID = i_UpdateUserID;
    }



    
    /**
     * 获取：创建时间
     */
    public Date getCreateTime()
    {
        return createTime;
    }


    
    /**
     * 设置：创建时间
     * 
     * @param i_CreateTime 创建时间
     */
    public void setCreateTime(Date i_CreateTime)
    {
        this.createTime = i_CreateTime;
    }


    
    /**
     * 获取：最后修改时间
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }


    
    /**
     * 设置：最后修改时间
     * 
     * @param i_UpdateTime 最后修改时间
     */
    public void setUpdateTime(Date i_UpdateTime)
    {
        this.updateTime = i_UpdateTime;
    }



    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
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
        StringBuilder v_Xml      = new StringBuilder();
        String        v_Level1   = "    ";
        String        v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level + 1 ,v_Level1);
        String        v_NewSpace = "\n" + v_LevelN + v_Level1;
        
        if ( !ExportType.UI.equals(i_ExportType) )
        {
            if ( this.id != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("id"      ,this.id));
            }
            if ( !Help.isNull(this.comment) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("comment" ,this.comment));
            }
            if ( !Help.isNull(this.enable) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("enable"  ,this.enable));
            }
            if ( !Help.isNull(this.data) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("data"    ,this.data ,v_NewSpace));
            }
        }
        else
        {
            if ( !Help.isNull(this.comment) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toComment(this.comment));
            }
        }
        
        if ( ExportType.UI.equals(i_ExportType) || ExportType.All.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.createUserID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("createUserID" ,this.createUserID));
            }
            if ( !Help.isNull(this.updateUserID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("updateUserID" ,this.updateUserID));
            }
            if ( this.createTime != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("createTime"   ,this.createTime.getFull()));
            }
            if ( this.updateTime != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("updateTime"   ,this.updateTime.getFull()));
            }
        }
        
        return v_Xml.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        // 无须克隆路由项，也不允许调用此方法
        throw new RuntimeException("Not allowed to call MockItem.newMy().");
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        // 无须克隆路由项，也不允许调用此方法
        throw new RuntimeException("Not allowed to call MockItem.cloneMyOnly().");
    }
    
    
    
    /**
     * 深度克隆编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
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
        MockItem v_Clone = (MockItem) io_Clone;
        
        v_Clone.id           = this.id;
        v_Clone.comment      = this.comment;
        v_Clone.enable       = this.enable;
        v_Clone.data         = this.data;
        v_Clone.createUserID = this.createUserID;
        v_Clone.updateUserID = this.updateUserID;
        v_Clone.createTime   = this.createTime == null ? null : new Date(this.createTime.getTime());
        v_Clone.updateTime   = this.updateTime == null ? null : new Date(this.updateTime.getTime());
    }



    /**
     * 深度克隆编排元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        // 无须克隆模拟项，也不允许调用此方法
        throw new RuntimeException("Not allowed to call MockItem.clone().");
    }
    
    
    
    /**
     * 获取：全局惟一标识ID
     */
    public String getXid()
    {
        return xid;
    }

    
    
    /**
     * 设置：全局惟一标识ID
     * 
     * @param i_Xid 全局惟一标识ID
     */
    public void setXid(String i_Xid)
    {
        if ( CallFlow.isSystemXID(i_Xid) )
        {
            throw new IllegalArgumentException("MockItem XID[" + i_Xid + "] is SystemXID.");
        }
        this.xid = i_Xid;
    }


    
    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_XJavaID)
    {
        if ( CallFlow.isSystemXID(i_XJavaID) )
        {
            throw new IllegalArgumentException("MockItem XJavaID[" + i_XJavaID + "] is SystemXID.");
        }
        this.xid = i_XJavaID;
    }
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    public String getXJavaID()
    {
        return this.xid;
    }
    
}
