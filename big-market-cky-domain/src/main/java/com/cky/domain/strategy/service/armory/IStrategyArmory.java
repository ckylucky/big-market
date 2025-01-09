package com.cky.domain.strategy.service.armory;

/**
 * @ClassName IStrategyArmory
 * @Description  策略装配接口
 * @Author lukcy
 * @Date 2024/12/14 11:40
 * @Version 1.0
 */
public interface IStrategyArmory {
    /**
     * 策略装配 实现奖品的占位 存储到redis
     * @param strategyId
     * @return
     */
    boolean assembleLotteryStrategy(Long strategyId);


    boolean assembleLotteryStrategyByActivityId(Long activityId);
}
