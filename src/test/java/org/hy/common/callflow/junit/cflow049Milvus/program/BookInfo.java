package org.hy.common.callflow.junit.cflow049Milvus.program;

import java.util.List;





/**
 * 数据层：图书
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-08-25
 * @version     v1.0
 */
public class BookInfo
{
    
    /** 主键 */
    private Integer     id;
    
    /** 图书名称 */
    private String      bookName;
    
    /** 图书向量A */
    private Float       vectorA;
    
    /** 图书向量B */
    private Float       vectorB;
    
    /** 图书向量 */
    private List<Float> bookVector;
    
    /** 图书标题向量 */
    private List<Float> titleVector;

    
    
    /**
     * 获取：主键
     */
    public Integer getId()
    {
        return id;
    }

    
    /**
     * 设置：主键
     * 
     * @param i_Id 主键
     */
    public void setId(Integer i_Id)
    {
        this.id = i_Id;
    }

    
    /**
     * 获取：图书名称
     */
    public String getBookName()
    {
        return bookName;
    }

    
    /**
     * 设置：图书名称
     * 
     * @param i_BookName 图书名称
     */
    public void setBookName(String i_BookName)
    {
        this.bookName = i_BookName;
    }

    
    /**
     * 获取：图书向量A
     */
    public Float getVectorA()
    {
        return vectorA;
    }

    
    /**
     * 设置：图书向量A
     * 
     * @param i_VectorA 图书向量A
     */
    public void setVectorA(Float i_VectorA)
    {
        this.vectorA = i_VectorA;
    }

    
    /**
     * 获取：图书向量B
     */
    public Float getVectorB()
    {
        return vectorB;
    }

    
    /**
     * 设置：图书向量B
     * 
     * @param i_VectorB 图书向量B
     */
    public void setVectorB(Float i_VectorB)
    {
        this.vectorB = i_VectorB;
    }

    
    /**
     * 获取：图书向量
     */
    public List<Float> getBookVector()
    {
        return bookVector;
    }

    
    /**
     * 设置：图书向量
     * 
     * @param i_BookVector 图书向量
     */
    public void setBookVector(List<Float> i_BookVector)
    {
        this.bookVector = i_BookVector;
    }

    
    /**
     * 获取：图书标题向量
     */
    public List<Float> getTitleVector()
    {
        return titleVector;
    }

    
    /**
     * 设置：图书标题向量
     * 
     * @param i_TitleVector 图书标题向量
     */
    public void setTitleVector(List<Float> i_TitleVector)
    {
        this.titleVector = i_TitleVector;
    }
    
}
