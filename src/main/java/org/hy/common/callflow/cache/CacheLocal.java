package org.hy.common.callflow.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;
import org.hy.common.xml.XJavaObject;





/**
 * 本地缓存
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-07-01
 * @version     v1.0
 * @param <Data>  缓存的数据对象
 *              v2.0  2024-09-20  添加：getRowsList 和 getRowsMap 全表数据获取的方法
 *              v3.0  2024-09-23  添加：开放字符串的get、set方法
 *              v4.0  2025-08-14  添加：相关配合编排引擎的多个方法
 */
public class CacheLocal<Data> implements ICache<Data>
{
    
    private static final String $Level = ">";
    
    /** 数据类型 */
    private Class<Data> dataClass;
    
    
    
    public CacheLocal(Class<Data> i_DataClass)
    {
        this.dataClass = i_DataClass;
    }
    
    
    
    /**
     * 保存数据（创建&更新）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @param i_ID        主键ID（要求：全域、全库、全表均是惟一的）
     * @param i_Data      数据
     * @return            返回影响的行数。负数表示异常
     */
    @Override
    public Long save(String i_Database ,String i_Table ,String i_ID ,Data i_Data)
    {
        XJava.putObject(i_Database + $Level + i_Table + $Level + i_ID ,i_Data);
        return 1L;
    }
    
    
    
    /**
     * 保存数据（创建&更新）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @param i_Database    数据库名称
     * @param i_Table       表名称
     * @param i_ID          主键ID（要求：全域、全库、全表均是惟一的）
     * @param i_Data        数据
     * @param i_ExpireTime  过期时长(单位：秒)。指当前时刻过i_ExpireTime秒后过期失效。
     * @return              返回影响的行数。负数表示异常
     */
    @Override
    public Long save(String i_Database ,String i_Table ,String i_ID ,Data i_Data ,long i_ExpireTime)
    {
        XJava.putObject(i_Database + $Level + i_Table + $Level + i_ID ,i_Data ,i_ExpireTime);
        return 1L;
    }
    
    
    
    /**
     * 保存一行数据（数据不存时：创建。数据存时：更新或删除）
     * 
     * 注：表不存时，自动创建表、库关系等信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-13
     * @version     v1.0
     * 
     * @param i_Database   库名称
     * @param i_Table      表名称
     * @param i_ID         主键ID（要求：全域、全库、全表均是惟一的）
     * @param i_Datas      数据信息。当为 Map.value 为 null 时，将执行Redis删除命令
     * @param i_ExpireTime 过期时间（单位：秒）
     * @return             返回影响的行数。负数表示异常
     */
    @Override
    public Long save(String i_Database ,String i_Table ,String i_ID ,Map<String ,Object> i_Datas ,Long i_ExpireTime)
    {
        XJava.putObject(i_Database + $Level + i_Table + $Level + i_ID ,i_Datas ,i_ExpireTime);
        return 1L;
    }
    
    
    
    /**
     * 保存一行数据（数据不存时：创建。数据存时：更新或删除）
     * 
     * 注：表不存时，自动创建表、库关系等信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-13
     * @version     v1.0
     * 
     * @param i_Database       库名称
     * @param i_Table          表名称
     * @param i_ID             主键ID（要求：全域、全库、全表均是惟一的）
     * @param i_Datas          数据信息。对象成员属性为 null 时，当 i_HaveNullValue 为假时，对象成员属性不参与更新
     *                                  对象成员属性为 null 时，当 i_HaveNullValue 为真时，对象成员属性将从Redis中删除
     * @param i_HaveNullValue  是否包含对象属性值为null的元素
     * @param i_ExpireTime     过期时间（单位：秒）
     * @return                 返回影响的行数。负数表示异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public Long save(String i_Database ,String i_Table ,String i_ID ,Object i_Datas ,boolean i_HaveNullValue ,Long i_ExpireTime)
    {
        String v_XID = i_Database + $Level + i_Table + $Level + i_ID;
        Data   v_Old = (Data) XJava.getObject(v_XID);
        
        if ( v_Old == null )
        {
            XJava.putObject(v_XID ,i_Datas ,i_ExpireTime);
            return 1L;
        }
        else
        {
            // TODO 2025-08-13 实现与Redis一样的 i_HaveNullValue 功能，执行成本太高，暂时不实现，等未来真的有用时再做
            XJava.putObject(v_XID ,i_Datas ,i_ExpireTime);
            return 1L;
        }
    }
    
    
    
    /**
     * 删除数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @param i_ID        主键ID（要求：全域、全库、全表均是惟一的）
     * @return            返回删除的数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public Data remove(String i_Database ,String i_Table ,String i_ID)
    {
        Data v_Old = (Data) XJava.getObject(i_Database + $Level + i_Table + $Level + i_ID);
        if ( v_Old != null )
        {
            XJava.remove(i_ID);
        }
        return v_Old;
    }
    
    
    
    /**
     * 删除内存表。会同时删除表数据、表关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-24
     * @version     v1.0
     *
     * @param i_Database   库名称
     * @param i_TableName  表名称
     */
    @Override
    public synchronized boolean dropTable(String i_Database ,String i_Table)
    {
        Map<String ,Object> v_Datas = XJava.getObjects(i_Database + $Level + i_Table + $Level);
        
        if ( !Help.isNull(v_Datas) )
        {
            for (String v_Key : v_Datas.keySet())
            {
                XJava.remove(v_Key);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 删除整个数据库
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-13
     * @version     v1.0
     *
     * @param i_Database  库名称
     * @return
     */
    @Override
    public synchronized boolean dropDatabase(String i_Database)
    {
        Map<String ,Object> v_Datas = XJava.getObjects(i_Database + $Level);
        
        if ( !Help.isNull(v_Datas) )
        {
            for (String v_Key : v_Datas.keySet())
            {
                XJava.remove(v_Key);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 获取数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @param i_ID        主键ID（要求：全域、全库、全表均是惟一的）
     * @return
     */
    @Override
    public Data get(String i_Database ,String i_Table ,String i_ID)
    {
        return this.getRow(i_Database ,i_Table ,i_ID);
    }
    
    
    
    /**
     * 获取一行数据（Map结构中元素类型的翻译）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-14
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @param i_ID        主键ID（要求：全域、全库、全表均是惟一的）
     * @return
     */
    @Override
    public Data getRow(String i_Database ,String i_Table ,String i_ID)
    {
        Object v_Object = XJava.getObject(i_Database + $Level + i_Table + $Level + i_ID ,false);
        return this.toData(v_Object);
    }
    
    
    
    /**
     * 尝试转为 Data 类型的对象 
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-14
     * @version     v1.0
     *
     * @param i_Data
     * @return
     */
    @SuppressWarnings("unchecked")
    private Data toData(Object i_Data)
    {
        if ( this.dataClass != null )
        {
            if ( i_Data == null )
            {
                return null;
            }
            else if ( i_Data.getClass().equals(this.dataClass) )
            {
                return (Data) i_Data;
            }
            else if ( MethodReflect.isExtendImplement(i_Data ,Map.class) )
            {
                Data v_Data = null;
                try
                {
                    v_Data = this.dataClass.getDeclaredConstructor().newInstance();
                }
                catch (Exception exce)
                {
                    throw new RuntimeException(exce);
                }
                
                Help.setValues(v_Data ,(Map<String ,?>) i_Data);
                return v_Data;
            }
            else if ( i_Data instanceof String )
            {
                XJSON v_XJson = new XJSON();
                return (Data) v_XJson.toJava((String) i_Data ,this.dataClass);
            }
        }
        
        return (Data) i_Data;
    }
    
    
    
    /**
     * 获取全库所有的表数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-14
     * @version     v1.0
     *
     * @param i_Database   库名称
     * @return             Map.key行主键，Map.key表名称，Map.value表的创建时间
     */
    @Override
    public Map<String ,String> getRows(String i_Database)
    {
        Map<String ,XJavaObject> v_Datas = XJava.getMetadatas(i_Database + $Level);
        Map<String ,String>      v_Ret   = new HashMap<String ,String>();
        
        if ( !Help.isNull(v_Datas) )
        {
            for (Map.Entry<String ,XJavaObject> v_Item : v_Datas.entrySet())
            {
                v_Ret.put(v_Item.getKey() ,v_Item.getValue().getCreateTime().getFull());
            }
            
            v_Datas.clear();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取全表数据（Map结构）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-20
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @return            Map.key行主键，Map.value行数据
     */
    @Override
    public Map<String ,Data> getRowsMap(String i_Database ,String i_Table)
    {
        Map<String ,Object> v_Datas = XJava.getObjects(i_Database + $Level + i_Table + $Level ,false);
        Map<String ,Data>   v_Ret   = new HashMap<String ,Data>();
        
        if ( !Help.isNull(v_Datas) )
        {
            for (Map.Entry<String ,Object> v_Item : v_Datas.entrySet())
            {
                if ( v_Item.getValue() != null && v_Item.getValue().getClass().equals(this.dataClass) )
                {
                    v_Ret.put(v_Item.getKey() ,this.toData(v_Item.getValue()));
                }
            }
            
            v_Datas.clear();
            v_Datas = null;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取全表数据（List结构）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-20
     * @version     v1.0
     *
     * @param i_Database  数据库名称
     * @param i_Table     表名称
     * @return
     */
    @Override
    public List<Data> getRowsList(String i_Database ,String i_Table)
    {
        Map<String ,Object> v_Datas = XJava.getObjects(i_Database + $Level + i_Table + $Level ,false);
        List<Data>          v_Ret   = new ArrayList<Data>();
        
        if ( !Help.isNull(v_Datas) )
        {
            for (Map.Entry<String ,Object> v_Item : v_Datas.entrySet())
            {
                if ( v_Item.getValue() != null && v_Item.getValue().getClass().equals(this.dataClass) )
                {
                    v_Ret.add(this.toData(v_Item.getValue()));
                }
            }
            
            v_Datas.clear();
            v_Datas = null;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 设置数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Key    关键字
     * @param i_Value  数据
     * @return         成功返回true
     */
    @Override
    public Boolean set(String i_Key ,String i_Value)
    {
        XJava.putObject(i_Key ,i_Value);
        return true;
    }
    
    
    
    /**
     * 设置数据，并且设定过期时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Key         关键字
     * @param i_Value       数据
     * @param i_ExpireTime  过期时间（单位：秒）
     * @return              成功返回true
     */
    @Override
    public Boolean setex(String i_Key ,String i_Value ,Long i_ExpireTime)
    {
        XJava.putObject(i_Key ,i_Value ,i_ExpireTime);
        return true;
    }
    
    
    
    /**
     * 设置数据，仅在关键字不存在时设置数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Key    关键字
     * @param i_Value  数据
     * @return         是否设置数据
     */
    @Override
    public synchronized Boolean setnx(String i_Key ,String i_Value)
    {
        if ( XJava.getObject(i_Key) == null )
        {
            XJava.putObject(i_Key ,i_Value);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 获取数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Key  关键字
     * @return
     */
    @Override
    public String get(String i_Key)
    {
        Object v_Value = XJava.getObject(i_Key);
        if ( v_Value != null )
        {
            return v_Value.toString();
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取数据并删除
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Key  关键字
     * @return
     */
    @Override
    public synchronized String getdel(String i_Key)
    {
        String v_Value = this.get(i_Key);
        XJava.remove(v_Value);
        return v_Value;
    }
    
    
    
    /**
     * 删除数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-09-23
     * @version     v1.0
     *
     * @param i_Keys  一个或多个关键字
     * @return        返回删除数据的数量
     */
    @Override
    public synchronized Long del(String ... i_Keys)
    {
        if ( !Help.isNull(i_Keys) )
        {
            long v_Count = 0L;
            for (String v_Key : i_Keys)
            {
                XJava.remove(v_Key);
                v_Count++;
            }
            
            return v_Count;
        }
        else
        {
            return 0L;
        }
    }
    
    
    
    /**
     * 删除一行记录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-13
     * @version     v1.0
     *
     * @param i_Database 库名称
     * @param i_Table    表名称
     * @param i_ID       主键ID（要求：全域、全库、全表均是惟一的）
     * @return              返回影响的行数。负数表示异常
     */
    @Override
    public synchronized Long delete(String i_Database ,String i_Table ,String i_ID)
    {
        String v_XID = i_Database + $Level + i_Table + $Level + i_ID;
        if ( XJava.getObject(v_XID) != null )
        {
            XJava.remove(v_XID);
            return 1L;
        }
        else
        {
            return 0L;
        }
    }
    
}
