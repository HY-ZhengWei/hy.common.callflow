package org.hy.common.callflow.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.KVKLinkMap;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.TotalNano;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.TreeIDHelp;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ExecuteStatus;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RedoType;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.mock.MockConfig;
import org.hy.common.callflow.mock.MockException;
import org.hy.common.callflow.mock.MockItem;
import org.hy.common.callflow.route.RouteConfig;
import org.hy.common.callflow.route.RouteItem;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 执行元素。
 * 它是执行节点、条件逻辑的基类。
 * 它主要定义元素的公共属性、界面展示属性、执行方法。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 *              v2.0  2025-08-16  添加：按导出类型生成三种XML内容
 *              v3.0  2025-09-26  添加：静态检查
 *              v4.0  2025-10-09  添加：是否在初始时立即执行
 *              v4.1  2025-10-10  添加：是否在初始时延时执行
 */
public abstract class ExecuteElement extends TotalNano implements IExecute ,Cloneable
{
    
    private static final Logger     $Logger = new Logger(ExecuteElement.class ,true);
    
    public  static final TreeIDHelp $TreeID = new TreeIDHelp("-" ,1 ,1);
    
    
    
    /** 关键属性有改动 */
    private   boolean                       keyChange;
    
    /** 主键标识 */
    protected String                        id;
                                         
    /** 全局惟一标识ID */
    protected String                        xid;
    
    /** 层级树ID。Map.key为本级树ID，Map.value为上级树ID */
    protected KVKLinkMap<String ,String>    treeIDs;
    
    /** 树层级。Map.key为本级树ID，Map.value为层次 */
    protected Map<String ,Integer>          treeLevels;
    
    /** 树中同层同父的序号编号。Map.key为本级树ID，Map.value为序号编号 */
    protected Map<String ,Integer>          treeNos;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    protected String                        comment;
                                         
    /** 整体样式名称 */                  
    protected String                        styleName;
                                         
    /** 位置x坐标值 */                   
    protected Double                        x;
                                         
    /** 位置y坐标值 */                   
    protected Double                        y;
                                         
    /** 位置z坐标值 */                   
    protected Double                        z;
                                         
    /** 图标高度 */                      
    protected Double                        height;
                                         
    /** 图标宽度 */                      
    protected Double                        width;
                                         
    /** 图标路径 */                      
    protected String                        iconURL;
                                         
    /** 透明度 */                        
    protected Double                        opacity;
                                         
    /** 背景色 */                        
    protected String                        backgroudColor;
                                         
    /** 边框线样式 */                    
    protected String                        lineStyle;
                                         
    /** 边框线颜色 */                    
    protected String                        lineColor;
                                         
    /** 边框线粗细 */                    
    protected Double                        lineSize;
                                         
    /** 文字颜色 */                      
    protected String                        fontColor;
                                         
    /** 文字名称 */                      
    protected String                        fontFamily;
                                         
    /** 文字粗体 */                      
    protected String                        fontWeight;
                                         
    /** 文字大小 */                      
    protected Double                        fontSize;
                                         
    /** 文字对齐方式 */                  
    protected String                        fontAlign;
                                         
    /** 创建人编号 */                    
    protected String                        createUserID;
                                         
    /** 修改者编号 */                    
    protected String                        updateUserID;
                                         
    /** 创建时间 */                      
    protected Date                          createTime;
                                         
    /** 最后修改时间 */                  
    protected Date                          updateTime;
                                         
    /** 为返回值定义的变量ID */          
    protected String                        returnID;
    
    /** 执行状态定义的变量ID */
    protected String                        statusID;
                                         
    /** 执行链：双向链表：前几个 */             
    protected List<IExecute>                previous;
                                         
    /** 执行链：双向链表：其后多个路由 */
    protected RouteConfig                   route;
    
    /** 编排整体重做的类型 */
    protected RedoType                      redoType;
    
    /** 模拟元素 */
    protected MockConfig                    mock;
    
    /** 向上下文中赋值 */
    protected String                        context;
    
    /** 向上下文中赋值，已解释完成的占位符（性能有优化，仅内部使用） */
    protected PartitionMap<String ,Integer> contextPlaceholders;
    
    /** 是否在初始时立即执行。注: 无论它delayedTime的值是多少，对象克隆时不会触发执行 */
    protected String                        delayedTime;
    
    
    
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
    public abstract boolean check(Return<Object> io_Result);
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-21
     * @version     v1.0
     *
     * @return
     */
    public abstract String makeXID();
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public ExecuteElement(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.treeIDs     = new KVKLinkMap   <String ,String>();
        this.treeLevels  = new LinkedHashMap<String ,Integer>();
        this.treeNos     = new LinkedHashMap<String ,Integer>();
        this.route       = new RouteConfig(this);
        this.mock        = new MockConfig();
        this.keyChange   = false;
        this.delayedTime = "-1";
        this.redoType    = RedoType.Default;
    }
    
    
    
    /**
     * 编排整体二次重做
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-17
     * @version     v1.0
     *
     * @param io_Context    上下文类型的变量信息
     * @param i_BeginTime   编排元素的开始时间
     * @param io_Result     编排元素的执行结果
     * @return              返回 true 表示：当前元素真的重新执行一次；否则，不会真的执行元素，而是延用上次执行结果
     */
    protected boolean redo(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result)
    {
        ExecuteResultNext v_OldERNext = CallFlow.getRedo(io_Context);
        if ( v_OldERNext != null )
        {
            ExecuteResult v_OldResult = v_OldERNext.next();
            if ( v_OldResult != null )
            {
                // 上次的与本次的执行元素必须一致
                if ( v_OldResult.getExecuteXID().equals(io_Result.getExecuteXID()) )
                {
                    // 必须重做：主动重做
                    if ( RedoType.Active.equals(this.redoType) )
                    {
                        return true;
                    }
                    // 上次异常时：重做
                    else if ( !v_OldResult.isSuccess() )
                    {
                        return true;
                    }
                    // 用户标记重做
                    else if ( RedoType.Active.equals(v_OldResult.getRedoType()) )
                    {
                        return true;
                    }
                    // 返回上次的结果
                    else
                    {
                        io_Result.setResult(v_OldResult.getResult());
                        io_Result.setExecuteLogic("NoRedo：" + io_Result.getExecuteLogic());
                        
                        this.refreshReturn(io_Context ,io_Result.getResult());
                        this.refreshStatus(io_Context ,io_Result.getStatus());
                        this.success(Date.getTimeNano() - i_BeginTime);
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-07
     * @version     v1.0
     *
     * @param io_Context     上下文类型的变量信息
     * @param i_BeginTime    编排元素的开始时间
     * @param io_Result      编排元素的执行结果
     * @param i_JsonRootKey  Json子项Key名称
     * @param i_DataClass    模拟数据的元类型
     * @return               表示是否有模拟数据
     */
    protected boolean mock(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result ,String i_JsonRootKey ,String i_DataClass) 
    {
        try
        {
            Object v_MockData = this.mock.mock(io_Context ,i_JsonRootKey ,i_DataClass);
            if ( v_MockData != null )
            {
                io_Result.setResult(v_MockData);
                io_Result.setExecuteLogic("Mock：" + io_Result.getExecuteLogic());
                
                this.refreshReturn(io_Context ,io_Result.getResult());
                this.refreshStatus(io_Context ,io_Result.getStatus());
                this.success(Date.getTimeNano() - i_BeginTime);
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (MockException exce)
        {
            io_Result.setExecuteLogic("Mock exception：" + io_Result.getExecuteLogic());
            io_Result.setException(exce);
            this.refreshStatus(io_Context ,io_Result.getStatus());
            return true;
        }
        catch (Exception exce)
        {
            io_Result.setException(exce);
            this.refreshStatus(io_Context ,io_Result.getStatus());
            return true;
        }
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-10
     * @version     v1.0
     *
     * @param io_Context     上下文类型的变量信息
     * @param i_BeginTime    编排元素的开始时间
     * @param io_Result      编排元素的执行结果
     * @param i_Split        行分隔符
     * @return               表示是否有模拟数据
     */
    protected boolean mockRows(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result ,String i_Split) 
    {
        try
        {
            List<String> v_MockData = this.mock.mockRows(io_Context ,i_Split);
            if ( v_MockData != null )
            {
                io_Result.setResult(v_MockData);
                io_Result.setExecuteLogic("Mock：" + io_Result.getExecuteLogic());
                
                this.refreshReturn(io_Context ,io_Result.getResult());
                this.refreshStatus(io_Context ,io_Result.getStatus());
                this.success(Date.getTimeNano() - i_BeginTime);
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (MockException exce)
        {
            io_Result.setExecuteLogic("Mock exception：" + io_Result.getExecuteLogic());
            io_Result.setException(exce);
            this.refreshStatus(io_Context ,io_Result.getStatus());
            return true;
        }
        catch (Exception exce)
        {
            io_Result.setException(exce);
            this.refreshStatus(io_Context ,io_Result.getStatus());
            return true;
        }
    }
    
    
    
    /**
     * 关键属性有改动
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-17
     * @version     v1.0
     *
     */
    protected void keyChange()
    {
        this.keyChange = true;
        if ( !Help.isNull(this.previous) )
        {
            for (IExecute v_Item : this.previous)
            {
                ((ExecuteElement) v_Item).keyChange();
            }
        }
        // System.out.println("keyChange：" + this.xid);
    }
    
    
    
    /**
     * 通过检测（静态检测环境），并符合要求。
     * 
     * 详见：ExecuteElementCheckHelp
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-17
     * @version     v1.0
     *
     */
    protected void checkOK()
    {
        this.keyChange = false;
    }
    
    
    
    /**
     * 关键属性有改动
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-17
     * @version     v1.0
     *
     * @return
     */
    public boolean isKeyChange()
    {
        return this.keyChange;
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
            throw new IllegalArgumentException("ExecuteElement's XID[" + i_Xid + "] is SystemXID.");
        }
        this.xid = i_Xid;
        this.keyChange();
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
            throw new IllegalArgumentException("ExecuteElement's XJavaID[" + i_XJavaID + "] is SystemXID.");
        }
        this.xid = i_XJavaID;
        this.keyChange();
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
    
    
    
    /**
     * 获取：向上下文中赋值
     */
    public synchronized String getContext()
    {
        return context;
    }

    
    
    /**
     * 设置：向上下文中赋值
     * 
     * @param i_Context 向上下文中赋值
     */
    public synchronized void setContext(String i_Context)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Context) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Context ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.contextPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.contextPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.context = i_Context;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 处理与解释自定义的上下文内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-07-09
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Result   编排结果
     * @return            是否处理与解释成功
     */
    @SuppressWarnings("unchecked")
    protected Exception handleContext(Map<String ,Object> io_Context)
    {
        if ( !Help.isNull(this.context) )
        {
            try
            {
                String v_Context = (String) ValueHelp.getValueReplace(this.context ,this.contextPlaceholders ,String.class ,"" ,io_Context);
                Map<String ,Object> v_ContextMap = (Map<String ,Object>) ValueHelp.getValue(v_Context ,Map.class ,null ,io_Context);
                io_Context.putAll(v_ContextMap);
                v_ContextMap.clear();
                v_ContextMap = null;
            }
            catch (Exception exce)
            {
                return exce;
            }
        }
        
        return null;
    }
    
    
    
    
    /**
     * 获取：是否在初始时立即执行
     */
    public String getExecute()
    {
        return delayedTime;
    }


    
    /**
     * 设置：是否在初始时立即执行
     * 
     * @param i_DelayedTime   延时执行时长(单位：毫秒)，0为立即执行，负数不执行，正数延时执行
     * @throws Exception 
     */
    public synchronized void setExecute(String i_DelayedTime)
    {
        this.delayedTime = i_DelayedTime.trim();
        if ( !Help.isNull(this.delayedTime) )
        {
            Long v_DelayedTime = null;
            try
            {
                v_DelayedTime = (Long) ValueHelp.getValue(this.delayedTime ,Long.class ,0L ,null);
                
                if ( v_DelayedTime != null )
                {
                    if ( v_DelayedTime == 0L )
                    {
                        this.execute();
                    }
                    else if ( v_DelayedTime > 0L )
                    {
                        Timer v_Timer = new Timer();
                        v_Timer.schedule(new TimerTask() 
                        {
                            @Override
                            public void run()
                            {
                                execute();
                            }
                        } ,v_DelayedTime); // 延时执行
                    }
                }
            }
            catch (Exception exce)
            {
                $Logger.error("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "].initExecute error" ,exce);
            }
        }
    }
    
    
    
    /**
     * 立即执行元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-10
     * @version     v1.0
     *
     */
    private void execute()
    {
        Return<Object> v_CheckRet = CallFlow.getHelpCheck().check(this);
        if ( !v_CheckRet.get() )
        {
            $Logger.error(v_CheckRet.getParamStr());  // 打印静态检查不合格的原因
            return;
        }
        
        // 没有此步下面的执行编排无法成功，因为XJava在初始本对象的过程还没有完成呢
        XJava.putObject(this.getXid() ,this);
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        ExecuteResult       v_Result  = CallFlow.execute(this ,v_Context);
        if ( !v_Result.isSuccess() )
        {
            StringBuilder v_ErrorLog = new StringBuilder();
            v_ErrorLog.append("Error XID = " + v_Result.getExecuteXID()).append("\n");
            v_ErrorLog.append("Error Msg = " + v_Result.getException().getMessage());
            if ( v_Result.getException() instanceof TimeoutException )
            {
                v_ErrorLog.append("is TimeoutException");
            }
            $Logger.error(v_ErrorLog.toString() ,v_Result.getException());
        }
        
        // 打印执行路径
        ExecuteResult v_FirstResult = CallFlow.getFirstResult(v_Context);
        $Logger.info("\n" + CallFlow.getHelpLog().logs(v_FirstResult));
        
        v_Context.clear();
        v_Context = null;
    }



    /**
     * 注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     *
     * @return
     */
    public String getComment()
    {
        return this.comment;
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
     * 生成本次树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     * @return               返回生成的树ID
     */
    public String setTreeID(String i_SuperTreeID ,int i_IndexNo)
    {
        String v_TreeID = $TreeID.getTreeID(i_SuperTreeID ,i_IndexNo);
        this.setTreeID(v_TreeID);
        return v_TreeID;
    }


    
    /**
     * 设置：层级树ID。
     * 注：当入参为空或NULL时将做清理动作
     * 
     * @param i_TreeID 层级树ID
     */
    public void setTreeID(String i_TreeID)
    {
        if ( Help.isNull(i_TreeID) )
        {
            this.treeIDs   .clear();
            this.treeLevels.clear();
            this.treeNos   .clear();
        }
        else
        {
            String v_SuperTreeID = $TreeID.getSuperTreeID(i_TreeID);
            if ( this.treeIDs.getReverse(v_SuperTreeID) != null )
            {
                throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s SuperTreeID[" + v_SuperTreeID + "] is exists for TreeID[" + i_TreeID + "].");
            }
            
            this.treeIDs   .put(i_TreeID ,v_SuperTreeID);
            this.treeLevels.put(i_TreeID ,$TreeID.getLevel(  i_TreeID));
            this.treeNos   .put(i_TreeID ,$TreeID.getIndexNo(i_TreeID));
        }
    }
    
    
    
    /**
     * 获取：层级树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return  树ID顺序按先后添加次序返回
     */
    public Collection<String> getTreeIDs()
    {
        return this.treeIDs.keySet();
    }
    
    
    
    /**
     * 获取：层级树ID
     * 
     * @param i_SuperTreeID  上级树ID
     */
    public String getTreeID(String i_SuperTreeID)
    {
        return Help.NVL(this.treeIDs.getReverse(Help.NVL(i_SuperTreeID)));
    }
    
    
    
    /**
     * 获取：上级树ID
     * 
     * @param i_TreeID  本级树ID
     */
    public String getTreeSuperID(String i_TreeID)
    {
        return Help.NVL(this.treeIDs.get(Help.NVL(i_TreeID)));
    }


    
    /**
     * 获取：树层级
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeLevel(String i_TreeID)
    {
        return this.treeLevels.get(Help.NVL(i_TreeID));
    }


    
    /**
     * 获取：树中同层同父的序号编号
     * 
     * @param i_TreeID  本级树ID
     */
    public Integer getTreeNo(String i_TreeID)
    {
        return this.treeNos.get(Help.NVL(i_TreeID));
    }
    
    
    
    /**
     * 获取最大的树ID。层次越高，值越大
     * 
     * 举例：当同时有1.3、1.0 时，返回 1.0
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMaxTreeID()
    {
        if ( this.treeIDs.size() <= 0 )
        {
            return "";
        }
        else if ( this.treeIDs.size() == 1 )
        {
            return this.treeIDs.keySet().iterator().next();
        }
        else
        {
            String [] v_TreeIDs = this.treeIDs.keySet().toArray(new String[] {});
            Arrays.sort(v_TreeIDs ,$TreeID);
            return v_TreeIDs[v_TreeIDs.length - 1];
        }
    }
    
    
    
    /**
     * 获取最小的树ID。层次越低，值越小
     * 
     * 举例：当同时有1.3、1.0 时，返回 1.3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @return
     */
    public String getMinTreeID()
    {
        if ( this.treeIDs.size() <= 0 )
        {
            return "";
        }
        else if ( this.treeIDs.size() == 1 )
        {
            return this.treeIDs.keySet().iterator().next();
        }
        else
        {
            String [] v_TreeIDs = this.treeIDs.keySet().toArray(new String[] {});
            Arrays.sort(v_TreeIDs ,$TreeID);
            return v_TreeIDs[0];
        }
    }



    /**
     * 获取：整体样式名称
     */
    public String getStyleName()
    {
        return styleName;
    }


    
    /**
     * 设置：整体样式名称
     * 
     * @param i_StyleName 整体样式名称
     */
    public void setStyleName(String i_StyleName)
    {
        this.styleName = i_StyleName;
    }


    
    /**
     * 获取：位置x坐标值
     */
    public Double getX()
    {
        return x;
    }


    
    /**
     * 设置：位置x坐标值
     * 
     * @param i_X 位置x坐标值
     */
    public void setX(Double i_X)
    {
        this.x = i_X;
    }


    
    /**
     * 获取：位置y坐标值
     */
    public Double getY()
    {
        return y;
    }


    
    /**
     * 设置：位置y坐标值
     * 
     * @param i_Y 位置y坐标值
     */
    public void setY(Double i_Y)
    {
        this.y = i_Y;
    }


    
    /**
     * 获取：位置z坐标值
     */
    public Double getZ()
    {
        return z;
    }


    
    /**
     * 设置：位置z坐标值
     * 
     * @param i_Z 位置z坐标值
     */
    public void setZ(Double i_Z)
    {
        this.z = i_Z;
    }


    
    /**
     * 获取：图标高度
     */
    public Double getHeight()
    {
        return height;
    }


    
    /**
     * 设置：图标高度
     * 
     * @param i_Height 图标高度
     */
    public void setHeight(Double i_Height)
    {
        this.height = i_Height;
    }


    
    /**
     * 获取：图标宽度
     */
    public Double getWidth()
    {
        return width;
    }


    
    /**
     * 设置：图标宽度
     * 
     * @param i_Width 图标宽度
     */
    public void setWidth(Double i_Width)
    {
        this.width = i_Width;
    }


    
    /**
     * 获取：图标路径
     */
    public String getIconURL()
    {
        return iconURL;
    }


    
    /**
     * 设置：图标路径
     * 
     * @param i_IconURL 图标路径
     */
    public void setIconURL(String i_IconURL)
    {
        this.iconURL = i_IconURL;
    }


    
    /**
     * 获取：透明度
     */
    public Double getOpacity()
    {
        return opacity;
    }


    
    /**
     * 设置：透明度
     * 
     * @param i_Opacity 透明度
     */
    public void setOpacity(Double i_Opacity)
    {
        this.opacity = i_Opacity;
    }


    
    /**
     * 获取：背景色
     */
    public String getBackgroudColor()
    {
        return backgroudColor;
    }


    
    /**
     * 设置：背景色
     * 
     * @param i_BackgroudColor 背景色
     */
    public void setBackgroudColor(String i_BackgroudColor)
    {
        this.backgroudColor = i_BackgroudColor;
    }


    
    /**
     * 获取：边框线样式
     */
    public String getLineStyle()
    {
        return lineStyle;
    }


    
    /**
     * 设置：边框线样式
     * 
     * @param i_LineStyle 边框线样式
     */
    public void setLineStyle(String i_LineStyle)
    {
        this.lineStyle = i_LineStyle;
    }


    
    /**
     * 获取：边框线颜色
     */
    public String getLineColor()
    {
        return lineColor;
    }


    
    /**
     * 设置：边框线颜色
     * 
     * @param i_LineColor 边框线颜色
     */
    public void setLineColor(String i_LineColor)
    {
        this.lineColor = i_LineColor;
    }


    
    /**
     * 获取：边框线粗细
     */
    public Double getLineSize()
    {
        return lineSize;
    }


    
    /**
     * 设置：边框线粗细
     * 
     * @param i_LineSize 边框线粗细
     */
    public void setLineSize(Double i_LineSize)
    {
        this.lineSize = i_LineSize;
    }


    
    /**
     * 获取：文字颜色
     */
    public String getFontColor()
    {
        return fontColor;
    }


    
    /**
     * 设置：文字颜色
     * 
     * @param i_FontColor 文字颜色
     */
    public void setFontColor(String i_FontColor)
    {
        this.fontColor = i_FontColor;
    }


    
    /**
     * 获取：文字名称
     */
    public String getFontFamily()
    {
        return fontFamily;
    }


    
    /**
     * 设置：文字名称
     * 
     * @param i_FontFamily 文字名称
     */
    public void setFontFamily(String i_FontFamily)
    {
        this.fontFamily = i_FontFamily;
    }


    
    /**
     * 获取：文字粗体
     */
    public String getFontWeight()
    {
        return fontWeight;
    }


    
    /**
     * 设置：文字粗体
     * 
     * @param i_FontWeight 文字粗体
     */
    public void setFontWeight(String i_FontWeight)
    {
        this.fontWeight = i_FontWeight;
    }


    
    /**
     * 获取：文字大小
     */
    public Double getFontSize()
    {
        return fontSize;
    }


    
    /**
     * 设置：文字大小
     * 
     * @param i_FontSize 文字大小
     */
    public void setFontSize(Double i_FontSize)
    {
        this.fontSize = i_FontSize;
    }


    
    /**
     * 获取：文字对齐方式
     */
    public String getFontAlign()
    {
        return fontAlign;
    }


    
    /**
     * 设置：文字对齐方式
     * 
     * @param i_FontAlign 文字对齐方式
     */
    public void setFontAlign(String i_FontAlign)
    {
        this.fontAlign = i_FontAlign;
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
        if ( CallFlow.isSystemXID(i_ReturnID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s returnID[" + i_ReturnID + "] is SystemXID.");
        }
        
        this.returnID = ValueHelp.standardValueID(i_ReturnID);
        this.keyChange();
    }
    
    
    
    /**
     * 获取：执行状态定义的变量ID
     */
    public String getStatusID()
    {
        return statusID;
    }

    
    
    /**
     * 设置：执行状态定义的变量ID
     * 
     * @param i_StatusID 执行状态定义的变量ID
     */
    public void setStatusID(String i_StatusID)
    {
        if ( CallFlow.isSystemXID(i_StatusID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s statusID[" + i_StatusID + "] is SystemXID.");
        }
        
        this.statusID = ValueHelp.standardValueID(i_StatusID);
        this.keyChange();
    }
    
    
    
    /**
     * 获取：执行链：双向链表：前几个
     * 防止Json无限制循环
     */
    public List<IExecute> gatPrevious()
    {
        return previous;
    }


    
    /**
     * 设置：执行链：双向链表：前一个
     * 
     * @param i_Previous 执行链：双向链表：前一个
     */
    public synchronized void setPrevious(ExecuteElement i_Previous)
    {
        if ( Help.isNull(this.previous) )
        {
            this.previous = new ArrayList<IExecute>();
        }
        this.previous.add(i_Previous);
        this.keyChange();
    }


    
    /**
     * 获取：执行链：双向链表：其后多个路由
     */
    public RouteConfig getRoute()
    {
        return route;
    }


    
    /**
     * 设置：执行链：双向链表：其后多个路由
     * 
     * @param i_Route 执行链：双向链表：其后多个路由
     */
    public void setRoute(RouteConfig i_Route)
    {
        this.route = i_Route;
        this.keyChange();
        if ( this.route != null )
        {
            this.route.setOwner(this);
        }
    }
    
    
    
    /**
     * 获取：模拟元素
     */
    public MockConfig getMock()
    {
        return mock;
    }


    
    /**
     * 设置：模拟元素
     * 
     * @param i_Mock 模拟元素
     */
    public void setMock(MockConfig i_Mock)
    {
        this.mock = i_Mock;
    }


    
    /**
     * 获取：编排整体重做的类型
     */
    public RedoType getRedoType()
    {
        return redoType;
    }


    
    /**
     * 设置：编排整体重做的类型
     * 
     * @param i_RedoType 编排整体重做的类型
     */
    public void setRedoType(RedoType i_RedoType)
    {
        this.redoType = i_RedoType;
    }
    


    /**
     * 刷新返回值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-21
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Return    返回值
     */
    protected void refreshReturn(Map<String ,Object> io_Context ,Object i_Return)
    {
        if ( !Help.isNull(this.returnID) && io_Context != null )
        {
            io_Context.put(this.returnID ,i_Return);
        }
    }
    
    
    
    /**
     * 刷新执行状态
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param i_Status    执行状态
     */
    protected void refreshStatus(Map<String ,Object> io_Context ,ExecuteStatus i_Status)
    {
        if ( !Help.isNull(this.statusID) && io_Context != null )
        {
            io_Context.put(this.statusID ,i_Status.getValue());
        }
    }



    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *              v2.0  2025-08-15  添加：导出类型
     *
     * @param i_Level       层级。最小下标从0开始。
     *                         0表示每行前面有0个空格；
     *                         1表示每行前面有4个空格；
     *                         2表示每行前面有8个空格；
     * @param i_ExportType  导出类型
     *                  
     * @return
     */
    public String toXml(int i_Level ,ExportType i_ExportType)
    {
        StringBuilder v_Xml      = new StringBuilder();
        String        v_Level1   = "    ";
        String        v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_NewSpace = "\n" + v_LevelN + v_Level1;
        
        if ( !ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.comment) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("comment" ,this.comment));
            }
            if ( !Help.isNull(this.id) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("id" ,this.id));
            }
            if ( !Help.isNull(this.treeIDs) )
            {
                for (String v_TreeID : this.treeIDs.keySet())
                {
                    v_Xml.append(v_NewSpace).append(IToXml.toValue("treeID" ,v_TreeID));
                }
            }
            if ( !Help.isNull(this.context) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("context" ,this.context ,v_NewSpace));
            }
            if ( !RedoType.Default.equals(this.redoType) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("redoType" ,this.redoType.getValue()));
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
            if ( !Help.isNull(this.styleName) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("styleName" ,this.styleName));
            }
            if ( this.x != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("x" ,this.x));
            }
            if ( this.y != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("y" ,this.y));
            }
            if ( this.z != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("z" ,this.z));
            }
            if ( this.height != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("height" ,this.height));
            }
            if ( this.width != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("width" ,this.width));
            }
            if ( !Help.isNull(this.iconURL) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("iconURL" ,this.iconURL));
            }
            if ( this.opacity != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("opacity" ,this.opacity));
            }
            if ( !Help.isNull(this.backgroudColor) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("backgroudColor" ,this.backgroudColor));
            }
            if ( !Help.isNull(this.lineStyle) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("lineStyle" ,this.lineStyle));
            }
            if ( !Help.isNull(this.lineColor) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("lineColor" ,this.lineColor));
            }
            if ( !Help.isNull(this.lineSize) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("lineSize" ,this.lineSize));
            }
            if ( !Help.isNull(this.fontColor) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("fontColor" ,this.fontColor));
            }
            if ( !Help.isNull(this.fontFamily) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("fontFamily" ,this.fontFamily));
            }
            if ( !Help.isNull(this.fontWeight) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("fontWeight" ,this.fontWeight));
            }
            if ( !Help.isNull(this.fontSize) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("fontSize" ,this.fontSize));
            }
            if ( !Help.isNull(this.fontAlign) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("fontAlign" ,this.fontAlign));
            }
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
                v_Xml.append(v_NewSpace).append(IToXml.toValue("createTime" ,this.createTime.getFull()));
            }
            if ( this.updateTime != null )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("updateTime" ,this.updateTime.getFull()));
            }
        }
        
        return v_Xml.toString();
    }
    
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param io_Xml        输出的字符串缓存
     * @param i_RouteItems  输出的字符串缓存
     * @param i_XmlName     XML节点名称
     * @param i_Level       层级。最小下标从0开始。
     *                         0表示每行前面有0个空格；
     *                         1表示每行前面有4个空格；
     *                         2表示每行前面有8个空格；
     * @param i_SuperTreeID 上级树ID
     * @param i_ExportType  导出类型
     *                  
     * @return
     */
    protected void toXmlMockItems(StringBuilder  io_Xml 
                                 ,List<MockItem> i_MockItems 
                                 ,String         i_XmlName 
                                 ,int            i_Level
                                 ,String         i_SuperTreeID
                                 ,ExportType     i_ExportType)
    {
        if ( !Help.isNull(i_MockItems) )
        {
            String v_Level1   = "    ";
            String v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
            String v_NewSpace = "\n" + v_LevelN + v_Level1;
            
            for (MockItem v_MockItem : i_MockItems)
            {
                if ( ExportType.UI.equals(i_ExportType) )
                {
                    io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toBeginThis(i_XmlName ,v_MockItem.getXJavaID()));
                }
                else
                {
                    io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toBeginID(i_XmlName ,v_MockItem.getXJavaID()));
                }
                
                io_Xml.append(v_MockItem.toXml(i_Level + 1 ,i_SuperTreeID ,i_ExportType));
                io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toEnd(i_XmlName));
            }
        }
    }
    
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-16
     * @version     v1.0
     *
     * @param io_Xml        输出的字符串缓存
     * @param i_RouteItems  输出的字符串缓存
     * @param i_XmlName     XML节点名称
     * @param i_Level       层级。最小下标从0开始。
     *                         0表示每行前面有0个空格；
     *                         1表示每行前面有4个空格；
     *                         2表示每行前面有8个空格；
     * @param i_SuperTreeID 上级树ID
     * @param i_ExportType  导出类型
     *                  
     * @return
     */
    protected void toXmlRouteItems(StringBuilder   io_Xml 
                                  ,List<RouteItem> i_RouteItems 
                                  ,String          i_XmlName 
                                  ,int             i_Level
                                  ,String          i_SuperTreeID
                                  ,ExportType      i_ExportType)
    {
        if ( !Help.isNull(i_RouteItems) )
        {
            String v_Level1   = "    ";
            String v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
            String v_NewSpace = "\n" + v_LevelN + v_Level1;
            
            for (RouteItem v_RouteItem : i_RouteItems)
            {
                if ( ExportType.UI.equals(i_ExportType) )
                {
                    io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toBeginThis(i_XmlName ,v_RouteItem.getXJavaID()));
                }
                else
                {
                    io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toBeginID(i_XmlName ,v_RouteItem.getXJavaID()));
                }
                
                io_Xml.append(v_RouteItem.toXml(i_Level + 1 ,i_SuperTreeID ,i_ExportType));
                io_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toEnd(i_XmlName));
            }
        }
    }
    
    
    
    /**
     * 转为Xml格式的内容
     * 
     * 注：它应在元素所有属性（非界面UI属性）最后面的位置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-09
     * @version     v1.0
     *
     * @param io_Xml      输出的字符串缓存
     * @param i_NewSpace  每行的前缀空格信息
     */
    protected void toXmlExecute(StringBuilder io_Xml ,String i_NewSpace)
    {
        if ( !Help.isNull(this.delayedTime) && !"-1".equalsIgnoreCase(this.delayedTime) )
        {
            io_Xml.append(i_NewSpace).append(IToXml.toValue("execute" ,this.delayedTime));
        }
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     * 
     * @param io_Clone
     */
    protected void cloneMyOnly(ExecuteElement io_Clone)
    {
        ExecuteElement v_Clone = io_Clone;
        
        v_Clone.id              = this.id;
        v_Clone.treeIDs.putAll(   this.treeIDs);
        v_Clone.treeLevels.putAll(this.treeLevels);
        v_Clone.treeNos.putAll(   this.treeNos);
        v_Clone.comment         = this.comment;
        v_Clone.styleName       = this.styleName;
        v_Clone.x               = this.x;
        v_Clone.y               = this.y;
        v_Clone.z               = this.z;
        v_Clone.height          = this.height;
        v_Clone.width           = this.width;
        v_Clone.iconURL         = this.iconURL;
        v_Clone.opacity         = this.opacity;
        v_Clone.backgroudColor  = this.backgroudColor;
        v_Clone.lineStyle       = this.lineStyle;
        v_Clone.lineColor       = this.lineColor;
        v_Clone.lineSize        = this.lineSize;
        v_Clone.fontColor       = this.fontColor;
        v_Clone.fontFamily      = this.fontFamily;
        v_Clone.fontWeight      = this.fontWeight;
        v_Clone.fontSize        = this.fontSize;
        v_Clone.fontAlign       = this.fontAlign;
        v_Clone.createUserID    = this.createUserID;
        v_Clone.updateUserID    = this.updateUserID;
        v_Clone.createTime      = this.createTime == null ? null : new Date(this.createTime.getTime());
        v_Clone.updateTime      = this.updateTime == null ? null : new Date(this.updateTime.getTime());
        v_Clone.returnID        = this.returnID;
        v_Clone.statusID        = this.statusID;
        v_Clone.context         = this.context;
        v_Clone.redoType        = this.redoType;
        v_Clone.delayedTime     = this.delayedTime;
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
        ExecuteElement v_Clone = (ExecuteElement) io_Clone;
        
        v_Clone.reset(this.getRequestTotal() ,this.getSuccessTotal());
        v_Clone.xid = this.cloneXID(this.xid ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID);
        io_XIDObjects.put(v_Clone.xid ,v_Clone);
        
        this.cloneMyOnly(v_Clone);
        
        if ( !Help.isNull(this.previous) )
        {
            v_Clone.previous = new ArrayList<IExecute>();
            for (IExecute v_Item : this.previous)
            {
                String         v_CloneItemXID = this.cloneXID(v_Item.getXJavaID() ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID);
                ExecuteElement v_CloneItem    = io_XIDObjects.get(v_CloneItemXID);
                if ( v_CloneItem == null )
                {
                    throw new RuntimeException("Clone XID[" + v_CloneItemXID + "] object is not exist.");
                }
                
                v_Clone.previous.add(v_CloneItem);
            }
        }
        
        if ( !Help.isNull(this.route.getSucceeds()) )
        {
            for (RouteItem v_RouteItem : this.route.getSucceeds())
            {
                RouteItem v_CloneRouteItem = new RouteItem(v_Clone.getRoute() ,v_RouteItem.getRouteType());
                v_RouteItem.clone(v_CloneRouteItem ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.getRoute().setSucceed(v_CloneRouteItem);
            }
        }
        
        if ( !Help.isNull(this.route.getFaileds()) )
        {
            for (RouteItem v_RouteItem : this.route.getFaileds())
            {
                RouteItem v_CloneRouteItem = new RouteItem(v_Clone.getRoute() ,v_RouteItem.getRouteType());
                v_RouteItem.clone(v_CloneRouteItem ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.getRoute().setFailed(v_CloneRouteItem);
            }
        }
        
        if ( !Help.isNull(this.route.getExceptions()) )
        {
            for (RouteItem v_RouteItem : this.route.getExceptions())
            {
                RouteItem v_CloneRouteItem = new RouteItem(v_Clone.getRoute() ,v_RouteItem.getRouteType());
                v_RouteItem.clone(v_CloneRouteItem ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.getRoute().setException(v_CloneRouteItem);
            }
        }
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
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
        // 请不要直接调用此方法，应调用子类的
        throw new RuntimeException("Not allowed to call ExecuteElement.clone().");
    }
    
}
