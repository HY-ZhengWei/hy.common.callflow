package org.hy.common.callflow.enums;

import org.hy.common.MethodReflect;

/**
 * 比较器的枚举
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-11
 * @version     v1.0
 */
public enum Comparer
{
    
    Equal("==" ,"等于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.equals(i_B);
            }
        }
        
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.equals(i_B);
            }
        }
    },
    
    EqualNot("!=" ,"不等于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return !i_A.equals(i_B);
            }
        }
        
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return !i_A.equals(i_B);
            }
        }
    },
    
    Greater(">" ,"大于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.compareTo(i_B) > 0;
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return false;
                }
            }
            else if ( i_B == null )
            {
                return true;
            }
            else if ( i_A == i_B )
            {
                return false;
            }
            else
            {
                if ( i_A.getClass().equals(i_B.getClass()) )
                {
                    if ( MethodReflect.isExtendImplement(i_A ,Comparable.class) )
                    {
                        return ((Comparable<Object>)i_A).compareTo(i_B) > 0;
                    }
                }
                return i_A.hashCode() > i_B.hashCode();
            }
        }
    },
    
    GreaterEqual(">=" ,"大于等于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.compareTo(i_B) >= 0;
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if ( i_B == null )
            {
                return true;
            }
            else if ( i_A == i_B )
            {
                return true;
            }
            else
            {
                if ( i_A.getClass().equals(i_B.getClass()) )
                {
                    if ( MethodReflect.isExtendImplement(i_A ,Comparable.class) )
                    {
                        return ((Comparable<Object>)i_A).compareTo(i_B) >= 0;
                    }
                }
                return i_A.hashCode() >= i_B.hashCode();
            }
        }
    },
    
    Less("<" ,"小于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.compareTo(i_B) < 0;
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return false;
                }
                else
                {
                    return false;
                }
            }
            else if ( i_B == null )
            {
                return false;
            }
            else if ( i_A == i_B )
            {
                return false;
            }
            else
            {
                if ( i_A.getClass().equals(i_B.getClass()) )
                {
                    if ( MethodReflect.isExtendImplement(i_A ,Comparable.class) )
                    {
                        return ((Comparable<Object>)i_A).compareTo(i_B) < 0;
                    }
                }
                return i_A.hashCode() < i_B.hashCode();
            }
        }
    },
    
    LessEqual("<=" ,"小于等于") {
        @Override
        public <V extends Comparable<V>> boolean compare(V i_A ,V i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return i_A.compareTo(i_B) <= 0;
            }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object i_A ,Object i_B)
        {
            if ( i_A == null )
            {
                if ( i_B == null )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if ( i_B == null )
            {
                return false;
            }
            else if ( i_A == i_B )
            {
                return true;
            }
            else
            {
                if ( i_A.getClass().equals(i_B.getClass()) )
                {
                    if ( MethodReflect.isExtendImplement(i_A ,Comparable.class) )
                    {
                        return ((Comparable<Object>)i_A).compareTo(i_B) <= 0;
                    }
                }
                return i_A.hashCode() <= i_B.hashCode();
            }
        }
    },
    
    ;
    
    
    
    /**
     * 比较
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_A  比较值A
     * @param i_B  比较值B
     * @return
     */
    public abstract <V extends Comparable<V>> boolean compare(V i_A ,V i_B);
    
    
    
    /**
     * 比较
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_A  比较值A
     * @param i_B  比较值B
     * @return
     */
    public abstract boolean compare(Object i_A ,Object i_B);
    
    
    
    /** 值 */
    private String  value;
    
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
    public static Comparer get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        for (Comparer v_Enum : Comparer.values())
        {
            if ( v_Enum.value.equals(i_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    Comparer(String i_Value ,String i_Comment)
    {
        this.value   = i_Value;
        this.comment = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
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
