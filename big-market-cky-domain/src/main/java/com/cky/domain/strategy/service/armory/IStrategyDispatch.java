package com.cky.domain.strategy.service.armory;

/**
 * @ClassName IStragegyDispatch
 * @Description
 * @Author lukcy
 * @Date 2024/12/15 9:19
 * @Version 1.0
 */
public interface IStrategyDispatch {
    /**
     * 由strategyid 得到一个范围随机值 随机得到奖品id
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(long strategyId);

    /**
     *
     * @param strategyId 策略id
     * @param ruleWeightValue  权重值
     * @return
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

}
