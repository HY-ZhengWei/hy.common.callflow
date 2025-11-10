package org.hy.common.callflow.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;





/**
 * 模拟元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-06
 * @version     v1.0
 */
public class MockConfig
{
    
    private static final Logger $Logger = new Logger(MockConfig.class);
    
    
    
    /** 
     * 是否有效，默认为无效。
     * 
     * 有意设计它为一个常量值。
     *   其一，加快判定性能，
     *   其二，防止正式环境（必须设置为false）下的恶意攻击 
     */
    private boolean        valid;
    
    /** 
     * 模拟数据类型
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     */
    private String         dataClass;
    
    /** 模拟成功 */
    private List<MockItem> succeeds;
    
    /** 模拟失败 */
    private List<MockItem> faileds;
    
    /** 模拟异常 */
    private List<MockItem> exceptions;
    
    
    
    public MockConfig()
    {
        this.valid = false;
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     *   注1：会按模拟数据类型的转数据结构
     *   注2：优先级为：编排系统的i_DataClass > 用户配置的this.dataClass
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_Context      上下文类型的变量信息
     * @param i_JsonRootKey  Json中子项的名称
     * @param i_DataClass    模拟数据类型
     * @return               没有符合要求的模拟数据时，返回NULL
     * @throws Exception
     */
    public Object mock(Map<String ,Object> i_Context ,String i_JsonRootKey ,String i_DataClass) throws Exception
    {
        if ( !this.valid )
        {
            return null;
        }
        
        Object v_Data = this.mock(i_Context);
        if ( v_Data == null )
        {
            return null;
        }
        else if ( !Help.isNull(i_DataClass) || !Help.isNull(this.dataClass) )
        {
            Class<?> v_DataClass = Help.forName(Help.NVL(i_DataClass ,this.dataClass));
            if ( v_Data.getClass().equals(v_DataClass) )
            {
                return v_Data;
            }
            else if ( v_Data instanceof String )
            {
                if ( Help.isBasicDataType(v_DataClass) )
                {
                    return Help.toObject(v_DataClass ,(String) v_Data);
                }
                else
                {
                    XJSON v_XJson = new XJSON();
                    if ( Help.isNull(i_JsonRootKey) )
                    {
                        return v_XJson.toJava((String) v_Data ,v_DataClass);
                    }
                    else
                    {
                        return v_XJson.toJava((String) v_Data ,i_JsonRootKey ,v_DataClass);
                    }
                }
            }
            else
            {
                return v_Data;
            }
        }
        else
        {
            return v_Data;
        }
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     *   将Mock中配置的字符串分隔为List<String>结构。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-10
     * @version     v1.0
     *
     * @param i_Context      上下文类型的变量信息
     * @param i_Split        行分隔符
     * @return               没有符合要求的模拟数据时，返回NULL
     * @throws Exception
     */
    public List<String> mockRows(Map<String ,Object> i_Context ,String i_Split) throws Exception
    {
        if ( !this.valid )
        {
            return null;
        }
        
        Object v_Data = this.mock(i_Context);
        if ( v_Data == null )
        {
            return null;
        }
        else
        {
            String       v_Text  = StringHelp.replaceAll(v_Data.toString() ,"\r\n" ,"\n");
            List<String> v_Datas = new ArrayList<String>();
            int          v_Len   = i_Split.length();
            int          v_Old   = 0 - v_Len;
            int          v_New   = v_Text.indexOf(i_Split);
            
            while ( v_New >= 0 )
            {
                v_Datas.add(v_Text.substring(v_Old + v_Len ,v_New).trim());
                v_Old = v_New;
                v_New = v_Text.indexOf(i_Split ,v_Old + v_Len);
            }
            v_Datas.add(v_Text.substring(v_Old + v_Len).trim());
            
            return v_Datas;
        }
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     *   注1：当有多个模拟数据均符合要求时，仅优先返回第一个。
     *   注2：优先级为：模拟异常 > 模拟失败 > 模拟成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           没有符合要求的模拟数据时，返回NULL
     * @throws Exception 
     */
    private Object mock(Map<String ,Object> i_Context) throws Exception
    {
        Object v_Data = null;
        
        if ( !Help.isNull(this.exceptions) )
        {
            v_Data = this.mock(i_Context ,this.exceptions);
            if ( v_Data != null )
            {
                throw new MockException("Mock exception：" + v_Data);
            }
        }
        
        if ( !Help.isNull(this.faileds) )
        {
            v_Data = this.mock(i_Context ,this.faileds);
            if ( v_Data != null )
            {
                return v_Data;
            }
        }
        
        if ( !Help.isNull(this.succeeds) )
        {
            v_Data = this.mock(i_Context ,this.succeeds);
            if ( v_Data != null )
            {
                return v_Data;
            }
        }
        
        return v_Data;
    }
    
    
    
    /**
     * 运行时中获取模拟数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_Context    上下文类型的变量信息
     * @param i_MockItems  模拟项的集合
     * @return             没有符合要求的模拟数据时，返回NULL
     * @throws Exception 
     */
    private Object mock(Map<String ,Object> i_Context ,List<MockItem> i_MockItems) throws Exception
    {
        Object v_Data = null;
        
        for (MockItem v_MockItem : i_MockItems)
        {
            v_Data = v_MockItem.mock(i_Context);
            if ( v_Data != null )
            {
                return v_Data;
            }
        }
        
        return v_Data;
    }

    
    
    /**
     * 获取：是否有效，默认为无效。有意设计它为一个常量值。其一，加快判定性能，其二，防止正式环境（必须设置为false）下的恶意攻击
     */
    public boolean isValid()
    {
        return valid;
    }


    
    /**
     * 设置：是否有效，默认为无效。有意设计它为一个常量值。其一，加快判定性能，其二，防止正式环境（必须设置为false）下的恶意攻击
     * 
     * @param i_Valid 是否有效，默认为无效。有意设计它为一个常量值。其一，加快判定性能，其二，防止正式环境（必须设置为false）下的恶意攻击
     */
    public void setValid(boolean i_Valid)
    {
        this.valid = i_Valid;
    }
    
    
    
    /**
     * 获取：模拟数据类型
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     */
    public String getDataClass()
    {
        return dataClass;
    }
    
    
    
    /**
     * 设置：模拟数据类型
     * 
     * 未直接使用Class<?>原因是： 允许类不存在，仅在要执行时存在即可。
     * 优点：提高可移植性。
     * 
     * @param i_DataClass  模拟数据类型
     */
    public void setDataClass(String i_DataClass)
    {
        if ( Help.isNull(i_DataClass) || Void.class.getName().equals(i_DataClass) )
        {
            this.dataClass = null;
        }
        else
        {
            this.dataClass = i_DataClass.trim();
        }
    }
    
    
    
    /**
     * 添加模拟成功时的模拟项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param io_MockItem  模拟项
     */
    public void setSucceed(MockItem io_MockItem)
    {
        if ( io_MockItem == null || Help.isNull(io_MockItem.getEnable()) )
        {
            $Logger.warn("MockItem is null or MockItem.enable is null");
            return;
        }
        
        synchronized ( this )
        {
            if ( Help.isNull(this.succeeds) )
            {
                this.succeeds = new ArrayList<MockItem>();
            }
        }
        
        if ( io_MockItem.getData() == null )
        {
            io_MockItem.setData("");
        }
        this.succeeds.add(io_MockItem);
    }
    
    
    
    /**
     * 添加模拟成功时的模拟项。"条件逻辑" 判定结果为真
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_MockItem  模拟项
     */
    public void setIf(MockItem i_MockItem)
    {
        this.setSucceed(i_MockItem);
    }
    
    
    
    /**
     * 添加模拟失败时的模拟项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param io_MockItem  模拟项
     */
    public void setFailed(MockItem io_MockItem)
    {
        if ( io_MockItem == null || Help.isNull(io_MockItem.getEnable()) )
        {
            $Logger.warn("MockItem is null or MockItem.enable is null");
            return;
        }
        
        synchronized ( this )
        {
            if ( Help.isNull(this.faileds) )
            {
                this.faileds = new ArrayList<MockItem>();
            }
        }
        
        if ( io_MockItem.getData() == null )
        {
            io_MockItem.setData("");
        }
        this.faileds.add(io_MockItem);
    }
    
    
    
    /**
     * 添加模拟失败时的模拟项。"条件逻辑" 判定结果为假
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param i_MockItem  模拟项
     */
    public void setElse(MockItem i_MockItem)
    {
        this.setFailed(i_MockItem);
    }
    
    
    
    /**
     * 添加模拟异常时的模拟项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param io_MockItem  模拟项
     */
    public void setException(MockItem io_MockItem)
    {
        if ( io_MockItem == null || Help.isNull(io_MockItem.getEnable()) )
        {
            $Logger.warn("MockItem is null or MockItem.enable is null");
            return;
        }
        
        synchronized ( this )
        {
            if ( Help.isNull(this.exceptions) )
            {
                this.exceptions = new ArrayList<MockItem>();
            }
        }
        
        if ( io_MockItem.getData() == null )
        {
            io_MockItem.setData("");
        }
        this.exceptions.add(io_MockItem);
    }
    
    
    
    /**
     * 添加模拟异常时的模拟项
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-06
     * @version     v1.0
     *
     * @param io_MockItem  模拟项
     */
    public void setError(MockItem i_MockItem)
    {
        this.setException(i_MockItem);
    }
    
    
    
    /**
     * 获取：模拟成功
     */
    public List<MockItem> getSucceeds()
    {
        return succeeds;
    }

    
    /**
     * 设置：模拟成功
     * 
     * @param i_Succeeds 模拟成功
     */
    public void setSucceeds(List<MockItem> i_Succeeds)
    {
        this.succeeds = i_Succeeds;
    }

    
    /**
     * 获取：模拟失败
     */
    public List<MockItem> getFaileds()
    {
        return faileds;
    }

    
    /**
     * 设置：模拟失败
     * 
     * @param i_Faileds 模拟失败
     */
    public void setFaileds(List<MockItem> i_Faileds)
    {
        this.faileds = i_Faileds;
    }

    
    /**
     * 获取：模拟异常
     */
    public List<MockItem> getExceptions()
    {
        return exceptions;
    }

    
    /**
     * 设置：模拟异常
     * 
     * @param i_Exceptions 模拟异常
     */
    public void setExceptions(List<MockItem> i_Exceptions)
    {
        this.exceptions = i_Exceptions;
    }
    
}
