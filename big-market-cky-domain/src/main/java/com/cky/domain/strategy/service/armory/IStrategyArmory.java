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

    /**
     * 由strategyid 得到一个范围随机值 随机得到奖品id
     * @param l
     * @return
     */
    Integer getRandomAwardId(long l);
}
