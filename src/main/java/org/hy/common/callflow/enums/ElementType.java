package org.hy.common.callflow.enums;





/**
 * 执行元素类型的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-03
 * @version     v1.0
 */
public enum ElementType
{
    
    Node(       "NODE"        ,"xnode"      ,"执行元素"),
                              
    Api (       "API"         ,"xapi"       ,"接口元素"),
                              
    Publish(    "PUBLISH"     ,"xpublish"   ,"发布元素"),
                              
    Subscribe(  "SUBSCRIBE"   ,"xsubscribe" ,"订阅元素"),
                              
    WSPush(     "WSPUSH"      ,"xwspush"    ,"点推元素"),
                              
    WSPull(     "WSPULL"      ,"xwspull"    ,"点拉元素"),
                              
    XSQL(       "XSQL"        ,"xsql"       ,"XSQL元素"),
    
    XCQL(       "XCQL"        ,"xcql"       ,"图谱元素"),
                              
    Command(    "COMMAND"     ,"xcommand"   ,"命令元素"),
                              
    Zip(        "ZIP"         ,"xzip"       ,"压缩元素"),
                              
    Unzip(      "UNZIP"       ,"xunzip"     ,"解压元素"),
    
    EncryptFile("ENCRYPTFILE" ,"xenf"       ,"密文元素"),
    
    DecryptFile("DECRYPTFILE" ,"xdef"       ,"解文元素"),
    
    Condition(  "CONDITION"   ,"xcondition" ,"条件逻辑"),
                              
    Wait(       "WAIT"        ,"xwait"      ,"等待元素"),
                              
    Calculate(  "CALCULATE"   ,"xcalculate" ,"计算元素"),
                              
    For(        "FOR"         ,"xfor"       ,"For循环"),
                              
    MT(         "MT"          ,"xmt"        ,"并发元素"),
                              
    Nesting(    "NESTING"     ,"xnesting"   ,"嵌套子编排"),
                              
    Return(     "RETURN"      ,"xreturn"    ,"返回元素"),
                              
    Job(        "JOB"         ,"xjob"       ,"定时元素"),
                              
    CacheGet(   "CACHEGET"    ,"xcg"        ,"缓存读元素"),
                              
    CacheSet(   "CACHESET"    ,"xcs"        ,"缓存写元素"),
    
    Java(       "JAVA"        ,"xjava"      ,"爪哇元素"),
    
    Python(     "PYTHON"      ,"xpython"    ,"蟒蛇元素"),
    
    Groovy(     "GROOVY"      ,"xgroovy"    ,"酷语元素"),
    
    Shell(      "SHELL"       ,"xshell"     ,"脚本元素"),
    
    Native(     "Native"      ,"xnative"    ,"实时元素"),
    
    Ftp(        "FTP"         ,"xftp"       ,"文传元素"),
                              
    SelfLoop(   "SELFLOOP"    ,""           ,"自循环"),
                              
    RouteItem(  "ROUTEITEM"   ,"xrouteItem" ,"路径项"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** XML名称 */
    private String  xmlName;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-11
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static ElementType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (ElementType v_Enum : ElementType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    ElementType(String i_Value ,String i_XmlName ,String i_Comment)
    {
        this.value   = i_Value;
        this.xmlName = i_XmlName;
        this.comment = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public String getXmlName()
    {
        return xmlName;
    }



    public String getComment()
    {
        return this.comment;
    }
    
    

    public String toString()
    {
        return this.value + "";
    }
    
}
