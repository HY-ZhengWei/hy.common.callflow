package org.hy.common.callflow.language;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.hy.common.ByteHelp;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;





/**
 * 酷语元素：在Java中嵌入Groovy代码
 * 
 * 注1：如果没有out时，仅返回new HashMap()
 * 注2：如果有out时，返回从Groovy获取的变量，数据类型也为Map
 * 注3：向Groovy传参后，先执行Groovy脚本文件，后执行Groovy代码
 * 
 * 安全策略如下：
 *     禁止：执行系统命令
 *     禁止：文件读取
 *     禁止：文件写入
 *     禁止：网络连接
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-24
 * @version     v1.0
 *              v1.1  2025-09-25  修正：解析后的脚本，不能在不同的GroovyShell中使用，Script对象是与特定的GroovyShell实例（及其关联的 Binding）绑定的
 */
public class GroovyConfig extends ExecuteElement implements Cloneable
{
    
    /** Java向Groovy传递参数 */
    private String              in;
    
    /** Groovy脚本文件。多个脚本文件间有换行分隔 */
    private String              script;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private List<File>          groovyScripts;
    
    /** Groovy代码 */
    private String              groovy;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private String              groovyScript;
    
    /** Java获取Groovy结果 */
    private String              out;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private Map<String ,String> outMap;
    
    
    
    public GroovyConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public GroovyConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    
    /**
     * 获取：Java向Groovy传递参数
     */
    public String getIn()
    {
        return in;
    }


    
    /**
     * 设置：Java向Groovy传递参数
     * 
     * @param i_In Java向Groovy传递参数
     */
    public void setIn(String i_In)
    {
        this.in = i_In;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：Groovy脚本文件。多个脚本文件间有换行分隔
     */
    public String getScript()
    {
        return script;
    }


    
    /**
     * 设置：Groovy脚本文件。多个脚本文件间有换行分隔
     * 
     * @param i_Script Groovy脚本。多个脚本文件间有换行分隔
     */
    public void setScript(String i_Script)
    {
        this.script        = i_Script;
        this.groovyScripts = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 获取：Groovy代码
     */
    public String getGroovy()
    {
        return groovy;
    }


    
    /**
     * 设置：Groovy代码
     * 
     * @param i_Groovy  Groovy代码
     */
    public void setGroovy(String i_Groovy)
    {
        this.groovy       = i_Groovy;
        this.groovyScript = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：Java获取Groovy结果
     */
    public String getOut()
    {
        return out;
    }


    
    /**
     * 设置：Java获取Groovy结果
     * 
     * @param i_Out Java获取Groovy结果
     */
    public void setOut(String i_Out)
    {
        this.out    = i_Out;
        this.outMap = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Groovy.getValue();
    }
    
    
    
    /**
     * 解释Java向Groovy传递参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回Map.key  为Groovy中的变量名称，
     *                   返回Map.value为Java中的参数数据
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private Map<String ,Object> parserIn(Map<String ,Object> i_Context) throws Exception
    {
        Map<String ,Object> v_In = new HashMap<String ,Object>();
        
        if ( !Help.isNull(this.in) )
        {
            XJSON               v_XJson  = new XJSON();
            Map<String ,Object> v_JDatas = (Map<String ,Object>) v_XJson.toJava(this.in);
            
            if ( !Help.isNull(v_JDatas) )
            {
                for (Map.Entry<String ,Object> v_Item : v_JDatas.entrySet())
                {
                    if ( v_Item.getValue() instanceof String )
                    {
                        String v_Value = v_Item.getValue().toString().trim();
                        if ( ValueHelp.isXID(v_Value) )
                        {
                            Object v_Obj = ValueHelp.getValue(v_Value ,null ,null ,i_Context);
                            v_In.put(v_Item.getKey() ,v_Obj);
                            continue;
                        }
                    }
                    
                    v_In.put(v_Item.getKey() ,v_Item.getValue());
                }
            }
        }
        
        return v_In;
    }
    
    
    
    /**
     * 解释Groovy脚本
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @param i_Shell  Groovy执行器
     * @return
     * @throws IOException 
     * @throws CompilationFailedException 
     */
    private synchronized List<Script> parserScript(GroovyShell i_Shell) throws CompilationFailedException, IOException
    {
        if ( !Help.isNull(this.script) )
        {
            if ( this.groovyScripts == null )
            {
                String    v_Temp    = StringHelp.replaceAll(this.script ,"\r\n" ,"\n");
                String [] v_Scripts = StringHelp.split(v_Temp ,"\n");
                this.groovyScripts  = new ArrayList<File>();
                
                for (String v_Script : v_Scripts)
                {
                    if ( Help.isNull(v_Script) )
                    {
                        continue;
                    }
                    
                    this.groovyScripts.add(new File(v_Script.trim()));
                }
            }
            
            for (File v_Script : this.groovyScripts)
            {
                i_Shell.parse(v_Script);
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 解释Groovy代码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @param i_Shell  Groovy执行器
     * @return
     */
    private synchronized Script parserGroovy(GroovyShell i_Shell)
    {
        if ( !Help.isNull(this.groovy) )
        {
            if ( this.groovyScript == null )
            {
                // 预设常用类包
                StringBuilder v_Import = new StringBuilder();
                v_Import.append("import ").append(Help      .class.getName()).append(";\n");
                v_Import.append("import ").append(StringHelp.class.getName()).append(";\n");
                v_Import.append("import ").append(ByteHelp  .class.getName()).append(";\n");
                v_Import.append("import ").append(Date      .class.getName()).append(";\n");
                v_Import.append("import ").append(Return    .class.getName()).append(";\n");
                v_Import.append("import ").append(Param     .class.getName()).append(";\n");
                v_Import.append("import ").append(XJava     .class.getName()).append(";\n");
                v_Import.append("import ").append(XJSON     .class.getName()).append(";\n");
                v_Import.append("import ").append(List      .class.getName()).append(";\n");
                v_Import.append("import ").append(ArrayList .class.getName()).append(";\n");
                v_Import.append("import ").append(Map       .class.getName()).append(";\n");
                v_Import.append("import ").append(HashMap   .class.getName()).append(";\n");
                v_Import.append("import ").append(Hashtable .class.getName()).append(";\n");
                v_Import.append(this.groovy);
                
                this.groovyScript = v_Import.toString();
            }
            return i_Shell.parse(this.groovyScript);
        }
        
        return null;
    }
    
    
    
    /**
     * 解释Java获取Groovy结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @return  返回Map.key  为Groovy中的变量名称，
     *          返回Map.value为Java中的上下文中的变量名称
     */
    @SuppressWarnings("unchecked")
    private synchronized Map<String ,String> parserOut()
    {
        if ( !Help.isNull(this.out) )
        {
            if ( this.outMap == null )
            {
                XJSON v_XJson = new XJSON();
                this.outMap = (Map<String ,String>) v_XJson.toJava(this.out);
            }
        }
        
        return this.outMap;
    }
    
    
    
    /**
     * 执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
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
        
        if ( Help.isNull(this.groovy) && Help.isNull(this.script) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s groovy and script is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        try
        {
            if ( !this.handleContext(io_Context ,v_Result) )
            {
                return v_Result;
            }
            
            Binding             v_Binding = new Binding();
            GroovyShell         v_Shell   = new GroovyShell(v_Binding);
            Map<String ,Object> v_In      = this.parserIn(io_Context);
            List<Script>        v_Scripts = this.parserScript(v_Shell);
            Script              v_Groovy  = this.parserGroovy(v_Shell);
            Map<String ,String> v_Out     = this.parserOut();
            Map<String ,Object> v_Ret     = new HashMap<String ,Object>();
            
            // Java向Groovy传递参数
            if ( !Help.isNull(v_In) )
            {
                for (Map.Entry<String ,Object> v_Item : v_In.entrySet())
                {
                    v_Binding.setVariable(v_Item.getKey() ,v_Item.getValue());
                }
                
                v_In.clear();
            }
            
            // 运行Groovy脚本
            if ( !Help.isNull(v_Scripts) )
            {
                for (Script v_Script : v_Scripts)
                {
                    v_Script.run();
                }
            }
            
            // 运行Groovy代码
            if ( v_Groovy != null )
            {
                v_Groovy.run();
            }
            
            // Java获取Groovy结果
            if ( !Help.isNull(v_Out) )
            {
                for (Map.Entry<String ,String> v_Item : v_Out.entrySet())
                {
                    Object v_Value = v_Binding.getVariable(v_Item.getKey());
                    v_Ret.put(v_Item.getValue() ,v_Value);
                }
            }
            
            v_Result.setResult(v_Ret);
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
     * @createDate  2025-09-24
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
        String        v_XName    = ElementType.Groovy.getXmlName();
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
            if ( !Help.isNull(this.in) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("in" ,this.in ,v_NewSpace));
            }
            if ( !Help.isNull(this.script) )
            {
                String v_Classhome = Help.getClassHomePath();
                String v_Webhome   = Help.getWebHomePath();
                String v_Script    = this.script;
                
                if ( v_Script.indexOf(v_Classhome) >= 0 )
                {
                    v_Script = StringHelp.replaceAll(v_Script ,v_Classhome ,"classhome:");
                }
                
                if ( v_Script.indexOf(v_Webhome) >= 0 )
                {
                    v_Script = StringHelp.replaceAll(v_Script ,v_Webhome ,"webhome:");
                }
                v_Xml.append(v_NewSpace).append(IToXml.toValue("script" ,v_Script));
            }
            if ( !Help.isNull(this.groovy) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("groovy" ,this.groovy));
            }
            if ( !Help.isNull(this.out) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("out" ,this.out ,v_NewSpace));
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
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder       v_Builder = new StringBuilder();
        Map<String ,Object> v_In      = null;
        
        try
        {
            v_In = this.parserIn(i_Context);
        }
        catch (Exception exce)
        {
            v_Builder.append("Error input parameters");
        }
        
        // Java向Groovy传递参数
        if ( !Help.isNull(v_In) )
        {
            for (Map.Entry<String ,Object> v_Item : v_In.entrySet())
            {
                v_Builder.append(v_Item.getKey()).append("=").append(v_Item.getValue()).append(" ");
            }
            
            v_In.clear();
        }
        else
        {
            v_Builder.append("No input parameters");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.in) )
        {
            v_Builder.append(this.in);
        }
        else
        {
            v_Builder.append("No input parameters");
        }
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new GroovyConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        GroovyConfig v_Clone = new GroovyConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.in           = this.in;
        v_Clone.script       = this.script;
        v_Clone.groovy       = this.groovy;
        v_Clone.out          = this.out;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
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
            throw new NullPointerException("Clone GroovyConfig xid is null.");
        }
        
        GroovyConfig v_Clone = (GroovyConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.in           = this.in;
        v_Clone.script       = this.script;
        v_Clone.groovy       = this.groovy;
        v_Clone.out          = this.out;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-24
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
            throw new NullPointerException("Clone GroovyConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        GroovyConfig                v_Clone      = new GroovyConfig();
        
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
