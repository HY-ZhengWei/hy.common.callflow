package org.hy.common.callflow.ftp;

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
import org.hy.common.db.DBSQL;
import org.hy.common.file.FileHelp;
import org.hy.common.ftp.FTPHelp;
import org.hy.common.ftp.FTPInfo;
import org.hy.common.ftp.FTPPath;
import org.hy.common.ftp.enums.PathType;
import org.hy.common.xml.log.Logger;





/**
 * FTP文传元素：File Transfer Protocol（文件传输协议）
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-10-19
 * @version     v1.0
 */
public class FtpConfig extends ExecuteElement implements Cloneable
{
    
    private static final Logger $Logger = new Logger(FtpConfig.class ,true);
    
    
    
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
    
    /** 连接成功后的初始化目录 */
    private String                        initPath;
    
    /** 本地是否被动模式 */
    private String                        localPassiveMode;
    
    /** 远程服务是否被动模式 */
    private String                        remotePassiveMode;
    
    /** 字符集（可解决中文路径问题） */
    private String                        charEncoding;
    
    /** 上传文件。多个文件间有换行分隔，本地文件与远程上传目录间用英文逗号,分隔 */
    private String                        upFile;
    
    /** 上传文件，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> upFilePlaceholders;
    
    /** 下载文件。多个文件间有换行分隔，远程文件与本地下载目录间用英文逗号,分隔 */
    private String                        downFile;
    
    /** 下载文件，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> downFilePlaceholders;
    
    
    
    public FtpConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public FtpConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.port              = "21";
        this.connectTimeout    = "0";
        this.timeout           = "0";
        this.localPassiveMode  = "false";
        this.remotePassiveMode = "false";
        this.charEncoding      = "UTF-8";
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
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
            if ( Help.isNull(this.getUpFile()) && Help.isNull(this.getDownFile()) )
            {
                io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].upFile and downFile is null.");
                return false;
            }
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
        return "XFTP_" + StringHelp.getUUID9n();
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
     * @createDate  2025-10-19
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
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_Host = null;
            }
            else
            {
                v_Host = v_Ftp.getHost();
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
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Integer gatPort(Map<String ,Object> i_Context) throws Exception
    {
        String v_Port = this.port;
        
        if ( (Help.isNull(v_Port) || "0".equals(v_Port) || "21".equals(v_Port)) && !Help.isNull(this.initXID) )
        {
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_Port = null;
            }
            else
            {
                v_Port = v_Ftp.getPort();
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
     * @createDate  2025-10-19
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
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_ConnectTimeout = null;
            }
            else
            {
                v_ConnectTimeout = v_Ftp.getConnectTimeout();
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
     * @createDate  2025-10-19
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
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_Timeout = null;
            }
            else
            {
                v_Timeout = v_Ftp.getTimeout();
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
     * @createDate  2025-10-19
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
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_User = null;
            }
            else
            {
                v_User = v_Ftp.getUser();
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
     * @createDate  2025-10-19
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
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_Password = null;
            }
            else
            {
                v_Password = v_Ftp.getPassword();
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
     * 连接成功后的初始化目录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private String gatInitPath(Map<String ,Object> i_Context) throws Exception
    {
        String v_InitPath = this.initPath;
        
        if ( Help.isNull(v_InitPath) && !Help.isNull(this.initXID) )
        {
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_InitPath = null;
            }
            else
            {
                v_InitPath = v_Ftp.getInitPath();
            }
        }
        
        return (String) ValueHelp.getValue(v_InitPath ,String.class ,"" ,i_Context);
    }
    
    
    
    /**
     * 获取：连接成功后的初始化目录
     */
    public String getInitPath()
    {
        return initPath;
    }


    
    /**
     * 设置：连接成功后的初始化目录
     * 
     * @param i_InitPath 连接成功后的初始化目录
     */
    public void setInitPath(String i_InitPath)
    {
        this.initPath = i_InitPath;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 本地是否被动模式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Boolean gatLocalPassiveMode(Map<String ,Object> i_Context) throws Exception
    {
        String v_LocalPassiveMode = this.localPassiveMode;
        
        if ( (Help.isNull(v_LocalPassiveMode) || "false".equals(v_LocalPassiveMode)) && !Help.isNull(this.initXID) )
        {
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_LocalPassiveMode = null;
            }
            else
            {
                v_LocalPassiveMode = v_Ftp.getLocalPassiveMode();
            }
        }
        
        return (Boolean) ValueHelp.getValue(v_LocalPassiveMode ,Boolean.class ,"false" ,i_Context);
    }


    
    /**
     * 获取：本地是否被动模式
     */
    public String getLocalPassiveMode()
    {
        return localPassiveMode;
    }


    
    /**
     * 设置：本地是否被动模式
     * 
     * @param i_LocalPassiveMode 本地是否被动模式
     */
    public void setLocalPassiveMode(String i_LocalPassiveMode)
    {
        this.localPassiveMode = i_LocalPassiveMode;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 远程服务是否被动模式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     * @throws Exception 
     */
    private Boolean gatRemotePassiveMode(Map<String ,Object> i_Context) throws Exception
    {
        String v_RemotePassiveMode = this.remotePassiveMode;
        
        if ( (Help.isNull(v_RemotePassiveMode) || "false".equals(v_RemotePassiveMode)) && !Help.isNull(this.initXID) )
        {
            FtpConfig v_Ftp = (FtpConfig) ValueHelp.getValue(this.getInitXID() ,FtpConfig.class ,null ,i_Context);
            if ( v_Ftp == null )
            {
                v_RemotePassiveMode = null;
            }
            else
            {
                v_RemotePassiveMode = v_Ftp.getRemotePassiveMode();
            }
        }
        
        return (Boolean) ValueHelp.getValue(v_RemotePassiveMode ,Boolean.class ,"false" ,i_Context);
    }


    
    /**
     * 获取：远程服务是否被动模式
     */
    public String getRemotePassiveMode()
    {
        return remotePassiveMode;
    }


    
    /**
     * 设置：远程服务是否被动模式
     * 
     * @param i_RemotePassiveMode 远程服务是否被动模式
     */
    public void setRemotePassiveMode(String i_RemotePassiveMode)
    {
        this.remotePassiveMode = i_RemotePassiveMode;
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
     * 获取：字符集（可解决中文路径问题）
     */
    public String getCharEncoding()
    {
        return charEncoding;
    }


    
    /**
     * 设置：字符集（可解决中文路径问题）
     * 
     * @param i_CharEncoding 字符集（可解决中文路径问题）
     */
    public void setCharEncoding(String i_CharEncoding)
    {
        this.charEncoding = i_CharEncoding;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Ftp.getValue();
    }
    
    
    
    /**
     * 解释上传文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     * 
     * @param io_Context  上下文类型的变量信息
     * @param i_FTPHelp   FTP操作的辅助类
     * 
     * @return
     */
    private List<FtpFile> parserUpFile(Map<String ,Object> i_Context ,FTPHelp i_FTPHelp)
    {
        if ( !Help.isNull(this.upFile) )
        {
            String        v_UpFile = ValueHelp.replaceByContext(this.upFile ,this.upFilePlaceholders ,i_Context);
            String        v_Temp   = StringHelp.replaceAll(v_UpFile ,"\r\n" ,"\n");
            String []     v_Files  = StringHelp.split(v_Temp ,"\n");
            List<FtpFile> v_Ret    = new ArrayList<FtpFile>();
            FileHelp      v_FHelp  = new FileHelp();
            
            // 先解析用户定义的每一行
            for (String v_Item : v_Files)
            {
                if ( Help.isNull(v_Item) )
                {
                    continue;
                }
                
                String [] v_FileAndDir = v_Item.trim().split(",");
                if ( v_FileAndDir.length != 2 || Help.isNull(v_FileAndDir[0]) || Help.isNull(v_FileAndDir[1]) )
                {
                    $Logger.warn("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] UP Invalid configuration[" + v_Item + "].");
                    continue;
                }
                
                String  v_SourceFile = v_FileAndDir[0].trim();
                String  v_TargetFile = v_FileAndDir[1].trim();
                
                v_SourceFile = StringHelp.replaceAll(v_SourceFile ,"\\" ,"/");
                v_TargetFile = StringHelp.replaceAll(v_TargetFile ,"\\" ,"/");
                
                File v_SourceLocal = new File(v_SourceFile);
                if ( !v_SourceLocal.exists() )
                {
                    throw new RuntimeException("File[" + v_SourceFile + "] is not exists.");
                }
                if ( !v_SourceLocal.canRead() )
                {
                    throw new RuntimeException("File[" + v_SourceFile + "] can not read.");
                }
                
                if ( v_SourceLocal.isFile() )
                {
                    PathType v_TargetType = i_FTPHelp.getPathType(v_TargetFile);
                    if ( PathType.Directory.equals(v_TargetType) )
                    {
                        v_TargetFile += v_SourceLocal.getName();
                        v_Ret.add(new FtpFile(v_SourceFile ,v_TargetFile));
                    }
                    else if ( PathType.File.equals(v_TargetType) )
                    {
                        v_Ret.add(new FtpFile(v_SourceFile ,v_TargetFile));
                    }
                    else if ( PathType.NotExist.equals(v_TargetType) )
                    {
                        v_Ret.add(new FtpFile(v_SourceFile ,v_TargetFile));
                    }
                    else
                    {
                        $Logger.warn("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] UP [" + v_SourceFile + "] is not File and Directory.");
                    }
                }
                // 遍历本地目录后，细化为每个文件
                else if ( v_SourceLocal.isDirectory() )
                {
                    if ( !v_TargetFile.endsWith("/") )
                    {
                        v_TargetFile += "/";
                    }
                    
                    List<File> v_ChildFiles = v_FHelp.getFiles(v_SourceFile ,false);
                    if ( !Help.isNull(v_ChildFiles) )
                    {
                        for (File v_ChildFile : v_ChildFiles)
                        {
                            String v_ChildFullName = v_ChildFile.getPath();
                            String v_TargetDir     = StringHelp.replaceFirst(StringHelp.replaceAll(v_ChildFullName ,"\\" ,"/") ,v_SourceFile ,"");
                            
                            if ( v_TargetDir.startsWith("/") )
                            {
                                v_TargetDir = v_TargetDir.substring(1);
                            }
                            
                            v_TargetDir = v_TargetFile + v_TargetDir;
                            v_Ret.add(new FtpFile(v_ChildFullName ,v_TargetDir));
                        }
                    }
                }
            }
            
            return v_Ret;
        }
        
        return null;
    }
    
    
    
    /**
     * 解释下载文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     * 
     * @param io_Context  上下文类型的变量信息
     * @param i_FTPHelp   FTP操作的辅助类
     * 
     * @return
     */
    private List<FtpFile> parserDownFile(Map<String ,Object> i_Context ,FTPHelp i_FTPHelp)
    {
        if ( !Help.isNull(this.downFile) )
        {
            String               v_DownFile = ValueHelp.replaceByContext(this.downFile ,this.downFilePlaceholders ,i_Context);
            String               v_Temp     = StringHelp.replaceAll(v_DownFile ,"\r\n" ,"\n");
            String []            v_Files    = StringHelp.split(v_Temp ,"\n");
            List<FtpFile>        v_Ret      = new ArrayList<FtpFile>();
            Map<String ,Integer> v_MakeDirs = new HashMap<String ,Integer>();
            
            for (String v_Item : v_Files)
            {
                if ( Help.isNull(v_Item) )
                {
                    continue;
                }
                
                String [] v_FileAndDir = v_Item.trim().split(",");
                if ( v_FileAndDir.length != 2 || Help.isNull(v_FileAndDir[0]) || Help.isNull(v_FileAndDir[1]) )
                {
                    $Logger.warn("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] DOWN Invalid configuration[" + v_Item + "].");
                    continue;
                }
                
                String v_SourceFile = v_FileAndDir[0].trim();         // 远程文件（下载时）
                String v_TargetFile = v_FileAndDir[1].trim();         // 本地文件（下载时）
                
                v_SourceFile = StringHelp.replaceAll(v_SourceFile ,"\\" ,"/");
                v_TargetFile = StringHelp.replaceAll(v_TargetFile ,"\\" ,"/");
                
                PathType v_SourceType = i_FTPHelp.getPathType(v_SourceFile);
                if ( PathType.Directory.equals(v_SourceType) )
                {
                    if ( !v_SourceFile.endsWith("/") )
                    {
                        v_SourceFile += "/";
                    }
                    if ( !v_TargetFile.endsWith("/") )
                    {
                        v_TargetFile += "/";
                    }
                    
                    List<FTPPath> v_SourcePaths = i_FTPHelp.getFiles(v_SourceFile ,false);
                    if ( !Help.isNull(v_SourcePaths) )
                    {
                        int v_SourceRootLen = v_SourceFile.length();
                        for (FTPPath v_FTPPath : v_SourcePaths)
                        {
                            // 仅要相对路径，也不要文件名称，掐头取尾
                            String  v_RelativePath = v_FTPPath.getPathName().substring(v_SourceRootLen ,v_FTPPath.getPathName().length() - v_FTPPath.getSimpleName().length());
                            String  v_TargetDir    = v_TargetFile + v_RelativePath;
                            FtpFile v_FtpFile      = new FtpFile(v_FTPPath.getPathName() ,v_TargetDir + v_FTPPath.getSimpleName());
                            v_Ret.add(v_FtpFile);
                            
                            if ( !v_MakeDirs.containsKey(v_TargetDir) )
                            {
                                // 自动创建目标目录
                                File v_TargetDirFile = new File(v_TargetDir);
                                if ( !v_TargetDirFile.exists() )
                                {
                                    v_TargetDirFile.mkdirs();
                                    v_MakeDirs.put(v_TargetDir ,1);
                                }
                                else
                                {
                                    v_MakeDirs.put(v_TargetDir ,0);
                                }
                            }
                        }
                    }
                }
                else if ( PathType.File.equals(v_SourceType) )
                {
                    int    v_Index      = v_SourceFile.lastIndexOf("/");
                    String v_SourceName = v_SourceFile;
                    if ( v_Index >= 0 )
                    {
                        v_SourceName = v_SourceFile.substring(v_Index + 1);
                    }
                    
                    if ( !v_MakeDirs.containsKey(v_TargetFile) )
                    {
                        File v_TargetDirFile = new File(v_TargetFile);
                        if ( !v_TargetDirFile.exists() )
                        {
                            // 以 / 结尾时，当目录处理
                            if ( v_TargetFile.endsWith("/") )
                            {
                                // 自动创建目标目录
                                v_TargetDirFile.mkdirs();
                                v_MakeDirs.put(v_TargetFile ,1);
                                
                                v_TargetFile += v_SourceName;
                            }
                            // 否则当文件处理
                            else
                            {
                                v_Index         = v_TargetFile.lastIndexOf("/");
                                v_TargetDirFile = new File(v_TargetFile.substring(0 ,v_Index));
                                if ( !v_TargetDirFile.exists() )
                                {
                                    // 自动创建目标目录
                                    v_TargetDirFile.mkdirs();
                                    v_MakeDirs.put(v_TargetDirFile.getPath() ,1);
                                }
                                else
                                {
                                    v_MakeDirs.put(v_TargetDirFile.getPath() ,0);
                                }
                            }
                        }
                        else if ( v_TargetDirFile.isDirectory() )
                        {
                            v_MakeDirs.put(v_TargetFile ,0);
                            v_TargetFile += v_SourceName;
                        }
                    }
                    else
                    {
                        // 之前已创建过目录
                        v_TargetFile += v_SourceName;
                    }
                    
                    FtpFile v_FtpFile = new FtpFile(v_SourceFile ,v_TargetFile);
                    v_Ret.add(v_FtpFile);
                }
                else
                {
                    $Logger.warn("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] DOWN [" + v_SourceFile + "] is not File and Directory.");
                }
            }
            
            v_MakeDirs.clear();
            v_MakeDirs = null;
            return v_Ret;
        }
        
        return null;
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
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
        
        if ( Help.isNull(this.upFile) && Help.isNull(this.downFile) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s upFile and downFile is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        // 编排整体二次重做
        if ( !this.redo(io_Context ,v_BeginTime ,v_Result) )
        {
            return v_Result;
        }
        
        // Mock模拟
        if ( super.mock(io_Context ,v_BeginTime ,v_Result ,null ,FtpResult.class.getName()) )
        {
            return v_Result;
        }
        
        try
        {
            FTPInfo       v_FTPInfo           = new FTPInfo();
            FtpResult     v_FtpRet            = new FtpResult();
            String        v_Host              = this.gatHost(io_Context);
            Integer       v_Port              = this.gatPort(io_Context);
            String        v_User              = this.gatUser(io_Context);
            String        v_Password          = this.gatPassword(io_Context);
            Integer       v_ConnectTime       = this.gatConnectTimeout(io_Context);
            Integer       v_Time              = this.gatTimeout(io_Context);
            String        v_InitPath          = this.gatInitPath(io_Context);
            Boolean       v_LocalPassiveMode  = this.gatLocalPassiveMode(io_Context);
            Boolean       v_RemotePassiveMode = this.gatRemotePassiveMode(io_Context);
            
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
            
            v_FTPInfo.setIp(v_Host);
            v_FTPInfo.setUser(v_User);
            
            if ( v_ConnectTime > 0 )
            {
                v_FTPInfo.setConnectTimeout(v_ConnectTime);
            }
            if ( v_Time > 0 )
            {
                v_FTPInfo.setDataTimeoutMillis(v_Time);
            }
            if ( v_Port > 0 && v_Port != 21 )
            {
                v_FTPInfo.setPort(v_Port);
            }
            if ( !Help.isNull(v_User) )
            {
                v_FTPInfo.setUser(v_User);
            }
            if ( !Help.isNull(v_Password) )
            {
                v_FTPInfo.setPassword(v_Password);
            }
            if ( !Help.isNull(v_InitPath) )
            {
                v_FTPInfo.setInitPath(v_InitPath);
            }
            if ( !Help.isNull(v_LocalPassiveMode) )
            {
                v_FTPInfo.setLocalPassiveMode(v_LocalPassiveMode);
            }
            if ( !Help.isNull(v_RemotePassiveMode) )
            {
                v_FTPInfo.setRemotePassiveMode(v_RemotePassiveMode);
            }
            v_FTPInfo.setControlEncoding(Help.NVL(this.charEncoding ,"UTF-8"));
            
            try (FTPHelp v_FTPHelp = new FTPHelp(v_FTPInfo))
            {
                v_FTPHelp.connect();
                
                // 先上传文件
                List<FtpFile> v_UpFiles = this.parserUpFile(io_Context ,v_FTPHelp);
                if ( !Help.isNull(v_UpFiles) )
                {
                    int                  v_Len               = (v_UpFiles.size() + "").length();
                    int                  v_Index             = 0;
                    Map<String ,Integer> v_MakeDirectoryRets = new HashMap<String ,Integer>();
                    for (FtpFile v_UpFile : v_UpFiles)
                    {
                        String v_UpFileRet     = v_FTPHelp.upload(v_UpFile.getSource() ,v_UpFile.getTarget() ,false);
                        String v_UpFileRetFlag = "成功 "; 
                        if ( !Help.isNull(v_UpFileRet) )
                        {
                            v_UpFile.setErrorInfo(v_UpFileRet);
                            v_UpFileRetFlag = "异常 ";
                        }
                        else
                        {
                            v_FtpRet.setUpSucceed(v_FtpRet.getUpSucceed() + 1);
                        }
                        
                        $Logger.info("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] " + StringHelp.rpad(++v_Index ,v_Len ," ") + " UP " + v_UpFileRetFlag + v_UpFile.getTarget());
                    }
                    v_MakeDirectoryRets.clear();
                    v_MakeDirectoryRets = null;
                    v_FtpRet.setUpFiles(v_UpFiles);
                }
                
                // 最后下载文件
                if ( !Help.isNull(this.downFile) )
                {
                    List<FtpFile> v_DownFiles = this.parserDownFile(io_Context ,v_FTPHelp);   // 应在此解析下载文件。适配先上传后下载上传的文件
                    int           v_Len       = (v_DownFiles.size() + "").length();
                    int           v_Index     = 0;
                    for (FtpFile v_DownFile : v_DownFiles)
                    {
                        String v_DownFileRet     = v_FTPHelp.download(v_DownFile.getSource() ,v_DownFile.getTarget());
                        String v_DownFileRetFlag = "成功 "; 
                        if ( !Help.isNull(v_DownFileRet) )
                        {
                            v_DownFile.setErrorInfo(v_DownFileRet);
                            v_DownFileRetFlag = "异常 ";
                        }
                        else
                        {
                            v_FtpRet.setDownSucceed(v_FtpRet.getDownSucceed() + 1);
                        }
                        
                        $Logger.info("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "] " + StringHelp.rpad(++v_Index ,v_Len ," ") + " DOWN " + v_DownFileRetFlag + v_DownFile.getTarget());
                    }
                    v_FtpRet.setDownFiles(v_DownFiles);
                }
            }
            
            v_Result.setResult(v_FtpRet);
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
     * @createDate  2025-10-19
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
        String        v_XName    = ElementType.Ftp.getXmlName();
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
            if ( !Help.isNull(this.port) && !"0".equals(this.port) && !"21".equals(this.port) )
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
            if ( !Help.isNull(this.initPath) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("initPath" ,this.initPath));
            }
            if ( !Help.isNull(this.charEncoding) && !"UTF-8".equalsIgnoreCase(this.charEncoding) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("charEncoding" ,this.charEncoding));
            }
            if ( !Help.isNull(this.localPassiveMode) && !"false".equalsIgnoreCase(this.localPassiveMode) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("localPassiveMode" ,this.localPassiveMode));
            }
            if ( !Help.isNull(this.remotePassiveMode) && !"false".equalsIgnoreCase(this.remotePassiveMode) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("remotePassiveMode" ,this.remotePassiveMode));
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
            
            // 模拟数据
            if ( !Help.isNull(this.mock.getSucceeds()) 
              || !Help.isNull(this.mock.getExceptions()) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toBegin("mock"));
                if ( this.mock.isValid() )
                {
                    v_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toValue("valid" ,"true"));
                }
                if ( !Help.isNull(this.mock.getDataClass()) )
                {
                    v_Xml.append(v_NewSpace).append(v_Level1).append(IToXml.toValue("dataClass" ,this.mock.getDataClass()));
                }
                this.toXmlMockItems(v_Xml ,this.mock.getSucceeds()   ,RouteType.Succeed.getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                this.toXmlMockItems(v_Xml ,this.mock.getExceptions() ,RouteType.Error  .getXmlName() ,i_Level ,v_TreeID ,i_ExportType);
                v_Xml.append(v_NewSpace).append(IToXml.toEnd("mock"));
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
     * @createDate  2025-10-19
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
        
        v_Builder.append("ftp ");
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
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
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
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new FtpConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        FtpConfig v_Clone = new FtpConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.initXID           = this.initXID;
        v_Clone.host              = this.host;
        v_Clone.port              = this.port;
        v_Clone.connectTimeout    = this.connectTimeout;
        v_Clone.timeout           = this.timeout;
        v_Clone.user              = this.user;
        v_Clone.password          = this.password;
        v_Clone.initPath          = this.initPath;
        v_Clone.localPassiveMode  = this.localPassiveMode;
        v_Clone.remotePassiveMode = this.remotePassiveMode;
        v_Clone.charEncoding      = this.charEncoding;
        v_Clone.upFile            = this.upFile;
        v_Clone.downFile          = this.downFile;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
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
            throw new NullPointerException("Clone FtpConfig xid is null.");
        }
        
        FtpConfig v_Clone = (FtpConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.initXID           = this.initXID;
        v_Clone.host              = this.host;
        v_Clone.port              = this.port;
        v_Clone.connectTimeout    = this.connectTimeout;
        v_Clone.timeout           = this.timeout;
        v_Clone.user              = this.user;
        v_Clone.password          = this.password;
        v_Clone.initPath          = this.initPath;
        v_Clone.localPassiveMode  = this.localPassiveMode;
        v_Clone.remotePassiveMode = this.remotePassiveMode;
        v_Clone.charEncoding      = this.charEncoding;
        v_Clone.upFile            = this.upFile;
        v_Clone.downFile          = this.downFile;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-19
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
            throw new NullPointerException("Clone FtpConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        FtpConfig                   v_Clone      = new FtpConfig();
        
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
