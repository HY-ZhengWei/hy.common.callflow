package org.hy.common.callflow.ifelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.enums.Logical;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteConfig;





/**
 * 条件逻辑
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 */
public class Condition extends ExecuteElement implements IfElse
{
    
    /** 逻辑 */
    private Logical      logical;
    
    /** 条件项或嵌套条件逻辑 */
    private List<IfElse> items;
    
    /** 为返回值定义的变量ID */
    private String       returnID;
    
    /** 路由 */
    private RouteConfig  route;
    
    
    
    public Condition()
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
    public Condition(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.logical = Logical.And;
        this.items   = new ArrayList<>();
        this.route   = new RouteConfig();
    }
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(Map<String ,Object> io_Context)
    {
        ExecuteResult v_Result = new ExecuteResult(this.getTreeID() ,this.xid ,this.toString(io_Context));
        
        try
        {
            long    v_BeginTime = this.request();
            boolean v_ExceRet   = this.allow(io_Context);
            
            if ( !Help.isNull(this.returnID) )
            {
                io_Context.put(this.returnID ,v_ExceRet);
            }
            
            this.success(Date.getTimeNano() - v_BeginTime);
            v_Result.setResult(v_ExceRet);
            return v_Result;
        }
        catch (Exception exce)
        {
            return v_Result.setException(exce);
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
    public boolean setCondition(Condition i_Condition)
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
    public boolean setItem(Condition i_Item)
    {
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        
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
        this.items = i_Items;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }
    
    
    /**
     * 获取：为返回值定义的变量ID
     */
    public String getReturnID()
    {
        return returnID;
    }

    
    /**
     * 设置：为返回值定义的变量ID
     * 
     * @param i_ReturnID 为返回值定义的变量ID
     */
    public void setReturnID(String i_ReturnID)
    {
        if ( CallFlow.$WorkID.equals(i_ReturnID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s returnID[" + i_ReturnID + "] equals " + CallFlow.$WorkID);
        }
        this.returnID = i_ReturnID;
    }


    /**
     * 获取：路由
     */
    public RouteConfig getRoute()
    {
        return route;
    }

    
    /**
     * 设置：路由
     * 
     * @param i_Route 路由
     */
    public void setRoute(RouteConfig i_Route)
    {
        this.route = i_Route;
    }
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_Level  层级。最小下标从0开始。
     *                   0表示每行前面有0个空格；
     *                   1表示每行前面有4个空格；
     *                   2表示每行前面有8个空格；
     *                  
     * @return
     */
    public String toXml(int i_Level)
    {
        StringBuilder v_Xml    = new StringBuilder();
        String        v_Level1 = "    ";
        String        v_LevelN = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName  = "xcondition";
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
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
                v_Xml.append(v_Item.toXml(i_Level + 1));
            }
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
        }
        if ( !Help.isNull(this.route.getSucceeds())
          || !Help.isNull(this.route.getFaileds())
          || !Help.isNull(this.route.getExceptions()) )
        {
            int v_MaxLpad = 0;
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                v_MaxLpad = 5;
            }
            else if ( !Help.isNull(this.route.getFaileds()) )
            {
                v_MaxLpad = 4;
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 真值路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (IExecute v_Item : this.route.getSucceeds())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("if" ,v_Item.getXJavaID() ,v_MaxLpad - 2));
                    }
                    else
                    {
                        v_Xml.append(v_Item.toXml(i_Level + 1));
                    }
                }
            }
            // 假值路由
            if ( !Help.isNull(this.route.getFaileds()) )
            {
                for (IExecute v_Item : this.route.getFaileds())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("else" ,v_Item.getXJavaID() ,v_MaxLpad - 4));
                    }
                    else
                    {
                        v_Xml.append(v_Item.toXml(i_Level + 1));
                    }
                }
            }
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (IExecute v_Item : this.route.getExceptions())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("error" ,v_Item.getXJavaID()));
                    }
                    else
                    {
                        v_Xml.append(v_Item.toXml(i_Level + 1));
                    }
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
                if ( v_Item instanceof Condition )
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
                if ( v_Item instanceof Condition )
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
    
}
