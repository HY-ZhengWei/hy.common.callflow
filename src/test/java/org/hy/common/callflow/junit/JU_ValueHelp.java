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
    
    private String                        name;
    
    private Double                        age;
    
    private Date                          createTime = new Date("2025-06-05");
    
    private String                        comment    = null;
    
    private Map<String ,JU_ValueHelpUser> users;
    
    
    
    @Test
    public void test_replaceByContext()
    {
        String v_Text = """
                        {
                          "name": ":School.name",
                          "age": :School.age,
                          "time": ":School.createTime",
                          "comment": ":School.comment",
                          "factory": ":Factory.name",
                          "factoryFriend1": ":Factory.users.$get(A).name",
                          "factoryFriend2": ":Factory.users.A.name",
                          "factorySchool1": ":Factory.users.$get({:School.users.$get(A).ref}).name",
                          "factorySchool2": ":Factory.users.{:School.users.$get(A).ref}.name",
                          "typeX": ":Types.X"
                        }
                        """;
        
        Map<String ,String> v_Types = new HashMap<String ,String>();
        v_Types.put("X" ,"9");
        v_Types.put("Y" ,"8");
        v_Types.put("Z" ,"7");
        
        Map<String ,Object> v_Context = new HashMap<String ,Object>();
        v_Context.put("School"  ,new JU_ValueHelp().setName("XiBeiGuoMianErChang").setAge(3.14159));
        v_Context.put("Factory" ,new JU_ValueHelp().setName("LPS").setAge(2.71828));
        v_Context.put("Types"   ,v_Types);
        
        System.out.println(ValueHelp.replaceByContext(v_Text ,v_Context));
    }
    
    
    
    public JU_ValueHelp()
    {
        this.users = new HashMap<String ,JU_ValueHelpUser>();
        this.users.put("A" ,new JU_ValueHelpUser("张三" ,18 ,"B"));
        this.users.put("B" ,new JU_ValueHelpUser("李四" ,19 ,"C"));
        this.users.put("C" ,new JU_ValueHelpUser("王五" ,20 ,"A"));
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


    
    public Map<String ,JU_ValueHelpUser> getUsers()
    {
        return users;
    }


    
    public void setUsers(Map<String ,JU_ValueHelpUser> i_Users)
    {
        this.users = i_Users;
    }
    
}
