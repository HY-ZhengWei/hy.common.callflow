package org.hy.common.callflow.common;

import java.util.Comparator;

import org.hy.common.Help;
import org.hy.common.StringHelp;





/**
 * 树目录结构的层次和序号组成的ID编码。
 * 
 * 特殊场景一：用数字0分隔
 *      本类可以配置成用0分隔的表达形式。将 split = 0 即可。
 *      优点是：可以用数字表示树ID。
 *      缺点是：0不能在出现在序号中。
 *      未直接采用：长整数表示树ID，而是用String，原因是：程志华反馈会有溢出可能。
 *          010203表示1.2.3
 *          011003表示1.10.3
 *          
 * 特殊场景二：用数字8分隔
 *      本类可以配置成用9分隔的表达形式。将 split = 8 即可。方案建议人：程志华
 *      优点是：可以用数字表示树ID，并且这是8进制表示的序号
 *      缺点是：8不能在出现在序号中，9逻辑上不能出在序号中。
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class TreeIDHelp implements Comparator<String>
{
    
    /** 每层树ID的分隔 */
    private String split      = ".";
    
    /** 根目录的层级 */
    private int    rootLevel  = 1;
    
    /** 可以允许的最小序号 */
    private int    minIndexNo = 1;
    
    
    
    public TreeIDHelp()
    {
        // Nothing.
    }
    
    
    
    public TreeIDHelp(int i_RootLevel ,int i_MinIndexNo)
    {
        this("." ,i_RootLevel ,i_MinIndexNo);
    }
    
    
    
    public TreeIDHelp(String i_Split)
    {
        this(i_Split ,1 ,1);
    }
    
    
    
    public TreeIDHelp(String i_Split ,int i_RootLevel ,int i_MinIndexNo)
    {
        this.checkSplitIndexNo(i_Split ,i_MinIndexNo);
        this.rootLevel  = i_RootLevel;
        this.minIndexNo = i_MinIndexNo;
        this.split      = i_Split;
    }
    
    
    
    /**
     * 获取顶级最小序号的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @return
     */
    public String getRootID()
    {
        return getTreeID(null ,this.minIndexNo);
    }
    
    
    
    /**
     * 获取顶级某个序号的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_IndexNo  本节点在上级树中的排列序号
     * @return
     */
    public String getRootID(int i_IndexNo)
    {
        return getTreeID(null ,i_IndexNo);
    }
    
    
    
    /**
     * 生成本次树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_SuperTreeID  上级树ID
     * @param i_IndexNo      本节点在上级树中的排列序号
     * @return
     */
    public String getTreeID(String i_SuperTreeID ,int i_IndexNo)
    {
        if ( this.minIndexNo > i_IndexNo )
        {
            throw new IllegalArgumentException("IndexNo[" + i_IndexNo + "] is less than MinIndexNo[" + this.minIndexNo + "]");
        }
        else
        {
            this.checkSplitIndexNo(this.split ,i_IndexNo);
        }
        
        if ( Help.isNull(i_SuperTreeID) )
        {
            return "" + i_IndexNo;
        }
        else
        {
            return i_SuperTreeID + this.split + i_IndexNo;
        }
    }
    
    
    
    /**
     * 从树ID中解析出层级
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TreeID  树ID
     * @return          层次下标默认从0开始
     */
    public int getLevel(String i_TreeID)
    {
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        String v_TreeID = i_TreeID.trim();
        return StringHelp.split(v_TreeID ,this.split).length + this.rootLevel - 1;
    }
    
    
    
    /**
     * 从树ID中解析在最后层级上的排列序号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TreeID  树ID
     * @param i_Level   解析哪个层级上的
     * @return
     */
    public int getIndexNo(String i_TreeID)
    {
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        String    v_TreeID = i_TreeID.trim();
        String [] v_IDs    = StringHelp.split(v_TreeID ,this.split);
        int       v_Index  = v_IDs.length - 1;
        return Integer.parseInt(v_IDs[v_Index]);
    }
    
    
    
    /**
     * 从树ID中解析在某一层级上的排列序号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-24
     * @version     v1.0
     *
     * @param i_TreeID  树ID
     * @param i_Level   解析哪个层级上的
     * @return
     */
    public int getIndexNo(String i_TreeID ,int i_Level)
    {
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        if ( i_Level < this.rootLevel )
        {
            throw new IllegalArgumentException("Level[" + i_Level + "] is less than RootLevel[" + this.rootLevel + "]");
        }
        
        int v_MaxLevel = getLevel(i_TreeID);
        if ( v_MaxLevel < i_Level )
        {
            throw new IllegalArgumentException("Level[" + i_Level + "] is greater than MaxLevel[" + v_MaxLevel + "]");
        }
        
        String    v_TreeID = i_TreeID.trim();
        String [] v_IDs    = StringHelp.split(v_TreeID ,this.split);
        int       v_Index  = i_Level - this.rootLevel;
        return Integer.parseInt(v_IDs[v_Index]);
    }
    
    
    
    /**
     * 从树ID中解析上级父级的树ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @param i_TreeID  树ID
     * @return          顶级树ID的父级树ID为空字符串
     */
    public String getSuperTreeID(String i_TreeID)
    {
        if ( Help.isNull(i_TreeID) )
        {
            throw new NullPointerException("TreeID is null.");
        }
        
        String v_TreeID = i_TreeID.trim();
        int    v_Index  = v_TreeID.lastIndexOf(this.split);
        if ( v_Index <= 0 )
        {
            // 顶级树ID的父级树ID为空字符串
            return "";
        }
        else
        {
            return v_TreeID.substring(0 ,v_Index);
        }
    }
    
    
    
    /**
     * 获取：每层树ID的分隔
     */
    public String getSplit()
    {
        return split;
    }


    
    /**
     * 检查分隔符与序号间的合法性
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-25
     * @version     v1.0
     *
     * @param i_Split    每层树ID的分隔
     * @param i_IndexNo  本节点在上级树中的排列序号
     */
    private void checkSplitIndexNo(String i_Split ,int i_IndexNo)
    {
        char [] v_SplitArr = this.split.toCharArray();
        String  v_IndexNo  = "" + i_IndexNo;
        for (char v_SplitChar : v_SplitArr)
        {
            if ( StringHelp.isContains(v_IndexNo ,StringHelp.toString(v_SplitChar)) )
            {
                throw new IllegalArgumentException("IndexNo[" + i_IndexNo + "] contains split[" + this.split + "]'s [" + StringHelp.toString(v_SplitChar) + "]");
            }
        }
    }


    
    /**
     * 获取：根目录的层级
     */
    public int getRootLevel()
    {
        return rootLevel;
    }


    
    /**
     * 获取：可以允许的最小序号
     */
    public int getMinIndexNo()
    {
        return minIndexNo;
    }
    
    
    
    /**
     * 比较两个树ID的大小。
     * 
     * 算法为：从最小层级逐一层级的序号比较；同级序号越大，比较结果越大；
     *       同级序号相同时，比较下一层级的序号大小。
     * 
     * 比较举例
     *      1.3 == 1.3
     *      1.3 <  1.3.2
     *      1.3 >  1
     *      1.3 <  1.5
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-27
     * @version     v1.0
     *
     * @param i_ATreeID  A的树ID
     * @param i_BTreeID  B的树ID
     * @return
     */
    @Override
    public int compare(String i_ATreeID ,String i_BTreeID)
    {
        if ( i_ATreeID == null )
        {
            return i_BTreeID == null ? 0 : -1;
        }
        else if ( i_BTreeID == null )
        {
            return 1;
        }
        else if ( i_ATreeID.equals(i_BTreeID) )
        {
            return 0;
        }
        else
        {
            String [] v_ATreeID = StringHelp.split(i_ATreeID ,this.split);
            String [] v_BTreeID = StringHelp.split(i_BTreeID ,this.split);
            int       v_Size    = Help.min(v_ATreeID.length ,v_BTreeID.length);
            
            for (int x=0; x<v_Size; x++)
            {
                Integer v_ANo       = Integer.valueOf(v_ATreeID[x]);
                Integer v_BNo       = Integer.valueOf(v_BTreeID[x]);
                int     v_CompareTo = v_ANo.compareTo(v_BNo);
                if ( v_CompareTo == 0 )
                {
                    continue;
                }
                else
                {
                    return v_CompareTo;
                }
            }
            
            return Integer.compare(v_ATreeID.length ,v_BTreeID.length);
        }
    }
    
}
