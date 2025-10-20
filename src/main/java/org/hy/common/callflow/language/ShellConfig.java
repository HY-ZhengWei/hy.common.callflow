package org.hy.common.callflow.language;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.hy.common.xml.log.Logger;

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
 * 注1：先上传文件，现执行Shell代码，最后下载文件
 * 注2：主机IP、端口、用户、密码、超时时长等属性未设置数据时，当参考对象initXID有值时，从参考对象XID中取值
 * 注3：若参考对象initXID有值时，主机IP、端口、用户、密码、超时时长等属性也有值时，它们优先级高于参考对象initXID
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-09-25
 * @version     v1.0
 *              v1.1  2025-09-30  添加：实时回显脚本执行日志
 *              v2.0  2025-10-20  修正：先handleContext()解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
 */
public class ShellConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(ShellConfig.class ,true);
    
    
    /** 基本连接参数可参考的对象XID */
    private String                        initXID;
    
    /** 主机IP地址。可以是数值、上下文变量、XID标识 */
    private String                        host;
    
    /** 主机端口。可以是数值、上下文变量、XID标识 */
    private String                        port;
    
    /** 连接超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String                        connectTimeout;
    
    /** 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识 */
    private String                        timeout;
    
    /** 用户名称。可以是数值、上下文变量、XID标识 */
    private String                        user;
    
    /** 用户密码。可以是数值、上下文变量、XID标识 */
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
    
    /** 是否实时回显脚本执行日志。默认值：null 不实时回显示 */
    private Boolean                       showLog;
    
    
    
    public ShellConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public ShellConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.port           = "22";
        this.connectTimeout = "0";
        this.timeout        = "0";
        this.showLog        = null;
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
        if ( Help.isNull(this.initXID) )
        {
            if ( Help.isNull(this.getHost()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].host is null.");
                return false;
            }
            if ( Help.isNull(this.getUser()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].user is null.");
                return false;
            }
            if ( Help.isNull(this.getShell()) && Help.isNull(this.getUpFile()) && Help.isNull(this.getDownFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].shell、upFile and downFile is null.");
                return false;
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 获取：基本连接参数可参考的对象XID
     */
    public String getInitXID()
    {
        return ValueHelp.standardRefID(this.initXID);
    }

    
    
    /**
     * 设置：基本连接参数可参考的对象XID
     * 
     * @param i_InitXID 初始化时，基本连接参数与XID保持一致
     */
    public void setInitXID(String i_InitXID)
    {
        // 虽然是引用ID，但为了执行性能，按定义ID处理，在getter方法还原成占位符
        this.initXID = ValueHelp.standardValueID(i_InitXID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    

    /**
     * 按运行时的上下文获取主机IP地址
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private String gatHost(Map<String ,Object> i_Context) throws Exception
    {
        String v_Host = this.host;
        
        if ( Help.isNull(v_Host) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_Host = null;
            }
            else
            {
                v_Host = v_Shell.getHost();
            }
        }
        
        return (String) ValueHelp.getValue(v_Host ,String.class ,"" ,i_Context);
    }
    
    
    
    /**
     * 获取：主机IP地址。可以是数值、上下文变量、XID标识
     */
    public String getHost()
    {
        return host;
    }


    
    /**
     * 设置：主机IP地址。可以是数值、上下文变量、XID标识
     * 
     * @param i_Host 主机IP地址。可以是数值、上下文变量、XID标识
     */
    public void setHost(String i_Host)
    {
        this.host = i_Host;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 按运行时的上下文获取主机端口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Integer gatPort(Map<String ,Object> i_Context) throws Exception
    {
        String v_Port = this.port;
        
        if ( (Help.isNull(v_Port) || "0".equals(v_Port) || "22".equals(v_Port)) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_Port = null;
            }
            else
            {
                v_Port = v_Shell.getPort();
            }
        }
        
        Integer v_PortInt = null;
        if ( Help.isNumber(v_Port) )
        {
            v_PortInt = Integer.valueOf(v_Port);
        }
        else
        {
            v_PortInt = (Integer) ValueHelp.getValue(v_Port ,Integer.class ,0 ,i_Context);
        }
        return v_PortInt;
    }

    
    
    /**
     * 获取：主机端口。可以是数值、上下文变量、XID标识
     */
    public String getPort()
    {
        return port;
    }


    
    /**
     * 设置：主机端口。可以是数值、上下文变量、XID标识
     * 
     * @param i_Port 主机端口。可以是数值、上下文变量、XID标识
     */
    public void setPort(String i_Port)
    {
        if ( Help.isNull(i_Port) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s port is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_Port) )
        {
            Integer v_Timeout = Integer.valueOf(i_Port);
            if ( v_Timeout < 0 )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s port Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            else if ( v_Timeout > 65535 )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s port Greater than 65535.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.port = i_Port.trim();
        }
        else
        {
            this.port = ValueHelp.standardRefID(i_Port);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 从上下文中获取连接时的超时时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Integer gatConnectTimeout(Map<String ,Object> i_Context) throws Exception
    {
        String v_ConnectTimeout = this.connectTimeout;
        
        if ( (Help.isNull(v_ConnectTimeout) || "0".equals(v_ConnectTimeout)) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_ConnectTimeout = null;
            }
            else
            {
                v_ConnectTimeout = v_Shell.getConnectTimeout();
            }
        }
        
        Integer v_ConnectTimeoutInt = null;
        if ( Help.isNumber(v_ConnectTimeout) )
        {
            v_ConnectTimeoutInt = Integer.valueOf(v_ConnectTimeout);
        }
        else
        {
            v_ConnectTimeoutInt = (Integer) ValueHelp.getValue(v_ConnectTimeout ,Integer.class ,0 ,i_Context);
        }
        
        return v_ConnectTimeoutInt;
    }

    
    
    /**
     * 获取：连接超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getConnectTimeout()
    {
        return connectTimeout;
    }

    
    
    /**
     * 设置：连接超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_ConnectTimeout 连接超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setConnectTimeout(String i_ConnectTimeout)
    {
        if ( Help.isNull(i_ConnectTimeout) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s connectTimeout is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_ConnectTimeout) )
        {
            Integer v_Timeout = Integer.valueOf(i_ConnectTimeout);
            if ( v_Timeout < 0 )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s connectTimeout Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.connectTimeout = i_ConnectTimeout.trim();
        }
        else
        {
            this.connectTimeout = ValueHelp.standardRefID(i_ConnectTimeout);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 从上下文中获取运行时的超时时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Integer gatTimeout(Map<String ,Object> i_Context) throws Exception
    {
        String v_Timeout = this.timeout;
        
        if ( (Help.isNull(v_Timeout) || "0".equals(v_Timeout)) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_Timeout = null;
            }
            else
            {
                v_Timeout = v_Shell.getTimeout();
            }
        }
        
        Integer v_TimeoutInt = null;
        if ( Help.isNumber(v_Timeout) )
        {
            v_TimeoutInt = Integer.valueOf(v_Timeout);
        }
        else
        {
            v_TimeoutInt = (Integer) ValueHelp.getValue(v_Timeout ,Integer.class ,0 ,i_Context);
        }
        
        return v_TimeoutInt;
    }

    
    
    /**
     * 获取：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public String getTimeout()
    {
        return timeout;
    }

    
    
    /**
     * 设置：执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     * 
     * @param i_Timeout 执行超时时长（单位：毫秒）。可以是数值、上下文变量、XID标识
     */
    public void setTimeout(String i_Timeout)
    {
        if ( Help.isNull(i_Timeout) )
        {
            NullPointerException v_Exce = new NullPointerException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout is null.");
            $Logger.error(v_Exce);
            throw v_Exce;
        }
        
        if ( Help.isNumber(i_Timeout) )
        {
            Integer v_Timeout = Integer.valueOf(i_Timeout);
            if ( v_Timeout < 0 )
            {
                IllegalArgumentException v_Exce = new IllegalArgumentException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s timeout Less than zero.");
                $Logger.error(v_Exce);
                throw v_Exce;
            }
            this.timeout = i_Timeout.trim();
        }
        else
        {
            this.timeout = ValueHelp.standardRefID(i_Timeout);
        }
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 按运行时的上下文获取用户名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private String gatUser(Map<String ,Object> i_Context) throws Exception
    {
        String v_User = this.user;
        
        if ( Help.isNull(v_User) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_User = null;
            }
            else
            {
                v_User = v_Shell.getUser();
            }
        }
        
        return (String) ValueHelp.getValue(v_User ,String.class ,"" ,i_Context);
    }
    
    
    
    /**
     * 获取：用户名称。可以是数值、上下文变量、XID标识
     */
    public String getUser()
    {
        return user;
    }


    
    /**
     * 设置：用户名称。可以是数值、上下文变量、XID标识
     * 
     * @param i_User 用户名称。可以是数值、上下文变量、XID标识
     */
    public void setUser(String i_User)
    {
        this.user = i_User;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 按运行时的上下文获取用户密码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private String gatPassword(Map<String ,Object> i_Context) throws Exception
    {
        String v_Password = this.password;
        
        if ( Help.isNull(v_Password) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_Password = null;
            }
            else
            {
                v_Password = v_Shell.getPassword();
            }
        }
        
        return (String) ValueHelp.getValue(v_Password ,String.class ,"" ,i_Context);
    }


    
    /**
     * 获取：用户密码。可以是数值、上下文变量、XID标识
     */
    public String getPassword()
    {
        return password;
    }


    
    /**
     * 设置：用户密码。可以是数值、上下文变量、XID标识
     * 
     * @param i_Password 用户密码。可以是数值、上下文变量、XID标识
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
     * 按运行时的上下文获取是否实时回显脚本执行日志
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-30
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Boolean gatShowLog(Map<String ,Object> i_Context) throws Exception
    {
        Boolean v_ShowLog = this.showLog;
        
        if ( Help.isNull(v_ShowLog) && !Help.isNull(this.initXID) )
        {
            ShellConfig v_Shell = (ShellConfig) ValueHelp.getValue(this.getInitXID() ,ShellConfig.class ,null ,i_Context);
            if ( v_Shell == null )
            {
                v_ShowLog = null;
            }
            else
            {
                v_ShowLog = v_Shell.getShowLog();
            }
        }
        
        return v_ShowLog;
    }
    
    
    
    /**
     * 获取：是否实时回显脚本执行日志。默认值：null 不实时回显示
     */
    public Boolean getShowLog()
    {
        return showLog;
    }


    
    /**
     * 设置：是否实时回显脚本执行日志。默认值：null 不实时回显示
     * 
     * @param i_ShowLog 是否实时回显脚本执行日志。默认值：null 不实时回显示
     */
    public void setShowLog(Boolean i_ShowLog)
    {
        this.showLog = i_ShowLog;
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
        Exception     v_ContextEr = this.handleContext(io_Context);  // 先解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
        ExecuteResult v_Result    = new ExecuteResult(CallFlow.getNestingLevel(io_Context) ,this.getTreeID(i_SuperTreeID) ,this.xid ,this.toString(io_Context));
        this.refreshStatus(io_Context ,v_Result.getStatus());
        
        if ( v_ContextEr != null )
        {
            v_Result.setException(v_ContextEr);
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
            ShellResult     v_ShellRet    = new ShellResult();
            List<ShellFile> v_UpFiles     = this.parserUpFile(io_Context);
            SCPFileTransfer v_SCP         = null;
            String          v_Host        = this.gatHost(io_Context);
            Integer         v_Port        = this.gatPort(io_Context);
            String          v_User        = this.gatUser(io_Context);
            String          v_Password    = this.gatPassword(io_Context);
            Integer         v_ConnectTime = this.gatConnectTimeout(io_Context);
            Integer         v_Time        = this.gatTimeout(io_Context);
            Boolean         v_ShowLog     = this.gatShowLog(io_Context);
            
            if ( Help.isNull(v_Host) )
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s host[" + this.host + "] is not find."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            if ( Help.isNull(v_User) )
            {
                v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s user[" + this.user + "] is not find."));
                this.refreshStatus(io_Context ,v_Result.getStatus());
                return v_Result;
            }
            
            if ( v_ConnectTime > 0 )
            {
                v_SSH.setConnectTimeout(v_ConnectTime);
            }
            if ( v_Time > 0 )
            {
                v_SSH.setTimeout(v_Time);
            }
            
            v_SSH.addHostKeyVerifier(new PromiscuousVerifier());
            if ( v_Port > 0 && v_Port != 22 )
            {
                v_SSH.connect(v_Host ,v_Port);
            }
            else
            {
                v_SSH.connect(v_Host);
            }
            
            if ( !Help.isNull(v_Password) )
            {
                v_SSH.authPassword(v_User ,v_Password);
            }
            else
            {
                String      v_PrivateKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa";
                KeyProvider v_KeyProvider    = v_SSH.loadKeys(v_PrivateKeyPath);  // 加载私钥（支持无密码或有密码的私钥）
                v_SSH.authPublickey(v_User, v_KeyProvider);                       // 用私钥认证
            }
            
            // 先上传文件
            if ( !Help.isNull(v_UpFiles) )
            {
                // 先创建远程目录，并且已创建过的，不在重复创建
                Map<String ,Integer> v_MakeDirectoryRets = new HashMap<String ,Integer>();
                for (ShellFile v_UpFile : v_UpFiles)
                {
                    Integer v_MDRet = v_MakeDirectoryRets.get(v_UpFile.getDir());
                    if ( v_MDRet == null )
                    {
                        try (Session v_Session = v_SSH.startSession()) 
                        {
                            try (Session.Command v_Command = v_Session.exec("mkdir -p " + v_UpFile.getDir()))
                            {
                                this.showLog(v_ShowLog ,v_Command);
                                v_Command.join();
                                v_MakeDirectoryRets.put(v_UpFile.getDir() ,v_Command.getExitStatus());
                            }
                        }
                    }
                }
                v_MakeDirectoryRets.clear();
                v_MakeDirectoryRets = null;
                
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
                    try ( Session.Command v_Command = v_Session.exec(v_Shell) )
                    {
                        ByteArrayOutputStream v_Output = null;
                        StringBuilder         v_Log    = this.showLog(v_ShowLog ,v_Command);
                        if ( v_Log == null )
                        {
                            v_Output = new ByteArrayOutputStream();
                            v_Command.getInputStream().transferTo(v_Output);
                            
                        }
                        
                        v_Command.join();
                        v_ShellRet.setExitStatus(v_Command.getExitStatus());
                        
                        if ( v_Log == null )
                        {
                            v_ShellRet.setResult(v_Output.toString().trim());
                            v_Output.close();
                        }
                        else
                        {
                            v_ShellRet.setResult(v_Log.toString());
                        }
                    }
                }
            }
            
            // 最后下载文件
            if ( !Help.isNull(this.downFile) )
            {
                List<ShellFile> v_DownFiles = this.parserDownFile(io_Context);  // 应在此解析下载文件。适配Shell代码动态生成的文件
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
     * 显示脚本执行的实时日志
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-30
     * @version     v1.0
     *
     * @param i_ShowLog  是否实时回显脚本执行日志
     * @param i_Command  脚本执行对象
     * @return           返回日志缓存
     */
    private StringBuilder showLog(Boolean i_ShowLog ,Session.Command i_Command)
    {
        if ( i_ShowLog == null || !i_ShowLog )
        {
            return null;
        }
        
        StringBuilder v_Log = new StringBuilder();
        // 读取标准输出
        new Thread(() -> 
        {
            try ( InputStream v_Input = i_Command.getInputStream(); BufferedReader v_Reader = new BufferedReader(new InputStreamReader(v_Input)) )
            {
                String v_Line = null;
                while ( (v_Line = v_Reader.readLine()) != null )
                {
                    v_Log.append(v_Line).append("\r\n");
                    $Logger.info(this.getXid() + "[INFO] " + v_Line);
                }
            }
            catch (Exception error)
            {
                $Logger.error(error);
            }
        }).start();
        
        // 读取错误输出
        new Thread(() -> 
        {
            try ( InputStream v_ErrorInput = i_Command.getErrorStream(); BufferedReader v_Reader = new BufferedReader(new InputStreamReader(v_ErrorInput)) )
            {
                String v_Line = null;
                while ( (v_Line = v_Reader.readLine()) != null )
                {
                    v_Log.append(v_Line).append("\r\n");
                    $Logger.info(this.getXid() + "[WARN] " + v_Line);
                }
            }
            catch (Exception error)
            {
                $Logger.error(error);
            }
        }).start();
        
        return v_Log;
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
            if ( !Help.isNull(this.initXID) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("initXID" ,this.getInitXID()));
            }
            if ( !Help.isNull(this.host) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("host" ,this.host));
            }
            if ( !Help.isNull(this.port) && !"0".equals(this.port) && !"22".equals(this.port) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("port" ,this.port));
            }
            if ( !Help.isNull(this.connectTimeout) && !"0".equals(this.connectTimeout) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("connectTimeout" ,this.connectTimeout));
            }
            if ( !Help.isNull(this.timeout) && !"0".equals(this.timeout) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("timeout" ,this.timeout));
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
            if ( !Help.isNull(this.showLog) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("showLog" ,this.showLog));
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
            
            this.toXmlExecute(v_Xml ,v_NewSpace);
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
        String        v_User    = this.user;
        String        v_Host    = this.host;
        String        v_Port    = this.port;
        
        try
        {
            v_User = this.gatUser(i_Context);
            v_Host = this.gatHost(i_Context);
            v_Port = this.gatPort(i_Context) + "";
        }
        catch (Exception exce)
        {
            $Logger.error("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "].toString is error" ,exce);
        }
        
        v_Builder.append("ssh ");
        if ( !Help.isNull(v_User) )
        {
            v_Builder.append(v_User);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append("@");
        if ( !Help.isNull(v_Host) )
        {
            v_Builder.append(v_Host);
        }
        else
        {
            v_Builder.append("?");
        }
        
        v_Builder.append(":");
        if ( !Help.isNull(v_Port) )
        {
            v_Builder.append(v_Port);
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
        
        v_Builder.append(":");
        if ( !Help.isNull(this.port) )
        {
            v_Builder.append(this.port);
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
        v_Clone.initXID        = this.initXID;
        v_Clone.host           = this.host;
        v_Clone.port           = this.port;
        v_Clone.connectTimeout = this.connectTimeout;
        v_Clone.timeout        = this.timeout;
        v_Clone.user           = this.user;
        v_Clone.password       = this.password;
        v_Clone.shell          = this.shell;
        v_Clone.upFile         = this.upFile;
        v_Clone.downFile       = this.downFile;
        v_Clone.showLog        = this.showLog;
        
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
        
        v_Clone.initXID        = this.initXID;
        v_Clone.host           = this.host;
        v_Clone.port           = this.port;
        v_Clone.connectTimeout = this.connectTimeout;
        v_Clone.timeout        = this.timeout;
        v_Clone.user           = this.user;
        v_Clone.password       = this.password;
        v_Clone.shell          = this.shell;
        v_Clone.upFile         = this.upFile;
        v_Clone.downFile       = this.downFile;
        v_Clone.showLog        = this.showLog;
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
