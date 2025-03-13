package org.hy.common.callflow.returns;

import java.util.List;
import java.util.Map;

import org.hy.common.xml.XJSON;





/**
 * 通用的返回数据
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-13
 * @version     v1.0
 */
public class ReturnData
{
    
    /** 返回真假数值 */
    private Boolean             retBoolean;
    
    /** 返回整数数值 */
    private Integer             retInt;
    
    /** 返回长整数数值 */
    private Long                retLong;
    
    /** 返回双精度数值 */
    private Double              retDouble;
    
    /** 返回字符串 */
    private String              retText;
    
    /** 返回Map集合数据 */
    private Map<String ,Object> retMap;
    
    /** 返回List集合数据 */
    private List<Object>        retList;

    
    
    /**
     * 获取：返回真假数值
     */
    public Boolean getRetBoolean()
    {
        return retBoolean;
    }

    
    /**
     * 设置：返回真假数值
     * 
     * @param i_RetBoolean 返回真假数值
     */
    public void setRetBoolean(Boolean i_RetBoolean)
    {
        this.retBoolean = i_RetBoolean;
    }

    
    /**
     * 获取：返回整数数值
     */
    public Integer getRetInt()
    {
        return retInt;
    }

    
    /**
     * 设置：返回整数数值
     * 
     * @param i_RetInt 返回整数数值
     */
    public void setRetInt(Integer i_RetInt)
    {
        this.retInt = i_RetInt;
    }

    
    /**
     * 获取：返回长整数数值
     */
    public Long getRetLong()
    {
        return retLong;
    }

    
    /**
     * 设置：返回长整数数值
     * 
     * @param i_RetLong 返回长整数数值
     */
    public void setRetLong(Long i_RetLong)
    {
        this.retLong = i_RetLong;
    }

    
    /**
     * 获取：返回双精度数值
     */
    public Double getRetDouble()
    {
        return retDouble;
    }

    
    /**
     * 设置：返回双精度数值
     * 
     * @param i_RetDouble 返回双精度数值
     */
    public void setRetDouble(Double i_RetDouble)
    {
        this.retDouble = i_RetDouble;
    }

    
    /**
     * 获取：返回字符串
     */
    public String getRetText()
    {
        return retText;
    }

    
    /**
     * 设置：返回字符串
     * 
     * @param i_RetText 返回字符串
     */
    public void setRetText(String i_RetText)
    {
        this.retText = i_RetText;
    }

    
    /**
     * 获取：返回Map集合数据
     */
    public Map<String ,Object> getRetMap()
    {
        return retMap;
    }

    
    /**
     * 设置：返回Map集合数据
     * 
     * @param i_RetMap 返回Map集合数据
     */
    public void setRetMap(Map<String ,Object> i_RetMap)
    {
        this.retMap = i_RetMap;
    }

    
    /**
     * 获取：返回List集合数据
     */
    public List<Object> getRetList()
    {
        return retList;
    }

    
    /**
     * 设置：返回List集合数据
     * 
     * @param i_RetList 返回List集合数据
     */
    public void setRetList(List<Object> i_RetList)
    {
        this.retList = i_RetList;
    }


    /**
     * 转Json字符
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-13
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        XJSON v_XJSON = new XJSON();
        v_XJSON.setReturnNVL(false);
        
        try
        {
            return v_XJSON.toJson(this).toJSONString();
        }
        catch (Exception exce)
        {
            return "[ERROR]";
        }
    }
    
}
