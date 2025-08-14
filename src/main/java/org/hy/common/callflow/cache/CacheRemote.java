package org.hy.common.callflow.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.hy.common.redis.IRedis;





/**
 * 远程缓存
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-07-01
 * @version     v1.0
 * @param <Data>  缓存的数据对象
 *              v2.0  2024-09-20  添加：getRowsList 和 getRowsMap 全表数据获取的方法
 *              v3.0  2024-09-23  添加：开放字符串的get、set方法
 */
public class CacheRemote<Data> implements ICache<Data>
{
    
    /** Redis接口 */
    private IRedis      redis;
    
    /** 数据类型 */
    private Class<Data> dataClass;
    
    
    
    public CacheRemote(IRedis i_Redis)
    {
        this.redis     = i_Redis;
        this.dataClass = this.parserDataType();
    }
    
    
    
    public CacheRemote(IRedis i_Redis ,Class<Data> i_DataClass)
    {
        this.redis     = i_Redis;
        this.dataClass = i_DataClass;
    }
    
    
    
    /**
     * 解析泛型<Data>的真实元类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<Data> parserDataType()
    {
        Type v_SuperClass = getClass().getGenericSuperclass();
        if (v_SuperClass instanceof ParameterizedType)
        {
            ParameterizedType v_ParameterizedType = (ParameterizedType) v_SuperClass;
            Type[] v_TypeArgs = v_ParameterizedType.getActualTypeArguments();
            if ( v_TypeArgs.length > 0 && v_TypeArgs[0] instanceof Class )
            {
                return (Class<Data>) v_TypeArgs[0];
            }
        }
        
        return null;
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
        return this.redis.save(i_Database ,i_Table ,i_ID ,i_Data ,true);
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
        return this.redis.save(i_Database ,i_Table ,i_ID ,i_Data ,true ,i_ExpireTime);
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
        return this.redis.save(i_Database ,i_Table ,i_ID ,i_Datas ,i_ExpireTime);
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
    @Override
    public Long save(String i_Database ,String i_Table ,String i_ID ,Object i_Datas ,boolean i_HaveNullValue ,Long i_ExpireTime)
    {
        return this.redis.save(i_Database ,i_Table ,i_ID ,i_Datas ,i_HaveNullValue ,i_ExpireTime);
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
    @Override
    public synchronized Data remove(String i_Database ,String i_Table ,String i_ID)
    {
        Data v_Old = this.redis.getRow(i_ID ,this.dataClass);
        if ( v_Old != null )
        {
            Long v_Count = this.redis.delete(i_Database ,i_Table ,i_ID);
            if ( v_Count == null || v_Count <= 0 )
            {
                return null;
            }
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
     * @param i_Database  库名称
     * @param i_Table     表名称
     */
    @Override
    public boolean dropTable(String i_Database ,String i_Table)
    {
        return this.redis.dropTable(i_Database ,i_Table);
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
    public boolean dropDatabase(String i_Database)
    {
        return this.redis.dropDatabase(i_Database);
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
        return this.redis.getRow(i_Database ,i_Table ,i_ID ,this.dataClass);
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
        return this.redis.getRow(i_Database ,i_Table ,i_ID ,this.dataClass);
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
        return this.redis.getRows(i_Database ,i_Table ,this.dataClass);
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
        return this.redis.getRowsList(i_Database ,i_Table ,this.dataClass);
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
        return this.redis.getRows(i_Database);
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
        return this.redis.set(i_Key ,i_Value);
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
        return this.redis.setex(i_Key ,i_Value ,i_ExpireTime);
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
    public Boolean setnx(String i_Key ,String i_Value)
    {
        return this.redis.setnx(i_Key ,i_Value);
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
        return this.redis.get(i_Key);
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
    public String getdel(String i_Key)
    {
        return this.redis.getdel(i_Key);
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
    public Long del(String ... i_Keys)
    {
        return this.redis.del(i_Keys);
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
    public Long delete(String i_Database ,String i_Table ,String i_ID)
    {
        return this.redis.delete(i_Database ,i_Table ,i_ID);
    }
    
}
