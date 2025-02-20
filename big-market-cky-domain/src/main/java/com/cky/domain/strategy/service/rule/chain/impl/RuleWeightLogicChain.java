package com.cky.domain.strategy.service.rule.chain.impl;

import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.armory.IStrategyDispatch;
import com.cky.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.cky.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.cky.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName RuleWeightLogicChain
 * @Description  权重过滤类
 * @Author lukcy
 * @Date 2024/12/17 20:20
 * @Version 1.0
*/

 @Slf4j
 @Component("rule_weight")
 public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;
    @Resource
    protected IStrategyDispatch strategyDispatch;

    // 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
    public Long userScore = 0L;
    /**
     * 权重责任链过滤；
     * 1. 权重规则格式；4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式；判断哪个范围符合用户的特定抽奖范围
     */
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        //依旧是根据规则来查询相应的值
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        // 1. todo  这里需要查询用户的总消费额度 来看是否超出了某个积分 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        //    1、得到值 之后来做相应的处理
        //4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()){
            log.warn("抽奖责任链-权重告警【策略配置权重，但ruleValue未配置相应值】 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);}


       //  TODO  这里应该是计算用户的积分表 这里先用抽奖次数代替
        Integer userScore = repository.queryActivityAccountTotalUseCount(userId, strategyId);
        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys); // 从小到大排序

        Long nextValue = analyticalSortedKeys.stream()
                .filter(key -> userScore >= key) // 过滤出小于等于 userScore 的 key
                .max(Comparator.naturalOrder())  // 找到其中最大的 key
                .orElse(null); // 如果没有符合条件的 key，返回 null


        // 4. 权重抽奖  即找到了用户对应的积分  通过之前策略装配来获取对应的奖品id
        if (null != nextValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder().awardId(awardId).logicModel(ruleModel()).build();
        }

        // 5. 过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }
    protected String ruleModel() {
        return "rule_weight";
    }
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            // 检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
}
