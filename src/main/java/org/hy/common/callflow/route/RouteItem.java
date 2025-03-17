package org.hy.common.callflow.route;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.clone.CloneableCallFlow;
import org.hy.common.callflow.enums.ElementType;
import org.hy.common.callflow.enums.RouteType;
import org.hy.common.callflow.enums.SelfLoopType;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.forloop.ForConfig;
import org.hy.common.callflow.ifelse.ConditionConfig;
import org.hy.common.callflow.nesting.NestingConfig;
import org.hy.common.callflow.node.CalculateConfig;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.WaitConfig;
import org.hy.common.callflow.returns.ReturnConfig;





/**
 * 路由项
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-09
 * @version     v1.0
 */
public class RouteItem implements IToXml ,CloneableCallFlow
{
    
    /** 归属者（仅对外开放setter方法，为防止死循环）（内部使用） */
    private RouteConfig    owner;
    
    /** 路由类型 */
    private RouteType      routeType;
    
    /** 主键标识 */
    private String         id;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String         comment;
                           
    /** 路径数据 */        
    private String         pathDatas;
                           
    /** 边框线样式 */        
    private String         lineStyle;
                             
    /** 边框线颜色 */        
    private String         lineColor;
                             
    /** 边框线粗细 */        
    private Double         lineSize;
                             
    /** 文字颜色 */          
    private String         fontColor;
                             
    /** 文字名称 */          
    private String         fontFamily;
                             
    /** 文字粗体 */          
    private String         fontWeight;
                             
    /** 文字大小 */          
    private Double         fontSize;
                             
    /** 文字对齐方式 */
    private String         fontAlign;
                             
    /** 创建人编号 */        
    private String         createUserID;
                             
    /** 修改者编号 */        
    private String         updateUserID;
                             
    /** 创建时间 */          
    private Date           createTime;
                             
    /** 最后修改时间 */   
    private Date           updateTime;
    
    /** 下一步 */
    private ExecuteElement next;
    
    
    
    public RouteItem(RouteConfig i_Route ,RouteType i_RouteType)
    {
        this.owner     = i_Route;
        this.routeType = i_RouteType;
    }
    
    
    /**
     * 获取：路由类型
     */
    public RouteType getRouteType()
    {
        return routeType;
    }

    
    /**
     * 获取：循环类型
     */
    public SelfLoopType getSelfLoopType()
    {
        if ( this.next == null )
        {
            return SelfLoopType.Normal;
        }
        else if ( this.next instanceof SelfLoop )
        {
            ExecuteElement v_RefExecute = ((SelfLoop) this.next).gatExecuteElement();
            if ( ElementType.For.getValue().equals(v_RefExecute.getElementType()) )
            {
                return SelfLoopType.For;
            }
            else
            {
                return SelfLoopType.While;
            }
        }
        else
        {
            return SelfLoopType.Normal;
        }
    }


    /**
     * 获取：主键标识
     */
    public String getId()
    {
        return id;
    }


    /**
     * 设置：主键标识
     * 
     * @param i_Id 主键标识
     */
    public void setId(String i_Id)
    {
        this.id = i_Id;
    }

    
    /**
     * 获取：注释。可用于日志的输出等帮助性的信息
     */
    public String getComment()
    {
        return comment;
    }

    
    /**
     * 设置：注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment 注释。可用于日志的输出等帮助性的信息
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }

    
    /**
     * 获取：路径数据
     */
    public String getPathDatas()
    {
        return pathDatas;
    }

    
    /**
     * 设置：路径数据
     * 
     * @param i_PathDatas 路径数据
     */
    public void setPathDatas(String i_PathDatas)
    {
        this.pathDatas = i_PathDatas;
    }

    
    /**
     * 获取：边框线样式
     */
    public String getLineStyle()
    {
        return lineStyle;
    }

    
    /**
     * 设置：边框线样式
     * 
     * @param i_LineStyle 边框线样式
     */
    public void setLineStyle(String i_LineStyle)
    {
        this.lineStyle = i_LineStyle;
    }

    
    /**
     * 获取：边框线颜色
     */
    public String getLineColor()
    {
        return lineColor;
    }

    
    /**
     * 设置：边框线颜色
     * 
     * @param i_LineColor 边框线颜色
     */
    public void setLineColor(String i_LineColor)
    {
        this.lineColor = i_LineColor;
    }

    
    /**
     * 获取：边框线粗细
     */
    public Double getLineSize()
    {
        return lineSize;
    }

    
    /**
     * 设置：边框线粗细
     * 
     * @param i_LineSize 边框线粗细
     */
    public void setLineSize(Double i_LineSize)
    {
        this.lineSize = i_LineSize;
    }

    
    /**
     * 获取：文字颜色
     */
    public String getFontColor()
    {
        return fontColor;
    }

    
    /**
     * 设置：文字颜色
     * 
     * @param i_FontColor 文字颜色
     */
    public void setFontColor(String i_FontColor)
    {
        this.fontColor = i_FontColor;
    }

    
    /**
     * 获取：文字名称
     */
    public String getFontFamily()
    {
        return fontFamily;
    }


    /**
     * 设置：文字名称
     * 
     * @param i_FontFamily 文字名称
     */
    public void setFontFamily(String i_FontFamily)
    {
        this.fontFamily = i_FontFamily;
    }

    
    /**
     * 获取：文字粗体
     */
    public String getFontWeight()
    {
        return fontWeight;
    }


    /**
     * 设置：文字粗体
     * 
     * @param i_FontWeight 文字粗体
     */
    public void setFontWeight(String i_FontWeight)
    {
        this.fontWeight = i_FontWeight;
    }

    
    /**
     * 获取：文字大小
     */
    public Double getFontSize()
    {
        return fontSize;
    }


    /**
     * 设置：文字大小
     * 
     * @param i_FontSize 文字大小
     */
    public void setFontSize(Double i_FontSize)
    {
        this.fontSize = i_FontSize;
    }

    
    /**
     * 获取：文字对齐方式
     */
    public String getFontAlign()
    {
        return fontAlign;
    }

    
    /**
     * 设置：文字对齐方式
     * 
     * @param i_FontAlign 文字对齐方式
     */
    public void setFontAlign(String i_FontAlign)
    {
        this.fontAlign = i_FontAlign;
    }

    
    /**
     * 获取：创建人编号
     */
    public String getCreateUserID()
    {
        return createUserID;
    }

    
    /**
     * 设置：创建人编号
     * 
     * @param i_CreateUserID 创建人编号
     */
    public void setCreateUserID(String i_CreateUserID)
    {
        this.createUserID = i_CreateUserID;
    }

    
    /**
     * 获取：修改者编号
     */
    public String getUpdateUserID()
    {
        return updateUserID;
    }

    
    /**
     * 设置：修改者编号
     * 
     * @param i_UpdateUserID 修改者编号
     */
    public void setUpdateUserID(String i_UpdateUserID)
    {
        this.updateUserID = i_UpdateUserID;
    }

    
    /**
     * 获取：创建时间
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    
    /**
     * 设置：创建时间
     * 
     * @param i_CreateTime 创建时间
     */
    public void setCreateTime(Date i_CreateTime)
    {
        this.createTime = i_CreateTime;
    }

    
    /**
     * 获取：最后修改时间
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    
    /**
     * 设置：最后修改时间
     * 
     * @param i_UpdateTime 最后修改时间
     */
    public void setUpdateTime(Date i_UpdateTime)
    {
        this.updateTime = i_UpdateTime;
    }

    
    /**
     * 获取：下一步
     * 防止Json无限制循环
     */
    public ExecuteElement gatNext()
    {
        return next;
    }


    /**
     * 设置：下一步
     * 
     * @param i_Next 下一步
     */
    public void setNext(ExecuteElement i_Next)
    {
        this.next = i_Next;
    }
    
    
    /**
     * 设置：下一步。自循环的引用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-09
     * @version     v1.0
     *
     * @param i_RefXID  引用执行元素的XID
     */
    public void setNext(String i_RefXID)
    {
        this.setNext(new SelfLoop(i_RefXID));
        this.owner.orderBy();
    }
    
    
    /**
     * 设置：归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     * 
     * @param i_Owner 归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     */
    public void setOwner(RouteConfig i_Owner)
    {
        this.owner = i_Owner;
    }
    
    
    /**
     * 设置：归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     * 防止Json无限制循环
     * 
     * @param i_Owner 归属者（仅对外开放setter方法，为防止死循环）（内部使用）
     */
    public RouteConfig gatOwner()
    {
        return this.owner;
    }


    /**
     * 转为Xml格式的内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_SuperTreeID  上级树ID
     * @return
     */
    @Override
    public String toXml(int i_Level ,String i_SuperTreeID)
    {
        StringBuilder v_Xml    = new StringBuilder();
        String        v_Level1 = "    ";
        String        v_LevelN = i_Level <= 0 ? "" : StringHelp.lpad("" ,i_Level + 1 ,v_Level1);
        
        if ( this.id != null )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("id" ,this.id));
        }
        if ( !Help.isNull(this.lineStyle) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("lineStyle" ,this.lineStyle));
        }
        if ( !Help.isNull(this.lineColor) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("lineColor" ,this.lineColor));
        }
        if ( !Help.isNull(this.lineSize) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("lineSize" ,this.lineSize));
        }
        if ( !Help.isNull(this.fontColor) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("fontColor" ,this.fontColor));
        }
        if ( !Help.isNull(this.fontFamily) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("fontFamily" ,this.fontFamily));
        }
        if ( !Help.isNull(this.fontWeight) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("fontWeight" ,this.fontWeight));
        }
        if ( !Help.isNull(this.fontSize) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("fontSize" ,this.fontSize));
        }
        if ( !Help.isNull(this.fontAlign) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("fontAlign" ,this.fontAlign));
        }
        if ( !Help.isNull(this.createUserID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("createUserID" ,this.createUserID));
        }
        if ( !Help.isNull(this.updateUserID) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("updateUserID" ,this.updateUserID));
        }
        if ( this.createTime != null )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("createTime" ,this.createTime.getFull()));
        }
        if ( this.updateTime != null )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("updateTime" ,this.updateTime.getFull()));
        }
        if ( !Help.isNull(this.pathDatas) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("pathDatas" ,this.pathDatas));
        }
        if ( !Help.isNull(this.comment) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("comment" ,this.comment));
        }
        
        if ( this.next instanceof SelfLoop )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toValue("next" ,((SelfLoop) this.next).getRefXID()));
        }
        else if ( !Help.isNull(this.next.getXJavaID()) )
        {
            v_Xml.append("\n").append(v_LevelN).append(v_Level1).append(IToXml.toRef("next" ,this.next.getXJavaID()));
        }
        else
        {
            v_Xml.append(this.next.toXml(i_Level + 1 ,i_SuperTreeID));
        }
        
        return v_Xml.toString();
    }
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-16
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        // 无须克隆路由项，也不允许调用此方法
        throw new RuntimeException("Not allowed to call RouteItem.cloneMyOnly().");
    }
    
    
    /**
     * 深度克隆编排
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-10
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
        RouteItem v_Clone = (RouteItem) io_Clone;
        
        v_Clone.id           = this.id;
        v_Clone.comment      = this.comment;
        v_Clone.pathDatas    = this.pathDatas;
        v_Clone.lineStyle    = this.lineStyle;
        v_Clone.lineColor    = this.lineColor;
        v_Clone.lineSize     = this.lineSize;
        v_Clone.fontColor    = this.fontColor;
        v_Clone.fontFamily   = this.fontFamily;
        v_Clone.fontWeight   = this.fontWeight;
        v_Clone.fontSize     = this.fontSize;
        v_Clone.fontAlign    = this.fontAlign;
        v_Clone.createUserID = this.createUserID;
        v_Clone.updateUserID = this.updateUserID;
        v_Clone.createTime   = this.createTime == null ? null : new Date(this.createTime.getTime());
        v_Clone.updateTime   = this.updateTime == null ? null : new Date(this.updateTime.getTime());
        
        if ( this.next != null )
        {
            if ( this.next instanceof NodeConfig )
            {
                NodeConfig v_CloneNode = new NodeConfig();
                ((NodeConfig) this.next).clone(v_CloneNode ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneNode;
            }
            else if ( this.next instanceof WaitConfig )
            {
                WaitConfig v_CloneWait = new WaitConfig();
                ((WaitConfig) this.next).clone(v_CloneWait ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneWait;
            }
            else if ( this.next instanceof ConditionConfig )
            {
                ConditionConfig v_CloneCondition = new ConditionConfig();
                ((ConditionConfig) this.next).clone(v_CloneCondition ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneCondition;
            }
            else if ( this.next instanceof NestingConfig )
            {
                NestingConfig v_CloneNesting = new NestingConfig();
                ((NestingConfig) this.next).clone(v_CloneNesting ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneNesting;
            }
            else if ( this.next instanceof CalculateConfig )
            {
                CalculateConfig v_CloneCalculate = new CalculateConfig();
                ((CalculateConfig) this.next).clone(v_CloneCalculate ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneCalculate;
            }
            else if ( this.next instanceof ForConfig )
            {
                ForConfig v_CloneFor = new ForConfig();
                ((ForConfig) this.next).clone(v_CloneFor ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneFor;
            }
            else if ( this.next instanceof ReturnConfig )
            {
                ReturnConfig v_CloneReturn = new ReturnConfig();
                ((ReturnConfig) this.next).clone(v_CloneReturn ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
                v_Clone.next = v_CloneReturn;
            }
            else if ( this.next instanceof SelfLoop )
            {
                // 不应当走到些
                throw new RuntimeException("Not allowed RouteItem's next is SelfLoop");
            }
            else
            {
                throw new RuntimeException("Unknown type[" + this.next.getClass().getName() + "] of exception");
            }
        }
    }
    
    
    /**
     * 深度克隆编排元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        // 无须克隆路由项，也不允许调用此方法
        throw new RuntimeException("Not allowed to call RouteItem.clone().");
    }
    
}
