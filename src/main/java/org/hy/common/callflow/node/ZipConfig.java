package org.hy.common.callflow.node;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.log.Logger;





/**
 * 压缩元素：将一个文件、一个文件流、多个文件或目录压缩成一个压缩包
 * 
 * 压缩成功时返回：压缩文件的全路径 和 加密密文
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-15
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class ZipConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger $Logger = new Logger(ZipConfig.class);
    
    /** 变量名称：压缩文件的全路径 */
    public  static final String $Key_DoneFile = "doneFile";
    
    /** 变量名称：加密密文 */
    public  static final String $Key_DonePwd  = "donePassword";
    
    
    
    /** 被压缩的文件、目录、数据流的变量名称。可以是数值、上下文变量、XID标识，但数据流只能是变量名称 */
    private NodeParam                     file;
    
    /** 压缩文件保存的目录。可以是数值、上下文变量、XID标识 */
    private NodeParam                     dir;
    
    /** 压缩文件保存的文件名称。可以是数值、上下文变量、XID标识 */
    private NodeParam                     name;
    
    /** 压缩密码。可以是数值、上下文变量、XID标识 */
    private NodeParam                     password;
    
    /** 是否覆盖保存。默认为：true */
    private Boolean                       overwrite;
    
    /** 完成后删除原文件。默认为：false */
    private Boolean                       doneDelete;
    
    /** 是否忽略错误（如多个文件时，某个文件不存在时或不可读时）。默认为：false */
    private Boolean                       ignoreError;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     */
    public ZipConfig()
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
    public ZipConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setCallMethod("createZip");
        
        this.file = new NodeParam();
        this.file.setValueClass(Object.class.getName());
        this.setCallParam(this.file);
        
        this.dir = new NodeParam();
        this.dir.setValueClass(String.class.getName());
        this.setCallParam(this.dir);
        
        this.name = new NodeParam();
        this.name.setValueClass(String.class.getName());
        this.setCallParam(this.name);
        
        this.password = new NodeParam();
        this.password.setValueClass(String.class.getName());
        this.setCallParam(this.password);
        
        this.overwrite   = true;
        this.doneDelete  = false;
        this.ignoreError = false;
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
            io_Result.set(false).setParamStr("CFlowCheck：ZipConfig[" + Help.NVL(this.getXid()) + "].file is null.");
            return false;
        }
        if ( Help.isNull(this.getDir()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：ZipConfig[" + Help.NVL(this.getXid()) + "].dir is null.");
            return false;
        }
        
        return true;
    }

    
    
    /**
     * 获取：被压缩的文件、目录、数据流的变量名称。可以是数值、上下文变量、XID标识，但数据流只能是变量名称
     */
    public String getFile()
    {
        return this.file.getValue();
    }

    
    /**
     * 设置：被压缩的文件、目录、数据流的变量名称。可以是数值、上下文变量、XID标识，但数据流只能是变量名称
     * 
     * @param i_File 被压缩的文件、目录、数据流的变量名称。可以是数值、上下文变量、XID标识，但数据流只能是变量名称
     */
    public void setFile(String i_File)
    {
        this.file.setValue(i_File);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：压缩文件保存的目录。可以是数值、上下文变量、XID标识
     */
    public String getDir()
    {
        return this.dir.getValue();
    }

    
    /**
     * 设置：压缩文件保存的目录。可以是数值、上下文变量、XID标识
     * 
     * @param i_Dir 压缩文件保存的目录。可以是数值、上下文变量、XID标识
     */
    public void setDir(String i_Dir)
    {
        this.dir.setValue(i_Dir);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：压缩文件保存的文件名称。可以是数值、上下文变量、XID标识
     */
    public String getName()
    {
        return this.name.getValue();
    }

    
    /**
     * 设置：压缩文件保存的文件名称。可以是数值、上下文变量、XID标识
     * 
     * @param i_Name 压缩文件保存的文件名称。可以是数值、上下文变量、XID标识
     */
    public void setName(String i_Name)
    {
        this.name.setValue(i_Name);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    /**
     * 获取：压缩密码。可以是数值、上下文变量、XID标识
     */
    public String getPassword()
    {
        return this.password.getValue();
    }

    
    /**
     * 设置：压缩密码。可以是数值、上下文变量、XID标识
     * 
     * @param i_Password 压缩密码。可以是数值、上下文变量、XID标识
     */
    public void setPassword(String i_Password)
    {
        this.password.setValue(i_Password);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    /**
     * 获取：是否覆盖保存。默认为：true
     */
    public Boolean getOverwrite()
    {
        return overwrite;
    }

    
    /**
     * 设置：是否覆盖保存。默认为：true
     * 
     * @param i_Overwrite 是否覆盖保存。默认为：true
     */
    public void setOverwrite(Boolean i_Overwrite)
    {
        this.overwrite = i_Overwrite == null ? true : i_Overwrite;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：完成后删除原文件。默认为：false
     */
    public Boolean getDoneDelete()
    {
        return doneDelete;
    }

    
    /**
     * 设置：完成后删除原文件。默认为：false
     * 
     * @param i_DoneDelete 完成后删除原文件。默认为：false
     */
    public void setDoneDelete(Boolean i_DoneDelete)
    {
        this.doneDelete = i_DoneDelete == null ? false : i_DoneDelete;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    /**
     * 获取：是否忽略错误（如多个文件时，某个文件不存在时或不可读时）。默认为：false
     */
    public Boolean getIgnoreError()
    {
        return ignoreError;
    }

    
    /**
     * 设置：是否忽略错误（如多个文件时，某个文件不存在时或不可读时）。默认为：false
     * 
     * @param i_IgnoreError 是否忽略错误（如多个文件时，某个文件不存在时或不可读时）。默认为：false
     */
    public void setIgnoreError(Boolean i_IgnoreError)
    {
        this.ignoreError = i_IgnoreError;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * 自己反射调用自己的实例中的方法
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_Xid)
    {
        super.setXJavaID(i_Xid);
        this.setCallXID(this.getXid());
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
        return ElementType.Zip.getValue();
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
        return ElementType.Zip.getXmlName();
    }
    
    
    
    /**
     * 转XML时是否显示retFalseIsError属性
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public boolean xmlShowRetFalseIsError()
    {
        return false;
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        // 其实就是返回自己。io_ExecuteObject 获取正确时，也是this自己
        return io_ExecuteObject == null ? this : io_ExecuteObject;
    }
    
    
    
    /**
     * 执行方法前，对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        return io_Params;
    }
    
    
    
    /**
     * 创建压缩文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_File      被压缩的文件、目录、数据流
     * @param i_ZipDir    压缩文件保存的目录
     * @param i_ZipName   压缩文件保存的文件名称
     * @param i_Password  压缩密码
     * @return
     */
    public Map<String ,String> createZip(Object i_File ,String i_ZipDir ,String i_ZipName ,String i_Password)
    {
        if ( Help.isNull(i_ZipDir) )
        {
            throw new RuntimeException("Dir is null");
        }
        
        File v_ZipDir = new File(i_ZipDir.endsWith(Help.getSysPathSeparator()) ? i_ZipDir : i_ZipDir + Help.getSysPathSeparator());
        if ( !v_ZipDir.exists() )
        {
            // 当目录不存在时，自动创建
            v_ZipDir.mkdirs();
        }
        
        File v_ZipFile = null;
        if ( Help.isNull(i_ZipName) )
        {
            v_ZipFile = new File(v_ZipDir.getPath() + StringHelp.getUUID9n() + ".zip");
        }
        else
        {
            v_ZipFile = new File(v_ZipDir.getPath() + i_ZipName);
        }
        
        if ( v_ZipFile.exists() )
        {
            if ( !this.overwrite )
            {
                throw new RuntimeException("Zip[" + v_ZipFile.getPath() + "] was exists.");
            }
            else
            {
                // 覆盖保存时删除
                v_ZipFile.delete();
                $Logger.info("Old Zip file[" + v_ZipFile.getPath() + "] was delete.");
            }
        }
        
        FileHelp      v_FHelp = new FileHelp();
        StringBuilder v_Msg   = new StringBuilder();
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
                
                String [] v_Files = v_FileString.split(";");
                if ( v_Files.length <= 1 )
                {
                    File v_File = new File(v_FileString);
                    v_Msg.append(v_File.getPath()).append(" Zip to ").append(v_ZipFile.getPath());
                    v_FHelp.createZip4j(v_ZipFile ,v_File ,i_Password);
                    
                    if ( this.doneDelete )
                    {
                        v_File.delete();
                    }
                }
                else
                {
                    List<File> v_FileList = new ArrayList<File>();
                    for (String v_Item : v_Files)
                    {
                        File v_File = new File(v_Item);
                        v_FileList.add(v_File);
                        v_Msg.append(v_File.getPath()).append(";");
                    }
                    
                    v_Msg.append(" Zip to ").append(v_ZipFile.getPath());
                    v_FHelp.createZip4j(v_ZipFile ,v_FileList ,i_Password);
                    
                    if ( this.doneDelete )
                    {
                        for (File v_Item : v_FileList)
                        {
                            v_Item.delete();
                        }
                    }
                }
            }
            else if ( i_File instanceof File )
            {
                File v_File = (File) i_File;
                v_Msg.append(v_File.getPath()).append(" Zip to ").append(v_ZipFile.getPath());
                v_FHelp.createZip4j(v_ZipFile ,v_File ,i_Password);
                
                if ( this.doneDelete )
                {
                    v_File.delete();
                }
            }
            else if ( i_File instanceof InputStream )
            {
                InputStream v_File = (InputStream) i_File;
                v_Msg.append("InputStream Zip to ").append(v_ZipFile.getPath());
                v_FHelp.createZip4j(v_ZipFile ,v_File ,i_Password);
                v_File.close();
            }
            else
            {
                throw new RuntimeException("File[" + v_ZipFile.getPath() + "] parameter type is error.");
            }
            
            v_Msg.append(" is Succeed.");
            $Logger.info(v_Msg.toString());
            Map<String ,String> v_Ret = new HashMap<String ,String>();
            v_Ret.put($Key_DoneFile ,v_ZipFile.getPath());
            v_Ret.put($Key_DonePwd  ,i_Password);
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
        
        if ( !Help.isNull(this.file.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("file"        ,this.file.getValue()));
        }
        if ( !Help.isNull(this.dir.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("dir"         ,this.dir.getValue()));
        }
        if ( !Help.isNull(this.name.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("name"        ,this.name.getValue()));
        }
        if ( this.overwrite != null && !this.overwrite )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("overwrite"   ,this.overwrite));
        }
        if ( this.doneDelete != null && this.doneDelete )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("doneDelete"  ,this.doneDelete));
        }
        if ( this.ignoreError != null && this.ignoreError )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("ignoreError" ,this.ignoreError));
        }
        if ( !Help.isNull(this.password.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("password"    ,this.password.getValue()));
        }
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.file.getValue()) )
        {
            v_Builder.append(this.file.getValue());
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" to ");
        if ( !Help.isNull(this.dir.getValue()) )
        {
            v_Builder.append(this.dir.getValue()).append(Help.getSysPathSeparator());
            if ( !Help.isNull(this.name.getValue()) )
            {
                v_Builder.append(this.name.getValue());
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.file.getValue()) )
        {
            v_Builder.append(this.file.getValue());
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" to ");
        if ( !Help.isNull(this.dir.getValue()) )
        {
            v_Builder.append(this.dir.getValue()).append(Help.getSysPathSeparator());
            if ( !Help.isNull(this.name.getValue()) )
            {
                v_Builder.append(this.name.getValue());
            }
        }
        else
        {
            v_Builder.append("?");
        }
        
        return v_Builder.toString();
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
        return new ZipConfig();
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
        ZipConfig v_Clone = new ZipConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.overwrite   = this.overwrite;
        v_Clone.doneDelete  = this.doneDelete;
        v_Clone.ignoreError = this.ignoreError;
        
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
            throw new NullPointerException("Clone ZipConfig xid is null.");
        }
        
        ZipConfig v_Clone = (ZipConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.overwrite   = this.overwrite;
        v_Clone.doneDelete  = this.doneDelete;
        v_Clone.ignoreError = this.ignoreError;
        
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
            throw new NullPointerException("Clone ZipConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ZipConfig                   v_Clone      = new ZipConfig();
        
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
