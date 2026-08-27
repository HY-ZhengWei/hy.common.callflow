package org.hy.common.callflow.milvus;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.milvus.MilvusData;
import org.hy.common.milvus.MilvusHelp;





/**
 * XMilvus功能中Delete语句的具体操作与实现。
 * 
 * 独立原因：从XMilvus主类中分离的主要原因是：减少XMilvus主类的代码量，方便维护。使XMilvus主类向外提供统一的操作，本类重点关注实现。
 * 静态原因：用static方法的原因：不想再构建太多的类实例，减少内存负担
 * 接口选择：未使用接口的原因：本类的每个方法的首个入参都有一个XMilvus类型，并且都是static方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-24
 * @version     v1.0
 */
public class XMilvusOPDelete
{
    
    /**
     * 占位符Content的Delete语句的执行。 -- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,(Object) null);
        long                v_IORowCount    = 0;
        String              v_Content       = null;
        
        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes();
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final Map<String ,?> i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus).setValuesMap(i_Values));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes(i_Values);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final Object i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus).setValuesObject(i_Values));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes(i_Values);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规Delete语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  删除条件的标量过滤条件，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final String i_Content)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,(Object) null);
        long                v_IORowCount    = 0L;
        
        try
        {
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,i_Content ,i_XMilvus.getMilvus());
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(i_Content ,exce ,i_XMilvus));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes();
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规Delete语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  删除条件的标量过滤条件，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     * @param i_Milvus   Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    private static MilvusData executeDelete_Inner(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        long v_BeginTime = i_XMilvus.request().getTime();
        
        try
        {
            if ( !i_Milvus.isValid() )
            {
                throw new RuntimeException("DataSourceGroup[" + i_Milvus.getXJavaID() + "] is not valid.");
            }
            if ( Help.isNull(i_XMilvus.getCollection()) )
            {
                throw new NullPointerException("Collection is null of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            if ( Help.isNull(i_Content) )
            {
                throw new NullPointerException("Content is null of XSQL[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            
            long v_Count =i_Milvus.deletes(i_XMilvus.getCollection() ,i_XMilvus.getPartition() ,i_Content);
            
            i_XMilvus.log(i_Content);
            
            Date v_EndTime = Date.getNowTime();
            long v_TimeLen = v_EndTime.getTime() - v_BeginTime;
            i_XMilvus.success(v_EndTime ,v_TimeLen ,1 ,v_Count);
            
            return new MilvusData(null ,v_Count ,1 ,v_TimeLen ,null);
        }
        catch (Exception exce)
        {
            XMilvus.erroring(i_Content ,exce ,i_XMilvus);
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。 -- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,(Object) null);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_Milvus);
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes();
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_Milvus);
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus).setValuesMap(i_Values));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes(i_Values);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final Object i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,v_Content ,i_Milvus);
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(v_Content ,exce ,i_XMilvus).setValuesObject(i_Values));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes(i_Values);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规Content的Delete语句的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-24
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  删除条件的标量过滤条件，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeDelete(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeDelete" ,(Object) null);
        long                v_IORowCount    = 0;
        
        try
        {
            MilvusData v_Ret = XMilvusOPDelete.executeDelete_Inner(i_XMilvus ,i_Content ,i_Milvus);
            v_IORowCount = v_Ret.getRowCount();
            return v_Ret;
        }
        // RuntimeException 是 NullPointerException 的父类，不用专门再写一个 NullPointerException 类型的catch
        catch (RuntimeException exce)
        {
            v_IsError   = true;
            v_ErrorInfo = Help.NVL(exce.getMessage() ,"E");
            if ( i_XMilvus.getError() != null )
            {
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(i_Content ,exce ,i_XMilvus));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executes();
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 本类不允许构建
     */
    private XMilvusOPDelete()
    {
        
    }
    
}
