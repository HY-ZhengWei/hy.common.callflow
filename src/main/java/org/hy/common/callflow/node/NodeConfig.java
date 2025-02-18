package org.hy.common.callflow.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Total;
import org.hy.common.XJavaID;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.execute.IExecute;
import org.hy.common.callflow.route.RouteConfig;
import org.hy.common.xml.XJava;





/**
 * 节点配置信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public class NodeConfig extends Total implements IExecute ,XJavaID
{
    
    /** 全局惟一标识ID */
    private String          xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String          comment;
    
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
     * @param io_Default  默认值类型的变量信息
     * @param io_Context  上下文类型的变量信息
     * @return
     */
    public ExecuteResult execute(int i_IndexNo ,Map<String ,Object> io_Default ,Map<String ,Object> io_Context)
    {
        ExecuteResult v_Result = new ExecuteResult(i_IndexNo ,this.xid);
        
        if ( Help.isNull(this.callXID) )
        {
            return v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null."));
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            return v_Result.setException(new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.callXID + "] is not find."));
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            for (int x=0; x<v_ParamValues.length; x++)
            {
                NodeParam v_NodeParam  = this.callParams.get(x);
                v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.getValueClass() ,io_Default ,io_Context);
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
            return v_Result.setException(exce);
        }
        
        try
        {
            long   v_BeginTime = this.request().getTime();
            Object v_ExceRet   = this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
            
            if ( !Help.isNull(this.returnID) )
            {
                io_Context.put(this.returnID ,v_ExceRet);
            }
            
            this.success(Date.getNowTime().getTime() - v_BeginTime);
            v_Result.setResult(v_ExceRet);
            return v_Result;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exce)
        {
            return v_Result.setException(exce);
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
     * @param io_Default  默认值类型的变量信息
     * @param io_Context  上下文类型的变量信息
     */
    public synchronized void init(Map<String ,Object> io_Default ,Map<String ,Object> io_Context)
    {
        if ( Help.isNull(this.callXID) )
        {
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID is null.");
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallXID[" + this.callXID + "] is not find.");
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamValues = new Object[this.callParams.size()];
            
            for (int x=0; x<v_ParamValues.length; x++)
            {
                NodeParam v_NodeParam  = this.callParams.get(x);
                v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.getValueClass() ,io_Default ,io_Context);
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
            throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "] is not find.");
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
                    if ( i_ParamValues[y] == null )
                    {
                        // Nothing.
                    }
                    else if ( i_ParamValues[y].getClass() == v_MPClassTypes[y] )                       // 3级：完全配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 3;
                    }
                    else if ( v_MPClassTypes[y] == Object.class )                                      // 1级：模糊配对
                    {
                        v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y);
                    }
                    else if ( MethodReflect.isExtendImplement(i_ParamValues[y] ,v_MPClassTypes[y]) )   // 2级：继承配对 或 接口实现配对
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
                    throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "](" + i_ParamValues.length + ") is find " + v_CallMethods.size() + " methods.");
                }
            }
            else
            {
                throw new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s CallMethod[" + this.callMehod + "](" + i_ParamValues.length + ") is not find.");
            }
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
        this.xid = i_Xid;
    }


    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_XJavaID)
    {
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
    
}
