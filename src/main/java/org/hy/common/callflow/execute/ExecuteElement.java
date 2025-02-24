package org.hy.common.callflow.execute;

import org.hy.common.Date;
import org.hy.common.TotalNano;





/**
 * 执行元素。
 * 它是执行节点、条件逻辑的基类。
 * 它主要定义元素的公共属性、界面展示属性、执行方法。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public abstract class ExecuteElement extends TotalNano implements IExecute
{
    
    /** 主键标识 */
    protected String  id;
    
    /** 全局惟一标识ID */
    protected String  xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    protected String  comment;
    
    /** 整体样式名称 */
    protected String  styleName;
    
    /** 位置x坐标值 */
    protected Double  x;
    
    /** 位置y坐标值 */
    protected Double  y;
    
    /** 位置z坐标值 */
    protected Double  z;
    
    /** 图标高度 */
    protected Double  height;
    
    /** 图标宽度 */
    protected Double  width;
    
    /** 图标路径 */
    protected String  iconURL;
    
    /** 透明度 */
    protected Double  opacity;
    
    /** 背景色 */
    protected String  backgroudColor;
    
    /** 边框西样式 */
    protected String  lineStyle;
    
    /** 边框线颜色 */
    protected String  lineColor;
    
    /** 边框线粗细 */
    protected Double  lineSize;
    
    /** 文字颜色 */
    protected String  fontColor;
    
    /** 文字名称 */
    protected String  fontFamily;
    
    /** 文字粗体 */
    protected String  fontWeight;
    
    /** 文字大小 */
    protected Double  fontSize;
    
    /** 文字对齐方式 */
    protected String  fontAlign;
    
    /** 创建人编号 */
    protected String  createUserID;
    
    /** 修改者编号 */
    protected String  updateUserID;
    
    /** 创建时间 */
    protected Date    createTime;
    
    /** 最后修改时间 */
    protected Date    updateTime;
    
    /** 删除标记。1删除；0未删除 */
    protected Integer isDel;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public ExecuteElement(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    
    /**
     * 获取：全局惟一标识ID
     */
    public String getXid()
    {
        return xid;
    }

    
    
    /**
     * 设置：全局惟一标识ID
     * 
     * @param i_Xid 全局惟一标识ID
     */
    public void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }

    

    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_XJavaID)
    {
        this.xid = i_XJavaID;
    }
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    public String getXJavaID()
    {
        return this.xid;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment
     */
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }
    
    
    
    /**
     * 注释。可用于日志的输出等帮助性的信息
     *
     * @return
     */
    public String getComment()
    {
        return this.comment;
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
     * 获取：整体样式名称
     */
    public String getStyleName()
    {
        return styleName;
    }


    
    /**
     * 设置：整体样式名称
     * 
     * @param i_StyleName 整体样式名称
     */
    public void setStyleName(String i_StyleName)
    {
        this.styleName = i_StyleName;
    }


    
    /**
     * 获取：位置x坐标值
     */
    public Double getX()
    {
        return x;
    }


    
    /**
     * 设置：位置x坐标值
     * 
     * @param i_X 位置x坐标值
     */
    public void setX(Double i_X)
    {
        this.x = i_X;
    }


    
    /**
     * 获取：位置y坐标值
     */
    public Double getY()
    {
        return y;
    }


    
    /**
     * 设置：位置y坐标值
     * 
     * @param i_Y 位置y坐标值
     */
    public void setY(Double i_Y)
    {
        this.y = i_Y;
    }


    
    /**
     * 获取：位置z坐标值
     */
    public Double getZ()
    {
        return z;
    }


    
    /**
     * 设置：位置z坐标值
     * 
     * @param i_Z 位置z坐标值
     */
    public void setZ(Double i_Z)
    {
        this.z = i_Z;
    }


    
    /**
     * 获取：图标高度
     */
    public Double getHeight()
    {
        return height;
    }


    
    /**
     * 设置：图标高度
     * 
     * @param i_Height 图标高度
     */
    public void setHeight(Double i_Height)
    {
        this.height = i_Height;
    }


    
    /**
     * 获取：图标宽度
     */
    public Double getWidth()
    {
        return width;
    }


    
    /**
     * 设置：图标宽度
     * 
     * @param i_Width 图标宽度
     */
    public void setWidth(Double i_Width)
    {
        this.width = i_Width;
    }


    
    /**
     * 获取：图标路径
     */
    public String getIconURL()
    {
        return iconURL;
    }


    
    /**
     * 设置：图标路径
     * 
     * @param i_IconURL 图标路径
     */
    public void setIconURL(String i_IconURL)
    {
        this.iconURL = i_IconURL;
    }


    
    /**
     * 获取：透明度
     */
    public Double getOpacity()
    {
        return opacity;
    }


    
    /**
     * 设置：透明度
     * 
     * @param i_Opacity 透明度
     */
    public void setOpacity(Double i_Opacity)
    {
        this.opacity = i_Opacity;
    }


    
    /**
     * 获取：背景色
     */
    public String getBackgroudColor()
    {
        return backgroudColor;
    }


    
    /**
     * 设置：背景色
     * 
     * @param i_BackgroudColor 背景色
     */
    public void setBackgroudColor(String i_BackgroudColor)
    {
        this.backgroudColor = i_BackgroudColor;
    }


    
    /**
     * 获取：边框西样式
     */
    public String getLineStyle()
    {
        return lineStyle;
    }


    
    /**
     * 设置：边框西样式
     * 
     * @param i_LineStyle 边框西样式
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
     * 获取：删除标记。1删除；0未删除
     */
    public Integer getIsDel()
    {
        return isDel;
    }


    
    /**
     * 设置：删除标记。1删除；0未删除
     * 
     * @param i_IsDel 删除标记。1删除；0未删除
     */
    public void setIsDel(Integer i_IsDel)
    {
        this.isDel = i_IsDel;
    }
}
