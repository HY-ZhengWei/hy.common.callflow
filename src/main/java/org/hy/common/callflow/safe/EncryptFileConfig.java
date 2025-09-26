package org.hy.common.callflow.safe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.callflow.node.ZipConfig;
import org.hy.common.file.FileHelp;
import org.hy.common.license.Hash;
import org.hy.common.license.IHash;
import org.hy.common.xml.log.Logger;





/**
 * 密文元素：对一个文件、一个文件流加密
 * 
 * 加密成功时返回：密文文件的全路径 和 加密密文
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-15
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class EncryptFileConfig extends ZipConfig implements NodeConfigBase
{
    
    private static final Logger $Logger = new Logger(EncryptFileConfig.class);
    
    
    
    /** 密钥生成类型。MD5V4 和 FAST 两种。默认为：MD5V4 */
    private String passwordType;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     */
    public EncryptFileConfig()
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
    public EncryptFileConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        this.setCallMethod("createEncryptFile");
        this.setPasswordType("MD5V4");
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
        if ( Help.isNull(this.getFile()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：EncryptFileConfig[" + Help.NVL(this.getXid()) + "].file is null.");
            return false;
        }
        if ( Help.isNull(this.getDir()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：EncryptFileConfig[" + Help.NVL(this.getXid()) + "].dir is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 获取：密钥生成类型。MD5 和 FAST 两种。默认为：MD5
     */
    public String getPasswordType()
    {
        return passwordType;
    }


    
    /**
     * 设置：密钥生成类型。MD5 和 FAST 两种。默认为：MD5
     * 
     * @param i_PasswordType 密钥生成类型。MD5 和 FAST 两种。默认为：MD5
     */
    public void setPasswordType(String i_PasswordType)
    {
        if ( Help.isNull(i_PasswordType) )
        {
            this.passwordType = "MD5V4";
        }
        else if ( "MD5V4".equalsIgnoreCase(i_PasswordType) || "FAST".equalsIgnoreCase(i_PasswordType) )
        {
            this.passwordType = i_PasswordType;
        }
        else
        {
            this.passwordType = "MD5V4";
        }
        
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
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
        return ElementType.EncryptFile.getValue();
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
        return ElementType.EncryptFile.getXmlName();
    }
    
    
    
    /**
     * 创建加密文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_File      被加密的文件、数据流
     * @param i_Dir       密文保存的目录
     * @param i_Name      密文保存的文件名称
     * @param i_Password  密文密码
     * @return
     */
    public Map<String ,String> createEncryptFile(Object i_File ,String i_Dir ,String i_Name ,String i_Password)
    {
        if ( Help.isNull(i_Dir) )
        {
            throw new RuntimeException("Dir is null");
        }
        
        File v_Dir = new File(i_Dir.endsWith(Help.getSysPathSeparator()) ? i_Dir : i_Dir + Help.getSysPathSeparator());
        if ( !v_Dir.exists() )
        {
            // 当目录不存在时，自动创建
            v_Dir.mkdirs();
        }
        
        File v_EnFile = null;
        if ( Help.isNull(i_Name) )
        {
            v_EnFile = new File(v_Dir.getPath() + StringHelp.getUUID9n());
        }
        else
        {
            v_EnFile = new File(v_Dir.getPath() + i_Name);
        }
        
        if ( v_EnFile.exists() )
        {
            if ( !this.getOverwrite() )
            {
                throw new RuntimeException("File[" + v_EnFile.getPath() + "] was exists.");
            }
            else
            {
                // 覆盖保存时删除
                v_EnFile.delete();
                $Logger.info("Old file[" + v_EnFile.getPath() + "] was delete.");
            }
        }
        
        FileHelp      v_FHelp    = new FileHelp();
        StringBuilder v_Msg      = new StringBuilder();
        String        v_Password = i_Password;
        try
        {
            
            if ( i_File == null )
            {
                throw new RuntimeException("File is null.");
            }
            else if ( i_File instanceof String )
            {
                String v_FileString = i_File.toString();
                if ( Help.isNull(v_FileString) )
                {
                    throw new RuntimeException("File is null.");
                }
                
                File v_File = new File(v_FileString);
                v_Msg.append(v_File.getPath()).append(" Encrypt to ").append(v_EnFile.getPath());
                
                if ( Help.isNull(v_Password) )
                {
                    if ( "FAST".equals(v_Password) )
                    {
                        v_Password = StringHelp.getUUID9n();
                    }
                    else
                    {
                        v_Password = makePassword(v_File);
                    }
                }
                
                v_FHelp.createZip4j(v_EnFile ,v_File ,v_Password);
                
                if ( this.getDoneDelete() )
                {
                    v_File.delete();
                }
            }
            else if ( i_File instanceof File )
            {
                File v_File = (File) i_File;
                v_Msg.append(v_File.getPath()).append(" Encrypt to ").append(v_EnFile.getPath());
                
                if ( Help.isNull(v_Password) )
                {
                    if ( "FAST".equals(v_Password) )
                    {
                        v_Password = StringHelp.getUUID9n();
                    }
                    else
                    {
                        v_Password = makePassword(v_File);
                    }
                }
                
                v_FHelp.createZip4j(v_EnFile ,v_File ,v_Password);
                
                if ( this.getDoneDelete() )
                {
                    v_File.delete();
                }
            }
            else if ( i_File instanceof InputStream )
            {
                InputStream v_File = (InputStream) i_File;
                v_Msg.append("InputStream Encrypt to ").append(v_EnFile.getPath());
                
                if ( Help.isNull(v_Password) )
                {
                    byte [] v_Content = v_FHelp.getContentByte(v_File);
                    if ( "FAST".equals(v_Password) )
                    {
                        v_Password = StringHelp.getUUID9n();
                    }
                    else
                    {
                        v_Password = makePassword(new ByteArrayInputStream(v_Content));
                    }
                    v_File = new ByteArrayInputStream(v_Content);
                }
                
                v_FHelp.createZip4j(v_EnFile ,v_File ,v_Password);
                v_File.close();
            }
            else
            {
                throw new RuntimeException("File[" + v_EnFile.getPath() + "] parameter type is error.");
            }
            
            v_Msg.append(" is Succeed.");
            $Logger.info(v_Msg.toString());
            Map<String ,String> v_Ret = new HashMap<String ,String>();
            v_Ret.put($Key_DoneFile ,v_EnFile.getPath());
            v_Ret.put($Key_DonePwd  ,v_Password);
            return v_Ret;
        }
        catch (RuntimeException exce)
        {
            $Logger.error(exce ,v_Msg.toString());
            throw exce;
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,v_Msg.toString());
            throw new RuntimeException(exce.getMessage() ,exce);
        }
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param io_Xml         XML内容的缓存区
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_Level1       单级层级的空格间隔
     * @param i_LevelN       N级层级的空格间隔
     * @param i_SuperTreeID  父级树ID
     * @param i_TreeID       当前树ID
     */
    public void toXmlContent(StringBuilder io_Xml ,int i_Level ,String i_Level1 ,String i_LevelN ,String i_SuperTreeID ,String i_TreeID)
    {
        String v_NewSpace = "\n" + i_LevelN + i_Level1;
        
        super.toXmlContent(io_Xml ,i_Level ,i_Level1 ,i_LevelN ,i_SuperTreeID ,i_TreeID);
        if ( !Help.isNull(this.passwordType) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("passwordType" ,this.passwordType));
        }
    }
    
    
    
    /**
     * 文件内容生成密钥
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_File
     * @return
     * @throws IOException
     */
    private String makePassword(File i_File) throws IOException
    {
        FileHelp v_FHelp   = new FileHelp();
        String   v_Content = v_FHelp.getContent(i_File);
        IHash    v_MD5     = new Hash();
        
        return v_MD5.encrypt(v_Content);
    }
    
    
    
    /**
     * 文件内容生成密钥
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param io_File
     * @return
     * @throws IOException
     */
    private String makePassword(InputStream io_File) throws IOException
    {
        FileHelp v_FHelp   = new FileHelp();
        String   v_Content = v_FHelp.getContent(io_File);
        IHash    v_MD5     = new Hash();
        
        return v_MD5.encrypt(v_Content);
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
        return new EncryptFileConfig();
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
        EncryptFileConfig v_Clone = new EncryptFileConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID   = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod   = this.callMethod; 
        v_Clone.timeout      = this.timeout;
        v_Clone.passwordType = this.passwordType;
        v_Clone.setOverwrite( this.getOverwrite());
        v_Clone.setDoneDelete(this.getDoneDelete());
        v_Clone.setIgnoreError(this.getIgnoreError());
        
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
            throw new NullPointerException("Clone EncryptFileConfig xid is null.");
        }
        
        EncryptFileConfig v_Clone = (EncryptFileConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID   = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod   = this.callMethod; 
        v_Clone.timeout      = this.timeout;
        v_Clone.passwordType = this.passwordType;
        v_Clone.setOverwrite( this.getOverwrite());
        v_Clone.setDoneDelete(this.getDoneDelete());
        v_Clone.setIgnoreError(this.getIgnoreError());
        
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
            throw new NullPointerException("Clone EncryptFileConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        EncryptFileConfig           v_Clone      = new EncryptFileConfig();
        
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
