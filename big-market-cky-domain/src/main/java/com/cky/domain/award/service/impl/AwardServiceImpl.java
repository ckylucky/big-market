package com.cky.domain.award.service.impl;

import com.cky.domain.award.event.SendAwardMessageEvent;
import com.cky.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.cky.domain.award.model.entity.TaskEntity;
import com.cky.domain.award.model.entity.UserAwardRecordEntity;
import com.cky.domain.award.model.valobj.TaskStateVO;
import com.cky.domain.award.repository.IAwardRepository;
import com.cky.domain.award.service.IAwardService;
import com.cky.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName AwardServiceImpl
 * @Description
 * @Author lukcy
 * @Date 2025/1/5 11:04
 * @Version 1.0
 */
@Service
public class AwardServiceImpl implements IAwardService {

    @Resource
    private IAwardRepository awardRepository;
    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;
    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage=new SendAwardMessageEvent.SendAwardMessage();
        sendAwardMessage.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        sendAwardMessage.setUserId(userAwardRecordEntity.getUserId());
        sendAwardMessage.setAwardId(userAwardRecordEntity.getAwardId());

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);
        //构建任务实体
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setUserId(userAwardRecordEntity.getUserId());
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setState(TaskStateVO.create);

        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate=new UserAwardRecordAggregate();
        userAwardRecordAggregate.setUserAwardRecordEntity(userAwardRecordEntity);
        userAwardRecordAggregate.setTaskEntity(taskEntity);

        //仓储层事务操作
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
