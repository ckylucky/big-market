package com.cky.domain.strategy.service.armory;

/**
 * @ClassName IStragegyDispatch
 * @Description
 * @Author lukcy
 * @Date 2024/12/15 9:19
 * @Version 1.0
 */
public interface IStrategyDispatch {
    Integer getRandomAwardId(Long strategyId);

    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 权重ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param key = strategyId + _ + ruleWeightValue；
     * @return 抽奖结果
     */
    Integer getRandomAwardId(String key);

    Boolean subtractionAwardStock(Long strategyId, Integer awardId);
}
