package org.hy.common.callflow.node;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ExecuteStatus;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.route.RouteConfig;
import org.hy.common.db.DBSQL;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 节点配置信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeConfig extends ExecuteElement
{
    
    private static final Logger $Logger = new Logger(NodeConfig.class);
    
    
    
    /** 执行对象的XID */
    private String          callXID;
    
    /** 执行方法名称 */
    private String          callMehod;
    
    /** 执行方法对象（仅内部使用） */
    private Method          callMethodObject;
    
    /** 是否初始化 */
    private boolean         isInit;
    
    /** 执行方法的参数 */
    private List<NodeParam> callParams;
    
    /** 为返回值定义的变量ID */
    private String          returnID;
    
    /** 执行状态定义的变量ID */
    private String          statusID;
    
    /** 路由 */
    private RouteConfig     route;
    
    
    
    public NodeConfig()
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
    public NodeConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.route  = new RouteConfig();
        this.isInit = false;
    }
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-15
     * @version     v1.0
     *
     * @param i_IndexNo   本方法要执行的执行序号。下标从1开始
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(Map<String ,Object> io_Context)
    {
        ExecuteResult v_Result = new ExecuteResult(this.getTreeID() ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        if ( Help.isNull(this.callXID) )
        {
            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.callXID + "] is not find."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            try
            {
                for (int x=0; x<v_ParamValues.length; x++)
                {
                    NodeParam v_NodeParam  = this.callParams.get(x);
                    v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.getValueClass() ,v_NodeParam.getValueDefaultObject() ,io_Context);
                }
            }
            catch (Exception exce)
            {
                v_Result.setException(exce);
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
        }
        else
        {
            v_ParamValues = new Object[0];
        }
        
        try
        {
            this.init(v_CallObject ,v_ParamValues);
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        try
        {
            long   v_BeginTime = this.request();
            Object v_ExceRet   = Void.TYPE;
            if ( Void.TYPE.equals(this.callMethodObject.getReturnType()) )
            {
                this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
            }
            else
            {
                v_ExceRet = this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
            }
            
            v_Result.setResult(v_ExceRet);
            this.success(Date.getTimeNano() - v_BeginTime);
            this.refreshReturn(io_Context ,v_ExceRet);
            this.refreshStatus(io_Context ,v_Result.getStatus());
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
     * 手动初始化。
     * 因预先做了初始动作，可加速执行用时，统计数据精准。
     * 
     * 注：初始未成功时，会抛出异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     */
    public synchronized void init(Map<String ,Object> io_Context)
    {
        if ( this.isInit )
        {
            return;
        }
        
        if ( Help.isNull(this.callXID) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.callXID + "] is not find.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            try
            {
                for (int x=0; x<v_ParamValues.length; x++)
                {
                    NodeParam v_NodeParam  = this.callParams.get(x);
                    v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.getValueClass() ,v_NodeParam.getValueDefaultObject() ,io_Context);
                }
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
                throw new RuntimeException(exce);
            }
        }
        else
        {
            v_ParamValues = new Object[0];
        }
        
        this.init(v_CallObject ,v_ParamValues);
    }
    
    
    /**
     * 自动初始化。
     * 用于匹配执行方法。仅在首次或关键成员属性改变时才执行。
     * 
     * 注：初始未成功时，会抛出异常
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_CallObject   执行对象的实例
     * @param i_ParamValues  方法的执行参数
     */
    private synchronized void init(Object i_CallObject ,Object [] i_ParamValues)
    {
        if ( this.isInit )
        {
            return;
        }
        
        if ( Help.isNull(this.callMehod) )
        {
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod is null.");
        }
        
        List<Method> v_CallMethods = MethodReflect.getMethods(i_CallObject.getClass() ,this.callMehod ,i_ParamValues.length);
        if ( Help.isNull(v_CallMethods) )
        {
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "(" + i_ParamValues.length + ")] is not find.");
        }
        
        if ( v_CallMethods.size() == 1 )
        {
            this.isInit           = true;
            this.callMethodObject = v_CallMethods.get(0);
        }
        else
        {
            // 参照 MethodReflect.getMethodsBest() 方法配对到最佳方法
            Map<Integer ,Method> v_Bests = new HashMap<Integer ,Method>();
            for (Method v_Method : v_CallMethods)
            {
                int         v_BestValue    = 0;
                Class<?> [] v_MPClassTypes = v_Method.getParameterTypes();
                for (int y=v_MPClassTypes.length - 1; y>=0; y--)
                {
                    Class<?> v_ParamValueClass = null;
                    if ( i_ParamValues[y] == null )
                    {
                        v_ParamValueClass = this.callParams.get(y).getValueClass();
                    }
                    else
                    {
                        v_ParamValueClass = i_ParamValues[y].getClass();
                    }
                    
                    if ( v_ParamValueClass == null )
                    {
                        // Nothing.
                    }
                    else if ( v_ParamValueClass == v_MPClassTypes[y] )                                  // 3级：完全配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 3;
                    }
                    else if ( v_MPClassTypes[y] == Object.class )                                       // 1级：模糊配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y);
                    }
                    else if ( MethodReflect.isExtendImplement(v_ParamValueClass ,v_MPClassTypes[y]) )   // 2级：继承配对 或 接口实现配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 2;
                    }
                }
                
                if ( v_BestValue > 0 )
                {
                    v_Bests.put(v_BestValue ,v_Method);
                }
            }
            
            if ( !Help.isNull(v_Bests) )
            {
                v_CallMethods.clear();
                v_CallMethods = Help.toList(Help.toReverse(v_Bests));
                v_Bests.clear();
                
                if ( v_CallMethods.size() == 1 )
                {
                    this.isInit           = true;
                    this.callMethodObject = v_CallMethods.get(0);
                }
                else
                {
                    throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "(" + i_ParamValues.length + ")] is find " + v_CallMethods.size() + " methods.");
                }
            }
            else
            {
                throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "(" + i_ParamValues.length + ")] is not find.");
            }
        }
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
    private void refreshReturn(Map<String ,Object> io_Context ,Object i_Return)
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
    private void refreshStatus(Map<String ,Object> io_Context ,ExecuteStatus i_Status)
    {
        if ( !Help.isNull(this.statusID) && io_Context != null )
        {
            io_Context.put(this.statusID ,i_Status.getValue());
        }
    }
    
    
    /**
     * 获取：执行对象的XID
     */
    public String getCallXID()
    {
        return callXID;
    }

    
    /**
     * 设置：执行对象的XID
     * 
     * @param i_CallXID 执行对象的XID
     */
    public void setCallXID(String i_CallXID)
    {
        this.callXID = i_CallXID;
        this.isInit  = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }

    
    /**
     * 获取：执行方法名称
     */
    public String getCallMehod()
    {
        return callMehod;
    }

    
    /**
     * 设置：执行方法名称
     * 
     * @param i_CallMehod 执行方法名称
     */
    public void setCallMehod(String i_CallMehod)
    {
        this.callMehod = i_CallMehod;
        this.isInit    = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }
    
    
    /**
     * 添加执行方法的参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-19
     * @version     v1.0
     *
     * @param i_Param  执行方法的参数
     */
    public void setCallParam(NodeParam i_Param)
    {
        synchronized (this)
        {
            if ( this.callParams == null )
            {
                this.callParams = new ArrayList<NodeParam>();
            }
        }
        
        this.callParams.add(i_Param);
        this.isInit = false;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
    }

    
    /**
     * 获取：执行方法的参数
     */
    public List<NodeParam> getCallParams()
    {
        return callParams;
    }

    
    /**
     * 设置：执行方法的参数
     * 
     * @param i_CallParams 执行方法的参数
     */
    public void setCallParams(List<NodeParam> i_CallParams)
    {
        this.callParams = i_CallParams;
        this.isInit     = false;
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
        if ( CallFlow.$WorkID.equals(i_StatusID) )
        {
            throw new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s returnID[" + i_StatusID + "] equals " + CallFlow.$WorkID);
        }
        this.statusID = i_StatusID;
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
        String        v_XName  = "xnode";
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(v_XName ,this.getXJavaID()));
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level));
        
        if ( !Help.isNull(this.callXID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("callXID" ,this.callXID));
        }
        if ( !Help.isNull(this.callMehod) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("callMehod" ,this.callMehod));
        }
        if ( !Help.isNull(this.callParams) )
        {
            for (NodeParam v_Param : this.callParams)
            {
                v_Xml.append(v_Param.toXml(i_Level + 1));
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
          || !Help.isNull(this.route.getExceptions()) )
        {
            int v_MaxLpad = 0;
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                v_MaxLpad = 7;
            }
            
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toBegin("route"));
            
            // 成功路由
            if ( !Help.isNull(this.route.getSucceeds()) )
            {
                for (IExecute v_Item : this.route.getSucceeds())
                {
                    if ( !Help.isNull(v_Item.getXJavaID()) )
                    {
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("succeed" ,v_Item.getXJavaID()));
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
                        v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(v_Level1).append(IToXml.toRef("error" ,v_Item.getXJavaID() ,v_MaxLpad - 5));
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
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
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
        
        if ( !Help.isNull(this.callMehod) )
        {
            v_Builder.append(this.callMehod);
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
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
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
        
        if ( !Help.isNull(this.callMehod) )
        {
            v_Builder.append(this.callMehod);
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
    
}
