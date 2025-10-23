package org.hy.common.callflow.safe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.callflow.node.UnzipConfig;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.log.Logger;





/**
 * 解文元素：密文元素的反向操作，解密文件
 * 
 * 解密成功时返回：解密路径
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-15
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class DecryptFileConfig extends UnzipConfig implements NodeConfigBase
{
    
    private static final Logger $Logger = new Logger(DecryptFileConfig.class);
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     */
    public DecryptFileConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public DecryptFileConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.setCallMethod("decryptFile");
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        if ( !super.check(io_Result) )
        {
            return false;
        }
        
        if ( Help.isNull(this.getFile()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].file is null.");
            return false;
        }
        if ( Help.isNull(this.getPassword()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].password is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-21
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        return "XDEF_" + StringHelp.getUUID9n();
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.DecryptFile.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.DecryptFile.getXmlName();
    }
    
    
    
    /**
     * 解密文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_File      密文文件
     * @param i_Dir       解密目录
     * @param i_Password  密文密码
     * @return
     */
    public String decryptFile(String i_File ,String i_Dir ,String i_Password)
    {
        if ( Help.isNull(i_Password) )
        {
            throw new RuntimeException("Password is null");
        }
        
        if ( Help.isNull(i_File) )
        {
            throw new RuntimeException("File is null");
        }
        
        File v_File = new File(i_File); 
        if ( !v_File.exists() )
        {
            throw new RuntimeException("File[" + i_File + "] is not exists.");
        }
        
        String v_UnzipDir = i_Dir;
        if ( Help.isNull(v_UnzipDir) )
        {
            v_UnzipDir = v_File.getParent();
        }
        
        try
        {
            FileHelp v_FHelp = new FileHelp();
            v_FHelp.UnCompressZip4j(i_File ,v_UnzipDir ,i_Password);
            if ( this.getDoneDelete() )
            {
                v_File.delete();
            }
            return i_Dir;
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,i_File + " Decrypt to " + i_Dir);
            throw new RuntimeException(exce.getMessage() ,exce);
        }
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new DecryptFileConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        DecryptFileConfig v_Clone = new DecryptFileConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.setDoneDelete(this.getDoneDelete());
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                v_Clone.callParams.add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone DecryptFileConfig xid is null.");
        }
        
        DecryptFileConfig v_Clone = (DecryptFileConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.setDoneDelete(this.getDoneDelete());
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                v_Clone.callParams.add((NodeParam) v_NodeParam.cloneMyOnly());
            }
        }
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone DecryptFileConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        DecryptFileConfig           v_Clone      = new DecryptFileConfig();
        
        if ( v_Version.booleanValue() )
        {
            this.clone(v_Clone ,v_Version.getParamStr() ,XIDVersion + (v_Version.getParamInt() + 1) ,""         ,v_XIDObjects);
        }
        else
        {
            this.clone(v_Clone ,""                      ,""                                         ,XIDVersion ,v_XIDObjects);
        }
        
        v_XIDObjects.clear();
        v_XIDObjects = null;
        return v_Clone;
    }
    
}

