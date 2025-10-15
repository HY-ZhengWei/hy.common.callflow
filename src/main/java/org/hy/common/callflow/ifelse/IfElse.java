package org.hy.common.callflow.ifelse;

import java.util.Map;

import org.hy.common.callflow.clone.CloneableCallFlow;
import org.hy.common.callflow.file.IToXml;





/**
 * 判断条件的统一接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-12
 * @version     v1.0
 *              v2.0  2025-10-15  添加：Switch分支
 */
public interface IfElse extends IToXml ,CloneableCallFlow
{
    
    /**
     * 允许判定。即：真判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           出错异常时抛出异常
     *                   返回判定结果 <= -1 时，表示假。同时在Switch逻辑下，
     *                     -1值表示走【假】值分支的第一个分支
     *                     -2值表示走【假】值分支的第二个分支
     *                     -n值表示走【假】值分支的第N个分支
     *                   返回判定结果 >=  1 时，表示真。同时在Switch逻辑下，
     *                      1值表示走【真】值分支的第一个分支
     *                      2值表示走【真】值分支的第二个分支
     *                      n值表示走【真】值分支的第N个分支
     *                   在Switch逻辑下，返回结果n值大于分支数量时，均走最后一个分支。
     */
    public int allow(Map<String ,Object> i_Context) throws Exception;
    
    
    
    /**
     * 拒绝判定。即：假判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-12
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return           出错异常时抛出异常
     *                   返回判定结果 <= -1 时，表示假。同时在Switch逻辑下，
     *                     -1值表示走【假】值分支的第一个分支
     *                     -2值表示走【假】值分支的第二个分支
     *                     -n值表示走【假】值分支的第N个分支
     *                   返回判定结果 >=  1 时，表示真。同时在Switch逻辑下，
     *                      1值表示走【真】值分支的第一个分支
     *                      2值表示走【真】值分支的第二个分支
     *                      n值表示走【真】值分支的第N个分支
     *                   在Switch逻辑下，返回结果n值大于分支数量时，均走最后一个分支。
     */
    public int reject(Map<String ,Object> i_Context) throws Exception;
    
    
    
    /**
     * 解析为实时运行时的逻辑判定表达式
     * 
     * 注：禁止在此真的执行逻辑判定
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-20
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context);
    
}
