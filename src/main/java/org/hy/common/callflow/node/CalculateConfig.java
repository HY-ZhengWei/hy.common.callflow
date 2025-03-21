package org.hy.common.callflow.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.log.Logger;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.context.MapContext;





/**
 * 计算元素：计算配置信息
 * 
 * 注1：计算元素也可以当作条件逻辑元素来用。
 *     当返回值变量returnID为NULL时按条件逻辑使用。
 *     
 * 注2：不建议计算配置共用，即使两个编排调用相同的计算配置也建议配置两个计算配置，使计算配置唯一隶属于一个编排中。
 *     原因1是考虑到后期升级维护编排，在共享计算配置下，无法做到升级时百分百的正确。
 *     原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *     如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-04
 * @version     v1.0
 */
public class CalculateConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(CalculateConfig.class);
    
    /**
     * 表达式引擎的阻断符或是限定符。
     * 阻断符最终将被替换为""空字符。
     * 
     * 用于阻断.点符号。
     * 
     * 如，表达式  :Name.indexOf("B") >= 0 ，:Name.indexOf 也可能被解释为面向对象的属性值获取方法。
     *     而.indexOf("B")是Fel处理的，无须再加工。为了防止歧义，所以要阻断或限定一下，变成下面的样子。
     *     {:Name}.indexOf("B") >= 0
     */
    private static final String [] $Fel_BlockingUp = {"{" ,"}"};
    
    /** 表达式引擎 */
    private static final FelEngine $FelEngine      = new FelEngineImpl();
    
    

    /**
     * 计算表达式
     * 
     * 形式为带占位符的Fel表达式，
     *    如：:c01=='1' && :c02=='2'
     *    如：:c01==NULL || :c01==''  判定是否为NULL对象或空字符串
     */
    private String                        calc;
    
    /**
     * 解释出来的Fel表达式。与this.calc的区别是：它是没有占位符（仅内部使用）
     * 
     *    如：c01=='1' && c02=='2'
     *    如：c01==NULL || c01==''  判定是否为NULL对象或空字符串
     */
    private String                        calcFel;
    
    /**
     * 占位符信息的集合
     * 
     * Map.key    为占位符。前缀为:符号
     * Map.Value  为占位符的顺序。下标从0开始
     */
    private PartitionMap<String ,Integer> placeholders;
    
    
    
    public CalculateConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public CalculateConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    /**
     * 执行元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Calculate.getValue();
    }
    
    
    /**
     * 获取：计算表达式
     * 
     * 形式为带占位符的Fel表达式，
     *    如：:c01=='1' && :c02=='2'
     *    如：:c01==NULL || :c01==''  判定是否为NULL对象或空字符串
     */
    public String getCalc()
    {
        return calc;
    }


    /**
     * 设置：计算表达式
     * 
     * 形式为带占位符的Fel表达式，
     *    如：:c01=='1' && :c02=='2'
     *    如：:c01==NULL || :c01==''  判定是否为NULL对象或空字符串
     * 
     * @param i_Calc 计算表达式
     */
    public void setCalc(String i_Calc)
    {
        this.calc         = i_Calc.trim();
        this.calcFel      = i_Calc.trim();
        this.placeholders = null;
        
        if ( !Help.isNull(this.calc) )
        {
            this.placeholders = Help.toReverse(StringHelp.parsePlaceholders(this.calc ,true));
            
            for (String v_Key : this.placeholders.keySet())
            {
                this.calcFel = StringHelp.replaceAll(this.calcFel ,DBSQL.$Placeholder + v_Key ,StringHelp.replaceAll(v_Key ,"." ,"_"));
            }
            
            this.calcFel = StringHelp.replaceAll(this.calcFel ,$Fel_BlockingUp ,new String[]{""});
        }
        else
        {
            this.calc         = null;
            this.calcFel      = null;
            this.placeholders = null;
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-04
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
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        try
        {
            if ( Help.isNull(this.calc) )
            {
                v_Result.setException(new NullPointerException("Calculate[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s calc is null"));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            FelContext v_FelContext = new MapContext();
            for (String v_Item : this.placeholders.keySet())
            {
                String v_Key   = DBSQL.$Placeholder + v_Item;
                Object v_Value = ValueHelp.getValue(v_Key ,null ,"" ,io_Context);
                
                v_Key = StringHelp.replaceAll(v_Item ,"." ,"_"); // "点" 原本就是Fel关键字，所以要替换 ZhengWei(HY) Add 2017-05-23
                
                v_FelContext.set(v_Key ,v_Value);
            }
            
            Object v_CalcRet = $FelEngine.eval(this.calcFel ,v_FelContext);
            this.refreshReturn(io_Context ,v_CalcRet);
            
            if ( Help.isNull(this.returnID) )
            {
                v_Result.setResult((Boolean) v_CalcRet);
                this.refreshStatus(io_Context ,v_Result.getStatus());
                
            }
            else
            {
                v_Result.setResult(v_CalcRet);
                this.refreshStatus(io_Context ,v_Result.getStatus());
            }
            
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
     * @createDate  2025-03-03
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
        String        v_XName  = ElementType.Calculate.getXmlName();
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.calc) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("calc" ,this.calc));
        }
        if ( !Help.isNull(this.returnID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("returnID" ,this.returnID));
        }
        
        if ( !Help.isNull(this.route.getSucceeds())
          || !Help.isNull(this.route.getFaileds())
          || !Help.isNull(this.route.getExceptions()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            if ( Help.isNull(this.returnID) )
            {
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
            }
            else
            {
                // 成功路由
                if ( !Help.isNull(this.route.getSucceeds()) )
                {
                    for (RouteItem v_RouteItem : this.route.getSucceeds())
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Succeed.getXmlName()));
                        v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toEnd(RouteType.Succeed.getXmlName()));
                    }
                }
            }
            
            // 异常路由
            if ( !Help.isNull(this.route.getExceptions()) )
            {
                for (RouteItem v_RouteItem : this.route.getExceptions())
                {
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
                    v_Xml.append(v_RouteItem.toXml(i_Level + 1 ,v_TreeID));
                    v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toBegin(RouteType.Error.getXmlName()));
                }
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toEnd("route"));
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        return v_Xml.toString();
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
        
        if ( Help.isNull(this.calc) )
        {
            v_Builder.append("?");
        }
        else
        {
            Map<String ,String> v_Replaces = new LinkedHashMap<String ,String>();
            for (String v_Item : this.placeholders.keySet())
            {
                try
                {
                    String v_Key   = DBSQL.$Placeholder + v_Item;
                    Object v_Value = ValueHelp.getValue(v_Key ,String.class ,"?" ,i_Context);
                    if ( v_Value == null )
                    {
                        v_Replaces.put(v_Key ,"?");
                    }
                    else
                    {
                        v_Replaces.put(v_Key ,v_Value.toString());
                    }
                }
                catch (Exception exce)
                {
                    $Logger.error("Calculate[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s calc[" + this.calc + "] get[" + v_Item + "] error." ,exce);
                    v_Replaces.put(v_Item ,"ERROR");
                }
            }
            
            v_Builder.append(StringHelp.replaceAll(this.calc ,v_Replaces ,true));
            v_Replaces.clear();
            v_Replaces = null;
        }
        
        return v_Builder.toString();
    }
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
        
        if ( Help.isNull(this.calc) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.calc);
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
        return new CalculateConfig();
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
        CalculateConfig v_Clone = new CalculateConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.setCalc(this.calc);
        
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
            throw new NullPointerException("Clone CalculateConfig xid is null.");
        }
        
        CalculateConfig v_Clone = (CalculateConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.setCalc(this.calc);
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
            throw new NullPointerException("Clone CalculateConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        CalculateConfig             v_Clone      = new CalculateConfig();
        
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
