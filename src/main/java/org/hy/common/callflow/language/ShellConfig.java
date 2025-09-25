package org.hy.common.callflow.language;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.language.shell.ShellFile;
import org.hy.common.callflow.language.shell.ShellResult;
import org.hy.common.db.DBSQL;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;





/**
 * 脚本元素：在Java中嵌入Shell代码
 * 
 *   支持远程连接1：用户&密码
 *   支持远程连接2：用户&免密私钥
 *   
 * 注：先上传文件，现执行Shell代码，最后下载文件
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-09-25
 * @version     v1.0
 */
public class ShellConfig extends ExecuteElement implements Cloneable
{
    
    /** 主机IP地址 */
    private String                        host;
    
    /** 用户名称 */
    private String                        user;
    
    /** 用户密码 */
    private String                        password;
    
    /** 脚本代码 */
    private String                        shell;
    
    /** 脚本代码，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> shellPlaceholders;
    
    /** 上传文件。多个文件间有换行分隔，本地文件与远程上传目录间用英文逗号,分隔 */
    private String                        upFile;
    
    /** 上传文件，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> upFilePlaceholders;
    
    /** 下载文件。多个文件间有换行分隔，远程文件与本地下载目录间用英文逗号,分隔 */
    private String                        downFile;
    
    /** 下载文件，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> downFilePlaceholders;
    
    /** 命令结果返回时字符集。默认为：UTF-8 */
    private String                        charEncoding;
    
    
    
    public ShellConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public ShellConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    
    /**
     * 获取：主机IP地址
     */
    public String getHost()
    {
        return host;
    }


    
    /**
     * 设置：主机IP地址
     * 
     * @param i_Host 主机IP地址
     */
    public void setHost(String i_Host)
    {
        this.host = i_Host;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：用户名称
     */
    public String getUser()
    {
        return user;
    }


    
    /**
     * 设置：用户名称
     * 
     * @param i_User 用户名称
     */
    public void setUser(String i_User)
    {
        this.user = i_User;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：用户密码
     */
    public String getPassword()
    {
        return password;
    }


    
    /**
     * 设置：用户密码
     * 
     * @param i_Password 用户密码
     */
    public void setPassword(String i_Password)
    {
        this.password = i_Password;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    

    /**
     * 获取：脚本内容
     */
    public String getShell()
    {
        return shell;
    }


    
    /**
     * 设置：脚本内容
     * 
     * @param i_Shell 脚本内容
     */
    public void setShell(String i_Shell)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Shell) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Shell ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.shellPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.shellPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.shell = i_Shell;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：命令结果返回时字符集。默认为：UTF-8
     */
    public String getCharEncoding()
    {
        return charEncoding;
    }


    
    /**
     * 设置：命令结果返回时字符集。默认为：UTF-8
     * 
     * @param i_CharEncoding 命令结果返回时字符集。默认为：UTF-8
     */
    public void setCharEncoding(String i_CharEncoding)
    {
        this.charEncoding = i_CharEncoding;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：上传文件。多个文件间有换行分隔，本地文件与远程上传目录间用英文逗号,分隔
     */
    public String getUpFile()
    {
        return upFile;
    }


    
    /**
     * 设置：上传文件。多个文件间有换行分隔，本地文件与远程上传目录间用英文逗号,分隔
     * 
     * @param i_UpFile 上传文件。多个文件间有换行分隔，本地文件与远程上传目录间用英文逗号,分隔
     */
    public void setUpFile(String i_UpFile)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_UpFile) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_UpFile ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.upFilePlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.upFilePlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.upFile = i_UpFile;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：下载文件。多个文件间有换行分隔，远程文件与本地下载目录间用英文逗号,分隔
     */
    public String getDownFile()
    {
        return downFile;
    }
    
    
    
    /**
     * 设置：下载文件。多个文件间有换行分隔，远程文件与本地下载目录间用英文逗号,分隔
     * 
     * @param i_DownFile 下载文件。多个文件间有换行分隔，远程文件与本地下载目录间用英文逗号,分隔
     */
    public void setDownFile(String i_DownFile)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_DownFile) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_DownFile ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.downFilePlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.downFilePlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.downFile = i_DownFile;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Shell.getValue();
    }
    
    
    
    /**
     * 解释上传文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     * 
     * @param io_Context  上下文类型的变量信息
     * 
     * @return
     */
    private List<ShellFile> parserUpFile(Map<String ,Object> i_Context)
    {
        if ( !Help.isNull(this.upFile) )
        {
            String          v_UpFile = ValueHelp.replaceByContext(this.upFile ,this.upFilePlaceholders ,i_Context);
            String          v_Temp   = StringHelp.replaceAll(v_UpFile ,"\r\n" ,"\n");
            String []       v_Files  = StringHelp.split(v_Temp ,"\n");
            List<ShellFile> v_Ret    = new ArrayList<ShellFile>();
            
            for (String v_Item : v_Files)
            {
                if ( Help.isNull(v_Item) )
                {
                    continue;
                }
                
                String [] v_FileAndDir = v_Item.trim().split(",");
                ShellFile v_ShellFile  = new ShellFile(v_FileAndDir[0].trim() ,v_FileAndDir[1].trim());
                File      v_LocalFile  = new File(v_ShellFile.getFile());
                
                if ( !v_LocalFile.exists() )
                {
                    throw new RuntimeException("File[" + v_ShellFile.getFile() + "] is not exists.");
                }
                if ( !v_LocalFile.canRead() )
                {
                    throw new RuntimeException("File[" + v_ShellFile.getFile() + "] can not read.");
                }
                
                v_Ret.add(v_ShellFile);
            }
            
            return v_Ret;
        }
        
        return null;
    }
    
    
    
    /**
     * 解释下载文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     * 
     * @param io_Context  上下文类型的变量信息
     * 
     * @return
     */
    private List<ShellFile> parserDownFile(Map<String ,Object> i_Context)
    {
        if ( !Help.isNull(this.downFile) )
        {
            String          v_DownFile = ValueHelp.replaceByContext(this.downFile ,this.downFilePlaceholders ,i_Context);
            String          v_Temp     = StringHelp.replaceAll(v_DownFile ,"\r\n" ,"\n");
            String []       v_Files    = StringHelp.split(v_Temp ,"\n");
            List<ShellFile> v_Ret      = new ArrayList<ShellFile>();
            
            for (String v_Item : v_Files)
            {
                if ( Help.isNull(v_Item) )
                {
                    continue;
                }
                
                String [] v_FileAndDir = v_Item.trim().split(",");
                ShellFile v_ShellFile  = new ShellFile(v_FileAndDir[0].trim() ,v_FileAndDir[1].trim());
                File      v_LocalDir   = new File(v_ShellFile.getDir());
                
                if ( !v_LocalDir.exists() )
                {
                    v_LocalDir.mkdirs();
                }
                
                v_Ret.add(v_ShellFile);
            }
            
            return v_Ret;
        }
        
        return null;
    }



    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @param i_SuperTreeID  父级执行对象的树ID
     * @param io_Context     上下文类型的变量信息
     * @return
     */
    @Override
    public ExecuteResult execute(String i_SuperTreeID ,Map<String ,Object> io_Context)
    {
        long          v_BeginTime = this.request();
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        if ( Help.isNull(this.host) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s host is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        if ( Help.isNull(this.user) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s user is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        if ( Help.isNull(this.shell) && Help.isNull(this.upFile) && Help.isNull(this.downFile) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s shell、upFile and downFile is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        try (SSHClient v_SSH = new SSHClient())
        {
            if ( !this.handleContext(io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            ShellResult     v_ShellRet = new ShellResult();
            List<ShellFile> v_UpFiles  = this.parserUpFile(io_Context);
            SCPFileTransfer v_SCP      = null;
            
            v_SSH.addHostKeyVerifier(new PromiscuousVerifier());
            v_SSH.connect(this.host);
            
            if ( !Help.isNull(this.password) )
            {
                v_SSH.authPassword(this.user ,this.password);
            }
            else
            {
                String      v_PrivateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";
                KeyProvider v_KeyProvider    = v_SSH.loadKeys(v_PrivateKeyPath);  // 加载私钥（支持无密码或有密码的私钥）
                v_SSH.authPublickey(this.user, v_KeyProvider);                    // 用私钥认证
            }
            
            // 先上传文件
            if ( !Help.isNull(v_UpFiles) )
            {
                // 先创建远程目录
                for (ShellFile v_UpFile : v_UpFiles)
                {
                    try (Session v_Session = v_SSH.startSession()) 
                    {
                        Session.Command v_Command = v_Session.exec("mkdir -p " + v_UpFile.getDir());
                        v_Command.join();
                    }
                }
                
                v_SCP = v_SSH.newSCPFileTransfer();
                for (ShellFile v_UpFile : v_UpFiles)
                {
                    v_SCP.upload(new FileSystemFile(v_UpFile.getFile()) ,v_UpFile.getDir());
                }
                v_ShellRet.setUpFiles(v_UpFiles);
            }
            
            // 再执行Shell脚本代码
            if ( !Help.isNull(this.shell) )
            {
                String v_Shell = ValueHelp.replaceByContext(this.shell ,this.shellPlaceholders ,io_Context);
                try (Session v_Session = v_SSH.startSession()) 
                {
                    Session.Command       v_Command = v_Session.exec(v_Shell);
                    ByteArrayOutputStream v_Output  = new ByteArrayOutputStream();
                    v_Command.getInputStream().transferTo(v_Output);
                    v_Command.join();
                    
                    v_ShellRet.setResult(v_Output.toString().trim());
                    v_ShellRet.setExitStatus(v_Command.getExitStatus());
                }
            }
            
            // 最后下载文件
            if ( !Help.isNull(this.downFile) )
            {
                List<ShellFile> v_DownFiles = this.parserDownFile(io_Context);
                if ( v_SCP == null )
                {
                    v_SCP = v_SSH.newSCPFileTransfer();
                }
                for (ShellFile v_DownFile : v_DownFiles)
                {
                    v_SCP.download(v_DownFile.getFile() ,new FileSystemFile(v_DownFile.getDir()));
                }
                v_ShellRet.setDownFiles(v_DownFiles);
            }
            
            v_Result.setResult(v_ShellRet);
            this.refreshReturn(io_Context ,v_Result.getResult());
            this.refreshStatus(io_Context ,v_Result.getStatus());
            this.success(Date.getTimeNano() - v_BeginTime);
            return v_Result;
        }
        catch (Exception exce)
        {
            v_Result.setException(exce);
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
    }
    
    
    
    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @param i_ExportType   导出类型
     * @return
     */
    @Override
    public String toXml(int i_Level ,String i_SuperTreeID ,ExportType i_ExportType)
    {
        String v_TreeID = this.getTreeID(i_SuperTreeID);
        if ( this.getTreeIDs().size() >= 2 )
        {
            String v_MinTreeID = this.getMinTreeID();
            if ( !v_TreeID.equals(v_MinTreeID) )
            {
                // 不等于最小的树ID，不生成Xml内容。防止重复生成
                return "";
            }
        }
        
        StringBuilder v_Xml      = new StringBuilder();
        String        v_Level1   = "    ";
        String        v_LevelN   = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level ,v_Level1);
        String        v_XName    = ElementType.Shell.getXmlName();
        String        v_NewSpace = "\n" + v_LevelN + v_Level1;
        
        if ( !Help.isNull(this.getXJavaID()) )
        {
            if ( ExportType.UI.equals(i_ExportType) )
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginThis(v_XName ,this.getXJavaID()));
            }
            else
            {
                v_Xml.append("\n").append(v_LevelN).append(IToXml.toBeginID(  v_XName ,this.getXJavaID()));
            }
        }
        else
        {
            v_Xml.append("\n").append(v_LevelN).append(IToXml.toBegin(v_XName));
        }
        
        v_Xml.append(super.toXml(i_Level ,i_ExportType));
        
        if ( !ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.host) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("host" ,this.host));
            }
            if ( !Help.isNull(this.user) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("user" ,this.user));
            }
            if ( !Help.isNull(this.password) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("password" ,this.password));
            }
            if ( !Help.isNull(this.upFile) )
            {
                String v_Classhome = Help.getClassHomePath();
                String v_Webhome   = Help.getWebHomePath();
                String v_UpFile    = this.upFile;
                
                if ( v_UpFile.indexOf(v_Classhome) >= 0 )
                {
                    v_UpFile = StringHelp.replaceAll(v_UpFile ,v_Classhome ,"classhome:");
                }
                
                if ( v_UpFile.indexOf(v_Webhome) >= 0 )
                {
                    v_UpFile = StringHelp.replaceAll(v_UpFile ,v_Webhome ,"webhome:");
                }
                v_Xml.append(v_NewSpace).append(IToXml.toValue("upFile" ,v_UpFile));
            }
            if ( !Help.isNull(this.shell) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("shell" ,this.shell));
            }
            if ( !Help.isNull(this.downFile) )
            {
                String v_Classhome = Help.getClassHomePath();
                String v_Webhome   = Help.getWebHomePath();
                String v_DownFile  = this.downFile;
                
                if ( v_DownFile.indexOf(v_Classhome) >= 0 )
                {
                    v_DownFile = StringHelp.replaceAll(v_DownFile ,v_Classhome ,"classhome:");
                }
                
                if ( v_DownFile.indexOf(v_Webhome) >= 0 )
                {
                    v_DownFile = StringHelp.replaceAll(v_DownFile ,v_Webhome ,"webhome:");
                }
                v_Xml.append(v_NewSpace).append(IToXml.toValue("downFile" ,v_DownFile));
            }
            if ( !Help.isNull(this.charEncoding) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("charEncoding" ,this.charEncoding));
            }
            if ( !Help.isNull(this.returnID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("returnID" ,this.returnID));
            }
            if ( !Help.isNull(this.statusID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("statusID" ,this.statusID));
            }
            
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toBegin("route"));
                
                // 真时的路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,RouteType.Error  .getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("route"));
            }
        }
        
        v_Xml.append("\n").append(v_LevelN).append(IToXml.toEnd(v_XName));
        
        // 编排流图时，提升路由项的层次，同时独立输出每个路由项
        if ( ExportType.UI.equals(i_ExportType) )
        {
            if ( !Help.isNull(this.route.getSucceeds()) 
              || !Help.isNull(this.route.getExceptions()) )
            {
                // 成功路由
                this.toXmlRouteItems(v_Xml ,this.route.getSucceeds()   ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
                // 异常路由
                this.toXmlRouteItems(v_Xml ,this.route.getExceptions() ,ElementType.RouteItem.getXmlName() ,i_Level - 2 ,v_TreeID ,i_ExportType);
            }
        }
        
        return v_Xml.toString();
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("ssh ");
        if ( !Help.isNull(this.user) )
        {
            v_Builder.append(this.user);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("@");
        if ( !Help.isNull(this.host) )
        {
            v_Builder.append(this.host);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" ");
        if ( !Help.isNull(this.shell) )
        {
            String v_Shell = ValueHelp.replaceByContext(this.shell.trim() ,this.shellPlaceholders ,i_Context).trim();
            v_Builder.append(StringHelp.replaceAll(v_Shell ,new String[]{"\r" ,"\n"} ,StringHelp.$ReplaceSpace));
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
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("ssh ");
        if ( !Help.isNull(this.user) )
        {
            v_Builder.append(this.user);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("@");
        if ( !Help.isNull(this.host) )
        {
            v_Builder.append(this.host);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(" ");
        if ( !Help.isNull(this.shell) )
        {
            v_Builder.append(this.shell);
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
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new ShellConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        ShellConfig v_Clone = new ShellConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.host         = this.host;
        v_Clone.user         = this.user;
        v_Clone.password     = this.password;
        v_Clone.shell        = this.shell;
        v_Clone.charEncoding = this.charEncoding;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
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
            throw new NullPointerException("Clone ShellConfig xid is null.");
        }
        
        ShellConfig v_Clone = (ShellConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.host         = this.host;
        v_Clone.user         = this.user;
        v_Clone.password     = this.password;
        v_Clone.shell        = this.shell;
        v_Clone.charEncoding = this.charEncoding;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
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
            throw new NullPointerException("Clone ShellConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        ShellConfig                 v_Clone      = new ShellConfig();
        
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
