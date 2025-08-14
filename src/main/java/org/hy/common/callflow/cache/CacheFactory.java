package org.hy.common.callflow.cache;

import org.hy.common.redis.IRedis;





/**
 * 缓存的简单工厂，构建本地缓存或远程缓存的统一方法类
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-07-01
 * @version     v1.0
 */
public class CacheFactory
{
    
    /**
     * 构建缓存实例对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-01
     * @version     v1.0
     *
     * @param <Data>       缓存数据对象
     * @param i_IRedis     远程缓存对象，同时它也是否启用远程缓存的标记
     * @param i_DataClass  缓存数据对象的元类型
     * @return
     */
    public static <Data> ICache<Data> newInstanceOf(IRedis i_IRedis ,Class<Data> i_DataClass)
    {
        if ( i_IRedis != null )
        {
            return new CacheRemote<Data>(i_IRedis ,i_DataClass);
        }
        else
        {
            return new CacheLocal<Data>(i_DataClass);
        }
    }
    
    
    
    private CacheFactory()
    {
        // 本类不可被 new
    }
    
}
