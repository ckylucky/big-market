package com.cky.domain.strategy.service.raffle;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @ClassName IRaffleAward
 * @Description  抽奖奖品接口
 * @Author lukcy
 * @Date 2024/12/22 9:34
 * @Version 1.0
 */
public interface IRaffleAward {


    /**
     * 根据策略ID查询抽奖奖品列表配置
     *
     * @param strategyId 策略ID
     * @return 奖品列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);
}
