package org.hy.common.callflow.milvus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.StringHelp;
import org.hy.common.milvus.MilvusData;
import org.hy.common.milvus.MilvusHelp;
import org.hy.common.xml.XJSON;

import io.milvus.v2.service.vector.request.data.BaseVector;
import io.milvus.v2.service.vector.request.data.EmbeddedText;
import io.milvus.v2.service.vector.request.data.FloatVec;





/**
 * XMilvus功能中Select语句的具体操作与实现。
 * 
 * 独立原因：从XMilvus主类中分离的主要原因是：减少XMilvus主类的代码量，方便维护。使XMilvus主类向外提供统一的操作，本类重点关注实现。
 * 静态原因：用static方法的原因：不想再构建太多的类实例，减少内存负担
 * 接口选择：未使用接口的原因：本类的每个方法的首个入参都有一个XMilvus类型，并且都是static方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-21
 * @version     v1.0
 */
public class XMilvusOPQuery
{
    
    /**
     * 占位符Content的查询。-- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,(Object) null);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 占位符Content的查询。-- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,(Object) null);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret =XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 占位符Content的查询。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Map<String ,?> i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,i_Values);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;
        
        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,i_Values);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;
        
        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final List<String> i_FilterColNames)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充集合。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final int [] i_FilterColNoArr)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 占位符Content的查询。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Object i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,i_Values);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 占位符Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Object i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,i_Values);
        long                v_IORowCount    = 0L;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 占位符Content的查询。（按输出字段名称过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Object i_Values ,final List<String> i_FilterColNames)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 占位符Content的查询。（按输出字段位置过滤）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     *   2. 并提交数据库执行Content，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Values          占位符Content的填充对象。
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final Object i_Values ,final int [] i_FilterColNoArr)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 常规Content的查询。（使用外部向量库操作连接）
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                             多个向量条件之间只能用 && 关系符，每个向量比较只能用 == 双等号。
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @param i_Milvus   外部Milvus的帮助类
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,(Object) null);
        long                v_IORowCount    = 0L;

        try
        {
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,i_Content ,i_Milvus);
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
     * 常规Content的查询。
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                             多个向量条件之间只能用 && 关系符，每个向量比较只能用 == 双等号。
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @return           结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final String i_Content)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("queryMilvusData" ,(Object) null);
        long                v_IORowCount    = 0L;

        try
        {
            MilvusData v_Ret = XMilvusOPQuery.queryMilvusData_Inner(i_XMilvus ,i_Content ,i_XMilvus.getMilvus());
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
     * 常规Content的查询。
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                   当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                 如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                             多个向量条件之间只能用 && 关系符，每个向量比较只能用 == 双等号。
     *                   当主键查询时，参考标量过滤规则
     *                   当为空值时，按全表查询
     * @param i_Milvus   Milvus的帮助类
     * @return           结构化的查询结果
     */
    private static MilvusData queryMilvusData_Inner(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        long v_BeginTime = i_XMilvus.request().getTime();
        
        try
        {
            if ( i_XMilvus.getResult() == null )
            {
                throw new NullPointerException("Result is null of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            if ( !i_Milvus.isValid() )
            {
                throw new RuntimeException("MilvusHelp[" + i_Milvus.getXJavaID() + "] is not valid.");
            }
            if ( Help.isNull(i_XMilvus.getCollection()) )
            {
                throw new NullPointerException("Collection is null of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            
            MilvusData v_MilvusData = null;
            
            // 全表查询
            if ( Help.isNull(i_Content) )
            {
                v_MilvusData = i_Milvus.query(i_XMilvus.getCollection() ,i_XMilvus.getResult());
            }
            else
            {
                Map<String ,String> v_FieldsVector = i_Milvus.queryFieldsVector(i_XMilvus.getCollection());
                
                // 标量查询：表上无向量字段
                if ( Help.isNull(v_FieldsVector) )
                {
                    v_MilvusData = i_Milvus.query(i_XMilvus.getCollection() 
                                                 ,i_XMilvus.getPartition() 
                                                 ,i_Content 
                                                 ,i_XMilvus.getResult());
                }
                else
                {
                    Map<String ,List<BaseVector>> v_ANNNameVectors = new LinkedHashMap<String ,List<BaseVector>>();
                    String []                     v_Relations      = i_Content.split("&&");
                    String                        v_ANNName        = null; 
                    for (String v_Relation : v_Relations)
                    {
                        String [] v_NameValue = v_Relation.split("==");
                        if ( v_NameValue.length == 2 )
                        {
                            String v_Name       = StringHelp.replaceAll(v_NameValue[0].trim() ,StringHelp.$ReplaceControl ,StringHelp.$ReplaceNil).trim();
                            String v_Value      = v_NameValue[1].trim();
                            String v_VectorName = v_FieldsVector.get(v_Name.toUpperCase());
                            if ( !Help.isNull(v_VectorName) && !Help.isNull(v_Value) )
                            {
                                v_ANNName = v_VectorName;
                                v_ANNNameVectors.put(v_VectorName ,XMilvusOPQuery.toVectors(v_Value));
                            }
                        }
                    }
                    
                    // 标量查询：未找到向量字段
                    if ( Help.isNull(v_ANNNameVectors) )
                    {
                        v_MilvusData = i_Milvus.query(i_XMilvus.getCollection() 
                                                     ,i_XMilvus.getPartition() 
                                                     ,i_Content 
                                                     ,i_XMilvus.getResult());
                    }
                    // 单向量查询
                    else if ( v_ANNNameVectors.size() == 1 )
                    {
                        v_MilvusData = i_Milvus.queryVector(i_XMilvus.getCollection() 
                                                           ,v_ANNName 
                                                           ,v_ANNNameVectors.get(v_ANNName) 
                                                           ,i_XMilvus.getTopK() 
                                                           ,i_XMilvus.getResult());
                    }
                    // 多向量查询
                    else
                    {
                        v_MilvusData = i_Milvus.queryVectors(i_XMilvus.getCollection() 
                                                            ,v_ANNNameVectors 
                                                            ,i_XMilvus.getTopK() 
                                                            ,i_XMilvus.getResult());
                    }
                    
                    v_ANNNameVectors.clear();
                }
            }
            
            i_XMilvus.log(i_Content);
            
            Date v_EndTime = Date.getNowTime();
            i_XMilvus.success(v_EndTime ,v_EndTime.getTime() - v_BeginTime ,1 ,v_MilvusData.getRowCount());
            
            i_XMilvus.fireAfterRule(v_MilvusData);
            
            return v_MilvusData;
        }
        catch (Exception exce)
        {
            XMilvus.erroring(i_Content ,exce ,i_XMilvus);
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 多组向量的Json格式转为List<BaseVector>
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     *
     * @param i_VectorsJson  多组向量值的格式：[[1.0 ,2.0] ,[3.0 ,4.0]]
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<BaseVector> toVectors(String i_VectorsJson)
    {
        XJSON              v_XJson      = new XJSON();
        List<BaseVector>   v_Vectors    = new ArrayList<BaseVector>();
        List<?>            v_ValuesList = (List<?>) v_XJson.toJava(i_VectorsJson);
        
        if ( Help.isNull(v_ValuesList) )
        {
            return v_Vectors;
        }
        
        Object v_FirstElement = v_ValuesList.get(0);
        if ( String.class.equals(v_FirstElement.getClass()) )
        {
            for (Object v_ValueItem : v_ValuesList)
            {
                v_Vectors.add(new EmbeddedText(v_ValueItem.toString()));
            }
        }
        else if ( MethodReflect.isExtendImplement(v_FirstElement ,List.class) )
        {
            List<List<?>> v_ValuesListList = (List<List<?>>) v_ValuesList;
            
            for (List<?> v_ValueItem : v_ValuesListList)
            {
                List<Float> v_VectorFloat = new ArrayList<Float>();
                
                for (Object v_Item : v_ValueItem)
                {
                    if ( Double.class.equals(v_Item.getClass()) || double.class.equals(v_Item.getClass()) )
                    {
                        v_VectorFloat.add(((Double) v_Item).floatValue());
                    }
                    else if ( Float.class.equals(v_Item.getClass()) || float.class.equals(v_Item.getClass()) )
                    {
                        v_VectorFloat.add((Float) v_Item);
                    }
                    else if ( Integer.class.equals(v_Item.getClass()) || int.class.equals(v_Item.getClass()) )
                    {
                        v_VectorFloat.add(((Integer) v_Item).floatValue());
                    }
                    else if ( Long.class.equals(v_Item.getClass()) || long.class.equals(v_Item.getClass()) )
                    {
                        v_VectorFloat.add(((Long) v_Item).floatValue());
                    }
                }
                
                v_Vectors.add(new FloatVec(v_VectorFloat));
            }
        }
        else
        {
            List<Float> v_VectorFloat = new ArrayList<Float>();
            
            for (Object v_Item : v_ValuesList)
            {
                if ( Double.class.equals(v_Item.getClass()) || double.class.equals(v_Item.getClass()) )
                {
                    v_VectorFloat.add(((Double) v_Item).floatValue());
                }
                else if ( Float.class.equals(v_Item.getClass()) || float.class.equals(v_Item.getClass()) )
                {
                    v_VectorFloat.add((Float) v_Item);
                }
                else if ( Integer.class.equals(v_Item.getClass()) || int.class.equals(v_Item.getClass()) )
                {
                    v_VectorFloat.add(((Integer) v_Item).floatValue());
                }
                else if ( Long.class.equals(v_Item.getClass()) || long.class.equals(v_Item.getClass()) )
                {
                    v_VectorFloat.add(((Long) v_Item).floatValue());
                }
            }
            
            v_Vectors.add(new FloatVec(v_VectorFloat));
        }
        
        v_ValuesList.clear();
        v_ValuesList = null;
        
        return v_Vectors;
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段名称过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                                    多个向量条件之间只能用 && 关系符，每个向量比较只能用 == 双等号。
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNames  按输出字段名称过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final String i_Content ,final List<String> i_FilterColNames)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 常规Content的查询。(按输出字段位置过滤)
     * 
     *   1. 提交数据库执行 i_Content ，将数据库结果集转化为Java实例对象返回
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-21
     * @version     v1.0
     * 
     * @param i_XMilvus         向量库操作对象
     * @param i_Content         当标量查询时，参考标量过滤规则： https://milvus.io/docs/zh/boolean.md
     *                          当向量查询时，为向量名称的WHERE表达式。如，vectorName1 == :vertorValue1
     *                                                        如，vectorName1 == :vertorValue1 && vectorName2 == :vertorValue2
     *                          当主键查询时，参考标量过滤规则
     *                          当为空值时，按全表查询
     * @param i_FilterColNoArr  按输出字段位置过滤。
     * @return                  结构化的查询结果
     */
    public static MilvusData queryMilvusData(final XMilvus i_XMilvus ,final String i_Content ,final int [] i_FilterColNoArr)
    {
        // Nothing. 待未来有需要时再实现。从XSQL使用情况来看，此方法极少使用
        return null;
    }
    
    
    
    /**
     * 本类不允许构建
     */
    private XMilvusOPQuery()
    {
        
    }
    
}
