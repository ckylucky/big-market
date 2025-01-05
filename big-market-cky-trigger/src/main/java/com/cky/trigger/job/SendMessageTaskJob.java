package com.cky.trigger.job;

import com.cky.domain.task.model.entity.TaskEntity;
import com.cky.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName SendMessageTaskJob
 * @Description   发送MQ消息任务队列 扫描补偿机制
 * @Author lukcy
 * @Date 2025/1/5 11:36
 * @Version 1.0
 */

@Slf4j
@Component()
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try{
            //获取分库数量
            int count = dbRouter.dbCount();
            // 逐个库扫描表【每个库一个任务表】
            for (int i = 1; i <=count; i++) {
                int finalI = i;
                executor.execute(()->{
                    try {
                        dbRouter.setDBKey(finalI);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                        if(taskEntities.isEmpty())
                            return;
                        for (TaskEntity taskEntitiy:taskEntities){
                            executor.execute(()->{
                                try {
                                    taskService.sendMessage(taskEntitiy);
                                    taskService.updateTaskSendMessageCompleted(taskEntitiy.getUserId(),taskEntitiy.getMessageId());
                                } catch (Exception e) {
                                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntitiy.getUserId(), taskEntitiy.getTopic());
                                    taskService.updateTaskSendMessageFail(taskEntitiy.getUserId(), taskEntitiy.getMessageId());
                                }
                            });
                        }
                    }

                    finally {
                        dbRouter.clear();
                    }
                });
            }
        }catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouter.clear();
        }


    }

}
