package org.hy.common.callflow.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;





/**
 * 模拟元素
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-11-06
 * @version     v1.0
 *              v2.0  2026-06-03  添加：模拟执行用时的等待时长（单位：毫秒）。合作解决人：李浩 
 *              v3.0  2026-10-08  添加：是否模拟的判定
 *                                优化：模拟执行用时的等待，仅在模拟生效时才等待
 */
public class MockConfig
{
    
    private static final Logger $Logger = new Logger(MockConfig.class);
    
    /** 模拟执行用时的等待时长（单位：毫秒）*/
    public static final String $DefWaitTime = "1000";
    
    
    
    /** 
     * 是否有效，默认为无效。
     * 
     * 有意设计它为一个常量值。
     *   其一，加快判定性能，
     *   其二，防止正式环境（必须设置为false）下的恶意攻击 
     */
    private boolean        valid;
    
    /** 模拟执行用时的等待时长（单位：毫秒）。仅在模拟生效时才等待。可以是数值、上下文变量、XID标识 */
    private String         waitTime;
    
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
        this.valid    = false;
        this.waitTime = $DefWaitTime;
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
     * @return               Return.paramStr  表示模拟项的注释说明，仅在返回 true 时有效
     *                       Return.paramInt  表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     *                       Return.paramObj  表示模拟数据，没有符合要求的模拟数据时，返回NULL
     *                       Return.exception 表示模拟异常
     * @throws Exception
     */
    public Return<Object> mock(Map<String ,Object> i_Context ,String i_JsonRootKey ,String i_DataClass) throws Exception
    {
        if ( !this.valid )
        {
            return new Return<Object>(false).setParamInt(0).setParamStr("").setParamObj(null).setException(null);
        }
        
        Return<Object> v_Data = this.mock(i_Context);
        if ( v_Data.getParamObj() == null )
        {
            return v_Data;
        }
        else
        {
            Long v_WaitTime = null;
            if ( Help.isNumber(this.waitTime) )
            {
                v_WaitTime = Long.valueOf(this.waitTime);
            }
            else
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,0L ,i_Context);
            }
            
            if ( v_WaitTime > 0 )
            {
                Thread.sleep(v_WaitTime ,0);
            }
            
            if ( !Help.isNull(i_DataClass) || !Help.isNull(this.dataClass) )
            {
                Class<?> v_DataClass = Help.forName(Help.NVL(i_DataClass ,this.dataClass));
                if ( v_Data.getParamObj().getClass().equals(v_DataClass) )
                {
                    return v_Data;
                }
                else if ( v_Data.getParamObj() instanceof String )
                {
                    if ( Help.isBasicDataType(v_DataClass) )
                    {
                        return v_Data.setParamObj(Help.toObject(v_DataClass ,(String) v_Data.getParamObj()));
                    }
                    else
                    {
                        XJSON v_XJson = new XJSON();
                        if ( Help.isNull(i_JsonRootKey) )
                        {
                            return v_Data.setParamObj(v_XJson.toJava((String) v_Data.getParamObj() ,v_DataClass));
                        }
                        else
                        {
                            return v_Data.setParamObj(v_XJson.toJava((String) v_Data.getParamObj() ,i_JsonRootKey ,v_DataClass));
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
     * @return               Return.paramStr  表示模拟项的注释说明，仅在返回 true 时有效
     *                       Return.paramInt  表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     *                       Return.paramObj  表示模拟数据，没有符合要求的模拟数据时，返回NULL
     *                       Return.exception 表示模拟异常
     * @throws Exception
     */
    public Return<List<String>> mockRows(Map<String ,Object> i_Context ,String i_Split) throws Exception
    {
        if ( !this.valid )
        {
            return new Return<List<String>>(false).setParamInt(0).setParamStr("").setParamObj(null).setException(null);
        }
        
        Return<Object> v_Data = this.mock(i_Context);
        if ( v_Data.getParamObj() == null )
        {
            return new Return<List<String>>(v_Data.get()).setParamInt(v_Data.getParamInt()).setParamStr(v_Data.getParamStr()).setParamObj(null).setException(v_Data.getException());
        }
        else
        {
            Long v_WaitTime = null;
            if ( Help.isNumber(this.waitTime) )
            {
                v_WaitTime = Long.valueOf(this.waitTime);
            }
            else
            {
                v_WaitTime = (Long) ValueHelp.getValue(this.waitTime ,Long.class ,0L ,i_Context);
            }
            
            if ( v_WaitTime > 0 )
            {
                Thread.sleep(v_WaitTime ,0);
            }
            
            String       v_Text  = StringHelp.replaceAll(v_Data.getParamObj().toString() ,"\r\n" ,"\n");
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
            
            return new Return<List<String>>(v_Data.get()).setParamInt(v_Data.getParamInt()).setParamStr(v_Data.getParamStr()).setParamObj(v_Datas).setException(v_Data.getException());
        }
    }
    
    
    
    /**
     * 是否模拟
     * 
     *   注1：当有多个模拟数据均符合要求时，仅优先返回第一个。
     *   注2：优先级为：模拟异常 > 模拟失败 > 模拟成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-10-08
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           Return.paramStr 表示模拟项的注释说明，仅在返回 true 时有效
     *                   Return.paramInt 表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     *                   Return.paramObj 表示模拟类型（EXCEPTION、FAILED、SUCCEED）
     * @throws Exception 
     */
    public Return<String> isMock(Map<String ,Object> i_Context) throws Exception
    {
        Return<String> v_IsMock = null;
        
        if ( !this.valid )
        {
            v_IsMock = new Return<String>(false).setParamInt(0).setParamStr("").setParamObj("");
            return v_IsMock;
        }
        
        if ( !Help.isNull(this.exceptions) )
        {
            v_IsMock = this.isMock(i_Context ,this.exceptions);
            v_IsMock.paramObj = "EXCEPTION";
            if ( v_IsMock.booleanValue() )
            {
                return v_IsMock;
            }
        }
        
        if ( !Help.isNull(this.faileds) )
        {
            v_IsMock = this.isMock(i_Context ,this.faileds);
            v_IsMock.paramObj = "FAILED";
            if ( v_IsMock.booleanValue() )
            {
                return v_IsMock;
            }
        }
        
        if ( !Help.isNull(this.succeeds) )
        {
            v_IsMock = this.isMock(i_Context ,this.succeeds);
            v_IsMock.paramObj = "SUCCEED";
            if ( v_IsMock.booleanValue() )
            {
                return v_IsMock;
            }
        }
        
        return v_IsMock.setParamInt(0).setParamStr("").setParamObj("");
    }
    
    
    
    /**
     * 是否模拟
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-10-08
     * @version     v1.0
     *
     * @param i_Context    上下文类型的变量信息
     * @param i_MockItems  模拟项的集合
     * @return             Return.paramStr 表示模拟项的注释说明，仅在返回 true 时有效
     *                     Return.paramInt 表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     * @throws Exception 
     */
    private Return<String> isMock(Map<String ,Object> i_Context ,List<MockItem> i_MockItems) throws Exception
    {
        Return<String> v_Ret = new Return<String>(false).setParamInt(0).setParamStr("");
        
        for (MockItem v_MockItem : i_MockItems)
        {
            v_Ret.paramInt++;
            boolean v_IsMock = v_MockItem.isMock(i_Context);
            if ( v_IsMock )
            {
                return v_Ret.set(true).setParamStr(Help.NVL(v_MockItem.getComment()));
            }
        }
        
        return v_Ret.setParamInt(0).setParamStr("");
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
     * @return           Return.paramStr  表示模拟项的注释说明，仅在返回 true 时有效
     *                   Return.paramInt  表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     *                   Return.paramObj  表示模拟数据，没有符合要求的模拟数据时，返回NULL
     *                   Return.exception 表示模拟异常
     * @throws Exception 
     */
    private Return<Object> mock(Map<String ,Object> i_Context) throws Exception
    {
        Return<Object> v_Data = null;
        
        if ( !Help.isNull(this.exceptions) )
        {
            v_Data = this.mock(i_Context ,this.exceptions);
            if ( v_Data.getParamObj() != null )
            {
                return v_Data.setException(new MockException("Mock exception：" + v_Data));
            }
        }
        
        if ( !Help.isNull(this.faileds) )
        {
            v_Data = this.mock(i_Context ,this.faileds);
            if ( v_Data.getParamObj() != null )
            {
                return v_Data;
            }
        }
        
        if ( !Help.isNull(this.succeeds) )
        {
            v_Data = this.mock(i_Context ,this.succeeds);
            if ( v_Data.getParamObj() != null )
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
     * @return             Return.paramStr 表示模拟项的注释说明，仅在返回 true 时有效
     *                     Return.paramInt 表示第几个模拟项，下标从1开始，仅在返回 true 时有效
     *                     Return.paramObj 表示模拟数据，没有符合要求的模拟数据时，返回NULL
     * @throws Exception 
     */
    private Return<Object> mock(Map<String ,Object> i_Context ,List<MockItem> i_MockItems) throws Exception
    {
        Return<Object> v_Ret = new Return<Object>(false).setParamInt(0).setParamStr("").setParamObj(null);
        
        for (MockItem v_MockItem : i_MockItems)
        {
            v_Ret.paramInt++;
            Object v_Data = v_MockItem.mock(i_Context);
            if ( v_Data != null )
            {
                return v_Ret.set(true).setParamStr(Help.NVL(v_MockItem.getComment())).setParamObj(v_Data);
            }
        }
        
        return v_Ret.setParamInt(0).setParamStr("").setParamObj(null);
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
     * 获取：等待时长（单位：毫秒）。仅在模拟生效时才等待。可以是数值、上下文变量、XID标识
     */
    public String getWaitTime()
    {
        return waitTime;
    }


    
    /**
     * 设置：等待时长（单位：毫秒）。仅在模拟生效时才等待。可以是数值、上下文变量、XID标识
     * 
     * @param i_WaitTime 等待时长（单位：毫秒）。仅在模拟生效时才等待。可以是数值、上下文变量、XID标识
     */
    public void setWaitTime(String i_WaitTime)
    {
        if ( Help.isNull(i_WaitTime) )
        {
            NullPointerException v_Exce = new NullPointerException("WaitTime is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_WaitTime) )
        {
            Long v_WaitTime = Long.valueOf(i_WaitTime);
            if ( v_WaitTime < 0L )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("WaitTime Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.waitTime = i_WaitTime.trim();
        }
        else
        {
            this.waitTime = ValueHelp.standardRefID(i_WaitTime);
        }
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
