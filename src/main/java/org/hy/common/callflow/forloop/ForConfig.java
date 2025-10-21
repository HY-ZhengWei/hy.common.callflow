package org.hy.common.callflow.forloop;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
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
import org.hy.common.xml.log.Logger;





/**
 * 循环元素：for循环配置信息
 * 
 * 支持形式1：数值循环： for (indexID=start; indexID<=end; indexID+=step)
 * 支持形式2：数值循环： for (start .. end)
 * 支持形式3：集合循环： for (elementID : end)
 * 支持形式4：集合循环： for (end)
 * 
 * 注：不建议循环配置共用，即使两个编排调用相同的循环配置也建议配置两个循环配置，使循环配置唯一隶属于一个编排中。
 *    原因1是考虑到后期升级维护编排，在共享循环配置下，无法做到升级时百分百的正确。
 *    原因2是在共享节点时，统计方面也无法独立区分出来。
 *    
 *    如果要共享，建议采用子编排的方式共享。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-05
 * @version     v1.0
 *              v2.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v3.0  2025-09-26  迁移：静态检查
 *              v4.0  2025-09-29  添加：For循环的每级元素"序号"的变量名称。下标从1开始
 *              v5.0  2025-10-20  修正：先handleContext()解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
 */
public class ForConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(ForConfig.class);
    
    
    
    /** 起始值。可以是数值、上下文变量、XID标识 */
    private String start;
    
    /** 结束值。可以是数值、上下文变量、XID标识 */
    private String end;
    
    /** 步长。可以是数值、上下文变量、XID标识 */
    private String step;
    
    /** For循环的每级元素"序号"的变量名称。下标从0开始 */
    private String indexID;
    
    /** For循环的每级元素"序号"的变量名称。下标从1开始 */
    private String indexNo;
    
    /** For循环的每级元素"对象"的变量名称 */
    private String elementID;
    
    
    
    public ForConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public ForConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
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
        // For循环元素必须有XID
        if ( Help.isNull(this.getXid()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + ".xid is null.");
            return false;
        }
        
        // For循环元素的结束值不能为空
        if ( Help.isNull(this.getEnd()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].end is null.");
            return false;
        }
        
        // For循环元素的必须有下一个成功路由
        if ( Help.isNull(this.getRoute().getSucceeds()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].Succeed route is null.");
            return false;
        }
        
        // 变量名称的是否合法
        if ( !Help.isNull(this.indexID) )
        {
            if ( !ValueHelp.isVarName(this.indexID) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].indexID[" + this.indexID + "] is illegal name.");
                return false;
            }
        }
        
        // 变量名称的是否合法
        if ( !Help.isNull(this.indexNo) )
        {
            if ( !ValueHelp.isVarName(this.indexNo) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].indexNo[" + this.indexNo + "] is illegal name.");
                return false;
            }
        }
        
        // 变量名称的是否合法
        if ( !Help.isNull(this.elementID) )
        {
            if ( !ValueHelp.isVarName(this.elementID) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].elementID[" + this.elementID + "] is illegal name.");
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
        return "XFor_" + StringHelp.getUUID9n();
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
        return ElementType.For.getValue();
    }
    
    
    
    /**
     * 获取：起始值。可以是数值、上下文变量、XID标识
     */
    public String getStart()
    {
        return start;
    }


    
    /**
     * 设置：起始值。可以是数值、上下文变量、XID标识
     * 
     * @param i_Start 起始值。可以是数值、上下文变量、XID标识
     */
    public void setStart(String i_Start)
    {
        if ( Help.isNull(i_Start) )
        {
            this.start = null;
            return;
        }
        
        String v_Start = i_Start.trim();
        if ( Help.isNumber(v_Start) )
        {
            this.start =  v_Start;
        }
        else
        {
            this.start = ValueHelp.standardRefID(v_Start);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }

    
    
    /**
     * 获取：结束值。可以是数值、上下文变量、XID标识
     */
    public String getEnd()
    {
        return end;
    }


    
    /**
     * 设置：结束值。可以是数值、上下文变量、XID标识
     * 
     * @param i_End 结束值。可以是数值、上下文变量、XID标识
     */
    public void setEnd(String i_End)
    {
        if ( Help.isNull(i_End) )
        {
            this.end = null;
            return;
        }
        
        String v_End = i_End.trim();
        if ( Help.isNumber(v_End) )
        {
            this.end =  v_End;
        }
        else
        {
            this.end = ValueHelp.standardRefID(v_End);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：步长。可以是数值、上下文变量、XID标识
     */
    public String getStep()
    {
        return step;
    }


    
    /**
     * 设置：步长。可以是数值、上下文变量、XID标识
     * 
     * @param i_Step 步长。可以是数值、上下文变量、XID标识
     */
    public void setStep(String i_Step)
    {
        if ( Help.isNull(i_Step) )
        {
            this.step = null;
            return;
        }
        
        String v_Step = i_Step.trim();
        if ( Help.isNumber(v_Step) )
        {
            this.step =  v_Step;
        }
        else
        {
            this.step = ValueHelp.standardRefID(v_Step);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }


    
    /**
     * 获取：For循环的每级元素"序号"的变量名称。下标从0开始
     */
    public String getIndexID()
    {
        return indexID;
    }


    
    /**
     * 设置：For循环的每级元素"序号"的变量名称。下标从0开始
     * 
     * @param i_IndexID For循环的每级元素"序号"的变量名称。下标从0开始
     */
    public void setIndexID(String i_IndexID)
    {
        if ( CallFlow.isSystemXID(i_IndexID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s indexID[" + i_IndexID + "] is SystemXID.");
        }
        this.indexID = ValueHelp.standardValueID(i_IndexID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    
    /**
     * 获取：For循环的每级元素"序号"的变量名称。下标从1开始
     */
    public String getIndexNo()
    {
        return indexNo;
    }


    
    /**
     * 设置：For循环的每级元素"序号"的变量名称。下标从1开始
     * 
     * @param i_IndexNo For循环的每级元素"序号"的变量名称。下标从1开始
     */
    public void setIndexNo(String i_IndexNo)
    {
        if ( CallFlow.isSystemXID(i_IndexNo) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s indexNo[" +i_IndexNo + "] is SystemXID.");
        }
        this.indexNo = ValueHelp.standardValueID(i_IndexNo);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：For循环的每级元素"对象"的变量名称
     */
    public String getElementID()
    {
        return elementID;
    }


    
    /**
     * 设置：For循环的每级元素"对象"的变量名称
     * 
     * @param i_ElementID For循环的每级元素"对象"的变量名称
     */
    public void setElementID(String i_ElementID)
    {
        if ( CallFlow.isSystemXID(i_ElementID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s elementID[" + i_ElementID + "] is SystemXID.");
        }
        this.elementID = ValueHelp.standardValueID(i_ElementID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
        
        try
        {
            String  v_WorkID     = CallFlow.getWorkID(io_Context);
            String  v_Prefix     = v_WorkID + "@" + this.getXid() + "@For@";
            String  v_IndexID    = v_Prefix + "index";
            String  v_IteratorID = v_Prefix + "iterator";
            Integer v_OldIndex   = (Integer) io_Context.get(v_IndexID);
            int     v_Index      = 0;
            Object  v_Element    = null;
            
            // 集合循环
            if ( Help.isNull(this.start) )
            {
                Object v_End = null;
                try
                {
                    v_End = ValueHelp.getValue(this.end ,null ,null ,io_Context);
                }
                catch (Exception exce)
                {
                    $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
                    v_Result.setException(exce);
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                if ( v_End == null )
                {
                    v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is null."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                // 集合循环： for (end)
                // 集合循环： for (elementID : end)
                if ( MethodReflect.isExtendImplement(v_End ,List.class) )
                {
                    List<?>     v_List     = (List<?>) v_End;
                    Iterator<?> v_Iterator = null;
                    if ( v_OldIndex != null )
                    {
                        v_Index    = v_OldIndex + 1;
                        v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                    }
                    else
                    {
                        v_Iterator = v_List.iterator();
                        io_Context.put(v_IteratorID ,v_Iterator);
                    }
                    
                    if ( !v_Iterator.hasNext() )
                    {
                        v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " List.hasNext() error."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    v_Element = v_Iterator.next();
                }
                else if ( MethodReflect.isExtendImplement(v_End ,Set.class) )
                {
                    Set<?>      v_Set      = (Set<?>) v_End;
                    Iterator<?> v_Iterator = null;
                    if ( v_OldIndex != null )
                    {
                        v_Index    = v_OldIndex + 1;
                        v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                    }
                    else
                    {
                        v_Iterator = v_Set.iterator();
                        io_Context.put(v_IteratorID ,v_Iterator);
                    }
                    
                    if ( !v_Iterator.hasNext() )
                    {
                        v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " Set.hasNext() error."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    v_Element = v_Iterator.next();
                }
                else if ( MethodReflect.isExtendImplement(v_End ,Collection.class) )
                {
                    Collection<?> v_Collection = (Collection<?>) v_End;
                    Iterator<?>   v_Iterator   = null;
                    if ( v_OldIndex != null )
                    {
                        v_Index    = v_OldIndex + 1;
                        v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                    }
                    else
                    {
                        v_Iterator = v_Collection.iterator();
                        io_Context.put(v_IteratorID ,v_Iterator);
                    }
                    
                    if ( !v_Iterator.hasNext() )
                    {
                        v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " Collection.hasNext() error."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    v_Element = v_Iterator.next();
                }
                else if ( MethodReflect.isExtendImplement(v_End ,Map.class) )
                {
                    Map<? ,?>   v_Map      = (Map<? ,?>) v_End;
                    Iterator<?> v_Iterator = null;
                    if ( v_OldIndex != null )
                    {
                        v_Index    = v_OldIndex + 1;
                        v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                    }
                    else
                    {
                        v_Iterator = v_Map.entrySet().iterator();
                        io_Context.put(v_IteratorID ,v_Iterator);
                    }
                    
                    if ( !v_Iterator.hasNext() )
                    {
                        v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " Map.hasNext() error."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    v_Element = v_Iterator.next();
                }
                else if ( Help.isArray(v_End) )
                {
                    int v_Len = Array.getLength(v_End);
                    if ( v_OldIndex != null )
                    {
                        v_Index = v_OldIndex + 1;
                    }
                    
                    if ( v_Len <= v_Index )
                    {
                        v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " Array.length " + v_Len + " <= " + v_Index + " error."));
                        this.refreshStatus(io_Context ,v_Result.getStatus());
                        return v_Result;
                    }
                    
                    v_Element = Array.get(v_End ,v_Index);
                }
                else
                {
                    v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is not list."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                io_Context.put(v_IndexID ,v_Index);
                refreshIndex(    io_Context ,v_Index);
                refreshElementID(io_Context ,v_Element);
            }
            // 数值循环
            else
            {
                Integer v_Start = null;
                try
                {
                    v_Start = (Integer) ValueHelp.getValue(this.start ,Integer.class ,null ,io_Context);
                }
                catch (Exception exce)
                {
                    $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s start[" + this.start + "] error." ,exce);
                    v_Result.setException(exce);
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                if ( v_Start == null )
                {
                    v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s start is null."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                Integer v_End = null;
                try
                {
                    v_End = (Integer) ValueHelp.getValue(this.end ,Integer.class ,null ,io_Context);
                }
                catch (Exception exce)
                {
                    $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
                    v_Result.setException(exce);
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                if ( v_End == null )
                {
                    v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is null."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                Integer v_Step = null;
                try
                {
                    v_Step = (Integer) ValueHelp.getValue(this.step ,Integer.class ,1 ,io_Context); // 默认值：1
                }
                catch (Exception exce)
                {
                    $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s step[" + this.step + "] error." ,exce);
                    v_Result.setException(exce);
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                // 数值循环： for (start .. end)
                // 数值循环： for (indexID=start; indexID<=end; indexID+=step)
                if ( v_OldIndex != null )
                {
                    v_Index = v_OldIndex + v_Step;
                }
                else
                {
                    v_Index = v_Start;
                }
                
                if ( v_End < v_Index )
                {
                    v_Result.setException(new IndexOutOfBoundsException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s " + this.end + " " + v_End + " < " + v_Index + " error."));
                    this.refreshStatus(io_Context ,v_Result.getStatus());
                    return v_Result;
                }
                
                io_Context.put(v_IndexID ,v_Index);
                refreshIndex(io_Context  ,v_Index);
            }
            
            v_Result.setResult(true);  // SelfLoop 中有设置结果为 false
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
     * 是否能有一个循环元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-06
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public boolean hasNext(Map<String ,Object> io_Context)
    {
        String  v_WorkID     = CallFlow.getWorkID(io_Context);
        String  v_Prefix     = v_WorkID + "@" + this.getXid() + "@For@";
        String  v_IndexID    = v_Prefix + "index";
        String  v_IteratorID = v_Prefix + "iterator";
        Integer v_OldIndex   = (Integer) io_Context.get(v_IndexID);
        int     v_Index      = 0;
        
        // 集合循环
        if ( Help.isNull(this.start) )
        {
            Object v_End = null;
            try
            {
                v_End = ValueHelp.getValue(this.end ,null ,null ,io_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
                return false;
            }
            
            if ( v_End == null )
            {
                $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is null."));
                return false;
            }
            
            // 集合循环： for (end)
            // 集合循环： for (elementID : end)
            if ( MethodReflect.isExtendImplement(v_End ,List.class) )
            {
                List<?>     v_List     = (List<?>) v_End;
                Iterator<?> v_Iterator = null;
                if ( v_OldIndex != null )
                {
                    v_Index    = v_OldIndex + 1;
                    v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                }
                else
                {
                    v_Iterator = v_List.iterator();
                    io_Context.put(v_IteratorID ,v_Iterator);
                }
                
                return v_Iterator.hasNext();
            }
            else if ( MethodReflect.isExtendImplement(v_End ,Set.class) )
            {
                Set<?>      v_Set      = (Set<?>) v_End;
                Iterator<?> v_Iterator = null;
                if ( v_OldIndex != null )
                {
                    v_Index    = v_OldIndex + 1;
                    v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                }
                else
                {
                    v_Iterator = v_Set.iterator();
                    io_Context.put(v_IteratorID ,v_Iterator);
                }
                
                return v_Iterator.hasNext();
            }
            else if ( MethodReflect.isExtendImplement(v_End ,Collection.class) )
            {
                Collection<?> v_Collection = (Collection<?>) v_End;
                Iterator<?>   v_Iterator   = null;
                if ( v_OldIndex != null )
                {
                    v_Index    = v_OldIndex + 1;
                    v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                }
                else
                {
                    v_Iterator = v_Collection.iterator();
                    io_Context.put(v_IteratorID ,v_Iterator);
                }
                
                return v_Iterator.hasNext();
            }
            else if ( MethodReflect.isExtendImplement(v_End ,Map.class) )
            {
                Map<? ,?>   v_Map      = (Map<? ,?>) v_End;
                Iterator<?> v_Iterator = null;
                if ( v_OldIndex != null )
                {
                    v_Index    = v_OldIndex + 1;
                    v_Iterator = (Iterator<?>) io_Context.get(v_IteratorID);
                }
                else
                {
                    v_Iterator = v_Map.entrySet().iterator();
                    io_Context.put(v_IteratorID ,v_Iterator);
                }
                
                return v_Iterator.hasNext();
            }
            else if ( Help.isArray(v_End) )
            {
                int v_Len = Array.getLength(v_End);
                if ( v_OldIndex != null )
                {
                    v_Index = v_OldIndex + 1;
                }
                
                return v_Len > v_Index;
            }
            else
            {
                $Logger.error(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is not list."));
                return false;
            }
        }
        // 数值循环
        else
        {
            Integer v_Start = null;
            try
            {
                v_Start = (Integer) ValueHelp.getValue(this.start ,Integer.class ,null ,io_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s start[" + this.start + "] error." ,exce);
                return false;
            }
            if ( v_Start == null )
            {
                $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s start is null."));
                return false;
            }
            
            Integer v_End = null;
            try
            {
                v_End = (Integer) ValueHelp.getValue(this.end ,Integer.class ,null ,io_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
                return false;
            }
            if ( v_End == null )
            {
                $Logger.error(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end is null."));
                return false;
            }
            
            Integer v_Step = null;
            try
            {
                v_Step = (Integer) ValueHelp.getValue(this.step ,Integer.class ,1 ,io_Context); // 默认值：1
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s step[" + this.step + "] error." ,exce);
                return false;
            }
            
            // 数值循环： for (start .. end)
            // 数值循环： for (indexID=start; indexID<=end; indexID+=step)
            if ( v_OldIndex != null )
            {
                v_Index = v_OldIndex + v_Step;
            }
            else
            {
                v_Index = v_Start;
            }
            
            return v_End >= v_Index;
        }
    }
    
    
    
    /**
     * 刷新For循环的每级元素"序号"的变量名称。下标从0开始
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-06
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Index     序号
     */
    private void refreshIndex(Map<String ,Object> io_Context ,Integer i_Index)
    {
        if ( !Help.isNull(this.indexID) && io_Context != null )
        {
            io_Context.put(this.indexID ,i_Index);
        }
        if ( !Help.isNull(this.indexNo) && io_Context != null )
        {
            io_Context.put(this.indexNo ,i_Index + 1);
        }
    }
    
    
    
    /**
     * For循环的每级元素"对象"的变量名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-06
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Element   元素对象
     */
    private void refreshElementID(Map<String ,Object> io_Context ,Object i_Element)
    {
        if ( !Help.isNull(this.elementID) && io_Context != null )
        {
            io_Context.put(this.elementID ,i_Element);
        }
    }

    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-03
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
        String        v_XName    = ElementType.For.getXmlName();
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
            if ( !Help.isNull(this.start) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("start" ,this.start));
            }
            if ( !Help.isNull(this.end) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("end" ,this.end));
            }
            if ( !Help.isNull(this.step) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("step" ,this.step));
            }
            if ( !Help.isNull(this.indexID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("indexID" ,this.indexID));
            }
            if ( !Help.isNull(this.indexNo) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("indexNo" ,this.indexNo));
            }
            if ( !Help.isNull(this.elementID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("elementID" ,this.elementID));
            }
            if ( !Help.isNull(this.statusID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("statusID" ,this.statusID));
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
     * @createDate  2025-03-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        Object        v_End     = null;
        
        String  v_WorkID   = CallFlow.getWorkID(i_Context);
        String  v_Prefix   = v_WorkID + "@" + this.getXid() + "@For@";
        String  v_IndexID  = v_Prefix + "index";
        Integer v_OldIndex = (Integer) i_Context.get(v_IndexID);
        int     v_Index    = 0;
        
        v_Builder.append("for (");
        
        // 集合循环
        if ( Help.isNull(this.start) )
        {
            try
            {
                v_End = ValueHelp.getValue(this.end ,null ,null ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
            }
            
            if ( v_OldIndex != null )
            {
                v_Index = v_OldIndex + 1;
            }
            
            if ( v_End == null )
            {
                v_End = "NULL";
            }
            else if ( MethodReflect.isExtendImplement(v_End ,List.class) )
            {
                v_End = "List(" + ((List<?>) v_End).size() + ")[" + v_Index + "]";
            }
            else if ( MethodReflect.isExtendImplement(v_End ,Set.class) )
            {
                v_End = "Set(" + ((Set<?>) v_End).size() + ")[" + v_Index + "]";
             }
            else if ( MethodReflect.isExtendImplement(v_End ,Collection.class) )
            {
                v_End = "Collection(" + ((Collection<?>) v_End).size() + ")[" + v_Index + "]";
            }
            else if ( MethodReflect.isExtendImplement(v_End ,Map.class) )
            {
                v_End = "Map(" + ((Map<? ,?>) v_End).size() + ")[" + v_Index + "]";
            }
            else if ( Help.isArray(v_End) )
            {
                v_End = "Array(" + Array.getLength(v_End) + ")[" + v_Index + "]";
            }
            else
            {
                v_End = "Not-List";
            }
            
            // 集合循环： for (end)
            if ( Help.isNull(this.elementID) )
            {
                v_Builder.append(this.end);
            }
            // 集合循环： for (indexID : end)
            else 
            {
                v_Builder.append(DBSQL.$Placeholder).append(this.elementID).append(" : ").append(this.end);
            }
            
            v_Builder.append("=").append(v_End);
        }
        // 数值循环
        else
        {
            Object v_Start = null;
            try
            {
                v_Start = ValueHelp.getValue(this.start ,Integer.class ,null ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s start[" + this.start + "] error." ,exce);
            }
            
            try
            {
                v_End = ValueHelp.getValue(this.end ,Integer.class ,null ,i_Context);
            }
            catch (Exception exce)
            {
                $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s end[" + this.end + "] error." ,exce);
            }
            
            // 数值循环： for (start .. end)
            if ( Help.isNull(this.indexID) )
            {
                v_Builder.append(v_Start).append(" .. ").append(v_End);
            }
            // 数值循环： for (indexID=start; indexID<=end; indexID+=step)
            else
            {
                Integer v_Step = null;
                try
                {
                    v_Step = (Integer) ValueHelp.getValue(this.step ,Integer.class ,1 ,i_Context);  // 默认值：1
                }
                catch (Exception exce)
                {
                    $Logger.error("ForConfig[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s step[" + this.step + "] error." ,exce);
                }
                
                if ( v_OldIndex != null )
                {
                    v_Start = v_OldIndex + v_Step;
                }
                
                v_Builder.append(DBSQL.$Placeholder).append(this.indexID).append("=") .append(v_Start).append("; ")
                         .append(DBSQL.$Placeholder).append(this.indexID).append("<=").append(v_End)  .append("; ")
                         .append(DBSQL.$Placeholder).append(this.indexID).append("+=").append(v_Step);
            }
        }
        
        v_Builder.append(")");
        
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
        
        v_Builder.append("for (");
        
        // 集合循环
        if ( Help.isNull(this.start) )
        {
            // 集合循环： for (end)
            if ( Help.isNull(this.elementID) )
            {
                v_Builder.append(Help.NVL(this.end ,"?"));
            }
            // 集合循环： for (elementID : end)
            else 
            {
                v_Builder.append(DBSQL.$Placeholder).append(this.elementID).append(" : ").append(Help.NVL(this.end ,"?"));
            }
        }
        // 数值循环
        else
        {
            // 数值循环： for (start .. end)
            if ( Help.isNull(this.indexID) )
            {
                v_Builder.append(this.start).append(" .. ").append(Help.NVL(this.end ,"?"));
            }
            // 数值循环： for (indexID=start; indexID<=end; indexID+=step)
            else
            {
                v_Builder.append(DBSQL.$Placeholder).append(this.indexID).append("=") .append(this.start).append("; ")
                         .append(DBSQL.$Placeholder).append(this.indexID).append("<=").append(Help.NVL(this.end ,"?")).append("; ")
                         .append(DBSQL.$Placeholder).append(this.indexID).append("+=").append(Help.NVL(this.step ,"1"));
            }
        }
        
        v_Builder.append(")");
        
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
        return new ForConfig();
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
        ForConfig v_Clone = new ForConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.start     = this.start;
        v_Clone.end       = this.end;
        v_Clone.step      = this.step;
        v_Clone.indexID   = this.indexID;
        v_Clone.indexNo   = this.indexNo;
        v_Clone.elementID = this.elementID;
        
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
            throw new NullPointerException("Clone ForConfig xid is null.");
        }
        
        ForConfig v_Clone = (ForConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.start     = this.start;
        v_Clone.end       = this.end;
        v_Clone.step      = this.step;
        v_Clone.indexID   = this.indexID;
        v_Clone.indexNo   = this.indexNo;
        v_Clone.elementID = this.elementID;
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
            throw new NullPointerException("Clone ForConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ForConfig                   v_Clone      = new ForConfig();
        
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
