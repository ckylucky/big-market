package com.cky.domain.strategy.service.rule.chain.impl;

import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.cky.domain.strategy.service.rule.chain.ILogicChain;
import com.cky.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.cky.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName BackListLogicChain
 * @Description  抽奖前黑名单责任链
 * @Author lukcy
 * @Date 2024/12/17 20:04
 * @Version 1.0
 */
@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository repository;
    @Override
    public  DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());

        // 查询规则值配置  看抽奖策略是否配置有黑名单策略 通过strategy_rule表 一个strategy可以配置多个 规则
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        //101:user001,user002,user003  黑名单配置的 值 就是说如果是黑名单 则直接发放101奖品
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);
        // 黑名单抽奖判断  查询当前用户是否在黑名单里
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {   //如果存在黑名单里 则直接返回
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder().awardId(awardId).logicModel(ruleModel()).build();
            }
        }

        // 过滤其他责任链  即权重规则
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }


    protected String ruleModel() {
        return "rule_blacklist";
    }
}
