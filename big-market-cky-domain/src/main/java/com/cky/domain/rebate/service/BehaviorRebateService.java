package com.cky.domain.rebate.service;

import com.cky.domain.rebate.event.SendRebateMessageEvent;
import com.cky.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.cky.domain.rebate.model.entity.BehaviorEntity;
import com.cky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.cky.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.cky.domain.rebate.model.valobj.TaskStateVO;
import com.cky.domain.rebate.repository.IBehaviorRebateRepository;
import com.cky.types.common.Constants;
import com.cky.types.event.BaseEvent;
import com.cky.domain.rebate.model.entity.TaskEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName BehaviorRebateService
 * @Description
 * @Author lukcy
 * @Date 2025/2/14 16:38
 * @Version 1.0
 */
@Service
public class BehaviorRebateService implements IBehaviorRebateService{

    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;
    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        //1、查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVO());

        if(dailyBehaviorRebateVOS.isEmpty()||dailyBehaviorRebateVOS==null){
            return new ArrayList<>();
        }
        //2、创建聚合对象
        List<String> orderIds=new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregates=new ArrayList<>();
        for (DailyBehaviorRebateVO dailyBehaviorRebateVO:dailyBehaviorRebateVOS){
            // 拼装业务ID；用户ID_返利类型_外部透彻业务ID
            String bizId = behaviorEntity.getUserId() + Constants.UNDERLINE + dailyBehaviorRebateVO.getRebateType() + Constants.UNDERLINE + behaviorEntity.getOutBusinessNo();
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .bizId(bizId)
                    .build();
            orderIds.add(behaviorRebateOrderEntity.getOrderId());

            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .bizId(bizId)
                    .build();

            // 构建事件消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage = sendRebateMessageEvent.buildEventMessage(rebateMessage);

            // 组装任务对象
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(behaviorEntity.getUserId());
            taskEntity.setTopic(sendRebateMessageEvent.topic());
            taskEntity.setMessageId(rebateMessageEventMessage.getId());
            taskEntity.setMessage(rebateMessageEventMessage);
            taskEntity.setState(TaskStateVO.create);


            //组装聚合对象
            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();

            behaviorRebateAggregates.add(behaviorRebateAggregate);

        }

        // 3. 存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateAggregates);

        // 4. 返回订单ID集合
        return orderIds;
    }
}
