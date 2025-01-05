package com.cky.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.cky.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.cky.domain.award.model.entity.TaskEntity;
import com.cky.domain.award.model.entity.UserAwardRecordEntity;
import com.cky.domain.award.repository.IAwardRepository;
import com.cky.infrastructure.event.EventPublisher;
import com.cky.infrastructure.persistent.dao.ITaskDao;
import com.cky.infrastructure.persistent.dao.IUserAwardRecordDao;
import com.cky.infrastructure.persistent.po.Task;
import com.cky.infrastructure.persistent.po.UserAwardRecord;
import com.cky.types.enums.ResponseCode;
import com.cky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @ClassName AwardRepository
 * @Description
 * @Author lukcy
 * @Date 2025/1/5 11:22
 * @Version 1.0
 */
@Slf4j
@Component
public class AwardRepository implements IAwardRepository {
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IUserAwardRecordDao userAwardRecordDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();

        String userId = userAwardRecordEntity.getUserId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        Long activityId = userAwardRecordEntity.getActivityId();

        UserAwardRecord userAwardRecord=new UserAwardRecord();
         userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
         userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
         userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
         userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
         userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
         userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
         userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
         userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task=new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());


        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    userAwardRecordDao.insert(userAwardRecord);
                    taskDao.insert(task);
                    return 1;//todo
                }
                catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);

                }
            });}
            finally {
                dbRouter.clear();
            }
        try {
            //发送mq，如果发送失败 后续会有补偿机制
            eventPublisher.publish(task.getTopic(),task.getMessage());
            // 更新数据库记录，task 任务表
            taskDao.updateTaskSendMessageCompleted(task);
        } catch (Exception e) {
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }

    }
}
