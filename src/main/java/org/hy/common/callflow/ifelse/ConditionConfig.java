package org.hy.common.callflow.ifelse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.Logical;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteItem;





/**
 * 条件逻辑
 * 
 * 注：不建议条件逻辑共用，即使两个编排调用相同的条件逻辑也建议配置两个条件逻辑，使条件逻辑唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享条件逻辑下，无法做到升级时百分百的正确。
 *    原因2是在共享条件逻辑时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 */
public class ConditionConfig extends ExecuteElement implements IfElse ,Cloneable
{
    
    /** 逻辑 */
    private Logical      logical;
    
    /** 条件项或嵌套条件逻辑 */
    private List<IfElse> items;
    
    
    
    public ConditionConfig()
    {
        this(0L ,0L);
    }
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public ConditionConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.logical = Logical.And;
        this.items   = new ArrayList<>();
    }
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Condition.getValue();
    }
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        try
        {
            boolean v_ExceRet = this.allow(io_Context);
            
            if ( !Help.isNull(this.returnID) )
            {
                io_Context.put(this.returnID ,v_ExceRet);
            }
            
            v_Result.setResult(v_ExceRet);
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
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     */
    public boolean allow(Map<String ,Object> i_Context) throws Exception
    {
        if ( this.logical == null )
        {
            throw new NullPointerException("Condition logical [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        if ( Help.isNull(this.items) )
        {
            throw new NullPointerException("Condition items [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
        }
        
        // 与
        if ( Logical.And.equals(this.logical) )
        {
            for (IfElse v_Item : this.items)
            {
                if ( v_Item == null )
                {
                    throw new NullPointerException("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
                }
                else
                {
                    boolean v_ChildRet = v_Item.allow(i_Context);
                    if ( !v_ChildRet )
                    {
                        return false;
                    }
                }
            }
            
            return true;
        }
        // 或
        else if ( Logical.Or.equals(this.logical) )
        {
            for (IfElse v_Item : this.items)
            {
                if ( v_Item == null )
                {
                    throw new NullPointerException("Condition list element [" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] is null.");
                }
                else
                {
                    boolean v_ChildRet = v_Item.allow(i_Context);
                    if ( v_ChildRet )
                    {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        return false;
    }
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回判定结果或抛出异常
     */
    public boolean reject(Map<String ,Object> i_Context) throws Exception
    {
        return !allow(i_Context);
    }
    
    
    /**
     * 添加条件项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-22
     * @version     v1.0
     *
     * @param i_ConditionItem  条件项
     */
    public boolean setConditionItem(ConditionItem i_ConditionItem)
    {
        return this.setItem(i_ConditionItem);
    }
    
    
    /**
     * 添加条件逻辑
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-22
     * @version     v1.0
     *
     * @param i_Condition  条件逻辑
     */
    public boolean setCondition(ConditionConfig i_Condition)
    {
        return this.setItem(i_Condition);
    }
    
    
    /**
     * 添加条件项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Item
     */
    public boolean setItem(ConditionItem i_Item)
    {
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
        
        if ( i_Item != null )
        {
            this.items.add(i_Item);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 添加嵌套条件逻辑
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Item
     */
    public boolean setItem(ConditionConfig i_Item)
    {
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
        
        if ( i_Item != null  )
        {
            this.items.add(i_Item);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 获取：逻辑
     */
    public Logical getLogical()
    {
        return logical;
    }

    
    /**
     * 设置：逻辑
     * 
     * @param i_Logical 逻辑
     */
    public void setLogical(Logical i_Logical)
    {
        this.logical = i_Logical;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 获取：条件项或嵌套条件逻辑
     */
    public List<IfElse> getItems()
    {
        return items;
    }
    
    
    /**
     * 设置：条件项或嵌套条件逻辑
     * 
     * @param i_Items 条件项或嵌套条件逻辑
     */
    public void setItems(List<IfElse> i_Items)
    {
        if ( i_Items == null )
        {
            this.items.clear();
        }
        else
        {
            this.items = i_Items;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
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
        String        v_XName  = null;
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_XName = ElementType.Condition.getXmlName();
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_XName = ElementType.Condition.getValue().toLowerCase();
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.logical) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("logical" ,this.logical.getValue()));
        }
        if ( !Help.isNull(this.items) )
        {
            for (IfElse v_Item : this.items)
            {
                v_Xml.append(v_Item.toXml(i_Level + 1 ,v_TreeID));
            }
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
          || !Help.isNull(this.route.getFaileds())
          || !Help.isNull(this.route.getExceptions()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 真值路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (RouteItem v_RouteItem : this.route.getSucceeds())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin("if"));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd("if"));
                }
            }
            // 假值路由
            if ( !Help.isNull(this.route.getFaileds()) )
            {
                for (RouteItem v_RouteItem : this.route.getFaileds())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Else.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Else.getXmlName()));
                }
            }
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (RouteItem v_RouteItem : this.route.getExceptions())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Error.getXmlName()));
                }
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toEnd("route"));
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        return v_Xml.toString();
    }

    
    /**
     * 解析为实时运行时的逻辑判定表达式
     * 
     * 注：禁止在此真的执行逻辑判定
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
        
        if ( this.logical != null && !Help.isNull(this.items) )
        {
            for (int x=0; x<this.items.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ").append(this.logical.getValue()).append(" ");
                }
                
                IfElse v_Item = this.items.get(x);
                if ( v_Item instanceof ConditionConfig )
                {
                    v_Builder.append("(").append(v_Item.toString(i_Context)).append(")");
                }
                else
                {
                    v_Builder.append(v_Item.toString(i_Context));
                }
            }
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为逻辑表达式
     * 
     * 注：禁止在此真的执行逻辑判定
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( this.logical != null && !Help.isNull(this.items) )
        {
            for (int x=0; x<this.items.size(); x++)
            {
                if ( x >= 1 )
                {
                    v_Builder.append(" ").append(this.logical.getValue()).append(" ");
                }
                
                IfElse v_Item = this.items.get(x);
                if ( v_Item instanceof ConditionConfig )
                {
                    v_Builder.append("(").append(v_Item.toString()).append(")");
                }
                else
                {
                    v_Builder.append(v_Item.toString());
                }
            }
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new ConditionConfig();
    }
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        ConditionConfig v_Clone = new ConditionConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.logical = this.logical;
        
        for (IfElse v_Item : this.items)
        {
            if ( v_Item instanceof ConditionItem )
            {
                v_Clone.items.add((ConditionItem) ((ConditionItem) v_Item).cloneMyOnly());
            }
            else if ( v_Item instanceof ConditionConfig )
            {
                v_Clone.items.add((ConditionConfig) ((ConditionConfig) v_Item).cloneMyOnly());
            }
            else
            {
                throw new RuntimeException("Unknown type[" + v_Item.getClass().getName() + "] of exception");
            }
        }
        
        return v_Clone;
    }
    
    
    /**
     * 深度克隆编排元素
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
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone ConditionConfig xid is null.");
        }
        
        ConditionConfig v_Clone = (ConditionConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.logical = this.logical;
        
        for (IfElse v_Item : this.items)
        {
            if ( v_Item instanceof ConditionItem )
            {
                ConditionItem v_CloneConditionItem = new ConditionItem();
                v_Item.clone(v_CloneConditionItem ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.items.add(v_CloneConditionItem);
            }
            else if ( v_Item instanceof ConditionConfig )
            {
                ConditionConfig v_CloneCondition = new ConditionConfig();
                v_Item.clone(v_CloneCondition ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.items.add(v_CloneCondition);
            }
            else
            {
                throw new RuntimeException("Unknown type[" + v_Item.getClass().getName() + "] of exception");
            }
        }
    }
    
    
    /**
     * 深度克隆编排元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
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
            throw new NullPointerException("Clone ConditionConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ConditionConfig             v_Clone      = new ConditionConfig();
        
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
