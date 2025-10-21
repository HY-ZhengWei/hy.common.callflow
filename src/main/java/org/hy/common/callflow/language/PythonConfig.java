package org.hy.common.callflow.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.fraction.Fraction;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.ExportType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.xml.XJSON;

import jep.SubInterpreter;





/**
 * 蟒蛇元素：在Java中嵌入Python代码
 * 
 * 注1：如果没有out时，仅返回new HashMap()
 * 注2：如果有out时，返回从Python获取的变量，数据类型也为Map
 * 注3：向Python传参后，先执行Python脚本文件，后执行Python代码
 * 
 * Jep适配Python版本的说明：https://pypi.org/project/jep/
 * 
 * 
 * 
 * Windows系统
 *   运行条件1：安装Microsoft C++ 生成工具 https://visualstudio.microsoft.com/zh-hans/visual-cpp-build-tools/ ,约1.7G
 *             注：勾选安装：MSVC v143 - VS 2022 C++ x64/x86 生成工具（必须包含 x64，对应 64 位编译）
 *             注：勾选安装：Windows 11 SDK（64 位编译依赖系统 SDK）
 *             
 *   运行条件2：安装Python解释器 https://www.python.org/ ,约26MB
 *   
 *   运行条件3：配置Python系统环境变量，默认是自动配置的，如PATH=D:\Software\Python313\;D:\Software\Python313\Scripts\
 *   
 *   运行条件4：安装Jep库 pip install jep  ,约3MB
 *             注：当Python是64位版本时，不要使用普通的 cmd 或 PowerShell，而搜索 "x64 Native Tools Command Prompt for VS 2022"，
 *                 在64位Visual Studio 2022 Developer Command中安装。
 *             注：成功时，Jep会被安装到D:\Software\Python313\Lib\site-packages\jep目录
 *                 
 *   运行条件5：运行Java程序时，在VM arguments中配置Jep环境：-Djava.library.path="D:\Software\Python313\Lib\site-packages\jep"
 *   
 *   
 *   
 * Ubuntu系统
 *   运行条件1：安装依赖 apt install gcc g++
 *   
 *   运行条件2：安装Python解释器 sudo apt install python3 python3-dev
 *   
 *   运行条件3：安装Python第三方库管理 sudo apt install python3-pip
 *             更新到最新版本 python3 -m pip install --upgrade pip setuptools --break-system-packages
 *             
 *   运行条件4：安装Python应用程序管理 sudo apt install pipx
 *             配置环境变量 pipx ensurepath ，执行后会在sudo vi ~/.profile 中添加如下配置
 *             export PATH="$PATH:/home/iot202498/.local/bin"
 *             
 *   运行条件5：安装Jep库 pipx install jep
 *             或安装国内镜像  pipx install jep --pip-args "--index-url https://mirrors.aliyun.com/pypi/simple/"
 *             成功安装后的Jep库目录  ~/.local/share/pipx/venvs/jep/lib/python3.12/site-packages/jep
 *             成功安装后的Python环境 ~/.local/share/pipx/venvs/jep/bin
 *             
 *   运行条件6：配置带有Jep库的Python解释器 sudo vi ~/.profile 中添加如下配置（注：将$PATH放在最后）
 *             export PATH="/home/iot202498/.local/bin:/home/iot202498/.local/share/pipx/venvs/jep/bin:$PATH"
 *             
 *   运行条件7：运行Java程序时，在VM arguments中配置Jep环境：-Djava.library.path="/home/iot202498/.local/share/pipx/venvs/jep/lib/python3.12/site-packages/jep"
 *             举例：java -Djava.library.path="/home/iot202498/.local/share/pipx/venvs/jep/lib/python3.12/site-packages/jep" -cp "classes:test-classes:lib/*" org.hy.common.callflow.junit.cflow033Python.JU_CFlow033
 *
 *
 *
 * OpenSUSE系统
 *   运行条件1：安装依赖 zypper install gcc gcc-c++
 *   
 *   运行条件2：安装Python解释器 zypper install python312 python312-devel
 *   
 *   运行条件3：安装Python第三方库管理 zypper install python312-pip
 *             更新到最新版本 python3.12 -m pip install --upgrade pip setuptools -i https://mirrors.aliyun.com/pypi/simple/
 *             
 *   运行条件4：切换操作系统默认的python指向
 *             rm /usr/bin/python3
 *             ln -s /usr/bin/python3.12 /usr/bin/python3
 *           
 *   运行条件5：安装Jep库 pip install jep
 *             或安装国内镜像 pip install jep -i https://mirrors.aliyun.com/pypi/simple/
 *             成功安装后的Jep库目录  /usr/local/lib64/python3.12/site-packages/jep
 *             
 *   运行条件6：运行Java程序时，在VM arguments中配置Jep环境：-Djava.library.path="/usr/local/lib64/python3.12/site-packages/jep"
 *             举例：java -Djava.library.path="/usr/local/lib64/python3.12/site-packages/jep" -cp "classes:test-classes:lib/*" org.hy.common.callflow.junit.cflow033Python.JU_CFlow033
 *
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-09-16
 * @version     v1.0
 *              v1.1  2025-09-24  添加：Python中复数结果的支持
 *                                添加：Python中分数结果的支持
 *              v1.2  2025-09-26  迁移：静态检查
 *              v2.0  2025-10-20  修正：先handleContext()解析上下文内容。如在toString()之后解析，可用无法在toString()中获取上下文中的内容。
 */
public class PythonConfig extends ExecuteElement implements Cloneable
{
    
    /** Java向Python传递参数 */
    private String              in;
    
    /** Python脚本文件。多个脚本文件间有换行分隔 */
    private String              script;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private List<String>        pythonyScripts;
    
    /** Python代码 */
    private String              python;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private String              pythonCode;
    
    /** Java获取Python结果 */
    private String              out;
    
    /** 解释一次，多次执行，加速性能（仅内部使用） */
    private Map<String ,String> outMap;
    
    /** Python代码print的字符集 */
    private String              charEncoding;
    
    
    
    public PythonConfig()
    {
        this(0L ,0L);
    }
    
    
    
    public PythonConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
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
        if ( Help.isNull(this.getPython()) && Help.isNull(this.getScript()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].script and python is null.");
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
        return "XPython_" + StringHelp.getUUID9n();
    }
    
    
    
    /**
     * 获取：Java向Python传递参数
     */
    public String getIn()
    {
        return in;
    }


    
    /**
     * 设置：Java向Python传递参数
     * 
     * @param i_In Java向Python传递参数
     */
    public void setIn(String i_In)
    {
        this.in = i_In;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }

    
    
    /**
     * 获取：Python脚本文件。多个脚本文件间有换行分隔
     */
    public String getScript()
    {
        return script;
    }


    
    /**
     * 设置：Python脚本文件。多个脚本文件间有换行分隔
     * 
     * @param i_Script Python脚本。多个脚本文件间有换行分隔
     */
    public void setScript(String i_Script)
    {
        this.script         = i_Script;
        this.pythonyScripts = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }



    /**
     * 获取：Python代码
     */
    public String getPython()
    {
        return python;
    }


    
    /**
     * 设置：Python代码
     * 
     * @param i_Python  Python代码
     */
    public void setPython(String i_Python)
    {
        this.python     = i_Python;
        this.pythonCode = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：Java获取Python结果
     */
    public String getOut()
    {
        return out;
    }


    
    /**
     * 设置：Java获取Python结果
     * 
     * @param i_Out Java获取Python结果
     */
    public void setOut(String i_Out)
    {
        this.out    = i_Out;
        this.outMap = null;
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：Python代码print的字符集
     */
    public String getCharEncoding()
    {
        return charEncoding;
    }


    
    /**
     * 设置：Python代码print的字符集
     * 
     * @param i_CharEncoding Python代码print的字符集
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
     * @createDate  2025-09-16
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return ElementType.Python.getValue();
    }
    
    
    
    /**
     * 解释Java向Python传递参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-18
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           返回Map.key  为Python中的变量名称，
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
     * 解释Python脚本
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-18
     * @version     v1.0
     *
     * @return
     */
    private synchronized List<String> parserScript()
    {
        if ( !Help.isNull(this.script) )
        {
            if ( this.pythonyScripts == null )
            {
                String    v_Temp    = StringHelp.replaceAll(this.script ,"\r\n" ,"\n");
                String [] v_Scripts = StringHelp.split(v_Temp ,"\n");
                this.pythonyScripts = new ArrayList<String>();
                
                for (String v_Item : v_Scripts)
                {
                    String v_PY = v_Item.trim();
                    if ( Help.isNull(v_PY) )
                    {
                        continue;
                    }
                    this.pythonyScripts.add(v_PY);
                }
            }
        }
        
        return this.pythonyScripts;
    }
    
    
    
    /**
     * 解释Python代码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-20
     * @version     v1.0
     *
     * @return
     */
    private synchronized String parserPython()
    {
        if ( !Help.isNull(this.python) )
        {
            if ( this.pythonCode == null )
            {
                int    v_First  = this.python.indexOf("\n");
                String v_Python = this.python;
                if ( v_First == 0 )
                {
                    // 去除有效内容前的换行
                    v_Python = v_Python.substring(1);
                }
                else if ( v_First > 0 )
                {
                    // 去除有效内容前的多个空格和换行
                    String v_Temp = v_Python.substring(0 ,v_First);
                    if ( "".equals(v_Temp.trim()) )
                    {
                        v_Python = v_Python.substring(v_First + 1);
                    }
                }
                
                String v_Temp = v_Python.replaceAll("^\\s+", "");
                int v_LPad = v_Python.length() - v_Temp.length();  // 按首行计算有效的Python缩进
                
                if ( v_LPad > 0 )
                {
                    v_Python = v_Python.trim().replaceAll("(?<=\\n)\\s+(?=\\n)" ,StringHelp.lpad("" ,v_LPad ," ")); // 将两个换行间的的多个空格，替换成两个换行间固定数量的空格
                    v_Python = v_Python.replaceAll("(\\n)\\s{1," + v_LPad + "}", "$1");                             // 去除每行前多余的空格
                }
                else
                {
                    v_Python = v_Python.trim();
                }
                
                this.pythonCode = v_Python;
            }
        }
        
        return this.pythonCode;
    }
    
    
    
    /**
     * 解释Java获取Python结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-18
     * @version     v1.0
     *
     * @return  返回Map.key  为Python中的变量名称，
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
     * @createDate  2025-09-16
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
        
        if ( Help.isNull(this.python) && Help.isNull(this.script) )
        {
            v_Result.setException(new RuntimeException("XID[" + Help.NVL(this.xid) + ":" + Help.NVL(this.comment) + "]'s python and script is null."));
            this.refreshStatus(io_Context ,v_Result.getStatus());
            return v_Result;
        }
        
        try (SubInterpreter v_Jep = new SubInterpreter())
        {
            Map<String ,Object> v_In      = this.parserIn(io_Context);
            List<String>        v_Scripts = this.parserScript();
            String              v_Python  = this.parserPython();
            Map<String ,String> v_Out     = this.parserOut();
            Map<String ,Object> v_Ret     = new HashMap<String ,Object>();
            
            // Java向Python传递参数
            if ( !Help.isNull(v_In) )
            {
                for (Map.Entry<String ,Object> v_Item : v_In.entrySet())
                {
                    v_Jep.set(v_Item.getKey() ,v_Item.getValue());
                }
                
                v_In.clear();
            }
            
            // 设置字符集
            if ( !Help.isNull(this.charEncoding) )
            {
                v_Jep.exec("import sys");
                v_Jep.exec("import io");
                v_Jep.exec("sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='" + this.charEncoding + "')");
                v_Jep.exec("sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='" + this.charEncoding + "')");
            }
            
            // 运行Python脚本
            if ( !Help.isNull(v_Scripts) )
            {
                for (String v_Item : v_Scripts)
                {
                    v_Jep.runScript(v_Item);
                }
            }
            
            // 运行Python代码
            if ( !Help.isNull(v_Python) )
            {
                v_Jep.exec(v_Python);
            }
            
            // Java获取Python结果
            if ( !Help.isNull(v_Out) )
            {
                for (Map.Entry<String ,String> v_Item : v_Out.entrySet())
                {
                    String v_Type = v_Jep.getValue("type(" + v_Item.getKey() + ").__name__").toString();
                    
                    // 分数
                    if ( "Fraction".equals(v_Type) )
                    {
                        Long v_Numerator   = (Long) v_Jep.getValue(v_Item.getKey() + ".numerator");   // 分子
                        Long v_Denominator = (Long) v_Jep.getValue(v_Item.getKey() + ".denominator"); // 分母
                        v_Ret.put(v_Item.getValue() ,new Fraction(v_Numerator.intValue() ,v_Denominator.intValue()));
                    }
                    // 复数
                    else if ( "complex".equals(v_Type) )
                    {
                        double v_Real      = (Double) v_Jep.getValue(v_Item.getKey() + ".real");  // 实部
                        double v_Imaginary = (Double) v_Jep.getValue(v_Item.getKey() + ".imag");  // 虚部  
                        v_Ret.put(v_Item.getValue() ,new Complex(v_Real ,v_Imaginary));
                    }
                    else
                    {
                        Object v_Value = v_Jep.getValue(v_Item.getKey());
                        v_Ret.put(v_Item.getValue() ,v_Value);
                    }
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
     * @createDate  2025-09-16
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
        String        v_XName    = ElementType.Python.getXmlName();
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
            if ( !Help.isNull(this.charEncoding) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("charEncoding" ,this.charEncoding));
            }
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
            if ( !Help.isNull(this.python) )
            {
                v_Xml.append(v_NewSpace).append(IToXml.toValue("python" ,this.python));
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
     * @createDate  2025-09-16
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
        
        // Java向Python传递参数
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
     * @createDate  2025-09-16
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
     * @createDate  2025-09-16
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new PythonConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        PythonConfig v_Clone = new PythonConfig();
        
        this.cloneMyOnly(v_Clone);
        v_Clone.charEncoding = this.charEncoding;
        v_Clone.in           = this.in;
        v_Clone.script       = this.script;
        v_Clone.python       = this.python;
        v_Clone.out          = this.out;
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-16
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
            throw new NullPointerException("Clone PythonConfig xid is null.");
        }
        
        PythonConfig v_Clone = (PythonConfig) io_Clone;
        super.clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        v_Clone.charEncoding = this.charEncoding;
        v_Clone.in           = this.in;
        v_Clone.script       = this.script;
        v_Clone.python       = this.python;
        v_Clone.out          = this.out;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-16
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
            throw new NullPointerException("Clone PythonConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        PythonConfig                v_Clone      = new PythonConfig();
        
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
