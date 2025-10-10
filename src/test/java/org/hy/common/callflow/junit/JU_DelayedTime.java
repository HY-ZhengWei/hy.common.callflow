package org.hy.common.callflow.junit;

import java.util.Timer;
import java.util.TimerTask;

public class JU_DelayedTime
{
    
    public static void main(String [] args)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run()
            {
                System.out.println("延时任务执行");
            }
        } ,3000); // 延时 3 秒执行
        System.out.println("任务已提交");
    }
    
}
