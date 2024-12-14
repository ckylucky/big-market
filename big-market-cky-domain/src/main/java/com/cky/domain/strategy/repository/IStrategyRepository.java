package com.cky.domain.strategy.repository;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName IStrategyRepository
 * @Description  策略仓储接口
 * @Author lukcy
 * @Date 2024/12/14 11:44
 * @Version 1.0
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(Long strategyId, int size, HashMap<Integer, Integer> shuffleStartegyAwardSearchTables);

    int getRateRange(long strategyId);

    Integer getStrategyAwardAssemble(long strategyId, int i);
}
