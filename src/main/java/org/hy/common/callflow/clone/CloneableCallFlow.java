package org.hy.common.callflow.clone;

import java.util.Map;

import org.hy.common.Help;
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
    
}
