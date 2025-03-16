package org.hy.common.callflow.clone;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.execute.ExecuteElement;





/**
 * 克隆编排
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-10
 * @version     v1.0
 * @param <E>
 */
public interface CloneableCallFlow
{
    
    /** XID版本的前缀 */
    public String XIDVersion = "_V";
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly();
    
    
    
    /**
     * 深度克隆编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects);
    
    
    
    /**
     * 克隆XID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @return
     */
    default String cloneXID(String i_XID ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID)
    {
        String v_New = i_XID;
        if ( !Help.isNull(i_ReplaceXID) )
        {
            v_New = StringHelp.replaceAll(v_New ,i_ReplaceXID ,Help.NVL(i_ReplaceByXID));
        }
        
        return v_New + Help.NVL(i_AppendXID);
    }
    
    
    
    /**
     * 从XID的编码规范中解析版本号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param i_XID
     * @return
     */
    default Return<String> parserXIDVersion(String i_XID)
    {
        Return<String> v_Ret   = new Return<String> (false);
        String []      v_Datas = StringHelp.split(i_XID ,XIDVersion);
        if ( v_Datas.length >= 2 )
        {
            String v_Version = v_Datas[v_Datas.length - 1];
            if ( Help.isNumber(v_Ret.getParamStr()) )
            {
                v_Ret.setParamInt(Integer.parseInt(v_Ret.getParamStr()));
                v_Ret.setParamStr(XIDVersion + v_Version);
                v_Ret.set(true);
            }
        }
        
        return v_Ret;
    }
    
}
