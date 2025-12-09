package org.hy.common.callflow.language.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;





/**
 * 内存编译源码。通过 JavaCompiler 编译内存中的源码，生成字节码（避免写入磁盘）
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-12-05
 * @version     v1.0
 */
public class CacheJavaFileObject extends SimpleJavaFileObject
{

    /** 类名称 */
    private final String          className;
    
    /** Java源码 */
    private final String          sourceCode;
    
    /** 编译后的字节码 */
    private ByteArrayOutputStream byteCode;
    
    
    
    /**
     * 构造器：用于存储源码（源码阶段）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName   类名称
     * @param i_SourceCode  源码
     */
    public CacheJavaFileObject(String i_ClassName ,String i_SourceCode) 
    {
        super(URI.create("string:///" + i_ClassName.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.className  = i_ClassName;
        this.sourceCode = i_SourceCode;
    }
    
    
    
    /**
     * 构造器：用于存储字节码（编译阶段）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-08
     * @version     v1.0
     *
     * @param i_ClassName   类名称
     * @param i_SourceCode  源码
     * @param i_Kind        文件种类
     */
    public CacheJavaFileObject(String i_ClassName ,String i_SourceCode ,Kind i_Kind) 
    {
        super(URI.create("bytes:///" + i_ClassName.replace('.', '/') + i_Kind.extension), i_Kind);
        if ( i_Kind != Kind.CLASS ) 
        {
            throw new UnsupportedOperationException("仅CLASS类型支持该方法");
        }
        
        this.className  = i_ClassName;
        this.sourceCode = i_SourceCode;
    }
    
    
    
    /**
     * 读取源码（编译器需要）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-05
     * @version     v1.0
     *
     * @param i_IgnoreEncodingErrors
     * @return
     *
     * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
     */
    @Override
    public CharSequence getCharContent(boolean i_IgnoreEncodingErrors) 
    {
        if ( getKind() != Kind.SOURCE ) 
        {
            throw new UnsupportedOperationException("仅SOURCE类型支持该方法");
        }
        
        return this.sourceCode;
    }
    
    
    
    /**
     * 写入字节码（编译器输出）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-05
     * @version     v1.0
     *
     * @return
     *
     * @see javax.tools.SimpleJavaFileObject#openOutputStream()
     */
    @Override
    public OutputStream openOutputStream() 
    {
        if ( getKind() != Kind.CLASS ) 
        {
            throw new UnsupportedOperationException("仅CLASS类型支持该方法");
        }
        
        this.byteCode = new ByteArrayOutputStream();
        return byteCode;
    }
    
    

    /**
     * 编译器读取字节码时调用（之前缺失此方法导致异常）
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-12-09
     * @version     v1.0
     *
     * @return
     *
     * @see javax.tools.SimpleJavaFileObject#openInputStream()
     */
    @Override
    public InputStream openInputStream() throws IOException
    {
        if ( getKind() != Kind.CLASS )
        {
            throw new UnsupportedOperationException("仅CLASS类型支持openInputStream()");
        }
        
        // 若输出流已写入，先刷新到字节数组
        if ( this.byteCode != null && this.byteCode.size() > 0 )
        {
            return new ByteArrayInputStream(this.byteCode.toByteArray());
        }
        
        return super.openInputStream();
    }

    
    
    
    /**
     * 获取编译后的字节码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-12-05
     * @version     v1.0
     *
     * @return
     */
    public byte[] getByteCode() 
    {
        return this.byteCode == null ? new byte[0] : this.byteCode.toByteArray();
    }

    
    
    /**
     * 设置：编译后的字节码
     * 
     * @param i_ByteCode 编译后的字节码
     */
    public CacheJavaFileObject setByteCode(byte[] i_ByteCode)
    {
        this.byteCode = new ByteArrayOutputStream();
        this.byteCode.writeBytes(i_ByteCode);
        return this;
    }



    /**
     * 获取：类名称
     */
    public String getClassName()
    {
        return className;
    }


    
    /**
     * 获取：Java源码
     */
    public String getSourceCode()
    {
        return sourceCode;
    }
    
}
