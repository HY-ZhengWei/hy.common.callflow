package org.hy.common.callflow.junit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hy.common.Date;
import org.hy.common.callflow.timeout.TimeoutConfig;
import org.junit.Test;





/**
 * 测试单元：超时
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-16
 * @version     v1.0
 */
public class JU_Timeout
{
    
    public static void main(String [] args) throws InterruptedException
    {
        test_Timeout(4000);
        test_Timeout(4999);
        test_Timeout(5000);  // 在临界点上，它有时未超时，它有时会超时
        test_Timeout(5002);
        test_Timeout(6000);
    }
    
    
    
    private static void test_Timeout(long i_Timeout) throws InterruptedException
    {
        TimeoutConfig<Object> v_TimeoutConfig = new TimeoutConfig<Object>(i_Timeout).future(() -> 
        {
            // throw new RuntimeException("模拟异常");
            System.out.println(Date.getTimeNano() + " 线程开始睡眠 5 秒，超时：" + i_Timeout);
            Thread.sleep(1000 * 5 ,0);
            System.out.println(Date.getTimeNano() + " 线程睡眠结束 ");
            return "我是返回值";
        });
        
        Object v_Ret = v_TimeoutConfig.execute();
        
        System.out.println(Date.getTimeNano() + " 等待退出：" + v_Ret);
        Thread.sleep(10 * 1000);
        System.out.println(Date.getTimeNano() + " 程序退出：" + v_TimeoutConfig.getException());
        System.out.println(Date.getTimeNano() + " 程序退出：" + v_Ret + "\n\n");
    }
    
    
    
    public static void test03(String [] args) throws InterruptedException
    {
        // 创建一个线程并启动
        Thread sleepThread = new Thread(() -> {
            try
            {
                System.out.println("线程开始睡眠 10 秒...");
                Thread.sleep(10000); // 睡眠 10 秒
                System.out.println("线程睡眠结束");
            }
            catch (InterruptedException e)
            {
                System.out.println("线程睡眠被中断");
                // 恢复中断状态（如果需要）
                Thread.currentThread().interrupt();
            }
        });
        sleepThread.start(); // 启动线程
        // 主线程等待 2 秒后中断睡眠线程
        
        sleepThread.join(2000);
        System.out.println("主线程尝试中断睡眠线程");
        sleepThread.interrupt(); // 中断睡眠线程
        
        
        Thread.sleep(60 * 1000);
    }
    
    
    
    public static void test02(String [] args) throws InterruptedException
    {
        // 创建一个线程并启动
        Thread sleepThread = new Thread(() -> {
            try
            {
                System.out.println("线程开始睡眠 10 秒...");
                Thread.sleep(10000); // 睡眠 10 秒
                System.out.println("线程睡眠结束");
            }
            catch (InterruptedException e)
            {
                System.out.println("线程睡眠被中断");
                // 恢复中断状态（如果需要）
                Thread.currentThread().interrupt();
            }
        });
        sleepThread.start(); // 启动线程
        // 主线程等待 2 秒后中断睡眠线程
        try
        {
            Thread.sleep(2000); // 主线程等待 2 秒
        }
        catch (InterruptedException e)
        {
            System.out.println("主线程被中断");
        }
        System.out.println("主线程尝试中断睡眠线程");
        sleepThread.interrupt(); // 中断睡眠线程
        
        
        Thread.sleep(60 * 1000);
    }
    
    

    public static void test01(String [] args) throws InterruptedException
    {
        // 创建一个异步任务
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try
            {
                System.out.println("任务开始执行...");
                TimeUnit.SECONDS.sleep(10); // 模拟长时间任务
                System.out.println("任务执行完成");
            }
            catch (InterruptedException e)
            {
                System.out.println("任务被中断");
            }
        }).orTimeout(5 ,TimeUnit.SECONDS); // 设置超时时间为 5 秒
        // 处理超时或完成
        future.exceptionally(ex -> {
            System.out.println("任务超时或异常: " + ex.getMessage());
            return null;
        });
        // 等待任务结束
        try
        {
            future.get(); // 阻塞等待任务完成
        }
        catch (Exception e)
        {
            System.out.println("任务状态: " + future.isDone());
        }
        
        
        Thread.sleep(60 * 1000);
    }
    
    
    
    @Test
    public void execute() 
    {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // 提交任务1
        Future<String> future1 = executor.submit(() -> {
            Thread.sleep(3000); // 模拟耗时操作
            return "T1：" + Date.getNowTime().getFullMilli();
        });
        
        // 提交任务2
        Future<String> future2 = executor.submit(() -> {
            Thread.sleep(5000); // 模拟耗时操作
            return "T2：" + Date.getNowTime().getFullMilli();
        });
        
        try
        {
            System.out.println(Date.getNowTime().getFullMilli() + " Start");
            // 设置超时时间为2秒
            String result1 = future1.get(10 ,TimeUnit.SECONDS);
            String result2 = future2.get(3  ,TimeUnit.SECONDS);
            System.out.println(Date.getNowTime().getFullMilli() + " Thread 1 Result: " + result1);
            System.out.println(Date.getNowTime().getFullMilli() + " Thread 2 Result: " + result2);
        }
        catch (TimeoutException e)
        {
            System.err.println("Task timed out: " + e.getMessage());
        }
        catch (ExecutionException | InterruptedException e)
        {
            System.err.println("Task execution failed: " + e.getMessage());
        }
        finally
        {
            // 关闭线程池
            executor.shutdown();
        }
        System.out.println("Main thread exits.");
    }
    
    
}
