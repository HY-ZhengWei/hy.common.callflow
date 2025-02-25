package org.hy.common.callflow.junit;

import org.hy.common.Help;
import org.hy.common.callflow.common.TreeIDHelp;
import org.junit.Test;





/**
 * 测试单元：树ID
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-24
 * @version     v1.0
 */
public class JU_TreeID
{
    
    @Test
    public void test_TreeID()
    {
        int        v_Count      = 20;
        TreeIDHelp v_TreeIDHelp = new TreeIDHelp(1 ,1);
        String     v_TreeID     = null;
        int        v_Level      = -1;
        String     v_IsOk       = "";
        
        System.out.println("层级最小下标：" + v_TreeIDHelp.getRootLevel());
        System.out.println("序号最小下标：" + v_TreeIDHelp.getMinIndexNo());
        System.out.println("树层级分隔符：" + v_TreeIDHelp.getSplit());
        System.out.println("-------------------------------------");
        System.out.println("正确？\t|层级\t|末序号\t|次末序号\t|树ID");
        
        v_TreeID = v_TreeIDHelp.getRootID();
        v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
        v_IsOk   = v_Level == 1 ? "." : "X";
        System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,1) + "\t|" + v_TreeID + "");
        
        for (int x=1; x<v_Count; x++)
        {
            v_TreeID = v_TreeIDHelp.getTreeID(v_TreeID ,Help.random(v_TreeIDHelp.getMinIndexNo() ,v_TreeIDHelp.getMinIndexNo() + v_Count));
            v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
            v_IsOk   = v_Level == x + 1 ? "." : "X";
            System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,x) + "\t|" + v_TreeID + "");
        }
    }
    
    
    
    @Test
    public void test_TreeID_RootLevel0()
    {
        int        v_Count      = 20;
        TreeIDHelp v_TreeIDHelp = new TreeIDHelp(0 ,1);
        String     v_TreeID     = null;
        int        v_Level      = -1;
        String     v_IsOk       = "";
        
        System.out.println("层级最小下标：" + v_TreeIDHelp.getRootLevel());
        System.out.println("序号最小下标：" + v_TreeIDHelp.getMinIndexNo());
        System.out.println("树层级分隔符：" + v_TreeIDHelp.getSplit());
        System.out.println("-------------------------------------");
        System.out.println("正确？\t|层级\t|末序号\t|次末序号\t|树ID");
        
        v_TreeID = v_TreeIDHelp.getRootID();
        v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
        v_IsOk   = v_Level == 0 ? "." : "X";
        System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,0) + "\t|" + v_TreeID + "");
        
        for (int x=1; x<v_Count; x++)
        {
            v_TreeID = v_TreeIDHelp.getTreeID(v_TreeID ,Help.random(v_TreeIDHelp.getMinIndexNo() ,v_TreeIDHelp.getMinIndexNo() + v_Count));
            v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
            v_IsOk   = v_Level == x + 0 ? "." : "X";
            System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,x - 1) + "\t|" + v_TreeID + "");
        }
    }
    
    
    
    @Test
    public void test_TreeID_0()
    {
        int        v_Count      = 20;
        TreeIDHelp v_TreeIDHelp = new TreeIDHelp("0" ,1 ,1);
        String     v_TreeID     = null;
        int        v_Level      = -1;
        String     v_IsOk       = "";
        
        System.out.println("层级最小下标：" + v_TreeIDHelp.getRootLevel());
        System.out.println("序号最小下标：" + v_TreeIDHelp.getMinIndexNo());
        System.out.println("树层级分隔符：" + v_TreeIDHelp.getSplit());
        System.out.println("-------------------------------------");
        System.out.println("正确？\t|层级\t|末序号\t|次末序号\t|树ID");
        
        v_TreeID = v_TreeIDHelp.getRootID();
        v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
        v_IsOk   = v_Level == 1 ? "." : "X";
        System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,1) + "\t|" + v_TreeID + "");
        
        for (int x=1; x<v_Count; x++)
        {
            v_TreeID = v_TreeIDHelp.getTreeID(v_TreeID ,Help.random(1 ,9));
            v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
            v_IsOk   = v_Level == x + 1 ? "." : "X";
            System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,x) + "\t|" + v_TreeID + "");
        }
    }
    
    
    
    @Test
    public void test_TreeID_8()
    {
        int        v_Count      = 20;
        TreeIDHelp v_TreeIDHelp = new TreeIDHelp("8" ,1 ,0);
        String     v_TreeID     = null;
        int        v_Level      = -1;
        String     v_IsOk       = "";
        
        System.out.println("层级最小下标：" + v_TreeIDHelp.getRootLevel());
        System.out.println("序号最小下标：" + v_TreeIDHelp.getMinIndexNo());
        System.out.println("树层级分隔符：" + v_TreeIDHelp.getSplit());
        System.out.println("-------------------------------------");
        System.out.println("正确？\t|层级\t|末序号\t|次末序号\t|树ID");
        
        v_TreeID = v_TreeIDHelp.getRootID();
        v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
        v_IsOk   = v_Level == 1 ? "." : "X";
        System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,1) + "\t|" + v_TreeID + "");
        
        for (int x=1; x<v_Count; x++)
        {
            v_TreeID = v_TreeIDHelp.getTreeID(v_TreeID ,Help.random(0 ,7));
            v_Level  = v_TreeIDHelp.getLevel(v_TreeID);
            v_IsOk   = v_Level == x + 1 ? "." : "X";
            System.out.println(v_IsOk + "\t|" + v_Level + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID) + "\t|" + v_TreeIDHelp.getIndexNo(v_TreeID ,x) + "\t|" + v_TreeID + "");
        }
    }
    
}
