package org.hy.common.callflow.milvus;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.enums.XMilvusType;
import org.hy.common.milvus.MilvusHelp;
import org.hy.common.milvus.schema.Collection;
import org.hy.common.xml.XJSON;





/**
 * XMilvus功能中DDL语句的具体操作与实现。
 * 
 * 独立原因：从XMilvus主类中分离的主要原因是：减少XMilvus主类的代码量，方便维护。使XMilvus主类向外提供统一的操作，本类重点关注实现。
 * 静态原因：用static方法的原因：不想再构建太多的类实例，减少内存负担
 * 接口选择：未使用接口的原因：本类的每个方法的首个入参都有一个XMilvus类型，并且都是static方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-08-17
 * @version     v1.0
 */
public class XMilvusOPDDL
{
    
    /**
     * 占位符Content的执行。-- 无填充值的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-18
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @return           是否执行成功
     */
    public static boolean execute(final XMilvus i_XMilvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,(Object) null);
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的执行。
     * 
     *   1. 按集合 Map<String ,Object> 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-18
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充集合。
     * @return           是否执行成功
     */
    public static boolean execute(final XMilvus i_XMilvus ,final Map<String ,?> i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,i_Values);
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的执行。
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-18
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @return           是否执行成功
     */
    public static boolean execute(final XMilvus i_XMilvus ,final Object i_Values)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,i_Values);
        String              v_Content       = null;
        
        try
        {
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规执行内容的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-18
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  执行内容。
     *                      创建集合对象时，为表的Json结构。与Milvus官网页面中的 “代码” 页面中的Json格式保持一致。
     *                      删除集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      加载集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      释放集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      存在集合对象时，为表的名称，仅支持单表判定。
     * @return           是否执行成功。
     */
    public static boolean execute(final XMilvus i_XMilvus ,final String i_Content)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,(Object) null);
        String              v_Content       = null;
        
        try
        {
            v_Content = i_Content;
            if ( Help.isNull(v_Content) )
            {
                v_Content = i_XMilvus.getContent().getContent();
            }
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_XMilvus.getMilvus());
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规执行内容的执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-18
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  执行内容。
     *                      创建集合对象时，为表的Json结构。与Milvus官网页面中的 “代码” 页面中的Json格式保持一致。
     *                      删除集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      加载集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      释放集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      存在集合对象时，为表的名称，仅支持单表判定。
     * @param i_Milvus   Milvus的帮助类
     * @return           是否执行成功。
     */
    private static boolean execute_Inner(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        long    v_BeginTime = i_XMilvus.request().getTime();
        String  v_Content   = i_Content;
        boolean v_Ret       = false;
        
        try
        {
            if ( !i_Milvus.isValid() )
            {
                throw new RuntimeException("MilvusHelp[" + i_Milvus.getXJavaID() + "] is not valid.");
            }
            
            // 创建集合
            if ( XMilvusType.DDLCreate.equals(i_XMilvus.getType()) )
            {
                if ( Help.isNull(i_Content) )
                {
                    throw new NullPointerException("Content is null of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
                }
                
                XJSON      v_XJson      = new XJSON();
                Collection v_Collection = (Collection) v_XJson.toJava(i_Content ,Collection.class);
                
                if ( !Help.isNull(i_XMilvus.getCollection()) )
                {
                    if ( !i_XMilvus.getCollection().equals(v_Collection.getCollection_name()) )
                    {
                        throw new RuntimeException("Collection[" + i_XMilvus.getCollection() + "] is not equal to Content.Collection_name[" + v_Collection.getCollection_name() + "] of XMilvus[" + Help.NVL(i_XMilvus.getXJavaID() ,i_XMilvus.getObjectID()) + "].");
                    }
                }
                
                // i_XMilvus.getCreateObjectName() 表示创建表的所属数据库名称
                v_Ret = i_Milvus.createCollection(i_XMilvus.getCreateObjectName() ,v_Collection);
            }
            // 删除集合
            else if ( XMilvusType.DDLDrop.equals(i_XMilvus.getType()) )
            {
                boolean v_IsAllowContent = true;
                if ( !Help.isNull(i_XMilvus.getCollection()) )
                {
                    v_Ret            = i_Milvus.dropCollection(i_XMilvus.getCollection());
                    v_IsAllowContent = v_Ret;
                }
                
                if ( v_IsAllowContent && !Help.isNull(i_Content) )
                {
                    String [] v_Tables = i_Content.split(",");
                    for (String v_TableName : v_Tables)
                    {
                        v_TableName = StringHelp.replaceAll(v_TableName ,StringHelp.$ReplaceControl ,StringHelp.$ReplaceNil).trim();
                        v_Ret = i_Milvus.dropCollection(v_TableName);
                        if ( !v_Ret )
                        {
                            break;
                        }
                    }
                }
            }
            // 加载集合
            else if ( XMilvusType.Load.equals(i_XMilvus.getType()) )
            {
                boolean v_IsAllowContent = true;
                if ( !Help.isNull(i_XMilvus.getCollection()) )
                {
                    v_Ret            = i_Milvus.loadCollection(i_XMilvus.getCollection());
                    v_IsAllowContent = v_Ret;
                }
                
                if ( v_IsAllowContent && !Help.isNull(i_Content) )
                {
                    String [] v_Tables = i_Content.split(",");
                    for (String v_TableName : v_Tables)
                    {
                        v_TableName = StringHelp.replaceAll(v_TableName ,StringHelp.$ReplaceControl ,StringHelp.$ReplaceNil).trim();
                        v_Ret = i_Milvus.loadCollection(v_TableName);
                        if ( !v_Ret )
                        {
                            break;
                        }
                    }
                }
            }
            // 释放集合
            else if ( XMilvusType.Release.equals(i_XMilvus.getType()) )
            {
                boolean v_IsAllowContent = true;
                if ( !Help.isNull(i_XMilvus.getCollection()) )
                {
                    v_Ret            = i_Milvus.releaseCollection(i_XMilvus.getCollection());
                    v_IsAllowContent = v_Ret;
                }
                
                if ( v_IsAllowContent && !Help.isNull(i_Content) )
                {
                    String [] v_Tables = i_Content.split(",");
                    for (String v_TableName : v_Tables)
                    {
                        v_TableName = StringHelp.replaceAll(v_TableName ,StringHelp.$ReplaceControl ,StringHelp.$ReplaceNil).trim();
                        v_Ret = i_Milvus.releaseCollection(v_TableName);
                        if ( !v_Ret )
                        {
                            break;
                        }
                    }
                }
            }
            // 存在集合
            else if ( XMilvusType.Exists.equals(i_XMilvus.getType()) )
            {
                if ( !Help.isNull(i_XMilvus.getCollection()) )
                {
                    return i_Milvus.exists(i_XMilvus.getCollection());
                }
                if ( !Help.isNull(i_Content) )
                {
                    return i_Milvus.exists(i_Content);
                }
            }
            else 
            {
                // TODO
            }
            
            i_XMilvus.log(v_Content);
            Date v_EndTime = Date.getNowTime();
            i_XMilvus.success(v_EndTime ,v_EndTime.getTime() - v_BeginTime ,1 ,1L);
            
            return v_Ret;
        }
        catch (Exception exce)
        {
            XMilvus.erroring(v_Content ,exce ,i_XMilvus);
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 占位符Content的执行。-- 无填充值的（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public static boolean execute(final XMilvus i_XMilvus ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,(Object) null);
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent();
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的执行。（使用外部向量库操作连接）
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
     * @return           是否执行成功。
     */
    public static boolean execute(final XMilvus i_XMilvus ,final Map<String ,?> i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,i_Values);
        String v_Content = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 占位符Content的执行。（使用外部向量库操作连接）
     * 
     *   1. 按对象 i_Obj 填充占位符Content，生成可执行的Content语句；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Values   占位符Content的填充对象。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public static boolean execute(final XMilvus i_XMilvus ,final Object i_Values ,final MilvusHelp i_Milvus)
    {
        i_XMilvus.checkContent();
        
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,i_Values);
        String              v_Content       = null;

        try
        {
            v_Content = i_XMilvus.getContent().getContent(i_Values);
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 常规Content的执行。（使用外部向量库操作连接）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-08-20
     * @version     v1.0
     *
     * @param i_XMilvus  向量库操作对象
     * @param i_Content  执行内容。
     *                      创建集合对象时，为表的Json结构。与Milvus官网页面中的 “代码” 页面中的Json格式保持一致。
     *                      删除集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      加载集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      释放集合对象时，为表的名称，多个表用英文逗号分隔。
     *                      存在集合对象时，为表的名称，仅支持单表判定。
     * @param i_Milvus   外部Milvus的帮助类
     * @return           是否执行成功。
     */
    public static boolean execute(final XMilvus i_XMilvus ,final String i_Content ,final MilvusHelp i_Milvus)
    {
        boolean             v_IsError       = false;
        String              v_ErrorInfo     = null;
        Map<String ,Object> v_TriggerParams = i_XMilvus.executeBeforeForTrigger("execute" ,(Object) null);
        String              v_Content       = null;

        try
        {
            v_Content = i_Content;
            if ( Help.isNull(v_Content) )
            {
                v_Content = i_XMilvus.getContent().getContent();
            }
            return XMilvusOPDDL.execute_Inner(i_XMilvus ,v_Content ,i_Milvus);
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
                    i_XMilvus.getTrigger().executes(i_XMilvus.executeAfterForTrigger(v_TriggerParams ,v_IsError?0L:1L ,v_ErrorInfo));
                }
            }
        }
    }
    
    
    
    /**
     * 本类不允许构建
     */
    private XMilvusOPDDL()
    {
        
    }
    
}
