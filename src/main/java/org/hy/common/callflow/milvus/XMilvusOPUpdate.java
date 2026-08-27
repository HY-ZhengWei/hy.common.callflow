package org.hy.common.callflow.milvus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.milvus.MilvusData;
import org.hy.common.milvus.MilvusHelp;
import org.hy.common.xml.XJSON;





/**
 * XMilvus功能中Update语句的具体操作与实现。
 * 
 * 独立原因：从XMilvus主类中分离的主要原因是：减少XMilvus主类的代码量，方便维护。使XMilvus主类向外提供统一的操作，本类重点关注实现。
 * 静态原因：用static方法的原因：不想再构建太多的类实例，减少内存负担
 * 接口选择：未使用接口的原因：本类的每个方法的首个入参都有一个XMilvus类型，并且都是static方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-20
 * @version     v1.0
 */
public class XMilvusOPUpdate
{
    
    /**
     * 占位符Content的Update语句的执行。 -- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,(Object) null);
        long                v_IORowCount    = 0;
        String              v_Content       = null;
        
        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 占位符Content的Update语句的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final Map<String ,?> i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final Object i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
     * 常规Update语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final String i_Content)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,(Object) null);
        long                v_IORowCount    = 0L;
        
        try
        {
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,i_Content ,i_XMilvus.getMilvus());
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
     * 常规Update语句的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @param i_Milvus   Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    private static MilvusData executeUpdate_Inner(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
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
            
            List<Object> v_Identitys = null;
            String       v_Content   = StringHelp.replaceAll(i_Content ,StringHelp.$ReplaceControl ,StringHelp.$ReplaceNil);
            if ( !XJSON.isJson(v_Content) )
            {
                throw new RuntimeException("Content is not JsonText of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            else if ( v_Content.trim().startsWith("{") )
            {
                v_Identitys = i_Milvus.upsert(i_XMilvus.getCollection() ,i_XMilvus.getPartition() ,i_Content);
            }
            else 
            {
                v_Identitys = i_Milvus.upserts(i_XMilvus.getCollection() ,i_XMilvus.getPartition() ,i_Content);
            }
            
            i_XMilvus.log(i_Content);
            
            int  v_Count   = Help.isNull(v_Identitys) ? 0 : v_Identitys.size();
            Date v_EndTime = Date.getNowTime();
            long v_TimeLen = v_EndTime.getTime() - v_BeginTime;
            i_XMilvus.success(v_EndTime ,v_TimeLen ,1 ,v_Count);
            
            return new MilvusData(v_Identitys ,v_Count ,1 ,v_TimeLen ,null);
        }
        catch (Exception exce)
        {
            XMilvus.erroring(i_Content ,exce ,i_XMilvus);
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 占位符Content的Update语句的执行。 -- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,(Object) null);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 占位符Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 占位符Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Values 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final Object i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,i_Values);
        long                v_IORowCount    = 0;
        String              v_Content       = null;

        try
        {
            i_XMilvus.fireBeforeRule(i_Values);
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
     * 常规Content的Update语句的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  常规Content执行内容。Json格式的数据
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数及ID。
     */
    public static MilvusData executeUpdate(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdate" ,(Object) null);
        long                v_IORowCount    = 0;
        
        try
        {
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,i_Content ,i_Milvus);
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
     * 批量执行：占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     *   
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeUpdates(final XMilvus i_XMilvus ,final List<?> i_ObjList)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdates" ,(Object) null);
        long                v_IORowCount    = 0;
        
        try
        {
            i_XMilvus.fireBeforeRule(i_ObjList);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdates_Inner(i_XMilvus ,i_ObjList ,null);
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
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(i_XMilvus.getContent().getContent() ,exce ,i_XMilvus).setValuesList(i_ObjList));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executeUpdates(i_ObjList);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 批量执行：占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     *   
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    public static MilvusData executeUpdates(final XMilvus i_XMilvus ,final List<?> i_ObjList ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("executeUpdates" ,(Object) null);
        long                v_IORowCount    = 0;
        
        try
        {
            i_XMilvus.fireBeforeRule(i_ObjList);
            MilvusData v_Ret = XMilvusOPUpdate.executeUpdates_Inner(i_XMilvus ,i_ObjList ,i_Milvus);
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
                i_XMilvus.getError().errorLog(new XMilvusErrorInfo(i_XMilvus.getContent().getContent() ,exce ,i_XMilvus).setValuesList(i_ObjList));
            }
            throw exce;
        }
        finally
        {
            if ( i_XMilvus.isTriggers(v_IsError) )
            {
                if ( v_TriggerParams == null )
                {
                    i_XMilvus.getTrigger().executeUpdates(i_ObjList);
                }
                else
                {
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IORowCount ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 批量执行：占位符Content的Update语句的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     *   注：只支持单一Content语句的执行
     *   
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     * 
     * @param i_XMilvus  向量库操作对象
     * @param i_ObjList  占位符Content的填充对象的集合。
     *                   1. 集合元素可以是Object
     *                   2. 集合元素可以是Map<String ,?>
     *                   3. 更可以是上面两者的混合元素组成的集合
     * @param i_Milvus   外部Milvus的帮助类
     * @return           返回语句影响的记录数。
     */
    private static MilvusData executeUpdates_Inner(final XMilvus i_XMilvus ,final List<?> i_ObjList ,final MilvusHelp i_Milvus)
    {
        int           v_Ret       = 0;
        long          v_BeginTime = i_XMilvus.request().getTime();
        String        v_Content   = null;
        List<Integer> v_Identitys = null;
        
        try
        {
            if ( !i_Milvus.isValid() )
            {
                throw new RuntimeException("DataSourceGroup[" + i_Milvus.getXJavaID() + "] is not valid.");
            }
            
            if ( Help.isNull(i_ObjList) )
            {
                throw new NullPointerException("Batch execute update List<Object> is null of XSQL[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
            }
            
            v_Identitys = new ArrayList<Integer>();
            
            for (Object v_Obj : i_ObjList)
            {
                v_Content = i_XMilvus.getContent().getContent(v_Obj);
                MilvusData v_ChildRet = XMilvusOPUpdate.executeUpdate_Inner(i_XMilvus ,v_Content ,i_Milvus);
                
                v_Ret += v_ChildRet.getRowCount();
                v_Identitys.addAll(v_ChildRet.getIdentitys());
                v_ChildRet.getIdentitys().clear();
            }
            
            Date v_EndTime = Date.getNowTime();
            long v_TimeLen = v_EndTime.getTime() - v_BeginTime;
            i_XMilvus.success(v_EndTime ,v_TimeLen ,v_Identitys.size() ,v_Ret);
            return new MilvusData(v_Identitys ,v_Ret ,1 ,v_TimeLen ,null);
        }
        catch (Exception exce)
        {
            XMilvus.erroring(v_Content ,exce ,i_XMilvus);
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 本类不允许构建
     */
    private XMilvusOPUpdate()
    {
        
    }
    
}
