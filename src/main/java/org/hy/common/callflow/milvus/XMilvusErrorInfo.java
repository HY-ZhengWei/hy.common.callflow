package org.hy.common.callflow.milvus;

import java.util.List;
import java.util.Map;





/**
 * XSQL异常信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-20
 * @version     v1.0
 */
public class XMilvusErrorInfo
{
    
    /** 异常时执行的Content语句 */
    private String         sql;
    
    /** 异常信息 */
    private Exception      exce;
    
    /** 异常时执行的XMilvus对象 */
    private XMilvus        xmilvus;
    
    /**
     * 异常时执行的XSQL入参数据（Map类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    private Map<String ,?> valuesMap;
    
    /**
     * 异常时执行的XSQL入参数据（Object类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    private Object         valuesObject;
    
    /**
     * 异常时执行的XSQL入参数据（List类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    private List<?>        valuesList;
    
    
    
    public XMilvusErrorInfo(String i_Content ,Exception i_Exce ,XMilvus i_XMilvus)
    {
        this.sql     = i_Content;
        this.exce    = i_Exce;
        this.xmilvus = i_XMilvus;
    }

    
    /**
     * 获取：异常时执行的SQL语句
     */
    public String getSql()
    {
        return sql;
    }

    
    /**
     * 获取：异常信息
     */
    public Exception getExce()
    {
        return exce;
    }

    
    /**
     * 获取：异常时执行的XMilvus对象
     */
    public XMilvus getXMilvus()
    {
        return xmilvus;
    }

    
    /**
     * 获取：* 异常时执行的XSQL入参数据（Map类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    public Map<String ,?> getValuesMap()
    {
        return valuesMap;
    }

    
    /**
     * 获取：* 异常时执行的XSQL入参数据（Object类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    public Object getValuesObject()
    {
        return valuesObject;
    }

    
    /**
     * 获取：* 异常时执行的XSQL入参数据（List类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     */
    public List<?> getValuesList()
    {
        return valuesList;
    }

    
    /**
     * 设置：* 异常时执行的XSQL入参数据（Map类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     * 
     * @param valuesMap
     */
    public XMilvusErrorInfo setValuesMap(Map<String ,?> valuesMap)
    {
        this.valuesMap = valuesMap;
        return this;
    }

    
    /**
     * 设置：* 异常时执行的XSQL入参数据（Object类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     * 
     * @param valuesObject
     */
    public XMilvusErrorInfo setValuesObject(Object valuesObject)
    {
        this.valuesObject = valuesObject;
        return this;
    }

    
    /**
     * 设置：* 异常时执行的XSQL入参数据（List类型的）。
     * 
     * valuesMap、valuesObject、valuesList三者同时只能有一个有值，或者均为NULL。
     * 
     * @param valuesList
     */
    public XMilvusErrorInfo setValuesList(List<?> valuesList)
    {
        this.valuesList = valuesList;
        return this;
    }
    
}
