package org.hy.common.callflow.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
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
public class NodeConfig implements IExecute ,XJavaID
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
    
    /** 执行方法的参数 */
    private List<NodeParam> callParams;
    
    /** 为返回值定义的变量ID */
    private String          returnID;
    
    /** 路由 */
    private RouteConfig     route;
    
    /** 累计的执行次数 */
    private long            requestTotal;
    
    /** 累计的执行成功次数 */
    private long            successTotal;
    
    /** 执行的次数 */
    private long            requestCount;
    
    /** 执行的成功次数 */
    private long            successCount;
    
    /** 执行成功，并成功返回的累计用时时长 */
    private double          successTimeLen;
    
    /** 执行成功，并成功返回的最大用时时长 */
    private double          successTimeLenMax;
    
    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     */
    private Date             executeTime;
    
    
    
    public NodeConfig()
    {
        this(0L ,0L);
    }
    
    
    public NodeConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        this.route             = new RouteConfig();
        this.requestTotal      = i_RequestTotal;
        this.successTotal      = i_SuccessTotal;
        this.requestCount      = 0L;
        this.successCount      = 0L;
        this.successTimeLen    = 0D;
        this.successTimeLenMax = 0D;
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
            return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallXID is null."));
        }
        
        // 获取执行对象
        Object v_CallObject = XJava.getObject(this.callXID);
        if ( v_CallObject == null )
        {
            return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallXID[" + this.callXID + "] is not find."));
        }
        
        // 获取及实时解析方法的执行参数
        Object [] v_ParamValues = null;
        int v_ParamCount = 0;
        if ( !Help.isNull(this.callParams) )
        {
            v_ParamCount  = this.callParams.size();
            v_ParamValues = new Object[v_ParamCount];
            
            for (int x=0; x<v_ParamCount; x++)
            {
                NodeParam v_NodeParam  = this.callParams.get(x);
                v_ParamValues[x] = ValueHelp.getValue(v_NodeParam.getValue() ,v_NodeParam.getValueClass() ,io_Default ,io_Context);
            }
        }
        else
        {
            v_ParamValues = new Object[v_ParamCount];
        }
        
        // 用于匹配执行方法
        // 仅在首次或关键成员属性改变时才执行
        if ( this.requestCount == 0 )
        {
            if ( Help.isNull(this.callMehod) )
            {
                return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallMethod is null."));
            }
            
            List<Method> v_CallMethods = MethodReflect.getMethods(v_CallObject.getClass() ,this.callMehod ,v_ParamCount);
            if ( Help.isNull(v_CallMethods) )
            {
                return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallMethod[" + this.callMehod + "] is not find."));
            }
            
            if ( v_CallMethods.size() == 1 )
            {
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
                        if ( v_ParamValues[y] == null )
                        {
                            // Nothing.
                        }
                        else if ( v_ParamValues[y].getClass() == v_MPClassTypes[y] )                       // 3级：完全配对
                        {
                            v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y) * 3;
                        }
                        else if ( v_MPClassTypes[y] == Object.class )                                      // 1级：模糊配对
                        {
                            v_BestValue += Math.pow(1 ,v_MPClassTypes.length - y);
                        }
                        else if ( MethodReflect.isExtendImplement(v_ParamValues[y] ,v_MPClassTypes[y]) )   // 2级：继承配对 或 接口实现配对
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
                        this.callMethodObject = v_CallMethods.get(0);
                    }
                    else
                    {
                        return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallMethod[" + this.callMehod + "](" + v_ParamCount + ") is find " + v_CallMethods.size() + " methods."));
                    }
                }
                else
                {
                    return v_Result.setException(new NullPointerException("XID[" + this.xid + "]'s CallMethod[" + this.callMehod + "](" + v_ParamCount + ") is not find."));
                }
            }
        }
        
        try
        {
            long   v_BeginTime = this.request().getTime();
            Object v_ExceRet   = this.callMethodObject.invoke(v_CallObject ,v_ParamValues);
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
     * 记录执行次数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @return
     */
    private synchronized Date request()
    {
        ++this.requestTotal;
        ++this.requestCount;
        this.executeTime = new Date();
        return this.executeTime;
    }
    
    
    /**
     * 记录成功次数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @param i_TimeLen  执行时长
     */
    private synchronized void success(double i_TimeLen)
    {
        ++this.successTotal;
        ++this.successCount;
        this.successTimeLen += i_TimeLen;
        this.successTimeLenMax = Math.max(this.successTimeLenMax ,i_TimeLen);
        this.executeTime = new Date();
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
        this.callXID     = i_CallXID;
        this.requestCount   = 0L;
        this.successCount = 0L;
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
        this.callMehod   = i_CallMehod;
        this.requestCount   = 0L;
        this.successCount = 0L;
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
        this.callParams  = i_CallParams;
        this.requestCount   = 0L;
        this.successCount = 0L;
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
     * 获取：累计的执行次数
     */
    public long getRequestTotal()
    {
        return requestTotal;
    }

    
    /**
     * 设置：累计的执行次数
     * 
     * @param i_RequestTotal 累计的执行次数
     */
    public void setRequestTotal(long i_RequestTotal)
    {
        this.requestTotal = i_RequestTotal;
    }


    /**
     * 获取：累计的执行成功次数
     */
    public long getSuccessTotal()
    {
        return successTotal;
    }

    
    /**
     * 设置：累计的执行成功次数
     * 
     * @param i_SuccessTotal 累计的执行成功次数
     */
    public void setSuccessTotal(long i_SuccessTotal)
    {
        this.successTotal = i_SuccessTotal;
    }


    /**
     * 获取：执行的次数
     */
    public Long getRequestCount()
    {
        return requestCount;
    }

    
    /**
     * 获取：执行的成功次数
     */
    public Long getSuccessCount()
    {
        return successCount;
    }
    
    
    /**
     * 获取：执行成功，并成功返回的累计用时时长。
     * 用的是Double，而不是long，因为在批量执行时。为了精度，会出现小数
     */
    public double getSuccessTimeLen()
    {
        return successTimeLen;
    }
    
    
    /**
     * 获取：执行成功，并成功返回的最大用时时长。
     */
    public double getSuccessTimeLenMax()
    {
        return successTimeLenMax;
    }
    
    
    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @return
     */
    public Date getExecuteTime()
    {
        return this.executeTime;
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
