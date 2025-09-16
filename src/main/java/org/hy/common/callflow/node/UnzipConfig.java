package org.hy.common.callflow.node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.log.Logger;





/**
 * 解压元素：将压缩包解压
 * 
 * 解压成功时返回：解压路径
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-15
 * @version     v1.0
 */
public class UnzipConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger $Logger = new Logger(UnzipConfig.class);
    
    
    /** 压缩包文件的全路径。可以是数值、上下文变量、XID标识 */
    private NodeParam file;
    
    /** 解压目录 */
    private NodeParam dir;
    
    /** 压缩密码。可以是数值、上下文变量、XID标识 */
    private NodeParam password;
    
    /** 完成后删除原文件。默认为：false */
    private Boolean   doneDelete;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     */
    public UnzipConfig()
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
    public UnzipConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setCallMethod("unzip");
        
        this.file = new NodeParam();
        this.file.setValueClass(String.class.getName());
        this.setCallParam(this.file);
        
        this.dir = new NodeParam();
        this.dir.setValueClass(String.class.getName());
        this.setCallParam(this.dir);
        
        this.password = new NodeParam();
        this.password.setValueClass(String.class.getName());
        this.setCallParam(this.password);
        
        this.doneDelete = false;
    }

    
    
    /**
     * 获取：压缩包文件的全路径。可以是数值、上下文变量、XID标识
     */
    public String getFile()
    {
        return this.file.getValue();
    }

    
    /**
     * 设置：压缩包文件的全路径。可以是数值、上下文变量、XID标识
     * 
     * @param i_File 压缩包文件的全路径。可以是数值、上下文变量、XID标识
     */
    public void setFile(String i_File)
    {
        this.file.setValue(i_File);
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
     * 获取：解压目录
     */
    public String getDir()
    {
        return this.dir.getValue();
    }

    
    /**
     * 设置：解压目录
     * 
     * @param i_Dir 解压目录
     */
    public void setDir(String i_Dir)
    {
        this.dir.setValue(i_Dir);
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
        return ElementType.Unzip.getValue();
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
        return ElementType.Unzip.getXmlName();
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
     * 解压文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-15
     * @version     v1.0
     *
     * @param i_ZipFile   压缩包文件
     * @param i_UnzipDir  解压目录
     * @param i_Password  压缩密码
     * @return
     */
    public String unzip(String i_ZipFile ,String i_UnzipDir ,String i_Password)
    {
        if ( Help.isNull(i_ZipFile) )
        {
            throw new RuntimeException("ZipFile is null");
        }
        
        File v_ZipFile = new File(i_ZipFile); 
        if ( !v_ZipFile.exists() )
        {
            throw new RuntimeException("Unzip[" + i_ZipFile + "] is not exists.");
        }
        
        String v_UnzipDir = i_UnzipDir;
        if ( Help.isNull(v_UnzipDir) )
        {
            v_UnzipDir = v_ZipFile.getParent();
        }
        
        try
        {
            FileHelp v_FHelp = new FileHelp();
            v_FHelp.UnCompressZip4j(i_ZipFile ,v_UnzipDir ,i_Password);
            if ( this.doneDelete )
            {
                v_ZipFile.delete();
            }
            return i_UnzipDir;
        }
        catch (Exception exce)
        {
            $Logger.error(exce ,i_ZipFile + " Unzip to " + i_UnzipDir);
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
            io_Xml.append(v_NewSpace).append(IToXml.toValue("file"       ,this.file.getValue()));
        }
        if ( !Help.isNull(this.dir.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("dir"        ,this.dir.getValue()));
        }
        if ( this.doneDelete != null && this.doneDelete )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("doneDelete" ,this.doneDelete));
        }
        if ( !Help.isNull(this.password.getValue()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("password"   ,this.password.getValue()));
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
            v_Builder.append(this.dir.getValue());
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
            v_Builder.append(this.dir.getValue());
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
        return new UnzipConfig();
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
        UnzipConfig v_Clone = new UnzipConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.doneDelete  = this.doneDelete;
        
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
            throw new NullPointerException("Clone UnzipConfig xid is null.");
        }
        
        UnzipConfig v_Clone = (UnzipConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod  = this.callMethod; 
        v_Clone.timeout     = this.timeout;
        v_Clone.doneDelete  = this.doneDelete;
        
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
            throw new NullPointerException("Clone UnzipConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        UnzipConfig                 v_Clone      = new UnzipConfig();
        
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
