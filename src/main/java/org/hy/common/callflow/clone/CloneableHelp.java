package org.hy.common.callflow.clone;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.execute.ExecuteElement;





/**
 * 克隆编排的辅助类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-18
 * @version     v1.0
 */
public class CloneableHelp
{
    
    private static CloneableHelp $Instance = new CloneableHelp();
    
    
    
    /**
     * 克隆编排的相关操作类
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @return
     */
    public static CloneableHelp getInstance()
    {
        return $Instance;
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    public <C extends ExecuteElement> C cloneMyOnly(C i_ExecuteElement)
    {
        return (C) i_ExecuteElement.cloneMyOnly();
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-18
     * @version     v1.0
     *
     * @param <C>
     * @param i_ExecuteElement  要克隆的原对象
     * @return
     * @throws CloneNotSupportedException
     */
    @SuppressWarnings("unchecked")
    public <C extends ExecuteElement> C clone(C i_ExecuteElement) throws CloneNotSupportedException
    {
        return (C) i_ExecuteElement.clone();
    }
    
    
    
    /**
     * 深度克隆编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
     * @version     v1.0
     *
     * @param i_ExecuteElement  要克隆的原对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends ExecuteElement> C clone(C i_ExecuteElement ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID)
    {
        if ( Help.isNull(i_ExecuteElement.getXid()) )
        {
            throw new NullPointerException("Clone xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = i_ExecuteElement.parserXIDVersion(i_ExecuteElement.getXid());
        C                           v_Clone      = (C) i_ExecuteElement.newMy();
        
        if ( v_Version.booleanValue() )
        {
            i_ExecuteElement.clone(v_Clone 
                                  ,v_Version.getParamStr() 
                                  ,CloneableCallFlow.XIDVersion + (v_Version.getParamInt() + 1) 
                                  ,""         
                                  ,v_XIDObjects);
        }
        else
        {
            i_ExecuteElement.clone(v_Clone 
                                  ,""                      
                                  ,""                                         
                                  ,CloneableCallFlow.XIDVersion 
                                  ,v_XIDObjects);
        }
        
        v_XIDObjects.clear();
        v_XIDObjects = null;
        
        return v_Clone;
    }
    
    
    
    private CloneableHelp()
    {
        // Nothing.
    }
    
}
