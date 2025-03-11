package org.hy.common.callflow.junit.cflow014.program;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 模拟被编排的程序 
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-10
 * @version     v1.0
 */
public class Program
{
    
    /** 字符测试 */
    private String       name;
    
    /** 整数测试 */
    private Integer      age;
    
    /** 时间测试 */
    private Date         birthday;
    
    /** 集合测试 */
    private List<String> listData;
    
    /** 集合测试 */
    private Set<String>  hashSetData;
    
    
    
    public Program method_Object(Program i_ObjectDatas)
    {
        System.out.println("\ncall method_Object");
        System.out.println("字符：" + i_ObjectDatas.getName());
        System.out.println("整数：" + i_ObjectDatas.getAge());
        System.out.println("时间：" + i_ObjectDatas.getBirthday());
        System.out.println("集合：" + i_ObjectDatas.getListData().size());
        Help.print(i_ObjectDatas.getListData());
        System.out.println("集合：" + i_ObjectDatas.getHashSetData().size());
        Help.print(i_ObjectDatas.getHashSetData());
        
        return i_ObjectDatas;
    }
    
    
    
    public Map<String ,Object> method_Map(Map<String ,Object> i_MapDatas)
    {
        System.out.println("\ncall method_Map");
        Help.print(i_MapDatas);
        return i_MapDatas;
    }
    
    
    
    public void method_List(List<Object> i_ListDatas)
    {
        System.out.println("\ncall method_List");
        Help.print(i_ListDatas);
    }
    
    
    
    public void method_Set(Set<Object> i_SetDatas)
    {
        System.out.println("\ncall method_Set");
        Help.print(i_SetDatas);
    }
    
    
    
    public void method_String(String i_String)
    {
        System.out.println("\ncall method_String：" + i_String);
    }


    
    /**
     * 获取：字符测试
     */
    public String getName()
    {
        return name;
    }


    /**
     * 设置：字符测试
     * 
     * @param i_Name 字符测试
     */
    public void setName(String i_Name)
    {
        this.name = i_Name;
    }


    /**
     * 获取：整数测试
     */
    public Integer getAge()
    {
        return age;
    }


    /**
     * 设置：整数测试
     * 
     * @param i_Age 整数测试
     */
    public void setAge(Integer i_Age)
    {
        this.age = i_Age;
    }


    /**
     * 获取：时间测试
     */
    public Date getBirthday()
    {
        return birthday;
    }


    /**
     * 设置：时间测试
     * 
     * @param i_Birthday 时间测试
     */
    public void setBirthday(Date i_Birthday)
    {
        this.birthday = i_Birthday;
    }

    
    /**
     * 获取：集合测试
     */
    public List<String> getListData()
    {
        return listData;
    }

    
    /**
     * 设置：集合测试
     * 
     * @param i_ListData 集合测试
     */
    public void setListData(List<String> i_ListData)
    {
        this.listData = i_ListData;
    }

    
    /**
     * 获取：集合测试
     */
    public Set<String> getHashSetData()
    {
        return hashSetData;
    }

    
    /**
     * 设置：集合测试
     * 
     * @param i_HashSetData 集合测试
     */
    public void setHashSetData(Set<String> i_HashSetData)
    {
        this.hashSetData = i_HashSetData;
    }
    
}
