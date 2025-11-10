package org.hy.common.callflow.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.db.DBSQL;





/**
 * 命令元素：执行操作系统命令的元素。衍生于执行元素。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2025-08-09
 * @version     v1.0
 *              v2.0  2025-09-26  迁移：静态检查
 */
public class CommandConfig extends NodeConfig implements NodeConfigBase
{
    
    /** 执行命令。可以是常量、上下文变量、XID标识，并且支持多个占位符 */
    private String                        command;
    
    /** 执行命令，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> commandPlaceholders;
    
    /** 是否等待命令执行完成。当等待时调用线程将被阻塞。默认为：true */
    private Boolean                       waitProcess;
    
    /** 命令结果返回时字符集。默认为：UTF-8 */
    private String                        charEncoding;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
     * @version     v1.0
     *
     */
    public CommandConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public CommandConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setWaitProcess(true);
        this.setCharEncoding("UTF-8");
        
        this.setCallMethod("executeCommand");
        
        NodeParam v_CallParam = new NodeParam();
        v_CallParam.setValueClass(Long.class.getName());
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
        
        v_CallParam = new NodeParam();
        v_CallParam.setValueClass(String.class.getName());
        v_CallParam.setValue("");
        this.setCallParam(v_CallParam);
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
        
        if ( Help.isNull(this.getCommand()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].command is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-07
     * @version     v1.0
     *
     * @param io_Context   上下文类型的变量信息
     * @param i_BeginTime  编排元素的开始时间
     * @param io_Result    编排元素的执行结果
     * @return             表示是否有模拟数据
     */
    public boolean mock(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result) 
    {
        return super.mockRows(io_Context ,i_BeginTime ,io_Result ,"\n");
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
        return "XCMD_" + StringHelp.getUUID9n();
    }
    
    
    
    /**
     * 获取：执行命令。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public String getCommand()
    {
        return this.command;
    }


    
    /**
     * 设置：执行命令。可以是常量、上下文变量、XID标识，并且支持多个占位符
     * 
     * @param i_Command 执行命令。可以是常量、上下文变量、XID标识，并且支持多个占位符
     */
    public void setCommand(String i_Command)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_Command) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_Command ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.commandPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.commandPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.command = i_Command;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：是否等待命令执行完成。当等待时调用线程将被阻塞。默认为：true
     */
    public Boolean getWaitProcess()
    {
        return waitProcess;
    }


    
    /**
     * 设置：是否等待命令执行完成。当等待时调用线程将被阻塞。默认为：true
     * 
     * @param i_WaitProcess 是否等待命令执行完成。当等待时调用线程将被阻塞。默认为：true
     */
    public void setWaitProcess(Boolean i_WaitProcess)
    {
        this.waitProcess = i_WaitProcess;
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
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Command.getValue();
    }
    
    
    
    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return ElementType.Command.getXmlName();
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
     * @createDate  2025-08-09
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
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @param io_Context  上下文类型的变量信息
     * @param io_Params   方法执行参数。已用NodeConfig自己的力量生成了执行参数。
     * @return
     * @throws Exception 
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        if ( !Help.isNull(this.timeout) )
        {
            try
            {
                io_Params[0] = ValueHelp.getValue(this.timeout ,Long.class ,0L ,io_Context);
            }
            catch (Exception exce)
            {
                throw new RuntimeException(this.getXid() + " timeout(" + this.timeout + ") error" ,exce);
            }
        }
        if ( !Help.isNull(this.command) )
        {
            io_Params[1] = ValueHelp.replaceByContext(this.command ,this.commandPlaceholders ,io_Context);
        }
        
        return io_Params;
    }
    
    
    
    /**
     * 向客户端群发消息。
     * 
     * 首次接入的客户端，将发送全部消息，之后将只发有变化的消息
     * 
     * @param i_Timeout    超时时长。超时后执行将被结束。（单位：微秒，须被转为秒）
     * @param i_Commands   执行命令
     */
    public List<String> executeCommand(Long i_Timeout ,String i_Command)
    {
        if ( Help.isNull(i_Command) )
        {
            throw new NullPointerException("Command is null.");
        }
        
        long v_Timeout = Help.NVL(i_Timeout ,0L) / 1000L;
        if ( v_Timeout < 0L )
        {
            v_Timeout = 0L;
        }
        
        List<String> v_CmdRet = Help.executeCommand(Help.NVL(this.charEncoding ,"UTF-8") 
                                                   ,Help.NVL(this.waitProcess ,true)
                                                   ,!Help.isNull(this.getReturnID())  
                                                   ,v_Timeout
                                                   ,i_Command.trim());
        
        return v_CmdRet;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
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
        if ( !Help.isNull(this.getCommand()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("command"      ,this.getCommand()));
        }
        if ( !Help.isNull(this.getWaitProcess()) && !this.getWaitProcess() )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("waitProcess"  ,this.getWaitProcess()));
        }
        if ( !Help.isNull(this.getCharEncoding()) && !"UTF-8".equalsIgnoreCase(this.getCharEncoding()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("charEncoding" ,this.getCharEncoding()));
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
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.getCommand()) )
        {
            try
            {
                String v_Command = ValueHelp.replaceByContext(this.command ,this.commandPlaceholders ,i_Context);
                if ( Help.isNull(v_Command) )
                {
                    v_Builder.append("?");
                }
                else
                {
                    v_Builder.append(v_Command);
                }
                
            }
            catch (Exception exce)
            {
                v_Builder.append(exce.getMessage());
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
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.getCommand()) )
        {
            v_Builder.append(this.getCommand());
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
     * @createDate  2025-08-09
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new CommandConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        CommandConfig v_Clone = new CommandConfig();
        
        this.cloneMyOnly(v_Clone);
        // v_Clone.callXID = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod = this.callMethod; 
        v_Clone.timeout    = this.timeout;
        
        v_Clone.setComment(     this.getComment());
        v_Clone.setWaitProcess( this.getWaitProcess());
        v_Clone.setCharEncoding(this.getCharEncoding());
        
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
     * @createDate  2025-08-09
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
            throw new NullPointerException("Clone CommandConfig xid is null.");
        }
        
        CommandConfig v_Clone = (CommandConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.callMethod = this.callMethod; 
        v_Clone.timeout    = this.timeout;
        
        v_Clone.setComment(     this.getComment());
        v_Clone.setWaitProcess( this.getWaitProcess());
        v_Clone.setCharEncoding(this.getCharEncoding());
        
        if ( !Help.isNull(this.callParams) )
        {
            v_Clone.callParams = new ArrayList<NodeParam>();
            for (NodeParam v_NodeParam : this.callParams)
            {
                NodeParam v_CloneNodeParam = new NodeParam();
                v_NodeParam.clone(v_CloneNodeParam ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.callParams.add(v_CloneNodeParam);
            }
        }
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-09
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
            throw new NullPointerException("Clone CommandConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        CommandConfig               v_Clone      = new CommandConfig();
        
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
