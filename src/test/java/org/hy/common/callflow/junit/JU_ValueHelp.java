package org.hy.common.callflow.junit;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.callflow.common.ValueHelp;
import org.junit.Test;





/**
 * 测试单元：从上下文区、全局区、默认值区三个区中取值
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-06-05
 * @version     v1.0
 */
public class JU_ValueHelp
{
    
    private String  name;
    
    private Double  age;
    
    private Date    createTime = new Date("2025-06-05");
    
    private String  comment    = null;
    
    
    
    @Test
    public void test_replaceByContext()
    {
        String              v_Text    = """
                                        {
                                          "name": ":User.name",
                                          "age": :User.age,
                                          "time": ":User.createTime",
                                          "comment": ":User.comment",
                                        }
                                        """;
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.put("User" ,new JU_ValueHelp().setName("ZhengWei").setAge(3.14));
        
        System.out.println(ValueHelp.replaceByContext(v_Text ,v_Context));
    }
    
    
    
    public JU_ValueHelp()
    {
    }
    
    
    
    public String getName()
    {
        return name;
    }



    public JU_ValueHelp setName(String i_Name)
    {
        this.name = i_Name;
        return this;
    }


    
    public Double getAge()
    {
        return age;
    }


    
    public JU_ValueHelp setAge(Double i_Age)
    {
        this.age = i_Age;
        return this;
    }



    public Date getCreateTime()
    {
        return createTime;
    }


    
    public JU_ValueHelp setCreateTime(Date i_CreateTime)
    {
        this.createTime = i_CreateTime;
        return this;
    }


    
    public String getComment()
    {
        return comment;
    }

    

    public JU_ValueHelp setComment(String i_Comment)
    {
        this.comment = i_Comment;
        return this;
    }
    
}
