package com.cky.domain.strategy.service.rule.impl;

import com.cky.domain.strategy.model.entity.RuleActionEntity;
import com.cky.domain.strategy.model.entity.RuleMatterEntity;
import com.cky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.annotation.LogicStrategy;
import com.cky.domain.strategy.service.rule.ILogicFilter;
import com.cky.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName RuleLockLogicFilter
 * @Description  抽奖中规则过滤 这里是加锁
 * @Author lukcy
 * @Date 2024/12/17 9:43
 * @Version 1.0
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity .RaffleCenterEntity> {

    @Resource
    private IStrategyRepository repository;

    // 用户抽奖次数，后续完成这部分流程开发的时候，从数据库/Redis中读取
    private Long userRaffleCount = 0L;
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        //1、得到奖品的rulevalue
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        if(userRaffleCount>=Long.parseLong(ruleValue)){
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder().
                    code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo()).build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder().
                code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo()).build();
    }
}
